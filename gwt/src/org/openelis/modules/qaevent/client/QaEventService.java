package org.openelis.modules.qaevent.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QaEventService implements QAEventServiceInt, QAEventServiceIntAsync {
    
    static QaEventService instance;
    
    QAEventServiceIntAsync service;
    
    public static QaEventService get() {
        if(instance == null)
            instance = new QaEventService();
        
        return instance;
    }
    
    private QaEventService() {
        service = (QAEventServiceIntAsync)GWT.create(QAEventServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<QaEventViewDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(QaEventViewDO data, AsyncCallback<QaEventViewDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void fetchByCommon(AsyncCallback<ArrayList<QaEventDO>> callback) {
        service.fetchByCommon(callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<QaEventViewDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByTestId(Integer id, AsyncCallback<ArrayList<QaEventDO>> callback) {
        service.fetchByTestId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<QaEventViewDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(QaEventViewDO data, AsyncCallback<QaEventViewDO> callback) {
        service.update(data, callback);
    }

    @Override
    public QaEventViewDO fetchById(Integer id) throws Exception {
        Callback<QaEventViewDO> callback;
        
        callback = new Callback<QaEventViewDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<QaEventDO> fetchByTestId(Integer id) throws Exception {
        Callback<ArrayList<QaEventDO>> callback;
        
        callback = new Callback<ArrayList<QaEventDO>>();
        service.fetchByTestId(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<QaEventDO> fetchByCommon() throws Exception {
        Callback<ArrayList<QaEventDO>> callback;
        
        callback = new Callback<ArrayList<QaEventDO>>();
        service.fetchByCommon(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();    }

    @Override
    public QaEventViewDO add(QaEventViewDO data) throws Exception {
        Callback<QaEventViewDO> callback;
        
        callback = new Callback<QaEventViewDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public QaEventViewDO update(QaEventViewDO data) throws Exception {
        Callback<QaEventViewDO> callback;
        
        callback = new Callback<QaEventViewDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public QaEventViewDO fetchForUpdate(Integer id) throws Exception {
        Callback<QaEventViewDO> callback;
        
        callback = new Callback<QaEventViewDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public QaEventViewDO abortUpdate(Integer id) throws Exception {
        Callback<QaEventViewDO> callback;
        
        callback = new Callback<QaEventViewDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
