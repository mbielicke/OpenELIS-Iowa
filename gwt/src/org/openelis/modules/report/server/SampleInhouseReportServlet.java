package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleInhouseReportBean;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.client.SampleInHouseReportServiceInt;

@WebServlet("/openelis/sampleInHouse")
public class SampleInhouseReportServlet extends RemoteServlet implements SampleInHouseReportServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleInhouseReportBean sampleInhouseReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        return sampleInhouseReport.getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = sampleInhouseReport.runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

}
