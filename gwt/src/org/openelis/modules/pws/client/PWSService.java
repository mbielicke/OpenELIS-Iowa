package org.openelis.modules.pws.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PWSService implements PWSServiceInt, PWSServiceIntAsync {
    
    static PWSService instance;
    
    PWSServiceIntAsync service;
    
    public static PWSService get() {
        if(instance == null)
            instance = new PWSService();
        
        return instance;
    }
    
    private PWSService() {
        service = (PWSServiceIntAsync)GWT.create(PWSServiceInt.class);
    }

    @Override
    public void fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber,
                                              AsyncCallback<PWSAddressManager> callback) {
        service.fetchAddressByTinwsysIsNumber(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchByTinwsysIsNumber(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback) {
        service.fetchByTinwsysIsNumber(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber,
                                               AsyncCallback<PWSFacilityManager> callback) {
        service.fetchFacilityByTinwsysIsNumber(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber,
                                              AsyncCallback<PWSMonitorManager> callback) {
        service.fetchMonitorByTinwsysIsNumber(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchPwsByNumber0(String pwsNumber0, AsyncCallback<PWSDO> callback) {
        service.fetchPwsByNumber0(pwsNumber0, callback);
    }

    @Override
    public void fetchWithAddresses(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback) {
        service.fetchWithAddresses(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchWithFacilities(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback) {
        service.fetchWithFacilities(tinwsysIsNumber, callback);
    }

    @Override
    public void fetchWithMonitors(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback) {
        service.fetchWithMonitors(tinwsysIsNumber, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSManager> callback;
        
        callback = new Callback<PWSManager>();
        service.fetchByTinwsysIsNumber(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSManager> callback;
        
        callback = new Callback<PWSManager>();
        service.fetchWithFacilities(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSManager> callback;
        
        callback = new Callback<PWSManager>();
        service.fetchWithAddresses(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSManager> callback;
        
        callback = new Callback<PWSManager>();
        service.fetchWithMonitors(tinwsysIsNumber, callback);
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
    public PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSFacilityManager> callback;
        
        callback = new Callback<PWSFacilityManager>();
        service.fetchFacilityByTinwsysIsNumber(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSAddressManager> callback;
        
        callback = new Callback<PWSAddressManager>();
        service.fetchAddressByTinwsysIsNumber(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Callback<PWSMonitorManager> callback;
        
        callback = new Callback<PWSMonitorManager>();
        service.fetchMonitorByTinwsysIsNumber(tinwsysIsNumber, callback);
        return callback.getResult();
    }

    @Override
    public PWSDO fetchPwsByNumber0(String pwsNumber0) throws Exception {
        Callback<PWSDO> callback;
        
        callback = new Callback<PWSDO>();
        service.fetchPwsByNumber0(pwsNumber0, callback);
        return callback.getResult();
    }

}
