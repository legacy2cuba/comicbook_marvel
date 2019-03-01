package com.xformix.comicbook.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import com.haulmont.cuba.core.global.DesignSupport;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.BaseIntegerIdEntity;
import com.haulmont.cuba.core.entity.BaseLongIdEntity;
import com.haulmont.cuba.core.entity.BaseStringIdEntity;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%d|id")
@DesignSupport("{'imported':true}")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "comicbooknum"))
})
@Table(name = "tblcatalog")
@Entity(name = "comicbook$Tblcatalog")
public class Tblcatalog extends BaseIntegerIdEntity {
    @Column(name = "issuenum")
    protected Double issuenum;

    @Column(name = "cost")
    protected Double cost;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date")
    protected Date issue_date;

    @Column(name = "comicTitle", length = 90)
    private String comicTitle;

    @Column(name = "marvelComicId")
    private Integer marvelComicId;
    
    @Column(name = "comments", length = 90)
    protected String comments;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titlenum")
    protected Tbltitle titlenum;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationnum")
    protected TblLocation locationnum;

    public void setIssuenum(Double issuenum) {
        this.issuenum = issuenum;
    }

    public Double getIssuenum() {
        return issuenum;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getCost() {
        return cost;
    }

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    public Date getIssue_date() {
        return issue_date;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setTitlenum(Tbltitle titlenum) {
        this.titlenum = titlenum;
    }

    public Tbltitle getTitlenum() {
        return titlenum;
    }

    public void setLocationnum(TblLocation location) {
        this.locationnum = location;
    }

    public TblLocation getLocationnum() {
        return locationnum;
    }

    public String getComicTitle() {
	return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
	this.comicTitle = comicTitle;
    }

    public Integer getMarvelComicId() {
	return marvelComicId;
    }

    public void setMarvelComicId(Integer marvelComicId) {
	this.marvelComicId = marvelComicId;
    }

}

