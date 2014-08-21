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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.widget.Window;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.manager.SampleSDWISManager;
import org.openelis.manager.StorageManager;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;


public abstract class SampleHistoryUtility {
    
    private SampleManager     manager;
    private Window      window;
    
    public SampleHistoryUtility(Window window) {
        this.window = window;
    }
    
    public void historySample(){
        IdNameVO hist;
        
        window.setBusy();
        hist = new IdNameVO(manager.getSample().getId(), manager.getSample().getAccessionNumber().toString());
        HistoryScreen.showHistory(OpenELIS.consts.get("historySample"),
                                  ReferenceTable.SAMPLE, hist);
        window.clearStatus();
    }
    
    public void historySampleEnvironmental(){
        IdNameVO hist;
        SampleEnvironmentalManager envMan;
        
        window.setBusy();
        try{
        envMan = (SampleEnvironmentalManager)manager.getDomainManager();
        hist = new IdNameVO(envMan.getEnvironmental().getId(), envMan.getEnvironmental().getLocation());
        HistoryScreen.showHistory(OpenELIS.consts.get("historySampleEnvironmental"),
                                  ReferenceTable.SAMPLE_ENVIRONMENTAL, hist);
        }catch(Exception e){
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleEnvironmental: "+e.getMessage());
        }
        
        window.clearStatus();
    }
    
    public void historySamplePrivateWell(){
        IdNameVO hist;
        SamplePrivateWellManager wellMan;
        
        window.setBusy();
        try{
        wellMan = (SamplePrivateWellManager)manager.getDomainManager();
        hist = new IdNameVO(wellMan.getPrivateWell().getId(), wellMan.getPrivateWell().getLocation());
        HistoryScreen.showHistory(OpenELIS.consts.get("historySamplePrivateWell"),
                                  ReferenceTable.SAMPLE_PRIVATE_WELL, hist);
        }catch(Exception e){
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySamplePrivateWell: "+e.getMessage());
        }
        
        window.clearStatus();
    }
    
    public void historySampleSDWIS(){
        IdNameVO hist;
        SampleSDWISManager sdwisMan;
        
        window.setBusy();
        try{
        sdwisMan = (SampleSDWISManager)manager.getDomainManager();
        hist = new IdNameVO(sdwisMan.getSDWIS().getId(), sdwisMan.getSDWIS().getLocation());
        HistoryScreen.showHistory(OpenELIS.consts.get("historySampleSDWIS"),
                                  ReferenceTable.SAMPLE_SDWIS, hist);
        }catch(Exception e){
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleSDWIS: "+e.getMessage());
        }
        
        window.clearStatus();
    }
    
