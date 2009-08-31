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

import org.openelis.domain.StorageDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.gwt.widget.table.rewrite.TableWidget;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedEvent;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedHandler;
import org.openelis.manager.StorageManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class StorageTab extends Screen {
    private boolean loaded;
    
    protected StorageManager manager;
    protected SampleDataBundle data;

    public StorageTab(ScreenDef def) {
        setDef(def);
        
        initialize();
    }
    
    private void initialize() {
        final TableWidget storageTable = (TableWidget)def.getWidget("storageTable");
        addScreenHandler(storageTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                storageTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                storageTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storageTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCell();
                StorageDO storageDO;
                TableDataRow tableRow = storageTable.getRow(row);
                try{
                    storageDO = manager.getStorageAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                
                switch (col){
                    case 0:
                            storageDO.setSystemUserId((Integer)val);
                            break;
                    case 1:
                            storageDO.setStorageLocationId((Integer)val);
                            break;
                    case 2:
                            storageDO.setCheckin((Datetime)val);
                            break;
                    case 3:
                            storageDO.setCheckout((Datetime)val);
                            break;
                }
            }
        });

        storageTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try{
                    manager.addStorage(new StorageDO());
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

        final AppButton addStorageButton = (AppButton)def.getWidget("addStorageButton");
        addScreenHandler(addStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                storageTable.addRow();
                storageTable.selectRow(storageTable.numRows()-1);
                storageTable.scrollToSelection();
                storageTable.startEditing(storageTable.numRows()-1, 0);
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
                removeStorageButton.enable(EnumSet.of(State.ADD, state.UPDATE).contains(event.getState()));
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
                StorageDO storageDO = manager.getStorageAt(iter);
            
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
        loaded = false;
    }

    public void draw(){
        int i;
        try{
            if(!loaded){
                if(data.type == SampleDataBundle.Type.ANALYSIS){
                    i = data.analysisManager.getIndex(data.analysisTestDO);
                    manager = data.analysisManager.getStorageAt(i);
                    
                }else if(data.type == SampleDataBundle.Type.SAMPLE_ITEM){
                    i = data.sampleItemManager.getIndex(data.sampleItemDO);
                    manager = data.sampleItemManager.getStorageAt(i);
                    
                }
                
                DataChangeEvent.fire(this);
            }

        }catch(Exception e){
            Window.alert(e.getMessage());
        }
        
        loaded = true;
     }
}
