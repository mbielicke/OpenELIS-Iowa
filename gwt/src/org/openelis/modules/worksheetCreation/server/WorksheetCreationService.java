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
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.WorksheetManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SectionRemote;
import org.openelis.remote.TestRemote;
import org.openelis.remote.WorksheetRemote;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.remote.WorksheetManagerRemote;

public class WorksheetCreationService {

    public AutocompleteRPC getTestMethodMatches(AutocompleteRPC rpc) throws Exception {
        rpc.model = (ArrayList)testRemote().getTestWithActiveAutoCompleteByName(rpc.match+"%", 10);
        
        return rpc;
    }

    public AutocompleteRPC getSectionMatches(AutocompleteRPC rpc) throws Exception {
        rpc.model = (ArrayList)sectionRemote().getAutoCompleteSectionByName(rpc.match+"%", 10);
        
        return rpc;
    }

    public ArrayList<WorksheetCreationVO> query(Query query) throws Exception {
        return creationRemote().query(query.getFields(), 0, 500);
    }

    public WorksheetManager add(WorksheetManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    private TestRemote testRemote() {
        return (TestRemote)EJBFactory.lookup("openelis/TestBean/remote");
    }
    
    private SectionRemote sectionRemote() {
        return (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
    }
    
    private WorksheetRemote remote() {
        return (WorksheetRemote)EJBFactory.lookup("openelis/WorksheetBean/remote");
    }
    
    private WorksheetCreationRemote creationRemote() {
        return (WorksheetCreationRemote)EJBFactory.lookup("openelis/WorksheetCreationBean/remote");
    }
    
    private WorksheetManagerRemote remoteManager() {
        return (WorksheetManagerRemote)EJBFactory.lookup("openelis/WorksheetManagerBean/remote");
    }
}
