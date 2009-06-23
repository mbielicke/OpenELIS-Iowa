package org.openelis.modules.main.client.service;

import org.openelis.gwt.common.RPC;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ScreenServiceIntAsync {
	
	public Request call(String method, RPC rpc, AsyncCallback<? extends RPC> callback);

}
