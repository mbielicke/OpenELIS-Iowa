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
import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.qaevent.client.QaeventLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class QAEventsTab extends Screen {
    private boolean                  loaded;
    protected SampleDataBundle       bundle;
    protected SampleDataBundle.Type  type;

    protected TableWidget            sampleQATable, analysisQATable;
    protected AutoComplete<Integer>  sampleQaEvent, analysisQaEvent;
    protected AppButton              removeAnalysisQAButton, removeSampleQAButton, sampleQAPicker,
                    analysisQAPicker;
    protected SampleQaEventManager   sampleQAManager;
    protected AnalysisQaEventManager analysisQAManager;
    protected SampleManager          sampleManager;
    protected AnalysisManager        anMan;
    protected AnalysisViewDO         anDO;
    protected Integer                analysisCancelledId, analysisReleasedId;

    protected QaeventLookupScreen    qaEventScreen;

    public QAEventsTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("controller?service=org.openelis.modules.qaevent.server.QaEventService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        sampleQATable = (TableWidget)def.getWidget("sampleQATable");
        addScreenHandler(sampleQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.load(getSampleQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQATable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                sampleQATable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sampleQATable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                //always allow selection
            }
        });

        sampleQATable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeSampleQAButton.enable(true);
            }
        });

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( (state == State.ADD || state == State.UPDATE) && !canEditSampleQA()) {
                    event.cancel();
                    removeSampleQAButton.enable(false);
                    sampleQAPicker.enable(false);
                } else {
                    removeSampleQAButton.enable(true);
                    sampleQAPicker.enable(true);
                }

                if (event.getCol() == 0 || !Window.confirm(consts.get("qaEventEditConfirm"))) {
                    event.cancel();
                }
            }
        });

        sampleQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;

                r = event.getRow();
                c = event.getCol();

                val = sampleQATable.getRow(r).cells.get(c).value;

                SampleQaEventViewDO qaDO;
                qaDO = sampleQAManager.getSampleQAAt(r);

                switch (c) {
                    case 1:
                        qaDO.setTypeId((Integer)val);
                        break;
                    case 2:
                        qaDO.setIsBillable((String)val);
                        break;
                }
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                sampleQAManager.removeSampleQAAt(event.getIndex());
                removeSampleQAButton.enable(false);
            }
        });

        removeSampleQAButton = (AppButton)def.getWidget("removeSampleQAButton");
        addScreenHandler(removeSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleQATable.getSelectedRow();
                if (selectedRow > -1 && sampleQATable.numRows() > 0) {
                    sampleQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSampleQAButton.enable(false);
            }
        });

        sampleQAPicker = (AppButton)def.getWidget("sampleQAPicker");
        addScreenHandler(sampleQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (qaEventScreen == null) {
                    createQaEventPickerScreen();
                }

                ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("qaEventSelection"));
                qaEventScreen.setType(QaeventLookupScreen.Type.SAMPLE);
                qaEventScreen.draw();
                modal.setContent(qaEventScreen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQAPicker.enable(EnumSet.of(State.ADD, State.UPDATE)
                                             .contains(event.getState()));
            }
        });

        analysisQATable = (TableWidget)def.getWidget("analysisQATable");
        addScreenHandler(analysisQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisQATable.load(getAnalysisQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQATable.enable( (State.QUERY == event.getState()) ||
                                       (canEditAnalysisQA() &&
                                        (SampleDataBundle.Type.ANALYSIS == type) &&
                                        anDO.getTestId() != null && EnumSet.of(State.ADD,
                                                                               State.UPDATE)
                                                                           .contains(
                                                                                     event.getState())));
                analysisQATable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        analysisQATable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                //always allow selection
            }
        });

        analysisQATable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeAnalysisQAButton.enable(true);
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() == 0 || !Window.confirm(consts.get("qaEventEditConfirm")))
                    event.cancel();
            }
        });

        analysisQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;

                r = event.getRow();
                c = event.getCol();

                val = sampleQATable.getRow(r).cells.get(c).value;

                AnalysisQaEventViewDO qaDO;
                qaDO = analysisQAManager.getAnalysisQAAt(r);

                switch (c) {
                    case 1:
                        qaDO.setTypeId((Integer)val);
                        break;
                    case 2:
                        qaDO.setIsBillable((String)val);
                        break;
                }
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                analysisQAManager.removeAnalysisQAAt(event.getIndex());
                removeAnalysisQAButton.enable(false);
            }
        });

        removeAnalysisQAButton = (AppButton)def.getWidget("removeAnalysisQAButton");
        addScreenHandler(removeAnalysisQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = analysisQATable.getSelectedRow();

                if (selectedRow > -1 && analysisQATable.numRows() > 0) {
                    analysisQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalysisQAButton.enable(false);
            }
        });

        analysisQAPicker = (AppButton)def.getWidget("analysisQAPicker");
        addScreenHandler(analysisQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (qaEventScreen == null)
                    createQaEventPickerScreen();

                ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("qaEventSelection"));
                qaEventScreen.setType(QaeventLookupScreen.Type.ANALYSIS);
                qaEventScreen.setTestId(anDO.getTestId());
                qaEventScreen.draw();
                modal.setContent(qaEventScreen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQAPicker.enable(canEditAnalysisQA() &&
                                        (SampleDataBundle.Type.ANALYSIS == type) &&
                                        anDO.getTestId() != null &&
                                        EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

    }

    private ArrayList<TableDataRow> getSampleQAEventTableModel() {
        SampleQaEventViewDO qa;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (sampleQAManager == null)
            return model;

        try {
            for (int iter = 0; iter < sampleQAManager.count(); iter++ ) {
                qa = sampleQAManager.getSampleQAAt(iter);
                model.add(createQaTableRow(qa.getQaEventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private ArrayList<TableDataRow> getAnalysisQAEventTableModel() {
        AnalysisQaEventViewDO qa;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (analysisQAManager == null)
            return model;

        try {
            for (int iter = 0; iter < analysisQAManager.count(); iter++ ) {
                qa = analysisQAManager.getAnalysisQAAt(iter);
                model.add(createQaTableRow(qa.getQaeventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private TableDataRow createQaTableRow(Integer id, String name, Integer type, String billable) {
        TableDataRow row;

        row = new TableDataRow(3);
        row.cells.get(0).value = name;
        row.cells.get(1).value = type;
        row.cells.get(2).value = billable;

        return row;
    }

    private void createQaEventPickerScreen() {
        try {
            qaEventScreen = new QaeventLookupScreen();
            qaEventScreen.addActionHandler(new ActionHandler<QaeventLookupScreen.Action>() {
                public void onAction(ActionEvent<QaeventLookupScreen.Action> event) {
                    ArrayList<TableDataRow> selections = (ArrayList<TableDataRow>)event.getData();

                    if (qaEventScreen.getType() == QaeventLookupScreen.Type.SAMPLE) {
                        sampleQATable.fireEvents(false);
                        for (int i = 0; i < selections.size(); i++ ) {
                            TableDataRow row = selections.get(i);
                            SampleQaEventViewDO qaEvent = new SampleQaEventViewDO();

                            qaEvent.setIsBillable((String)row.cells.get(3).value);
                            qaEvent.setQaEventId((Integer)row.key);
                            qaEvent.setQaEventName((String)row.cells.get(0).value);
                            qaEvent.setTypeId((Integer)row.cells.get(2).value);

                            sampleQAManager.addSampleQA(qaEvent);
                            sampleQATable.addRow(createQaTableRow(qaEvent.getQaEventId(),
                                                                  qaEvent.getQaEventName(),
                                                                  qaEvent.getTypeId(),
                                                                  qaEvent.getIsBillable()));
                        }
                        sampleQATable.fireEvents(true);

                    } else if (qaEventScreen.getType() == QaeventLookupScreen.Type.ANALYSIS) {
                        analysisQATable.fireEvents(false);
                        for (int i = 0; i < selections.size(); i++ ) {
                            TableDataRow row = selections.get(i);
                            AnalysisQaEventViewDO qaEvent = new AnalysisQaEventViewDO();

                            qaEvent.setIsBillable((String)row.cells.get(3).value);
                            qaEvent.setQaEventId((Integer)row.key);
                            qaEvent.setQaEventName((String)row.cells.get(0).value);
                            qaEvent.setTypeId((Integer)row.cells.get(2).value);

                            analysisQAManager.addAnalysisQA(qaEvent);
                            analysisQATable.addRow(createQaTableRow(qaEvent.getQaEventId(),
                                                                    qaEvent.getQaEventName(),
                                                                    qaEvent.getTypeId(),
                                                                    qaEvent.getIsBillable()));
                        }
                        analysisQATable.fireEvents(true);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("error: " + e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        ArrayList<TableDataRow> model;

        // qa event type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("qaevent_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)sampleQATable.getColumns().get(1).getColumnWidget()).setModel(model);
        ((Dropdown<Integer>)analysisQATable.getColumns().get(1).getColumnWidget()).setModel(model);
    }

    private boolean canEditSampleQA() {
        try {
            return !sampleManager.hasReleasedAnalysis();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean canEditAnalysisQA() {
        SectionPermission perm;
        SectionViewDO     sectionVDO;
        
        if (anDO != null && anDO.getSectionId() != null) {
            try {
                sectionVDO = SectionCache.getSectionFromId(anDO.getSectionId());
                perm = OpenELIS.getSystemUserPermission().getSection(sectionVDO.getName());
                return !analysisCancelledId.equals(anDO.getStatusId()) &&
                       !analysisReleasedId.equals(anDO.getStatusId()) &&
                       perm != null &&
                       (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }
        return false;
    }

    public void setData(SampleDataBundle data) {
        try {
            if (data == null || SampleDataBundle.Type.SAMPLE_ITEM.equals(data.getType())) {
                anDO = new AnalysisViewDO();
                anMan = null;
                type = SampleDataBundle.Type.SAMPLE_ITEM;

            } else {
                anMan = data.getSampleManager()
                            .getSampleItems()
                            .getAnalysisAt(data.getSampleItemIndex());
                anDO = anMan.getAnalysisAt(data.getAnalysisIndex());
                type = data.getType();
            }

            bundle = data;
            loaded = false;

        } catch (Exception e) {
            Window.alert("qaEventsTab setData: " + e.getMessage());
        }
    }

    public void setManager(SampleManager sampleManager) {
        this.sampleManager = sampleManager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                // sample
                if (sampleManager != null)
                    sampleQAManager = sampleManager.getQaEvents();
                else
                    sampleQAManager = SampleQaEventManager.getInstance();

                // analysis
                if (anMan == null)
                    analysisQAManager = AnalysisQaEventManager.getInstance();
                else
                    analysisQAManager = anMan.getQAEventAt(bundle.getAnalysisIndex());

                if (state != State.QUERY) 
                    StateChangeEvent.fire(this, state);
                
                DataChangeEvent.fire(this);
                
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
