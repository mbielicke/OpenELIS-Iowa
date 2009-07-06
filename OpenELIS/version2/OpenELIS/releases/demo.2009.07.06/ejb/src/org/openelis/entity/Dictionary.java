/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

/**
  * Dictionary Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Dictionary;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Dictionary.Dictionary", query = "select distinct new org.openelis.domain.DictionaryDO(d.id, d.categoryId, d.relatedEntryId, dre.entry, " +
                           "d.systemName,d.isActive,  d.localAbbrev, d.entry)" +                                                                                                  
                           "  from  Dictionary d left join d.relatedEntry dre  where d.categoryId = :id " +
                           " order by d.entry "),
@NamedQuery(name = "Dictionary.DictionaryByEntrySystemName", query = "select distinct new org.openelis.domain.DictionaryDO(d.id, d.categoryId, d.relatedEntryId, dre.entry, " +
                           " d.systemName,d.isActive,  d.localAbbrev, d.entry)" +                                                                                                  
                           " from  Dictionary d left join d.relatedEntry dre where d.systemName = :name " +
                           " order by d.entry "),
@NamedQuery(name = "Dictionary.DictionaryByEntryId", query = "select distinct new org.openelis.domain.DictionaryDO(d.id, d.categoryId, d.relatedEntryId, dre.entry, " +
                           " d.systemName,d.isActive,  d.localAbbrev, d.entry)" +                                                                                                  
                           " from  Dictionary d left join d.relatedEntry dre where d.id = :id " +
                           " order by d.entry "),
@NamedQuery(name = "Dictionary.EntriesByCategoryName", query = "select distinct new org.openelis.domain.DictionaryDO(d.id, d.categoryId, d.relatedEntryId, dre.entry, " +
                           " d.systemName,d.isActive,  d.localAbbrev, d.entry)" +                                                                                                  
                           " from  Dictionary d left join d.relatedEntry dre left join d.category c where c.systemName = :name " +
                           " order by d.entry "),
@NamedQuery(name = "Dictionary.DropdownValues", query = "select new org.openelis.domain.IdNameDO(d.id, d.entry) from Dictionary d where " +
                                " d.isActive='Y' and d.categoryId = :id order by d.entry"),
@NamedQuery(name = "Dictionary.IdEntrySystemName", query = "select new org.openelis.domain.DictionaryIdEntrySysNameDO(d.id, d.entry,d.systemName) from Dictionary d where " +
                                " d.isActive='Y' and d.categoryId = :id order by d.entry"),                                
@NamedQuery(name = "Dictionary.DropdownAbbreviations", query = "select new org.openelis.domain.IdNameDO(d.id, d.localAbbrev) from Dictionary d where " +
                                " d.isActive='Y' and d.categoryId = :id order by d.localAbbrev"),                                
@NamedQuery(name = "Dictionary.autoCompleteByEntry", query = "select new org.openelis.domain.IdNameDO(d.id, d.entry) from Dictionary d where d.entry like :entry order by d.entry"),
@NamedQuery(name = "Dictionary.IdBySystemName", query = "select d.id from Dictionary d where d.systemName = :systemName"),
@NamedQuery(name = "Dictionary.IdByEntry", query = "select d.id from Dictionary d where d.entry = :entry"),
@NamedQuery(name = "Dictionary.CategoryIdBySystemName", query = "select d.categoryId from Dictionary d where d.systemName = :systemName"),
@NamedQuery(name = "Dictionary.CategoryIdByEntry", query = "select d.categoryId from Dictionary d where d.entry = :entry"),
@NamedQuery(name = "Dictionary.SystemNameById", query = "select d.systemName from Dictionary d where d.id = :id"),
@NamedQuery(name = "Dictionary.EntryById", query = "select d.entry from Dictionary d where d.id = :id"),
@NamedQuery(name = "Dictionary.DictionaryListByPatternAndCategory", query = "select new org.openelis.domain.IdNameDO(d.id, d.entry)" +
        " from Dictionary d where d.categoryId = :categoryId and d.entry like :pattern order by d.entry"),
@NamedQuery(name = "Dictionary.SystemNamesByCatSysName", query = "select  new org.openelis.domain.DictionaryIdEntrySysNameDO(d.id, d.entry, d.systemName) from Dictionary d, Category c " +
        " where d.categoryId = c.id and c.systemName = :systemName ")        })

@Entity
@Table(name="dictionary")
@EntityListeners({AuditUtil.class})
public class Dictionary implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="category_id")
  private Integer categoryId;             

  @Column(name="related_entry_id")
  private Integer relatedEntryId;             

  @Column(name="system_name")
  private String systemName;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="local_abbrev")
  private String localAbbrev;             

  @Column(name="entry")
  private String entry;   
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "related_entry_id",insertable = false, updatable = false)
  private Dictionary relatedEntry;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id",insertable = false, updatable = false)
  private Category category;
  


  @Transient
  private Dictionary original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getCategoryId() {
    return categoryId;
  }
  public void setCategoryId(Integer categoryId) {
    if((categoryId == null && this.categoryId != null) || 
       (categoryId != null && !categoryId.equals(this.categoryId)))
      this.categoryId = categoryId;
  }

  public Integer getRelatedEntryId() {
    return relatedEntryId;
  }
  public void setRelatedEntryId(Integer relatedEntryId) {
    if((relatedEntryId == null && this.relatedEntryId != null) || 
       (relatedEntryId != null && !relatedEntryId.equals(this.relatedEntryId)))
      this.relatedEntryId = relatedEntryId;
  }

  public String getSystemName() {
    return systemName;
  }
  public void setSystemName(String systemName) {
    if((systemName == null && this.systemName != null) || 
       (systemName != null && !systemName.equals(this.systemName)))
      this.systemName = systemName;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getLocalAbbrev() {
    return localAbbrev;
  }
  public void setLocalAbbrev(String localAbbrev) {
    if((localAbbrev == null && this.localAbbrev != null) || 
       (localAbbrev != null && !localAbbrev.equals(this.localAbbrev)))
      this.localAbbrev = localAbbrev;
  }

  public String getEntry() {
    return entry;
  }
  public void setEntry(String entry) {
    if((entry == null && this.entry != null) || 
       (entry != null && !entry.equals(this.entry)))
      this.entry = entry;
  }

  
  public void setClone() {
    try {
      original = (Dictionary)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(categoryId,original.categoryId,doc,"category_id");

      AuditUtil.getChangeXML(relatedEntryId,original.relatedEntryId,doc,"related_entry_id");

      AuditUtil.getChangeXML(systemName,original.systemName,doc,"system_name");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(localAbbrev,original.localAbbrev,doc,"local_abbrev");

      AuditUtil.getChangeXML(entry,original.entry,doc,"entry");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "dictionary";
  }
  
    
    public Dictionary getRelatedEntry() {
      return relatedEntry;
    }
    public void setRelatedEntry(Dictionary relatedEntryRow) {
      this.relatedEntry = relatedEntryRow;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
  
}   
