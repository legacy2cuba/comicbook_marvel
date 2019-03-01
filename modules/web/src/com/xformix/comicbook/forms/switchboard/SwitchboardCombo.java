package com.xformix.comicbook.forms.switchboard;

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

import com.xformix.comicbook.entity.SwitchboardItems ;
import com.xformix.cuba.utils.CustomLookup;

public class SwitchboardCombo extends CustomLookup {
    
    @Inject
    protected Table navigationTable;    

    @Inject
    Datasource<SwitchboardItems> switchboarditemsDs ;

    @Inject
    com.haulmont.cuba.gui.components.Button Option1;

    @Inject
    com.haulmont.cuba.gui.components.Button Option2;

    @Inject
    com.haulmont.cuba.gui.components.Button Option3;

    @Inject
    com.haulmont.cuba.gui.components.Button Option4;

    @Inject
    com.haulmont.cuba.gui.components.Button Option5;

    @Inject
    com.haulmont.cuba.gui.components.Button Option6;

    @Inject
    com.haulmont.cuba.gui.components.Button Option7;

    @Inject
    com.haulmont.cuba.gui.components.Button Option8;

    @Override
    public void init(Map<String, Object> params) {

setup(navigationTable, switchboarditemsDs);
        switchboarditemsDs.addItemChangeListener(new Datasource.ItemChangeListener<SwitchboardItems>() {
            @Override
            public void itemChanged(ItemChangeEvent<SwitchboardItems> e) {


            }
        });


    }
}