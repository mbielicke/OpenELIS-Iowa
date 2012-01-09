package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

@Remote
public interface WorksheetManagerRemote {

    public WorksheetManager fetchById(Integer id) throws Exception;

    public WorksheetManager fetchWithItems(Integer id) throws Exception;

    public WorksheetManager fetchWithNotes(Integer id) throws Exception;
    
    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception;
    
    public WorksheetManager add(WorksheetManager man) throws Exception;
    
    public WorksheetManager update(WorksheetManager man) throws Exception;
    
    public WorksheetManager fetchForUpdate(Integer id) throws Exception;
    
    public WorksheetManager abortUpdate(Integer id) throws Exception;
    
    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception;

    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception;

    public WorksheetResultManager fetchWorksheetResultByWorksheetAnalysisId(Integer id) throws Exception;

    public WorksheetQcResultManager fetchWorksheetQcResultByWorksheetAnalysisId(Integer id) throws Exception;
}
