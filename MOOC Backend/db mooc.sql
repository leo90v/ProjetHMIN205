use MOOC;

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
	active integer DEFAULT 0,
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
    grade varchar(15),
    name varchar(50),
    primary key(id)
);

create table video(
	id integer,
    id_course integer,
    filename varchar(50),
    primary key(id),
    foreign key (id_course) references course(id)
);

create table pdf(
	id integer,
    id_course integer,
    filename varchar(50),
    primary key(id),
    foreign key (id_course) references course(id)
);

#INSERTS
insert into user values (1,'John','Doe','john@gmail.com','02ca39431c68cc2edf6d1068fa73d6225307caf5155b2df293505de8894559d9e0669db982dae673f30e79ef63a16180e93d86ecf578ae3edc13e3d7e33a7a89','22ca24db59b3f5b8',1,1);

insert into course values (1, '4', 'Introduction to computer science and programming');

insert into video values (1, 1, 'MIT6_00SCS11_lec01_300k.mp4');
insert into video values (2, 1, 'MIT6_00SCS11_lec02_300k.mp4');
insert into video values (3, 1, 'MIT6_00SCS11_lec03_300k.mp4');

insert into pdf values (1, 1, 'MIT6_00SCS11_lec01_slides.pdf');
insert into pdf values (2, 1, 'MIT6_00SCS11_lec02.pdf');
insert into pdf values (3, 1, 'MIT6_00SCS11_lec03.pdf');