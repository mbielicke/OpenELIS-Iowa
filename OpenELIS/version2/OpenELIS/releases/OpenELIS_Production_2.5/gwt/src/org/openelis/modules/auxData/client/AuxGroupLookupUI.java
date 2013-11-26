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
package org.openelis.modules.auxData.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to choose aux groups for a sample, order
 * etc.
 */
public abstract class AuxGroupLookupUI extends Screen {

    @UiTemplate("AuxGroupLookup.ui.xml")
    interface AuxGroupLookupUIBinder extends UiBinder<Widget, AuxGroupLookupUI> {
    };

    private static AuxGroupLookupUIBinder uiBinder = GWT.create(AuxGroupLookupUIBinder.class);

    @UiField
    protected Table                       table;

    @UiField
    protected Button                      okButton, cancelButton;

    protected ArrayList<Row>              groupsModel;

    protected ArrayList<Integer>             groupIds;

    public AuxGroupLookupUI() {
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

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });
        
        table.setAllowMultipleSelection(true);

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }
        });
    }
    
    /**
     * refreshes the screen's view by setting the state and loading the data in
     * the widgets
     */
    public void setData() {
        setState(state);
        fireDataChange();
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();
    
    /**
     * returns the list of the ids of the aux groups selected by the user
     */
    public ArrayList<Integer> getGroupIds() {
        return groupIds;
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        Integer selRows[];
        AuxFieldGroupDO data;        

        if (groupIds == null)
            groupIds = new ArrayList<Integer>();
        else
            groupIds.clear();

        /*
         * create the list of the ids of the aux groups selected by the user
         */
        selRows = table.getSelectedRows();
        for (int i = 0; i < selRows.length; i++ ) {
            data = (AuxFieldGroupDO)table.getRowAt(selRows[i]).getData();
            groupIds.add(data.getId());
        }

        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<AuxFieldGroupDO> groups;
        AuxFieldGroupDO data;
        Row row;

        if (groupsModel != null)
            return groupsModel;

        groupsModel = new ArrayList<Row>();
        try {
            groups = AuxiliaryService.get().fetchActive();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return groupsModel;
        }

        for (int i = 0; i < groups.size(); i++ ) {
            data = groups.get(i);

            row = new Row(2);

            row.setCell(0, data.getName());
            row.setCell(1, data.getDescription());
            row.setData(data);
            groupsModel.add(row);
        }

        return groupsModel;
    }
}