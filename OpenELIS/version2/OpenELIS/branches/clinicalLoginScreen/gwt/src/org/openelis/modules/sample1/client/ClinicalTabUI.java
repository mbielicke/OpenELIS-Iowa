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
package org.openelis.modules.sample1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.patient.client.PatientLookupUI;
import org.openelis.modules.patient.client.PatientService;
import org.openelis.modules.provider.client.ProviderService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ClinicalTabUI extends Screen {
    @UiTemplate("ClinicalTab.ui.xml")
    interface ClinicalTabUIBinder extends UiBinder<Widget, ClinicalTabUI> {
    };

    private static ClinicalTabUIBinder uiBinder = GWT.create(ClinicalTabUIBinder.class);

    protected SampleManager1           manager;

    @UiField
    protected Calendar                 patientBirthDate;

    @UiField
    protected TextBox<Integer>         patientId;

    @UiField
    protected TextBox<String>          patientLastName, patientMiddleName, patientFirstName,
                    patientNationalId, patientAddrMultipleUnit, patientAddrStreetAddress,
                    patientAddrCity, patientAddrZipCode, providerFirstName, providerPhone;

    @UiField
    protected Dropdown<Integer>        patientGender, patientRace, patientEthnicity;

    @UiField
    protected Dropdown<String>         patientAddrState;

    @UiField
    protected AutoComplete             providerLastName;

    @UiField
    protected Button                   fullSearchButton, nameSearchButton, unlinkPatientButton,
                    editPatientButton;

    protected Screen                   parentScreen;

    protected PatientLookupUI          patientLookup;

    protected EventBus                 parentBus;

    protected boolean                  canEditSample, canEditPatient, isBusy, isPatientLocked,
                    isVisible, redraw;

    public ClinicalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        String r;
        Item<Integer> row;
        Item<String> strow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stmodel;

        addScreenHandler(patientId,
                         SampleMeta.getClinicalPatientId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientId.setValue(getPatientId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientId.setEnabled(isState(QUERY));
                                 patientId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientLastName : providerPhone;
                             }
                         });

        addScreenHandler(fullSearchButton, "fullSearchButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                fullSearchButton.setEnabled(canEditSample && !isPatientLocked &&
                                            isState(ADD, UPDATE));
            }
        });

        addScreenHandler(nameSearchButton, "nameSearchButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                nameSearchButton.setEnabled(canEditSample && !isPatientLocked &&
                                            isState(ADD, UPDATE));
            }
        });

        addScreenHandler(unlinkPatientButton, "unlinkPatientButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unlinkPatientButton.setEnabled(canEditSample && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(editPatientButton, "editPatientButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editPatientButton.setEnabled(canEditSample && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(patientLastName,
                         SampleMeta.getClinicalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientLastName.setValue(getPatientLastName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientLastName(event.getValue());
                                 if (getPatientLastName() != null && getPatientFirstName() != null)
                                     patientQueryChanged(patientLastName);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientLastName.setEnabled(isState(QUERY) ||
                                                            (canEditSample && canEditPatient && isState(ADD,
                                                                                                        UPDATE)));
                                 patientLastName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientMiddleName : patientId;
                             }
                         });

        addScreenHandler(patientMiddleName,
                         SampleMeta.getClinicalPatientMiddleName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientMiddleName.setValue(getPatientMiddleName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientMiddleName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientMiddleName.setEnabled(isState(QUERY) ||
                                                              (canEditSample && canEditPatient && isState(ADD,
                                                                                                          UPDATE)));
                                 patientMiddleName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientFirstName : patientLastName;
                             }
                         });

        addScreenHandler(patientFirstName,
                         SampleMeta.getClinicalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientFirstName.setValue(getPatientFirstName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientFirstName(event.getValue());
                                 if (getPatientLastName() != null && getPatientFirstName() != null)
                                     patientQueryChanged(patientFirstName);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientFirstName.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientFirstName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientGender : patientMiddleName;
                             }
                         });

        addScreenHandler(patientGender,
                         SampleMeta.getClinicalPatientGenderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientGender.setValue(getPatientGenderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientGenderId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientGender.setEnabled(isState(QUERY) ||
                                                          (canEditSample && canEditPatient && isState(ADD,
                                                                                                      UPDATE)));
                                 patientGender.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientRace : patientFirstName;
                             }
                         });

        addScreenHandler(patientRace,
                         SampleMeta.getClinicalPatientRaceId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientRace.setValue(getPatientRaceId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientRaceId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientRace.setEnabled(isState(QUERY) ||
                                                        (canEditSample && canEditPatient && isState(ADD,
                                                                                                    UPDATE)));
                                 patientRace.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientEthnicity : patientGender;
                             }
                         });

        addScreenHandler(patientEthnicity,
                         SampleMeta.getClinicalPatientEthnicityId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientEthnicity.setValue(getPatientEthnicityId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientEthnicityId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientEthnicity.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientEthnicity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientBirthDate : patientRace;
                             }
                         });

        addScreenHandler(patientBirthDate,
                         SampleMeta.getClinicalPatientBirthDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientBirthDate.setValue(getPatientBirthDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPatientBirthDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientBirthDate.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientBirthDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientNationalId : patientEthnicity;
                             }
                         });

        addScreenHandler(patientNationalId,
                         SampleMeta.getClinicalPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientNationalId.setValue(getPatientNationalId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientNationalId(event.getValue());
                                 if (getPatientNationalId() != null)
                                     patientQueryChanged(patientNationalId);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientNationalId.setEnabled(isState(QUERY) ||
                                                              (canEditSample && canEditPatient && isState(ADD,
                                                                                                          UPDATE)));
                                 patientNationalId.setQueryMode(event.getState() == State.QUERY);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrMultipleUnit : patientBirthDate;
                             }
                         });

        addScreenHandler(patientAddrMultipleUnit,
                         SampleMeta.getClinicalPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrMultipleUnit.setValue(getPatientAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrMultipleUnit.setEnabled(isState(QUERY) ||
                                                                    (canEditSample &&
                                                                     canEditPatient && isState(ADD,
                                                                                               UPDATE)));
                                 patientAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrStreetAddress : patientNationalId;
                             }
                         });

        addScreenHandler(patientAddrStreetAddress,
                         SampleMeta.getClinicalPatientAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrStreetAddress.setValue(getPatientAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrStreetAddress.setEnabled(isState(QUERY) ||
                                                                     (canEditSample &&
                                                                      canEditPatient && isState(ADD,
                                                                                                UPDATE)));
                                 patientAddrStreetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrCity : patientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(patientAddrCity,
                         SampleMeta.getClinicalPatientAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrCity.setValue(getPatientAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrCity.setEnabled(isState(QUERY) ||
                                                            (canEditSample && canEditPatient && isState(ADD,
                                                                                                        UPDATE)));
                                 patientAddrCity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrState : patientAddrStreetAddress;
                             }
                         });

        addScreenHandler(patientAddrState,
                         SampleMeta.getClinicalPatientAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrState.setValue(getPatientAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrState.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientAddrState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrZipCode : patientAddrCity;
                             }
                         });

        addScreenHandler(patientAddrZipCode,
                         SampleMeta.getClinicalPatientAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrZipCode.setValue(getPatientAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrZipCode.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditPatient && isState(ADD,
                                                                                                           UPDATE)));
                                 patientAddrZipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerLastName : patientAddrState;
                             }
                         });

        addScreenHandler(providerLastName,
                         SampleMeta.getClinicalProviderLastName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerLastName.setValue(getProviderId(), getProviderLastName());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 ProviderDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (ProviderDO)event.getValue().getData();
                                 setProvider(data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerLastName.setEnabled(isState(QUERY) ||
                                                             (canEditSample && isState(ADD, UPDATE)));
                                 providerLastName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerFirstName : patientAddrZipCode;
                             }
                         });

        providerLastName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ProviderDO data;
                ArrayList<ProviderDO> list;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;

                parentScreen.setBusy();
                try {
                    list = ProviderService.get()
                                          .fetchByLastNameNpiExternalId(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getLastName());
                        row.setCell(1, data.getFirstName());
                        row.setCell(2, data.getMiddleName());
                        row.setCell(3, data.getNpi());

                        model.add(row);
                    }
                    providerLastName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.clearStatus();
            }
        });

        addScreenHandler(providerFirstName,
                         SampleMeta.getClinicalProviderFirstName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerFirstName.setValue(getProviderFirstName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerFirstName.setEnabled(isState(QUERY) ||
                                                              (canEditSample && isState(ADD, UPDATE)));
                                 providerFirstName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerPhone : providerLastName;
                             }
                         });

        addScreenHandler(providerPhone,
                         SampleMeta.getClinicalProviderPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerPhone.setValue(getProviderPhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setProviderPhone(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerPhone.setEnabled(isState(QUERY) ||
                                                          (canEditSample && isState(ADD, UPDATE)));
                                 providerPhone.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientId : providerFirstName;
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySampleClinical();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("gender")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientGender.setModel(model);

        model = new ArrayList<Item<Integer>>();
        /*
         * show the combination of code (1,2,3 etc.) and entry("White", "Black")
         * for each race
         */
        for (DictionaryDO d : CategoryCache.getBySystemName("race")) {
            r = DataBaseUtil.concatWithSeparator(d.getCode(), " - ", d.getEntry());
            row = new Item<Integer>(d.getId(), r);
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientRace.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("ethnicity")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientEthnicity.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled( ("Y".equals(d.getIsActive())));
            stmodel.add(strow);
        }

        patientAddrState.setModel(stmodel);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * returns true if some operation performed by the tab needs to be completed
     * before the data can be committed
     */
    public boolean getIsBusy() {
        return isBusy;
    }

    /**
     * returns true if the patient linked to the sample is locked
     */
    public boolean getIsPatientLocked() {
        return isPatientLocked;
    }

    /**
     * sets true if the patient was locked from outside the tab, false if it was
     * unlocked
     */
    public void setIsPatientLocked(boolean isPatientLocked) {
        this.isPatientLocked = isPatientLocked;
    }

    /**
     * notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        redraw = true;
        displaySampleClinical();
    }

    /**
     * shows the popup for patient lookup with no initial search specified
     */
    @UiHandler("fullSearchButton")
    protected void fullSearch(ClickEvent event) {
        if ( !isBusy)
            lookupPatient(null, false);
    }

    /**
     * shows the popup for patient lookup to search by the data in some of the
     * patient's fields
     */
    @UiHandler("nameSearchButton")
    protected void nameSearch(ClickEvent event) {
        if ( !isBusy)
            lookupPatient(manager.getSampleClinical().getPatient(), false);
    }

    /**
     * unlinks the current patient from the sample and unlocks the patient if
     * it's an existing one
     */
    @UiHandler("unlinkPatientButton")
    protected void unlinkPatient(ClickEvent event) {
        try {
            unlockPatient();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        manager.getSampleClinical().setPatientId(null);
        manager.getSampleClinical().setPatient(new PatientDO());
        setState(state);
        fireDataChange();
        patientLastName.setFocus(true);
    }

    /**
     * if the patient is an existing one and not locked then locks it and
     * enables the patient fields for editing
     */
    @UiHandler("editPatientButton")
    protected void editPatient(ClickEvent event) {
        PatientDO data;

        if (isPatientLocked || manager.getSampleClinical().getPatientId() == null) {
            patientLastName.setFocus(true);
            return;
        }

        try {
            data = PatientService.get().fetchForUpdate(manager.getSampleClinical().getPatientId());
            manager.getSampleClinical().setPatient(data);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        isPatientLocked = true;
        setState(state);
        fireDataChange();
        patientLastName.setFocus(true);
    }

    private void displaySampleClinical() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    /**
     * unlocks the patient if it's locked
     */
    private void unlockPatient() throws Exception {
        PatientDO data;

        if (isPatientLocked) {
            data = PatientService.get().abortUpdate(manager.getSampleClinical().getPatientId());
            manager.getSampleClinical().setPatient(data);
            isPatientLocked = false;
        }
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEditSample = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                                  .getStatusId()));
        canEditPatient = (getPatientId() == null || isPatientLocked);
    }

    /**
     * returns the patient's id or null if either the manager or the patient DO
     * is null
     */
    private Integer getPatientId() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getId();
    }

    /**
     * sets the patient's id
     */
    private void setPatientId(Integer id) {
        manager.getSampleClinical().getPatient().setId(id);
    }

    /**
     * returns the patient's last name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientLastName() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getLastName();
    }

    /**
     * sets the patient's last name
     */
    private void setPatientLastName(String name) {
        manager.getSampleClinical().getPatient().setLastName(name);
    }

    /**
     * returns the patient's middle name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientMiddleName() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getMiddleName();
    }

    /**
     * sets the patient's middle name
     */
    private void setPatientMiddleName(String name) {
        manager.getSampleClinical().getPatient().setMiddleName(name);
    }

    /**
     * returns the patient's first name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientFirstName() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getFirstName();
    }

    /**
     * sets the patient's first name
     */
    private void setPatientFirstName(String name) {
        manager.getSampleClinical().getPatient().setFirstName(name);
    }

    /**
     * returns the patient's gender or null if either the manager or the patient
     * DO is null
     */
    private Integer getPatientGenderId() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getGenderId();
    }

    /**
     * sets the patient's gender
     */
    private void setPatientGenderId(Integer genderId) {
        manager.getSampleClinical().getPatient().setGenderId(genderId);
    }

    /**
     * returns the patient's race or null if either the manager or the patient
     * DO is null
     */
    private Integer getPatientRaceId() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getRaceId();
    }

    /**
     * sets the patient's race
     */
    private void setPatientRaceId(Integer raceId) {
        manager.getSampleClinical().getPatient().setRaceId(raceId);
    }

    /**
     * returns the patient's ethnicity or null if either the manager or the
     * patient DO is null
     */
    private Integer getPatientEthnicityId() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getEthnicityId();
    }

    /**
     * sets the patient's ethnicity
     */
    private void setPatientEthnicityId(Integer ethnicityId) {
        manager.getSampleClinical().getPatient().setEthnicityId(ethnicityId);
    }

    /**
     * returns the patient's birth date or null if either the manager or the
     * patient DO is null
     */
    private Datetime getPatientBirthDate() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getBirthDate();
    }

    /**
     * sets the patient's birth date
     */
    private void setPatientBirthDate(Datetime date) {
        manager.getSampleClinical().getPatient().setBirthDate(date);
    }

    /**
     * returns the patient's national id or null if either the manager or the
     * patient DO is null
     */
    private String getPatientNationalId() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getNationalId();
    }

    /**
     * sets the patient's national id
     */
    private void setPatientNationalId(String nationalId) {
        manager.getSampleClinical().getPatient().setNationalId(nationalId);
    }

    /**
     * returns the patient's multiple unit (apt/suite) or null if either the
     * manager or the patient DO is null
     */
    private String getPatientAddressMultipleUnit() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getAddress().getMultipleUnit();
    }

    /**
     * sets the patient's multiple unit (apt/suite)
     */
    private void setPatientAddressMultipleUnit(String multipleUnit) {
        manager.getSampleClinical().getPatient().getAddress().setMultipleUnit(multipleUnit);
    }

    /**
     * returns the patient's street address or null if either the manager or the
     * patient DO is null
     */
    private String getPatientAddressStreetAddress() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getAddress().getStreetAddress();
    }

    /**
     * sets the patient's street address
     */
    private void setPatientAddressStreetAddress(String streetAddress) {
        manager.getSampleClinical().getPatient().getAddress().setStreetAddress(streetAddress);
    }

    /**
     * returns the patient's city or null if either the manager or the patient
     * DO is null
     */
    private String getPatientAddressCity() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getAddress().getCity();
    }

    /**
     * sets the patient's city
     */
    private void setPatientAddressCity(String city) {
        manager.getSampleClinical().getPatient().getAddress().setCity(city);
    }

    /**
     * returns the patient's state or null if either the manager or the patient
     * DO is null
     */
    private String getPatientAddressState() {
        if (manager == null || manager.getSampleClinical()== null)
            return null;
        return manager.getSampleClinical().getPatient().getAddress().getState();
    }

    /**
     * sets the patient's state
     */
    private void setPatientAddressState(String state) {
        manager.getSampleClinical().getPatient().getAddress().setState(state);
    }

    /**
     * returns the patient's zip code or null if either the manager or the
     * patient DO is null
     */
    private String getPatientAddressZipCode() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getPatient().getAddress().getZipCode();
    }

    /**
     * sets the patient's zip code
     */
    private void setPatientAddressZipCode(String zipCode) {
        manager.getSampleClinical().getPatient().getAddress().setZipCode(zipCode);
    }

    private Integer getProviderId() {
        if (manager == null)
            return null;
        return manager.getSampleClinical().getProviderId();
    }

    private void setProviderId(Integer providerId) {
        manager.getSampleClinical().setProviderId(providerId);
    }

    private void setProvider(ProviderDO data) {
        if (data == null || data.getId() == null)
            setProviderId(null);
        else
            setProviderId(data.getId());
        manager.getSampleClinical().setProvider(data);
        providerLastName.setValue(getProviderId(), getProviderLastName());
        providerFirstName.setValue(getProviderFirstName());
    }

    private String getProviderLastName() {
        if (manager == null || manager.getSampleClinical().getProvider() == null)
            return null;
        return manager.getSampleClinical().getProvider().getLastName();
    }

    private String getProviderFirstName() {
        if (manager == null || manager.getSampleClinical().getProvider() == null)
            return null;
        return manager.getSampleClinical().getProvider().getFirstName();
    }

    private String getProviderPhone() {
        if (manager == null || manager.getSampleClinical() == null)
            return null;
        return manager.getSampleClinical().getProviderPhone();
    }

    private void setProviderPhone(String providerPhone) {
        manager.getSampleClinical().setProviderPhone(providerPhone);
    }

    private void lookupPatient(PatientDO data, boolean dontShowIfSinglePatient) {
        if (patientLookup == null) {
            patientLookup = new PatientLookupUI() {
                public void select() {
                    setPatient(patientLookup.getSelectedPatient());
                    // setFocusToNext();
                    isBusy = false;
                }

                public void cancel() {
                    // setFocusToNext();
                    isBusy = false;
                }
            };
        }

        patientLookup.query(data, dontShowIfSinglePatient);
    }

    /**
     * sets the busy flag and the passed widget as the current one with focus;
     * looks up the patients matching the data entered in the patient's fields
     */
    private void patientQueryChanged(Widget queryWidget) {
        /*
         * look up patients only if the current patient is not locked
         */
        if ( !isPatientLocked) {
            isBusy = true;
            // patientQueryWidget = queryWidget;
            lookupPatient(manager.getSampleClinical().getPatient(), true);
        }
    }

    private void setPatient(PatientDO data) {
        if (data == null)
            return;

        manager.getSampleClinical().setPatientId(data.getId());
        manager.getSampleClinical().setPatient(data);

        if (getPatientLastName() != null)
            patientLastName.clearExceptions();

        if (getPatientAddressStreetAddress() != null)
            patientAddrStreetAddress.clearExceptions();

        evaluateEdit();
        setState(state);
        fireDataChange();
    }
}