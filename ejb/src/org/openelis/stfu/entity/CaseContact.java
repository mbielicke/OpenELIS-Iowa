package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
	@NamedQuery( name = "CaseContact.fetchById",
			     query = "select new org.openelis.common.stfu.domain.CaseContactDO(id,sourceReference,sourceReferenceId,lastName,"
			             +"firstName,typeId,npi) from CaseContact where id = :id"),
    @NamedQuery( name = "CaseContact.fetchByIds",
			     query = "select new org.openelis.common.stfu.domain.CaseContactDO(id,sourceReference,sourceReferenceId,lastName,"
	      	             +"firstName,typeId,npi) from CaseContact where id in (:ids)")			             
})

@Entity
@Table(name = "case_contact")
@EntityListeners(value=AuditUtil.class)
public class CaseContact implements Auditable, Cloneable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "source_reference")
	private Integer sourceReference;
	
	@Column(name = "source_reference_id")
	private String sourceReferenceId;
	
	@Column(name = "last_name") 
	private String lastName;
	
	@Column(name = "first_name")
	private String firstName;
		
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
		if(isDifferent(id,this.id))
			this.id = id;
	}

	public Integer getSource() {
		return sourceReference;
	}

	public void setSource(Integer source) {
		if(isDifferent(source,this.sourceReference))
			this.sourceReference = source;
	}

	public String getSourceReference() {
		return sourceReferenceId;
	}

	public void setSourceReference(String sourceReference) {
		if(isDifferent(sourceReference,this.sourceReferenceId))
			this.sourceReferenceId = sourceReference;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if(isDifferent(lastName,this.lastName))
			this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if(isDifferent(firstName,this.firstName))
			this.firstName = firstName;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		if(isDifferent(typeId,this.typeId))
			this.typeId = typeId;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		if(isDifferent(npi,this.npi))
			this.npi = npi;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseContact)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
		Audit audit;

		audit = new Audit(activity);
		audit.setReferenceTableId(Constants.table().CASE_CONTACT);
		audit.setReferenceId(getId());

		if(original != null) {
			audit.setField("id", id, original.id)
			     .setField("source_reference", sourceReference, original.sourceReference)
			     .setField("sourece_reference_id", sourceReferenceId, original.sourceReferenceId)
			     .setField("last_name", lastName, original.lastName)
			     .setField("first_name", firstName, original.firstName)
			     .setField("type_id", typeId, original.typeId)
			     .setField("npi", npi, original.npi);
		}

		return audit;
	}
	

}
