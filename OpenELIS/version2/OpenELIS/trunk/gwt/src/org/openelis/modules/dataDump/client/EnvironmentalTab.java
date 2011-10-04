package org.openelis.modules.dataDump.client;

import java.util.EnumSet;

import org.openelis.domain.DataDumpVO;
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
    
    private DataDumpVO data;
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
            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                data.setSampleEnvironmentalIsHazardous(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envIsHazardous.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envPriority = (CheckBox)def.getWidget(SampleWebMeta.getEnvPriority());
        addScreenHandler(envPriority, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envPriority.setValue(data.getSampleEnvironmentalPriority());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalPriority(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envPriority.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envCollector = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorHeader());
        addScreenHandler(envCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollector.setValue(data.getSampleEnvironmentalCollectorHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorHeader(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envCollector.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envCollectorPhone = (CheckBox)def.getWidget(SampleWebMeta.getEnvCollectorPhone());
        addScreenHandler(envCollectorPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollectorPhone.setValue(data.getSampleEnvironmentalCollectorPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalCollectorPhone(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envCollectorPhone.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envLocation = (CheckBox)def.getWidget(SampleWebMeta.getEnvLocationHeader());
        addScreenHandler(envLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envLocation.setValue(data.getSampleEnvironmentalLocationHeader());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationHeader(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envLocation.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        locationAddrCity = (CheckBox)def.getWidget(SampleWebMeta.getLocationAddrCity());
        addScreenHandler(locationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                locationAddrCity.setValue(data.getSampleEnvironmentalLocationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalLocationAddressCity(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationAddrCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        envDescription = (CheckBox)def.getWidget(SampleWebMeta.getEnvDescription());
        addScreenHandler(envDescription, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envDescription.setValue(data.getSampleEnvironmentalDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSampleEnvironmentalDescription(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                envDescription.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });
    }       
    
    public void setData(DataDumpVO data) {
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
    
    private void changeCount(String val) {
        if ("Y".equals(val))
            checkCount++;
        else
            checkCount--;
    }
}