
package org.openelis.entity;

/**
  * WorksheetAnalysis Entity POJO for database 
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

@NamedQueries({
    @NamedQuery( name = "WorksheetAnalysis.FetchByWorksheetItemId",
                query = "select new org.openelis.domain.WorksheetAnalysisDO(wa.id,"+
                        "wa.worksheetItemId,wa.referenceId,wa.referenceTableId) "+
                        "from WorksheetItem wa where wa.worksheetItemId = :id")})

@Entity
@Table(name="worksheet_analysis")
@EntityListeners({AuditUtil.class})
public class WorksheetAnalysis implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="worksheet_item_id")
  private Integer worksheetItemId;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             


  @Transient
  private WorksheetAnalysis original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getWorksheetItemId() {
    return worksheetItemId;
  }
  public void setWorksheetItemId(Integer worksheetItemId) {
    if((worksheetItemId == null && this.worksheetItemId != null) || 
       (worksheetItemId != null && !worksheetItemId.equals(this.worksheetItemId)))
      this.worksheetItemId = worksheetItemId;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    if((referenceId == null && this.referenceId != null) || 
       (referenceId != null && !referenceId.equals(this.referenceId)))
      this.referenceId = referenceId;
  }

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  public void setReferenceTableId(Integer referenceTableId) {
    if((referenceTableId == null && this.referenceTableId != null) || 
       (referenceTableId != null && !referenceTableId.equals(this.referenceTableId)))
      this.referenceTableId = referenceTableId;
  }

  
  public void setClone() {
    try {
      original = (WorksheetAnalysis)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(worksheetItemId,original.worksheetItemId,doc,"worksheet_item_id");

      AuditUtil.getChangeXML(referenceId,original.referenceId,doc,"reference_id");

      AuditUtil.getChangeXML(referenceTableId,original.referenceTableId,doc,"reference_table_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "worksheet_analysis";
  }
  
}   
