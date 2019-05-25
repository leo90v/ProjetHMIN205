var crypto = require('crypto');
var shortid = require('shortid');
var express = require('express');
var mysql = require('mysql');
var bodyParser = require('body-parser');
var fs = require('fs');
var path = require('path');
nodeMailer = require('nodemailer');

//Database connection
var con = mysql.createConnection({
  host: 'localhost',
  user: 'lmoros',
  password: '1234567',
  database: 'MOOC',
  port: '3306'
});

//Password encryption
var genRandomString = function(length) {
  return crypto.randomBytes(Math.ceil(length/2)).toString('hex').slice(0,length);
}

var sha512 = function(password, salt) {
  var hash = crypto.createHmac('sha512', salt);
  hash.update(password);
  var value = hash.digest('hex');
  return {
    salt: salt,
    passwordHash: value
  }
};

function saltHashPassword(userPassword) {
  var salt = genRandomString(16);
  var passwordData = sha512(userPassword, salt);
  return passwordData;
}

function checkHashPassword(userPassword, salt) {
  var passwordData =  sha512(userPassword, salt);
  return passwordData;
}

function sendEmail(email, short_id, res) {
  let transporter = nodeMailer.createTransport({
    host: 'smtp.gmail.com',
    port: 465,
    secure: true,
    auth: {
        user: 'moochmin205@gmail.com',
        pass: 'bonjouratous'
    }
  });

  let text = 'Please use this code ' + short_id + ' to validate your account.'
  let html = 'Please use this code <b>' + short_id + '</b> to validate your account.'

  let mailOptions = {
      from: '"Mooc HMIN205" <moochmin205@gmail.com>', // sender address
      to: email, // list of receivers
      subject: 'Welcome to MOOC HMIN205', // Subject line
      text: text, // plain text body
      html: html // html body
  };

  transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
        console.log(error)
        res.end(JSON.stringify('Signup failed!'))
      }
      res.end(JSON.stringify('Signup succesful!'))
      console.log('Message %s sent: %s', info.messageId, info.response);
  });
}

//REST server code
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

//Sign Up
app.post('/signup/student', (req, res, next) => {
  var post_data = req.body;
  var plaint_password = post_data.password;
  var hash_data = saltHashPassword(plaint_password);
  var password = hash_data.passwordHash;
  var salt = hash_data.salt;

  var name = post_data.name;
  var last_name = post_data.last_name;
  var email = post_data.email;
  var user_type = post_data.user_type;
  var grade = post_data.grade;

  con.query('SELECT * FROM user WHERE email=?',[email],function(err, result, fields) {
    con.on('error',function(err) {
      console.log('[MySQL ERROR]', err);
    });

    if (result && result.length)
      res.json('User already exists!');
    else {
      con.query("INSERT INTO user (name, last_name, email, password, salt, user_type, grade) VALUES (?,?,?,?,?,?,?)", [name, last_name, email, password, salt, user_type, grade], function(err, result, fields) {
        con.on('error',function(err) {
          console.log('[MySQL ERROR]', err);
        });
        let short_id = shortid.generate();
        con.query("INSERT INTO user_validation (email, short_id) VALUES (?,?)", [email, short_id], function(err, result, fields) {
          con.on('error',function(err) {
            console.log('[MySQL ERROR]', err);
          });
          sendEmail(email, short_id, res);
        });
      });
    }
  });
});

