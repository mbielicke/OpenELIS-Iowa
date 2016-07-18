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
package org.openelis.portal.modules.finalReport.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.FinalReportBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.portal.modules.finalReport.client.FinalReportServiceInt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelisweb/finalReport")
public class FinalReportServlet extends RemoteServlet implements FinalReportServiceInt {

    private static final long     serialVersionUID = 1L;

    @EJB
    private SessionCacheBean      session;

    @EJB
    private FinalReportBean       finalReportPortal;

    public ReportStatus runReportForWeb(Query query) throws Exception {
        ReportStatus st;

        try {
            st = finalReportPortal.runReportForPortal(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ArrayList<SampleViewVO> getSampleList(Query query) throws Exception {
        try {
            return finalReportPortal.getSampleList(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> getProjectList() throws Exception {
        try {
            return finalReportPortal.getProjectList();
        } catch (Exception anyE) {
            anyE.printStackTrace();
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ReportStatus getStatus() throws Exception {
        try {
            return (ReportStatus)session.getAttribute("FinalReport");
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}