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

public class NeonatalTabUI extends Screen {
    @UiTemplate("NeonatalTab.ui.xml")
    interface NeonatalTabUIBinder extends UiBinder<Widget, NeonatalTabUI> {
    };

    private static NeonatalTabUIBinder uiBinder = GWT.create(NeonatalTabUIBinder.class);

    @UiField
    protected CheckBox                 neonatalPatientId, neonatalPatientLastName,
                    neonatalPatientFirstName, neonatalPatientBirthDate,
                    neonatalPatientAddrMultipleUnit, neonatalPatientAddrStreetAddress,
                    neonatalPatientAddrCity, neonatalPatientAddrState, neonatalPatientAddrZipCode,
                    neonatalPatientGenderId, neonatalPatientRaceId, neonatalPatientEthnicityId,
                    neonatalIsNicu, neonatalBirthOrder, neonatalGestationalAge, neonatalFeedingId,
                    neonatalWeight, neonatalIsTransfused, neonatalTransfusionDate,
                    neonatalIsRepeat, neonatalCollectionAge, neonatalIsCollectionValid,
                    neonatalFormNumber, neonatalNextOfKinId, neonatalNextOfKinLastName,
                    neonatalNextOfKinMiddleName, neonatalNextOfKinFirstName,
                    neonatalNextOfKinRelationId, neonatalNextOfKinBirthDate,
                    neonatalNextOfKinNationalId, neonatalNextOfKinAddrMultipleUnit,
                    neonatalNextOfKinAddrStreetAddress, neonatalNextOfKinAddrCity,
                    neonatalNextOfKinAddrState, neonatalNextOfKinAddrZipCode,
                    neonatalNextOfKinAddrHomePhone, neonatalNextOfKinGenderId,
                    neonatalNextOfKinRaceId, neonatalNextOfKinEthnicityId,
                    neonatalProviderLastName, neonatalProviderFirstName;

    @UiField
    protected Label<String>            fieldsDisabledLabel;

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    protected DataView1VO              data;

    protected ModulePermission         patientPermission;

    protected String                   domain;

    protected boolean                  canEditDomain, canEditPatient;

    public NeonatalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();

        patientPermission = UserCache.getPermission().getModule("dataview_patient");

        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(neonatalPatientId,
                         SampleWebMeta.NEO_PATIENT_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientId.setValue(getValue(SampleWebMeta.NEO_PATIENT_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientId.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                              canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientLastName
                                               : neonatalProviderFirstName;
                             }
                         });

        addScreenHandler(neonatalPatientLastName,
                         SampleWebMeta.NEO_PATIENT_LAST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientLastName.setValue(getValue(SampleWebMeta.NEO_PATIENT_LAST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_LAST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientLastName.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientFirstName : neonatalPatientId;
                             }
                         });

        addScreenHandler(neonatalPatientFirstName,
                         SampleWebMeta.NEO_PATIENT_FIRST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientFirstName.setValue(getValue(SampleWebMeta.NEO_PATIENT_FIRST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_FIRST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientFirstName.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientBirthDate
                                               : neonatalPatientLastName;
                             }
                         });

        addScreenHandler(neonatalPatientBirthDate,
                         SampleWebMeta.NEO_PATIENT_BIRTH_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientBirthDate.setValue(getValue(SampleWebMeta.NEO_PATIENT_BIRTH_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_BIRTH_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientBirthDate.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrMultipleUnit
                                               : neonatalPatientFirstName;
                             }
                         });

