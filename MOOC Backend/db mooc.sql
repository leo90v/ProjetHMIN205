use MOOC;

drop table quiz_result;
drop table answer;
drop table question;
drop table quiz;
drop table pdfviews;
drop table videoviews;
drop table user_validation;
drop table user;
drop table video;
drop table pdf;
drop table course;

#CREATE TABLES
create table user(
	id integer AUTO_INCREMENT, 
	name varchar(50),
	last_name varchar(50), 
	email varchar(100), 
	password varchar(128),
	salt varchar(16),
	user_type integer,
    grade integer DEFAULT 0,
	active integer DEFAULT 0,
    id_parent integer DEFAULT 0,
	primary key(id)
);

create table user_validation(
	id integer AUTO_INCREMENT, 
	email varchar(100), 
	short_id varchar(12),
	primary key(id)
);

create table course(
	id integer,
    grade integer,
    name varchar(100),
    description varchar(1000),
    primary key(id)
);

create table video(
	id integer,
    id_course integer,
    filename varchar(50),
    name varchar(100),
    primary key(id),
    foreign key (id_course) references course(id)
);

create table pdf(
	id integer,
    id_course integer,
    filename varchar(50),
    name varchar(100),
    primary key(id),
    foreign key (id_course) references course(id)
);

create table pdfviews(
	id integer AUTO_INCREMENT,
    id_pdf integer,
    id_user integer,
    completion_time datetime,
    primary key(id),
    foreign key (id_pdf) references pdf(id),
    foreign key (id_user) references user(id)
);

create table videoviews(
	id integer AUTO_INCREMENT,
    id_video integer,
    id_user integer,
    completion_time datetime,
    primary key(id),
    foreign key (id_video) references video(id),
    foreign key (id_user) references user(id)
);

create table quiz(
	id integer AUTO_INCREMENT,
    name varchar(100),
    id_course integer,
    primary key(id),
    foreign key (id_course) references course(id)
);

create table question(
	id integer AUTO_INCREMENT,
    id_quiz integer,
    question varchar(100),
    primary key(id),
    foreign key (id_quiz) references quiz(id)
);

create table answer(
	id integer AUTO_INCREMENT,
    id_question integer,
    answer varchar(100),
    correct integer,
    primary key(id),
    foreign key (id_question) references question(id)
);

create table quiz_result(
    id_quiz integer,
    id_question integer,
    id_answer integer,
    id_user integer,
    primary key(id_quiz, id_question, id_user)
);

#COURSES INSERTS
insert into user values (1,'Jane','Doe','jane@gmail.com','02ca39431c68cc2edf6d1068fa73d6225307caf5155b2df293505de8894559d9e0669db982dae673f30e79ef63a16180e93d86ecf578ae3edc13e3d7e33a7a89','22ca24db59b3f5b8',2,0,1,0);
insert into user values (2,'John','Doe','john@gmail.com','02ca39431c68cc2edf6d1068fa73d6225307caf5155b2df293505de8894559d9e0669db982dae673f30e79ef63a16180e93d86ecf578ae3edc13e3d7e33a7a89','22ca24db59b3f5b8',1,4,1,1);

insert into course values (1, 4, 'Introduction to computer science and programming','This subject is aimed at students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems. It also aims to help students, regardless of their major, to feel justifiably confident of their ability to write small programs that allow them to accomplish useful goals. The class will use the Python programming language.');
insert into course values (2, 4, 'Introduction to computational thinking and data science','6.0002 is the continuation of 6.0001 Introduction to Computer Science and Programming in Python and is intended for students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems and to help students, regardless of their major, feel justifiably confident of their ability to write small programs that allow them to accomplish useful goals. The class uses the Python 3.5 programming language.');
insert into course values (3, 4, 'Mathematics for computer science','This subject offers an interactive introduction to discrete mathematics oriented toward computer science and engineering. On completion of 6.042J, students will be able to explain and apply the basic methods of discrete (noncontinuous) mathematics in computer science. They will be able to use these methods in subsequent courses in the design and analysis of algorithms, computability theory, software engineering, and computer systems.');

insert into video values (1, 1, 'MIT6_00SCS11_lec01_300k.mp4','Introduction');
insert into video values (2, 1, 'MIT6_00SCS11_lec02_300k.mp4','Core elements of a program');
insert into video values (3, 1, 'MIT6_00SCS11_lec03_300k.mp4','Problem solving');

insert into video values (4, 2, 'MIT6_0002F16_lec01_300k.mp4','Introduction');
insert into video values (5, 2, 'MIT6_0002F16_lec02_300k.mp4','Optimization problems');
insert into video values (6, 2, 'MIT6_0002F16_lec03_300k.mp4','Graph models');
insert into video values (7, 2, 'MIT6_0002F16_lec04_300k.mp4','Stochastic thinking');

