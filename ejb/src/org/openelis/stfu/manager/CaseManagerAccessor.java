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

	
	public static CaseDO getCase(CaseManager cm) {
		return cm.getCase();
	}
	
	public static void setCase(CaseManager cm, CaseDO _case) {
		cm.mycase = _case;
	}
	
	public static PatientDO gePatient(CaseManager cm) {
		return cm.patient;
	}
	
	public static void setPatient(CaseManager cm, PatientDO patient) {
		cm.patient = patient;
	}
	
	public static PatientDO getNextkin(CaseManager cm) {
		return cm.patient;
	}
	
	public static void setNextkin(CaseManager cm, PatientDO nextkin) {
		cm.nextkin = nextkin;
	}
	
	public static CasePatientDO getCasePatient(CaseManager cm) {
		return cm.casePatient;
	}
	
	public static void setPatient(CaseManager cm, CasePatientDO casePatient) {
		cm.casePatient = casePatient;
	}
	
	public static CasePatientDO getCaseNextkin(CaseManager cm) {
		return cm.caseNextkin;
	}
	
	public static void setCaseNextkin(CaseManager cm, CasePatientDO caseNextkin) {
		cm.caseNextkin = caseNextkin;
	}
	
	public static OrganizationViewDO getOrganization(CaseManager cm) {
		return cm.organization;
	}
	
	public static void setOrganization(CaseManager cm, OrganizationViewDO organization) {
		cm.organization = organization;
	}
	
	public static CaseUserDO getCaseUser(CaseManager cm) {
		return cm.caseUser;
	}
	
	public static void setCaseUser(CaseManager cm, CaseUserDO caseUser) {
		cm.caseUser = caseUser;
	}
	
	public static CaseProviderDO getCaseProvider(CaseManager cm) {
		return cm.caseProvider;
	}
	
	public static void setCaseProvider(CaseManager cm, CaseProviderDO caseProvider) {
		cm.caseProvider = caseProvider;
	}
	
	public static ArrayList<CaseAnalysisDO> getCaseAnalyses(CaseManager cm) {
		return cm.caseAnalyses;
	}
	
	public static void setCaseAnalyses(CaseManager cm, ArrayList<CaseAnalysisDO> analyses) {
		cm.caseAnalyses = analyses;
	}
	
	public static void addAnalysis(CaseManager cm, CaseAnalysisDO analysis) {
		cm.analysis.add(analysis);
	}
	
	public static ArrayList<CaseResultDO> getCaseResults(CaseManager cm) {
		return cm.caseResults;
	}
	
	public static void setCaseResults(CaseManager cm, ArrayList<CaseResultDO> results) {
		cm.caseResults = results;
	}
	
	public static void addResult(CaseManager cm, CaseResultDO result) {
		cm.result.add(result);
	}
	
	public static ArrayList<CaseTagDO> getCaseTags(CaseManager cm) {
		return cm.caseTags;
	}
	
	public static void setCaseTags(CaseManager cm, ArrayList<CaseTagDO> tags) {
		cm.caseTags = tags;
	}
	
	public static void addTag(CaseManager cm, CaseTagDO tag) {
		cm.tag.add(tag);
	}
	
	public static ArrayList<DataObject> getRemoved(CaseManager cm) {
		return cm.removed;
	}
}
