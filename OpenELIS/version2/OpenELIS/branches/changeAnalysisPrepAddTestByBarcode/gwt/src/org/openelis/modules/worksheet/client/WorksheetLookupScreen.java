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
package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;
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
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.meta.WorksheetCompletionMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetLookupScreen extends Screen implements HasActionHandlers<WorksheetLookupScreen.Action> {

    protected AppButton             searchButton, selectButton, cancelButton;
    protected AutoComplete<Integer> systemUserId;
    protected CalendarLookUp        createdDate;
    protected Dropdown<Integer>     statusId;
    protected TableWidget           worksheetTable;
    protected TextBox<Integer>      worksheetNumber;
    protected TextBox<String>       description;

    public enum Action {
        SELECT, CANCEL
    };

    public WorksheetLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetLookupDef.class));

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred command.
     */
    private void postConstructor() {
        initialize();

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {                
                worksheetTable.clear();
            }
        });

        setState(State.QUERY);
        initializeDropdowns();
        DataChangeEvent.fire(this);
        setFocus(worksheetNumber);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // screen fields and buttons
        //
        worksheetNumber = (TextBox<Integer>)def.getWidget(WorksheetCompletionMeta.getId());
        addScreenHandler(worksheetNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetNumber.enable(true);
                worksheetNumber.setQueryMode(true);
            }
        });

        systemUserId = (AutoComplete)def.getWidget(WorksheetCompletionMeta.getSystemUserId());
        addScreenHandler(systemUserId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                systemUserId.enable(true);
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<TableDataRow> model;
                
                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (SystemUserVO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        createdDate = (CalendarLookUp)def.getWidget(WorksheetCompletionMeta.getCreatedDate());
        addScreenHandler(createdDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                createdDate.enable(true);
                createdDate.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(WorksheetCompletionMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(true);
                statusId.setQueryMode(true);
            }
        });

        description = (TextBox)def.getWidget(WorksheetCompletionMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(true);
                description.setQueryMode(true);
            }
        });

        searchButton = (AppButton)def.getWidget("searchButton");
        addScreenHandler(searchButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                searchButton.enable(true);
            }
        });

        //
        // analysis search results table
        //
        worksheetTable = (TableWidget)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.enable(true);
                worksheetTable.enableMultiSelect(false);
            }
        });

        worksheetTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetTable.getSelectedRow() != -1)
                    selectButton.enable(true);
            }
        });
        
        worksheetTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent event) {
                if (worksheetTable.getSelectedRow() == -1)
                    selectButton.enable(false);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        selectButton = (AppButton)def.getWidget("select");
        addScreenHandler(selectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                select();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectButton.enable(false);
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
    
    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<Object>       statusWorking;
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        statusWorking = new ArrayList<Object>();
        //
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList) {
            if ("worksheet_working".equals(resultDO.getSystemName()))
                statusWorking.add(resultDO.getId());
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        }
        statusId.setModel(model);
        statusId.setSelectionKeys(statusWorking);
        ((Dropdown<Integer>)worksheetTable.getColumns().get(3).getColumnWidget()).setModel(model);
    }

    //
    // overriding AutoComplete's getQuery to return the id of the
    // selection instead of the text
    //
    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData            qd;
        QueryFieldUtil       qField;
        TableDataRow         row;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof AutoComplete) {
                row = ((AutoComplete)def.getWidget(key)).getSelection();
                if(row != null && row.key != null) {
                    qd = new QueryData();
                    qd.setKey(key);
                    qd.setQuery(((Integer)row.key).toString());
                    qd.setType(QueryData.Type.INTEGER);
                    list.add(qd);

                    qField = new QueryFieldUtil();
                    qField.parse(qd.getQuery());
                }
            } else if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }
        return list;
    }

    protected void executeQuery() {
        Query query;

        if (!validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        if (query.getFields().size() > 0) {
            window.setBusy(Messages.get().querying());
    
            query.setRowsPerPage(50);
            WorksheetService.get().query(query, new AsyncCallback<ArrayList<WorksheetViewDO>>() {
                public void onSuccess(ArrayList<WorksheetViewDO> list) {
                    setQueryResult(list);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: WorksheetLookup call query failed; "+error.getMessage());
                        window.setError(Messages.get().queryFailed());
                    }
                }
            });
        } else {
            window.setDone(Messages.get().emptyQueryException());
        }
    }

    private void setQueryResult(ArrayList<WorksheetViewDO> list) {
        int                       i, j;
        ArrayList<AnalysisViewDO> testList;
        ArrayList<TableDataRow>   model;
        TableDataRow              row;
        AnalysisViewDO            aVDO;
        WorksheetViewDO           worksheetRow;
        
        window.setDone(Messages.get().queryingComplete());

        if (list == null || list.size() == 0) {
            window.setDone(Messages.get().noRecordsFound());
            
            worksheetTable.clear();
        } else {
            window.setDone(Messages.get().queryingComplete());

            model = new ArrayList<TableDataRow>();
            for (i = 0; i < list.size(); i++) {
                worksheetRow = list.get(i);
                
                row = new TableDataRow(5);
                row.key = worksheetRow.getId();
                row.cells.get(0).value = worksheetRow.getId();
                row.cells.get(1).value = worksheetRow.getSystemUser();
                row.cells.get(2).value = worksheetRow.getCreatedDate();
                row.cells.get(3).value = worksheetRow.getStatusId();
                row.cells.get(4).value = worksheetRow.getDescription();
                row.data = worksheetRow;
                model.add(row);
            }

            worksheetTable.load(model);
        }
    }
    
    private void select() {
        ArrayList<TableDataRow> selections;
        
        selections = worksheetTable.getSelections();
        window.close();
        if (selections.size() > 0)
            ActionEvent.fire(this, Action.SELECT, selections);
    }
    
    private void cancel() {
        window.close();
        ActionEvent.fire(this, Action.CANCEL, null);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}