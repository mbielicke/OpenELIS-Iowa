package org.openelis.modules.qc.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcService implements QcServiceInt, QcServiceIntAsync {
    
    static QcService instance;
    
    QcServiceIntAsync service;
    
    public static QcService get() {
        if(instance == null)
            instance = new QcService();
        
        return instance;
    }
    
    private QcService() {
        service = (QcServiceIntAsync)GWT.create(QcServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<QcManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(QcManager man, AsyncCallback<QcManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<QcManager> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public void fetchActiveByName(Query query, AsyncCallback<ArrayList<QcLotViewDO>> callback) {
        service.fetchActiveByName(query, callback);
    }

    @Override
    public void fetchActiveByName(String search, AsyncCallback<ArrayList<QcLotViewDO>> callback) {
        service.fetchActiveByName(search, callback);
    }

    @Override
    public void fetchAnalyteByQcId(Integer id, AsyncCallback<QcAnalyteManager> callback) {
        service.fetchAnalyteByQcId(id, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<QcManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<QcDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<QcManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchLotById(Integer id, AsyncCallback<QcLotViewDO> callback) {
        service.fetchLotById(id, callback);
    }

    @Override
    public void fetchLotByQcId(Integer id, AsyncCallback<QcLotManager> callback) {
        service.fetchLotByQcId(id, callback);
    }

    @Override
    public void fetchWithAnalytes(Integer id, AsyncCallback<QcManager> callback) {
        service.fetchWithAnalytes(id, callback);
    }

    @Override
    public void fetchWithLots(Integer id, AsyncCallback<QcManager> callback) {
        service.fetchWithLots(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(QcManager man, AsyncCallback<QcManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void validateForDelete(QcLotViewDO data, AsyncCallback<QcLotDO> callback) {
        service.validateForDelete(data, callback);
    }

    @Override
    public QcManager fetchById(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<QcDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<QcDO>> callback;
        
        callback = new Callback<ArrayList<QcDO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<QcLotViewDO> fetchActiveByName(Query query) throws Exception {
        Callback<ArrayList<QcLotViewDO>> callback;
        
        callback = new Callback<ArrayList<QcLotViewDO>>();
        service.fetchActiveByName(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<QcLotViewDO> fetchActiveByName(String search) throws Exception {
        Callback<ArrayList<QcLotViewDO>> callback;
        
        callback = new Callback<ArrayList<QcLotViewDO>>();
        service.fetchActiveByName(search, callback);
        return callback.getResult();
    }

    @Override
    public QcManager fetchWithAnalytes(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.fetchWithAnalytes(id, callback);
        return callback.getResult();
    }

    @Override
    public QcManager fetchWithLots(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.fetchWithLots(id, callback);
        return callback.getResult();
    }

    @Override
    public QcLotViewDO fetchLotById(Integer id) throws Exception {
        Callback<QcLotViewDO> callback;
        
        callback = new Callback<QcLotViewDO>();
        service.fetchLotById(id, callback);
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
    public QcManager add(QcManager man) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public QcManager update(QcManager man) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public QcManager fetchForUpdate(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public QcManager abortUpdate(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public QcManager duplicate(Integer id) throws Exception {
        Callback<QcManager> callback;
        
        callback = new Callback<QcManager>();
        service.duplicate(id, callback);
        return callback.getResult();
    }

    @Override
    public QcAnalyteManager fetchAnalyteByQcId(Integer id) throws Exception {
        Callback<QcAnalyteManager> callback;
        
        callback = new Callback<QcAnalyteManager>();
        service.fetchAnalyteByQcId(id, callback);
        return callback.getResult();
    }

    @Override
    public QcLotManager fetchLotByQcId(Integer id) throws Exception {
        Callback<QcLotManager> callback;
        
        callback = new Callback<QcLotManager>();
        service.fetchLotByQcId(id, callback);
        return callback.getResult();
    }

    @Override
    public QcLotDO validateForDelete(QcLotViewDO data) throws Exception {
        Callback<QcLotDO> callback;
        
        callback = new Callback<QcLotDO>();
        service.validateForDelete(data, callback);
        return callback.getResult();
    }

}
