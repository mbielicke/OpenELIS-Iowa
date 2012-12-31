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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/**
 * Exchange Profile Entity POJO for database
 *
 */

@NamedQuery( name = "ExchangeProfile.FetchByExchangeCriteriaId",
            query = "select new org.openelis.domain.ExchangeProfileDO(ep.id, ep.exchangeCriteriaId, ep.profileId, ep.sortOrder)"
                  + " from ExchangeProfile ep where ep.exchangeCriteriaId = :id")

@Entity
@Table(name = "exchange_profile")
@EntityListeners({AuditUtil.class})
public class ExchangeProfile implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                     id;
    
    @Column(name = "exchange_criteria_id")
    private Integer                     exchangeCriteriaId;
    
    @Column(name = "profile_id")
    private Integer                     profileId;
    
    @Column(name = "sort_order")
    private Integer                     sortOrder;
    
    @Transient
    private ExchangeProfile             original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getExchangeCriteriaId() {
        return exchangeCriteriaId;
    }

    public void setExchangeCriteriaId(Integer exchangeCriteriaId) {
        if (DataBaseUtil.isDifferent(exchangeCriteriaId, this.exchangeCriteriaId))
            this.exchangeCriteriaId = exchangeCriteriaId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        if (DataBaseUtil.isDifferent(profileId, this.profileId))
            this.profileId = profileId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public void setClone() {
        try {
            original = (ExchangeProfile)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().EXCHANGE_PROFILE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)                 
                 .setField("exchange_criteria_id", exchangeCriteriaId, original.exchangeCriteriaId)
                 .setField("profile_id", profileId, original.profileId, Constants.table().DICTIONARY)
                 .setField("sort_order", sortOrder, original.sortOrder);

        return audit;
    }

}
