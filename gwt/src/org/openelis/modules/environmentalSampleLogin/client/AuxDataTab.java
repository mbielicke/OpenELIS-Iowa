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

import org.openelis.domain.AuxFieldViewDO;
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
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.modules.auxGroupPicker.client.AuxGroupPickerScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class AuxDataTab extends Screen implements GetMatchesHandler{
    private boolean loaded;
    
    protected AuxGroupPickerScreen auxGroupScreen;
    protected TableWidget auxValsTable;
    protected AppButton addAuxButton, removeAuxButton;
    protected TextBox auxMethod, auxUnits, auxDesc;
    
    protected AuxDataManager manager;
    
    public AuxDataTab(ScreenDefInt def) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDef(def);
        
        initialize();
    }
    
    private void initialize() {
        auxValsTable = (TableWidget)def.getWidget("auxValsTable");
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
              //  auxValsTable.load(); // FIXME load(model)
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                auxValsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        //TODO add matches handler to value col
      //  AutoComplete<Integer> ac = (AutoComplete<Integer>)auxValsTable.columns.get(2).getColumnWidget();
      //  ac.addGetMatchesHandler(this);
        
        auxValsTable.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
                // TODO Auto-generated method stub
                
            }
        });
        /*
        auxValsTable.addSelectionHandler(new SelectionHandler<TableRow>(){
           public void onSelection(SelectionEvent<TableRow> event) {
               event.cancel();
           } 
        });*/
        
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
                //qaEventScreen.setType(QaeventPickerScreen.Type.SAMPLE);
                //qaEventScreen.draw();
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
         //       auxMethod.setValue(AUXMETHOD_DO.auxMethod());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
         //       AUXMETHOD_DO.auxMethod(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxMethod.enable(false);
            }
        });

        auxUnits = (TextBox)def.getWidget("auxUnits");
        addScreenHandler(auxUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
         //       auxUnits.setValue(AUXUNITS_DO.auxUnits());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
        //        AUXUNITS_DO.auxUnits(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxUnits.enable(false);
            }
        });

        auxDesc = (TextBox)def.getWidget("auxDesc");
        addScreenHandler(auxDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
          //      auxDesc.setValue(AUXDESC_DO.auxDesc());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
         //       AUXDESC_DO.auxDesc(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDesc.enable(false);
            }
        });
    }
    
    private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields){
        AuxFieldManager man;
        AuxFieldViewDO fieldDO;
        TableDataRow row;
        
        auxValsTable.fireEvents(false);
        for(int i=0; i<fields.size(); i++){
            man = fields.get(i);
            
            for(int j=0; j<man.count(); j++){
                fieldDO = man.getAuxFieldAt(j);
                
                row = new TableDataRow(3);
                row.cells.get(0).value = fieldDO.getIsReportable();
                row.cells.get(1).value = fieldDO.getAnalyteName();
                //TODO not sure right now...row.cells.get(2).value = fieldDO.get;
                auxValsTable.addRow(row);
            }
        }
        auxValsTable.fireEvents(true);
    }
    
    public void onGetMatches(GetMatchesEvent event) {
        // TODO Auto-generated method stub
        
    }
    
    public void setManager(AuxDataManager manager){
        this.manager = manager;
        loaded = false;
    }
    
    public void draw(){
        if(!loaded){
            DataChangeEvent.fire(this);
            loaded = true;
        }
    }
}
