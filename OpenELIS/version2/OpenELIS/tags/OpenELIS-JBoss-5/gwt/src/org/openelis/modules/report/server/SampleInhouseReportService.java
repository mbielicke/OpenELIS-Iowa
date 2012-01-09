package org.openelis.modules.report.server;

import java.util.ArrayList;

import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleInhouseReportRemote;
import org.openelis.report.Prompt;
import org.openelis.util.SessionManager;

public class SampleInhouseReportService {
    public ArrayList<Prompt> getPrompts() throws Exception{
        return remote().getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = remote().runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
    
    private SampleInhouseReportRemote remote() {
        return (SampleInhouseReportRemote)EJBFactory.lookup("openelis/SampleInhouseReportBean/remote");
    } 

}
