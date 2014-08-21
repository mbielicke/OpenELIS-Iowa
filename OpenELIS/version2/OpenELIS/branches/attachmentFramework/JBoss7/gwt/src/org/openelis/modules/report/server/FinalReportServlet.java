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
package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.FinalReportBean;
import org.openelis.bean.FinalReportWebBean;
import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.client.FinalReportServiceInt;

@WebServlet("/openelis/finalReport")
public class FinalReportServlet extends RemoteServlet implements FinalReportServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    FinalReportBean    finalReport;
    
    @EJB
    FinalReportWebBean finalReportWeb;

    public ArrayList<Prompt> getPromptsForSingle() throws Exception {
        return finalReport.getPromptsForSingle();
    }

    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        return finalReport.getPromptsForBatch();
    }

    public ArrayList<Prompt> getPromptsForBatchReprint() throws Exception {
        return finalReport.getPromptsForBatchReprint();
    }

    public ReportStatus runReportForSingle(Query query) throws Exception {
        ReportStatus st;

        st = finalReport.runReportForSingle(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForPreview(Query query) throws Exception {
        ReportStatus st;

        st = finalReport.runReportForPreview(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForBatch(Query query) throws Exception {
        ReportStatus st;

        st = finalReport.runReportForBatch(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForBatchReprint(Query query) throws Exception {
        ReportStatus st;

        st = finalReport.runReportForBatchReprint(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWeb(Query query) throws Exception {
        ReportStatus st;       

        st = finalReport.runReportForWeb(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ArrayList<FinalReportWebVO> getSampleEnvironmentalList(Query query) throws Exception {        
        return finalReportWeb.getSampleEnvironmentalList(query.getFields());
    } 
    
    public ArrayList<FinalReportWebVO> getSamplePrivateWellList(Query query) throws Exception{        
        return finalReportWeb.getSamplePrivateWellList(query.getFields());
    }  
    
    public ArrayList<FinalReportWebVO> getSampleSDWISList(Query query) throws Exception {        
        return finalReportWeb.getSampleSDWISList(query.getFields());
    }     
    
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {        
        return finalReportWeb.getEnvironmentalProjectList();
    }
    
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {        
        return finalReportWeb.getPrivateWellProjectList();
    }
    
    public ArrayList<IdNameVO> getSDWISProjectList() throws Exception {        
        return finalReportWeb.getSDWISProjectList();
    }
}