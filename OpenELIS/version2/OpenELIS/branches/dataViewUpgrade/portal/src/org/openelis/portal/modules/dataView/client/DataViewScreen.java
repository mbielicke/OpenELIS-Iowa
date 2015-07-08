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
import java.util.Map;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DataViewAnalyteVO;
import org.openelis.domain.DataViewValueVO;
import org.openelis.domain.IdNameVO;
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
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
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
            Window.alert(Messages.get().error_screenPerm("Spreadsheet View Screen"));
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
                updateAllTestAnalyte("Y");
            }
        });

        ui.getUnselectAllAnalytesButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAllTestAnalyte("N");
            }
        });

        ui.getSelectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAllAuxField("Y");
            }
        });

        ui.getUnselectAllAuxButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                updateAllAuxField("N");
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
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSampleCollected(),
                         SampleWebMeta.getCollectionDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleReceived(),
                         SampleWebMeta.getReceivedDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleReleased(),
                         SampleWebMeta.getReleasedDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleStatus(), SampleWebMeta.getStatusId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getProjectId(), SampleWebMeta.getProjectName(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getClientReferenceHeader(),
                         SampleWebMeta.getClientReferenceHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleType(), SampleWebMeta.getItemTypeofSampleId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSource(), SampleWebMeta.getItemSourceOfSampleId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getOrganizationName(),
                         SampleWebMeta.getSampleOrgOrganizationName(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationApt(),
                         SampleWebMeta.getAddressMultipleUnit(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationAddress(),
                         SampleWebMeta.getAddressStreetAddress(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationCity(),
                         SampleWebMeta.getAddressCity(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationState(),
                         SampleWebMeta.getAddressState(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getOrganizationZip(),
                         SampleWebMeta.getAddressZipCode(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisTest(), SampleWebMeta.getAnalysisTestNameHeader(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisMethod(),
                         SampleWebMeta.getAnalysisMethodNameHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisRevision(),
                         SampleWebMeta.getAnalysisRevision(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisUnit(), SampleWebMeta.getAnalysisUnitOfMeasureId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getAnalysisStarted(),
                         SampleWebMeta.getAnalysisStartedDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisCompleted(),
                         SampleWebMeta.getAnalysisCompletedDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisReleased(),
                         SampleWebMeta.getAnalysisReleasedDate(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getAnalysisQa(), SampleWebMeta.getAnalysisSubQaName(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientLastName(),
                         SampleWebMeta.getClinicalPatientLastNameHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientFirstName(),
                         SampleWebMeta.getClinicalPatientFirstNameHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientBirth(), SampleWebMeta.getClinicalPatientBirthDate(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientGender(), SampleWebMeta.getClinicalPatientGenderId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientRace(), SampleWebMeta.getClinicalPatientRaceId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPatientEthnicity(),
                         SampleWebMeta.getClinicalPatientEthnicityId(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPatientPhone(), SampleWebMeta.getClinicalPatientAddrHomePhone(), new ScreenHandler<String>() {            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getProviderLastName(),
                         SampleWebMeta.getClinicalProviderLastName(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getProviderFirstName(),
                         SampleWebMeta.getClinicalProviderFirstName(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getPwsIdHeader(), SampleWebMeta.getSDWISPwsId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getPwsName(), SampleWebMeta.getPwsName(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSdwisCollectorHeader(),
                         SampleWebMeta.getSDWISCollectorHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSdwisLocation(), SampleWebMeta.getSDWISLocation(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getFacilityId(), SampleWebMeta.getSDWISFacilityId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getSdwisSampleType(),
                         SampleWebMeta.getSDWISSampleTypeId(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleCategory(),
                         SampleWebMeta.getSDWISSampleCategoryId(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSamplePointId(), SampleWebMeta.getSDWISSamplePointId(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getEnvCollectorHeader(),
                         SampleWebMeta.getEnvCollectorHeader(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getEnvLocation(), SampleWebMeta.getEnvLocation(), new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return ui.getBackButton();
            }
        });

        addScreenHandler(ui.getEnvLocationCity(),
                         SampleWebMeta.getLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getCollectorPhone(),
                         SampleWebMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public Widget onTab(boolean forward) {
                                 return ui.getBackButton();
                             }
                         });

        addScreenHandler(ui.getSampleDescription(),
                         SampleWebMeta.getEnvDescription(),
                         new ScreenHandler<String>() {
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
                updateTestAnalyte((DataViewAnalyteVO)ui.getAnalyteTable()
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
                updateAuxData((DataViewAnalyteVO)ui.getAuxTable()
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
     * Returns true if both of the passed widgets are empty or if both have a
     * value; returns false otherwise
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
                ArrayList<Row> model;
                
                data.setTestAnalytes(result.getTestAnalytes());
                data.setAuxFields(result.getAuxFields());
                try {
                    model = getAnalyteTableModel();
                    /*
                     * load the result and aux data analytes in the tables and
                     * sort the tables by analyte name, because they're not
                     * sorted in the back-end
                     */
                    ui.getAnalyteTable().setModel(model);
                    if (model != null && model.size() > 0)
                        ui.getAnalyteTable().applySort(1, Table.SORT_ASCENDING, null);
                    model = getAuxTableModel();
                    ui.getAuxTable().setModel(model);
                    if (model != null && model.size() > 0)
                        ui.getAuxTable().applySort(1, Table.SORT_ASCENDING, null);
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
     * create the model for the analyte table from the data view object
     */
    private ArrayList<Row> getAnalyteTableModel() {
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (data.getTestAnalytes() == null || data.getTestAnalytes().size() < 1)
            return model;

        for (DataViewAnalyteVO ana : data.getTestAnalytes()) {
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

        for (DataViewAnalyteVO aux : data.getAuxFields()) {
            row = new Row(2);
            row.setCell(0, "N");
            row.setCell(1, aux.getAnalyteName());
            row.setData(aux);
            model.add(row);
        }
        return model;
    }
    
    /**
     * Sets the passed value as the "include" flag for all test analytes and
     * their results
     */
    private void updateAllTestAnalyte(String newVal) {
        Row row;
        
        for (int i = 0; i < ui.getAnalyteTable().getRowCount(); i++) {
            ui.getAnalyteTable().setValueAt(i, 0, newVal);
            row = ui.getAnalyteTable().getRowAt(i);
            updateTestAnalyte((DataViewAnalyteVO)row.getData(), newVal);
        }
    }
    
    /**
     * Sets the passed value as the "include" flag for all aux fields and their
     * aux data
     */
    private void updateAllAuxField(String newVal) {
        Row row;
        
        for (int i = 0; i < ui.getAuxTable().getRowCount(); i++) {
            ui.getAuxTable().setValueAt(i, 0, newVal);
            row = ui.getAuxTable().getRowAt(i);
            updateAuxData((DataViewAnalyteVO)row.getData(), newVal);
        }
    }

    private void updateTestAnalyte(DataViewAnalyteVO data, String val) {
        ArrayList<DataViewValueVO> list;

        data.setIsIncluded(val);
        list = data.getValues();
        for (DataViewValueVO res : list)
            res.setIsIncluded(val);
    }

    private void updateAuxData(DataViewAnalyteVO data, String val) {
        ArrayList<DataViewValueVO> list;

        data.setIsIncluded(val);
        list = data.getValues();
        for (DataViewValueVO value : list)
            value.setIsIncluded(val);
    }

    /**
     * create a spreadsheet with the checked data fields
     */
    protected void runReport() {
        int numTA, numAux;
        Widget widget;
        CheckBox cb;
        ArrayList<String> columns;
        ArrayList<DataViewAnalyteVO> taList, afList;

        numTA = 0;
        numAux = 0;
        taList = data.getTestAnalytes();
        if (taList != null) {
            for (DataViewAnalyteVO ta : taList) {
                if ("Y".equals(ta.getIsIncluded()))
                    numTA++ ;
            }
        }

        if (numTA == 0) {
            afList = data.getAuxFields();
            if (afList != null) {
                for (DataViewAnalyteVO af : afList) {
                    if ("Y".equals(af.getIsIncluded()))
                        numAux++ ;
                }
            }
            if (numAux == 0) {
                window.setError(Messages.get().dataView_selectOneAnaOrAux());
                return;
            }
        }
        
        /*
         * Add the keys for all checked checkboxes to the list of columns shown
         * in the generated excel file. Note: This will need additional checks
         * to work correctly if any checkboxes get added to the query fields, so 
         * that their keys don't get added to the list of columns.
         */
        columns = new ArrayList<String>();
        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            widget = entry.getValue().widget;
            if (widget instanceof CheckBox) {
                cb = (CheckBox)widget;
                if ("Y".equals(cb.getValue()))
                    columns.add(entry.getKey());
            }
        }
        
        data.setColumns(columns);

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
