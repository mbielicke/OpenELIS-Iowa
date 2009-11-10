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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.entity.AuxFieldGroup;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AuxFieldGroupLocal;

@Stateless
@SecurityDomain("openelis")
//@RolesAllowed("organization-select")
public class AuxFieldGroupBean implements AuxFieldGroupLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;

    public AuxFieldGroupDO fetchById(Integer id) throws Exception {
        Query query;
        AuxFieldGroupDO data;
        
        query = manager.createNamedQuery("AuxFieldGroup.AuxFieldGroupDO");
        query.setParameter("id", id);
        try {
            data = (AuxFieldGroupDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    
    public AuxFieldGroupDO add(AuxFieldGroupDO data) throws Exception {
        AuxFieldGroup entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AuxFieldGroup();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public AuxFieldGroupDO update(AuxFieldGroupDO data) throws Exception {
        AuxFieldGroup entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxFieldGroup.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());

        return data;
    }
}
