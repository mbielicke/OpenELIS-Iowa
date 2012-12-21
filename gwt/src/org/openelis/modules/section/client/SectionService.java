package org.openelis.modules.section.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SectionManager;
import org.openelis.manager.SectionParameterManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SectionService implements SectionServiceInt, SectionServiceIntAsync {
    
    static SectionService instance;
    
    SectionServiceIntAsync service;
    
    public static SectionService get() {
        if(instance == null)
            instance = new SectionService();
        
        return instance;
    }
    
    private SectionService() {
        service = (SectionServiceIntAsync)GWT.create(SectionServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<SectionManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(SectionManager man, AsyncCallback<SectionManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<SectionManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<SectionDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<SectionManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchParameterBySectionId(Integer id,
                                          AsyncCallback<SectionParameterManager> callback) {
        service.fetchParameterBySectionId(id, callback);
    }

    @Override
    public void fetchWithParameters(Integer id, AsyncCallback<SectionManager> callback) {
        service.fetchWithParameters(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(SectionManager man, AsyncCallback<SectionManager> callback) {
        service.update(man, callback);
    }

    @Override
    public SectionManager fetchById(Integer id) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SectionDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<SectionDO>> callback;
        
        callback = new Callback<ArrayList<SectionDO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }

    @Override
    public SectionManager fetchWithParameters(Integer id) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.fetchWithParameters(id, callback);
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
    public SectionManager add(SectionManager man) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.add(man, callback);
        return callback.getResult();    }

    @Override
    public SectionManager update(SectionManager man) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public SectionManager fetchForUpdate(Integer id) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public SectionManager abortUpdate(Integer id) throws Exception {
        Callback<SectionManager> callback;
        
        callback = new Callback<SectionManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public SectionParameterManager fetchParameterBySectionId(Integer id) throws Exception {
        Callback<SectionParameterManager> callback;
        
        callback = new Callback<SectionParameterManager>();
        service.fetchParameterBySectionId(id, callback);
        return callback.getResult();
    }

}
