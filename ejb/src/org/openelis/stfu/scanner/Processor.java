package org.openelis.stfu.scanner;

import java.util.ArrayList;

import org.openelis.stfu.manager.CaseManager;
import org.openelis.utils.EJBFactory;

public abstract class Processor {
	
	abstract CaseManager process() throws Exception;

	protected boolean isResultFlagged(Integer testResultId) {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(testResultId);
			
		return !EJBFactory.getTestResult().fetchTestResultsByTestIdsAndFlagPattern(ids, "m").isEmpty();
	}
	

}
