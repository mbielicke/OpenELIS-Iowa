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
 * ProviderAnalyte Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery( name = "ProviderAnalyte.FetchByProviderId",
                           query = "select new org.openelis.domain.ProviderAnalyteViewDO(pa.id,pa.providerId,pa.sortOrder,pa.analyteId,a.name)"
                                 + " from ProviderAnalyte pa left join pa.analyte a where pa.providerId = :id order by pa.sortOrder"),
               @NamedQuery( name = "ProviderAnalyte.FetchByProviderIds",
                           query = "select new org.openelis.domain.ProviderAnalyteViewDO(pa.id,pa.providerId,pa.sortOrder,pa.analyteId,a.name)"
                                 + " from ProviderAnalyte pa left join pa.analyte a where pa.providerId in (:ids) order by pa.providerId, pa.sortOrder")})
@Entity
@Table(name = "provider_analyte")
@EntityListeners({AuditUtil.class})
public class ProviderAnalyte implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer         id;

    @Column(name = "provider_id")
    private Integer         providerId;

    @Column(name = "sort_order")
    private Integer         sortOrder;

    @Column(name = "analyte_id")
    private Integer         analyteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyte_id", insertable = false, updatable = false)
    private Analyte         analyte;

    @Transient
    private ProviderAnalyte original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        if (DataBaseUtil.isDifferent(providerId, this.providerId))
            this.providerId = providerId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        if (DataBaseUtil.isDifferent(analyteId, this.analyteId))
            this.analyteId = analyteId;
    }

    public Analyte getAnalyte() {
        return analyte;
    }

    public void setAnalyte(Analyte analyte) {
        this.analyte = analyte;
    }

    public void setClone() {
        try {
            original = (ProviderAnalyte)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PROVIDER_ANALYTE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("provider_id", providerId, original.providerId)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("analyte_id", analyteId, original.analyteId, Constants.table().ANALYTE);

        return audit;
    }
}
