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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SampleItemTabUI extends Screen {

    @UiTemplate("SampleItemTab.ui.xml")
    interface SampleItemTabUIBinder extends UiBinder<Widget, SampleItemTabUI> {
    };

    private static SampleItemTabUIBinder uiBinder = GWT.create(SampleItemTabUIBinder.class);

    @UiField
    protected TextBox<String>            sourceOther, containerReference;

    @UiField
    protected TextBox<Double>            quantity;

    @UiField
    protected Dropdown<Integer>          typeOfSample, sourceOfSample, container, unitOfMeasure;

    protected Screen                     parentScreen;

    protected SampleItemTabUI            screen;

    protected EventBus                   parentBus;

    protected SampleManager1             manager;

    protected SampleItemViewDO           sampleItem;

    protected String                     displayedUid;

    protected boolean                    canEdit, isVisible, redraw, hasReleasedAnalysis, canQuery;

    public SampleItemTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;

        screen = this;

        addScreenHandler(typeOfSample,
                         SampleMeta.getItemTypeOfSampleId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 typeOfSample.setValue(getTypeOfSampleId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setTypeOfSample(event.getValue(), typeOfSample.getDisplay());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 typeOfSample.setEnabled( (isState(QUERY) && canQuery) ||
                                                         (isState(ADD, UPDATE) && canEdit && !hasReleasedAnalysis));
                                 typeOfSample.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sourceOfSample : unitOfMeasure;
                             }
                         });

        addScreenHandler(sourceOfSample,
                         SampleMeta.getItemSourceOfSampleId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sourceOfSample.setValue(getSourceOfSampleId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSourceOfSample(event.getValue(), sourceOfSample.getDisplay());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sourceOfSample.setEnabled( (isState(QUERY) && canQuery) ||
                                                           (isState(ADD, UPDATE) && canEdit));
                                 sourceOfSample.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sourceOther : typeOfSample;
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
                sourceOther.setEnabled( (isState(QUERY) && canQuery) ||
                                       (isState(ADD, UPDATE) && canEdit));
                sourceOther.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? container : sourceOfSample;
            }
        });

        addScreenHandler(container, SampleMeta.getItemContainerId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                container.setValue(getContainerId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setContainer(event.getValue(), container.getDisplay());
            }

            public void onStateChange(StateChangeEvent event) {
                container.setEnabled( (isState(QUERY) && canQuery) ||
                                     (isState(ADD, UPDATE) && canEdit));
                container.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? containerReference : sourceOther;
            }
        });

        addScreenHandler(containerReference,
                         SampleMeta.getItemContainerReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 containerReference.setValue(getContainerReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setContainerReference(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 containerReference.setEnabled( (isState(QUERY) && canQuery) ||
                                                               (isState(ADD, UPDATE) && canEdit));
                                 containerReference.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? quantity : container;
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
                quantity.setEnabled( (isState(QUERY) && canQuery) ||
                                    (isState(ADD, UPDATE) && canEdit));
                quantity.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? unitOfMeasure : containerReference;
            }
        });

        addScreenHandler(unitOfMeasure,
                         SampleMeta.getItemUnitOfMeasureId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 unitOfMeasure.setValue(getUnitOfMeasureId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setUnitOfMeasureId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 unitOfMeasure.setEnabled( (isState(QUERY) && canQuery) ||
                                                          (isState(ADD, UPDATE) && canEdit));
                                 unitOfMeasure.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? typeOfSample : quantity;
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (sampleItem != null)
                    uid = Constants.uid().get(sampleItem);
                else
                    uid = null;
                displaySampleItem(uid);
            }
        });

        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid;
                AnalysisDO a;

                switch (event.getSelectedType()) {
                    case SAMPLE_ITEM:
                        uid = event.getUid();
                        break;
                    case ANALYSIS:
                        a = (AnalysisDO)manager.getObject(event.getUid());
                        uid = Constants.uid().getSampleItem(a.getSampleItemId());
                        break;
                    default:
                        uid = null;
                        break;
                }

                if (uid != null)
                    sampleItem = (SampleItemViewDO)manager.getObject(uid);
                else
                    sampleItem = null;

                /*
                 * The widgets are compared with the sample item's fields to
                 * reload the tab even if the current uid is the same as
                 * previous but the data in some fields is different; this can
                 * happen on complete and release screen, where the selected
                 * analysis' manager may be replaced with a locked and refetched
                 * manager containing changes from the database.
                 */
                if (DataBaseUtil.isDifferent(displayedUid, uid) ||
                    DataBaseUtil.isDifferent(typeOfSample.getValue(), getTypeOfSampleId()) ||
                    DataBaseUtil.isDifferent(sourceOfSample.getValue(), getSourceOfSampleId()) ||
                    DataBaseUtil.isDifferent(sourceOther.getValue(), getSourceOther()) ||
                    DataBaseUtil.isDifferent(container.getValue(), getContainerId()) ||
                    DataBaseUtil.isDifferent(containerReference.getValue(), getContainerReference()) ||
                    DataBaseUtil.isDifferent(quantity.getValue(), getQuantity()) ||
                    DataBaseUtil.isDifferent(unitOfMeasure.getValue(), getUnitOfMeasureId())) {
                    redraw = true;
                }

                setState(state);
                displaySampleItem(uid);
            }
        });

        // sample type dropdown
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("type_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        typeOfSample.setModel(model);

        // source dropdown
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("source_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        sourceOfSample.setModel(model);

        // sample container dropdown
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_container")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        container.setModel(model);

        // unit of measure dropdown
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        unitOfMeasure.setModel(model);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setCanQuery(boolean canQuery) {
        this.canQuery = canQuery;
    }

    public void setFocus() {
        /*
         * set the first widget in the tabbing order in focus, i.e. sample type
         * if it's enabled
         */
        typeOfSample.setFocus(typeOfSample.isEnabled());
    }

    private void evaluateEdit() {
        canEdit = false;
        hasReleasedAnalysis = false;
        if (manager != null && sampleItem != null) {
            canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                            .getStatusId());
            hasReleasedAnalysis = manager.analysis.hasReleasedAnalysis(sampleItem);
        }
    }

    private void displaySampleItem(String uid) {
        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            displayedUid = uid;
            fireDataChange();
        }
    }

    private Integer getTypeOfSampleId() {
        if (sampleItem != null)
            return sampleItem.getTypeOfSampleId();

        return null;
    }

    private void setTypeOfSample(Integer typeId, String display) {
        sampleItem.setTypeOfSampleId(typeId);
        sampleItem.setTypeOfSample(display);
        parentBus.fireEvent(new SampleItemChangeEvent(displayedUid,
                                                      SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED));
    }

    private Integer getSourceOfSampleId() {
        if (sampleItem != null)
            return sampleItem.getSourceOfSampleId();

        return null;
    }

    private void setSourceOfSample(Integer sourceId, String display) {
        sampleItem.setSourceOfSampleId(sourceId);
        sampleItem.setSourceOfSample(display);
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
        parentBus.fireEvent(new SampleItemChangeEvent(displayedUid,
                                                      SampleItemChangeEvent.Action.CONTAINER_CHANGED));
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