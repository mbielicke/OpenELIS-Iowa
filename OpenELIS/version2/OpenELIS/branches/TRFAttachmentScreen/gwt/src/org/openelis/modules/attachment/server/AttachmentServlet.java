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
package org.openelis.modules.attachment.server;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.openelis.bean.AttachmentBean;
import org.openelis.bean.AttachmentIssueBean;
import org.openelis.bean.AttachmentManagerBean;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.modules.attachment.client.AttachmentServiceInt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/attachment")
public class AttachmentServlet extends RemoteServlet implements AttachmentServiceInt {

    private static final long     serialVersionUID = 1L;

    @EJB
    private AttachmentManagerBean attachmentManager;

    @EJB
    private AttachmentBean        attachment;

    @EJB
    private AttachmentIssueBean   attachmentIssue;

    @Override
    public ReportStatus get(Integer id) throws Exception {
        ReportStatus st;

        try {
            st = attachmentManager.get(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    @Override
    public ArrayList<AttachmentManager> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        try {
            return attachmentManager.fetchByQuery(fields, first, max);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AttachmentManager> fetchByQueryUnattached(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        try {
            return attachmentManager.fetchByQueryUnattached(fields, first, max);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AttachmentManager> fetchByQueryDescending(ArrayList<QueryData> fields,
                                                               int first, int max) throws Exception {
        try {
            return attachmentManager.fetchByQueryDescending(fields, first, max);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AttachmentIssueViewDO> fetchIssues() throws Exception {
        try {
            return attachmentIssue.fetchList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AttachmentManager> fetchUnattachedByDescription(String description, int first,
                                                                     int max) throws Exception {
        try {
            return attachmentManager.fetchUnattachedByDescription(description, first, max);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentManager fetchForUpdate(Integer attachmentId) throws Exception {
        try {
            return attachmentManager.fetchForUpdate(attachmentId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentManager fetchForReserve(Integer attachmentId) throws Exception {
        try {
            return attachmentManager.fetchForReserve(attachmentId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentIssueViewDO fetchIssueForUpdate(Integer attachmentId) throws Exception {
        try {
            return attachmentIssue.fetchForUpdate(attachmentId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        
    }

    public ArrayList<AttachmentDO> query(Query query) throws Exception {
        try {
            return attachment.query(query.getFields(),
                                    query.getPage() * query.getRowsPerPage(),
                                    query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentManager update(AttachmentManager data) throws Exception {
        try {
            return attachmentManager.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentIssueViewDO addIssue(AttachmentIssueViewDO data) throws Exception {
        try {
            return attachmentIssue.add(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentIssueViewDO updateIssue(AttachmentIssueViewDO data) throws Exception {
        try {
            return attachmentIssue.update(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentManager unlock(Integer attachmentId) throws Exception {
        try {
            return attachmentManager.unlock(attachmentId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public AttachmentIssueViewDO unlockIssue(Integer attachmentId) throws Exception {
        try {
            return attachmentIssue.unlock(attachmentId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ArrayList<AttachmentManager> put() throws Exception {
        List<String> paths;
        HttpSession session;

        session = getThreadLocalRequest().getSession();
        paths = (List<String>)session.getAttribute("upload");
        try {
            return attachmentManager.put(paths, null);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        } finally {
            session.removeAttribute("upload");
        }
    }

    @Override
    public void deleteIssue(AttachmentIssueViewDO data) throws Exception {
        try {
            attachmentIssue.delete(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}