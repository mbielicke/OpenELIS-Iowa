package org.openelis.modules.eorder.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

import org.openelis.domain.EOrderDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.services.TokenService;

public class EOrderService implements EOrderServiceInt, EOrderServiceIntAsync {

    static EOrderService instance;
    
    EOrderServiceIntAsync service;
    
    public static EOrderService get() {
        if (instance == null)
            instance = new EOrderService();
        
        return instance;
    }
    
    private EOrderService() {
        service = (EOrderServiceIntAsync)GWT.create(EOrderServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<EOrderDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByPaperOrderValidator(String pov, AsyncCallback<ArrayList<EOrderDO>> callback) {
        service.fetchByPaperOrderValidator(pov, callback);
    }

    @Override
    public EOrderDO fetchById(Integer id) throws Exception {
        Callback<EOrderDO> callback;
        
        callback = new Callback<EOrderDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<EOrderDO> fetchByPaperOrderValidator(String pov) throws Exception {
        Callback<ArrayList<EOrderDO>> callback;
        
        callback = new Callback<ArrayList<EOrderDO>>();
        service.fetchByPaperOrderValidator(pov, callback);
        return callback.getResult();
    }
}