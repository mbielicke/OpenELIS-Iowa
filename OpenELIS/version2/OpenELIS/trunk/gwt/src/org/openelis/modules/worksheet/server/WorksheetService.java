/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.worksheet.server;

import java.util.ArrayList;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.server.EJBFactory;

public class WorksheetService {
    public ArrayList<WorksheetViewDO> query(Query query) throws Exception {
        return EJBFactory.getWorksheet().query(query.getFields(), 0, 500);
    }

    public WorksheetManager fetchById(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchById(id);
    }
    
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        return EJBFactory.getWorksheet().fetchByAnalysisId(id);
    }

    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWithItems(id);
    }

    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWithNotes(id);
    }

    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWithItemsAndNotes(id);
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        return EJBFactory.getWorksheetManager().add(manager);
    }

    public WorksheetManager update(WorksheetManager manager) throws Exception {
        return EJBFactory.getWorksheetManager().update(manager);
    }
    
    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchForUpdate(id);
    }
    
    public WorksheetManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().abortUpdate(id);
    }
    
    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWorksheetItemByWorksheetId(id);
    }

    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWorksheetAnalysisByWorksheetItemId(id);
    }

    public WorksheetResultManager fetchWorksheeetResultByWorksheetAnalysisId(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWorksheetResultByWorksheetAnalysisId(id);
    }

    public WorksheetQcResultManager fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id) throws Exception {
        return EJBFactory.getWorksheetManager().fetchWorksheetQcResultByWorksheetAnalysisId(id);
    }
    
}
