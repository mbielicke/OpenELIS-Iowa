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
package org.openelis.modules.secondDataEntry.client;

import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample1.client.PatientChangeEvent;
import org.openelis.modules.sample1.client.RunScriptletEvent;
import org.openelis.modules.secondDataEntry.client.field.AccessionNumber;
import org.openelis.modules.secondDataEntry.client.field.AuxData;
import org.openelis.modules.secondDataEntry.client.field.ClientReference;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrCity;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrHomePhone;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrMultipleUnit;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrState;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrStreetAddress;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientAddrZipCode;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientBirthDate;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientEthnicityId;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientFirstName;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientGenderId;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientLastName;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientNationalId;
import org.openelis.modules.secondDataEntry.client.field.ClinPatientRaceId;
import org.openelis.modules.secondDataEntry.client.field.ClinProviderLastName;
import org.openelis.modules.secondDataEntry.client.field.ClinProviderPhone;
import org.openelis.modules.secondDataEntry.client.field.CollectionDate;
import org.openelis.modules.secondDataEntry.client.field.CollectionTime;
import org.openelis.modules.secondDataEntry.client.field.EOrderPaperOrderValidator;
import org.openelis.modules.secondDataEntry.client.field.ReceivedDate;
import org.openelis.modules.secondDataEntry.client.field.SampleOrganization;
import org.openelis.modules.secondDataEntry.client.field.SampleProject;
import org.openelis.modules.secondDataEntry.client.field.SampleQAEvent;
import org.openelis.modules.secondDataEntry.client.field.VerificationField;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ClinicalTabUI extends VerificationScreen {
    @UiTemplate("ClinicalTab.ui.xml")
    interface ClinicalTabUiBinder extends UiBinder<Widget, ClinicalTabUI> {
    };

    private static ClinicalTabUiBinder          uiBinder = GWT.create(ClinicalTabUiBinder.class);

    @UiField
    protected TableRowElement                    verClinAccessionNumber, verClinEOrderPOV,
                    verClinCollectionDate, verClinCollectionTime, verClinReceivedDate,
                    verClinClientReference, verClinPatLastName, verClinPatFirstName,
                    verClinPatBirthDate, verClinPatNationalId, verClinPatMultipleUnit,
                    verClinPatStreetAddress, verClinPatCity, verClinPatState, verClinPatZipCode,
                    verClinPatHomePhone, verClinPatGenderId, verClinPatRaceId,
                    verClinPatEthnicityId, verClinProvLastName, verClinProvPhone, verClinReportTo,
                    verClinBillTo, verClinProject, verClinSampleQAEvents, verClinAuxData;

    @UiField
    protected TextBox<Integer>                   accessionNumber, accessionNumber1;

    @UiField
    protected Image                              accessionNumberIM, accessionNumberIC, eorderPOVIM,
                    eorderPOVIC, collectionDateIM, collectionDateIC, collectionTimeIM,
                    collectionTimeIC, receivedDateIM, receivedDateIC, clientReferenceIM,
                    clientReferenceIC, clinPatientLastNameIM, clinPatientLastNameIC,
                    clinPatientFirstNameIM, clinPatientFirstNameIC, clinPatientBirthDateIM,
                    clinPatientBirthDateIC, clinPatientNationalIdIM, clinPatientNationalIdIC,
                    clinPatientAddrMultipleUnitIM, clinPatientAddrMultipleUnitIC,
                    clinPatientAddrStreetAddressIM, clinPatientAddrStreetAddressIC,
                    clinPatientAddrCityIM, clinPatientAddrCityIC, clinPatientAddrStateIM,
                    clinPatientAddrStateIC, clinPatientAddrZipCodeIM, clinPatientAddrZipCodeIC,
                    clinPatientAddrHomePhoneIM, clinPatientAddrHomePhoneIC, clinPatientGenderIdIM,
                    clinPatientGenderIdIC, clinPatientRaceIdIM, clinPatientRaceIdIC,
                    clinPatientEthnicityIdIM, clinPatientEthnicityIdIC, clinProviderLastNameIM,
                    clinProviderLastNameIC, clinProviderPhoneIM, clinProviderPhoneIC,
                    reportToNameIM, reportToNameIC, billToNameIM, billToNameIC, projectNameIM,
                    projectNameIC;

    @UiField
    protected Calendar                           collectionDate, collectionDate1, collectionTime,
                    collectionTime1, receivedDate, receivedDate1, clinPatientBirthDate,
                    clinPatientBirthDate1;

    @UiField
    protected TextBox<String>                    eorderPOV, eorderPOV1, clientReference,
                    clientReference1, clinPatientLastName, clinPatientLastName1,
                    clinPatientFirstName, clinPatientFirstName1, clinPatientNationalId,
                    clinPatientNationalId1, clinPatientAddrMultipleUnit,
                    clinPatientAddrMultipleUnit1, clinPatientAddrStreetAddress,
                    clinPatientAddrStreetAddress1, clinPatientAddrCity, clinPatientAddrCity1,
                    clinPatientAddrZipCode, clinPatientAddrZipCode1, clinPatientAddrHomePhone,
                    clinPatientAddrHomePhone1, clinProviderPhone, clinProviderPhone1;

    @UiField
    protected Button                             patientSearchButton;

    @UiField
    protected Dropdown<String>                   clinPatientAddrState, clinPatientAddrState1;

    @UiField
    protected Dropdown<Integer>                  clinPatientGenderId, clinPatientGenderId1,
                    clinPatientRaceId, clinPatientRaceId1, clinPatientEthnicityId,
                    clinPatientEthnicityId1;

    @UiField
    protected AutoComplete                       clinProviderLastName, clinProviderLastName1,
                    reportToName, reportToName1, billToName, billToName1, projectName,
                    projectName1;

    @UiField
    protected TextArea                           clinPatientId, clinProviderFirstName,
                    clinProviderFirstName1, reportToDetails, reportToDetails1, billToDetails,
                    billToDetails1;

    @UiField
    protected Table                              sampleQATable, auxDataTable;

    protected SampleManager1                     manager;

    protected Screen                             parentScreen, screen;

    protected EventBus                           parentBus;

    protected Focusable                          firstFocusWidget;

    protected HashMap<String, VerificationField> fields;

    protected boolean                            isInitialized;

    protected Validation                         validation;

    protected String                             logText;

    protected static final String                REPORT_TO = "report_to", BILL_TO = "bill_to",
                    AUX_DATA = "auxData", SAMPLE_QA_EVENTS = "sampleQAEvents";

    protected enum Operation {
        COPY_FROM_SAMPLE, COPY_TO_SAMPLE, VALUE_CHANGED
    }

    public ClinicalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        int i, prev, next;
        VerificationField field;
        ArrayList<VerificationField> tabs;

        screen = this;
        
        addStateChangeHandler(new StateChangeEvent.Handler() {
            ScheduledCommand cmd;

            /*
             * set the focus to the first editable widget; if the focus is not
             * set in this command, the style for focus doesn't show up even if
             * the widget has the cursor and is enabled
             */
            private void createCmd() {
                if (cmd == null) {
                    cmd = new ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (firstFocusWidget != null)
                                firstFocusWidget.setFocus(true);
                        }
                    };
                }
            }

            public void onStateChange(StateChangeEvent event) {
                if ( !isState(UPDATE))
                    return;

                createCmd();
                Scheduler.get().scheduleDeferred(cmd);
                validation = null;
            }
        });

        /*
         * get the category that defines which fields for this domain need to be
         * verified; make the rows visible that show those fields and initialize
         * the widgets in those rows; also keep track of the visible editable
         * widgets so that their tabbing order can be set
         */
        i = -1;
        fields = new HashMap<String, VerificationField>();
        tabs = new ArrayList<VerificationField>();
        for (DictionaryDO d : CategoryCache.getBySystemName("ver_clin_fields")) {
            if ("N".equals(d.getIsActive()))
                continue;
            switch (d.getSystemName()) {
                case "ver_clin_accession_number":
                    field = new AccessionNumber(this,
                                                verClinAccessionNumber,
                                                accessionNumber,
                                                accessionNumber1,
                                                accessionNumberIM,
                                                accessionNumberIC,
                                                ++i);
                    fields.put(SampleMeta.ACCESSION_NUMBER, field);
                    tabs.add(field);
                    break;
                case "ver_clin_eorder_pov":
                    field = new EOrderPaperOrderValidator(this,
                                                          verClinEOrderPOV,
                                                          eorderPOV,
                                                          eorderPOV1,
                                                          eorderPOVIM,
                                                          eorderPOVIC,
                                                          ++i);
                    fields.put(SampleMeta.EORDER_PAPER_ORDER_VALIDATOR, field);
                    tabs.add(field);
                    break;
                case "ver_clin_collection_date":
                    field = new CollectionDate(this,
                                               verClinCollectionDate,
                                               collectionDate,
                                               collectionDate1,
                                               collectionDateIM,
                                               collectionDateIC,
                                               ++i);
                    fields.put(SampleMeta.COLLECTION_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_collection_time":
                    field = new CollectionTime(this,
                                               verClinCollectionTime,
                                               collectionTime,
                                               collectionTime1,
                                               collectionTimeIM,
                                               collectionTimeIC,
                                               ++i);
                    fields.put(SampleMeta.COLLECTION_TIME, field);
                    tabs.add(field);
                    break;
                case "ver_clin_received_date":
                    field = new ReceivedDate(this,
                                             verClinReceivedDate,
                                             receivedDate,
                                             receivedDate1,
                                             receivedDateIM,
                                             receivedDateIC,
                                             ++i);
                    fields.put(SampleMeta.RECEIVED_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_client_reference":
                    field = new ClientReference(this,
                                                verClinClientReference,
                                                clientReference,
                                                clientReference1,
                                                clientReferenceIM,
                                                clientReferenceIC,
                                                ++i);
                    fields.put(SampleMeta.CLIENT_REFERENCE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_last_name":
                    field = new ClinPatientLastName(this,
                                                    verClinPatLastName,
                                                    clinPatientLastName,
                                                    clinPatientLastName1,
                                                    clinPatientLastNameIM,
                                                    clinPatientLastNameIC,
                                                    ++i,
                                                    patientSearchButton,
                                                    clinPatientId);
                    fields.put(SampleMeta.CLIN_PATIENT_LAST_NAME, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_first_name":
                    field = new ClinPatientFirstName(this,
                                                     verClinPatFirstName,
                                                     clinPatientFirstName,
                                                     clinPatientFirstName1,
                                                     clinPatientFirstNameIM,
                                                     clinPatientFirstNameIC,
                                                     ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_FIRST_NAME, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_birth_date":
                    field = new ClinPatientBirthDate(this,
                                                     verClinPatBirthDate,
                                                     clinPatientBirthDate,
                                                     clinPatientBirthDate1,
                                                     clinPatientBirthDateIM,
                                                     clinPatientBirthDateIC,
                                                     ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_BIRTH_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_national_id":
                    field = new ClinPatientNationalId(this,
                                                      verClinPatNationalId,
                                                      clinPatientNationalId,
                                                      clinPatientNationalId1,
                                                      clinPatientNationalIdIM,
                                                      clinPatientNationalIdIC,
                                                      ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_NATIONAL_ID, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_multiple_unit":
                    field = new ClinPatientAddrMultipleUnit(this,
                                                            verClinPatMultipleUnit,
                                                            clinPatientAddrMultipleUnit,
                                                            clinPatientAddrMultipleUnit1,
                                                            clinPatientAddrMultipleUnitIM,
                                                            clinPatientAddrMultipleUnitIC,
                                                            ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_street_address":
                    field = new ClinPatientAddrStreetAddress(this,
                                                             verClinPatStreetAddress,
                                                             clinPatientAddrStreetAddress,
                                                             clinPatientAddrStreetAddress1,
                                                             clinPatientAddrStreetAddressIM,
                                                             clinPatientAddrStreetAddressIC,
                                                             ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_city":
                    field = new ClinPatientAddrCity(this,
                                                    verClinPatCity,
                                                    clinPatientAddrCity,
                                                    clinPatientAddrCity1,
                                                    clinPatientAddrCityIM,
                                                    clinPatientAddrCityIC,
                                                    ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_CITY, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_state":
                    field = new ClinPatientAddrState(this,
                                                     verClinPatState,
                                                     clinPatientAddrState,
                                                     clinPatientAddrState1,
                                                     clinPatientAddrStateIM,
                                                     clinPatientAddrStateIC,
                                                     ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_STATE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_zip_code":
                    field = new ClinPatientAddrZipCode(this,
                                                       verClinPatZipCode,
                                                       clinPatientAddrZipCode,
                                                       clinPatientAddrZipCode1,
                                                       clinPatientAddrZipCodeIM,
                                                       clinPatientAddrZipCodeIC,
                                                       ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_ZIP_CODE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_home_phone":
                    field = new ClinPatientAddrHomePhone(this,
                                                         verClinPatHomePhone,
                                                         clinPatientAddrHomePhone,
                                                         clinPatientAddrHomePhone1,
                                                         clinPatientAddrHomePhoneIM,
                                                         clinPatientAddrHomePhoneIC,
                                                         ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ADDR_HOME_PHONE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_gender_id":
                    field = new ClinPatientGenderId(this,
                                                    verClinPatGenderId,
                                                    clinPatientGenderId,
                                                    clinPatientGenderId1,
                                                    clinPatientGenderIdIM,
                                                    clinPatientGenderIdIC,
                                                    ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_GENDER_ID, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_race_id":
                    field = new ClinPatientRaceId(this,
                                                  verClinPatRaceId,
                                                  clinPatientRaceId,
                                                  clinPatientRaceId1,
                                                  clinPatientRaceIdIM,
                                                  clinPatientRaceIdIC,
                                                  ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_RACE_ID, field);
                    tabs.add(field);
                    break;
                case "ver_clin_pat_ethnicity_id":
                    field = new ClinPatientEthnicityId(this,
                                                       verClinPatEthnicityId,
                                                       clinPatientEthnicityId,
                                                       clinPatientEthnicityId1,
                                                       clinPatientEthnicityIdIM,
                                                       clinPatientEthnicityIdIC,
                                                       ++i);
                    fields.put(SampleMeta.CLIN_PATIENT_ETHNICITY_ID, field);
                    tabs.add(field);
                    break;
                case "ver_clin_prov_last_name":
                    field = new ClinProviderLastName(this,
                                                     verClinProvLastName,
                                                     clinProviderLastName,
                                                     clinProviderLastName1,
                                                     clinProviderLastNameIM,
                                                     clinProviderLastNameIC,
                                                     ++i,
                                                     clinProviderFirstName,
                                                     clinProviderFirstName1);
                    fields.put(SampleMeta.CLIN_PROVIDER_LAST_NAME, field);
                    tabs.add(field);
                    break;
                case "ver_clin_prov_phone":
                    field = new ClinProviderPhone(this,
                                                  verClinProvPhone,
                                                  clinProviderPhone,
                                                  clinProviderPhone1,
                                                  clinProviderPhoneIM,
                                                  clinProviderPhoneIC,
                                                  ++i);
                    fields.put(SampleMeta.CLIN_PROVIDER_PHONE, field);
                    tabs.add(field);
                    break;
                case "ver_clin_report_to":
                    field = new SampleOrganization(this,
                                                   verClinReportTo,
                                                   reportToName,
                                                   reportToName1,
                                                   reportToNameIM,
                                                   reportToNameIC,
                                                   ++i,
                                                   Constants.dictionary().ORG_REPORT_TO,
                                                   reportToDetails,
                                                   reportToDetails1);
                    fields.put(REPORT_TO, field);
                    tabs.add(field);
                    break;
                case "ver_clin_bill_to":
                    field = new SampleOrganization(this,
                                                   verClinBillTo,
                                                   billToName,
                                                   billToName1,
                                                   billToNameIM,
                                                   billToNameIC,
                                                   ++i,
                                                   Constants.dictionary().ORG_BILL_TO,
                                                   billToDetails,
                                                   billToDetails1);
                    fields.put(BILL_TO, field);
                    tabs.add(field);
                    break;
                case "ver_clin_project":
                    field = new SampleProject(this,
                                              verClinProject,
                                              projectName,
                                              projectName1,
                                              projectNameIM,
                                              projectNameIC,
                                              ++i);
                    fields.put(SampleMeta.PROJECT_NAME, field);
                    tabs.add(field);
                    break;
                case "ver_clin_sample_qa_events":
                    field = new SampleQAEvent(this, verClinSampleQAEvents, sampleQATable, ++i);
                    fields.put(SAMPLE_QA_EVENTS, field);
                    tabs.add(field);
                    break;
                case "ver_clin_aux_data":
                    field = new AuxData(this, verClinAuxData, auxDataTable, ++i);
                    fields.put(AUX_DATA, field);
                    tabs.add(field);
                    break;
            }
        }

        /*
         * set the tabbing order for the editable widgets
         */
        for (i = 0; i < tabs.size(); i++ ) {
            field = tabs.get(i);
            prev = i - 1;
            next = i + 1;
            if (i == 0) {
                prev = tabs.size() - 1;
                firstFocusWidget = (Focusable)field.getEditableWidget();
            } else if (i == tabs.size() - 1) {
                next = 0;
            }
            field.setPrevTabWidget((Widget)tabs.get(prev).getEditableWidget());
            field.setNextTabWidget((Widget)tabs.get(next).getEditableWidget());
        }
        tabs = null;

        /*
         * add handlers for the shortcuts "Ctrl+1", "Ctrl+2" and "Ctrl+3";
         * handlers are added for the number keys above the letter keys, as well
         * the ones on the numeric pad
         */
        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.COPY_FROM_SAMPLE);
            }
        }, '1', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.COPY_FROM_SAMPLE);
            }
        }, (char)97, CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.COPY_TO_SAMPLE);
            }
        }, '2', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.COPY_TO_SAMPLE);
            }
        }, (char)98, CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.VALUE_CHANGED);
            }
        }, '3', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                execute(Operation.VALUE_CHANGED);
            }
        }, (char)99, CTRL);
        
        bus.addHandler(PatientChangeEvent.getType(), new PatientChangeEvent.Handler() {
            @Override
            public void onPatientChange(PatientChangeEvent event) {
                fireScriptletEvent(null, SampleMeta.getClinicalPatientId(), Action_Before.PATIENT);
            }
        });
        
        bus.addHandler(RunScriptletEvent.getType(), new RunScriptletEvent.Handler() {
            @Override
            public void onRunScriptlet(RunScriptletEvent event) {
                if (screen != event.getSource())
                    fireScriptletEvent(event.getUid(), event.getChanged(), event.getOperation());
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    /**
     * Overridden because we want to delay adding the handlers until we know
     * which widgets are shown
     */
    public void setState(State state) {
        if ( !isInitialized && manager != null) {
            initialize();
            isInitialized = true;
        }

        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        fireDataChange();
    }

    public Validation validate() {
        boolean isVerified;
        String fieldText;

        validation = super.validate();

        if ( !isState(UPDATE))
            return validation;
        /*
         * verify all fields; show an error if some fields are not verified
         */
        isVerified = true;
        logText = null;
        for (VerificationField field : fields.values()) {
            if ( !field.getIsVerified()) {
                field.verify();
                if (isVerified)
                    isVerified = field.getIsVerified();
            }
            /*
             * create the text for the event log for this sample; the text would
             * show which operation e.g. copy from or to the sample was
             * performed on which field
             */
            fieldText = field.getLogText();
            if (fieldText != null) {
                if (logText == null)
                    logText = fieldText;
                else
                    logText = DataBaseUtil.concatWithSeparator(logText, ", ", fieldText);
            }
        }

        if ( !isVerified) {
            validation.setStatus(Validation.Status.ERRORS);
            validation.addException(new Exception(Messages.get().secondDataEntry_verifyAllFields()));
        }

        return validation;
    }

    public SampleManager1 getManager() {
        return manager;
    }

    public WindowInt getWindow() {
        return window;
    }

    public CacheProvider getCacheProvider() {
        return (CacheProvider)parentScreen;
    }

    public Validation getValidation() {
        return validation;
    }

    /**
     * Returns the text for the event log for this sample; the text shows which
     * operation, e.g. copy from or to the sample, was performed on which field
     */
    public String getLogText() {
        return logText;
    }

    /**
     * If a widget has focus, copies the value of a specific field from the
     * manager to the widget if operation is COPY_FROM_SAMPLE (first data entry)
     * or vice-versa if it is COPY_TO_SAMPLE (second data entry); if operation
     * is VALUE_CHANGED, forces the widget to act as if its value was changed by
     * the user so that the value can be verified and the value in the manager
     * can be shown in the non editable widget
     */
    private void execute(Operation operation) {
        VerificationField field;

        if (!isState(UPDATE))
            return;
        
        field = null;
        if (auxDataTable.isEditing()) {
            /*
             * the "focused" widget is not set if a table has focus because the
             * focus gets set to the editable cell's widget and not the table
             */
            field = fields.get(AUX_DATA);
        } else {
            for (VerificationField f : fields.values()) {
                if (focused == f.getEditableWidget()) {
                    field = f;
                    break;
                }
            }
        }

        if (field != null) {
            switch (operation) {
                case COPY_FROM_SAMPLE:
                    field.copyFromSample();
                    break;
                case COPY_TO_SAMPLE:
                    field.copyToSample();
                    break;
                case VALUE_CHANGED:
                    field.valueChanged();
                    break;
            }
        }
    }
    
    /**
     * Fires an event, for the passed uid and operation, to the parent screen to
     * inform it that some field was changed and scriptlets may need to be run
     */
    protected void fireScriptletEvent(String uid, String changed, Action_Before operation) {
        parentBus.fireEventFromSource(new RunScriptletEvent(uid, changed, operation), screen);
    }
}