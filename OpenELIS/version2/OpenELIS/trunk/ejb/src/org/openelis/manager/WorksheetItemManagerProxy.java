
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

import org.openelis.domain.WorksheetItemDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetItemLocal;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetItemManager.WorksheetItemListItem;
import org.openelis.utilcommon.DataBaseUtil;

public class WorksheetItemManagerProxy {
    
    public WorksheetItemManager fetchByWorksheetId(Integer id) throws Exception {
        int                        i;
        WorksheetItemManager       manager;
        ArrayList<WorksheetItemDO> items;
        
        items = local().fetchByWorksheetId(id);
        manager = WorksheetItemManager.getInstance();
        manager.setWorksheetId(id);
        for (i = 0; i < items.size(); i++)
            manager.addWorksheetItem(items.get(i));
        
        return manager;
    }
    
    public WorksheetItemManager add(WorksheetItemManager manager) throws Exception {
        int                   i;
        WorksheetItemDO       item;
        WorksheetItemListItem listItem;
        WorksheetItemLocal    local;
        
        local = local();
        for (i = 0; i < manager.count(); i++) {
            item = manager.getWorksheetItemAt(i);
            item.setWorksheetId(manager.getWorksheetId());
            local.add(item);
            
            listItem = manager.getItemAt(i);
            if (listItem.analysis != null) {
                manager.getWorksheetAnalysisAt(i).setWorksheetItemId(item.getId());
                manager.getWorksheetAnalysisAt(i).add();
            }
        }
        
        return manager;
    }

    public WorksheetItemManager update(WorksheetItemManager manager) throws Exception {
        int                   i, j;
        WorksheetItemDO       item;
        WorksheetItemListItem listItem;
        WorksheetItemLocal    local;
        
        local = local();
        for (j = 0; j < manager.deleteCount(); j++)
            local.delete(manager.getDeletedAt(j).worksheetItem);
        
        for (i = 0; i < manager.count(); i++) {
            item = manager.getWorksheetItemAt(i);
            
            if (item.getId() == null) {
                item.setWorksheetId(manager.getWorksheetId());
                local.add(item);
            } else {
                local.update(item);
            }
            
            listItem = manager.getItemAt(i);
            if (listItem.analysis != null) {
                manager.getWorksheetAnalysisAt(i).setWorksheetItemId(item.getId());
                manager.getWorksheetAnalysisAt(i).update();
            }
        }

        return manager;
    }

    public void validate(WorksheetItemManager manager) throws Exception {
        int                   i;
        ValidationErrorsList  list;
        WorksheetItemListItem listItem;
        WorksheetItemLocal    local;

        local = local();
        list  = new ValidationErrorsList();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetItemAt(i));
                
                listItem = manager.getItemAt(i);
                if (listItem.analysis != null)
                    manager.getWorksheetAnalysisAt(i).validate();
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private WorksheetItemLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (WorksheetItemLocal)ctx.lookup("openelis/WorksheetItemBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }
}
