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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
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
import org.openelis.gwt.widget.ScreenWindowInt;
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
import org.openelis.modules.qaevent.client.QaeventLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class QAEventsTab extends Screen {
    private boolean                  loaded;
    protected SampleDataBundle       bundle;
    protected SampleDataBundle.Type  type;

    protected TableWidget            sampleQATable, analysisQATable;
    protected AutoComplete<Integer>  sampleQaEvent, analysisQaEvent;
    protected AppButton              removeAnalysisQAButton, removeSampleQAButton,
                                     sampleQAPicker, analysisQAPicker;
    protected Label                  sampleBillableLabel, analysisBillableLabel;
    protected SampleQaEventManager   sampleQAManager;
    protected AnalysisQaEventManager analysisQAManager;
    protected SampleManager          sampleManager;
    protected AnalysisManager        analysisManager;
    protected AnalysisViewDO         analysis, emptyAnalysis;
    protected Integer                analysisCancelledId, analysisReleasedId, sampleReleasedId, 
                                     qaInternal, qaWarning, qaOverride;

    protected QaeventLookupScreen    qaEventScreen;

    public QAEventsTab(ScreenDefInt def, ScreenWindowInt window) {
        service = new ScreenService("controller?service=org.openelis.modules.qaevent.server.QaEventService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() { 

        sampleBillableLabel = (Label)def.getWidget("sampleBillableLabel");
        
        sampleQATable = (TableWidget)def.getWidget("sampleQATable");
        addScreenHandler(sampleQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.load(getSampleQAEventTableModel());
                showSampleBillableMessage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQATable.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
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
				if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
					removeSampleQAButton.enable(true);
			}
		});

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
            	if (state == State.ADD || state == State.UPDATE) {
                	if (event.getCol() == 0 || !canEditSampleQA())
                	    event.cancel();
                }
            }
        });

        sampleQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                SampleQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = sampleQATable.getObject(r,c);
                data = sampleQAManager.getSampleQAAt(r);
                
                if (!Window.confirm(consts.get("qaEventEditConfirm"))) {
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
                }
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                sampleQAManager.removeSampleQAAt(event.getIndex());
                removeSampleQAButton.enable(false);
                showSampleBillableMessage();
            }
        });

        removeSampleQAButton = (AppButton)def.getWidget("removeSampleQAButton");
        addScreenHandler(removeSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
				int r;
                SampleQaEventViewDO data;

                r = sampleQATable.getSelectedRow();
                if (r < 0)
                	return;
                data = sampleQAManager.getSampleQAAt(r);
                /*
                 * allow removal of only internal qa events if sample is released
                 * or any analysis is released
                 */
        		window.clearStatus();
                if (!canEditSampleQA(data.getTypeId()))
					window.setError(consts.get("cantUpdateSampleQAEvent"));
				else
					sampleQATable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSampleQAButton.enable(false);
            }
        });

        sampleQAPicker = (AppButton)def.getWidget("sampleQAPicker");
        addScreenHandler(sampleQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                createQaEventPickerScreen();
                qaEventScreen.setType(QaeventLookupScreen.Type.SAMPLE);
                qaEventScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	sampleQAPicker.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        analysisBillableLabel = (Label)def.getWidget("analysisBillableLabel");
        
        analysisQATable = (TableWidget)def.getWidget("analysisQATable");
        addScreenHandler(analysisQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisQATable.load(getAnalysisQAEventTableModel());
                showAnalysisBillableMessage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQATable.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()) &&
                                       SampleDataBundle.Type.ANALYSIS == type &&
                                       analysis.getTestId() != null); 
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
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeAnalysisQAButton.enable(true);
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
            	if (state == State.ADD || state == State.UPDATE) {
                    if (event.getCol() == 0 || !canEditAnalysisQA())
                        event.cancel();
                }
            }
        });

        analysisQATable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AnalysisQaEventViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = analysisQATable.getObject(r,c);
                data = analysisQAManager.getAnalysisQAAt(r);

                if (!Window.confirm(consts.get("qaEventEditConfirm"))) {
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
                }
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                analysisQAManager.removeAnalysisQAAt(event.getIndex());
                removeAnalysisQAButton.enable(false);
                showAnalysisBillableMessage();
            }
        });

        removeAnalysisQAButton = (AppButton)def.getWidget("removeAnalysisQAButton");
        addScreenHandler(removeAnalysisQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
				int r;
                AnalysisQaEventViewDO data;

                r = analysisQATable.getSelectedRow();
                if (r < 0)
                	return;
                data = analysisQAManager.getAnalysisQAAt(r);
                /*
                 * allow removal of only internal qa events if any analysis is released
                 */
        		window.clearStatus();
                if (!canEditAnalysisQA(data.getTypeId()))
					window.setError(consts.get("cantUpdateAnalysisQAEvent"));
				else
					analysisQATable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalysisQAButton.enable(false);
            }
        });

        analysisQAPicker = (AppButton)def.getWidget("analysisQAPicker");
        addScreenHandler(analysisQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	createQaEventPickerScreen();
                qaEventScreen.setType(QaeventLookupScreen.Type.ANALYSIS);
                qaEventScreen.setTestId(analysis.getTestId());
                qaEventScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQAPicker.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()) &&
                                        SampleDataBundle.Type.ANALYSIS == type &&
                                        analysis.getTestId() != null);
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
                model.add(new TableDataRow(qa.getQaEventId(), qa.getQaEventName(), qa.getTypeId(),
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
                model.add(new TableDataRow(qa.getQaeventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void createQaEventPickerScreen() {
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
                            window.setError(consts.get("cantAddQAEvent"));
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
        modal.setName(consts.get("qaEventSelection"));
        modal.setContent(qaEventScreen);
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // qa event type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("qaevent_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)sampleQATable.getColumns().get(1).getColumnWidget()).setModel(model);
        ((Dropdown<Integer>)analysisQATable.getColumns().get(1).getColumnWidget()).setModel(model);

        try {
            analysisCancelledId = DictionaryCache.getIdBySystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdBySystemName("analysis_released");
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
            qaInternal = DictionaryCache.getIdBySystemName("qaevent_internal");
            qaWarning = DictionaryCache.getIdBySystemName("qaevent_warning");
            qaOverride = DictionaryCache.getIdBySystemName("qaevent_override");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean canEditSampleQA() {
        return canEditSampleQA(null);
    }
    
    private boolean canEditSampleQA(Integer type) {
        try {
            return ((!sampleReleasedId.equals(sampleManager.getSample().getStatusId()) &&
                      !sampleManager.hasReleasedAnalysis()) ||
                     qaInternal.equals(type));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean canEditAnalysisQA() {
        return canEditAnalysisQA(null);
    }

    private boolean canEditAnalysisQA(Integer type) {
        SectionPermission perm;
        SectionViewDO     sectionVDO;
        
        if (analysis != null && analysis.getSectionId() != null) {
            try {
                sectionVDO = SectionCache.getById(analysis.getSectionId());
                perm = UserCache.getPermission().getSection(sectionVDO.getName());
                return !analysisCancelledId.equals(analysis.getStatusId()) &&
                        (!analysisReleasedId.equals(analysis.getStatusId()) || qaInternal.equals(type)) &&
                        perm != null &&
                        (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }
        return false;
    }

    public void setData(SampleDataBundle data) {
        boolean dirty;
        
        try {
            if (data != null && SampleDataBundle.Type.ANALYSIS.equals(data.getType())) {
                analysisManager = data.getSampleManager().getSampleItems().getAnalysisAt(data.getSampleItemIndex());
                analysis = analysisManager.getAnalysisAt(data.getAnalysisIndex());
                type = data.getType();                  
            } else {
                analysisManager = null;
                dirty = (analysis == emptyAnalysis);
                analysis = emptyAnalysis;
                type = SampleDataBundle.Type.SAMPLE_ITEM;

                if (dirty)
                    DataChangeEvent.fire(this);
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
                if (analysisManager != null)
                    analysisQAManager = analysisManager.getQAEventAt(bundle.getAnalysisIndex());
                else
                    analysisQAManager = AnalysisQaEventManager.getInstance();

                if (state != State.QUERY) 
                    StateChangeEvent.fire(this, state);
                
                DataChangeEvent.fire(this);
                
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
    
    private void showSampleBillableMessage() {
        if (sampleQAManager != null && sampleQAManager.hasNotBillableQA()) 
            sampleBillableLabel.setText(consts.get("sampleNotBillable"));
        else
            sampleBillableLabel.setText("");
    }
    
    private void showAnalysisBillableMessage() {
        if (analysisQAManager != null && analysisQAManager.hasNotBillableQA()) 
            analysisBillableLabel.setText(consts.get("analysisNotBillable"));
        else
            analysisBillableLabel.setText("");
    }
}