# --- !Ups

create table "CAR" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"title" VARCHAR NOT NULL,"fuel" VARCHAR NOT NULL,"price" INTEGER NOT NULL,"new" BOOLEAN NOT NULL,"mileage" INTEGER,"firstRegistration" DATE);
create table "FUEL" ("name"  VARCHAR NOT NULL PRIMARY KEY);
insert into "FUEL" values ('gasoline'), ('diesel'); 

# --- !Downs

drop table "CAR";
drop table "FUEL";
