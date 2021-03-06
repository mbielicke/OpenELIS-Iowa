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
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

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
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
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

    @Override
    public ReportStatus getTRF(Integer sampleId) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.getTRF(sampleId, callback);
        return callback.getResult();
    }
    
    @Override
    public void getTRF(Integer sampleId, AsyncCallback<ReportStatus> callback) {
        service.getTRF(sampleId, callback);
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
    public ArrayList<AttachmentManager> fetchByQueryUnattached(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        Callback<ArrayList<AttachmentManager>> callback;

        callback = new Callback<ArrayList<AttachmentManager>>();
        service.fetchByQueryUnattached(fields, first, max, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchByQueryUnattached(ArrayList<QueryData> fields, int first, int max,
                                       AsyncCallback<ArrayList<AttachmentManager>> callback) {
        service.fetchByQueryUnattached(fields, first, max, callback);
    }
    
    @Override
    public ArrayList<AttachmentManager> fetchByQueryDescending(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        Callback<ArrayList<AttachmentManager>> callback;

        callback = new Callback<ArrayList<AttachmentManager>>();
        service.fetchByQueryDescending(fields, first, max, callback);
        return callback.getResult();
    }

    
    @Override
    public void fetchByQueryDescending(ArrayList<QueryData> fields, int first, int max,
                                       AsyncCallback<ArrayList<AttachmentManager>> callback) {
        service.fetchByQueryDescending(fields, first, max, callback);
    }
    
    @Override
    public ArrayList<AttachmentIssueViewDO> fetchIssues() throws Exception {
        Callback<ArrayList<AttachmentIssueViewDO>> callback;

        callback = new Callback<ArrayList<AttachmentIssueViewDO>>();
        service.fetchIssues(callback);
        return callback.getResult();
    }

    @Override
    public void fetchIssues(AsyncCallback<ArrayList<AttachmentIssueViewDO>> callback) {
        service.fetchIssues(callback);
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
    public AttachmentManager fetchForReserve(Integer attachmentId) throws Exception {
        Callback<AttachmentManager> callback;

        callback = new Callback<AttachmentManager>();
        service.fetchForReserve(attachmentId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForReserve(Integer attachmentId, AsyncCallback<AttachmentManager> callback) {
        service.fetchForReserve(attachmentId, callback);
    }
    
    @Override
    public AttachmentIssueViewDO fetchIssueForUpdate(Integer attachmentId) throws Exception {
        Callback<AttachmentIssueViewDO> callback;

        callback = new Callback<AttachmentIssueViewDO>();
        service.fetchIssueForUpdate(attachmentId, callback);
        return callback.getResult();
    }
    
    @Override
    public void fetchIssueForUpdate(Integer attachmentId,
                                    AsyncCallback<AttachmentIssueViewDO> callback) {
        service.fetchIssueForUpdate(attachmentId, callback);
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
    public AttachmentIssueViewDO addIssue(AttachmentIssueViewDO data) throws Exception {
        Callback<AttachmentIssueViewDO> callback;

        callback = new Callback<AttachmentIssueViewDO>();
        service.addIssue(data, callback);
        return callback.getResult();
    }

    @Override
    public void addIssue(AttachmentIssueViewDO data, AsyncCallback<AttachmentIssueViewDO> callback) {
        service.addIssue(data, callback);
    }

    @Override
    public AttachmentIssueViewDO updateIssue(AttachmentIssueViewDO data) throws Exception {
        Callback<AttachmentIssueViewDO> callback;

        callback = new Callback<AttachmentIssueViewDO>();
        service.updateIssue(data, callback);
        return callback.getResult();
    }

    @Override
    public void updateIssue(AttachmentIssueViewDO data,
                            AsyncCallback<AttachmentIssueViewDO> callback) {
        service.updateIssue(data, callback);
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
    public AttachmentIssueViewDO unlockIssue(Integer attachmentId) throws Exception {
        Callback<AttachmentIssueViewDO> callback;

        callback = new Callback<AttachmentIssueViewDO>();
        service.unlockIssue(attachmentId, callback);
        return callback.getResult();
    }

    @Override
    public void unlockIssue(Integer attachmentId, AsyncCallback<AttachmentIssueViewDO> callback) {
        service.unlockIssue(attachmentId, callback);
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
    public void delete(ArrayList<AttachmentManager> ams) throws Exception {
        Callback<Void> callback;

        callback = new Callback<Void>();
        service.delete(ams, callback);
    }

    @Override
    public void delete(ArrayList<AttachmentManager> ams, AsyncCallback<Void> callback) {
        service.delete(ams, callback);
    }

    @Override
    public void deleteIssue(AttachmentIssueViewDO data) throws Exception {
        Callback<Void> callback;

        callback = new Callback<Void>();
        service.deleteIssue(data, callback);
    }

    @Override
    public void deleteIssue(AttachmentIssueViewDO data, AsyncCallback<Void> callback) {
        service.deleteIssue(data, callback);
    }
}