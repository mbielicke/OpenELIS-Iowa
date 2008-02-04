package org.openelis.modules.main.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.gwt.services.AppServiceIntAsync;

public interface OpenELISServiceIntAsync extends AppServiceIntAsync {
	
	public void getMenuList(AsyncCallback callback);
}
