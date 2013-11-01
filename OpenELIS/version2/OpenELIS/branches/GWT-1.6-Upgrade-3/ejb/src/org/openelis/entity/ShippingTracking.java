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
  * ShippingTracking Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
     @NamedQuery(name = "ShippingTracking.Tracking", query = "select new org.openelis.domain.ShippingTrackingDO(s.id, s.shippingId, s.trackingNumber) "+
                                " from ShippingTracking s where s.shippingId = :id")})
            
@Entity
@Table(name="shipping_tracking")
@EntityListeners({AuditUtil.class})
public class ShippingTracking implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="shipping_id")
  private Integer shippingId;             

  @Column(name="tracking_number")
  private String trackingNumber;             


  @Transient
  private ShippingTracking original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getShippingId() {
    return shippingId;
  }
  public void setShippingId(Integer shippingId) {
    if((shippingId == null && this.shippingId != null) || 
       (shippingId != null && !shippingId.equals(this.shippingId)))
      this.shippingId = shippingId;
  }

  public String getTrackingNumber() {
    return trackingNumber;
  }
  public void setTrackingNumber(String trackingNumber) {
    if((trackingNumber == null && this.trackingNumber != null) || 
       (trackingNumber != null && !trackingNumber.equals(this.trackingNumber)))
      this.trackingNumber = trackingNumber;
  }

  
  public void setClone() {
    try {
      original = (ShippingTracking)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(shippingId,original.shippingId,doc,"shipping_id");

      AuditUtil.getChangeXML(trackingNumber,original.trackingNumber,doc,"tracking_number");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "shipping_tracking";
  }
  
}   