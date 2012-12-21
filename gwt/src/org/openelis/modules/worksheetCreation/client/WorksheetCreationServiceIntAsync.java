package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetCreationServiceIntAsync {

    void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback);

    void query(Query query, AsyncCallback<ArrayList<WorksheetCreationVO>> callback);

}