insert into video values (8, 3, 'MIT6_042JS15_welcome_6042S15_ipod.mp4','Introduction');
insert into video values (9, 3, 'MIT6_042JS15_proof1_ipod.mp4','Proofs: Part 1');
insert into video values (10, 3, 'MIT6_042JS15_proof2_ipod.mp4','Proofs: Part 2');
insert into video values (11, 3, 'MIT6_042JS15_contradiction_ipod.mp4','Proof by contradiction');

insert into pdf values (1, 1, 'MIT6_00SCS11_lec01_slides.pdf','Introduction');
insert into pdf values (2, 1, 'MIT6_00SCS11_lec02.pdf','Core elements of a program');
insert into pdf values (3, 1, 'MIT6_00SCS11_lec03.pdf','Problem solving');

insert into pdf values (4, 2, 'MIT6_0002F16_lec1.pdf','Introduction');
insert into pdf values (5, 2, 'MIT6_0002F16_lec2.pdf','Optimization problems');
insert into pdf values (6, 2, 'MIT6_0002F16_lec3.pdf','Graph models');
insert into pdf values (7, 2, 'MIT6_0002F16_lec4.pdf','Stochastic thinking');

insert into pdf values (8, 3, 'MIT6_042JS16_Welcome6.042.pdf','Introduction');
insert into pdf values (9, 3, 'MIT6_042JS16_Introduction.pdf','Proofs');
insert into pdf values (10, 3, 'MIT6_042JS16_ProofContrad.pdf','Proof by contradiction');

