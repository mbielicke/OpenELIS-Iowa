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
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
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
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.manager.HasAuxDataInt;
import org.openelis.modules.auxGroupPicker.client.AuxGroupPickerScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class AuxDataTab extends Screen implements GetMatchesHandler{
    private boolean loaded;
    
    protected AuxGroupPickerScreen auxGroupScreen;
    protected TableWidget auxValsTable;
    protected AppButton addAuxButton, removeAuxButton;
    protected TextBox auxMethod, auxUnits, auxDesc;
    protected AutoComplete<Integer> ac;
    
    protected HasAuxDataInt parentMan;
    protected AuxDataManager manager;
    
    public AuxDataTab(ScreenDefInt def) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDef(def);
        
        initialize();
        
        //load types into cache
        DictionaryCache.getListByCategorySystemName("aux_field_value_type");
    }
    
    private void initialize() {
        auxValsTable = (TableWidget)def.getWidget("auxValsTable");
        ((AuxTableColumn)auxValsTable.getColumns().get(2)).setScreen(this);
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxValsTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                auxValsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        auxValsTable.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
                // TODO Auto-generated method stub
                
            }
        });
        
        auxValsTable.addSelectionHandler(new SelectionHandler<TableRow>(){
           public void onSelection(SelectionEvent<TableRow> event) {
               TableRow row = event.getSelectedItem();
               AuxFieldViewDO fieldDO = (AuxFieldViewDO)row.row.data;
               
               auxMethod.setValue(fieldDO.getMethodName());
               auxDesc.setValue(fieldDO.getDescription());
               auxUnits.setValue(fieldDO.getUnitOfMeasureName());
           }; 
        });
        
        addAuxButton = (AppButton)def.getWidget("addAuxButton");
        addScreenHandler(addAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (auxGroupScreen == null) {
                    try{
                    auxGroupScreen = new AuxGroupPickerScreen();
                    }catch(Exception e){
                        Window.alert(e.getMessage());
                    }
                    auxGroupScreen.addActionHandler(new ActionHandler<AuxGroupPickerScreen.Action>() {
                        public void onAction(ActionEvent<AuxGroupPickerScreen.Action> event) {
                            groupsSelectedFromLookup((ArrayList<AuxFieldManager>)event.getData());
                        }
                    });
                }

                ScreenWindow modal = new ScreenWindow("Aux Group Selection", "auxGroupScreen", "",
                                                      true, false);
                modal.setName(consts.get("auxGroupSelection"));
                modal.setContent(auxGroupScreen);
                auxGroupScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeAuxButton = (AppButton)def.getWidget("removeAuxButton");
        addScreenHandler(removeAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if(Window.confirm(consts.get("removeAuxMessage")))
                    Window.alert("yes");
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAuxButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });


        auxMethod = (TextBox)def.getWidget("auxMethod");
        addScreenHandler(auxMethod, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxMethod.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxMethod.enable(false);
            }
        });

        auxUnits = (TextBox)def.getWidget("auxUnits");
        addScreenHandler(auxUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxUnits.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxUnits.enable(false);
            }
        });

        auxDesc = (TextBox)def.getWidget("auxDesc");
        addScreenHandler(auxDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxDesc.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDesc.enable(false);
            }
        });
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        AuxDataDO data;
        AuxFieldViewDO field;
        AuxFieldValueDO val;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.count(); i++) {
                data = manager.getAuxDataAt(i);
                field = manager.getFieldsAt(i).getAuxFieldAt(0);
                val = manager.getFieldsAt(i).getValuesAt(0).getAuxFieldValueAt(0);
                
                row = new TableDataRow(3);
                row.cells.get(0).value = data.getIsReportable();
                row.cells.get(1).value = field.getAnalyteName();
                row.cells.get(2).value = getCorrectValueByType(data.getValue(), val.getTypeId());
                
                field.setTypeId(val.getTypeId());
                row.data = field;
                
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields){
        AuxFieldManager man;
        AuxFieldViewDO fieldDO;
        AuxFieldValueDO valueDO;
        AuxDataDO dataDO;
        TableDataRow row;
        
        try{
            auxValsTable.fireEvents(false);
            for(int i=0; i<fields.size(); i++){
                man = fields.get(i);
                
                for(int j=0; j<man.count(); j++){
                    fieldDO = man.getAuxFieldAt(j);
                    valueDO = man.getValuesAt(j).getAuxFieldValueAt(0);
                    dataDO = new AuxDataDO();
                    
                    dataDO.setTypeId(valueDO.getTypeId());
                    dataDO.setAuxFieldId(fieldDO.getId());
                    dataDO.setIsReportable(fieldDO.getIsReportable());
                    dataDO.setValue(valueDO.getValue());
                    
                    manager.addAuxData(dataDO);
                    manager.setFieldsAt(man, i);
                    row = new TableDataRow(3);
                    row.cells.get(0).value = fieldDO.getIsReportable();
                    row.cells.get(1).value = fieldDO.getAnalyteName();
                    if(valueDO.getValue() != null)
                        row.cells.get(2).value = getCorrectValueByType(valueDO.getValue(), valueDO.getTypeId());
                    
                    fieldDO.setTypeId(valueDO.getTypeId());
                    row.data = fieldDO;
                    auxValsTable.addRow(row);
                }
            }
            auxValsTable.fireEvents(true);
            
        }catch(Exception e){
            
            Window.alert(e.getMessage());
        }
    }
    
    private Object getCorrectValueByType(String value, Integer typeId){
        if(DictionaryCache.getIdFromSystemName("aux_alpha_lower").equals(typeId) || 
                        DictionaryCache.getIdFromSystemName("aux_alpha_upper").equals(typeId) || 
                        DictionaryCache.getIdFromSystemName("aux_alpha_mixed").equals(typeId) || 
                        DictionaryCache.getIdFromSystemName("aux_numeric").equals(typeId) || 
                        DictionaryCache.getIdFromSystemName("aux_time").equals(typeId))
            return value;
            
        else if(DictionaryCache.getIdFromSystemName("aux_date").equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.DAY, new Date(value));
        
        else if(DictionaryCache.getIdFromSystemName("aux_date_time").equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(value));
        
        else if(DictionaryCache.getIdFromSystemName("aux_dictionary").equals(typeId))
            return new TableDataRow(new Integer(1), value);
        
        return null;
    }
    
    public void onGetMatches(GetMatchesEvent event) {
        ArrayList<TableDataRow> model;
        int index;
        AuxFieldValueDO valDO;
        
        index = auxValsTable.getSelectedRow();
        model = new ArrayList<TableDataRow>();
        
        //give them the dictionary entries
        try{
            AuxFieldValueManager valMan = manager.getFieldsAt(index).getValuesAt(0);
            
            for(int i=0; i<valMan.count(); i++){
                valDO = valMan.getAuxFieldValueAt(i);
                model.add(new TableDataRow(valDO.getValue(), valDO.getValue()));
            }
        }catch(Exception e){
            Window.alert(e.getMessage());
            return;
        }
        
        ((AutoComplete<Integer>)event.getSource()).showAutoMatches(model);
    }
    
    public void setManager(HasAuxDataInt parentMan){
        this.parentMan = parentMan;
        loaded = false;
    }
    
    public void draw(){
        if(!loaded){
            try{
                if(state == State.UPDATE || state == State.ADD)
                    manager = parentMan.getAuxDataforUpdate();
                else
                    manager = parentMan.getAuxData();
                
                DataChangeEvent.fire(this);
                loaded = true;
                
            }catch(Exception e){
                Window.alert(e.getMessage());
            }
        }
    }
}
