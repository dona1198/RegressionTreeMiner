use MapDB;
create table MapDB.prova(
	X varchar(10),
    Y varchar(10),
    C float(5, 2)
);

insert into MapDB.prova values('A','A',1);
insert into MapDB.prova values('A','A',1);
insert into MapDB.prova values('A','A',1);
insert into MapDB.prova values('A','A',1);
insert into MapDB.prova values('A','B',1.5);
insert into MapDB.prova values('A','B',1.5);
insert into MapDB.prova values('A','B',1.5);
insert into MapDB.prova values('B','B',10);
insert into MapDB.prova values('A','B',1.5);
insert into MapDB.prova values('A','B',1.5);
insert into MapDB.prova values('B','C',10);
insert into MapDB.prova values('B','B',10);
insert into MapDB.prova values('B','C',10);
insert into MapDB.prova values('B','C',10);
insert into MapDB.prova values('A','A',1);
commit;