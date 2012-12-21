package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorkSheetServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<WorksheetManager> callback);

    void add(WorksheetManager manager, AsyncCallback<WorksheetManager> callback);

    void fetchByAnalysisId(Integer id, AsyncCallback<ArrayList<WorksheetViewDO>> callback);

    void fetchById(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchWithAllData(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchWithItems(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchWithItemsAndNotes(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<WorksheetManager> callback);

    void fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id,
                                                      AsyncCallback<WorksheetQcResultManager> callback);

    void fetchWorksheeetResultByWorksheetAnalysisId(Integer id,
                                                    AsyncCallback<WorksheetResultManager> callback);

    void fetchWorksheetAnalysisByWorksheetItemId(Integer id,
                                                 AsyncCallback<WorksheetAnalysisManager> callback);

    void fetchWorksheetItemByWorksheetId(Integer id, AsyncCallback<WorksheetItemManager> callback);

    void query(Query query, AsyncCallback<ArrayList<WorksheetViewDO>> callback);

    void update(WorksheetManager manager, AsyncCallback<WorksheetManager> callback);

}
