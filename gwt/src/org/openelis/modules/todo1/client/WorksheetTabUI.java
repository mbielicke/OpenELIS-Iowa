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
package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class WorksheetTabUI extends Screen {

    @UiTemplate("WorksheetTab.ui.xml")
    interface WorksheetTabUiBinder extends UiBinder<Widget, WorksheetTabUI> {
    };

    private static WorksheetTabUiBinder               uiBinder = GWT.create(WorksheetTabUiBinder.class);

    @UiField
    protected Table                                   table;

    protected Screen                                  parentScreen;

    protected EventBus                                parentBus;

    private boolean                                   visible, load, mySection;

    private ArrayList<ToDoWorksheetVO>                worksheets;

    private AsyncCallback<ArrayList<ToDoWorksheetVO>> getWorksheetCall;

    public WorksheetTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();

        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {

        mySection = true;
        load = true;

        addScreenHandler(table, "worksheetTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                visible = event.isVisible();
                update();
            }
        });

        /*
         * call for fetch data
         */
        getWorksheetCall = new AsyncCallback<ArrayList<ToDoWorksheetVO>>() {
            public void onSuccess(ArrayList<ToDoWorksheetVO> result) {
                worksheets = result;
                fireDataChange();
                parentScreen.clearStatus();
            }

            public void onFailure(Throwable error) {
                worksheets = null;
                fireDataChange();
                if (error instanceof NotFoundException) {
                    parentScreen.setDone(Messages.get().gen_noRecordsFound());
                } else {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    parentScreen.clearStatus();
                }
            }
        };
    }

    /*
     * Returns the current selected records's id
     */
    public Integer getSelectedId() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row != null)
            return ((ToDoWorksheetVO)row.getData()).getId();

        return null;
    }

    public void setMySectionOnly(boolean mySection) {
        if (this.mySection != mySection) {
            this.mySection = mySection;
            fireDataChange();
        }
    }

    /*
     * Refetch's the data
     */
    public void refresh() {
        load = true;
        update();
    }

    /*
     * update the data, chart, etc.
     */
    public void update() {
        if (visible && load) {
            load = false;
            parentScreen.setBusy(Messages.get().gen_fetching());
            ToDoService1Impl.INSTANCE.getWorksheet(getWorksheetCall);
        }
    }

    /*
     * Returns the fetched data as a table model
     */
    private ArrayList<Row> getTableModel() {
        Row row;
        ArrayList<Row> model;
        SystemUserPermission perm;

        model = new ArrayList<Row>();
        if (worksheets != null) {
            perm = UserCache.getPermission();

            for (ToDoWorksheetVO w : worksheets) {
                if (mySection && perm.getSection(w.getSectionName()) == null)
                    continue;

                row = new Row(w.getId(),
                              w.getSystemUserName(),
                              w.getSectionName(),
                              w.getDescription(),
                              w.getCreatedDate());
                row.setData(w);
                model.add(row);
            }
        }

        return model;
    }
}
