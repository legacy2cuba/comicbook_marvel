package com.xformix.comicbook.reports.rpt_title;

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

import com.xformix.comicbook.entity.QryRptTitle;
import com.xformix.cuba.utils.CustomLookup;
import com.xformix.cuba.utils.ReportGenerator;

public class Rpt_titleCombo extends ReportGenerator {

    protected int Page;

    protected int Pages;

    @Inject
    Datasource<QryRptTitle> qryrpttitleDs;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportHeader;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text17;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageHeaderSection;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout Detail;

    @Inject
    com.haulmont.cuba.gui.components.TextField CountOfComicBookNUM;

    @Inject
    com.haulmont.cuba.gui.components.TextField SumOfCost;

    @Inject
    com.haulmont.cuba.gui.components.TextField Publisher;

    @Inject
    com.haulmont.cuba.gui.components.TextField Title;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout PageFooterSection;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text9;

    @Inject
    com.haulmont.cuba.gui.components.TextField Text10;

    @Inject
    com.haulmont.cuba.web.gui.components.WebGridLayout ReportFooter;

    @Override
    protected void setReportFieldValue(Object field, String fieldName, String name, Entity ent) {
//	if (name.equalsIgnoreCase("Text17")) {
//	    setValue(field, fieldName, Count("0"));
//	}
	if (name.equalsIgnoreCase("CountOfComicBookNUM")) {
	    setValue(field, fieldName, getValue(ent, "countofcomicbooknum"));
	}
	if (name.equalsIgnoreCase("SumOfCost")) {
	    setValue(field, fieldName, getValue(ent, "sumofcost"));
	}
	if (name.equalsIgnoreCase("Publisher")) {
	    setValue(field, fieldName, getValue(ent, "publisher"));
	}
	if (name.equalsIgnoreCase("Title")) {
	    setValue(field, fieldName, getValue(ent, "title"));
	}
	if (name.equalsIgnoreCase("Text9")) {
	    setValue(field, fieldName, getValue(Now()));
	}
	if (name.equalsIgnoreCase("Text10")) {
	    setValue(field, fieldName, "Page " + getValue(ent, "page") + " of " + getValue(ent, "pages"));
	}

    }

    public String getBreakValue0(Entity entity) {
	Object val = entity.getValue("countofcomicbooknum");
	return val + "";
    }

    public String getBreakValue1(Entity entity) {
	Object val = entity.getValue("sumofcost");
	return val + "";
    }

    @Override
    public void init(Map<String, Object> params) {

	formats.put("Text9", "Long Date");
	addCollector("Text17", "CountOfComicBookNUM");
	addReportSection("reportheader", ReportHeader, -1, qryrpttitleDs);
	addReportSection("pageheadersection", PageHeaderSection, -1, qryrpttitleDs);
	addReportSection("detail", Detail, -1, qryrpttitleDs);
	addReportSection("pagefootersection", PageFooterSection, -1, qryrpttitleDs);
	addReportSection("reportfooter", ReportFooter, -1, qryrpttitleDs);

	fillReport(qryrpttitleDs);

    }
}