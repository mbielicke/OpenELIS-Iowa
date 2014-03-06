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
package org.openelis.modules.worksheetCompletion.client;

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

public class OverridesTabUI extends Screen {

    @UiTemplate("OverridesTab.ui.xml")
    interface WorksheetItemTabUiBinder extends UiBinder<Widget, OverridesTabUI> {
    };
    
    private static WorksheetItemTabUiBinder             uiBinder = GWT.create(WorksheetItemTabUiBinder.class);

    private boolean                                     canEdit, isVisible;
    private WorksheetManager1                           manager, displayedManager;

    @UiField
    protected Button                                    editMultipleButton;
    @UiField
    protected Table                                     overridesTable;
    
    protected Screen                                    parentScreen;
    
    public OverridesTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        if (bus != null)
            setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        manager = null;
        displayedManager = null;
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        addScreenHandler(overridesTable, "overridesTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                overridesTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                overridesTable.setEnabled(true);
                overridesTable.setAllowMultipleSelection(true);
                overridesTable.unselectAll();
            }
        });

        overridesTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                if (overridesTable.getSelectedRows().length > 1 && isState(ADD, UPDATE) && canEdit)
                    editMultipleButton.setEnabled(true);
                else
                    editMultipleButton.setEnabled(false);
            }
        });
        
        overridesTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                DataObject data;
                SectionPermission perm;
                WorksheetAnalysisViewDO waVDO;

                if (!isState(UPDATE) || !canEdit || getUpdatePushMode() || event.getCol() < 5) {
                    event.cancel();
                } else {
                    data = manager.getObject((String)overridesTable.getRowAt(event.getRow())
                                                                   .getData());
                    if (data instanceof WorksheetAnalysisViewDO) {
                        waVDO = (WorksheetAnalysisViewDO)data;
                        if (waVDO.getFromOtherId() != null ||
                            (waVDO.getQcLotId() != null && event.getCol() == 7)) {
                            event.cancel();
                        } else if (waVDO.getAnalysisId() != null) {
                            if (Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                                Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
                                event.cancel();
                            } else {
                                perm = UserCache.getPermission().getSection(waVDO.getSectionName());
                                if (perm == null || !perm.hasCompletePermission()) {
                                    Window.alert(Messages.get()
                                                         .worksheet_completePermissionRequiredForOperation(waVDO.getSectionName(),
                                                                                                           Messages.get()
                                                                                                                   .edit()));
                                    event.cancel();
                                }
                            }
                        }
                    } else {
                        event.cancel();
                    }
                }
            }
        });
        
        overridesTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                ArrayList<String> userNames;
                ArrayList<SystemUserVO> validUserVOs;
                Object val;
                Row row;
                String validUsers;
                WorksheetAnalysisViewDO waVDO;

                r = event.getRow();
                c = event.getCol();
                
                row = overridesTable.getRowAt(r);
                val = overridesTable.getValueAt(r,c);

                waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)row.getData());
                switch (c) {
                    case 5:
                        validUsers = "";
                        if (val != null) {
                            userNames = new ArrayList<String>(Arrays.asList(((String)val).split(",")));
                            try {
                                validUserVOs = UserCache.validateSystemUsers(userNames);
                                for (SystemUserVO userVO : validUserVOs) {
                                    if (userNames.contains(userVO.getLoginName())) {
                                        if (validUsers.length() > 0)
                                            validUsers += ",";
                                        validUsers += userVO.getLoginName();
                                        userNames.remove(userVO.getLastName());
                                    }
                                }
                                waVDO.setSystemUsers(validUsers);
                                if (userNames.size() > 0)
                                    Window.alert(Messages.get().worksheet_invalidUsersException(userNames.toString()));
                            } catch (Exception anyE) {
                                Window.alert(Messages.get().worksheet_overrideUsersValidationException());
                            }
                        }
                        waVDO.setSystemUsers(validUsers);
                        break;
                    case 6:
                        waVDO.setStartedDate((Datetime)val);
                        break;
                    case 7:
                        waVDO.setCompletedDate((Datetime)val);
                        break;
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                editMultipleButton.setEnabled(false);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayOverrideData();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(), parentScreen, new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                evaluateEdit();
                setState(event.getState());
            }
        });

        bus.addHandlerToSource(DataChangeEvent.getType(), parentScreen, new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                displayOverrideData();
            }
        });
    }
    
    public void setData(WorksheetManager1 manager) {
        if (!DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }
    
    private boolean getUpdatePushMode() {
        return ((WorksheetCompletionScreenUI)parentScreen).updatePushMode;
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null)
            canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                             .getStatusId());
    }

    private void displayOverrideData() {
        int i, a, count1, count2, count3, count4;
        boolean dataChanged;
        WorksheetAnalysisViewDO waDO1, waDO2;
        WorksheetItemDO wiDO1, wiDO2;

        if (!isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.item.count();
        count2 = manager == null ? 0 : manager.item.count();

        /*
         * find out if there's any difference between the item data of the two
         * managers
         */
        if (count1 == count2) {
            dataChanged = false;
            if (count1 != 0) {
                items:
                for (i = 0; i < count1; i++ ) {
                    wiDO1 = displayedManager.item.get(i);
                    wiDO2 = manager.item.get(i);
                    count3 = displayedManager.analysis.count(wiDO1);
                    count4 = manager.analysis.count(wiDO2);
                    if (count3 == count4) {
                        for (a = 0; a < count3; a++ ) {
                            waDO1 = displayedManager.analysis.get(wiDO1, a);
                            waDO2 = manager.analysis.get(wiDO2, a);
                            if (DataBaseUtil.isDifferent(waDO1.getSystemUsers(), waDO2.getSystemUsers()) ||
                                DataBaseUtil.isDifferent(waDO1.getStartedDate(), waDO2.getStartedDate()) ||
                                DataBaseUtil.isDifferent(waDO1.getCompletedDate(), waDO2.getCompletedDate())) {
                                dataChanged = true;
                                break items;
                            }
                        }
                    } else {
                        dataChanged = true;
                        break;
                    }
                }
            }
        } else {
            dataChanged = true;
        }

        if (dataChanged) {
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i, j;
        ArrayList<Row> model;
        Row row;
        WorksheetAnalysisViewDO waDO;
        WorksheetItemDO wiDO;

        model = new ArrayList<Row>();
        if (manager != null) {
            try {
                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = (WorksheetItemDO)manager.item.get(i);
    
                    row = new Row(8);
                    row.setCell(0, wiDO.getPosition());
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        if (j > 0) {
                            row = new Row(8);
                            row.setCell(0, wiDO.getPosition());
                        }
                        row.setCell(1, waDO.getAccessionNumber());
                        row.setCell(2, waDO.getDescription());
                        
                        if (waDO.getAnalysisId() != null) {
                            row.setCell(3, waDO.getTestName());
                            row.setCell(4, waDO.getMethodName());
                        } else if (waDO.getQcLotId() != null) {
                            row.setCell(3, "");
                            row.setCell(4, "");
                        }

                        row.setCell(5, waDO.getSystemUsers());
                        row.setCell(6, waDO.getStartedDate());
                        row.setCell(7, waDO.getCompletedDate());
                        row.setData(manager.getUid(waDO)); 
                        model.add(row);
                    }
                    if (j == 0) {
                        row.setCell(2, "ERROR: Item with no analyses");
                        row.setData(manager.getUid(wiDO));
                        model.add(row);
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }        

        return model;
    }
}