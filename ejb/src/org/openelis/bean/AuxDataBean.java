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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.entity.AuxData;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utilcommon.ResultFormatter;

@Stateless
@SecurityDomain("openelis")
public class AuxDataBean {
    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private AuxFieldBean        auxField;

    @EJB
    private DictionaryCacheBean dictionaryCache;

    @EJB
    private DictionaryBean      dictionary;

    private static final Logger log = Logger.getLogger("openelis");

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
                if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()) &&
                    data.getValue() != null) {
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

    public ArrayList<AuxDataViewDO> fetchByIds(ArrayList<Integer> referenceIds,
                                               Integer referenceTableId) {
        Integer id;
        Query query;
        DictionaryDO d;
        List<AuxDataViewDO> ads;
        ArrayList<DictionaryViewDO> ds;
        ArrayList<Integer> r;
        HashSet<Integer> ids;
        HashMap<Integer, DictionaryViewDO> map;

        query = manager.createNamedQuery("AuxData.FetchByIds");
        query.setParameter("tableId", referenceTableId);
        ads = new ArrayList<AuxDataViewDO>();
        r = DataBaseUtil.createSubsetRange(referenceIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", referenceIds.subList(r.get(i), r.get(i + 1)));
            ads.addAll(query.getResultList());
        }

        ids = new HashSet<Integer>();
        for (AuxDataDO data : ads) {
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                ids.add(Integer.valueOf(data.getValue()));
        }

        map = new HashMap<Integer, DictionaryViewDO>();
        if (ids.size() > 0) {
            log.log(Level.INFO, "Fetching dictionaries for aux data");
            ds = dictionary.fetchByIds(DataBaseUtil.toArrayList(ids));
            for (DictionaryViewDO data : ds)
                map.put(data.getId(), data);
        }

        for (AuxDataViewDO data : ads) {
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId())) {
                id = Integer.valueOf(data.getValue());
                d = map.get(id);
                /*
                 * this is to make sure that the code doesn't fail if the aux
                 * field value linked to this aux data was removed and then the
                 * dictionary entry linked to the aux field value was removed
                 */
                if (d != null)
                    data.setDictionary(d.getEntry());
                else
                    log.log(Level.SEVERE, "Failed to lookup dictionary entry with id: " + id);
            }
        }

        return DataBaseUtil.toArrayList(ads);
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

        query = manager.createNamedQuery("AuxFieldGroup.FetchByName");
        query.setParameter("name", sysVariable.getValue());
        groupDO = (AuxFieldGroupDO)query.getSingleResult();

        idVO = new IdVO(groupDO.getId());

        return idVO;
    }

    public ArrayList<AuxDataViewDO> fetchByIdAnalyteName(Integer referenceId,
                                                         Integer referenceTableId,
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

    public void validate(AuxDataViewDO data, ResultFormatter rf, Integer accession) throws Exception {
        String value;
        ValidationErrorsList e;

        if ( !DataBaseUtil.isEmpty(data.getValue())) {
            e = new ValidationErrorsList();
            if (data.getTypeId() == null) {
                e.add(new FormErrorException(Messages.get()
                                                     .aux_valueInvalidException(accession,
                                                                                data.getAnalyteName(),
                                                                                data.getValue())));
            } else {
                try {
                    rf.isValid(null, data.getAuxFieldId(), null, data.getValue());
                } catch (Exception ex) {
                    if (data.getDictionary() != null)
                        value = data.getDictionary();
                    else
                        value = data.getValue();
                    e.add(new FormErrorException(Messages.get()
                                                         .aux_valueInvalidException(accession,
                                                                                    data.getAnalyteName(),
                                                                                    value)));
                }
            }

            if (e.size() > 0)
                throw e;
        }
    }

    public void validateForDelete(AuxFieldViewDO data) throws Exception {
        Query query;
        ValidationErrorsList list;
        List result;

        list = new ValidationErrorsList();

        query = manager.createNamedQuery("AuxData.ReferenceCheckForField");
        query.setParameter("id", data.getId());
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FieldErrorException(Messages.get().aux_deleteException(), null));
            throw list;
        }
    }
}