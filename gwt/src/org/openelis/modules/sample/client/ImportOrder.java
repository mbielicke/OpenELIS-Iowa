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
            reportToSampOrg.setTypeId(DictionaryCache.getIdFromSystemName("org_report_to"));
            reportToSampOrg.setOrganizationName(reportToDO.getName());
            reportToSampOrg.setOrganizationCity(reportToDO.getAddress().getCity());
            reportToSampOrg.setOrganizationState(reportToDO.getAddress().getState());
            man.getOrganizations().addOrganization(reportToSampOrg);
        } else {
            reportToSampOrg.setOrganizationId(shipToDO.getId());
            reportToSampOrg.setOrganizationAttention(orderDO.getOrganizationAttention());
            reportToSampOrg.setTypeId(DictionaryCache.getIdFromSystemName("org_report_to"));
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
            billToSampOrg.setTypeId(DictionaryCache.getIdFromSystemName("org_bill_to"));
            billToSampOrg.setOrganizationName(billToDO.getName());
            billToSampOrg.setOrganizationCity(billToDO.getAddress().getCity());
            billToSampOrg.setOrganizationState(billToDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToSampOrg);
        } else if (reportToDO != null) {
            billToSampOrg.setOrganizationId(reportToDO.getId());
            billToSampOrg.setOrganizationAttention(orderDO.getReportToAttention());
            billToSampOrg.setTypeId(DictionaryCache.getIdFromSystemName("org_bill_to"));
            billToSampOrg.setOrganizationName(reportToDO.getName());
            billToSampOrg.setOrganizationCity(reportToDO.getAddress().getCity());
            billToSampOrg.setOrganizationState(reportToDO.getAddress().getState());
            man.getOrganizations().addOrganization(billToSampOrg);
        } else {
            billToSampOrg.setOrganizationId(shipToDO.getId());
            billToSampOrg.setOrganizationAttention(orderDO.getOrganizationAttention());
            billToSampOrg.setTypeId(DictionaryCache.getIdFromSystemName("org_bill_to"));
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
                itemDO.setContainer(DictionaryCache.getEntryFromId(containerDO.getContainerId()).getEntry());
                itemDO.setTypeOfSampleId(containerDO.getTypeOfSampleId());
                if(containerDO.getTypeOfSampleId() != null)
                    itemDO.setTypeOfSample(DictionaryCache.getEntryFromId(containerDO.getTypeOfSampleId()).getEntry());
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
    
    public ArrayList<OrderTestViewDO> getTestsFromOrder(Integer orderId) throws Exception {
        OrderTestManager testManager;
        
        if(orderMan == null )
            orderMan = OrderManager.fetchById(orderId);
        
        testManager = orderMan.getTests();
        
        return testManager.getTests();
    }
}
