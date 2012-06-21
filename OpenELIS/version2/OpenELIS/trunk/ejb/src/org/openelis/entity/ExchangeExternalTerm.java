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

/**
 * ExchangeExternalTerm Entity POJO for database 
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery( name = "ExchangeExternalTerm.FetchByExchangeLocalTermId",
            query = "select new org.openelis.domain.ExchangeExternalTermDO(et.id, et.exchangeLocalTermId," +
                    "et.profileId, et.isActive, et.externalTerm, et.externalDescription, et.externalCodingSystem)"
                  + " from ExchangeExternalTerm et where et.exchangeLocalTermId = :id")

@Entity
@Table(name = "exchange_external_term")
@EntityListeners({AuditUtil.class})
public class ExchangeExternalTerm implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer              id;

    @Column(name = "exchange_local_term_id")
    private Integer              exchangeLocalTermId;

    @Column(name = "profile_id")
    private Integer              profileId;

    @Column(name = "is_active")
    private String               isActive;

    @Column(name = "external_term")
    private String               externalTerm;

    @Column(name = "external_description")
    private String               externalDescription;

    @Column(name = "external_coding_system")
    private String               externalCodingSystem; 
    
    @Transient
    private ExchangeExternalTerm original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getExchangeLocalTermId() {
        return exchangeLocalTermId;
    }

    public void setExchangeLocalTermId(Integer exchangeLocalTermId) {
        if (DataBaseUtil.isDifferent(exchangeLocalTermId, this.exchangeLocalTermId))
            this.exchangeLocalTermId = exchangeLocalTermId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        if (DataBaseUtil.isDifferent(profileId, this.profileId))
            this.profileId = profileId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive)) 
            this.isActive = isActive;
    }

    public String getExternalTerm() {
        return externalTerm;
    }

    public void setExternalTerm(String externalTerm) {
        if (DataBaseUtil.isDifferent(externalTerm, this.externalTerm)) 
            this.externalTerm = externalTerm;
    }

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        if (DataBaseUtil.isDifferent(externalDescription, this.externalDescription)) 
           this.externalDescription = externalDescription;
    }

    public String getExternalCodingSystem() {
        return externalCodingSystem;
    }

    public void setExternalCodingSystem(String externalCodingSystem) {
        if (DataBaseUtil.isDifferent(externalCodingSystem, this.externalCodingSystem))
        this.externalCodingSystem = externalCodingSystem;
    }

    public void setClone() {
        try {
            original = (ExchangeExternalTerm)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.EXCHANGE_EXTERNAL_TERM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("exchange_local_term_id", exchangeLocalTermId, original.exchangeLocalTermId)
                 .setField("profile_id", profileId, original.profileId, ReferenceTable.DICTIONARY)
                 .setField("is_active", isActive, original.isActive)
                 .setField("external_term", externalTerm, original.externalTerm)
                 .setField("external_description", externalDescription, original.externalDescription)
                 .setField("external_coding_system", externalCodingSystem, original.externalCodingSystem);
            
        return audit;
    }

}
