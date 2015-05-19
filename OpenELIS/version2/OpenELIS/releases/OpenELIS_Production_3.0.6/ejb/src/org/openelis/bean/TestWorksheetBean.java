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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.entity.TestWorksheet;
import org.openelis.meta.TestMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class TestWorksheetBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;
    
    public TestWorksheetViewDO fetchByTestId(Integer testId) throws Exception {
        Query query;
        TestWorksheetViewDO data;        
        
        data = null;
        query = manager.createNamedQuery("TestWorksheet.FetchByTestId");
        query.setParameter("testId", testId);                
        
        try {
            data = (TestWorksheetViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            //ignore the exception because
            //it is possible to have a test without a test_worksheet record
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        
        return data;
    }
    
    public ArrayList<TestWorksheetViewDO> fetchByTestIds(Collection<Integer> testIds) throws Exception {
        Query query;

        query = manager.createNamedQuery("TestWorksheet.FetchByTestIds");
        query.setParameter("testIds", testIds);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public TestWorksheetDO add(TestWorksheetDO data) throws Exception {
        TestWorksheet entity;      
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new TestWorksheet();
        
        entity.setSubsetCapacity(data.getSubsetCapacity());
        entity.setFormatId(data.getFormatId());
        entity.setScriptletId(data.getScriptletId());
        entity.setTestId(data.getTestId());
        entity.setTotalCapacity(data.getTotalCapacity());
        
        manager.persist(entity);

        data.setId(entity.getId());
        
        return data;

    }

    public TestWorksheetDO update(TestWorksheetDO data) throws Exception {
        TestWorksheet entity;
        
        if(!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(TestWorksheet.class, data.getId());
        
        entity.setSubsetCapacity(data.getSubsetCapacity());
        entity.setFormatId(data.getFormatId());
        entity.setScriptletId(data.getScriptletId());
        entity.setTestId(data.getTestId());
        entity.setTotalCapacity(data.getTotalCapacity());
        
        return data;

    }

    public void validate(TestWorksheetDO data) throws Exception {
        boolean checkForMultiple = true;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        
        if (DataBaseUtil.isEmpty(data.getSubsetCapacity())) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getWorksheetSubsetCapacity()));
            checkForMultiple = false;
        } else if (data.getSubsetCapacity() <= 0) {
            list.add(new FieldErrorException(Messages.get().subsetCapacityMoreThanZeroException(),
                                             TestMeta.getWorksheetSubsetCapacity()));
            checkForMultiple = false;
        }
        if (DataBaseUtil.isEmpty(data.getTotalCapacity())) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getWorksheetTotalCapacity()));
            checkForMultiple = false;
        } else if (data.getTotalCapacity() <= 0) {
            list.add(new FieldErrorException(Messages.get().totalCapacityMoreThanZeroException(),
                                             TestMeta.getWorksheetTotalCapacity()));
            checkForMultiple = false;
        }

        if (DataBaseUtil.isEmpty(data.getFormatId())) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             TestMeta.getWorksheetFormatId()));
        }

        if (checkForMultiple) {
            if ( (data.getTotalCapacity() % data.getSubsetCapacity()) != 0) {
                list.add(new FieldErrorException(Messages.get().totalCapacityMultipleException(),
                                                 TestMeta.getWorksheetTotalCapacity()));
            }
        }
        
        if(list.size() > 0)
            throw list;
    }


}
