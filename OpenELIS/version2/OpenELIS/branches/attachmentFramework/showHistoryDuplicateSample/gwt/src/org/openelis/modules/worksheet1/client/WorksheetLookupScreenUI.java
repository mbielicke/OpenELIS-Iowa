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
package org.openelis.modules.worksheet1.client;

import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.modules.worksheet.client.WorksheetService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public class WorksheetLookupScreenUI extends Screen 
                                     implements HasActionHandlers<WorksheetLookupScreenUI.Action> {
    
    @UiTemplate("WorksheetLookup.ui.xml")
    interface WorksheetLookupUiBinder extends UiBinder<Widget, WorksheetLookupScreenUI> {
    };
    
    private static WorksheetLookupUiBinder uiBinder = GWT.create(WorksheetLookupUiBinder.class);

    @UiField
    protected AutoComplete                 systemUserId;
    @UiField
    protected Button                       search, select, cancel;
    @UiField
    protected Calendar                     createdDate;
    @UiField
    protected Dropdown<Integer>            statusId, tableStatusId;
    @UiField
    protected Table                        worksheetTable;
    @UiField
    protected TextBox<Integer>             worksheetId;
    @UiField
    protected TextBox<String>              description;

    public enum Action {
        SELECT, CANCEL
    };

    public WorksheetLookupScreenUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();

        setState(QUERY);
        fireDataChange();
        worksheetId.setFocus(true);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        Integer statusWorkingId;
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model, model2;

        //
        // screen fields and buttons
        //
        addScreenHandler(worksheetId, WorksheetCompletionMeta.getId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                worksheetId.setEnabled(true);
                worksheetId.setQueryMode(true);
            }
        });

        addScreenHandler(systemUserId, WorksheetCompletionMeta.getSystemUserId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                systemUserId.setEnabled(true);
            }
        });

        systemUserId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                
                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    systemUserId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        addScreenHandler(createdDate, WorksheetCompletionMeta.getCreatedDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                createdDate.setEnabled(true);
                createdDate.setQueryMode(true);
            }
        });

        addScreenHandler(statusId, WorksheetCompletionMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                statusId.setEnabled(true);
                statusId.setQueryMode(true);
            }
        });

        addScreenHandler(description, WorksheetCompletionMeta.getDescription(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(true);
                description.setQueryMode(true);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(true);
            }
        });

        //
        // analysis search results table
        //
        addScreenHandler(worksheetTable, "worksheetTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                worksheetTable.setEnabled(true);
                worksheetTable.setAllowMultipleSelection(false);
            }
        });

        worksheetTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (worksheetTable.getSelectedRow() != -1)
                    select.setEnabled(true);
            }
        });
        
        worksheetTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (worksheetTable.getSelectedRow() == -1)
                    select.setEnabled(false);
            }
        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                select.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(true);
            }
        });

        statusWorkingId = null;
        //
        // load worksheet status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("worksheet_status");
        model = new ArrayList<Item<Integer>>();
        model2 = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList) {
            if ("worksheet_working".equals(resultDO.getSystemName()))
                statusWorkingId = resultDO.getId();
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
            model2.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        }
        statusId.setModel(model);
        statusId.setValue(statusWorkingId);
        tableStatusId.setModel(model2);
    }
    
    @UiHandler("search")
    protected void executeQuery(ClickEvent event) {
        Query query;
        Validation validation;

        validation = validate();
        
        if (validation.getStatus() != VALID) {
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
        ArrayList<Item<Integer>> model;
        Item<Integer> row;
        
        model = new ArrayList<Item<Integer>>();
        if (list == null || list.size() == 0) {
            window.setDone(Messages.get().noRecordsFound());
        } else {
            for (WorksheetViewDO worksheetRow : list) {
                row = new Item<Integer>(5);
                row.setKey(worksheetRow.getId());
                row.setCell(0, worksheetRow.getId());
                row.setCell(1, worksheetRow.getSystemUser());
                row.setCell(2, worksheetRow.getCreatedDate());
                row.setCell(3, worksheetRow.getStatusId());
                row.setCell(4, worksheetRow.getDescription());
                row.setData(worksheetRow);
                model.add(row);
            }

            window.setDone(Messages.get().queryingComplete());
        }

        worksheetTable.setModel(model);
    }
    
    @UiHandler("select")
    protected void select(ClickEvent event) {
        Item<Integer> selectedRow;
        
        selectedRow = null;
        if (worksheetTable.getSelectedRow() != -1)
            selectedRow = worksheetTable.getRowAt(worksheetTable.getSelectedRow());
        window.close();
        ActionEvent.fire(this, Action.SELECT, selectedRow);
    }
    
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
        ActionEvent.fire(this, Action.CANCEL, null);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    public void setWindow(WindowInt window) {
        super.setWindow(window);
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {                
                worksheetTable.setModel(null);
            }
        });
    }
}