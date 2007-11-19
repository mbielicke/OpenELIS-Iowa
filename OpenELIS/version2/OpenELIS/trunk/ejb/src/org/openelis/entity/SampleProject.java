
package org.openelis.entity;

/**
  * SampleProject Entity POJO for database 
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
@Table(name="sample_project")
@EntityListeners({AuditUtil.class})
public class SampleProject implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample")
  private Integer sample;             

  @Column(name="project")
  private Integer project;             

  @Column(name="is_permanent")
  private String isPermanent;             


  @Transient
  private SampleProject original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSample() {
    return sample;
  }
  public void setSample(Integer sample) {
    if((sample == null && this.sample != null) || 
       (sample != null && !sample.equals(this.sample)))
      this.sample = sample;
  }

  public Integer getProject() {
    return project;
  }
  public void setProject(Integer project) {
    if((project == null && this.project != null) || 
       (project != null && !project.equals(this.project)))
      this.project = project;
  }

  public String getIsPermanent() {
    return isPermanent;
  }
  public void setIsPermanent(String isPermanent) {
    if((isPermanent == null && this.isPermanent != null) || 
       (isPermanent != null && !isPermanent.equals(this.isPermanent)))
      this.isPermanent = isPermanent;
  }

  
  public void setClone() {
    try {
      original = (SampleProject)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((sample == null && original.sample != null) || 
         (sample != null && !sample.equals(original.sample))){
        Element elem = doc.createElement("sample");
        elem.appendChild(doc.createTextNode(original.sample.toString()));
        root.appendChild(elem);
      }      

      if((project == null && original.project != null) || 
         (project != null && !project.equals(original.project))){
        Element elem = doc.createElement("project");
        elem.appendChild(doc.createTextNode(original.project.toString()));
        root.appendChild(elem);
      }      

      if((isPermanent == null && original.isPermanent != null) || 
         (isPermanent != null && !isPermanent.equals(original.isPermanent))){
        Element elem = doc.createElement("is_permanent");
        elem.appendChild(doc.createTextNode(original.isPermanent.toString()));
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
    return "sample_project";
  }
  
}   
