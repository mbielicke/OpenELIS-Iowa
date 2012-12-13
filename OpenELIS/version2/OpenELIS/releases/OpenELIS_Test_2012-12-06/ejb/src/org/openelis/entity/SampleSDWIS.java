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

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/**
 * Sample SDWIS Entity POJO for database
 */

@NamedQueries( {
    @NamedQuery(name = "SampleSDWIS.FetchBySampleId", query = "select new org.openelis.domain.SampleSDWISViewDO(s.id, s.sampleId, s.pwsId, s.stateLabId," +
                       "s.facilityId, s.sampleTypeId, s.sampleCategoryId, s.samplePointId, s.location, s.collector, p.name, p.number0) " +
                       " from SampleSDWIS s left join s.pws p where s.sampleId = :id")})
                       
@Entity
@Table(name = "sample_sdwis")
@EntityListeners({AuditUtil.class})
public class SampleSDWIS implements Auditable, Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer     id;

    @Column(name = "sample_id")
    private Integer     sampleId;

    @Column(name = "pws_id")
    private Integer     pwsId;

    @Column(name = "state_lab_id")
    private Integer     stateLabId;

    @Column(name = "facility_id")
    private String      facilityId;

    @Column(name = "sample_type_id")
    private Integer     sampleTypeId;

    @Column(name = "sample_category_id")
    private Integer     sampleCategoryId;

    @Column(name = "sample_point_id")
    private String      samplePointId;

    @Column(name = "location")
    private String      location;

    @Column(name = "collector")
    private String      collector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pws_id", insertable = false, updatable = false)
    private PWS         pws;

    @Transient
    private SampleSDWIS original;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }

    public Integer getPwsId() {
        return pwsId;
    }

    public void setPwsId(Integer pwsId) {
        if (DataBaseUtil.isDifferent(pwsId, this.pwsId))
            this.pwsId = pwsId;
    }

    public Integer getStateLabId() {
        return stateLabId;
    }

    public void setStateLabId(Integer stateLabId) {
        if (DataBaseUtil.isDifferent(stateLabId, this.stateLabId))
            this.stateLabId = stateLabId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        if (DataBaseUtil.isDifferent(facilityId, this.facilityId))
            this.facilityId = facilityId;
    }

    public Integer getSampleTypeId() {
        return sampleTypeId;
    }

    public void setSampleTypeId(Integer sampleTypeId) {
        if (DataBaseUtil.isDifferent(sampleTypeId, this.sampleTypeId))
            this.sampleTypeId = sampleTypeId;
    }

    public Integer getSampleCategoryId() {
        return sampleCategoryId;
    }

    public void setSampleCategoryId(Integer sampleCategoryId) {
        if (DataBaseUtil.isDifferent(sampleCategoryId, this.sampleCategoryId))
            this.sampleCategoryId = sampleCategoryId;
    }

    public String getSamplePointId() {
        return samplePointId;
    }

    public void setSamplePointId(String samplePointId) {
        if (DataBaseUtil.isDifferent(samplePointId, this.samplePointId))
            this.samplePointId = samplePointId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location, this.location))
            this.location = location;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        if (DataBaseUtil.isDifferent(collector, this.collector))
            this.collector = collector;
    }

    public PWS getPws() {
        return pws;
    }

    public void setPws(PWS pws) {
        this.pws = pws;
    }

    public void setClone() {
        try {
            original = (SampleSDWIS)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SAMPLE_SDWIS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("pws_id", pwsId, original.pwsId)
                 .setField("state_lab_id", stateLabId, original.stateLabId)
                 .setField("facility_id", facilityId, original.facilityId)
                 .setField("sample_type_id", sampleTypeId, original.sampleTypeId)
                 .setField("sample_category_id", sampleCategoryId, original.sampleCategoryId)
                 .setField("sample_point_id", samplePointId, original.samplePointId)
                 .setField("location", location, original.location)
                 .setField("collector", collector, original.collector);

        return audit;
    }
}
