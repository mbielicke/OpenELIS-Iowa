/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.storage.client.StorageService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class StorageLocationLookupScreen extends Screen implements HasActionHandlers<StorageLocationLookupScreen.Action> {

    protected AutoComplete<Integer> location;
    protected AppButton             okButton, cancelButton;
    protected TableWidget           storageLocationTable;
    protected ButtonGroup           azButtons;

    public enum Action {
        OK, CANCEL
    };

    public StorageLocationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(StorageLocationLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {

        location = (AutoComplete<Integer>)def.getWidget("location");
        
        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                StorageLocationViewDO data;
                ArrayList<StorageLocationViewDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();

                try {
                    list = StorageService.get().fetchAvailableByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);                        
                        row = new TableDataRow(3);
                        row.key = data.getId();
                        row.cells.get(0).setValue(data.getName());
                        row.cells.get(1).setValue(data.getStorageUnitDescription());
                        row.cells.get(2).setValue(data.getLocation());                        
                        row.data = data;
                        model.add(row);
                    }
                    storageLocationTable.load(model);
                    
                    if(model.size() > 0)
                        storageLocationTable.selectRow(0);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        addScreenHandler(location, new ScreenEventHandler<ArrayList<TableDataRow>>() {            
            public void onDataChange(DataChangeEvent event) {
                location.setSelection(new TableDataRow(null, ""));
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(true);
            }
        });

        storageLocationTable = (TableWidget)def.getWidget("storageLocationTable");
        addScreenHandler(storageLocationTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                storageLocationTable.load(null);
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                storageLocationTable.enable(true);
            }
        });               
        
        storageLocationTable.multiSelect(false);

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });
    }

    public void ok() {
        TableDataRow row;
        
        row = storageLocationTable.getSelection();
        if(row != null)
            ActionEvent.fire(this, Action.OK, row.data);
        else 
            ActionEvent.fire(this, Action.OK, null);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<StorageLocationLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void clearFields() {
        DataChangeEvent.fire(this);        
    }
}
