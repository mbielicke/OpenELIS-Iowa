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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.SampleItemManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SampleItemLookupScreen extends Screen implements HasActionHandlers<SampleItemLookupScreen.Action> {

    protected TableWidget                sampleItemTable;
    protected AppButton                  okButton, cancelButton;

    private ArrayList<TestAnalyteViewDO> selections;
    private SampleItemManager            manager;
    private SampleTreeUtility            treeUtil; 

    public enum Action {
        OK, CANCEL
    };

    public SampleItemLookupScreen(SampleItemManager manager, SampleTreeUtility treeUtil) throws Exception {
        super((ScreenDefInt)GWT.create(SampleItemLookupDef.class));

        this.manager = manager;
        this.treeUtil = treeUtil;

        // Setup link between Screen and widget Handlers
        initialize();
        initializeDropdowns();
        
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        selections = null;

        sampleItemTable = (TableWidget)def.getWidget("sampleItemTable");
        addScreenHandler(sampleItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTable.load(getTestAnalyteTable());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTable.enable(true);
            }
        });
        
        sampleItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() < 2)
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
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("type_of_sample");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        ((Dropdown)sampleItemTable.getColumnWidget("type")).setModel(model);
    }

    public void ok() {
        ActionEvent.fire(this, Action.OK, selections);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<SampleItemLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void refresh(SampleItemManager manager) {
        this.manager = manager;
        DataChangeEvent.fire(this);
    }

    private ArrayList<TableDataRow> getTestAnalyteTable() {
        ArrayList<TableDataRow> model;
        SampleItemViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        for (int i = 0; i < manager.count(); i++ ) {
            data = manager.getSampleItemAt(i);
            row = new TableDataRow(3);
            row.cells.get(0).setValue(data.getItemSequence() + " - " +
                                          treeUtil.formatTreeString(data.getContainer()));
            row.cells.get(1).setValue(data.getTypeOfSampleId());
            row.cells.get(2).setValue("N");
            model.add(row);
        }

        return model;
    }
}
