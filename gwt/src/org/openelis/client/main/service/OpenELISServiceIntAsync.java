package org.openelis.client.main.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.gwt.client.services.AppServiceIntAsync;

public interface OpenELISServiceIntAsync extends AppServiceIntAsync {
	
	public void getMenuList(AsyncCallback callback);
}
