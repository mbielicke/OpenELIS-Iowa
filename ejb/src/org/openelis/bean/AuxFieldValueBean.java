/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.entity.AuxFieldValue;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.meta.AuxFieldGroupMeta;

@Stateless
@SecurityDomain("openelis")
public class AuxFieldValueBean implements AuxFieldValueLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    @EJB
    private DictionaryLocal                  dictionary;

    private static int                       typeDict, typeNumeric, typeDateTime,
                                             typeDate, typeTime, typeAlphaLower,
                                             typeAlphaUpper, typeAlphaMixed;      
    
    @PostConstruct
    public void init() {
        DictionaryDO data;

        if (typeDict == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_dictionary");
                typeDict = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeDict = 0;
            }
        }

        if (typeNumeric == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_numeric");
                typeNumeric = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeNumeric = 0;
            }
        }

        if (typeDateTime == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_date_time");
                typeDateTime = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeDateTime = 0;
            }
        }

        if (typeDate == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_date");
                typeDate = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeDate = 0;
            }
        }

        if (typeTime == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_time");
                typeTime = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeTime = 0;
            }
        }

        if (typeAlphaLower == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_alpha_lower");
                typeAlphaLower = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeAlphaLower = 0;
            }
        }

        if (typeAlphaUpper == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_alpha_upper");
                typeAlphaUpper = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeAlphaUpper = 0;
            }
        }

        if (typeAlphaMixed == 0) {
            try {
                data = dictionary.fetchBySystemName("aux_alpha_mixed");
                typeAlphaMixed = data.getId();
            } catch (Throwable e) {
                e.printStackTrace();
                typeAlphaMixed = 0;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public AuxFieldValueViewDO fetchById(Integer id) throws Exception {
        Query query;
        List list;
        AuxFieldValueViewDO data;
        DictionaryDO dict;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchById");
        query.setParameter("id", id);

        try {
            data = (AuxFieldValueViewDO)query.getSingleResult();
            if (typeDict == data.getTypeId()) {
                //
                // for entries that are dictionary, we want to fetch the dictionary
                // text and set it for display
                //
                dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                if (dict != null)
                    data.setDictionary(dict.getEntry());
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByAuxDataRefIdRefTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        List list;
        AuxFieldValueViewDO data;
        DictionaryDO dict;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByDataRefId");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        //
        // for entries that are dictionary, we want to fetch the dictionary
        // text and set it for display
        //
        try {                       
            for (int i = 0 ; i < list.size(); i++) {
                data = (AuxFieldValueViewDO)list.get(i);                
                if (typeDict == data.getTypeId()) {
                    dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                    if (dict != null)
                        data.setDictionary(dict.getEntry());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList) list;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByFieldId(Integer fieldId) throws Exception {
        Query query;
        List list;
        AuxFieldValueViewDO data;
        DictionaryDO dict;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByFieldId");
        query.setParameter("fieldId", fieldId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        //
        // for entries that are dictionary, we want to fetch the dictionary
        // text and set it for display
        //
        try {                       
            for (int i = 0 ; i < list.size(); i++) {
                data = (AuxFieldValueViewDO)list.get(i);                
                if (typeDict == data.getTypeId()) {
                    dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                    if (dict != null)
                        data.setDictionary(dict.getEntry());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList) list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldValueViewDO> fetchByGroupId(Integer groupId) throws Exception {
        Query query;
        List list;
        AuxFieldValueViewDO data;
        DictionaryDO dict;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByGroupId");
        query.setParameter("groupId", groupId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        //
        // for entries that are dictionary, we want to fetch the dictionary
        // text and set it for display
        //
        try {                       
            for (int i = 0 ; i < list.size(); i++) {
                data = (AuxFieldValueViewDO)list.get(i);                
                if (typeDict == data.getTypeId()) {
                    dict = dictionary.fetchById(Integer.parseInt(data.getValue()));
                    if (dict != null)
                        data.setDictionary(dict.getEntry());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return (ArrayList) list;
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
        
        if (!data.isChanged())
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
        
        if(DataBaseUtil.isEmpty(typeId))
            list.add(new FieldErrorException("fieldRequiredException",
                                             AuxFieldGroupMeta.getFieldValueTypeId()));
        
        if(DataBaseUtil.isEmpty(value) && (DataBaseUtil.isSame(typeNumeric,typeId) ||
                        DataBaseUtil.isSame(typeDict,typeId))) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             AuxFieldGroupMeta.getFieldValueValue()));
        } else if (!DataBaseUtil.isEmpty(value) &&
                   (DataBaseUtil.isSame(typeDateTime,typeId) || DataBaseUtil.isSame(typeTime,typeId) ||
                    DataBaseUtil.isSame(typeDate,typeId) || DataBaseUtil.isSame(typeAlphaLower,typeId) || 
                    DataBaseUtil.isSame(typeAlphaUpper,typeId) || DataBaseUtil.isSame(typeAlphaMixed,typeId))) {
            list.add(new FieldErrorException("valuePresentForTypeException",
                                             AuxFieldGroupMeta.getFieldValueValue()));
        }
        
        if (list.size() > 0)
            throw list;
    }
}
