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
 * Organization Entity POJO for database
 */

import java.util.Collection;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Organization.FetchById",
                query = "select new org.openelis.domain.OrganizationViewDO(o.id,o.parentOrganizationId," +
                        "o.name,o.isActive,o.address.id,o.address.multipleUnit,o.address.streetAddress," +
                        "o.address.city,o.address.state,o.address.zipCode,o.address.workPhone," +
                        "o.address.homePhone,o.address.cellPhone,o.address.faxPhone,o.address.email," +
                        "o.address.country, p.name)"
                      + " from Organization o left join o.parentOrganization p where o.id = :id"),
    @NamedQuery( name = "Organization.FetchByIds",
                query = "select new org.openelis.domain.OrganizationViewDO(o.id,o.parentOrganizationId," +
                        "o.name,o.isActive,o.address.id,o.address.multipleUnit,o.address.streetAddress," +
                        "o.address.city,o.address.state,o.address.zipCode,o.address.workPhone," +
                        "o.address.homePhone,o.address.cellPhone,o.address.faxPhone,o.address.email," +
                        "o.address.country, p.name)"
                      + " from Organization o left join o.parentOrganization p where o.id in ( :ids )"),
    @NamedQuery( name = "Organization.FetchActiveById",
                query = "select new org.openelis.domain.OrganizationDO(o.id,o.parentOrganizationId," +
                        "o.name,o.isActive,o.address.id,o.address.multipleUnit,o.address.streetAddress," +
                        "o.address.city,o.address.state,o.address.zipCode,o.address.workPhone," +
                        "o.address.homePhone,o.address.cellPhone,o.address.faxPhone,o.address.email," +
                        "o.address.country)"
                      + " from Organization o where o.id = :id and o.isActive = 'Y'"),
    @NamedQuery( name = "Organization.FetchActiveByName",
                query = "select new org.openelis.domain.OrganizationDO(o.id,o.parentOrganizationId," +
                        "o.name,o.isActive,o.address.id,o.address.multipleUnit,o.address.streetAddress," +
                        "o.address.city,o.address.state,o.address.zipCode,o.address.workPhone," +
                        "o.address.homePhone,o.address.cellPhone,o.address.faxPhone,o.address.email," +
                        "o.address.country)"
                      + " from Organization o where o.name like :name and o.isActive = 'Y' order by o.name")})
@Entity
@Table(name = "organization")
@EntityListeners( {AuditUtil.class})
public class Organization implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                         id;

    @Column(name = "parent_organization_id")
    private Integer                         parentOrganizationId;

    @Column(name = "name")
    private String                          name;

    @Column(name = "is_active")
    private String                          isActive;

    @Column(name = "address_id")
    private Integer                         addressId;

    // address table is mapped in the organizationContact entity
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Collection<OrganizationContact> organizationContact;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Collection<OrganizationParameter> organizationParameter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address                         address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_organization_id", insertable = false, updatable = false)
    private Organization                    parentOrganization;

    @Transient
    private Organization original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Integer parentOrganizationId) {
        if (DataBaseUtil.isDifferent(parentOrganizationId, this.parentOrganizationId))
            this.parentOrganizationId = parentOrganizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }
    
    /*
     * support lookup entities
     */
    public Collection<OrganizationContact> getOrganizationContact() {
        return organizationContact;
    }

    public void setOrganizationContact(Collection<OrganizationContact> organizationContact) {
        this.organizationContact = organizationContact;
    }

    public Collection<OrganizationParameter> getOrganizationParameter() {
        return organizationParameter;
    }

    public void setOrganizationParameter(Collection<OrganizationParameter> organizationParameter) {
        this.organizationParameter = organizationParameter;
    }

    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrg) {
        this.parentOrganization = parentOrg;
    }

    public Address getAddress() {
        return address;
    }

    public void setClone() {
        try {
            original = (Organization)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().ORGANIZATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("parent_organization_id", parentOrganizationId, original.parentOrganizationId,Constants.table().ORGANIZATION)
                 .setField("name", name, original.name)
                 .setField("is_active", isActive, original.isActive);
                 //.setField("address_id", addressId, original.addressId, Constants.table().ADDRESS);

        return audit;
    }

}