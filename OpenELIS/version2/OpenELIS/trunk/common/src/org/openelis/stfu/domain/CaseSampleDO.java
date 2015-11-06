package org.openelis.stfu.domain;

import static org.openelis.ui.common.DataBaseUtil.*;

import java.util.Date;

import org.openelis.domain.DataObject;
import org.openelis.ui.common.Datetime;

public class CaseSampleDO extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id,caseId,sampleId,organizationId;
	private String accession;
	private Datetime collectionDate;
	
	public CaseSampleDO() {
		
	}
	
	public CaseSampleDO(Integer id, Integer sampleId, Integer caseId, Integer organizationId, Date collectionDate) {
		setId(id);
		setSampleId(sampleId);
		setCaseId(caseId);
		setAccession(accession);
		setOrganizationId(organizationId);
		setCollectionDate(toYM(collectionDate));
		_changed = false;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		_changed = true;
	}
	
	public Integer getSampleId() {
		return sampleId;
	}
	
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
		_changed = true;
	}

	public Integer getCaseId() {
		return caseId;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
		_changed = true;
	}
	
	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
		_changed = true;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = trim(accession);
		_changed = true;
	}

	public Datetime getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Datetime collectionDate) {
		this.collectionDate = toYM(collectionDate);
		_changed = true;
	}	
}
