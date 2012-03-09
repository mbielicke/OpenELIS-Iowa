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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;

public abstract class ImportOrder {
    protected OrderManager         orderMan;
    
    protected AuxFieldGroupManager auxFieldGroupMan;
    
    protected int                  lastAuxFieldIndex;  
    
    protected static final String  AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    
    protected ScreenService        auxDataService;
    
    protected ImportOrder() {
        auxDataService = new ScreenService("controller?service=" + AUX_DATA_SERVICE_URL);
    }
    
    protected ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager,
                                                   String sysVariableKey) throws Exception {
        Integer auxGroupId;
        AuxDataDO auxData;
        ArrayList<AuxDataViewDO> auxDataList;

        if (orderId == null)
            return null;

        auxData = new AuxDataDO();
        auxData.setReferenceId(orderId);
        auxData.setReferenceTableId(ReferenceTable.ORDER);

        orderMan = null;
        auxFieldGroupMan = null;
        auxDataList = auxDataService.callList("fetchByRefId", auxData);

        // we don't want to use a hard-coded reference to aux group (language).
        // Use one level indirect by looking up system variable that points to
        // the aux group
        auxGroupId = ((IdVO)auxDataService.call("getAuxGroupIdFromSystemVariable",
                                                sysVariableKey)).getId();

        // grab order for report to/bill to
        loadReportToBillTo(orderId, manager);

        // grab order tests including number of bottles
        loadSampleItems(orderId, manager);

        // inject the data into the manager
        return importData(auxDataList, auxGroupId, manager);
    }
    
    public ArrayList<OrderTestViewDO> getTestsFromOrder(Integer orderId) throws Exception {
        OrderTestManager testManager;
        
        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);
        
        testManager = orderMan.getTests();
        
        return testManager.getTests();
    }   
    
    protected abstract ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId, SampleManager manager) throws Exception;    
    
    protected void loadReportToBillTo(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO              orderDO;
        OrganizationDO           shipToDO, reportToDO, billToDO;
        SampleOrganizationViewDO reportToSampOrg, billToSampOrg;

        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);

        orderDO    = orderMan.getOrder();
        shipToDO   = orderDO.getOrganization();
        reportToDO = orderDO.getReportTo();
        billToDO   = orderDO.getBillTo();

        //report to
        reportToSampOrg = new SampleOrganizationViewDO();
        if (reportToDO != null) {
            reportToSampOrg.setOrganizationId(reportToDO.getId());
            reportToSampOrg.setOrganizationAttention(orderDO.getReportToAttention());
            reportToSampOrg.setTypeId(DictionaryCache.getIdBySystemName("org_report_to"));
            reportToSampOrg.setOrganizationName(reportToDO.getName());
            reportToSampOrg.setOrganizationCity(reportToDO.getAddress().getCity());
            reportToSampOrg.setOrganizationState(reportToDO.getAddress().getState());
            man.getOrganizations().addOrganization(reportToSampOrg);
        } else {
            reportToSampOrg.setOrganizationId(shipToDO.getId());
            reportToSampOrg.setOrganizationAttention(orderDO.getOrganizationAttention());
            reportToSampOrg.setTypeId(DictionaryCache.getIdBySystemName("org_report_to"));
            reportToSampOrg.setOrganizationName(shipToDO.getName());
            reportToSampOrg.setOrganizationCity(shipToDO.getAddress().getCity());
            reportToSampOrg.setOrganizationState(shipToDO.getAddress().getState());
            man.getOrganizations().addOrganization(reportToSampOrg);
        }
        
        //bill to
        billToSampOrg = new SampleOrganizationViewDO();
        if (billToDO != null) {
            billToSampOrg.setOrganizationId(billToDO.getId());
            billToSampOrg.setOrganizationAttention(orderDO.getBillToAttention());
            billToSampOrg.setTypeId(DictionaryCache.getIdBySystemName("org_bill_to"));
            billToSampOrg.setOrganizationName(billToDO.getName());
            billToSampOrg.setOrganizationCity(billToDO.getAddress().getCity());
            billToSampOrg.setOrganizationState(billToDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToSampOrg);
        } else if (reportToDO != null) {
            billToSampOrg.setOrganizationId(reportToDO.getId());
            billToSampOrg.setOrganizationAttention(orderDO.getReportToAttention());
            billToSampOrg.setTypeId(DictionaryCache.getIdBySystemName("org_bill_to"));
            billToSampOrg.setOrganizationName(reportToDO.getName());
            billToSampOrg.setOrganizationCity(reportToDO.getAddress().getCity());
            billToSampOrg.setOrganizationState(reportToDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToSampOrg);
        } else {
            billToSampOrg.setOrganizationId(shipToDO.getId());
            billToSampOrg.setOrganizationAttention(orderDO.getOrganizationAttention());
            billToSampOrg.setTypeId(DictionaryCache.getIdBySystemName("org_bill_to"));
            billToSampOrg.setOrganizationName(shipToDO.getName());
            billToSampOrg.setOrganizationCity(shipToDO.getAddress().getCity());
            billToSampOrg.setOrganizationState(shipToDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToSampOrg);
        }
    }
    
    protected void loadSampleItems(Integer orderId, SampleManager man) throws Exception {
        OrderContainerManager containerMan;
        OrderContainerDO containerDO;
        SampleItemManager itemMan;
        SampleItemViewDO itemDO;
        int addedIndex;
        
        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);
        
        containerMan = orderMan.getContainers();
        itemMan = man.getSampleItems();
        
        for(int i=0; i<containerMan.count(); i++){
            containerDO = containerMan.getContainerAt(i);
            
            for(int j=0; j<containerDO.getNumberOfContainers(); j++){
                addedIndex = itemMan.addSampleItem();
                itemDO = itemMan.getSampleItemAt(addedIndex);
                itemDO.setContainerId(containerDO.getContainerId());
                itemDO.setContainer(DictionaryCache.getById(containerDO.getContainerId()).getEntry());
                itemDO.setTypeOfSampleId(containerDO.getTypeOfSampleId());
                if(containerDO.getTypeOfSampleId() != null)
                    itemDO.setTypeOfSample(DictionaryCache.getById(containerDO.getTypeOfSampleId()).getEntry());
            }
        }
        
        //
        // if the order has tests specified and there are no sample items either
        // preexisting or created above, a sample item must be added to which the
        // tests can be assigned
        //
        if (orderMan.getTests().count() > 0 && itemMan.count() < 1)
            itemMan.addSampleItem();
    }
    
    protected DictionaryDO getDropdownByKey(String key, String dictSystemName) {
        Integer id;
        ArrayList<DictionaryDO> entries;

        if (key != null) {
            try {
                id = new Integer(key);
                entries = CategoryCache.getBySystemName(dictSystemName);
                for (DictionaryDO data : entries) {
                    if (id.equals(data.getId()))
                        return data;
                }
            } catch (Exception e) {
            }
        }
        return null;    
    }
    
    protected DictionaryDO getDropdownByValue(String value, String dictSystemName) {
        ArrayList<DictionaryDO> entries;

        if (value != null) {
            entries = CategoryCache.getBySystemName(dictSystemName);
            for (DictionaryDO data : entries) {
                if (value.equals(data.getEntry()))
                    return data;
            }
        }
        return null;
    } 
    
    protected void saveAuxData(AuxDataViewDO auxData, ValidationErrorsList errorsList, SampleManager manager) throws Exception {
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
            errorsList.add(new FormErrorException("orderAuxDataNotFoundError",                                                  
                                                  auxData.getAnalyteName()));
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
        
        errorsList.add(new FormErrorException("orderAuxDataNotFoundError", auxData.getAnalyteName()));
    }
}
