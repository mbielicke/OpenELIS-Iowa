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
package org.openelis.modules.dictionary.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.meta.CategoryMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DictionaryLookupScreen extends Screen implements
                                                       HasActionHandlers<DictionaryLookupScreen.Action> {

    protected TextBox<String>       findTextBox;
    protected Button                findButton,okButton,cancelButton;
    protected Table                 dictEntTable;
    protected Dropdown<Integer>     category;
    protected ButtonGroup           azButtons;

    public enum Action {
        OK, CANCEL
    };

    public DictionaryLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(DictionaryLookupDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");        
        
        // Setup link between Screen and widget Handlers
        initialize();
        initializeDropdowns(); 
        
        setState(State.DEFAULT);      
    }    

    private void initialize() {        

        category = (Dropdown<Integer>)def.getWidget("category");
        addScreenHandler(category, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                category.setValue(null);
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                category.setEnabled(true);
            }
        });

        findTextBox = (TextBox<String>)def.getWidget("findTextBox");
        addScreenHandler(findTextBox, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                findTextBox.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findTextBox.setEnabled(true);
            }
        });

        findButton = (Button)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {                
                executeQuery(findTextBox.getText());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.setEnabled(true);
            }
        });

        dictEntTable = (Table)def.getWidget("dictEntTable");
        addScreenHandler(dictEntTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                dictEntTable.setEnabled(true);
            }
        });

        okButton = (Button)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.setEnabled(true);
            }
        });

        cancelButton = (Button)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.setEnabled(true);
            }
        });

        // Get AZ buttons and setup Screen listeners and call to for query
        azButtons = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                String query;

                query = ((Button)event.getSource()).getAction();
                findTextBox.setText(query);
                executeQuery(query);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                azButtons.setEnabled(true);
            }

        });
    }

    public void ok() {
    	Integer[] selections;
        ArrayList<IdNameVO> list;               
        
        list = null;
        
        selections = dictEntTable.getSelectedRows();
        if(selections != null) {
            list = new ArrayList<IdNameVO>();
            for(int i = 0; i < selections.length; i++) 
                list.add((IdNameVO)dictEntTable.getRowAt(selections[i]).getData());
        }
        
        ActionEvent.fire(this, Action.OK, list);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<DictionaryLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        Item<Integer> item;
        ArrayList<IdNameVO> list;

        model = null;
        try {
            list = service.callList("fetchByCategoryName","%");
            model = new ArrayList<Item<Integer>>();
            model.add(new Item<Integer>(null, ""));
            for (IdNameVO data : list) {
                item = new Item<Integer>(data.getId(),data.getName());
                model.add(item);
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());

        }
        category.setModel(model);
    }
    
    public void clearFields() {
        DataChangeEvent.fire(this);
    }

    public void executeQuery(String pattern) {
        Integer catId;
        Query query;  
        QueryData field;
        ArrayList<QueryData> fields;
       
        if (DataBaseUtil.isEmpty(pattern))
            return;              

        findTextBox.setText(pattern);
        
        query = new Query();
        fields = new ArrayList<QueryData>();
        catId = category.getValue();        
        if(catId != null) {            
            field = new QueryData(CategoryMeta.getId(),QueryData.Type.INTEGER,catId.toString());
            fields.add(field);
        }
        field = new QueryData(CategoryMeta.getDictionaryEntry(),QueryData.Type.STRING,pattern);
        fields.add(field);
        
        field = new QueryData(CategoryMeta.getIsSystem(),QueryData.Type.STRING,"N");
        fields.add(field);
        
        query.setFields(fields);
        
        window.setBusy(consts.get("querying"));
        
        service.callList("fetchByEntry", query, new AsyncCallback<ArrayList<IdNameVO>>() {
            public void onSuccess(ArrayList<IdNameVO> result) {
                setQueryResult(result);
            }

            public void onFailure(Throwable error) {
                setQueryResult(null);
                if (error instanceof NotFoundException) {
                    window.setDone(consts.get("noRecordsFound"));                   
                } else {
                    Window.alert("Error: Query failed; " + error.getMessage());
                    window.setError(consts.get("queryFailed"));
                }               
            }
        });                        
    }
    
    public void setQueryResult(String pattern, ArrayList<IdNameVO> list) {
        if (DataBaseUtil.isEmpty(pattern))
            return;              

        findTextBox.setText(pattern);
        setQueryResult(list);
    }

    private void setQueryResult(ArrayList<IdNameVO> list) {
        ArrayList<Item<Integer>> model;
        Item<Integer> item;

        dictEntTable.clear();

        model = new ArrayList<Item<Integer>>();
        if(list != null) {
            for (IdNameVO data : list)   {  
                item = new Item<Integer>(data.getId(), data.getName(), data.getDescription());
                item.setData(data);
                model.add(item);
            }
        }
        
        dictEntTable.setModel(model);
        
        window.setDone(consts.get("done"));
    }

}
