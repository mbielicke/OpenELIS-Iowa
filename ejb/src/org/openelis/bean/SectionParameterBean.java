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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SectionParameterDO;
import org.openelis.entity.SectionParameter;
import org.openelis.meta.SectionMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class SectionParameterBean  {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;
    
    public ArrayList<SectionParameterDO> fetchBySectionId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("SectionParameter.FetchBySectionId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public ArrayList<SectionParameterDO> fetchBySectionIdAndTypeId(Integer id, Integer typeId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("SectionParameter.FetchBySectionIdAndTypeId");
        query.setParameter("id", id);
        query.setParameter("typeId", typeId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }
    
    public SectionParameterDO add(SectionParameterDO data) throws Exception {
        SectionParameter entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new SectionParameter();
        entity.setSectionId(data.getSectionId());        
        entity.setTypeId(data.getTypeId());        
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public SectionParameterDO update(SectionParameterDO data) throws Exception {
        SectionParameter entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SectionParameter.class, data.getId());
        entity.setSectionId(data.getSectionId());        
        entity.setTypeId(data.getTypeId());        
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(SectionParameterDO data) throws Exception {
        SectionParameter entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SectionParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(SectionParameterDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             SectionMeta.getParameterTypeId()));
        
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             SectionMeta.getParameterValue()));
        if (list.size() > 0)
            throw list;
    }

}
