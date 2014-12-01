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
import org.openelis.portal.modules.finalReport.client.StatusBarPopupScreenUI;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreen extends Screen {

    private DataViewUI             ui = GWT.create(DataViewUIImpl.class);

    private ModulePermission       userPermission;

    private DataViewVO             data;

    private StatusBarPopupScreenUI statusScreen;

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
        IdNameVO project;
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
            }
        });

        ui.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                data = new DataViewVO();
                ui.clearErrors();
                fireDataChange();
            }
        });

        ui.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String no;

                no = "N";
                ui.getDeck().showWidget(0);
                data = new DataViewVO();
                ui.getAccession().setValue(no, true);
                ui.getSampleCollected().setValue(no, true);
                ui.getSampleReceived().setValue(no, true);
                ui.getSampleReleased().setValue(no, true);
                ui.getSampleStatus().setValue(no, true);
                ui.getProjectId().setValue(no, true);
                ui.getClientReferenceHeader().setValue(no, true);
                ui.getCollectorHeader().setValue(no, true);
                ui.getCollectionSiteHeader().setValue(no, true);
                ui.getSampleDescription().setValue(no, true);
                ui.getCollectorPhone().setValue(no, true);
                ui.getSampleType().setValue(no, true);
                ui.getSource().setValue(no, true);
                ui.getSampleLocationCity().setValue(no, true);
                ui.getOrganizationName().setValue(no, true);
                ui.getOrganizationApt().setValue(no, true);
                ui.getOrganizationAddress().setValue(no, true);
                ui.getOrganizationCity().setValue(no, true);
                ui.getOrganizationState().setValue(no, true);
                ui.getOrganizationZip().setValue(no, true);
                ui.getAnalysisTest().setValue(no, true);
                ui.getAnalysisMethod().setValue(no, true);
                ui.getAnalysisRevision().setValue(no, true);
                ui.getAnalysisUnit().setValue(no, true);
                ui.getAnalysisStarted().setValue(no, true);
                ui.getAnalysisCompleted().setValue(no, true);
                ui.getAnalysisReleased().setValue(no, true);
                ui.getAnalysisQa().setValue(no, true);
                ui.getPatientLastName().setValue(no, true);
                ui.getPatientFirstName().setValue(no, true);
                ui.getPatientBirth().setValue(no, true);
                ui.getPatientGender().setValue(no, true);
                ui.getPatientRace().setValue(no, true);
                ui.getPatientEthnicity().setValue(no, true);
            }
        });

        ui.getSelectAllSampleFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getAccession().setValue(yes, true);
                ui.getSampleCollected().setValue(yes, true);
                ui.getSampleReceived().setValue(yes, true);
                ui.getSampleReleased().setValue(yes, true);
                ui.getSampleStatus().setValue(yes, true);
                ui.getProjectId().setValue(yes, true);
                ui.getClientReferenceHeader().setValue(yes, true);
                ui.getCollectorHeader().setValue(yes, true);
                ui.getCollectionSiteHeader().setValue(yes, true);
                ui.getSampleDescription().setValue(yes, true);
                ui.getCollectorPhone().setValue(yes, true);
                ui.getSampleType().setValue(yes, true);
                ui.getSource().setValue(yes, true);
                ui.getSampleLocationCity().setValue(yes, true);
            }
        });

        ui.getSelectAllOrgFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getOrganizationName().setValue(yes, true);
                ui.getOrganizationApt().setValue(yes, true);
                ui.getOrganizationAddress().setValue(yes, true);
                ui.getOrganizationCity().setValue(yes, true);
                ui.getOrganizationState().setValue(yes, true);
                ui.getOrganizationZip().setValue(yes, true);
            }
        });

        ui.getSelectAllAnalysisFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getAnalysisTest().setValue(yes, true);
                ui.getAnalysisMethod().setValue(yes, true);
                ui.getAnalysisRevision().setValue(yes, true);
                ui.getAnalysisUnit().setValue(yes, true);
                ui.getAnalysisStarted().setValue(yes, true);
                ui.getAnalysisCompleted().setValue(yes, true);
                ui.getAnalysisReleased().setValue(yes, true);
                ui.getAnalysisQa().setValue(yes, true);
            }
        });

        ui.getSelectAllPatientFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getPatientLastName().setValue(yes, true);
                ui.getPatientFirstName().setValue(yes, true);
                ui.getPatientBirth().setValue(yes, true);
                ui.getPatientGender().setValue(yes, true);
                ui.getPatientRace().setValue(yes, true);
                ui.getPatientEthnicity().setValue(yes, true);
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
                                 if (data.getCollectionDateFrom() != null)
                                     ui.getCollectedFrom()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getCollectionDateFrom()));
                                 else
                                     ui.getCollectedFrom().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setCollectedError(null);
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
                                 if (data.getCollectionDateTo() != null)
                                     ui.getCollectedTo()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getCollectionDateTo()));
                                 else
                                     ui.getCollectedTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setCollectedError(null);
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
                                 if (data.getReleasedDateFrom() != null)
                                     ui.getReleasedFrom()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getReleasedDateFrom()));
                                 else
                                     ui.getReleasedFrom().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
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
                                 if (data.getReleasedDateTo() != null)
                                     ui.getReleasedTo()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getReleasedDateTo()));
                                 else
                                     ui.getReleasedTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
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
                                 ui.setAccessionError(null);
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
                                 ui.setAccessionError(null);
                                 ui.getAccessionTo().clearExceptions();
                                 ui.getAccessionFrom().clearExceptions();
                                 data.setAccessionNumberTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollector() : ui.getAccessionFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getAccessionTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollector(),
                         SampleWebMeta.getEnvCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollector().setValue(data.getSampleEnvironmentalCollector());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setCollectorError(null);
                                 ui.getCollector().clearExceptions();
                                 data.setSampleEnvironmentalCollector(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getClientReference() : ui.getAccessionTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollector().getQuery();
                             }
                         });

        addScreenHandler(ui.getClientReference(),
                         SampleWebMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getClientReference().setValue(data.getClientReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setClientReferenceError(null);
                                 ui.getClientReference().clearExceptions();
                                 data.setClientReference(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollectionSite() : ui.getCollector();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getClientReference().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollectionSite(),
                         SampleWebMeta.getEnvLocation(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectionSite()
                                   .setValue(data.getSampleEnvironmentalLocation());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setCollectionSiteError(null);
                                 ui.getCollectionSite().clearExceptions();
                                 data.setSampleEnvironmentalLocation(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollectionTown() : ui.getClientReference();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectionSite().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollectionTown(),
                         SampleWebMeta.getLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectionTown()
                                   .setValue(data.getSampleEnvironmentalLocationAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setCollectionTownError(null);
                                 ui.getCollectionSite().clearExceptions();
                                 data.setSampleEnvironmentalLocationAddressCity(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getProjectCode() : ui.getCollectionSite();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectionTown().getQuery();
                             }
                         });

        addScreenHandler(ui.getProjectCode(),
                         SampleWebMeta.getProjectId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getProjectCode().setValue(data.getProjectId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.setProjectError(null);
                                 ui.getProjectCode().clearExceptions();
                                 data.setProjectId(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getContinueButton() : ui.getCollectionTown();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getProjectCode().getQuery();
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
                ui.getPatientLastName().setValue(data.getSampleClinicalPatientLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientLastName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientFirstName(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientFirstName().setValue(data.getSampleClinicalPatientFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientFirstName(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientBirth(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientBirth().setValue(data.getSampleClinicalPatientBirth());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientBirth(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientGender(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientGender().setValue(data.getSampleClinicalPatientGender());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientGender(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientRace(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientRace().setValue(data.getSampleClinicalPatientRace());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientRace(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientEthnicity(), "", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getPatientEthnicity().setValue(data.getSampleClinicalPatientEthnicity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleClinicalPatientEthnicity(event.getValue());
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
                return forward ? ui.getResetButton() : ui.getProjectCode();
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
            list = DataViewService.get().fetchProjectListForPortal();
            for (int i = 0; i < list.size(); i++ ) {
                project = list.get(i);
                row = new Item<Integer>(2);
                row.setKey(project.getId());
                row.setCell(0, project.getName());
                row.setCell(1, project.getDescription());
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
        finishEditing();
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
                    ui.getDeck().showWidget(1);
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
        boolean error;
        HashMap<String, QueryData> fieldMap;

        fieldMap = new HashMap<String, QueryData>();
        for (QueryData data : fields) {
            fieldMap.put(data.getKey(), data);
        }

        error = false;
        try {
            getRangeQuery(SampleWebMeta.getCollectionDateFrom(),
                          SampleWebMeta.getCollectionDateTo(),
                          SampleWebMeta.getCollectionDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setCollectedError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }

        try {
            getRangeQuery(SampleWebMeta.getReleasedDateFrom(),
                          SampleWebMeta.getReleasedDateTo(),
                          SampleWebMeta.getReleasedDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setReleasedError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }

        try {
            getRangeQuery(SampleWebMeta.getAccessionNumberFrom(),
                          SampleWebMeta.getAccessionNumberTo(),
                          SampleWebMeta.getAccessionNumber(),
                          fieldMap);
        } catch (Exception e) {
            ui.setAccessionError(Messages.get().finalReport_error_noStartAccession());
            error = true;
        }

        /*
         * if there was an error validating the fields, do not query for samples
         */
        if (error)
            throw new Exception();

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
        if (data.getTestAnalytes() == null || data.getTestAnalytes().size() < 1)
            return model;

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
        if (data.getAuxFields() == null || data.getAuxFields().size() < 1)
            return model;

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

        popup(data);
    }

    /**
     * creates a popup that shows the progress of creating data view report
     */
    private void popup(DataViewVO data) {
        final PopupPanel statusPanel;

        if (statusScreen == null)
            statusScreen = new StatusBarPopupScreenUI();
        statusScreen.setStatus(null);

        /*
         * initialize and show the popup screen
         */
        statusPanel = new PopupPanel();
        statusPanel.setSize("450px", "125px");
        statusScreen.setSize("450px", "125px");
        statusPanel.setWidget(statusScreen);
        statusPanel.setPopupPosition(this.getAbsoluteLeft() + 20, this.getAbsoluteTop() + 20);
        statusPanel.setModal(true);
        statusPanel.show();

        /*
         * Create final reports. Hide popup when database is updated
         * successfully or error is thrown.
         */
        DataViewService.get().runReportForPortal(data, new AsyncCallback<ReportStatus>() {

            @Override
            public void onSuccess(ReportStatus result) {
                if (result.getStatus() == ReportStatus.Status.SAVED) {
                    String url = "/portal/portal/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "DataView", null);
                }
                window.clearStatus();
                statusPanel.hide();
                statusScreen.setStatus(null);
            }

            @Override
            public void onFailure(Throwable caught) {
                window.clearStatus();
                statusPanel.hide();
                statusScreen.setStatus(null);
                if (caught instanceof NotFoundException)
                    window.setError(Messages.get().dataView_error_noResults());
                else
                    Window.alert(caught.getMessage());
            }

        });

        /*
         * refresh the status of creating the reports every second, until the
         * process successfully completes or is aborted because of an error
         */
        Timer timer = new Timer() {
            public void run() {
                ReportStatus status;
                try {
                    status = DataViewService.get().getStatus();
                    /*
                     * the status only needs to be refreshed while the status
                     * panel is showing because once the job is finished, the
                     * panel is closed
                     */
                    if (statusPanel.isShowing()) {
                        statusScreen.setStatus(status);
                        this.schedule(50);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    remote.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        };
        timer.schedule(50);
    }
}
