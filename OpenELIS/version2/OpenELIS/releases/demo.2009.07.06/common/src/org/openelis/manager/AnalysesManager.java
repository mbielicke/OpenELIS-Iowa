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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.AnalysisTestDO;
import org.openelis.gwt.common.RPC;

public class AnalysesManager implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected Integer                           sampleItemId;
    protected ArrayList<Item>                   items;
    protected boolean                           cached;
    protected transient AnalysesManagerIOInt manager;

    class Item implements Serializable{
        private static final long   serialVersionUID = 1L;
        AnalysisTestDO              analysis;
        AnalysisQaEventsManager     qaEvents;
        NotesManager                notes;
        StorageManager      storage;
    }

    public static AnalysesManager getInstance() {
        AnalysesManager sm;

        sm = new AnalysesManager();
        sm.items = new ArrayList<Item>();

        return sm;
    }
    
    public int count(){
        fetch();
        
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
    
    //analysis
    public AnalysisTestDO getAnalysisAt(int i) {
        fetch();
        
        return getItem(i).analysis;
    }
    
    public void setAnalysisAt(AnalysisTestDO analysis, int i) {
        getItem(i).analysis = analysis;
    }
    
    //qaevents
    public AnalysisQaEventsManager getQAEventsAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.qaEvents == null) {
            item.qaEvents = AnalysisQaEventsManager.getInstance();
            item.qaEvents.setAnalysisId(item.analysis.getId());
        }

        return item.qaEvents;
    }
    
    public void setQAEventsAt(AnalysisQaEventsManager qaEvents, int i) {
        getItem(i).qaEvents = qaEvents;
    }
    
    //notes
    public NotesManager getNotesAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.notes == null) {
            item.notes = NotesManager.getInstance();
            item.notes.setReferenceId(item.analysis.getId());
            //FIXME item.notes.setReferenceTableId(referenceTableId);
        }

        return item.notes;
    }
    
    public void setNotesAt(NotesManager notes, int i) {
        getItem(i).notes = notes;
    }
    
    //storage
    public StorageManager getStorageAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.storage == null) {
            item.storage = StorageManager.getInstance();
            item.storage.setReferenceId(item.analysis.getId());
            //item.storage.setReferenceTableId(referenceTableId);
        }

        return item.storage;
    }
    
    public void setStorageAt(StorageManager storage, int i) {
        getItem(i).storage = storage;
    }
    
    //item
    private Item getItem(int i) {
        fetch();
        return (Item)items.get(i);
    }
    
    //manager methods
    public void update() {
        manager().update(this);
    }

    protected void fetch() {
        if (cached)
            return;

        cached = true;
        List analyses = manager().fetch(sampleItemId);

        for (int i = 0; i < analyses.size(); i++) {
            Item item = new Item();
            item.analysis = (AnalysisTestDO)analyses.get(i);
            items.add(item);
        }
    }
    
    private AnalysesManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getAnalysesManagerIO();

        return manager;
    }
}