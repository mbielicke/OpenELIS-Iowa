package org.openelis.client.service;

import com.google.gwt.user.client.rpc.RemoteService;

public interface OpenELISServiceInt extends RemoteService {

	public void logout();
	
	public String getMenuList(); 
}