#PROGRESSION INSERTS
insert into pdfviews (id_pdf,id_user,completion_time) values (1,2,'2019-01-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (2,2,'2019-01-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (3,2,'2019-02-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (4,2,'2019-03-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (5,2,'2019-03-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (6,2,'2019-03-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (7,2,'2019-04-01 00:00:00');
insert into pdfviews (id_pdf,id_user,completion_time) values (8,2,'2019-04-01 00:00:00');
#insert into pdfviews (id_pdf,id_user,completion_time) values (9,2,'2019-04-01 00:00:00');
#insert into pdfviews (id_pdf,id_user,completion_time) values (10,2,'2019-05-01 00:00:00');

insert into videoviews (id_video,id_user,completion_time) values (1,2,'2019-01-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (2,2,'2019-02-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (3,2,'2019-03-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (4,2,'2019-04-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (5,2,'2019-05-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (6,2,'2019-06-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (7,2,'2019-07-01 00:00:00');
insert into videoviews (id_video,id_user,completion_time) values (8,2,'2019-08-01 00:00:00');
#insert into videoviews (id_video,id_user,completion_time) values (9,2,'2019-09-01 00:00:00');
#insert into videoviews (id_video,id_user,completion_time) values (10,2,'2019-10-01 00:00:00');
#insert into videoviews (id_video,id_user,completion_time) values (11,2,'2019-11-01 00:00:00');

#DEMO INSERTS
insert into user values (0,'Demo','Demo','demo@gmail.com','demo','demo',1,0,1,0);

insert into course values (4, 0, 'Introduction to computer science and programming','This subject is aimed at students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems. It also aims to help students, regardless of their major, to feel justifiably confident of their ability to write small programs that allow them to accomplish useful goals. The class will use the Python programming language.');
insert into course values (5, 0, 'Introduction to computational thinking and data science','6.0002 is the continuation of 6.0001 Introduction to Computer Science and Programming in Python and is intended for students with little or no programming experience. It aims to provide students with an understanding of the role computation can play in solving problems and to help students, regardless of their major, feel justifiably confident of their ability to write small programs that allow them to accomplish useful goals. The class uses the Python 3.5 programming language.');
insert into course values (6, 0, 'Mathematics for computer science','This subject offers an interactive introduction to discrete mathematics oriented toward computer science and engineering. On completion of 6.042J, students will be able to explain and apply the basic methods of discrete (noncontinuous) mathematics in computer science. They will be able to use these methods in subsequent courses in the design and analysis of algorithms, computability theory, software engineering, and computer systems.');

insert into video values (12, 4, 'MIT6_00SCS11_lec01_300k.mp4','Introduction');
insert into video values (13, 4, 'MIT6_00SCS11_lec02_300k.mp4','Core elements of a program');
insert into video values (14, 4, 'MIT6_00SCS11_lec03_300k.mp4','Problem solving');

insert into video values (15, 5, 'MIT6_0002F16_lec01_300k.mp4','Introduction');
insert into video values (16, 5, 'MIT6_0002F16_lec02_300k.mp4','Optimization problems');
insert into video values (17, 5, 'MIT6_0002F16_lec03_300k.mp4','Graph models');
insert into video values (18, 5, 'MIT6_0002F16_lec04_300k.mp4','Stochastic thinking');

insert into video values (19, 6, 'MIT6_042JS15_welcome_6042S15_ipod.mp4','Introduction');
insert into video values (20, 6, 'MIT6_042JS15_proof1_ipod.mp4','Proofs: Part 1');
insert into video values (21, 6, 'MIT6_042JS15_proof2_ipod.mp4','Proofs: Part 2');
insert into video values (22, 6, 'MIT6_042JS15_contradiction_ipod.mp4','Proof by contradiction');

insert into pdf values (11, 4, 'MIT6_00SCS11_lec01_slides.pdf','Introduction');
insert into pdf values (12, 4, 'MIT6_00SCS11_lec02.pdf','Core elements of a program');
insert into pdf values (13, 4, 'MIT6_00SCS11_lec03.pdf','Problem solving');

insert into pdf values (14, 5, 'MIT6_0002F16_lec1.pdf','Introduction');
insert into pdf values (15, 5, 'MIT6_0002F16_lec2.pdf','Optimization problems');
insert into pdf values (16, 5, 'MIT6_0002F16_lec3.pdf','Graph models');
insert into pdf values (17, 5, 'MIT6_0002F16_lec4.pdf','Stochastic thinking');

insert into pdf values (18, 6, 'MIT6_042JS16_Welcome6.042.pdf','Introduction');
insert into pdf values (19, 6, 'MIT6_042JS16_Introduction.pdf','Proofs');
insert into pdf values (20, 6, 'MIT6_042JS16_ProofContrad.pdf','Proof by contradiction');

#QUIZ INSERTS
insert into quiz values (1,'Quiz 1',3);

insert into question values (1,1,'Question 1');
insert into question values (2,1,'Question 2');
insert into question values (3,1,'Question 3');
insert into question values (4,1,'Question 4');

insert into answer values (1,1,'Answer 1',1);
insert into answer values (2,1,'Answer 2',0);
insert into answer values (3,1,'Answer 3',0);
insert into answer values (4,1,'Answer 4',0);

insert into answer values (5,2,'Answer 1',0);
insert into answer values (6,2,'Answer 2',1);
insert into answer values (7,2,'Answer 3',0);
insert into answer values (8,2,'Answer 4',0);

insert into answer values (9,3,'Answer 1',0);
insert into answer values (10,3,'Answer 2',0);
insert into answer values (11,3,'Answer 3',1);
insert into answer values (12,3,'Answer 4',0);

insert into answer values (13,4,'Answer 1',0);
insert into answer values (14,4,'Answer 2',0);
insert into answer values (15,4,'Answer 3',0);
insert into answer values (16,4,'Answer 4',1);

insert into quiz_result values (1,1,1,2);
insert into quiz_result values (1,2,5,2);
insert into quiz_result values (1,3,9,2);
insert into quiz_result values (1,4,13,2);

insert into quiz values (2,'Quiz 2',3);

insert into question values (5,2,'Question 1');
insert into question values (6,2,'Question 2');
insert into question values (7,2,'Question 3');
insert into question values (8,2,'Question 4');

insert into answer values (17,5,'Answer 1',1);
insert into answer values (18,5,'Answer 2',0);
insert into answer values (19,5,'Answer 3',0);
insert into answer values (20,5,'Answer 4',0);

insert into answer values (21,6,'Answer 1',0);
insert into answer values (22,6,'Answer 2',1);
insert into answer values (23,6,'Answer 3',0);
insert into answer values (24,6,'Answer 4',0);

insert into answer values (25,7,'Answer 1',0);
insert into answer values (26,7,'Answer 2',0);
insert into answer values (27,7,'Answer 3',1);
insert into answer values (28,7,'Answer 4',0);

insert into answer values (29,8,'Answer 1',0);
insert into answer values (30,8,'Answer 2',0);
insert into answer values (31,8,'Answer 3',0);
insert into answer values (32,8,'Answer 4',1);


insert into quiz values (3,'Quiz 1',2);

insert into question values (9,3,'Question 1');
insert into question values (10,3,'Question 2');
insert into question values (11,3,'Question 3');
insert into question values (12,3,'Question 4');

insert into answer values (33,9,'Answer 1',1);
insert into answer values (34,9,'Answer 2',0);
insert into answer values (35,9,'Answer 3',0);
insert into answer values (36,9,'Answer 4',0);

insert into answer values (37,10,'Answer 1',0);
insert into answer values (38,10,'Answer 2',1);
insert into answer values (39,10,'Answer 3',0);
insert into answer values (40,10,'Answer 4',0);

insert into answer values (41,11,'Answer 1',0);
insert into answer values (42,11,'Answer 2',0);
insert into answer values (43,11,'Answer 3',1);
insert into answer values (44,11,'Answer 4',0);

insert into answer values (45,12,'Answer 1',0);
insert into answer values (46,12,'Answer 2',0);
insert into answer values (47,12,'Answer 3',0);
insert into answer values (48,12,'Answer 4',1);

insert into quiz_result values (3,9,33,2);
insert into quiz_result values (3,10,38,2);
insert into quiz_result values (3,11,42,2);
insert into quiz_result values (3,12,45,2);