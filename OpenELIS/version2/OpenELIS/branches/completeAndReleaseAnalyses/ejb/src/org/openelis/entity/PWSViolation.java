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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.AuditUtil;

@NamedQueries({
               @NamedQuery(name = "PWSViolation.FetchByTinwsysIsNumber",
                           query = "select new org.openelis.domain.PWSViolationDO(p.id, p.tinwsysIsNumber, p.facilityId, p.series, p.violationDate, p.sampleId)"
                                   + " from PWSViolation p where p.tinwsysIsNumber = :tinwsysIsNumber"),
               @NamedQuery(name = "PWSViolation.FetchByFacilityIdAndSeries",
                           query = "select new org.openelis.domain.PWSViolationDO(p.id, p.tinwsysIsNumber, p.facilityId, p.series, p.violationDate, p.sampleId)"
                                   + " from PWSViolation p where p.tinwsysIsNumber = :tinwsysIsNumber and p.facilityId = :facilityId and"
                                   + " p.series = :series and p.violationDate between :startTime and :endTime"),
               @NamedQuery(name = "PWSViolation.FetchById",
                           query = "select new org.openelis.domain.PWSViolationDO(p.id, p.tinwsysIsNumber, p.facilityId, p.series, p.violationDate, p.sampleId)"
                                   + " from PWSViolation p where p.id = :id"),
               @NamedQuery(name = "PWSViolation.FetchAll",
                           query = "select new org.openelis.domain.PWSViolationDO(p.id, p.tinwsysIsNumber, p.facilityId, p.series, p.violationDate, p.sampleId)"
                                   + " from PWSViolation p"),
               @NamedQuery(name = "PWSViolation.DeleteList",
                           query = "delete from PWSViolation p where p.id in ( :deleteList )")})
@Entity
@Table(name = "pws_violation")
@EntityListeners({AuditUtil.class})
public class PWSViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tinwsys_is_number")
    private Integer tinwsysIsNumber;

    @Column(name = "st_asgn_ident_cd")
    private String  facilityId;

    @Column(name = "series")
    private String  series;

    @Column(name = "violation_date")
    private Date    violationDate;

    @Column(name = "sample_id")
    private Integer sampleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        if (DataBaseUtil.isDifferent(tinwsysIsNumber, this.tinwsysIsNumber))
            this.tinwsysIsNumber = tinwsysIsNumber;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        if (DataBaseUtil.isDifferent(facilityId, this.facilityId))
            this.facilityId = facilityId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        if (DataBaseUtil.isDifferent(series, this.series))
            this.series = series;
    }

    public Datetime getViolationDate() {
        return DataBaseUtil.toYD(violationDate);
    }

    public void setViolationDate(Datetime violationDate) {
        if (DataBaseUtil.isDifferentYD(violationDate, this.violationDate))
            this.violationDate = DataBaseUtil.toDate(violationDate);
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }
}
