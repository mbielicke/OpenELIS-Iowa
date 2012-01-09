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
 * Qaevent Entity POJO for database
 */

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

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "qaevent")
@EntityListeners( {AuditUtil.class})
@NamedQueries({
    @NamedQuery( name = "QaEvent.FetchById",
                query = "select new org.openelis.domain.QaEventViewDO(q.id,q.name,q.description,q.testId," +
                		"q.typeId,q.isBillable,q.reportingSequence,q.reportingText,t.name,m.name)"
                      + " from QaEvent q left join q.test t left join q.test.method m where q.id = :id"),
    @NamedQuery( name = "QaEvent.FetchByName",
                query = "select new org.openelis.domain.QaEventVO(q.id,q.name,q.description,q.testId," +
                        "q.typeId,q.isBillable,q.reportingSequence,t.name,m.name)"
                      + " from QaEvent q left join q.test t left join q.test.method m where q.name = :name"),
    @NamedQuery( name = "QaEvent.FetchByTestId",
                query = "select new org.openelis.domain.QaEventVO(q.id,q.name,q.description,q.testId," +
                		"q.typeId,q.isBillable,q.reportingSequence,t.name,m.name)"
                      + " from QaEvent q left join q.test t left join t.method m where q.testId = :testId" +
                      		" or (q.testId is null and q.name not in " +
                      		    "(select q2.name from QaEvent q2 where q2.testId=q.testId)) order by q.name"),
    @NamedQuery( name = "QaEvent.FetchByCommon",
                query = "select new org.openelis.domain.QaEventVO(q.id,q.name,q.description,q.testId," +
                		"q.typeId,q.isBillable,q.reportingSequence,'','')"
                      + " from QaEvent q where q.testId is null order by q.name"),
    @NamedQuery( name = "QaEvent.FetchByAnalysisId",
                 query = "select new org.openelis.domain.QaEventDO(q.id,q.name,q.description,q.testId," +
                         "q.typeId,q.isBillable,q.reportingSequence,q.reportingText)"
                       + " from AnalysisQaevent aq left join aq.qaEvent q"
                       + " where aq.analysisId = :id order by aq.id"),                  
    @NamedQuery( name = "QaEvent.FetchNotInternalByAnalysisId",                
                 query = "select new org.openelis.domain.QaEventDO(q.id,q.name,q.description,q.testId," +
                         "q.typeId,q.isBillable,q.reportingSequence,q.reportingText)"
                       + " from AnalysisQaevent aq left join aq.qaEvent q left join aq.dictionary d"
                       + " where aq.analysisId = :id and d.id not in"
                       + " (select d1.id from Dictionary d1 where d1.systemName = 'qaevent_internal') order by aq.id"),
    @NamedQuery( name = "QaEvent.FetchBySampleId",
                 query = "select new org.openelis.domain.QaEventDO(q.id,q.name,q.description,q.testId," +
                         "q.typeId,q.isBillable,q.reportingSequence,q.reportingText)"
                       + " from SampleQaevent sq left join sq.qaEvent q"
                       + " where sq.sampleId = :id order by sq.id"),                  
    @NamedQuery( name = "QaEvent.FetchNotInternalBySampleId",                
                 query = "select new org.openelis.domain.QaEventDO(q.id,q.name,q.description,q.testId," +
                         "q.typeId,q.isBillable,q.reportingSequence,q.reportingText)"
                       + " from SampleQaevent sq left join sq.qaEvent q left join sq.dictionary d"
                       + " where sq.sampleId = :id and d.id not in"
                       + " (select d1.id from Dictionary d1 where d1.systemName = 'qaevent_internal') order by sq.id")})

public class QaEvent implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String  name;

    @Column(name = "description")
    private String  description;

    @Column(name = "test_id")
    private Integer testId;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "is_billable")
    private String  isBillable;

    @Column(name = "reporting_sequence")
    private Integer reportingSequence;

    @Column(name = "reporting_text")
    private String  reportingText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    private Test    test;

    @Transient
    private QaEvent original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name != null))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        if (DataBaseUtil.isDifferent(isBillable, this.isBillable))
            this.isBillable = isBillable;
    }

    public Integer getReportingSequence() {
        return reportingSequence;
    }

    public void setReportingSequence(Integer reportingSequence) {
        if (DataBaseUtil.isDifferent(reportingSequence, this.reportingSequence))
            this.reportingSequence = reportingSequence;
    }

    public String getReportingText() {
        return reportingText;
    }

    public void setReportingText(String reportingText) {
        if (DataBaseUtil.isDifferent(reportingText, this.reportingText))
            this.reportingText = reportingText;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public void setClone() {
        try {
            original = (QaEvent)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.QAEVENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("test_id", testId, original.testId, ReferenceTable.TEST)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("is_billable", isBillable, original.isBillable)
                 .setField("reporting_sequence", reportingSequence, original.reportingSequence)
                 .setField("reporting_text", reportingText, original.reportingText);

        return audit;
    }
}
