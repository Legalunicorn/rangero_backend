INSERT INTO _user(email,username,ROLE,password,enabled)
VALUES ('rangero.help@gmail.com','test','USER','$2a$10$kQl.WKWO0lUphaD6EOZJEOdkVG7KKg5XgXSVYlaC42gfrhQ6c8y0K',true);
INSERT INTO _user(email,username,ROLE,password,enabled)
VALUES ('test2@gmail.com','2test','USER','$2a$10$kQl.WKWO0lUphaD6EOZJEOdkVG7KKg5XgXSVYlaC42gfrhQ6c8y0K',true);
INSERT INTO _user(email,username,ROLE,password,enabled)
VALUES ('3test@gmail.com','3testz','USER','$2a$10$kQl.WKWO0lUphaD6EOZJEOdkVG7KKg5XgXSVYlaC42gfrhQ6c8y0K',true);

INSERT INTO project(strict_mode, created_on, creator_id,id,updated_on,name)
VALUES ('f','2025-03-20 15:27:27.920793',1,1,'2025-03-20 15:27:27.920793','first project');

INSERT INTO project_member(id,project_id,user_id,project_role)
VALUES(1,1,1,'OWNER');
INSERT INTO project_member(id,project_id,user_id,project_role)
VALUES(2,1,2,'ADMIN');

INSERT INTO task(due_date,assignee_id,id,project_id,priority,status,title,created_on,updated_on)
VALUES ('2025-03-18',1,1,1,'LOW','UNASSIGNED','new task-1','2025-03-20 15:27:27.920793','2025-03-20 15:27:27.920793');


--Change the SEQUAL T
ALTER SEQUENCE task_id_seq RESTART WITH 1000;
ALTER SEQUENCE project_id_seq RESTART WITH 1000;
ALTER SEQUENCE project_member_id_seq RESTART WITH 1000;
ALTER SEQUENCE _user_id_seq RESTART WITH 1000;