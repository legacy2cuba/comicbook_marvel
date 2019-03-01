package com.xformix.cuba.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.KeyCombination;
import com.haulmont.cuba.gui.components.SplitPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Table.Column;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.Datasource.ItemChangeEvent;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import com.haulmont.cuba.web.gui.components.WebVBoxLayout;
//import com.vaadin.ui.GridLayout.Area;

public class CustomLookup extends AbstractLookup {

    protected CollectionDatasource mainDs;
    protected Table navTable;
    protected Entity newItem;
    protected Entity currentEntity;

    protected WebGridLayout subReportGrid;
    protected List<Column> subReportColumns;
    /**
     * The {@link RemoveAction} instance, related to
     */
    @Named("navigationTable.remove")
    protected RemoveAction navigationTableRemove;

    /**
     * The {@link BoxLayout} instance that contains components on the right side
     * of {@link SplitPanel}
     */
    @Inject
    private BoxLayout editBox;

    /**
     * is being created
     */
    protected boolean creating;

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

    protected void setup(Table navigationTable, Datasource mainDs) {

	this.navTable = navigationTable;
	this.mainDs = (CollectionDatasource) mainDs;

	this.mainDs.addItemChangeListener(new Datasource.ItemChangeListener() {
	    @Override
	    public void itemChanged(ItemChangeEvent e) {
		for (Datasource ds : mainDs.getDsContext().getAll()) {
		    if (ds.getItem() == null && ds instanceof CollectionDatasource) {
			CollectionDatasource gds = (CollectionDatasource) ds;
			if (gds.getItems() != null && gds.getItems().size() == 1) {
			    Entity item = (Entity) gds.getItems().iterator().next();
			    if (item != null) {
				gds.setItem(item);
			    }
			}
		    }
		}
	    }
	});

	/*
	 * Adding ESCAPE shortcut that invokes cancel() method
	 */
	editBox.addShortcutAction(new ShortcutAction(new KeyCombination(KeyCombination.Key.ESCAPE), shortcutTriggeredEvent -> cancel()));

	navTable.addAction(new CreateAction(navTable) {
	    @Override
	    protected void internalOpenEditor(CollectionDatasource datasource, Entity newItem, Datasource parentDs, Map<String, Object> params) {
		navTable.setSelected(Collections.emptyList());
		setNewItem(newItem);
		mainDs.setItem(newItem);
		refreshOptionsForLookupFields();
		enableEditControls(true);
	    }
	});
	/*
	 * enables controls for record editing
	 */
	navTable.addAction(new EditAction(navTable) {
	    @Override
	    protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
		if (navTable.getSelected().size() == 1) {
		    refreshOptionsForLookupFields();
		    enableEditControls(false);
		}
	    }

	    @Override
	    public void refreshState() {
		if (target != null) {
		    CollectionDatasource ds = target.getDatasource();
		    if (ds != null && !captionInitialized) {
			setCaption(messages.getMainMessage("actions.Edit"));
		    }
		}
		super.refreshState();
	    }

	    @Override
	    protected boolean isPermitted() {
		CollectionDatasource ownerDatasource = target.getDatasource();
		boolean entityOpPermitted = security.isEntityOpPermitted(ownerDatasource.getMetaClass(), EntityOp.UPDATE);
		if (!entityOpPermitted) {
		    return false;
		}
		return super.isPermitted();
	    }
	});
	/*
	 * Setting {@link RemoveAction#afterRemoveHandler} for {@link
	 * categoriesesTableRemove} to reset record, contained in {@link
	 * categoriesEditDs}
	 */
	navigationTableRemove.setAfterRemoveHandler(removedItems -> mainDs.setItem(null));

	disableEditControls();

    }

    protected void refreshOptionsForLookupFields() {
	// for (Component component : fieldGroup.getOwnComponents()) {
	// if (component instanceof LookupField) {
	// CollectionDatasource optionsDatasource = ((LookupField)
	// component).getOptionsDatasource();
	// if (optionsDatasource != null) {
	// optionsDatasource.refresh();
	// }
	// }
	// }
    }

    protected void setNewItem(Entity newItem) {
	this.newItem = newItem;
    }

    /**
     * Enabling controls for record editing
     * 
     * @param creating
     *            created
     */
    protected void enableEditControls(boolean creating) {
	this.creating = creating;
	initEditComponents(true);
	customFrame.requestFocus();
    }

    /**
     * Disabling editing controls
     */
    protected void disableEditControls() {
	initEditComponents(false);
	navTable.requestFocus();
    }

    /**
     * Initiating edit controls, depending on if they should be enabled/disabled
     * 
     * @param enabled
     *            if true - enables editing controls and disables controls on
     *            the left side of the splitter if false - visa versa
     */
    protected void initEditComponents(boolean enabled) {
	// fieldGroup.setEditable(enabled);
	setEditable(enabled, customFrame);
	actionsPane.setVisible(enabled);
	lookupBox.setEnabled(!enabled);
    }

    protected void collectValidatables(Component comp, List<Validatable> validatables) {
	try {
	    if (comp instanceof Validatable) {
		validatables.add((Validatable) comp);
	    }
	} catch (Exception e) {
	}
	if (comp instanceof Container) {
	    for (Component component : ((Container) comp).getOwnComponents()) {
		collectValidatables(component, validatables);
	    }
	}
    }

    protected void setEditable(boolean editable, Component comp) {
	try {
	    Method m = comp.getClass().getMethod("setEditable", boolean.class);
	    m.invoke(comp, editable);
	} catch (Exception e) {
	}
	if (comp instanceof DataGrid) {
	    ((DataGrid) comp).setEditorEnabled(editable);
	}
	if (comp instanceof Container) {
	    for (Component component : ((Container) comp).getOwnComponents()) {
		setEditable(editable, component);
	    }
	}
    }

    /**
     * Method that is invoked by clicking Cancel button, discards changes and
     * disables controls for record editing
     */
    public void cancel() {
	// different from original - suppliersesDs not used - is this going to
	// work?
	Entity selectedItem = mainDs.getItem();
	if (selectedItem != null) {
	    try {
		Entity reloadedItem = getDsContext().getDataSupplier().reload(selectedItem, mainDs.getView());
		mainDs.setItem(reloadedItem);
	    } catch (Exception ex) {
		System.err.println("Error during cancel op: " + ex);
		mainDs.setItem(null);
	    }
	} else {
	    mainDs.setItem(null);
	}
	disableEditControls();
    }

    /**
     * Method that is invoked by clicking Ok button after editing an existing or
     * creating a new record
     */
    public void save() {

	List<Validatable> vals = new ArrayList<>();
	collectValidatables(customFrame, vals);
	if (!validate(vals)) {
	    return;
	}

	if (creating) {
	    mainDs.addItem(newItem);
	}

	boolean saved = getDsContext().commit();
	if (!saved)
	    System.out.println("NOT SAVED - likely that the datasource is not marked as changed");

	Entity editedItem = mainDs.getItem();
	if (creating) {
	    mainDs.includeItem(editedItem);
	} else {
	    mainDs.updateItem(editedItem);
	}
	navTable.setSelected(editedItem);

	disableEditControls();
    }

}
