package org.openelis.stfu.scanner;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.utils.EJBFactory;

public class CFTRProcessor extends Processor {

	SampleManager1 sm;
	AnalysisViewDO analysis;
	
	public CFTRProcessor(SampleManager1 sm, AnalysisViewDO analysis) {
		this.sm = sm;
		this.analysis = analysis;
	}
		
	public CaseManager process() throws Exception {
		CaseFactory caseFactory;
		CaseManager cm = null;
		
		ResultViewDO result = sm.result.get(analysis, 1, 1);
		if(isResultFlagged(result.getTestResultId())) {
			caseFactory = new CaseFactory(EJBFactory.getDictionaryCache().getBySystemName("cftr_condition").getId());
			cm = caseFactory.create(sm, analysis, result);
		}
		
		return cm;

	}

}
