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

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class QAEventsTabUI extends Screen {
    
    @UiTemplate("QAEventsTab.ui.xml")
    interface QAEventsTabUIBinder extends UiBinder<Widget, QAEventsTabUI> {        
    };
    
    private static QAEventsTabUIBinder uiBinder = GWT.create(QAEventsTabUIBinder.class);
    
    @UiField
    protected Table sampleQATable, analysisQATable;
    
    @UiField
    protected Dropdown<Integer> sampleQAType, analysisQAType;
    
    @UiField
    protected Button removeSampleQAButton, sampleQALookupButton, removeAnalysisQAButton, analysisQALookupButton;
    
    //@UiField
    protected Label<String> analysisBillableLabel;
    
    protected Screen                      parentScreen;

    protected SampleManager1          manager, displayedManager;
    
    protected boolean                canEditSampleQA, isVisible;
    
    public QAEventsTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    private void initialize() { 
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        
        addScreenHandler(sampleQATable, "sampleQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.setModel(getSampleQATableModel());
                showSampleBillableMessage();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleQATable.setEnabled(isState(ADD, UPDATE, DISPLAY));
            }
        });       

        sampleQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                removeSampleQAButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               if (!isState(ADD, UPDATE) || event.getCol() == 0 || !canEditSampleQA)
                        event.cancel();                
            }
        });

        sampleQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                /*int r, c;
                Object val;
                SampleQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = sampleQATable.getObject(r,c);
                data = sampleQAManager.getSampleQAAt(r);
                
                if (!Window.confirm(Messages.get().qaEventEditConfirm())) {
                    switch (c) {
                        case 1:
                            sampleQATable.setCell(r,c, data.getTypeId());
                            break;
                        case 2:
                            sampleQATable.setCell(r,c, data.getIsBillable());
                            break;
                    }
                } else {                        
                    switch (c) {
                        case 1:
                            data.setTypeId((Integer)val);
                            break;
                        case 2:
                            data.setIsBillable((String)val);
                            showSampleBillableMessage();
                            break;
                    }
                }*/
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                /*sampleQAManager.removeSampleQAAt(event.getIndex());
                removeSampleQAButton.enable(false);
                showSampleBillableMessage();*/
            }
        });

        addScreenHandler(removeSampleQAButton, "removeSampleQAButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                SampleQaEventViewDO data;

                r = sampleQATable.getSelectedRow();
                if (r < 0)
                    return;
                //data = sampleQAManager.getSampleQAAt(r);
                /*
                 * allow removal of only internal qa events if sample is released
                 * or any analysis is released
                 */
                window.clearStatus();
                /*if (!canEditSampleQA(data.getTypeId()))
                    window.setError(Messages.get().cantUpdateSampleQAEvent());
                else
                    sampleQATable.deleteRow(r);*/
            }

            public void onStateChange(StateChangeEvent event) {
                removeSampleQAButton.setEnabled(false);
            }
        });

        addScreenHandler(sampleQALookupButton, "sampleQALookupButton",  new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                //createQaEventPickerScreen();
                //qaEventScreen.setType(QaeventLookupScreen.Type.SAMPLE);
                //qaEventScreen.draw();
            }

            public void onStateChange(StateChangeEvent event) {
                //sampleQAPicker.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
        
        addScreenHandler(analysisQATable, "analysisQATable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                //analysisQATable.load(getAnalysisQAEventTableModel());
               // showAnalysisBillableMessage();
            }

            public void onStateChange(StateChangeEvent event) {
                /*analysisQATable.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()) &&
                                       SampleDataBundle.Type.ANALYSIS == type &&
                                       analysis.getTestId() != null); 
                analysisQATable.setQueryMode(event.getState() == State.QUERY);*/
            }
        });

        analysisQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                //if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    //removeAnalysisQAButton.enable(true);
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                /*if (state == State.ADD || state == State.UPDATE) {
                    if (event.getCol() == 0 || !canEditAnalysisQA())
                        event.cancel();
                }*/
            }
        });

        analysisQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                /*val = analysisQATable.getObject(r,c);
                data = analysisQAManager.getAnalysisQAAt(r);

                if (!Window.confirm(Messages.get().qaEventEditConfirm())) {
                    switch (c) {
                        case 1:
                            analysisQATable.setCell(r,c, data.getTypeId());
                            break;
                        case 2:
                            analysisQATable.setCell(r,c, data.getIsBillable());
                            break;
                    }
                } else {                        
                    switch (c) {
                        case 1:
                            data.setTypeId((Integer)val);
                            break;
                        case 2:
                            data.setIsBillable((String)val);
                            showAnalysisBillableMessage();
                            break;
                    }
                }*/
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                //analysisQAManager.removeAnalysisQAAt(event.getIndex());
               // removeAnalysisQAButton.enable(false);
                //showAnalysisBillableMessage();
            }
        });

        addScreenHandler(removeAnalysisQAButton, "removeAnalysisQAButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                AnalysisQaEventViewDO data;

                r = analysisQATable.getSelectedRow();
                if (r < 0)
                    return;
                //data = analysisQAManager.getAnalysisQAAt(r);
                /*
                 * allow removal of only internal qa events if any analysis is released
                 */
                window.clearStatus();
                /*if (!canEditAnalysisQA(data.getTypeId()))
                    window.setError(Messages.get().cantUpdateAnalysisQAEvent());
                else
                    analysisQATable.deleteRow(r);*/
            }

            public void onStateChange(StateChangeEvent event) {
                removeAnalysisQAButton.setEnabled(false);
            }
        });

        addScreenHandler(analysisQALookupButton, "analysisQALookupButton", new ScreenHandler<Object>() {
            public void onClick(ClickEvent event) {
                /*createQaEventPickerScreen();
                qaEventScreen.setType(QaeventLookupScreen.Type.ANALYSIS);
                qaEventScreen.setTestId(analysis.getTestId());
                qaEventScreen.draw();*/
            }

            public void onStateChange(StateChangeEvent event) {
                //analysisQALookupButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()) &&
                  //                      SampleDataBundle.Type.ANALYSIS == type &&
                    //                    analysis.getTestId() != null);
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
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandlerToSource(DataChangeEvent.getType(),
                               parentScreen,
                               new DataChangeEvent.Handler() {
                                   public void onDataChange(DataChangeEvent event) {
                                       displayQAEvents();
                                   }
                                });
        

        // qa event type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("qaevent_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        sampleQAType.setModel(model);
        analysisQAType.setModel(model);
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
        }

        return model;
    }
    
    private void displayQAEvents() {
        // TODO Auto-generated method stub
        
    }   

    private ArrayList<Row> getAnalysisQAEventTableModel() {
        AnalysisQaEventViewDO qa;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        /*try {
            for (int iter = 0; iter < analysisQAManager.count(); iter++ ) {
                qa = analysisQAManager.getAnalysisQAAt(iter);
                model.add(new TableDataRow(qa.getQaeventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/

        return model;
    }

    /*private void createQaEventPickerScreen() {
        ScreenWindow modal;
        
        if (qaEventScreen == null) {
            try {
                qaEventScreen = new QaeventLookupScreen();
                qaEventScreen.addActionHandler(new ActionHandler<QaeventLookupScreen.Action>() {
                    public void onAction(ActionEvent<QaeventLookupScreen.Action> event) {
                        boolean nonInte;
                        QaEventDO data;
                        TableDataRow row;
                        ArrayList<TableDataRow> list;
                        SampleQaEventViewDO samQaEvent;
                        AnalysisQaEventViewDO anaQaEvent;

                        nonInte = false;
                        list = (ArrayList<TableDataRow>)event.getData();
                        if (qaEventScreen.getType() == QaeventLookupScreen.Type.SAMPLE) {
                            sampleQATable.fireEvents(false);
                            for (TableDataRow r: list) {
                                data = (QaEventDO) r.data;
                                if (!canEditSampleQA(data.getTypeId())) {
                                    nonInte = true;
                                    continue;
                                }
                                row = new TableDataRow(data.getId(), data.getName(), data.getTypeId(),
                                                       data.getIsBillable());
                                samQaEvent = new SampleQaEventViewDO();
                                samQaEvent.setQaEventId(data.getId());
                                samQaEvent.setQaEventName(data.getName());
                                samQaEvent.setTypeId(data.getTypeId());                            
                                samQaEvent.setIsBillable(data.getIsBillable());
                                sampleQAManager.addSampleQA(samQaEvent);
                                sampleQATable.addRow(row);
                                sampleQATable.fireEvents(true);
                                showSampleBillableMessage();
                            }                            
                            
                        } else {
                            analysisQATable.fireEvents(false);
                            for (TableDataRow r: list) {
                                data = (QaEventDO) r.data;
                                if (!canEditAnalysisQA(data.getTypeId())) {
                                    nonInte = true;
                                    continue;
                                }
                                row = new TableDataRow(data.getId(), data.getName(), data.getTypeId(),
                                                       data.getIsBillable());
                                anaQaEvent = new AnalysisQaEventViewDO();
                                anaQaEvent.setQaEventId(data.getId());
                                anaQaEvent.setQaEventName(data.getName());
                                anaQaEvent.setTypeId(data.getTypeId());                            
                                anaQaEvent.setIsBillable(data.getIsBillable());
                                analysisQAManager.addAnalysisQA(anaQaEvent);
                                analysisQATable.addRow(row);
                                analysisQATable.fireEvents(true);
                                showAnalysisBillableMessage();
                            }
                        }
                        if (nonInte)
                            window.setError(Messages.get().cantAddQAEvent());
                        else
                            window.clearStatus();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(Messages.get().qaEventSelection());
        modal.setContent(qaEventScreen);
    }*/

    
    //TODO implement this 

  //TODO implement this 
    private boolean evaluateEdit() {
        canEditSampleQA = false;
        
        if (manager != null) {
            canEditSampleQA = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId()) &&
                            !manager.analysis.hasReleasedAnalysis();
        }
        /*try {
            return (( ||
                            Constants.dictionary().QAEVENT_INTERNAL.equals(type));
        } catch (Exception e) {
            return false;
        }*/
        return false;
    }

    //TODO implement this 
    private boolean canEditAnalysisQA() {
        return canEditAnalysisQA(null);
    }

    //TODO implement this 
    private boolean canEditAnalysisQA(Integer type) {
        /*SectionPermission perm;
        SectionViewDO     sectionVDO;
        
        if (analysis != null && analysis.getSectionId() != null) {
            try {
                sectionVDO = SectionCache.getById(analysis.getSectionId());
                perm = UserCache.getPermission().getSection(sectionVDO.getName());
                return !Constants.dictionary().ANALYSIS_CANCELLED.equals(analysis.getStatusId()) &&
                        (!Constants.dictionary().ANALYSIS_RELEASED.equals(analysis.getStatusId()) ||
                                        Constants.dictionary().QAEVENT_INTERNAL.equals(type)) &&
                        perm != null &&
                        (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }*/
        return false;
    }
    //TODO implement this
    private void showSampleBillableMessage() {
        /*if (manager != null && !manager.qaEvent.hasNonBillable()) 
            sampleBillableLabel.setText(Messages.get().sampleNotBillable());
        else
            sampleBillableLabel.setText("");*/
    }
    
    //TODO implement this
    private void showAnalysisBillableMessage() {
        /*if (analysisQAManager != null && analysisQAManager.hasNotBillableQA()) 
            analysisBillableLabel.setText(Messages.get().analysisNotBillable());
        else
            analysisBillableLabel.setText("");*/
    }
}