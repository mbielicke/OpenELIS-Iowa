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

import org.openelis.domain.AnalysisTestDO;
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.RPC;

public class AnalysisManager implements RPC, HasNotesInt {
    private static final long serialVersionUID = 1L;
    
    protected Integer                           sampleItemId, analysisReferenceId, analysisInternalReferenceTableId;
    protected ArrayList<AnalysisListItem>                   items, deletedList;
    
    protected transient static AnalysisManagerProxy proxy;


    public static AnalysisManager getInstance() {
        AnalysisManager sm;

        sm = new AnalysisManager();
        sm.items = new ArrayList<AnalysisListItem>();

        return sm;
    }
    
    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static AnalysisManager findBySampleItemId(Integer sampleItemId) throws Exception {
        return proxy().fetchBySampleItemId(sampleItemId);
    }
    
    public int count(){
        if(items == null)
            return 0;
        
        return items.size();
    }

    //getters/setters
    public Integer getSampleItemId() {
        return sampleItemId;
    }

    public void setSampleItemId(Integer sampleItemId) {
        this.sampleItemId = sampleItemId;
    }
    
    public Integer getAnalysisReferenceId() {
        return analysisReferenceId;
    }

    public void setAnalysisReferenceId(Integer analysisReferenceId) {
        this.analysisReferenceId = analysisReferenceId;
    }
    
    public Integer getAnalysisInternalReferenceTableId() {
        return analysisInternalReferenceTableId;
    }

    public void setAnalysisInternalReferenceTableId(Integer analysisInternalReferenceTableId) {
        this.analysisInternalReferenceTableId = analysisInternalReferenceTableId;
    }  
    
    public NoteManager getNotes() throws Exception {
      throw new UnsupportedOperationException();
    } 
    
    //analysis
    public AnalysisTestDO getAnalysisAt(int i) {
        return getItem(i).analysis;

    }

    public void setAnalysisAt(AnalysisTestDO analysis, int i) {
        getItem(i).analysis = analysis;
    }
    
    public void addAnalysis(AnalysisTestDO analysis){
        AnalysisListItem item = new AnalysisListItem();
        item.analysis = analysis;
        items.add(item);
    }
    
    public void removeAnalysisAt(int i){
        if(items == null || i >= items.size())
            return;
        
        AnalysisListItem tmpList = items.remove(i);
        
        if(deletedList == null)
            deletedList = new ArrayList<AnalysisListItem>();
        
        if(tmpList.analysis.getId() != null)
            deletedList.add(tmpList);
    }
    
    public int getIndex(AnalysisTestDO aDO){
        for(int i=0; i<count(); i++)
            if(items.get(i).analysis == aDO)
                return i;
        
        return -1;
    }
    
    //qaevents
    public AnalysisQaEventManager getQAEventAt(int i) throws Exception {
        return null;
        /*
        AnalysisListItem item = getItem(i);

        if (item.qaEvents == null) {
            if(item.analysis != null && item.analysis.getId() != null){
                try{
                    item.qaEvents = qa.findBySampleItemId(item.sampleItem.getId());
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
        if(item.analysis == null)
            item.analysis = AnalysisManager.getInstance();
    
        return item.analysis;*/
    }

    public void setQAEventAt(AnalysisQaEventManager qaEvent, int i) {
        getItem(i).qaEvents = qaEvent;
    }
    
    
    //internal notes
    public NoteManager getInternalNotesAt(int i) throws Exception {
        AnalysisListItem item = getItem(i);
        if(item.analysisInternalNotes == null){
            if(item.analysis != null && item.analysis.getId() != null && analysisInternalReferenceTableId != null){
                try{
                    item.analysisInternalNotes = NoteManager.findByRefTableRefId(analysisInternalReferenceTableId, item.analysis.getId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(item.analysisInternalNotes == null)
            item.analysisInternalNotes = NoteManager.getInstance();

        return item.analysisInternalNotes;
    }
    
    public void setInternalNotes(NoteManager notes, int i) {
        getItem(i).analysisInternalNotes = notes;
    }
    
    //external note
    public NoteManager getExternalNoteAt(int i) throws Exception {
        AnalysisListItem item = getItem(i);
        if(item.analysisExternalNote == null){
            if(item.analysis != null && item.analysis.getId() != null && analysisReferenceId != null){
                try{
                    item.analysisExternalNote = NoteManager.findByRefTableRefId(analysisReferenceId, item.analysis.getId());
                    
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
        
        if(item.analysisExternalNote == null)
            item.analysisExternalNote = NoteManager.getInstance();

        return item.analysisExternalNote;
    }
    
    public void setExternalNoteAt(NoteManager note, int i) {
        getItem(i).analysisExternalNote = note;
    }
    
    //storage
    public StorageManager getStorageAt(int i) throws Exception {
        AnalysisListItem item = getItem(i);

        if (item.storage == null) {
            if(item.analysis != null && item.analysis.getId() != null && analysisReferenceId != null){
                try{
                    item.storage = StorageManager.findByRefTableRefId(analysisReferenceId, item.analysis.getId());
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
        if(item.storage == null)
            item.storage = StorageManager.getInstance();
    
        return item.storage;
    }
    
    public void setStorageAt(StorageManager storage, int i) {
        getItem(i).storage = storage;
    }
    
    //item
    private AnalysisListItem getItem(int i) {
        return items.get(i);
    }
    
    // service methods
    public AnalysisManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    private static AnalysisManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisManagerProxy();

        return proxy;
    }
    
    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AnalysisListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }       
}