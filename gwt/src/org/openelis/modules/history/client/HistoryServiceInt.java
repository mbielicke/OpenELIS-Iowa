package org.openelis.modules.history.client;

import java.util.ArrayList;

import org.openelis.domain.HistoryVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("history")
public interface HistoryServiceInt extends RemoteService {

    ArrayList<HistoryVO> fetchByReferenceIdAndTable(Query query) throws Exception;

}