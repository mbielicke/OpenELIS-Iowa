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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class SampleTypeTab extends Screen {
    private TestManager manager;
    private boolean dropdownsInited, loaded;        
    
    private TableWidget sampleTypeTable;
    
    public SampleTypeTab(ScreenDefInt def) {
        setDef(def);
        initialize();          
    }

    private void initialize() {                

        sampleTypeTable = (TableWidget)def.getWidget("sampleTypeTable");
        addScreenHandler(sampleTypeTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {                
                sampleTypeTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleTypeTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                sampleTypeTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sampleTypeTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Integer val;
                TestTypeOfSampleDO sampleTypeDO;
                
                row = event.getRow();
                col = event.getCell();                         
                val = (Integer)sampleTypeTable.getRow(row).cells.get(col).value;                
                try{
                    sampleTypeDO = manager.getSampleTypes().getTypeAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(col) {
                    case 0:
                        sampleTypeDO.setTypeOfSampleId(val);
                        break;
                    case 1:
                        sampleTypeDO.setUnitOfMeasureId(val);
                        break;
                }
            }
        });

        sampleTypeTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try{ 
                    manager.getSampleTypes().addType(new TestTypeOfSampleDO());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
                
            }
        });

        sampleTypeTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try{
                    manager.getSampleTypes().removeTypeAt(event.getIndex());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });

        final AppButton addSampleTypeButton = (AppButton)def.getWidget("addSampleTypeButton");
        addScreenHandler(addSampleTypeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleTypeTable.addRow();
                sampleTypeTable.selectRow(sampleTypeTable.numRows()-1);
                sampleTypeTable.scrollToSelection();
                sampleTypeTable.startEditing(sampleTypeTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addSampleTypeButton.enable(true);
                else
                    addSampleTypeButton.enable(false);
            }
        });

        final AppButton removeSampleTypeButton = (AppButton)def.getWidget("removeSampleTypeButton");
        addScreenHandler(removeSampleTypeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleTypeTable.getSelectedIndex();
                if (selectedRow > -1 && sampleTypeTable.numRows() > 0) {
                    sampleTypeTable.deleteRow(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeSampleTypeButton.enable(true);
                else
                    removeSampleTypeButton.enable(false);
            }
        });

    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestTypeOfSampleManager ttsm;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
                
        if(manager == null)
            return model;
        
        try {
            ttsm = manager.getSampleTypes();
            for(int i = 0; i < ttsm.count(); i++) {
                TestTypeOfSampleDO sampleType = ttsm.getTypeAt(i);
                row = new TableDataRow(2);
                row.key = sampleType.getId();               
                
                row.cells.get(0).value = sampleType.getTypeOfSampleId();
                row.cells.get(1).value = sampleType.getUnitOfMeasureId();
                model.add(row);
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }
    
        return model;
    }

    private void setSampleTypes() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("type_of_sample");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown)sampleTypeTable.columns.get(0).getColumnWidget()).setModel(model);
        
    }

    private void setUnitsOfMeasure() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown)sampleTypeTable.columns.get(1).getColumnWidget()).setModel(model);
        
    }
    
    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
        if(!dropdownsInited) {
            setSampleTypes();
            setUnitsOfMeasure();
            dropdownsInited = true;
        }
    }
    
    public void draw(){
        if(!loaded)
            DataChangeEvent.fire(this);
        
        loaded = true;
    }       
    
}
