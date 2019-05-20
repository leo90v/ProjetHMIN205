use MOOC;

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
