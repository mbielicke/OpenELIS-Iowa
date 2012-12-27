package org.openelis.web.modules.sampleStatusReport.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleStatusReportBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.web.modules.sampleStatusReport.client.SampleStatusReportServiceInt;

@WebServlet("/openelisweb/sampleStatus")
public class SampleStatusReportServlet extends RemoteServlet implements SampleStatusReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleStatusReportBean sampleStatusReport;
    
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception {        
        return sampleStatusReport.getSampleListForSampleStatusReport(query.getFields());
    }    
    
    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        return sampleStatusReport.getProjectList();
    }

}
