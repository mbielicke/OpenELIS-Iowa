package org.openelis.stfu.manager;

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CasePatientDO;
import org.openelis.stfu.domain.CaseProviderDO;
import org.openelis.stfu.domain.CaseResultDO;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.domain.CaseUserDO;

public class CaseManagerAccessor {
	
	public CaseDO getCase(CaseManager cm) {
		return cm.getCase();
	}
	
	public void setCase(CaseManager cm, CaseDO _case) {
		cm._case = _case;
	}
	
	public PatientDO gePatient(CaseManager cm) {
		return cm.patient;
	}
	
	public void setPatient(CaseManager cm, PatientDO patient) {
		cm.patient = patient;
	}
	
	public PatientDO getNextkin(CaseManager cm) {
		return cm.patient;
	}
	
	public void setNextkin(CaseManager cm, PatientDO nextkin) {
		cm.nextkin = nextkin;
	}
	
	public CasePatientDO getCasePatient(CaseManager cm) {
		return cm.casePatient;
	}
	
	public void setPatient(CaseManager cm, CasePatientDO casePatient) {
		cm.casePatient = casePatient;
	}
	
	public CasePatientDO getCaseNextkin(CaseManager cm) {
		return cm.caseNextkin;
	}
	
	public void setCaseNextkin(CaseManager cm, CasePatientDO caseNextkin) {
		cm.caseNextkin = caseNextkin;
	}
	
	public OrganizationViewDO getOrganization(CaseManager cm) {
		return cm.organization;
	}
	
	public void setOrganization(CaseManager cm, OrganizationViewDO organization) {
		cm.organization = organization;
	}
	
	public CaseUserDO getCaseUser(CaseManager cm) {
		return cm.caseUser;
	}
	
	public void setCaseUser(CaseManager cm, CaseUserDO caseUser) {
		cm.caseUser = caseUser;
	}
	
	public CaseProviderDO getCaseProvider(CaseManager cm) {
		return cm.caseProvider;
	}
	
	public void setCaseProvider(CaseManager cm, CaseProviderDO caseProvider) {
		cm.caseProvider = caseProvider;
	}
	
	public ArrayList<CaseAnalysisDO> getCaseAnalyses(CaseManager cm) {
		return cm.caseAnalyses;
	}
	
	public void setCaseAnalyses(CaseManager cm, ArrayList<CaseAnalysisDO> analyses) {
		cm.caseAnalyses = analyses;
	}
	
	public void addAnalysis(CaseManager cm, CaseAnalysisDO analysis) {
		cm.analysis.add(analysis);
	}
	
	public ArrayList<CaseResultDO> getCaseResults(CaseManager cm) {
		return cm.caseResults;
	}
	
	public void setCaseResults(CaseManager cm, ArrayList<CaseResultDO> results) {
		cm.caseResults = results;
	}
	
	public void addResult(CaseManager cm, CaseResultDO result) {
		cm.result.add(result);
	}
	
	public ArrayList<CaseTagDO> getCaseTags(CaseManager cm) {
		return cm.caseTags;
	}
	
	public void setCaseTags(CaseManager cm, ArrayList<CaseTagDO> tags) {
		cm.caseTags = tags;
	}
	
	public void addTag(CaseManager cm, CaseTagDO tag) {
		cm.tag.add(tag);
	}
	
	public ArrayList<DataObject> getRemoved(CaseManager cm) {
		return cm.removed;
	}
}
