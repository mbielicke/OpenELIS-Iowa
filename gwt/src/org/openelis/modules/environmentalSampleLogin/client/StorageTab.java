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

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.StorageLocationAutoDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.StorageManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class StorageTab extends Screen {
    private boolean loaded;

    protected TableWidget storageTable;
    
    protected StorageManager manager;
    protected SampleDataBundle data;

    public StorageTab(ScreenDefInt def) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.storage.server.StorageService");
        setDef(def);
        
        initialize();
    }
    
    private void initialize() {
        storageTable = (TableWidget)def.getWidget("storageTable");
        addScreenHandler(storageTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                storageTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                storageTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        storageTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCol();
                StorageViewDO storageDO;
                TableDataRow tableRow = storageTable.getRow(row);
                try{
                    storageDO = manager.getStorageAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                Datetime checkin, checkout;
                
                switch (col){
                    case 0:
                            storageDO.setSystemUserId((Integer)val);
                            break;
                    case 1:
                            storageDO.setStorageLocationId((Integer)((TableDataRow)val).key);
                            break;
                    case 2:
                            storageDO.setCheckin((Datetime)val);
                            
                            checkin = (Datetime)tableRow.cells.get(2).value;
                            checkout = (Datetime)tableRow.cells.get(3).value;
                            
                            if(checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                                storageTable.setCellException(row, col, new LocalizedException("checkinDateAfterCheckoutDateException"));
                            break;
                    case 3:
                            storageDO.setCheckout((Datetime)val);
                            
                            checkin = (Datetime)tableRow.cells.get(2).value;
                            checkout = (Datetime)tableRow.cells.get(3).value;
                            
                            if(checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                                storageTable.setCellException(row, col, new LocalizedException("checkinDateAfterCheckoutDateException"));
                            break;
                }
            }
        });

        storageTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try{
                    TableDataRow selectedRow = storageTable.getRow(0);
                    StorageViewDO storageDO = new StorageViewDO();
                    storageDO.setCheckin((Datetime)selectedRow.cells.get(2).value);
                    
                    manager.addStorage(storageDO);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });

        storageTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try{
                    manager.removeStorageAt(event.getIndex());
                    
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });
        
        final AutoComplete<Integer> location = ((AutoComplete<Integer>)storageTable.columns.get(1).colWidget);
        location.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc = new AutocompleteRPC();
                rpc.match = event.getMatch();
                try {
                    rpc = service.call("getStorageMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i=0; i<rpc.model.size(); i++){
                        StorageLocationAutoDO autoDO = (StorageLocationAutoDO)rpc.model.get(i);
                        
                        TableDataRow row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getLocation();

                        model.add(row);
                    } 
                    
                    location.showAutoMatches(model);
                        
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
                
            }
        });

        final AppButton addStorageButton = (AppButton)def.getWidget("addStorageButton");
        addScreenHandler(addStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Datetime date = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
                
                if(storageTable.numRows() > 0 && storageTable.getCell(0, 3).value == null)
                    storageTable.setCell(0, 3, date);
                
                TableDataRow newRow = new TableDataRow(4);
                newRow.cells.get(2).value = date;
                storageTable.addRow(0, newRow);
                storageTable.selectRow(0);
                storageTable.scrollToSelection();
                storageTable.startEditing(0, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addStorageButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton removeStorageButton = (AppButton)def.getWidget("removeStorageButton");
        addScreenHandler(removeStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = storageTable.getSelectedIndex();
                if (selectedRow > -1 && storageTable.numRows() > 0) {
                    storageTable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeStorageButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < manager.count();iter++) {
                StorageViewDO storageDO = manager.getStorageAt(iter);
            
               TableDataRow row = new TableDataRow(4);
               row.key = storageDO.getId();

               //FIXME not possible right now  row.cells.get(0).value = storageDO.getUserName();
               row.cells.get(1).value = new TableDataRow(storageDO.getStorageLocationId(), storageDO.getStorageLocation());
               row.cells.get(2).value = storageDO.getCheckin();
               row.cells.get(3).value = storageDO.getCheckout();
               
               model.add(row);
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
    
    
    public void setData(SampleDataBundle data) {
        this.data = data;
        manager = null;
        loaded = false;
    }
    
    public void draw(){
        int i;
        try{
            if(!loaded){
                if(data.type == SampleDataBundle.Type.ANALYSIS){
                    i = data.analysisManager.getIndex(data.analysisTestDO);
                    manager = data.analysisManager.getStorageAt(i);
                    
                    if(state == State.ADD || state == State.UPDATE)
                        StateChangeEvent.fire(this, State.UPDATE);
                    
                }else if(data.type == SampleDataBundle.Type.SAMPLE_ITEM){
                    i = data.sampleItemManager.getIndex(data.sampleItemDO);
                    manager = data.sampleItemManager.getStorageAt(i);
                    
                    if(state == State.ADD || state == State.UPDATE)
                        StateChangeEvent.fire(this, State.UPDATE);
                    
                }else{
                    manager = StorageManager.getInstance();
                    StateChangeEvent.fire(this, State.DEFAULT);
                }
            }
            
            DataChangeEvent.fire(this);

        }catch(Exception e){
            Window.alert(e.getMessage());
        }
        
        loaded = true;
     }
    
    public boolean validate() {
        if(!loaded)
            return true;
        
        Datetime checkout,checkin;
        boolean returnValue = true;
        
        for(int i=0; i<storageTable.numRows(); i++){
            checkin = (Datetime)storageTable.getObject(i, 2);
            checkout = (Datetime)storageTable.getObject(i, 3);
            
            if(checkin != null && checkout != null && checkout.compareTo(checkin) <= 0){
                storageTable.setCellException(i, 3, new LocalizedException("checkinDateAfterCheckoutDateException"));
                returnValue = false;
            }
        }

        return returnValue;
    }
}
