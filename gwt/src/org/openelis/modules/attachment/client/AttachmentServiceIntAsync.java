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
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ScreenServiceIntAsync is the Asynchronous version of the ScreenServiceInt
 * interface.
 */
public interface AttachmentServiceIntAsync {

    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             AsyncCallback<ArrayList<AttachmentManager>> callback);
    
    public void fetchByQueryUnattached(ArrayList<QueryData> fields, int first, int max,
                                         AsyncCallback<ArrayList<AttachmentManager>> callback);
    
    public void fetchByQueryDescending(ArrayList<QueryData> fields, int first, int max,
                                       AsyncCallback<ArrayList<AttachmentManager>> callback);

    public void fetchIssues(AsyncCallback<ArrayList<AttachmentIssueViewDO>> callback);

    public void fetchForUpdate(Integer attachmentId, AsyncCallback<AttachmentManager> callback);

    public void fetchForReserve(Integer attachmentId, AsyncCallback<AttachmentManager> callback);

    public void fetchIssueForUpdate(Integer attachmentId, AsyncCallback<AttachmentIssueViewDO> callback);

    public void query(Query query, AsyncCallback<ArrayList<AttachmentDO>> callback);

    public void update(AttachmentManager data, AsyncCallback<AttachmentManager> callback);
    
    public void put(AsyncCallback<ArrayList<AttachmentManager>> callback);
    
    public void get(Integer id, AsyncCallback<ReportStatus> callback);
    
    public void getTRF(Integer sampleId, AsyncCallback<ReportStatus> callback);

    public void addIssue(AttachmentIssueViewDO data, AsyncCallback<AttachmentIssueViewDO> callback);

    public void updateIssue(AttachmentIssueViewDO data, AsyncCallback<AttachmentIssueViewDO> callback);

    public void unlock(Integer attachmentId, AsyncCallback<AttachmentManager> callback);

    public void unlockIssue(Integer attachmentId, AsyncCallback<AttachmentIssueViewDO> callback);
    
    public void delete(ArrayList<AttachmentManager> ams, AsyncCallback<Void> callback);

    public void deleteIssue(AttachmentIssueViewDO data, AsyncCallback<Void> callback);
}