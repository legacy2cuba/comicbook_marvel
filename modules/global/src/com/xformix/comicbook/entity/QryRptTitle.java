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
@Table(name = "qry_rpt_title")
@Entity(name = "comicbook$QryRptTitle")
public class QryRptTitle extends BaseLongIdEntity {
    @Column(name = "countofcomicbooknum", length = 40)
    protected Integer countofcomicbooknum;

    @Column(name = "publisher", length = 20)
    protected String publisher;

    @Column(name = "title", length = 50)
    protected String title;

    @Column(name = "sumofcost")
    protected Double sumofcost;

    public void setCountofcomicbooknum(Integer countofcomicbooknum) {
        this.countofcomicbooknum = countofcomicbooknum;
    }

    public Integer getCountofcomicbooknum() {
        return countofcomicbooknum;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSumofcost(Double sumofcost) {
        this.sumofcost = sumofcost;
    }

    public Double getSumofcost() {
        return sumofcost;
    }


}

