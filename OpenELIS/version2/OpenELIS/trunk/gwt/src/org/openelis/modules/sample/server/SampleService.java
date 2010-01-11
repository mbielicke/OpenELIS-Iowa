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

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SampleManagerRemote;
import org.openelis.remote.SampleQAEventManagerRemote;
import org.openelis.remote.SampleRemote;

public class SampleService {
    private static final int rowPP = 12;
    
    //sample methods
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }
    
    public SampleManager fetch(Integer sampleId) throws Exception {
        return managerRemote().fetch(sampleId);
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        return managerRemote().fetchWithItemsAnalysis(sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return managerRemote().fetchByAccessionNumber(accessionNumber);
    }

    public SampleManager add(SampleManager man) throws Exception {
        return managerRemote().add(man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return managerRemote().update(man);
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return managerRemote().fetchForUpdate(sampleId);
    }
    
    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return managerRemote().abortUpdate(sampleId);
    }
    
    public void validateAccessionNumber(SampleDO sampleDO) throws Exception {
        managerRemote().validateAccessionNumber(sampleDO);
    }
    
    //sample org methods
    public SampleOrganizationManager fetchSampleOrganizationsBySampleId(Integer sampleId) throws Exception {
        return managerRemote().fetchSampleOrgsBySampleId(sampleId);
    }
    
    //sample project methods
    public SampleProjectManager fetchSampleprojectsBySampleId(Integer sampleId) throws Exception {
        return managerRemote().fetchSampleProjectsBySampleId(sampleId);
    }
    
    //sample item methods
    public SampleItemManager fetchSampleItemsBySampleId(Integer sampleId) throws Exception {
        return managerRemote().fetchSampleItemsBySampleId(sampleId);
    }
    
    //sample qa method
    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception {
        return qaRemote().fetchBySampleId(sampleId);
    }
    
    private SampleRemote remote(){
        return (SampleRemote)EJBFactory.lookup("openelis/SampleBean/remote");
    }
    
    private SampleManagerRemote managerRemote(){
        return (SampleManagerRemote)EJBFactory.lookup("openelis/SampleManagerBean/remote");
    }
    
    private SampleQAEventManagerRemote qaRemote(){
        return (SampleQAEventManagerRemote)EJBFactory.lookup("openelis/SampleQAEventManagerBean/remote");
    }
}
