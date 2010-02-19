package org.openelis.entity;

/**
 * PwsMonitor Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "pws_monitor")
@EntityListeners( {AuditUtil.class})
public class PwsMonitor implements Auditable, Cloneable {

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

    @Transient
    private PwsMonitor original;

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
        if (DataBaseUtil.isDifferentYD(compBeginDate, comp_begin_date))
            this.compBeginDate = DataBaseUtil.toDate(comp_begin_date);
    }

    public Datetime getCompEndDate() {
        return DataBaseUtil.toYD(compEndDate);
    }

    public void setCompEndDate(Datetime comp_end_date) {
        if (DataBaseUtil.isDifferentYD(compEndDate, comp_end_date))
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

    public void setClone() {
        try {
            original = (PwsMonitor)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.PWS_MONITOR);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("tinwsys_is_number", tinwsysIsNumber, original.tinwsysIsNumber)
                 .setField("st_asgn_ident_cd", stAsgnIdentCd, original.stAsgnIdentCd)
                 .setField("name", name, original.name)
                 .setField("tiaanlgp_tiaanlyt_name", tiaanlgpTiaanlytName, original.tiaanlgpTiaanlytName)
                 .setField("number_samples", numberSamples, original.numberSamples)
                 .setField("comp_begin_date", compBeginDate, original.compBeginDate)
                 .setField("comp_end_date", compEndDate, original.compEndDate)
                 .setField("frequency_name", frequencyName, original.frequencyName)
                 .setField("period_name", periodName, original.periodName);

        return audit;
    }

}