//hello darkness my old friend
//signup parent
app.post('/signup/parent', (req, res, next) => {
  var parent = req.body;
  var emails = new Array();
  emails.push(parent.email)

  //for each student
  parent['users'].forEach(function(student) {
    emails.push(student.email);
  });

  con.query("SELECT * FROM user WHERE email IN (?)",[emails],function(err, result, fields) {
    con.on('error',function(error) {
      console.log('[MySQL ERROR]', error);
    });

    if (result && result.length) {
      var existingEmails = new Array()
      result.forEach(function(user) {
        existingEmails.push(user.email);
      });
      res.end(JSON.stringify('User(s) already exist(s) : ' + existingEmails.join()))
    }
    else {
      //fun begins here
      var children = parent.users;
      delete parent.users;
      var n_childs = children.length;

      var plaint_password = parent.password;
      var hash_data = saltHashPassword(plaint_password);
      var password = hash_data.passwordHash;
      var salt = hash_data.salt;

      con.beginTransaction(function (err) {
        //insert parent
        con.query("INSERT INTO user (name, last_name, email, password, salt, user_type) VALUES (?,?,?,?,?,?)", [parent.name, parent.last_name, parent.email, password, salt, parent.user_type], function(err, result, fields) {
          con.on('error',function(err) {
            console.log('[MySQL ERROR]', err);
            if (err) {
              con.rollback(function() {
                throw err;
              });
            }
          });
          //save parent id
          parent.id = result.insertId
          //insert parent user validation
          let short_id = shortid.generate();
          con.query("INSERT INTO user_validation (email, short_id) VALUES (?,?)", [parent.email, short_id], function(err, result, fields) {
            con.on('error',function(err) {
              console.log('[MySQL ERROR]', err);
              if (err) {
                con.rollback(function() {
                  throw err;
                });
              }
            });

            var inserted_children = 0;
            //Parent registred succesfully now let's register the kids
            children.forEach(function(child) {
              var hash_data = saltHashPassword(child.password);
              child.password = hash_data.passwordHash;
              child.salt = hash_data.salt;

              con.query("INSERT INTO user (name, last_name, email, password, salt, user_type, grade, id_parent) VALUES (?,?,?,?,?,?,?,?)", [child.name, child.last_name, child.email, child.password, child.salt, child.user_type, child.grade, parent.id], function(err, result, fields) {
                con.on('error',function(err) {
                  console.log('[MySQL ERROR]', err);
                  if (err) {
                    con.rollback(function() {
                      throw err;
                    });
                  }
                });
                inserted_children++;
                if (inserted_children == n_childs) {
                  con.commit(function(err) {
                    if (err) {
                      con.rollback(function() {
                        throw err;
                      });
                    }
                    //Send email to parent
                    let transporter = nodeMailer.createTransport({
                      host: 'smtp.gmail.com',
                      port: 465,
                      secure: true,
                      auth: {
                          user: 'moochmin205@gmail.com',
                          pass: 'bonjouratous'
                      }
                    });

                    let text = 'Please use this code ' + short_id + ' to validate your account.'
                    let html = 'Please use this code <b>' + short_id + '</b> to validate your account.'

                    let mailOptions = {
                        from: '"Mooc HMIN205" <moochmin205@gmail.com>', // sender address
                        to: parent.email, // list of receivers
                        subject: 'Welcome to MOOC HMIN205', // Subject line
                        text: text, // plain text body
                        html: html // html body
                    };

                    transporter.sendMail(mailOptions, (error, info) => {
                        if (error) {
                          console.log(error)
                          res.end(JSON.stringify('Signup failed!'))
                        }
                        res.end(JSON.stringify('Signup succesful!'))
                        console.log('Message %s sent: %s', info.messageId, info.response);
                    });
                  });
                }
              });
            });
          });
        });
      })
    }
  });
});

//Login
app.post('/login/', (req, res, next) => {
  var post_data = req.body;
  var user_password = post_data.password;
  var email = post_data.email;

  //update user set last_login = now() where id=1;

  con.query('SELECT * FROM user WHERE email=?', [email], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      var salt = result[0].salt;
      var encrypted_password = result[0].password;
      var hashed_password = checkHashPassword(user_password,salt).passwordHash;
      if (encrypted_password == hashed_password)
      {
        con.query('update user set last_login = now() where email=?;', [email], function(error, result2, fields) {
          con.on('error', function(err) {
            console.log('[MySQL ERROR]', err);
            res.end(JSON.stringify({id:-1,message:'SQL Error'}));
          })
          res.end(JSON.stringify(result[0]));
        })
      }
      else
        res.end(JSON.stringify({id:-1,message:'Wrong user or password'}));
    }
    else {
      res.end(JSON.stringify({id:-1,message:'Wrong user or password'}));
    }
  })
});

