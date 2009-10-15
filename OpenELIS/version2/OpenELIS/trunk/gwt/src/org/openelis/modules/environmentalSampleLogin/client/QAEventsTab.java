/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.environmentalSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventAutoDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleQaEventManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class QAEventsTab extends Screen {
    private boolean loaded;
    private SampleDataBundle.Type type;
    
    protected TableWidget sampleQATable, analysisQATable;
    protected AutoComplete<Integer> sampleQaEvent, analysisQaEvent;
    
    protected SampleQaEventManager sampleQAManager;
    protected AnalysisQaEventManager analysisQAManager;
    protected SampleManager sampleManager;
    protected AnalysisManager anMan;
    protected AnalysisViewDO anDO;
    
    public QAEventsTab(ScreenDefInt def) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService");
        setDef(def);
        
        initialize();
       
        setTypesModel(DictionaryCache.getListByCategorySystemName("qaevent_type"));
    }
    
    private void initialize() {
        
        sampleQATable = (TableWidget)def.getWidget("sampleQATable");
        addScreenHandler(sampleQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleQATable.load(getSampleQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleQATable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sampleQATable.setQueryMode(event.getState() == State.QUERY);
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
                
                switch (c){
                    case 0:
                            TableDataRow selectedRow = sampleQaEvent.getSelection();
                            Integer id = null;
                            String name = null;
                            Integer typeId = null;
                            String isBillable = null;
    
                            if (selectedRow.key != null) {
                                id = (Integer)selectedRow.key;
                                name = (String)selectedRow.cells.get(0).value;
                                typeId = (Integer)selectedRow.cells.get(2).value;
                                isBillable = (String)selectedRow.data;
                            }
    
                            sampleQATable.setCell(sampleQATable.getSelectedIndex(), 1, typeId);
                            sampleQATable.setCell(sampleQATable.getSelectedIndex(), 2, isBillable);
                       
                            qaDO.setQaEventId(id);
                            qaDO.setQaEventName(name);
                            qaDO.setTypeId(typeId);
                            qaDO.setIsBillable(isBillable);
                            break;
                    case 1:
                            qaDO.setTypeId((Integer)val);
                            break;
                    case 2:
                            qaDO.setIsBillable((String)val);
                            break;
                }
            }
        });

        sampleQATable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                sampleQAManager.addSampleQA(new SampleQaEventViewDO());
            }
        });

        sampleQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                sampleQAManager.removeSampleQAAt(event.getIndex());
            }
        });
        
        sampleQaEvent = ((AutoComplete<Integer>)sampleQATable.columns.get(0).colWidget);
        sampleQaEvent.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc = new AutocompleteRPC();
                rpc.match = event.getMatch();
                try {
                    rpc = service.call("getQAEventMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i=0; i<rpc.model.size(); i++){
                        QaEventAutoDO autoDO = (QaEventAutoDO)rpc.model.get(i);
                        
                        TableDataRow row = new TableDataRow(3);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        row.cells.get(1).value = autoDO.getDescription();
                        row.cells.get(2).value = autoDO.getTypeId();
                        row.data = autoDO.getIsBillable();
                        model.add(row);
                    } 
                    
                    sampleQaEvent.showAutoMatches(model);
                        
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
             } 
         });

        final AppButton addSampleQAButton = (AppButton)def.getWidget("addSampleQAButton");
        addScreenHandler(addSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleQATable.addRow();
                sampleQATable.selectRow(sampleQATable.numRows()-1);
                sampleQATable.scrollToSelection();
                sampleQATable.startEditing(sampleQATable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addSampleQAButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton removeSampleQAButton = (AppButton)def.getWidget("removeSampleQAButton");
        addScreenHandler(removeSampleQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleQATable.getSelectedIndex();
                if (selectedRow > -1 && sampleQATable.numRows() > 0) {
                    sampleQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeSampleQAButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        analysisQATable = (TableWidget)def.getWidget("analysisQATable");
        addScreenHandler(analysisQATable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                analysisQATable.load(getAnalysisQAEventTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisQATable.enable((SampleDataBundle.Type.ANALYSIS == type) && EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                analysisQATable.setQueryMode(event.getState() == State.QUERY);
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
                
                switch (c){
                    case 0:
                            TableDataRow selectedRow = analysisQaEvent.getSelection();
                            Integer id = null;
                            String name = null;
                            Integer typeId = null;
                            String isBillable = null;
    
                            if (selectedRow.key != null) {
                                id = (Integer)selectedRow.key;
                                name = (String)selectedRow.cells.get(0).value;
                                typeId = (Integer)selectedRow.cells.get(2).value;
                                isBillable = (String)selectedRow.data;
                            }
    
                            analysisQATable.setCell(analysisQATable.getSelectedIndex(), 1, typeId);
                            analysisQATable.setCell(analysisQATable.getSelectedIndex(), 2, isBillable);
                            
                            qaDO.setQaEventId(id);
                            qaDO.setQaEventName(name);
                            qaDO.setTypeId(typeId);
                            qaDO.setIsBillable(isBillable);
                            break;
                    case 1:
                            qaDO.setTypeId((Integer)val);
                            break;
                    case 2:
                            qaDO.setIsBillable((String)val);
                            break;
                }
            }
        });

        analysisQATable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                analysisQAManager.addAnalysisQA(new AnalysisQaEventViewDO());
            }
        });

        analysisQATable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                analysisQAManager.removeAnalysisQAAt(event.getIndex());
            }
        });
        
        analysisQaEvent = ((AutoComplete<Integer>)analysisQATable.columns.get(0).colWidget);
        analysisQaEvent.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc = new AutocompleteRPC();
                rpc.match = event.getMatch();
                try {
                    rpc = service.call("getQAEventMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i=0; i<rpc.model.size(); i++){
                        QaEventAutoDO autoDO = (QaEventAutoDO)rpc.model.get(i);
                        
                        TableDataRow row = new TableDataRow(3);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        row.cells.get(1).value = autoDO.getDescription();
                        row.cells.get(2).value = autoDO.getTypeId();
                        row.data = autoDO.getIsBillable();
                        
                        model.add(row);
                    } 
                    
                    analysisQaEvent.showAutoMatches(model);
                        
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
             } 
         });

        final AppButton addAanalysisQAButton = (AppButton)def.getWidget("addAanalysisQAButton");
        addScreenHandler(addAanalysisQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analysisQATable.addRow();
                analysisQATable.selectRow(analysisQATable.numRows()-1);
                analysisQATable.scrollToSelection();
                analysisQATable.startEditing(analysisQATable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAanalysisQAButton.enable((SampleDataBundle.Type.ANALYSIS == type) && EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton removeAnalysisQAButton = (AppButton)def.getWidget("removeAnalysisQAButton");
        addScreenHandler(removeAnalysisQAButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = analysisQATable.getSelectedIndex();
                
                if (selectedRow > -1 && analysisQATable.numRows() > 0) {
                    analysisQATable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalysisQAButton.enable((SampleDataBundle.Type.ANALYSIS == type) && EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    private ArrayList<TableDataRow> getSampleQAEventTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(sampleQAManager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < sampleQAManager.count();iter++) {
                SampleQaEventViewDO sampleQARow = (SampleQaEventViewDO)sampleQAManager.getSampleQAAt(iter);
            
               TableDataRow row = new TableDataRow(3);
               row.key = sampleQARow.getId();

               row.cells.get(0).value = new TableDataRow(sampleQARow.getQaEventId(), sampleQARow.getQaEventName());
               row.cells.get(1).value = sampleQARow.getTypeId();
               row.cells.get(2).value = sampleQARow.getIsBillable();
               model.add(row);
                }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
    
    private ArrayList<TableDataRow> getAnalysisQAEventTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(analysisQAManager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < analysisQAManager.count();iter++) {
                AnalysisQaEventViewDO analysisQARow = (AnalysisQaEventViewDO)analysisQAManager.getAnalysisQAAt(iter);
            
               TableDataRow row = new TableDataRow(3);
               row.key = analysisQARow.getId();

               row.cells.get(0).value = new TableDataRow(analysisQARow.getQaEventId(), analysisQARow.getQaEventName());
               row.cells.get(1).value = analysisQARow.getTypeId();
               row.cells.get(2).value = analysisQARow.getIsBillable();
               model.add(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
    
    private void setTypesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown<Integer>)sampleQATable.columns.get(1).getColumnWidget()).setModel(model);
        ((Dropdown<Integer>)analysisQATable.columns.get(1).getColumnWidget()).setModel(model);
    }
    
    public void setData(SampleDataBundle data) {
        if(data.analysisTestDO == null)
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
     
     public void draw(){
         if (!loaded) {
             try {
                 //sample
                 if (sampleManager != null) 
                     sampleQAManager = sampleManager.getQaEvents();
                 else
                     sampleQAManager = SampleQaEventManager.getInstance();
                 
                 //analysis
                 if(anMan == null)
                     analysisQAManager = AnalysisQaEventManager.getInstance();
                 else{
                     int index = anMan.getIndex(anDO);
                     
                     if(index != -1)
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
