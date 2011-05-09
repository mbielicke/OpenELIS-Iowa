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


import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.entity.TestWorksheet;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.meta.TestMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestWorksheetBean implements TestWorksheetLocal {
    
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
    
    public TestWorksheetViewDO add(TestWorksheetViewDO data) throws Exception {
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

    public TestWorksheetViewDO update(TestWorksheetViewDO data) throws Exception {
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

    public void validate(TestWorksheetViewDO data) throws Exception {
        boolean checkForMultiple = true;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        
        if (DataBaseUtil.isEmpty(data.getSubsetCapacity())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetSubsetCapacity()));
            checkForMultiple = false;
        } else if (data.getSubsetCapacity() <= 0) {
            list.add(new FieldErrorException("subsetCapacityMoreThanZeroException",
                                             TestMeta.getWorksheetSubsetCapacity()));
            checkForMultiple = false;
        }
        if (DataBaseUtil.isEmpty(data.getTotalCapacity())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetTotalCapacity()));
            checkForMultiple = false;
        } else if (data.getTotalCapacity() <= 0) {
            list.add(new FieldErrorException("totalCapacityMoreThanZeroException",
                                             TestMeta.getWorksheetTotalCapacity()));
            checkForMultiple = false;
        }

        if (DataBaseUtil.isEmpty(data.getFormatId())) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             TestMeta.getWorksheetFormatId()));
        }

        if (checkForMultiple) {
            if ( (data.getTotalCapacity() % data.getSubsetCapacity()) != 0) {
                list.add(new FieldErrorException("totalCapacityMultipleException",
                                                 TestMeta.getWorksheetTotalCapacity()));
            }
        }
        
        if(list.size() > 0)
            throw list;
    }


}
