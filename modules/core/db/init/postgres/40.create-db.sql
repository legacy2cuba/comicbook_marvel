
--
-- TOC entry 2323 (class 0 OID 634802)
-- Dependencies: 224
-- Data for Name: tbl_location; Type: TABLE DATA; Schema: public; Owner: cuba
--

INSERT INTO public.tbl_location (locationnum, location, sort_order) VALUES (1, 'Unknown', 1);
INSERT INTO public.tbl_location (locationnum, location, sort_order) VALUES (2, 'Library', 2);
INSERT INTO public.tbl_location (locationnum, location, sort_order) VALUES (3, 'Basement', 3);
INSERT INTO public.tbl_location (locationnum, location, sort_order) VALUES (4, 'Attic', 4);

create sequence public.seq_id_comicbook_tbl_location start 50;

--
-- TOC entry 2324 (class 0 OID 634807)
-- Dependencies: 225
-- Data for Name: tbltitle; Type: TABLE DATA; Schema: public; Owner: cuba
--

INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (1, 'Spider-Girl (2010 - 2011)', 'Spider-girl', 9856, 'Marvel', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (2, 'Batman Returns Again', 'Batman', NULL, 'DC Comics', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (3, 'Hulk (2016 - 2017)', 'Hulk', 22580, 'Marvel', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (5, 'Battlefield (1952 - 1953)', 'Battlefield', 26670, 'Marvel', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (4, 'S.H.I.E.L.D. Origins (2013 - Present)', 'S.H.I.E.L.D', 18543, 'Marvel', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (7, 'Captain America & the Falcon (2004 - 2005)', 'Captain America', 716, 'Marvel', NULL);
INSERT INTO public.tbltitle (titlenum, title, series, marvelseriesid, publisher, current) VALUES (8, 'Hulk & Thing: Hard Knocks (2004)', 'Hulk', 792, 'Marvel', NULL);

create sequence public.seq_id_comicbook_tbltitle start 50;

-- Completed on 2019-02-08 18:56:45

--
-- PostgreSQL database dump complete
--



--
-- TOC entry 2325 (class 0 OID 634812)
-- Dependencies: 226
-- Data for Name: tblcatalog; Type: TABLE DATA; Schema: public; Owner: cuba
--

INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (1, 1, 3.9900000000000002, '2010-11-17', 'Spider-Girl (2010) #1', 30029, NULL, 1, 1);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (2, 2, 2.9900000000000002, '2010-12-29', 'Spider-Girl (2010) #2', 30030, NULL, 1, 2);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (3, 3, 2.9900000000000002, '2011-02-02', 'Spider-Girl (2010) #3', 30031, NULL, 1, 3);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (6, 1, 2, '2019-01-06', 'Batman Returns Again #1', 0, NULL, 2, 2);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (9, 2, 0, '2019-01-14', 'Batman Returns Again #2', 0, NULL, 2, 3);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (10, 1, 3.9900000000000002, '2016-12-28', 'Hulk (2016) #1', 61531, NULL, 3, 1);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (11, 2, 3.9900000000000002, '2017-01-25', 'Hulk (2016) #2', 61532, NULL, 3, 3);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (12, 1, 7.9900000000000002, '2013-11-06', 'S.H.I.E.L.D. Origins (2013) #1', 49318, NULL, 4, 2);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (13, 1, 0, '1952-04-01', 'Battlefield (1952) #1', 73981, NULL, 5, 3);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (14, 2, 0, '1952-06-01', 'Battlefield (1952) #2', 73982, NULL, 5, 3);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (15, 1, 2.9900000000000002, '2004-03-03', 'Captain America & the Falcon (2004) #1', 220, NULL, 7, 2);
INSERT INTO public.tblcatalog (comicbooknum, issuenum, cost, issue_date, comictitle, marvelcomicid, comments, titlenum, locationnum) VALUES (16, 1, 3.5, '2004-09-01', 'Hulk & Thing: Hard Knocks (2004) #1', 792, NULL, 8, 2);

create sequence public.seq_id_comicbook_tblcatalog start 50;

