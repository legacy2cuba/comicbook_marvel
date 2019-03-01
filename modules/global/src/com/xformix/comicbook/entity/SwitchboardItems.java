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
        @AttributeOverride(name = "id", column = @Column(name = "id"))
})
@Table(name = "switchboard_items")
@Entity(name = "comicbook$SwitchboardItems")
public class SwitchboardItems extends BaseUuidEntity {
    @Column(name = "switchboardid")
    protected Integer switchboardid;

    @Column(name = "itemnumber")
    protected Integer itemnumber;

    @Column(name = "itemtext", length = 255)
    protected String itemtext;

    @Column(name = "command")
    protected Integer command;

    @Column(name = "argument", length = 50)
    protected String argument;

    public void setSwitchboardid(Integer switchboardid) {
        this.switchboardid = switchboardid;
    }

    public Integer getSwitchboardid() {
        return switchboardid;
    }

    public void setItemnumber(Integer itemnumber) {
        this.itemnumber = itemnumber;
    }

    public Integer getItemnumber() {
        return itemnumber;
    }

    public void setItemtext(String itemtext) {
        this.itemtext = itemtext;
    }

    public String getItemtext() {
        return itemtext;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public Integer getCommand() {
        return command;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }


}

