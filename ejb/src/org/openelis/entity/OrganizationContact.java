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
 * OrganizationContact Entity POJO for database
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

@NamedQueries({
    @NamedQuery( name = "OrganizationContact.FetchByOrganizationId",
                query = "select new org.openelis.domain.OrganizationContactDO(c.id,c.organizationId," +
                		"c.contactTypeId,c.name,c.address.id,c.address.multipleUnit,c.address.streetAddress," +
                		"c.address.city,c.address.state,c.address.zipCode,c.address.workPhone,c.address.homePhone," +
                		"c.address.cellPhone,c.address.faxPhone,c.address.email,c.address.country)"
                      + " from OrganizationContact c where c.organizationId = :id")})
@Entity
@Table(name = "organization_contact")
@EntityListeners({AuditUtil.class})
public class OrganizationContact implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer             id;

    @Column(name = "organization_id")
    private Integer             organizationId;

    @Column(name = "contact_type_id")
    private Integer             contactTypeId;

    @Column(name = "name")
    private String              name;

    @Column(name = "address_id")
    private Integer             addressId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address             address;

    @Transient
    private OrganizationContact original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }

    public Integer getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(Integer contactTypeId) {
        if (DataBaseUtil.isDifferent(contactTypeId, this.contactTypeId))
            this.contactTypeId = contactTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }

    public Address getAddressTable() {
        return address;
    }

    public void setAddressTable(Address addressTable) {
        this.address = addressTable;
    }

    public void setClone() {
        try {
            original = (OrganizationContact)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.ORGANIZATION_CONTACT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("organization_id", organizationId, original.organizationId)
                 .setField("contact_type_id", contactTypeId, original.contactTypeId, ReferenceTable.DICTIONARY)
                 .setField("name", name, original.name) 
                 .setField("address_id", addressId, original.addressId, ReferenceTable.ADDRESS);

        return audit;
    }
}
