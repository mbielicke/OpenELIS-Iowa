package org.openelis.entity;

/**
 * PwsFacility Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "pws_facility")
@EntityListeners( {AuditUtil.class})
public class PwsFacility implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer     id;

    @Column(name = "tinwsys_is_number")
    private Integer     tinwsysIsNumber;

    @Column(name = "name")
    private String      name;

    @Column(name = "type_code")
    private String      typeCode;

    @Column(name = "st_asgn_ident_cd")
    private String      stAsgnIdentCd;

    @Column(name = "activity_status_cd")
    private String      activityStatusCd;

    @Column(name = "water_type_code")
    private String      waterTypeCode;

    @Column(name = "availability_code")
    private String      availabilityCode;

    @Column(name = "identification_cd")
    private String      identificationCd;

    @Column(name = "description_text")
    private String      descriptionText;

    @Column(name = "source_type_code")
    private String      sourceTypeCode;

    @Transient
    private PwsFacility original;

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

    public void setClone() {
        try {
            original = (PwsFacility)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.PWS_FACILITY);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("tinwsys_is_number", tinwsysIsNumber, original.tinwsysIsNumber)
                 .setField("name", name, original.name)
                 .setField("type_code", typeCode, original.typeCode)
                 .setField("st_asgn_ident_cd", stAsgnIdentCd, original.stAsgnIdentCd)
                 .setField("activity_status_cd", activityStatusCd, original.activityStatusCd)
                 .setField("water_type_code", waterTypeCode, original.waterTypeCode)
                 .setField("availability_code", availabilityCode, original.availabilityCode)
                 .setField("identification_cd", identificationCd, original.identificationCd)
                 .setField("description_text", descriptionText, original.descriptionText)
                 .setField("source_type_code", sourceTypeCode, original.sourceTypeCode);

        return audit;
    }

}
