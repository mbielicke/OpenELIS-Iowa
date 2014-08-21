package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrganizationService implements OrganizationServiceInt, OrganizationServiceIntAsync{
    
    static OrganizationService instance;
    
    private OrganizationServiceIntAsync service;
    
    public static OrganizationService get() {
        if (instance == null)
            instance = new OrganizationService();
        return instance;
    }
    
    private OrganizationService() {
        service = (OrganizationServiceIntAsync)GWT.create(OrganizationServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(OrganizationManager man, AsyncCallback<OrganizationManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByIdList(Query query, AsyncCallback<ArrayList<OrganizationManager>> callback) {
        service.fetchByIdList(query, callback);
    }

    @Override
    public void fetchByIdOrName(String search, AsyncCallback<ArrayList<OrganizationDO>> callback) {
        service.fetchByIdOrName(search, callback);
    }

    @Override
    public void fetchContactByOrganizationId(Integer id,
                                             AsyncCallback<OrganizationContactManager> callback) {
        service.fetchContactByOrganizationId(id, callback);
        
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchParameterByOrganizationId(Integer id,
                                               AsyncCallback<OrganizationParameterManager> callback) {
        service.fetchParameterByOrganizationId(id, callback);
    }

    @Override
    public void fetchParametersByDictionarySystemName(String systemName,
                                                      AsyncCallback<ArrayList<OrganizationParameterDO>> callback) {
        service.fetchParametersByDictionarySystemName(systemName, callback);
        
    }

    @Override
    public void fetchWithContacts(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.fetchWithContacts(id, callback);
        
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void fetchWithParameters(Integer id, AsyncCallback<OrganizationManager> callback) {
        service.fetchWithParameters(id, callback);
        
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(OrganizationManager man, AsyncCallback<OrganizationManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void updateForNotify(OrganizationManager man, AsyncCallback<OrganizationManager> callback) {
        service.updateForNotify(man, callback);
    }

    @Override
    public OrganizationManager fetchById(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<OrganizationManager> fetchByIdList(Query query) throws Exception {
        Callback<ArrayList<OrganizationManager>> callback;
        
        callback = new Callback<ArrayList<OrganizationManager>>();
        service.fetchByIdList(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<OrganizationDO> fetchByIdOrName(String search) throws Exception {
        Callback<ArrayList<OrganizationDO>> callback;
        
        callback = new Callback<ArrayList<OrganizationDO>>();
        service.fetchByIdOrName(search, callback);
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
    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.fetchWithContacts(id, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager fetchWithParameters(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
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
    public OrganizationManager add(OrganizationManager man) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager update(OrganizationManager man) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager updateForNotify(OrganizationManager man) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.updateForNotify(man, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager fetchForUpdate(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationManager abortUpdate(Integer id) throws Exception {
        Callback<OrganizationManager> callback;
        
        callback = new Callback<OrganizationManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception {
        Callback<OrganizationContactManager> callback;
        
        callback = new Callback<OrganizationContactManager>();
        service.fetchContactByOrganizationId(id, callback);
        return callback.getResult();
    }

    @Override
    public OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception {
        Callback<OrganizationParameterManager> callback;
        
        callback = new Callback<OrganizationParameterManager>();
        service.fetchParameterByOrganizationId(id, callback);
        return callback.getResult();
    }

}
