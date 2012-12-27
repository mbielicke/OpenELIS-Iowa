package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleStatusReportService implements SampleStatusReportServiceInt,
                                      SampleStatusReportServiceIntAsync {
    
    static SampleStatusReportService instance;
    
    SampleStatusReportServiceIntAsync service;
    
    public static SampleStatusReportService get() {
        if(instance == null)
            instance = new SampleStatusReportService();
        
        return instance;
    }
    
    private SampleStatusReportService() {
        service = (SampleStatusReportServiceIntAsync)GWT.create(SampleStatusReportServiceInt.class);
    }

    @Override
    public void getSampleListForSampleStatusReport(Query query,
                                                   AsyncCallback<ArrayList<SampleStatusWebReportVO>> callback) {
        service.getSampleListForSampleStatusReport(query, callback);
    }

    @Override
    public void getSampleStatusProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getSampleStatusProjectList(callback);
    }

    @Override
    public ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception {
        Callback<ArrayList<SampleStatusWebReportVO>> callback;
        
        callback = new Callback<ArrayList<SampleStatusWebReportVO>>();
        service.getSampleListForSampleStatusReport(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getSampleStatusProjectList(callback);
        return callback.getResult();
    }

}
