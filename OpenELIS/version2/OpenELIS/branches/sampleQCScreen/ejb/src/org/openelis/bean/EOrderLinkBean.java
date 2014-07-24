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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.EOrderLinkDO;
import org.openelis.entity.EOrderLink;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class EOrderLinkBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<EOrderLinkDO> fetchByEOrderId(Integer eOrderId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("EOrderLink.FetchByEOrderId");
        query.setParameter("eOrderId", eOrderId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public EOrderLinkDO add(EOrderLinkDO data) {
        EOrderLink entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new EOrderLink();
        entity.setEOrderId(data.getEOrderId());
        entity.setReference(data.getReference());
        entity.setSubId(data.getSubId());
        entity.setName(data.getName());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public void delete(EOrderLinkDO data) throws Exception {
        EOrderLink entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(EOrderLink.class, data.getId());
        if (entity != null)
            manager.remove(entity);

    }
}
