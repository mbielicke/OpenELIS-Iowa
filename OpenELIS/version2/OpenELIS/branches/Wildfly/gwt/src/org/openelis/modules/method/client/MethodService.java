/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.method.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class MethodService implements MethodServiceInt, MethodServiceIntAsync {
    
    static MethodService instance;
    
    MethodServiceIntAsync service;
    
    public static MethodService get() {
        if(instance == null)
            instance = new MethodService();
        
        return instance;
    }
    
    private MethodService() {
        service = (MethodServiceIntAsync)GWT.create(MethodServiceInt.class);
        if(System.getProperty("mock") == null)
            ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }
    
    @Override
    public void abortUpdate(Integer id, AsyncCallback<MethodDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(MethodDO data, AsyncCallback<MethodDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<MethodDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<MethodDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<MethodDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(MethodDO data, AsyncCallback<MethodDO> callback) {
        service.update(data, callback);
    }

    @Override
    public ArrayList<MethodDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<MethodDO>> callback;
        
        callback = new Callback<ArrayList<MethodDO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO fetchById(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.fetchById(id, callback);
        return callback.getResult();    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO add(MethodDO data) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO update(MethodDO data) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO fetchForUpdate(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public MethodDO abortUpdate(Integer id) throws Exception {
        Callback<MethodDO> callback;
        
        callback = new Callback<MethodDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
