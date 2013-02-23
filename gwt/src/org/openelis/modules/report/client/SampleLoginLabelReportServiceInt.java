package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sampleLoginLabelReport")
public interface SampleLoginLabelReportServiceInt extends RemoteService {

    ArrayList<Prompt> getPrompts() throws Exception;

    ReportStatus runReport(Query query) throws Exception;

    ArrayList<Prompt> getAdditionalPrompts() throws Exception;

    ReportStatus runAdditionalReport(Query query) throws Exception;

}