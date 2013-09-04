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
import org.openelis.domain.IdVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.modules.worksheet.client.WorksheetServiceInt;

@WebServlet("/openelis/worksheet")
public class WorksheetServlet extends RemoteServlet implements WorksheetServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    WorksheetManagerBean worksheetManager;
    
    @EJB
    WorksheetBean        worksheet;

    public ArrayList<WorksheetViewDO> query(Query query) throws Exception {
        ArrayList<IdVO> idVOs;
        ArrayList<Integer> ids;
        
        try {
            idVOs = worksheet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
            ids = new ArrayList<Integer>();
            for (IdVO vo : idVOs)
                ids.add(vo.getId());
            return worksheet.fetchByIds(ids);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager fetchById(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        try {        
            return worksheet.fetchByAnalysisId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWithItems(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWithNotes(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWithItemsAndNotes(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager fetchWithAllData(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWithAllData(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        try {        
            return worksheetManager.add(manager);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetManager update(WorksheetManager manager) throws Exception {
        try {        
            return worksheetManager.update(manager);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager abortUpdate(Integer id) throws Exception {
        try {        
            return worksheetManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWorksheetItemByWorksheetId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWorksheetAnalysisByWorksheetItemId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetResultManager fetchWorksheeetResultByWorksheetAnalysisId(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWorksheetResultByWorksheetAnalysisId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public WorksheetQcResultManager fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id) throws Exception {
        try {        
            return worksheetManager.fetchWorksheetQcResultByWorksheetAnalysisId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
