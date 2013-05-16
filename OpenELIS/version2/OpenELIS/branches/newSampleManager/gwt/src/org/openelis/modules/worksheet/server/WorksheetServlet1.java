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
import org.openelis.bean.WorksheetManager1Bean;
import org.openelis.domain.IdVO;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.worksheet.client.WorksheetServiceInt1;
import org.openelis.ui.common.data.Query;

@WebServlet("/openelis/worksheet")
public class WorksheetServlet1 extends RemoteServlet implements WorksheetServiceInt1 {

    private static final long serialVersionUID = 1L;
    
    @EJB
    WorksheetBean worksheet;
    @EJB
    WorksheetManager1Bean worksheetManager1;

    public WorksheetManager1 getInstance() throws Exception {
        return worksheetManager1.getInstance();
    }
    
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        return worksheetManager1.fetchById(worksheetId, elements);
    }
    
    public ArrayList<IdVO> fetchByQuery(Query query) throws Exception {
        return worksheet.fetchByQuery(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        return worksheetManager1.fetchForUpdate(worksheetId);
    }
}