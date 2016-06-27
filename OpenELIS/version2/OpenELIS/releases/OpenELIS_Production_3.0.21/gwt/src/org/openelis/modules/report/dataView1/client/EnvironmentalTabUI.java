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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class EnvironmentalTabUI extends Screen {
    @UiTemplate("EnvironmentalTab.ui.xml")
    interface EnvironmentalTabUIBinder extends UiBinder<Widget, EnvironmentalTabUI> {
    };

    private static EnvironmentalTabUIBinder uiBinder = GWT.create(EnvironmentalTabUIBinder.class);

    @UiField
    protected CheckBox                      envIsHazardous, envPriority, envCollector,
                    envCollectorPhone, envDescription, envLocation, locationAddrMultipleUnit,
                    locationAddrStreetAddress, locationAddrCity, locationAddrState,
                    locationAddrZipCode, locationAddrCountry;

    @UiField
    protected Label<String>                 fieldsDisabledLabel;

    protected Screen                        parentScreen;

    protected EventBus                      parentBus;

    protected DataView1VO                   data;

    protected String                        domain;

    protected boolean                       canEdit;

    public EnvironmentalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(envIsHazardous,
                         SampleWebMeta.ENV_IS_HAZARDOUS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 envIsHazardous.setValue(getValue(SampleWebMeta.ENV_IS_HAZARDOUS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ENV_IS_HAZARDOUS, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envIsHazardous.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envPriority : locationAddrCountry;
                             }
                         });

        addScreenHandler(envPriority, SampleWebMeta.ENV_PRIORITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                envPriority.setValue(getValue(SampleWebMeta.ENV_PRIORITY));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ENV_PRIORITY, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                envPriority.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? envCollector : envIsHazardous;
            }
        });

        addScreenHandler(envCollector,
                         SampleWebMeta.ENV_COLLECTOR_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 envCollector.setValue(getValue(SampleWebMeta.ENV_COLLECTOR_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ENV_COLLECTOR_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envCollector.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envCollectorPhone : envPriority;
                             }
                         });

        addScreenHandler(envCollectorPhone,
                         SampleWebMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 envCollectorPhone.setValue(getValue(SampleWebMeta.ENV_COLLECTOR_PHONE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ENV_COLLECTOR_PHONE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envCollectorPhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envDescription : envCollector;
                             }
                         });

        addScreenHandler(envDescription,
                         SampleWebMeta.ENV_DESCRIPTION,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 envDescription.setValue(getValue(SampleWebMeta.ENV_DESCRIPTION));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ENV_DESCRIPTION, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envDescription.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envLocation : envCollectorPhone;
                             }
                         });

        addScreenHandler(envLocation, SampleWebMeta.ENV_LOCATION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                envLocation.setValue(getValue(SampleWebMeta.ENV_LOCATION));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ENV_LOCATION, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                envLocation.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? locationAddrMultipleUnit : envDescription;
            }
        });

        addScreenHandler(locationAddrMultipleUnit,
                         SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrMultipleUnit.setValue(getValue(SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_MULTIPLE_UNIT,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrMultipleUnit.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrStreetAddress : envLocation;
                             }
                         });

        addScreenHandler(locationAddrStreetAddress,
                         SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrStreetAddress.setValue(getValue(SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrStreetAddress.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrCity : locationAddrMultipleUnit;
                             }
                         });

        addScreenHandler(locationAddrCity,
                         SampleWebMeta.LOCATION_ADDR_CITY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrCity.setValue(getValue(SampleWebMeta.LOCATION_ADDR_CITY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_CITY, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrState : locationAddrStreetAddress;
                             }
                         });

        addScreenHandler(locationAddrState,
                         SampleWebMeta.LOCATION_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrState.setValue(getValue(SampleWebMeta.LOCATION_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_STATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrZipCode : locationAddrCity;
                             }
                         });

        addScreenHandler(locationAddrZipCode,
                         SampleWebMeta.LOCATION_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrZipCode.setValue(getValue(SampleWebMeta.LOCATION_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_ZIP_CODE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrCountry : locationAddrState;
                             }
                         });

        addScreenHandler(locationAddrCountry,
                         SampleWebMeta.LOCATION_ADDR_COUNTRY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddrCountry.setValue(getValue(SampleWebMeta.LOCATION_ADDR_COUNTRY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.LOCATION_ADDR_COUNTRY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrCountry.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envIsHazardous : locationAddrZipCode;
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

                if (Constants.domain().ENVIRONMENTAL.equals(prevDom) && !canEdit) {
                    /*
                     * the previous domain was environmental but is not anymore;
                     * if some columns from environmental were selected before
                     * the domain was changed, remove them from the ones shown
                     * in the report
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

    private void evaluateEdit() {
        canEdit = Constants.domain().ENVIRONMENTAL.equals(domain);
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