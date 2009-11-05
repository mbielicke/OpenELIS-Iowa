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
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventVO;
import org.openelis.domain.SampleQaEventViewDO;
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
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.modules.qaevent.client.QaeventLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class QAEventsTab extends Screen {
    private boolean                  loaded;
    private SampleDataBundle.Type    type;

    protected TableWidget            sampleQATable, analysisQATable;
    protected AutoComplete<Integer>  sampleQaEvent, analysisQaEvent;

    protected SampleQaEventManager   sampleQAManager;
    protected AnalysisQaEventManager analysisQAManager;
    protected SampleManager          sampleManager;
    protected AnalysisManager        anMan;
    protected AnalysisViewDO         anDO;

    protected QaeventLookupScreen    qaEventScreen;

    public QAEventsTab(ScreenDefInt def) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.qaevent.server.QaEventService");
        setDef(def);

        initialize();

        setTypesModel(DictionaryCache.getListByCategorySystemName("qaevent_type"));
    }

    private void initialize() {
        final QAEventsTab tab = this;

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
        
        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               if(event.getCol() == 0)
                   event.cancel();
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
            }
        });

        final AppButton removeSampleQAButton = (AppButton)def.getWidget("removeSampleQAButton");
        addScreenHandler(removeSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleQATable.getSelectedRow();
                if (selectedRow > -1 && sampleQATable.numRows() > 0) {
                    sampleQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSampleQAButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
            }
        });

        final AppButton sampleQAPicker = (AppButton)def.getWidget("sampleQAPicker");
        addScreenHandler(sampleQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (qaEventScreen == null) {
                    createQaEventPickerScreen();
                }

                ScreenWindow modal = new ScreenWindow("QA Event Selection", "qaEventScreen", "",
                                                      true, false);
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
                analysisQATable.enable( (SampleDataBundle.Type.ANALYSIS == type) &&
                                       anDO.getTestId() != null &&
                                       EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                analysisQATable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               if(event.getCol() == 0)
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
            }
        });

        final AppButton removeAnalysisQAButton = (AppButton)def.getWidget("removeAnalysisQAButton");
        addScreenHandler(removeAnalysisQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = analysisQATable.getSelectedRow();

                if (selectedRow > -1 && analysisQATable.numRows() > 0) {
                    analysisQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalysisQAButton.enable( (SampleDataBundle.Type.ANALYSIS == type) &&
                                              anDO.getTestId() != null &&
                                              EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });

        final AppButton analysisQAPicker = (AppButton)def.getWidget("analysisQAPicker");
        addScreenHandler(analysisQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (qaEventScreen == null)
                    createQaEventPickerScreen();

                ScreenWindow modal = new ScreenWindow("QA Event Selection", "qaEventScreen", "",
                                                      true, false);
                modal.setName(consts.get("qaEventSelection"));
                qaEventScreen.setType(QaeventLookupScreen.Type.ANALYSIS);
                qaEventScreen.setTestId(anDO.getTestId());
                qaEventScreen.draw();
                modal.setContent(qaEventScreen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQAPicker.enable( (SampleDataBundle.Type.ANALYSIS == type) &&
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

    private void setTypesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)sampleQATable.getColumns().get(1).getColumnWidget()).setModel(model);
        ((Dropdown<Integer>)analysisQATable.getColumns().get(1).getColumnWidget()).setModel(model);
    }

    public void setData(SampleDataBundle data) {
        if (data.analysisTestDO == null)
            anDO = new AnalysisViewDO();
        else
            anDO = data.analysisTestDO;

        type = data.type;

        anMan = data.analysisManager;
        loaded = false;
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
                else {
                    int index = anMan.getIndex(anDO);

                    if (index != -1)
                        analysisQAManager = anMan.getQAEventAt(index);
                }

                StateChangeEvent.fire(this, state);
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
