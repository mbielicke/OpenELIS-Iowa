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
package org.openelis.modules.qaeventPicker.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.QaEventViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.modules.testPrepPicker.client.TestPrepPickerScreen.Action;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class QaeventPickerScreen extends Screen implements HasActionHandlers<QaeventPickerScreen.Action>{
    public enum Action {COMMIT};
    
    protected ArrayList<QaEventViewDO> qaEvents;
    
    private AppButton commitButton, abortButton;
    private TableWidget prepTestTable;
    
    public QaeventPickerScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QaeventPickerDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService");
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        try{
        //if the qa events list hasnt been created. create it
        if(qaEvents == null)
            qaEvents = service.callList("getListOfQaevents");
        }catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        prepTestTable = (TableWidget)def.getWidget("qaEventTable");
        addScreenHandler(prepTestTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                prepTestTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepTestTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                prepTestTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        prepTestTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>(){
           public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
               //do nothing
           }; 
        });
        
        prepTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (commitButton.isEnabled())
                    commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (abortButton.isEnabled())
                    abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }
    
    private void commit(){
        ArrayList<TableDataRow> selections = prepTestTable.getSelections();
        
        if(selections.size() > 0)
            ActionEvent.fire(this, Action.COMMIT, selections);
        
        Window.alert(""+selections.size());
        
        window.close();
    }
    
    private void abort(){
        window.close();
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(qaEvents == null) 
            return model;

        for(int i=0; i<qaEvents.size(); i++) {
            QaEventViewDO qaEventDO = qaEvents.get(i);
        
           TableDataRow row = new TableDataRow(4);
           row.key = qaEventDO.getId();

           row.cells.get(0).value = qaEventDO.getName();
           row.cells.get(1).value = qaEventDO.getDescription();
           row.cells.get(2).value = qaEventDO.getTypeId();
           row.cells.get(3).value = qaEventDO.getIsBillable();
           
           model.add(row);
        }
            
        return model;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
