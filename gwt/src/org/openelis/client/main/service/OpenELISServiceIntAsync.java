package org.openelis.client.main.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OpenELISServiceIntAsync {

	public void logout(AsyncCallback callback);
	
	public void getMenuList(AsyncCallback callback);
}
