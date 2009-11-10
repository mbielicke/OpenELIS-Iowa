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

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.SampleLocal;

public class SampleManagerProxy {
    public SampleManager add(SampleManager man) throws Exception {
        Integer sampleId, sampleRefId, sampleInternalRefId;
        SampleLocal sl = getSampleLocal();
        sl.add(man.getSample());
        
        sampleId = man.getSample().getId();
        sampleRefId = ReferenceTable.SAMPLE;
        sampleInternalRefId = ReferenceTable.SAMPLE_INTERNAL_NOTE;
        
        man.getDomainManager().setSampleId(sampleId);
        man.getDomainManager().add();
        
        man.getSampleItems().setSampleId(sampleId);
        man.getSampleItems().add();
        
        man.getOrganizations().setSampleId(sampleId);
        man.getOrganizations().add();
        
        man.getProjects().setSampleId(sampleId);
        man.getProjects().add();
        
        man.getQaEvents().setSampleId(sampleId);
        man.getQaEvents().add();
        
        man.getAuxData().setReferenceTableId(sampleRefId);
        man.getAuxData().setReferenceId(sampleId);
        man.getAuxData().add();
        
        man.getInternalNotes().setReferenceTableId(sampleInternalRefId);
        man.getInternalNotes().setReferenceId(sampleId);
        man.getInternalNotes().add();
        
        man.getExternalNote().setReferenceTableId(sampleRefId);
        man.getExternalNote().setReferenceId(sampleId);
        man.getExternalNote().add();
        
        return man;
    }

    public SampleManager update(SampleManager man) throws Exception {
        Integer sampleId, sampleRefId, sampleInternalRefId;
        SampleLocal sl = getSampleLocal();
        sl.update(man.getSample());
        
        sampleId = man.getSample().getId();
        sampleRefId = ReferenceTable.SAMPLE;
        sampleInternalRefId = ReferenceTable.SAMPLE_INTERNAL_NOTE;
        
        man.getDomainManager().setSampleId(sampleId);
        man.getDomainManager().update();
        
        man.getSampleItems().setSampleId(sampleId);
        man.getSampleItems().update();
        
        man.getOrganizations().setSampleId(sampleId);
        man.getOrganizations().update();
        
        man.getProjects().setSampleId(sampleId);
        man.getProjects().update();
        
        man.getQaEvents().setSampleId(sampleId);
        man.getQaEvents().update();
        
        man.getAuxData().setReferenceTableId(sampleRefId);
        man.getAuxData().setReferenceId(sampleId);
        man.getAuxData().update();
        
        man.getInternalNotes().setReferenceTableId(sampleInternalRefId);
        man.getInternalNotes().setReferenceId(sampleId);
        man.getInternalNotes().update();
        
        man.getExternalNote().setReferenceTableId(sampleRefId);
        man.getExternalNote().setReferenceId(sampleId);
        man.getExternalNote().update();
        
        return man;
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
        
        SampleItemManager sim = sm.getSampleItems();
        
        for(int i=0; i<sim.count(); i++)
            sim.getAnalysisAt(i);
        
        return sm;
    }
    
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
        throw new UnsupportedOperationException();
    }
    
    public SampleManager abort(Integer sampleId) throws Exception {
        throw new UnsupportedOperationException();    }
    
    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        
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
