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
@Table(name = "qry_rpt_cost")
@Entity(name = "comicbook$QryRptCost")
public class QryRptCost extends BaseLongIdEntity {
    @Column(name = "sum_of_cost")
    protected Double sum_of_cost;

    @Column(name = "avg_of_cost", length = 40)
    protected String avg_of_cost;

    @Column(name = "min_of_cost", length = 40)
    protected String min_of_cost;

    @Column(name = "max_of_cost", length = 40)
    protected String max_of_cost;

    @Column(name = "count_of_qry_rpt_catalog", length = 40)
    protected String count_of_qry_rpt_catalog;

    public void setSum_of_cost(Double sum_of_cost) {
        this.sum_of_cost = sum_of_cost;
    }

    public Double getSum_of_cost() {
        return sum_of_cost;
    }

    public void setAvg_of_cost(String avg_of_cost) {
        this.avg_of_cost = avg_of_cost;
    }

    public String getAvg_of_cost() {
        return avg_of_cost;
    }

    public void setMin_of_cost(String min_of_cost) {
        this.min_of_cost = min_of_cost;
    }

    public String getMin_of_cost() {
        return min_of_cost;
    }

    public void setMax_of_cost(String max_of_cost) {
        this.max_of_cost = max_of_cost;
    }

    public String getMax_of_cost() {
        return max_of_cost;
    }

    public void setCount_of_qry_rpt_catalog(String count_of_qry_rpt_catalog) {
        this.count_of_qry_rpt_catalog = count_of_qry_rpt_catalog;
    }

    public String getCount_of_qry_rpt_catalog() {
        return count_of_qry_rpt_catalog;
    }


}

