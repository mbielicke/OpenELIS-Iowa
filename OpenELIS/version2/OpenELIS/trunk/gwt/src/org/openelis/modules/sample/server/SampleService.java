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
package org.openelis.modules.sample.server;

import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleManagerRemote;

public class SampleService {
    //sample methods
    public SampleManager fetch(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        try{
        return remote.fetch(sampleId);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.fetchByAccessionNumber(accessionNumber);
    }

    public SampleManager add(SampleManager man) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.add(man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.update(man);
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.fetchForUpdate(sampleId);
    }
    
    public SampleManager abort(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.abortUpdate(sampleId);
    }
    
    //sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.fetchSampleOrgsBySampleId(sampleId);
    }
    
    //sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        
        return remote.fetchSampleProjectsBySampleId(sampleId);
    }
    
    //sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        SampleManagerRemote remote = (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
        try{
        return remote.fetchSampleItemsBySampleId(sampleId);
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
