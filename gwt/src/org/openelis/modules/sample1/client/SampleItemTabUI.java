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
package org.openelis.modules.sample1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.manager.SampleDataBundle;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SampleItemTabUI extends Screen implements HasActionHandlers<SampleItemTabUI.Action> {
    
    @UiTemplate("SampleItemTab.ui.xml")
    interface SampleItemTabUIBinder extends UiBinder<Widget, SampleItemTabUI> {        
    };
    
    private static SampleItemTabUIBinder uiBinder = GWT.create(SampleItemTabUIBinder.class);
    
    public enum Action {
        CHANGED
    };

    protected SampleDataBundle  bundle;
    protected SampleItemViewDO  sampleItem;
    
    @UiField
    protected TextBox<String>    sourceOther, containerReference;
    
    @UiField
    protected TextBox<Double>  quantity;
    
    @UiField
    protected Dropdown<Integer> typeOfSampleId, sourceOfSampleId, containerId, unitOfMeasureId;

    public SampleItemTabUI() {
        initWidget(uiBinder.createAndBindUi(this));
        
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        final SampleItemTabUI itemTab = this;

        addScreenHandler(typeOfSampleId, SampleMeta.getItemTypeOfSampleId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeOfSampleId.setValue(sampleItem.getTypeOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setTypeOfSampleId(event.getValue());
                sampleItem.setTypeOfSample(typeOfSampleId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent event) {                
                typeOfSampleId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                typeOfSampleId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(sourceOfSampleId, SampleMeta.getItemSourceOfSampleId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOfSampleId.setValue(sampleItem.getSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setSourceOfSampleId(event.getValue());
                sampleItem.setSourceOfSample(sourceOfSampleId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent event) {
                sourceOfSampleId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                sourceOfSampleId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(sourceOther, SampleMeta.getItemSourceOther(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOther.setValue(sampleItem.getSourceOther());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setSourceOther(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sourceOther.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                sourceOther.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(containerId, SampleMeta.getItemContainerId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                containerId.setValue(sampleItem.getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setContainerId(event.getValue());
                sampleItem.setContainer(containerId.getDisplay());
                ActionEvent.fire(itemTab, Action.CHANGED, null);
            }

            public void onStateChange(StateChangeEvent event) {
                containerId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                containerId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(containerReference, SampleMeta.getItemContainerReference(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                containerReference.setValue(sampleItem.getContainerReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                sampleItem.setContainerReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                containerReference.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                containerReference.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(quantity, SampleMeta.getItemQuantity(), new ScreenHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                quantity.setValue(sampleItem.getQuantity());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                sampleItem.setQuantity((Double)quantity.getValue());

            }

            public void onStateChange(StateChangeEvent event) {
                quantity.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                quantity.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(unitOfMeasureId, SampleMeta.getItemUnitOfMeasureId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                unitOfMeasureId.setValue(sampleItem.getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleItem.setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {                              
                unitOfMeasureId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                unitOfMeasureId.setQueryMode(isState(QUERY));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        Item<Integer>            row;

        // sample type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("type_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeOfSampleId.setModel(model);

        // source dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("source_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        sourceOfSampleId.setModel(model);

        // sample container dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_container")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        containerId.setModel(model);

        // unit of measure dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        unitOfMeasureId.setModel(model);
    }

    public void setData(SampleDataBundle data) {
        /*try{
            bundle = data;
            if (data == null) {
                sampleItem = new SampleItemViewDO();
    
                if (state != State.QUERY)
                    StateChangeEvent.fire(this, State.DEFAULT);
            } else {
                sampleItem = data.getSampleManager().getSampleItems().getSampleItemAt(data.getSampleItemIndex());
    
                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            }
            loaded = false;
        }catch(Exception e){
            Window.alert("sampleItemTab setData: "+e.getMessage());
        }*/
    }

    public void draw() {
           // DataChangeEvent.fire(this);

        //loaded = true;
    }
    
    private boolean canEdit() {
        return (bundle != null && bundle.getSampleManager() != null &&
                !Constants.dictionary().SAMPLE_RELEASED.equals(bundle.getSampleManager().getSample().getStatusId()));
    }
    
    private boolean itemHasReleasedAnalyses() {
        boolean hasReleased = true;
        
        try {
            if (bundle != null && bundle.getSampleManager() != null)
                hasReleased = bundle.getSampleManager().getSampleItems()
                                    .getAnalysisAt(bundle.getSampleItemIndex()).hasReleasedAnalysis();
        } catch (Exception anyE) {
            Window.alert("sampleItemTab itemHasReleasedAnalyses: "+anyE.getMessage());
        }
        
        return hasReleased;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
