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

public class EnvironmentalTabUI extends Screen {
    @UiTemplate("EnvironmentalTab.ui.xml")
    interface EnvironmentalTabUIBinder extends UiBinder<Widget, EnvironmentalTabUI> {
    };

    private static EnvironmentalTabUIBinder uiBinder = GWT.create(EnvironmentalTabUIBinder.class);

    @UiField
    protected CheckBox                      envIsHazardous, envPriority, envCollectorHeader,
                    envCollectorPhone, envLocationHeader, locationAddrCityHeader, envDescription;

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
                         SampleWebMeta.getEnvIsHazardous(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envIsHazardous.setValue(getValue(SampleWebMeta.getEnvIsHazardous()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envIsHazardous.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envPriority : envDescription;
                             }
                         });

        addScreenHandler(envPriority, SampleWebMeta.getEnvPriority(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envPriority.setValue(getValue(SampleWebMeta.getEnvPriority()));
            }

            public void onStateChange(StateChangeEvent event) {
                envPriority.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? envCollectorHeader : envIsHazardous;
            }
        });

        addScreenHandler(envCollectorHeader,
                         SampleWebMeta.getEnvCollectorHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envCollectorHeader.setValue(getValue(SampleWebMeta.getEnvCollectorHeader()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envCollectorHeader.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envCollectorPhone : envPriority;
                             }
                         });

        addScreenHandler(envCollectorPhone,
                         SampleWebMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envCollectorPhone.setValue(getValue(SampleWebMeta.getEnvCollectorPhone()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envCollectorPhone.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envLocationHeader : envCollectorHeader;
                             }
                         });

        addScreenHandler(envLocationHeader,
                         SampleWebMeta.getEnvLocationHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envLocationHeader.setValue(getValue(SampleWebMeta.getEnvLocationHeader()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envLocationHeader.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddrCityHeader : envCollectorPhone;
                             }
                         });

        addScreenHandler(locationAddrCityHeader,
                         SampleWebMeta.getLocationAddrCityHeader(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddrCityHeader.setValue(getValue(SampleWebMeta.getLocationAddrCityHeader()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddrCityHeader.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envDescription : envLocationHeader;
                             }
                         });

        addScreenHandler(envDescription,
                         SampleWebMeta.getEnvDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envDescription.setValue(getValue(SampleWebMeta.getEnvDescription()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envDescription.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envIsHazardous : locationAddrCityHeader;
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
        canEdit = (domain == null || Constants.domain().ENVIRONMENTAL.equals(domain));
    }
}