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

import java.util.ArrayList;
import java.util.Map;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class PrivateWellTabUI extends Screen {
    @UiTemplate("PrivateWellTab.ui.xml")
    interface PrivateWellTabUIBinder extends UiBinder<Widget, PrivateWellTabUI> {
    };

    private static PrivateWellTabUIBinder uiBinder = GWT.create(PrivateWellTabUIBinder.class);

    @UiField
    protected CheckBox                    wellOwner, wellCollector, wellWellNumber,
                    wellReportToAddressWorkPhone, wellReportToAddressFaxPhone, wellLocation,
                    wellLocationAddrMultipleUnit, wellLocationAddrStreetAddress,
                    wellLocationAddrCity, wellLocationAddrState, wellLocationAddrZipCode;

    @UiField
    protected Label<String>               fieldsDisabledLabel;

    protected Screen                      parentScreen;

    protected EventBus                    parentBus;

    protected DataView1VO                 data;

    protected String                      domain;

    protected boolean                     canEdit;

    public PrivateWellTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(wellOwner, SampleWebMeta.getWellOwner(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellOwner.setValue(getValue(SampleWebMeta.getWellOwner()));
            }

            public void onStateChange(StateChangeEvent event) {
                wellOwner.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? wellCollector : wellLocationAddrZipCode;
            }
        });

        addScreenHandler(wellCollector,
                         SampleWebMeta.getWellCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellCollector.setValue(getValue(SampleWebMeta.getWellCollector()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellCollector.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellWellNumber : wellOwner;
                             }
                         });

        addScreenHandler(wellWellNumber,
                         SampleWebMeta.getWellWellNumber(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellWellNumber.setValue(getValue(SampleWebMeta.getWellWellNumber()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellWellNumber.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellReportToAddressWorkPhone : wellCollector;
                             }
                         });

        addScreenHandler(wellReportToAddressWorkPhone,
                         SampleWebMeta.getWellReportToAddressWorkPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellReportToAddressWorkPhone.setValue(getValue(SampleWebMeta.getWellReportToAddressWorkPhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellReportToAddressWorkPhone.setEnabled(isState(DEFAULT) &&
                                                                         canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellReportToAddressFaxPhone : wellWellNumber;
                             }
                         });

        addScreenHandler(wellReportToAddressFaxPhone,
                         SampleWebMeta.getWellReportToAddressFaxPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellReportToAddressFaxPhone.setValue(getValue(SampleWebMeta.getWellReportToAddressFaxPhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellReportToAddressFaxPhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocation : wellReportToAddressWorkPhone;
                             }
                         });

        addScreenHandler(wellLocation,
                         SampleWebMeta.getWellLocation(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocation.setValue(getValue(SampleWebMeta.getWellLocation()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocation.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocationAddrMultipleUnit
                                               : wellReportToAddressFaxPhone;
                             }
                         });

        addScreenHandler(wellLocationAddrMultipleUnit,
                         SampleWebMeta.getWellLocationAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocationAddrMultipleUnit.setValue(getValue(SampleWebMeta.getWellLocationAddrMultipleUnit()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocationAddrMultipleUnit.setEnabled(isState(DEFAULT) &&
                                                                         canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocationAddrStreetAddress : wellLocation;
                             }
                         });

        addScreenHandler(wellLocationAddrStreetAddress,
                         SampleWebMeta.getWellLocationAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocationAddrStreetAddress.setValue(getValue(SampleWebMeta.getWellLocationAddrStreetAddress()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocationAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                          canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocationAddrCity
                                               : wellLocationAddrMultipleUnit;
                             }
                         });

        addScreenHandler(wellLocationAddrCity,
                         SampleWebMeta.getWellLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocationAddrCity.setValue(getValue(SampleWebMeta.getWellLocationAddrCity()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocationAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocationAddrState
                                               : wellLocationAddrStreetAddress;
                             }
                         });

        addScreenHandler(wellLocationAddrState,
                         SampleWebMeta.getWellLocationAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocationAddrState.setValue(getValue(SampleWebMeta.getWellLocationAddrState()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocationAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellLocationAddrZipCode : wellLocationAddrCity;
                             }
                         });

        addScreenHandler(wellLocationAddrZipCode,
                         SampleWebMeta.getWellLocationAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 wellLocationAddrZipCode.setValue(getValue(SampleWebMeta.getWellLocationAddrZipCode()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 wellLocationAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? wellOwner : wellLocationAddrState;
                             }
                         });

        addScreenHandler(fieldsDisabledLabel, "fieldsDisabledLabel", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                fieldsDisabledLabel.setText(canEdit ? null : Messages.get()
                                                                     .dataView_tabFieldsDisabled());
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

    /**
     * Adds the keys for all checked checkboxes to the list of columns shown in
     * the generated excel file
     */
    public void addColumns(ArrayList<String> columns) {
        Widget w;
        CheckBox cb;

        if ( !canEdit)
            return;

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
        canEdit = (domain == null || Constants.domain().PRIVATEWELL.equals(domain));
    }
}