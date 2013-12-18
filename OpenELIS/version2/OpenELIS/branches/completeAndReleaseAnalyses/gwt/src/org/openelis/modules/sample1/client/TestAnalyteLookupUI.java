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

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
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
 * This class is used to allow users to add projects related to a sample
 */
public abstract class TestAnalyteLookupUI extends Screen {

    @UiTemplate("TestAnalyteLookup.ui.xml")
    interface TestAnalyteLookupUIBinder extends UiBinder<Widget, TestAnalyteLookupUI> {
    };

    private static TestAnalyteLookupUIBinder uiBinder = GWT.create(TestAnalyteLookupUIBinder.class);

    protected TestManager                    manager;

    protected Integer                        rowGroup;

    @UiField
    protected Table                          table;

    @UiField
    protected Dropdown<Integer>              type;

    @UiField
    protected Button                         okButton, cancelButton;

    protected ArrayList<TestAnalyteViewDO>   analytes;

    public TestAnalyteLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Item<Integer>>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() > 0)
                    event.cancel();
            }
        });

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

        // type dropdown
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("test_analyte_type"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        type.setModel(model);
    }

    public void setData(TestManager manager, Integer rowGroup) {
        this.manager = manager;
        this.rowGroup = rowGroup;

        setState(state);
        fireDataChange();
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public ArrayList<TestAnalyteViewDO> getAnalytes() {
        return analytes;
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        TestAnalyteViewDO ta;
        Row row;

        if (analytes == null)
            analytes = new ArrayList<TestAnalyteViewDO>();
        else
            analytes.clear();

        /*
         * make a list of the anaytes selected by the user
         */
        for (int i = 0; i < table.getRowCount(); i++ ) {
            row = table.getRowAt(i);
            if ("Y".equals(row.getCell(0))) {
                ta = (TestAnalyteViewDO)row.getData();
                analytes.add(ta);
            }
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
        Row row;
        TestAnalyteViewDO data;
        TestAnalyteManager tam;
        ArrayList<Row> model;

        model = new ArrayList<Row>();

        try {
            tam = manager.getTestAnalytes();
            for (int i = 0; i < tam.rowCount(); i++ ) {
                if ( !tam.getAnalyteAt(i, 0).getRowGroup().equals(rowGroup))
                    continue;
                /*
                 * show the analytes belonging to the selected row group
                 */
                while (i < tam.rowCount() && tam.getAnalyteAt(i, 0).getRowGroup().equals(rowGroup)) {
                    data = tam.getAnalyteAt(i, 0);
                    row = new Item<Integer>(3);
                    row.setCell(0, "N");
                    row.setCell(1, data.getAnalyteName());
                    row.setCell(2, data.getTypeId());
                    row.setData(data);
                    model.add(row);
                    i++ ;
                }
                break;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return model;
    }
}
