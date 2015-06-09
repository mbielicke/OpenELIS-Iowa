package org.openelis.portal.modules.cases.client;

import java.util.ArrayList;

import org.openelis.stfu.manager.CaseManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CasesServiceIntAsync {
	
	void getActiveCases(AsyncCallback<ArrayList<CaseManager>> callback) throws Exception;

}
