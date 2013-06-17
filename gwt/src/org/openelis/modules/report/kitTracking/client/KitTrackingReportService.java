package org.openelis.modules.report.kitTracking.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class KitTrackingReportService implements KitTrackingReportServiceInt,
                                     KitTrackingReportServiceIntAsync {

    private static KitTrackingReportService  instance;
    private KitTrackingReportServiceIntAsync service;

    public static KitTrackingReportService get() {
        if (instance == null)
            instance = new KitTrackingReportService();
        return instance;
    }

    private KitTrackingReportService() {
        service = (KitTrackingReportServiceIntAsync)GWT.create(KitTrackingReportServiceInt.class);
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReport(query, callback);
    }

    @Override
    public ReportStatus runReport(Query query) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReport(query, callback);
        return callback.getResult();
    }

    public void getPrinterListByType(String type, AsyncCallback<ArrayList<OptionListItem>> callback) {
        service.getPrinterListByType(type, callback);
    }

    public ArrayList<OptionListItem> getPrinterListByType(String type) {
        Callback<ArrayList<OptionListItem>> callback;

        callback = new Callback<ArrayList<OptionListItem>>();
        service.getPrinterListByType(type, callback);
        try {
            return callback.getResult();
        } catch (Exception e) {
            return new ArrayList<OptionListItem>();
        }
    }

}
