package org.openelis.entity;

/**
 * PWSAddress Entity POJO for database
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.AuditUtil;

@NamedQueries({
               @NamedQuery(name = "PWSAddress.FetchByTinwsysIsNumber",
                           query = "select new org.openelis.domain.PWSAddressDO(p.pk.tinwslecIsNumber, p.pk.tinlgentIsNumber, p.tinwsysIsNumber,"
                                   + "p.typeCode, p.activeIndCd,p.name, p.addrLineOneTxt, p.addrLineTwoTxt,"
                                   + "p.addressCityName, p.addressStateCode, p.addressZipCode,p.stateFipsCode, p.phoneNumber)"
                                   + " from PWSAddress p where p.tinwsysIsNumber = :tinwsysIsNumber"),
               @NamedQuery(name = "PWSAddress.FetchByTinwslecIsNumberAndTinlgentIsNumber",
                           query = "select new org.openelis.domain.PWSAddressDO(p.pk.tinwslecIsNumber, p.pk.tinlgentIsNumber, p.tinwsysIsNumber,"
                                   + "p.typeCode, p.activeIndCd,p.name, p.addrLineOneTxt, p.addrLineTwoTxt,"
                                   + "p.addressCityName, p.addressStateCode, p.addressZipCode,p.stateFipsCode, p.phoneNumber)"
                                   + " from PWSAddress p where p.pk.tinwslecIsNumber = :tinwslecIsNumber and p.pk.tinlgentIsNumber = :tinlgentIsNumber"),
               @NamedQuery(name = "PWSAddress.FetchAll",
                           query = "select new org.openelis.domain.PWSAddressDO(p.pk.tinwslecIsNumber, p.pk.tinlgentIsNumber, p.tinwsysIsNumber,"
                                   + "p.typeCode, p.activeIndCd,p.name, p.addrLineOneTxt, p.addrLineTwoTxt,"
                                   + "p.addressCityName, p.addressStateCode, p.addressZipCode,p.stateFipsCode, p.phoneNumber)"
                                   + " from PWSAddress p")})
@Entity
@Table(name = "pws_address")
@EntityListeners({AuditUtil.class})
public class PWSAddress {

    @EmbeddedId
    private PK      pk;

    @Column(name = "tinwsys_is_number")
    private Integer tinwsysIsNumber;

    @Column(name = "type_code")
    private String  typeCode;

    @Column(name = "active_ind_cd")
    private String  activeIndCd;

    @Column(name = "name")
    private String  name;

    @Column(name = "addr_line_one_txt")
    private String  addrLineOneTxt;

    @Column(name = "addr_line_two_txt")
    private String  addrLineTwoTxt;

    @Column(name = "address_city_name")
    private String  addressCityName;

    @Column(name = "address_state_code")
    private String  addressStateCode;

    @Column(name = "address_zip_code")
    private String  addressZipCode;

    @Column(name = "state_fips_code")
    private String  stateFipsCode;

    @Column(name = "phone_number")
    private String  phoneNumber;

    public PK getPk() {
        return pk;
    }

    public void setPk(PK pk) {
        this.pk = pk;
    }

    public Integer getTinwslecIsNumber() {
        if (pk == null)
            pk = new PK();
        return pk.tinwslecIsNumber;
    }

    public void setTinwslecIsNumber(Integer tinwslecIsNumber) {
        if (pk == null)
            pk = new PK();
        if (DataBaseUtil.isDifferent(tinwslecIsNumber, this.pk.tinwslecIsNumber))
            this.pk.tinwslecIsNumber = tinwslecIsNumber;
    }

    public Integer getTinlgentIsNumber() {
        if (pk == null)
            pk = new PK();
        return pk.tinlgentIsNumber;
    }

    public void setTinlgentIsNumber(Integer tinlgentIsNumber) {
        if (pk == null)
            pk = new PK();
        if (DataBaseUtil.isDifferent(tinlgentIsNumber, this.pk.tinlgentIsNumber))
            this.pk.tinlgentIsNumber = tinlgentIsNumber;
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

    public String getAddrLineOneTxt() {
        return addrLineOneTxt;
    }

    public void setAddrLineOneTxt(String addrLineOneTxt) {
        if (DataBaseUtil.isDifferent(addrLineOneTxt, this.addrLineOneTxt))
            this.addrLineOneTxt = addrLineOneTxt;
    }

    public String getAddrLineTwoTxt() {
        return addrLineTwoTxt;
    }

    public void setAddrLineTwoTxt(String addrLineTwoTxt) {
        if (DataBaseUtil.isDifferent(addrLineTwoTxt, this.addrLineTwoTxt))
            this.addrLineTwoTxt = addrLineTwoTxt;
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

    @Embeddable
    public static class PK implements Serializable {

        @Column(name = "tinwslec_is_number")
        private Integer           tinwslecIsNumber;

        @Column(name = "tinlgent_is_number")
        private Integer           tinlgentIsNumber;

        private static final long serialVersionUID = 1L;

        public PK() {
        }

        public PK(Integer tinwslecIsNumber, Integer tinlgentIsNumber) {
            this.tinwslecIsNumber = tinwslecIsNumber;
            this.tinlgentIsNumber = tinlgentIsNumber;
        }

        public Integer getTinwslecIsNumber() {
            return tinwslecIsNumber;
        }

        public void setTinwslecIsNumber(Integer tinwslecIsNumber) {
            this.tinwslecIsNumber = tinwslecIsNumber;
        }

        public Integer getTinlgentIsNumber() {
            return tinlgentIsNumber;
        }

        public void setTinlgentIsNumber(Integer tinlgentIsNumber) {
            this.tinlgentIsNumber = tinlgentIsNumber;
        }

        public int hashCode() {
            return tinwslecIsNumber.hashCode() * 200 + tinlgentIsNumber.hashCode();
        }

        public boolean equals(Object arg) {
            return (arg instanceof PK &&
                    !DataBaseUtil.isDifferent( ((PK)arg).tinwslecIsNumber, tinwslecIsNumber) && !DataBaseUtil.isDifferent( ((PK)arg).tinlgentIsNumber,
                                                                                                                          tinlgentIsNumber));
        }
    }
}
