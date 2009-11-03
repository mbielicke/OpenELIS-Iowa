package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;

@Remote
public interface WorksheetManagerRemote {

    public WorksheetManager fetchById(Integer id) throws Exception;

    public WorksheetManager fetchWithItems(Integer id) throws Exception;

    public WorksheetManager fetchWithNotes(Integer id) throws Exception;
    
    public WorksheetManager add(WorksheetManager man) throws Exception;
    
    public WorksheetManager update(WorksheetManager man) throws Exception;
    
    public WorksheetManager fetchForUpdate(Integer id) throws Exception;
    
    public WorksheetManager abortUpdate(Integer id) throws Exception;
    
    public WorksheetItemManager fetchItemByWorksheetId(Integer id) throws Exception;
}
