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
package org.openelis.portal.modules.dataView.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.TestAnalyteDataViewVO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreen extends Screen {

    private DataViewUI       ui = GWT.create(DataViewUIImpl.class);

    private ModulePermission userPermission;

    private DataViewVO       data;

    // private HashMap<Integer, String> selected;

    public DataViewScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_dataview_environmental");
        if (userPermission == null) {
            Window.alert(Messages.get().error_screenPerm("Data View Screen"));
            return;
        }

        try {
            CategoryCache.getBySystemNames("sample_status");
        } catch (Exception e) {
            remote.log(Level.SEVERE, e.getMessage(), e);
            Window.alert("error accessing cache");
            return;
        }
        initialize();
        setState(QUERY);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<IdNameVO> list;
        Item<Integer> row;

        ui.initialize();
        data = new DataViewVO();
        ui.getDeck().showWidget(0);

        ui.getContinueButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getSamples();
                ui.getDeck().showWidget(1);
            }
        });

        ui.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                data = new DataViewVO();
                clearErrors();
                fireDataChange();
            }
        });

        ui.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getDeck().showWidget(0);
            }
        });

        ui.getSelectAllSampleFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getAccession().setValue("Y");
                ui.getSampleCollected().setValue("Y");
                ui.getSampleReceived().setValue("Y");
                ui.getSampleReleased().setValue("Y");
                ui.getSampleStatus().setValue("Y");
                ui.getProjectId().setValue("Y");
                ui.getClientReferenceHeader().setValue("Y");
                ui.getCollectorHeader().setValue("Y");
                ui.getCollectionSiteHeader().setValue("Y");
                ui.getSampleDescription().setValue("Y");
                ui.getCollectorPhone().setValue("Y");
                ui.getSampleType().setValue("Y");
                ui.getSource().setValue("Y");
                ui.getSampleLocationCity().setValue("Y");
            }
        });

        ui.getSelectAllOrgFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getOrganizationName().setValue("Y");
                ui.getOrganizationApt().setValue("Y");
                ui.getOrganizationAddress().setValue("Y");
                ui.getOrganizationCity().setValue("Y");
                ui.getOrganizationState().setValue("Y");
                ui.getOrganizationZip().setValue("Y");
            }
        });

        ui.getSelectAllAnalysisFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getAnalysisTest().setValue("Y");
                ui.getAnalysisMethod().setValue("Y");
                ui.getAnalysisRevision().setValue("Y");
                ui.getAnalysisUnit().setValue("Y");
                ui.getAnalysisStarted().setValue("Y");
                ui.getAnalysisCompleted().setValue("Y");
                ui.getAnalysisReleased().setValue("Y");
                ui.getAnalysisQa().setValue("Y");
            }
        });

        ui.getSelectAllPatientFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getPatientLastName().setValue("Y");
                ui.getPatientFirstName().setValue("Y");
                ui.getPatientBirth().setValue("Y");
                ui.getPatientGender().setValue("Y");
                ui.getPatientRace().setValue("Y");
                ui.getPatientEthnicity().setValue("Y");
            }
        });

        ui.getSelectAllAnalytesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAnalyteTable().getModel()) {
                    row.setCell(0, "Y");
                    updateTestAnalyte((TestAnalyteDataViewVO)row.getData(), "Y");
                }
                /*
                 * needs to set the model to refresh the table
                 */
                ui.getAnalyteTable().setModel(ui.getAnalyteTable().getModel());
            }
        });

        ui.getUnselectAllAnalytesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAnalyteTable().getModel()) {
                    row.setCell(0, "N");
                    updateTestAnalyte((TestAnalyteDataViewVO)row.getData(), "N");
                }
                ui.getAnalyteTable().setModel(ui.getAnalyteTable().getModel());
            }
        });

        ui.getSelectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAuxTable().getModel()) {
                    row.setCell(0, "Y");
                    updateAuxData((AuxFieldDataViewVO)row.getData(), "Y");
                }
                ui.getAuxTable().setModel(ui.getAuxTable().getModel());
            }
        });

        ui.getUnselectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAuxTable().getModel()) {
                    row.setCell(0, "N");
                    updateAuxData((AuxFieldDataViewVO)row.getData(), "N");
                }
                ui.getAuxTable().setModel(ui.getAuxTable().getModel());
            }
        });

        ui.getRunReportButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                runReport();
            }
        });

        addScreenHandler(ui.getCollectedFrom(),
                         SampleWebMeta.getCollectionDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedFrom()
                                   .setValue(new Datetime(Datetime.YEAR,
                                                          Datetime.SECOND,
                                                          data.getCollectionDateFrom()));
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedFrom().clearExceptions();
                                 ui.getCollectedTo().clearExceptions();
                                 data.setCollectionDateFrom(event.getValue().getDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollectedTo() : ui.getResetButton();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectedFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollectedTo(),
                         SampleWebMeta.getCollectionDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedTo()
                                   .setValue(new Datetime(Datetime.YEAR,
                                                          Datetime.SECOND,
                                                          data.getCollectionDateTo()));
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedTo().clearExceptions();
                                 ui.getCollectedFrom().clearExceptions();
                                 data.setCollectionDateTo(event.getValue().getDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getReleasedFrom() : ui.getCollectedFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectedTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getReleasedFrom(),
                         SampleWebMeta.getReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedFrom()
                                   .setValue(new Datetime(Datetime.YEAR,
                                                          Datetime.SECOND,
                                                          data.getReleasedDateFrom()));
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getReleasedFrom().clearExceptions();
                                 ui.getReleasedTo().clearExceptions();
                                 data.setReleasedDateFrom(event.getValue().getDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getReleasedTo() : ui.getCollectedTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getReleasedFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getReleasedTo(),
                         SampleWebMeta.getReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedTo()
                                   .setValue(new Datetime(Datetime.YEAR,
                                                          Datetime.SECOND,
                                                          data.getReleasedDateTo()));
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getReleasedTo().clearExceptions();
                                 ui.getReleasedFrom().clearExceptions();
                                 data.setReleasedDateTo(event.getValue().getDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionFrom() : ui.getReleasedFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getReleasedTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccessionFrom(),
                         SampleWebMeta.getAccessionNumberFrom(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionFrom().setValue(data.getAccessionNumberFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionFrom().clearExceptions();
                                 ui.getAccessionTo().clearExceptions();
                                 data.setAccessionNumberFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionTo() : ui.getReleasedTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getAccessionFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccessionTo(),
                         SampleWebMeta.getAccessionNumberTo(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionTo().setValue(data.getAccessionNumberTo());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionTo().clearExceptions();
                                 ui.getAccessionFrom().clearExceptions();
                                 data.setAccessionNumberTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getClientReference() : ui.getAccessionFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getAccessionTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getClientReference(),
                         SampleWebMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getClientReference().setValue(data.getClientReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.getClientReference().clearExceptions();
                                 data.setClientReference(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getProjectCode() : ui.getAccessionTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getClientReference().getQuery();
                             }
                         });

        addScreenHandler(ui.getProjectCode(),
                         SampleWebMeta.getProjectId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getProjectCode().setValue(data.getProjectId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getProjectCode().clearExceptions();
                                 data.setProjectId(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollector() : ui.getClientReference();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getProjectCode().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollector(),
                         SampleWebMeta.getEnvCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollector().setValue(data.getSampleEnvironmentalCollector());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.getCollector().clearExceptions();
                                 data.setSampleEnvironmentalCollector(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollector() : ui.getProjectCode();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollector().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccession(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAccession().setValue(data.getAccessionNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAccessionNumber(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleCollected(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleCollected().setValue(data.getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleReceived(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleReceived().setValue(data.getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReceivedDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleReleased(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleReleased().setValue(data.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReleasedDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleStatus(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleStatus().setValue(data.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setStatusId(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getProjectId(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getProjectId().setValue(data.getProjectName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setProjectName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getClientReferenceHeader(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getClientReferenceHeader().setValue(data.getClientReferenceHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReferenceHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getCollectorHeader(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getCollectorHeader().setValue(data.getSampleEnvironmentalCollectorHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getCollectionSiteHeader(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getCollectionSiteHeader().setValue(data.getSampleEnvironmentalLocationHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleDescription(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleDescription().setValue(data.getSampleEnvironmentalDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalDescription(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getCollectorPhone(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getCollectorPhone().setValue(data.getSampleEnvironmentalCollectorPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorPhone(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleType(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleType().setValue(data.getSampleItemTypeofSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemTypeofSampleId(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSource(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSource().setValue(data.getSampleItemSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOfSampleId(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleLocationCity(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSampleLocationCity()
                  .setValue(data.getSampleEnvironmentalLocationAddressCityHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationAddressCityHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationName(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationName().setValue(data.getReportToOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReportToOrganizationName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationApt(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationApt().setValue(data.getOrganizationAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressMultipleUnit(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationAddress(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationAddress().setValue(data.getOrganizationAddressAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressAddress(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationCity(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationCity().setValue(data.getOrganizationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressCity(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationState(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationCity().setValue(data.getOrganizationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressCity(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationZip(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getOrganizationZip().setValue(data.getOrganizationAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressZipCode(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisTest(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisTest().setValue(data.getAnalysisTestNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestNameHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisMethod(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisMethod().setValue(data.getAnalysisTestMethodNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestMethodNameHeader(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisRevision(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisRevision().setValue(data.getAnalysisRevision());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisRevision(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisUnit(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisUnit().setValue(data.getAnalysisUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisUnitOfMeasureId(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisStarted(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisStarted().setValue(data.getAnalysisStartedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStartedDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisCompleted(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisCompleted().setValue(data.getAnalysisCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisReleased(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisReleased().setValue(data.getAnalysisReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedDate(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisQa(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getAnalysisQa().setValue(data.getAnalysisQaName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisQaName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientLastName(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientLastName().setValue(data.getPatientLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientLastName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientFirstName(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientFirstName().setValue(data.getPatientFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientFirstName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientBirth(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientBirth().setValue(data.getPatientBirth());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientBirth(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientGender(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientGender().setValue(data.getPatientGender());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientGender(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientRace(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientRace().setValue(data.getPatientRace());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientRace(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientEthnicity(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientEthnicity().setValue(data.getPatientEthnicity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setPatientEthnicity(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        ui.getAnalyteTable().addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() >= 1)
                    event.cancel();
            }
        });

        ui.getAnalyteTable().addCellEditedHandler(new CellEditedHandler() {

            @Override
            public void onCellUpdated(CellEditedEvent event) {
                updateTestAnalyte((TestAnalyteDataViewVO)ui.getAnalyteTable()
                                                           .getRowAt(event.getRow())
                                                           .getData(),
                                  (String)ui.getAnalyteTable().getValueAt(event.getRow(),
                                                                          event.getCol()));
            }
        });

        ui.getAuxTable().addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() >= 1)
                    event.cancel();
            }
        });

        ui.getAuxTable().addCellEditedHandler(new CellEditedHandler() {

            @Override
            public void onCellUpdated(CellEditedEvent event) {
                updateAuxData((AuxFieldDataViewVO)ui.getAuxTable()
                                                    .getRowAt(event.getRow())
                                                    .getData(),
                              (String)ui.getAuxTable().getValueAt(event.getRow(), event.getCol()));
            }
        });

        addScreenHandler(ui.getContinueButton(), "", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getResetButton() : ui.getCollector();
            }
        });

        addScreenHandler(ui.getResetButton(), "", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getCollectedFrom() : ui.getContinueButton();
            }
        });

        /*
         * Initializing the project code drop down
         */
        model = new ArrayList<Item<Integer>>();
        try {
            // TODO
            list = DataViewService.get().fetchEnvironmentalProjectListForWeb();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(list.get(i).getId(), list.get(i).getName());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            remote.log(Level.SEVERE, e.getMessage(), e);
        }
        ui.getProjectCode().setModel(model);

    }

    /**
     * fetch samples that match the search criteria
     */
    private void getSamples() {
        ArrayList<QueryData> fields;

        // ui.clearErrors();
        fields = new ArrayList<QueryData>();

        try {
            fields.addAll(createWhereFromParamFields(getQueryFields()));
        } catch (Exception e) {
            return;
        }

        if (fields.size() < 1) {
            Window.alert(Messages.get().finalReport_error_emptyQueryException());
            return;
        }

        data.setQueryFields(fields);
        window.setBusy(Messages.get().gen_fetchingSamples());

        DataViewService.get().fetchAnalyteAndAuxField(data, new AsyncCallback<DataViewVO>() {

            @Override
            public void onSuccess(DataViewVO result) {
                data.setAnalytes(result.getTestAnalytes());
                data.setAuxFields(result.getAuxFields());
                try {
                    ui.getAnalyteTable().setModel(getAnalyteTableModel());
                    ui.getAuxTable().setModel(getAuxTableModel());
                    ui.getAnalyteTable().onResize();
                    ui.getAuxTable().onResize();
                } catch (Exception e) {
                    window.clearStatus();
                    Window.alert(e.getMessage());
                    throw e;
                }
                window.clearStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof NotFoundException) {
                    window.setError(Messages.get().finalReport_error_noSamples());
                } else {
                    window.clearStatus();
                    Window.alert(caught.getMessage());
                }
            }
        });
    }

    /**
     * create the range queries for variables with from and to fields
     */
    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) throws Exception {
        HashMap<String, QueryData> fieldMap;

        fieldMap = new HashMap<String, QueryData>();
        for (QueryData data : fields) {
            fieldMap.put(data.getKey(), data);
        }

        try {
            getRangeQuery(SampleWebMeta.getCollectionDateFrom(),
                          SampleWebMeta.getCollectionDateTo(),
                          SampleWebMeta.getCollectionDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setCollectedError(Messages.get().finalReport_error_noStartDate());
            throw e;
        }

        try {
            getRangeQuery(SampleWebMeta.getReleasedDateFrom(),
                          SampleWebMeta.getReleasedDateTo(),
                          SampleWebMeta.getReleasedDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setReleasedError(Messages.get().finalReport_error_noStartDate());
            throw e;
        }

        try {
            getRangeQuery(SampleWebMeta.getAccessionNumberFrom(),
                          SampleWebMeta.getAccessionNumberTo(),
                          SampleWebMeta.getAccessionNumber(),
                          fieldMap);
        } catch (Exception e) {
            ui.setAccessionError(Messages.get().finalReport_error_noStartAccession());
            throw e;
        }

        return new ArrayList<QueryData>(fieldMap.values());
    }

    /**
     * create a range query string
     */
    private HashMap<String, QueryData> getRangeQuery(String fromKey, String toKey, String key,
                                                     HashMap<String, QueryData> fieldMap) throws Exception {
        QueryData from, to, range;

        from = fieldMap.get(fromKey);
        to = fieldMap.get(toKey);

        if (to == null && from == null) {
            return fieldMap;
        } else if (to != null && from == null) {
            throw new Exception();
        } else if (to == null && from != null) {
            range = fieldMap.get(fromKey);
            range.setKey(key);
            range.setQuery(from.getQuery() + ".." + from.getQuery());
            fieldMap.put(key, range);
            fieldMap.remove(fromKey);
            fieldMap.remove(toKey);
        } else {
            range = fieldMap.get(fromKey);
            range.setKey(key);
            range.setQuery(from.getQuery() + ".." + to.getQuery());
            fieldMap.put(key, range);
            fieldMap.remove(fromKey);
            fieldMap.remove(toKey);
        }

        return fieldMap;
    }

    private ArrayList<Row> getAnalyteTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        for (TestAnalyteDataViewVO ana : data.getTestAnalytes()) {
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, ana.getAnalyteName());
            row.setData(ana);
            model.add(row);
        }
        return model;
    }

    private ArrayList<Row> getAuxTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        for (AuxFieldDataViewVO aux : data.getAuxFields()) {
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, aux.getAnalyteName());
            row.setData(aux);
            model.add(row);
        }
        return model;
    }

    private void updateTestAnalyte(TestAnalyteDataViewVO data, String val) {
        ArrayList<ResultDataViewVO> list;

        data.setIsIncluded(val);
        list = data.getResults();
        for (ResultDataViewVO res : list)
            res.setIsIncluded(val);
    }

    private void updateAuxData(AuxFieldDataViewVO data, String val) {
        ArrayList<AuxDataDataViewVO> list;

        data.setIsIncluded(val);
        list = data.getValues();
        for (AuxDataDataViewVO value : list)
            value.setIsIncluded(val);
    }

    protected void runReport() {
        int numTA, numAux;
        ArrayList<TestAnalyteDataViewVO> taList;
        ArrayList<AuxFieldDataViewVO> afList;

        numTA = 0;
        numAux = 0;
        taList = data.getTestAnalytes();
        if (taList != null) {
            for (TestAnalyteDataViewVO ta : taList) {
                if ("Y".equals(ta.getIsIncluded()))
                    numTA++ ;
            }
        }

        if (numTA == 0) {
            afList = data.getAuxFields();
            if (afList != null) {
                for (AuxFieldDataViewVO af : afList) {
                    if ("Y".equals(af.getIsIncluded()))
                        numAux++ ;
                }
            }
            if (numAux == 0) {
                window.setError(Messages.get().dataView_selectOneAnaOrAux());
                return;
            }
        }

        window.setBusy(Messages.get().gen_genReportMessage());
        DataViewService.get().runReportForWebEnvironmental(data, new AsyncCallback<ReportStatus>() {
            @Override
            public void onSuccess(ReportStatus result) {
                if (result.getStatus() == ReportStatus.Status.SAVED) {
                    String url = "/portal/portal/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "FinalReport", null);
                }
                window.clearStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                window.clearStatus();
                Window.alert(caught.getMessage());
            }
        });
    }
}
