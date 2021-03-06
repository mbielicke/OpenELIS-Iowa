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
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.StorageDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.entity.Storage;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;

@Stateless
@SecurityDomain("openelis")
public class StorageBean {
    @PersistenceContext(unitName = "openelis")
    private EntityManager   manager;

    @EJB
    private SampleItemBean sampleItem;

    @EJB
    private AnalysisBean    analysis;

    @EJB
    private SampleBean      sample;

    @EJB
    private UserCacheBean   userCache;

    public ArrayList<StorageViewDO> fetchById(Integer referenceId, Integer refTableId) throws Exception {
        Query query;
        SystemUserVO user;
        StorageViewDO data;
        ArrayList<StorageViewDO> list;

        query = manager.createNamedQuery("Storage.FetchById");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", refTableId);

        list = DataBaseUtil.toArrayList(query.getResultList());
        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }
        }

        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }

    public ArrayList<StorageViewDO> fetchByIds(ArrayList<Integer> referenceIds,
                                               Integer refTableId) {
        int i;
        Query query;
        SystemUserVO user;
        StorageViewDO data;
        List<StorageViewDO> s;
        ArrayList<Integer> range;
        
        query = manager.createNamedQuery("Storage.FetchByIds");
        query.setParameter("tableId", refTableId);
        s = new ArrayList<StorageViewDO>();
        range = DataBaseUtil.createSubsetRange(referenceIds.size());
        for (i = 0; i < range.size() - 1; i++ ) {
            query.setParameter("ids", referenceIds.subList(range.get(i), range.get(i + 1)));
            s.addAll(query.getResultList());
        }

        for (i = 0; i < s.size(); i++ ) {
            data = s.get(i);

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }
        }

        return DataBaseUtil.toArrayList(s);
    }

    public ArrayList<StorageViewDO> fetchCurrentByLocationId(Integer id) throws Exception {
        Query query;
        SystemUserVO user;
        StorageViewDO data;
        AnalysisViewDO anaDO;
        SampleItemViewDO itemDO;
        SampleDO sampleDO;
        Integer refTableId, sampleItemId, analysisId;
        ArrayList<StorageViewDO> list;
        String description, container;

        sampleItemId = Constants.table().SAMPLE_ITEM;
        analysisId = Constants.table().ANALYSIS;

        query = manager.createNamedQuery("Storage.FetchCurrentByLocationId");
        query.setParameter("id", id);

        list = DataBaseUtil.toArrayList(query.getResultList());
        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            refTableId = data.getReferenceTableId();

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }

            if (sampleItemId.equals(refTableId)) {
                itemDO = sampleItem.fetchById(data.getReferenceId());
                sampleDO = sample.fetchById(itemDO.getSampleId());
                description = sampleDO.getAccessionNumber() + " - " +
                              itemDO.getItemSequence();
                container = itemDO.getContainer();
                if (container != null)
                    data.setItemDescription(description + "," + container);
                else
                    data.setItemDescription(description);
            } else if (analysisId.equals(refTableId)) {
                anaDO = analysis.fetchById(data.getReferenceId());
                itemDO = sampleItem.fetchById(anaDO.getSampleItemId());
                sampleDO = sample.fetchById(itemDO.getSampleId());
                data.setItemDescription(sampleDO.getAccessionNumber() + " - " +
                                        itemDO.getItemSequence() + "," +
                                        anaDO.getTestName() + " : " +
                                        anaDO.getMethodName());
            }

        }

        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }

    public ArrayList<StorageViewDO> fetchHistoryByLocationId(Integer id, int first,
                                                             int max) throws Exception {
        SystemUserVO user;
        StorageViewDO data;
        AnalysisViewDO anaDO;
        SampleItemViewDO itemDO;
        SampleDO sampleDO;
        Query query;
        Integer refTableId, sampleItemId, analysisId;
        ArrayList<StorageViewDO> list;
        String description, container;

        sampleItemId = Constants.table().SAMPLE_ITEM;
        analysisId = Constants.table().ANALYSIS;

        query = manager.createNamedQuery("Storage.FetchHistoryByLocationId");
        query.setParameter("id", id);
        query.setMaxResults(first + max);

        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.isEmpty())
            throw new NotFoundException();

        list = (ArrayList<StorageViewDO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            refTableId = data.getReferenceTableId();

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }

            if (sampleItemId.equals(refTableId)) {
                itemDO = sampleItem.fetchById(data.getReferenceId());
                sampleDO = sample.fetchById(itemDO.getSampleId());
                description = sampleDO.getAccessionNumber() + "," +
                              itemDO.getItemSequence();
                container = itemDO.getContainer();
                if (container != null)
                    data.setItemDescription(description + "," + container);
                else
                    data.setItemDescription(description);
            } else if (analysisId.equals(refTableId)) {
                anaDO = analysis.fetchById(data.getReferenceId());
                itemDO = sampleItem.fetchById(anaDO.getSampleItemId());
                sampleDO = sample.fetchById(itemDO.getSampleId());
                data.setItemDescription(sampleDO.getAccessionNumber() + "," +
                                        anaDO.getTestName() + " : " +
                                        anaDO.getMethodName());
            }

        }

        return list;
    }

    public StorageDO add(StorageDO data) throws Exception {
        Storage entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Storage();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setCheckin(data.getCheckin());
        entity.setCheckout(data.getCheckout());
        entity.setSystemUserId(userCache.getId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public StorageDO update(StorageDO data) throws Exception {
        Storage entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Storage.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setCheckin(data.getCheckin());
        entity.setCheckout(data.getCheckout());
        // will not need to update the user id
        // entity.setSystemUserId(data.getSystemUserId());

        return data;
    }

    public void delete(StorageDO data) throws Exception {
        Storage entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Storage.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void deleteById(Integer referenceId, Integer refTableId) throws Exception {
        ArrayList<StorageViewDO> list;

        try {
            list = fetchById(referenceId, refTableId);
            for (StorageViewDO data : list)
                delete(data);
        } catch (NotFoundException e) {
            // there may not be any storages linked to the reference table and
            // reference id
        }
    }
}