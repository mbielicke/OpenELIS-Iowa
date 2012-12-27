package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleStatusReportServiceIntAsync {

    void getSampleListForSampleStatusReport(Query query,
                                            AsyncCallback<ArrayList<SampleStatusWebReportVO>> callback);

    void getSampleStatusProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

}
