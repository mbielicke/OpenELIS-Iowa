
package org.openelis.entity;

/**
  * Analyte Entity POJO for database 
  */

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

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({@NamedQuery(name = "getAnalyte", query = "select new org.openelis.domain.AnalyteDO(a.id,a.name,a.isActive,a.analyteGroup,a.parentAnalyteId,p.name,a.externalId) from " + 
		                                                " Analyte a left join a.parentAnalyte p where a.id = :id"),
	           @NamedQuery(name = "getAnalyteAutoCompleteByName", query = "select a.id, a.name " +
			   											 " from Analyte a where a.name like :name order by a.name"),
			   @NamedQuery(name = "getAnalyteAutoCompleteById", query = "select a.id, a.name " +
			   											 " from Analyte a where a.id = :id order by a.name")})
			   											 
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

  @Column(name="analyte_group")
  private Integer analyteGroup;             

  @Column(name="parent_analyte")
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

  public Integer getAnalyteGroup() {
    return analyteGroup;
  }
  public void setAnalyteGroup(Integer analyteGroup) {
    if((analyteGroup == null && this.analyteGroup != null) || 
       (analyteGroup != null && !analyteGroup.equals(this.analyteGroup)))
      this.analyteGroup = analyteGroup;
  }

  public Integer getParentAnalyteId() {
    return parentAnalyteId;
  }
  public void setParentAnalyteId(Integer parentAnalyte) {
    if((parentAnalyte == null && this.parentAnalyteId != null) || 
       (parentAnalyte != null && !parentAnalyte.equals(this.parentAnalyteId)))
      this.parentAnalyteId = parentAnalyte;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString().trim()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString().trim()));
        root.appendChild(elem);
      }      

      if((analyteGroup == null && original.analyteGroup != null) || 
         (analyteGroup != null && !analyteGroup.equals(original.analyteGroup))){
        Element elem = doc.createElement("analyte_group");
        elem.appendChild(doc.createTextNode(original.analyteGroup.toString().trim()));
        root.appendChild(elem);
      }      

      if((parentAnalyteId == null && original.parentAnalyteId != null) || 
         (parentAnalyteId != null && !parentAnalyteId.equals(original.parentAnalyteId))){
        Element elem = doc.createElement("parent_analyte");
        elem.appendChild(doc.createTextNode(original.parentAnalyteId.toString().trim()));
        root.appendChild(elem);
      }      

      if((externalId == null && original.externalId != null) || 
         (externalId != null && !externalId.equals(original.externalId))){
        Element elem = doc.createElement("external_id");
        elem.appendChild(doc.createTextNode(original.externalId.toString().trim()));
        root.appendChild(elem);
      }      

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
