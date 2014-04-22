/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.patient.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

import org.openelis.domain.PatientDO;
import org.openelis.domain.PatientRelationVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

public class PatientService implements PatientServiceInt, PatientServiceIntAsync {
    
    static PatientService instance;
    
    PatientServiceIntAsync service;
    
    public static PatientService get() {
        if (instance == null)
            instance = new PatientService();
        
        return instance;
    }
    
    private PatientService() {
        service = (PatientServiceIntAsync)GWT.create(PatientServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public ArrayList<PatientRelationVO> fetchByRelatedPatientId(Integer patientId) throws Exception {
        Callback<ArrayList<PatientRelationVO>> callback;
        
        callback = new Callback<ArrayList<PatientRelationVO>>();
        service.fetchByRelatedPatientId(patientId, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByRelatedPatientId(Integer patientId, AsyncCallback<ArrayList<PatientRelationVO>> callback) {
        service.fetchByRelatedPatientId(patientId, callback);
    }

    @Override
    public PatientDO fetchForUpdate(Integer patientId) throws Exception {
        Callback<PatientDO> callback;
        
        callback = new Callback<PatientDO>();
        service.fetchForUpdate(patientId, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchForUpdate(Integer patientId, AsyncCallback<PatientDO> callback) {
        service.fetchForUpdate(patientId, callback);
    }
    
    @Override
    public ArrayList<PatientDO> query(Query query) throws Exception {
        Callback<ArrayList<PatientDO>> callback;
        
        callback = new Callback<ArrayList<PatientDO>>();
        service.query(query, callback);
        return callback.getResult();
    }
    
    @Override
    public void query(Query query, AsyncCallback<ArrayList<PatientDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public PatientDO add(PatientDO data) throws Exception {
        Callback<PatientDO> callback;
        
        callback = new Callback<PatientDO>();
        service.add(data, callback);
        return callback.getResult();
    }
    
    @Override
    public void add(PatientDO data, AsyncCallback<PatientDO> callback) {
        service.add(data, callback);
    }
    
    @Override
    public PatientDO update(PatientDO data) throws Exception {
        Callback<PatientDO> callback;
        
        callback = new Callback<PatientDO>();
        service.update(data, callback);
        return callback.getResult();
    }
    
    @Override
    public void update(PatientDO data, AsyncCallback<PatientDO> callback) {
        service.update(data, callback);
    }

    @Override
    public PatientDO abortUpdate(Integer patientId) throws Exception {
        Callback<PatientDO> callback;
        
        callback = new Callback<PatientDO>();
        service.abortUpdate(patientId, callback);
        return callback.getResult();
    }

    @Override
    public void abortUpdate(Integer patientId, AsyncCallback<PatientDO> callback) {
        service.abortUpdate(patientId, callback);
    }

    @Override
    public void validate(PatientDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.validate(data, callback);
        callback.getResult();
    }

    @Override
    public void validate(PatientDO data, AsyncCallback<Void> callback) {
        service.validate(data, callback);
    }
}