//Validate user
//Child accounts are activates once the parent account is activated
app.post('/validate/', (req, res, next) => {
  var post_data = req.body;
  var short_id = post_data.short_id;
  var email = post_data.email;

  con.query('SELECT v.*, u.user_type, u.id AS user_id FROM user_validation v, user u WHERE v.email=? AND v.short_id=? AND v.email = u.email', [email,short_id], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      var user_id = result[0].user_id;
      var user_type = result[0].user_type;
      con.beginTransaction(function (err) {
        con.query('DELETE FROM user_validation WHERE email=? AND short_id=?', [email,short_id], function(error, result, fields) {
          con.on('error', function(err) {
            console.log('[MySQL ERROR]', err);
            if (err) {
              con.rollback(function() {
                throw err;
              });
            }
          })
          con.query('UPDATE user SET active=1 WHERE email=?', [email], function(error, result, fields) {
            con.on('error', function(err) {
              console.log('[MySQL ERROR]', err);
              if (err) {
                con.rollback(function() {
                  throw err;
                });
              }
            })
            if (user_type == 1) {
              con.commit(function(err) {
                if (err) {
                  con.rollback(function() {
                    throw err;
                  });
                }
                res.end(JSON.stringify('User validated'));
              });
            }
            else { //Activate child accounts
              con.query('UPDATE user SET active=1 WHERE id_parent=?', [user_id], function(error, result, fields) {
                con.on('error', function(err) {
                  console.log('[MySQL ERROR]', err);
                  if (err) {
                    con.rollback(function() {
                      throw err;
                    });
                  }
                })
                con.commit(function(err) {
                  if (err) {
                    con.rollback(function() {
                      throw err;
                    });
                  }
                  res.end(JSON.stringify('User validated'));
                });
              });
            }
          })
        })
      });
    }
    else { //c-c-c-combo breaker
      res.end(JSON.stringify('Wrong validation code'));
    }
  })
});

//Stream video
app.get('/video/:video', function(req, res) {
  let video = req.params.video
  const path = './videos/' + video

  var stat = fs.statSync(path);
  var total = stat.size;
  if (req.headers['range']) {
    var range = req.headers.range;
    var parts = range.replace(/bytes=/, "").split("-");
    var partialstart = parts[0];
    var partialend = parts[1];

    var start = parseInt(partialstart, 10);
    var end = partialend ? parseInt(partialend, 10) : total-1;
    var chunksize = (end-start)+1;
    console.log('RANGE: ' + start + ' - ' + end + ' = ' + chunksize);

    var file = fs.createReadStream(path, {start: start, end: end});
    res.writeHead(206, { 'Content-Range': 'bytes ' + start + '-' + end + '/' + total, 'Accept-Ranges': 'bytes', 'Content-Length': chunksize, 'Content-Type': 'video/mp4' });
    file.pipe(res);
  } else {
    console.log('ALL: ' + total);
    res.writeHead(200, { 'Content-Length': total, 'Content-Type': 'video/mp4' });
    fs.createReadStream(path).pipe(res);
  }
})

//Send pdf
app.get('/pdf/:file', function(req, res) {
  let pdf = req.params.file
  const path = './pdfs/' + pdf
  /*fs.readFile(path, function (err,data){
     res.contentType("application/pdf");
     res.send(data);
  });*/
  var file = fs.createReadStream(path);
  var stat = fs.statSync(path);
  res.setHeader('Content-Length', stat.size);
  res.setHeader('Content-Type', 'application/pdf');
  res.setHeader('Content-Disposition', 'attachment; filename='+pdf);
  file.pipe(res);
})

//Get courses
app.get('/course/:grade', function(req, res) {
  let grade = req.params.grade
  con.query('SELECT * FROM course WHERE grade=?', [grade], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify([{"id":0,"grade":0,"name":"Under construction","description":"Under construction"}]));
    }
  })
})

//Get videos
app.get('/video/list/:course', function(req, res) {
  let course = req.params.course
  con.query('SELECT * FROM video WHERE id_course=?', [course], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify('Invalid course'));
    }
  })
})

