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
package org.openelis.modules.sampleLocation.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.metamap.SampleEnvironmentalMetaMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SampleLocationScreen extends Screen implements HasActionHandlers<SampleLocationScreen.Action> {

    protected Dropdown<String> state, country;
    private boolean dropdownsInited;
    private SampleEnvironmentalDO        envDO;

    public enum Action {
        COMMIT
    };

    private SampleEnvironmentalMetaMap       meta = new SampleEnvironmentalMetaMap();

    public SampleLocationScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.sampleLocation.server.SampleLocationService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }
    
    private void initialize() {
        final TextBox samplingLocation = (TextBox)def.getWidget(meta.getSamplingLocation());
        addScreenHandler(samplingLocation, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                samplingLocation.setValue(envDO.getSamplingLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.setSamplingLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                samplingLocation.enable(true);
            }
        });

        final TextBox multipleUnit = (TextBox)def.getWidget(meta.ADDRESS.getMultipleUnit());
        addScreenHandler(multipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                multipleUnit.setValue(envDO.getAddressDO().getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                multipleUnit.enable(true);
            }
        });

        final TextBox streetAddress = (TextBox)def.getWidget(meta.ADDRESS.getStreetAddress());
        addScreenHandler(streetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                streetAddress.setValue(envDO.getAddressDO().getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                streetAddress.enable(true);
            }
        });

        final TextBox city = (TextBox)def.getWidget(meta.ADDRESS.getCity());
        addScreenHandler(city, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(envDO.getAddressDO().getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                city.enable(true);
            }
        });

        state = (Dropdown)def.getWidget(meta.ADDRESS.getState());
        addScreenHandler(state, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                state.setSelection(envDO.getAddressDO().getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                state.enable(true);
            }
        });

        final TextBox zipCode = (TextBox)def.getWidget(meta.ADDRESS.getZipCode());
        addScreenHandler(zipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                zipCode.setValue(envDO.getAddressDO().getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                zipCode.enable(true);
            }
        });

        country = (Dropdown)def.getWidget(meta.ADDRESS.getCountry());
        addScreenHandler(country, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                country.setSelection(envDO.getAddressDO().getCountry());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                envDO.getAddressDO().setCountry(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                country.enable(true);
            }
        });
        
        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });
    }
        
    public void commit() {
        ActionEvent.fire(this, Action.COMMIT, null);
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
            setCountriesModel(DictionaryCache.getListByCategorySystemName("country"));
            setStatesModel(DictionaryCache.getListByCategorySystemName("state"));
            dropdownsInited = true;
        }
        
        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}