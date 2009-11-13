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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.entity.AuxData;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AuxDataLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
public class AuxDataBean implements AuxDataLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    public ArrayList<AuxDataViewDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        ArrayList<AuxDataViewDO> data;
        AuxDataViewDO dataDO;
        Integer dictionaryTypeId;
        DictionaryDO dictDO;
        
        query = manager.createNamedQuery("AuxData.FetchById");
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

    public AuxDataViewDO add(AuxDataViewDO data) throws Exception {
        AuxData entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AuxData();
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setIsReportable(data.getIsReportable());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setSortOrder(data.getSortOrder());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public AuxDataViewDO update(AuxDataViewDO data) throws Exception {
        AuxData entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxData.class, data.getId());
        
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setIsReportable(data.getIsReportable());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setSortOrder(data.getSortOrder());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(AuxDataViewDO data) throws Exception {
        AuxData entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AuxData.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}
