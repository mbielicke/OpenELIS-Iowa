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
 * PatientRelation Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "patient_relation")
@EntityListeners( {AuditUtil.class})
public class PatientRelation implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer         id;

    @Column(name = "relation_id")
    private Integer         relationId;

    @Column(name = "patient_id")
    private Integer         patientId;

    @Column(name = "related_patient_id")
    private Integer         relatedPatientId;

    @Transient
    private PatientRelation original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        if (DataBaseUtil.isDifferent(relationId, this.relationId))
            this.relationId = relationId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        if (DataBaseUtil.isDifferent(patientId, this.patientId))
            this.patientId = patientId;
    }

    public Integer getRelatedPatientId() {
        return relatedPatientId;
    }

    public void setRelatedPatientId(Integer relatedPatientId) {
        if (DataBaseUtil.isDifferent(relatedPatientId, this.relatedPatientId))
            this.relatedPatientId = relatedPatientId;
    }

    public void setClone() {
        try {
            original = (PatientRelation)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PATIENT_RELATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("relation_id", relationId, original.relationId, Constants.table().DICTIONARY)
                 .setField("patient_id", patientId, original.patientId, Constants.table().PATIENT)
                 .setField("related_patient_id", relatedPatientId, original.relatedPatientId, Constants.table().PATIENT);

        return audit;
    }
}