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
package org.openelis.manager;

import javax.naming.InitialContext;

import org.openelis.domain.SampleDO;
import org.openelis.local.SampleLocal;
import org.openelis.utils.ReferenceTableCache;

public class SampleManagerProxy {
    public SampleManager add(SampleManager man) throws Exception {
        return null;
    }

    public SampleManager update(SampleManager man) throws Exception {
        return null;
    }
    
    public SampleManager fetch(Integer sampleId) throws Exception {
        SampleLocal sl = getSampleLocal();
        SampleDO sampleDO = sl.fetchById(sampleId);
        
        SampleManager sm = SampleManager.getInstance();
        sm.setSample(sampleDO);
        
        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();
        sm.getSampleItems();
        
        sm.setSampleReferenceTableId(ReferenceTableCache.getReferenceTable("sample"));
        sm.setSampleInternalReferenceTableId(ReferenceTableCache.getReferenceTable("sample_internal_note"));
        
        return sm;
    }
                         
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        SampleLocal sl = getSampleLocal();
        SampleDO sampleDO = sl.fetchById(sampleId);
        
        SampleManager sm = SampleManager.getInstance();
        sm.setSample(sampleDO);
        
        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();
        
        sm.setSampleReferenceTableId(ReferenceTableCache.getReferenceTable("sample"));
        sm.setSampleInternalReferenceTableId(ReferenceTableCache.getReferenceTable("sample_internal_note"));
        
        SampleItemManager sim = sm.getSampleItems();
        
        for(int i=0; i<sim.count(); i++)
            sim.getAnalysisAt(i);
        
        return sm;
    }
    
    //FIXME not sure if we want to load the whole top form for this method
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleLocal sl = getSampleLocal();
        SampleDO sampleDO = sl.fetchByAccessionNumber(accessionNumber);
        
        SampleManager sm = SampleManager.getInstance();
        sm.setSample(sampleDO);
        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();
        sm.getSampleItems();
        
        return sm;
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return null;
    }
    
    public SampleManager abort(Integer sampleId) throws Exception {
        return null;
    }
    
    public void validate(SampleManager man) throws Exception {
        
    }
    
    private SampleLocal getSampleLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleLocal)ctx.lookup("openelis/SampleBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
