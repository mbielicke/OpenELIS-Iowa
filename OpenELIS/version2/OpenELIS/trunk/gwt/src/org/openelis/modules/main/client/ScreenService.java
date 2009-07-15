/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public Software License(the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. All Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be used under the terms of a UIRF Software license ("UIRF Software License"), in which case the provisions of a UIRF Software License are applicable instead of those above.
 */
package org.openelis.modules.main.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.services.ScreenServiceInt;
import org.openelis.gwt.services.ScreenServiceIntAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.SyncCallback;

public class ScreenService implements ScreenServiceInt, ScreenServiceIntAsync {
    private ScreenServiceIntAsync service;

    public ScreenService(ScreenServiceIntAsync service) {
        this.service = service;
    }

    public ScreenService(String url) {
        service = (ScreenServiceIntAsync)GWT.create(ScreenServiceInt.class);
        ((ServiceDefTarget)service).setServiceEntryPoint(((ServiceDefTarget)service).getServiceEntryPoint() + url);
    }

    public ScreenServiceIntAsync getAsyncService() {
        return service;
    }

    private class Callback<T> implements SyncCallback<T> {
        T         result;
        Throwable caught;

        public void onFailure(Throwable caught) {
            this.caught = caught;
        }

        public void onSuccess(T result) {
            this.result = result;
        }

        public T getResult() throws RPCException {
            if (caught != null)
                throw (RPCException)caught;
            return result;
        }
    }

    public <T extends RPC> T call(String method, Integer param) throws Exception {
        Callback<T> callback = new Callback<T>();
        service.call(method, param, callback);
        return callback.getResult();
    }

    public <T extends RPC> T call(String method, RPC param) throws Exception {
        Callback<T> callback = new Callback<T>();
        service.call(method, param, callback);
        return callback.getResult();
    }
    
    public <T extends RPC> T call(String method, String param) throws Exception {
        Callback<T> callback = new Callback<T>();
        service.call(method, param, callback);
        return callback.getResult();
    }

    public Request call(String method, Integer param, AsyncCallback<? extends RPC> callback) {
        return service.call(method, param, callback);
    }

    public Request call(String method, RPC param, AsyncCallback<? extends RPC> callback) {
        return service.call(method, param, callback);
    }

    public Request call(String method, String param, AsyncCallback<? extends RPC> callback) {
        return service.call(method, param, callback);
    }

    public <T extends RPC> Request callScreen(String method, T rpc, AsyncCallback<? extends RPC> callback) {
        return service.call(method, rpc, callback);
    }

    public Request getScreen(AsyncCallback<String> callback) {
        return null;
    }

    public <T extends RPC> T callScreen(String method, T rpc) throws Exception {
        return null;
    }

    public String getScreen() throws RPCException {
        return null;
    }
}
