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
package org.openelis.manager;

import java.util.HashMap;

import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;

public class OrderManagerProxy {

    protected static final String MANAGER_SERVICE_URL = "org.openelis.modules.order.server.OrderService",
                                  TEST_MANAGER_SERVICE_URL = "org.openelis.modules.test.server.TestService";
    protected ScreenService       service, testService;

    public OrderManagerProxy() {
        service = new ScreenService("controller?service=" + MANAGER_SERVICE_URL);
        testService = new ScreenService("controller?service=" + TEST_MANAGER_SERVICE_URL);
    }

    public OrderManager fetchById(Integer id) throws Exception {
        return service.call("fetchById", id);
    }
    
    public OrderManager fetchWithOrganizations(Integer id) throws Exception {
        return service.call("fetchWithOrganizations", id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return service.call("fetchWithItems", id);
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return service.call("fetchWithFills", id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return service.call("fetchWithNotes", id);
    }
    
    public OrderManager fetchWithTests(Integer id) throws Exception {
        return service.call("fetchWithTests", id);
    }
    
    public OrderManager fetchWithContainers(Integer id) throws Exception {
        return service.call("fetchWithContainers", id);
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        return service.call("fetchWithRecurring", id);
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        return service.call("fetchRecurrenceByOrderId", id);
    }

    public OrderManager add(OrderManager man) throws Exception {
        return service.call("add", man);
    }

    public OrderManager update(OrderManager man) throws Exception {
        return service.call("update", man);
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate", id);
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }

    public void validate(OrderManager man) throws Exception {
        int i, testCount, contCount;
        Integer sequence, testId;
        Boolean testHasSamType;
        OrderTestManager testMan;
        OrderContainerManager contMan;
        OrderTestViewDO test;
        OrderContainerDO cont;
        ValidationErrorsList warnList;
        HashMap<Integer, Boolean> samTypeMap;
        TestTypeOfSampleManager samTypeMan;

        testMan = man.tests;
        contMan = man.containers;
        warnList = new ValidationErrorsList();
        
        testCount = testMan == null ? 0 : testMan.count();
        contCount = contMan == null ? 0 : contMan.count();
        
        samTypeMap = new HashMap<Integer, Boolean>();
        
        for (i = 0; i < testCount; i++ ) {
            test = testMan.getTestAt(i);
            sequence = test.getItemSequence();
            testId = test.getTestId();
            
            if (sequence == null)
                continue;
            
            if (sequence >= contCount) {
                /*
                 * no container is present for this item sequence 
                 */
                warnList.add(new FieldErrorWarning("noContainerWithItemNumWarning", null,
                                                   sequence.toString(), getTestLabel(test)));
            } else if (sequence >= 0 && testId != null) {
                cont = contMan.getContainerAt(sequence);
                if (cont.getTypeOfSampleId() == null) 
                    continue;                
                
                testHasSamType = samTypeMap.get(testId);
                /*
                 * fetch the sample types for this test and find out if the sample
                 * type specified for the container is valid for the test 
                 */
                if (testHasSamType == null) {
                    try {
                        samTypeMan = testService.call("fetchSampleTypeByTestId", testId);
                        testHasSamType = testHasSampleType(testId, cont.getTypeOfSampleId(), samTypeMan);
                    } catch (NotFoundException e) {
                        testHasSamType = Boolean.FALSE;
                    }
                    
                    samTypeMap.put(testId, testHasSamType);
                }
                
                if (!testHasSamType) 
                    /*
                     * the sample type is not valid for the test    
                     */
                    warnList.add(new FieldErrorWarning("invalidSampleTypeForTestWarning", null,
                                                       sequence.toString(), getTestLabel(test)));                                   
            }
        }
        
        for (i = 0; i < contCount; i++) {
            cont = contMan.getContainerAt(i);
            if (cont.getTypeOfSampleId() == null) 
                warnList.add(new FieldErrorWarning("noSampleTypeForContainerWarning", null,
                                                   cont.getItemSequence().toString()));            
        }

        if (warnList.size() > 0)
            throw warnList;
    }

    private Boolean testHasSampleType(Integer testId, Integer sampleTypeId,
                                      TestTypeOfSampleManager samTypeMan) {
        TestTypeOfSampleDO samType;
        for (int i = 0; i < samTypeMan.count(); i++) {
            samType = samTypeMan.getTypeAt(i);
            if (DataBaseUtil.isSame(samType.getTypeOfSampleId(), sampleTypeId)) 
                return Boolean.TRUE;            
        }
        return Boolean.FALSE;
    }
    
    private String getTestLabel(OrderTestViewDO test) {
        return (test.getTestId() == null) ? "" :
            DataBaseUtil.concatWithSeparator(test.getTestName(), ", ", test.getMethodName());
    }
}