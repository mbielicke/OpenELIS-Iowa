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

public class AnimalTabUI extends Screen {
    @UiTemplate("AnimalTab.ui.xml")
    interface AnimalTabUIBinder extends UiBinder<Widget, AnimalTabUI> {
    };

    private static AnimalTabUIBinder uiBinder = GWT.create(AnimalTabUIBinder.class);

    @UiField
    protected CheckBox               aniAnimalCommonNameId, aniAnimalScientificNameId, aniLocation,
                    aniLocationAddrMultipleUnit, aniLocationAddrStreetAddress, aniLocationAddrCity,
                    aniLocationAddrState, aniLocationAddrZipCode, aniProviderLastName,
                    aniProviderFirstName, aniProviderPhone;

    @UiField
    protected Label<String>          fieldsDisabledLabel;

    protected Screen                 parentScreen;

    protected EventBus               parentBus;

    protected DataView1VO            data;

    protected String                 domain;

    protected boolean                canEdit;

    public AnimalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(aniAnimalCommonNameId, SampleWebMeta.ANI_ANIMAL_COMMON_NAME_ID, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                aniAnimalCommonNameId.setValue(getValue(SampleWebMeta.ANI_ANIMAL_COMMON_NAME_ID));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ANI_ANIMAL_COMMON_NAME_ID, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                aniAnimalCommonNameId.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? aniAnimalScientificNameId : aniProviderPhone;
            }
        });
        
        addScreenHandler(aniAnimalScientificNameId, SampleWebMeta.ANI_ANIMAL_SCIENTIFIC_NAME_ID, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                aniAnimalScientificNameId.setValue(getValue(SampleWebMeta.ANI_ANIMAL_SCIENTIFIC_NAME_ID));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ANI_ANIMAL_SCIENTIFIC_NAME_ID, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                aniAnimalScientificNameId.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? aniLocation : aniAnimalCommonNameId;
            }
        });
        
        addScreenHandler(aniLocation, SampleWebMeta.ANI_LOCATION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                aniLocation.setValue(getValue(SampleWebMeta.ANI_LOCATION));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.ANI_LOCATION, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                aniLocation.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? aniLocationAddrMultipleUnit : aniAnimalScientificNameId;
            }
        });

        addScreenHandler(aniLocationAddrMultipleUnit,
                         SampleWebMeta.ANI_LOCATION_ADDR_MULTIPLE_UNIT,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniLocationAddrMultipleUnit.setValue(getValue(SampleWebMeta.ANI_LOCATION_ADDR_MULTIPLE_UNIT));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_LOCATION_ADDR_MULTIPLE_UNIT,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniLocationAddrMultipleUnit.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniLocationAddrStreetAddress : aniLocation;
                             }
                         });

        addScreenHandler(aniLocationAddrStreetAddress,
                         SampleWebMeta.ANI_LOCATION_ADDR_STREET_ADDRESS,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniLocationAddrStreetAddress.setValue(getValue(SampleWebMeta.ANI_LOCATION_ADDR_STREET_ADDRESS));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_LOCATION_ADDR_STREET_ADDRESS,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniLocationAddrStreetAddress.setEnabled(isState(DEFAULT) &&
                                                                         canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniLocationAddrCity : aniLocationAddrMultipleUnit;
                             }
                         });

        addScreenHandler(aniLocationAddrCity,
                         SampleWebMeta.ANI_LOCATION_ADDR_CITY,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniLocationAddrCity.setValue(getValue(SampleWebMeta.ANI_LOCATION_ADDR_CITY));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_LOCATION_ADDR_CITY,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniLocationAddrCity.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniLocationAddrState
                                               : aniLocationAddrStreetAddress;
                             }
                         });

        addScreenHandler(aniLocationAddrState,
                         SampleWebMeta.ANI_LOCATION_ADDR_STATE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniLocationAddrState.setValue(getValue(SampleWebMeta.ANI_LOCATION_ADDR_STATE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_LOCATION_ADDR_STATE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniLocationAddrState.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniLocationAddrZipCode : aniLocationAddrCity;
                             }
                         });

        addScreenHandler(aniLocationAddrZipCode,
                         SampleWebMeta.ANI_LOCATION_ADDR_ZIP_CODE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniLocationAddrZipCode.setValue(getValue(SampleWebMeta.ANI_LOCATION_ADDR_ZIP_CODE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_LOCATION_ADDR_ZIP_CODE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniLocationAddrZipCode.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniProviderLastName : aniLocationAddrState;
                             }
                         });

        addScreenHandler(aniProviderLastName,
                         SampleWebMeta.ANI_PROVIDER_LAST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniProviderLastName.setValue(getValue(SampleWebMeta.ANI_PROVIDER_LAST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_PROVIDER_LAST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniProviderLastName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniProviderFirstName : aniLocationAddrZipCode;
                             }
                         });

        addScreenHandler(aniProviderFirstName,
                         SampleWebMeta.ANI_PROVIDER_FIRST_NAME,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniProviderFirstName.setValue(getValue(SampleWebMeta.ANI_PROVIDER_FIRST_NAME));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_PROVIDER_FIRST_NAME,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniProviderFirstName.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniProviderPhone : aniProviderLastName;
                             }
                         });

        addScreenHandler(aniProviderPhone,
                         SampleWebMeta.ANI_PROVIDER_PHONE,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 aniProviderPhone.setValue(getValue(SampleWebMeta.ANI_PROVIDER_PHONE));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.ANI_PROVIDER_PHONE,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 aniProviderPhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? aniAnimalCommonNameId : aniProviderFirstName;
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

                if (Constants.domain().ANIMAL.equals(prevDom) && !canEdit) {
                    /*
                     * the previous domain was animal but is not anymore;
                     * if some columns from animal were selected before
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
        canEdit = Constants.domain().ANIMAL.equals(domain);
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