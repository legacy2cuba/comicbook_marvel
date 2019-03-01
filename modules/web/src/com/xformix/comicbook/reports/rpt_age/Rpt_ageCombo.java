package com.xformix.comicbook.reports.rpt_age;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.xformix.comicbook.entity.QryRptAge ;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_ageCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptAge> qryrptageDs ;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text25;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeader;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout GroupHeader0;

    @Inject
    com.haulmont.cuba.gui.components.TextField Issue_Date_by_Month;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout Detail;

    @Inject
    com.haulmont.cuba.gui.components.TextField Title;

    @Inject
    com.haulmont.cuba.gui.components.TextField IssueNUM;

    @Inject
    com.haulmont.cuba.gui.components.TextField Publisher;

    @Inject
    com.haulmont.cuba.gui.components.TextField Comments;

    @Inject
    com.haulmont.cuba.gui.components.TextField Location;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageFooter;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text13;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text14;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportFooter;

    
    @Override
    protected void setReportFieldValue(Object field, String fieldName, String name, Entity ent) {
//        if(name.equalsIgnoreCase("Text25")) {
//            setValue(field, fieldName, Count("0"));
//        }
        if(name.equalsIgnoreCase("Issue_Date_by_Month")) {
            setValue(field, fieldName, Format(  getValue(ent, "issue_date"),  "mmm yyyy" ));
        }
        if(name.equalsIgnoreCase("Title")) {
            setValue(field, fieldName, getValue(ent, "title"));
        }
        if(name.equalsIgnoreCase("IssueNUM")) {
            setValue(field, fieldName, getValue(ent, "issuenum"));
        }
        if(name.equalsIgnoreCase("Publisher")) {
            setValue(field, fieldName, getValue(ent, "publisher"));
        }
        if(name.equalsIgnoreCase("Comments")) {
            setValue(field, fieldName, getValue(ent, "comments"));
        }
        if(name.equalsIgnoreCase("Location")) {
            setValue(field, fieldName, getValue(ent, "location"));
        }
        if(name.equalsIgnoreCase("Text13")) {
            setValue(field, fieldName, getValue(Now(  )));
        }
        if(name.equalsIgnoreCase("Text14")) {
            setValue(field, fieldName,  "Page " +  getValue(ent, "page") +  " of " +  getValue(ent, "pages"));
        }

    }
    
    public String getBreakValue0(Entity entity) {
       Object val = entity.getValue("issue_date");
       return val+"";
   }

    public String getBreakValue1(Entity entity) {
       Object val = entity.getValue("title");
       return val+"";
   }

    public String getBreakValue2(Entity entity) {
       Object val = entity.getValue("issuenum");
       return val+"";
   }

    public String getBreakValue3(Entity entity) {
       Object val = entity.getValue("issue_date");
       return val+"";
   }



    @Override
    public void init(Map<String, Object> params) {

	    formats.put("Text13", "Long Date");
	    formats.put("IssueNUM", "'#' ###");
    addCollector("Text25", "1");
    addReportSection("reportheader", ReportHeader, -1, qryrptageDs);
    addReportSection("pageheader", PageHeader, -1, qryrptageDs);
    addReportSection("groupheader0", GroupHeader0, 0, qryrptageDs);
    addReportSection("detail", Detail, -1, qryrptageDs);
    addReportSection("pagefooter", PageFooter, -1, qryrptageDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptageDs);

		fillReport(qryrptageDs);
    }
}