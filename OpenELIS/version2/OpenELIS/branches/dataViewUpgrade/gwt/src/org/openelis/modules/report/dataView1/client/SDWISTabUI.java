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
        addScreenHandler(sdwisPwsId, SampleWebMeta.getSDWISPwsId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisPwsId.setValue(getValue(SampleWebMeta.getSDWISPwsId()));
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisPwsId.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? pwsName : sdwisCollector;
            }
        });

        addScreenHandler(pwsName, SampleWebMeta.getPwsName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(getValue(SampleWebMeta.getPwsName()));
            }

            public void onStateChange(StateChangeEvent event) {
                pwsName.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? sdwisStateLabId : sdwisPwsId;
            }
        });

        addScreenHandler(sdwisStateLabId,
                         SampleWebMeta.getSDWISStateLabId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisStateLabId.setValue(getValue(SampleWebMeta.getSDWISStateLabId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisStateLabId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisFacilityId : pwsName;
                             }
                         });

        addScreenHandler(sdwisFacilityId,
                         SampleWebMeta.getSDWISFacilityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisFacilityId.setValue(getValue(SampleWebMeta.getSDWISFacilityId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisFacilityId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleTypeId : sdwisStateLabId;
                             }
                         });

        addScreenHandler(sdwisSampleTypeId,
                         SampleWebMeta.getSDWISSampleTypeId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSampleTypeId.setValue(getValue(SampleWebMeta.getSDWISSampleTypeId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleTypeId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleCategoryId : sdwisFacilityId;
                             }
                         });

        addScreenHandler(sdwisSampleCategoryId,
                         SampleWebMeta.getSDWISSampleCategoryId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSampleCategoryId.setValue(getValue(SampleWebMeta.getSDWISSampleCategoryId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleCategoryId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSamplePointId : sdwisSampleTypeId;
                             }
                         });

        addScreenHandler(sdwisSamplePointId,
                         SampleWebMeta.getSDWISSamplePointId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSamplePointId.setValue(getValue(SampleWebMeta.getSDWISSamplePointId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSamplePointId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisLocation : sdwisSampleCategoryId;
                             }
                         });

        addScreenHandler(sdwisLocation,
                         SampleWebMeta.getSDWISLocation(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisLocation.setValue(getValue(SampleWebMeta.getSDWISLocation()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisLocation.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisPriority : sdwisSamplePointId;
                             }
                         });

        addScreenHandler(sdwisPriority,
                         SampleWebMeta.getSDWISPriority(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisPriority.setValue(getValue(SampleWebMeta.getSDWISPriority()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisPriority.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisCollector : sdwisLocation;
                             }
                         });

        addScreenHandler(sdwisCollector,
                         SampleWebMeta.getSDWISCollectorHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisCollector.setValue(getValue(SampleWebMeta.getSDWISCollectorHeader()));
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
        canEdit = (domain == null || Constants.domain().SDWIS.equals(domain));
    }
}