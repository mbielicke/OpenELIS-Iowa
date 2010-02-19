package org.openelis.entity;

/**
 * PwsAddress Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Entity
@Table(name = "pws_address")
@EntityListeners( {AuditUtil.class})
public class PwsAddress implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer    id;

    @Column(name = "tinwsys_is_number")
    private Integer    tinwsysIsNumber;

    @Column(name = "type_code")
    private String     typeCode;

    @Column(name = "active_ind_cd")
    private String     activeIndCd;

    @Column(name = "name")
    private String     name;

    @Column(name = "address_line_one_text")
    private String     addressLineOneText;

    @Column(name = "address_line_two_text")
    private String     addressLineTwoText;

    @Column(name = "address_city_name")
    private String     addressCityName;

    @Column(name = "address_state_code")
    private String     addressStateCode;

    @Column(name = "address_zip_code")
    private String     addressZipCode;

    @Column(name = "state_fips_code")
    private String     stateFipsCode;

    @Column(name = "phone_number")
    private String     phoneNumber;

    @Transient
    private PwsAddress original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        if (DataBaseUtil.isDifferent(typeCode, this.typeCode))
            this.typeCode = typeCode;
    }

    public String getActiveIndCd() {
        return activeIndCd;
    }

    public void setActiveIndCd(String activeIndCd) {
        if (DataBaseUtil.isDifferent(activeIndCd, this.activeIndCd))
            this.activeIndCd = activeIndCd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getAddressLineOneText() {
        return addressLineOneText;
    }

    public void setAddressLineOneText(String addressLineOneText) {
        if (DataBaseUtil.isDifferent(addressLineOneText, this.addressLineOneText))
            this.addressLineOneText = addressLineOneText;
    }

    public String getAddressLineTwoText() {
        return addressLineTwoText;
    }

    public void setAddressLineTwoText(String addressLineTwoText) {
        if (DataBaseUtil.isDifferent(addressLineTwoText, this.addressLineTwoText))
            this.addressLineTwoText = addressLineTwoText;
    }

    public String getAddressCityName() {
        return addressCityName;
    }

    public void setAddressCityName(String addressCityName) {
        if (DataBaseUtil.isDifferent(addressCityName, this.addressCityName))
            this.addressCityName = addressCityName;
    }

    public String getAddressStateCode() {
        return addressStateCode;
    }

    public void setAddressStateCode(String addressStateCode) {
        if (DataBaseUtil.isDifferent(addressStateCode, this.addressStateCode))
            this.addressStateCode = addressStateCode;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        if (DataBaseUtil.isDifferent(addressZipCode, this.addressZipCode))
            this.addressZipCode = addressZipCode;
    }

    public String getStateFipsCode() {
        return stateFipsCode;
    }

    public void setStateFipsCode(String stateFipsCode) {
        if (DataBaseUtil.isDifferent(stateFipsCode, this.stateFipsCode))
            this.stateFipsCode = stateFipsCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (DataBaseUtil.isDifferent(phoneNumber, this.phoneNumber))
            this.phoneNumber = phoneNumber;
    }

    public void setClone() {
        try {
            original = (PwsAddress)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.PWS_ADDRESS);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("tinwsys_is_number", tinwsysIsNumber, original.tinwsysIsNumber)
                 .setField("type_code", typeCode, original.typeCode)
                 .setField("active_ind_cd", activeIndCd, original.activeIndCd)
                 .setField("name", name, original.name)
                 .setField("address_line_one_text", addressLineOneText, original.addressLineOneText)
                 .setField("address_line_two_text", addressLineTwoText, original.addressLineTwoText)
                 .setField("address_city_name", addressCityName, original.addressCityName)
                 .setField("address_state_code", addressStateCode, original.addressStateCode)
                 .setField("address_zip_code", addressZipCode, original.addressZipCode)
                 .setField("state_fips_code", stateFipsCode, original.stateFipsCode)
                 .setField("phone_number", phoneNumber, original.phoneNumber);

        return audit;
    }

}
