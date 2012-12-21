package org.openelis.modules.auxData.client;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AuxDataManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuxDataService implements AuxDataServiceInt, AuxDataServiceIntAsync{

    static AuxDataService instance;
    
    AuxDataServiceIntAsync service;
    
    public static AuxDataService get() {
        if(instance == null)
            instance = new AuxDataService();
        
        return instance;
    }
    
    private AuxDataService() {
        service = (AuxDataServiceIntAsync)GWT.create(AuxDataServiceInt.class);
    }

    @Override
    public void fetchById(AuxDataDO auxData, AsyncCallback<AuxDataManager> callback) {
        service.fetchById(auxData, callback);
    }

    @Override
    public void fetchByRefId(AuxDataDO auxData, AsyncCallback<ArrayList<AuxDataViewDO>> callback) {
        service.fetchByRefId(auxData, callback);
    }

    @Override
    public void getAuxGroupIdFromSystemVariable(String sysVariableKey, AsyncCallback<IdVO> callback) {
        service.getAuxGroupIdFromSystemVariable(sysVariableKey, callback);
    }

    @Override
    public AuxDataManager fetchById(AuxDataDO auxData) throws Exception {
        Callback<AuxDataManager> callback;
        
        callback = new Callback<AuxDataManager>();
        service.fetchById(auxData, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AuxDataViewDO> fetchByRefId(AuxDataDO auxData) throws Exception {
        Callback<ArrayList<AuxDataViewDO>> callback;
        
        callback = new Callback<ArrayList<AuxDataViewDO>>();
        service.fetchByRefId(auxData, callback);
        return callback.getResult();

    }

    @Override
    public IdVO getAuxGroupIdFromSystemVariable(String sysVariableKey) throws Exception {
        Callback<IdVO> callback;
        
        callback = new Callback<IdVO>();
        service.getAuxGroupIdFromSystemVariable(sysVariableKey, callback);
        return callback.getResult();

    }
    
    
    
    
}
