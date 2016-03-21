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
import org.openelis.modules.secondDataEntry.client.SDWISTabUI.Operation;
import org.openelis.modules.secondDataEntry.client.field.AccessionNumber;
import org.openelis.modules.secondDataEntry.client.field.AuxData;
import org.openelis.modules.secondDataEntry.client.field.ClientReference;
import org.openelis.modules.secondDataEntry.client.field.CollectionDate;
import org.openelis.modules.secondDataEntry.client.field.CollectionTime;
import org.openelis.modules.secondDataEntry.client.field.PTAdditionalDomain;
import org.openelis.modules.secondDataEntry.client.field.PTDueDate;
import org.openelis.modules.secondDataEntry.client.field.PTPTProviderId;
import org.openelis.modules.secondDataEntry.client.field.PTSeries;
import org.openelis.modules.secondDataEntry.client.field.ReceivedById;
import org.openelis.modules.secondDataEntry.client.field.ReceivedDate;
import org.openelis.modules.secondDataEntry.client.field.SampleOrganization;
import org.openelis.modules.secondDataEntry.client.field.SampleProject;
import org.openelis.modules.secondDataEntry.client.field.SampleQAEvent;
import org.openelis.modules.secondDataEntry.client.field.OrderId;
import org.openelis.modules.secondDataEntry.client.field.VerificationField;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
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

public class PTTabUI extends VerificationScreen {
    @UiTemplate("PTTab.ui.xml")
    interface PTTabUiBinder extends UiBinder<Widget, PTTabUI> {
    };

    private static PTTabUiBinder                uiBinder = GWT.create(PTTabUiBinder.class);

    @UiField
    protected TableRowElement                    verPTAccessionNumber, verPTOrderId,
                    verPTCollectionDate, verPTCollectionTime, verPTReceivedDate,
                    verPTClientReference, verPTptProviderId, verPTSeries, verPTDueDate,
                    verPTAdditionalDomain, verPTReceivedById, verPTReportTo, verPTBillTo,
                    verPTProject, verPTSampleQAEvents, verPTAuxData;

    @UiField
    protected TextBox<Integer>                   accessionNumber, accessionNumber1, orderId,
                    orderId1;

    @UiField
    protected Image                              accessionNumberIM, accessionNumberIC, orderIdIM,
                    orderIdIC, collectionDateIM, collectionDateIC, collectionTimeIM,
                    collectionTimeIC, receivedDateIM, receivedDateIC, clientReferenceIM,
                    clientReferenceIC, ptPTProviderIdIM, ptPTProviderIdIC, ptSeriesIM, ptSeriesIC,
                    ptDueDateIM, ptDueDateIC, ptAdditionalDomainIM, ptAdditionalDomainIC,
                    receivedByIdIM, receivedByIdIC, reportToNameIM, reportToNameIC, billToNameIM,
                    billToNameIC, projectNameIM, projectNameIC;

    @UiField
    protected Calendar                           collectionDate, collectionDate1, collectionTime,
                    collectionTime1, receivedDate, receivedDate1, ptDueDate, ptDueDate1;

    @UiField
    protected TextBox<String>                    clientReference, clientReference1, ptSeries,
                    ptSeries1;

    @UiField
    protected Dropdown<Integer>                  ptPTProviderId, ptPTProviderId1;

    @UiField
    protected Dropdown<String>                   ptAdditionalDomain, ptAdditionalDomain1;

    @UiField
    protected AutoComplete                       receivedById, receivedById1, reportToName,
                    reportToName1, billToName, billToName1, projectName, projectName1;

    @UiField
    protected TextArea                           reportToDetails, reportToDetails1, billToDetails,
                    billToDetails1;

    @UiField
    protected Table                              sampleQATable, auxDataTable;

    protected SampleManager1                     manager;

    protected Screen                             parentScreen;

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

