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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.entity.TestWorksheet;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.TestWorksheetLocal;
import org.openelis.metamap.TestMetaMap;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("test-select")
public class TestWorksheetBean implements TestWorksheetLocal {
    
    @PersistenceContext(name = "openelis")
    private EntityManager            manager;
    
    private static final TestMetaMap meta = new TestMetaMap();
    
    public TestWorksheetViewDO fetchByTestId(Integer testId) throws Exception {
        Query query;
        TestWorksheetViewDO worksheetDO;        
        
        query = manager.createNamedQuery("TestWorksheet.FetchByTestId");
        query.setParameter("testId", testId);                
        
        try {
            worksheetDO = (TestWorksheetViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        
        return worksheetDO;
    }
    
    public TestWorksheetViewDO add(TestWorksheetViewDO data) throws Exception {
        TestWorksheet testWorksheet;      
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheet = new TestWorksheet();
        
        testWorksheet.setBatchCapacity(data.getBatchCapacity());
        testWorksheet.setFormatId(data.getFormatId());
        testWorksheet.setScriptletId(data.getScriptletId());
        testWorksheet.setTestId(data.getTestId());
        testWorksheet.setTotalCapacity(data.getTotalCapacity());
        
        manager.persist(testWorksheet);

        data.setId(testWorksheet.getId());
        
        return data;

    }

    public TestWorksheetViewDO update(TestWorksheetViewDO data) throws Exception {
        TestWorksheet testWorksheet;
        
        if(!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        testWorksheet = manager.find(TestWorksheet.class, data.getId());
        
        testWorksheet.setBatchCapacity(data.getBatchCapacity());
        testWorksheet.setFormatId(data.getFormatId());
        testWorksheet.setScriptletId(data.getScriptletId());
        testWorksheet.setTestId(data.getTestId());
        testWorksheet.setTotalCapacity(data.getTotalCapacity());
        
        return data;

    }

    public void validate(TestWorksheetViewDO worksheetDO) throws Exception {
        boolean checkForMultiple = true;
        ValidationErrorsList exceptionList;

        //
        // This check is put here in order to distinguish between the cases where
        // the TestWorksheetDO was changed on the screen and where it was not.
        // This is necessary because it is possible for the users to enter no
        // information on the screen in the fields related to the DO and
        // commit the data and since the DO can't be null because then the fields
        // on the screen won't get refreshed on fetch, the validation code below
        // will make error messages get displayed on the screen when there was
        // no fault of the user.
        //
        if (!worksheetDO.isChanged())
            return;

        exceptionList = new ValidationErrorsList();
        
        if (worksheetDO.getBatchCapacity() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      meta.getTestWorksheet().getBatchCapacity()));
            checkForMultiple = false;
        }
        if (worksheetDO.getTotalCapacity() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      meta.getTestWorksheet().getTotalCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getBatchCapacity() != null && worksheetDO.getBatchCapacity() <= 0) {
            exceptionList.add(new FieldErrorException("batchCapacityMoreThanZeroException",
                                                      meta.getTestWorksheet().getBatchCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getTotalCapacity() != null && worksheetDO.getTotalCapacity() <= 0) {
            exceptionList.add(new FieldErrorException("totalCapacityMoreThanZeroException",
                                                      meta.getTestWorksheet().getTotalCapacity()));
            checkForMultiple = false;
        }

        if (worksheetDO.getFormatId() == null) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      meta.getTestWorksheet().getFormatId()));
        }

        if (checkForMultiple) {
            if ( (worksheetDO.getTotalCapacity() % worksheetDO.getBatchCapacity()) != 0) {
                exceptionList.add(new FieldErrorException("totalCapacityMultipleException",
                                                          meta.getTestWorksheet()
                                                              .getTotalCapacity()));
            }
        }
        
        if(exceptionList.size() > 0)
            throw exceptionList;
    }


}
