package org.openelis.modules.auxiliary.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuxiliaryService implements AuxiliaryServiceInt, AuxiliaryServiceIntAsync {
    
    static AuxiliaryService instance;
    
    AuxiliaryServiceIntAsync service;
    
    public static AuxiliaryService get() {
        if (instance == null)
            instance = new AuxiliaryService();
        
        return instance;
    }
    
    private AuxiliaryService() {
        service = (AuxiliaryServiceIntAsync)GWT.create(AuxiliaryServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<AuxFieldGroupManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(AuxFieldGroupManager man, AsyncCallback<AuxFieldGroupManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActive(AsyncCallback<ArrayList<AuxFieldGroupDO>> callback) {
        service.fetchActive(callback);
    }

    @Override
    public void fetchFieldByGroupId(Integer groupId, AsyncCallback<AuxFieldManager> callback) {
        service.fetchFieldByGroupId(groupId, callback);
    }

    @Override
    public void fetchFieldByGroupIdWithValues(Integer groupId,
                                              AsyncCallback<AuxFieldManager> callback) {
        service.fetchFieldByGroupIdWithValues(groupId, callback);
    }

    @Override
    public void fetchFieldById(Integer id, AsyncCallback<AuxFieldManager> callback) {
        service.fetchFieldById(id, callback);
    }

    @Override
    public void fetchFieldValueByFieldId(Integer fieldId,
                                         AsyncCallback<AuxFieldValueManager> callback) {
        service.fetchFieldValueByFieldId(fieldId, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<AuxFieldGroupManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchGroupById(Integer id, AsyncCallback<AuxFieldGroupManager> callback) {
        service.fetchGroupById(id, callback);
    }

    @Override
    public void fetchGroupByIdWithFields(Integer id, AsyncCallback<AuxFieldGroupManager> callback) {
        service.fetchGroupByIdWithFields(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(AuxFieldGroupManager man, AsyncCallback<AuxFieldGroupManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ArrayList<AuxFieldGroupDO> fetchActive() throws Exception {
        Callback<ArrayList<AuxFieldGroupDO>> callback;
        
        callback = new Callback<ArrayList<AuxFieldGroupDO>>();
        service.fetchActive(callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldGroupManager fetchGroupById(Integer id) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.fetchGroupById(id, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldGroupManager fetchGroupByIdWithFields(Integer id) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.fetchGroupByIdWithFields(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.add(man,callback);
        return callback.getResult();    }

    @Override
    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        Callback<AuxFieldGroupManager> callback;
        
        callback = new Callback<AuxFieldGroupManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldManager fetchFieldById(Integer id) throws Exception {
        Callback<AuxFieldManager> callback;
        
        callback = new Callback<AuxFieldManager>();
        service.fetchFieldById(id, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldManager fetchFieldByGroupId(Integer groupId) throws Exception {
        Callback<AuxFieldManager> callback;
        
        callback = new Callback<AuxFieldManager>();
        service.fetchFieldByGroupId(groupId, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldManager fetchFieldByGroupIdWithValues(Integer groupId) throws Exception {
        Callback<AuxFieldManager> callback;
        
        callback = new Callback<AuxFieldManager>();
        service.fetchFieldByGroupIdWithValues(groupId, callback);
        return callback.getResult();
    }

    @Override
    public AuxFieldValueManager fetchFieldValueByFieldId(Integer fieldId) throws Exception {
        Callback<AuxFieldValueManager> callback;
        
        callback = new Callback<AuxFieldValueManager>();
        service.fetchFieldValueByFieldId(fieldId, callback);
        return callback.getResult();
    }
    
    

}
