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
	@NamedQuery( name = "CaseContactLocation.fetchById",
			     query = "select new org.openelis.stfu.domain.CaseContactLocationDO(id,caseContactId,"
			             +"location,addressId) from CaseContactLocation where id = :id"),
    @NamedQuery( name = "CaseContactLocation.fetchByIds",
	    	     query = "select new org.openelis.stfu.domain.CaseContactLocationDO(id,caseContactId,"
		                 +"location,addressId) from CaseContactLocation where id in (:ids)")			             
})

@Entity
@Table(name = "case_contact_location")
@EntityListeners(value=AuditUtil.class)
public class CaseContactLocation implements Auditable, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "case_contact_id")
	private Integer caseContactId;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "address_id") 
	private Integer addressId;
	
	@Transient
	private CaseContactLocation original;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(isDifferent(id,this.id))
			this.id = id;
	}

	public Integer getCaseContactId() {
		return caseContactId;
	}

	public void setCaseContactId(Integer caseContactId) {
		if(isDifferent(caseContactId,this.caseContactId))
			this.caseContactId = caseContactId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		if(isDifferent(location,this.location))
			this.location = location;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		if(isDifferent(addressId,this.addressId))
			this.addressId = addressId;
	}

	@Override
	public void setClone() {
		try {
			original = (CaseContactLocation)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
		Audit audit;

		audit = new Audit(activity);
		audit.setReferenceTableId(Constants.table().CASE_CONTACT_LOCATION);
		audit.setReferenceId(getId());

		if(original != null) {
			audit.setField("id", id, original.id)
			     .setField("case_contact_id", caseContactId, original.caseContactId, Constants.table().CASE_CONTACT)
			     .setField("location", location, original.location)
			     .setField("address_id", addressId, original.addressId, Constants.table().ADDRESS);
		}
		
		return audit;
	}

}
