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
package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class WorksheetCreationService implements WorksheetCreationServiceInt,
                                                 WorksheetCreationServiceIntAsync {
    
    static WorksheetCreationService instance;
    
    WorksheetCreationServiceIntAsync service;
    
    public static WorksheetCreationService get() {
        if(instance == null)
            instance = new WorksheetCreationService();
        
        return instance;
    }
    
    private WorksheetCreationService() {
        service = (WorksheetCreationServiceIntAsync)GWT.create(WorksheetCreationServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getColumnNames(formatId, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<WorksheetCreationVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<WorksheetCreationVO> query(Query query) throws Exception {
        Callback<ArrayList<WorksheetCreationVO>> callback;
        
        callback = new Callback<ArrayList<WorksheetCreationVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getColumnNames(formatId, callback);
        return callback.getResult();
    }

}
