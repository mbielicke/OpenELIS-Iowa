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

import org.openelis.constants.Messages;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorWarning;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.modules.order.client.OrderService;
import org.openelis.modules.test.client.TestService;

public class IOrderManagerProxy {

    public IOrderManagerProxy() {
    }

    public IOrderManager fetchById(Integer id) throws Exception {
        return OrderService.get().fetchById(id);
    }
    
    public IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        return OrderService.get().fetchWithOrganizations(id);
    }

    public IOrderManager fetchWithItems(Integer id) throws Exception {
        return OrderService.get().fetchWithItems(id);
    }

    public IOrderManager fetchWithFills(Integer id) throws Exception {
        return OrderService.get().fetchWithFills(id);
    }

    public IOrderManager fetchWithNotes(Integer id) throws Exception {
        return OrderService.get().fetchWithNotes(id);
    }
    
    public IOrderManager fetchWithTests(Integer id) throws Exception {
        return OrderService.get().fetchWithTests(id);
    }
    
    public IOrderManager fetchWithContainers(Integer id) throws Exception {
        return OrderService.get().fetchWithContainers(id);
    }
    
    public IOrderManager fetchWithRecurring(Integer id) throws Exception {
        return OrderService.get().fetchWithRecurring(id);
    }
    
    public IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        return OrderService.get().fetchRecurrenceByIorderId(id);
    }

    public IOrderManager add(IOrderManager man) throws Exception {
        return OrderService.get().add(man);
    }

    public IOrderManager update(IOrderManager man) throws Exception {
        return OrderService.get().update(man);
    }

    public IOrderManager fetchForUpdate(Integer id) throws Exception {
        return OrderService.get().fetchForUpdate(id);
    }

    public IOrderManager abortUpdate(Integer id) throws Exception {
        return OrderService.get().abortUpdate(id);
    }

    public void validate(IOrderManager man) throws Exception {
        int i, testCount, contCount;
        Integer sequence, testId;
        Boolean testHasSamType;
        IOrderTestManager testMan;
        IOrderContainerManager contMan;
        IOrderTestViewDO test;
        IOrderContainerDO cont;
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
                warnList.add(new FieldErrorWarning(Messages.get().noContainerWithItemNumWarning(sequence.toString(), getTestLabel(test)), null));
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
                        samTypeMan = TestService.get().fetchSampleTypeByTestId(testId);
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
                    warnList.add(new FieldErrorWarning(Messages.get().invalidSampleTypeForTestWarning(sequence.toString(), getTestLabel(test)), null));                                   
            }
        }
        
        for (i = 0; i < contCount; i++) {
            cont = contMan.getContainerAt(i);
            if (cont.getTypeOfSampleId() == null) 
                warnList.add(new FieldErrorWarning(Messages.get().noSampleTypeForContainerWarning(cont.getItemSequence().toString()), null));            
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
    
    private String getTestLabel(IOrderTestViewDO test) {
        return (test.getTestId() == null) ? "" :
            DataBaseUtil.concatWithSeparator(test.getTestName(), ", ", test.getMethodName());
    }
}