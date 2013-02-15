package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleService implements SampleServiceInt, SampleServiceIntAsync {
    
    static SampleService instance;
    
    SampleServiceIntAsync service;
    
    public static SampleService get() {
        if(instance == null)
            instance = new SampleService();
        
        return instance;
    }
    
    private SampleService() {
        service = (SampleServiceIntAsync)GWT.create(SampleServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer sampleId, AsyncCallback<SampleManager> callback) {
        service.abortUpdate(sampleId, callback);
    }

    @Override
    public void add(SampleManager man, AsyncCallback<SampleManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchByAccessionNumber(Integer accessionNumber,
                                       AsyncCallback<SampleManager> callback) {
        service.fetchByAccessionNumber(accessionNumber, callback);
    }

    @Override
    public void fetchById(Integer sampleId, AsyncCallback<SampleManager> callback) {
        service.fetchById(sampleId, callback);
    }

    @Override
    public void fetchBySampleId(Integer sampleId, AsyncCallback<SampleQaEventManager> callback) {
        service.fetchBySampleId(sampleId, callback);
    }

    @Override
    public void fetchForUpdate(Integer sampleId, AsyncCallback<SampleManager> callback) {
        service.fetchForUpdate(sampleId, callback);
    }

    @Override
    public void fetchPwsByPwsId(String number0, AsyncCallback<PWSDO> callback) {
        service.fetchPwsByPwsId(number0, callback);
    }

    @Override
    public void fetchSampleItemsBySampleId(Integer sampleId,
                                           AsyncCallback<SampleItemManager> callback) {
        service.fetchSampleItemsBySampleId(sampleId, callback);
    }

    @Override
    public void fetchSampleOrganizationsBySampleId(Integer sampleId,
                                                   AsyncCallback<SampleOrganizationManager> callback) {
        service.fetchSampleOrganizationsBySampleId(sampleId, callback);
    }

    @Override
    public void fetchSampleprojectsBySampleId(Integer sampleId,
                                              AsyncCallback<SampleProjectManager> callback) {
        service.fetchSampleprojectsBySampleId(sampleId, callback);
    }

    @Override
    public void fetchWithAllDataByAccessionNumber(Integer accessionNumber,
                                                  AsyncCallback<SampleManager> callback) {
        service.fetchWithAllDataByAccessionNumber(accessionNumber, callback);
    }

    @Override
    public void fetchWithAllDataById(Integer sampleId, AsyncCallback<SampleManager> callback) {
        service.fetchWithAllDataById(sampleId, callback);
    }

    @Override
    public void fetchWithItemsAnalyses(Integer sampleId, AsyncCallback<SampleManager> callback) {
        service.fetchWithItemsAnalyses(sampleId, callback);
    }

    @Override
    public void getNewAccessionNumber(AsyncCallback<Integer> callback) {
        service.getNewAccessionNumber(callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdAccessionVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(SampleManager man, AsyncCallback<SampleManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void validateAccessionNumber(SampleDO sampleDO, AsyncCallback<SampleManager> callback) {
        service.validateAccessionNumber(sampleDO, callback);
    }

    @Override
    public SampleManager fetchById(Integer sampleId) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchById(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchByAccessionNumber(accessionNumber, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchWithItemsAnalyses(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchWithAllDataById(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchWithAllDataByAccessionNumber(accessionNumber, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdAccessionVO> query(Query query) throws Exception {
        Callback<ArrayList<IdAccessionVO>> callback;
        
        callback = new Callback<ArrayList<IdAccessionVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager add(SampleManager man) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager update(SampleManager man) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.fetchForUpdate(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.abortUpdate(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId) throws Exception {
        Callback<SampleOrganizationManager> callback;
        
        callback = new Callback<SampleOrganizationManager>();
        service.fetchSampleOrganizationsBySampleId(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        Callback<SampleProjectManager> callback;
        
        callback = new Callback<SampleProjectManager>();
        service.fetchSampleprojectsBySampleId(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        Callback<SampleItemManager> callback;
        
        callback = new Callback<SampleItemManager>();
        service.fetchSampleItemsBySampleId(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        Callback<SampleQaEventManager> callback;
        
        callback = new Callback<SampleQaEventManager>();
        service.fetchBySampleId(sampleId, callback);
        return callback.getResult();
    }

    @Override
    public SampleManager validateAccessionNumber(SampleDO sampleDO) throws Exception {
        Callback<SampleManager> callback;
        
        callback = new Callback<SampleManager>();
        service.validateAccessionNumber(sampleDO, callback);
        return callback.getResult();
    }

    @Override
    public Integer getNewAccessionNumber() throws Exception {
        Callback<Integer> callback;
        
        callback = new Callback<Integer>();
        service.getNewAccessionNumber(callback);
        return callback.getResult();
    }

    @Override
    public PWSDO fetchPwsByPwsId(String number0) throws Exception {
        Callback<PWSDO> callback;
        
        callback = new Callback<PWSDO>();
        service.fetchPwsByPwsId(number0, callback);
        return callback.getResult();
    }

}
