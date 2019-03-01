package com.xformix.comicbook.web.tblcatalog;

import java.util.Map;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.xformix.comicbook.entity.Tblcatalog;

public class TblcatalogEdit extends AbstractEditor<Tblcatalog> {

    private CloseListener closeListener;
    
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }
    @Override
    protected void postInit() {
        setShowSaveNotification(false);
    }

    @Override
    protected boolean preCommit() {
	if(this.closeListener != null) {
	    boolean wasNew = PersistenceHelper.isNew(getItem());
	    if (wasNew) {
		this.closeListener.windowClosed("new");
	    } else {
		this.closeListener.windowClosed("old");
	    }
	}
        return super.preCommit();
    }
    
    public void addBeforeCommitListener(CloseListener closeListener) {
	this.closeListener = closeListener;
    }
    
}