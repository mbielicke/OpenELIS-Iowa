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
 * SamplePT Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SamplePT.FetchBySampleIds",
                query = "select distinct new org.openelis.domain.SamplePTDO(s.id, s.sampleId, s.ptProviderId," +
                		"s.series, s.dueDate, s.additionalDomain)"
                      + " from SamplePT s where s.sampleId in (:ids)")})
@Entity
@Table(name = "sample_pt")
@EntityListeners({AuditUtil.class})
public class SamplePT implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer        id;

    @Column(name = "sample_id")
    private Integer        sampleId;

    @Column(name = "pt_provider_id")
    private Integer        ptProviderId;

    @Column(name = "series")
    private String         series;
    
    @Column(name = "due_date")
    private Date           dueDate;
    
    @Column(name = "additional_domain")
    private String         additionalDomain;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample         sample;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_provider_id", insertable = false, updatable = false)
    private Dictionary      ptProvider;

    @Transient
    private SamplePT original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
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

    public Integer getPTProviderId() {
        return ptProviderId;
    }

    public void setPTProviderId(Integer ptProviderId) {
        if (DataBaseUtil.isDifferent(ptProviderId, this.ptProviderId))
            this.ptProviderId = ptProviderId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        if (DataBaseUtil.isDifferent(series, this.series))
            this.series = series;
    }
    
    public Datetime getDueDate() {
        return DataBaseUtil.toYM(dueDate);
    }

    public void setDueDate(Datetime dueDate) {
        if (DataBaseUtil.isDifferentYM(dueDate, this.dueDate))
            this.dueDate = DataBaseUtil.toDate(dueDate);
    }
    
    public String getAdditionalDomain() {
        return series;
    }

    public void setAdditionalDomain(String additionalDomain) {
        if (DataBaseUtil.isDifferent(series, this.additionalDomain))
            this.additionalDomain = additionalDomain;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Dictionary getPTProvider() {
        return ptProvider;
    }

    public void setPTProvider(Dictionary ptProvider) {
        this.ptProvider = ptProvider;
    }

    public void setClone() {
        try {
            original = (SamplePT)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().SAMPLE_PT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId, Constants.table().SAMPLE)
                 .setField("pt_provider_id", ptProviderId, original.ptProviderId, Constants.table().DICTIONARY)
                 .setField("series", series, original.series)
                 .setField("due_date", dueDate, original.dueDate)
                 .setField("additional_domain", additionalDomain, original.additionalDomain);

        return audit;
    }
}