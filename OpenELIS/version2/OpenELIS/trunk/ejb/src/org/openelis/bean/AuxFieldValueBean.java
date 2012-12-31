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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.entity.AuxFieldValue;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.meta.AuxFieldGroupMeta;

@Stateless
@SecurityDomain("openelis")
public class AuxFieldValueBean implements AuxFieldValueLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager        manager;

    @EJB
    private DictionaryCacheLocal dictionaryCache;
    
    private static final Logger log = Logger.getLogger("openelis");

    public AuxFieldValueViewDO fetchById(Integer id) throws Exception {
        Query query;
        AuxFieldValueViewDO data;

        query = manager.createNamedQuery("AuxFieldValue.FetchById");
        query.setParameter("id", id);

        try {
            data = (AuxFieldValueViewDO)query.getSingleResult();
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                setDictionaryValue(data);            
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByAuxDataRefIdRefTableId(Integer referenceId,
                                                                        Integer referenceTableId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("AuxFieldValue.FetchByDataRefId");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        try {
            setDictionaryValues(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList)list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByFieldId(Integer fieldId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("AuxFieldValue.FetchByFieldId");
        query.setParameter("fieldId", fieldId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);

        try {
            setDictionaryValues(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList)list;
    }
    
    private void setDictionaryValue(AuxFieldValueViewDO data) throws Exception {
        DictionaryDO dict;
        
        try {
            /*
             * for entries that are dictionary, we want to fetch the dictionary
             * text and set it for display
             */
            dict = dictionaryCache.getById(Integer.parseInt(data.getValue()));
            if (dict != null)
                data.setDictionary(dict.getEntry());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not fetch dictionary record with id "+ data.getValue(), e);
            throw e;
        }
    }

    private void setDictionaryValues(List list) throws Exception {
        AuxFieldValueViewDO data;
        
        for (int i = 0; i < list.size(); i++ ) {
            data = (AuxFieldValueViewDO)list.get(i);
            if (DataBaseUtil.isSame(Constants.dictionary().AUX_DICTIONARY, data.getTypeId()))
                setDictionaryValue(data);                
        }        
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByGroupId(Integer groupId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("AuxFieldValue.FetchByGroupId");
        query.setParameter("groupId", groupId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        try {
            setDictionaryValues(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList)list;
    }

    public AuxFieldValueDO add(AuxFieldValueDO data) throws Exception {
        AuxFieldValue entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AuxFieldValue();
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AuxFieldValueDO update(AuxFieldValueDO data) throws Exception {
        AuxFieldValue entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxFieldValue.class, data.getId());
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(AuxFieldValueDO data) throws Exception {
        AuxFieldValue entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AuxFieldValue.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(AuxFieldValueDO data) throws Exception {
        ValidationErrorsList list;
        Integer typeId;
        String value;

        list = new ValidationErrorsList();

        value = data.getValue();
        typeId = data.getTypeId();

        if (typeId == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             AuxFieldGroupMeta.getFieldValueTypeId()));

        if (value == null &&
            (DataBaseUtil.isSame(Constants.dictionary().AUX_NUMERIC, typeId) ||
             DataBaseUtil.isSame(Constants.dictionary().AUX_DICTIONARY, typeId))) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             AuxFieldGroupMeta.getFieldValueValue()));
        } else if (value != null &&
                   (DataBaseUtil.isSame(Constants.dictionary().AUX_DATE_TIME, typeId) ||
                    DataBaseUtil.isSame(Constants.dictionary().AUX_TIME, typeId) ||
                    DataBaseUtil.isSame(Constants.dictionary().AUX_DATE, typeId) ||
                    DataBaseUtil.isSame(Constants.dictionary().AUX_ALPHA_LOWER, typeId) ||
                    DataBaseUtil.isSame(Constants.dictionary().AUX_ALPHA_UPPER, typeId) ||
                    DataBaseUtil.isSame(Constants.dictionary().AUX_ALPHA_MIXED, typeId))) {
            list.add(new FieldErrorException("valuePresentForTypeException",
                                             AuxFieldGroupMeta.getFieldValueValue()));
        }

        if (list.size() > 0)
            throw list;
    }
}
