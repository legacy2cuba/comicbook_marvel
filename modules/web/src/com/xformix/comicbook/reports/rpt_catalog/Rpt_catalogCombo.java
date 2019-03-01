package com.xformix.comicbook.reports.rpt_catalog;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.xformix.comicbook.entity.QryRptCatalog ;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_catalogCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptCatalog> qryrptcatalogDs ;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text18;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text20;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeader;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout GroupHeader0;

    @Inject
    com.haulmont.cuba.gui.components.TextField Title;

    @Inject
    com.haulmont.cuba.gui.components.TextField Publisher;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout Detail;

    @Inject
    com.haulmont.cuba.gui.components.TextField IssueNUM;

    @Inject
    com.haulmont.cuba.gui.components.TextField Cost;

    @Inject
    com.haulmont.cuba.gui.components.DateField Issue_Date;

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
        if(name.equalsIgnoreCase("Text18")) {
            setValue(field, fieldName, Sum("Text18"));
        }
        if(name.equalsIgnoreCase("Text20")) {
            setValue(field, fieldName, Count(qryrptcatalogDs));
        }
        if(name.equalsIgnoreCase("Title")) {
            setValue(field, fieldName, getValue(ent, "title"));
        }
        if(name.equalsIgnoreCase("Publisher")) {
            setValue(field, fieldName, getValue(ent, "publisher"));
        }
        if(name.equalsIgnoreCase("IssueNUM")) {
            setValue(field, fieldName, getValue(ent, "issuenum"));
        }
        if(name.equalsIgnoreCase("Cost")) {
            setValue(field, fieldName, getValue(ent, "cost"));
        }
        if(name.equalsIgnoreCase("Issue_Date")) {
            setValue(field, fieldName, getValue(ent, "issue_date"));
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
       Object val = entity.getValue("title");
       return val+"";
   }

    public String getBreakValue1(Entity entity) {
       Object val = entity.getValue("issuenum");
       return val+"";
   }

    public String getBreakValue2(Entity entity) {
       Object val = entity.getValue("cost");
       return val+"";
   }



    @Override
    public void init(Map<String, Object> params) {

    formats.put("Text18", "$#,##0.00;($#,##0.00)");
    formats.put("Issue_Date", "mm/yy");
    formats.put("Text13", "Long Date");
    addCollector("Text18", "Cost");
    addCollector("Text20", "1");
    addReportSection("reportheader", ReportHeader, -1, qryrptcatalogDs);
    addReportSection("pageheader", PageHeader, -1, qryrptcatalogDs);
    addReportSection("groupheader0", GroupHeader0, 0, qryrptcatalogDs);
    addReportSection("detail", Detail, -1, qryrptcatalogDs);
    addReportSection("pagefooter", PageFooter, -1, qryrptcatalogDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptcatalogDs);

		fillReport(qryrptcatalogDs);

    }
}