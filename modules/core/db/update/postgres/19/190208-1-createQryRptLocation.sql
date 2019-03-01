create table qry_rpt_location (
    rownum bigint,
    --
    sort_order integer,
    location varchar(50),
    publisher varchar(20),
    title varchar(50),
    issuenum double precision,
    comments varchar(90),
    issue_date timestamp,
    cost double precision,
    --
    primary key (rownum)
);
