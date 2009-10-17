
package org.openelis.entity;

/**
  * Worksheet Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Worksheet.WorksheetById",
                query = "select new org.openelis.domain.WorksheetDO(id, createdDate, systemUserId, statusId, formatId"
                      + " from Worksheet where id = :id")})

@Entity
@Table(name="worksheet")
@EntityListeners({AuditUtil.class})
public class Worksheet implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="created_date")
  private Date createdDate;             

  @Column(name="system_user_id")
  private Integer systemUserId;             

  @Column(name="status_id")
  private Integer statusId;             

  @Column(name="format_id")
  private Integer formatId;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "worksheet_id")
  private Collection<WorksheetItem> worksheetItem;

  @Transient
  private Worksheet original;

  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Datetime getCreatedDate() {
    if(createdDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.MINUTE,createdDate);
  }
  public void setCreatedDate (Datetime created_date){
    if((createdDate == null && this.createdDate != null) || 
       (createdDate != null && !createdDate.equals(this.createdDate)))
      this.createdDate = created_date.getDate();
  }

  public Integer getSystemUserId() {
    return systemUserId;
  }
  public void setSystemUserId(Integer systemUserId) {
    if((systemUserId == null && this.systemUserId != null) || 
       (systemUserId != null && !systemUserId.equals(this.systemUserId)))
      this.systemUserId = systemUserId;
  }

  public Integer getStatusId() {
    return statusId;
  }
  public void setStatusId(Integer statusId) {
    if((statusId == null && this.statusId != null) || 
       (statusId != null && !statusId.equals(this.statusId)))
      this.statusId = statusId;
  }

  public Integer getFormatId() {
    return formatId;
  }
  public void setFormatId(Integer formatId) {
    if((formatId == null && this.formatId != null) || 
       (formatId != null && !formatId.equals(this.formatId)))
      this.formatId = formatId;
  }

  public Collection<WorksheetItem> getWorksheetItem() {
      return worksheetItem;
  }
  public void setWorksheetItem(Collection<WorksheetItem> worksheetItem) {
      this.worksheetItem = worksheetItem;
  }
    
  public void setClone() {
    try {
      original = (Worksheet)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(createdDate,original.createdDate,doc,"created_date");

      AuditUtil.getChangeXML(systemUserId,original.systemUserId,doc,"system_user_id");

      AuditUtil.getChangeXML(statusId,original.statusId,doc,"status_id");

      AuditUtil.getChangeXML(formatId,original.formatId,doc,"format_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "worksheet";
  }
}   