    public void historySampleProject(){
        int i, count;
        IdNameVO refVoList[];
        SampleProjectManager man;
        SampleProjectViewDO data;

        window.setBusy();
        try {
            man = manager.getProjects();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getProjectAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getProjectName());
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historySampleProject"),
                                      ReferenceTable.SAMPLE_PROJECT, refVoList);
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleProject: "+e.getMessage());
        }
        
        window.clearStatus();
    } 
     
    public void historySampleOrganization(){
        int i, count;
        IdNameVO refVoList[];
        SampleOrganizationManager man;
        SampleOrganizationViewDO data;

        window.setBusy();
        try {
            man = manager.getOrganizations();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getOrganizationAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getOrganizationName());
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historySampleOrganization"),
                                      ReferenceTable.SAMPLE_ORGANIZATION, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleProject: "+e.getMessage());
        }        
    }
    
    public void historySampleItem(){
        int i, count;
        IdNameVO refVoList[];
        SampleItemManager man;
        SampleItemViewDO data;
        String container;

        window.setBusy();
        try {
            man = manager.getSampleItems();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getSampleItemAt(i);
                container = data.getContainer();
                if (container == null)
                    container = "";
                else 
                    container += " - ";
                refVoList[i] = new IdNameVO(data.getId(), data.getItemSequence()+container);
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historySampleItem"),
                                      ReferenceTable.SAMPLE_ITEM, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleItem: "+e.getMessage());
        }        
    }
    
    public void historyAnalysis(){
        int i, j, k, listIndex, itemCount, anCount;
        IdNameVO refVoList[];
        SampleItemManager man;
        AnalysisManager anMan;
        AnalysisViewDO data;

        window.setBusy();
        try {
            man = manager.getSampleItems();
            itemCount = man.count();
            
            //figure out total # of analyses
            anCount = 0;
            for(i=0; i<itemCount; i++)
                anCount += man.getAnalysisAt(i).count();
            
            refVoList = new IdNameVO[anCount];
            listIndex = 0;
            for (j = 0; j < itemCount; j++ ) {
                anMan = man.getAnalysisAt(j);
                anCount = anMan.count();
                
                for(k=0; k < anCount; k++){
                    data = anMan.getAnalysisAt(k);
                    refVoList[listIndex] = new IdNameVO(data.getId(), data.getTestName()+" : "+data.getMethodName());
                    listIndex++;
                }
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historyAnalysis"),
                                      ReferenceTable.ANALYSIS, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("historyAnalysis: "+e.getMessage());
            window.clearStatus();
        }        
    }
    
    public abstract void historyCurrentResult();
    
    public void historyStorage(){
        int i, j, k, itemCount, anCount, storageCount;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;
        SampleItemManager man;
        AnalysisManager anMan;
        StorageManager storageMan;
        StorageViewDO data;

        window.setBusy();
        try {
            man = manager.getSampleItems();
            itemCount = man.count();
            
            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < itemCount; i++ ) {
                anMan = man.getAnalysisAt(i);
                anCount = anMan.count();
                storageMan = man.getStorageAt(i);
                storageCount = storageMan.count();
                
                for(j=0; j<storageCount; j++){
                    data = storageMan.getStorageAt(j);
                    refVoArrayList.add(new IdNameVO(data.getId(), data.getStorageLocation()));
                }
                
                for(j=0; j < anCount; j++){
                    storageMan = anMan.getStorageAt(j);
                    storageCount = storageMan.count();
                    
                    for(k=0; k<storageCount; k++){
                        data = storageMan.getStorageAt(k);
                        refVoArrayList.add(new IdNameVO(data.getId(), data.getStorageLocation()));
                    }
                }
            }
            
            refVoList = new IdNameVO[refVoArrayList.size()];
            for(i=0; i<refVoArrayList.size(); i++)
                refVoList[i] = refVoArrayList.get(i);
            
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historyStorage"),
                                      ReferenceTable.STORAGE, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historyStorage: "+e.getMessage());
        }        
    }
    
    public void historySampleQA(){
        int i, count;
        IdNameVO refVoList[];
        SampleQaEventManager man;
        SampleQaEventViewDO data;

        window.setBusy();
        try {
            man = manager.getQaEvents();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getSampleQAAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getQaEventName());
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historySampleItem"),
                                      ReferenceTable.SAMPLE_ITEM, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historySampleQA: "+e.getMessage());
        }        
    }
    
    public void historyAnalysisQA(){
        int i, j, k, itemCount, anCount, qaCount;
        ArrayList<IdNameVO> refVoArrayList;
        IdNameVO[] refVoList;
        SampleItemManager man;
        AnalysisManager anMan;
        AnalysisQaEventManager qaMan;
        AnalysisQaEventViewDO data;

        window.setBusy();
        try {
            man = manager.getSampleItems();
            itemCount = man.count();
            
            refVoArrayList = new ArrayList<IdNameVO>();
            for (i = 0; i < itemCount; i++ ) {
                anMan = man.getAnalysisAt(i);
                anCount = anMan.count();
                
                for(j=0; j < anCount; j++){
                    qaMan = anMan.getQAEventAt(j);
                    qaCount = qaMan.count();
                    
                    for(k=0; k<qaCount; k++){
                        data = qaMan.getAnalysisQAAt(k);
                        refVoArrayList.add(new IdNameVO(data.getId(), data.getQaEventName()));
                    }
                }
            }
            
            refVoList = new IdNameVO[refVoArrayList.size()];
            for(i=0; i<refVoArrayList.size(); i++)
                refVoList[i] = refVoArrayList.get(i);
            
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historyAnalysisQA"),
                                      ReferenceTable.ANALYSIS_QAEVENT, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historyAnalysis: "+e.getMessage());
        }        
    }
    
    public void historyAuxData(){
        int i, count;
        IdNameVO refVoList[];
        AuxDataManager man;
        AuxDataViewDO data;
        AuxFieldViewDO fieldDO;

        window.setBusy();
        try {
            man = manager.getAuxData();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getAuxDataAt(i);
                fieldDO = man.getFieldsAt(i).getAuxFieldAt(0);
                refVoList[i] = new IdNameVO(data.getId(), fieldDO.getAnalyteName());
            }
            
            HistoryScreen.showHistory(OpenELIS.consts.get("historyAuxData"),
                                      ReferenceTable.AUX_DATA, refVoList);
            window.clearStatus();
        } catch (Exception e) {
            window.clearStatus();
            com.google.gwt.user.client.Window.alert("historyAuxData: "+e.getMessage());
        }        
    }
    
    public void setManager(SampleManager manager) {
        this.manager = manager;
    }
}
