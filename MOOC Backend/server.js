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
app.post('/signup/', (req, res, next) => {
  var post_data = req.body;
  var plaint_password = post_data.password;
  var hash_data = saltHashPassword(plaint_password);
  var password = hash_data.passwordHash;
  var salt = hash_data.salt;

  var name = post_data.name;
  var last_name = post_data.last_name;
  var email = post_data.email;
  var user_type = post_data.user_type;

  con.query('SELECT * FROM user WHERE email=?',[email],function(err, result, fields) {
    con.on('error',function(err) {
      console.log('[MySQL ERROR]', err);
    });

    if (result && result.length)
      res.json('User already exists!');
    else {
      con.query("INSERT INTO user (name, last_name, email, password, salt, user_type) VALUES (?,?,?,?,?,?)", [name, last_name, email, password, salt, user_type], function(err, result, fields) {
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
app.post('/validate/', (req, res, next) => {
  var post_data = req.body;
  var short_id = post_data.short_id;
  var email = post_data.email;

  con.query('SELECT * FROM user_validation WHERE email=? AND short_id=?', [email,short_id], function(error, result, fields) {
    con.on('error', function(err) {
      console.log('[MySQL ERROR]', err);
      res.json('Error: ', err);
    })

    if (result && result.length) {
      con.query('DELETE FROM user_validation WHERE email=? AND short_id=?', [email,short_id], function(error, result, fields) {
        con.on('error', function(err) {
          console.log('[MySQL ERROR]', err);
          res.json('Error: ', err);
        })
        con.query('UPDATE user SET active=1 WHERE email=?', [email], function(error, result, fields) {
          con.on('error', function(err) {
            console.log('[MySQL ERROR]', err);
            res.json('Error: ', err);
          })
          res.end(JSON.stringify('User validated'));
        })
      })
    }
    else {
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
      res.end(JSON.stringify('Invalid grade'));
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
