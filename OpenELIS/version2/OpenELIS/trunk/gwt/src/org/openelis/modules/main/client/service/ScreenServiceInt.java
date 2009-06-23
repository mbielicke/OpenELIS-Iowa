package org.openelis.modules.main.client.service;

import org.openelis.gwt.common.RPC;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("OpenELISServlet")
public interface ScreenServiceInt extends RemoteService{
    
	public <T extends RPC> T call(String method, T rpc) throws Exception;

}
