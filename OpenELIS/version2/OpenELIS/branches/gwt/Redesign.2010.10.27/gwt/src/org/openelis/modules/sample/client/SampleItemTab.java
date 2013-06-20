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

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.manager.SampleDataBundle;
import org.openelis.meta.SampleMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SampleItemTab extends Screen implements HasActionHandlers<SampleItemTab.Action> {
    public enum Action {
        CHANGED
    };

    private boolean            loaded;

    protected SampleDataBundle bundle;
    protected SampleItemViewDO sampleItem;
    protected TextBox          sourceOther, containerReference, quantity;
    protected Dropdown<Integer> typeOfSampleId, sourceOfSampleId, containerId, unitOfMeasureId;

    public SampleItemTab(ScreenDefInt def, Window window) {
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        final SampleItemTab itemTab = this;

        typeOfSampleId = (Dropdown)def.getWidget(SampleMeta.getItemTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeOfSampleId.setValue(sampleItem.getTypeOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setTypeOfSampleId(event.getValue());
                sampleItem.setTypeOfSample(typeOfSampleId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                             .contains(event.getState()));
                typeOfSampleId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sourceOfSampleId = (Dropdown)def.getWidget(SampleMeta.getItemSourceOfSampleId());
        addScreenHandler(sourceOfSampleId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOfSampleId.setValue(sampleItem.getSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setSourceOfSampleId(event.getValue());
                sampleItem.setSourceOfSample(sourceOfSampleId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sourceOfSampleId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                sourceOfSampleId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sourceOther = (TextBox)def.getWidget(SampleMeta.getItemSourceOther());
        addScreenHandler(sourceOther, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOther.setValue(sampleItem.getSourceOther());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setSourceOther(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sourceOther.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                sourceOther.setQueryMode(event.getState() == State.QUERY);
            }
        });

        containerId = (Dropdown)def.getWidget(SampleMeta.getItemContainerId());
        addScreenHandler(containerId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                containerId.setValue(sampleItem.getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setContainerId(event.getValue());
                sampleItem.setContainer(containerId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
                containerId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        containerReference = (TextBox)def.getWidget(SampleMeta.getItemContainerReference());
        addScreenHandler(containerReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                containerReference.setValue(sampleItem.getContainerReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setContainerReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                containerReference.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE,
                                                     State.DELETE).contains(event.getState()));
                containerReference.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantity = (TextBox)def.getWidget(SampleMeta.getItemQuantity());
        addScreenHandler(quantity, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                quantity.setValue(sampleItem.getQuantity());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                sampleItem.setQuantity((Double)quantity.getValue());

            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantity.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                       .contains(event.getState()));
                quantity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        unitOfMeasureId = (Dropdown)def.getWidget(SampleMeta.getItemUnitOfMeasureId());
        addScreenHandler(unitOfMeasureId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                unitOfMeasureId.setValue(sampleItem.getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unitOfMeasureId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE,
                                                  State.DELETE).contains(event.getState()));
                unitOfMeasureId.setQueryMode(event.getState() == State.QUERY);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        Item<Integer>            row;

        // sample type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("type_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeOfSampleId.setModel(model);

        // source dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("source_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        sourceOfSampleId.setModel(model);

        // sample container dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("sample_container")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        containerId.setModel(model);

        // unit of measure dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("unit_of_measure")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        unitOfMeasureId.setModel(model);
    }

    public void setData(SampleDataBundle data) {
        try{
            if (data == null) {
                sampleItem = new SampleItemViewDO();
    
                if (state != State.QUERY)
                    StateChangeEvent.fire(this, State.DEFAULT);
            } else {
                sampleItem = data.getSampleManager().getSampleItems().getSampleItemAt(data.getSampleItemIndex());
    
                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            }
            
            bundle = data;
            loaded = false;
        }catch(Exception e){
            com.google.gwt.user.client.Window.alert("sampleItemTab setData: "+e.getMessage());
        }
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}