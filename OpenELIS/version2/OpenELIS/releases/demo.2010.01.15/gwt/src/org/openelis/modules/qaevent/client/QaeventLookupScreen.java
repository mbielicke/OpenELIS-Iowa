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
package org.openelis.modules.qaevent.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventVO;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class QaeventLookupScreen extends Screen implements HasActionHandlers<QaeventLookupScreen.Action>{
    public enum Action {OK};
    public enum Type {SAMPLE, ANALYSIS};
    protected Integer testId;
    protected Type type;
    protected ArrayList<QaEventVO> qaEvents;
    
    private AppButton okButton, cancelButton;
    private TableWidget qaEventTable;
    
    
    public QaeventLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QaeventLookupDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.qaevent.server.QaEventService");
        // Setup link between Screen and widget Handlers
        initialize();
        
        initializeDropdowns();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        qaEventTable = (TableWidget)def.getWidget("qaEventTable");
        addScreenHandler(qaEventTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                qaEventTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaEventTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                qaEventTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        qaEventTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>(){
           public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
               //do nothing
           }; 
        });
        
        qaEventTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (okButton.isEnabled())
                    ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (cancelButton.isEnabled())
                    cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });
    }
    
    private void ok(){
        ArrayList<TableDataRow> selections = qaEventTable.getSelections();
        
        if(selections.size() > 0)
            ActionEvent.fire(this, Action.OK, selections);
        
        window.close();
    }
    
    private void cancel(){
        window.close();
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(qaEvents == null) 
            return model;

        for(int i=0; i<qaEvents.size(); i++) {
            QaEventVO qaEventDO = qaEvents.get(i);
        
           TableDataRow row = new TableDataRow(qaEventDO.getId(), 
                                               qaEventDO.getName(), 
                                               qaEventDO.getDescription(), 
                                               qaEventDO.getTypeId(), 
                                               qaEventDO.getIsBillable());
           
           model.add(row);
        }
            
        return model;
    }
    
    public void draw(){
        try{
            qaEvents = new ArrayList<QaEventVO>();
            
            if(type == Type.ANALYSIS && testId != null)
                qaEvents = service.callList("fetchByTestId", testId);
            else if(type == Type.SAMPLE)
                qaEvents = service.callList("fetchByCommon");
            
            DataChangeEvent.fire(this);
        }catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("qaevent_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)qaEventTable.getColumns().get(2).getColumnWidget()).setModel(model);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }
}
