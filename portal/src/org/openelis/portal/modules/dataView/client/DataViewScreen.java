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

import static org.openelis.portal.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataView1VO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.TestAnalyteDataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.portal.modules.finalReport.client.StatusBarPopupScreenUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Queryable;
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
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreen extends Screen {

    private DataViewUI             ui = GWT.create(DataViewUIImpl.class);

    private ModulePermission       userPermission;

    private DataView1VO             data;

    private StatusBarPopupScreenUI statusScreen;
    
    public DataViewScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_dataview");
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
        data = new DataView1VO();
        ui.getDeck().showWidget(0);

        ui.getContinueButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getSamples();
                uncheckAllFields();
            }
        });

        ui.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                data = new DataView1VO();
                ui.clearErrors();
                fireDataChange();
            }
        });

        ui.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getDeck().showWidget(0);
                data = new DataView1VO();
                uncheckAllFields();
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
                ui.getSampleType().setValue(yes, true);
                ui.getSource().setValue(yes, true);
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
                ui.getPatientPhone().setValue(yes, true);
                ui.getProviderLastName().setValue(yes, true);
                ui.getProviderFirstName().setValue(yes, true);
            }
        });

        ui.getSelectAllSdwisFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getPwsIdHeader().setValue(yes, true);
                ui.getPwsName().setValue(yes, true);
                ui.getSdwisCollectorHeader().setValue(yes, true);
                ui.getSdwisLocation().setValue(yes, true);
                ui.getFacilityId().setValue(yes, true);
                ui.getSdwisSampleType().setValue(yes, true);
                ui.getSampleCategory().setValue(yes, true);
                ui.getSamplePointId().setValue(yes, true);
            }
        });

        ui.getSelectAllEnvironmentalFieldsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String yes;

                yes = "Y";
                ui.getEnvCollectorHeader().setValue(yes, true);
                ui.getEnvLocation().setValue(yes, true);
                ui.getEnvLocationCity().setValue(yes, true);
                ui.getCollectorPhone().setValue(yes, true);
                ui.getSampleDescription().setValue(yes, true);
            }
        });

        ui.getSelectAllAnalytesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAnalyteTable().getModel()) {
                    row.setCell(0, "Y");
                    updateTestAnalyte((TestAnalyteDataView1VO)row.getData(), "Y");
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
                    updateTestAnalyte((TestAnalyteDataView1VO)row.getData(), "N");
                }
                ui.getAnalyteTable().setModel(ui.getAnalyteTable().getModel());
            }
        });

        ui.getSelectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAuxTable().getModel()) {
                    row.setCell(0, "Y");
                    updateAuxData((AuxFieldDataView1VO)row.getData(), "Y");
                }
                ui.getAuxTable().setModel(ui.getAuxTable().getModel());
            }
        });

        ui.getUnselectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (Row row : ui.getAuxTable().getModel()) {
                    row.setCell(0, "N");
                    updateAuxData((AuxFieldDataView1VO)row.getData(), "N");
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
                                 /*if (data.getCollectionDateFrom() != null)
                                     ui.getCollectedFrom()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getCollectionDateFrom()));
                                 else*/
                                     ui.getCollectedFrom().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setCollectedError(null);
                                 ui.getCollectedFrom().clearExceptions();
                                 ui.getCollectedTo().clearExceptions();
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
                                 /*if (data.getCollectionDateTo() != null)
                                     ui.getCollectedTo()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getCollectionDateTo()));
                                 else*/
                                     ui.getCollectedTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setCollectedError(null);
                                 ui.getCollectedTo().clearExceptions();
                                 ui.getCollectedFrom().clearExceptions();
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
                         SampleWebMeta.getAnalysisReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*if (data.getReleasedDateFrom() != null)
                                     ui.getReleasedFrom()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getReleasedDateFrom()));
                                 else*/
                                     ui.getReleasedFrom().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
                                 ui.getReleasedFrom().clearExceptions();
                                 ui.getReleasedTo().clearExceptions();
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
                         SampleWebMeta.getAnalysisReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*if (data.getReleasedDateTo() != null)
                                     ui.getReleasedTo()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getReleasedDateTo()));
                                 else*/
                                     ui.getReleasedTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
                                 ui.getReleasedTo().clearExceptions();
                                 ui.getReleasedFrom().clearExceptions();
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
                                 //ui.getAccessionFrom().setValue(data.getAccessionNumberFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.setAccessionError(null);
                                 ui.getAccessionFrom().clearExceptions();
                                 ui.getAccessionTo().clearExceptions();
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
                                 //ui.getAccessionTo().setValue(data.getAccessionNumberTo());
                                 ui.getAccessionTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.setAccessionError(null);
                                 ui.getAccessionTo().clearExceptions();
                                 ui.getAccessionFrom().clearExceptions();
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
                                 //ui.getClientReference().setValue(data.getClientReference());
                                 ui.getClientReference().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setClientReferenceError(null);
                                 ui.getClientReference().clearExceptions();
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
                         new ScreenHandler<ArrayList<Integer>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*ArrayList<Integer> ids;

                                 ids = null;
                                 if (data.getProjectId() != null) {
                                     ids = new ArrayList<Integer>();
                                     ids.add(data.getProjectId());
                                 }
                                 ui.getProjectCode().setValue(ids);*/
                                 ui.getProjectCode().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<ArrayList<Integer>> event) {
                                 ui.setProjectError(null);
                                 ui.getProjectCode().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getEnvCollector() : ui.getClientReference();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getProjectCode().getQuery();
                             }
                         });

        addScreenHandler(ui.getEnvCollector(),
                         SampleWebMeta.getEnvCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*ui.getEnvCollector()
                                   .setValue(data.getSampleEnvironmentalCollector());*/
                                 ui.getEnvCollector().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setEnvCollectorError(null);
                                 ui.getEnvCollector().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getSdwisCollector() : ui.getProjectCode();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getEnvCollector().getQuery();
                             }
                         });

        addScreenHandler(ui.getSdwisCollector(), SampleWebMeta.getSDWISCollector(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSdwisCollector().setValue(data.getSampleSDWISCollector());
                ui.getSdwisCollector().setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                ui.setSdwisCollectorError(null);
                ui.getSdwisCollector().clearExceptions();
            }

            public Widget onTab(boolean forward) {
                return forward ? ui.getPwsId() : ui.getEnvCollector();
            }

            @Override
            public Object getQuery() {
                QueryData q;

                if (ui.getSdwisCollector().getQuery() == null)
                    return null;
                q = (QueryData)ui.getSdwisCollector().getQuery();
                q.setKey(SampleWebMeta.getSDWISCollector());
                return q;
            }
        });

        addScreenHandler(ui.getPwsId(), SampleWebMeta.getPwsNumber0(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPwsId().setValue(data.getSampleSDWISPwsId());
                ui.getPwsId().setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                ui.setPwsError(null);
                ui.getPwsId().clearExceptions();
            }

            public Widget onTab(boolean forward) {
                return forward ? ui.getPatientLast() : ui.getSdwisCollector();
            }

            @Override
            public Object getQuery() {
                return ui.getPwsId().getQuery();
            }
        });

        addScreenHandler(ui.getPatientLast(),
                         SampleWebMeta.getClinicalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getPatientLast()
                                   //.setValue(data.getSampleClinicalPatientLastName());
                                 ui.getPatientLast().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setPatientLastError(null);
                                 ui.getPatientLast().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientFirst() : ui.getPwsId();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientLast().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientFirst(),
                         SampleWebMeta.getClinicalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getPatientFirst()
                                   //.setValue(data.getSampleClinicalPatientFirstName());
                                 ui.getPatientFirst().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setPatientFirstError(null);
                                 ui.getPatientFirst().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientBirthFrom() : ui.getPatientLast();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientFirst().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientBirthFrom(),
                         SampleWebMeta.getClinicalPatientBirthDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*if (data.getSampleClinicalPatientBirthDateFrom() != null)
                                     ui.getPatientBirthFrom()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getSampleClinicalPatientBirthDateFrom()));
                                 else*/
                                     ui.getPatientBirthFrom().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setPatientBirthError(null);
                                 ui.getPatientBirthFrom().clearExceptions();
                                 ui.getPatientBirthTo().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientBirthTo() : ui.getPatientFirst();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientBirthFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientBirthTo(),
                         SampleWebMeta.getClinicalPatientBirthDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 /*if (data.getSampleClinicalPatientBirthDateTo() != null)
                                     ui.getPatientBirthTo()
                                       .setValue(new Datetime(Datetime.YEAR,
                                                              Datetime.SECOND,
                                                              data.getSampleClinicalPatientBirthDateTo()));
                                 else*/
                                     ui.getPatientBirthTo().setValue(null);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setPatientBirthError(null);
                                 ui.getPatientBirthTo().clearExceptions();
                                 ui.getPatientBirthFrom().clearExceptions();
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getContinueButton() : ui.getPatientBirthFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientBirthTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccession(), SampleWebMeta.getAccessionNumber(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getAccession().setValue(data.getAccessionNumber());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleCollected(),
                         SampleWebMeta.getCollectionDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSampleCollected().setValue(data.getCollectionDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleReceived(),
                         SampleWebMeta.getReceivedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSampleReceived().setValue(data.getReceivedDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleReleased(),
                         SampleWebMeta.getReleasedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSampleReleased().setValue(data.getReleasedDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleStatus(), SampleWebMeta.getStatusId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSampleStatus().setValue(data.getStatusId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getProjectId(), SampleWebMeta.getProjectName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getProjectId().setValue(data.getProjectName());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getClientReferenceHeader(),
                         SampleWebMeta.getClientReferenceHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getClientReferenceHeader()
                                   //.setValue(data.getClientReferenceHeader());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleType(), SampleWebMeta.getItemTypeofSampleId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSampleType().setValue(data.getSampleItemTypeofSampleId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSource(), SampleWebMeta.getItemSourceOfSampleId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSource().setValue(data.getSampleItemSourceOfSampleId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationName(),
                         SampleWebMeta.getSampleOrgOrganizationName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationName().setValue(data.getOrganizationName());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationApt(),
                         SampleWebMeta.getAddressMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationApt()
                                   //.setValue(data.getOrganizationAddressMultipleUnit());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationAddress(),
                         SampleWebMeta.getAddressStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationAddress()
                                   //.setValue(data.getOrganizationAddressAddress());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationCity(),
                         SampleWebMeta.getAddressCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationCity()
                                   //.setValue(data.getOrganizationAddressCity());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationState(),
                         SampleWebMeta.getAddressState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationState()
                                   //.setValue(data.getOrganizationAddressState());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationZip(),
                         SampleWebMeta.getAddressZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getOrganizationZip()
                                   //.setValue(data.getOrganizationAddressZipCode());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisTest(), SampleWebMeta.getAnalysisTestNameHeader(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getAnalysisTest().setValue(data.getAnalysisTestNameHeader());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisMethod(),
                         SampleWebMeta.getAnalysisMethodNameHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getAnalysisMethod()
                                   //.setValue(data.getAnalysisTestMethodNameHeader());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisRevision(),
                         SampleWebMeta.getAnalysisRevision(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getAnalysisRevision().setValue(data.getAnalysisRevision());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisUnit(), SampleWebMeta.getAnalysisUnitOfMeasureId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getAnalysisUnit().setValue(data.getAnalysisUnitOfMeasureId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisStarted(),
                         SampleWebMeta.getAnalysisStartedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getAnalysisStarted().setValue(data.getAnalysisStartedDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisCompleted(),
                         SampleWebMeta.getAnalysisCompletedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getAnalysisCompleted()
                                   //.setValue(data.getAnalysisCompletedDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisReleased(),
                         SampleWebMeta.getAnalysisReleasedDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getAnalysisReleased().setValue(data.getAnalysisReleasedDate());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisQa(), SampleWebMeta.getAnalysisSubQaName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getAnalysisQa().setValue(data.getAnalysisQaName());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientLastName(),
                         SampleWebMeta.getClinicalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getPatientLastName()
                                   //.setValue(data.getSampleClinicalPatientLastName());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientFirstName(),
                         SampleWebMeta.getClinicalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getPatientFirstName()
                                   //.setValue(data.getSampleClinicalPatientFirstName());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientBirth(), SampleWebMeta.getClinicalPatientBirthDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPatientBirth().setValue(data.getSampleClinicalPatientBirth());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientGender(), SampleWebMeta.getClinicalPatientGenderId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPatientGender().setValue(data.getSampleClinicalPatientGender());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientRace(), SampleWebMeta.getClinicalPatientRaceId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPatientRace().setValue(data.getSampleClinicalPatientRace());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientEthnicity(),
                         SampleWebMeta.getClinicalPatientEthnicityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getPatientEthnicity()
                                   //.setValue(data.getSampleClinicalPatientEthnicity());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientPhone(), SampleWebMeta.getClinicalPatientAddrHomePhone(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPatientPhone().setValue(data.getSampleClinicalPatientPhoneNumber());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getProviderLastName(),
                         SampleWebMeta.getClinicalProviderLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getProviderLastName()
                                   //.setValue(data.getSampleClinicalProviderLastName());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getProviderFirstName(),
                         SampleWebMeta.getClinicalProviderFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getProviderFirstName()
                                   //.setValue(data.getSampleClinicalProviderFirstName());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPwsIdHeader(), SampleWebMeta.getSDWISPwsId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPwsIdHeader().setValue(data.getSampleSDWISPwsId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPwsName(), SampleWebMeta.getPwsName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getPwsName().setValue(data.getSampleSDWISPwsName());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSdwisCollectorHeader(),
                         SampleWebMeta.getSDWISCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSdwisCollectorHeader()
                                   //.setValue(data.getSampleSDWISCollector());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSdwisLocation(), SampleWebMeta.getSDWISLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSdwisLocation().setValue(data.getSampleSDWISLocation());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getFacilityId(), SampleWebMeta.getSDWISFacilityId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getFacilityId().setValue(data.getSampleSDWISFacilityId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSdwisSampleType(),
                         SampleWebMeta.getSDWISSampleTypeId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSdwisSampleType()
                                  //.setValue(data.getSampleSDWISSampleTypeId());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleCategory(),
                         SampleWebMeta.getSDWISSampleCategoryId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSampleCategory()
                                   //.setValue(data.getSampleSDWISSampleCategoryId());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSamplePointId(), SampleWebMeta.getSDWISSamplePointId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getSamplePointId().setValue(data.getSampleSDWISSamplePointId());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getEnvCollectorHeader(),
                         SampleWebMeta.getEnvCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getEnvCollectorHeader()
                                   //.setValue(data.getSampleEnvironmentalCollectorHeader());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getEnvLocation(), SampleWebMeta.getEnvLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //ui.getEnvLocation().setValue(data.getSampleEnvironmentalLocationHeader());
            }

            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getEnvLocationCity(),
                         SampleWebMeta.getLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getEnvLocationCity()
                                   //.setValue(data.getSampleEnvironmentalLocationAddressCityHeader());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getCollectorPhone(),
                         SampleWebMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getCollectorPhone()
                                   //.setValue(data.getSampleEnvironmentalCollectorPhone());
                             }

                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleDescription(),
                         SampleWebMeta.getEnvDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 //ui.getSampleDescription()
                                   //.setValue(data.getSampleEnvironmentalDescription());
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
                updateTestAnalyte((TestAnalyteDataView1VO)ui.getAnalyteTable()
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
                updateAuxData((AuxFieldDataView1VO)ui.getAuxTable()
                                                    .getRowAt(event.getRow())
                                                    .getData(),
                              (String)ui.getAuxTable().getValueAt(event.getRow(), event.getCol()));
            }
        });

        addScreenHandler(ui.getContinueButton(), "continueButton", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getResetButton() : ui.getPatientBirthTo();
            }
        });

        addScreenHandler(ui.getResetButton(), "resetButton", new ScreenHandler<Integer>() {
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
    
    public Validation validate() {
        boolean error;
        Validation validation;
        
        validation = super.validate();
        
        /*
         * both widgets in a pair should either be empty or they both should
         * have values
         */
        error = false;
        if (!isFromToValid(ui.getCollectedFrom(), ui.getCollectedTo())) {
            ui.setCollectedError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }
        
        if (!isFromToValid(ui.getReleasedFrom(), ui.getReleasedTo())) {
            ui.setReleasedError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }
        
        if (!isFromToValid(ui.getAccessionFrom(), ui.getAccessionTo())) {
            ui.setAccessionError(Messages.get().finalReport_error_noStartAccession());
            error = true;
        }
        
        if (!isFromToValid(ui.getPatientBirthFrom(), ui.getPatientBirthTo())) {
            ui.setPatientBirthError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }
        
        if (error)
            validation.setStatus(Validation.Status.ERRORS);
        
        return validation;
    }
    
    /**
     * Returns a list of QueryData for the widgets in the tab; only one
     * QueryData is created for a pair of "from" and "to" widgets
     */
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();

        addQueryData(ui.getCollectedFrom(),
                     ui.getCollectedTo(),
                     SampleWebMeta.getCollectionDate(),
                     QueryData.Type.DATE,
                     fields);
        
        addQueryData(ui.getReleasedFrom(),
                     ui.getReleasedFrom(),
                     SampleWebMeta.getAnalysisReleasedDate(),
                     QueryData.Type.DATE,
                     fields);
        
        addQueryData(ui.getAccessionFrom(),
                     ui.getAccessionTo(),
                     SampleWebMeta.getAccessionNumber(),
                     QueryData.Type.INTEGER,
                     fields);

        addQueryData(ui.getClientReference(), SampleWebMeta.getClientReference(), fields);
        addQueryData(ui.getProjectCode(), SampleWebMeta.getProjectId(), fields);
        addQueryData(ui.getEnvCollector(), SampleWebMeta.getEnvCollector(), fields);
        addQueryData(ui.getSdwisCollector(), SampleWebMeta.getSDWISCollector(), fields);
        addQueryData(ui.getPwsId(), SampleWebMeta.getPwsNumber0(), fields);
        addQueryData(ui.getPatientLast(), SampleWebMeta.getClinicalPatientLastName(), fields);
        addQueryData(ui.getPatientFirst(), SampleWebMeta.getClinicalPatientFirstName(), fields);

        addQueryData(ui.getPatientBirthFrom(),
                     ui.getPatientBirthTo(),
                     SampleWebMeta.getClinicalPatientBirthDate(),
                     QueryData.Type.DATE,
                     fields);
        
        return fields;
    }
    
    /**
     * Returns true if both of the passed widgets is empty or if both have
     * values; returns false otherwise
     */
    private boolean isFromToValid(HasValue fromWidget, HasValue toWidget) {
        return (fromWidget.getValue() == null && toWidget.getValue() == null) ||
               (fromWidget.getValue() != null && toWidget.getValue() != null);
    }

    /**
     * fetch samples that match the search criteria
     */
    private void getSamples() {
        int numDomains;
        String domain;
        Validation validation;
        ArrayList<QueryData> fields;

        ui.clearErrors();
        finishEditing();
        numDomains = 0;
        domain = null;

        /*
         * determine the domain that is being queried.
         */
        if ( !DataBaseUtil.isEmpty(ui.getPwsId().getText()) ||
            !DataBaseUtil.isEmpty(ui.getSdwisCollector().getText())) {
            domain = Constants.domain().SDWIS;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getEnvCollector().getText())) {
            domain = Constants.domain().ENVIRONMENTAL;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getPatientFirst().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientLast().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthFrom().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthTo().getText())) {
            domain = Constants.domain().CLINICAL;
            numDomains++ ;
        }

        if (numDomains > 1) {
            Window.alert(Messages.get().finalReport_error_queryDomainException());
            return;
        }

        validation = validate();
        if (validation.getStatus() == Validation.Status.ERRORS)
            return;        
        
        fields = getQueryFields();

        if (domain != null)
            fields.add(new QueryData(SampleWebMeta.getDomain(), QueryData.Type.STRING, domain));

        if (fields.size() < 1) {
            Window.alert(Messages.get().finalReport_error_emptyQueryException());
            return;
        }

        data.setQueryFields(fields);
        window.setBusy(Messages.get().gen_fetchingSamples());

        DataViewService.get().fetchAnalyteAndAuxField(data, new AsyncCallback<DataView1VO>() {

            @Override
            public void onSuccess(DataView1VO result) {
                data.setTestAnalytes(result.getTestAnalytes());
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
     * Creates a query data where the query string contains the values of the
     * passed widgets separated by a delimiter; the key and type are set to the
     * passed values; doesn't create the query data if the value of either
     * widget is null or empty
     */
    private void addQueryData(Queryable fromWidget, Queryable toWidget, String key,
                              QueryData.Type type, ArrayList<QueryData> fields) {

        QueryData fromField, toField, field;
        Object fromQuery, toQuery;

        fromField = (QueryData)fromWidget.getQuery();
        toField = (QueryData)toWidget.getQuery();
        fromQuery = fromField != null ? fromField.getQuery() : null;
        toQuery = toField != null ? toField.getQuery() : null;

        if ( !DataBaseUtil.isEmpty(fromQuery) && !DataBaseUtil.isEmpty(toQuery)) {
            field = new QueryData();
            field.setKey(key);
            field.setQuery(DataBaseUtil.concatWithSeparator(fromQuery, "..", toQuery));
            field.setType(type);
            fields.add(field);
        }
    }

    /**
     * Adds the query data for the passed widget to the passed list; sets the
     * passed key in the query data
     */
    private void addQueryData(Queryable widget, String key, ArrayList<QueryData> fields) {
        QueryData field;

        field = getQueryData(widget, key);
        if (field != null)
            fields.add(field);
    }

    /**
     * Returns a query data created from the passed widget; a query data is
     * created only if there's a query string specified in the widget; sets the
     * passed key in the query data
     */
    private QueryData getQueryData(Queryable widget, String key) {
        QueryData field;

        field = (QueryData)widget.getQuery();
        /*
         * the key is set here because it's not set when the query data is
         * created; this is because, the widget doesn't know what its key is;
         * the key is in the hashmap for screen handlers
         */
        if (field != null)
            field.setKey(key);

        return field;
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
            getRangeQuery(SampleWebMeta.getAnalysisReleasedDateFrom(),
                          SampleWebMeta.getAnalysisReleasedDateTo(),
                          SampleWebMeta.getAnalysisReleasedDate(),
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

        try {
            getRangeQuery(SampleWebMeta.getClinPatientBirthDateFrom(),
                          SampleWebMeta.getClinPatientBirthDateTo(),
                          SampleWebMeta.getClinPatientBirthDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setPatientBirthError(Messages.get().finalReport_error_noStartDate());
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

    /**
     * create the model for the analyte table from the data view object
     */
    private ArrayList<Row> getAnalyteTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (data.getTestAnalytes() == null || data.getTestAnalytes().size() < 1)
            return model;

        for (TestAnalyteDataView1VO ana : data.getTestAnalytes()) {
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, ana.getAnalyteName());
            row.setData(ana);
            model.add(row);
        }
        return model;
    }

    /**
     * create the model for the auxiliary data table from the data view object
     */
    private ArrayList<Row> getAuxTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (data.getAuxFields() == null || data.getAuxFields().size() < 1)
            return model;

        for (AuxFieldDataView1VO aux : data.getAuxFields()) {
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, aux.getAnalyteName());
            row.setData(aux);
            model.add(row);
        }
        return model;
    }

    private void updateTestAnalyte(TestAnalyteDataView1VO data, String val) {
        ArrayList<ResultDataViewVO> list;

        data.setIsIncluded(val);
        list = data.getResults();
        for (ResultDataViewVO res : list)
            res.setIsIncluded(val);
    }

    private void updateAuxData(AuxFieldDataView1VO data, String val) {
        ArrayList<AuxDataDataViewVO> list;

        data.setIsIncluded(val);
        list = data.getValues();
        for (AuxDataDataViewVO value : list)
            value.setIsIncluded(val);
    }

    /**
     * create a spreadsheet with the checked data fields
     */
    protected void runReport() {
        int numTA, numAux;
        ArrayList<TestAnalyteDataView1VO> taList;
        ArrayList<AuxFieldDataView1VO> afList;

        numTA = 0;
        numAux = 0;
        taList = data.getTestAnalytes();
        if (taList != null) {
            for (TestAnalyteDataView1VO ta : taList) {
                if ("Y".equals(ta.getIsIncluded()))
                    numTA++ ;
            }
        }

        if (numTA == 0) {
            afList = data.getAuxFields();
            if (afList != null) {
                for (AuxFieldDataView1VO af : afList) {
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
    private void popup(DataView1VO data) {
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
        statusPanel.setPopupPosition(this.getAbsoluteLeft() + 50, this.getAbsoluteTop() + 50);
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
                    String url = "/openelisweb/openelisweb/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "DataView", "resizable=yes");
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

    private void uncheckAllFields() {
        String no;

        no = "N";
        ui.getAccession().setValue(no, true);
        ui.getSampleCollected().setValue(no, true);
        ui.getSampleReceived().setValue(no, true);
        ui.getSampleReleased().setValue(no, true);
        ui.getSampleStatus().setValue(no, true);
        ui.getProjectId().setValue(no, true);
        ui.getClientReferenceHeader().setValue(no, true);
        ui.getSampleType().setValue(no, true);
        ui.getSource().setValue(no, true);
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
        ui.getPatientPhone().setValue(no, true);
        ui.getProviderLastName().setValue(no, true);
        ui.getProviderFirstName().setValue(no, true);
        ui.getPwsIdHeader().setValue(no, true);
        ui.getPwsName().setValue(no, true);
        ui.getSdwisCollectorHeader().setValue(no, true);
        ui.getSdwisLocation().setValue(no, true);
        ui.getFacilityId().setValue(no, true);
        ui.getSdwisSampleType().setValue(no, true);
        ui.getSampleCategory().setValue(no, true);
        ui.getSamplePointId().setValue(no, true);
        ui.getEnvCollectorHeader().setValue(no, true);
        ui.getEnvLocation().setValue(no, true);
        ui.getEnvLocationCity().setValue(no, true);
        ui.getCollectorPhone().setValue(no, true);
        ui.getSampleDescription().setValue(no, true);
    }
}
