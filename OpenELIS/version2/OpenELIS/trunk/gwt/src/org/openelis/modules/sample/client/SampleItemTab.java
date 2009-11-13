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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.metamap.SampleMetaMap;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class SampleItemTab extends Screen implements HasActionHandlers<SampleItemTab.Action> {
    public enum Action {CHANGED};
    private boolean loaded;
    
    private SampleMetaMap meta;
    protected SampleItemViewDO sampleItem;
    protected Dropdown<Integer> typeOfSampleId, sourceOfSampleId, containerId, unitOfMeasureId;

    public SampleItemTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        
        meta = new SampleMetaMap("sample.");
        
        initialize();
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                setSampleTypesModel(DictionaryCache.getListByCategorySystemName("type_of_sample"));
                setSourceModel(DictionaryCache.getListByCategorySystemName("source_of_sample"));
                setContainersModel(DictionaryCache.getListByCategorySystemName("sample_container"));
                setUnitsModel(DictionaryCache.getListByCategorySystemName("unit_of_measure"));
            }
       });
    }
    
    private void initialize() {
        final SampleItemTab itemTab = this;
        
        typeOfSampleId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeOfSampleId.setSelection(sampleItem.getTypeOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setTypeOfSampleId(event.getValue());
                sampleItem.setTypeOfSample(typeOfSampleId.getTextBoxDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                typeOfSampleId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sourceOfSampleId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getSourceOfSampleId());
        addScreenHandler(sourceOfSampleId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOfSampleId.setSelection(sampleItem.getSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setSourceOfSampleId(event.getValue());
                sampleItem.setSourceOfSample(sourceOfSampleId.getTextBoxDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sourceOfSampleId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sourceOfSampleId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        final TextBox sourceOther = (TextBox)def.getWidget(meta.SAMPLE_ITEM.getSourceOther());
        addScreenHandler(sourceOther, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOther.setValue(sampleItem.getSourceOther());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setSourceOther(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sourceOther.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sourceOther.setQueryMode(event.getState() == State.QUERY);
            }
        });

        containerId = (Dropdown)def.getWidget(meta.SAMPLE_ITEM.getContainerId());
        addScreenHandler(containerId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                containerId.setSelection(sampleItem.getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setContainerId(event.getValue());
                sampleItem.setContainer(containerId.getTextBoxDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
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
                quantity.setValue(getString(sampleItem.getQuantity()));
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                sampleItem.setQuantity((Double)quantity.getFieldValue());
                
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
    
    private void setSampleTypesModel(ArrayList<DictionaryDO> arrayList) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  arrayList){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        typeOfSampleId.setModel(model);
    }
    
    private void setSourceModel(ArrayList<DictionaryDO> arrayList) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  arrayList){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        sourceOfSampleId.setModel(model);
    }
    
    private void setContainersModel(ArrayList<DictionaryDO> arrayList) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  arrayList){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        containerId.setModel(model);
    }
    
    private void setUnitsModel(ArrayList<DictionaryDO> arrayList) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  arrayList){            
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        unitOfMeasureId.setModel(model);
    }
    
    public void setData(SampleDataBundle data) {
        if(data.sampleItemDO == null){
            sampleItem = new SampleItemViewDO();
            StateChangeEvent.fire(this, State.DEFAULT);   
        }else{
            sampleItem = data.sampleItemDO;
            
            if(state == State.ADD || state == State.UPDATE)
                StateChangeEvent.fire(this, State.UPDATE);
        }
        
        loaded = false;
    }
    
     public void draw(){
         if(!loaded)
             DataChangeEvent.fire(this);
         
         loaded = true;
     }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
