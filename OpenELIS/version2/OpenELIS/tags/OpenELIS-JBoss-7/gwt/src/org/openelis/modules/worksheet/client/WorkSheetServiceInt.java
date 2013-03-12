package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("worksheet")
public interface WorkSheetServiceInt extends RemoteService {

    ArrayList<WorksheetViewDO> query(Query query) throws Exception;

    WorksheetManager fetchById(Integer id) throws Exception;

    ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception;

    WorksheetManager fetchWithItems(Integer id) throws Exception;

    WorksheetManager fetchWithNotes(Integer id) throws Exception;

    WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception;

    WorksheetManager fetchWithAllData(Integer id) throws Exception;

    WorksheetManager add(WorksheetManager manager) throws Exception;

    WorksheetManager update(WorksheetManager manager) throws Exception;

    WorksheetManager fetchForUpdate(Integer id) throws Exception;

    WorksheetManager abortUpdate(Integer id) throws Exception;

    WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception;

    WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception;

    WorksheetResultManager fetchWorksheeetResultByWorksheetAnalysisId(Integer id) throws Exception;

    WorksheetQcResultManager fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id) throws Exception;

}