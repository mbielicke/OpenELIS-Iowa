
package org.openelis.entity;

/**
  * Analyte Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Analyte.Analyte", query = "select new org.openelis.domain.AnalyteDO(a.id,a.name,a.isActive,a.analyteGroupId,a.parentAnalyteId,p.name,a.externalId) from " + 
" Analyte a left join a.parentAnalyte p where a.id = :id"),
@NamedQuery(name = "Analyte.AnalyteByParentId", query = "select a.id from Analyte a where a.parentAnalyteId = :id"),
@NamedQuery(name = "Analyte.UpdateNameCompare", query = "select a.id from Analyte a where a.name = :name and a.id != :id"),
@NamedQuery(name = "Analyte.AddNameCompare", query = "select a.id from Analyte a where a.name = :name"),
@NamedQuery(name = "Analyte.AutoCompleteByName", query = "select new org.openelis.domain.IdNameDO(a.id, a.name) " +
     " from Analyte a where a.name like :name order by a.name")})
     
@Entity
@Table(name="analyte")
@EntityListeners({AuditUtil.class})
public class Analyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="analyte_group_id")
  private Integer analyteGroupId;             

  @Column(name="parent_analyte_id")
  private Integer parentAnalyteId;             

  @Column(name="external_id")
  private String externalId;  
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_analyte", insertable = false, updatable = false)
  private Analyte parentAnalyte;


  @Transient
  private Analyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Integer getAnalyteGroupId() {
    return analyteGroupId;
  }
  public void setAnalyteGroupId(Integer analyteGroupId) {
    if((analyteGroupId == null && this.analyteGroupId != null) || 
       (analyteGroupId != null && !analyteGroupId.equals(this.analyteGroupId)))
      this.analyteGroupId = analyteGroupId;
  }

  public Integer getParentAnalyteId() {
    return parentAnalyteId;
  }
  public void setParentAnalyteId(Integer parentAnalyteId) {
    if((parentAnalyteId == null && this.parentAnalyteId != null) || 
       (parentAnalyteId != null && !parentAnalyteId.equals(this.parentAnalyteId)))
      this.parentAnalyteId = parentAnalyteId;
  }

  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    if((externalId == null && this.externalId != null) || 
       (externalId != null && !externalId.equals(this.externalId)))
      this.externalId = externalId;
  }

  
  public void setClone() {
    try {
      original = (Analyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(analyteGroupId,original.analyteGroupId,doc,"analyte_group_id");

      AuditUtil.getChangeXML(parentAnalyteId,original.parentAnalyteId,doc,"parent_analyte_id");

      AuditUtil.getChangeXML(externalId,original.externalId,doc,"external_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "analyte";
  }
  
  public Analyte getParentAnalyte() {
    return parentAnalyte;
  }
  public void setParentAnalyte(Analyte parentAnalyte) {
    this.parentAnalyte = parentAnalyte;
  }
  
}   
