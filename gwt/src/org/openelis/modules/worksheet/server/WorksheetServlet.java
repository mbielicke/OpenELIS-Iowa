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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetManagerBean;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.modules.worksheet.client.WorkSheetServiceInt;

@WebServlet("/openelis/worksheet")
public class WorksheetServlet extends RemoteServlet implements WorkSheetServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    WorksheetManagerBean worksheetManager;
    
    @EJB
    WorksheetBean        worksheet;

    public ArrayList<WorksheetViewDO> query(Query query) throws Exception {
        return worksheet.query(query.getFields(), 0, query.getRowsPerPage());
    }

    public WorksheetManager fetchById(Integer id) throws Exception {
        return worksheetManager.fetchById(id);
    }
    
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        return worksheet.fetchByAnalysisId(id);
    }

    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        return worksheetManager.fetchWithItems(id);
    }

    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        return worksheetManager.fetchWithNotes(id);
    }

    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        return worksheetManager.fetchWithItemsAndNotes(id);
    }

    public WorksheetManager fetchWithAllData(Integer id) throws Exception {
        return worksheetManager.fetchWithAllData(id);
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        return worksheetManager.add(manager);
    }

    public WorksheetManager update(WorksheetManager manager) throws Exception {
        return worksheetManager.update(manager);
    }
    
    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        return worksheetManager.fetchForUpdate(id);
    }
    
    public WorksheetManager abortUpdate(Integer id) throws Exception {
        return worksheetManager.abortUpdate(id);
    }
    
    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception {
        return worksheetManager.fetchWorksheetItemByWorksheetId(id);
    }

    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception {
        return worksheetManager.fetchWorksheetAnalysisByWorksheetItemId(id);
    }

    public WorksheetResultManager fetchWorksheeetResultByWorksheetAnalysisId(Integer id) throws Exception {
        return worksheetManager.fetchWorksheetResultByWorksheetAnalysisId(id);
    }

    public WorksheetQcResultManager fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id) throws Exception {
        return worksheetManager.fetchWorksheetQcResultByWorksheetAnalysisId(id);
    }
}
