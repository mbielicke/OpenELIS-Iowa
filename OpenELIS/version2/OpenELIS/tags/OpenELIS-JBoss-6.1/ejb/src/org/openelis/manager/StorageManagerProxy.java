/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.StorageLocal;
import org.openelis.utils.EJBFactory;

public class StorageManagerProxy {
    public StorageManager fetchById(Integer tableId, Integer id) throws Exception {
        ArrayList<StorageViewDO> list;
        StorageManager sm;
        
        list = EJBFactory.getStorage().fetchById(id, tableId);
        
        sm = StorageManager.getInstance();
        sm.setStorages(list);
        sm.setReferenceId(id);
        sm.setReferenceTableId(tableId);
        
        return sm;
    }

    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {
        StorageLocal sl;
        ArrayList<StorageViewDO> list;
        StorageManager sm;

        sl = EJBFactory.getStorage();
        list = sl.fetchCurrentByLocationId(id);
        sm = StorageManager.getInstance();
        sm.setStorages(list);

        return sm;
    }

    public StorageManager fetchHistoryByLocationId(Integer id, int first, int max) throws Exception {
        ArrayList<StorageViewDO> list;
        StorageManager sm;

        list = EJBFactory.getStorage().fetchHistoryByLocationId(id, first, max);
        sm = StorageManager.getInstance();
        sm.setStorages(list);

        return sm;
    }

    public StorageManager add(StorageManager man) throws Exception {
        StorageViewDO data;
        StorageLocal l;
        
        l = EJBFactory.getStorage();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getStorageAt(i);

            //
            // in storage screen, we need to be able to move different reference
            // items at the same time without destroying their original
            // reference id and table id
            //
            if (data.getReferenceId() == null) {
                data.setReferenceId(man.getReferenceId());
                data.setReferenceTableId(man.getReferenceTableId());
            }
            l.add(data);
        }

        return man;
    }

    public StorageManager update(StorageManager man) throws Exception {
        StorageViewDO data;
        StorageLocal l;
        
        l = EJBFactory.getStorage();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getStorageAt(i);
            if (data.getId() == null) {
                //
                // in storage screen, we need to be able to move different reference
                // items at the same time without destroying their original
                // reference id and table id
                //
                if (data.getReferenceId() == null) {
                    data.setReferenceId(man.getReferenceId());
                    data.setReferenceTableId(man.getReferenceTableId());
                }
                l.add(data);
            } else
                l.update(data);
        }

        return man;
    }

    public void validate(StorageManager man, ValidationErrorsList errorsList) throws Exception {
    }
}
