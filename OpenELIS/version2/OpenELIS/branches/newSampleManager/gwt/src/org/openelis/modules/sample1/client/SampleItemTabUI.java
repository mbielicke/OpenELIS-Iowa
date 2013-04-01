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
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.event.StateChangeEvent.Handler;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SampleItemTabUI extends Screen implements HasActionHandlers<SampleItemTabUI.Action> {
    
    @UiTemplate("SampleItemTab.ui.xml")
    interface SampleItemTabUIBinder extends UiBinder<Widget, SampleItemTabUI> {        
    };
    
    private static SampleItemTabUIBinder uiBinder = GWT.create(SampleItemTabUIBinder.class);
    
    public enum Action {
        CHANGED
    };
    
    @UiField
    protected TextBox<String>   sourceOther, containerReference;
    
    @UiField
    protected TextBox<Double>   quantity;
    
    @UiField
    protected Dropdown<Integer> typeOfSampleId, sourceOfSampleId, containerId, unitOfMeasureId;
    
    protected SampleItemTabUI   screen;

    protected boolean          loaded;
    
    protected SampleManager1    manager;
    
    protected SampleItemViewDO  sampleItem;
    
    protected Screen            parentScreen;
    
    protected boolean          canEdit, canQuery;
    

    public SampleItemTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        manager = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer>            row;
        
        screen = this;
        
        addScreenHandler(typeOfSampleId, SampleMeta.getItemTypeOfSampleId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeOfSampleId.setValue(getTypeOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setTypeOfSample(event.getValue(), typeOfSampleId.getDisplay());
            }

            public void onStateChange(StateChangeEvent event) {     
                typeOfSampleId.setEnabled(canEdit);
                typeOfSampleId.setQueryMode(canQuery);
            }
        });

        addScreenHandler(sourceOfSampleId, SampleMeta.getItemSourceOfSampleId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOfSampleId.setValue(getSourceOfSampleId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setSourceOfSample(event.getValue(), sourceOfSampleId.getDisplay());
            }

            public void onStateChange(StateChangeEvent event) {
                sourceOfSampleId.setEnabled(canEdit);
                sourceOfSampleId.setQueryMode(canQuery);
            }
        });

        addScreenHandler(sourceOther, SampleMeta.getItemSourceOther(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sourceOther.setValue(getSourceOther());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setSourceOther(event);
            }

            public void onStateChange(StateChangeEvent event) {
                sourceOther.setEnabled(canEdit);
                sourceOther.setQueryMode(canQuery);
            }
        });

        addScreenHandler(containerId, SampleMeta.getItemContainerId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                containerId.setValue(getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setContainer(event.getValue(), containerId.getDisplay());
            }

            public void onStateChange(StateChangeEvent event) {
                containerId.setEnabled(canEdit);
                containerId.setQueryMode(canQuery);
            }
        });

        addScreenHandler(containerReference, SampleMeta.getItemContainerReference(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                containerReference.setValue(getContainerReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setContainerReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                containerReference.setEnabled(canEdit);
                containerReference.setQueryMode(canQuery);
            }
        });

        addScreenHandler(quantity, SampleMeta.getItemQuantity(), new ScreenHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                quantity.setValue(getQuantity());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                setQuantity(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                quantity.setEnabled(canEdit);
                quantity.setQueryMode(canQuery);
            }
        });

        addScreenHandler(unitOfMeasureId, SampleMeta.getItemUnitOfMeasureId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                unitOfMeasureId.setValue(getUnitOfMeasureId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setUnitOfMeasureId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {   
                unitOfMeasureId.setEnabled(canEdit);
                unitOfMeasureId.setQueryMode(canQuery);
            }
        });
        
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
        
        /*
         * handlers for the events fired by the screen containing this tab 
         */
        bus.addHandlerToSource(StateChangeEvent.getType(), parentScreen, new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {                
                setState(event.getState());
                evaluateEdit();
            }
        });
        
        bus.addHandlerToSource(SelectionEvent.getType(), parentScreen, new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid, oldUid;
                AnalysisDO a;
                                     
                switch (event.getSelectedType()) {
                    case SAMPLE_ITEM:
                        uid = event.getUid();
                        break;
                    case ANALYSIS:
                        a = (AnalysisDO)manager.getObject(event.getUid());
                        uid = manager.getSampleItemUid(a.getSampleItemId());
                        break;
                    default:
                        uid = null;
                        break;
                }             
                
                /*
                 * don't redraw unless the data has changed
                 */
                oldUid = null;
                if (sampleItem != null)
                    oldUid = manager.getSampleItemUid(sampleItem.getId());
                if (DataBaseUtil.isDifferent(oldUid, uid)) {
                    sampleItem = null;
                    if (uid != null) 
                        sampleItem = (SampleItemViewDO)manager.getObject(uid);
                    
                    evaluateEdit();
                    setState(state);
                    fireDataChange();
                }
            }
        });
    }

    public void setData(SampleManager1 manager) {        
        if (!DataBaseUtil.isSame(this.manager, manager)) {
            this.manager = manager;
            evaluateEdit();
        }        
    }
    
    public void evaluateEdit() {
        canEdit = canQuery = false;
        if (isState(QUERY)) {
            canEdit = canQuery = true;
        } else if (manager != null) {
            if (isState(ADD, UPDATE))
                canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId());
        }  
    }
    
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }
    
    private boolean itemHasReleasedAnalyses() {
        /*boolean hasReleased = true;
        
        try {
            if (manager != null)
                hasReleased = bundle.getSampleManager().getSampleItems()
                                    .getAnalysisAt(bundle.getSampleItemIndex()).hasReleasedAnalysis();
        } catch (Exception anyE) {
            Window.alert("sampleItemTab itemHasReleasedAnalyses: "+anyE.getMessage());
        }
        
        return hasReleased;*/
        
        //TODO not in original code
        return false;
    }    

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    private Integer getTypeOfSampleId() {
        if (sampleItem != null)
            return sampleItem.getTypeOfSampleId();
        
        return null;
    }
    
    private void setTypeOfSample(Integer typeId, String display) {
        sampleItem.setTypeOfSampleId(typeId);
        sampleItem.setTypeOfSample(display);
        ActionEvent.fire(screen, Action.CHANGED, null);
    }
    
    private Integer getSourceOfSampleId() {
        if (sampleItem != null)
            return sampleItem.getSourceOfSampleId();
        
        return null;
    }
    
    private void setSourceOfSample(Integer sourceId, String display) {
        sampleItem.setSourceOfSampleId(sourceId);
        sampleItem.setSourceOfSample(display);
        ActionEvent.fire(screen, Action.CHANGED, null);
    }
    
    private String getSourceOther() {
        if (sampleItem != null)
            return sampleItem.getSourceOther();
        
        return null;
    }
    
    private void setSourceOther(ValueChangeEvent<String> event) {
        sampleItem.setSourceOther(event.getValue());
    }
    
    private Integer getContainerId() {
        if (sampleItem != null)
            return sampleItem.getContainerId();
        
        return null;
    }
    
    private void setContainer(Integer containerId, String display) {        
        sampleItem.setContainerId(containerId);
        sampleItem.setContainer(display);
        ActionEvent.fire(screen, Action.CHANGED, null);
    }
    
    private String getContainerReference() {
        if (sampleItem != null)
            return sampleItem.getContainerReference();
        
        return null; 
    }

    private void setContainerReference(String containerReference) {
        sampleItem.setContainerReference(containerReference);
    }
    
    private Double getQuantity() {
        if (sampleItem != null)
            return sampleItem.getQuantity();
        
        return null;
    }
    
    private void setQuantity(Double quantity) {
        sampleItem.setQuantity(quantity);
    }
    
    private Integer getUnitOfMeasureId() {
        if (sampleItem != null)
            return sampleItem.getUnitOfMeasureId();
        
        return null;
    }

    private void setUnitOfMeasureId(Integer unitId) {
        sampleItem.setUnitOfMeasureId(unitId);
    }
}