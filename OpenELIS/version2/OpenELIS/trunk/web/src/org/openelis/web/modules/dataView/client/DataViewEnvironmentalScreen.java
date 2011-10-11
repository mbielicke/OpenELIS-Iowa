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
package org.openelis.web.modules.dataView.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.TestAnalyteDataViewVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.DeckPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.meta.SampleWebMeta;
import org.openelis.web.util.ReportScreenUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DataViewEnvironmentalScreen extends Screen {

    private DataViewVO                                     data;
    private ModulePermission                               userPermission;
    private CalendarLookUp                                 releasedDateFrom, releasedDateTo, collectedDateFrom,
                                                           collectedDateTo;
    private TextBox                                        collectorName, accessionFrom,
                                                           accessionTo, clientReference, collectionSite, collectionTown;
    private CheckBox                                       accessionNumber, collectorNameHeader, clientReferenceHeader, collectionSiteHeader, collectedDate, 
                                                           projectCodeHeader, description, 
                                                           receivedDate, releasedDate, statusId, envCollectorPhone, itemTypeofSampleId, itemSourceOfSampleId,
                                                           sampleOrgOrganizationName, addressMultipleUnit, addressStreetAddress, addressCity, addressState, addressZipCode,
                                                           analysisTestNameHeader, analysisMethodNameHeader, analysisRevision, analysisStartedDate, analysisCompletedDate, analysisReleasedDate;
    private Dropdown<Integer>                              projectCode;
    private ReportScreenUtility                            util;
    private DeckPanel                                      deckpanel;
    private Decks                                          deck;
    private HorizontalPanel                                hp;
    private AbsolutePanel                                  ap;
    private TableWidget                                    availAnalyteTable, availAuxTable;
    private Label<String>                                  queryDeckLabel;
    private AppButton                                      getSampleListButton, resetButton, selectAllSampleFields, selectAllOrganizationFields, selectAllAnalysisFields,
                                                           runReportButton, backButton, selectAllAnalyteButton, unselectAllAnalyteButton, selectAllAuxButton, unselectAllAuxButton;
    private ScreenService finalReportService;
    private Screen  screen;
    private boolean loadTable;
    private enum Decks {
        QUERY, LIST
    };

    /**
     * No-Arg constructor
     */
    public DataViewEnvironmentalScreen() throws Exception {
        super((ScreenDefInt)GWT.create(DataViewEnvironmentalDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.dataView.server.DataViewService");
        finalReportService = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");

        userPermission = UserCache.getPermission().getModule("w_datadump_environmental");
        if (userPermission == null)
            throw new PermissionException("screenPermException",
                                          "Final Report Environmental Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        deck = Decks.QUERY;
        data = new DataViewVO();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        loadTable = true;
        DataChangeEvent.fire(this);
    }

    /**
     * Initialize widgets
     */
    private void initialize() {
        
        screen = this;

        util = new ReportScreenUtility(def);

        deckpanel = (DeckPanel)def.getWidget("deck");
        
        releasedDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getReleasedDateFrom());
        addScreenHandler(releasedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDateFrom.setValue(DataBaseUtil.toYD(data.getReleasedDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedDateFrom(event.getValue().getDate());
                if (releasedDateFrom.getValue() != null && releasedDateTo.getValue() == null) {
                    releasedDateTo.setValue(releasedDateFrom.getValue().add(1));
                    releasedDateTo.setFocus(true);
                    releasedDateTo.selectText();
                    data.setReleasedDateTo(event.getValue().getDate());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        releasedDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getReleasedDateTo());
        addScreenHandler(releasedDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDateTo.setValue(DataBaseUtil.toYD(data.getReleasedDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedDateTo(event.getValue().getDate());
                if (releasedDateTo.getValue() != null && releasedDateFrom.getValue() == null) {
                    releasedDateFrom.setValue(releasedDateTo.getValue().add( -1));
                    releasedDateFrom.setFocus(true);
                    releasedDateFrom.selectText();
                    data.setReleasedDateFrom(event.getValue().getDate());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectedDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateFrom());
        addScreenHandler(collectedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDateFrom.setValue(DataBaseUtil.toYD(data.getCollectionDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectionDateFrom(event.getValue().getDate());
                if (collectedDateFrom.getValue() != null && collectedDateTo.getValue() == null) {
                    collectedDateTo.setValue(collectedDateFrom.getValue().add(1));
                    collectedDateTo.setFocus(true);
                    collectedDateTo.selectText();
                    data.setCollectionDateTo(event.getValue().getDate());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectedDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateTo());
        addScreenHandler(collectedDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDateTo.setValue(DataBaseUtil.toYD(data.getCollectionDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectionDateTo(event.getValue().getDate());
                if (collectedDateTo.getValue() != null && collectedDateFrom.getValue() == null) {
                    collectedDateFrom.setValue(collectedDateTo.getValue().add( -1));
                    collectedDateFrom.setFocus(true);
                    collectedDateFrom.selectText();
                    data.setCollectionDateFrom(event.getValue().getDate());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectorName = (TextBox)def.getWidget(SampleWebMeta.getEnvCollector());
        addScreenHandler(collectorName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorName.setValue(data.getSampleEnvironmentalCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectorName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }

        });

        accessionFrom = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberFrom());
        addScreenHandler(accessionFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionFrom.setValue(data.getAccessionNumberFrom());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionNumberFrom(event.getValue());
                if ( !DataBaseUtil.isEmpty(accessionFrom.getValue()) &&
                    DataBaseUtil.isEmpty(accessionTo.getValue())) {
                    accessionTo.setFieldValue(accessionFrom.getValue());
                    accessionTo.setFocus(true);
                    accessionTo.selectAll();
                    data.setAccessionNumberTo(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        accessionTo = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberTo());
        addScreenHandler(accessionTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionTo.setValue(data.getAccessionNumberTo());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionNumberTo(event.getValue());
                if ( !DataBaseUtil.isEmpty(accessionTo.getValue()) &&
                    DataBaseUtil.isEmpty(accessionFrom.getValue())) {
                    accessionFrom.setFieldValue(accessionTo.getValue());
                    accessionFrom.setFocus(true);
                    accessionFrom.selectAll();
                    data.setAccessionNumberFrom(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        clientReference = (TextBox)def.getWidget(SampleWebMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(data.getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionSite = (TextBox)def.getWidget(SampleWebMeta.getEnvLocation());
        addScreenHandler(collectionSite, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionSite.setValue(data.getSampleEnvironmentalLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionSite.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionTown = (TextBox)def.getWidget(SampleWebMeta.getLocationAddrCity());
        addScreenHandler(collectionTown, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionTown.setValue(data.getSampleEnvironmentalLocationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionTown.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        projectCode = (Dropdown)def.getWidget(SampleWebMeta.getProjectId());
        addScreenHandler(projectCode, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                projectCode.setSelection(data.getProjectId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setProjectId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectCode.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        accessionNumber = (CheckBox)def.getWidget(SampleWebMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(data.getAccessionNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        collectorNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorHeader());
        addScreenHandler(collectorNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorNameHeader.setValue(data.getSampleEnvironmentalCollectorHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectorNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        

        clientReferenceHeader = (CheckBox)def.getWidget(SampleWebMeta.getClientReferenceHeader());
        addScreenHandler(clientReferenceHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReferenceHeader.setValue(data.getClientReferenceHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReferenceHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReferenceHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        collectionSiteHeader = (CheckBox)def.getWidget(SampleWebMeta.getEnvLocationHeader());
        addScreenHandler(collectionSiteHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionSiteHeader.setValue(data.getSampleEnvironmentalLocationHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionSiteHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        collectedDate = (CheckBox)def.getWidget(SampleWebMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(data.getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        projectCodeHeader = (CheckBox)def.getWidget(SampleWebMeta.getProjectIdHeader());
        addScreenHandler(projectCodeHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                projectCodeHeader.setValue(data.getProjectName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setProjectName(event.getValue());
                //changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectCodeHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        description = (CheckBox)def.getWidget(SampleWebMeta.getEnvDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(data.getSampleEnvironmentalDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalDescription(event.getValue());
               // changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });    
        
        receivedDate = (CheckBox)def.getWidget(SampleWebMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(data.getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        }); 
        
        releasedDate = (CheckBox)def.getWidget(SampleWebMeta.getReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setValue(data.getReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        }); 
        
        statusId = (CheckBox)def.getWidget(SampleWebMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(data.getStatusId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        }); 
        
        envCollectorPhone = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorPhone());
        addScreenHandler(envCollectorPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollectorPhone.setValue(data.getSampleEnvironmentalCollectorPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorPhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envCollectorPhone.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        }); 
        
        itemTypeofSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemTypeofSampleId());
        addScreenHandler(itemTypeofSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemTypeofSampleId.setValue(data.getSampleItemTypeofSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemTypeofSampleId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemTypeofSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        }); 
        
        itemSourceOfSampleId = (CheckBox)def.getWidget(SampleWebMeta.getItemSourceOfSampleId());
        addScreenHandler(itemSourceOfSampleId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                itemSourceOfSampleId.setValue(data.getSampleItemSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleItemSourceOfSampleId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                itemSourceOfSampleId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        sampleOrgOrganizationName = (CheckBox)def.getWidget(SampleWebMeta.getSampleOrgOrganizationName());
        addScreenHandler(sampleOrgOrganizationName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrgOrganizationName.setValue(data.getOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrgOrganizationName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        addressMultipleUnit = (CheckBox)def.getWidget(SampleWebMeta.getAddressMultipleUnit());
        addScreenHandler(addressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressMultipleUnit.setValue(data.getOrganizationAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressMultipleUnit.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressStreetAddress = (CheckBox)def.getWidget(SampleWebMeta.getAddressStreetAddress());
        addScreenHandler(addressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressStreetAddress.setValue(data.getOrganizationAddressAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressStreetAddress.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressCity = (CheckBox)def.getWidget(SampleWebMeta.getAddressCity());
        addScreenHandler(addressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressCity.setValue(data.getOrganizationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressState = (CheckBox)def.getWidget(SampleWebMeta.getAddressState());
        addScreenHandler(addressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressState.setValue(data.getOrganizationAddressState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressState.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        addressZipCode = (CheckBox)def.getWidget(SampleWebMeta.getAddressZipCode());
        addScreenHandler(addressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                addressZipCode.setValue(data.getOrganizationAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOrganizationAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressZipCode.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisTestNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisTestNameHeader());
        addScreenHandler(analysisTestNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTestNameHeader.setValue(data.getAnalysisTestNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestNameHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTestNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisMethodNameHeader = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisMethodNameHeader());
        addScreenHandler(analysisMethodNameHeader, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisMethodNameHeader.setValue(data.getAnalysisTestMethodNameHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestMethodNameHeader(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisMethodNameHeader.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisRevision = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisRevision());
        addScreenHandler(analysisRevision, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisRevision.setValue(data.getAnalysisRevision());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisRevision(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisRevision.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisStartedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisStartedDate());
        addScreenHandler(analysisStartedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStartedDate.setValue(data.getAnalysisStartedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStartedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisCompletedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisCompletedDate());
        addScreenHandler(analysisCompletedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDate.setValue(data.getAnalysisCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        analysisReleasedDate = (CheckBox)def.getWidget(SampleWebMeta.getAnalysisReleasedDate());
        addScreenHandler(analysisReleasedDate, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDate.setValue(data.getAnalysisReleasedDate());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisReleasedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedDate.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        selectAllSampleFields = (AppButton)def.getWidget("selectAllSampleFields");
        addScreenHandler(selectAllSampleFields, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onClick(ClickEvent event) {
                data.setAccessionNumber("Y");
                data.setSampleEnvironmentalCollectorHeader("Y");
                data.setClientReferenceHeader("Y");
                data.setSampleEnvironmentalLocationHeader("Y");
                data.setCollectionDate("Y");
                //data.setCollectionTime(event.getValue());
                data.setAnalysisTestMethodName("Y");
                data.setProjectName("Y");
                data.setSampleEnvironmentalDescription("Y");
                data.setReceivedDate("Y");
                data.setReleasedDate("Y");
                data.setStatusId("Y");
                data.setSampleEnvironmentalCollectorPhone("Y");
                data.setSampleItemTypeofSampleId("Y");
                data.setSampleItemSourceOfSampleId("Y");
                loadTable = false;
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllSampleFields.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        selectAllOrganizationFields = (AppButton)def.getWidget("selectAllOrganizationFields");
        addScreenHandler(selectAllOrganizationFields, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                
            }

            public void onClick(ClickEvent event) {
                data.setOrganizationName("Y");
                data.setOrganizationAddressMultipleUnit("Y");
                data.setOrganizationAddressAddress("Y");
                data.setOrganizationAddressCity("Y");
                data.setOrganizationAddressState("Y");
                data.setOrganizationAddressZipCode("Y");               
                loadTable = false;
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllOrganizationFields.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        selectAllAnalysisFields = (AppButton)def.getWidget("selectAllAnalysisFields");
        addScreenHandler(selectAllAnalysisFields, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //selectAllOrganizationFields.setValue(data.getSampleEnvironmentalDescription());
            }

            public void onClick(ClickEvent event) {
                data.setAnalysisTestNameHeader("Y");
                data.setAnalysisTestMethodNameHeader("Y");
                data.setAnalysisRevision("Y");
                data.setAnalysisStartedDate("Y");
                data.setAnalysisCompletedDate("Y");
                data.setAnalysisReleasedDate("Y");               
                loadTable = false;
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAnalysisFields.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        availAnalyteTable = (TableWidget)def.getWidget("availAnalyteTable");
        addScreenHandler(availAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {  
                /*
                 * Since datachange event gets fired when checkboxes in the screen gets checked, 
                 * so we need to make sure that the tables dont get loaded every time a check box is clicked.
                 */
                if (loadTable)
                    availAnalyteTable.load(getAnalyteTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                availAnalyteTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        availAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                TableDataRow arow;
                TestAnalyteDataViewVO data;
                String val;
                
                r = event.getRow();
                c = event.getCol();
                arow = availAnalyteTable.getRow(r);
                val = (String)availAnalyteTable.getObject(r, c);
                data = (TestAnalyteDataViewVO)arow.data;
                switch (c) {
                    case 0: 
                        /*
                         * When the checkbox for an analyte is checked or unchecked
                         * the analyte need to be flagged as "included" or not
                         * appropriately   
                         */
                        updateTestAnalyte(data,val);
                        break;
                }
            }
        });
        
        
        selectAllAnalyteButton = (AppButton)def.getWidget("selectAllAnalyteButton");
        addScreenHandler(selectAllAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                TestAnalyteDataViewVO data;
                Object val;
                
                model = availAnalyteTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = (String)availAnalyteTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {
                        row = model.get(i);
                        data = (TestAnalyteDataViewVO)row.data;
                        updateTestAnalyte(data,"Y");
                        availAnalyteTable.setCell(i, 0, "Y");                       
                    }
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAnalyteButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        unselectAllAnalyteButton = (AppButton)def.getWidget("unselectAllAnalyteButton");
        addScreenHandler(unselectAllAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                TestAnalyteDataViewVO data;
                Object val;
                
                model = availAnalyteTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = availAnalyteTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        row = model.get(i);
                        data = (TestAnalyteDataViewVO)row.data;
                        updateTestAnalyte(data,"N");
                        availAnalyteTable.setCell(i, 0, "N");
                    }
                }                                   
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllAnalyteButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });       
        
        availAuxTable = (TableWidget)def.getWidget("availAuxTable");
        addScreenHandler(availAuxTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {  
                if (loadTable)
                    availAuxTable.load(getAuxTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                availAuxTable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        availAuxTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                TableDataRow arow;
                AuxFieldDataViewVO data;
                String val;
                r = event.getRow();
                c = event.getCol();
                arow = availAuxTable.getRow(r);
                val = (String)availAuxTable.getObject(r, c);
                data = (AuxFieldDataViewVO)arow.data;
                switch (c) {
                    case 0: 
                        /*
                         * When the checkbox for an analyte is checked or unchecked
                         * the analyte need to be flagged as "included" or not
                         * appropriately   
                         */
                        updateAuxData(data, val);                                    
                        break;
                }
            }
        });
        
        selectAllAuxButton = (AppButton)def.getWidget("selectAllAuxButton");
        addScreenHandler(selectAllAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AuxFieldDataViewVO data;
                Object val;
                
                model = availAuxTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = (String)availAuxTable.getCell(i, 0).getValue();
                    if ("N".equals(val)) {
                        row = model.get(i);
                        data = (AuxFieldDataViewVO)row.data;
                        updateAuxData(data, "Y");  
                        availAuxTable.setCell(i, 0, "Y");                       
                    }
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllAuxButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
        
        unselectAllAuxButton = (AppButton)def.getWidget("unselectAllAuxButton");
        addScreenHandler(unselectAllAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ArrayList<TableDataRow> model;
                TableDataRow row;
                AuxFieldDataViewVO data;
                Object val;
                
                model = availAuxTable.getData();
                for (int i = 0; i < model.size(); i++) {
                    val = availAuxTable.getCell(i, 0).getValue();
                    if ("Y".equals(val)) {
                        row = model.get(i);
                        data = (AuxFieldDataViewVO)row.data;
                        updateAuxData(data, "N");  
                        availAuxTable.setCell(i, 0, "N");
                    }
                }                                   
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectAllAuxButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });  
        
        getSampleListButton = (AppButton)def.getWidget("getSampleListButton");
        addScreenHandler(getSampleListButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                getSamples();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                getSampleListButton.enable(true);
            }
        });       
        
        resetButton = (AppButton)def.getWidget("resetButton");
        addScreenHandler(resetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                reset();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resetButton.enable(true);
            }
        });

        runReportButton = (AppButton)def.getWidget("runReportButton");
        addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                runReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                runReportButton.enable(true);
            }
        });

        backButton = (AppButton)def.getWidget("backButton");
        addScreenHandler(backButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
               loadDeck();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                backButton.enable(true);
            }
        });

        queryDeckLabel = new Label("Query");
        queryDeckLabel.setStyleName("ScreenLabel");
        hp = new HorizontalPanel();
        ap = new AbsolutePanel();
        ap.setStyleName("PreviousButtonImage");
        hp.add(ap);
        hp.add(queryDeckLabel);
        backButton.setWidget(hp);        
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
        TableDataRow row;
        /*
         * Initializing the project code drop down
         */
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        try {
            list = finalReportService.callList("getEnvironmentalProjectList");
            for (int counter = 0; counter < list.size(); counter++ ) {
                row = new TableDataRow(list.get(counter).getId(), list.get(counter).getName());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        projectCode.setModel(model);
    }
    
    private void updateTestAnalyte(TestAnalyteDataViewVO data, String val){
        ArrayList<ResultDataViewVO> list;
        
        data.setIsIncluded(val);    
        list = data.getResults();
        for (ResultDataViewVO res : list) 
            res.setIsIncluded(val);
    }
    
    private void updateAuxData(AuxFieldDataViewVO data, String val){
        ArrayList<AuxDataDataViewVO> list;
        
        data.setIsIncluded(val);    
        list = data.getValues();
        for (AuxDataDataViewVO value : list) 
            value.setIsIncluded(val);
    }

    protected void getSamples() {
        Query query;
        DataViewVO temp;
        ArrayList<QueryData> queryList;

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
        query = new Query();
        queryList = createWhereFromParamFields(util.getQueryFields());
        /*
         * if user does not enter any search details, throw an error.
         */
        if (queryList.size() == 0) {
            window.setError(consts.get("nofieldSelectedError"));
            return;
        }
        query.setFields(queryList);
        data.setQueryFields(query.getFields());
        window.setBusy(consts.get("querying"));

        try {
            temp = service.call("fetchAnalyteAndAuxFieldForWebEnvironmental", query);
            data.setAnalytes(temp.getTestAnalytes());
            data.setAuxFields(temp.getAuxFields());
            loadDeck();
        } catch (NotFoundException e) {
            Window.alert(consts.get("noSamplesFoundChangeSearch"));
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    /**
     * Resets all the fields to their original report specified values
     */
    protected void reset() {
        data = new DataViewVO();
        setState(State.DEFAULT);
        loadTable = true;
        DataChangeEvent.fire(this);
        clearErrors();
    }

    protected void loadDeck() {
        switch (deck) {
            case QUERY:
                deckpanel.showWidget(1);
                deck = Decks.LIST;
                setState(State.DEFAULT);
                setData(data);
                backButton.setVisible(true);                
                break;
            case LIST:
                deckpanel.showWidget(0);
                deck = Decks.QUERY;
                break;
        }
    }

    public void setData(DataViewVO data) {
        this.data = data;
        setState(State.DEFAULT);
        loadTable = true;
        DataChangeEvent.fire(this);
    }

    protected void runReport() {
        int numTA, numAux;
        String url;
        ReportStatus st;
        ArrayList<TestAnalyteDataViewVO> taList;
        ArrayList<AuxFieldDataViewVO> afList;  

        numTA = 0;
        numAux = 0;
        taList = data.getTestAnalytes();
        if (taList != null) {
            for (TestAnalyteDataViewVO ta : taList) {
                if ("Y".equals(ta.getIsIncluded()))
                    numTA++;
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
                window.setError(consts.get("selectOneAnaOrAux"));
                return;
            }
        }
        try {
            st = service.call("runReportForWebEnvironmental", data);
            if (st.getStatus() == ReportStatus.Status.SAVED) {
                url = "report?file=" + st.getMessage();

                Window.open(URL.encode(url), "Final Report", null);
                window.setStatus("Generated file " + st.getMessage(), "");
            } else {
                window.setStatus(st.getMessage(), "");
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }    
    
    private ArrayList<TableDataRow> getAnalyteTableModel() {
        ArrayList<TableDataRow> model;
        ArrayList<TestAnalyteDataViewVO> analytes;        
        TableDataRow row;              
        
        if (data == null || data.getTestAnalytes() == null)
            return null;
        analytes =  data.getTestAnalytes();
        model = new ArrayList<TableDataRow>();
        for (TestAnalyteDataViewVO ana : analytes) {
            row = new TableDataRow(2);   
            row.cells.get(0).setValue("N");
            row.cells.get(1).setValue(ana.getAnalyteName());
            ana.setIsIncluded("N");
            row.data = ana;
            model.add(row);
        }
        return model;
    }
    
    
    private ArrayList<TableDataRow> getAuxTableModel() {
        ArrayList<TableDataRow> model;
        ArrayList<AuxFieldDataViewVO> auxFields;        
        TableDataRow row;              
        
        if (data == null || data.getAuxFields() == null)
            return null;
        
        auxFields =  data.getAuxFields();
        model = new ArrayList<TableDataRow>();
        for (AuxFieldDataViewVO aux : auxFields) {
            row = new TableDataRow(2);
            row.cells.get(0).setValue("N");
            row.cells.get(1).setValue(aux.getAnalyteName());
            aux.setIsIncluded("N");
            row.data = aux;
            model.add(row);
        }        
        return model;
    }
    
    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fRel, fCol, fAcc;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fRel = fCol = fAcc = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (SampleWebMeta.getReleasedDateFrom()).equals(field.key)) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = field.query + ".." + fRel.query;
                    list.add(fRel);
                }
            } else if ( (SampleWebMeta.getReleasedDateTo()).equals(field.key)) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = fRel.query + ".." + field.query;
                    list.add(fRel);
                }
            } else if ( (SampleWebMeta.getCollectionDateFrom()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = field.query + ".." + fCol.query;
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getCollectionDateTo()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = fCol.query + ".." + field.query;
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getAccessionNumberFrom()).equals(field.key)) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = field.query + ".." + fAcc.query;
                    list.add(fAcc);
                }
            } else if ( (SampleWebMeta.getAccessionNumberTo()).equals(field.key)) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = fAcc.query + ".." + field.query;
                    list.add(fAcc);
                }
            } else {
                list.add(field);
            }
        }
        return list;
    }
}
