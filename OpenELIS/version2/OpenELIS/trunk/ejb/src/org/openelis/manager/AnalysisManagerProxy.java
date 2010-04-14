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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.LoginLocal;
import org.openelis.manager.AnalysisManager.AnalysisListItem;

public class AnalysisManagerProxy {
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        AnalysisViewDO anDO;
        ArrayList<AnalysisViewDO> items;
        AnalysisManager man;
        
        items = analysisLocal().fetchBySampleItemId(sampleItemId);
        man = AnalysisManager.getInstance();
        
        for(int i=0; i<items.size(); i++){
            anDO = items.get(i);
            man.addAnalysis(anDO);
        }
        
        man.setSampleItemId(sampleItemId);
        
        return man;
    }
    
    public int add(AnalysisManager man, HashMap<Integer, Integer> idHash) throws Exception {
        int numOfUnresolved;
        AnalysisViewDO analysisDO;
        
        numOfUnresolved = 0;
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
                    
                    if(idHash.containsKey(oldId))
                        numOfUnresolved--;    
                    
                    idHash.put(oldId, analysisDO.getId());
                    idHash.put(analysisDO.getId(), null);
                }else{
                    idHash.put(analysisDO.getId(), null);
                    numOfUnresolved++;
                }
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
        
        return numOfUnresolved;
    }
    
    public int update(AnalysisManager man, HashMap<Integer, Integer> idHash) throws Exception {
        int numOfUnresolved;
        AnalysisViewDO analysisDO;
        
        numOfUnresolved = 0;
        for(int i=0; i<man.count(); i++){
            analysisDO = man.getAnalysisAt(i);

            if(analysisDO.getPreAnalysisId() == null){
                if(!idHash.containsKey(analysisDO.getId())){
                    Integer oldId = analysisDO.getId();
                    
                    if(oldId != null && oldId > 0)
                        update(man, analysisDO, i);
                    else
                        add(man, analysisDO, i);
                    
                    idHash.put(oldId, analysisDO.getId());
                    idHash.put(analysisDO.getId(), null);
                }
            }else if(analysisDO.getPreAnalysisId() < 0){
                Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                
                if(prepId != null){
                    Integer oldId = analysisDO.getId();
                    analysisDO.setPreAnalysisId(prepId);
                    
                    if(oldId != null && oldId > 0)
                        update(man, analysisDO, i);
                    else
                        add(man, analysisDO, i);
                    
                    if(idHash.containsKey(oldId))
                        numOfUnresolved--;    
                    
                    idHash.put(oldId, analysisDO.getId());
                    idHash.put(analysisDO.getId(), null);
                }else{
                    idHash.put(analysisDO.getId(), null);
                    numOfUnresolved++;
                }
            }else if(!idHash.containsKey(analysisDO.getId())){
                Integer prepId = idHash.get(analysisDO.getPreAnalysisId());
                if(prepId == null){
                    Integer oldId = analysisDO.getId();
                    
                    if(oldId != null && oldId > 0)
                        update(man, analysisDO, i);
                    else
                        add(man, analysisDO, i);
                    
                    idHash.put(oldId, analysisDO.getId());
                    
                    if(!oldId.equals(analysisDO.getId()))
                        idHash.put(analysisDO.getId(), null);
               }
            }
        }
        
        return numOfUnresolved;
    }
    
    private void add(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        Integer analysisRefId;
        AnalysisListItem item;
        
        analysisRefId = ReferenceTable.ANALYSIS;
        
        analysisDO.setSampleItemId(man.getSampleItemId());
        analysisLocal().add(analysisDO);
        
        item = man.getItemAt(i);
        
        if(item.analysisResult != null){
            man.getAnalysisResultAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisResultAt(i).add();
        }
        
        if(item.qaEvents != null){
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).add();
        }
        
        if(item.analysisInternalNotes != null){
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(analysisRefId);
            man.getInternalNotesAt(i).add();
        }
        
        if(item.analysisExternalNote != null){
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(analysisRefId);
            man.getExternalNoteAt(i).add();
        }
        
        if(item.storages != null){
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(analysisRefId);
            man.getStorageAt(i).add();
        }
    }
    
    private void update(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        Integer analysisRefId;
        AnalysisListItem item;
        
        analysisRefId = ReferenceTable.ANALYSIS;

        if(analysisDO.getId() == null){
            analysisDO.setSampleItemId(man.getSampleItemId());
            analysisLocal().add(analysisDO);
        }else
            analysisLocal().update(analysisDO);
        
        item = man.getItemAt(i);
        if(item.analysisResult != null){
            man.getAnalysisResultAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisResultAt(i).update();
        }
        
        if(item.qaEvents != null){
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).update();
        }
        
        if(item.analysisInternalNotes != null){
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(analysisRefId);
            man.getInternalNotesAt(i).update();
        }
        
        if(item.analysisExternalNote != null){
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(analysisRefId);
            man.getExternalNoteAt(i).update();
        }
        
        if(item.storages != null){
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(analysisRefId);
            man.getStorageAt(i).update();
        }
    }

    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin, end);
    }
    
    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        validate(man, null, null, errorsList);
    }

    public void validate(AnalysisManager man, String sampleItemSequence, Integer sampleTypeId, ValidationErrorsList errorsList) throws Exception {
        AnalysisListItem item;
        Integer cancelledStatusId;
        AnalysisViewDO analysisDO;
        TestManager testMan;
        
        cancelledStatusId = dictionaryLocal().fetchBySystemName("analysis_cancelled").getId();
        
        for(int i=0; i<man.count(); i++){
            analysisDO = man.getAnalysisAt(i);
            testMan = man.getTestAt(i);
            
            if(analysisDO.getTestId() == null)
                errorsList.add(new FormErrorException("analysisTestIdMissing", sampleItemSequence));
            
            if(analysisDO.getTestId() != null && analysisDO.getSectionId() == null)
                errorsList.add(new FormErrorException("analysisSectionIdMissing", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            item = man.getItemAt(i);
            //validate the children
            
            //we want to always run thorugh validate so it loads the data
            //if the user didnt click on the tab
            man.getAnalysisResultAt(i).validate(analysisDO, errorsList);
            
            if(item.qaEvents != null)
                man.getQAEventAt(i).validate(errorsList);
            
            if(item.storages != null)
                man.getStorageAt(i).validate(errorsList);
        }
    }
    
    public SecurityUtil getSecurityUtil() {
        return loginLocal().getSecurityUtil();
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception{
        DictionaryDO dictDO = dictionaryLocal().fetchBySystemName(systemName);
        
        return dictDO.getId();
    }
    
    private AnalysisLocal analysisLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (AnalysisLocal)ctx.lookup("openelis/AnalysisBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private static DictionaryLocal dictionaryLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    private static LoginLocal loginLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (LoginLocal)ctx.lookup("openelis/LoginBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
    public SectionViewDO getSectionFromId(Integer sectionId) throws Exception {
        assert false : "not supported";
        return null;
    }
}
