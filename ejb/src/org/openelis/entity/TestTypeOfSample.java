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
 * TestTypeOfSample Entity POJO for database
 */

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
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "TestTypeOfSample.FetchByTestId",
                query = "select distinct new org.openelis.domain.TestTypeOfSampleDO(ts.id, ts.testId, ts.typeOfSampleId,ts.unitOfMeasureId) "
                      + " from TestTypeOfSample ts where ts.testId = :id")})
@Entity
@Table(name = "test_type_of_sample")
@EntityListeners({AuditUtil.class})
public class TestTypeOfSample implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "test_id")
    private Integer          testId;

    @Column(name = "type_of_sample_id")
    private Integer          typeOfSampleId;

    @Column(name = "unit_of_measure_id")
    private Integer          unitOfMeasureId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_sample_id", insertable = false, updatable = false)
    private Dictionary       dictionary;

    @Transient
    private TestTypeOfSample original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        if (DataBaseUtil.isDifferent(testId, this.testId))
            this.testId = testId;
    }

    public Integer getTypeOfSampleId() {
        return typeOfSampleId;
    }

    public void setTypeOfSampleId(Integer typeOfSampleId) {
        if (DataBaseUtil.isDifferent(typeOfSampleId, this.typeOfSampleId))
            this.typeOfSampleId = typeOfSampleId;
    }

    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }

    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        if (DataBaseUtil.isDifferent(unitOfMeasureId, this.unitOfMeasureId))
            this.unitOfMeasureId = unitOfMeasureId;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setClone() {
        try {
            original = (TestTypeOfSample)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.TEST_TYPE_OF_SAMPLE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("test_id", testId, original.testId)
                 .setField("type_of_sample_id", typeOfSampleId, original.typeOfSampleId, ReferenceTable.DICTIONARY)
                 .setField("unit_of_measure_id", unitOfMeasureId, original.unitOfMeasureId, ReferenceTable.DICTIONARY);

        return audit;
    }
}
