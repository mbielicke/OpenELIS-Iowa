package org.openelis.modules.provider1.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ProviderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class ProviderService1 implements ProviderServiceInt1, ProviderServiceInt1Async {

    static ProviderService1  instance;

    ProviderServiceInt1Async service;

    public static ProviderService1 get() {
        if (instance == null)
            instance = new ProviderService1();

        return instance;
    }

    private ProviderService1() {
        service = (ProviderServiceInt1Async)GWT.create(ProviderServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public ProviderManager1 getInstance() throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.getInstance(callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(AsyncCallback<ProviderManager1> callback) {
        service.getInstance(callback);
    }

    @Override
    public ProviderManager1 fetchById(Integer id) throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ProviderManager1> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public ArrayList<ProviderManager1> fetchByIds(ArrayList<Integer> ids) throws Exception {
        Callback<ArrayList<ProviderManager1>> callback;

        callback = new Callback<ArrayList<ProviderManager1>>();
        service.fetchByIds(ids, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByIds(ArrayList<Integer> ids,
                           AsyncCallback<ArrayList<ProviderManager1>> callback) {
        service.fetchByIds(ids, callback);
    }

    @Override
    public void fetchByLastNameNpiExternalId(String search,
                                             AsyncCallback<ArrayList<ProviderDO>> callback) {
        service.fetchByLastNameNpiExternalId(search, callback);
    }

    @Override
    public ArrayList<ProviderDO> fetchByLastNameNpiExternalId(String search) throws Exception {
        Callback<ArrayList<ProviderDO>> callback;

        callback = new Callback<ArrayList<ProviderDO>>();
        service.fetchByLastNameNpiExternalId(search, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdFirstLastNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdFirstLastNameVO>> callback;

        callback = new Callback<ArrayList<IdFirstLastNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdFirstLastNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<ProviderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Callback<ArrayList<ProviderManager1>> callback;

        callback = new Callback<ArrayList<ProviderManager1>>();
        service.fetchByQuery(fields, first, max, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             AsyncCallback<ArrayList<ProviderManager1>> callback) {
        service.fetchByQuery(fields, first, max, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ProviderManager1> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public ProviderManager1 fetchForUpdate(Integer id) throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(ArrayList<Integer> providerIds,
                               AsyncCallback<ArrayList<ProviderManager1>> callback) {
        service.fetchForUpdate(providerIds, callback);
    }

    @Override
    public ArrayList<ProviderManager1> fetchForUpdate(ArrayList<Integer> providerIds) throws Exception {
        Callback<ArrayList<ProviderManager1>> callback;

        callback = new Callback<ArrayList<ProviderManager1>>();
        service.fetchForUpdate(providerIds, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(Integer providerId, AsyncCallback<ProviderManager1> callback) {
        service.unlock(providerId, callback);
    }

    @Override
    public ProviderManager1 unlock(Integer providerId) throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.unlock(providerId, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(ArrayList<Integer> providerIds,
                       AsyncCallback<ArrayList<ProviderManager1>> callback) {
        service.unlock(providerIds, callback);
    }

    @Override
    public ArrayList<ProviderManager1> unlock(ArrayList<Integer> providerIds) throws Exception {
        Callback<ArrayList<ProviderManager1>> callback;

        callback = new Callback<ArrayList<ProviderManager1>>();
        service.unlock(providerIds, callback);
        return callback.getResult();
    }

    @Override
    public ProviderManager1 update(ProviderManager1 man) throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public void update(ProviderManager1 man, AsyncCallback<ProviderManager1> callback) {
        service.update(man, callback);
    }

    @Override
    public ProviderManager1 update(ProviderManager1 man, boolean ignoreWarnings) throws Exception {
        Callback<ProviderManager1> callback;

        callback = new Callback<ProviderManager1>();
        service.update(man, ignoreWarnings, callback);
        return callback.getResult();
    }

    @Override
    public void update(ProviderManager1 man, boolean ignoreWarnings,
                       AsyncCallback<ProviderManager1> callback) {
        service.update(man, ignoreWarnings, callback);
    }

}