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
 * Address Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Address.FetchById",
                query = "select new org.openelis.domain.AddressDO(a.id,a.multipleUnit,a.streetAddress,a.city,"+
                        "a.state,a.zipCode,a.workPhone,a.homePhone,a.cellPhone,a.faxPhone,a.email,a.country)"
                      + " from Address a where a.id = :id")})
@Entity
@Table(name = "address")
@EntityListeners( {AuditUtil.class})
public class Address implements Auditable, Cloneable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "multiple_unit")
    private String  multipleUnit;

    @Column(name = "street_address")
    private String  streetAddress;

    @Column(name = "city")
    private String  city;

    @Column(name = "state")
    private String  state;

    @Column(name = "zip_code")
    private String  zipCode;

    @Column(name = "work_phone")
    private String  workPhone;

    @Column(name = "home_phone")
    private String  homePhone;

    @Column(name = "cell_phone")
    private String  cellPhone;

    @Column(name = "fax_phone")
    private String  faxPhone;

    @Column(name = "email")
    private String  email;

    @Column(name = "country")
    private String  country;

    @Transient
    private Address original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getMultipleUnit() {
        return multipleUnit;
    }

    public void setMultipleUnit(String multipleUnit) {
        if (DataBaseUtil.isDifferent(multipleUnit, this.multipleUnit))
            this.multipleUnit = multipleUnit;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        if (DataBaseUtil.isDifferent(streetAddress, this.streetAddress))
            this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (DataBaseUtil.isDifferent(city, this.city))
            this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (DataBaseUtil.isDifferent(state, this.state))
            this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        if (DataBaseUtil.isDifferent(zipCode, this.zipCode))
            this.zipCode = zipCode;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        if (DataBaseUtil.isDifferent(workPhone, this.workPhone))
            this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        if (DataBaseUtil.isDifferent(homePhone, this.homePhone))
            this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        if (DataBaseUtil.isDifferent(cellPhone, this.cellPhone))
            this.cellPhone = cellPhone;
    }

    public String getFaxPhone() {
        return faxPhone;
    }

    public void setFaxPhone(String faxPhone) {
        if (DataBaseUtil.isDifferent(faxPhone, this.faxPhone))
            this.faxPhone = faxPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (DataBaseUtil.isDifferent(email, this.email))
            this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (DataBaseUtil.isDifferent(country, this.country))
            this.country = country;
    }

    public void setClone() {
        try {
            original = (Address)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ADDRESS);
        audit.setReferenceId(getId());        
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("multiple_unit", multipleUnit, original.multipleUnit)
                 .setField("street_address", streetAddress, original.streetAddress)
                 .setField("city", city, original.city)
                 .setField("state", state, original.state)
                 .setField("zip_code", zipCode, original.zipCode)
                 .setField("work_phone", workPhone, original.workPhone)
                 .setField("home_phone", homePhone, original.homePhone)
                 .setField("cell_phone", cellPhone, original.cellPhone)
                 .setField("fax_phone", faxPhone, original.faxPhone)
                 .setField("email", email, original.email)
                 .setField("country", country, original.country);

        return audit;
    }
}