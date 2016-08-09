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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SDWISTabUI extends Screen {
    @UiTemplate("SDWISTab.ui.xml")
    interface SDWISTabUIBinder extends UiBinder<Widget, SDWISTabUI> {
    };

    private static SDWISTabUIBinder uiBinder = GWT.create(SDWISTabUIBinder.class);

    @UiField
    protected CheckBox              sdwisPwsId, pwsName, sdwisStateLabId, sdwisFacilityId,
                    sdwisSampleTypeId, sdwisSampleCategoryId, sdwisSamplePointId, sdwisLocation,
                    sdwisPriority, sdwisCollector;

    @UiField
    protected Label<String>         fieldsDisabledLabel;

    protected Screen                parentScreen;

    protected EventBus              parentBus;

    protected DataView1VO           data;

    protected String                domain;

    protected boolean               canEdit;

    public SDWISTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(sdwisPwsId, SampleWebMeta.SDWIS_PWS_ID, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                sdwisPwsId.setValue(getValue(SampleWebMeta.SDWIS_PWS_ID));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.SDWIS_PWS_ID, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisPwsId.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? pwsName : sdwisCollector;
            }
        });

        addScreenHandler(pwsName, SampleWebMeta.PWS_NAME, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                pwsName.setValue(getValue(SampleWebMeta.PWS_NAME));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.PWS_NAME, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                pwsName.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? sdwisStateLabId : sdwisPwsId;
            }
        });

        addScreenHandler(sdwisStateLabId,
                         SampleWebMeta.SDWIS_STATE_LAB_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisStateLabId.setValue(getValue(SampleWebMeta.SDWIS_STATE_LAB_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_STATE_LAB_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisStateLabId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisFacilityId : pwsName;
                             }
                         });

        addScreenHandler(sdwisFacilityId,
                         SampleWebMeta.SDWIS_FACILITY_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisFacilityId.setValue(getValue(SampleWebMeta.SDWIS_FACILITY_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_FACILITY_ID, event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisFacilityId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleTypeId : sdwisStateLabId;
                             }
                         });

        addScreenHandler(sdwisSampleTypeId,
                         SampleWebMeta.SDWIS_SAMPLE_TYPE_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisSampleTypeId.setValue(getValue(SampleWebMeta.SDWIS_SAMPLE_TYPE_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_SAMPLE_TYPE_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleTypeId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleCategoryId : sdwisFacilityId;
                             }
                         });

        addScreenHandler(sdwisSampleCategoryId,
                         SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisSampleCategoryId.setValue(getValue(SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_SAMPLE_CATEGORY_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleCategoryId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSamplePointId : sdwisSampleTypeId;
                             }
                         });

        addScreenHandler(sdwisSamplePointId,
                         SampleWebMeta.SDWIS_SAMPLE_POINT_ID,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisSamplePointId.setValue(getValue(SampleWebMeta.SDWIS_SAMPLE_POINT_ID));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_SAMPLE_POINT_ID,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSamplePointId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisLocation : sdwisSampleCategoryId;
                             }
                         });

        addScreenHandler(sdwisLocation, SampleWebMeta.SDWIS_LOCATION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                sdwisLocation.setValue(getValue(SampleWebMeta.SDWIS_LOCATION));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.SDWIS_LOCATION, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisLocation.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? sdwisPriority : sdwisSamplePointId;
            }
        });

        addScreenHandler(sdwisPriority, SampleWebMeta.SDWIS_PRIORITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                sdwisPriority.setValue(getValue(SampleWebMeta.SDWIS_PRIORITY));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                addRemoveColumn(SampleWebMeta.SDWIS_PRIORITY, event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisPriority.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? sdwisCollector : sdwisLocation;
            }
        });

        addScreenHandler(sdwisCollector,
                         SampleWebMeta.SDWIS_COLLECTOR_HEADER,
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 sdwisCollector.setValue(getValue(SampleWebMeta.SDWIS_COLLECTOR_HEADER));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 addRemoveColumn(SampleWebMeta.SDWIS_COLLECTOR_HEADER,
                                                 event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisCollector.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisPwsId : sdwisPriority;
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

                if (Constants.domain().SDWIS.equals(prevDom) && !canEdit) {
                    /*
                     * the previous domain was SDWIS but is not anymore; if some
                     * columns from SDWIS were selected before the domain was
                     * changed, remove them from the ones shown in the report
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
        canEdit = Constants.domain().SDWIS.equals(domain);
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