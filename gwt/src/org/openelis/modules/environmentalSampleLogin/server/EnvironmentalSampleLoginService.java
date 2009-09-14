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
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.modules.environmentalSampleLogin.client.SampleEnvQuery;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ProjectRemote;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.remote.SampleRemote;
import org.openelis.remote.SectionRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class EnvironmentalSampleLoginService {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    private static final int leftTableRowsPerPage = 12;
    
    public SampleEnvQuery query(SampleEnvQuery query) throws Exception {
        SampleEnvironmentalRemote remote = (SampleEnvironmentalRemote)EJBFactory.lookup("openelis/SampleEnvironmentalBean/remote");

        try{    
            query.results = new ArrayList<IdNameDO>();
            ArrayList<IdNameDO> results = (ArrayList<IdNameDO>)remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            for(IdNameDO result : results) {
                query.results.add(result);
            }
        }catch(LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
        return query;
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
    
    public AutocompleteRPC getSectionMatches(AutocompleteRPC rpc) throws Exception {
        SectionRemote remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
        rpc.model = (ArrayList)remote.getAutoCompleteSectionByName(rpc.match+"%", 10);
        
        return rpc;
    }
}