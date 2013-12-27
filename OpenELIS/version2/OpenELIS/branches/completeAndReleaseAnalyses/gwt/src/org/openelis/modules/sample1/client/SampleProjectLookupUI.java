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
package org.openelis.modules.sample1.client;

import static org.openelis.ui.screen.State.*;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;

import org.openelis.constants.Messages;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to add/remove/change projects related to a
 * sample
 */
public abstract class SampleProjectLookupUI extends Screen {

    @UiTemplate("SampleProjectLookup.ui.xml")
    interface SampleProjectLookupUIBinder extends UiBinder<Widget, SampleProjectLookupUI> {
    };

    private static SampleProjectLookupUIBinder uiBinder = GWT.create(SampleProjectLookupUIBinder.class);

    protected SampleManager1                   manager;

    @UiField
    protected Table                            table;

    @UiField
    protected AutoComplete                     project;

    @UiField
    protected Button                           addProjectButton, removeProjectButton, okButton;

    public SampleProjectLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                removeProjectButton.setEnabled(isState(ADD, UPDATE));
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || event.getCol() == 1)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                SampleProjectViewDO data;
                ProjectDO proj;
                Object val;
                AutoCompleteValue row;

                r = event.getRow();
                c = event.getCol();

                data = manager.project.get(r);

                val = table.getValueAt(r, c);

                switch (c) {
                    case 0:
                        row = (AutoCompleteValue)val;
                        if (row != null) {
                            proj = (ProjectDO)row.getData();
                            data.setProjectId(proj.getId());
                            data.setProjectName(proj.getName());
                            data.setProjectDescription(proj.getDescription());
                        } else {
                            data.setProjectId(null);
                            data.setProjectName(null);
                            data.setProjectDescription(null);
                        }

                        table.setValueAt(r, 1, data.getProjectDescription());
                        break;
                    case 2:
                        data.setIsPermanent((String)val);
                        break;
                }
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                window.setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getDescription());
                        row.setData(data);
                        model.add(row);
                    }
                    project.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                SampleProjectViewDO data;

                data = manager.project.add();
                data.setIsPermanent("N");
                removeProjectButton.setEnabled(true);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.project.remove(event.getIndex());
                removeProjectButton.setEnabled(false);
            }
        });

        addScreenHandler(addProjectButton, "addProjectButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addProjectButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addScreenHandler(removeProjectButton, "removeProjectButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeProjectButton.setEnabled(false);
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });
    }

    /**
     * refreshes the screen's view by setting the state and loading the data in
     * the widgets
     */
    public void setData(SampleManager1 manager, State state) {
        this.manager = manager;
        setState(state);
        fireDataChange();

        if (isState(ADD, UPDATE) && table.getRowCount() > 0)
            table.startEditing(0, 0);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    @UiHandler("addProjectButton")
    protected void addProject(ClickEvent event) {
        int r;

        table.addRow();
        r = table.getRowCount() - 1;
        table.selectRowAt(r);
        table.scrollToVisible(r);
        table.startEditing(r, 0);
    }

    @UiHandler("removeProjectButton")
    protected void removeProject(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {        
        Validation validation;
        
        table.finishEditing();
        
        validation = validate();

        if (validation.getStatus() != VALID) {
            window.setError(Messages.get().correctErrors());
            return;
        }
        
        window.close();
        ok();
    }    

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        SampleProjectViewDO data;
        Row row;

        model = new ArrayList<Row>();
        for (int i = 0; i < manager.project.count(); i++ ) {
            data = manager.project.get(i);

            row = new Row(3);

            row.setCell(0, new AutoCompleteValue(data.getProjectId(), data.getProjectName()));
            row.setCell(1, data.getProjectDescription());
            row.setCell(2, data.getIsPermanent());
            model.add(row);
        }

        return model;
    }
}