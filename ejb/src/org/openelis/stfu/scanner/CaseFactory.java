package org.openelis.stfu.scanner;



import java.util.HashMap;

import org.openelis.ui.common.Datetime;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.manager.CaseManagerAccessor;

public class CaseFactory {
	
	static CaseManagerAccessor accessor = new CaseManagerAccessor();
	
	CaseManager cm;
	Integer condition;
	
	public static CaseManager createCase(SampleManager1 sm, HashMap<Integer,CaseManager> cases) {
		CaseManager cm;
		Integer patientId;
		patientId = SampleManager1Accessor.getSampleNeonatal(sm).getPatientId();
		cm = cases.get(patientId);
		if (cm == null) {
			cm = create(sm);
		}
		return cm;
	}
	
	public static CaseManager create(SampleManager1 sm) {
		CaseManager cm = new CaseManager();
        accessor.setCase(cm, createCase(sm));
		return cm;
	}
	
	public static CaseDO createCase(SampleManager1 sm) {
		CaseDO caseDO = new CaseDO();
		caseDO.setPatientId(sm.getSampleNeonatal().getPatientId());
		caseDO.setNextkinId(sm.getSampleNeonatal().getNextOfKinId());
		caseDO.setOrganizationId(SampleManager1Accessor.getOrganizations(sm).get(0).getOrganizationId());
		caseDO.setCreated(Datetime.getInstance(Datetime.YEAR,Datetime.DAY));
		return caseDO;
	}
	
	public static CaseTagDO createCaseTag(Integer typeId) {
		CaseTagDO caseTag = new CaseTagDO();
		caseTag.setTypeId(typeId);
		return caseTag;
	}
	
//	public CaseFactory(Integer condition) {
//		this.condition = condition;
//		cm = new CaseManager();
//	}
//	
//	public CaseManager create(SampleManager1 sm, AnalysisViewDO analysis, ResultViewDO result) throws Exception {
//		createCase(sm,analysis);
//		createAnalysis(sm,analysis);
//		createResult(sm,analysis,result,cm.analysis.get(cm.analysis.count()-1));
//		return cm;
//	}
//
//	protected void createCase(SampleManager1 sm, AnalysisViewDO analysis) throws Exception {
//		CaseDO caseDO = new CaseDO();
//		caseDO.setId(cm.getNextUID());
//		caseDO.setCreated(Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE));
//		caseDO.setIsFinalized("N");
//		caseDO.setPatientId(sm.getSampleNeonatal().getPatientId());
//		caseDO.setNextkinId(sm.getSampleNeonatal().getNextOfKinId());
//		caseDO.setOrganizationId(sm.organization.get(0).getId());
//		accessor.setCase(cm, caseDO);
//	}
//		
//	protected void createAnalysis(SampleManager1 sm, AnalysisViewDO analysis) throws Exception {
//		
//		CaseAnalysisDO analysisDO = new CaseAnalysisDO();
//		analysisDO.setId(cm.getNextUID());
//		analysisDO.setCaseId(cm.getCase().getId());
//		analysisDO.setAccession(String.valueOf(sm.getSample().getAccessionNumber()));
//		analysisDO.setCollectionDate(sm.getSample().getCollectionDate());
//		analysisDO.setOrganizationId(sm.organization.get(0).getId());
//		analysisDO.setTestId(analysis.getTestId());
//		analysisDO.setConditionId(condition);
//		analysisDO.setStatusId(analysis.getStatusId());
//		accessor.addAnalysis(cm,analysisDO);
//	}
//
//	public void createResult(SampleManager1 sm, AnalysisViewDO analysis, ResultViewDO result, CaseAnalysisDO caseAnalysis) throws Exception {
//		CaseResultDO resultDO = new CaseResultDO();
//		resultDO.setId(cm.getNextUID());
//		resultDO.setAnalyteId(result.getAnalyteId());
//		resultDO.setCaseAnalysisId(caseAnalysis.getId());
//		resultDO.setCol(result.getResultGroup());
//		resultDO.setIsReportable(result.getIsReportable());
//		resultDO.setRow(result.getRowGroup());
//		resultDO.setTestResultId(result.getTestResultId());
//		resultDO.setTestAnalyteId(result.getTestAnalyteId());
//		resultDO.setTypeId(result.getTypeId());
//		resultDO.setValue(result.getValue());
//		accessor.addResult(cm, resultDO);
//	}
}
