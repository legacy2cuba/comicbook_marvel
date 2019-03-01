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

@NamePattern("%s|title")
@DesignSupport("{'imported':true}")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "titlenum"))
})
@Table(name = "tbltitle")
@Entity(name = "comicbook$Tbltitle")
public class Tbltitle extends BaseIntegerIdEntity {
    @Column(name = "title", length = 50)
    protected String title;

    @Column(name = "series", length = 50)
    protected String series;

    @Column(name = "marvelSeriesId")
    private Integer marvelSeriesId;

    @Column(name = "publisher", length = 20)
    protected String publisher;

    @Column(name = "current")
    protected Boolean current;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean getCurrent() {
        return current;
    }

    public Integer getMarvelSeriesId() {
	return marvelSeriesId;
    }

    public void setMarvelSeriesId(Integer marvelSeriesId) {
	this.marvelSeriesId = marvelSeriesId;
    }


}

