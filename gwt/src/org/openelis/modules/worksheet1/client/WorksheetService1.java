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
package org.openelis.modules.worksheet1.client;

import java.util.ArrayList;

import org.openelis.domain.IdVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetService1 implements WorksheetServiceInt1, WorksheetServiceInt1Async {
    
    static WorksheetService1 instance;
    
    WorksheetServiceInt1Async service;
    
    public static WorksheetService1 get() {
        if (instance == null)
            instance = new WorksheetService1();
        
        return instance;
    }
    
    private WorksheetService1() {
        service = (WorksheetServiceInt1Async)GWT.create(WorksheetServiceInt1.class);
    }

    @Override
    public WorksheetManager1 getInstance() throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.getInstance(callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(AsyncCallback<WorksheetManager1> callback) {
        service.getInstance(callback);
    }
    
    @Override
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        Callback<WorksheetManager1> callback;
        
        callback = new Callback<WorksheetManager1>();
        service.fetchById(worksheetId, elements, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchById(Integer worksheetId, WorksheetManager1.Load elements[], AsyncCallback<WorksheetManager1> callback) {
        service.fetchById(worksheetId, elements, callback);
    }

    @Override
    public ArrayList<IdVO> fetchByQuery(Query query) throws Exception {
        Callback<ArrayList<IdVO>> callback;
        
        callback = new Callback<ArrayList<IdVO>>();
        service.fetchByQuery(query, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByQuery(Query query, AsyncCallback<ArrayList<IdVO>> callback) {
        service.fetchByQuery(query, callback);
    }

    @Override
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.fetchForUpdate(worksheetId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer worksheetId, AsyncCallback<WorksheetManager1> callback) {
        service.fetchForUpdate(worksheetId, callback);
    }
}
