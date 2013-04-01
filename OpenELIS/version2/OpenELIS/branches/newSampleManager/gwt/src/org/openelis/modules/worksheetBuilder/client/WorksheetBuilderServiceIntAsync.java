package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetBuilderServiceIntAsync {

    void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void lookupAnalyses(Query query, AsyncCallback<ArrayList<WorksheetBuilderVO>> callback);

}
