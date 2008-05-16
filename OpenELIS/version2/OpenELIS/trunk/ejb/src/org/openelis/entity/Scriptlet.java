
package org.openelis.entity;

/**
  * Scriptlet Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "getScriptlets", query = "select distinct new org.openelis.domain.IdNameDO(script.id, script.name) from Scriptlet script  " +
        "order by script.name ")
@Entity
@Table(name="scriptlet")
@EntityListeners({AuditUtil.class})
public class Scriptlet implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="code_source")
  private String codeSource;             


  @Transient
  private Scriptlet original;

  
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

  public String getCodeSource() {
    return codeSource;
  }
  public void setCodeSource(String codeSource) {
    if((codeSource == null && this.codeSource != null) || 
       (codeSource != null && !codeSource.equals(this.codeSource)))
      this.codeSource = codeSource;
  }

  
  public void setClone() {
    try {
      original = (Scriptlet)this.clone();
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

      if((codeSource == null && original.codeSource != null) || 
         (codeSource != null && !codeSource.equals(original.codeSource))){
        Element elem = doc.createElement("code_source");
        elem.appendChild(doc.createTextNode(original.codeSource.toString().trim()));
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
    return "scriptlet";
  }
  
}   
