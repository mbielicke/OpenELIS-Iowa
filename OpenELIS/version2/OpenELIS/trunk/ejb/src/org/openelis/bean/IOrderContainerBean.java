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
import org.openelis.constants.Messages;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.entity.IOrderContainer;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class IOrderContainerBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager        manager;
    private static final Integer MAX_QUANTITY = 99;

    @SuppressWarnings("unchecked")
    public ArrayList<IOrderContainerDO> fetchByIorderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("IOrderContainer.FetchByIorderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IOrderContainerDO> fetchByIorderIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("IOrderContainer.FetchByIorderIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public IOrderContainerDO add(IOrderContainerDO data) throws Exception {
        IOrderContainer entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new IOrderContainer();
        entity.setIorderId(data.getIorderId());
        entity.setContainerId(data.getContainerId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public IOrderContainerDO update(IOrderContainerDO data) throws Exception {
        IOrderContainer entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderContainer.class, data.getId());
        entity.setIorderId(data.getIorderId());
        entity.setContainerId(data.getContainerId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());

        return data;
    }

    public void delete(IOrderContainerDO data) throws Exception {
        IOrderContainer entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderContainer.class, data.getId());
        if (entity != null)
            manager.remove(entity);

    }

    public void validate(IOrderContainerDO data) throws Exception {
        Integer iorderId;
        ValidationErrorsList list;
        Integer num;

        /*
         * for display
         */
        iorderId = data.getIorderId();
        if (iorderId == null)
            iorderId = 0;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getContainerId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_containerRequiredException(iorderId)));
        num = data.getItemSequence();
        if (DataBaseUtil.isEmpty(num)) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_containerItemSequenceRequiredException(iorderId)));
        } else if (num > MAX_QUANTITY) {
            list.add(new FormErrorException(Messages.get()
                                                    .order_qtyNotMoreThanMaxException(iorderId,
                                                                                      DataBaseUtil.toString(MAX_QUANTITY))));
        }

        if (list.size() > 0)
            throw list;

    }

}
