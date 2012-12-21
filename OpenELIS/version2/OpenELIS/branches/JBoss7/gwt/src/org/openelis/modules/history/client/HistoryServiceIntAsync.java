package org.openelis.modules.history.client;

import java.util.ArrayList;

import org.openelis.domain.HistoryVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HistoryServiceIntAsync {

    void fetchByReferenceIdAndTable(Query query, AsyncCallback<ArrayList<HistoryVO>> callback);

}
