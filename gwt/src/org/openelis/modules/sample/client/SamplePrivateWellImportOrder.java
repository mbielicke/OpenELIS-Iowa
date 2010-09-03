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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.OrderManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SamplePrivateWellManager;

public class SamplePrivateWellImportOrder extends ImportOrder {
    protected static final String AUX_DATA_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    protected static final String PROJECT_SERVICE_URL  = "org.openelis.modules.project.server.ProjectService";
    protected ScreenService       auxDataService, projectService;

    public SamplePrivateWellImportOrder() {
        auxDataService = new ScreenService("OpenELISServlet?service=" + AUX_DATA_SERVICE_URL);
        projectService = new ScreenService("controller?service=" + PROJECT_SERVICE_URL);
    }

    public ValidationErrorsList importOrderInfo(Integer orderId, SampleManager manager) throws Exception {
        // grab order aux data
        AuxDataDO data;
        ArrayList<AuxDataViewDO> auxDataList;
        Integer auxGroupId;

        orderMan = null;
        data = new AuxDataDO();
        data.setReferenceId(orderId);
        data.setReferenceTableId(ReferenceTable.ORDER);
        auxDataList = auxDataService.callList("fetchByRefId", data);

        // grab aux group id from sys variable ish
        auxGroupId = ((IdVO)auxDataService.call("getAuxGroupIdFromSystemVariable",
                                                "sample_well_aux_data")).getId();

        // load order report to/bill to
        loadReportToBillTo(orderId, manager);

        // grab order tests including number of bottles
        loadSampleItems(orderId, manager);

        // inject the data into the manager
        return importData(auxDataList, auxGroupId, manager);
    }

