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
package org.openelis.portal.modules.dataView.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.DataViewReportBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.IdNameVO;
import org.openelis.portal.modules.dataView.client.DataViewServiceInt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelisweb/dataView")
public class DataViewServlet extends RemoteServlet implements DataViewServiceInt {

    private static final long serialVersionUID = 1L;

    @EJB
    private SessionCacheBean  session;

    @EJB
    private DataViewReportBean     dataView;

    public ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception {
        try {
            return dataView.fetchProjectListForPortal();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public DataView1VO fetchAnalyteAndAuxField(DataView1VO data) throws Exception {
        /*
         * these flags are set here to make sure that not reportable results or
         * aux data won't be shown though the portal even if someone manages to
         * set them in the client
         */
        data.setIncludeNotReportableResults("N");
        data.setIncludeNotReportableAuxData("N");
        try {
            return dataView.fetchAnalyteAndAuxFieldForPortal(data);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ReportStatus runReportForPortal(DataView1VO data) throws Exception {
        ReportStatus st;

        /*
         * these flags are set here to make sure that not reportable results or
         * aux data won't be shown through the portal even if someone manages to
         * set them in the client
         */
        data.setIncludeNotReportableResults("N");
        data.setIncludeNotReportableAuxData("N");
        try {
            st = dataView.runReportForPortal(data);
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
}