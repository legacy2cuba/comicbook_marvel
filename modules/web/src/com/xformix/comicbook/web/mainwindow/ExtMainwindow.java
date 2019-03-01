package com.xformix.comicbook.web.mainwindow;

import com.haulmont.cuba.gui.components.AbstractMainWindow;
import com.haulmont.cuba.gui.components.Embedded;
import com.haulmont.cuba.gui.components.mainwindow.FtsField;

import javax.inject.Inject;
import java.util.Map;

public class ExtMainwindow extends AbstractMainWindow {
    @Inject
    private FtsField ftsField;

    @Inject
    private Embedded logoImage;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initLayoutAnalyzerContextMenu(logoImage);
        initLogoImage(logoImage);
        initFtsField(ftsField);
    }
}