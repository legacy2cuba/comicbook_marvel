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
        @AttributeOverride(name = "id", column = @Column(name = "rownum"))
})
@Table(name = "qry_rpt_age")
@Entity(name = "comicbook$QryRptAge")
public class QryRptAge extends BaseLongIdEntity {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    protected Date issue_date;

    @Column(name = "title", length = 50)
    protected String title;

    @Column(name = "issuenum")
    protected Double issuenum;

    @Column(name = "publisher", length = 20)
    protected String publisher;

    @Column(name = "cost")
    protected Double cost;

    @Column(name = "comments", length = 90)
    protected String comments;

    @Column(name = "location", length = 50)
    protected String location;

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    public Date getIssue_date() {
        return issue_date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIssuenum(Double issuenum) {
        this.issuenum = issuenum;
    }

    public Double getIssuenum() {
        return issuenum;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getCost() {
        return cost;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }


}

