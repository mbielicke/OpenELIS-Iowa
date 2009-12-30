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

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

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
    public static SampleItemManager fetchBySampleId(Integer sampleId) throws Exception {
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
    public SampleItemViewDO getSampleItemAt(int i) {
        return getItemAt(i).sampleItem;

    }

    public void setSampleItemAt(SampleItemViewDO sampleItem, int i) {
        getItemAt(i).sampleItem = sampleItem;
    }
    
    public void addSampleItem(SampleItemViewDO sampleItem){
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
    
    public void setChangedAt(boolean changed, int i){
        getItemAt(i).changed = changed;
    }
    
    public boolean hasChangedAt(int i){
        return getItemAt(i).changed;
    }

    // storage
    public StorageManager getStorageAt(int i) throws Exception {
        SampleItemListItem item = getItemAt(i);

        if (item.storage == null) {
            if(item.sampleItem != null && item.sampleItem.getId() != null){
                try{
                    item.storage = StorageManager.fetchByRefTableRefId(ReferenceTable.SAMPLE_ITEM, item.sampleItem.getId());
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
        getItemAt(i).storage = storage;
    }

    // analysis
    public AnalysisManager getAnalysisAt(int i) throws Exception {
        return getAnalysisAt(i, false);
    }
    
    public AnalysisManager getAnalysisAtForUpdate(int i) throws Exception {
        return getAnalysisAt(i, true);
    }
    
    private AnalysisManager getAnalysisAt(int i, boolean update) throws Exception {
        SampleItemListItem item = getItemAt(i);

        if (item.analysis == null) {
            if(item.sampleItem != null && item.sampleItem.getId() != null){
                try{
                    if(update)
                        item.analysis = AnalysisManager.fetchBySampleItemIdForUpdate(item.sampleItem.getId());
                    else
                        item.analysis = AnalysisManager.fetchBySampleItemId(item.sampleItem.getId());
                    
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
    
    public int getIndex(SampleItemViewDO itemDO){
        for(int i=0; i<count(); i++)
            if(items.get(i).sampleItem == itemDO)
                return i;
        
        return -1;
    }

    public void setAnalysisAt(AnalysisManager analysis, int i) {
        getItemAt(i).analysis = analysis;
    }

    public SampleItemListItem getItemAt(int i) {
        return (SampleItemListItem)items.get(i);
    }

    // service methods
    public SampleItemManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleItemManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();
        
        proxy().validate(this, errorsList);
        
        if(errorsList.size() > 0)
            throw errorsList;
    }
    
    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
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
    
    static class SampleItemListItem implements RPC {
        private static final long serialVersionUID = 1L;

        boolean changed = false;
        SampleItemViewDO    sampleItem;
        StorageManager  storage;
        AnalysisManager analysis;
    }
}
