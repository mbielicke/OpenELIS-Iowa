package org.openelis.portal.modules.sampleStatus.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleStatusServiceIntAsync {

    void getSampleListForSampleStatusReport(Query query,
                                            AsyncCallback<ArrayList<SampleStatusWebReportVO>> callback);

    void getSampleStatusProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getSampleQaEventsBySampleId(Integer id,
                                     AsyncCallback<ArrayList<SampleQaEventViewDO>> callback);

    void getAnalysisQaEventsByAnalysisId(Integer id,
                                         AsyncCallback<ArrayList<AnalysisQaEventViewDO>> callback);

}
