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
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.qaevent.client.QAEventLookupUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class QAEventTabUI extends Screen {

    @UiTemplate("QAEventTab.ui.xml")
    interface QAEventsTabUIBinder extends UiBinder<Widget, QAEventTabUI> {
    };

    private static QAEventsTabUIBinder uiBinder = GWT.create(QAEventsTabUIBinder.class);

    @UiField
    protected Table                    sampleQATable, analysisQATable;

    @UiField
    protected Dropdown<Integer>        sampleQAType, analysisQAType;

    @UiField
    protected Button                   removeSampleQAButton, sampleQALookupButton,
                    removeAnalysisQAButton, analysisQALookupButton;

    @UiField
    protected Label<String>            sampleBillableLabel, analysisBillableLabel;

    protected Screen                   parentScreen;

    protected QAEventTabUI            screen;

    protected SampleManager1           manager, displayedManager;

    protected AnalysisViewDO           analysis;

    protected boolean                  sampleOrAnyAnaReleased, canEditAnalysisQA, isVisible, redraw;

    protected String                   displayedUid;

    protected QAEventLookupUI          qaEventLookup;

    public QAEventTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
        displayedUid = null;
    }

    private void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;

        screen = this;

        addScreenHandler(sampleQATable, "sampleQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.setModel(getSampleQATableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                sampleQATable.setEnabled(true);
            }
        });

        sampleQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeSampleQAButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || event.getCol() == 0 || sampleOrAnyAnaReleased)
                    event.cancel();
            }
        });

        sampleQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                SampleQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = sampleQATable.getValueAt(r, c);
                data = manager.qaEvent.get(r);

                if ( !Window.confirm(Messages.get().gen_qaEventEditConfirm())) {
                    switch (c) {
                        case 1:
                            sampleQATable.setValueAt(r, c, data.getTypeId());
                            break;
                        case 2:
                            sampleQATable.setValueAt(r, c, data.getIsBillable());
                            break;
                    }
                } else {
                    switch (c) {
                        case 1:
                            data.setTypeId((Integer)val);
                            notifyQAChanged(null);
                            break;
                        case 2:
                            data.setIsBillable((String)val);
                            showSampleBillableMessage();
                            break;
                    }
                }
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.qaEvent.remove(event.getIndex());
                removeSampleQAButton.setEnabled(false);
                showSampleBillableMessage();
            }
        });

        addScreenHandler(removeSampleQAButton, "removeSampleQAButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeSampleQAButton.setEnabled(false);
            }
        });

        addScreenHandler(sampleQALookupButton, "sampleQALookupButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                sampleQALookupButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addScreenHandler(sampleBillableLabel, "sampleBillableLabel", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                showSampleBillableMessage();
            }
        });

        addScreenHandler(analysisQATable, "analysisQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisQATable.setModel(getAnalysisQATableModel());
                showAnalysisBillableMessage();
            }

            public void onStateChange(StateChangeEvent event) {
                analysisQATable.setEnabled(true);
            }
        });

        analysisQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeAnalysisQAButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || event.getCol() == 0 || !canEditAnalysisQA)
                    event.cancel();
            }
        });

        analysisQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = analysisQATable.getValueAt(r, c);
                data = manager.qaEvent.get(analysis, r);

                if ( !Window.confirm(Messages.get().gen_qaEventEditConfirm())) {
                    switch (c) {
                        case 1:
                            analysisQATable.setValueAt(r, c, data.getTypeId());
                            break;
                        case 2:
                            analysisQATable.setValueAt(r, c, data.getIsBillable());
                            break;
                    }
                } else {
                    switch (c) {
                        case 1:
                            data.setTypeId((Integer)val);
                            notifyQAChanged(analysis.getId());
                            break;
                        case 2:
                            data.setIsBillable((String)val);
                            showAnalysisBillableMessage();
                            break;
                    }
                }
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.qaEvent.remove(analysis, event.getIndex());
                removeAnalysisQAButton.setEnabled(false);
                showAnalysisBillableMessage();
            }
        });

        addScreenHandler(removeAnalysisQAButton,
                         "removeAnalysisQAButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 removeAnalysisQAButton.setEnabled(false);
                             }
                         });

        addScreenHandler(analysisQALookupButton,
                         "analysisQALookupButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 analysisQALookupButton.setEnabled(isState(ADD, UPDATE) &&
                                                                   canEditAnalysisQA);
                             }
                         });

        addScreenHandler(analysisBillableLabel,
                         "analysisBillableLabel",
                         new ScreenHandler<Object>() {
                             public void onDataChange(DataChangeEvent event) {
                                 showAnalysisBillableMessage();
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = manager.getUid(analysis);
                else
                    uid = null;

                displayQAEvents(uid);
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                int i, count1, count2;
                String uid;
                SampleQaEventViewDO sqa1, sqa2;
                AnalysisQaEventViewDO aqa1, aqa2;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }
                
                if (DataBaseUtil.isDifferent(displayedUid, uid)) {
                    displayedUid = uid;
                    redraw = true;
                } else {
                    /*
                     * compare sample qa events
                     */
                    count1 = displayedManager == null ? 0 : displayedManager.qaEvent.count();
                    count2 = manager == null ? 0 : manager.qaEvent.count();

                    if (count1 == count2) {
                        for (i = 0; i < count1; i++ ) {
                            sqa1 = displayedManager.qaEvent.get(i);
                            sqa2 = manager.qaEvent.get(i);
                            if (DataBaseUtil.isDifferent(sqa1.getId(), sqa2.getId()) ||
                                DataBaseUtil.isDifferent(sqa1.getQaEventId(), sqa2.getQaEventId()) ||
                                DataBaseUtil.isDifferent(sqa1.getTypeId(), sqa2.getTypeId()) ||
                                DataBaseUtil.isDifferent(sqa1.getIsBillable(), sqa1.getIsBillable())) {
                                redraw = true;
                                break;
                            }
                        }
                    } else {
                        redraw = true;
                    }

                    if ( !redraw && analysis != null) {
                        /*
                         * compare analysis qa events
                         */
                        count1 = displayedManager == null ? 0 : displayedManager.qaEvent.count(analysis);
                        count2 = manager == null ? 0 : manager.qaEvent.count(analysis);

                        if (count1 == count2) {
                            for (i = 0; i < count1; i++ ) {
                                aqa1 = displayedManager.qaEvent.get(analysis, i);
                                aqa2 = manager.qaEvent.get(analysis, i);
                                if (DataBaseUtil.isDifferent(aqa1.getId(), aqa2.getId()) ||
                                    DataBaseUtil.isDifferent(aqa1.getQaeventId(), aqa2.getQaeventId()) ||
                                    DataBaseUtil.isDifferent(aqa1.getTypeId(), aqa2.getTypeId()) ||
                                    DataBaseUtil.isDifferent(aqa1.getIsBillable(), aqa2.getIsBillable())) {
                                    redraw = true;
                                    break;
                                }
                            }
                        } else {
                            redraw = true;
                        }
                    }
                }

                displayQAEvents(uid);
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) ||
                    AnalysisChangeEvent.Action.SECTION_CHANGED.equals(event.getAction())) {
                    /*
                     * reevaluate the permissions for this section or status to
                     * enable or disable the widgets in the tab
                     */
                    evaluateEdit();
                    setState(state);
                }
            }
        });

        // qa event type dropdown
        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("qaevent_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        sampleQAType.setModel(model);
        analysisQAType.setModel(model);
    }

    public void setData(SampleManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        sampleOrAnyAnaReleased = false;
        canEditAnalysisQA = false;

        if (manager != null) {
            sampleOrAnyAnaReleased = Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                          .getStatusId()) ||
                                     manager.analysis.hasReleasedAnalysis();

            perm = null;
            sectId = getSectionId();
            statId = getStatusId();

            try {
                if (sectId != null) {
                    sect = SectionCache.getById(sectId);
                    perm = UserCache.getPermission().getSection(sect.getName());
                }
                canEditAnalysisQA = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                                    perm != null &&
                                    (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception e) {
                Window.alert("canEdit:" + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void displayQAEvents(String uid) {
        if (uid != null)
            analysis = (AnalysisViewDO)manager.getObject(uid);
        else
            analysis = null;

        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    @UiHandler("sampleQALookupButton")
    protected void sampleQALookup(ClickEvent event) {
        showQAEventLookup(null);
    }

    @UiHandler("removeSampleQAButton")
    protected void removeSampleQA(ClickEvent event) {
        int r;
        SampleQaEventViewDO data;

        r = sampleQATable.getSelectedRow();
        if (r < 0)
            return;
        data = manager.qaEvent.get(r);
        parentScreen.getWindow().clearStatus();
        /*
         * allow removal of only internal qa events if sample is released or any
         * analysis is released
         */
        if (sampleOrAnyAnaReleased &&
            !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId())) {
            parentScreen.getWindow().setError(Messages.get().sample_cantRemoveQAEvent());
        } else {
            sampleQATable.removeRowAt(r);
            notifyQAChanged(null);
        }
    }

    @UiHandler("analysisQALookupButton")
    protected void analysisQALookup(ClickEvent event) {
        showQAEventLookup(analysis.getTestId());
    }

    @UiHandler("removeAnalysisQAButton")
    protected void removeAnalysisQA(ClickEvent event) {
        int r;
        AnalysisQaEventViewDO data;

        r = analysisQATable.getSelectedRow();
        if (r < 0)
            return;

        data = manager.qaEvent.get(analysis, r);
        parentScreen.getWindow().clearStatus();
        /*
         * allow removal of only internal qa events if the analysis is released
         */
        if (Constants.dictionary().ANALYSIS_RELEASED.equals(getStatusId()) &&
            !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId())) {
            parentScreen.getWindow().setError(Messages.get().analysis_cantRemoveQAEvent());
        } else {
            analysisQATable.removeRowAt(r);
            notifyQAChanged(analysis.getId());
        }
    }

    private ArrayList<Row> getSampleQATableModel() {
        SampleQaEventViewDO data;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.qaEvent.count(); i++ ) {
            data = manager.qaEvent.get(i);
            row = new Row(3);
            row.setCell(0, data.getQaEventName());
            row.setCell(1, data.getTypeId());
            row.setCell(2, data.getIsBillable());
            model.add(row);
        }

        return model;
    }

    private ArrayList<Row> getAnalysisQATableModel() {
        AnalysisQaEventViewDO data;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null || analysis == null)
            return model;

        for (int i = 0; i < manager.qaEvent.count(analysis); i++ ) {
            data = manager.qaEvent.get(analysis, i);
            row = new Row(3);
            row.setCell(0, data.getQaEventName());
            row.setCell(1, data.getTypeId());
            row.setCell(2, data.getIsBillable());
            model.add(row);
        }

        return model;
    }

    private void showQAEventLookup(Integer testId) {
        ModalWindow modal;

        if (qaEventLookup == null) {
            qaEventLookup = new QAEventLookupUI() {

                @Override
                public void ok() {
                    boolean released, showError;
                    String error;
                    AnalysisQaEventViewDO aqa;
                    SampleQaEventViewDO sqa;
                    Row row;

                    if (qaEventLookup.getSelectedQAEvents() == null)
                        return;

                    showError = false;
                    error = null;
                    if (qaEventLookup.getTestId() != null) {
                        /*
                         * add analysis qa events
                         */
                        released = Constants.dictionary().ANALYSIS_RELEASED.equals(analysis.getStatusId());
                        for (QaEventDO data : qaEventLookup.getSelectedQAEvents()) {
                            /*
                             * if the analysis is released then only internal qa
                             * events can be added to it
                             */
                            if (released &&
                                !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId())) {
                                showError = true;
                                error = Messages.get().analysis_cantAddQAEvent();
                                continue;
                            }

                            aqa = manager.qaEvent.add(analysis, data);
                            row = new Row(3);
                            row.setCell(0, aqa.getQaEventName());
                            row.setCell(1, aqa.getTypeId());
                            row.setCell(2, aqa.getIsBillable());
                            analysisQATable.addRow(row);
                            showAnalysisBillableMessage();
                        }
                        notifyQAChanged(analysis.getId());
                    } else {
                        /*
                         * add sample qa events
                         */
                        released = Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                        .getStatusId());
                        for (QaEventDO data : qaEventLookup.getSelectedQAEvents()) {
                            /*
                             * if the sample is released then only internal qa
                             * events can be added to it
                             */
                            if (released &&
                                !Constants.dictionary().QAEVENT_INTERNAL.equals(data.getTypeId())) {
                                showError = true;
                                error = Messages.get().sample_cantAddQAEvent();
                                continue;
                            }

                            sqa = manager.qaEvent.add(data);
                            row = new Row(3);
                            row.setCell(0, sqa.getQaEventName());
                            row.setCell(1, sqa.getTypeId());
                            row.setCell(2, sqa.getIsBillable());
                            sampleQATable.addRow(row);
                            showSampleBillableMessage();
                        }
                        notifyQAChanged(null);
                    }

                    if (showError)
                        parentScreen.getWindow().setError(error);
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("520px", "350px");
        modal.setName(Messages.get().qaEventSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(qaEventLookup);

        qaEventLookup.setWindow(modal);
        qaEventLookup.setData(testId);
    }

    private void showSampleBillableMessage() {
        if (manager != null && manager.qaEvent.hasNonBillable())
            sampleBillableLabel.setText(Messages.get().sample_notBillable());
        else
            sampleBillableLabel.setText("");

    }

    private void showAnalysisBillableMessage() {
        if (analysis != null && manager.qaEvent.hasNonBillable(analysis))
            analysisBillableLabel.setText(Messages.get().analysis_notBillable());
        else
            analysisBillableLabel.setText("");
    }

    /**
     * notify any tabs e.g. result tab that the qa events have changed so that
     * they can take some action based on this e.g. show the user a message
     */
    private void notifyQAChanged(Integer analysisId) {
        String uid;

        if (analysisId == null)
            uid = null;
        else
            uid = manager.getAnalysisUid(analysisId);
        bus.fireEvent(new QAEventChangeEvent(uid));
    }
}