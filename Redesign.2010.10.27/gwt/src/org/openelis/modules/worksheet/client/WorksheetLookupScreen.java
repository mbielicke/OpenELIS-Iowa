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
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.meta.WorksheetCompletionMeta;

public class WorksheetLookupScreen extends Screen implements HasActionHandlers<WorksheetLookupScreen.Action> {

    protected Button                searchButton, selectButton, cancelButton;
    protected AutoComplete          systemUserId;
    protected Calendar              createdDate;
    protected Dropdown<Integer>     statusId;
    protected Table                 worksheetTable;
    protected TextBox<Integer>      worksheetNumber;

    private ScreenService           userService;
    
    public enum Action {
        SELECT, CANCEL
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

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                worksheetTable.clear();
            }
        });

        setState(State.DEFAULT);
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
        worksheetNumber = (TextBox)def.getWidget(WorksheetCompletionMeta.getId());
        addScreenHandler(worksheetNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetNumber.setEnabled(true);
                worksheetNumber.setQueryMode(true);
            }
        });

        systemUserId = (AutoComplete)def.getWidget(WorksheetCompletionMeta.getSystemUserId());
        addScreenHandler(systemUserId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                systemUserId.setEnabled(true);
                systemUserId.setQueryMode(true);
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                String param = "";
                
                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    users = userService.callList("fetchByLoginName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.toString());
                }
            }
        });

        createdDate = (Calendar)def.getWidget(WorksheetCompletionMeta.getCreatedDate());
        addScreenHandler(createdDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                createdDate.setEnabled(true);
                createdDate.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(WorksheetCompletionMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                statusId.setEnabled(true);
                statusId.setQueryMode(true);
            }
        });

        searchButton = (Button)def.getWidget("searchButton");
        addScreenHandler(searchButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                searchButton.setEnabled(true);
            }
        });

        //
        // analysis search results table
        //
        worksheetTable = (Table)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.setEnabled(true);
                worksheetTable.setAllowMultipleSelection(false);
            }
        });

        worksheetTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent event) {
                if (worksheetTable.getSelectedRow() != -1)
                    selectButton.setEnabled(true);
            }
        });
        
        worksheetTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent event) {
                if (worksheetTable.getSelectedRow() == -1)
                    selectButton.setEnabled(false);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        selectButton = (Button)def.getWidget("select");
        addScreenHandler(selectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                select();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectButton.setEnabled(false);
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
    }
    
    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        //
        // load worksheet status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("worksheet_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);
        ((Dropdown<Integer>)worksheetTable.getColumnWidget(3)).setModel(model);
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
                        com.google.gwt.user.client.Window.alert("Error: WorksheetLookup call query failed; "+error.getMessage());
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
        ArrayList<Row>            model;
        Row                       row;
        AnalysisViewDO            aVDO;
        WorksheetViewDO           worksheetRow;
        
        window.setDone(consts.get("queryingComplete"));

        if (list == null || list.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
            
            worksheetTable.clear();
        } else {
            window.setDone(consts.get("queryingComplete"));

            model = new ArrayList<Row>();
            for (i = 0; i < list.size(); i++) {
                worksheetRow = list.get(i);
                
                row = new Row(11);
                //row.key = worksheetRow.getId();
                row.setCell(0,worksheetRow.getId());
                row.setCell(1,worksheetRow.getSystemUser());
                row.setCell(2,worksheetRow.getCreatedDate());
                row.setCell(3,worksheetRow.getStatusId());

                testList = worksheetRow.getTestList();
                if (testList != null && testList.size() > 0) {
                    for (j = 0; j < testList.size(); j++) {
                        aVDO = testList.get(j);
                        if (j != 0) {
                            row.setCell(0,null);
                            row.setCell(1,null);
                            row.setCell(2,null);
                            row.setCell(3,null);
                        }
                        row.setCell(4,aVDO.getTestName());
                        row.setCell(5,aVDO.getMethodName());
                        row.setData(worksheetRow);
                        model.add(row);
                    }
                } else {
                    row.setCell(4,null);
                    row.setCell(5,null);
                    row.setData(worksheetRow);
                    model.add(row);
                }
            }

            worksheetTable.setModel(model);
        }
    }
    
    private void select() {
    	Integer[] sels;
        ArrayList<Row> selections;
        
        sels = worksheetTable.getSelectedRows();
        selections = new ArrayList<Row>(sels.length);
        
        for(int i = 0; i < sels.length; i++)
        	selections.add(worksheetTable.getRowAt(sels[i]));
        
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
