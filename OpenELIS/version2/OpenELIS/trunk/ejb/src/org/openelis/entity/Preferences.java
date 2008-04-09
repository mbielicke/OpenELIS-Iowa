
package org.openelis.entity;

/**
  * Preferences Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name="getPreference", query="select new org.openelis.domain.PreferencesDO(id,systemUser,key,text) from Preferences where systemUser = :systemUser and key = :key")})

@Entity
@Table(name="preferences")
@EntityListeners({AuditUtil.class})
public class Preferences implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="system_user")
  private Integer systemUser;             

  @Column(name="key")
  private String key;             

  @Column(name="text")
  private String text;             


  @Transient
  private Preferences original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSystemUser() {
    return systemUser;
  }
  public void setSystemUser(Integer systemUser) {
    if((systemUser == null && this.systemUser != null) || 
       (systemUser != null && !systemUser.equals(this.systemUser)))
      this.systemUser = systemUser;
  }

  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    if((key == null && this.key != null) || 
       (key != null && !key.equals(this.key)))
      this.key = key;
  }

  public String getText() {
    return text;
  }
  public void setText(String text) {
    if((text == null && this.text != null) || 
       (text != null && !text.equals(this.text)))
      this.text = text;
  }

  
  public void setClone() {
    try {
      original = (Preferences)this.clone();
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

      if((systemUser == null && original.systemUser != null) || 
         (systemUser != null && !systemUser.equals(original.systemUser))){
        Element elem = doc.createElement("system_user");
        elem.appendChild(doc.createTextNode(original.systemUser.toString().trim()));
        root.appendChild(elem);
      }      

      if((key == null && original.key != null) || 
         (key != null && !key.equals(original.key))){
        Element elem = doc.createElement("key");
        elem.appendChild(doc.createTextNode(original.key.toString().trim()));
        root.appendChild(elem);
      }      

      if((text == null && original.text != null) || 
         (text != null && !text.equals(original.text))){
        Element elem = doc.createElement("text");
        elem.appendChild(doc.createTextNode(original.text.toString().trim()));
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
    return "preferences";
  }
  
}   
