package com.xformix.comicbook.forms.frm_comic_entry;

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

import com.xformix.comicbook.entity.Tbltitle ;
import com.xformix.cuba.utils.CustomLookup;
import com.xformix.comicbook.entity.Tblcatalog ;

public class Frm_comic_entryCombo extends CustomLookup {
    
    @Inject
    protected Table navigationTable;    

    @Inject
    Datasource<Tbltitle> tbltitlesDs ;

    @Inject
    Datasource<Tblcatalog> tblcatalogDs ;
    
    @Override
    public void init(Map<String, Object> params) {
	super.init(params);

	((Frm_comic_entryEdit)getComponent("customFrame")).setCombo(this);

	setup(navigationTable, tbltitlesDs);

    }
}