    private ValidationErrorsList importData(ArrayList<AuxDataViewDO> auxDataList,
                                            Integer envAuxGroupId,
                                            SampleManager manager) throws Exception {
        AuxDataViewDO data;
        ProjectDO proj;
        SampleProjectViewDO sampleProj;
        String analyteId;
        SamplePrivateWellManager wellMan;
        boolean reportToLoaded, reportToError;
        ValidationErrorsList errorsList;

        errorsList = new ValidationErrorsList();
        wellMan = (SamplePrivateWellManager)manager.getDomainManager();
        reportToLoaded = wellMan.getPrivateWell().getOrganizationId() != null;
        reportToLoaded = false;
        reportToError = false;

        // aux data
        for (int i = 0; i < auxDataList.size(); i++ ) {
            data = auxDataList.get(i);

            try {
                if (data.getGroupId().equals(envAuxGroupId)) {
                    analyteId = data.getAnalyteExternalId();

                    if (analyteId.equals("smpl_collected_date")) {
                        manager.getSample().setCollectionDate(Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY));
                    } else if (analyteId.equals("smpl_collected_time")) {
                        DateField df = new DateField();
                        df.setBegin(Datetime.HOUR);
                        df.setEnd(Datetime.MINUTE);
                        df.setStringValue(data.getValue());
                        manager.getSample().setCollectionTime(df.getValue());
                    } else if (analyteId.equals("smpl_client_ref")) {
                        manager.getSample().setClientReference(data.getValue());
                    } else if (analyteId.equals("report_to")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else
                            wellMan.getPrivateWell().setReportToName(data.getValue());

                    } else if (analyteId.equals("report_to_attention")) {
                        wellMan.getPrivateWell().setReportToAttention(data.getValue());
                    } else if (analyteId.equals("report_to_mult_unit")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell()
                                   .getReportToAddress()
                                   .setMultipleUnit(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_street_add")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell()
                                   .getReportToAddress()
                                   .setStreetAddress(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_city")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell().getReportToAddress().setCity(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_state")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell().getReportToAddress().setState(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_zip_code")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell()
                                   .getReportToAddress()
                                   .setZipCode(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_phone_num")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell()
                                   .getReportToAddress()
                                   .setWorkPhone(data.getValue());
                        }

                    } else if (analyteId.equals("report_to_fax_num")) {
                        if (reportToLoaded) {
                            if (data.getValue() != null)
                                reportToError = true;
                        } else {
                            loadReportToAddress(wellMan);
                            wellMan.getPrivateWell()
                                   .getReportToAddress()
                                   .setFaxPhone(data.getValue());
                        }

                    } else if (analyteId.equals("location")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .setLocation(data.getValue());
                    } else if (analyteId.equals("loc_mult_unit")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .getLocationAddress()
                                                                              .setMultipleUnit(data.getValue());
                    } else if (analyteId.equals("loc_street_address")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .getLocationAddress()
                                                                              .setStreetAddress(data.getValue());
                    } else if (analyteId.equals("loc_city")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .getLocationAddress()
                                                                              .setCity(data.getValue());
                    } else if (analyteId.equals("loc_state")) {
                        if (validateDropdownValue(data.getValue(), "state"))
                            ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                                  .getLocationAddress()
                                                                                  .setState(data.getValue());
                        else if (data.getValue() != null)
                            errorsList.add(new FormErrorException("orderImportError", "state",
                                                                  data.getValue()));
                    } else if (analyteId.equals("loc_zip_code")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .getLocationAddress()
                                                                              .setZipCode(data.getValue());
                    } else if (analyteId.equals("owner")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .setOwner(data.getValue());
                    } else if (analyteId.equals("collector")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .setCollector(data.getValue());
                    } else if (analyteId.equals("well_number")) {
                        ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell()
                                                                              .setWellNumber(new Integer(data.getValue()));
                    } else if (analyteId.equals("project_name") && data.getValue() != null) {

                        proj = projectService.call("fetchSingleByName", data.getValue());

                        if (proj != null) {
                            sampleProj = new SampleProjectViewDO();
                            sampleProj.setIsPermanent("Y");
                            sampleProj.setProjectId(proj.getId());
                            sampleProj.setProjectName(proj.getName());
                            sampleProj.setProjectDescription(proj.getDescription());

                            manager.getProjects().addFirstPermanentProject(sampleProj);

                        } else
                            errorsList.add(new FormErrorException("orderImportError", "project",
                                                                  data.getValue()));
                    }

                } else {
                    manager.getAuxData().addAuxData(data);
                }
            } catch (Exception e) {
                // problem with aux input, ignore
            }
        }

        if (reportToError) {
            errorsList.add(new FormErrorException("importOrderReportToException"));
            return errorsList;
        }

        return null;
    }

    protected void loadReportToBillTo(Integer orderId, SampleManager man) throws Exception {
        OrderViewDO data;
        OrganizationDO org;
        SamplePrivateWellManager wellMan;
        SamplePrivateWellViewDO privateWell;
        SampleOrganizationViewDO billTo;

        if (orderMan == null)
            orderMan = OrderManager.fetchById(orderId);

        // report to
        wellMan = (SamplePrivateWellManager)man.getDomainManager();
        privateWell = wellMan.getPrivateWell();
        data = orderMan.getOrder();
        org = data.getReportTo();

        if (org != null) {
            privateWell.setOrganizationId(org.getId());
            privateWell.getOrganization().setName(org.getName());
        } else {
            privateWell.setOrganizationId(null);
        }

        // bill to
        billTo = new SampleOrganizationViewDO();
        org = orderMan.getOrder().getBillTo();

        if (org != null) {
            billTo.setOrganizationId(org.getId());
            billTo.setOrganizationAttention(data.getBillToAttention());
            billTo.setTypeId(DictionaryCache.getIdFromSystemName("org_bill_to"));
            billTo.setOrganizationName(org.getName());
            billTo.setOrganizationCity(org.getAddress().getCity());
            billTo.setOrganizationState(org.getAddress().getState());
            man.getOrganizations().addOrganization(billTo);
        }
    }

    private void loadReportToAddress(SamplePrivateWellManager man) {
        if (man.getPrivateWell().getReportToAddress().getId() == null) {
            // man.setReportToAddress(new AddressDO());
        }
    }

    private boolean validateDropdownValue(String entry, String dictSystemName) {
        ArrayList<DictionaryDO> entries;
        DictionaryDO data;
        boolean valid;

        entries = DictionaryCache.getListByCategorySystemName(dictSystemName);
        valid = false;

        for (int i = 0; i < entries.size(); i++ ) {
            data = entries.get(i);

            if (entry.equals(data.getEntry())) {
                valid = true;
                break;
            }
        }

        return valid;
    }
}
