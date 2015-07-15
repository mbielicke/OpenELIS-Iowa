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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.InventoryXPutDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")

public class InventoryXPutBean  {

    @PersistenceContext(unitName = "openelis")
    EntityManager          manager;

    @EJB
    InventoryLocationBean inventoryLocation;

    public ArrayList<InventoryXPutViewDO> fetchByInventoryReceiptId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryXPut.FetchByInventoryReceiptId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<InventoryXPutViewDO> fetchByInventoryLocationId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryXPut.FetchByInventoryLocationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<InventoryXPutViewDO> fetchByIorderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("InventoryXPut.FetchByIorderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<InventoryXPutViewDO> fetchByIorderIds(ArrayList<Integer> ids) {
        Query query;
        List<InventoryXPutViewDO> x;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("InventoryXPut.FetchByIorderIds");
        x = new ArrayList<InventoryXPutViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            x.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(x);
    }

    public InventoryXPutDO add(InventoryXPutDO data) throws Exception {
        return data;
    }

    public InventoryXPutDO update(InventoryXPutDO data) throws Exception {

        return data;
    }

    public void delete(InventoryXPutDO data) throws Exception {
    }

}
