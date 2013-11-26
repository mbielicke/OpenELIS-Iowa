package org.openelis.web.modules.sampleStatusReport.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SampleStatusReportBean;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.web.modules.sampleStatusReport.client.SampleStatusReportServiceInt;

@WebServlet("/openelisweb/sampleStatus")
public class SampleStatusReportServlet extends RemoteServlet implements SampleStatusReportServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    SampleStatusReportBean sampleStatusReport;
    
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception {        
        try {
            return sampleStatusReport.getSampleListForSampleStatusReport(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }    
    
    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        try {
            return sampleStatusReport.getProjectList();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SampleQaEventViewDO> getSampleQaEventsBySampleId(Integer id) throws Exception {
        try {
            return sampleStatusReport.getSampleQaEventsBySampleId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<AnalysisQaEventViewDO> getAnalysisQaEventsByAnalysisId(Integer id) throws Exception {
        try {
            return sampleStatusReport.getAnalysisQaEventsByAnalysisId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
