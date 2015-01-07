package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("instrumentBarcodeReport")
public interface InstrumentBarcodeReportServiceInt extends XsrfProtectedService {

    public ArrayList<Prompt> getPrompts() throws Exception;

    public ReportStatus runReport(Query query) throws Exception;
}