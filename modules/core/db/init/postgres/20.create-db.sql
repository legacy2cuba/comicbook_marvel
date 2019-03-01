-- begin TBLCATALOG
alter table tblcatalog add constraint FK_TBLCATALOG_ON_TITLENUM foreign key (TITLENUM) references tbltitle(titlenum)^
alter table tblcatalog add constraint FK_TBLCATALOG_ON_LOCATIONNUM foreign key (LOCATIONNUM) references tbl_location(locationnum)^
create index IDX_TBLCATALOG_ON_TITLENUM on tblcatalog (TITLENUM)^
create index IDX_TBLCATALOG_ON_LOCATIONNUM on tblcatalog (LOCATIONNUM)^
-- end TBLCATALOG
