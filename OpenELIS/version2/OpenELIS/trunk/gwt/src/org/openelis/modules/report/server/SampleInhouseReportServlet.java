package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleInhouseReportBean;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.report.client.SampleInHouseReportServiceInt;

@WebServlet("/openelis/sampleInHouse")
public class SampleInhouseReportServlet extends RemoteServlet implements SampleInHouseReportServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleInhouseReportBean sampleInhouseReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        try {        
            return sampleInhouseReport.getPrompts();      
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        try {        
            st = sampleInhouseReport.runReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }

        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

}
