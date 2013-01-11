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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.ExchangeExternalTermDO;
import org.openelis.domain.ExchangeExternalTermViewDO;
import org.openelis.entity.ExchangeExternalTerm;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.ExchangeLocalTermMeta;

@Stateless
@SecurityDomain("openelis")

public class ExchangeExternalTermBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;
    
    public ArrayList<ExchangeExternalTermDO> fetchByExchangeLocalTermId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("ExchangeExternalTerm.FetchByExchangeLocalTermId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<ExchangeExternalTermViewDO> fetchByReferenceTableIdReferenceIdsProfileIds(Integer referenceTableId,
                                                                                           Collection<Integer> referenceIds,
                                                                                           Collection<Integer> profileIds) throws Exception {
        int i;
        Integer referenceId;
        Query query;
        ExchangeExternalTermViewDO existing;
        List<ExchangeExternalTermViewDO> results;
        HashMap<Integer, ExchangeExternalTermViewDO> refIdExtTermMap;
        HashMap<Integer, Integer> profIndexMap;
        ArrayList<ExchangeExternalTermViewDO> returnList;
        Iterator<ExchangeExternalTermViewDO> iter;

        query = manager.createNamedQuery("ExchangeExternalTerm.FetchByReferenceTableIdReferenceIdsProfileIds");
        query.setParameter("referenceTableId", referenceTableId);
        query.setParameter("referenceIds", referenceIds);
        query.setParameter("profileIds", profileIds);

        results = query.getResultList();
        if (results.isEmpty())
            throw new NotFoundException();       
        
        profIndexMap = new HashMap<Integer, Integer>();
        /*
         * This mapping is created to make it easy to determine the order of precedence
         * of the profiles. The profile with the lower number as the value in the
         * map is of a higher precedence. 
         */
        i = 0;
        for (Integer id : profileIds) 
            profIndexMap.put(id, i++);        
        
        refIdExtTermMap = new HashMap<Integer, ExchangeExternalTermViewDO>();
        for (ExchangeExternalTermViewDO current : results) {
            referenceId = current.getExchangeLocalTermReferenceId();
            existing = refIdExtTermMap.get(referenceId);
                
            if (existing == null) {
                refIdExtTermMap.put(referenceId, current);
            } else {
                /*
                 * Only the DOs with the priority with the highest precedence defined
                 * for any reference id are kept in the map; i.e. if the priority
                 * with the highest possible precdence is not defined for a reference,
                 * then DO with the next highest is searched for and so on. The 
                 * precedence is determined by order in the list of profile ids
                 * passed to the method.
                 */
                if (profIndexMap.get(current.getProfileId()) < profIndexMap.get(existing.getProfileId())) 
                    refIdExtTermMap.put(referenceId, current);
            }
        }
        
        returnList = new ArrayList<ExchangeExternalTermViewDO>();
        iter = refIdExtTermMap.values().iterator();
        
        while (iter.hasNext()) 
            returnList.add(iter.next());        
        
        return returnList;
    }

    public ExchangeExternalTermDO add(ExchangeExternalTermDO data) throws Exception {
        ExchangeExternalTerm entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new ExchangeExternalTerm();
        entity.setExchangeLocalTermId(data.getExchangeLocalTermId());
        entity.setProfileId(data.getProfileId());
        entity.setIsActive(data.getIsActive());
        entity.setExternalTerm(data.getExternalTerm());
        entity.setExternalDescription(data.getExternalDescription());
        entity.setExternalCodingSystem(data.getExternalCodingSystem());
        entity.setVersion(data.getVersion());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public ExchangeExternalTermDO update(ExchangeExternalTermDO data) throws Exception {
        ExchangeExternalTerm entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(ExchangeExternalTerm.class, data.getId());
        entity.setExchangeLocalTermId(data.getExchangeLocalTermId());
        entity.setProfileId(data.getProfileId());
        entity.setIsActive(data.getIsActive());
        entity.setExternalTerm(data.getExternalTerm());
        entity.setExternalDescription(data.getExternalDescription());
        entity.setExternalCodingSystem(data.getExternalCodingSystem());
        entity.setVersion(data.getVersion());
        
        return data;
    }

    public void delete(ExchangeExternalTermDO data) throws Exception {
        ExchangeExternalTerm entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ExchangeExternalTerm.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(ExchangeExternalTermDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (data.getProfileId() == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             ExchangeLocalTermMeta.getExternalTermExchangeProfileId()));
        if (DataBaseUtil.isEmpty(data.getExternalTerm()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             ExchangeLocalTermMeta.getExternalTermExternalTerm()));
        
        if (list.size() > 0)
            throw list;
    }
}
