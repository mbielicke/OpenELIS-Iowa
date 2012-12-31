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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AddressDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;

public class SamplePrivateWellImportOrder extends ImportOrder {

    public SamplePrivateWellImportOrder() throws Exception {
    }
    
    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        return super.importOrderInfo(orderId, manager, "sample_well_aux_data");
    }

    protected void loadFieldsFromAuxData(ArrayList<AuxDataViewDO> auxDataList, Integer envAuxGroupId,
                                         SampleManager manager, ValidationErrorsList errors) throws Exception {
        AuxDataViewDO data;
        ProjectDO proj;
        SampleProjectViewDO sampleProj;
        String analyteId;
        SampleDO sample;
        SamplePrivateWellManager wellMan;
        SamplePrivateWellViewDO well;
        AddressDO locAddr;
        DateField df;

        sample = manager.getSample();
        wellMan = (SamplePrivateWellManager)manager.getDomainManager();
        well = wellMan.getPrivateWell();
        locAddr = well.getLocationAddress();

        for (int i = 0; i < auxDataList.size(); i++ ) {
            data = auxDataList.get(i);
            try {
                if ( !data.getGroupId().equals(envAuxGroupId)) {
                    saveAuxData(data, errors, manager);
                    continue;
                }
                analyteId = data.getAnalyteExternalId();
                if ("smpl_collected_date".equals(analyteId)) {
                    df = new DateField();
                    df.setBegin(Datetime.YEAR);
                    df.setEnd(Datetime.DAY);
                    df.setStringValue(data.getValue());
                    sample.setCollectionDate(df.getValue());
                } else if ("smpl_collected_time".equals(analyteId)) {
                    df = new DateField();
                    df.setBegin(Datetime.HOUR);
                    df.setEnd(Datetime.MINUTE);
                    df.setStringValue(data.getValue());
                    sample.setCollectionTime(df.getValue());
                } else if ("smpl_client_ref".equals(analyteId)) {
                    sample.setClientReference(data.getValue());
                } else if ("location".equals(analyteId)) {
                    well.setLocation(data.getValue());
                } else if ("loc_mult_unit".equals(analyteId)) {
                    locAddr.setMultipleUnit(data.getValue());
                } else if ("loc_street_address".equals(analyteId)) {
                    locAddr.setStreetAddress(data.getValue());
                } else if ("loc_city".equals(analyteId)) {
                    locAddr.setCity(data.getValue());
                } else if ("loc_state".equals(analyteId)) {
                    if (getDictionaryByEntry(data.getValue(), "state") != null)
                        locAddr.setState(data.getValue());
                    else if (data.getValue() != null)
                        errors.add(new FormErrorException("orderImportError", "state",
                                                              data.getValue()));
                } else if ("loc_zip_code".equals(analyteId)) {
                    locAddr.setZipCode(data.getValue());
                } else if ("owner".equals(analyteId)) {
                    well.setOwner(data.getValue());
                } else if ("collector".equals(analyteId)) {
                    well.setCollector(data.getValue());
                } else if ("well_number".equals(analyteId)) {
                    well.setWellNumber(new Integer(data.getValue()));
                } else if ("project_name".equals(analyteId) && data.getValue() != null) {
                    proj = projectService.call("fetchSingleByName", data.getValue());
                    if (proj != null) {
                        sampleProj = new SampleProjectViewDO();
                        sampleProj.setIsPermanent("Y");
                        sampleProj.setProjectId(proj.getId());
                        sampleProj.setProjectName(proj.getName());
                        sampleProj.setProjectDescription(proj.getDescription());

                        manager.getProjects().addFirstPermanentProject(sampleProj);

                    } else {
                        errors.add(new FormErrorException("orderImportError", "project",
                                                              data.getValue()));
                    }
                }
            } catch (Exception e) {
                // problem with aux input, ignore
            }
        }
    }

    protected void loadOrganizations(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO order;
        OrganizationDO shipTo;
        SamplePrivateWellManager wellMan;
        SamplePrivateWellViewDO well;
        OrderOrganizationManager orderOrgMan;
        SampleOrganizationManager samOrgMan;
        OrderOrganizationViewDO ordOrg, ordReportTo;
        SampleOrganizationViewDO samBillTo;

        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);

        wellMan = (SamplePrivateWellManager)man.getDomainManager();
        well = wellMan.getPrivateWell();
        order = orderMan.getOrder();
        shipTo = order.getOrganization();
        
        orderOrgMan = orderMan.getOrganizations(); 
        samOrgMan = man.getOrganizations();
        ordReportTo = null;
        samBillTo = null;
        
        for (int i = 0; i < orderOrgMan.count(); i++) {
            ordOrg = orderOrgMan.getOrganizationAt(i);
            /*
             * create the report-to, bill-to and secondary report-to organizations
             * for the sample if the corresponding organizations are defined in
             * the order
             */
            if (Constants.dictionary().ORG_REPORT_TO.equals(ordOrg.getTypeId())) {
                ordReportTo = ordOrg;
                well.setOrganizationId(ordReportTo.getOrganizationId());
                well.setOrganization(createOrganization(ordOrg));
                well.setReportToAttention(ordOrg.getOrganizationAttention());
            } else if (Constants.dictionary().ORG_BILL_TO.equals(ordOrg.getTypeId())) {
                samBillTo = createSampleOrganization(ordOrg, Constants.dictionary().ORG_BILL_TO);
                samOrgMan.addOrganization(samBillTo);
            } else if (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(ordOrg.getTypeId())) {
                samOrgMan.addOrganization(createSampleOrganization(ordOrg, Constants.dictionary().ORG_SECOND_REPORT_TO));
            }
        }       
                
        /*
         * if report-to was not found then set the ship-to as the report-to 
         */
        order = orderMan.getOrder();
        if (ordReportTo == null) {
            well.setOrganizationId(shipTo.getId());
            well.setOrganization(shipTo);
            well.setReportToAttention(order.getOrganizationAttention());
        }
        
        /*
         * if bill-to was not found and if report-to was found then set it as the
         * bill-to otherwise set the ship-to as the bill-to 
         */
        if (samBillTo == null) {
            if (ordReportTo != null)
                samOrgMan.addOrganization(createSampleOrganization(ordReportTo, Constants.dictionary().ORG_BILL_TO));
            else
                samOrgMan.addOrganization(createSampleOrganization(order, Constants.dictionary().ORG_BILL_TO));
        }
    }
    
    private OrganizationDO createOrganization(OrderOrganizationViewDO org) {
        OrganizationDO data;
        AddressDO addr;
        
        data = new OrganizationDO();
        data.setId(org.getOrganizationId());
        data.setName(org.getOrganizationName());
        
        addr = data.getAddress();
        addr.setMultipleUnit(org.getOrganizationAddressMultipleUnit());
        addr.setStreetAddress(org.getOrganizationAddressStreetAddress());
        addr.setCity(org.getOrganizationAddressCity());
        addr.setState(org.getOrganizationAddressState());
        addr.setZipCode(org.getOrganizationAddressZipCode());
        addr.setFaxPhone(org.getOrganizationAddressFaxPhone());
        addr.setWorkPhone(org.getOrganizationAddressWorkPhone());
        
        return data;
    }
}