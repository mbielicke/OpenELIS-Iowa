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
import com.google.gwt.user.client.ui.HasValue;
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

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    protected DataViewVO1              data;

    protected String                   domain;

    protected boolean                  canEdit;

    public ClinicalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(clinicalPatientId,
                         SampleWebMeta.getClinicalPatientId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientId.setValue(getValue(SampleWebMeta.getClinicalPatientId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientLastName : clinicalProviderPhone;
                             }
                         });

        addScreenHandler(clinicalPatientLastName,
                         SampleWebMeta.getClinicalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientLastName.setValue(getValue(SampleWebMeta.getClinicalPatientLastName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientFirstName : clinicalPatientId;
                             }
                         });

        addScreenHandler(clinicalPatientFirstName,
                         SampleWebMeta.getClinicalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientFirstName.setValue(getValue(SampleWebMeta.getClinicalPatientFirstName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientBirthDate
                                               : clinicalPatientLastName;
                             }
                         });

        addScreenHandler(clinicalPatientBirthDate,
                         SampleWebMeta.getClinicalPatientBirthDate(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientBirthDate.setValue(getValue(SampleWebMeta.getClinicalPatientBirthDate()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientBirthDate.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientNationalId
                                               : clinicalPatientFirstName;
                             }
                         });

        addScreenHandler(clinicalPatientNationalId,
                         SampleWebMeta.getClinicalPatientNationalId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientNationalId.setValue(getValue(SampleWebMeta.getClinicalPatientNationalId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientNationalId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrMultipleUnit
                                               : clinicalPatientBirthDate;
                             }
                         });

        addScreenHandler(clinicalPatientAddrMultipleUnit,
                         SampleWebMeta.getClinicalPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrMultipleUnit.setValue(getValue(SampleWebMeta.getClinicalPatientAddrMultipleUnit()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrMultipleUnit.setEnabled(isState(DEFAULT) &&
                                                                            canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrStreetAddress
                                               : clinicalPatientNationalId;
                             }
                         });

        addScreenHandler(clinicalPatientAddrStreetAddress,
                         SampleWebMeta.getClinicalPatientAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrStreetAddress.setValue(getValue(SampleWebMeta.getClinicalPatientAddrStreetAddress()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                             canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrCity
                                               : clinicalPatientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(clinicalPatientAddrCity,
                         SampleWebMeta.getClinicalPatientAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrCity.setValue(getValue(SampleWebMeta.getClinicalPatientAddrCity()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrState
                                               : clinicalPatientAddrStreetAddress;
                             }
                         });

        addScreenHandler(clinicalPatientAddrState,
                         SampleWebMeta.getClinicalPatientAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrState.setValue(getValue(SampleWebMeta.getClinicalPatientAddrState()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrZipCode
                                               : clinicalPatientAddrCity;
                             }
                         });

        addScreenHandler(clinicalPatientAddrZipCode,
                         SampleWebMeta.getClinicalPatientAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrZipCode.setValue(getValue(SampleWebMeta.getClinicalPatientAddrZipCode()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientAddrHomePhone
                                               : clinicalPatientAddrState;
                             }
                         });

        addScreenHandler(clinicalPatientAddrHomePhone,
                         SampleWebMeta.getClinicalPatientAddrHomePhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientAddrHomePhone.setValue(getValue(SampleWebMeta.getClinicalPatientAddrHomePhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientAddrHomePhone.setEnabled(isState(DEFAULT) &&
                                                                         canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientGenderId
                                               : clinicalPatientAddrZipCode;
                             }
                         });

        addScreenHandler(clinicalPatientGenderId,
                         SampleWebMeta.getClinicalPatientGenderId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientGenderId.setValue(getValue(SampleWebMeta.getClinicalPatientGenderId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientGenderId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientRaceId
                                               : clinicalPatientAddrHomePhone;
                             }
                         });

        addScreenHandler(clinicalPatientRaceId,
                         SampleWebMeta.getClinicalPatientRaceId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientRaceId.setValue(getValue(SampleWebMeta.getClinicalPatientRaceId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientRaceId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientEthnicityId
                                               : clinicalPatientGenderId;
                             }
                         });

        addScreenHandler(clinicalPatientEthnicityId,
                         SampleWebMeta.getClinicalPatientEthnicityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalPatientEthnicityId.setValue(getValue(SampleWebMeta.getClinicalPatientEthnicityId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalPatientEthnicityId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderLastName : clinicalPatientRaceId;
                             }
                         });

        addScreenHandler(clinicalProviderLastName,
                         SampleWebMeta.getClinicalProviderLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalProviderLastName.setValue(getValue(SampleWebMeta.getClinicalProviderLastName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderFirstName
                                               : clinicalPatientEthnicityId;
                             }
                         });

        addScreenHandler(clinicalProviderFirstName,
                         SampleWebMeta.getClinicalProviderFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalProviderFirstName.setValue(getValue(SampleWebMeta.getClinicalProviderFirstName()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalProviderPhone : clinicalProviderLastName;
                             }
                         });

        addScreenHandler(clinicalProviderPhone,
                         SampleWebMeta.getClinicalProviderPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clinicalProviderPhone.setValue(getValue(SampleWebMeta.getClinicalProviderPhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clinicalProviderPhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clinicalPatientId : clinicalProviderFirstName;
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
        
        logger.fine("Clinical tab");
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
        canEdit = (domain == null || Constants.domain().CLINICAL.equals(domain));
    }
}
