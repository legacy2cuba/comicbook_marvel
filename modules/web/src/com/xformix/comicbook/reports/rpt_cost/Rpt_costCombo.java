package com.xformix.comicbook.reports.rpt_cost;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.Datasource;
import com.xformix.comicbook.entity.QryRptCost ;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_costCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptCost> qryrptcostDs ;

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
    com.haulmont.cuba.gui.components.TextField Min_Of_Cost;

    @Inject
    com.haulmont.cuba.gui.components.TextField Max_Of_Cost;

    @Inject
    com.haulmont.cuba.gui.components.TextField Count_Of_qry_Catalog;

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
            setValue(field, fieldName, getValue(ent, "sum_of_cost"));
        }
        if(name.equalsIgnoreCase("Avg_Of_Cost")) {
            setValue(field, fieldName, getValue(ent, "avg_of_cost"));
        }
        if(name.equalsIgnoreCase("Min_Of_Cost")) {
            setValue(field, fieldName, getValue(ent, "min_of_cost"));
        }
        if(name.equalsIgnoreCase("Max_Of_Cost")) {
            setValue(field, fieldName, getValue(ent, "max_of_cost"));
        }
        if(name.equalsIgnoreCase("Count_Of_qry_Catalog")) {
            setValue(field, fieldName, getValue(ent, "count_of_qry_rpt_catalog"));
        }
        if(name.equalsIgnoreCase("Text11")) {
            setValue(field, fieldName, getValue(Now(  )));
        }
        if(name.equalsIgnoreCase("Text12")) {
            setValue(field, fieldName,  "Page " +  getValue(ent, "page") +  " of " +  getValue(ent, "pages"));
        }

    }
    


    @Override
    public void init(Map<String, Object> params) {

    formats.put("Text11", "Long Date");
    addReportSection("reportheader", ReportHeader, -1, qryrptcostDs);
    addReportSection("pageheadersection", PageHeaderSection, -1, qryrptcostDs);
    addReportSection("detail", Detail, -1, qryrptcostDs);
    addReportSection("pagefootersection", PageFooterSection, -1, qryrptcostDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptcostDs);

		fillReport(qryrptcostDs);

    }
}