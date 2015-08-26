package org.openelis.stfu.scanner;



import org.openelis.manager.SampleManager1;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.manager.CaseManagerAccessor;

public class CaseFactory {
	
	static CaseManagerAccessor accessor = new CaseManagerAccessor();
	
	CaseManager cm;
	Integer condition;
	
	public static CaseManager create(SampleManager1 sm, Integer tag) {
		CaseManager _case = new CaseManager();
		CaseTagDO caseTag = new CaseTagDO();
		caseTag.setTypeId(tag);
		accessor.addTag(_case,caseTag);
		return _case;
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
