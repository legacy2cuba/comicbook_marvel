-- begin SWITCHBOARD_ITEMS
create table switchboard_items (
    id uuid,
    --
    switchboardid integer,
    itemnumber integer,
    itemtext varchar(255),
    command integer,
    argument varchar(50),
    --
    primary key (id)
)^
-- end SWITCHBOARD_ITEMS
-- begin TBL_LOCATION
create table tbl_location (
    locationnum integer,
    --
    location varchar(50) not null,
    sort_order integer,
    --
    primary key (locationnum)
)^
-- end TBL_LOCATION
-- begin TBLTITLE
create table tbltitle (
    titlenum integer,
    --
    title varchar(50),
    series varchar(50),
    marvelSeriesId integer,
    publisher varchar(20),
    current boolean,
    --
    primary key (titlenum)
)^
-- end TBLTITLE
-- begin TBLCATALOG
create table tblcatalog (
    comicbooknum integer,
    --
    issuenum double precision,
    cost double precision,
    issue_date date,
    comicTitle varchar(90),
    marvelComicId integer,
    comments varchar(90),
    titlenum integer,
    locationnum integer,
    --
    primary key (comicbooknum)
)^
-- end TBLCATALOG