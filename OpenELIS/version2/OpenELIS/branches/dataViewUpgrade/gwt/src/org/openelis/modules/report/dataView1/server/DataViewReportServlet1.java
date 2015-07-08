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
package org.openelis.modules.report.dataView1.server;

import java.util.List;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.openelis.bean.DataViewReportBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.domain.DataView1VO;
import org.openelis.modules.report.dataView1.client.DataViewReportServiceInt1;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/dataViewReport1")
public class DataViewReportServlet1 extends RemoteServlet implements DataViewReportServiceInt1 {

    private static final long  serialVersionUID = 1L;

    @EJB
    private SessionCacheBean   session;

    @EJB
    private DataViewReportBean      dataViewReport;

    public DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data) throws Exception {
        try {
            return dataViewReport.fetchTestAnalyteAndAuxField(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public DataView1VO loadQuery() throws Exception {
        List<String> paths;
        HttpSession session;

        session = getThreadLocalRequest().getSession();
        paths = (List<String>)session.getAttribute("upload");
        if (paths != null && paths.size() > 0) {
            try {
                return dataViewReport.loadQuery(paths.get(0));
            } catch (Exception anyE) {
                throw serializeForGWT(anyE);
            } finally {
                session.removeAttribute("upload");
            }
        }
        
        return null;
    }

    public ReportStatus saveQuery(DataView1VO data) throws Exception {
        ReportStatus st;

        try {
            st = dataViewReport.saveQuery(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForInternal(DataView1VO data) throws Exception {
        ReportStatus st;

        try {
            st = dataViewReport.runReportForInternal(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    @Override
    public ReportStatus getStatus() throws Exception {
        try {
            return (ReportStatus)session.getAttribute("DataViewReportStatus");
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public void stopReport() {
        ReportStatus status;
        
        status = (ReportStatus)session.getAttribute("DataViewReportStatus");
        status.setStatus(ReportStatus.Status.CANCEL);
    }
}