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

import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.common.RPC;

public class SampleItemsManager implements RPC {

    private static final long                   serialVersionUID = 1L;

    protected Integer                           sampleId;
    protected ArrayList<Item>                   items;
    protected boolean                           cached;
    protected transient SampleItemsManagerIOInt manager;

    class Item implements Serializable{
        private static final long serialVersionUID = 1L;
        SampleItemDO    sampleItem;
        StorageManager  storage;
        AnalysesManager analysis;
    }

    /**
     * Creates a new instance of this object.
     */
    public static SampleItemsManager getInstance() {
        SampleItemsManager sim;

        sim = new SampleItemsManager();
        sim.items = new ArrayList<Item>();

        return sim;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleItemsManager findBySampleId(Integer sampleId) {
        SampleItemsManager sim = new SampleItemsManager();

        sim.setSampleId(sampleId);
        sim.fetch();

        return sim;
    }
    
    public int count(){
        fetch();
        return items.size();
    }
    
    // getters/setters of child objects
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public SampleItemDO add(){
        Item newItem = new Item();
        newItem.sampleItem = new SampleItemDO();
        items.add(newItem);
        
        return newItem.sampleItem;
    }

    // sample item
    public SampleItemDO getSampleItemAt(int i) {
        fetch();
        return getItem(i).sampleItem;

    }

    public void setSampleItemAt(SampleItemDO sampleItem, int i) {
        getItem(i).sampleItem = sampleItem;
    }

    // storage
    public StorageManager getStorageAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.storage == null) {
            item.storage = StorageManager.getInstance();
            item.storage.setReferenceId(item.sampleItem.getId());
            //item.storage.setReferenceTableId(referenceTableId);
        }

        return item.storage;

    }

    public void setStorageAt(StorageManager storage, int i) {
        getItem(i).storage = storage;
    }

    // analysis
    public AnalysesManager getAnalysisAt(int i) {
        Item item;

        fetch();
        item = getItem(i);

        if (item.analysis == null) {
            item.analysis = AnalysesManager.getInstance();
            item.analysis.setSampleItemId(item.sampleItem.getId());
        }

        return item.analysis;
    }

    public void setAnalysisAt(AnalysesManager analysis, int i) {
        getItem(i).analysis = analysis;
    }

    private Item getItem(int i) {
        fetch();
        return (Item)items.get(i);
    }

    public SampleItemsManagerIOInt getManager() {
        return manager;
    }

    public void setManager(SampleItemsManagerIOInt manager) {
        this.manager = manager;
    }

    public void validate() {

    }

    // manager methods
    public Integer update() {
        return manager().update(this);
    }

    protected void fetch() {
        if (cached)
            return;

        cached = true;
        List sampleItems = manager().fetch(sampleId);

        for (int i = 0; i < sampleItems.size(); i++) {
            Item item = new Item();
            item.sampleItem = (SampleItemDO)sampleItems.get(i);
            items.add(item);
        }
    }

    private SampleItemsManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getSampleItemManagerIO();

        return manager;
    }
}
