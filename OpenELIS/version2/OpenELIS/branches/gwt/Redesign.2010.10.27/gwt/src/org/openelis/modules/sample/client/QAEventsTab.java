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
import org.openelis.domain.QaEventVO;
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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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

public class QAEventsTab extends Screen {
    private boolean                  loaded;
    protected SampleDataBundle       bundle;
    protected SampleDataBundle.Type  type;

    protected Table                  sampleQATable, analysisQATable;
    protected AutoComplete           sampleQaEvent, analysisQaEvent;
    protected Button                 removeAnalysisQAButton, removeSampleQAButton, sampleQAPicker,analysisQAPicker;
    protected SampleQaEventManager   sampleQAManager;
    protected AnalysisQaEventManager analysisQAManager;
    protected SampleManager          sampleManager;
    protected AnalysisManager        anMan;
    protected AnalysisViewDO         anDO;
    protected Integer                analysisCancelledId, analysisReleasedId;
	protected Integer				 qaInternal, qaWarning, qaOverride;

    protected QaeventLookupScreen    qaEventScreen;

    public QAEventsTab(ScreenDefInt def,Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.qaevent.server.QaEventService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
    	/*
    	 * sample qa
    	 */
        sampleQATable = (Table)def.getWidget("sampleQATable");
        addScreenHandler(sampleQATable, new ScreenEventHandler<ArrayList<Item<Integer>>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.setModel(getSampleQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQATable.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                sampleQATable.setQueryMode(false);//event.getState() == State.QUERY);
            }
        });
        
