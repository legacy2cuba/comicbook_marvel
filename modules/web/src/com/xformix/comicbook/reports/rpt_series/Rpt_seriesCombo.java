package com.xformix.comicbook.reports.rpt_series;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.xformix.comicbook.entity.QryRptSeries ;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_seriesCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptSeries> qryrptseriesDs ;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeaderSection;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout Detail;

    @Inject
    com.haulmont.cuba.gui.components.TextField Sum_Of_Cost;

    @Inject
    com.haulmont.cuba.gui.components.TextField Avg_Of_Cost;

    @Inject
    com.haulmont.cuba.gui.components.TextField SumOfCost;

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
        if(name.equalsIgnoreCase("Sum_Of_Cost")) {
            setValue(field, fieldName, getValue(ent, "series"));
        }
        if(name.equalsIgnoreCase("Avg_Of_Cost")) {
            setValue(field, fieldName, getValue(ent, "countofcomicbooknum"));
        }
        if(name.equalsIgnoreCase("SumOfCost")) {
            setValue(field, fieldName, getValue(ent, "sumofcost"));
        }
        if(name.equalsIgnoreCase("Text11")) {
            setValue(field, fieldName, getValue(Now(  )));
        }
        if(name.equalsIgnoreCase("Text12")) {
            setValue(field, fieldName,  "Page " +  getValue(ent, "page") +  " of " +  getValue(ent, "pages"));
        }

    }
    
    public String getBreakValue0(Entity entity) {
       Object val = entity.getValue("countofcomicbooknum");
       return val+"";
   }

    public String getBreakValue1(Entity entity) {
       Object val = entity.getValue("sumofcost");
       return val+"";
   }



    @Override
    public void init(Map<String, Object> params) {

    formats.put("Text11", "Long Date");
    addReportSection("reportheader", ReportHeader, -1, qryrptseriesDs);
    addReportSection("pageheadersection", PageHeaderSection, -1, qryrptseriesDs);
    addReportSection("detail", Detail, -1, qryrptseriesDs);
    addReportSection("pagefootersection", PageFooterSection, -1, qryrptseriesDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptseriesDs);

		fillReport(qryrptseriesDs);

    }
}