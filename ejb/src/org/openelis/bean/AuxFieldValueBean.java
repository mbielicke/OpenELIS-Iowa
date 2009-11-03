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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.entity.AuxFieldValue;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AuxFieldValueLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
//@RolesAllowed("organization-select")
public class AuxFieldValueBean implements AuxFieldValueLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;

    public ArrayList<AuxFieldValueViewDO> fetchById(Integer id) throws Exception {
        Query query;
        ArrayList<AuxFieldValueViewDO> data;
        AuxFieldValueViewDO dataDO;
        Integer dictionaryTypeId;
        DictionaryDO dictDO;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchById");
        query.setParameter("auxFieldId", id);
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
            
            if(data.size() > 0){
                query = manager.createNamedQuery("Dictionary.FetchBySystemName");
                query.setParameter("name", "aux_dictionary");
                dictDO = (DictionaryDO)query.getResultList().get(0);
                dictionaryTypeId = dictDO.getId();
                
                for(int i=0; i<data.size(); i++){
                    dataDO = data.get(i);
                
                    if(dictionaryTypeId.equals(dataDO.getTypeId())){
                        query = manager.createNamedQuery("Dictionary.FetchById");
                        query.setParameter("id", new Integer(dataDO.getValue()));
                        dictDO = (DictionaryViewDO)query.getResultList().get(0);
                        dataDO.setDictionary(dictDO.getEntry());
                    }
                }
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<AuxFieldValueViewDO> fetchByAuxDataRefIdRefTableId(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        ArrayList<AuxFieldValueViewDO> data;
        AuxFieldValueViewDO dataDO;
        Integer dictionaryTypeId;
        DictionaryDO dictDO;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByDataRefId");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
            
            if(data.size() > 0){
                query = manager.createNamedQuery("Dictionary.FetchBySystemName");
                query.setParameter("name", "aux_dictionary");
                dictDO = (DictionaryDO)query.getResultList().get(0);
                dictionaryTypeId = dictDO.getId();
                
                for(int i=0; i<data.size(); i++){
                    dataDO = data.get(i);
                
                    if(dictionaryTypeId.equals(dataDO.getTypeId())){
                        query = manager.createNamedQuery("Dictionary.FetchById");
                        query.setParameter("id", new Integer(dataDO.getValue()));
                        dictDO = (DictionaryViewDO)query.getResultList().get(0);
                        dataDO.setDictionary(dictDO.getEntry());
                    }
                }
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<AuxFieldValueViewDO> fetchByGroupId(Integer groupId) throws Exception {
        Query query;
        ArrayList<AuxFieldValueViewDO> data;
        AuxFieldValueViewDO dataDO;
        Integer dictionaryTypeId;
        DictionaryDO dictDO;
        
        query = manager.createNamedQuery("AuxFieldValue.FetchByGroupId");
        query.setParameter("groupId", groupId);
        try {
            data = DataBaseUtil.toArrayList(query.getResultList());
            
            if(data.size() > 0){
                query = manager.createNamedQuery("Dictionary.FetchBySystemName");
                query.setParameter("name", "aux_dictionary");
                dictDO = (DictionaryDO)query.getResultList().get(0);
                dictionaryTypeId = dictDO.getId();
                
                for(int i=0; i<data.size(); i++){
                    dataDO = data.get(i);
                
                    if(dictionaryTypeId.equals(dataDO.getTypeId())){
                        query = manager.createNamedQuery("Dictionary.FetchById");
                        query.setParameter("id", new Integer(dataDO.getValue()));
                        dictDO = (DictionaryViewDO)query.getResultList().get(0);
                        dataDO.setDictionary(dictDO.getEntry());
                    }
                }
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public AuxFieldValueViewDO add(AuxFieldValueViewDO data) throws Exception {
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

    public AuxFieldValueViewDO update(AuxFieldValueViewDO data) throws Exception {
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
    
    public void delete(AuxFieldValueViewDO data) throws Exception {
        AuxFieldValue entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AuxFieldValue.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}