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
  * Label Entity POJO for database 
  */

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Label.Label", query = "select new org.openelis.domain.LabelViewDO(l.id,l.name,l.description,l.printerTypeId,s.id, s.name)" +                                                                                                  
                                                  "  from Label l left join l.scriptlet s where l.id = :id"),
               @NamedQuery(name = "Label.LabelIdName", query = "select distinct new org.openelis.domain.IdNameDO(l.id, l.name) " + "  from Label l order by l.name"),
               @NamedQuery(name = "Label.FetchByName", query = "select distinct new org.openelis.domain.IdNameVO(l.id, l.name) " + "  from Label l where l.name like :name order by l.name"),
               @NamedQuery(name = "Label.FetchTestForDeleteCheck", query = "select distinct new org.openelis.domain.TestViewDO(t.id, t.name,t.description,t.reportingDescription," +
                           "t.methodId,t.isActive,t.activeBegin,t.activeEnd,t.isReportable,"+
                           "t.timeTransit,t.timeHolding,t.timeTaAverage,t.timeTaWarning,t.timeTaMax,t.labelId,"+
                           "t.labelQty,t.testTrailerId,t.scriptletId,t.testFormatId,t.revisionMethodId,"+
                           "t.reportingMethodId,t.sortingMethodId,t.reportingSequence,m.name,l.name,tt.name,s.name) "
                         + " from Test t left join t.scriptlet s left join t.testTrailer tt left join t.label l left join t.method m where t.testTrailerId = :id")})


@Entity
@Table(name="label")
@EntityListeners({AuditUtil.class})
public class Label implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="printer_type_id")
  private Integer printerTypeId;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scriptlet_id",insertable = false, updatable = false)
  private Scriptlet scriptlet;

  @Transient
  private Label original;

  
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

  public Integer getPrinterTypeId() {
    return printerTypeId;
  }
  public void setPrinterTypeId(Integer printerTypeId) {
    if((printerTypeId == null && this.printerTypeId != null) || 
       (printerTypeId != null && !printerTypeId.equals(this.printerTypeId)))
      this.printerTypeId = printerTypeId;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
  }

  
  public void setClone() {
    try {
      original = (Label)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(printerTypeId,original.printerTypeId,doc,"printer_type_id");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "label";
  }
public Scriptlet getScriptlet() {
    return scriptlet;
}
public void setScriptlet(Scriptlet scriptlet) {
    this.scriptlet = scriptlet;
}
  
}   
