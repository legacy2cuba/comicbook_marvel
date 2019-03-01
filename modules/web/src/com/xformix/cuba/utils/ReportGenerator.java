package com.xformix.cuba.utils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.GridLayout.Area;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.SplitPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Table.Column;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.gui.data.impl.ValueGroupDatasourceImpl;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;
import com.haulmont.cuba.web.gui.components.WebImage;
import com.haulmont.cuba.web.gui.components.WebLabel;
import com.haulmont.cuba.web.gui.components.WebVBoxLayout;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
//import com.vaadin.ui.GridLayout.Area;

public class ReportGenerator extends AbstractWindow {

    private static Pattern tagMatchPattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
    protected CollectionDatasource mainDs;
    protected Table navTable;
    protected Entity newItem;
    protected Entity currentEntity;
    protected String currentSection;

    protected WebGridLayout subReportGrid;
    protected List<Column> subReportColumns;

    private int pageLimit = 40;

    /**
     * The {@link BoxLayout} instance that contains components on the left side
     * of {@link SplitPanel}
     */
    @Inject
    protected BoxLayout lookupBox;
    /**
     * The {@link BoxLayout} instance that contains buttons to invoke Save or
     * Cancel actions in edit mode
     */
    @Inject
    protected BoxLayout actionsPane;

    /**
     */
    @Inject
    protected Frame customFrame;

    @Inject
    Component Detail;

    @Inject
    WebVBoxLayout TOP_LEVEL;

    Integer pageNumber;
    Integer totalPages = null;

    static final int LONG_DIMENSION = 1700;
    static final int SHORT_DIMENSION = 950;

    float reportWidth;
    float pageHeight;

    float availablePageHeight;

    WebVBoxLayout reportLayout;
    WebGridLayout pageLayout;
    WebHBoxLayout pagePartLayout;
    WebVBoxLayout columnLayout;

    List<WebGridLayout> layoutStack = new ArrayList<>();
    boolean hasGroupBreaks;
    float columnWidth = 0;
    int numColsInPage;

    protected HashMap<HasValue, String> delayedValueFields = new HashMap<>();
    protected HashMap<String, String> formats = new HashMap<>();
    protected HashMap<String, Label> fields = new HashMap<>();
    protected HashMap<String, Collector> collectors = new HashMap<>();
    protected List<FutureSetter> futures = new ArrayList<>();
    protected HashMap<String, FutureEvaluator> futureEvals = new HashMap<>();
    protected HashMap<String, Object> groupValues = new HashMap<>();

    protected class FutureEvaluator {
	Function<Object, Object> func;
	String evalType;
	String fieldName;
	HasValue field;
	String id;
    }

    protected class FutureRunnable {
	public void run(FutureSetter fut) {
	}
    }

    protected class FutureSetter {
	public String waitingOn;
	public FutureRunnable action;
	public Entity ent;
	public HashMap<String, Label> currentFields;
	public boolean usedUp = false;

	public FutureSetter(String waitingOn, HashMap<String, Label> fields, Entity ent, FutureRunnable action) {
	    this.ent = ent;
	    this.action = action;
	    this.waitingOn = waitingOn;
	    this.currentFields = fields;
	}

	public float getFloatValue(String name) {
	    try {
		return Float.parseFloat(getValue(name).toString());
	    } catch (Exception ex) {
	    }
	    return 0f;
	}

	public Object getValue(String name) {
	    Object val = null;
	    if (currentFields.containsKey(name)) {
		val = currentFields.get(name).getValue();
	    } else if (fields.containsKey(name)) {
		val = fields.get(name).getValue();
	    } else {
		try {
		    val = ent.getValue(name);
		} catch (Exception ex) {
		    System.err.println("Unable to get field " + name + " from " + ent + ", " + ex);
		}
	    }
	    return val;
	}

	public Label getField(String name) {
	    if (currentFields.containsKey(name))
		return currentFields.get(name);
	    return fields.get(name);
	}
    }

    protected abstract class Collector {
	public float value;
	public String fieldName;
	public String collectingName;
	public float collectingNumber = 0;

	public Collector(String fieldName, String collectingName) {
	    this.fieldName = fieldName;
	    this.collectingName = collectingName;
	    try {
		this.collectingNumber = Float.parseFloat(collectingName);
	    } catch (Exception e) {
	    }
	}

	public void reset() {
	    value = 0;
	}

	public abstract void handle(float value);

	public void updateField(Label field) {
	    setValue(field, fieldName, value);
	    reset();
	}

	public boolean collectField(Label field, String name) {
	    if (collectingNumber != 0 && field == null) {
		handle(collectingNumber);
		return true;
	    } else {
		if (collectingName.equals(name)) {
		    handle(getFloatValue(field));
		    return true;
		}
	    }
	    return false;
	}

	public void collectFromEntity(Entity entity, String name) {
	    if (name == null || collectingName.equals(name)) {
		try {
		    Object val = entity.getValue(collectingName.toLowerCase());
		    // System.out.println("COLLECTED "+val+" for
		    // "+collectingName+", "+fieldName+" from "+entity);
		    handle(Float.parseFloat(val + ""));
		} catch (Exception ex) {
		}
	    }
	}
    }

