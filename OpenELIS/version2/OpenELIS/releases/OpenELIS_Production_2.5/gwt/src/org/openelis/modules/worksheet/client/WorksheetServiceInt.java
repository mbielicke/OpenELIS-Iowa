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
package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("worksheet")
public interface WorksheetServiceInt extends XsrfProtectedService {

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