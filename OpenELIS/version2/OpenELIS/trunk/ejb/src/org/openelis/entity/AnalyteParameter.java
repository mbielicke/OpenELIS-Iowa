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

/**
 * AnalyteParameter Entity POJO for database 
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "AnalyteParameter.FetchActiveByAnaIdRefIdRefTableId",
                query = "select new org.openelis.domain.AnalyteParameterViewDO(ap.id, ap.referenceId," +
                        "ap.referenceTableId, ap.analyteId, ap.typeOfSampleId, ap.isActive, ap.activeBegin," +
                        "ap.activeEnd, ap.p1, ap.p2, ap.p3, ap.analyte.name)"
                      + " from AnalyteParameter ap where ap.analyteId = :analyteId and ap.referenceId = :referenceId and"
                      +	" ap.referenceTableId = :referenceTableId and ap.isActive = 'Y'"),
    @NamedQuery( name = "AnalyteParameter.FetchActiveByRefIdRefTableId",
                query = "select new org.openelis.domain.AnalyteParameterViewDO(ap.id, ap.referenceId," +
                        "ap.referenceTableId, ap.analyteId, ap.typeOfSampleId, ap.isActive, ap.activeBegin," +
                        "ap.activeEnd, ap.p1, ap.p2, ap.p3, ap.analyte.name)"
                      + " from AnalyteParameter ap where ap.referenceId = :referenceId and ap.referenceTableId = :referenceTableId"
                      + " and ap.isActive = 'Y' order by ap.analyte.name, ap.activeBegin desc"),
    @NamedQuery( name = "AnalyteParameter.FetchByAnaIdRefIdRefTableId",
                query = "select new org.openelis.domain.AnalyteParameterViewDO(ap.id, ap.referenceId," +
                        "ap.referenceTableId, ap.analyteId, ap.typeOfSampleId, ap.isActive, ap.activeBegin," +
                        "ap.activeEnd, ap.p1, ap.p2, ap.p3, ap.analyte.name)"
                       + " from AnalyteParameter ap where ap.analyteId = :analyteId and ap.referenceId = :referenceId and"
                       + " ap.referenceTableId = :referenceTableId order by ap.analyte.name, ap.activeBegin desc"),                  
    @NamedQuery( name = "AnalyteParameter.FetchById",
                query = "select new org.openelis.domain.AnalyteParameterViewDO(ap.id, ap.referenceId," +
                        "ap.referenceTableId, ap.analyteId, ap.typeOfSampleId, ap.isActive, ap.activeBegin," +
                        "ap.activeEnd, ap.p1, ap.p2, ap.p3, ap.analyte.name)"
                      + " from AnalyteParameter ap where ap.id = :id"),
    @NamedQuery( name = "AnalyteParameter.FetchForQcChartReport",
                query = "select new org.openelis.domain.AnalyteParameterViewDO(ap.id, ap.referenceId," +
                        "ap.referenceTableId, ap.analyteId, ap.typeOfSampleId, ap.isActive, ap.activeBegin," +
                        "ap.activeEnd, ap.p1, ap.p2, ap.p3, ap.analyte.name) " +
                        "from AnalyteParameter ap " +
                        "where ap.referenceId = :referenceId and ap.referenceTableId = :referenceTableId and ap.analyteId = :analyteId and" +
                        " ap.activeBegin < :worksheetCreatedDate and ap.activeEnd > :worksheetCreatedDate")})

@Entity
@Table(name = "analyte_parameter")
@EntityListeners({AuditUtil.class})
public class AnalyteParameter implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "reference_id")
    private Integer          referenceId;

    @Column(name = "reference_table_id")
    private Integer          referenceTableId;

    @Column(name = "analyte_id")
    private Integer          analyteId;

    @Column(name = "type_of_sample_id")
    private Integer          typeOfSampleId;

    @Column(name = "is_active")
    private String           isActive;

    @Column(name = "active_begin")
    private Date             activeBegin;

    @Column(name = "active_end")
    private Date             activeEnd;

    @Column(name = "p1")
    private Double           p1;

    @Column(name = "p2")
    private Double           p2;

    @Column(name = "p3")
    private Double           p3;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Test             test;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Qc               qc;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id", insertable = false, updatable = false)
    private Provider         provider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte          analyte;
    
    @Transient
    private AnalyteParameter original;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId, this.referenceId))
            this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        if (DataBaseUtil.isDifferent(typeOfSampleId, this.typeOfSampleId))
            this.typeOfSampleId = typeOfSampleId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public Date getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Datetime activeBegin) {
        if (DataBaseUtil.isDifferentYM(activeBegin, this.activeBegin))
            this.activeBegin = activeBegin.getDate();
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYM(activeEnd);
    }

    public void setActiveEnd(Datetime activeEnd) {
        if (DataBaseUtil.isDifferentYM(activeEnd, this.activeEnd))
            this.activeEnd = activeEnd.getDate();
    }

    public Double getP1() {
        return p1;
    }

    public void setP1(Double p1) {
        if (DataBaseUtil.isDifferent(p1, this.p1))
            this.p1 = p1;
    }

    public Double getP2() {
        return p2;
    }

    public void setP2(Double p2) {
        if (DataBaseUtil.isDifferent(p2, this.p2))
            this.p2 = p2;
    }

    public Double getP3() {
        return p3;
    }

    public void setP3(Double p3) {
        if (DataBaseUtil.isDifferent(p3, this.p3))
            this.p3 = p3;
    }
    
    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Qc getQc() {
        return qc;
    }

    public void setQc(Qc qc) {
        this.qc = qc;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public void setClone() {
        try {
            original = (AnalyteParameter)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ANALYTE_PARAMETER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId)
                 .setField("analyte_id", analyteId, original.analyteId)
                 .setField("type_of_sample_id", typeOfSampleId, original.typeOfSampleId)
                 .setField("is_active", isActive, original.isActive)
                 .setField("active_begin", activeBegin, original.activeBegin)
                 .setField("active_end", activeEnd, original.activeEnd)
                 .setField("p1", p1, original.p1)
                 .setField("p2", p2, original.p2)
                 .setField("p3", p3, original.p3);
            
        return audit;        
    }
}
