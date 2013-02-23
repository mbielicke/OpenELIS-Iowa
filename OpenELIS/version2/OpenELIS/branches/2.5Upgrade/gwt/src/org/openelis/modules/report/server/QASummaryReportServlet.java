package org.openelis.modules.report.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.QASummaryReportBean;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.modules.report.client.QASummaryReportServiceInt;

@WebServlet("/openelis/qaSummary")
public class QASummaryReportServlet extends RemoteServlet implements QASummaryReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    QASummaryReportBean qaSummaryReport;

    public ArrayList<Prompt> getPrompts() throws Exception{
        return qaSummaryReport.getPrompts();      
    }
    
    public ReportStatus runReport(Query query) throws Exception { 
        ReportStatus st;
        
        st = qaSummaryReport.runReport(query.getFields());
        if (st.getStatus() == ReportStatus.Status.SAVED)
            getThreadLocalRequest().getSession().setAttribute(st.getMessage(), st);

        return st;
    }

}
