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

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen Widget. GWT
 * RemoteServiceServlets that want to provide server side logic for Screens must
 * implement this interface.
 */
@RemoteServiceRelativePath("attachment")
public interface AttachmentServiceInt extends XsrfProtectedService {
    
    public ReportStatus get(Integer id) throws Exception;
    
    public ReportStatus getTRF(Integer sampleId) throws Exception;

    public ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception;
    
    public ArrayList<AttachmentManager> fetchByQueryUnattached(ArrayList<QueryData> fields, int first, int max) throws Exception;

    public ArrayList<AttachmentManager> fetchByQueryDescending(ArrayList<QueryData> fields, int first, int max) throws Exception;
    
    public ArrayList<AttachmentManager> fetchUnattachedByDescription(String description, int first, int max) throws Exception;
    
    public ArrayList<AttachmentIssueViewDO> fetchIssues() throws Exception;
    
    public AttachmentManager fetchForUpdate(Integer attachmentId) throws Exception;
    
    public AttachmentManager fetchForReserve(Integer attachmentId) throws Exception;
    
    public AttachmentIssueViewDO fetchIssueForUpdate(Integer attachmentId) throws Exception;
    
    public ArrayList<AttachmentDO> query(Query query) throws Exception;

    public AttachmentManager update(AttachmentManager data) throws Exception;
    
    public AttachmentIssueViewDO addIssue(AttachmentIssueViewDO data) throws Exception;
    
    public AttachmentIssueViewDO updateIssue(AttachmentIssueViewDO data) throws Exception;

    public AttachmentManager unlock(Integer attachmentId) throws Exception;
    
    public AttachmentIssueViewDO unlockIssue(Integer attachmentId) throws Exception;

    public ArrayList<AttachmentManager> put() throws Exception;
    
    public void deleteIssue(AttachmentIssueViewDO data) throws Exception;
}