        sampleQATable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //always allow selection
            }
        });

        sampleQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeSampleQAButton.setEnabled(true);
            }
        });

        sampleQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
            	if (state == State.ADD || state == State.UPDATE) {
                	if (! canEditSampleQA()) {
                		window.setError(consts.get("cantUpdateSampleQAEvent"));
                		event.cancel();
                	} else
                		window.clearStatus();
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
                val = sampleQATable.getValueAt(r,c);
                data = sampleQAManager.getSampleQAAt(r);
                
                if (!com.google.gwt.user.client.Window.confirm(consts.get("qaEventEditConfirm"))) {
                    switch (c) {
                        case 1:
                            sampleQATable.setValueAt(r,c, data.getTypeId());
                            break;
                        case 2:
                            sampleQATable.setValueAt(r,c, data.getIsBillable());
                            break;
                    }
                } else {                		
	                switch (c) {
	                    case 1:
	                        data.setTypeId((Integer)val);
	                        break;
	                    case 2:
	                        data.setIsBillable((String)val);
	                        break;
	                }
                }
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                sampleQAManager.removeSampleQAAt(event.getIndex());
                removeSampleQAButton.setEnabled(false);
            }
        });

        removeSampleQAButton = (Button)def.getWidget("removeSampleQAButton");
        addScreenHandler(removeSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
				int r;
                SampleQaEventViewDO data;

                r = sampleQATable.getSelectedRow();
                if (r < 0)
                	return;
                data = sampleQAManager.getSampleQAAt(r);
                /*
                 * allow removal of only internal qa events if any analysis is released
                 */
        		window.clearStatus();
                if (data.getTypeId() != qaInternal && !canEditSampleQA())
					window.setError(consts.get("cantUpdateSampleQAEvent"));
				else
					sampleQATable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSampleQAButton.setEnabled(false);
            }
        });

        sampleQAPicker = (Button)def.getWidget("sampleQAPicker");
        addScreenHandler(sampleQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                createQaEventPickerScreen();
                qaEventScreen.setType(QaeventLookupScreen.Type.SAMPLE);
                qaEventScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQAPicker.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                             .contains(event.getState()));
            }
        });

        /*
         * analysis qa
         */
        analysisQATable = (Table)def.getWidget("analysisQATable");
        addScreenHandler(analysisQATable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisQATable.setModel(getAnalysisQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	boolean enable;
            	
            	enable = event.getState() == State.QUERY ||
            			 (EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()) &&
    					  SampleDataBundle.Type.ANALYSIS == type &&
            			  anDO.getTestId() != null && 
            			  canEditAnalysisQA());
                analysisQATable.setEnabled(enable); 
                analysisQATable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        analysisQATable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //always allow selection
            }
        });

        analysisQATable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeAnalysisQAButton.setEnabled(true);
            }
        });

        analysisQATable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
            	if (state == State.ADD || state == State.UPDATE) {
                	if (! canEditAnalysisQA()) {
                		window.setError(consts.get("cantUpdateAnalysisQAEvent"));
                		event.cancel();
                	} else
                		window.clearStatus();
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
                val = analysisQATable.getValueAt(r,c);
                data = analysisQAManager.getAnalysisQAAt(r);

                if (!com.google.gwt.user.client.Window.confirm(consts.get("qaEventEditConfirm"))) {
                    switch (c) {
                        case 1:
                        	analysisQATable.setValueAt(r,c, data.getTypeId());
                            break;
                        case 2:
                        	analysisQATable.setValueAt(r,c, data.getIsBillable());
                            break;
                    }
                } else {                		
	                switch (c) {
	                    case 1:
	                        data.setTypeId((Integer)val);
	                        break;
	                    case 2:
	                        data.setIsBillable((String)val);
	                        break;
	                }
                }
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                analysisQAManager.removeAnalysisQAAt(event.getIndex());
                removeAnalysisQAButton.setEnabled(false);
            }
        });

        removeAnalysisQAButton = (Button)def.getWidget("removeAnalysisQAButton");
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
                if (data.getTypeId() != qaInternal && !canEditAnalysisQA())
					window.setError(consts.get("cantUpdateAnalysisQAEvent"));
				else
					analysisQATable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalysisQAButton.setEnabled(false);
            }
        });

        analysisQAPicker = (Button)def.getWidget("analysisQAPicker");
        addScreenHandler(analysisQAPicker, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	createQaEventPickerScreen();
                qaEventScreen.setType(QaeventLookupScreen.Type.ANALYSIS);
                qaEventScreen.setTestId(anDO.getTestId());
                qaEventScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
            	boolean enable;
            	
            	enable = SampleDataBundle.Type.ANALYSIS == type && anDO.getTestId() != null &&
    					 EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                analysisQAPicker.setEnabled(enable);
            }
        });

    }

    private ArrayList<Row> getSampleQAEventTableModel() {
        SampleQaEventViewDO qa;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (sampleQAManager == null)
            return model;

        try {
            for (int iter = 0; iter < sampleQAManager.count(); iter++ ) {
                qa = sampleQAManager.getSampleQAAt(iter);
                model.add(new Row(qa.getQaEventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private ArrayList<Row> getAnalysisQAEventTableModel() {
        AnalysisQaEventViewDO qa;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (analysisQAManager == null)
            return model;

        try {
            for (int iter = 0; iter < analysisQAManager.count(); iter++ ) {
                qa = analysisQAManager.getAnalysisQAAt(iter);
                model.add(new Row(qa.getQaeventId(), qa.getQaEventName(), qa.getTypeId(),
                                           qa.getIsBillable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void createQaEventPickerScreen() {
    	ModalWindow modal;
    	
        if (qaEventScreen == null) {
	    	try {
	            qaEventScreen = new QaeventLookupScreen();

	            qaEventScreen.addActionHandler(new ActionHandler<QaeventLookupScreen.Action>() {
	            	public void onAction(ActionEvent<QaeventLookupScreen.Action> event) {
	                	boolean nonInte, canEdit;
	                	QaEventVO data;
	                	Row row;
	                    ArrayList<Row> list;
	                    
	                    if (qaEventScreen.getType() == QaeventLookupScreen.Type.SAMPLE) {
	                    	nonInte = false;
	                    	canEdit = canEditSampleQA();
	                        //sampleQATable.fireEvents(false);
	                    } else { 
	                    	nonInte = false;
	                    	canEdit = canEditAnalysisQA();
	                    	//analysisQATable.fireEvents(false);
	                    }
	                    	
	                    list = (ArrayList<Row>)event.getData();
	                    for (Row r: list) {
	                    	data = (QaEventVO) r.getData();
	                    	if (!canEdit && data.getTypeId() != qaInternal) {
	                    		nonInte = true;
	                    		continue;
	                    	}
	                    	row = new Row(data.getId(), data.getName(), data.getTypeId(),
	                    						   data.getIsBillable());
		                    if (qaEventScreen.getType() == QaeventLookupScreen.Type.SAMPLE) {
			                	SampleQaEventViewDO qaEvent;

			                	qaEvent = new SampleQaEventViewDO();
	                            qaEvent.setQaEventId(data.getId());
	                            qaEvent.setQaEventName(data.getName());
	                            qaEvent.setTypeId(data.getTypeId());	                    	
	                            qaEvent.setIsBillable(data.getIsBillable());
	                            sampleQAManager.addSampleQA(qaEvent);
	                            sampleQATable.addRow(row);
		                        //sampleQATable.fireEvents(true);
		                    } else {
		                    	AnalysisQaEventViewDO qaEvent;
		                    	
		                    	qaEvent = new AnalysisQaEventViewDO();
	                            qaEvent.setQaEventId(data.getId());
	                            qaEvent.setQaEventName(data.getName());
	                            qaEvent.setTypeId(data.getTypeId());	                    	
	                            qaEvent.setIsBillable(data.getIsBillable());
	                            analysisQAManager.addAnalysisQA(qaEvent);
	                            analysisQATable.addRow(row);
		                        //analysisQATable.fireEvents(true);
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
	            com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
	            return;
	        }
        }
        modal = new ModalWindow();
        modal.setName(consts.get("qaEventSelection"));
        modal.setContent(qaEventScreen);
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;

        // qa event type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("qaevent_type"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        ((Dropdown<Integer>)sampleQATable.getColumnWidget(1)).setModel(model);
        ((Dropdown<Integer>)analysisQATable.getColumnWidget(1)).setModel(model);

        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            qaInternal = DictionaryCache.getIdFromSystemName("qaevent_internal");
            qaWarning = DictionaryCache.getIdFromSystemName("qaevent_warning");
            qaOverride = DictionaryCache.getIdFromSystemName("qaevent_override");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
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
                com.google.gwt.user.client.Window.alert("canEdit:" + anyE.getMessage());
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
            com.google.gwt.user.client.Window.alert("qaEventsTab setData: " + e.getMessage());
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
                com.google.gwt.user.client.Window.alert(e.getMessage());
            }
        }
    }
}