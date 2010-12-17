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

import javax.naming.InitialContext;

import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.StorageLocal;

public class StorageManagerProxy {
    public StorageManager fetchById(Integer tableId, Integer id) throws Exception {
        ArrayList<StorageViewDO> storages;
        StorageManager sm;
        
        storages = local().fetchByRefId(tableId, id);
        
        sm = StorageManager.getInstance();
        sm.setStorages(storages);
        sm.setReferenceId(id);
        sm.setReferenceTableId(tableId);
        
        return sm;
    }

    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {
        StorageLocal sl;
        ArrayList<StorageViewDO> storages;
        StorageManager sm;

        sl = local();
        storages = sl.fetchCurrentByLocationId(id);
        sm = StorageManager.getInstance();
        sm.setStorages(storages);

        return sm;
    }

    public StorageManager fetchHistoryByLocationId(Query query) throws Exception {
        StorageLocal sl;
        ArrayList<StorageViewDO> storages;
        StorageManager sm;
        QueryData field;
        Integer max;

        sl = local();

        field = query.getFields().get(1);
        max = Integer.valueOf(field.getQuery());

        storages = sl.fetchHistoryByLocationId(query.getFields(), query.getPage(), max);
        sm = StorageManager.getInstance();
        sm.setStorages(storages);

        return sm;
    }

    public StorageManager add(StorageManager man) throws Exception {
        StorageViewDO storage;
        StorageLocal l;
        
        l = local();
        for (int i = 0; i < man.count(); i++ ) {
            storage = man.getStorageAt(i);

            //
            // in storage screen, we need to be able to move different reference
            // items at the same time without destroying their original
            // reference id and table id
            //
            if (storage.getReferenceId() == null) {
                storage.setReferenceId(man.getReferenceId());
                storage.setReferenceTableId(man.getReferenceTableId());
            }
            l.add(storage);
        }

        return man;
    }

    public StorageManager update(StorageManager man) throws Exception {
        StorageViewDO storage;
        StorageLocal l;
        
        l = local();
        for (int i = 0; i < man.count(); i++ ) {
            storage = man.getStorageAt(i);
            if (storage.getId() == null) {
                //
                // in storage screen, we need to be able to move different reference
                // items at the same time without destroying their original
                // reference id and table id
                //
                if (storage.getReferenceId() == null) {
                    storage.setReferenceId(man.getReferenceId());
                    storage.setReferenceTableId(man.getReferenceTableId());
                }
                l.add(storage);
            } else
                l.update(storage);
        }

        return man;
    }

    public void validate(StorageManager man, ValidationErrorsList errorsList) throws Exception {

    }

    private StorageLocal local(){
        try{
            InitialContext ctx = new InitialContext();
            return (StorageLocal)ctx.lookup("openelis/StorageBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
