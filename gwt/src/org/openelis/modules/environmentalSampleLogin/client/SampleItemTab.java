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
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.metamap.SampleMetaMap;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class SampleItemTab extends Screen {
    private boolean dropdownsInited, loaded;
    
    private SampleMetaMap meta;
    protected SampleItemDO sampleItem;
    protected Dropdown<Integer> typeOfSampleId, containerId, unitOfMeasureId;

    public SampleItemTab(ScreenDef def) {
        setDef(def);
        
        meta = new SampleMetaMap("sample.");
        
        initialize();
    }
    
    private void initialize() {
        typeOfSampleId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeOfSampleId.setSelection(sampleItem.getTypeOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setTypeOfSampleId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                typeOfSampleId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        containerId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getContainerId());
        addScreenHandler(containerId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                containerId.setSelection(sampleItem.getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setContainerId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                containerId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox containerReference = (TextBox)def.getWidget(meta.SAMPLE_ITEM.getContainerReference());
        addScreenHandler(containerReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                containerReference.setValue(sampleItem.getContainerReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setContainerReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerReference.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                containerReference.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox quantity = (TextBox)def.getWidget(meta.SAMPLE_ITEM.getQuantity());
        addScreenHandler(quantity, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                quantity.setValue(Double.toString(sampleItem.getQuantity()));
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                sampleItem.setQuantity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantity.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                quantity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        unitOfMeasureId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getUnitOfMeasureId());
        addScreenHandler(unitOfMeasureId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                unitOfMeasureId.setSelection(sampleItem.getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unitOfMeasureId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                unitOfMeasureId.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }
    
    private void setSampleTypesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        typeOfSampleId.setModel(model);
    }
    
    private void setContainersModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        containerId.setModel(model);
    }
    
    private void setUnitsModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        unitOfMeasureId.setModel(model);
    }
    
    public void setItemDO(SampleItemDO itemDO) {
        sampleItem = itemDO;
        loaded = false;
         
         if(!dropdownsInited) {
             setSampleTypesModel(DictionaryCache.getListByCategorySystemName("type_of_sample"));
             setContainersModel(DictionaryCache.getListByCategorySystemName("sample_container"));
             setUnitsModel(DictionaryCache.getListByCategorySystemName("unit_of_measure"));
             dropdownsInited = true;
         }                
     }
     
     public void draw(){
         if(!loaded)
             DataChangeEvent.fire(this);
         
         loaded = true;
     }
}
