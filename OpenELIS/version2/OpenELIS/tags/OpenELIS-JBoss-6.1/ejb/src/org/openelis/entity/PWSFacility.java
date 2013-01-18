package org.openelis.entity;

/**
 * PWSFacility Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.AuditUtil;

@NamedQuery(name = "PWSFacility.FetchByTinwsysIsNumber",
           query = "select new org.openelis.domain.PWSFacilityDO(p.id, p.tinwsysIsNumber, p.name," +
                   "p.typeCode, p.stAsgnIdentCd, p.activityStatusCd, p.waterTypeCode, p.availabilityCode," +
            	   "p.identificationCd, p.descriptionText, p.sourceTypeCode)"
                 + " from PWSFacility p where p.tinwsysIsNumber = :tinwsysIsNumber")
@Entity
@Table(name = "pws_facility")
@EntityListeners({AuditUtil.class})
public class PWSFacility implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tinwsys_is_number")
    private Integer tinwsysIsNumber;

    @Column(name = "name")
    private String  name;

    @Column(name = "type_code")
    private String  typeCode;

    @Column(name = "st_asgn_ident_cd")
    private String  stAsgnIdentCd;

    @Column(name = "activity_status_cd")
    private String  activityStatusCd;

    @Column(name = "water_type_code")
    private String  waterTypeCode;

    @Column(name = "availability_code")
    private String  availabilityCode;

    @Column(name = "identification_cd")
    private String  identificationCd;

    @Column(name = "description_text")
    private String  descriptionText;

    @Column(name = "source_type_code")
    private String  sourceTypeCode;

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
}
