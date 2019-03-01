package com.xformix.comicbook.reports.rpt_subscriptions;

import java.util.Map;

import javax.inject.Inject;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.Datasource.ItemChangeEvent;
import com.haulmont.cuba.gui.data.Datasource.StateChangeEvent;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.gui.data.impl.ValueGroupDatasourceImpl;

import com.xformix.comicbook.entity.QryRptSubscriptions ;
import com.xformix.cuba.utils.CustomLookup;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_subscriptionsCombo extends ReportGenerator {
    
    @Inject
    protected Table navigationTable;    

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptSubscriptions> qryrptsubscriptionsDs ;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeaderSection;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout Detail;

    @Inject
    com.haulmont.cuba.gui.components.TextField Title;

    @Inject
    com.haulmont.cuba.gui.components.TextField Publisher;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageFooterSection;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text5;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text6;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportFooter;

    
    @Override
    protected void setReportFieldValue(Object field, String fieldName, String name, Entity ent) {
        if(name.equalsIgnoreCase("Title")) {
            setValue(field, fieldName, getValue(ent, "title"));
        }
        if(name.equalsIgnoreCase("Publisher")) {
            setValue(field, fieldName, getValue(ent, "publisher"));
        }
        if(name.equalsIgnoreCase("Text5")) {
            setValue(field, fieldName, getValue(Now(  )));
        }
        if(name.equalsIgnoreCase("Text6")) {
            setValue(field, fieldName,  "Page " +  getValue(ent, "page") +  " of " +  getValue(ent, "pages"));
        }

    }
    
    public String getBreakValue0(Entity entity) {
       Object val = entity.getValue("title");
       return val+"";
   }



    @Override
    public void init(Map<String, Object> params) {

    formats.put("Text5", "Long Date");
    addReportSection("reportheader", ReportHeader, -1, qryrptsubscriptionsDs);
    addReportSection("pageheadersection", PageHeaderSection, -1, qryrptsubscriptionsDs);
    addReportSection("detail", Detail, -1, qryrptsubscriptionsDs);
    addReportSection("pagefootersection", PageFooterSection, -1, qryrptsubscriptionsDs);
    addReportSection("reportfooter", ReportFooter, -1, qryrptsubscriptionsDs);

		fillReport(qryrptsubscriptionsDs);

    }
}