app.get('/video/list/:course/:user', function(req, res) {
  let course = req.params.course
  let user = req.params.user

  con.query(`select video.*, not isnull(videoviews.id) viewed
             from video left outer join videoviews
             on video.id = videoviews.id_video
             where video.id_course=?
             and (videoviews.id_user=? or isnull(videoviews.id_user))`,
  [course, user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify('Invalid course'));
    }
  })
})

//Get pdfs
app.get('/pdf/list/:course', function(req, res) {
  let course = req.params.course
  con.query('SELECT * FROM pdf WHERE id_course=?', [course], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify('Invalid course'));
    }
  })
})

app.get('/pdf/list/:course/:user', function(req, res) {
  let course = req.params.course
  let user = req.params.user

  con.query(`select pdf.*, not isnull(pdfviews.id) viewed
             from pdf left outer join pdfviews
             on pdf.id = pdfviews.id_pdf
             where pdf.id_course=?
             and (pdfviews.id_user=? or isnull(pdfviews.id_user))`,
  [course, user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify('Invalid course'));
    }
  })
})

//Get pdf views
app.get('/pdf/completed/:user', function(req, res) {
  let user = req.params.user

  con.query(`select c.id, c.name cname, p.name pname, v.completion_time
             from pdf p, pdfviews v, course c
             where p.id = v.id_pdf
             and c.id = p.id_course
             and v.id_user = ?
             order by c.id, v.completion_time;`, [user], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      let course = {id : result[0].id, name : result[0].cname };
      let views = new Array();
      let final_result = new Array();

      for (i = 0; i < result.length; i++) {
        if (course.id != result[i].id) {
          course['views'] = views;
          final_result.push(course);
          course = {id : result[i].id, name : result[i].cname }
          views = new Array();
        }
        views.push({"name":result[i].pname,"completion_time":result[i].completion_time.toLocaleString('en-GB', { timeZone: 'Europe/Paris' })})
      }
      course['views'] = views;
      final_result.push(course);
      res.end(JSON.stringify(final_result))
    }
    else {
      res.end(JSON.stringify([{"id":0}]));
    }
  })
})

//Get pdf views count
app.get('/pdf/count/:year/:user', function(req, res) {
  let year = req.params.year
  let user = req.params.user

  con.query(`select COUNT(id_pdf) as 'count', MONTH(completion_time) as 'month'
            from pdfviews
            where id_user = ? and YEAR(completion_time) = ?
            group by 2
            order by 2`, [user,year], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify([{"count":0,"month":0}]));
    }
  })
})

//Get video views
app.get('/video/completed/:user', function(req, res) {
  let user = req.params.user

  con.query(`select c.id, c.name cname, p.name pname, v.completion_time
             from pdf p, videoviews v, course c
             where p.id = v.id_video
             and c.id = p.id_course
             and v.id_user = ?
             order by c.id, v.completion_time;`, [user], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      let course = {id : result[0].id, name : result[0].cname };
      let views = new Array();
      let final_result = new Array();

      for (i = 0; i < result.length; i++) {
        if (course.id != result[i].id) {
          course['views'] = views;
          final_result.push(course);
          course = {id : result[i].id, name : result[i].cname }
          views = new Array();
        }

        views.push({"name":result[i].pname,"completion_time":result[i].completion_time.toLocaleString('en-GB', { timeZone: 'Europe/Paris' })})
      }
      course['views'] = views;
      final_result.push(course);
      res.end(JSON.stringify(final_result))
    }
    else {
      res.end(JSON.stringify([{"id":0}]));
    }
  })
})

//Get video views count
app.get('/video/count/:year/:user', function(req, res) {
  let year = req.params.year
  let user = req.params.user

  con.query(`select COUNT(id_video) as 'count', MONTH(completion_time) as 'month'
             from videoviews
             where id_user = ? and YEAR(completion_time) = ?
             group by 2
             order by 2;`, [user,year], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify([{"count":0,"month":0}]));
    }
  })
})

