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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.entity.PanelItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AuxFieldGroupLocal;
import org.openelis.local.PanelItemLocal;
import org.openelis.local.TestLocal;
import org.openelis.meta.PanelMeta;

@Stateless
@SecurityDomain("openelis")

public class PanelItemBean implements PanelItemLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;
    
    @EJB 
    private TestLocal                  test;
    
    @EJB
    private AuxFieldGroupLocal         auxFieldGroup;
    
    @SuppressWarnings("unchecked")
    public ArrayList<PanelItemDO> fetchByPanelId(Integer id) throws Exception {
        Query query;
        List list;
        
        query = manager.createNamedQuery("PanelItem.FetchByPanelId");
        query.setParameter("id", id);
        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();

        list = DataBaseUtil.toArrayList(list);
        
        return (ArrayList) list;
    }
    
    public PanelItemDO add(PanelItemDO data) throws Exception {
        PanelItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new PanelItem();       
        entity.setPanelId(data.getPanelId());
        entity.setType(data.getType());
        entity.setSortOrder(data.getSortOrder());
        entity.setName(data.getName());
        entity.setMethodName(data.getMethodName());
        
        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }
    
    public PanelItemDO update(PanelItemDO data) throws Exception {
        PanelItem entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(PanelItem.class, data.getId());       
        entity.setPanelId(data.getPanelId());
        entity.setType(data.getType());
        entity.setSortOrder(data.getSortOrder());
        entity.setName(data.getName());
        entity.setMethodName(data.getMethodName());        

        return data;
    }

    public void delete(PanelItemDO data) throws Exception {
        PanelItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(PanelItem.class, data.getId()); 
        if(entity != null)
            manager.remove(entity);
    }

    public void validate(PanelItemDO data) throws Exception {
        boolean match;
        ValidationErrorsList list;
        TestMethodVO tm;
        ArrayList<TestMethodVO> tests;

        list = new ValidationErrorsList();
        if ("T".equals(data.getType())) {
            tests = test.fetchByName(data.getName(), 10000);
            match = false;

            if (tests.size() == 0) {
                list.add(new FieldErrorException("noActiveTestsException", PanelMeta.getItemName()));
                throw list;
            } else {
                for (int i = 0; i < tests.size(); i++ ) {
                    tm = tests.get(i);
                    if (DataBaseUtil.isSame(tm.getMethodName(), data.getMethodName())) {
                        match = true;
                        break;
                    }
                }

                if ( !match) {
                    list.add(new FieldErrorException("noActiveTestsException",
                                                     PanelMeta.getItemName()));
                    throw list;
                }
            }
        } else if ("A".equals(data.getType())) {
            try {
                auxFieldGroup.fetchActiveByName(data.getName());
            } catch (NotFoundException e) {
                list.add(new FieldErrorException("noActiveAuxGrpException",
                                                 PanelMeta.getItemName()));
                throw list;
            }
        }
    }
}
