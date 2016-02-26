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
package org.openelis.modules.worksheetCompletion1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
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

    private boolean                                     canEdit, isVisible, redraw;
    private WorksheetManager1                           manager;

    @UiField
    protected Button                                    editMultipleButton, selectAllButton,
                                                        unselectAllButton;
    @UiField
    protected Table                                     overridesTable;
    
    protected EventBus                                  parentBus;
    protected OverridesEditMultiplePopupUI              editMultiplePopup;
    protected Screen                                    parentScreen;
    
    public OverridesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        manager = null;
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
                if (overridesTable.getSelectedRows().length > 1 && isState(UPDATE) && canEdit)
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

                if (!isState(UPDATE) || !canEdit || getUpdateTransferMode() || event.getCol() < 5) {
                    event.cancel();
                } else {
                    data = manager.getObject((String)overridesTable.getRowAt(event.getRow())
                                                                   .getData());
                    if (data instanceof WorksheetAnalysisViewDO) {
                        waVDO = (WorksheetAnalysisViewDO)data;
                        if (waVDO.getQcLotId() != null && event.getCol() == 7) {
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
                                        userNames.remove(userVO.getLoginName());
                                    }
                                }
                                if (userNames.size() > 0)
                                    Window.alert(Messages.get().worksheet_invalidUsersException(userNames.toString()));
                            } catch (Exception anyE) {
                                Window.alert(Messages.get().worksheet_overrideUsersValidationException());
                            }
                        }
                        waVDO.setSystemUsers(validUsers);
                        overridesTable.setValueAt(r, c, validUsers);
                        break;
                    case 6:
                        if (val instanceof Datetime)
                            waVDO.setStartedDate((Datetime)val);
                        else
                            waVDO.setStartedDate(null); 
                        break;
                    case 7:
                        if (val instanceof Datetime)
                            waVDO.setCompletedDate((Datetime)val);
                        else
                            waVDO.setCompletedDate(null);
                        break;
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                editMultipleButton.setEnabled(false);
                if (isState(UPDATE) && canEdit && !getUpdateTransferMode()) {
                    selectAllButton.setEnabled(true);
                    unselectAllButton.setEnabled(true);
                } else {
                    selectAllButton.setEnabled(false);
                    unselectAllButton.setEnabled(false);
                }
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayOverrideData();
            }
        });
    }
    
    public void setData(WorksheetManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
            if (manager != null)
                canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                                 .getStatusId());
            else
                canEdit = false;
        }
    }
    
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int i, a, rowIndex;
        Row row;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;

        if ((overridesTable.getRowCount() > 0 && (manager == null || manager.item.count() == 0)) ||
            (overridesTable.getRowCount() == 0 && manager != null && manager.item.count() > 0)) {
            redraw = true;
        } else if (manager != null) {
            rowIndex = 0;
            items:
            for (i = 0; i < manager.item.count(); i++) {
                wiDO = manager.item.get(i);
                if (manager.analysis.count(wiDO) > 0) {
                    for (a = 0; a < manager.analysis.count(wiDO); a++) {
                        waVDO = manager.analysis.get(wiDO, a);
                        row = overridesTable.getRowAt(rowIndex);
                        if (!manager.getUid(waVDO).equals(row.getData()) || 
                            DataBaseUtil.isDifferent(waVDO.getAccessionNumber(), row.getCell(1)) ||
                            DataBaseUtil.isDifferent(waVDO.getDescription(), row.getCell(2)) ||
                            DataBaseUtil.isDifferent(waVDO.getTestName(), row.getCell(3)) ||
                            DataBaseUtil.isDifferent(waVDO.getMethodName(), row.getCell(4)) ||
                            DataBaseUtil.isDifferent(waVDO.getSystemUsers(), row.getCell(5)) ||
                            DataBaseUtil.isDifferent(waVDO.getStartedDate(), row.getCell(6)) ||
                            DataBaseUtil.isDifferent(waVDO.getCompletedDate(), row.getCell(7))) {
                            redraw = true;
                            break items;
                        }
                        rowIndex++;
                    }
                } else {
                    row = overridesTable.getRowAt(rowIndex);
                    if (!manager.getUid(wiDO).equals(row.getData()) || 
                        DataBaseUtil.isDifferent(wiDO.getPosition(), row.getCell(0))) {
                        redraw = true;
                        break;
                    }
                    rowIndex++;
                }
            }
        }
        
        displayOverrideData();
    }

    @SuppressWarnings("unused")
    @UiHandler("selectAllButton")
    public void selectAll(ClickEvent event) {
        overridesTable.selectAll();
        if (overridesTable.getSelectedRows().length > 0)
            editMultipleButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    @UiHandler("unselectAllButton")
    public void unselectAll(ClickEvent event) {
        overridesTable.unselectAll();
        editMultipleButton.setEnabled(false);
    }

    private void displayOverrideData() {
        if (!isVisible)
            return;

        if (redraw) {
            redraw = false;
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
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }        

        return model;
    }

    private boolean getUpdateTransferMode() {
        return ((WorksheetCompletionScreenUI)parentScreen).getUpdateTransferMode();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("editMultipleButton")
    protected void editMultiple(ClickEvent event) {
        ArrayList<WorksheetAnalysisViewDO> dataList;
        DataObject data;
        ModalWindow modal;
        StringBuilder errorList;
        WorksheetAnalysisViewDO waVDO;
        
        dataList = new ArrayList<WorksheetAnalysisViewDO>();
        errorList = new StringBuilder();
        for (Integer rowIndex : overridesTable.getSelectedRows()) {
            data = (DataObject) manager.getObject((String)overridesTable.getRowAt(rowIndex).getData());
            if (data instanceof WorksheetItemDO)
                continue;
            waVDO = (WorksheetAnalysisViewDO) data;
            if (Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()) ||
                Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId())) {
                errorList.append("\n").append("\t").append("Row: ").append(rowIndex + 1);
                continue;
            }
            dataList.add(waVDO);
        }
        
        if (errorList.length() > 0) {
            Window.alert(Messages.get().worksheet_wrongStatusNoEditRows(errorList.toString()));
            return;
        }

        if (editMultiplePopup == null) {
            editMultiplePopup = new OverridesEditMultiplePopupUI() {
                @Override
                public void ok() {
                    onDataChange();
                }
            };
        }
        
        modal = new ModalWindow();
        modal.setName(Messages.get().worksheet_editMultiple());
        modal.setContent(editMultiplePopup);
        modal.setSize("240px", "200px");
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        editMultiplePopup.setWindow(modal);
        editMultiplePopup.initialize();
        editMultiplePopup.setData(dataList);
    }
}