package org.openelis.stfu.entity;

import static org.openelis.ui.common.DataBaseUtil.isDifferentYM;
import static org.openelis.ui.common.DataBaseUtil.toDate;
import static org.openelis.ui.common.DataBaseUtil.toYM;
import static org.openelis.ui.common.DataBaseUtil.isDifferent;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="case_sample")
@EntityListeners({AuditUtil.class})
public class CaseSample implements Auditable, Cloneable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                     id;
    
    @Column(name="case_id")
    private Integer caseId;
    
    @Column(name="sample_id")
    private Integer sampleId;
    
    @Column(name="accession")
    private String accession;
    
    @Column(name="organization_id")
    private Integer organizationId;
    
    @Column(name="collection_date")
    private Date collectionDate;

    @Transient
    private CaseSample original;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if (isDifferent(id, this.id))
			this.id = id;
	}

	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		if (isDifferent(caseId, this.caseId))
			this.caseId = caseId;
	}

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		if (isDifferent(sampleId, this.sampleId))
			this.sampleId = sampleId;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		if (isDifferent(accession, this.accession))
			this.accession = accession;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		if (isDifferent(organizationId, this.organizationId))
			this.organizationId = organizationId;
	}

	public Datetime getCollectionDate() {
		return toYM(collectionDate);
	}

	public void setTimeStamp(Datetime collectionDate) {
		if(isDifferentYM(collectionDate,this.collectionDate))
			this.collectionDate = toDate(collectionDate);
	}
    
	@Override
	public void setClone() {
		try {
			original = (CaseSample)this.clone();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().CASE_SAMPLE);
        audit.setReferenceId(getId());
        
        if(original != null) {
        	audit.setField("id", id, original.id)
        	     .setField("case_id", caseId, original.caseId)
        	     .setField("sample_id", sampleId, original.sampleId)
        	     .setField("accession", accession, original.accession)
        	     .setField("organization_id", organizationId, original.organizationId, Constants.table().ORGANIZATION)
        	     .setField("collection_date", collectionDate, original.collectionDate);
        }
		
        return audit;
	}

}
