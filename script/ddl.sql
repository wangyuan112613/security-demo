create table if not exists user
(
    id bigint auto_increment primary key,
    username varchar(50) not null,
    email varchar(50) not null,
    password_hash varchar(60) not null
);

create table if not exists role
(
     id bigint auto_increment primary key,
    name varchar(50) not null primary key
);

create table if not exists user_role
(
    user_id bigint not null,
    role_id bigint not null,
    constraint fk_role_id
		foreign key (role_id) references role (id),
	constraint fk_user_id
		foreign key (user_id) references user (id)
);

create table if not exists authority
(
     id bigint auto_increment primary key,
    name varchar(50) not null primary key
);

create table if not exists role_authority
(
    role_id bigint not null,
    authority_id bigint not null,
    constraint fk_authority_id
		foreign key (authority_id) references authority (id),
	constraint fk_role_id
		foreign key (role_id) references role (id)
);