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
package org.openelis.modules.worksheet1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.WorksheetBean;
import org.openelis.bean.WorksheetManager1Bean;
import org.openelis.domain.IdVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.modules.worksheet1.client.WorksheetServiceInt1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/worksheet1")
public class WorksheetServlet1 extends RemoteServlet implements WorksheetServiceInt1 {

    private static final long serialVersionUID = 1L;
    
    @EJB
    WorksheetBean worksheet;
    @EJB
    WorksheetManager1Bean worksheetManager1;

    public WorksheetManager1 getInstance() throws Exception {
        try {
            return worksheetManager1.getInstance();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        try {
            return worksheetManager1.fetchById(worksheetId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdVO> query(Query query) throws Exception {
        try {
            return worksheet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        try {
            return worksheetManager1.fetchForUpdate(worksheetId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 unlock(Integer worksheetId, Load... elements) throws Exception {
        try {
            return worksheetManager1.unlock(worksheetId, elements);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public WorksheetManager1 update(WorksheetManager1 wm) throws Exception {
        try {
            return worksheetManager1.update(wm);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}