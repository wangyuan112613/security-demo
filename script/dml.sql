insert into user(id,username,email,password_hash)
values
(1,"admin","www.admin@123.com","$2a$10$AVFHJs6suIgMeJZSn7RVgu53ZXqXVSFPR7OgfzW99vg2BWLxLehhG"),
(2,"user","www.user@123.com","$2a$10$xBSwyVW0M0qwLcrPiGKA6egXQCyA3Fn1OLeKEsXldnyk.i8Cam7AO");

INSERT INTO `role` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER'),(3,'ROLE_TEST');

insert into authority
values
(1,"user::get"),
(2,"item::get");

insert into user_role
values
(1,1),
(2,2);


INSERT INTO `role_authority`
VALUES
(1,1),
(2,2);