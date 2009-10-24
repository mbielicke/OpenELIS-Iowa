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

import org.openelis.utilcommon.DataBaseUtil;
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
    @NamedQuery( name = "QaEvent.FetchByTest",
                query = "select new org.openelis.domain.QaEventVO(q.id,q.name,q.description,q.testId," +
                		"q.typeId,q.isBillable,q.reportingSequence,t.name,m.name)"
                      + " from QaEvent q left join q.test t left join t.method m where q.testId = :testId" +
                      		" or (q.testId is null and q.name not in " +
                      		    "(select q2.name from QaEvent q2 where q2.testId=q.testId)) order by q.name"),
    @NamedQuery( name = "QaEvent.FetchByCommon",
                query = "select new org.openelis.domain.QaEventVO(q.id,q.name,q.description,q.testId," +
                		"q.typeId,q.isBillable,q.reportingSequence,'','')"
                      + " from QaEvent q where q.testId is null order by q.name")})

public class QaEvent implements Auditable, Cloneable {

    @Id
    @GeneratedValue
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
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(description, original.description, doc, "description");
            AuditUtil.getChangeXML(testId, original.testId, doc, "test_id");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(isBillable, original.isBillable, doc, "is_billable");
            AuditUtil.getChangeXML(reportingSequence, original.reportingSequence, doc, "reporting_sequence");
            AuditUtil.getChangeXML(reportingText, original.reportingText, doc, "reporting_text");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "qaevent";
    }
}
