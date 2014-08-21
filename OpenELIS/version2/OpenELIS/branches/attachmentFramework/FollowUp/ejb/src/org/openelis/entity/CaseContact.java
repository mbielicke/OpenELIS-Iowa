package org.openelis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name = "case_contact")
@EntityListeners({AuditUtil.class})
public class CaseContact  implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "source")
    private Integer source;
    
    @Column(name = "source_reference")
    private String sourceReference;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "middle_name")
    private String middleName;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "npi")
    private String npi;
    
    @Transient
    private CaseContact original;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getSource() {
        return source;
    }
    
    public void setSource(Integer source) {
        if(DataBaseUtil.isDifferent(source, this.source))
            this.source = source;
    }
    
    public String getSourceReference() {
        return sourceReference;
    }
    
    public void setSourceReference(String sourceReference) {
        if(DataBaseUtil.isDifferent(sourceReference, this.sourceReference))
            this.sourceReference = sourceReference;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (DataBaseUtil.isDifferent(lastName, this.lastName))
            this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (DataBaseUtil.isDifferent(firstName, this.firstName))
            this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        if(DataBaseUtil.isDifferent(middleName, this.middleName))
            this.middleName = middleName;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        if(DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }
    
    public String getNPI() {
        return npi;
    }
    
    public void setNPI(String npi) {
        if(DataBaseUtil.isDifferent(npi, this.npi))
            this.npi = npi;
    }
    
    @Override
    public void setClone() {
        try {
            original = (CaseContact)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_CONTACT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("source", source, original.source, Constants.table().DICTIONARY)
                 .setField("source_reference", sourceReference, original.sourceReference)
                 .setField("last_name", lastName, original.lastName)
                 .setField("first_name", firstName, original.firstName)
                 .setField("middle_name", middleName, original.middleName)
                 .setField("type_id", typeId, original.typeId, Constants.table().DICTIONARY)
                 .setField("npi", npi, original.npi);

        return audit;
    }

}
