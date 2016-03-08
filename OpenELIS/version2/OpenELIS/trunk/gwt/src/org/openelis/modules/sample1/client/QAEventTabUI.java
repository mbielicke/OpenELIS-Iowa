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
import org.openelis.scriptlet.SampleSO.Action_Before;
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

    protected QAEventTabUI             screen;

    protected EventBus                 parentBus;

    protected SampleManager1           manager;

    protected AnalysisViewDO           analysis;

    protected boolean                  isVisible, redraw;

    protected String                   displayedUid;

    protected QAEventLookupUI          qaEventLookup;

    public QAEventTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;

        screen = this;

        addScreenHandler(sampleQATable, "sampleQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                sampleQATable.setModel(getSampleQATableModel());
                showSampleBillableMessage();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleQATable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? removeSampleQAButton : analysisQALookupButton;
            }
        });

        sampleQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeSampleQAButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || !canChangeSampleQA())
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
                removeSampleQAButton.setEnabled(isState(ADD, UPDATE) && sampleQATable.getSelectedRow() > -1);
            }

            public Widget onTab(boolean forward) {
                return forward ? sampleQALookupButton : sampleQATable;
            }
        });

        addScreenHandler(sampleQALookupButton, "sampleQALookupButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                sampleQALookupButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisQATable : removeSampleQAButton;
            }
        });

        addScreenHandler(analysisQATable, "analysisQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                analysisQATable.setModel(getAnalysisQATableModel());
                showAnalysisBillableMessage();
            }

            public void onStateChange(StateChangeEvent event) {
                analysisQATable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? removeAnalysisQAButton : sampleQALookupButton;
            }
        });

        analysisQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeAnalysisQAButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || !canChangeAnalysisQA())
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
                                 removeAnalysisQAButton.setEnabled(isState(ADD, UPDATE) && analysisQATable.getSelectedRow() > -1);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisQALookupButton : analysisQATable;
                             }
                         });

        addScreenHandler(analysisQALookupButton,
                         "analysisQALookupButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 analysisQALookupButton.setEnabled(isState(ADD, UPDATE) &&
                                                                   getTestId() != null);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sampleQATable : removeAnalysisQAButton;
                             }
                         });

        addScreenHandler(analysisBillableLabel,
                         "analysisBillableLabel",
                         new ScreenHandler<Object>() {
                             public void onDataChange(DataChangeEvent<Object> event) {
                                 showAnalysisBillableMessage();
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayQAEvents();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                int i, count1, count2;
                String uid;
                SampleQaEventViewDO sqa;
                AnalysisQaEventViewDO aqa;
                Row row;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }

                if (uid != null)
                    analysis = (AnalysisViewDO)manager.getObject(uid);
                else
                    analysis = null;

                /*
                 * compare sample qa events
                 */
                count1 = sampleQATable.getRowCount();
                count2 = manager == null ? 0 : manager.qaEvent.count();

                if (count1 == count2) {
                    for (i = 0; i < count1; i++ ) {
                        row = sampleQATable.getRowAt(i);
                        sqa = manager.qaEvent.get(i);
                        if (DataBaseUtil.isDifferent(sqa.getQaEventName(), row.getCell(0)) ||
                            DataBaseUtil.isDifferent(sqa.getTypeId(), row.getCell(1)) ||
                            DataBaseUtil.isDifferent(sqa.getIsBillable(), row.getCell(2))) {
                            redraw = true;
                            break;
                        }
                    }
                } else {
                    redraw = true;
                }

                if ( !redraw) {
                    count1 = analysisQATable.getRowCount();
                    if (analysis != null) {
                        /*
                         * compare analysis qa events
                         */
                        count2 = manager.qaEvent.count(analysis);

                        if (count1 == count2) {
                            for (i = 0; i < count1; i++ ) {
                                row = analysisQATable.getRowAt(i);
                                aqa = manager.qaEvent.get(analysis, i);
                                if (DataBaseUtil.isDifferent(aqa.getQaEventName(), row.getCell(0)) ||
                                    DataBaseUtil.isDifferent(aqa.getTypeId(), row.getCell(1)) ||
                                    DataBaseUtil.isDifferent(aqa.getIsBillable(), row.getCell(2))) {
                                    redraw = true;
                                    break;
                                }
                            }
                        } else {
                            redraw = true;
                        }
                    } else if (count1 > 0) {
                        /*
                         * if an analysis is not selected and the table for
                         * analysis qa events has some data then remove that
                         * data
                         */
                        redraw = true;
                    }
                }
                setState(state);
                displayQAEvents();
            }
        });

        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) ||
                    AnalysisChangeEvent.Action.SECTION_CHANGED.equals(event.getAction())) {
                    /*
                     * reevaluate the permissions for this section or status to
                     * enable or disable the widgets in the tab
                     */
                    analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                    setState(state);
                }
            }
        });
        
        parentBus.addHandler(QAEventAddedEvent.getType(), new QAEventAddedEvent.Handler() {
            @Override
            public void onQAEventAdded(QAEventAddedEvent event) {
                redraw = true;
                analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                displayQAEvents();
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
        this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setFocus() {
        /*
         * set the button for sample qa lookup in focus
         */
        if (isState(ADD, UPDATE))
            sampleQALookupButton.setFocus(true);
    }

    private void displayQAEvents() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    private Integer getTestId() {
        if (analysis != null)
            return analysis.getTestId();

        return null;
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
        parentScreen.clearStatus();
        /*
         * allow removal of only internal qa events if sample is released or any
         * analysis is released
         */
        if ( !canChangeSampleQA(data.getTypeId())) {
            parentScreen.setError(Messages.get().sample_cantRemoveQA());
        } else {
            sampleQATable.removeRowAt(r);
            parentBus.fireEventFromSource(new RunScriptletEvent(null,
                                                                null,
                                                                Action_Before.QA),
                                          screen);
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
        parentScreen.clearStatus();
        /*
         * allow removal of only internal qa events if the analysis is released;
         * don't allow removal of any qa events if the analysis is cancelled
         */
        if ( !canChangeAnalysisQA(data.getTypeId())) {
            if (Constants.dictionary().ANALYSIS_CANCELLED.equals(getStatusId()))
                parentScreen.setError(Messages.get().analysis_cantRemoveQACancelled());
            else
                parentScreen.setError(Messages.get().analysis_cantRemoveQAReleased());
        } else {
            analysisQATable.removeRowAt(r);
            parentBus.fireEventFromSource(new RunScriptletEvent(null,
                                                                null,
                                                                Action_Before.QA),
                                          screen);
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

    private void showQAEventLookup(final Integer testId) {
        ModalWindow modal;

        if (qaEventLookup == null) {
            qaEventLookup = new QAEventLookupUI() {

                @Override
                public void ok() {
                    boolean hasNonBillableQA, hasReleasedAna;
                    String error, message;
                    AnalysisQaEventViewDO aqa;
                    SampleQaEventViewDO sqa;
                    Row row;

                    if (qaEventLookup.getSelectedQAEvents() == null)
                        return;

                    error = null;
                    message = null;
                    if (qaEventLookup.getTestId() != null) {
                        /*
                         * find out if the analysis has any non-billable qa
                         * events
                         */
                        hasNonBillableQA = manager.qaEvent.hasNonBillable(analysis);

                        /*
                         * add analysis qa events
                         */
                        for (QaEventDO data : qaEventLookup.getSelectedQAEvents()) {
                            if ( !canChangeAnalysisQA(data.getTypeId())) {
                                /*
                                 * if the analysis is cancelled then no qa
                                 * events can be added to it; if it's released
                                 * then only internal qa events can be added to
                                 * it
                                 */
                                if (Constants.dictionary().ANALYSIS_CANCELLED.equals(getStatusId()))
                                    error = Messages.get().analysis_cantAddQACancelled();
                                else
                                    error = Messages.get().sample_cantAddQA();
                                continue;
                            }

                            aqa = manager.qaEvent.add(analysis, data);
                            /*
                             * if the analysis is released and has only billable
                             * qa events and the qa event to be added is
                             * not-billable then make it billable to prevent it
                             * from making a billable analysis not-billable
                             */
                            if (Constants.dictionary().ANALYSIS_RELEASED.equals(analysis.getStatusId()) &&
                                !hasNonBillableQA && "N".equals(data.getIsBillable())) {
                                aqa.setIsBillable("Y");
                                message = Messages.get()
                                                  .sample_changedToBillable(aqa.getQaEventName());
                            }
                            row = new Row(3);
                            row.setCell(0, aqa.getQaEventName());
                            row.setCell(1, aqa.getTypeId());
                            row.setCell(2, aqa.getIsBillable());
                            analysisQATable.addRow(row);
                            showAnalysisBillableMessage();
                            parentBus.fireEventFromSource(new RunScriptletEvent(Constants.uid()
                                                                                .getAnalysisQAEvent(aqa.getId()),
                                                                       null,
                                                                       Action_Before.QA),
                                                 screen);
                        }
                        notifyQAChanged(analysis.getId());
                    } else {
                        /*
                         * find out if the sample has any non-billable qa events
                         * and released analyses
                         */
                        hasNonBillableQA = manager.qaEvent.hasNonBillable();
                        hasReleasedAna = manager.analysis.hasReleasedAnalysis();

                        /*
                         * add sample qa events
                         */
                        for (QaEventDO data : qaEventLookup.getSelectedQAEvents()) {
                            /*
                             * if the sample is released or any of its analyses
                             * is released then only internal qa events can be
                             * added to it
                             */
                            if ( !canChangeSampleQA(data.getTypeId())) {
                                error = Messages.get().sample_cantAddQA();
                                continue;
                            }

                            sqa = manager.qaEvent.add(data);
                            /*
                             * if the sample is released and has only billable
                             * qa events and the qa event to be added is
                             * not-billable then make it billableto prevent it
                             * from making a billable sample, and its analyses,
                             * not-billable
                             */
                            if ( (Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                       .getStatusId()) || hasReleasedAna) &&
                                !hasNonBillableQA && "N".equals(data.getIsBillable())) {
                                sqa.setIsBillable("Y");
                                message = Messages.get()
                                                  .sample_changedToBillable(sqa.getQaEventName());
                            }
                            row = new Row(3);
                            row.setCell(0, sqa.getQaEventName());
                            row.setCell(1, sqa.getTypeId());
                            row.setCell(2, sqa.getIsBillable());
                            sampleQATable.addRow(row);
                            showSampleBillableMessage();
                            parentBus.fireEventFromSource(new RunScriptletEvent(Constants.uid()
                                                                                         .getSampleQAEvent(sqa.getId()),
                                                                                null,
                                                                                Action_Before.QA),
                                                          screen);
                        }
                        notifyQAChanged(null);
                    }

                    if (error != null)
                        parentScreen.setError(error);
                    else if (message != null)
                        parentScreen.setDone(message);
                    else
                        parentScreen.clearStatus();
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

    private boolean canChangeSampleQA() {
        return canChangeSampleQA(null);
    }

    private boolean canChangeSampleQA(Integer typeId) {
        return ( ( !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId()) && !manager.analysis.hasReleasedAnalysis()) || Constants.dictionary().QAEVENT_INTERNAL.equals(typeId));
    }

    private boolean canChangeAnalysisQA() {
        return canChangeAnalysisQA(null);
    }

    private boolean canChangeAnalysisQA(Integer typeId) {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        sectId = getSectionId();
        statId = getStatusId();
        if (sectId != null) {
            try {
                sect = SectionCache.getById(sectId);
                perm = UserCache.getPermission().getSection(sect.getName());
                return !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                       ( !Constants.dictionary().ANALYSIS_RELEASED.equals(statId) || Constants.dictionary().QAEVENT_INTERNAL.equals(typeId)) &&
                       perm != null && (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception e) {
                Window.alert("canEditAnalysisQA:" + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return false;
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
            uid = Constants.uid().getAnalysis(analysisId);
        parentBus.fireEvent(new QAEventChangeEvent(uid));
    }
}