
package org.openelis.entity;

/**
  * Category Entity POJO for database 
  */

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "getCategory", query = "select new org.openelis.domain.CategoryDO(c.id,c.systemName,c.name,c.description,c.section)" +                                                                                                  
                              "  from Category c where c.id = :id"),
              @NamedQuery(name = "getDictionaryEntries", query = "select distinct new org.openelis.domain.DictionaryDO(d.id, d.category, d.relatedEntryId," +
                             "d.systemName,d.isActive,  d.localAbbrev, d.entry)" +                                                                                                  
                              "  from  Dictionary d  where d.category = :id " +
                              " order by d.systemName "),
              @NamedQuery(name = "getDictionaryDropdownValues", query = "select d.id, d.entry from Dictionary d where d.isActive='Y' and d.category = :id order by d.entry"),
              @NamedQuery(name = "getMatchingEntries", query = "select d.id, d.entry from Dictionary d where d.entry like :entry order by d.entry"),
              @NamedQuery(name = "getEntryIdForSystemName", query = "select d.id from Dictionary d where d.systemName = :systemName"),
              @NamedQuery(name = "getEntryIdForEntry", query = "select d.id from Dictionary d where d.entry = :entry"),
              @NamedQuery(name = "getCategoryIdBySystemName", query = "select c.id from Category c where c.systemName = :systemName"),              
              @NamedQuery(name = "getEntryAutoCompleteById", query = "select d.id, d.entry from Dictionary d " +"  where d.id = :id order by d.entry"),
              @NamedQuery(name = "getCategoryIdForDictSysName", query = "select d.category from Dictionary d where d.systemName = :systemName"),
              @NamedQuery(name = "getCategoryIdForCatSysName", query = "select c.id from Category c where c.systemName = :systemName")})
               
@Entity
@Table(name="category")
@EntityListeners({AuditUtil.class})
public class Category implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="system_name")
  private String systemName;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="section")
  private Integer section;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "category")
  private Collection<Dictionary> dictionary;
  
  @Transient
  private Category original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getSystemName() {
    return systemName;
  }
  public void setSystemName(String systemName) {
    if((systemName == null && this.systemName != null) || 
       (systemName != null && !systemName.equals(this.systemName)))
      this.systemName = systemName;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getSection() {
    return section;
  }
  public void setSection(Integer section) {
    if((section == null && this.section != null) || 
       (section != null && !section.equals(this.section)))
      this.section = section;
  }

  
  public void setClone() {
    try {
      original = (Category)this.clone();
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

        if((systemName == null && original.systemName != null) || 
           (systemName != null && !systemName.equals(original.systemName))){
          Element elem = doc.createElement("system_name");
          elem.appendChild(doc.createTextNode(original.systemName.toString().trim()));
          root.appendChild(elem);
        }      

        if((name == null && original.name != null) || 
           (name != null && !name.equals(original.name))){
          Element elem = doc.createElement("name");
          elem.appendChild(doc.createTextNode(original.name.toString().trim()));
          root.appendChild(elem);
        }      

        if((description == null && original.description != null) || 
           (description != null && !description.equals(original.description))){
          Element elem = doc.createElement("description");
          elem.appendChild(doc.createTextNode(original.description.toString().trim()));
          root.appendChild(elem);
        }      

        if((section == null && original.section != null) || 
           (section != null && !section.equals(original.section))){
          Element elem = doc.createElement("section");
          elem.appendChild(doc.createTextNode(original.section.toString().trim()));
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
    return "category";
  }
public Collection<Dictionary> getDictionary() {
    return dictionary;
}
public void setDictionary(Collection<Dictionary> dictionary) {
    this.dictionary = dictionary;
}
public Category getOriginal() {
    return original;
}
public void setOriginal(Category original) {
    this.original = original;
}
  
}   
