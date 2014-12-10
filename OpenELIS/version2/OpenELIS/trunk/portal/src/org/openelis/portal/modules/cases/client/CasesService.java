package org.openelis.portal.modules.cases.client;

import java.util.ArrayList;

import org.openelis.stfu.manager.CaseManager;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public enum CasesService implements CasesServiceInt, CasesServiceIntAsync {
	
	INSTANCE;
	
	protected CasesServiceIntAsync service;
	
	private CasesService() {
		service = GWT.create(CasesServiceInt.class);
		((HasRpcToken)service).setRpcToken(TokenService.getToken());
	}

	@Override
	public void getActiveCases(AsyncCallback<ArrayList<CaseManager>> callback) throws Exception {
		service.getActiveCases(callback);
		
	}

	@Override
	public ArrayList<CaseManager> getActiveCases() throws Exception {
		Callback<ArrayList<CaseManager>> callback;
		
		callback = new Callback<ArrayList<CaseManager>>();
		service.getActiveCases(callback);
		return callback.getResult();
	}

	
}
