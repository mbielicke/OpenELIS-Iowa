package org.openelis.web.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataViewReport")
public interface DataViewServiceInt extends RemoteService {

    ArrayList<IdNameVO> fetchEnvironmentalProjectListForWeb() throws Exception;

    DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception;

    DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data) throws Exception;

    DataViewVO openQuery() throws Exception;

    ReportStatus saveQuery(DataViewVO data) throws Exception;

    ReportStatus runReport(DataViewVO data) throws Exception;

    ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception;

}