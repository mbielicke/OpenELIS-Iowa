package org.openelis.stfu.scanner;



import java.util.HashMap;

import org.openelis.ui.common.Datetime;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CaseResultDO;
import org.openelis.stfu.domain.CaseSampleDO;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.manager.CaseManagerAccessor;

public class CaseFactory {
	
	CaseManager cm;
	Integer condition;
	
	public static CaseManager getCase(SampleManager1 sm, HashMap<Integer,CaseManager> cases) {
		CaseManager cm;
		Integer patientId;
		patientId = SampleManager1Accessor.getSampleNeonatal(sm).getPatientId();
		cm = cases.get(patientId);
		if (cm == null) {
			cm = create(sm);
			cases.put(patientId,cm);
		}
		return cm;
	}
	
	public static CaseManager create(SampleManager1 sm) {
		CaseManager cm;
		CaseDO _case;
		CaseSampleDO sample;
		CaseAnalysisDO caseAnalysis;
		
		cm = new CaseManager();
		_case = createCase(cm,sm);
        CaseManagerAccessor.setCase(cm, _case);
        sample = createCaseSample(cm, sm);
        CaseManagerAccessor.addSample(cm, sample);
        if (SampleManager1Accessor.getAnalyses(sm) != null) {
        	for (AnalysisViewDO analysis : SampleManager1Accessor.getAnalyses(sm)) {
        		caseAnalysis = createCaseAnalysis(cm,sm,sample, analysis);
        		CaseManagerAccessor.addAnalysis(cm, caseAnalysis);
        		for (int r = 0; r < sm.result.count(analysis); r++) {
        			for (int c = 0; r < sm.result.count(analysis,r); c++) {
        				CaseManagerAccessor.addResult(cm, createCaseResult(cm,sm,caseAnalysis,r,c,sm.result.get(analysis, r, c)));
        			}
        		}
        	}
        }
		return cm;
	}
	
	public static CaseDO createCase(CaseManager cm, SampleManager1 sm) {
		CaseDO caseDO = new CaseDO();
		
		caseDO.setId(cm.getNextUID());
		caseDO.setPatientId(sm.getSampleNeonatal().getPatientId());
		caseDO.setNextkinId(sm.getSampleNeonatal().getNextOfKinId());
		caseDO.setOrganizationId(SampleManager1Accessor.getOrganizations(sm).get(0).getOrganizationId());
		caseDO.setCreated(Datetime.getInstance(Datetime.YEAR,Datetime.DAY));
		
		return caseDO;
	}
	
	public static CaseSampleDO createCaseSample(CaseManager cm, SampleManager1 sm) {
		CaseSampleDO caseSample;
		
		caseSample = new CaseSampleDO();
		caseSample.setId(cm.getNextUID());
		caseSample.setSampleId(sm.getSample().getId());
		caseSample.setCaseId(cm.getCase().getId());
		caseSample.setAccession(sm.getSample().getAccessionNumber().toString());
		caseSample.setCollectionDate(sm.getSample().getCollectionDate());
		caseSample.setOrganizationId(SampleManager1Accessor.getOrganizations(sm).get(0).getId());
		
		return caseSample;
		
	}
	
	public static CaseAnalysisDO createCaseAnalysis(CaseManager cm, SampleManager1 sm, CaseSampleDO sample, AnalysisViewDO analysis) {
		CaseAnalysisDO caseAnalysis;
		
		caseAnalysis = new CaseAnalysisDO();
		caseAnalysis.setId(cm.getNextUID());
		caseAnalysis.setCaseSampleId(sample.getId());
		caseAnalysis.setTestId(analysis.getTestId());
		caseAnalysis.setStatusId(analysis.getStatusId());
		caseAnalysis.setCompletedDate(analysis.getCompletedDate());
		
		return caseAnalysis;
	}
	
	public static CaseResultDO createCaseResult(CaseManager cm, SampleManager1 sm, CaseAnalysisDO analysis, int row, int col, ResultViewDO result) {
		CaseResultDO caseResult;
		
		caseResult = new CaseResultDO();
		caseResult.setId(cm.getNextUID());
		caseResult.setCaseAnalysisId(analysis.getId());
		caseResult.setAnalyteId(result.getAnalyteId());
		caseResult.setIsReportable(result.getIsReportable());
		caseResult.setRow(row);
		caseResult.setCol(col);
		caseResult.setTestAnalyteId(result.getTestAnalyteId());
		caseResult.setTestResultId(result.getTestResultId());
		caseResult.setTypeId(result.getTypeId());
		caseResult.setValue(result.getValue());
		
		return caseResult;
	}
	
	public static CaseTagDO createCaseTag(Integer typeId) {
		CaseTagDO caseTag = new CaseTagDO();
		caseTag.setTypeId(typeId);
		return caseTag;
	}
}
