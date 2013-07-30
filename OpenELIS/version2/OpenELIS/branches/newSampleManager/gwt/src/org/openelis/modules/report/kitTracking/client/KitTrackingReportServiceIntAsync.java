package org.openelis.modules.report.kitTracking.client;

import java.util.ArrayList;

import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface KitTrackingReportServiceIntAsync {

	public void runReport(Query query, AsyncCallback<ReportStatus> callback);
	public void getPrinterListByType(String type, AsyncCallback<ArrayList<OptionListItem>> callback);

}
