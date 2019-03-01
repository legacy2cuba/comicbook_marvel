package com.xformix.comicbook.reports.rpt_location;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.xformix.comicbook.entity.QryRptLocation ;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_locationCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptLocation> qryrptlocationDs ;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeaderSection;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout GroupHeader0;

    @Inject
    com.haulmont.cuba.gui.components.TextField Sort_Order;

    @Inject
    com.haulmont.cuba.gui.components.TextField Location;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text19;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text24;

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
    com.haulmont.cuba.gui.components.TextField Cost;

    @Inject
    com.haulmont.cuba.gui.components.DateField Issue_Date;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageFooterSection;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text11;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text12;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportFooter;

    
    @Override
    protected void setReportFieldValue(Object field, String fieldName, String name, Entity ent) {
        if(name.equalsIgnoreCase("Sort_Order")) {
            setValue(field, fieldName, getValue(ent, "sort_order"));
        }
        if(name.equalsIgnoreCase("Location")) {
            setValue(field, fieldName, getValue(ent, "location"));
        }
        if(name.equalsIgnoreCase("Text19")) {
            setValue(field, fieldName, (o) -> { return Count("0"); },"GroupFooter0");
        }
        if(name.equalsIgnoreCase("Text24")) {
            setValue(field, fieldName, (o) -> { return Sum("Text24"); },"GroupFooter0");
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
        if(name.equalsIgnoreCase("Cost")) {
            setValue(field, fieldName, getValue(ent, "cost"));
        }
        if(name.equalsIgnoreCase("Issue_Date")) {
            setValue(field, fieldName, getValue(ent, "issue_date"));
        }
        if(name.equalsIgnoreCase("Text11")) {
            setValue(field, fieldName, getValue(Now(  )));
        }
        if(name.equalsIgnoreCase("Text12")) {
            setValue(field, fieldName,  "Page " +  getValue(ent, "page") +  " of " +  getValue(ent, "pages"));
        }

    }
    
    public String getBreakValue0(Entity entity) {
       Object val = entity.getValue("sort_order");
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



    @Override
    public void init(Map<String, Object> params) {

    formats.put("Text24", "$#,##0.00;($#,##0.00)");
    formats.put("Issue_Date", "MM/yy");
    formats.put("Text11", "Long Date");
    addCollector("Text24", "Cost");
    addReportSection("reportheader", ReportHeader, -1, qryrptlocationDs);
    addReportSection("pageheadersection", PageHeaderSection, -1, qryrptlocationDs);
    addReportSection("groupheader0", GroupHeader0, 0, qryrptlocationDs);
    addReportSection("detail", Detail, -1, qryrptlocationDs);
    addReportSection("pagefootersection", PageFooterSection, -1, qryrptlocationDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptlocationDs);

		fillReport(qryrptlocationDs);

    }
}