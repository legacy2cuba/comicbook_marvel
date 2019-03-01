update tbl_location set LOCATION = '' where LOCATION is null ;
alter table tbl_location alter column LOCATION set not null ;
