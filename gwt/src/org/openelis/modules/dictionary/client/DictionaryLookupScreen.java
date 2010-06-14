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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.meta.CategoryMeta;
import org.openelis.utilcommon.DataBaseUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DictionaryLookupScreen extends Screen implements
                                                       HasActionHandlers<DictionaryLookupScreen.Action> {

    protected TextBox               findTextBox;
    protected AppButton             findButton,okButton,cancelButton;
    protected TableWidget           dictEntTable;
    protected Dropdown<Integer>     category;
    protected ButtonGroup           azButtons;

    private ArrayList<TableDataRow> selectionList;

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

        selectionList = null;

        category = (Dropdown)def.getWidget("category");
        addScreenHandler(category, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                category.setValue(null);
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                category.enable(true);
            }
        });

        findTextBox = (TextBox<String>)def.getWidget("findTextBox");
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
                executeQuery(findTextBox.getText());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        dictEntTable = (TableWidget)def.getWidget("dictEntTable");
        addScreenHandler(dictEntTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                dictEntTable.enable(true);
            }
        });

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

        // Get AZ buttons and setup Screen listeners and call to for query
        azButtons = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                String query;

                query = ((AppButton)event.getSource()).action;
                findTextBox.setText(query);
                executeQuery(query);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                azButtons.enable(true);
            }

        });
    }

    public void ok() {
        ArrayList<IdNameVO> list;               
        
        list = null;
        
        selectionList = dictEntTable.getSelections();
        if(selectionList != null) {
            list = new ArrayList<IdNameVO>();
            for(TableDataRow row: selectionList)
                list.add((IdNameVO)row.data);
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
        ArrayList<TableDataRow> model;
        TableDataRow row;
        ArrayList<IdNameVO> list;

        model = null;
        try {
            list = service.callList("fetchByCategoryName","%");
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            for (IdNameVO data : list) {
                row = new TableDataRow(data.getId(),data.getName());
                model.add(row);
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
            field = new QueryData();
            field.key = CategoryMeta.getId();
            field.type = QueryData.Type.INTEGER;
            field.query = catId.toString();
            fields.add(field);
        }
        field = new QueryData();
        field.key = CategoryMeta.getDictionaryEntry();
        field.type = QueryData.Type.STRING;
        field.query = pattern;
        fields.add(field);
        
        field = new QueryData();
        field.key = CategoryMeta.getIsSystem();
        field.type = QueryData.Type.STRING;
        field.query = "N";
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
        ArrayList<TableDataRow> model;
        TableDataRow row;

        dictEntTable.clear();

        model = new ArrayList<TableDataRow>();
        if(list != null) {
            for (IdNameVO data : list)   {  
                row = new TableDataRow(data.getId(), data.getName(), data.getDescription());
                row.data = data;
                model.add(row);
            }
        }
        
        dictEntTable.load(model);
        
        window.setDone(consts.get("done"));
    }

}
