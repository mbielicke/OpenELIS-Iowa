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

public class EnvironmentalTab extends Screen {
    
    private DataViewVO data;
    private CheckBox   envIsHazardous, envPriority, envCollector, envCollectorPhone,
                       envLocation, locationAddrCity, envDescription;
    private boolean    loaded;
    private int        checkCount;
    
    public EnvironmentalTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }
    
    private void initialize() {
        envIsHazardous = (CheckBox)def.getWidget(SampleWebMeta.getEnvIsHazardous());
        addScreenHandler(envIsHazardous, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envIsHazardous.setValue(data.getSampleEnvironmentalIsHazardous());
                changeCount(data.getSampleEnvironmentalIsHazardous(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                data.setSampleEnvironmentalIsHazardous(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envIsHazardous.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envPriority = (CheckBox)def.getWidget(SampleWebMeta.getEnvPriority());
        addScreenHandler(envPriority, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envPriority.setValue(data.getSampleEnvironmentalPriority());
                changeCount(data.getSampleEnvironmentalPriority(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalPriority(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envPriority.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envCollector = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorHeader());
        addScreenHandler(envCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollector.setValue(data.getSampleEnvironmentalCollectorHeader());
                changeCount(data.getSampleEnvironmentalCollectorHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envCollector.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envCollectorPhone = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorPhone());
        addScreenHandler(envCollectorPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollectorPhone.setValue(data.getSampleEnvironmentalCollectorPhone());
                changeCount(data.getSampleEnvironmentalCollectorPhone(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorPhone(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envCollectorPhone.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envLocation = (CheckBox)def.getWidget(SampleWebMeta.getEnvLocationHeader());
        addScreenHandler(envLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envLocation.setValue(data.getSampleEnvironmentalLocationHeader());
                changeCount(data.getSampleEnvironmentalLocationHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envLocation.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        locationAddrCity = (CheckBox)def.getWidget(SampleWebMeta.getLocationAddrCityHeader());
        addScreenHandler(locationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrCity.setValue(data.getSampleEnvironmentalLocationAddressCityHeader());
                changeCount(data.getSampleEnvironmentalLocationAddressCityHeader(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationAddressCityHeader(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envDescription = (CheckBox)def.getWidget(SampleWebMeta.getEnvDescription());
        addScreenHandler(envDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envDescription.setValue(data.getSampleEnvironmentalDescription());
                changeCount(data.getSampleEnvironmentalDescription(), false);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalDescription(event.getValue());
                changeCount(event.getValue(), true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envDescription.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
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