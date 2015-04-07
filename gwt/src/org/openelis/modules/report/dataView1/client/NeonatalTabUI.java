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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Map;

import org.openelis.domain.Constants;
import org.openelis.domain.DataViewVO1;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.CheckBox;

import com.google.gwt.core.client.GWT;
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
                    neonatalPatientFirstName, neonatalPatientBirthDate, neonatalPatientBirthTime,
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

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    protected DataViewVO1              data;

    protected String                   domain;

    protected boolean                  canEdit;

    public NeonatalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(neonatalPatientId,
                         SampleWebMeta.getNeonatalPatientId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientId.setValue(getValue(SampleWebMeta.getNeonatalPatientId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientLastName
                                               : neonatalProviderFirstName;
                             }
                         });

        addScreenHandler(neonatalPatientLastName,
                         SampleWebMeta.getNeonatalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientLastName.setValue(getValue(SampleWebMeta.getNeonatalPatientLastName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientFirstName : neonatalPatientId;
                             }
                         });

        addScreenHandler(neonatalPatientFirstName,
                         SampleWebMeta.getNeonatalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientFirstName.setValue(getValue(SampleWebMeta.getNeonatalPatientFirstName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientBirthDate
                                               : neonatalPatientLastName;
                             }
                         });

        addScreenHandler(neonatalPatientBirthDate,
                         SampleWebMeta.getNeonatalPatientBirthDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientBirthDate.setValue(getValue(SampleWebMeta.getNeonatalPatientBirthDate()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientBirthDate.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientBirthTime
                                               : neonatalPatientFirstName;
                             }
                         });

        addScreenHandler(neonatalPatientBirthTime,
                         SampleWebMeta.getNeonatalPatientBirthTime(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientBirthTime.setValue(getValue(SampleWebMeta.getNeonatalPatientBirthTime()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientBirthTime.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrMultipleUnit
                                               : neonatalPatientBirthDate;
                             }
                         });

        addScreenHandler(neonatalPatientAddrMultipleUnit,
                         SampleWebMeta.getNeonatalPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientAddrMultipleUnit.setValue(getValue(SampleWebMeta.getNeonatalPatientAddrMultipleUnit()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrMultipleUnit.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrStreetAddress
                                               : neonatalPatientBirthTime;
                             }
                         });

        addScreenHandler(neonatalPatientAddrStreetAddress,
                         SampleWebMeta.getNeonatalPatientAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientAddrStreetAddress.setValue(getValue(SampleWebMeta.getNeonatalPatientAddrStreetAddress()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrStreetAddress.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrCity
                                               : neonatalPatientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(neonatalPatientAddrCity,
                         SampleWebMeta.getNeonatalPatientAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientAddrCity.setValue(getValue(SampleWebMeta.getNeonatalPatientAddrCity()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrState
                                               : neonatalPatientAddrStreetAddress;
                             }
                         });

        addScreenHandler(neonatalPatientAddrState,
                         SampleWebMeta.getNeonatalPatientAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientAddrState.setValue(getValue(SampleWebMeta.getNeonatalPatientAddrState()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientAddrZipCode
                                               : neonatalPatientAddrCity;
                             }
                         });

        addScreenHandler(neonatalPatientAddrZipCode,
                         SampleWebMeta.getNeonatalPatientAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientAddrZipCode.setValue(getValue(SampleWebMeta.getNeonatalPatientAddrZipCode()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientGenderId
                                               : neonatalPatientAddrState;
                             }
                         });

        addScreenHandler(neonatalPatientGenderId,
                         SampleWebMeta.getNeonatalPatientGenderId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientGenderId.setValue(getValue(SampleWebMeta.getNeonatalPatientGenderId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientGenderId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientRaceId
                                               : neonatalPatientAddrZipCode;
                             }
                         });

        addScreenHandler(neonatalPatientRaceId,
                         SampleWebMeta.getNeonatalPatientRaceId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientRaceId.setValue(getValue(SampleWebMeta.getNeonatalPatientRaceId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientRaceId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientEthnicityId
                                               : neonatalPatientGenderId;
                             }
                         });

        addScreenHandler(neonatalPatientEthnicityId,
                         SampleWebMeta.getNeonatalPatientEthnicityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalPatientEthnicityId.setValue(getValue(SampleWebMeta.getNeonatalPatientEthnicityId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalPatientEthnicityId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsNicu : neonatalPatientRaceId;
                             }
                         });

        addScreenHandler(neonatalIsNicu,
                         SampleWebMeta.getNeonatalIsNicu(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalIsNicu.setValue(getValue(SampleWebMeta.getNeonatalIsNicu()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsNicu.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalBirthOrder : neonatalPatientEthnicityId;
                             }
                         });

        addScreenHandler(neonatalBirthOrder,
                         SampleWebMeta.getNeonatalBirthOrder(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalBirthOrder.setValue(getValue(SampleWebMeta.getNeonatalBirthOrder()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalBirthOrder.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalGestationalAge : neonatalIsNicu;
                             }
                         });

        addScreenHandler(neonatalGestationalAge,
                         SampleWebMeta.getNeonatalGestationalAge(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalGestationalAge.setValue(getValue(SampleWebMeta.getNeonatalGestationalAge()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalGestationalAge.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalFeedingId : neonatalBirthOrder;
                             }
                         });

        addScreenHandler(neonatalFeedingId,
                         SampleWebMeta.getNeonatalFeedingId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalFeedingId.setValue(getValue(SampleWebMeta.getNeonatalFeedingId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalFeedingId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalWeight : neonatalGestationalAge;
                             }
                         });
        

        addScreenHandler(neonatalWeight,
                         SampleWebMeta.getNeonatalWeight(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalWeight.setValue(getValue(SampleWebMeta.getNeonatalWeight()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalWeight.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsTransfused : neonatalFeedingId;
                             }
                         });

        addScreenHandler(neonatalIsTransfused,
                         SampleWebMeta.getNeonatalIsTransfused(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalIsTransfused.setValue(getValue(SampleWebMeta.getNeonatalIsTransfused()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsTransfused.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalTransfusionDate : neonatalWeight;
                             }
                         });

        addScreenHandler(neonatalTransfusionDate,
                         SampleWebMeta.getNeonatalTransfusionDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalTransfusionDate.setValue(getValue(SampleWebMeta.getNeonatalTransfusionDate()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalTransfusionDate.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsRepeat : neonatalIsTransfused;
                             }
                         });

        addScreenHandler(neonatalIsRepeat,
                         SampleWebMeta.getNeonatalIsRepeat(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalIsRepeat.setValue(getValue(SampleWebMeta.getNeonatalIsRepeat()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsRepeat.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalCollectionAge : neonatalTransfusionDate;
                             }
                         });

        addScreenHandler(neonatalCollectionAge,
                         SampleWebMeta.getNeonatalCollectionAge(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalCollectionAge.setValue(getValue(SampleWebMeta.getNeonatalCollectionAge()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalCollectionAge.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalIsCollectionValid : neonatalIsRepeat;
                             }
                         });

        addScreenHandler(neonatalIsCollectionValid,
                         SampleWebMeta.getNeonatalIsCollectionValid(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalIsCollectionValid.setValue(getValue(SampleWebMeta.getNeonatalIsCollectionValid()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalIsCollectionValid.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalFormNumber : neonatalCollectionAge;
                             }
                         });

        addScreenHandler(neonatalFormNumber,
                         SampleWebMeta.getNeonatalFormNumber(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalFormNumber.setValue(getValue(SampleWebMeta.getNeonatalFormNumber()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalFormNumber.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinId : neonatalIsCollectionValid;
                             }
                         });

        addScreenHandler(neonatalNextOfKinId,
                         SampleWebMeta.getNeonatalNextOfKinId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinLastName : neonatalFormNumber;
                             }
                         });

        addScreenHandler(neonatalNextOfKinLastName,
                         SampleWebMeta.getNeonatalNextOfKinLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinLastName.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinLastName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinMiddleName : neonatalNextOfKinId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinMiddleName,
                         SampleWebMeta.getNeonatalNextOfKinMiddleName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinMiddleName.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinMiddleName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinMiddleName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinFirstName
                                               : neonatalNextOfKinLastName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinFirstName,
                         SampleWebMeta.getNeonatalNextOfKinFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinFirstName.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinFirstName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinRelationId
                                               : neonatalNextOfKinMiddleName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinRelationId,
                         SampleWebMeta.getNeonatalNextOfKinRelationId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinRelationId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinRelationId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinRelationId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinBirthDate
                                               : neonatalNextOfKinFirstName;
                             }
                         });

        addScreenHandler(neonatalNextOfKinBirthDate,
                         SampleWebMeta.getNeonatalNextOfKinBirthDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinBirthDate.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinBirthDate()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinBirthDate.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinNationalId
                                               : neonatalNextOfKinRelationId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinNationalId,
                         SampleWebMeta.getNeonatalNextOfKinNationalId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinNationalId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinNationalId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinNationalId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrMultipleUnit
                                               : neonatalNextOfKinBirthDate;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrMultipleUnit,
                         SampleWebMeta.getNeonatalNextOfKinAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrMultipleUnit.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrMultipleUnit()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrMultipleUnit.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrStreetAddress
                                               : neonatalNextOfKinNationalId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrStreetAddress,
                         SampleWebMeta.getNeonatalNextOfKinAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrStreetAddress.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrStreetAddress()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrStreetAddress.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrCity
                                               : neonatalNextOfKinAddrMultipleUnit;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrCity,
                         SampleWebMeta.getNeonatalNextOfKinAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrCity.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrCity()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrState
                                               : neonatalNextOfKinAddrStreetAddress;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrState,
                         SampleWebMeta.getNeonatalNextOfKinAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrState.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrState()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrZipCode
                                               : neonatalNextOfKinAddrCity;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrZipCode,
                         SampleWebMeta.getNeonatalNextOfKinAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrZipCode.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrZipCode()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinAddrHomePhone
                                               : neonatalNextOfKinAddrState;
                             }
                         });

        addScreenHandler(neonatalNextOfKinAddrHomePhone,
                         SampleWebMeta.getNeonatalNextOfKinAddrHomePhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinAddrHomePhone.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinAddrHomePhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinAddrHomePhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinGenderId
                                               : neonatalNextOfKinAddrZipCode;
                             }
                         });

        addScreenHandler(neonatalNextOfKinGenderId,
                         SampleWebMeta.getNeonatalNextOfKinGenderId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinGenderId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinGenderId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinGenderId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinRaceId
                                               : neonatalNextOfKinAddrHomePhone;
                             }
                         });

        addScreenHandler(neonatalNextOfKinRaceId,
                         SampleWebMeta.getNeonatalNextOfKinRaceId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinRaceId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinRaceId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinRaceId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalNextOfKinEthnicityId
                                               : neonatalNextOfKinGenderId;
                             }
                         });

        addScreenHandler(neonatalNextOfKinEthnicityId,
                         SampleWebMeta.getNeonatalNextOfKinEthnicityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalNextOfKinEthnicityId.setValue(getValue(SampleWebMeta.getNeonatalNextOfKinEthnicityId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalNextOfKinEthnicityId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalProviderLastName
                                               : neonatalNextOfKinRaceId;
                             }
                         });

        addScreenHandler(neonatalProviderLastName,
                         SampleWebMeta.getNeonatalProviderLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalProviderLastName.setValue(getValue(SampleWebMeta.getNeonatalProviderLastName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalProviderLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalProviderFirstName
                                               : neonatalNextOfKinEthnicityId;
                             }
                         });

        addScreenHandler(neonatalProviderFirstName,
                         SampleWebMeta.getNeonatalProviderFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 neonatalProviderFirstName.setValue(getValue(SampleWebMeta.getNeonatalProviderFirstName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 neonatalProviderFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? neonatalPatientId : neonatalProviderLastName;
                             }
                         });

        parentBus.addHandler(DomainChangeEvent.getType(), new DomainChangeEvent.Handler() {
            @Override
            public void onDomainChange(DomainChangeEvent event) {
                /*
                 * the widgets in this tab need to be enabled or disabled based
                 * on the current domain
                 */
                domain = event.getDomain();
                setState(state);
            }
        });
    }

    public void setData(DataViewVO1 data) {
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
    
    /**
     * Adds the keys for all checked checkboxes to the list of columns shown
     * in the generated excel file
     */
    public void addColumns(ArrayList<String> columns) {
        Widget w;
        CheckBox cb;

        if (!canEdit)
            return;
        
        logger.fine("Neonatal Tab");
        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            w = entry.getValue().widget;
            if (w instanceof CheckBox) {
                cb = (CheckBox)w;
                if ("Y".equals(cb.getValue()))
                    columns.add(entry.getKey());
            }
        }
    }

    private String getValue(String column) {
        if (data == null || data.getColumns() == null)
            return "N";
        return data.getColumns().contains(column) ? "Y" : "N";
    }

    private void evaluateEdit() {
        canEdit = (domain == null || Constants.domain().NEONATAL.equals(domain));
    }
}