    public PTTabUI(Screen parentScreen) {
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
        for (DictionaryDO d : CategoryCache.getBySystemName("ver_pt_fields")) {
            if ("N".equals(d.getIsActive()))
                continue;
            switch (d.getSystemName()) {
                case "ver_pt_accession_number":
                    field = new AccessionNumber(this,
                                                verPTAccessionNumber,
                                                accessionNumber,
                                                accessionNumber1,
                                                accessionNumberIM,
                                                accessionNumberIC,
                                                ++i);
                    fields.put(SampleMeta.ACCESSION_NUMBER, field);
                    tabs.add(field);
                    break;
                case "ver_pt_order_id":
                    field = new OrderId(this,
                                        verPTOrderId,
                                        orderId,
                                        orderId1,
                                        orderIdIM,
                                        orderIdIC,
                                        ++i);
                    fields.put(SampleMeta.ORDER_ID, field);
                    tabs.add(field);
                    break;
                case "ver_pt_collection_date":
                    field = new CollectionDate(this,
                                               verPTCollectionDate,
                                               collectionDate,
                                               collectionDate1,
                                               collectionDateIM,
                                               collectionDateIC,
                                               ++i);
                    fields.put(SampleMeta.COLLECTION_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_pt_collection_time":
                    field = new CollectionTime(this,
                                               verPTCollectionTime,
                                               collectionTime,
                                               collectionTime1,
                                               collectionTimeIM,
                                               collectionTimeIC,
                                               ++i);
                    fields.put(SampleMeta.COLLECTION_TIME, field);
                    tabs.add(field);
                    break;
                case "ver_pt_received_date":
                    field = new ReceivedDate(this,
                                             verPTReceivedDate,
                                             receivedDate,
                                             receivedDate1,
                                             receivedDateIM,
                                             receivedDateIC,
                                             ++i);
                    fields.put(SampleMeta.RECEIVED_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_pt_client_reference":
                    field = new ClientReference(this,
                                                verPTClientReference,
                                                clientReference,
                                                clientReference1,
                                                clientReferenceIM,
                                                clientReferenceIC,
                                                ++i);
                    fields.put(SampleMeta.CLIENT_REFERENCE, field);
                    tabs.add(field);
                    break;
                case "ver_pt_pt_provider_id":
                    field = new PTPTProviderId(this,
                                               verPTptProviderId,
                                               ptPTProviderId,
                                               ptPTProviderId1,
                                               ptPTProviderIdIM,
                                               ptPTProviderIdIC,
                                               ++i);
                    fields.put(SampleMeta.PT_PT_PROVIDER_ID, field);
                    tabs.add(field);
                    break;
                case "ver_pt_series":
                    field = new PTSeries(this,
                                         verPTSeries,
                                         ptSeries,
                                         ptSeries1,
                                         ptSeriesIM,
                                         ptSeriesIC,
                                         ++i);
                    fields.put(SampleMeta.PT_SERIES, field);
                    tabs.add(field);
                    break;
                case "ver_pt_due_date":
                    field = new PTDueDate(this,
                                          verPTDueDate,
                                          ptDueDate,
                                          ptDueDate1,
                                          ptDueDateIM,
                                          ptDueDateIC,
                                          ++i);
                    fields.put(SampleMeta.PT_DUE_DATE, field);
                    tabs.add(field);
                    break;
                case "ver_pt_additional_domain":
                    field = new PTAdditionalDomain(this,
                                                   verPTAdditionalDomain,
                                                   ptAdditionalDomain,
                                                   ptAdditionalDomain1,
                                                   ptAdditionalDomainIM,
                                                   ptAdditionalDomainIC,
                                                   ++i);
                    fields.put(SampleMeta.PT_ADDITIONAL_DOMAIN, field);
                    tabs.add(field);
                    break;
                case "ver_pt_received_by_id":
                    field = new ReceivedById(this,
                                             verPTReceivedById,
                                             receivedById,
                                             receivedById1,
                                             receivedByIdIM,
                                             receivedByIdIC,
                                             ++i);
                    fields.put(SampleMeta.RECEIVED_BY_ID, field);
                    tabs.add(field);
                    break;
                case "ver_pt_report_to":
                    field = new SampleOrganization(this,
                                                   verPTReportTo,
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
                case "ver_pt_bill_to":
                    field = new SampleOrganization(this,
                                                   verPTBillTo,
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
                case "ver_pt_project":
                    field = new SampleProject(this,
                                              verPTProject,
                                              projectName,
                                              projectName1,
                                              projectNameIM,
                                              projectNameIC,
                                              ++i);
                    fields.put(SampleMeta.PROJECT_NAME, field);
                    tabs.add(field);
                    break;
                case "ver_pt_sample_qa_events":
                    field = new SampleQAEvent(this, verPTSampleQAEvents, sampleQATable, ++i);
                    fields.put(SAMPLE_QA_EVENTS, field);
                    tabs.add(field);
                    break;
                case "ver_pt_aux_data":
                    field = new AuxData(this, verPTAuxData, auxDataTable, ++i);
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
}