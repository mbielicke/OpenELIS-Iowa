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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.OrderContainerDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;

public class ImportOrder {
    protected OrderManager orderMan;
    
    protected void loadReportToBillTo(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO orderDO;
        OrganizationDO orgDO;
        SampleOrganizationViewDO reportToDO, billToDO;

        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);
        
        //report to
        orderDO = orderMan.getOrder();
        orgDO = orderDO.getReportTo();
        reportToDO = new SampleOrganizationViewDO();
        
        if(orgDO != null){
            reportToDO.setOrganizationId(orgDO.getId());
            reportToDO.setOrganizationAttention(orderDO.getReportToAttention());
            reportToDO.setTypeId(DictionaryCache.getIdFromSystemName("org_report_to"));
            reportToDO.setOrganizationName(orgDO.getName());
            reportToDO.setOrganizationCity(orgDO.getAddress().getCity());
            reportToDO.setOrganizationState(orgDO.getAddress().getState());
            man.getOrganizations().addOrganization(reportToDO);
        }
        
        //bill to
        billToDO = new SampleOrganizationViewDO();
        orgDO = orderMan.getOrder().getBillTo();
        
        if(orgDO != null){
            billToDO.setOrganizationId(orgDO.getId());
            billToDO.setOrganizationAttention(orderDO.getBillToAttention());
            billToDO.setTypeId(DictionaryCache.getIdFromSystemName("org_bill_to"));
            billToDO.setOrganizationName(orgDO.getName());
            billToDO.setOrganizationCity(orgDO.getAddress().getCity());
            billToDO.setOrganizationState(orgDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToDO);
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
                itemDO.setContainer(DictionaryCache.getEntryFromId(containerDO.getContainerId()).getEntry());
                itemDO.setTypeOfSampleId(containerDO.getTypeOfSampleId());
                if(containerDO.getTypeOfSampleId() != null)
                    itemDO.setTypeOfSample(DictionaryCache.getEntryFromId(containerDO.getTypeOfSampleId()).getEntry());
            }
        }
    }
    
    public ArrayList<OrderTestViewDO> getTestsFromOrder(Integer orderId) throws Exception {
        OrderTestManager testManager;
        
        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);
        
        testManager = orderMan.getTests();
        
        return testManager.getTests();
    }
}
