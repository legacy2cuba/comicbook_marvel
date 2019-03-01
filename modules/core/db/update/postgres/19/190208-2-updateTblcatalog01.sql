alter table tblcatalog add constraint FK_TBLCATALOG_ON_LOCATIONNUM foreign key (LOCATIONNUM) references tbl_location(locationnum);
create index IDX_TBLCATALOG_ON_LOCATIONNUM on tblcatalog (LOCATIONNUM);
