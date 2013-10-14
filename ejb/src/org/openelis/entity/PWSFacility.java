package org.openelis.entity;

/**
 * PWSFacility Entity POJO for database
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
               @NamedQuery(name = "PWSFacility.FetchByTinwsysIsNumber",
                           query = "select new org.openelis.domain.PWSFacilityDO(p.pk.tinwsfIsNumber, p.pk.tsasmpptIsNumber, p.tinwsysIsNumber, p.name,"
                                   + "p.typeCode, p.stAsgnIdentCd, p.activityStatusCd, p.waterTypeCode, p.availabilityCode,"
                                   + "p.identificationCd, p.descriptionText, p.sourceTypeCode)"
                                   + " from PWSFacility p where p.tinwsysIsNumber = :tinwsysIsNumber"),

               @NamedQuery(name = "PWSFacility.FetchByTinwsfIsNumberAndTsasmpptIsNumber",
                           query = "select new org.openelis.domain.PWSFacilityDO(p.pk.tinwsfIsNumber, p.pk.tsasmpptIsNumber, p.tinwsysIsNumber, p.name,"
                                   + "p.typeCode, p.stAsgnIdentCd, p.activityStatusCd, p.waterTypeCode, p.availabilityCode,"
                                   + "p.identificationCd, p.descriptionText, p.sourceTypeCode)"
                                   + " from PWSFacility p where p.pk.tinwsfIsNumber = :tinwsfIsNumber and p.pk.tsasmpptIsNumber = :tsasmpptIsNumber"),
               @NamedQuery(name = "PWSFacility.FetchAll",
                           query = "select new org.openelis.domain.PWSFacilityDO(p.pk.tinwsfIsNumber, p.pk.tsasmpptIsNumber, p.tinwsysIsNumber, p.name,"
                                   + "p.typeCode, p.stAsgnIdentCd, p.activityStatusCd, p.waterTypeCode, p.availabilityCode,"
                                   + "p.identificationCd, p.descriptionText, p.sourceTypeCode)"
                                   + " from PWSFacility p")})
@Entity
@Table(name = "pws_facility")
@EntityListeners({AuditUtil.class})
public class PWSFacility implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PK                pk;

    @Column(name = "tinwsys_is_number")
    private Integer           tinwsysIsNumber;

    @Column(name = "name")
    private String            name;

    @Column(name = "type_code")
    private String            typeCode;

    @Column(name = "st_asgn_ident_cd")
    private String            stAsgnIdentCd;

    @Column(name = "activity_status_cd")
    private String            activityStatusCd;

    @Column(name = "water_type_code")
    private String            waterTypeCode;

    @Column(name = "availability_code")
    private String            availabilityCode;

    @Column(name = "identification_cd")
    private String            identificationCd;

    @Column(name = "description_text")
    private String            descriptionText;

    @Column(name = "source_type_code")
    private String            sourceTypeCode;

    public PK getPk() {
        return pk;
    }

    public void setPk(PK pk) {
        this.pk = pk;
    }

    public Integer getTinwsfIsNumber() {
        if (pk == null)
            pk = new PK();
        return pk.tinwsfIsNumber;
    }

    public void setTinwsfIsNumber(Integer tinwsfIsNumber) {
        if (pk == null)
            pk = new PK();
        if (DataBaseUtil.isDifferent(tinwsfIsNumber, this.pk.tinwsfIsNumber))
        this.pk.tinwsfIsNumber = tinwsfIsNumber;
    }

    public Integer getTsasmpptIsNumber() {
        if (pk == null)
            pk = new PK();
        return pk.tsasmpptIsNumber;
    }

    public void setTsasmpptIsNumber(Integer tsasmpptIsNumber) {
        if (pk == null)
            pk = new PK();
        if (DataBaseUtil.isDifferent(tsasmpptIsNumber, this.pk.tsasmpptIsNumber))
        this.pk.tsasmpptIsNumber = tsasmpptIsNumber;
    }

    public Integer getTinwsysIsNumber() {
        return tinwsysIsNumber;
    }

    public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
        if (DataBaseUtil.isDifferent(tinwsysIsNumber, this.tinwsysIsNumber))
            this.tinwsysIsNumber = tinwsysIsNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        if (DataBaseUtil.isDifferent(typeCode, this.typeCode))
            this.typeCode = typeCode;
    }

    public String getStAsgnIdentCd() {
        return stAsgnIdentCd;
    }

    public void setStAsgnIdentCd(String stAsgnIdentCd) {
        if (DataBaseUtil.isDifferent(stAsgnIdentCd, this.stAsgnIdentCd))
            this.stAsgnIdentCd = stAsgnIdentCd;
    }

    public String getActivityStatusCd() {
        return activityStatusCd;
    }

    public void setActivityStatusCd(String activityStatusCd) {
        if (DataBaseUtil.isDifferent(activityStatusCd, this.activityStatusCd))
            this.activityStatusCd = activityStatusCd;
    }

    public String getWaterTypeCode() {
        return waterTypeCode;
    }

    public void setWaterTypeCode(String waterTypeCode) {
        if (DataBaseUtil.isDifferent(waterTypeCode, this.waterTypeCode))
            this.waterTypeCode = waterTypeCode;
    }

    public String getAvailabilityCode() {
        return availabilityCode;
    }

    public void setAvailabilityCode(String availabilityCode) {
        if (DataBaseUtil.isDifferent(availabilityCode, this.availabilityCode))
            this.availabilityCode = availabilityCode;
    }

    public String getIdentificationCd() {
        return identificationCd;
    }

    public void setIdentificationCd(String identificationCd) {
        if (DataBaseUtil.isDifferent(identificationCd, this.identificationCd))
            this.identificationCd = identificationCd;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        if (DataBaseUtil.isDifferent(descriptionText, this.descriptionText))
            this.descriptionText = descriptionText;
    }

    public String getSourceTypeCode() {
        return sourceTypeCode;
    }

    public void setSourceTypeCode(String sourceTypeCode) {
        if (DataBaseUtil.isDifferent(sourceTypeCode, this.sourceTypeCode))
            this.sourceTypeCode = sourceTypeCode;
    }

    @Embeddable
    public static class PK implements Serializable {

        @Column(name = "tinwsf_is_number")
        private Integer           tinwsfIsNumber;

        @Column(name = "tsasmppt_is_number")
        private Integer           tsasmpptIsNumber;

        private static final long serialVersionUID = 1L;

        public PK() {
        }

        public PK(Integer tinwsfIsNumber, Integer tsasmpptIsNumber) {
            this.tinwsfIsNumber = tinwsfIsNumber;
            this.tsasmpptIsNumber = tsasmpptIsNumber;
        }

        public Integer getTinwsfIsNumber() {
            return tinwsfIsNumber;
        }

        public void setTinwsfIsNumber(Integer tinwsfIsNumber) {
            this.tinwsfIsNumber = tinwsfIsNumber;
        }

        public Integer getTsasmpptIsNumber() {
            return tsasmpptIsNumber;
        }

        public void setTsasmpptIsNumber(Integer tsasmpptIsNumber) {
            this.tsasmpptIsNumber = tsasmpptIsNumber;
        }

        public int hashCode() {
            return tinwsfIsNumber.hashCode() * 200 + tsasmpptIsNumber.hashCode();
        }

        public boolean equals(Object arg) {
            return (arg instanceof PK &&
                    !DataBaseUtil.isDifferent( ((PK)arg).tinwsfIsNumber, tinwsfIsNumber) && !DataBaseUtil.isDifferent( ((PK)arg).tsasmpptIsNumber,
                                                                                                                      tsasmpptIsNumber));
        }
    }
}
