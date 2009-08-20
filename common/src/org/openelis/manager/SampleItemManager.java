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

import org.openelis.domain.SampleItemDO;
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.RPC;

public class SampleItemManager implements RPC {

    private static final long                   serialVersionUID = 1L;

    protected Integer                           sampleId;
    protected ArrayList<SampleItemListItem>                   items;
    protected ArrayList<SampleItemListItem>                   deletedList;

    protected transient static SampleItemManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleItemManager getInstance() {
        SampleItemManager sim;

        sim = new SampleItemManager();
        sim.items = new ArrayList<SampleItemListItem>();

        return sim;
    }

    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleItemManager findBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }
    
    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }
    
    // getters/setters of child objects
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    // sample item
    public SampleItemDO getSampleItemAt(int i) {
        return getItem(i).sampleItem;

    }

    public void setSampleItemAt(SampleItemDO sampleItem, int i) {
        getItem(i).sampleItem = sampleItem;
    }
    
    public void addSampleItem(SampleItemDO sampleItem){
        SampleItemListItem item = new SampleItemListItem();
        item.sampleItem = sampleItem;
        items.add(item);
    }
    
    public void removeSampleItemAt(int i){
        if(items == null || i >= items.size())
            return;
        
        SampleItemListItem tmpList = items.remove(i);
        
        if(deletedList == null)
            deletedList = new ArrayList<SampleItemListItem>();
        
        if(tmpList.sampleItem.getId() != null)
            deletedList.add(tmpList);
    }

    // storage
    public StorageManager getStorageAt(int i) throws Exception {
        SampleItemListItem item = getItem(i);

        if (item.storage == null) {
            if(item.sampleItem != null && item.sampleItem.getId() != null){
                try{
                    item.storage = StorageManager.findBySampleItemId(item.sampleItem.getId());
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

    // analysis
    public AnalysisManager getAnalysisAt(int i) throws Exception {
        SampleItemListItem item = getItem(i);

        if (item.analysis == null) {
            if(item.sampleItem != null && item.sampleItem.getId() != null){
                try{
                    item.analysis = AnalysisManager.findBySampleItemId(item.sampleItem.getId());
                }catch(NotFoundException e){
                    //ignore
                }catch(Exception e){
                    throw e;
                }
            }
        }
            
        if(item.analysis == null)
            item.analysis = AnalysisManager.getInstance();
    
        return item.analysis;
    }
    
    public int getIndex(SampleItemDO itemDO){
        for(int i=0; i<count(); i++)
            if(items.get(i).sampleItem == itemDO)
                return i;
        
        return -1;
    }

    public void setAnalysisAt(AnalysisManager analysis, int i) {
        getItem(i).analysis = analysis;
    }

    private SampleItemListItem getItem(int i) {
        return (SampleItemListItem)items.get(i);
    }

    // service methods
    public SampleItemManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleItemManager update() throws Exception {
        return proxy().update(this);
    }

    private static SampleItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleItemManagerProxy();

        return proxy;
    }
    
    //these are friendly methods so only managers and proxies can call this method
    //ArrayList<SampleItemListItem> getItems() {
    //    return items;
   // }

   // void setItems(ArrayList<SampleItemListItem> items) {
   //     this.items = items;
   // }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleItemListItem getDeletedAt(int i) {
        return deletedList.get(i);
    }
}
