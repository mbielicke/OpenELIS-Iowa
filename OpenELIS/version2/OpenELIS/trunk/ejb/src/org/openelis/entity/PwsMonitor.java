
package org.openelis.entity;

/**
  * PwsMonitor Entity POJO for database 
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
@Table(name="pws_monitor")
@EntityListeners({AuditUtil.class})
public class PwsMonitor implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="tinwsys_is_number")
  private Integer tinwsysIsNumber;             

  @Column(name="st_asgn_ident_cd")
  private String stAsgnIdentCd;             

  @Column(name="name")
  private String name;             

  @Column(name="tiaanlgp_tiaanlyt_name")
  private String tiaanlgpTiaanlytName;             

  @Column(name="number_samples")
  private Integer numberSamples;             

  @Column(name="comp_begin_date")
  private Date compBeginDate;             

  @Column(name="comp_end_date")
  private Date compEndDate;             

  @Column(name="frequency_name")
  private String frequencyName;             

  @Column(name="period_name")
  private String periodName;             


  @Transient
  private PwsMonitor original;

  
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

  public String getStAsgnIdentCd() {
    return stAsgnIdentCd;
  }
  public void setStAsgnIdentCd(String stAsgnIdentCd) {
    if((stAsgnIdentCd == null && this.stAsgnIdentCd != null) || 
       (stAsgnIdentCd != null && !stAsgnIdentCd.equals(this.stAsgnIdentCd)))
      this.stAsgnIdentCd = stAsgnIdentCd;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getTiaanlgpTiaanlytName() {
    return tiaanlgpTiaanlytName;
  }
  public void setTiaanlgpTiaanlytName(String tiaanlgpTiaanlytName) {
    if((tiaanlgpTiaanlytName == null && this.tiaanlgpTiaanlytName != null) || 
       (tiaanlgpTiaanlytName != null && !tiaanlgpTiaanlytName.equals(this.tiaanlgpTiaanlytName)))
      this.tiaanlgpTiaanlytName = tiaanlgpTiaanlytName;
  }

  public Integer getNumberSamples() {
    return numberSamples;
  }
  public void setNumberSamples(Integer numberSamples) {
    if((numberSamples == null && this.numberSamples != null) || 
       (numberSamples != null && !numberSamples.equals(this.numberSamples)))
      this.numberSamples = numberSamples;
  }

  public Datetime getCompBeginDate() {
    if(compBeginDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,compBeginDate);
  }
  public void setCompBeginDate (Datetime comp_begin_date){
    if((compBeginDate == null && this.compBeginDate != null) || 
       (compBeginDate != null && !compBeginDate.equals(this.compBeginDate)))
      this.compBeginDate = comp_begin_date.getDate();
  }

  public Datetime getCompEndDate() {
    if(compEndDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,compEndDate);
  }
  public void setCompEndDate (Datetime comp_end_date){
    if((compEndDate == null && this.compEndDate != null) || 
       (compEndDate != null && !compEndDate.equals(this.compEndDate)))
      this.compEndDate = comp_end_date.getDate();
  }

  public String getFrequencyName() {
    return frequencyName;
  }
  public void setFrequencyName(String frequencyName) {
    if((frequencyName == null && this.frequencyName != null) || 
       (frequencyName != null && !frequencyName.equals(this.frequencyName)))
      this.frequencyName = frequencyName;
  }

  public String getPeriodName() {
    return periodName;
  }
  public void setPeriodName(String periodName) {
    if((periodName == null && this.periodName != null) || 
       (periodName != null && !periodName.equals(this.periodName)))
      this.periodName = periodName;
  }

  
  public void setClone() {
    try {
      original = (PwsMonitor)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(tinwsysIsNumber,original.tinwsysIsNumber,doc,"tinwsys_is_number");

      AuditUtil.getChangeXML(stAsgnIdentCd,original.stAsgnIdentCd,doc,"st_asgn_ident_cd");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(tiaanlgpTiaanlytName,original.tiaanlgpTiaanlytName,doc,"tiaanlgp_tiaanlyt_name");

      AuditUtil.getChangeXML(numberSamples,original.numberSamples,doc,"number_samples");

      AuditUtil.getChangeXML(compBeginDate,original.compBeginDate,doc,"comp_begin_date");

      AuditUtil.getChangeXML(compEndDate,original.compEndDate,doc,"comp_end_date");

      AuditUtil.getChangeXML(frequencyName,original.frequencyName,doc,"frequency_name");

      AuditUtil.getChangeXML(periodName,original.periodName,doc,"period_name");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "pws_monitor";
  }
  
}   
