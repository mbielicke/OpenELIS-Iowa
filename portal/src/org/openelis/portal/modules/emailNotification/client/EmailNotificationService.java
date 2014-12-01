package org.openelis.portal.modules.emailNotification.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class EmailNotificationService implements EmailNotificationServiceInt,
                                     EmailNotificationServiceIntAsync {

    private static EmailNotificationService  instance;

    private EmailNotificationServiceIntAsync service;

    public static EmailNotificationService get() {
        if (instance == null)
            instance = new EmailNotificationService();

        return instance;
    }

    private EmailNotificationService() {
        service = (EmailNotificationServiceIntAsync)GWT.create(EmailNotificationServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchByIds(ArrayList<Integer> ids,
                           AsyncCallback<ArrayList<OrganizationViewDO>> callback) {
        service.fetchByIds(ids, callback);
    }

    @Override
    public void fetchParametersByDictionarySystemName(String systemName,
                                                      AsyncCallback<ArrayList<OrganizationParameterDO>> callback) {
        service.fetchParametersByDictionarySystemName(systemName, callback);

    }

    @Override
    public void fetchParametersByOrganizationId(Integer id,
                                                AsyncCallback<ArrayList<OrganizationParameterDO>> callback) {
        service.fetchParametersByOrganizationId(id, callback);

    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void updateForNotify(ArrayList<OrganizationParameterDO> parameters,
                                AsyncCallback<ArrayList<OrganizationParameterDO>> callback) {
        service.updateForNotify(parameters, callback);
    }

    @Override
    public ArrayList<OrganizationViewDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        Callback<ArrayList<OrganizationViewDO>> callback;

        callback = new Callback<ArrayList<OrganizationViewDO>>();
        service.fetchByIds(ids, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<OrganizationParameterDO> fetchParametersByDictionarySystemName(String systemName) throws Exception {
        Callback<ArrayList<OrganizationParameterDO>> callback;

        callback = new Callback<ArrayList<OrganizationParameterDO>>();
        service.fetchParametersByDictionarySystemName(systemName, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<OrganizationParameterDO> fetchParametersByOrganizationId(Integer id) throws Exception {
        Callback<ArrayList<OrganizationParameterDO>> callback;

        callback = new Callback<ArrayList<OrganizationParameterDO>>();
        service.fetchParametersByOrganizationId(id, callback);
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
    public ArrayList<OrganizationParameterDO> updateForNotify(ArrayList<OrganizationParameterDO> parameters) throws Exception {
        Callback<ArrayList<OrganizationParameterDO>> callback;

        callback = new Callback<ArrayList<OrganizationParameterDO>>();
        service.updateForNotify(parameters, callback);
        return callback.getResult();
    }
}