//Set video views
app.get('/video/set/:video/:user', function(req, res) {
  let video = req.params.video
  let user = req.params.user

  con.query(`select *
             from videoviews
             where id_user = ? and id_video = ?
            `, [user,video], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify("Success"))
    }
    else {
      con.query("INSERT INTO videoviews (id_video, id_user, completion_time) VALUES (?,?,now())", [video, user], function(err, result, fields) {
        con.on('error',function(err) {
          console.log('[MySQL ERROR]', err);
          if (err) {
              throw err;
              res.end(JSON.stringify("Error"));
          }
        });
        res.end(JSON.stringify("Success"));
      });
    }
  })
})

//Set pdf views
app.get('/pdf/set/:pdf/:user', function(req, res) {
  let pdf = req.params.pdf
  let user = req.params.user

  con.query(`select *
             from pdfviews
             where id_user = ? and id_pdf = ?
            `, [user,pdf], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify("Success"))
    }
    else {
      con.query("INSERT INTO pdfviews (id_pdf, id_user, completion_time) VALUES (?,?,now())", [pdf, user], function(err, result, fields) {
        con.on('error',function(err) {
          console.log('[MySQL ERROR]', err);
          if (err) {
              throw err;
              res.end(JSON.stringify("Error"));
          }
        });
        res.end(JSON.stringify("Success"));
      });
    }
  })
})

//Get Quiz and Result
app.get('/quiz/:id/:user', function(req, res) {
  let quiz = req.params.id
  let user = req.params.user

  con.query(`select a.id_question, q.question, a.id, a.answer,
                  a.correct, qr.id_answer user_answer
             from question q inner join answer a
	                on q.id_quiz = ? and q.id = a.id_question
             left outer join quiz_result qr
                  on qr.id_quiz = q.id_quiz and qr.id_question = a.id_question
                  and qr.id_user = ?
             order by a.id_question, a.id;`,
  [quiz, user], function(error, result, fields) {

    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      let quizjson = new Array();
      for (i = 0; i < result.length; i+=4) {
        a1 = {id: result[i].id, answer: result[i].answer, correct: result[i].correct}
        a2 = {id: result[i+1].id, answer: result[i+1].answer, correct: result[i+1].correct}
        a3 = {id: result[i+2].id, answer: result[i+2].answer, correct: result[i+2].correct}
        a4 = {id: result[i+3].id, answer: result[i+3].answer, correct: result[i+3].correct}
        q = {id_question: result[i].id_question, question: result[i].question}
        if (result[i].user_answer)
          q['user_answer'] = result[i].user_answer
        q['answers'] = [a1,a2,a3,a4];
        quizjson.push(q);
      }
      res.end(JSON.stringify(quizjson))
    }
    else {
      res.end(JSON.stringify([{"id_question":0}]));
    }
  })
})

//Get Quiz List
app.get('/quiz/list/:course/:user', function(req, res) {
  let course = req.params.course
  let user = req.params.user

  con.query(`select distinct q.id, q.id_course, q.name,
	               not isnull(qr.id_user) viewed
             from quiz q
             left outer join quiz_result qr
             on qr.id_quiz = q.id
             where q.id_course = ? and (qr.id_user = ? or isnull(qr.id_user))
             order by q.id;`,
  [course, user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify([{"id":0,"id_course":0,"name":"No quizzes","viewed":0}]));
    }
  })
})

//Save quiz answers
app.post('/quiz/answer', (req, res, next) => {
  var post_data = req.body;
  var answers = post_data.answers;
  var user_id = post_data.id_user;
  var quiz_id = post_data.id_quiz;

  var n_answers = answers.length;
  var inserted_answers = 0;

  con.beginTransaction(function (err) {
    answers.forEach(function(answer) {
      con.query("INSERT INTO quiz_result (id_quiz, id_question, id_answer, id_user) VALUES (?,?,?,?)",
      [quiz_id, answer.id_question, answer.user_answer, user_id],
      function(err, result, fields) {
        con.on('error',function(err) {
          console.log('[MySQL ERROR]', err);
          if (err) {
            con.rollback(function() {
              throw err;
              res.end(JSON.stringify('Error'));
            });
          }
        });

        inserted_answers++;
        if (inserted_answers == n_answers) {
          con.commit(function(err) {
            if (err) {
              con.rollback(function() {
                throw err;
                res.end(JSON.stringify('Error'));
              });
            }
            res.end(JSON.stringify('Success'));
          });
        }
      });
    });
  });
});

