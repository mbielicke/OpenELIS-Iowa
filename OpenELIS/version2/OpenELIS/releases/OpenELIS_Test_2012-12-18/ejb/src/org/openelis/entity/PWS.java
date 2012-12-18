package org.openelis.entity;

/**
 * PWS Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@NamedQueries( {
    @NamedQuery( name = "PWS.FetchById",
                query = "select new org.openelis.domain.PWSDO(p.id, p.tinwsysIsNumber, p.number0, p.alternateStNum, p.name," +
                        "p.activityStatusCd, p.dPrinCitySvdNm, p.dPrinCntySvdNm,p.dPopulationCount, p.dPwsStTypeCd," +
                        "p.activityRsnTxt, p.startDay, p.startMonth, p.endDay, p.endMonth, p.effBeginDt, p.effEndDt)"
                      + " from PWS p where p.id = :id"),
    @NamedQuery( name = "PWS.FetchByTinwsysIsNumber",
                query = "select new org.openelis.domain.PWSDO(p.id, p.tinwsysIsNumber, p.number0, p.alternateStNum, p.name," +
            	        "p.activityStatusCd, p.dPrinCitySvdNm, p.dPrinCntySvdNm,p.dPopulationCount, p.dPwsStTypeCd," +
             	        "p.activityRsnTxt, p.startDay, p.startMonth, p.endDay, p.endMonth, p.effBeginDt, p.effEndDt)"
                      + " from PWS p where p.tinwsysIsNumber = :tinwsysIsNumber"),               
    @NamedQuery( name = "PWS.FetchByNumber0",
                query = "select new org.openelis.domain.PWSDO(p.id, p.tinwsysIsNumber, p.number0, p.alternateStNum, p.name," +
                        " p.activityStatusCd, p.dPrinCitySvdNm, p.dPrinCntySvdNm,p.dPopulationCount, p.dPwsStTypeCd," +
                        " p.activityRsnTxt, p.startDay, p.startMonth, p.endDay, p.endMonth, p.effBeginDt, p.effEndDt)"
                      + " from PWS p where p.number0 = :number0")})                 

@Entity
@Table(name = "pws")
public class PWS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tinwsys_is_number")
    private Integer tinwsysIsNumber;

    @Column(name = "number0")
    private String  number0;

    @Column(name = "alternate_st_num")
    private String  alternateStNum;

    @Column(name = "name")
    private String  name;

    @Column(name = "activity_status_cd")
    private String  activityStatusCd;

    @Column(name = "d_prin_city_svd_nm")
    private String  dPrinCitySvdNm;

    @Column(name = "d_prin_cnty_svd_nm")
    private String  dPrinCntySvdNm;

    @Column(name = "d_population_count")
    private Integer dPopulationCount;

    @Column(name = "d_pws_st_type_cd")
    private String  dPwsStTypeCd;

    @Column(name = "activity_rsn_txt")
    private String  activityRsnTxt;

    @Column(name = "start_day")
    private Integer startDay;

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "end_day")
    private Integer endDay;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "eff_begin_dt")
    private Date    effBeginDt;

    @Column(name = "eff_end_dt")
    private Date    effEndDt;

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

    public String getNumber0() {
        return number0;
    }

    public void setNumber0(String number0) {
        if (DataBaseUtil.isDifferent(number0, this.number0))
            this.number0 = number0;
    }

    public String getAlternateStNum() {
        return alternateStNum;
    }

    public void setAlternateStNum(String alternateStNum) {
        if (DataBaseUtil.isDifferent(alternateStNum, this.alternateStNum))
            this.alternateStNum = alternateStNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getActivityStatusCd() {
        return activityStatusCd;
    }

    public void setActivityStatusCd(String activityStatusCd) {
        if (DataBaseUtil.isDifferent(activityStatusCd, this.number0))
            this.activityStatusCd = activityStatusCd;
    }

    public String getDPrinCitySvdNm() {
        return dPrinCitySvdNm;
    }

    public void setDPrinCitySvdNm(String dPrinCitySvdNm) {
        if (DataBaseUtil.isDifferent(dPrinCitySvdNm, this.dPrinCitySvdNm))
            this.dPrinCitySvdNm = dPrinCitySvdNm;
    }

    public String getDPrinCntySvdNm() {
        return dPrinCntySvdNm;
    }

    public void setDPrinCntySvdNm(String dPrinCntySvdNm) {
        if (DataBaseUtil.isDifferent(dPrinCntySvdNm, this.dPrinCntySvdNm))
            this.dPrinCntySvdNm = dPrinCntySvdNm;
    }

    public Integer getDPopulationCount() {
        return dPopulationCount;
    }

    public void setDPopulationCount(Integer dPopulationCount) {
        if (DataBaseUtil.isDifferent(dPopulationCount, this.dPopulationCount))
            this.dPopulationCount = dPopulationCount;
    }

    public String getDPwsStTypeCd() {
        return dPwsStTypeCd;
    }

    public void setDPwsStTypeCd(String dPwsStTypeCd) {
        if (DataBaseUtil.isDifferent(dPwsStTypeCd, this.dPwsStTypeCd))
            this.dPwsStTypeCd = dPwsStTypeCd;
    }

    public String getActivityRsnTxt() {
        return activityRsnTxt;
    }

    public void setActivityRsnTxt(String activityRsnTxt) {
        if (DataBaseUtil.isDifferent(activityRsnTxt, this.activityRsnTxt))
            this.activityRsnTxt = activityRsnTxt;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        if (DataBaseUtil.isDifferent(startDay, this.startDay))
            this.startDay = startDay;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        if (DataBaseUtil.isDifferent(startMonth, this.startMonth))
            this.startMonth = startMonth;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        if (DataBaseUtil.isDifferent(endDay, this.endDay))
            this.endDay = endDay;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        if (DataBaseUtil.isDifferent(endMonth, this.endMonth))
            this.endMonth = endMonth;
    }

    public Datetime getEffBeginDt() {
        return DataBaseUtil.toYD(effBeginDt);
    }

    public void setEffBeginDt(Datetime eff_begin_dt) {
        if (DataBaseUtil.isDifferentYD(eff_begin_dt, this.effBeginDt))
            this.effBeginDt = DataBaseUtil.toDate(eff_begin_dt);
    }

    public Datetime getEffEndDt() {
        return DataBaseUtil.toYD(effEndDt);
    }

    public void setEffEndDt(Datetime eff_end_dt) {
        if (DataBaseUtil.isDifferentYD(eff_end_dt, this.effEndDt))
            this.effEndDt = DataBaseUtil.toDate(eff_end_dt);
    }
}
