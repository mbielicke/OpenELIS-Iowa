package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.IdVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetServiceInt1Async {
    public void getInstance(AsyncCallback<WorksheetManager1> callback);
    public void fetchById(Integer worksheetId, WorksheetManager1.Load elements[], AsyncCallback<WorksheetManager1> callback);
    public void fetchByQuery(Query query, AsyncCallback<ArrayList<IdVO>> callback);
    public void fetchForUpdate(Integer worksheetId, AsyncCallback<WorksheetManager1> callback);
}
