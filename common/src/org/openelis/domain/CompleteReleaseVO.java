package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.RPC;

public class CompleteReleaseVO implements RPC {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer sampleId, sampleItemId, accession, analysisId, analysisStatus, specimenStatus;
	protected String test, method;
	
	public CompleteReleaseVO() {
		
	}
	
	public CompleteReleaseVO(Integer sampleId, Integer sampleItemId,
	                         Integer analysisId, Integer accession,String test,
	                         String method,Integer analysisStatus,Integer specimenStatus) {
		this.sampleId = sampleId;
		this.sampleItemId = sampleItemId;
		this.analysisId = analysisId;
		this.accession = accession;
		this.test = test;
		this.method = method;
		this.analysisStatus = analysisStatus;
		this.specimenStatus = specimenStatus;
	}

	public Integer getSampleId() {
		return sampleId;
	}
	
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}
	
	public Integer getAnalysisId() {
		return analysisId;
	}
	
	public void setAnalysisId(Integer analysisId) {
		this.analysisId = analysisId;
	}
	
	public Integer getAccession() {
		return accession;
	}

	public void setAccession(Integer accession) {
		this.accession = accession;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = DataBaseUtil.trim(test);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = DataBaseUtil.trim(method);
	}

	public Integer getAnalysisStatus() {
		return analysisStatus;
	}

	public void setAnalysisStatus(Integer analysisStatus) {
		this.analysisStatus = analysisStatus;
	}

	public Integer getSpecimenStatus() {
		return specimenStatus;
	}

	public void setSpecimenStatus(Integer specimenStatus) {
		this.specimenStatus = specimenStatus;
	}

}
