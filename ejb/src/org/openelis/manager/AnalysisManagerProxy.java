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
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.manager.AnalysisManager.AnalysisListItem;

public class AnalysisManagerProxy {
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        AnalysisLocal al = getAnalysisLocal();
        ArrayList<AnalysisViewDO> items = (ArrayList<AnalysisViewDO>)al.fetchBySampleItemId(sampleItemId);
        AnalysisManager am = AnalysisManager.getInstance();

        for(int i=0; i<items.size(); i++)
            am.addAnalysis(items.get(i));
        
        am.setSampleItemId(sampleItemId);
        
        return am;
    }
    
    public AnalysisManager add(AnalysisManager man) throws Exception {
        HashMap<Integer, Integer> idHash = new HashMap<Integer, Integer>();
        AnalysisViewDO analysisDO;
        
        boolean notDone = false;
        do{
            notDone = false;
            for(int i=0; i<man.count(); i++){
                analysisDO = man.getAnalysisAt(i);

                if(analysisDO.getPreAnalysisId() == null){
                    if(!idHash.containsKey(analysisDO.getId())){
                        Integer oldId = analysisDO.getId();
                        add(man, analysisDO, i);

                        idHash.put(oldId, analysisDO.getId());
                        idHash.put(analysisDO.getId(), null);
                    }
                }else if(analysisDO.getPreAnalysisId() < 0){
                    Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                    
                    if(prepId != null){
                        Integer oldId = analysisDO.getId();
                        analysisDO.setPreAnalysisId(prepId);
                        add(man, analysisDO, i);
                        
                        idHash.put(oldId, analysisDO.getId());
                        idHash.put(analysisDO.getId(), null);
                    }else
                        notDone = true;
                }else if(!idHash.containsKey(analysisDO.getId())){
                    Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                    if(prepId == null){
                        Integer oldId = analysisDO.getId();
                        
                        add(man, analysisDO, i);
                        
                        idHash.put(oldId, analysisDO.getId());
                        
                        if(!oldId.equals(analysisDO.getId()))
                            idHash.put(analysisDO.getId(), null);
                   }
                }
            }
        }while(notDone);
        
        return man;
    }
    
    public AnalysisManager update(AnalysisManager man) throws Exception {
        HashMap<Integer, Integer> idHash = new HashMap<Integer, Integer>();
        AnalysisViewDO analysisDO;
        
        boolean notDone = false;
        do{
            notDone = false;
            for(int i=0; i<man.count(); i++){
                analysisDO = man.getAnalysisAt(i);

                if(analysisDO.getPreAnalysisId() == null){
                    if(!idHash.containsKey(analysisDO.getId())){
                        Integer oldId = analysisDO.getId();
                        update(man, analysisDO, i);

                        idHash.put(oldId, analysisDO.getId());
                        idHash.put(analysisDO.getId(), null);
                    }
                }else if(analysisDO.getPreAnalysisId() < 0){
                    Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                    
                    if(prepId != null){
                        Integer oldId = analysisDO.getId();
                        analysisDO.setPreAnalysisId(prepId);
                        update(man, analysisDO, i);
                        
                        idHash.put(oldId, analysisDO.getId());
                        idHash.put(analysisDO.getId(), null);
                    }else
                        notDone = true;
                }else if(!idHash.containsKey(analysisDO.getId())){
                    Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                    if(prepId == null){
                        Integer oldId = analysisDO.getId();
                        
                        update(man, analysisDO, i);
                        
                        idHash.put(oldId, analysisDO.getId());
                        
                        if(!oldId.equals(analysisDO.getId()))
                            idHash.put(analysisDO.getId(), null);
                   }
                }
            }
       }while(notDone);
        
        return man;
    }
    
    private void add(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        Integer anRefId, anIntRefId;
        AnalysisLocal al = getAnalysisLocal();
        
        anRefId = ReferenceTable.ANALYSIS;
        anIntRefId = ReferenceTable.ANALYSIS_INTERNAL_NOTE;
        
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
    
    private void update(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        Integer anRefId, anIntRefId;
        AnalysisListItem item;
        AnalysisLocal al = getAnalysisLocal();
        
        anRefId = ReferenceTable.ANALYSIS;
        anIntRefId = ReferenceTable.ANALYSIS_INTERNAL_NOTE;

        if(analysisDO.getId() == null){
            analysisDO.setSampleItemId(man.getSampleItemId());
            al.add(analysisDO);
        }else
            al.update(analysisDO);
        
        item = man.getItemAt(i);
        if(item.qaEvents != null){
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).update();
        }
        
        if(item.analysisInternalNotes != null){
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(anIntRefId);
            man.getInternalNotesAt(i).update();
        }
        
        if(item.analysisExternalNote != null){
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(anRefId);
            man.getExternalNoteAt(i).update();
        }
        
        if(item.storage != null){
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(anRefId);
            man.getStorageAt(i).update();
        }
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
