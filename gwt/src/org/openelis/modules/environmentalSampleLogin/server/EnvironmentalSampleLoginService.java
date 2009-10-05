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
package org.openelis.modules.environmentalSampleLogin.server;

import java.util.ArrayList;

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ProjectRemote;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.remote.SampleRemote;
import org.openelis.server.constants.Constants;

public class EnvironmentalSampleLoginService {

    private static final int rowPP = 12;
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }
    
    public String getScreen() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/environmentalSampleLogin.xsl");      
    }
    
    public void validateAccessionNumber(Integer accessionNumber) throws Exception {
        SampleRemote remote = (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
        
        remote.validateAccessionNumber(accessionNumber);
    }
    
    public AutocompleteRPC getProjectMatches(AutocompleteRPC rpc) throws Exception {
        ProjectRemote remote = (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
        rpc.model = (ArrayList)remote.autoCompleteLookupByName(rpc.match+"%", 10);
        return rpc;
    }
    
    public AutocompleteRPC getOrganizationMatches(AutocompleteRPC rpc) throws Exception {
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        rpc.model = (ArrayList)remote.autoCompleteLookupByName(rpc.match+"%", 10);
        
        return rpc;
    }
    
    private SampleEnvironmentalRemote remote() {
        return (SampleEnvironmentalRemote)EJBFactory.lookup("openelis/SampleEnvironmentalBean/remote");
    }
}