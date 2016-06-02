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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.ui.screen.State.*;

import java.util.Map;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class ClinicalTabUI extends Screen {
    @UiTemplate("ClinicalTab.ui.xml")
    interface ClinicalTabUIBinder extends UiBinder<Widget, ClinicalTabUI> {
    };

    private static ClinicalTabUIBinder uiBinder = GWT.create(ClinicalTabUIBinder.class);

    @UiField
    protected CheckBox                 clinicalPatientId, clinicalPatientLastName,
                    clinicalPatientFirstName, clinicalPatientBirthDate, clinicalPatientNationalId,
                    clinicalPatientAddrMultipleUnit, clinicalPatientAddrStreetAddress,
                    clinicalPatientAddrCity, clinicalPatientAddrState, clinicalPatientAddrZipCode,
                    clinicalPatientAddrHomePhone, clinicalPatientGenderId, clinicalPatientRaceId,
                    clinicalPatientEthnicityId, clinicalProviderLastName,
                    clinicalProviderFirstName, clinicalProviderPhone;

    @UiField
    protected Label<String>            fieldsDisabledLabel;

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    protected DataView1VO              data;

    protected ModulePermission         patientPermission;

    protected String                   domain;

    protected boolean                  canEditDomain, canEditPatient;

    public ClinicalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();

        patientPermission = UserCache.getPermission().getModule("dataview_patient");

        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(clinicalPatientId,
                         SampleWebMeta.CLIN_PATIENT_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientId.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientId.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                              canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientLastName : clinicalProviderPhone;
                             }
                         });

        addScreenHandler(clinicalPatientLastName,
                         SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientLastName.setValue(getValue(SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_LAST_NAME_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientLastName.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientFirstName : clinicalPatientId;
                             }
                         });

        addScreenHandler(clinicalPatientFirstName,
                         SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientFirstName.setValue(getValue(SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_FIRST_NAME_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientFirstName.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientBirthDate
                                               : clinicalPatientLastName;
                             }
                         });

        addScreenHandler(clinicalPatientBirthDate,
                         SampleWebMeta.CLIN_PATIENT_BIRTH_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientBirthDate.setValue(getValue(SampleWebMeta.CLIN_PATIENT_BIRTH_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_BIRTH_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientBirthDate.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientNationalId
                                               : clinicalPatientFirstName;
                             }
                         });

        addScreenHandler(clinicalPatientNationalId,
                         SampleWebMeta.CLIN_PATIENT_NATIONAL_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientNationalId.setValue(getValue(SampleWebMeta.CLIN_PATIENT_NATIONAL_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_NATIONAL_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientNationalId.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain &&
                                                                      canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrMultipleUnit
                                               : clinicalPatientBirthDate;
                             }
                         });

        addScreenHandler(clinicalPatientAddrMultipleUnit,
                         SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrMultipleUnit.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_MULTIPLE_UNIT,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrMultipleUnit.setEnabled(isState(DEFAULT) &&
                                                                            canEditDomain &&
                                                                            canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrStreetAddress
                                               : clinicalPatientNationalId;
                             }
                         });

        addScreenHandler(clinicalPatientAddrStreetAddress,
                         SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrStreetAddress.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                             canEditDomain &&
                                                                             canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrCity
                                               : clinicalPatientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(clinicalPatientAddrCity,
                         SampleWebMeta.CLIN_PATIENT_ADDR_CITY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrCity.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_CITY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_CITY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrCity.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrState
                                               : clinicalPatientAddrStreetAddress;
                             }
                         });

        addScreenHandler(clinicalPatientAddrState,
                         SampleWebMeta.CLIN_PATIENT_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrState.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_STATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrState.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrZipCode
                                               : clinicalPatientAddrCity;
                             }
                         });

        addScreenHandler(clinicalPatientAddrZipCode,
                         SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrZipCode.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_ZIP_CODE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrZipCode.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrHomePhone
                                               : clinicalPatientAddrState;
                             }
                         });

        addScreenHandler(clinicalPatientAddrHomePhone,
                         SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientAddrHomePhone.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ADDR_HOME_PHONE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrHomePhone.setEnabled(isState(DEFAULT) &&
                                                                         canEditDomain &&
                                                                         canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientGenderId
                                               : clinicalPatientAddrZipCode;
                             }
                         });

        addScreenHandler(clinicalPatientGenderId,
                         SampleWebMeta.CLIN_PATIENT_GENDER_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientGenderId.setValue(getValue(SampleWebMeta.CLIN_PATIENT_GENDER_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_GENDER_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientGenderId.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientRaceId
                                               : clinicalPatientAddrHomePhone;
                             }
                         });

        addScreenHandler(clinicalPatientRaceId,
                         SampleWebMeta.CLIN_PATIENT_RACE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientRaceId.setValue(getValue(SampleWebMeta.CLIN_PATIENT_RACE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_RACE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientRaceId.setEnabled(isState(DEFAULT) &&
                                                                  canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientEthnicityId
                                               : clinicalPatientGenderId;
                             }
                         });

        addScreenHandler(clinicalPatientEthnicityId,
                         SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalPatientEthnicityId.setValue(getValue(SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PATIENT_ETHNICITY_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientEthnicityId.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderLastName : clinicalPatientRaceId;
                             }
                         });

        addScreenHandler(clinicalProviderLastName,
                         SampleWebMeta.CLIN_PROVIDER_LAST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalProviderLastName.setValue(getValue(SampleWebMeta.CLIN_PROVIDER_LAST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PROVIDER_LAST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderLastName.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderFirstName
                                               : clinicalPatientEthnicityId;
                             }
                         });

        addScreenHandler(clinicalProviderFirstName,
                         SampleWebMeta.CLIN_PROVIDER_FIRST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalProviderFirstName.setValue(getValue(SampleWebMeta.CLIN_PROVIDER_FIRST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PROVIDER_FIRST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderFirstName.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderPhone : clinicalProviderLastName;
                             }
                         });

        addScreenHandler(clinicalProviderPhone,
                         SampleWebMeta.CLIN_PROVIDER_PHONE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clinicalProviderPhone.setValue(getValue(SampleWebMeta.CLIN_PROVIDER_PHONE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.CLIN_PROVIDER_PHONE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderPhone.setEnabled(isState(DEFAULT) && canEditDomain);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientId : clinicalProviderFirstName;
                             }
                         });

        addScreenHandler(fieldsDisabledLabel, "fieldsDisabledLabel", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                String dtxt, ptxt;

                dtxt = canEditDomain ? null : Messages.get().dataView_tabFieldsDisabled();
                ptxt = canEditPatient ? Messages.get().dataView_hipaaNotification()
                                     : Messages.get().dataView_noPermToViewPatientException();

                fieldsDisabledLabel.setText(DataBaseUtil.concatWithSeparator(dtxt, " ", ptxt));
            }
        });

        parentBus.addHandler(DomainChangeEvent.getType(), new DomainChangeEvent.Handler() {
            @Override
            public void onDomainChange(DomainChangeEvent event) {
                String prevDom;
                Widget w;
                CheckBox cb;

                prevDom = domain;
                /*
                 * the widgets in this tab need to be enabled or disabled based
                 * on the current domain
                 */
                domain = event.getDomain();
                setState(state);

                if (Constants.domain().CLINICAL.equals(prevDom) && !canEditDomain) {
                    /*
                     * the previous domain was clinical but is not anymore; if
                     * some columns from clinical were selected before the
                     * domain was changed, remove them from the ones shown in
                     * the report
                     */
                    for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
                        w = entry.getValue().widget;
                        if (w instanceof CheckBox) {
                            cb = (CheckBox)w;
                            if ("Y".equals(cb.getValue())) {
                                cb.setValue("N");
                                addRemoveColumn(entry.getKey(), "N");
                            }
                        }
                    }
                }
            }
        });
    }

    public void setData(DataView1VO data) {
        this.data = data;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        fireDataChange();
    }

    public Validation validate() {
        Widget w;
        CheckBox cb;
        Validation validation;

        validation = super.validate();

        if ( !canEditDomain)
            return validation;

        /*
         * show an error if the checkbox for a patient field has been checked
         * but the user doesn't have the permission to view patient fields; this
         * can happen if the screen is loaded from a file that had those fields
         * checked; this validation is done only if clinical domain is selected
         * on Query tab, because otherwise this tab's fields don't get added to
         * the list sent to the back-end; provider fields don't require the
         * permission to be checked
         */
        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            w = entry.getValue().widget;
            if (w instanceof CheckBox) {
                cb = (CheckBox)w;
                if ("Y".equals(cb.getValue()) && !canEditPatient &&
                    entry.getKey().startsWith("_clinicalPatient")) {
                    validation.addException(new Exception(Messages.get()
                                                                  .dataView_noPermToViewPatientException()));
                    validation.setStatus(Validation.Status.ERRORS);
                    break;
                }
            }
        }

        return validation;
    }

    private void evaluateEdit() {
        canEditDomain = Constants.domain().CLINICAL.equals(domain);
        canEditPatient = patientPermission != null;
    }

    /**
     * Returns the value indicating whether the passed column is selected or not
     * to be shown in the report; if the column is selected, the value is "Y";
     * otherwise it's "N"
     */
    private String getValue(String column) {
        if (data == null || data.getColumns() == null)
            return "N";
        return data.getColumns().contains(column) ? "Y" : "N";
    }

    /**
     * Fires an event to notify column order tab that the passed column needs to
     * be added to or removed from the list of columns shown in the report; the
     * column is added if the passed value is "Y"; it's removed otherwise
     */
    private void addRemoveColumn(String column, String value) {
        ColumnEvent.Action action;

        action = "Y".equals(value) ? ColumnEvent.Action.ADD : ColumnEvent.Action.REMOVE;
        parentBus.fireEvent(new ColumnEvent(column, action));
    }
}