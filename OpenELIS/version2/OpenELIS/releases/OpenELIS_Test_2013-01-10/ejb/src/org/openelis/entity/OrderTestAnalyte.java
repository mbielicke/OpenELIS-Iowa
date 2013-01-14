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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "OrderTestAnalyte.FetchByOrderTestId",
                query = "select distinct new org.openelis.domain.OrderTestAnalyteViewDO(ota.id,ota.orderTestId,ota.analyteId,a.name,"
                    +	"0,0,'Y','Y')"
                      + " from OrderTestAnalyte ota left join ota.analyte a"
                      + " where ota.orderTestId = :id"),
   @NamedQuery( name = "OrderTestAnalyte.FetchRowAnalytesByOrderTestId",
               query = "select distinct new org.openelis.domain.OrderTestAnalyteViewDO(0,0,ta.analyteId,a.name,ta.sortOrder,ta.typeId,ta.isReportable,'Y')"
                     + " from OrderTest ot left join ot.test t left join t.testAnalyte ta left join ta.analyte a"
                     + " where ot.id = :id and ta.isColumn = 'N' order by ta.sortOrder"),                  
    @NamedQuery( name = "OrderTestAnalyte.FetchRowAnalytesByTestId",
                query = "select distinct new org.openelis.domain.OrderTestAnalyteViewDO(0,0,ta.analyteId,a.name,ta.sortOrder,ta.typeId,ta.isReportable,'Y')"
                      + " from TestAnalyte ta left join ta.analyte a"
                      +	" where ta.testId = :testId and ta.isColumn = 'N' order by ta.sortOrder")})                   
                   
@Entity
@Table(name = "order_test_analyte")
@EntityListeners( {AuditUtil.class})
public class OrderTestAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer   id;
    
    @Column(name = "order_test_id")
    private Integer   orderTestId;
    
    @Column(name = "analyte_id")
    private Integer   analyteId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_test_id", insertable = false, updatable = false)
    private OrderTest   orderTest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte   analyte;
    
    @Transient
    private OrderTestAnalyte original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getOrderTestId() {
        return orderTestId;
    }

    public void setOrderTestId(Integer orderTestId) {
        if (DataBaseUtil.isDifferent(orderTestId, this.orderTestId))
            this.orderTestId = orderTestId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }
    
    public OrderTest getOrderTest() {
        return orderTest;
    }

    public void setOrderTest(OrderTest orderTest) {
        this.orderTest = orderTest;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public void setClone() {
        try {
            original = (OrderTestAnalyte)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ORDER_TEST_ANALYTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("order_test_id", orderTestId, original.orderTestId)
                 .setField("analyte_id", analyteId, original.analyteId, Constants.table().ANALYTE);

        return audit;
    }
}
