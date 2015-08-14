package org.openelis.stfu.scanner;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.manager.SampleManager1;

public class ProcessorFactory {
	
	public Processor process(SampleManager1 sm, AnalysisViewDO analysis) throws Exception {
		String test = analysis.getTestName().substring(4, analysis.getTestName().indexOf(","));
		switch(test) {
			case "bt" :
				 return new BTProcessor(sm,analysis);
			case "cah" :
				 return new CAHProcessor(sm,analysis);
			case "cftr" :
				return new CFTRProcessor(sm,analysis);
			case "galt" :
				return new GALTProcessor(sm, analysis);
			case "hb" :
				return new HBProcessor(sm, analysis);
			case "irt" :
				return new IRTProcessor(sm, analysis);
			case "tms" :
				return new TSHProcessor(sm, analysis);
			default :
				throw new IllegalArgumentException();
		}
	}
}
