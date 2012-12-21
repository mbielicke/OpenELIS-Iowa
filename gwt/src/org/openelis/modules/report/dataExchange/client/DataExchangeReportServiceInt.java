package org.openelis.modules.report.dataExchange.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataExchangeReport")
public interface DataExchangeReportServiceInt extends RemoteService {

    ArrayList<Prompt> getPrompts() throws Exception;

    ReportStatus exportToLocation(Query query) throws Exception;

}