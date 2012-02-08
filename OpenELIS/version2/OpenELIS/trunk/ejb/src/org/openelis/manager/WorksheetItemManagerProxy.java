
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

import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetItemLocal;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetItemManager.WorksheetItemListItem;
import org.openelis.utils.EJBFactory;

public class WorksheetItemManagerProxy {
    
    public WorksheetItemManager fetchByWorksheetId(Integer id) throws Exception {
        int                        i;
        WorksheetItemManager       manager;
        ArrayList<WorksheetItemDO> items;
        WorksheetViewDO            worksheet;
        
        worksheet = EJBFactory.getWorksheet().fetchById(id);
        items = EJBFactory.getWorksheetItem().fetchByWorksheetId(id);
        manager = WorksheetItemManager.getInstance();
        manager.setWorksheet(worksheet);
        for (i = 0; i < items.size(); i++)
            manager.addWorksheetItem(items.get(i));
        
        return manager;
    }
    
    public WorksheetItemManager add(WorksheetItemManager manager) throws Exception {
        boolean                  notDone;
        int                      i;
        HashMap<Integer,Integer> idHash;
        WorksheetItemDO          item;
        WorksheetItemLocal       local;
        
        local = EJBFactory.getWorksheetItem();
        for (i = 0; i < manager.count(); i++) {
            item = manager.getWorksheetItemAt(i);
            item.setWorksheetId(manager.getWorksheet().getId());
            local.add(item);
        }
        
        idHash = new HashMap<Integer,Integer>();
        do {
            notDone = false;
            for (i = 0; i < manager.count(); i++) {
                item = manager.getWorksheetItemAt(i);
                manager.getWorksheetAnalysisAt(i).setWorksheet(manager.getWorksheet());
                manager.getWorksheetAnalysisAt(i).setWorksheetItemId(item.getId());
                manager.getWorksheetAnalysisAt(i).add(idHash);
                if (manager.getWorksheetAnalysisAt(i).getNotDone())
                    notDone = true;
            }
        } while (notDone);

        return manager;
    }

    public WorksheetItemManager update(WorksheetItemManager manager) throws Exception {
        int                i, j;
        WorksheetItemDO    item;
        WorksheetItemLocal local;
        
        local = EJBFactory.getWorksheetItem();
        for (j = 0; j < manager.deleteCount(); j++)
            local.delete(manager.getDeletedAt(j).worksheetItem);
        
        for (i = 0; i < manager.count(); i++) {
            item = manager.getWorksheetItemAt(i);
            if (item.getId() == null) {
                item.setWorksheetId(manager.getWorksheet().getId());
                local.add(item);
            } else {
                local.update(item);
            }

            manager.getWorksheetAnalysisAt(i).setWorksheet(manager.getWorksheet());
            manager.getWorksheetAnalysisAt(i).setWorksheetItemId(item.getId());
            manager.getWorksheetAnalysisAt(i).update();
        }

        return manager;
    }

    public void validate(WorksheetItemManager manager, ValidationErrorsList errorList) throws Exception {
        int                   i;
        WorksheetItemListItem listItem;
        WorksheetItemLocal    local;

        local = EJBFactory.getWorksheetItem();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetItemAt(i));
            } catch (Exception e) {
//                DataBaseUtil.mergeException(errorList, e, "itemTable", i);
                DataBaseUtil.mergeException(errorList, e);
            }
            
            listItem = manager.getItemAt(i);
            if (listItem.analysis != null)
                manager.getWorksheetAnalysisAt(i).validate(errorList);
        }
    }
}
