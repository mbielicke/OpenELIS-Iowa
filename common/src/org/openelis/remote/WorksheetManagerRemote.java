package org.openelis.remote;

import javax.ejb.Remote;

//import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;

@Remote
public interface WorksheetManagerRemote {
    public WorksheetManager add(WorksheetManager man) throws Exception;
    public WorksheetManager update(WorksheetManager man) throws Exception;

    public WorksheetManager fetch(Integer worksheetId) throws Exception;
    public WorksheetManager fetchWithItems(Integer worksheetId) throws Exception;
    public WorksheetManager fetchWithNotes(Integer worksheetId) throws Exception;
    
    public WorksheetManager fetchForUpdate(Integer worksheetId) throws Exception;
    public WorksheetManager abortUpdate(Integer worksheetId) throws Exception;
    
//    public WorksheetItemManager fetchItemById(Integer id) throws Exception;
//    public WorksheetItemManager fetchItemByWorksheetId(Integer worksheetId) throws Exception;
}
