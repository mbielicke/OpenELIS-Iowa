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
package org.openelis.server.handlers;

import java.util.HashMap;

import org.openelis.domain.InventoryItemDO;
import org.openelis.messages.InventoryItemCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.InventoryItemRemote;

public class InventoryItemCacheHandler implements MessageHandler<InventoryItemCacheMessage> {

    public void handle(InventoryItemCacheMessage message) {
        HashMap<Integer, InventoryItemDO> idHash;
        InventoryItemDO data;

        idHash = (HashMap<Integer, InventoryItemDO>)CachingManager.getElement("InitialData",
                                                                                  "inventoryItemIdValues");

        if (idHash != null) {
            data = idHash.get(message.getInventoryItemDO().getId());
            if (data != null)
                idHash.remove(data);
        }
        CachingManager.remove("InitialData", "inventoryItemIdValues");

    }

    public static InventoryItemDO getActiveInventoryItemDOFromId(Integer id) {
        HashMap<Integer, InventoryItemDO> idHash;
        InventoryItemDO data;

        idHash = (HashMap<Integer, InventoryItemDO>)CachingManager.getElement("InitialData",
                                                                                  "inventoryItemIdValues");
        if (idHash == null)
            idHash = new HashMap<Integer, InventoryItemDO>();

        data = idHash.get(id);
        if (data == null) {
            try {
                data = remote().fetchActiveById(id);

                if (data != null) {
                    idHash.put(id, data);
                    CachingManager.putElement("InitialData", "inventoryItemIdValues", idHash);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return data;
    }

    private static InventoryItemRemote remote() {
        return (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
    }
}
