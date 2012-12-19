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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.AuxData;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.remote.AuxDataRemote;

@Stateless
@SecurityDomain("openelis")

public class AuxDataBean implements AuxDataLocal, AuxDataRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager        manager;

    @EJB
    private DictionaryLocal      dictionary;

    @EJB
    private DictionaryCacheLocal dictionaryCache;
    
    private static Integer       dictionaryTypeId;
    
    private static final Logger log = Logger.getLogger("openelis");
    
    @PostConstruct
    public void init() {
        if (dictionaryTypeId == null) {
            try {
                dictionaryTypeId = dictionary.fetchBySystemName("aux_dictionary").getId(); 
            } catch (Throwable e) {
                log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
            }
        }
    }

    public ArrayList<AuxDataViewDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        ArrayList<AuxDataViewDO> list;
        AuxDataViewDO data;
        DictionaryDO dict;

        query = manager.createNamedQuery("AuxData.FetchById");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);
        try {
            list = DataBaseUtil.toArrayList(query.getResultList());
            for (int i = 0; i < list.size(); i++ ) {
                data = list.get(i);
                if (dictionaryTypeId.equals(data.getTypeId()) && data.getValue() != null) {
                    dict = dictionaryCache.getById(new Integer(data.getValue()));
                    data.setDictionary(dict.getEntry());
                }
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return list;
    }
    
    public ArrayList<AuxDataViewDO> fetchByIds(ArrayList<Integer> referenceIds, Integer referenceTableId) {
        Integer dictId;
        String value;
        Query query;
        ArrayList<AuxDataViewDO> list;
        AuxDataViewDO data;
        DictionaryDO dict;
        HashMap<String, DictionaryDO> dictMap;

        query = manager.createNamedQuery("AuxData.FetchByIds");
        query.setParameter("ids", referenceIds);
        query.setParameter("tableId", referenceTableId);        
        list = DataBaseUtil.toArrayList(query.getResultList());
        
        dictMap = new HashMap<String, DictionaryDO>();
        
        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);

            value = data.getValue();
            if (dictionaryTypeId.equals(data.getTypeId()) && value != null) {
                dict = dictMap.get(value);
                /*
                 * the following helps avoid creating new integers from the same
                 * string over and over again potentially hundreds or thousands
                 * of times and also trying to fetch a dictionary record even if
                 * fetching it failed previously
                 */
                if (dict == null) {
                    dictId = Integer.valueOf(value);                    
                    
                    try {
                        dict = dictionaryCache.getById(dictId);
                        dictMap.put(value, dict);
                        data.setDictionary(dict.getEntry());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Failed to lookup dictionary entry with id: " + dictId, e);
                    }
                } else {
                    data.setDictionary(dict.getEntry());
                }
            }
        }

        return list;
    }
    
    
    public ArrayList<AuxDataViewDO> fetchForDataView(Integer referenceTableId,
                                                     ArrayList<Integer> ids) throws Exception {
        Query query;
        List<AuxDataViewDO> auxList;

        if (ids.size() == 0)
            throw new NotFoundException();
        
        query = manager.createNamedQuery("AuxData.FetchForDataView");
        query.setParameter("ids", ids);
        query.setParameter("tableId", referenceTableId);
        auxList = (List<AuxDataViewDO>)query.getResultList();

        if (auxList.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(auxList);
    }    

    public IdVO fetchGroupIdBySystemVariable(String sysVariableKey) throws Exception {
        SystemVariableDO sysVariable;
        AuxFieldGroupDO groupDO;
        IdVO idVO;
        Query query;
    
        query = manager.createNamedQuery("SystemVariable.FetchByName");
        query.setParameter("name", sysVariableKey);
        sysVariable = (SystemVariableDO)query.getSingleResult();
    
        query = manager.createNamedQuery("AuxFieldGroup.FetchActiveByName");
        query.setParameter("name", sysVariable.getValue());
        groupDO = (AuxFieldGroupDO)query.getSingleResult();
    
        idVO = new IdVO(groupDO.getId());
    
        return idVO;
    }
    
    public ArrayList<AuxDataViewDO> fetchByIdAnalyteName(Integer referenceId, Integer referenceTableId,
                                                         String analyteName) throws Exception {
        Query query;
        List<AuxDataViewDO> list;
        
        query = manager.createNamedQuery("AuxData.FetchByIdAnalyteName");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);
        query.setParameter("analyteName", analyteName);
        
        list = (List<AuxDataViewDO>)query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<AuxDataViewDO> fetchByRefIdRefTableIdGroupName(Integer referenceId,
                                                                    Integer referenceTableId,
                                                                    String auxFieldGroupName) throws Exception {
        Query query;
        List<AuxDataViewDO> list;
        
        query = manager.createNamedQuery("AuxData.FetchByRefIdRefTableIdGroupName");
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);
        query.setParameter("auxFieldGroupName", auxFieldGroupName);
        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }

    public AuxDataDO add(AuxDataDO data) throws Exception {
        AuxData entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AuxData();
        entity.setSortOrder(data.getSortOrder());
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setIsReportable(data.getIsReportable());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AuxDataDO update(AuxDataDO data) throws Exception {
        AuxData entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxData.class, data.getId());

        entity.setSortOrder(data.getSortOrder());
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setIsReportable(data.getIsReportable());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(AuxDataDO data) throws Exception {
        AuxData entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AuxData.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}