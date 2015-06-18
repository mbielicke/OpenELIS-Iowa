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
package org.openelis.modules.worksheet1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.WorksheetReagentViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheetCompletion1.client.WorksheetCompletionScreenUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

public class WorksheetReagentTabUI extends Screen {

    @UiTemplate("WorksheetReagentTab.ui.xml")
    interface WorksheetReagentTabUiBinder extends UiBinder<Widget, WorksheetReagentTabUI> {
    };
    
    private static WorksheetReagentTabUiBinder          uiBinder = GWT.create(WorksheetReagentTabUiBinder.class);

    private boolean                                     canEdit, isVisible, redraw;
    private RowComparator                               rowComp;
    private WorksheetManager1                           manager;
    private WorksheetReagentComparator                  wrComp;

    @UiField
    protected AutoComplete                              description;
    @UiField
    protected Button                                    addRowButton, moveDownButton,
                                                        moveUpButton, removeRowButton;
    @UiField
    protected Table                                     worksheetReagentTable;

    protected EventBus                                  parentBus;
    protected HashMap<Integer, DictionaryDO>            unitOfMeasureMap;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModels;
    protected HashMap<String, ArrayList<QcLotViewDO>>   reagentChoices;
    protected QcLookupScreen                            qcLookupScreen;
    protected Screen                                    parentScreen;
    
    public WorksheetReagentTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        manager = null;
        rowComp = null;
        wrComp = null;
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        int i;
        Column column;
        MenuItem sortAsc, sortDesc;

