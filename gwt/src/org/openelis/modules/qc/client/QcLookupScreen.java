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
package org.openelis.modules.qc.client;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcLotViewDO;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.meta.QcMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcLookupScreen extends Screen implements HasActionHandlers<QcLookupScreen.Action> {

    protected TextBox     findTextBox;
    protected AppButton   findButton,okButton,cancelButton;
    protected TableWidget qcTable;
    protected ButtonGroup azButtons;

    private ArrayList<TableDataRow> selectionList;

    public enum Action {
        OK, CANCEL
    };

    public QcLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QcLookupDef.class));
        
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
        initializeDropdowns();
    }

    @SuppressWarnings("unchecked")
    private void initialize() {        

        selectionList = null;

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

        qcTable = (TableWidget)def.getWidget("qcTable");
        addScreenHandler(qcTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {

            public void onStateChange(StateChangeEvent<State> event) {
                qcTable.enable(true);

            }
        });

        qcTable.addSelectionHandler(new SelectionHandler() {
            public void onSelection(SelectionEvent event) {
                selectionList = qcTable.getSelections();
            }

        });

        qcTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
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
        ArrayList<QcLotViewDO> list;               
        
        list = null;
        
        if(selectionList != null) {
            list = new ArrayList<QcLotViewDO>();
            for(TableDataRow row: selectionList)
                list.add((QcLotViewDO)row.data);
        }
        
        window.close();
        ActionEvent.fire(this, Action.OK, list);
    }

    public void cancel() {
        window.close();
        ActionEvent.fire(this, Action.CANCEL, null);
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<QcLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        //
        // load location dropdown model
        //
        dictList  = CategoryCache.getBySystemName("laboratory_location");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)qcTable.getColumns().get(2).getColumnWidget()).setModel(model);
    }
    
    public void clearFields() {
        findTextBox.setText(null);
    }

    public void executeQuery(String pattern) {
        Query query;  
        QueryData field;
        ArrayList<QueryData> fields;

        if (DataBaseUtil.isEmpty(pattern))
            return;              

        findTextBox.setText(pattern);
        
        query = new Query();
        fields = new ArrayList<QueryData>();

        field = new QueryData();
        field.key = QcMeta.getName();
        field.type = QueryData.Type.STRING;
        field.query = pattern;
        fields.add(field);
        
        field = new QueryData();
        field.key = QcMeta.getIsActive();
        field.type = QueryData.Type.STRING;
        field.query = "Y";
        fields.add(field);
        
        field = new QueryData();
        field.key = QcMeta.getQcLotIsActive();
        field.type = QueryData.Type.STRING;
        field.query = "Y";
        fields.add(field);
        
        query.setFields(fields);

        window.setBusy(consts.get("querying"));

        QcService.get().fetchActiveByName(query, new AsyncCallback<ArrayList<QcLotViewDO>>() {
            public void onSuccess(ArrayList<QcLotViewDO> result) {
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
    
    public void setQueryResult(String pattern, ArrayList<QcLotViewDO> list) {
        if (DataBaseUtil.isEmpty(pattern))
            return;              

        findTextBox.setText(pattern);
        setQueryResult(list);
    }

    private void setQueryResult(ArrayList<QcLotViewDO> list) {
        ArrayList<TableDataRow> model;
        TableDataRow row;

        qcTable.clear();

        model = new ArrayList<TableDataRow>();
        if(list != null) {
            for (QcLotViewDO data : list)   {  
                row = new TableDataRow(data.getId(), data.getQcName(), data.getLotNumber(),
                                       data.getLocationId(), data.getUsableDate(),
                                       data.getExpireDate());
                row.data = data;
                model.add(row);
            }
        }
        
        qcTable.load(model);
        
        window.setDone(consts.get("done"));
    }

    public void enableMultiSelect(boolean multi) {
        qcTable.enableMultiSelect(multi);
    }
}
