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
package org.openelis.modules.report.server;

import java.util.ArrayList;

import org.openelis.gwt.common.data.Query;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.ReportStatus;
import org.openelis.util.SessionManager;

public class FinalReportService {    
    
    public ArrayList<Prompt> getPromptsForSingle() throws Exception{
        return remote().getPromptsForSingle();      
    }
    
    public ReportStatus runReportForSingle(Query query) throws Exception { 
        ReportStatus st;
        
        st = remote().runReportForSingle(query.getFields());
        SessionManager.getSession().setAttribute(st.getMessage(), st.getMessage());
        return st;
    }
    
    private FinalReportRemote remote() {
        return (FinalReportRemote)EJBFactory.lookup("openelis/FinalReportBean/remote");
    } 
}


