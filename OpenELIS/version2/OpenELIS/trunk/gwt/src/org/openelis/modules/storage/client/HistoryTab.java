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
package org.openelis.modules.storage.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.StorageLocationManager;
import org.openelis.manager.StorageManager;
import org.openelis.manager.StorageViewManager;
import org.openelis.meta.StorageMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class HistoryTab extends Screen {

    private StorageViewManager manager;
    private TableWidget        storageHistoryTable;
    private AppButton          historyPrevButton, historyNextButton;
    private HistoryTab         screen; 
    private boolean            loaded;
    private int                pageNum;
    
    public HistoryTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        initialize();  
    }
    
    private void initialize() {        
        screen = this;
        
        storageHistoryTable = (TableWidget)def.getWidget("storageHistoryTable");
        addScreenHandler(storageHistoryTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                loadHistoryModel(); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageHistoryTable.enable(EnumSet.of(State.UPDATE).contains(event.getState()));                
            }
        });
        
        storageHistoryTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }            
        });

        historyPrevButton = (AppButton)def.getWidget("historyPrevButton");
        addScreenHandler(historyPrevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) { 
                if(pageNum > 0) {                               
                    pageNum--;
                    DataChangeEvent.fire(screen, storageHistoryTable);                                   
                } else {  
                    window.setError(consts.get("noMoreRecordInDir"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyPrevButton.enable(EnumSet.of(State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });

        historyNextButton = (AppButton)def.getWidget("historyNextButton");
        addScreenHandler(historyNextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {           
                pageNum++;                   
                DataChangeEvent.fire(screen, storageHistoryTable);           
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyNextButton.enable(EnumSet.of(State.UPDATE,State.DISPLAY).contains(event.getState()));
            }
        });
    }
    
    private void loadHistoryModel() {
        int i;
        TableDataRow row;
        StorageViewDO data;
        ArrayList<TableDataRow> model;
        StorageManager sm;  
        StorageLocationManager slm;
        Query query;
        QueryData field;
        String location;
                
        model = new ArrayList<TableDataRow>();
        sm = null;
        
        if (manager == null)        
            return;
        
        slm  = manager.getStorageLocation();
        
        if(slm == null)
            return;
        
        try {           
            window.setBusy(consts.get("fetching"));
            
            query = new Query();
            
            field = new QueryData();
            field.key = StorageMeta.getStorageLocationId();
            field.query = slm.getStorageLocation().getId().toString();
            field.type = QueryData.Type.INTEGER;            
            query.setFields(field);
                         
            query.setPage(pageNum);
            
            sm = manager.getHistory(query);       
        } catch (LastPageException e) {
            window.setError(consts.get("noMoreRecordInDir"));
            pageNum--;
            return;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            pageNum--;
            window.clearStatus();
            return;
        }
        
        for (i = 0; i < sm.count(); i++) {
            data = sm.getStorageAt(i);
            
            row = new TableDataRow(5);            
            location = data.getStorageLocation();
            if(location != null)
                location = location.replaceAll(",", "");
            row.cells.get(0).setValue(location);
            row.cells.get(1).setValue(data.getItemDescription());
            row.cells.get(2).setValue(data.getUserName());
            row.cells.get(3).setValue(data.getCheckin());
            row.cells.get(4).setValue(data.getCheckout());
            
            model.add(row);
        }
        
        storageHistoryTable.load(model);
        window.clearStatus();                
    }
    
    public void setManager(StorageViewManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if (!loaded) {             
            pageNum = 0; 
            DataChangeEvent.fire(this);
        }

        loaded = true;
    }
}
