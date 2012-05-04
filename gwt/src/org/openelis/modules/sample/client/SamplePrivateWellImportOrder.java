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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.OrderManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SamplePrivateWellManager;

public class SamplePrivateWellImportOrder extends ImportOrder {
    protected static final String PROJECT_SERVICE_URL  = "org.openelis.modules.project.server.ProjectService";
    
    protected ScreenService       projectService;

    public SamplePrivateWellImportOrder() {
        projectService = new ScreenService("controller?service=" + PROJECT_SERVICE_URL);
    }
    
    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        return super.importOrderInfo(orderId, manager, "sample_well_aux_data");
    }

    protected ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId, SampleManager manager) throws Exception {
        AuxDataViewDO data;
        ProjectDO proj;
        SampleProjectViewDO sampleProj;
        String analyteId;
        SampleDO sample;
        SamplePrivateWellManager wellMan;
        SamplePrivateWellViewDO well;
        AddressDO locAddr;
        ValidationErrorsList errorsList;
        DateField df;

        errorsList = new ValidationErrorsList();
        sample = manager.getSample();
        wellMan = (SamplePrivateWellManager)manager.getDomainManager();
        well = wellMan.getPrivateWell();
        locAddr = well.getLocationAddress();

        for (int i = 0; i < auxDataList.size(); i++ ) {
            data = auxDataList.get(i);
            try {
                if ( !data.getGroupId().equals(envAuxGroupId)) {
                    saveAuxData(data, errorsList, manager);
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
                    if (getDropdownByValue(data.getValue(), "state") != null)
                        locAddr.setState(data.getValue());
                    else if (data.getValue() != null)
                        errorsList.add(new FormErrorException("orderImportError", "state",
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
                        errorsList.add(new FormErrorException("orderImportError", "project",
                                                              data.getValue()));
                    }
                }
            } catch (Exception e) {
                // problem with aux input, ignore
            }
        }
        
        if (errorsList.size() > 0)
            return errorsList;

        return null;
    }

    protected void loadReportToBillTo(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO orderDO;
        OrganizationDO shipToDO, reportToDO, billToDO;
        SamplePrivateWellManager wellMan;
        SamplePrivateWellViewDO privateWell;
        SampleOrganizationViewDO billToSampOrg;

        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);

        wellMan = (SamplePrivateWellManager)man.getDomainManager();
        privateWell = wellMan.getPrivateWell();
        orderDO = orderMan.getOrder();
        shipToDO = orderDO.getOrganization();

        // report to
        /*if (reportToDO != null) {
            privateWell.setOrganizationId(reportToDO.getId());
            privateWell.setOrganization(reportToDO);
            privateWell.setReportToAttention(orderDO.getReportToAttention());
        } else {
            privateWell.setOrganizationId(shipToDO.getId());
            privateWell.setOrganization(shipToDO);
            privateWell.setReportToAttention(orderDO.getOrganizationAttention());
        }

        // bill to
        billToSampOrg = new SampleOrganizationViewDO();
        if (billToDO != null) {
            billToSampOrg.setOrganizationId(billToDO.getId());
            //billToSampOrg.setOrganizationAttention(orderDO.getBillToAttention());
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
        }*/
    }
}