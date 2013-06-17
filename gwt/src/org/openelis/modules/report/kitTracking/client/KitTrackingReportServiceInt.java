package org.openelis.modules.report.kitTracking.client;

import java.util.ArrayList;

import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("kitTrackingReport")
public interface KitTrackingReportServiceInt extends RemoteService {

	public ReportStatus runReport(Query query) throws Exception;
	public ArrayList<OptionListItem> getPrinterListByType(String type);

}