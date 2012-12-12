package org.openelis.modules.report.server;

import java.util.ArrayList;

import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.report.Prompt;
import org.openelis.server.EJBFactory;
import org.openelis.util.SessionManager;

public class VolumeReportService {
    public ArrayList<Prompt> getPrompts() throws Exception{
        return EJBFactory.getVolumeReport().getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = EJBFactory.getVolumeReport().runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            SessionManager.getSession().setAttribute(st.getMessage(), st);

        return st;
    }
   
}
