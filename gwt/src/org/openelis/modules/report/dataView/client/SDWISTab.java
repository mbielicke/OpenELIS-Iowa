package org.openelis.modules.report.dataView.client;

import java.util.EnumSet;

import org.openelis.domain.DataViewVO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.meta.SampleWebMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class SDWISTab extends Screen {
    
    private DataViewVO data;
    private CheckBox   sdwisPwsId, pwsName, sdwisStateLabId, sdwisFacilityId, 
                       sdwisSampleTypeId, sdwisSampleCategoryId, sdwisSamplePointId,
                       sdwisLocation, sdwisCollector;
    private boolean    loaded;
    private int        checkCount;
    
    public SDWISTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }
    
    private void initialize() {
        checkCount = 0;
        
        sdwisPwsId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISPwsId());
        addScreenHandler(sdwisPwsId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisPwsId.setValue(data.getSampleSDWISPwsId());
                changeCount(data.getSampleSDWISPwsId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISPwsId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisPwsId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        pwsName = (CheckBox)def.getWidget(SampleWebMeta.getPwsName());
        addScreenHandler(pwsName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(data.getSampleSDWISPwsName());
                changeCount(data.getSampleSDWISPwsName(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISPwsName(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                pwsName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisStateLabId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISStateLabId());
        addScreenHandler(sdwisStateLabId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisStateLabId.setValue(data.getSampleSDWISStateLabId());
                changeCount(data.getSampleSDWISStateLabId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISStateLabId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisStateLabId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisFacilityId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISFacilityId());
        addScreenHandler(sdwisFacilityId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisFacilityId.setValue(data.getSampleSDWISFacilityId());
                changeCount(data.getSampleSDWISFacilityId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISFacilityId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisFacilityId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisSampleTypeId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISSampleTypeId());
        addScreenHandler(sdwisSampleTypeId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisSampleTypeId.setValue(data.getSampleSDWISSampleTypeId());
                changeCount(data.getSampleSDWISSampleTypeId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISSampleTypeId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisSampleTypeId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisSampleCategoryId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISSampleCategoryId());
        addScreenHandler(sdwisSampleCategoryId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisSampleCategoryId.setValue(data.getSampleSDWISSampleCategoryId());
                changeCount(data.getSampleSDWISSampleCategoryId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISSampleCategoryId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisSampleCategoryId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisSamplePointId = (CheckBox)def.getWidget(SampleWebMeta.getSDWISSamplePointId());
        addScreenHandler(sdwisSamplePointId, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisSamplePointId.setValue(data.getSampleSDWISSamplePointId());
                changeCount(data.getSampleSDWISSamplePointId(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISSamplePointId(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisSamplePointId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisLocation = (CheckBox)def.getWidget(SampleWebMeta.getSDWISLocation());
        addScreenHandler(sdwisLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisLocation.setValue(data.getSampleSDWISLocation());
                changeCount(data.getSampleSDWISLocation(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISLocation(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisLocation.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        sdwisCollector = (CheckBox)def.getWidget(SampleWebMeta.getSDWISCollector());
        addScreenHandler(sdwisCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisCollector.setValue(data.getSampleSDWISCollector());
                changeCount(data.getSampleSDWISCollector(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleSDWISCollector(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisCollector.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    } 
    
    public void setData(DataViewVO data) {
        this.data = data;
        loaded = false;
        checkCount = 0;
    }

    public void draw() {
        if ( !loaded)        
            DataChangeEvent.fire(this);        

        loaded = true;
    }
    
    public int getCheckIndicator() {
        if (checkCount > 0)
            return 1;
         return 0;
    }
    
    private void changeCount(String val, boolean manual) {
        /*
         * CheckCount keeps track of the number of checkboxes checked in the tab.
         * It's decremented only when the value is changed manually i.e. 
         * when ValueChangeEvent gets fired and only if it doesn't become negative.
         * This is done to make sure that only the checkboxes unchecked by the user 
         * affect the value rather than the default values in the VO.     
         */
        if ("Y".equals(val))            
            checkCount++;        
        else if (checkCount > 0 && manual)
            checkCount--;
    }
}