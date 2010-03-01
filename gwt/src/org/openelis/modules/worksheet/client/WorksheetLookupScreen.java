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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.meta.WorksheetCompletionMeta;

public class WorksheetLookupScreen extends Screen 
                                           implements HasActionHandlers<WorksheetLookupScreen.Action> {

    protected AppButton         searchButton, okButton, cancelButton;
    protected AutoComplete<Integer> systemUserId;
    protected CalendarLookUp    createdDate;
    protected Dropdown<Integer> statusId;
    protected TableWidget       worksheetTable;
    protected TextBox<Integer>  worksheetNumber;
    
    private ScreenService         userService;
    
    public enum Action {
        OK, CANCEL
    };

    public WorksheetLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetLookupDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheet.server.WorksheetService");
        userService = new ScreenService("controller?service=org.openelis.server.SystemUserService");

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
        setState(State.DEFAULT);
        setState(State.QUERY);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // screen fields and buttons
        //
        worksheetNumber = (TextBox)def.getWidget(WorksheetCompletionMeta.getId());
        addScreenHandler(worksheetNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetNumber.enable(true);
                worksheetNumber.setQueryMode(true);
            }
        });

        systemUserId = (AutoComplete<Integer>)def.getWidget(WorksheetCompletionMeta.getSystemUserId());
        addScreenHandler(systemUserId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                systemUserId.enable(true);
                systemUserId.setQueryMode(true);
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SecuritySystemUserDO> users;
                ArrayList<TableDataRow> model;
                
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    users = userService.callList("fetchByLogin", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (SecuritySystemUserDO user : users)
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
                    okButton.enable(true);
            }
        });
        
        worksheetTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent event) {
                if (worksheetTable.getSelectedRow() == -1)
                    okButton.enable(false);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
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
                okButton.enable(false);
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
        ArrayList<DictionaryDO> dictList;
        ArrayList<TableDataRow> model;

        //
        // load worksheet status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("worksheet_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);
        ((Dropdown<Integer>)worksheetTable.getColumns().get(3).getColumnWidget()).setModel(model);
    }

    protected void executeQuery() {
        Query query;

        if (!validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        if (query.getFields().size() > 0) {
            window.setBusy(consts.get("querying"));
    
            service.callList("query", query, new AsyncCallback<ArrayList<WorksheetViewDO>>() {
                public void onSuccess(ArrayList<WorksheetViewDO> list) {
                    setQueryResult(list);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(consts.get("noRecordsFound"));
                    } else {
                        Window.alert("Error: WorksheetLookup call query failed; "+error.getMessage());
                        window.setError(consts.get("queryFailed"));
                    }
                }
            });
        } else {
            window.setDone(consts.get("emptyQueryException"));
        }
    }

    private void setQueryResult(ArrayList<WorksheetViewDO> list) {
        int                       i, j;
        ArrayList<AnalysisViewDO> testList;
        ArrayList<TableDataRow>   model;
        TableDataRow              row;
        WorksheetViewDO           worksheetRow;
        
        window.setDone(consts.get("queryingComplete"));

        if (list == null || list.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
            
            worksheetTable.clear();
        } else {
            window.setDone(consts.get("queryingComplete"));

            model = new ArrayList<TableDataRow>();
            for (i = 0; i < list.size(); i++) {
                worksheetRow = list.get(i);
                
                row = new TableDataRow(11);
                row.key = worksheetRow.getId();
                row.cells.get(0).value = worksheetRow.getId();
                row.cells.get(1).value = worksheetRow.getSystemUser();
                row.cells.get(2).value = worksheetRow.getCreatedDate();
                row.cells.get(3).value = worksheetRow.getStatusId();
/*                
                testList = worksheetRow.getTestList();
                if (testList != null) {
                    for (j = 0; j < testList.size(); j++) { 
                        row.cells.get(4).value = worksheetRow.getTestName();
                        row.cells.get(5).value = worksheetRow.getMethodName();
                    }
                }
*/
                row.data = worksheetRow;

                model.add(row);
            }

            worksheetTable.load(model);
        }
    }
    
    private void ok() {
        ArrayList<TableDataRow> selections = worksheetTable.getSelections();
        
        if (selections.size() > 0)
            ActionEvent.fire(this, Action.OK, selections);
        
        window.close();
    }
    
    private void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
