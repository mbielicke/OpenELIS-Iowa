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
package org.openelis.modules.sample.client;

import java.util.EnumSet;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class SDWISTab extends Screen {
    private Dropdown<Integer>              sDWISSampleTypeId, sDWISRepeatCodeId,
                    sDWISSampleCategoryId, sDWISPbSampleTypeId;
    private CalendarLookUp                 sDWISCompositeDate;
    private TextBox                        sDWISPwsId, pwsName, sDWISStateLabId, sDWISFacilityId,
                    sDWISOriginalSampleNumber, sDWISSamplePointId, pointDesc, sDWISCollector,
                    sequence, sDWISCompositeSampleNumber;
    private AutoComplete<Integer>          orgName, billTo;
    private AppButton                      pwsButton, reportToLookup, billToLookup;
    private CheckBox                       sDWISCompositeIndicator;

    private SampleOrganizationLookupScreen organizationScreen;

    protected ScreenService                orgService;

    private SampleManager                  manager;

    protected boolean                      loaded = false;

    public SDWISTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);

        orgService = new ScreenService(
                                       "controller?service=org.openelis.modules.organization.server.OrganizationService");

        initialize();
    }

    public void initialize() {
        /*
        sDWISPwsId = (TextBox)def.getWidget(SampleMeta.getSDWISPwsId());
        addScreenHandler(sDWISPwsId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISPwsId.setValue(DO.getSDWISPwsId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISPwsId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISPwsId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISPwsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pwsButton = (AppButton)def.getWidget("pwsButton");
        addScreenHandler(pwsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        pwsName = (TextBox)def.getWidget("pwsName");
        addScreenHandler(pwsName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(PWSNAME_DO.pwsName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                PWSNAME_DO.pwsName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                pwsName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISStateLabId = (TextBox)def.getWidget(SampleMeta.getSDWISStateLabId());
        addScreenHandler(sDWISStateLabId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISStateLabId.setValue(DO.getSDWISStateLabId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISStateLabId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISStateLabId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISStateLabId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISFacilityId = (TextBox)def.getWidget(SampleMeta.getSDWISFacilityId());
        addScreenHandler(sDWISFacilityId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISFacilityId.setValue(DO.getSDWISFacilityId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISFacilityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISFacilityId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISFacilityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSampleTypeId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleTypeId());
        addScreenHandler(sDWISSampleTypeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSampleTypeId.setSelection(DO.getSDWISSampleTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISSampleTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSampleTypeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISSampleTypeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISOriginalSampleNumber = (TextBox)def.getWidget(SampleMeta.getSDWISOriginalSampleNumber());
        addScreenHandler(sDWISOriginalSampleNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISOriginalSampleNumber.setValue(DO.getSDWISOriginalSampleNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISOriginalSampleNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISOriginalSampleNumber.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISOriginalSampleNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSamplePointId = (TextBox)def.getWidget(SampleMeta.getSDWISSamplePointId());
        addScreenHandler(sDWISSamplePointId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSamplePointId.setValue(DO.getSDWISSamplePointId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISSamplePointId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSamplePointId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISSamplePointId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        pointDesc = (TextBox)def.getWidget(SampleMeta.getSDWISLocation());
        addScreenHandler(pointDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pointDesc.setValue(POINTDESC_DO.pointDesc());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                POINTDESC_DO.pointDesc(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pointDesc.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                pointDesc.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISRepeatCodeId = (Dropdown)def.getWidget(SampleMeta.getSDWISRepeatCodeId());
        addScreenHandler(sDWISRepeatCodeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISRepeatCodeId.setSelection(DO.getSDWISRepeatCodeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISRepeatCodeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISRepeatCodeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISRepeatCodeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISSampleCategoryId = (Dropdown)def.getWidget(SampleMeta.getSDWISSampleCategoryId());
        addScreenHandler(sDWISSampleCategoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISSampleCategoryId.setSelection(DO.getSDWISSampleCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISSampleCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISSampleCategoryId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISSampleCategoryId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISCollector = (TextBox)def.getWidget(SampleMeta.getSDWISCollector());
        addScreenHandler(sDWISCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISCollector.setValue(DO.getSDWISCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                DO.setSDWISCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISCollector.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISCollector.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISPbSampleTypeId = (Dropdown)def.getWidget(SampleMeta.getSDWISPbSampleTypeId());
        addScreenHandler(sDWISPbSampleTypeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISPbSampleTypeId.setSelection(DO.getSDWISPbSampleTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISPbSampleTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISPbSampleTypeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISPbSampleTypeId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sDWISCompositeIndicator = (CheckBox)def.getWidget(SampleMeta.getSDWISCompositeIndicator());
        addScreenHandler(sDWISCompositeIndicator, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISCompositeIndicator.setValue(DO.getSDWISCompositeIndicator());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                DO.setSDWISCompositeIndicator(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISCompositeIndicator.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISCompositeIndicator.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sequence = (TextBox)def.getWidget(SampleMeta.getSDWISCompositeSequence());
        addScreenHandler(sequence, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sequence.setValue(SEQUENCE_DO.sequence());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                SEQUENCE_DO.sequence(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sequence.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sequence.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISCompositeSampleNumber = (TextBox)def.getWidget(SampleMeta.getSDWISCompositeSampleNumber());
        addScreenHandler(sDWISCompositeSampleNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISCompositeSampleNumber.setValue(DO.getSDWISCompositeSampleNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                DO.setSDWISCompositeSampleNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISCompositeSampleNumber.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISCompositeSampleNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sDWISCompositeDate = (CalendarLookUp)def.getWidget(SampleMeta.getSDWISCompositeDate());
        addScreenHandler(sDWISCompositeDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                sDWISCompositeDate.setValue(DO.getSDWISCompositeDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                DO.setSDWISCompositeDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sDWISCompositeDate.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sDWISCompositeDate.setQueryMode(event.getState() == State.QUERY);
            }
        });*/
    }
}
