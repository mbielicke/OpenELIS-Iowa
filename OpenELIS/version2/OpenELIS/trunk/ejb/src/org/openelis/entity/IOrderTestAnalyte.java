/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
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
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
   @NamedQuery(name = "IOrderTestAnalyte.FetchByIorderTestId",
              query = "select distinct new org.openelis.domain.IOrderTestAnalyteViewDO(ota.id,ota.iorderTestId,"
                    + "ota.analyteId,a.name,0,0,'Y','Y')"
                    + " from IOrderTestAnalyte ota left join ota.analyte a"
                    + " where ota.iorderTestId = :id"),
   @NamedQuery(name = "IOrderTestAnalyte.FetchByIorderTestIds",
              query = "select distinct new org.openelis.domain.IOrderTestAnalyteViewDO(ota.id,ota.iorderTestId,"
                    + "ota.analyteId,a.name,0,0,'Y','Y')"
                    + " from IOrderTestAnalyte ota left join ota.analyte a"
                    + " where ota.iorderTestId in ( :ids )"),
   @NamedQuery(name = "IOrderTestAnalyte.FetchRowAnalytesByIorderTestId",
              query = "select distinct new org.openelis.domain.IOrderTestAnalyteViewDO(0,0,ta.analyteId,a.name,"
                    + "ta.sortOrder,ta.typeId,ta.isReportable,'Y')"
                    + " from IOrderTest ot left join ot.test t left join t.testAnalyte ta left join ta.analyte a"
                    + " where ot.id = :id and ta.isColumn = 'N' order by ta.sortOrder"),
   @NamedQuery(name = "IOrderTestAnalyte.FetchRowAnalytesByTestId",
              query = "select distinct new org.openelis.domain.IOrderTestAnalyteViewDO(0,0,ta.analyteId,"
                    + "a.name,ta.sortOrder,ta.typeId,ta.isReportable,'Y')"
                    + " from TestAnalyte ta left join ta.analyte a"
                    + " where ta.testId = :testId and ta.isColumn = 'N' order by ta.sortOrder")})
@Entity
@Table(name = "iorder_test_analyte")
@EntityListeners({AuditUtil.class})
public class IOrderTestAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "iorder_test_id")
    private Integer          iorderTestId;

    @Column(name = "analyte_id")
    private Integer          analyteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_test_id", insertable = false, updatable = false)
    private IOrderTest        iorderTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte          analyte;

    @Transient
    private IOrderTestAnalyte original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getIorderTestId() {
        return iorderTestId;
    }

    public void setIorderTestId(Integer iorderTestId) {
        if (DataBaseUtil.isDifferent(iorderTestId, this.iorderTestId))
            this.iorderTestId = iorderTestId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }

    public IOrderTest getIorderTest() {
        return iorderTest;
    }

    public void setIorderTest(IOrderTest iorderTest) {
        this.iorderTest = iorderTest;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public void setClone() {
        try {
            original = (IOrderTestAnalyte)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().IORDER_TEST_ANALYTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("iorder_test_id", iorderTestId, original.iorderTestId)
                 .setField("analyte_id", analyteId, original.analyteId, Constants.table().ANALYTE);

        return audit;
    }
}
