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
 * ProviderLocation Entity POJO for database
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

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery(name = "ProviderLocation.FetchByProviderId",
                           query = "select new org.openelis.domain.ProviderLocationDO(pl.id,pl.location,pl.externalId,pl.providerId,"
                                   + "a.id,a.multipleUnit,a.streetAddress,a.city,a.state,a.zipCode,a.workPhone,a.homePhone,"
                                   + "a.cellPhone,a.faxPhone,a.email,a.country)"
                                   + " from ProviderLocation pl left join pl.address a where pl.providerId = :id order by pl.location"),
               @NamedQuery(name = "ProviderLocation.FetchByProviderIds",
                           query = "select new org.openelis.domain.ProviderLocationDO(pl.id,pl.location,pl.externalId,pl.providerId,"
                                   + "a.id,a.multipleUnit,a.streetAddress,a.city,a.state,a.zipCode,a.workPhone,a.homePhone,"
                                   + "a.cellPhone,a.faxPhone,a.email,a.country)"
                                   + " from ProviderLocation pl left join pl.address a where pl.providerId in (:ids) order by pl.location")})
@Entity
@Table(name = "provider_location")
@EntityListeners({AuditUtil.class})
public class ProviderLocation implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "location")
    private String           location;

    @Column(name = "external_id")
    private String           externalId;

    @Column(name = "provider_id")
    private Integer          providerId;

    @Column(name = "address_id")
    private Integer          addressId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address          address;

    @Transient
    private ProviderLocation original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location, this.location))
            this.location = location;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        if (DataBaseUtil.isDifferent(externalId, this.externalId))
            this.externalId = externalId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        if (DataBaseUtil.isDifferent(providerId, this.providerId))
            this.providerId = providerId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setClone() {
        try {
            original = (ProviderLocation)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PROVIDER_LOCATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("location", location, original.location)
                 .setField("external_id", externalId, original.externalId)
                 .setField("provider_id", providerId, original.providerId)
                 .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS);

        return audit;
    }
}
