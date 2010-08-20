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

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.entity.Storage;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.LoginLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.StorageLocal;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.*;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("storage-select")
public class StorageBean implements StorageLocal {
    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;

    @EJB (mappedName="security/SystemUserUtilBean") private SystemUserUtilRemote sysUser;
    
    @EJB private LoginLocal               login;
    
    @EJB private SampleItemLocal          sampleItem;
    
    @EJB private AnalysisLocal            analysis;
    
    @EJB private SampleLocal              sample;    

    public ArrayList<StorageViewDO> fetchByRefId(Integer refTableId, Integer refId) throws Exception {
        SystemUserDO user;
        StorageViewDO storage;
        Query query;
        ArrayList<StorageViewDO> list;

        query = manager.createNamedQuery("Storage.FetchById");
        query.setParameter("referenceTable", refTableId);
        query.setParameter("id", refId);

        list = DataBaseUtil.toArrayList(query.getResultList());

        for (int i = 0; i < list.size(); i++ ) {
            storage = list.get(i);

            if (storage.getSystemUserId() != null) {
                user = sysUser.getSystemUser(storage.getSystemUserId());
                if (user != null)
                    storage.setUserName(user.getLoginName());
            }
        }

        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }
    
    public ArrayList<StorageViewDO> fetchCurrentByLocationId(Integer id) throws Exception {
        SystemUserDO user;
        StorageViewDO data;
        AnalysisViewDO anaDO;
        SampleItemViewDO itemDO; 
        SampleDO sampleDO;
        Query query;
        Integer refTableId, sampleItemId, analysisId;
        ArrayList<StorageViewDO> list;
        String description, container;

        sampleItemId = ReferenceTable.SAMPLE_ITEM;
        analysisId = ReferenceTable.ANALYSIS;

        query = manager.createNamedQuery("Storage.FetchCurrentByLocationId");
        query.setParameter("id", id);

        list = DataBaseUtil.toArrayList(query.getResultList());

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);
            refTableId = data.getReferenceTableId();

            if (data.getSystemUserId() != null) {
                user = sysUser.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }

            if (sampleItemId.equals(refTableId)) {
                itemDO = sampleItem.fetchById(data.getReferenceId()); 
                sampleDO = sample.fetchById(itemDO.getSampleId());
                description = sampleDO.getAccessionNumber()+" - "+itemDO.getItemSequence();
                container = itemDO.getContainer();
                if(container != null)
                    data.setItemDescription(description+","+container);       
                else 
                    data.setItemDescription(description);
            } else if (analysisId.equals(refTableId)) {
                anaDO = analysis.fetchById(data.getReferenceId());
                itemDO = sampleItem.fetchById(anaDO.getSampleItemId()); 
                sampleDO = sample.fetchById(itemDO.getSampleId());
                data.setItemDescription(sampleDO.getAccessionNumber()+" - "+ itemDO.getItemSequence()
                                        +","+ anaDO.getTestName()+" : "+anaDO.getMethodName());
            }

        }

        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }

    public ArrayList<StorageViewDO> fetchHistoryByLocationId(ArrayList<QueryData> fields, int first, int max) throws Exception {
        SystemUserDO user;
        StorageViewDO data;
        AnalysisViewDO anaDO;
        SampleItemViewDO itemDO; 
        SampleDO sampleDO;
        Query query;
        Integer refTableId, sampleItemId, analysisId, id;
        ArrayList<StorageViewDO> list;
        QueryData field;
        String description, container;

        sampleItemId = ReferenceTable.SAMPLE_ITEM;
        analysisId = ReferenceTable.ANALYSIS;

        field = fields.get(0);
        id = Integer.valueOf(field.query);
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
                user = sysUser.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setUserName(user.getLoginName());
            }
            
            if (sampleItemId.equals(refTableId)) {
                itemDO = sampleItem.fetchById(data.getReferenceId()); 
                sampleDO = sample.fetchById(itemDO.getSampleId());
                description = sampleDO.getAccessionNumber()+","+itemDO.getItemSequence();
                container = itemDO.getContainer();
                if(container != null)
                    data.setItemDescription(description+","+container);       
                else 
                    data.setItemDescription(description);
            } else if (analysisId.equals(refTableId)) {
                anaDO = analysis.fetchById(data.getReferenceId());
                itemDO = sampleItem.fetchById(anaDO.getSampleItemId()); 
                sampleDO = sample.fetchById(itemDO.getSampleId());
                data.setItemDescription(sampleDO.getAccessionNumber()+","+
                                        anaDO.getTestName()+" : "+anaDO.getMethodName());
            }

        }

        return list;
    }

    public StorageViewDO add(StorageViewDO data) throws Exception {
        Storage entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Storage();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setStorageLocationId(data.getStorageLocationId());
        entity.setCheckin(data.getCheckin());
        entity.setCheckout(data.getCheckout());
        entity.setSystemUserId(login.getSystemUserId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public StorageViewDO update(StorageViewDO data) throws Exception {
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
        entity.setSystemUserId(login.getSystemUserId());
        
        return data;
    }

    public void delete(StorageViewDO data) throws Exception {
        Storage entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Storage.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }
}
