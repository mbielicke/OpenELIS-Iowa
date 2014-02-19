package org.openelis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_contact_location")
@EntityListeners({AuditUtil.class})
public class CaseContactLocation implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "case_contact_id")
    private Integer caseContactId;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "address_id")
    private Integer addressId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_contact_id", insertable = false, updatable = false)
    private CaseContact caseContact;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;
    
    @Transient
    private CaseContactLocation original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getCaseContactId() {
        return caseContactId;
    }
    
    public void setCaseContactId(Integer caseContactId) {
        if(DataBaseUtil.isDifferent(caseContactId, this.caseContactId))
            this.caseContactId = caseContactId;
    }
    
    public CaseContact getCaseContact() {
        return caseContact;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if ( DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
    }
    
    public Address getAddress() {
        return address;
    }

    @Override
    public void setClone() {
        try {
            original = (CaseContactLocation)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_CONTACT_LOCATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("case_contact_id", caseContactId, original.caseContactId, Constants.table().CASE_CONTACT)
                 .setField("location", location, original.location)
                 .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS);
        
        return audit;
    }
}
