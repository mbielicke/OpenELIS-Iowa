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

public class PrivateWellTab extends Screen {

    private DataDumpVO data;
    private CheckBox   wellOwner, wellCollector, wellWellNumber, wellReportToAddressWorkPhone,
                    wellReportToAddressFaxPhone, wellLocation, wellLocationAddrCity;
    private boolean    loaded;
    private int        checkCount;

    public PrivateWellTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        checkCount = 0;
        
        wellOwner = (CheckBox)def.getWidget(SampleWebMeta.getWellOwner());
        addScreenHandler(wellOwner, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellOwner.setValue(data.getSamplePrivateWellOwner());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellOwner(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellOwner.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        wellCollector = (CheckBox)def.getWidget(SampleWebMeta.getWellCollector());
        addScreenHandler(wellCollector, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellCollector.setValue(data.getSamplePrivateWellCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellCollector(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellCollector.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        wellWellNumber = (CheckBox)def.getWidget(SampleWebMeta.getWellWellNumber());
        addScreenHandler(wellWellNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellWellNumber.setValue(data.getSamplePrivateWellWellNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellWellNumber(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellWellNumber.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        wellReportToAddressWorkPhone = (CheckBox)def.getWidget(SampleWebMeta.getWellReportToAddressWorkPhone());
        addScreenHandler(wellReportToAddressWorkPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellReportToAddressWorkPhone.setValue(data.getSamplePrivateWellReportToAddressWorkPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellReportToAddressWorkPhone(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellReportToAddressWorkPhone.enable(EnumSet.of(State.DEFAULT)
                                                           .contains(event.getState()));
            }
        });

        wellReportToAddressFaxPhone = (CheckBox)def.getWidget(SampleWebMeta.getWellReportToAddressFaxPhone());
        addScreenHandler(wellReportToAddressFaxPhone, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellReportToAddressFaxPhone.setValue(data.getSamplePrivateWellReportToAddressFaxPhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellReportToAddressFaxPhone(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellReportToAddressFaxPhone.enable(EnumSet.of(State.DEFAULT)
                                                          .contains(event.getState()));
            }
        });

        wellLocation = (CheckBox)def.getWidget(SampleWebMeta.getWellLocation());
        addScreenHandler(wellLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellLocation.setValue(data.getSamplePrivateWellLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellLocation(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellLocation.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        wellLocationAddrCity = (CheckBox)def.getWidget(SampleWebMeta.getWellLocationAddrCity());
        addScreenHandler(wellLocationAddrCity, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellLocationAddrCity.setValue(data.getSamplePrivateWellLocationAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setSamplePrivateWellLocationAddressCity(event.getValue());
                changeCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                wellLocationAddrCity.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
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