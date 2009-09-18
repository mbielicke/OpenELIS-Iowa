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

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.utils.ReferenceTableCache;

public class AnalysisManagerProxy {
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        AnalysisLocal al = getAnalysisLocal();
        ArrayList<AnalysisViewDO> items = (ArrayList<AnalysisViewDO>)al.fetchBySampleItemId(sampleItemId);
        AnalysisManager am = AnalysisManager.getInstance();

        for(int i=0; i<items.size(); i++)
            am.addAnalysis(items.get(i));
        
        am.setSampleItemId(sampleItemId);
        am.setAnalysisReferenceId(ReferenceTableCache.getReferenceTable("analysis"));
        am.setAnalysisInternalReferenceTableId(ReferenceTableCache.getReferenceTable("analysis_internal_note"));

        return am;
    }
    
    public AnalysisManager add(AnalysisManager man) throws Exception {
        System.out.println("inside analysis add");
        Integer anRefId, anIntRefId;
        AnalysisLocal al = getAnalysisLocal();
        
        AnalysisViewDO analysisDO;
        anRefId = ReferenceTableCache.getReferenceTable("analysis");
        anIntRefId = ReferenceTableCache.getReferenceTable("analysis_internal_note");
        
        for(int i=0; i<man.count(); i++){
            analysisDO = man.getAnalysisAt(i);
            analysisDO.setSampleItemId(man.getSampleItemId());
            
            al.add(analysisDO);
            
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).add();
            
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(anIntRefId);
            man.getInternalNotesAt(i).add();
            
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(anRefId);
            man.getExternalNoteAt(i).add();
            
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(anRefId);
            man.getStorageAt(i).add();
        }
        
        return man;
    }
    
    public AnalysisManager update(AnalysisManager man) throws Exception {
        Integer anRefId, anIntRefId;
        AnalysisLocal al = getAnalysisLocal();
        
        AnalysisViewDO analysisDO;
        anRefId = ReferenceTableCache.getReferenceTable("analysis");
        anIntRefId = ReferenceTableCache.getReferenceTable("analysis_internal_note");
        
        for(int j=0; j<man.deleteCount(); j++)
            al.delete(man.getDeletedAt(j).analysis);
        
        for(int i=0; i<man.count(); i++){
            analysisDO = man.getAnalysisAt(i);
            
            if(analysisDO.getId() == null){
                analysisDO.setSampleItemId(man.getSampleItemId());
                al.add(analysisDO);
            }else
                al.update(analysisDO);
            
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).update();
            
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(anIntRefId);
            man.getInternalNotesAt(i).update();
            
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(anRefId);
            man.getExternalNoteAt(i).update();
            
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(anRefId);
            man.getStorageAt(i).update();
        }

        return man;
    }
    
    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        
    }

    public void validate(AnalysisManager man, String sampleItemSequence, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    private AnalysisLocal getAnalysisLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (AnalysisLocal)ctx.lookup("openelis/AnalysisBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
