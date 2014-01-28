/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetManager1;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class WorksheetCompletionService implements WorksheetCompletionServiceInt,
                                                   WorksheetCompletionServiceIntAsync {
    
    static WorksheetCompletionService instance;
    
    WorksheetCompletionServiceIntAsync service;
    
    public static WorksheetCompletionService get() {
        if(instance == null)
            instance = new WorksheetCompletionService();
        
        return instance;
    }
    
    private WorksheetCompletionService() {
        service = (WorksheetCompletionServiceIntAsync)GWT.create(WorksheetCompletionServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getHeaderLabelsForScreen(WorksheetManager manager,
                                         AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getHeaderLabelsForScreen(manager, callback);
    }

    @Override
    public void getHeaderLabelsForScreen(WorksheetManager1 manager,
                                         AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getHeaderLabelsForScreen(manager, callback);
    }

    @Override
    public void loadFromEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.loadFromEdit(manager, callback);
    }

    @Override
    public void saveForEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.saveForEdit(manager, callback);
    }

    @Override
    public WorksheetManager saveForEdit(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.saveForEdit(manager, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.loadFromEdit(manager, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager manager) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getHeaderLabelsForScreen(manager, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager1 manager) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getHeaderLabelsForScreen(manager, callback);
        return callback.getResult();
    }
}