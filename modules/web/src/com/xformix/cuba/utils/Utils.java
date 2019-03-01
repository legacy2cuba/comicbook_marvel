package com.xformix.cuba.utils;

import java.util.HashMap;
import java.util.Map;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.LoadContext.Query;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.Frame.MessageType;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window.CloseWithCommitListener;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.GroupDatasource;

public class Utils {

    private static Map<Class, CollectionDatasource> searchDatasources = new HashMap();

    public static Entity load(Class entityClass, String query, DataManager dataManager) {
	String nqName = entityClass.getName();
	nqName = nqName.substring(nqName.lastIndexOf(".") + 1);
	Query q = LoadContext.createQuery(query);
	return dataManager.load(LoadContext.create(entityClass).setView(nqName.toLowerCase() + "-view").setQuery(q));
    }

    public static Double parseDouble(Object value) {
	try {
	    return Double.parseDouble(value + "");
	} catch (Exception e) {
	}
	return 0.0;
    }

    public static Integer parseInt(Object value) {
	try {
	    return Integer.parseInt(value + "");
	} catch (Exception e) {
	}
	return 0;
    }

    public static AbstractEditor openEditorDialog(AbstractEditor abEd, Datasource ds, Entity att, String frameName, Table table, Map<String, Object> map) {
	AbstractEditor ed = abEd.openEditor(frameName, att, WindowManager.OpenType.DIALOG, map);

	ed.addCloseWithCommitListener(new CloseWithCommitListener() {
	    @Override
	    public void windowClosedWithCommitAction() {
		ds.commit();
		ds.refresh();
		table.getDatasource().refresh();
	    }
	});
	return ed;
    }

    public static void deleteEntityConfirm(AbstractEditor abEd, CollectionDatasource ds, Entity att, String msg, final Table attGrid) {
	abEd.showOptionDialog("Confirm", msg, MessageType.CONFIRMATION, new Action[] { new DialogAction(DialogAction.Type.YES) {
	    @Override
	    public void actionPerform(Component component) {
		ds.removeItem(att);
		ds.commit();
		attGrid.getDatasource().refresh();
	    }
	}, new DialogAction(DialogAction.Type.NO) });
    }

    public static void setupNewOptionLookup(LookupField lookup) {
	lookup.setNewOptionAllowed(true);
	lookup.setNewOptionHandler(new LookupField.NewOptionHandler() {
	    @Override
	    public void addNewOption(String newVal) {
		lookup.getOptionsList().add(newVal);
		lookup.setValue(newVal);
	    }
	});
    }

    public static Datasource getDatasource(DsContext context, String dsId) {
	while (context != null) {
	    Datasource ds = context.get(dsId);
	    if (ds != null) {
		return ds;
	    }
	    context = context.getParent();
	}
	System.out.println("DIDNT FIND DS " + dsId);
	return null;
    }

    public static CollectionDatasource getDatasource(DsContext dsContext, String dsId, String query) {
	CollectionDatasource ds = (CollectionDatasource) getDatasource(dsContext, dsId);
	ds.setQuery(query);
	ds.refresh();
	return ds;
    }

    public static void setDsForTable(AbstractEditor editor, String frameId, String tableId, String dsId) {
	Table grid = (Table) ((Frame) editor.getComponent(frameId)).getComponent(tableId);
	grid.setDatasource((CollectionDatasource) Utils.getDatasource(editor.getDsContext(), dsId));
    }

    public static void updateDatasource(DsContext dsContext, String dsID) {
	Datasource ds = getDatasource(dsContext, dsID);
	if (ds instanceof GroupDatasource) {
	    try {
		GroupDatasource gds = (GroupDatasource) ds;
		if (gds.getItems() != null && gds.getItems().size() == 1) {
		    Entity item = (Entity) gds.getItems().iterator().next();
		    if (item != null)
			gds.setItem(item);
		}
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    public static void refreshDatasource(DsContext dsContext, String dsId) {
	getDatasource(dsContext, dsId).refresh();
    }
    
    public static <T extends Entity> T reload(DsContext dsContext, T entity, Datasource<T> datasource) {
	return dsContext.getDataSupplier().reload(entity, datasource.getView(), null, datasource.getLoadDynamicAttributes());
    }
    

    public static <T extends Entity> T create(Class<T> entityClass, Metadata metadata, DataManager dataManager) {
	T created = metadata.create(entityClass);
	boolean ok = false;
	int attempt = 0;
	while (attempt++ < 100 && !ok) {
	    ok = true;
	    try {
		LoadContext<T> context = new LoadContext<>(metadata.getClass(entityClass));
		context.setId(created.getId());
		T ass = dataManager.load(context);
		if (ass != null) {
		    ok = false;
		}
	    } catch (Exception e) {
		ok = false;
	    }
	    if (!ok) {
		created = metadata.create(entityClass);
	    }
	}
	return created;
    }

}
