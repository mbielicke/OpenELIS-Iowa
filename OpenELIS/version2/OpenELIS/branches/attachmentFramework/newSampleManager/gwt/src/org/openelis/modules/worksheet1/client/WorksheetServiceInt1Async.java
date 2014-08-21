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
package org.openelis.modules.worksheet1.client;

import java.util.ArrayList;

import org.openelis.domain.IdVO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorksheetServiceInt1Async {
    public void getInstance(AsyncCallback<WorksheetManager1> callback);
    public void fetchById(Integer worksheetId, WorksheetManager1.Load elements[], AsyncCallback<WorksheetManager1> callback);
    public void query(Query query, AsyncCallback<ArrayList<IdVO>> callback);
    public void fetchForUpdate(Integer worksheetId, AsyncCallback<WorksheetManager1> callback);
    public void unlock(Integer worksheetId, Load elements[], AsyncCallback<WorksheetManager1> callback) throws Exception;
    public void update(WorksheetManager1 wm, AsyncCallback<WorksheetManager1> callback) throws Exception;
}
