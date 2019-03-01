create table qry_rpt_cost (
    rownum bigint,
    --
    sum_of_cost double precision,
    avg_of_cost varchar(40),
    min_of_cost varchar(40),
    max_of_cost varchar(40),
    count_of_qry_rpt_catalog varchar(40),
    --
    primary key (rownum)
);
