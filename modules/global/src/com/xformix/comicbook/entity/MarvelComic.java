package com.xformix.comicbook.entity;

import java.util.Date;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.xformix.apitools.JSONPath;

@NamePattern("%s|title")
@MetaClass(name = "comicbook$MarvelComic")
public class MarvelComic extends BaseUuidEntity {
    private static final long serialVersionUID = 6471978615339841615L;

    @MetaProperty
    protected String title;

    @JSONPath(path="id")
    @MetaProperty
    protected Integer marvelid;

    @MetaProperty
    private Integer issueNumber;

    @JSONPath(path="prices[0]/price")
    @MetaProperty
    private Double price;

    @JSONPath(path="dates[0]/date")
    @MetaProperty
    private Date issueDate;

    @JSONPath(path="images[0]/path|{/standard_xlarge.}|images[0]/extension")
    @MetaProperty
    protected String image;

//    can't add @MetaProperty here because JPA does not support String[]
    @JSONPath(path="creators/items/name")
    private String[] creators;

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

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public Integer getIssueNumber() {
	return issueNumber;
    }

    public void setIssueNumber(Integer issueNumber) {
	this.issueNumber = issueNumber;
    }

    public Double getPrice() {
	return price;
    }

    public void setPrice(Double price) {
	this.price = price;
    }

    public Date getIssueDate() {
	return issueDate;
    }

    public void setIssueDate(Date issueDate) {
	this.issueDate = issueDate;
    }

    public String[] getCreators() {
	return creators;
    }

    public void setCreators(String[] creators) {
	this.creators = creators;
    }


}