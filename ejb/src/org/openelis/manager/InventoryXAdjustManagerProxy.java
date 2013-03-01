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

import org.openelis.bean.InventoryItemCacheBean;
import org.openelis.bean.InventoryLocationBean;
import org.openelis.bean.InventoryXAdjustBean;
import org.openelis.constants.Messages;
import org.openelis.domain.InventoryXAdjustViewDO;
import org.openelis.meta.InventoryAdjustmentMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class InventoryXAdjustManagerProxy {

    public InventoryXAdjustManager fetchByInventoryAdjustmentId(Integer id) throws Exception {
        InventoryXAdjustManager m;
        ArrayList<InventoryXAdjustViewDO> adjustments;

        adjustments = EJBFactory.getInventoryXAdjust().fetchByInventoryAdjustmentId(id);
        m = InventoryXAdjustManager.getInstance();
        m.setInventoryAdjustmentId(id);
        m.setAdjustments(adjustments);

        return m;
    }

    public InventoryXAdjustManager add(InventoryXAdjustManager man) throws Exception {
        int i;
        Integer invLocId;
        ArrayList<Integer> invLocIdList;
        InventoryXAdjustBean cl;
        InventoryXAdjustViewDO data;
        InventoryLocationBean il;

        cl = EJBFactory.getInventoryXAdjust();
        il = EJBFactory.getInventoryLocation();
        invLocIdList = new ArrayList<Integer>();
        
        for (i = 0; i < man.count(); i++ ) {
            data = man.getAdjustmentAt(i);
            invLocId = data.getInventoryLocationId();
            il.fetchForUpdate(invLocId);
            invLocIdList.add(invLocId);
            data.setInventoryAdjustmentId(man.getInventoryAdjustmentId());
            cl.add(data);
        }
        
        for (i = 0; i < invLocIdList.size(); i++ ) {            
            invLocId = invLocIdList.get(i);
            il.abortUpdate(invLocId);
        }

        return man;
    }

    public InventoryXAdjustManager update(InventoryXAdjustManager man) throws Exception {
        int i;
        Integer invLocId;
        ArrayList<Integer> invLocIdList;
        InventoryXAdjustBean cl;
        InventoryXAdjustViewDO data;
        InventoryLocationBean il;

        cl = EJBFactory.getInventoryXAdjust();
        il = EJBFactory.getInventoryLocation();
        
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        invLocIdList = new ArrayList<Integer>();
        for (i = 0; i < man.count(); i++ ) {
            data = man.getAdjustmentAt(i);
            invLocId = data.getInventoryLocationId();
            il.fetchForUpdate(invLocId);
            invLocIdList.add(invLocId);
            if (data.getId() == null) {
                data.setInventoryAdjustmentId(man.getInventoryAdjustmentId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }
        
        for (i = 0; i < invLocIdList.size(); i++ ) {            
            invLocId = invLocIdList.get(i);
            il.abortUpdate(invLocId);
        }

        return man;
    }
    
    public void validate(InventoryXAdjustManager man) throws Exception {
        Integer storeId, prevStoreId, locationId;
        ArrayList<Integer> locationIdList;
        ValidationErrorsList list;
        InventoryXAdjustBean cl;
        InventoryXAdjustViewDO data;
        InventoryItemCacheBean il;

        cl = EJBFactory.getInventoryXAdjust();
        il = EJBFactory.getInventoryItemCache();
        list = new ValidationErrorsList();
        data = null;
        locationIdList = new ArrayList<Integer>();
        prevStoreId = null;
        for (int i = 0; i < man.count(); i++ ) {
            try {
                data = man.getAdjustmentAt(i);
                cl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "adjustmentTable", i);
            }
            
            locationId = data.getInventoryLocationId();            
            if (locationId != null && locationIdList.contains(locationId)) {
                list.add(new TableFieldErrorException(Messages.get().fieldUniqueOnlyException(),i,
                                                      InventoryAdjustmentMeta.getInventoryLocationInventoryItemName(),
                                                      "adjustmentTable"));
            } else {
                locationIdList.add(locationId);
            }
            
            storeId = il.getById(data.getInventoryLocationInventoryItemId()).getStoreId();
            if (i == 0) {
                prevStoreId = storeId;
                continue;
            }
            
            if (!storeId.equals(prevStoreId)) 
                list.add(new TableFieldErrorException(Messages.get().allItemsSameStoreException(),i,
                                                      InventoryAdjustmentMeta.getInventoryLocationInventoryItemName(),
                                                      "adjustmentTable"));            
            prevStoreId = storeId;
        }
        if (list.size() > 0)
            throw list;
    }
}
