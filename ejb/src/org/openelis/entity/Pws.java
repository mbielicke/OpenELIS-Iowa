
package org.openelis.entity;

/**
  * Pws Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="pws")
@EntityListeners({AuditUtil.class})
public class Pws implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="tinwsys_is_number")
  private Integer tinwsysIsNumber;             

  @Column(name="number0")
  private String number0;             

  @Column(name="alternate_st_num")
  private String alternateStNum;             

  @Column(name="name")
  private String name;             

  @Column(name="activity_status_cd")
  private String activityStatusCd;             

  @Column(name="d_prin_city_svd_nm")
  private String dPrinCitySvdNm;             

  @Column(name="d_prin_cnty_svd_nm")
  private String dPrinCntySvdNm;             

  @Column(name="d_population_count")
  private Integer dPopulationCount;             

  @Column(name="d_pws_st_type_cd")
  private String dPwsStTypeCd;             

  @Column(name="activity_rsn_txt")
  private String activityRsnTxt;             

  @Column(name="start_day")
  private Integer startDay;             

  @Column(name="start_month")
  private Integer startMonth;             

  @Column(name="end_day")
  private Integer endDay;             

  @Column(name="end_month")
  private Integer endMonth;             

  @Column(name="eff_begin_dt")
  private Date effBeginDt;             

  @Column(name="eff_end_dt")
  private Date effEndDt;             


  @Transient
  private Pws original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTinwsysIsNumber() {
    return tinwsysIsNumber;
  }
  public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
    if((tinwsysIsNumber == null && this.tinwsysIsNumber != null) || 
       (tinwsysIsNumber != null && !tinwsysIsNumber.equals(this.tinwsysIsNumber)))
      this.tinwsysIsNumber = tinwsysIsNumber;
  }

  public String getNumber0() {
    return number0;
  }
  public void setNumber0(String number0) {
    if((number0 == null && this.number0 != null) || 
       (number0 != null && !number0.equals(this.number0)))
      this.number0 = number0;
  }

  public String getAlternateStNum() {
    return alternateStNum;
  }
  public void setAlternateStNum(String alternateStNum) {
    if((alternateStNum == null && this.alternateStNum != null) || 
       (alternateStNum != null && !alternateStNum.equals(this.alternateStNum)))
      this.alternateStNum = alternateStNum;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getActivityStatusCd() {
    return activityStatusCd;
  }
  public void setActivityStatusCd(String activityStatusCd) {
    if((activityStatusCd == null && this.activityStatusCd != null) || 
       (activityStatusCd != null && !activityStatusCd.equals(this.activityStatusCd)))
      this.activityStatusCd = activityStatusCd;
  }

  public String getDPrinCitySvdNm() {
    return dPrinCitySvdNm;
  }
  public void setDPrinCitySvdNm(String dPrinCitySvdNm) {
    if((dPrinCitySvdNm == null && this.dPrinCitySvdNm != null) || 
       (dPrinCitySvdNm != null && !dPrinCitySvdNm.equals(this.dPrinCitySvdNm)))
      this.dPrinCitySvdNm = dPrinCitySvdNm;
  }

  public String getDPrinCntySvdNm() {
    return dPrinCntySvdNm;
  }
  public void setDPrinCntySvdNm(String dPrinCntySvdNm) {
    if((dPrinCntySvdNm == null && this.dPrinCntySvdNm != null) || 
       (dPrinCntySvdNm != null && !dPrinCntySvdNm.equals(this.dPrinCntySvdNm)))
      this.dPrinCntySvdNm = dPrinCntySvdNm;
  }

  public Integer getDPopulationCount() {
    return dPopulationCount;
  }
  public void setDPopulationCount(Integer dPopulationCount) {
    if((dPopulationCount == null && this.dPopulationCount != null) || 
       (dPopulationCount != null && !dPopulationCount.equals(this.dPopulationCount)))
      this.dPopulationCount = dPopulationCount;
  }

  public String getDPwsStTypeCd() {
    return dPwsStTypeCd;
  }
  public void setDPwsStTypeCd(String dPwsStTypeCd) {
    if((dPwsStTypeCd == null && this.dPwsStTypeCd != null) || 
       (dPwsStTypeCd != null && !dPwsStTypeCd.equals(this.dPwsStTypeCd)))
      this.dPwsStTypeCd = dPwsStTypeCd;
  }

  public String getActivityRsnTxt() {
    return activityRsnTxt;
  }
  public void setActivityRsnTxt(String activityRsnTxt) {
    if((activityRsnTxt == null && this.activityRsnTxt != null) || 
       (activityRsnTxt != null && !activityRsnTxt.equals(this.activityRsnTxt)))
      this.activityRsnTxt = activityRsnTxt;
  }

  public Integer getStartDay() {
    return startDay;
  }
  public void setStartDay(Integer startDay) {
    if((startDay == null && this.startDay != null) || 
       (startDay != null && !startDay.equals(this.startDay)))
      this.startDay = startDay;
  }

  public Integer getStartMonth() {
    return startMonth;
  }
  public void setStartMonth(Integer startMonth) {
    if((startMonth == null && this.startMonth != null) || 
       (startMonth != null && !startMonth.equals(this.startMonth)))
      this.startMonth = startMonth;
  }

  public Integer getEndDay() {
    return endDay;
  }
  public void setEndDay(Integer endDay) {
    if((endDay == null && this.endDay != null) || 
       (endDay != null && !endDay.equals(this.endDay)))
      this.endDay = endDay;
  }

  public Integer getEndMonth() {
    return endMonth;
  }
  public void setEndMonth(Integer endMonth) {
    if((endMonth == null && this.endMonth != null) || 
       (endMonth != null && !endMonth.equals(this.endMonth)))
      this.endMonth = endMonth;
  }

  public Datetime getEffBeginDt() {
    if(effBeginDt == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,effBeginDt);
  }
  public void setEffBeginDt (Datetime eff_begin_dt){
    if((effBeginDt == null && this.effBeginDt != null) || 
       (effBeginDt != null && !effBeginDt.equals(this.effBeginDt)))
      this.effBeginDt = eff_begin_dt.getDate();
  }

  public Datetime getEffEndDt() {
    if(effEndDt == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,effEndDt);
  }
  public void setEffEndDt (Datetime eff_end_dt){
    if((effEndDt == null && this.effEndDt != null) || 
       (effEndDt != null && !effEndDt.equals(this.effEndDt)))
      this.effEndDt = eff_end_dt.getDate();
  }

  
  public void setClone() {
    try {
      original = (Pws)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(tinwsysIsNumber,original.tinwsysIsNumber,doc,"tinwsys_is_number");

      AuditUtil.getChangeXML(number0,original.number0,doc,"number0");

      AuditUtil.getChangeXML(alternateStNum,original.alternateStNum,doc,"alternate_st_num");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(activityStatusCd,original.activityStatusCd,doc,"activity_status_cd");

      AuditUtil.getChangeXML(dPrinCitySvdNm,original.dPrinCitySvdNm,doc,"d_prin_city_svd_nm");

      AuditUtil.getChangeXML(dPrinCntySvdNm,original.dPrinCntySvdNm,doc,"d_prin_cnty_svd_nm");

      AuditUtil.getChangeXML(dPopulationCount,original.dPopulationCount,doc,"d_population_count");

      AuditUtil.getChangeXML(dPwsStTypeCd,original.dPwsStTypeCd,doc,"d_pws_st_type_cd");

      AuditUtil.getChangeXML(activityRsnTxt,original.activityRsnTxt,doc,"activity_rsn_txt");

      AuditUtil.getChangeXML(startDay,original.startDay,doc,"start_day");

      AuditUtil.getChangeXML(startMonth,original.startMonth,doc,"start_month");

      AuditUtil.getChangeXML(endDay,original.endDay,doc,"end_day");

      AuditUtil.getChangeXML(endMonth,original.endMonth,doc,"end_month");

      AuditUtil.getChangeXML(effBeginDt,original.effBeginDt,doc,"eff_begin_dt");

      AuditUtil.getChangeXML(effEndDt,original.effEndDt,doc,"eff_end_dt");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "pws";
  }
  
}   
