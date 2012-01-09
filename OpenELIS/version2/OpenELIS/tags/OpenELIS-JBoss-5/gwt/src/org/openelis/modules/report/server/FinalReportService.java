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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleEnvironmentalFinalReportWebVO;
import org.openelis.domain.SamplePrivateWellFinalReportWebVO;
import org.openelis.domain.SampleSDWISFinalReportWebVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.util.SessionManager;

public class FinalReportService {

    public ArrayList<Prompt> getPromptsForSingle() throws Exception {
        return remote().getPromptsForSingle();
    }

    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        return remote().getPromptsForBatch();
    }

    public ArrayList<Prompt> getPromptsForBatchReprint() throws Exception {
        return remote().getPromptsForBatchReprint();
    }

    public ReportStatus runReportForSingle(Query query) throws Exception {
        ReportStatus st;

        st = remote().runReportForSingle(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForPreview(Query query) throws Exception {
        ReportStatus st;

        st = remote().runReportForPreview(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ReportStatus runReportForBatch(Query query) throws Exception {
        ReportStatus st;

        st = remote().runReportForBatch(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForBatchReprint(Query query) throws Exception {
        ReportStatus st;

        st = remote().runReportForBatchReprint(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    public ReportStatus runReportForWeb(Query query) throws Exception {
        ReportStatus st;       

        st = remote().runReportForWeb(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }

    public ArrayList<SampleEnvironmentalFinalReportWebVO> getSampleEnvironmentalList(Query query) throws Exception {        
        return remote().getSampleEnvironmentalList(query.getFields());
    } 
    
    public ArrayList<SamplePrivateWellFinalReportWebVO> getSamplePrivateWellList(Query query) throws Exception{        
        return remote().getSamplePrivateWellList(query.getFields());
    }  
    
    public ArrayList<SampleSDWISFinalReportWebVO> getSampleSDWISList(Query query) throws Exception {        
        return remote().getSampleSDWISList(query.getFields());
    }     
    
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {        
        return remote().getEnvironmentalProjectList();
    }
    
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {        
        return remote().getPrivateWellProjectList();
    }

    private FinalReportRemote remote() {
        return (FinalReportRemote)EJBFactory.lookup("openelis/FinalReportBean/remote");
    }
}
