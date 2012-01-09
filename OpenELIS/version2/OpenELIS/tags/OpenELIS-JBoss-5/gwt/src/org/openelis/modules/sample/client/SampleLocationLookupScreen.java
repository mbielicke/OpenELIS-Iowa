/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.meta.SampleMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class SampleLocationLookupScreen extends Screen implements HasActionHandlers<SampleLocationLookupScreen.Action> {

    protected Dropdown<String> state, country;
    protected TextBox samplingLocation, multipleUnit, streetAddress,
                      city, zipCode;
    protected AppButton okButton;
    
    private boolean dropdownsInited;
    private SampleEnvironmentalDO        envDO;

    public enum Action {
        OK
    };

    public SampleLocationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleLocationLookupDef.class));
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }
    
    private void initialize() {
        samplingLocation = (TextBox)def.getWidget(SampleMeta.getEnvLocation());
        addScreenHandler(samplingLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                samplingLocation.setValue(envDO.getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplingLocation.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                samplingLocation.setQueryMode(event.getState() == State.QUERY);
            }
        });

        multipleUnit = (TextBox)def.getWidget(SampleMeta.getLocationAddrMultipleUnit());
        addScreenHandler(multipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                multipleUnit.setValue(envDO.getLocationAddress().getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                multipleUnit.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                multipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        streetAddress = (TextBox)def.getWidget(SampleMeta.getLocationAddrStreetAddress());
        addScreenHandler(streetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                streetAddress.setValue(envDO.getLocationAddress().getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                streetAddress.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                streetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        city = (TextBox)def.getWidget(SampleMeta.getLocationAddrCity());
        addScreenHandler(city, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(envDO.getLocationAddress().getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                city.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                city.setQueryMode(event.getState() == State.QUERY);
            }
        });

        state = (Dropdown)def.getWidget(SampleMeta.getLocationAddrState());
        addScreenHandler(state, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                state.setSelection(envDO.getLocationAddress().getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                state.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                state.setQueryMode(event.getState() == State.QUERY);
            }
        });

        zipCode = (TextBox)def.getWidget(SampleMeta.getLocationAddrZipCode());
        addScreenHandler(zipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                zipCode.setValue(envDO.getLocationAddress().getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                zipCode.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                zipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        country = (Dropdown)def.getWidget(SampleMeta.getLocationAddrCountry());
        addScreenHandler(country, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                country.setSelection(envDO.getLocationAddress().getCountry());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getLocationAddress().setCountry(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                country.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                country.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });
    }
        
    public void ok() {
        ActionEvent.fire(this, Action.OK, null);
        window.close();
    }

    public void setCountriesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        
        country.setModel(model);
    }
    
    public void setStatesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        }
        state.setModel(model);
    }
    
    public void setEnvDO(SampleEnvironmentalDO envDO) {
        this.envDO = envDO;
        
        if(!dropdownsInited) {
            setCountriesModel(CategoryCache.getBySystemName("country"));
            setStatesModel(CategoryCache.getBySystemName("state"));
            dropdownsInited = true;
        }
        
        DataChangeEvent.fire(this);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                setFocus(samplingLocation);
            }
        });
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}