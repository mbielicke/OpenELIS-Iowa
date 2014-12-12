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
package org.openelis.modules.attachment.client;

import java.util.ArrayList;

import org.openelis.domain.AttachmentDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class AttachmentService implements AttachmentServiceInt, AttachmentServiceIntAsync {
    /**
     * GWT.created service to make calls to the server
     */
    private AttachmentServiceIntAsync service;

    private static AttachmentService  instance;

    public static AttachmentService get() {
        if (instance == null)
            instance = new AttachmentService();

        return instance;
    }

    private AttachmentService() {
        service = (AttachmentServiceIntAsync)GWT.create(AttachmentServiceInt.class);
    }

    @Override
    public ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Callback<ArrayList<AttachmentManager>> callback;

        callback = new Callback<ArrayList<AttachmentManager>>();
        service.fetchByQuery(fields, first, max, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             AsyncCallback<ArrayList<AttachmentManager>> callback) {
        service.fetchByQuery(fields, first, max, callback);
    }

    @Override
    public ArrayList<AttachmentManager> fetchUnattachedByDescription(String description, int first, int max) throws Exception {
        Callback<ArrayList<AttachmentManager>> callback;

        callback = new Callback<ArrayList<AttachmentManager>>();
        service.fetchUnattachedByDescription(description, first, max, callback);
        return callback.getResult();
    }

    @Override
    public void fetchUnattachedByDescription(String description, int first, int max,
                                             AsyncCallback<ArrayList<AttachmentManager>> callback) {
        service.fetchUnattachedByDescription(description, first, max, callback);
    }

    @Override
    public AttachmentManager fetchForUpdate(Integer attachmentId) throws Exception {
        Callback<AttachmentManager> callback;

        callback = new Callback<AttachmentManager>();
        service.fetchForUpdate(attachmentId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer attachmentId, AsyncCallback<AttachmentManager> callback) {
        service.fetchForUpdate(attachmentId, callback);
    }
    
    @Override
    public ArrayList<AttachmentDO> query(Query query) throws Exception {
        Callback<ArrayList<AttachmentDO>> callback;

        callback = new Callback<ArrayList<AttachmentDO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<AttachmentDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public AttachmentManager getReserved(Integer attachmentId) throws Exception {
        Callback<AttachmentManager> callback;

        callback = new Callback<AttachmentManager>();
        service.getReserved(attachmentId, callback);
        return callback.getResult();
    }
    
    @Override
    public void getReserved(Integer attachmentId, AsyncCallback<AttachmentManager> callback) {
        service.getReserved(attachmentId, callback);
    }

    @Override
    public AttachmentManager update(AttachmentManager data) throws Exception {
        Callback<AttachmentManager> callback;

        callback = new Callback<AttachmentManager>();
        service.update(data, callback);
        return callback.getResult();
    }
    
    @Override
    public void update(AttachmentManager data, AsyncCallback<AttachmentManager> callback) {
        service.update(data, callback);
    }

    @Override
    public AttachmentManager unlock(Integer attachmentId) throws Exception {
        Callback<AttachmentManager> callback;

        callback = new Callback<AttachmentManager>();
        service.unlock(attachmentId, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(Integer attachmentId, AsyncCallback<AttachmentManager> callback) {
        service.unlock(attachmentId, callback);
    }

    @Override
    public ArrayList<AttachmentManager> put() throws Exception {
        Callback<ArrayList<AttachmentManager>> callback;

        callback = new Callback<ArrayList<AttachmentManager>>();
        service.put(callback);
        return callback.getResult();
    }
    
    @Override
    public void put(AsyncCallback<ArrayList<AttachmentManager>> callback) {
        service.put(callback);
    }

    @Override
    public ReportStatus get(Integer attachmentId) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.get(attachmentId, callback);
        return callback.getResult();
    }

    @Override
    public void get(Integer attachmentId, AsyncCallback<ReportStatus> callback) {
        service.get(attachmentId, callback);
    }
}