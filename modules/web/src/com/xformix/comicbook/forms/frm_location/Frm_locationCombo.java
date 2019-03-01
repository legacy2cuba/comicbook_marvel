package com.xformix.comicbook.forms.frm_location;

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

import com.xformix.comicbook.entity.TblLocation ;
import com.xformix.cuba.utils.CustomLookup;

public class Frm_locationCombo extends CustomLookup {
    
    @Inject
    protected Table navigationTable;    

    @Inject
    Datasource<TblLocation> tbllocationDs ;

    @Inject
    com.haulmont.cuba.gui.components.TextField Location;

    @Inject
    com.haulmont.cuba.gui.components.TextField Sort_Order;

    @Inject
    com.haulmont.cuba.gui.components.Button Command7;

    @Override
    public void init(Map<String, Object> params) {

setup(navigationTable, tbllocationDs);
        tbllocationDs.addItemChangeListener(new Datasource.ItemChangeListener<TblLocation>() {
            @Override
            public void itemChanged(ItemChangeEvent<TblLocation> e) {


            }
        });


    }
}