        addScreenHandler(worksheetReagentTable, "worksheetReagentTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetReagentTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetReagentTable.setEnabled(true);
                worksheetReagentTable.setAllowMultipleSelection(true);
                worksheetReagentTable.unselectAll();
            }
        });

        worksheetReagentTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                if (worksheetReagentTable.getSelectedRow() != -1 && isState(ADD, UPDATE) &&
                    canEdit && !getUpdateTransferMode()) {
                    removeRowButton.setEnabled(true);
                    if (worksheetReagentTable.getSelectedRow() != 0 &&
                        worksheetReagentTable.getSelectedRows().length == 1)
                        moveUpButton.setEnabled(true);
                    else
                        moveUpButton.setEnabled(false);
                    if (worksheetReagentTable.getSelectedRow() != worksheetReagentTable.getRowCount() - 1 &&
                        worksheetReagentTable.getSelectedRows().length == 1)
                        moveDownButton.setEnabled(true);
                    else
                        moveDownButton.setEnabled(false);
                } else {
                    removeRowButton.setEnabled(false);
                    moveUpButton.setEnabled(false);
                    moveDownButton.setEnabled(false);
                }
            }
        });
        
        worksheetReagentTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (!isState(ADD, UPDATE) || !canEdit || getUpdateTransferMode() || event.getCol() != 0 ||
                    (reagentChoices == null || reagentChoices.get(worksheetReagentTable.getRowAt(worksheetReagentTable.getSelectedRow())
                                                                                       .getData()) == null))
                    event.cancel();
            }
        });
        
        worksheetReagentTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                Row row;
                WorksheetReagentViewDO wrVDO;

                r = event.getRow();
                c = event.getCol();
                
                row = worksheetReagentTable.getRowAt(r);
                val = worksheetReagentTable.getValueAt(r,c);

                wrVDO = (WorksheetReagentViewDO)manager.getObject((String)row.getData());
                switch (c) {
                    case 0:
                        if (val != null)
                            wrVDO.setQcLotId(((AutoCompleteValue)val).getId());
                        else
                            wrVDO.setQcLotId(null);
                        reloadRow(r, wrVDO, (AutoCompleteValue)val); 
                        break;
                }
            }
        });

        worksheetReagentTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.reagent.remove(event.getIndex());
                removeRowButton.setEnabled(false);
            }
        });

        //
        // add header menus for column sorting to table
        //
        for (i = 0; i < worksheetReagentTable.getColumnCount(); i++) {
            final int col = i;
            column = worksheetReagentTable.getColumnAt(i);
            sortAsc = new MenuItem(UIResources.INSTANCE.menuCss().Ascending(), org.openelis.ui.messages.Messages.get().header_ascending(), "");
            sortAsc.addCommand(new Command() {
                public void execute() {
                    if (worksheetReagentTable.getRowCount() > 1)
                        sortItems(col, Table.SORT_ASCENDING);
                }
            });
            column.addMenuItem(sortAsc);
            sortDesc = new MenuItem(UIResources.INSTANCE.menuCss().Descending(), org.openelis.ui.messages.Messages.get().header_descending(), "");
            sortDesc.addCommand(new Command() {
                public void execute() {
                    if (worksheetReagentTable.getRowCount() > 1)
                        sortItems(col, Table.SORT_DESCENDING);
                }
            });
            column.addMenuItem(sortDesc);
        }

        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<QcLotViewDO> list;

                model = new ArrayList<Item<Integer>>();
                list = reagentChoices.get(worksheetReagentTable.getRowAt(worksheetReagentTable.getSelectedRow()).getData());
                for (QcLotViewDO qcLot : list)
                    model.add(new Item<Integer>(qcLot.getId(), qcLot.getQcName() + "(" + qcLot.getLotNumber() + ")"));
                description.showAutoMatches(model);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addRowButton.setEnabled(isState(ADD, UPDATE) && canEdit && !getUpdateTransferMode());
                removeRowButton.setEnabled(false);
                moveDownButton.setEnabled(false);
                moveUpButton.setEnabled(false);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayReagentData();
            }
        });
        
        parentBus.addHandler(ReagentsWithChoicesEvent.getType(), new ReagentsWithChoicesEvent.Handler() {
            public void onReagentsWithChoicesLoaded(ReagentsWithChoicesEvent event) {
                int i;
                ArrayList<String> uids;
                ArrayList<QcLotViewDO> choices, tempChoices;
                String lastUid;

                lastUid = null;
                choices = event.getReagentChoices();
                uids = event.getReagentChoiceUids();
                tempChoices = null;
                if (choices != null) {
                    reagentChoices = new HashMap<String, ArrayList<QcLotViewDO>>();
                    for (i = 0; i < choices.size(); i++) {
                        if (!uids.get(i).equals(lastUid)) {
                            tempChoices = new ArrayList<QcLotViewDO>();
                            lastUid = uids.get(i);
                            reagentChoices.put(lastUid, tempChoices);
                        }
                        tempChoices.add(choices.get(i));
                    }
                } else {
                    reagentChoices = null;
                }
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
        int i;
        Row row;
        WorksheetReagentViewDO wrVDO;

        if ((worksheetReagentTable.getRowCount() > 0 && (manager == null || manager.reagent.count() == 0)) ||
            (worksheetReagentTable.getRowCount() == 0 && manager != null && manager.reagent.count() > 0)) {
            redraw = true;
        } else if (manager != null) {
            for (i = 0; i < manager.reagent.count(); i++) {
                wrVDO = manager.reagent.get(i);
                row = worksheetReagentTable.getRowAt(i);
                if (!manager.getUid(wrVDO).equals(row.getData()) || 
                    DataBaseUtil.isDifferent(DataBaseUtil.concatWithSeparator(wrVDO.getQcName(),
                                                                              " (",
                                                                              DataBaseUtil.concat(wrVDO.getLotNumber(),
                                                                                                  ")")),
                                             row.getCell(0)) ||
                    DataBaseUtil.isDifferent(wrVDO.getLocation(), row.getCell(1)) ||
                    DataBaseUtil.isDifferent(wrVDO.getPreparedDate(), row.getCell(2)) ||
                    DataBaseUtil.isDifferent(wrVDO.getUsableDate(), row.getCell(3)) ||
                    DataBaseUtil.isDifferent(wrVDO.getExpireDate(), row.getCell(4)) ||
                    DataBaseUtil.isDifferent(wrVDO.getPreparedVolume(), row.getCell(5)) ||
                    DataBaseUtil.isDifferent(wrVDO.getPreparedUnit(), row.getCell(6)) ||
                    DataBaseUtil.isDifferent(wrVDO.getPreparedByName(), row.getCell(7))) {
                    redraw = true;
                    break;
                }
            }
        }
        
        displayReagentData();
    }

    private void displayReagentData() {
        if (!isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        ArrayList<Row> model;
        Row row;
        ValidationErrorsList multiQcMessages;
        WorksheetReagentViewDO wrVDO;
        
        model = new ArrayList<Row>();
        multiQcMessages = new ValidationErrorsList();
        if (manager != null) {
            try {
                for (i = 0; i < manager.reagent.count(); i++) {
                    wrVDO = (WorksheetReagentViewDO)manager.reagent.get(i);
    
                    row = new Row(8);
                    row.setCell(0, new AutoCompleteValue(wrVDO.getQcLotId(),
                                                         DataBaseUtil.concatWithSeparator(wrVDO.getQcName(),
                                                                                          " (",
                                                                                          DataBaseUtil.concat(wrVDO.getLotNumber(),
                                                                                                              ")"))));
                    row.setCell(1, wrVDO.getLocation());
                    row.setCell(2, wrVDO.getPreparedDate());
                    row.setCell(3, wrVDO.getUsableDate());
                    row.setCell(4, wrVDO.getExpireDate());
                    row.setCell(5, wrVDO.getPreparedVolume());
                    row.setCell(6, wrVDO.getPreparedUnit());
                    row.setCell(7, wrVDO.getPreparedByName());
                    row.setData(manager.getUid(wrVDO)); 
                    model.add(row);

                    if (reagentChoices != null && reagentChoices.get(manager.getUid(wrVDO)) != null)
                        multiQcMessages.add(new FormErrorException(Messages.get()
                                                                           .worksheet_multiMatchingActiveReagent(i + 1)));
                }
                
                if (multiQcMessages.size() > 0)
                    parentScreen.showErrors(multiQcMessages);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }        

        return model;
    }

    @SuppressWarnings("unused")
    @UiHandler("addRowButton")
    protected void openQCLookup(ClickEvent event) {
        ModalWindow win;
        
        try {
            if (qcLookupScreen == null) {
                qcLookupScreen = new QcLookupScreen();
                qcLookupScreen.postConstructor();
                qcLookupScreen.addActionHandler(new org.openelis.gwt.event.ActionHandler<QcLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(org.openelis.gwt.event.ActionEvent<QcLookupScreen.Action> event) {
                        int index;
                        ArrayList<QcLotViewDO> list;
                        Row row;
                        WorksheetReagentViewDO wrVDO;

                        if (event.getAction() == QcLookupScreen.Action.OK) {
                            list = (ArrayList<QcLotViewDO>)event.getData();
                            if (list != null && list.size() > 0) {
                                index = worksheetReagentTable.getRowCount();
                                for (QcLotViewDO qcLotVDO : list) {
                                    wrVDO = manager.reagent.add(index++);
                                    wrVDO.setQcLotId(qcLotVDO.getId());
                                    wrVDO.setLotNumber(qcLotVDO.getLotNumber());
                                    wrVDO.setLocationId(qcLotVDO.getLocationId());
                                    wrVDO.setPreparedDate(qcLotVDO.getPreparedDate());
                                    wrVDO.setPreparedVolume(qcLotVDO.getPreparedVolume());
                                    wrVDO.setPreparedUnitId(qcLotVDO.getPreparedUnitId());
                                    wrVDO.setPreparedById(qcLotVDO.getPreparedById());
                                    wrVDO.setUsableDate(qcLotVDO.getUsableDate());
                                    wrVDO.setExpireDate(qcLotVDO.getExpireDate());
                                    wrVDO.setIsActive(qcLotVDO.getIsActive());
                                    if (qcLotVDO.getLocationId() != null) {
                                        try {
                                            wrVDO.setLocation(DictionaryCache.getById(qcLotVDO.getLocationId()).getEntry());
                                        } catch (Exception anyE) {
                                            Window.alert(anyE.getMessage());
                                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                                        }
                                    }
                                    if (qcLotVDO.getPreparedUnitId() != null) {
                                        try {
                                            wrVDO.setPreparedUnit(DictionaryCache.getById(qcLotVDO.getPreparedUnitId()).getEntry());
                                        } catch (Exception anyE) {
                                            Window.alert(anyE.getMessage());
                                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                                        }
                                    }
                                    if (qcLotVDO.getPreparedById() != null) {
                                        try {
                                            wrVDO.setPreparedByName(UserCache.getSystemUser(qcLotVDO.getPreparedById()).getLoginName());
                                        } catch (Exception anyE) {
                                            Window.alert(anyE.getMessage());
                                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                                        }
                                    }
                                    wrVDO.setQcName(qcLotVDO.getQcName());

                                    row = new Row(8);
                                    row.setCell(0, DataBaseUtil.concatWithSeparator(wrVDO.getQcName(),
                                                                                    " (",
                                                                                    DataBaseUtil.concat(wrVDO.getLotNumber(),
                                                                                                        ")")));
                                    row.setCell(1, wrVDO.getLocation());
                                    row.setCell(2, wrVDO.getPreparedDate());
                                    row.setCell(3, wrVDO.getUsableDate());
                                    row.setCell(4, wrVDO.getExpireDate());
                                    row.setCell(5, wrVDO.getPreparedVolume());
                                    row.setCell(6, wrVDO.getPreparedUnit());
                                    row.setCell(7, wrVDO.getPreparedByName());
                                    row.setData(manager.getUid(wrVDO)); 
                                    worksheetReagentTable.addRow(row);
                                }
                            }
                        }
                    }
                });
                qcLookupScreen.enableMultiSelect(true);
            }

            win = new ModalWindow(false);
            win.setName(Messages.get().qc_qcLookup());
            win.setSize("729px", "360px");
            qcLookupScreen.setWindow(win);
            win.setContent(qcLookupScreen);
        } catch (Exception e) {
            Window.alert("error: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        int i, rowIndex;
        Integer rows[];
        Row dataRow;
        WorksheetReagentViewDO data;

        worksheetReagentTable.finishEditing();
        rows = worksheetReagentTable.getSelectedRows();
        Arrays.sort(rows);
        for (i = rows.length - 1; i >= 0; i--) {
            rowIndex = rows[i];
            dataRow = worksheetReagentTable.getRowAt(rowIndex);
            data = (WorksheetReagentViewDO)manager.getObject((String)dataRow.getData());
            if (reagentChoices != null)
                reagentChoices.remove(manager.getUid(data));
            worksheetReagentTable.removeRowAt(rowIndex);
        }
    }
    
    @SuppressWarnings("unused")
    @UiHandler("moveDownButton")
    protected void moveRowDown(ClickEvent event) {
        int index;
        Row row1, row2;

        worksheetReagentTable.finishEditing();
        index = worksheetReagentTable.getSelectedRow();
        manager.reagent.move(index, false);
        
        row1 = worksheetReagentTable.getRowAt(index);
        row2 = worksheetReagentTable.getRowAt(index + 1);
        worksheetReagentTable.setRowAt(index, row2);
        worksheetReagentTable.setRowAt(index + 1, row1);
        worksheetReagentTable.selectRowAt(++index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        if (index >= worksheetReagentTable.getRowCount() - 1)
            moveDownButton.setEnabled(false);
        if (!moveUpButton.isEnabled() && index > 0)
            moveUpButton.setEnabled(true);
    }
        
    @SuppressWarnings("unused")
    @UiHandler("moveUpButton")
    protected void moveRowUp(ClickEvent event) {
        int index;
        Row row1, row2;

        worksheetReagentTable.finishEditing();
        index = worksheetReagentTable.getSelectedRow();
        manager.reagent.move(index, true);
        
        row1 = worksheetReagentTable.getRowAt(index);
        row2 = worksheetReagentTable.getRowAt(index - 1);
        worksheetReagentTable.setRowAt(index, row2);
        worksheetReagentTable.setRowAt(index - 1, row1);
        worksheetReagentTable.selectRowAt(--index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        if (!moveDownButton.isEnabled() && index < worksheetReagentTable.getRowCount() - 1)
            moveDownButton.setEnabled(true);
        if (index <= 0)
            moveUpButton.setEnabled(false);
    }
        
    private void sortItems(int col, int dir) {
        try {
            if (rowComp == null)
                rowComp = new RowComparator();
            rowComp.setColumn(col);
            rowComp.setDirection(dir);
            if (wrComp == null)
                wrComp = new WorksheetReagentComparator();
            wrComp.setColumn(col);
            wrComp.setDirection(dir);
            manager.reagent.sort(wrComp);
            worksheetReagentTable.applySort(col, dir, rowComp);
        } catch (Exception anyE) {
            Window.alert("error: " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }
    }
    
    private boolean getUpdateTransferMode() {
        if (parentScreen instanceof WorksheetCompletionScreenUI)
            return ((WorksheetCompletionScreenUI)parentScreen).getUpdateTransferMode();
        else
            return false;
    }
    
    private void reloadRow(int rowIndex, WorksheetReagentViewDO wrVDO, AutoCompleteValue val) {
        Row row;
        
        row = worksheetReagentTable.getRowAt(rowIndex);
        if (val != null) {
            for (QcLotViewDO qcLotVDO : reagentChoices.get(manager.getUid(wrVDO))) {
                if (qcLotVDO.getId().equals(val.getId())) {
                    wrVDO.setQcLotId(qcLotVDO.getId());
                    wrVDO.setLotNumber(qcLotVDO.getLotNumber());
                    wrVDO.setLocationId(qcLotVDO.getLocationId());
                    wrVDO.setPreparedDate(qcLotVDO.getPreparedDate());
                    wrVDO.setPreparedVolume(qcLotVDO.getPreparedVolume());
                    wrVDO.setPreparedUnitId(qcLotVDO.getPreparedUnitId());
                    wrVDO.setPreparedById(qcLotVDO.getPreparedById());
                    wrVDO.setUsableDate(qcLotVDO.getUsableDate());
                    wrVDO.setExpireDate(qcLotVDO.getExpireDate());
                    wrVDO.setIsActive(qcLotVDO.getIsActive());
                    if (qcLotVDO.getLocationId() != null) {
                        try {
                            wrVDO.setLocation(DictionaryCache.getById(qcLotVDO.getLocationId()).getEntry());
                        } catch (Exception anyE) {
                            Window.alert(anyE.getMessage());
                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                        }
                    }
                    if (qcLotVDO.getPreparedUnitId() != null) {
                        try {
                            wrVDO.setPreparedUnit(DictionaryCache.getById(qcLotVDO.getPreparedUnitId()).getEntry());
                        } catch (Exception anyE) {
                            Window.alert(anyE.getMessage());
                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                        }
                    }
                    if (qcLotVDO.getPreparedById() != null) {
                        try {
                            wrVDO.setPreparedByName(UserCache.getSystemUser(qcLotVDO.getPreparedById()).getLoginName());
                        } catch (Exception anyE) {
                            Window.alert(anyE.getMessage());
                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                        }
                    }
                    wrVDO.setQcName(qcLotVDO.getQcName());

                    row.setCell(0, DataBaseUtil.concatWithSeparator(wrVDO.getQcName(),
                                                                    " (",
                                                                    DataBaseUtil.concat(wrVDO.getLotNumber(),
                                                                                        ")")));
                    row.setCell(1, wrVDO.getLocation());
                    row.setCell(2, wrVDO.getPreparedDate());
                    row.setCell(3, wrVDO.getUsableDate());
                    row.setCell(4, wrVDO.getExpireDate());
                    row.setCell(5, wrVDO.getPreparedVolume());
                    row.setCell(6, wrVDO.getPreparedUnit());
                    row.setCell(7, wrVDO.getPreparedByName());
                    break;
                }
            }
        } else {
            row.setCell(0, new AutoCompleteValue(null, null));
            row.setCell(1, null);
            row.setCell(2, null);
            row.setCell(3, null);
            row.setCell(4, null);
            row.setCell(5, null);
            row.setCell(6, null);
            row.setCell(7, null);
            
            wrVDO.setQcLotId(null);
            wrVDO.setLotNumber(null);
            wrVDO.setLocationId(null);
            wrVDO.setPreparedDate(null);
            wrVDO.setPreparedVolume(null);
            wrVDO.setPreparedUnitId(null);
            wrVDO.setPreparedById(null);
            wrVDO.setUsableDate(null);
            wrVDO.setExpireDate(null);
            wrVDO.setIsActive(null);
            wrVDO.setLocation(null);
            wrVDO.setPreparedUnit(null);
            wrVDO.setPreparedByName(null);
            wrVDO.setQcName(null);
        }
        
        worksheetReagentTable.setRowAt(rowIndex, row);
    }
    
    //
    // Sort for table
    //
    private class RowComparator implements Comparator<Row> {
        int col, dir;
        
        public RowComparator() {
            col = 0;
            dir = Table.SORT_ASCENDING;
        }
        
        public void setColumn(int col) {
            this.col = col;
        }
        
        public void setDirection(int dir) {
            this.dir = dir;
        }
        
        public int compare(Row row1, Row row2) {
            Comparable c1, c2;
            
            c1 = row1.getCell(col);
            c2 = row2.getCell(col);
            if (c1 == null && c2 == null) {
                return 0;
            } else if (c1 != null && c2 != null) {
                return dir * c1.compareTo(c2);
            } else {
                if (c1 == null && c2 != null)
                    return 1;
                else 
                    return -1;
            }           
        }
    }

    //
    // Sort for manager
    //
    private class WorksheetReagentComparator implements Comparator<WorksheetReagentViewDO> {
        int col, dir;
        
        public WorksheetReagentComparator() {
            col = 0;
            dir = Table.SORT_ASCENDING;
        }
        
        public void setColumn(int col) {
            this.col = col;
        }
        
        public void setDirection(int dir) {
            this.dir = dir;
        }
        
        public int compare(WorksheetReagentViewDO wrVDO1, WorksheetReagentViewDO wrVDO2) {
            Comparable c1, c2;
            
            c1 = null;
            c2 = null;
            switch (col) {
                case 0:         // Name (Lot Number)
                    c1 = DataBaseUtil.concatWithSeparator(wrVDO1.getQcName(), " (", DataBaseUtil.concat(wrVDO1.getLotNumber(), ")"));
                    c2 = DataBaseUtil.concatWithSeparator(wrVDO2.getQcName(), " (", DataBaseUtil.concat(wrVDO2.getLotNumber(), ")"));
                    break;
                    
                case 1:
                    c1 = wrVDO1.getLocation();
                    c2 = wrVDO2.getLocation();
                    break;
                    
                case 2:
                    c1 = wrVDO1.getPreparedDate();
                    c2 = wrVDO2.getPreparedDate();
                    break;
                    
                case 3:
                    c1 = wrVDO1.getUsableDate();
                    c2 = wrVDO2.getUsableDate();
                    break;
                    
                case 4:
                    c1 = wrVDO1.getExpireDate();
                    c2 = wrVDO2.getExpireDate();
                    break;
                    
                case 5:
                    c1 = wrVDO1.getPreparedVolume();
                    c2 = wrVDO2.getPreparedVolume();
                    break;
                    
                case 6:
                    c1 = wrVDO1.getPreparedUnit();
                    c2 = wrVDO2.getPreparedUnit();
                    break;
                    
                case 7:
                    c1 = wrVDO1.getPreparedByName();
                    c2 = wrVDO2.getPreparedByName();
                    break;
            }
            
            if (c1 == null && c2 == null) {
                return 0;
            } else if (c1 != null && c2 != null) {
                return dir * c1.compareTo(c2);
            } else {
                if (c1 == null && c2 != null)
                    return 1;
                else 
                    return -1;
            }           
        }
    }
}