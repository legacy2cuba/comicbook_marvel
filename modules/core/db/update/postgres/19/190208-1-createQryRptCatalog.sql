create table qry_rpt_catalog (
    rownum bigint,
    --
    title varchar(50),
    issuenum double precision,
    comicbooknum integer,
    publisher varchar(20),
    cost double precision,
    issue_date timestamp,
    comments varchar(90),
    titlenum integer,
    location varchar(50),
    --
    primary key (rownum)
);
