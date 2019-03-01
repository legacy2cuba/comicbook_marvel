create table qry_rpt_age (
    rownum bigint,
    --
    issue_date timestamp,
    title varchar(50),
    issuenum double precision,
    publisher varchar(20),
    cost double precision,
    comments varchar(90),
    location varchar(50),
    --
    primary key (rownum)
);
