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
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.modules.sample1.client.PatientPermission;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.utilcommon.TurnaroundUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ReleasedTabUI extends Screen {

    @UiTemplate("ReleasedTab.ui.xml")
    interface ReleasedTabUiBinder extends UiBinder<Widget, ReleasedTabUI> {
    };

    private static ReleasedTabUiBinder               uiBinder = GWT.create(ReleasedTabUiBinder.class);

    @UiField
    protected Table                                  table;

    @UiField
    protected Dropdown<String>                       domain;

    protected Screen                                 parentScreen;

    protected EventBus                               parentBus;

    protected PatientPermission                      patientPermission;

    private boolean                                  visible, load, mySection;

    private ArrayList<AnalysisViewVO>                analyses;

    private AsyncCallback<ArrayList<AnalysisViewVO>> getReleasedCall;

    public ReleasedTabUI(Screen parentScreen, PatientPermission patientPermission) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.patientPermission = patientPermission;

        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        Item<String> row;
        List<DictionaryDO> list;
        ArrayList<Item<String>> model;

        mySection = true;
        load = true;

        addScreenHandler(table, "releasedTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                parentBus.fireEvent(event);
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

        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive())) {
                row = new Item<String>(data.getCode(), data.getEntry());
                model.add(row);
            }
        }
        domain.setModel(model);

        /*
         * call for fetch data
         */
        getReleasedCall = new AsyncCallback<ArrayList<AnalysisViewVO>>() {
            public void onSuccess(ArrayList<AnalysisViewVO> result) {
                analyses = result;
                fireDataChange();
                parentScreen.clearStatus();
            }

            public void onFailure(Throwable error) {
                analyses = null;
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
            return ((AnalysisViewVO)row.getData()).getSampleId();

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
            ToDoService1Impl.INSTANCE.getReleased(getReleasedCall);
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
        if (analyses != null) {
            perm = UserCache.getPermission();

            for (AnalysisViewVO a : analyses) {
                if ( (mySection && perm.getSection(a.getSectionName()) == null) ||
                    !patientPermission.canViewSample(a))
                    continue;

                row = new Row(a.getAccessionNumber(),
                              a.getDomain(),
                              a.getSectionName(),
                              a.getTestName(),
                              a.getMethodName(),
                              TurnaroundUtil.getCombinedYM(a.getCollectionDate(),
                                                           a.getCollectionTime()),
                              a.getReleasedDate(),
                              a.getAnalysisResultOverride(),
                              a.getToDoDescription(),
                              a.getPrimaryOrganizationName());
                row.setData(a);
                model.add(row);
            }
        }

        return model;
    }
}