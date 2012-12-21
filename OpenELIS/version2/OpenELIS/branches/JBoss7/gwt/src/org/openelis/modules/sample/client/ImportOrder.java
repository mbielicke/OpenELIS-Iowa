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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.exception.MultipleNoteException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.TestManager;
import org.openelis.modules.auxData.client.AuxDataService;
import org.openelis.modules.test.client.TestService;

public abstract class ImportOrder {
    protected OrderManager         orderMan;
    
    protected AuxFieldGroupManager auxFieldGroupMan;
    
    protected int                  lastAuxFieldIndex;  
    
    protected Integer              reportToId, billToId, secondReportToId, supplementalId;
    
    protected static final String  AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
        
    protected ImportOrder() throws Exception {
            
        reportToId = DictionaryCache.getIdBySystemName("org_report_to");
        billToId = DictionaryCache.getIdBySystemName("org_bill_to");
        secondReportToId = DictionaryCache.getIdBySystemName("org_second_report_to");
        supplementalId = DictionaryCache.getIdBySystemName("test_analyte_suplmtl");
    }   
    
    protected ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager,
                                                   String sysVariableKey) throws Exception {
        Integer auxGroupId;
        AuxDataDO auxData;
        ArrayList<AuxDataViewDO> auxDataList;
        ValidationErrorsList errors;

        if (orderId == null)
            return null;

        auxData = new AuxDataDO();
        auxData.setReferenceId(orderId);
        auxData.setReferenceTableId(ReferenceTable.ORDER);

        orderMan = null;
        auxFieldGroupMan = null;
        auxDataList = AuxDataService.get().fetchByRefId(auxData);

        // we don't want to use a hard-coded reference to aux group (language).
        // Use one level indirect by looking up system variable that points to
        // the aux group
        auxGroupId = ((IdVO)AuxDataService.get().getAuxGroupIdFromSystemVariable(sysVariableKey)).getId();
        
        errors = new ValidationErrorsList();

        loadFieldsFromAuxData(auxDataList, auxGroupId, manager, errors);
        loadOrganizations(orderId, manager);
        loadSampleItems(orderId, manager);
        loadAnalyses(orderId,  manager, errors);       
        loadNotes(orderId, manager, errors);
        
        return errors;
    }
    
    protected abstract void loadFieldsFromAuxData(ArrayList<AuxDataViewDO> auxDataList, Integer auxGroupId,
                                                  SampleManager manager, ValidationErrorsList errors) throws Exception;    
    
    protected void loadOrganizations(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO order;
        OrderOrganizationManager orderOrgMan;
        SampleOrganizationManager samOrgMan;
        OrderOrganizationViewDO ordOrg, ordReportTo;
        SampleOrganizationViewDO samReportTo, samBillTo;

        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);
        
        orderOrgMan = orderMan.getOrganizations(); 
        samOrgMan = man.getOrganizations();
        ordReportTo = null;
        samReportTo = null;
        samBillTo = null;
        
        for (int i = 0; i < orderOrgMan.count(); i++) {
            ordOrg = orderOrgMan.getOrganizationAt(i);
            /*
             * create the report-to, bill-to and secondary report-to organizations
             * for the sample if the corresponding organizations are defined in
             * the order
             */
            if (reportToId.equals(ordOrg.getTypeId())) {
                ordReportTo = ordOrg;
                samReportTo = createSampleOrganization(ordReportTo, reportToId);
                samOrgMan.addOrganization(samReportTo);
            } else if (billToId.equals(ordOrg.getTypeId())) {
                samBillTo = createSampleOrganization(ordOrg, billToId);
                samOrgMan.addOrganization(samBillTo);
            } else if (secondReportToId.equals(ordOrg.getTypeId())) {
                samOrgMan.addOrganization(createSampleOrganization(ordOrg, secondReportToId));
            }
        }       
                
        /*
         * if report-to was not found then set the ship-to as the report-to 
         */
        order = orderMan.getOrder();
        if (samReportTo == null)
            samOrgMan.addOrganization(createSampleOrganization(order, reportToId));        
        
        /*
         * if bill-to was not found and if report-to was found then set it as the
         * bill-to otherwise set the ship-to as the bill-to 
         */
        if (samBillTo == null) {
            if (ordReportTo != null)
                samOrgMan.addOrganization(createSampleOrganization(ordReportTo, billToId));
            else
                samOrgMan.addOrganization(createSampleOrganization(order, billToId));
        }
    }
    
    protected void loadSampleItems(Integer orderId, SampleManager man) throws Exception {
        int addedIndex;
        OrderContainerManager containerMan;
        OrderContainerDO container;
        SampleItemManager itemMan;
        SampleItemViewDO item;
        
        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);
        
        containerMan = orderMan.getContainers();
        itemMan = man.getSampleItems();
        
        for (int i = 0; i < containerMan.count(); i++){
            container = containerMan.getContainerAt(i);
            addedIndex = itemMan.addSampleItem();
            item = itemMan.getSampleItemAt(addedIndex);
            item.setContainerId(container.getContainerId());
            item.setContainer(DictionaryCache.getById(container.getContainerId()).getEntry());
            item.setTypeOfSampleId(container.getTypeOfSampleId());
            if (container.getTypeOfSampleId() != null)
                item.setTypeOfSample(DictionaryCache.getById(container.getTypeOfSampleId()).getEntry());     
        }
        
        //
        // if the order has tests specified and there are no sample items either
        // preexisting or created above, a sample item must be added to which the
        // tests can be assigned
        //
        if (orderMan.getTests().count() > 0 && itemMan.count() == 0)
            itemMan.addSampleItem();
    }
    
    protected void loadAnalyses(Integer orderId, SampleManager manager, ValidationErrorsList errors) {
        Query query;
        QueryData testField, methodField;
        ArrayList<QueryData> fields;
        OrderTestViewDO orderTest;
        OrderTestManager orderTestMan;
        TestViewDO test;
        SampleItemManager itemMan;
        HashMap<Integer, TestManager> testMap;

        try {
            if (orderMan == null)
                orderMan = OrderManager.fetchById(orderId);

            orderTestMan = orderMan.getTests();
            itemMan = manager.getSampleItems();
        } catch (Exception e) {
            e.printStackTrace();          
            return;
        }

        query = new Query();
        fields = new ArrayList<QueryData>();

        testField = new QueryData();
        testField.type = QueryData.Type.STRING;
        fields.add(testField);

        methodField = new QueryData();
        methodField.type = QueryData.Type.STRING;
        fields.add(methodField);

        query.setFields(fields);
        testMap = new HashMap<Integer, TestManager>();
        for (int i = 0; i < orderTestMan.count(); i++ ) {
            orderTest = orderTestMan.getTestAt(i);
            /*
             * check to see if this test is inactive and if it is then try to
             * find one with the same name and method that is active
             */
            if ("N".equals(orderTest.getIsActive())) {
                testField.query = orderTest.getTestName();
                methodField.query = orderTest.getMethodName();

                try {
                    test = TestService.get().fetchActiveByNameMethodName(query);
                    orderTest.setTestId(test.getId());
                    orderTest.setDescription(test.getDescription());
                } catch (NotFoundException nfE) {
                    /*
                     * add an error if such a test couldn't be found
                     */
                    errors.add(new FormErrorException("inactiveTestOnOrderException",
                                                      orderTest.getTestName(),
                                                      orderTest.getMethodName()));
                    continue;
                } catch (Exception anyE) {
                    anyE.printStackTrace();
                    continue;
                }
            }

            try {
                /*
                 * load the analysis with data from the order
                 */
                loadAnalysis(orderTest, orderTestMan.getAnalytesAt(i), itemMan, testMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void loadNotes(Integer orderId, SampleManager man, ValidationErrorsList errors) throws Exception {
        NoteViewDO note;
        NoteManager ordNoteMan, samNoteMan;
        
        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);
        
        ordNoteMan = orderMan.getSampleNotes();
        if (ordNoteMan.count() == 0)
            return;
        
        samNoteMan = man.getInternalNotes();
        note = new NoteViewDO();
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        note.setIsExternal("N");
        note.setSystemUserId(UserCache.getId());
        note.setSystemUser(UserCache.getName());
        note.setSubject(Screen.consts.get("orderNoteSubject"));
        note.setText(ordNoteMan.getNoteAt(0).getText());
        
        try {
            samNoteMan.addNote(note);
        } catch (MultipleNoteException e) {
            /*
             * This exception can be thrown if the sample manager alreayd has an
             * internal note and the imported order has a sample note and 
             * NoteManager doesn't allow adding the new note because a sample can
             * only have one new internal note. If this exception is thrown by
             * this method then the whole process of importing the order could be
             * abandoned. 
             */
            errors.add(new FormErrorException("multipleInternalNoteException"));
        }
    }
    
    protected DictionaryDO getDictionaryByKey(String key, String categorySystemName) {
        Integer id;
        ArrayList<DictionaryDO> entries;

        if (key != null) {
            try {
                id = new Integer(key);
                entries = CategoryCache.getBySystemName(categorySystemName);
                for (DictionaryDO data : entries) {
                    if (id.equals(data.getId()))
                        return data;
                }
            } catch (NumberFormatException e) {
                return null;
            }
            
        }
        return null;
    }
    
    protected DictionaryDO getDictionaryByEntry(String entry, String categorySystemName) {
        ArrayList<DictionaryDO> entries;

        if (entry != null) {
            entries = CategoryCache.getBySystemName(categorySystemName);
            for (DictionaryDO data : entries) {
                if (entry.equals(data.getEntry()))
                    return data;
            }
        }
        return null;
    } 
    
    protected void saveAuxData(AuxDataViewDO auxData, ValidationErrorsList errors, SampleManager manager) throws Exception {
        int j;
        AuxFieldManager afman;
        AuxFieldValueManager afvman;
        AuxFieldViewDO auxfData;
        
        // get a new manager if this aux group's id is encountered for the first time
        if (auxFieldGroupMan == null || !auxData.getGroupId().equals(auxFieldGroupMan.getGroup().getId())) {
            auxFieldGroupMan = AuxFieldGroupManager.fetchById(auxData.getGroupId());
            lastAuxFieldIndex = 0;
        }
        afman = auxFieldGroupMan.getFields();        
        
        if (afman.count() < lastAuxFieldIndex) {
            errors.add(new FormErrorException("orderAuxDataNotFoundError", auxData.getAnalyteName()));
            return;
        }
        
        auxfData = auxFieldGroupMan.getFields().getAuxFieldAt(lastAuxFieldIndex);
        /* 
         * find out if the aux field in the aux group at this index is the same
         * as the one that this aux data is linked to in the order
         */  
        if (auxData.getAuxFieldId().equals(auxfData.getId())) {
            // if it matches then add the aux data to the sample  
            afvman = auxFieldGroupMan.getFields().getValuesAt(lastAuxFieldIndex);
            manager.getAuxData().addAuxDataFieldAndValues(auxData, auxfData, afvman.getValues());
            lastAuxFieldIndex++;
            return;
        } else {
            j = 0;
            /*
             * if it doesn't match then find where the aux field is and add the
             * aux data to the sample  
             */
            while (j < afman.count()) {
                auxfData = afman.getAuxFieldAt(j);
                if (auxData.getAuxFieldId().equals(auxfData.getId())) {
                    afvman = auxFieldGroupMan.getFields().getValuesAt(j);
                    manager.getAuxData().addAuxDataFieldAndValues(auxData,
                                                                  auxfData, afvman.getValues());
                    return;
                }
            }
        }
        
        errors.add(new FormErrorException("orderAuxDataNotFoundError", auxData.getAnalyteName()));
    }
    
    protected SampleOrganizationViewDO createSampleOrganization(OrderOrganizationViewDO org, Integer typeId) {
        SampleOrganizationViewDO data;
        
        data = new SampleOrganizationViewDO();
        data.setOrganizationId(org.getOrganizationId());
        data.setOrganizationAttention(org.getOrganizationAttention());
        data.setTypeId(typeId);
        data.setOrganizationName(org.getOrganizationName());
        data.setOrganizationMultipleUnit(org.getOrganizationAddressMultipleUnit());
        data.setOrganizationStreetAddress(org.getOrganizationAddressStreetAddress());
        data.setOrganizationCity(org.getOrganizationAddressCity());
        data.setOrganizationState(org.getOrganizationAddressState());
        data.setOrganizationZipCode(org.getOrganizationAddressZipCode());
        data.setOrganizationCountry(org.getOrganizationAddressCountry());
        
        return data;
    }
    
    protected SampleOrganizationViewDO createSampleOrganization(OrderViewDO order, Integer typeId) {
        SampleOrganizationViewDO data;
        OrganizationDO org;
        AddressDO addr;
        
        org = order.getOrganization();
        addr = org.getAddress();
        
        data = new SampleOrganizationViewDO();        
        data.setOrganizationId(org.getId());
        data.setOrganizationAttention(order.getOrganizationAttention());
        data.setTypeId(typeId);
        data.setOrganizationName(org.getName());
        data.setOrganizationMultipleUnit(addr.getMultipleUnit());
        data.setOrganizationStreetAddress(addr.getStreetAddress());
        data.setOrganizationCity(addr.getCity());
        data.setOrganizationState(addr.getState());
        data.setOrganizationZipCode(addr.getZipCode());
        data.setOrganizationCountry(addr.getCountry());
        
        return data;
    }
    
    private void loadAnalysis(OrderTestViewDO orderTest, OrderTestAnalyteManager orderTestAnaMan,
                              SampleItemManager itemMan, HashMap<Integer, TestManager> testMap) throws Exception {
        int sequence, anaIndex;
        Integer testId;
        TestManager testMan;
        AnalysisManager anaMan;

        sequence = orderTest.getItemSequence();
        /*
         * verify whether there is a sample item(container) to which this test
         * is assigned and if there isn't one then add the analyses to the first
         * sample item
         */
        if (sequence >= itemMan.count())
            sequence = 0;

        anaMan = itemMan.getAnalysisAt(sequence);
        anaIndex = anaMan.addAnalysis();

        testId = orderTest.getTestId();
        testMan = testMap.get(testId);
        if (testMan == null) {
            testMan = TestManager.fetchWithSampleTypes(testId);
            testMap.put(testId, testMan);
        }
        /*
         * set the defaults e.g. section and unit and load the results
         */
        anaMan.setTestAt(testMan, anaIndex, true);
        /*
         * set the row analytes under the analysis repotable if they were added
         * to this test in the order 
         */
        setAnalytesReportable(orderTestAnaMan, anaMan.getAnalysisResultAt(anaIndex));
    }
        
    private void setAnalytesReportable(OrderTestAnalyteManager analyteMan, 
                                       AnalysisResultManager resultMan) {
        int i;
        boolean reportable;
        HashSet<Integer> anaSet;
        ResultViewDO data;
        
        if (resultMan.rowCount() == 0)
            return;
        
        anaSet = new HashSet<Integer>(); 
        for (i = 0; i < analyteMan.count(); i++) 
            anaSet.add(analyteMan.getAnalyteAt(i).getAnalyteId()); 
        
        i = 0;
        while (i < resultMan.rowCount()) {
            data = resultMan.getResultAt(i, 0);
            /*
             * if this analyte was added to the test in the order, then mark it 
             * as reportable under this analysis otherwise mark it not reportable  
             */
            reportable = anaSet.contains(data.getAnalyteId());
            
            if (supplementalId.equals(data.getTestAnalyteTypeId()) && !reportable) {
                /* 
                 * supplemental analytes are only shown if they are marked reportable  
                 *                
                 */
                resultMan.removeRowAt(i);
                continue;
            }
            data.setIsReportable(reportable ? "Y" : "N");
            i++;
        }
    }
}