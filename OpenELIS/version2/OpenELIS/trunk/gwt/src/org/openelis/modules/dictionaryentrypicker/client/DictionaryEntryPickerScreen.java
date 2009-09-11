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
package org.openelis.modules.dictionaryentrypicker.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.DictionaryMetaMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class DictionaryEntryPickerScreen extends Screen implements
                                                          HasActionHandlers<DictionaryEntryPickerScreen.Action> {

    protected TextBox<String> findTextBox;
    protected AppButton findButton;
    protected TableWidget dictEntTable;
    protected Dropdown<Integer> category;
    
    private ArrayList<TableDataRow> selectionList;
    
    private DictionaryMetaMap dictMeta;  

    public enum Action {
        COMMIT, ABORT
    };

    public DictionaryEntryPickerScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.dictionaryentrypicker.server.DictionaryEntryPickerService");

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
                
    }

    private void initialize() {        
        dictMeta = new DictionaryMetaMap();
        
        selectionList = null;
        
        category = (Dropdown)def.getWidget("category");
        addScreenHandler(category, new ScreenEventHandler<Integer>() {

            public void onStateChange(StateChangeEvent<State> event) {
                category.enable(true);
            }
        });

        findTextBox = (TextBox)def.getWidget("findTextBox");
        addScreenHandler(findTextBox, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                findTextBox.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findTextBox.enable(true);
            }
        });

        findButton = (AppButton)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        dictEntTable = (TableWidget)def.getWidget("dictEntTable");
        addScreenHandler(dictEntTable,new ScreenEventHandler<ArrayList<TableDataRow>>() {
            
            public void onStateChange(StateChangeEvent<State> event) {
                dictEntTable.enable(true);
                
            }
        });
        

        dictEntTable.addSelectionHandler(new SelectionHandler(){
            public void onSelection(SelectionEvent event) {
                selectionList = dictEntTable.getSelections();               
            }
            
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
        
        // Get AZ buttons and setup Screen listeners and call to for query
        final ButtonGroup azButtons = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                String baction;
                
                baction = ((AppButton)event.getSource()).action;
                findTextBox.setText(baction.substring(6, baction.length()));
                executeQuery();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                azButtons.enable(true);
            }

        });
        
        category.setModel(getCategoryModel());

    }

    public void commit() {
        ActionEvent.fire(this, Action.COMMIT, selectionList);
        window.close();
    }

    public void abort() {
        ActionEvent.fire(this, Action.ABORT, null);
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<DictionaryEntryPickerScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    
    
    private ArrayList<TableDataRow> getCategoryModel() {
        DictionaryEntryPickerDataRPC rpc;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        
        model = null;
        rpc = new DictionaryEntryPickerDataRPC();
        try {
            rpc = service.call("getCategoryModel",rpc);
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null,""));
            for(IdNameDO autoDO : rpc.categoryModel) {
                row = new TableDataRow(1);
                row.key = autoDO.getId();
                row.cells.get(0).value = autoDO.getName();
                model.add(row);
            }
            
        }catch(Exception e) {
            Window.alert(e.getMessage());
            
        }
        return model;
    }
    
    private void executeQuery() {
        Integer catId;
        String pattern;        
        DictionaryEntryPickerDataRPC rpc;
        QueryData id, entry;
        
        catId = category.getValue();        
        pattern = findTextBox.getText();
        
        if(catId == null) {
            Window.alert(consts.get("plsSelCat"));
            return;
        }               
        
        if(pattern == null || "".equals(pattern.trim()))
            return;
        
        rpc = new DictionaryEntryPickerDataRPC();
        rpc.fields = new ArrayList<QueryData>();
        
        id = new QueryData();
        id.key = dictMeta.getCategoryId();
        id.type = QueryData.Type.INTEGER;
        id.query = catId.toString();        
        rpc.fields.add(id);
        
        entry = new QueryData();
        entry.key = dictMeta.getEntry();
        entry.type = QueryData.Type.STRING;
        entry.query = pattern;        
        rpc.fields.add(entry);
        
        try {
            rpc = service.call("getDictionaryEntries",rpc);
            fillDictEntryTable(rpc.dictionaryTableModel);
        } catch(Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            
        }                           
    }
    
    private void fillDictEntryTable(ArrayList<IdNameDO> entries) {
        IdNameDO resultDO;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        
        dictEntTable.clear();
        
        model = new ArrayList<TableDataRow>();
        for(int i = 0; i < entries.size(); i++) {
            resultDO = entries.get(i);
            row = new TableDataRow(resultDO.getId(),resultDO.getName());
            model.add(row);
        }
        
        dictEntTable.load(model);
        
    }
    
    
}