    protected class SumCollector extends Collector {
	public SumCollector(String fieldName, String collectingName) {
	    super(fieldName, collectingName);
	}

	@Override
	public void handle(float value) {
	    this.value += value;
	}
    }

    protected class CountCollector extends Collector {
	public CountCollector(String fieldName, String collectingName) {
	    super(fieldName, collectingName);
	}

	@Override
	public void handle(float value) {
	    this.value += 1;
	}
    }

    class ComponentInfo {
	Component comp;
	Area area;
	public WebLabel label;

	public ComponentInfo(Component comp, Area componentPosition) {
	    this.comp = comp;
	    this.area = componentPosition;
	}
    }

    class SectionInfo {
	String name;
	WebGridLayout layout;
	List<ComponentInfo> componentInfos;
	float width;
	float height;
	int numRows;
	int numCols;
	int groupOn;
	int groupNumber;
	Datasource ds;
	Object currentValue;
	int colsInRow = 0;
	int rowCountInGroup;

	@Override
	public String toString() {
	    return super.toString() + " [name: " + name + ", width: " + width + ", height: " + height + "]";
	}
    }

    protected HashMap<String, SectionInfo> reportSections = new HashMap<String, SectionInfo>();
    protected SectionInfo reportHeader;
    protected SectionInfo reportFooter;
    protected SectionInfo pageHeader;
    protected SectionInfo pageFooter;

    protected void addCollector(String collectorName, String collectedName) {
	collectors.put(collectorName, new SumCollector(collectorName, collectedName));
    }

    protected void addFutureSetter(String watchedFieldName, Entity ent, FutureRunnable futureRunnable) {
	futures.add(new FutureSetter(watchedFieldName, fields, ent, futureRunnable));
    }

    protected void addReportSection(String name, WebGridLayout grid, int property, Datasource ds) {
	if (grid == null) {
	    System.out.println("Unable to add report section " + name + " - layout is null");
	    return;
	}

	SectionInfo info = new SectionInfo();
	info.layout = grid;
	info.width = grid.getWidth();
	info.height = grid.getHeight();
	info.groupNumber = property;
	info.ds = ds;
	info.componentInfos = new ArrayList<>();
	float width = 0;
	float height = 0;
	for (Component comp : grid.getOwnComponents()) {
	    Area pos = grid.getComponentArea(comp);
	    if (pos != null) {
		if (pos.getColumn1() == 0) {
		    height += comp.getHeight() + 10; // allow for space between
						     // rows
		}
		if (pos.getRow1() == 0) {
		    width += comp.getWidth();
		}
	    }
	    info.componentInfos.add(new ComponentInfo(comp, pos));
	}
	if (width > info.width)
	    info.width = width;
	if (height > info.height)
	    info.height = height;
	if (name.contains("header") || name.contains("footer") || name.equals("detail")) {
	    grid.removeAll();
	    TOP_LEVEL.remove(grid);
	}
	info.name = name.toLowerCase();
	System.out.println("ADDED REPORT SECTION " + info);
	reportSections.put(info.name, info);
    }

    protected void setReportFieldValue(Object field, String fieldName, String name, Entity ent) {
    }

    private SectionInfo getSection(String sectionName) {
	sectionName = sectionName.toLowerCase();
	for (String name : reportSections.keySet()) {
	    if (name.toLowerCase().startsWith(sectionName)) {
		return reportSections.get(name);
	    }
	}
	return null;
    }

    private WebButton exportButton;

