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
package org.openelis.web.modules.finalReport.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.FinalReportBean;
import org.openelis.bean.FinalReportWebBean;
import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.web.modules.finalReport.client.FinalReportServiceInt;

@WebServlet("/openelisweb/finalReport")
public class FinalReportServlet extends RemoteServlet implements FinalReportServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    FinalReportBean    finalReport;
    
    @EJB
    FinalReportWebBean finalReportWeb;

    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        try {
            return finalReport.getPromptsForBatch();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<Prompt> getPromptsForBatchReprint() throws Exception {
        try {
            return finalReport.getPromptsForBatchReprint();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ReportStatus runReportForSingle(Query query) throws Exception {
        ReportStatus st;

        try {
            st = finalReport.runReportForSingle(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForPreview(Query query) throws Exception {
        ReportStatus st;

        try {
            st = finalReport.runReportForPreview(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForBatch(Query query) throws Exception {
        ReportStatus st;

        try {
            st = finalReport.runReportForBatch(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForBatchReprint(Query query) throws Exception {
        ReportStatus st;

        try {
            st = finalReport.runReportForBatchReprint(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWeb(Query query) throws Exception {
        ReportStatus st;       

        try {
            st = finalReport.runReportForWeb(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ArrayList<FinalReportWebVO> getSampleEnvironmentalList(Query query) throws Exception {        
        try {
            return finalReportWeb.getSampleEnvironmentalList(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    } 
    
    public ArrayList<FinalReportWebVO> getSamplePrivateWellList(Query query) throws Exception{        
        try {
            return finalReportWeb.getSamplePrivateWellList(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }  
    
    public ArrayList<FinalReportWebVO> getSampleSDWISList(Query query) throws Exception {        
        try {
            return finalReportWeb.getSampleSDWISList(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }     
    
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {        
        try {
            return finalReportWeb.getEnvironmentalProjectList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {        
        try {
            return finalReportWeb.getPrivateWellProjectList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> getSDWISProjectList() throws Exception {        
        try {
            return finalReportWeb.getSDWISProjectList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}