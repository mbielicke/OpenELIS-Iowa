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
package org.openelis.modules.worksheetCreation.server;

import java.util.ArrayList;

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.WorksheetCreationViewDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationQuery;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.TestRemote;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class WorksheetCreationService {

    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));

    public AutocompleteRPC getTestMethodMatches(AutocompleteRPC rpc) throws Exception {
        TestRemote remote = (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
        rpc.model = (ArrayList)remote.getTestWithActiveAutoCompleteByName(rpc.match+"%", 10);
        
        return rpc;
    }

    public WorksheetCreationQuery query(WorksheetCreationQuery query) throws Exception {
        ArrayList<WorksheetCreationViewDO> results;
        WorksheetCreationRemote            remote;
        
        remote = (WorksheetCreationRemote)EJBFactory.lookup("openelis/WorksheetCreationBean/remote");

        try {    
            query.setResults(new ArrayList<WorksheetCreationViewDO>());
            results = (ArrayList<WorksheetCreationViewDO>)remote.query(query.getFields(),0,500);
            for (WorksheetCreationViewDO result : results) {
                query.getResults().add(result);
            }
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("noRecordsFound"));
        }
        return query;
    }
}