        addScreenHandler(neonatalPatientAddrMultipleUnit,
                         SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientAddrMultipleUnit.setValue(getValue(SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ADDR_MULTIPLE_UNIT,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrMultipleUnit.setEnabled(isState(DEFAULT) &&
                                                                            canEditDomain &&
                                                                            canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrStreetAddress
                                               : neonatalPatientBirthDate;
                             }
                         });

        addScreenHandler(neonatalPatientAddrStreetAddress,
                         SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientAddrStreetAddress.setValue(getValue(SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                             canEditDomain &&
                                                                             canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrCity
                                               : neonatalPatientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(neonatalPatientAddrCity,
                         SampleWebMeta.NEO_PATIENT_ADDR_CITY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientAddrCity.setValue(getValue(SampleWebMeta.NEO_PATIENT_ADDR_CITY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ADDR_CITY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrCity.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrState
                                               : neonatalPatientAddrStreetAddress;
                             }
                         });

        addScreenHandler(neonatalPatientAddrState,
                         SampleWebMeta.NEO_PATIENT_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientAddrState.setValue(getValue(SampleWebMeta.NEO_PATIENT_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ADDR_STATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrState.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain &&
                                                                     canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrZipCode
                                               : neonatalPatientAddrCity;
                             }
                         });

        addScreenHandler(neonatalPatientAddrZipCode,
                         SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientAddrZipCode.setValue(getValue(SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ADDR_ZIP_CODE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrZipCode.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientGenderId
                                               : neonatalPatientAddrState;
                             }
                         });

        addScreenHandler(neonatalPatientGenderId,
                         SampleWebMeta.NEO_PATIENT_GENDER_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientGenderId.setValue(getValue(SampleWebMeta.NEO_PATIENT_GENDER_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_GENDER_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientGenderId.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientRaceId
                                               : neonatalPatientAddrZipCode;
                             }
                         });

        addScreenHandler(neonatalPatientRaceId,
                         SampleWebMeta.NEO_PATIENT_RACE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientRaceId.setValue(getValue(SampleWebMeta.NEO_PATIENT_RACE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_RACE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientRaceId.setEnabled(isState(DEFAULT) &&
                                                                  canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientEthnicityId
                                               : neonatalPatientGenderId;
                             }
                         });

        addScreenHandler(neonatalPatientEthnicityId,
                         SampleWebMeta.NEO_PATIENT_ETHNICITY_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalPatientEthnicityId.setValue(getValue(SampleWebMeta.NEO_PATIENT_ETHNICITY_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PATIENT_ETHNICITY_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientEthnicityId.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsNicu : neonatalPatientRaceId;
                             }
                         });

        addScreenHandler(neonatalIsNicu, SampleWebMeta.NEO_IS_NICU, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                neonatalIsNicu.setValue(getValue(SampleWebMeta.NEO_IS_NICU));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.NEO_IS_NICU, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                neonatalIsNicu.setEnabled(isState(DEFAULT) && canEditDomain && canEditPatient);
            }

            public Widget onTab(boolean forward) {
                return forward ? neonatalBirthOrder : neonatalPatientEthnicityId;
            }
        });

        addScreenHandler(neonatalBirthOrder,
                         SampleWebMeta.NEO_BIRTH_ORDER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalBirthOrder.setValue(getValue(SampleWebMeta.NEO_BIRTH_ORDER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_BIRTH_ORDER, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalBirthOrder.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                               canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalGestationalAge : neonatalIsNicu;
                             }
                         });

        addScreenHandler(neonatalGestationalAge,
                         SampleWebMeta.NEO_GESTATIONAL_AGE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalGestationalAge.setValue(getValue(SampleWebMeta.NEO_GESTATIONAL_AGE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_GESTATIONAL_AGE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalGestationalAge.setEnabled(isState(DEFAULT) &&
                                                                   canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalFeedingId : neonatalBirthOrder;
                             }
                         });

        addScreenHandler(neonatalFeedingId,
                         SampleWebMeta.NEO_FEEDING_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalFeedingId.setValue(getValue(SampleWebMeta.NEO_FEEDING_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_FEEDING_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalFeedingId.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                              canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalWeight : neonatalGestationalAge;
                             }
                         });

        addScreenHandler(neonatalWeight, SampleWebMeta.NEO_WEIGHT, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                neonatalWeight.setValue(getValue(SampleWebMeta.NEO_WEIGHT));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.NEO_WEIGHT, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                neonatalWeight.setEnabled(isState(DEFAULT) && canEditDomain && canEditPatient);
            }

            public Widget onTab(boolean forward) {
                return forward ? neonatalIsTransfused : neonatalFeedingId;
            }
        });

        addScreenHandler(neonatalIsTransfused,
                         SampleWebMeta.NEO_IS_TRANSFUSED,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalIsTransfused.setValue(getValue(SampleWebMeta.NEO_IS_TRANSFUSED));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_IS_TRANSFUSED, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsTransfused.setEnabled(isState(DEFAULT) &&
                                                                 canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalTransfusionDate : neonatalWeight;
                             }
                         });

        addScreenHandler(neonatalTransfusionDate,
                         SampleWebMeta.NEO_TRANSFUSION_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalTransfusionDate.setValue(getValue(SampleWebMeta.NEO_TRANSFUSION_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_TRANSFUSION_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalTransfusionDate.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsRepeat : neonatalIsTransfused;
                             }
                         });

        addScreenHandler(neonatalIsRepeat,
                         SampleWebMeta.NEO_IS_REPEAT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalIsRepeat.setValue(getValue(SampleWebMeta.NEO_IS_REPEAT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_IS_REPEAT, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsRepeat.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                             canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalCollectionAge : neonatalTransfusionDate;
                             }
                         });

        addScreenHandler(neonatalCollectionAge,
                         SampleWebMeta.NEO_COLLECTION_AGE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalCollectionAge.setValue(getValue(SampleWebMeta.NEO_COLLECTION_AGE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_COLLECTION_AGE, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalCollectionAge.setEnabled(isState(DEFAULT) &&
                                                                  canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsCollectionValid : neonatalIsRepeat;
                             }
                         });

        addScreenHandler(neonatalIsCollectionValid,
                         SampleWebMeta.NEO_IS_COLLECTION_VALID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalIsCollectionValid.setValue(getValue(SampleWebMeta.NEO_IS_COLLECTION_VALID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_IS_COLLECTION_VALID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsCollectionValid.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain &&
                                                                      canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalFormNumber : neonatalCollectionAge;
                             }
                         });

        addScreenHandler(neonatalFormNumber,
                         SampleWebMeta.NEO_FORM_NUMBER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalFormNumber.setValue(getValue(SampleWebMeta.NEO_FORM_NUMBER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_FORM_NUMBER, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalFormNumber.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                               canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinId : neonatalIsCollectionValid;
                             }
                         });

        addScreenHandler(neonatalNextOfKinId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinId.setEnabled(isState(DEFAULT) && canEditDomain &&
                                                                canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinLastName : neonatalFormNumber;
                             }
                         });

        addScreenHandler(neonatalNextOfKinLastName,
                         SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinLastName.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_LAST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinLastName.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain &&
                                                                      canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinMiddleName : neonatalNextOfKinId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinMiddleName,
                         SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinMiddleName.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_MIDDLE_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinMiddleName.setEnabled(isState(DEFAULT) &&
                                                                        canEditDomain &&
                                                                        canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinFirstName
                                               : neonatalNextOfKinLastName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinFirstName,
                         SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinFirstName.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_FIRST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinFirstName.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinRelationId
                                               : neonatalNextOfKinMiddleName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinRelationId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinRelationId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_RELATION_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinRelationId.setEnabled(isState(DEFAULT) &&
                                                                        canEditDomain &&
                                                                        canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinBirthDate
                                               : neonatalNextOfKinFirstName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinBirthDate,
                         SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinBirthDate.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_BIRTH_DATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinBirthDate.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinNationalId
                                               : neonatalNextOfKinRelationId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinNationalId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinNationalId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_NATIONAL_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinNationalId.setEnabled(isState(DEFAULT) &&
                                                                        canEditDomain &&
                                                                        canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrMultipleUnit
                                               : neonatalNextOfKinBirthDate;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrMultipleUnit,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrMultipleUnit.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_MULTIPLE_UNIT,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrMultipleUnit.setEnabled(isState(DEFAULT) &&
                                                                              canEditDomain &&
                                                                              canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrStreetAddress
                                               : neonatalNextOfKinNationalId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrStreetAddress,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrStreetAddress.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                               canEditDomain &&
                                                                               canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrCity
                                               : neonatalNextOfKinAddrMultipleUnit;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrCity,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrCity.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_CITY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrCity.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain &&
                                                                      canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrState
                                               : neonatalNextOfKinAddrStreetAddress;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrState,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrState.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_STATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrState.setEnabled(isState(DEFAULT) &&
                                                                       canEditDomain &&
                                                                       canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrZipCode
                                               : neonatalNextOfKinAddrCity;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrZipCode,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrZipCode.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_ZIP_CODE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrZipCode.setEnabled(isState(DEFAULT) &&
                                                                         canEditDomain &&
                                                                         canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrHomePhone
                                               : neonatalNextOfKinAddrState;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrHomePhone,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinAddrHomePhone.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ADDR_HOME_PHONE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrHomePhone.setEnabled(isState(DEFAULT) &&
                                                                           canEditDomain &&
                                                                           canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinGenderId
                                               : neonatalNextOfKinAddrZipCode;
                             }
                         });

        addScreenHandler(neonatalNextOfKinGenderId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinGenderId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_GENDER_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinGenderId.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain &&
                                                                      canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinRaceId
                                               : neonatalNextOfKinAddrHomePhone;
                             }
                         });

        addScreenHandler(neonatalNextOfKinRaceId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinRaceId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_RACE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinRaceId.setEnabled(isState(DEFAULT) &&
                                                                    canEditDomain && canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinEthnicityId
                                               : neonatalNextOfKinGenderId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinEthnicityId,
                         SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalNextOfKinEthnicityId.setValue(getValue(SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_NEXT_OF_KIN_ETHNICITY_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinEthnicityId.setEnabled(isState(DEFAULT) &&
                                                                         canEditDomain &&
                                                                         canEditPatient);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalProviderLastName
                                               : neonatalNextOfKinRaceId;
                             }
                         });

        addScreenHandler(neonatalProviderLastName,
                         SampleWebMeta.NEO_PROVIDER_LAST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalProviderLastName.setValue(getValue(SampleWebMeta.NEO_PROVIDER_LAST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PROVIDER_LAST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalProviderLastName.setEnabled(isState(DEFAULT) &&
                                                                     canEditDomain);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalProviderFirstName
                                               : neonatalNextOfKinEthnicityId;
                             }
                         });

        addScreenHandler(neonatalProviderFirstName,
                         SampleWebMeta.NEO_PROVIDER_FIRST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 neonatalProviderFirstName.setValue(getValue(SampleWebMeta.NEO_PROVIDER_FIRST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.NEO_PROVIDER_FIRST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalProviderFirstName.setEnabled(isState(DEFAULT) &&
                                                                      canEditDomain);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientId : neonatalProviderLastName;
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

                if (Constants.domain().NEONATAL.equals(prevDom) && !canEditDomain) {
                    /*
                     * the previous domain was neonatal but is not anymore; if
                     * some columns from neonatal were selected before the
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
         * but the user doesn't have the permission to view patient or patient
         * related domain fields like "feeding"; this can happen if the screen
         * is loaded from a file that had those fields checked; this validation
         * is done only if neonatal domain is selected on Query tab, because
         * otherwise this tab's fields don't get added to the list of columns
         * sent to the back-end; provider fields don't require the permission to
         * be checked
         */
        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            w = entry.getValue().widget;
            if (w instanceof CheckBox) {
                cb = (CheckBox)w;
                if ("Y".equals(cb.getValue()) &&
                    !canEditPatient &&
                    (entry.getKey().startsWith("_neonatalPatient") ||
                     entry.getKey().startsWith("_neonatalNextOfKin") || entry.getKey()
                                                                             .startsWith("_sampleNeonatal"))) {
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
        canEditDomain = Constants.domain().NEONATAL.equals(domain);
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