    protected void fillReport(Datasource mainDs) {

	totalPages = null;
	mainDs.refresh();
	Collection<Entity> entities = ((GroupDatasource) mainDs).getItems();
	System.out.println("FOUND " + entities.size() + " entities, query=" + ((GroupDatasource) mainDs).getQuery());
	// if(entities.size() == 0) {
	// return;
	// }

	if (exportButton != null)
	    TOP_LEVEL.remove(exportButton);

	if (reportLayout != null) {
	    pageLayout = null;
	    pagePartLayout = null;
	    columnLayout = null;
	    reportLayout.removeAll();
	    TOP_LEVEL.remove(reportLayout);
	}

	reportLayout = new WebVBoxLayout();
	exportButton = new WebButton();
	exportButton.setCaption("Export to PDF");
	exportButton.setAction(new AbstractAction("exportPdf") {
	    @Override
	    public void actionPerform(Component component) {
		exportToPdf();
	    }
	});
	TOP_LEVEL.add(exportButton);
	TOP_LEVEL.add(reportLayout);

	SectionInfo detailInfo = getSection("detail");

	// numRowsAdded = 0;
	pageNumber = 1;

	reportHeader = getSection("reportheader");
	reportFooter = getSection("reportfooter");

	pageHeader = getSection("pageheader");
	if (pageHeader != null)
	    pageHeader.currentValue = 0;

	pageFooter = getSection("pagefooter");
	if (pageFooter != null)
	    pageFooter.currentValue = 1;

	hasGroupBreaks = false;
	for (SectionInfo sec : reportSections.values()) {
	    sec.rowCountInGroup = 0;
	    boolean isGroup = sec.groupNumber >= 0 && (sec.name.contains("header") || sec.name.contains("footer"));
	    if (isGroup)
		hasGroupBreaks = true;
	    if (isGroup || sec.name.equals("detail")) {
		columnWidth = Math.max(columnWidth, sec.layout.getWidth());
	    }
	}

	reportWidth = TOP_LEVEL.getWidth();
	if (reportWidth < SHORT_DIMENSION) {
	    reportWidth = SHORT_DIMENSION;
	    pageHeight = LONG_DIMENSION;
	} else {
	    reportWidth = LONG_DIMENSION;
	    pageHeight = SHORT_DIMENSION;
	}

	numColsInPage = (int) (reportWidth / columnWidth);
	System.out.println("COL WIDTHS: " + columnWidth + ", REPORT WIDTH: " + reportWidth + ", NUM COLS: " + numColsInPage);

	detailInfo.layout.removeAll();
	// reportLayout.setColumns(1);

	Entity[] entityArray = new Entity[entities.size()];
	entities.toArray(entityArray);

	// if (entityArray.length == 0)
	// return;

	Entity firstEntity = null;
	Entity lastEntity = null;
	int numEntities = entities.size();
	if (numEntities > 0) {
	    firstEntity = entityArray[0];
	    lastEntity = entityArray[numEntities - 1];
	}

	if (reportHeader != null) {
	    fillSection(reportHeader, firstEntity, "reportheader");
	}
	if (pageHeader != null) {
	    fillSection(pageHeader, firstEntity, "pageHeader");
	}

	for (int i = 0; i < entityArray.length; i++) {
	    Entity entity = entityArray[i];
	    currentEntity = entity;

	    // handle up to 10 groups
	    for (int gn = 0; gn < 10; gn++) {
		SectionInfo sec = getSectionInfo("groupheader" + gn);
		if (sec != null) {
		    try {
			sec.rowCountInGroup++;
			Method m = getClass().getMethod("getBreakValue" + sec.groupNumber, Entity.class);
			String val = (String) m.invoke(this, entity);
			if (!val.equals(sec.currentValue)) {
			    sec.currentValue = val;
			    fillSection(sec, entity, "groupheader" + gn);
			    sec.rowCountInGroup = 1;
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		Object groupVal = getGroupValue(gn, entity);
		Object currGroupVal = groupValues.get("HEADER" + gn);
		if (groupVal != null && !groupVal.equals(currGroupVal)) {
		    fillFutureEvals("GroupHeader" + gn);
		    groupValues.put("HEADER" + gn, groupVal);
		}
	    }

	    if (detailInfo != null) {
		HashMap<String, Label> newFields = new HashMap<>();
		newFields.putAll(fields);
		fields = newFields;
		fillSection(detailInfo, entity, "detail");
	    }
	    if (subReportGrid != null) {
		int colNum = 0;
		int rows = subReportGrid.getRows();
		subReportGrid.setRows(rows + 1);
		for (Column col : subReportColumns) {
		    MetaPropertyPath path = (MetaPropertyPath) col.getId();
		    WebLabel label = getFieldLabel(path.getMetaProperty().getName(), "access-label", entity);
		    subReportGrid.add(label, colNum++, rows);
		}
	    }

	    boolean didAddFooter = false;
	    // handle up to 10 groups
	    for (int gn = 9; gn >= 0; gn--) {
		boolean doGroupFooter = false;
		SectionInfo sec = getSectionInfo("groupfooter" + gn);
		Object currGroupVal = groupValues.get("FOOTER" + gn);
		if (currGroupVal == null)
		    currGroupVal = getGroupValue(gn, entityArray[i]);
		Object nextGroupVal = null;
		if (entityArray.length > i + 1) {
		    nextGroupVal = getGroupValue(gn, entityArray[i + 1]);
		} else {
		    doGroupFooter = true;
		}
		groupValues.put("FOOTER" + gn, nextGroupVal);
		if (nextGroupVal != null && !nextGroupVal.equals(currGroupVal)) {
		    doGroupFooter = true;
		}
		if (doGroupFooter) {
		    fillFutureEvals("GroupFooter" + gn);
		}
		if (sec != null) {
		    try {
			sec.rowCountInGroup++;
			sec.currentValue = nextGroupVal;
			if (doGroupFooter) {
			    didAddFooter = true;
			    fillSection(sec, entity, "groupfooter" + gn);
			    sec.rowCountInGroup = 0;
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	    if (didAddFooter) {
		fields = new HashMap<>();
	    }

	    if (pageNumber > pageLimit)
		break;

	}

	if (reportFooter != null) {
	    fillSection(reportFooter, lastEntity, "reportfooter");
	}
	if (reportHeader != null) {
	    updateSection(reportHeader, lastEntity, "reportHeader");
	}
	if (pageFooter != null) {
	    fillSection(pageFooter, lastEntity, "pagefooter");
	}

	totalPages = pageNumber;

	for (HasValue field : delayedValueFields.keySet()) {
	    String val = delayedValueFields.get(field);
	    while (true) {
		Matcher m = tagMatchPattern.matcher(val);
		if (m.find()) {
		    String key = m.group(1);
		    val = val.substring(0, m.start()) + getValue(lastEntity, key) + val.substring(m.end());
		} else {
		    break;
		}
	    }
	    field.setValue(val);
	}
    }

    private Object getGroupValue(int gn, Entity entity) {
	try {
	    Method m = getClass().getMethod("getBreakValue" + gn, Entity.class);
	    return m.invoke(this, entity);
	} catch (Exception e) {
	}
	return null;
    }

    private SectionInfo getSectionInfo(String name) {
	for (SectionInfo sec : reportSections.values()) {
	    if (sec.name.equalsIgnoreCase(name)) {
		return sec;
	    }
	}
	return null;
    }

    private WebHBoxLayout getPagePartLayout(boolean createNew) {
	return getPagePartLayout(createNew, getPageLayout(false));
    }

    private WebHBoxLayout getPagePartLayout(boolean createNew, WebGridLayout pageLayout) {
	if (pagePartLayout == null || (createNew && pagePartLayout.getOwnComponents().size() > 0)) {
	    System.out.println("NEW PAGE PART LAYOUT");
	    pagePartLayout = new WebHBoxLayout();
	    pagePartLayout.setMargin(true);
	    pagePartLayout.setSpacing(true);
	    pageLayout.add(pagePartLayout);
	}
	return pagePartLayout;
    }

    private WebGridLayout getPageLayout(boolean createNew) {
	if (pageLayout == null || (createNew && pageLayout.getOwnComponents().size() > 0)) {
	    System.out.println("NEW PAGE LAYOUT");

	    availablePageHeight = pageHeight;
	    if (pageHeader != null)
		availablePageHeight -= pageHeader.height;
	    if (pageFooter != null)
		availablePageHeight -= pageFooter.height;
	    if (reportHeader != null && reportHeader.currentValue == null)
		availablePageHeight -= reportHeader.height;

	    pageLayout = new WebGridLayout();
	    pageLayout.setColumns(1);
	    pageLayout.setMargin(true);
	    pageLayout.setSpacing(true);
	    if (hasGroupBreaks)
		pageLayout.setHeight(pageHeight + "px");
	    getPagePartLayout(true);
	    reportLayout.add(pageLayout);
	}
	return pageLayout;
    }

    private void newPage(Entity entity, boolean useLatestPart) {
	WebHBoxLayout temp = null;
	if (useLatestPart) {
	    pageLayout.remove(pagePartLayout);
	    temp = pagePartLayout;
	}
	System.out.println("NEW PAGE " + pageNumber + ", AVAIL. HEIGHT=" + availablePageHeight);
	if (pageFooter != null) {
	    fillSection(pageFooter, entity, "pagefooter");
	}
	pageLayout = getPageLayout(true);
	if (temp != null) {
	    pageLayout.remove(pagePartLayout);
	    pagePartLayout = temp;
	    pageLayout.add(pagePartLayout);
	    columnLayout = (WebVBoxLayout) pagePartLayout.getOwnComponents().iterator().next();
	}
	if (pageHeader != null) {
	    fillSection(pageHeader, entity, "pageheader");
	}
	pageNumber++;
    }

    private WebLabel getFieldLabel(String name, String styleName, Entity entity) {
	WebLabel wtf = new WebLabel();
	WebLabel field = (WebLabel) wtf;
	fields.put(name, field);
	field.setAlignment(Alignment.MIDDLE_LEFT);
	field.setStyleName(styleName);
	// field.setAlignment( ((WebTextField) comp).getAlignment());
	setReportFieldValue(field, name, name, entity);
	return wtf;
    }

    protected void updateSection(SectionInfo sectionInfo, Entity entity, String type) {
	for (int i = 0; i < sectionInfo.componentInfos.size(); i++) {
	    ComponentInfo compInfo = sectionInfo.componentInfos.get(i);
	    Component comp = compInfo.comp;
	    WebLabel field = compInfo.label;
	    String name = comp.getId();
	    if (collectors.containsKey(name)) {
		collectors.get(name).updateField(field);
	    }
	}
    }

    protected void fillSection(SectionInfo sectionInfo, Entity entity, String type) {

	currentSection = type;

	// System.out.println("FILL SECTION " + type+",
	// "+sectionInfo.componentInfos.size());
	if (sectionInfo.componentInfos.size() == 0) {
	    // if the detail section is empty, still need to collect fields from
	    // the entity
	    for (Collector col : collectors.values()) {
		col.collectFromEntity(entity, null);
	    }
	    // System.out.println("NOT FILLING - NO COMPS");
	    return;
	}

	WebGridLayout layout = new WebGridLayout();
	layout.setWidth(sectionInfo.width + "px");

	int sectionHeight = (int) (sectionInfo.height + 5);

	System.out.println("SECTION " + type + " HEIGHT=" + sectionHeight + " HEIGHT IN PAGE=" + getHeight(getPageLayout(false), true));

	// comment out to allow height to self-adjust
	layout.setHeight(sectionHeight + "px");

	if (type.equals("reportheader") || type.equals("reportfooter")) {
	    getPagePartLayout(true).add(layout);
	    getPagePartLayout(true);
	} else if (type.equals("pageheader")) {
	    getPagePartLayout(true).add(layout);
	    getPagePartLayout(true);
	} else if (type.equals("pagefooter")) {
	    float height = availablePageHeight - getHeight(getPageLayout(false), true);
	    getPagePartLayout(true).add(layout);
	    System.out.println("Page footer height = " + height);
	    pagePartLayout.setHeight(height + "px");
	} else {

	    int columnCount = getPagePartLayout(false).getOwnComponents().size();

	    float nextHeight = getHeight(getPageLayout(false), true) + sectionHeight;

	    if (hasGroupBreaks) {
		System.out.println("GROUPS, COLUMN COUNT= " + columnCount + ", NUM COLS=" + numColsInPage + ", NEXT HT=" + nextHeight + ", AV HT=" + availablePageHeight);
		boolean useLastPart = numColsInPage > 1; // TODO: fix this
							 // better
		if (nextHeight > availablePageHeight) {
		    newPage(entity, useLastPart);
		}
		if ((numColsInPage > 1 && type.startsWith("groupheader")) || columnCount == 0) {
		    if (columnCount >= numColsInPage) {
			newPage(entity, false);
		    }
		    columnLayout = new WebVBoxLayout();
		    pagePartLayout.add(columnLayout);
		}
		columnLayout.add(layout);
	    } else {
		System.out.println("NO GROUPS, COLUMN COUNT=" + columnCount);
		if (nextHeight > availablePageHeight) {
		    newPage(entity, true);
		} else {
		    if (columnCount >= numColsInPage) {
			getPagePartLayout(true);
		    }
		}
		pagePartLayout.add(layout);
	    }
	}

	if (type.equals("detail")) {
	    // handle collectors that count the number of rows
	    for (Collector col : collectors.values()) {
		col.collectField(null, null);
	    }
	}

	for (int i = 0; i < sectionInfo.componentInfos.size(); i++) {
	    ComponentInfo compInfo = sectionInfo.componentInfos.get(i);
	    Area area = compInfo.area;
	    Component comp = compInfo.comp;
	    Component wtf = null;
	    String name = comp.getId();
	    try {
		if (comp instanceof Field) {
		    wtf = getFieldLabel(comp.getId(), comp.getStyleName(), entity);
		    WebLabel field = (WebLabel) wtf;
		    compInfo.label = field;

		    if (collectors.containsKey(name)) {
			collectors.get(name).updateField(field);
		    }
		    for (Collector col : collectors.values()) {
			// if there is no field with the given name, see if the
			// entity has a field of the same name
			if (!col.collectField(field, name)) {
			    col.collectFromEntity(entity, name);
			}
		    }

		    List<FutureSetter> removes = new ArrayList<>();
		    for (FutureSetter fut : futures) {
			if (!fut.usedUp && fut.waitingOn.equalsIgnoreCase(name)) {
			    fut.action.run(fut);
			    fut.usedUp = true;
			    removes.add(fut);
			}
		    }
		    for (FutureSetter fut : removes) {
			futures.remove(fut);
		    }

		} else if (comp instanceof Label) {
		    wtf = new WebLabel();
		    ((WebLabel) wtf).setValue(((Label) comp).getValue());
		    ((WebLabel) wtf).setAlignment(Alignment.MIDDLE_LEFT);
		    // ((WebLabel) wtf).setAlignment( ((Label)
		    // comp).getAlignment());

		} else if (comp instanceof Image) {
		    wtf = new WebImage();
		    // wtf.setValue(((Image)component).getValue());

		} else if (comp instanceof Frame) {
		    Frame frame = (Frame) comp;
		    Collection<Component> fcomps = frame.getOwnComponents();
		    for (Component fcomp : fcomps) {
			if (fcomp instanceof Table) {
			    Table table = (Table) fcomp;
			    subReportColumns = table.getColumns();
			    subReportGrid = new WebGridLayout();
			    wtf = subReportGrid;
			    subReportGrid.setColumns(subReportColumns.size());
			    subReportGrid.setWidth("100%");
			    System.out.println("Got table " + table + ", ds=" + table.getDatasource() + ", cols=" + subReportColumns);
			} else {
			    System.out.println("Got fcomp " + fcomp);
			}
		    }
		}
		if (wtf != null) {
		    wtf.setStyleName(comp.getStyleName());
		    wtf.setWidth(comp.getWidth() + "px");

		    if (area != null) {
			if (layout.getColumns() < area.getColumn2() + 1)
			    layout.setColumns(area.getColumn2() + 1);

			if (layout.getRows() < area.getRow2() + 1)
			    layout.setRows(area.getRow2() + 1);

			layout.add(wtf, area.getColumn1(), area.getRow1(), area.getColumn2(), area.getRow2());
			// System.out.println("ADD COMP " + wtf + ", " +
			// area.getColumn1() + ", " + area.getRow1());
		    } else {
			layout.add(wtf);
			// System.out.println("ADD COMP " + wtf);
		    }
		} else {
		    System.out.println("NEED TO HANDLE: " + comp);
		}
	    } catch (Exception ex) {
		System.err.println("ERROR ADDING COMPONENT " + compInfo + ", " + ex);
		ex.printStackTrace();
	    }
	}
    }

    private void fillFutureEvals(String type) {
	List<String> toRemove = new ArrayList<>();
	for (String id : futureEvals.keySet()) {
	    FutureEvaluator fEval = futureEvals.get(id);
	    if (fEval.evalType.equals(type)) {
		Object val = fEval.func.apply(null);
		setValue(fEval.field, fEval.fieldName, val);
		toRemove.add(id);
	    }
	}
	for (String id : toRemove) {
	    futureEvals.remove(id);
	}
    }

    private float getHeight(Component comp, boolean lastSection) {
	float height = 0;
	if (comp instanceof WebHBoxLayout) {
	    Collection parts = ((WebHBoxLayout) comp).getOwnComponents();
	    Object lastPart = null;
	    for (Object part : parts) {
		lastPart = part;
		if (!lastSection)
		    height = Math.max(getHeight((Component) part, lastSection), height);
	    }
	    if (lastSection && lastPart != null) {
		height = getHeight((Component) lastPart, lastSection);
	    }
	} else if (comp instanceof WebVBoxLayout) {
	    Collection parts = ((WebVBoxLayout) comp).getOwnComponents();
	    for (Object part : parts) {
		height += getHeight((Component) part, lastSection);
	    }
	} else if (comp instanceof WebGridLayout) {
	    WebGridLayout grid = (WebGridLayout) comp;
	    if (grid.getColumns() == 1) {
		Collection parts = grid.getOwnComponents();
		for (Object part : parts) {
		    height += getHeight((Component) part, lastSection);
		}
	    } else {
		height = comp.getHeight();
	    }
	} else {
	    height = comp.getHeight();
	}
	return height;
    }

    protected void setNewItem(Entity newItem) {
	this.newItem = newItem;
    }

    protected Object getValue(Entity ent, String field) {
	try {
	    Object value = ent.getValueEx(field.toLowerCase());
	    if (value == null)
		value = "";
	    return value;
	} catch (Exception e) {
	    if (field.equals("page"))
		return pageNumber + "";
	    if (field.equals("pages")) {
		if (totalPages != null) {
		    return totalPages;
		} else {
		    return "${pages}";
		}
	    }
	    System.out.println("Unable to get value " + field + " of " + ent);
	    return null;
	}
    }

    protected Object getValueFromObject(Object field) {
	Object val = field;
	if (field instanceof HasValue) {
	    val = ((HasValue) field).getValue();
	} else if (field instanceof String) {
	    String fieldName = (String) field;
	    Label f = fields.get(fieldName);
	    // System.out.println("LOOKING FOR FIELD "+field+", found "+f);
	    if (f != null) {
		val = f.getValue();
	    } else {
		try {
		    val = currentEntity.getValueEx(fieldName.toLowerCase());
		} catch (Exception ex) {
		    // System.out.println("Error trying to get "+fieldName+"
		    // from "+currentEntity+", "+ex);
		}
	    }
	}
	return val;
    }

    protected String getValue(Object field) {
	return getValueFromObject(field) + "";
    }

    protected float getFloatValue(Object field) {
	try {
	    if (field instanceof HasValue)
		field = ((HasValue) field).getValue();
	    String val = field + "";
	    val = val.replace("$", "").replace("(", "").replace(")", "").replace(",", "");
	    return Float.parseFloat(val);
	} catch (Exception e) {
	    return 0f;
	}
    }

    protected void setValue(Object field, String fieldName, Function<Object, Object> func, String evalType) {
	FutureEvaluator fEval = new FutureEvaluator();
	fEval.evalType = evalType;
	fEval.field = (HasValue) field;
	fEval.id = "FUTURE_EVAL" + futureEvals.size();
	fEval.func = func;
	fEval.fieldName = fieldName;
	fEval.field.setValue("${" + fEval.id + "}");
	futureEvals.put(fEval.id, fEval);
    }

    protected void setValue(Object field, String fieldName, Object value) {
	try {
	    if (value == null)
		return;
	    String val = value.toString();
	    String format = formats.get(fieldName);
	    if ("percent".equalsIgnoreCase(format)) {
		val = (((int) (Float.parseFloat(val) * 10000)) / 100f) + " %";
	    } else if (format != null) {
		try {
		    if (value instanceof Date) {
			format = format.replace("mmm", "MMM");
			format = format.replace("mm", "MM");
			val = new SimpleDateFormat(format).format((Date) value);
		    } else if (!format.equalsIgnoreCase("general number")) {
			DecimalFormat f = new DecimalFormat(format);
			val = f.format(Float.parseFloat(val));
		    }
		} catch (Exception e) {
		}
	    } else if (value instanceof Date) {
		val = new SimpleDateFormat("dd/MM/yyyy").format((Date) value);
	    }

	    if (field instanceof HasValue) {
		if (val.contains("${")) {
		    delayedValueFields.put((HasValue) field, val);
		}
		((HasValue) field).setValue(val);
	    }
	} catch (Exception e) {
	    System.err.println("Error setting value " + value + ", " + e);
	}
    }

    protected Object getValue(ValueGroupDatasourceImpl ds, String param) {
	if (!ds.getItems().iterator().hasNext())
	    return null;
	KeyValueEntity item = ds.getItems().iterator().next();
	if (item == null)
	    return null;
	return item.getValue(param);
    }

    protected Object getValue(Datasource ds, String param) {
	if (ds instanceof CollectionDatasourceImpl) {
	    CollectionDatasourceImpl cds = (CollectionDatasourceImpl) ds;
	    Entity item = cds.getItem();
	    if (item == null) {
		if (!cds.getItems().iterator().hasNext())
		    return null;
		item = (Entity) cds.getItems().iterator().next();
	    }
	    if (item == null)
		return null;
	    return item.getValue(param);
	}
	return null;
    }

    protected Date Date() {
	return new Date();
    }

    protected float Abs(Object num) {
	return Math.abs(Float.parseFloat("" + num));
    }

    protected float Avg(Object num) {
	// TODO: clearly this isn't right, but this is used in a report and so
	// we gotta do something else
	return Float.parseFloat("" + num);
    }

    protected int Round(Object num, Object numDigObj) {
	// TODO: handle this property - numDigObj should be an integer
	return Math.round(Float.parseFloat("" + num));
    }

    protected float NZ(Object val, float ifNull) {
	if (val == null)
	    return ifNull;
	return Float.parseFloat("" + val);
    }

    protected Date Now() {
	return new Date();
    }

    protected String DatePart(String format, Object obj) {
	if (obj instanceof String) {
	    obj = new Date((String) obj);
	}
	if (obj instanceof Date) {
	    try {
		Date d = (Date) obj;
		if (format.equalsIgnoreCase("q")) {
		    return ((int) (d.getMonth() / 3) + 1) + "";
		}
		return new SimpleDateFormat(format).format((Date) obj);
	    } catch (Exception e) {
		System.err.println("Error compiling date format " + format + ", " + e);
	    }
	}
	System.out.println("Need to handle date part properly for " + format + ", " + obj);
	return obj + "";
    }

    protected String Format(Object obj, String format) {
	if (obj instanceof Date && format != null) {
	    try {
		if ("Long Date".equalsIgnoreCase(format)) {
		    format = "dd-MMM-yyyy";
		} else if ("Medium Date".equalsIgnoreCase(format)) {
		    format = "dd-MM-yy";
		} else {
		    format = format.replace("mmm", "MMM");
		    format = format.replace("mm", "MM");
		}
		return new SimpleDateFormat(format).format((Date) obj);
	    } catch (Exception e) {
		System.err.println("Error compiling date format " + format + ", " + e);
	    }
	}
	return obj + "";
    }

    protected String Left(Object field, int num) {
	Object val = getValueFromObject(field);
	if (val == null)
	    return "";
	return val.toString().substring(0, num);
    }

    protected float Sum(Object source) {
	if (collectors.containsKey(source)) {
	    return (float) collectors.get(source).value;
	}
	return 0f;
    }

    protected int Count(Object source) {
	if (collectors.containsKey(source)) {
	    return (int) collectors.get(source).value;
	}
	if (source instanceof String) {
	    SectionInfo candidate = null;
	    for (SectionInfo sec : reportSections.values()) {
		if (sec.groupNumber >= 0) {
		    candidate = sec;
		    if ((sec.groupNumber + "").equals(source)) {
			return sec.rowCountInGroup;
		    }
		}
	    }
	    if (candidate != null)
		return candidate.rowCountInGroup;
	}
	if (source instanceof CollectionDatasource) {
	    return ((CollectionDatasource) source).getItems().size();
	}
	return -1;
    }

    protected Object iif(boolean cond, Object opt1, Object opt2) {
	if (cond)
	    return opt1;
	return opt2;
    }

    @Inject
    private ExportDisplay exportDisplay;
    private Font pdfFont = new Font(FontFamily.HELVETICA, 8);
    private Rectangle pdfPageSize;

    private void exportToPdf() {
	try {
	    pdfPageSize = PageSize.A4;

	    System.out.println("EXPORT HEIGHT=" + pageHeight + ", WIDTH=" + reportWidth);
	    if (pageHeight < reportWidth) {
		pdfPageSize = PageSize.A4.rotate();
	    }

	    Document document = new Document(pdfPageSize, -50, -50, 50, 50);
	    // note the negative margins for left and right - otherwise side
	    // margins are too big.

	    // document.setPageSize(new Rectangle(800, 1100));
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PdfWriter writer = PdfWriter.getInstance(document, outputStream);

	    document.open();

	    processForPDF(reportLayout, null, null, writer, document);

	    document.close();

	    exportDisplay.show(new ByteArrayDataProvider(outputStream.toByteArray()), getCaption() + ".pdf", ExportFormat.PDF);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private int processForPDF(Component comp, PdfPTable parent, WebGridLayout grid, PdfWriter writer, Document document) {

	if (comp instanceof Container) {
	    int colCnt = 1;
	    Container cont = (Container) comp;
	    if (comp instanceof WebHBoxLayout) {
		WebHBoxLayout hbox = (WebHBoxLayout) comp;
		colCnt = hbox.getOwnComponents().size();
	    } else if (comp instanceof WebVBoxLayout) {
	    } else if (comp instanceof WebGridLayout) {
		grid = (WebGridLayout) comp;
		colCnt = grid.getColumns();
	    } else {
		System.out.println("DIDNT EXPECT A " + comp + " HERE!");
	    }
	    colCnt = Math.max(1, colCnt);
	    PdfPTable table = null;

	    boolean isTopLevel = comp.equals(reportLayout);

	    if (!isTopLevel) {
		table = new PdfPTable(colCnt);

		table.setWidthPercentage(95);
		table.setSpacingAfter(0);
		table.setSpacingBefore(0);
		table.setPaddingTop(0);
	    }

	    int count = 0;
	    for (Component child : cont.getOwnComponents()) {
		count += processForPDF(child, table, grid, writer, document);
	    }

	    if (table != null) {
		while (count % colCnt != 0) {
		    count++;
		    addCell(-1, 1, 0, -1, false, table, null);
		}
		if (parent != null) {
		    System.out.println("Add table " + table + ", comp=" + comp + ", height " + comp.getHeight() + ", parent=" + parent);
		    addCell(-1, 1, 0, comp.getHeight(), false, parent, table);
		} else {
		    try {
			table.setWidthPercentage(85);
			document.add(table);
			if (hasGroupBreaks) {
			    document.newPage();
			}
		    } catch (DocumentException e) {
			e.printStackTrace();
		    }
		}
	    }

	    return 1;

	} else {
	    Area area = grid.getComponentArea(comp);
	    int colspan = area.getColumn2() - area.getColumn1() + 1;
	    if (comp instanceof WebLabel) {
		WebLabel label = (WebLabel) comp;
		Element element = null;
		if ("report-line".equals(label.getStyleName())) {
		    element = createLine(writer, label.getWidth());
		} else {
		    element = new Phrase(label.getValue(), pdfFont);
		}
		addCell(area.getRow1(), colspan, 1, comp.getHeight(), false, parent, element);
	    } else if (comp instanceof WebImage) {
		WebImage img = (WebImage) comp;
		// Element image =
		// com.itextpdf.text.Image.getInstance(img.getSource());
		// PdfPCell cell = new PdfPCell();
		// cell.addElement(image);
		// parent.addCell(cell);
	    }
	    return colspan;
	}
    }

    private PdfPCell addCell(int row, int colspan, int padding, float height, boolean noWrap, PdfPTable table, Element element) {
	while (row > table.getRows().size()) {
	    table.addCell(createCell(1, padding, height, noWrap, null));
	}
	PdfPCell cell = createCell(colspan, padding, height, noWrap, element);
	table.addCell(cell);
	return cell;
    }

    private PdfPCell createCell(int colspan, int padding, float height, boolean noWrap, Element element) {

	System.out.println("ADD CELL " + element + ", " + height);
	height = 0.9f * height * pdfPageSize.getHeight() / pageHeight;
	// height = (int)(height * 0.65);

	PdfPCell cell = new PdfPCell(new Phrase());
	cell.setPadding(padding);
	cell.setColspan(colspan);
	cell.setNoWrap(noWrap);
	cell.setBorder(Rectangle.NO_BORDER);
	if (height > 0) {
	    cell.setFixedHeight(height);
	}
	// cell.addElement HAS to come before table.addCell
	if (element != null) {
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.addElement(element);
	}
	return cell;
    }

    private com.itextpdf.text.Image createLine(PdfWriter writer, float width) {
	try {
	    PdfContentByte canvas = writer.getDirectContent();
	    PdfTemplate template = canvas.createTemplate(width, 5);
	    template.setLineWidth(1);
	    template.moveTo(1, 3);
	    template.lineTo(width, 3);
	    template.stroke();
	    return com.itextpdf.text.Image.getInstance(template);
	} catch (BadElementException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public int getPageLimit() {
	return pageLimit;
    }

    public void setPageLimit(int pageLimit) {
	this.pageLimit = pageLimit;
    }
}
