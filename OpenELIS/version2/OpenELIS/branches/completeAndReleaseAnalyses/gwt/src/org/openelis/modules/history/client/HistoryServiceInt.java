package org.openelis.modules.history.client;

import java.util.ArrayList;

import org.openelis.domain.HistoryVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("history")
public interface HistoryServiceInt extends XsrfProtectedService {

    ArrayList<HistoryVO> fetchByReferenceIdAndTable(Query query) throws Exception;

}