app.get('/grades/:course/:user', function(req, res) {
  let course = req.params.course
  let user = req.params.user

  con.query(`select qr.id_quiz, q.name, a.correct, count(qr.id_question) count from
             quiz_result qr, answer a, quiz q
             where qr.id_answer = a.id and q.id = qr.id_quiz
             and q.id_course = ? and qr.id_user = ?
             group by qr.id_quiz, q.name, a.correct
             order by id_quiz, a.correct`,
  [course, user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      let grades = new Array();
      for (i = 0; i < result.length; i++) {
        let qr = {id_quiz : result[i].id_quiz, name : result[i].name}
        let gr = new Array();

        if (i == result.length - 1) {
          gr.push({correct : result[i].correct, count : result[i].count});
        }
        else {
          if (result[i].id_quiz == result[i+1].id_quiz) {
            gr.push({correct : result[i].correct, count : result[i].count});
            i++;
            gr.push({correct : result[i].correct, count : result[i].count});
          }
          else {
            gr.push({correct : result[i].correct, count : result[i].count});
          }
        }

        qr['grades'] = gr;
        grades.push(qr);
      }
      res.end(JSON.stringify(grades))
    }
    else {
      res.end(JSON.stringify([{"id_quiz":0,"name":"No grades for this course"}]));
    }
  })
});

//Get Student List
app.get('/student/list/:user', function(req, res) {
  let user = req.params.user

  con.query(`select s.* from user p, user s
             where s.id_parent = p.id
             and p.id = ?;`,
  [user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      result.forEach (function (u) {
        var localDate = new Date(u.last_login);
        u.last_login = localDate.toLocaleString('en-GB', { timeZone: 'Europe/Paris' });
      });

      res.end(JSON.stringify(result))
    }
    else {
      res.end(JSON.stringify([{"id":0}]));
    }
  })
})

//Get Grades Student
app.get('/allgrades/:user', function(req, res) {
  let user = req.params.user

  con.query(`select q.id_course, c.name course_name, qr.id_quiz, q.name quiz_name, a.correct, count(qr.id_question) count from
             quiz_result qr, answer a, quiz q, course c
             where qr.id_answer = a.id and q.id = qr.id_quiz
             and qr.id_user = ?
             and c.id = q.id_course
             group by c.id, c.name, qr.id_quiz, q.name, a.correct
             order by c.id, id_quiz, a.correct;`,
  [user], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      final_result = new Array();
      course = new Object();
      r = new Array();
      let qr = null;

      course['course_name'] = result[0].course_name;
      for (i = 0; i < result.length; i++) {
        if (course['course_name'] != result[i].course_name) {
          course['results'] = r;
          final_result.push(course);
          course = new Object();
          r = new Array();
          course['course_name'] = result[i].course_name;
        }

        qr = {id_quiz : result[i].id_quiz, name : result[i].quiz_name}
        let gr = new Array();

        if (i == result.length - 1) {
          gr.push({correct : result[i].correct, count : result[i].count});
        }
        else {
          if (result[i].id_quiz == result[i+1].id_quiz) {
            gr.push({correct : result[i].correct, count : result[i].count});
            i++;
            gr.push({correct : result[i].correct, count : result[i].count});
          }
          else {
            gr.push({correct : result[i].correct, count : result[i].count});
          }
        }

        qr['grades'] = gr;
        r.push(qr);
      }
      course['results'] = r;
      final_result.push(course);
      res.end(JSON.stringify(final_result))
    }
    else {
      res.end(JSON.stringify([{"course_name":""}]));
    }
  })
})

//Send email
app.get('/email/', function(req,res) {
  sendEmail('leomoros90v@gmail.com',shortid.generate(),res);
})

app.listen(8888, () => {
  console.log('Server listening on port 8888');
});
