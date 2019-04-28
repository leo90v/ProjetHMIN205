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
        res.end(JSON.stringify(result[0]));
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

//Send email
app.get('/email/', function(req,res) {
  sendEmail('leomoros90v@gmail.com',shortid.generate(),res);
})

app.listen(8888, () => {
  console.log('Server listening on port 8888');
});
