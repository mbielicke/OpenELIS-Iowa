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
  * StandardNote Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
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
@NamedQueries({@NamedQuery(name = "StandardNote.StandardNote", query = "select new org.openelis.domain.StandardNoteDO(s.id,s.name,s.description,s.typeId,s.text) from StandardNote s where s.id = :id"),
    @NamedQuery(name = "StandardNote.StandardNoteByType", query = "select new org.openelis.domain.StandardNoteDO(s.id,s.name,s.description,s.typeId,s.text) from StandardNote s where "+
                      " (s.name like :name OR s.description like :desc) and s.typeId = :type order by s.name"),
    @NamedQuery(name = "StandardNote.TypeByNameDesc", query="SELECT distinct new org.openelis.domain.IdNameDO(d.id, d.entry) " + "FROM StandardNote s LEFT JOIN s.dictionary d WHERE (s.name like :name OR s.description like :desc) ORDER BY d.entry")})
            
    
@Entity
@Table(name="standard_note")
@EntityListeners({AuditUtil.class})
public class StandardNote implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="text")
  private String text;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", insertable = false, updatable = false)
  private Dictionary dictionary;
  
  @Transient
  private StandardNote original;

  
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

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
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
      original = (StandardNote)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(text,original.text,doc,"text");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "standard_note";
  }
  
}   
