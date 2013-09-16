create sequence credit_id_seq start with 100;

create table credit (id integer primary key default nextval('credit_id_seq')
,description text);

alter table payment add column credit_id integer;
