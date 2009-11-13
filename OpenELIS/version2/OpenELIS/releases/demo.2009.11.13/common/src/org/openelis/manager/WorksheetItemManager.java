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

import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class WorksheetItemManager implements RPC {
    
    private static final long                  serialVersionUID = 1L;
    protected Integer                          worksheetId;
    protected ArrayList<WorksheetItemListItem> items, deleted;
    
    protected transient static WorksheetItemManagerProxy proxy;
    
    /**
     * Creates a new instance of this object.
     */
    public static WorksheetItemManager getInstance() {
        return new WorksheetItemManager();
    }
    
    public int count() {
        if (items == null)
            return 0;
        
        return items.size();
    }
    
    public WorksheetItemDO getWorksheetItemAt(int i) {
        return items.get(i).worksheetItem;
    }
    
    public void setWorksheetItemAt(WorksheetItemDO item, int i) {
        if (items == null)
            items = new ArrayList<WorksheetItemListItem>();
        items.get(i).worksheetItem = item;
    }
    
    public void addWorksheetItem(WorksheetItemDO item) {
        WorksheetItemListItem listItem;
        
        if (items == null)
            items = new ArrayList<WorksheetItemListItem>();
        listItem = new WorksheetItemListItem();
        listItem.worksheetItem = item;
        items.add(listItem);
    }
    
    public void removeItemAt(int i) {
        WorksheetItemListItem tmp;
        
        if (items == null || i >= items.size())
            return;
        
        tmp = items.remove(i);
        if (tmp.worksheetItem.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<WorksheetItemListItem>();
            deleted.add(tmp);
        }
    }
    
    public WorksheetAnalysisManager getWorksheetAnalysisAt(int i) throws Exception {
        WorksheetItemListItem item = items.get(i);

        if (item.analysis == null) {
            if (item.worksheetItem != null && item.worksheetItem.getId() != null) {
                try {
                    item.analysis = WorksheetAnalysisManager.fetchByWorksheetItemId(item.worksheetItem.getId());
                } catch (NotFoundException e) {
                    //ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }
            
        if (item.analysis == null)
            item.analysis = WorksheetAnalysisManager.getInstance();
    
        return item.analysis;
    }

    public void setWorksheetAnalysisAt(WorksheetAnalysisManager analysis, int i) {
        items.get(i).analysis = analysis;
    }

    //service methods
    public static WorksheetItemManager fetchByWorksheetId(Integer id) throws Exception {
        return proxy().fetchByWorksheetId(id);
    }

    public WorksheetItemManager add() throws Exception {
        return proxy().add(this);
    }
    
    public WorksheetItemManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
       
    // friendly methods used by managers and proxies
    Integer getWorksheetId() {
        return worksheetId;
    }

    void setWorksheetId(Integer id) {
        worksheetId = id;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    WorksheetItemListItem getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static WorksheetItemManagerProxy proxy() {
        if(proxy == null)
            proxy = new WorksheetItemManagerProxy();
        return proxy;
    }
    
    static class WorksheetItemListItem implements RPC {
        private static final long serialVersionUID = 1L;

        WorksheetItemDO          worksheetItem;
        WorksheetAnalysisManager analysis;
    }
}
