package com.xformix.comicbook.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.xformix.apitools.JSONPath;

@NamePattern("%s|title")
@MetaClass(name = "comicbook$MarvelSeries")
public class MarvelSeries extends BaseUuidEntity {
    private static final long serialVersionUID = 7751762870601429660L;

    @MetaProperty
    protected String title;

    @JSONPath(path="id")
    @MetaProperty
    protected Integer marvelid;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMarvelid(Integer marvelid) {
        this.marvelid = marvelid;
    }

    public Integer getMarvelid() {
        return marvelid;
    }


}