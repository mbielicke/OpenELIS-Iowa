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
package org.openelis.modules.newbornScreeningSampleLogin.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.domain.IdAccessionVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class NewbornScreeningSampleLoginScreen extends Screen {
    

    private SampleManager1         manager;
    private Dropdown<Integer>      statusId;
    // feeding, feeding;
    private CalendarLookUp         collectedDate, receivedDate;
    // birth, birth, ;
    private TextBox                accessionNumber, orderNumber, collectedTime,
                                    clientReference, barcode;
    private Dropdown<Integer>      feedingId, birthOrderId;
    private CalendarLookUp         patientBirthDate, nextOfKinBirthDate, transfusionDate;
    private TextBox                patientLastName, patientFirstName, patientBirthTime,
                                    patientAddrMultipleUnit, patientAddrStreetAddress,
                                    patientAddrCity, patientAddrZipCode, nextOfKinLastName, nextOfKinMiddleName,
                                    nextOfKinFirstName, nextOfKinAddrHomePhone,
                                    nextOfKinAddressMultipleUnit, nextOfKinAddressStreetAddress,
                                    nextOfKinAddressCity, nextOfKinAddressZipCode,
                                    gestationalAge, weight, transfusionAge,
                                    collectionAge;
    private CheckBox               neoIsTransfused, neoIsRepeat, neoIsCollectionValid;
    private Dropdown<String>       neoPatientGenderId, neoPatientRaceId, neoPatientEthnicityId,
                                    neoPatientAddrState, neoNextOfKinRelationId, neoNextOfKinGenderId,
                                    neoNextOfKinRaceId, neoNextOfKinEthnicityId, neoNextOfKinAddressState;
    private AutoComplete<Integer>  projectName, orgName, billTo;
    private AppButton              queryButton, previousButton, nextButton, addButton,
                                    updateButton, commitButton, abortButton, orderLookup, addItemButton,
                                    addAnalysisButton, removeRowButton, popoutTree, projectLookup, reportToLookup,
                                    billToLookup;
    // private CheckBox tpn, repeat, repeat, tpn;
    private TabPanel               sampleItemTabPanel;

    protected SampleHistoryUtility historyUtility;

protected MenuItem                 duplicate, historySample,
                                    historySamplePrivateWell, historySampleProject, historySampleItem,
                                    historyAnalysis, historyCurrentResult, historyStorage,
                                    historySampleQA, historyAnalysisQA, historyAuxData;

private ScreenNavigator                nav;

private ModulePermission               userPermission;
private CheckBox neoIsNicu;  
    
    public NewbornScreeningSampleLoginScreen() throws Exception {
        super((ScreenDefInt)GWT.create(NewbornScreeningSampleLoginDef.class));

        userPermission = UserCache.getPermission().getModule("samplenewborn");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Neonatal Screening Sample Login Screen");

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
        initialize();
        //initializeDropdowns();
        
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }
    
    private void initialize() {
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                                userPermission.hasSelectPermission())
                                queryButton.enable(true);
                            else if (event.getState() == State.QUERY)
                                queryButton.setState(ButtonState.LOCK_PRESSED);
                            else
                                queryButton.enable(false);
            }
        });
        

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                                userPermission.hasAddPermission())
                                addButton.enable(true);
                            else if (EnumSet.of(State.ADD).contains(event.getState()))
                                addButton.setState(ButtonState.LOCK_PRESSED);
                            else
                                addButton.enable(false);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                userPermission.hasUpdatePermission())
                                updateButton.enable(true);
                            else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                                updateButton.setState(ButtonState.LOCK_PRESSED);
                            else
                                updateButton.enable(false);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                    .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
            }
        });
        
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                 userPermission.hasAddPermission());
            }
        });

        accessionNumber = (TextBox)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderNumber = (TextBox)def.getWidget(SampleMeta.getOrderId());
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                orderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderLookup = (AppButton)def.getWidget("orderButton");
        addScreenHandler(orderLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderLookup.enable(event.getState() == State.DISPLAY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
            }
        });

        collectedDate = (CalendarLookUp)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                collectedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedTime = (TextBox)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Date>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Date> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY); 
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        clientReference = (TextBox)def.getWidget(SampleMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<String> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(event.getState() == State.QUERY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
                clientReference.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientLastName = (TextBox)def.getWidget(SampleMeta.getNeoPatientLastName());
        addScreenHandler(patientLastName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientLastName.setValue(manager.getSampleNeonatal().getPatientLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientLastName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientLastName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientFirstName = (TextBox)def.getWidget(SampleMeta.getNeoPatientFirstName());
        addScreenHandler(patientFirstName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientFirstName.setValue(DO.getNeoPatientFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientFirstName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientFirstName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoPatientGenderId = (Dropdown)def.getWidget(SampleMeta.getNeoPatientGenderId());
        addScreenHandler(neoPatientGenderId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientGenderId.setSelection(DO.getNeoPatientGenderId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientGenderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoPatientGenderId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoPatientGenderId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoPatientRaceId = (Dropdown)def.getWidget(SampleMeta.getNeoPatientRaceId());
        addScreenHandler(neoPatientRaceId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientRaceId.setSelection(DO.getNeoPatientRaceId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientRaceId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoPatientRaceId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoPatientRaceId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoPatientEthnicityId = (Dropdown)def.getWidget(SampleMeta.getNeoPatientEthnicityId());
        addScreenHandler(neoPatientEthnicityId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientEthnicityId.setSelection(DO.getNeoPatientEthnicityId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientEthnicityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoPatientEthnicityId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoPatientEthnicityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientBirthDate = (CalendarLookUp)def.getWidget(SampleMeta.getNeoPatientBirthDate());
        addScreenHandler(patientBirthDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
               //neoPatientBirthDate.setValue(DO.getNeoPatientBirthDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                //DO.setNeoPatientBirthDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientBirthDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientBirthDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientBirthTime = (TextBox)def.getWidget(SampleMeta.getNeoPatientBirthTime());
        addScreenHandler(patientBirthTime, new ScreenEventHandler<Date>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientBirthTime.setValue(DO.getNeoPatientBirthTime());
            }

            public void onValueChange(ValueChangeEvent<Date> event) {
                //DO.setNeoPatientBirthTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientBirthTime.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientBirthTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientAddrMultipleUnit = (TextBox)def.getWidget(SampleMeta.getNeoPatientAddrMultipleUnit());
        addScreenHandler(patientAddrMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientAddrMultipleUnit.setValue(DO.getNeoPatientAddrMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientAddrMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientAddrMultipleUnit.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientAddrStreetAddress = (TextBox)def.getWidget(SampleMeta.getNeoPatientAddrStreetAddress());
        addScreenHandler(patientAddrStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientAddrStreetAddress.setValue(DO.getNeoPatientAddrStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientAddrStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientAddrStreetAddress.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientAddrStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientAddrCity = (TextBox)def.getWidget(SampleMeta.getNeoPatientAddrCity());
        addScreenHandler(patientAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientAddrCity.setValue(DO.getNeoPatientAddrCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientAddrCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientAddrCity.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientAddrCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoPatientAddrState = (Dropdown)def.getWidget(SampleMeta.getNeoPatientAddrState());
        addScreenHandler(neoPatientAddrState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientAddrState.setSelection(DO.getNeoPatientAddrState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientAddrState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoPatientAddrState.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoPatientAddrState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        patientAddrZipCode = (TextBox)def.getWidget(SampleMeta.getNeoPatientAddrZipCode());
        addScreenHandler(patientAddrZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoPatientAddrZipCode.setValue(DO.getNeoPatientAddrZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoPatientAddrZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                patientAddrZipCode.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                patientAddrZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinLastName = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinLastName());
        addScreenHandler(nextOfKinLastName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinLastName.setValue(DO.getNeoNextOfKinLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinLastName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinLastName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinMiddleName = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinMiddleName());
        addScreenHandler(nextOfKinMiddleName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinMiddleName.setValue(DO.getNeoNextOfKinMiddleName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinMiddleName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinMiddleName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinMiddleName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinFirstName = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinFirstName());
        addScreenHandler(nextOfKinFirstName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinFirstName.setValue(DO.getNeoNextOfKinFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinFirstName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinFirstName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoNextOfKinRelationId = (Dropdown)def.getWidget(SampleMeta.getNeoNextOfKinRelationId());
        addScreenHandler(neoNextOfKinRelationId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinRelationId.setSelection(DO.getNeoNextOfKinRelationId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinRelationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoNextOfKinRelationId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoNextOfKinRelationId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoNextOfKinGenderId = (Dropdown)def.getWidget(SampleMeta.getNeoNextOfKinGenderId());
        addScreenHandler(neoNextOfKinGenderId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinGenderId.setSelection(DO.getNeoNextOfKinGenderId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinGenderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoNextOfKinGenderId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoNextOfKinGenderId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoNextOfKinRaceId = (Dropdown)def.getWidget(SampleMeta.getNeoNextOfKinRaceId());
        addScreenHandler(neoNextOfKinRaceId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinRaceId.setSelection(DO.getNeoNextOfKinRaceId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinRaceId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoNextOfKinRaceId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoNextOfKinRaceId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoNextOfKinEthnicityId = (Dropdown)def.getWidget(SampleMeta.getNeoNextOfKinEthnicityId());
        addScreenHandler(neoNextOfKinEthnicityId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinEthnicityId.setSelection(DO.getNeoNextOfKinEthnicityId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinEthnicityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoNextOfKinEthnicityId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoNextOfKinEthnicityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinBirthDate = (CalendarLookUp)def.getWidget(SampleMeta.getNeoNextOfKinBirthDate());
        addScreenHandler(nextOfKinBirthDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinBirthDate.setValue(DO.getNeoNextOfKinBirthDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                //DO.setNeoNextOfKinBirthDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinBirthDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinBirthDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinAddrHomePhone = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinAddrHomePhone());
        addScreenHandler(nextOfKinAddrHomePhone, new ScreenEventHandler<Date>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddrHomePhone.setValue(DO.getNeoNextOfKinAddrHomePhone());
            }

            public void onValueChange(ValueChangeEvent<Date> event) {
                //DO.setNeoNextOfKinAddrHomePhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinAddrHomePhone.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinAddrHomePhone.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinAddressMultipleUnit = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinAddrMultipleUnit());
        addScreenHandler(nextOfKinAddressMultipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressMultipleUnit.setValue(DO.getNeoNextOfKinAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinAddressMultipleUnit.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinAddressMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinAddressStreetAddress = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinAddrStreetAddress());
        addScreenHandler(nextOfKinAddressStreetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressStreetAddress.setValue(DO.getNeoNextOfKinAddressStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinAddressStreetAddress.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinAddressStreetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinAddressCity = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinAddrCity());
        addScreenHandler(nextOfKinAddressCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressCity.setValue(DO.getNeoNextOfKinAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinAddressCity.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinAddressCity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoNextOfKinAddressState = (Dropdown)def.getWidget(SampleMeta.getNeoNextOfKinAddrState());
        addScreenHandler(neoNextOfKinAddressState, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressState.setSelection(DO.getNeoNextOfKinAddressState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoNextOfKinAddressState.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoNextOfKinAddressState.setQueryMode(event.getState() == State.QUERY);
            }
        });

        nextOfKinAddressZipCode = (TextBox)def.getWidget(SampleMeta.getNeoNextOfKinAddrZipCode());
        addScreenHandler(nextOfKinAddressZipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressZipCode.setValue(DO.getNeoNextOfKinAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextOfKinAddressZipCode.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                nextOfKinAddressZipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoIsNicu = (CheckBox)def.getWidget(SampleMeta.getNeoIsNicu());
        addScreenHandler(neoIsNicu, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressZipCode.setValue(DO.getNeoNextOfKinAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoNextOfKinAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoIsNicu.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoIsNicu.setQueryMode(event.getState() == State.QUERY);
            }
        });

        birthOrderId = (Dropdown)def.getWidget(SampleMeta.getNeoBirthOrder());
        addScreenHandler(birthOrderId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoNextOfKinAddressZipCode.setSelection(DO.getNeoNextOfKinAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoNextOfKinAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                birthOrderId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                birthOrderId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        gestationalAge = (TextBox)def.getWidget(SampleMeta.getNeoGestationalAge());
        addScreenHandler(gestationalAge, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoGestationalAge.setValue(DO.getNeoGestationalAge());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoGestationalAge(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                gestationalAge.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                gestationalAge.setQueryMode(event.getState() == State.QUERY);
            }
        });

        feedingId = (Dropdown)def.getWidget(SampleMeta.getNeoFeedingId());
        addScreenHandler(feedingId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoFeedingId.setSelection(DO.getNeoFeedingId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoFeedingId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                feedingId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                feedingId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        weight = (TextBox)def.getWidget(SampleMeta.getNeoWeight());
        addScreenHandler(weight, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoWeight.setValue(DO.getNeoWeight());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoWeight(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                weight.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                weight.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoIsTransfused = (CheckBox)def.getWidget(SampleMeta.getNeoIsTransfused());
        addScreenHandler(neoIsTransfused, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoIsTransfused.setValue(DO.getNeoIsTransfused());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoIsTransfused(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoIsTransfused.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoIsTransfused.setQueryMode(event.getState() == State.QUERY);
            }
        });

        transfusionDate = (CalendarLookUp)def.getWidget(SampleMeta.getNeoTransfusionDate());
        addScreenHandler(transfusionDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                //neoTransfusionDate.setValue(DO.getNeoTransfusionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                //DO.setNeoTransfusionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                transfusionDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                transfusionDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        transfusionAge = (TextBox)def.getWidget("transfusionAge");
        addScreenHandler(transfusionAge, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoTransfusionAge.setValue(DO.getNeoTransfusionAge());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoTransfusionAge(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                transfusionAge.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                transfusionAge.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoIsRepeat = (CheckBox)def.getWidget(SampleMeta.getNeoIsRepeat());
        addScreenHandler(neoIsRepeat, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoIsRepeat.setValue(DO.getNeoIsRepeat());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoIsRepeat(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoIsRepeat.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoIsRepeat.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectionAge = (TextBox)def.getWidget(SampleMeta.getNeoCollectionAge());
        addScreenHandler(collectionAge, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                //neoCollectionAge.setValue(DO.getNeoCollectionAge());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                //DO.setNeoCollectionAge(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionAge.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                collectionAge.setQueryMode(event.getState() == State.QUERY);
            }
        });

        neoIsCollectionValid = (CheckBox)def.getWidget(SampleMeta.getNeoIsCollectionValid());
        addScreenHandler(neoIsCollectionValid, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                //neoIsCollectionValid.setValue(DO.getNeoIsCollectionValid());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                //DO.setNeoIsCollectionValid(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                neoIsCollectionValid.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                neoIsCollectionValid.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addItemButton = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        addAnalysisButton = (AppButton)def.getWidget("addAnalysisButton");
        addScreenHandler(addAnalysisButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        popoutTree = (AppButton)def.getWidget("popoutTree");
        addScreenHandler(popoutTree, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        projectName = (AutoComplete)def.getWidget(SampleMeta.getProjectName());
        addScreenHandler(projectName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        projectLookup = (AppButton)def.getWidget("projectLookup");
        addScreenHandler(projectLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        orgName = (AutoComplete)def.getWidget(SampleMeta.getOrgName());
        addScreenHandler(orgName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        reportToLookup = (AppButton)def.getWidget("reportToLookup");
        addScreenHandler(reportToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        billTo = (AutoComplete)def.getWidget(SampleMeta.getBillTo());
        addScreenHandler(billTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        billToLookup = (AppButton)def.getWidget("billToLookup");
        addScreenHandler(billToLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        barcode = (TextBox)def.getWidget(SampleMeta.getNeoFormNumber());
        addScreenHandler(barcode, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        sampleItemTabPanel = (TabPanel)def.getWidget("sampleItemTabPanel");
        sampleItemTabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

            }
        });

      /*  tab0 = new Tab0(def, window);
        addScreenHandler(tab0, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab1 = new Tab1(def, window);
        addScreenHandler(tab1, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab2 = new Tab2(def, window);
        addScreenHandler(tab2, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab3 = new Tab3(def, window);
        addScreenHandler(tab3, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab4 = new Tab4(def, window);
        addScreenHandler(tab4, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab5 = new Tab5(def, window);
        addScreenHandler(tab5, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab6 = new Tab6(def, window);
        addScreenHandler(tab6, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        tab7 = new Tab7(def, window);
        addScreenHandler(tab7, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });
*/
        nav = new ScreenNavigator<IdAccessionVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                query.setRowsPerPage(5);
                SampleService.get().query(query, new AsyncCallback<ArrayList<IdAccessionVO>>() {
                    public void onSuccess(ArrayList<IdAccessionVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: envsample call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(IdAccessionVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdAccessionVO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (IdAccessionVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getAccessionNumber()));
                }
                return model;
            }
        };
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }
    
    protected void query() {        
    }
    
    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }
    
    protected void add() {
        
    }
    
    protected void update() {
        
    }
    
    protected void commit() {
        
    }
    
    protected void abort() {
        
    }
    
    protected void duplicate() {
        
    }
    
    protected boolean fetchById(Integer id) {
        return false;
    }
    
    private boolean canEdit() {
        // TODO Auto-generated method stub
        return false;
    }
}