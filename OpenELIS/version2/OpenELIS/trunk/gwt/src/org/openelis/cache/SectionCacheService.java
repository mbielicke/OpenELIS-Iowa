package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

class SectionCacheService implements SectionCacheServiceInt, SectionCacheServiceIntAsync {

    private static SectionCacheService  instance;

    private SectionCacheServiceIntAsync service;

    private SectionCacheService() {
        service = (SectionCacheServiceIntAsync)GWT.create(SectionCacheServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    static SectionCacheService get() {
        if (instance == null)
            instance = new SectionCacheService();

        return instance;
    }

    @Override
    public void getList(String name, AsyncCallback<ArrayList<SectionViewDO>> callback) {
        service.getList(name, callback);
    }

    @Override
    public ArrayList<SectionViewDO> getList(String name) throws Exception {
        Callback<ArrayList<SectionViewDO>> callback;

        callback = new Callback<ArrayList<SectionViewDO>>();
        service.getList(name, callback);
        return callback.getResult();
    }
}
