package org.openelis.entity;

/**
 * PWSMonitor Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.AuditUtil;

@NamedQuery(name = "PWSMonitor.FetchByTinwsysIsNumber",
           query = "select new org.openelis.domain.PWSMonitorDO(p.id, p.tinwsysIsNumber, p.stAsgnIdentCd, p.name," +
                   "p.tiaanlgpTiaanlytName, p.numberSamples, p.compBeginDate, p.compEndDate, p.frequencyName, p.periodName)"
                 + " from PWSMonitor p where p.tinwsysIsNumber = :tinwsysIsNumber")
@Entity
@Table(name = "pws_monitor")
@EntityListeners( {AuditUtil.class})
public class PWSMonitor {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer    id;

    @Column(name = "tinwsys_is_number")
    private Integer    tinwsysIsNumber;

    @Column(name = "st_asgn_ident_cd")
    private String     stAsgnIdentCd;

    @Column(name = "name")
    private String     name;

    @Column(name = "tiaanlgp_tiaanlyt_name")
    private String     tiaanlgpTiaanlytName;

    @Column(name = "number_samples")
    private Integer    numberSamples;

    @Column(name = "comp_begin_date")
    private Date       compBeginDate;

    @Column(name = "comp_end_date")
    private Date       compEndDate;

    @Column(name = "frequency_name")
    private String     frequencyName;

    @Column(name = "period_name")
    private String     periodName;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        if (DataBaseUtil.isDifferent(tinwsysIsNumber, this.tinwsysIsNumber))
            this.tinwsysIsNumber = tinwsysIsNumber;
    }

    public String getStAsgnIdentCd() {
        return stAsgnIdentCd;
    }

    public void setStAsgnIdentCd(String stAsgnIdentCd) {
        if (DataBaseUtil.isDifferent(stAsgnIdentCd, this.stAsgnIdentCd))
            this.stAsgnIdentCd = stAsgnIdentCd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getTiaanlgpTiaanlytName() {
        return tiaanlgpTiaanlytName;
    }

    public void setTiaanlgpTiaanlytName(String tiaanlgpTiaanlytName) {
        if (DataBaseUtil.isDifferent(tiaanlgpTiaanlytName, this.tiaanlgpTiaanlytName))
            this.tiaanlgpTiaanlytName = tiaanlgpTiaanlytName;
    }

    public Integer getNumberSamples() {
        return numberSamples;
    }

    public void setNumberSamples(Integer numberSamples) {
        if (DataBaseUtil.isDifferent(numberSamples, this.numberSamples))
            this.numberSamples = numberSamples;
    }

    public Datetime getCompBeginDate() {
        return DataBaseUtil.toYD(compBeginDate);
    }

    public void setCompBeginDate(Datetime comp_begin_date) {
        if (DataBaseUtil.isDifferentYD(comp_begin_date, this.compBeginDate))
            this.compBeginDate = DataBaseUtil.toDate(comp_begin_date);
    }

    public Datetime getCompEndDate() {
        return DataBaseUtil.toYD(compEndDate);
    }

    public void setCompEndDate(Datetime comp_end_date) {
        if (DataBaseUtil.isDifferentYD(comp_end_date, this.compEndDate))
            this.compEndDate = DataBaseUtil.toDate(comp_end_date);
    }

    public String getFrequencyName() {
        return frequencyName;
    }

    public void setFrequencyName(String frequencyName) {
        if (DataBaseUtil.isDifferent(frequencyName, this.frequencyName))
            this.frequencyName = frequencyName;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        if (DataBaseUtil.isDifferent(periodName, this.periodName))
            this.periodName = periodName;
    }        
}
