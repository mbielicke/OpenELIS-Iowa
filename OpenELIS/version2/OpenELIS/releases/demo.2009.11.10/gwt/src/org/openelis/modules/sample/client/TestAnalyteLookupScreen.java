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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class TestAnalyteLookupScreen extends Screen implements HasActionHandlers<TestAnalyteLookupScreen.Action>{

    private AppButton commitButton, abortButton;
    private TableWidget testAnalyteTable;
    
    //private TestAnalyteManager testAnalyteManager;
    
    public enum Action {
        OK, CANCEL
    };
    
    public TestAnalyteLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestAnalyteLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();    

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
   
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);

    }
    
    private void initialize() {
        testAnalyteTable = (TableWidget)def.getWidget("testAnalyteTable");
        addScreenHandler(testAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //testAnalyteTable.load(); // FIXME load(model)
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testAnalyteTable.enable(true);
            }
        });

        testAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;

                //val = testAnalyteTable.getObject(r,c);

// FIXME missing table col!!! using old table format?

            }
        });
        
        testAnalyteTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>(){
           public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
               //always allow
           }
        });

        testAnalyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });
        
        commitButton = (AppButton)def.getWidget("ok");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        abortButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }
    
    private void ok(){
        window.close();
    }
    
    private void cancel(){
        window.close();
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<TestAnalyteLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }  
}
