CREATE OR REPLACE VIEW public.qry_list_location AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.location,
    view_table.sort_order
   FROM ( SELECT tbl_location.location,
            tbl_location.sort_order
           FROM tbl_location
          ORDER BY tbl_location.sort_order) view_table;

CREATE OR REPLACE VIEW public.qry_list_publisher AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.publisher
   FROM ( SELECT tbltitle.publisher
           FROM tbltitle
          GROUP BY tbltitle.publisher
          ORDER BY tbltitle.publisher) view_table;

CREATE OR REPLACE VIEW public.qry_list_series AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.series
   FROM ( SELECT tbltitle.series
           FROM tbltitle
          ORDER BY tbltitle.series) view_table;

CREATE OR REPLACE VIEW public.qry_list_title AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.title,
    view_table.publisher
   FROM ( SELECT tbltitle.title,
            tbltitle.publisher
           FROM tbltitle
          ORDER BY tbltitle.title, tbltitle.publisher) view_table;

CREATE OR REPLACE VIEW public.qry_rpt_age AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    tblcatalog.issue_date,
    tbltitle.title,
    tblcatalog.issuenum,
    tbltitle.publisher,
    tblcatalog.cost,
    tblcatalog.comments,
    tbl_location.location
   FROM tbltitle,
    tblcatalog,
    tbl_location
  WHERE tbltitle.titlenum = tblcatalog.titlenum AND tbl_location.locationnum = tblcatalog.locationnum AND tblcatalog.issue_date < '1990-01-01 00:00:00'::timestamp without time zone
  ORDER BY tblcatalog.issue_date, tbltitle.title, tblcatalog.issuenum;

CREATE OR REPLACE VIEW public.qry_rpt_catalog AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    tbltitle.title,
    tblcatalog.issuenum,
    tblcatalog.comicbooknum,
    tbltitle.publisher,
    tblcatalog.cost,
    tblcatalog.issue_date,
    tblcatalog.comments,
    tblcatalog.titlenum,
    tbl_location.location
   FROM tbltitle,
    tblcatalog,
    tbl_location
  WHERE tbltitle.titlenum = tblcatalog.titlenum AND tbl_location.locationnum = tblcatalog.locationnum
  ORDER BY tbltitle.title, tblcatalog.issuenum;

CREATE OR REPLACE VIEW public.qry_rpt_cost AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    sum(qry_rpt_catalog.cost) AS sum_of_cost,
    avg(qry_rpt_catalog.cost) AS avg_of_cost,
    min(qry_rpt_catalog.cost) AS min_of_cost,
    max(qry_rpt_catalog.cost) AS max_of_cost,
    count(*) AS count_of_qry_rpt_catalog
   FROM qry_rpt_catalog;

CREATE OR REPLACE VIEW public.qry_rpt_location AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    tbl_location.sort_order,
    tbl_location.location,
    tbltitle.publisher,
    tbltitle.title,
    tblcatalog.issuenum,
    tblcatalog.comments,
    tblcatalog.issue_date,
    tblcatalog.cost
   FROM tbltitle,
    tbl_location,
    tblcatalog
  WHERE tbl_location.locationnum = tblcatalog.locationnum AND tbltitle.titlenum = tblcatalog.titlenum
  ORDER BY tbl_location.sort_order, tbltitle.title, tblcatalog.issuenum;

CREATE OR REPLACE VIEW public.qry_rpt_publisher AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.publisher,
    view_table.countofissuenum,
    view_table.sumofcost
   FROM ( SELECT tbltitle.publisher,
            count(tblcatalog.issuenum) AS countofissuenum,
            sum(tblcatalog.cost) AS sumofcost
           FROM tbltitle
             JOIN tblcatalog ON tbltitle.titlenum = tblcatalog.titlenum
          GROUP BY tbltitle.publisher
          ORDER BY (count(tblcatalog.issuenum)) DESC) view_table;

CREATE OR REPLACE VIEW public.qry_rpt_series AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.series,
    view_table.countofcomicbooknum,
    view_table.sumofcost
   FROM ( SELECT tbltitle.series,
            count(tblcatalog.comicbooknum) AS countofcomicbooknum,
            sum(tblcatalog.cost) AS sumofcost
           FROM tbltitle
             JOIN tblcatalog ON tbltitle.titlenum = tblcatalog.titlenum
          GROUP BY tbltitle.series
          ORDER BY tbltitle.series) view_table;

CREATE OR REPLACE VIEW public.qry_rpt_subscriptions AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.publisher,
    view_table.title
   FROM ( SELECT tbltitle.publisher,
            tbltitle.title
           FROM tbltitle
          WHERE tbltitle.current = true) view_table;

CREATE OR REPLACE VIEW public.qry_rpt_title AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    view_table.countofcomicbooknum,
    view_table.publisher,
    view_table.title,
    view_table.sumofcost
   FROM ( SELECT count(tblcatalog.comicbooknum) AS countofcomicbooknum,
            tbltitle.publisher,
            tbltitle.title,
            sum(tblcatalog.cost) AS sumofcost
           FROM tbltitle
             JOIN tblcatalog ON tbltitle.titlenum = tblcatalog.titlenum
          GROUP BY tbltitle.publisher, tbltitle.title
          ORDER BY (count(tblcatalog.comicbooknum)) DESC) view_table;

CREATE OR REPLACE VIEW public.qry_rpt_unknown AS 
 SELECT row_number() OVER (ORDER BY 1::integer) AS rownum,
    tbltitle.publisher,
    tbltitle.title,
    tblcatalog.issuenum,
    tblcatalog.cost
   FROM tbltitle,
    tbl_location,
    tblcatalog
  WHERE tbl_location.locationnum = tblcatalog.locationnum AND tbltitle.titlenum = tblcatalog.titlenum AND tbl_location.location::text = 'Unknown'::text
  ORDER BY tbltitle.title, tblcatalog.issuenum;
