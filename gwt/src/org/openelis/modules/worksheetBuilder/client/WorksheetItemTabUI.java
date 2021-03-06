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
package org.openelis.modules.worksheetBuilder.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.sample1.client.SelectedType;
import org.openelis.modules.sample1.client.SelectionEvent;
import org.openelis.modules.worksheet1.client.ReagentsWithChoicesEvent;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetManagerModifiedEvent;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
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
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.utilcommon.TurnaroundUtil;

public class WorksheetItemTabUI extends Screen {

    @UiTemplate("WorksheetItemTab.ui.xml")
    interface WorksheetItemTabUiBinder extends UiBinder<Widget, WorksheetItemTabUI> {
    };
    
    private static WorksheetItemTabUiBinder             uiBinder = GWT.create(WorksheetItemTabUiBinder.class);

    private boolean                                     canEdit, isVisible, redraw;
    private int                                         addRowDirection;
    private WorksheetItemTabUI                          screen;
    private WorksheetManager1                           manager;

    @UiField
    protected AutoComplete                              description, unitOfMeasureId;
    @UiField
    protected Button                                    addRowButton, duplicateRowButton,
                                                        loadTemplateButton, moveBottomButton,
                                                        moveDownButton, moveTopButton,
                                                        moveUpButton, removeRowButton,
                                                        undoQcsButton;
    @UiField
    protected Dropdown<Integer>                         analysisStatusId, qcLink;
    @UiField
    protected Menu                                      addRowMenu, loadTemplateMenu,
                                                        undoQcsMenu;
    @UiField
    protected MenuItem                                  insertAnalysisAbove, insertAnalysisThisPosition,
                                                        insertAnalysisBelow, insertFromWorksheetAbove,
                                                        insertFromWorksheetBelow, 
                                                        insertFromQcTableAbove,
                                                        insertFromQcTableBelow, 
                                                        undoAll, undoManual, undoTemplate;
    @UiField
    protected Table                                     worksheetItemTable;

    protected ArrayList<Item<Integer>>                  qcLinkModel;
    protected ArrayList<String>                         manualAnalysisUids, templateAnalysisUids;
    protected Confirm                                   worksheetRemoveDuplicateQCConfirm,
                                                        worksheetRemoveQCConfirm, 
                                                        worksheetRemoveLastOfQCConfirm, 
                                                        worksheetSaveConfirm, worksheetExitConfirm;
    protected EventBus                                  parentBus;
    protected HashMap<Integer, Exception>               qcErrors;
    protected HashMap<Integer, DictionaryDO>            unitOfMeasureMap;
    protected HashMap<MenuItem, Integer>                templateMap;
    protected HashMap<String, ArrayList<QcLotViewDO>>   qcChoices;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModels;
    protected QcLookupScreen                            qcLookupScreen;
    protected Screen                                    parentScreen;
    protected TestWorksheetDO                           testWorksheetDO;
    protected TestWorksheetManager                      twManager;
    protected WorksheetAnalysisSelectionScreenUI        waSelectionScreen;
    protected WorksheetBuilderLookupScreenUI            wbLookupScreen;
    protected WorksheetLookupScreenUI                   wLookupScreen;
    
    /**
     * Flags that specifies what type of data is in each row
     */
    public enum QC_SOURCE {
        MANUAL, TEMPLATE;
    };
    
    public WorksheetItemTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        addRowDirection = 1;
        manager = null;
        qcErrors = new HashMap<Integer, Exception>();
        templateMap = new HashMap<MenuItem, Integer>();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        int i;
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;
        Column column;
        Item<Integer> item;
        MenuItem sortAsc, sortDesc;

        screen = this;

        addScreenHandler(worksheetItemTable, "worksheetItemTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTable.setModel(getTableModel());
                enableMoveMenu(false);
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetItemTable.setEnabled(true);
                worksheetItemTable.setAllowMultipleSelection(true);
                worksheetItemTable.unselectAll();
            }
        });
        
        worksheetItemTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                int itemIndex;
                Row row;
                SelectionEvent selEvent;
                WorksheetAnalysisViewDO waVDO;
                WorksheetItemDO wiDO;
                
                if (worksheetItemTable.getSelectedRow() != -1) {
                    row = worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow());
                    if (isState(ADD, UPDATE) && canEdit) {
                        removeRowButton.setEnabled(true);
                        if (worksheetItemTable.getSelectedRows().length == 1)
                            duplicateRowButton.setEnabled(true);
                        else
                            duplicateRowButton.setEnabled(false);
                        
                        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)row.getData());
                        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
                        itemIndex = manager.item.indexOf(wiDO);

                        if (itemIndex != 0 && worksheetItemTable.getSelectedRows().length == 1) {
                            moveUpButton.setEnabled(true);
                            moveTopButton.setEnabled(true);
                        } else {
                            moveUpButton.setEnabled(false);
                            moveTopButton.setEnabled(false);
                        }

                        if (itemIndex != manager.item.count() - 1 &&
                            worksheetItemTable.getSelectedRows().length == 1) {
                            moveDownButton.setEnabled(true);
                            moveBottomButton.setEnabled(true);
                        } else {
                            moveDownButton.setEnabled(false);
                            moveBottomButton.setEnabled(false);
                        }
                    }
                    selEvent = new SelectionEvent(SelectedType.ANALYSIS, (String)row.getData());
                } else {
                    removeRowButton.setEnabled(false);
                    duplicateRowButton.setEnabled(true);
                    enableMoveMenu(false);
                    selEvent = new SelectionEvent(SelectedType.ANALYSIS, null);
                }
                
                parentBus.fireEvent(selEvent);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                SectionPermission perm;
                WorksheetAnalysisViewDO data;
                
                //
                // The only columns that can be edited are Description if there
                // is more than one QC choice for the selected row, QC Link, and
                // Unit of Measure if the row is linked to an editable analysis
                //
                if (!isState(ADD, UPDATE) || !canEdit || (event.getCol() != 2 && event.getCol() != 3 && event.getCol() != 6)) {
                    event.cancel();
                } else if (event.getCol() == 2 &&
                           (qcChoices == null || qcChoices.get(worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow()).getData()) == null)) {
                    event.cancel();
                } else if (event.getCol() == 6) {
                    data = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow())
                                                                                                .getData());
                    perm = UserCache.getPermission().getSection(data.getSectionName());
                    if (data.getQcLotId() != null ||
                        Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) ||
                        Constants.dictionary().ANALYSIS_CANCELLED.equals(data.getStatusId())) {
                        event.cancel();
                    } else {
                        if (perm == null || !perm.hasCompletePermission()) {
                            Window.alert(Messages.get().worksheet_completePermissionRequiredForOperation(data.getSectionName(),
                                                                                                         Messages.get().edit()));
                            event.cancel();
                        } else if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
                            Window.alert(Messages.get().worksheet_builderUpdateWarning());
                            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
                            event.cancel();
                        }
                    }
                }
            }
        });
        
        worksheetItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                Row row;
                WorksheetAnalysisViewDO waVDO;

                r = event.getRow();
                c = event.getCol();
                
                row = worksheetItemTable.getRowAt(r);
                val = worksheetItemTable.getValueAt(r,c);

                waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)row.getData());
                switch (c) {
                    case 2:
                        if (val != null)
                            waVDO.setQcLotId(((AutoCompleteValue)val).getId());
                        else
                            waVDO.setQcLotId(null);
                        break;
                    case 3:
                        waVDO.setWorksheetAnalysisId((Integer)val);
                        break;
                    case 6:
                        if (val != null) {
                            waVDO.setUnitOfMeasureId(((AutoCompleteValue)val).getId());
                            waVDO.setUnitOfMeasure(((AutoCompleteValue)val).getDisplay());
                        } else {
                            waVDO.setUnitOfMeasureId(null);
                            waVDO.setUnitOfMeasure(null);
                        }
                        break;
                }
            }
        });

        //
        // add header menus for column sorting to table
        //
        for (i = 0; i < worksheetItemTable.getColumnCount(); i++) {
            //
            // skip the columns for position (0) and qc link (3)
            //
            if (i == 0 || i == 3)
                continue;

            final int col = i;
            column = worksheetItemTable.getColumnAt(i);
            sortAsc = new MenuItem(UIResources.INSTANCE.menuCss().Ascending(), org.openelis.ui.messages.Messages.get().header_ascending(), "");
            sortAsc.addCommand(new Command() {
                public void execute() {
                    if (worksheetItemTable.getRowCount() > 1) {
                        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
                            Window.alert(Messages.get().worksheet_builderUpdateWarning());
                            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
                        } else {
                            sortItems(col, Table.SORT_ASCENDING);
                        }
                    }
                }
            });
            column.addMenuItem(sortAsc);
            sortDesc = new MenuItem(UIResources.INSTANCE.menuCss().Descending(), org.openelis.ui.messages.Messages.get().header_descending(), "");
            sortDesc.addCommand(new Command() {
                public void execute() {
                    if (worksheetItemTable.getRowCount() > 1) {
                        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
                            Window.alert(Messages.get().worksheet_builderUpdateWarning());
                            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
                        } else {
                            sortItems(col, Table.SORT_DESCENDING);
                        }
                    }
                }
            });
            column.addMenuItem(sortDesc);
        }

        description.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<QcLotViewDO> list;

                model = new ArrayList<Item<Integer>>();
                list = qcChoices.get(worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow()).getData());
                for (QcLotViewDO qcLot : list)
                    model.add(new Item<Integer>(qcLot.getId(), qcLot.getQcName() + "(" + qcLot.getLotNumber() + ")"));
                description.showAutoMatches(model);
            }
        });

        unitOfMeasureId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<IdNameVO> list;
                WorksheetAnalysisViewDO data;

                try {
                    model = new ArrayList<Item<Integer>>();
                    data = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow())
                                                                                                .getData());
                    list = WorksheetService1.get()
                                            .fetchUnitsForWorksheetAutocomplete(data.getAnalysisId(),
                                                                                QueryFieldUtil.parseAutocomplete(event.getMatch()) +
                                                                                                "%");
                    for (IdNameVO unitVO : list)
                        model.add(new Item<Integer>(unitVO.getId(), unitVO.getName()));
                    unitOfMeasureId.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        insertAnalysisAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertAnalysisAbove();
            }
        });

        insertAnalysisThisPosition.addCommand(new Command() {
            @Override
            public void execute() {
                insertAnalysisThisPosition();
            }
        });

        insertAnalysisBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertAnalysisBelow();
            }
        });

        insertFromWorksheetAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromWorksheetAbove();
            }
        });

        insertFromWorksheetBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromWorksheetBelow();
            }
        });

        insertFromQcTableAbove.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromQcTableAbove();
            }
        });

        insertFromQcTableBelow.addCommand(new Command() {
            @Override
            public void execute() {
                insertFromQcTableBelow();
            }
        });

        undoAll.addCommand(new Command() {
            @Override
            public void execute() {
                undoAllQcs();
            }
        });

        undoManual.addCommand(new Command() {
            @Override
            public void execute() {
                undoManualQcs();
            }
        });

        undoTemplate.addCommand(new Command() {
            @Override
            public void execute() {
                undoTemplateQcs();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                int i, j;
                ArrayList<MenuItem> menuItems;
                MenuItem item;
                
                templateMap.clear();
                loadTemplateMenu.clearItems();
                manualAnalysisUids = null;
                templateAnalysisUids = null;
                qcChoices = null;

                removeRowButton.setEnabled(false);
                enableLoadTemplateMenu(false);
                enableUndoQcsMenu(false);
                enableMoveMenu(false);
                duplicateRowButton.setEnabled(false);

                enableAddRowMenu(isState(ADD, UPDATE) && canEdit);
                for (i = 0; i < worksheetItemTable.getColumnCount(); i++) {
                    menuItems = worksheetItemTable.getColumnAt(i).getMenuItems();
                    if (menuItems != null) {
                        for (j = 0; j < menuItems.size(); j++) {
                            item = menuItems.get(j);
                            item.setEnabled(isState(ADD, UPDATE) && canEdit);
                        }
                    }
                }
                
                
                if (wbLookupScreen != null && wbLookupScreen.isAttached())
                    wbLookupScreen.getWindow().close();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayItemData();
            }
        });

        bus.addHandler(RowsAddedEvent.getType(), new RowsAddedEvent.Handler() {
            public void onRowsAdded(RowsAddedEvent event) {
                int index, extra;
                ArrayList<WorksheetAnalysisViewDO> newData;
                ArrayList<Row> list;
                Integer selectedRows[];
                Row selectedRow;
                WorksheetItemDO itemDO, rowItemDO;
                WorksheetAnalysisViewDO data, rowData;
                
                if (!wbLookupScreen.equals(event.getSource()))
                    return;

                list = (ArrayList<Row>)event.getRows();
                if (list != null && list.size() > 0) {
                    if (addRowDirection != 0 && manager.getTotalCapacity() != null &&
                        worksheetItemTable.getRowCount() + list.size() > manager.getTotalCapacity()) {
                        extra = worksheetItemTable.getRowCount() + list.size() - manager.getTotalCapacity();
                        Window.alert(Messages.get().worksheet_capacityExceeded(extra));
                        while (extra > 0) {
                            list.remove(list.size() - 1);
                            extra--;
                        }
                        if (list.size() == 0)
                            return;
                    }

                    selectedRows = worksheetItemTable.getSelectedRows();
                    if (selectedRows.length > 0) {
                        Arrays.sort(selectedRows);
                        if (addRowDirection > 0) {
                            selectedRow = worksheetItemTable.getRowAt(selectedRows[selectedRows.length - 1]);
                            rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                            rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                            index = manager.item.indexOf(rowItemDO) + 1;
                        } else {
                            selectedRow = worksheetItemTable.getRowAt(selectedRows[0]);
                            rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                            rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                            index = manager.item.indexOf(rowItemDO);
                        }
                    } else {
                        index = manager.item.count();
                    }
                
                    newData = new ArrayList<WorksheetAnalysisViewDO>();
                    for (Row row : list) {
                        if (addRowDirection == 0) {
                            if (manager.item.count() == index)
                                itemDO = manager.item.add(index);
                            else
                                itemDO = manager.item.get(index);
                        } else {
                            itemDO = manager.item.add(index++);
                        }
                        data = manager.analysis.add(itemDO);
                        copyViewToDO((AnalysisViewVO)row.getData(), data);
                        newData.add(data);

                        //
                        // add template to list
                        //
                        if (!templateMap.containsValue(data.getTestId())) {
                            final MenuItem item = new MenuItem("AddRowButtonImage", data.getTestName()+", "+data.getMethodName(), null);
                            item.setVisible(true);
                            item.setEnabled(true);
                            item.addHandler(new ClickHandler() {
                                public void onClick(ClickEvent event) {
                                    loadTemplate(templateMap.get(item));
                                }
                            }, ClickEvent.getType());
                            loadTemplateMenu.add(item);
                            templateMap.put(item, data.getTestId());
                            enableLoadTemplateMenu(true);
                        }
                        
                        if (manager.getWorksheet().getFormatId() == null)
                            manager.getWorksheet().setFormatId(((AnalysisViewVO)row.getData()).getWorksheetFormatId());
                    }
                    
                    try {
                        manager = WorksheetService1.get().initializeResults(manager, newData);
                        parentBus.fireEventFromSource(new WorksheetManagerModifiedEvent(manager), screen);
                    } catch (Exception anyE) {
                        Window.alert(anyE.getMessage());
                        logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                    }
                }
            }
        });

        //
        // load empty QC Link dropdown model
        //
        qcLinkModel = new ArrayList<Item<Integer>>();
        qcLinkModel.add(new Item<Integer>(null, ""));
        qcLink.setModel(qcLinkModel);

        //
        // load analysis status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList) {
            item = new Item<Integer>(resultDO.getId(),resultDO.getEntry());
            if (!"analysis_initiated".equals(resultDO.getSystemName()) ||
                !"analysis_oh_hold".equals(resultDO.getSystemName()) ||
                !"analysis_requeue".equals(resultDO.getSystemName()) ||
                !"analysis_completed".equals(resultDO.getSystemName()))
                item.setEnabled(false);
            model.add(item);
        }
        analysisStatusId.setModel(model);
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
        if (!isState(state)) {
            this.state = state;
            bus.fireEventFromSource(new StateChangeEvent(state), this);
        }
    }

    public void onDataChange() {
        int i, a, rowIndex;
        Row row;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;

        if ((worksheetItemTable.getRowCount() > 0 && (manager == null || manager.item.count() == 0)) ||
            (worksheetItemTable.getRowCount() == 0 && manager != null && manager.item.count() > 0)) {
            redraw = true;
        } else if (manager != null) {
            rowIndex = 0;
            items:
            for (i = 0; i < manager.item.count(); i++ ) {
                wiDO = manager.item.get(i);
                if (manager.analysis.count(wiDO) > 0) {
                    for (a = 0; a < manager.analysis.count(wiDO); a++) {
                        waVDO = manager.analysis.get(wiDO, a);
                        row = worksheetItemTable.getRowAt(rowIndex);
                        if (!manager.getUid(waVDO).equals(row.getData()) || 
                            DataBaseUtil.isDifferent(waVDO.getAccessionNumber(), row.getCell(1)) ||
                            DataBaseUtil.isDifferent(waVDO.getDescription(), row.getCell(2)) ||
                            DataBaseUtil.isDifferent(waVDO.getWorksheetAnalysisId(), row.getCell(3)) ||
                            DataBaseUtil.isDifferent(waVDO.getTestName(), row.getCell(4)) ||
                            DataBaseUtil.isDifferent(waVDO.getMethodName(), row.getCell(5)) ||
                            DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(), row.getCell(6)) ||
                            DataBaseUtil.isDifferent(waVDO.getStatusId(), row.getCell(7)) ||
                            DataBaseUtil.isDifferent(waVDO.getCollectionDate(), row.getCell(8)) ||
                            DataBaseUtil.isDifferent(waVDO.getReceivedDate(), row.getCell(9)) ||
                            DataBaseUtil.isDifferent(waVDO.getDueDays(), row.getCell(10)) ||
                            DataBaseUtil.isDifferent(waVDO.getExpireDate(), row.getCell(11))) {
                            redraw = true;
                            break items;
                        }
                        rowIndex++;
                    }
                } else {
                    row = worksheetItemTable.getRowAt(rowIndex);
                    if (!manager.getUid(wiDO).equals(row.getData()) || 
                        DataBaseUtil.isDifferent(wiDO.getPosition(), row.getCell(0))) {
                        redraw = true;
                        break;
                    }
                    rowIndex++;
                }
            }
        }
        
        displayItemData();
    }

    private void displayItemData() {
        if (!isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        boolean hasOther;
        int i, j;
        ArrayList<Row> model;
        Row row;
        ValidationErrorsList multiQcMessages;
        WorksheetAnalysisViewDO waDO;
        WorksheetItemDO wiDO;
        
        qcLinkModel.clear();
        qcLinkModel.add(new Item<Integer>(null, ""));

        hasOther = false;
        model = new ArrayList<Row>();
        multiQcMessages = new ValidationErrorsList();
        if (manager != null) {
            try {
                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = (WorksheetItemDO)manager.item.get(i);
    
                    row = new Row(13);
                    row.setCell(0, wiDO.getPosition());
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        qcLinkModel.add(new Item<Integer>(waDO.getId(), waDO.getAccessionNumber() +
                                                                        " (" + wiDO.getPosition() +
                                                                        ")"));
    
                        if (j > 0) {
                            row = new Row(13);
                            row.setCell(0, wiDO.getPosition());
                        }
                        row.setCell(1, waDO.getAccessionNumber());
                        
                        if (waDO.getAnalysisId() != null) {
                            row.setCell(2, new AutoCompleteValue(null, waDO.getDescription()));
                            row.setCell(3, waDO.getWorksheetAnalysisId());
                            row.setCell(4, waDO.getTestName());
                            row.setCell(5, waDO.getMethodName());
                            
                            if (waDO.getUnitOfMeasureId() != null) {
                                row.setCell(6, new AutoCompleteValue(waDO.getUnitOfMeasureId(),
                                                                     waDO.getUnitOfMeasure()));
                            } else {
                                row.setCell(6, new AutoCompleteValue());
                            }
                            row.setCell(7, waDO.getStatusId());
                            row.setCell(8, waDO.getCollectionDate());
                            row.setCell(9, waDO.getReceivedDate());
                            row.setCell(10, waDO.getDueDays());
                            row.setCell(11, waDO.getExpireDate());
                        } else if (waDO.getQcLotId() != null) {
                            row.setCell(2, new AutoCompleteValue(waDO.getQcLotId(),
                                                                 waDO.getDescription()));
                            row.setCell(3, waDO.getWorksheetAnalysisId());
                            row.setCell(6, new AutoCompleteValue());
                            
                            if (qcChoices != null && qcChoices.get(manager.getUid(waDO)) != null)
                                multiQcMessages.add(new FormErrorException(Messages.get()
                                                                                   .worksheetMultiMatchingActiveQc(wiDO.getPosition()
                                                                                                                       .toString())));
                        }
                        if (templateAnalysisUids != null && templateAnalysisUids.contains(manager.getUid(waDO)))
                            row.setCell(12, QC_SOURCE.TEMPLATE);
                        else if (manualAnalysisUids != null && manualAnalysisUids.contains(manager.getUid(waDO)))
                            row.setCell(12, QC_SOURCE.MANUAL);
                        row.setData(manager.getUid(waDO)); 
                        model.add(row);
                    }
                }
                
                if (multiQcMessages.size() > 0)
                    parentScreen.showErrors(multiQcMessages);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }        

        if (isState(ADD))
            parentBus.fireEventFromSource(new FormatSetEnabledEvent(!hasOther), screen);
        
        /*
         * Reload the model for the QC Link column
         */
        qcLink.setModel(qcLinkModel);

        return model;
    }

    private void insertAnalysisAbove() {
        addRowDirection = -1;
        openLookupWindow();
    }
    
    private void insertAnalysisThisPosition() {
        addRowDirection = 0;
        openLookupWindow();
    }
    
    private void insertAnalysisBelow() {
        addRowDirection = 1;
        openLookupWindow();
    }
    
    private void openLookupWindow() {
        org.openelis.ui.widget.Window win;
        
        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        if (wbLookupScreen == null) {
            try {
                wbLookupScreen = new WorksheetBuilderLookupScreenUI(bus);
            } catch (Exception e) {
                Window.alert("error: " + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }

        win = new org.openelis.ui.widget.Window();
        win.setName(Messages.get().worksheet_worksheetBuilderLookup());
        win.setSize("1053px", "522px");
        wbLookupScreen.setWindow(win);
        win.setContent(wbLookupScreen);
        try {
            wbLookupScreen.initialize();
        } catch (Exception e) {
            Window.alert("error: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        OpenELIS.getBrowser().addWindow(win, "wbLookupScreen");
    }
    
    private void loadTemplate(Integer testId) {
        int i;
        ArrayList<String> uids;
        ArrayList<QcLotViewDO> choices, tempChoices;
        String lastUid;
        StringBuffer removedMessage;
        WorksheetQcChoiceVO wqcVO;
        
        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        try {
            wqcVO = WorksheetService1.get().loadTemplate(manager, testId);
            
            if (wqcVO.getNewAnalyses() != null) {
                if (templateAnalysisUids == null)
                    templateAnalysisUids = new ArrayList<String>();
                for (WorksheetAnalysisViewDO waVDO : wqcVO.getNewAnalyses())
                    templateAnalysisUids.add(manager.getUid(waVDO));
            }
            
            if (wqcVO.getReagentChoices() != null) {
                parentBus.fireEventFromSource(new ReagentsWithChoicesEvent(wqcVO.getReagentChoices(),
                                                                           wqcVO.getReagentChoiceUids()),
                                              screen);
                Window.alert(Messages.get().worksheet_checkReagentTab());
            }
            
            lastUid = null;
            choices = wqcVO.getChoices();
            uids = wqcVO.getChoiceUids();
            tempChoices = null;
            if (choices != null) {
                qcChoices = new HashMap<String, ArrayList<QcLotViewDO>>();
                for (i = 0; i < choices.size(); i++) {
                    if (!uids.get(i).equals(lastUid)) {
                        tempChoices = new ArrayList<QcLotViewDO>();
                        lastUid = uids.get(i);
                        qcChoices.put(lastUid, tempChoices);
                    }
                    tempChoices.add(choices.get(i));
                }
            } else {
                qcChoices = null;
            }
            
            if (wqcVO.getErrors() != null && wqcVO.getErrors().size() > 0)
                parentScreen.showErrors(wqcVO.getErrors());
                
            if (wqcVO.getRemovedAnalyses() != null) {
                removedMessage = new StringBuffer();
                removedMessage.append(Messages.get().worksheetTemplateRemovedAnalyses())
                              .append("\n\n");
                for (WorksheetAnalysisViewDO waVDO : wqcVO.getRemovedAnalyses()) {
                    removedMessage.append("\t").append(waVDO.getAccessionNumber())
                                  .append("\t").append(waVDO.getDescription()).append("\n");
                }
                Window.alert(removedMessage.toString());
            }
            
            parentBus.fireEventFromSource(new WorksheetManagerModifiedEvent(wqcVO.getManager()), screen);
            enableUndoQcsMenu(true);
        } catch (Exception anyE) {
            Window.alert("error: " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }
    }
    
    private void insertFromWorksheetAbove() {
        addRowDirection = -1;
        openWorksheetAnalysisLookup();
    }
    
    private void insertFromWorksheetBelow() {
        addRowDirection = 1;
        openWorksheetAnalysisLookup();
    }
    
    private void openWorksheetAnalysisLookup() {
        ModalWindow modal;
        
        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        if (manager.getWorksheet().getFormatId() == null) {
            Window.alert(Messages.get().worksheet_chooseFormatBeforeAddFromOther());
            return;
        }
        
        try {
            if (wLookupScreen == null) {
                wLookupScreen = new WorksheetLookupScreenUI();
                wLookupScreen.addActionHandler(new ActionHandler<WorksheetLookupScreenUI.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(ActionEvent<WorksheetLookupScreenUI.Action> event) {
                        Item<Integer> row;
                        ModalWindow modal2;
                        WorksheetViewDO wVDO;

                        if (event.getAction() == WorksheetLookupScreenUI.Action.SELECT) {
                            row = (Item<Integer>)event.getData();
                            if (row != null) {
                                wVDO = (WorksheetViewDO)row.getData();
                                try {
                                    if (waSelectionScreen == null) {
                                        waSelectionScreen = new WorksheetAnalysisSelectionScreenUI();
                                        waSelectionScreen.addActionHandler(new ActionHandler<WorksheetAnalysisSelectionScreenUI.Action>() {
                                            public void onAction(ActionEvent<WorksheetAnalysisSelectionScreenUI.Action> event) {
                                                int index, extra;
                                                ArrayList<WorksheetAnalysisViewDO> list, newData;
                                                Integer fromWorksheetId, selectedRows[];
                                                Row selectedRow;
                                                WorksheetItemDO itemDO, rowItemDO;
                                                WorksheetAnalysisViewDO data, rowData;
    
                                                if (event.getAction() == WorksheetAnalysisSelectionScreenUI.Action.SELECT) {
                                                    list = (ArrayList<WorksheetAnalysisViewDO>)event.getData();
                                                    if (list != null && list.size() > 0) {
                                                        if (addRowDirection != 0 && manager.getTotalCapacity() != null &&
                                                            worksheetItemTable.getRowCount() + list.size() > manager.getTotalCapacity()) {
                                                            extra = worksheetItemTable.getRowCount() + list.size() - manager.getTotalCapacity();
                                                            Window.alert(Messages.get().worksheet_capacityExceeded(extra));
                                                            while (extra > 0) {
                                                                list.remove(list.size() - 1);
                                                                extra--;
                                                            }
                                                            if (list.size() == 0)
                                                                return;
                                                        }

                                                        newData = new ArrayList<WorksheetAnalysisViewDO>();
                                                        selectedRows = worksheetItemTable.getSelectedRows();
                                                        if (selectedRows.length > 0) {
                                                            Arrays.sort(selectedRows);
                                                            if (addRowDirection > 0) {
                                                                selectedRow = worksheetItemTable.getRowAt(selectedRows[selectedRows.length - 1]);
                                                                rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                                                                rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                                                                index = manager.item.indexOf(rowItemDO) + 1;
                                                            } else {
                                                                selectedRow = worksheetItemTable.getRowAt(selectedRows[0]);
                                                                rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                                                                rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                                                                index = manager.item.indexOf(rowItemDO);
                                                            }
                                                        } else {
                                                            index = manager.item.count();
                                                        }
                                                        
                                                        if (manualAnalysisUids == null)
                                                            manualAnalysisUids = new ArrayList<String>();

                                                        fromWorksheetId = null;
                                                        for (WorksheetAnalysisViewDO waDO : list) {
                                                            if (fromWorksheetId == null)
                                                                fromWorksheetId = waDO.getWorksheetId();
                                                            
                                                            itemDO = manager.item.add(index++);
                                                            data = manager.analysis.add(itemDO);
                                                            copyDO(waDO, data);
                                                            data.setFromOtherId(waDO.getId());
                                                            newData.add(data);
                                                            manualAnalysisUids.add(manager.getUid(data));
                                                        }
                                                        
                                                        try {
                                                            manager = WorksheetService1.get().initializeResultsFromOther(manager, list, newData, fromWorksheetId);
                                                            parentBus.fireEventFromSource(new WorksheetManagerModifiedEvent(manager), screen);
                                                        } catch (Exception anyE) {
                                                            Window.alert(anyE.getMessage());
                                                            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    
                                    modal2 = new ModalWindow();
                                    modal2.setName(Messages.get().worksheet_worksheetAnalysisSelection() + " (#" + wVDO.getId().toString() + ")");
                                    modal2.setContent(waSelectionScreen);
                                    modal2.setSize("502px", "395px");
                                    waSelectionScreen.setWindow(modal2);
                                    waSelectionScreen.setWorksheetId(wVDO.getId());
                                    waSelectionScreen.initialize();
                                } catch (Exception e) {
                                    Window.alert("error: " + e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                    return;
                                }
                            }
                        }
                    }
                });
            }
            
            modal = new ModalWindow();
            modal.setName(Messages.get().worksheet_worksheetLookup());
            modal.setContent(wLookupScreen);
            modal.setSize("636px", "385px");
            wLookupScreen.setWindow(modal);
        } catch (Exception e) {
            Window.alert("error: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
    }

    private void insertFromQcTableAbove() {
        addRowDirection = -1;
        openQCLookup();
    }
    
    private void insertFromQcTableBelow() {
        addRowDirection = 1;
        openQCLookup();
    }
    
    private void openQCLookup() {
        ModalWindow win;
        
        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        try {
            if (qcLookupScreen == null) {
                qcLookupScreen = new QcLookupScreen();
                qcLookupScreen.postConstructor();
                qcLookupScreen.addActionHandler(new org.openelis.gwt.event.ActionHandler<QcLookupScreen.Action>() {
                    @SuppressWarnings("unchecked")
                    public void onAction(org.openelis.gwt.event.ActionEvent<QcLookupScreen.Action> event) {
                        int index, extra;
                        ArrayList<QcLotViewDO> list;
                        ArrayList<WorksheetAnalysisViewDO> newData;
                        Integer selectedRows[];
                        Row selectedRow;
                        WorksheetItemDO itemDO, rowItemDO;
                        WorksheetAnalysisViewDO data, rowData;

                        if (event.getAction() == QcLookupScreen.Action.OK) {
                            list = (ArrayList<QcLotViewDO>)event.getData();
                            if (list != null && list.size() > 0) {
                                if (addRowDirection != 0 && manager.getTotalCapacity() != null &&
                                    worksheetItemTable.getRowCount() + list.size() > manager.getTotalCapacity()) {
                                    extra = worksheetItemTable.getRowCount() + list.size() - manager.getTotalCapacity();
                                    Window.alert(Messages.get().worksheet_capacityExceeded(extra));
                                    while (extra > 0) {
                                        list.remove(list.size() - 1);
                                        extra--;
                                    }
                                    if (list.size() == 0)
                                        return;
                                }
                                           
                                newData = new ArrayList<WorksheetAnalysisViewDO>();
                                selectedRows = worksheetItemTable.getSelectedRows();
                                if (selectedRows.length > 0) {
                                    Arrays.sort(selectedRows);
                                    if (addRowDirection > 0) {
                                        selectedRow = worksheetItemTable.getRowAt(selectedRows[selectedRows.length - 1]);
                                        rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                                        rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                                        index = manager.item.indexOf(rowItemDO) + 1;
                                    } else {
                                        selectedRow = worksheetItemTable.getRowAt(selectedRows[0]);
                                        rowData = (WorksheetAnalysisViewDO)manager.getObject((String)selectedRow.getData());
                                        rowItemDO = manager.item.getById(rowData.getWorksheetItemId());
                                        index = manager.item.indexOf(rowItemDO);
                                    }
                                } else {
                                    index = manager.item.count();
                                }
                                
                                if (manualAnalysisUids == null)
                                    manualAnalysisUids = new ArrayList<String>();

                                for (QcLotViewDO qcLotVDO : list) {
                                    itemDO = manager.item.add(index++);
                                    data = manager.analysis.add(itemDO);
                                    data.setAccessionNumber("X." + itemDO.getPosition());
                                    data.setQcLotId(qcLotVDO.getId());
                                    data.setDescription(qcLotVDO.getQcName() + " (" +
                                                        qcLotVDO.getLotNumber() + ")");
                                    newData.add(data);
                                    manualAnalysisUids.add(manager.getUid(data));
                                }
                                
                                try {
                                    manager = WorksheetService1.get().initializeResults(manager, newData);
                                    parentBus.fireEventFromSource(new WorksheetManagerModifiedEvent(manager), screen);
                                    enableUndoQcsMenu(true);
                                } catch (Exception anyE) {
                                    Window.alert(anyE.getMessage());
                                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
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
        int i, j, rowIndex;
        Integer rows[];
        Row dataRow, tempRow;
        SectionPermission perm;
        StringBuffer buffer;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO data, tempData;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        buffer = new StringBuffer();
        worksheetItemTable.finishEditing();
        rows = worksheetItemTable.getSelectedRows();
        Arrays.sort(rows);
        ROW:
        for (i = rows.length - 1; i >= 0; i--) {
            rowIndex = rows[i];
            dataRow = worksheetItemTable.getRowAt(rowIndex);
            data = (WorksheetAnalysisViewDO)manager.getObject((String)dataRow.getData());
            
            //
            // If this is an analysis record, check to see if the user has permission
            // to modify the record
            //
            if (data.getAnalysisId() != null) {
                perm = UserCache.getPermission().getSection(data.getSectionName());
                if (perm == null || !perm.hasCompletePermission()) {
                    if (buffer.length() > 0)
                        buffer.insert(0, "\n");
                    buffer.insert(0, "Row " + (i + 1) + ": " + Messages.get().worksheet_completePermissionRequiredForOperation(data.getSectionName(),
                                                                                                                               Messages.get().gen_remove()));
                    continue;
                }
            }
            
            //
            // Check if other rows are linked to this one via QC Link
            //
            for (j = 0; j < worksheetItemTable.getRowCount(); j++) {
                tempRow = worksheetItemTable.getRowAt(j);
                tempData = (WorksheetAnalysisViewDO)manager.getObject((String)tempRow.getData());
                if (tempRow != dataRow && tempData.getWorksheetAnalysisId() != null) {
                    if (data.getId().equals(tempData.getWorksheetAnalysisId())) {
                        if (buffer.length() > 0)
                            buffer.insert(0, "\n");
                        buffer.insert(0, "Row " + (i + 1) + ": " + Messages.get().worksheet_oneOrMoreQcLinkOnRemove());
                        continue ROW;
                    }
                }
            }
            
            if (manualAnalysisUids != null)
                manualAnalysisUids.remove(manager.getUid(data));
            if (templateAnalysisUids != null)
                templateAnalysisUids.remove(manager.getUid(data));
            if (qcChoices != null)
                qcChoices.remove(manager.getUid(data));
            
            worksheetItemTable.removeRowAt(rowIndex);
            wiDO = manager.item.getById(data.getWorksheetItemId());
            manager.analysis.remove(wiDO, data);
            if (manager.analysis.count(wiDO) == 0)
                manager.item.remove(wiDO);
        }
        
        if (buffer.length() > 0)
            Window.alert(buffer.toString());
        
        fireDataChange();
    }
    
    private void undoAllQcs() {
        ArrayList<String> analysisUids;
        ArrayList<WorksheetAnalysisViewDO> analyses;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> qcLinks;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;
        
        analysisUids = new ArrayList<String>();
        if (manualAnalysisUids != null)
            analysisUids.addAll(manualAnalysisUids);
        if (templateAnalysisUids != null)
            analysisUids.addAll(templateAnalysisUids);
        
        if (analysisUids.size() > 0) {
            qcLinks = getQcLinkList();
            for (String uid : analysisUids) {
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(uid);

                //
                // Remove QC Links from rows that are referencing this analysis
                //
                analyses = qcLinks.get(waVDO.getId());
                if (analyses != null) {
                    for (WorksheetAnalysisViewDO waVDO1 : analyses)
                        waVDO1.setWorksheetAnalysisId(null);
                }
                
                //
                // Remove QC Choices for this analysis if needed
                //
                if (qcChoices != null)
                    qcChoices.remove(manager.getUid(waVDO));
                
                wiDO = manager.item.getById(waVDO.getWorksheetItemId());
                manager.analysis.remove(wiDO, waVDO);
                if (manager.analysis.count(wiDO) == 0)
                    manager.item.remove(wiDO);
            }
        }
        manualAnalysisUids = null;
        templateAnalysisUids = null;
        fireDataChange();
    }

    private void undoManualQcs() {
        ArrayList<WorksheetAnalysisViewDO> analyses;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> qcLinks;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;
        
        if (manualAnalysisUids != null) {
            qcLinks = getQcLinkList();
            for (String uid : manualAnalysisUids) {
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(uid);

                //
                // Remove QC Links from rows that are referencing this analysis
                //
                analyses = qcLinks.get(waVDO.getId());
                if (analyses != null) {
                    for (WorksheetAnalysisViewDO waVDO1 : analyses)
                        waVDO1.setWorksheetAnalysisId(null);
                }

                //
                // Remove QC Choices for this analysis if needed
                //
                if (qcChoices != null)
                    qcChoices.remove(manager.getUid(waVDO));
                
                wiDO = manager.item.getById(waVDO.getWorksheetItemId());
                manager.analysis.remove(wiDO, waVDO);
                if (manager.analysis.count(wiDO) == 0)
                    manager.item.remove(wiDO);
            }
        }
        manualAnalysisUids = null;
        fireDataChange();
    }

    private void undoTemplateQcs() {
        ArrayList<WorksheetAnalysisViewDO> analyses;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> qcLinks;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;
        
        if (templateAnalysisUids != null) {
            qcLinks = getQcLinkList();
            for (String uid : templateAnalysisUids) {
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(uid);

                //
                // Remove QC Links from rows that are referencing this analysis
                //
                analyses = qcLinks.get(waVDO.getId());
                if (analyses != null) {
                    for (WorksheetAnalysisViewDO waVDO1 : analyses)
                        waVDO1.setWorksheetAnalysisId(null);
                }

                //
                // Remove QC Choices for this analysis if needed
                //
                if (qcChoices != null)
                    qcChoices.remove(manager.getUid(waVDO));
                
                wiDO = manager.item.getById(waVDO.getWorksheetItemId());
                manager.analysis.remove(wiDO, waVDO);
                if (manager.analysis.count(wiDO) == 0)
                    manager.item.remove(wiDO);
            }
        }
        templateAnalysisUids = null;
        fireDataChange();
    }

    @SuppressWarnings("unused")
    @UiHandler("duplicateRowButton")
    protected void duplicateRow(ClickEvent event) {
        int index, itemIndex;
        Row dataRow;
        SectionPermission perm;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        worksheetItemTable.finishEditing();
        index = worksheetItemTable.getSelectedRow();
        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        
        //
        // If this is an analysis record, check to see if the user has permission
        // to modify the record
        //
        if (waVDO.getAnalysisId() != null) {
            perm = UserCache.getPermission().getSection(waVDO.getSectionName());
            if (perm == null || !perm.hasCompletePermission()) {
                Window.alert(Messages.get().worksheet_completePermissionRequiredForOperation(waVDO.getSectionName(),
                                                                                             Messages.get().duplicateRecord()));
                return;
            }
            if (Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(waVDO.getStatusId()) ||
                Constants.dictionary().ANALYSIS_INPREP.equals(waVDO.getStatusId()) ||
                Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
                Window.alert(Messages.get().worksheet_wrongStatusNoDuplicate());
                return;
            }
        }

        if (manager.getTotalCapacity() != null &&
            worksheetItemTable.getRowCount() + 1 > manager.getTotalCapacity()) {
            Window.alert(Messages.get().worksheet_atCapacity());
            return;
        }
        
        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
        itemIndex = manager.item.indexOf(wiDO);
        manager.item.duplicate(itemIndex);
        worksheetItemTable.setModel(getTableModel());
        while (waVDO.getWorksheetItemId().equals(wiDO.getId())) {
            index++;
            waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        }
        worksheetItemTable.selectRowAt(index);
    }
        
    @SuppressWarnings("unused")
    @UiHandler("moveDownButton")
    protected void moveRowDown(ClickEvent event) {
        int index, itemIndex;
        String uid;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        worksheetItemTable.finishEditing();
        index = worksheetItemTable.getSelectedRow();
        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
        itemIndex = manager.item.indexOf(wiDO);
        manager.item.move(itemIndex, false);
        worksheetItemTable.setModel(getTableModel());
        uid = manager.getUid(waVDO);
        while (!uid.equals((String)worksheetItemTable.getRowAt(index).getData()))
            index++;
        worksheetItemTable.selectRowAt(index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        itemIndex++;
        if (itemIndex >= manager.item.count() - 1) {
            moveDownButton.setEnabled(false);
            moveBottomButton.setEnabled(false);
        }
        if (!moveUpButton.isEnabled() && itemIndex > 0) {
            moveUpButton.setEnabled(true);
            moveTopButton.setEnabled(true);
        }
    }
        
    @SuppressWarnings("unused")
    @UiHandler("moveUpButton")
    protected void moveRowUp(ClickEvent event) {
        int index, itemIndex;
        String uid;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        worksheetItemTable.finishEditing();
        index = worksheetItemTable.getSelectedRow();
        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
        itemIndex = manager.item.indexOf(wiDO);
        manager.item.move(itemIndex, true);
        worksheetItemTable.setModel(getTableModel());
        uid = manager.getUid(waVDO);
        while (!uid.equals((String)worksheetItemTable.getRowAt(index).getData()))
            index--;
        worksheetItemTable.selectRowAt(index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        itemIndex--;
        if (!moveDownButton.isEnabled() && itemIndex < manager.item.count() - 1) {
            moveDownButton.setEnabled(true);
            moveBottomButton.setEnabled(true);
        }
        if (itemIndex <= 0) {
            moveUpButton.setEnabled(false);
            moveTopButton.setEnabled(false);
        }
    }
        
    @SuppressWarnings("unused")
    @UiHandler("moveBottomButton")
    protected void moveRowBottom(ClickEvent event) {
        int index, itemIndex;
        String uid;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        worksheetItemTable.finishEditing();
        index = worksheetItemTable.getSelectedRow();
        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
        itemIndex = manager.item.indexOf(wiDO);
        while (itemIndex < manager.item.count() - 1) {
            manager.item.move(itemIndex, false);
            itemIndex++;
        }
        worksheetItemTable.setModel(getTableModel());
        index = worksheetItemTable.getRowCount() - 1;
        uid = manager.getUid(waVDO);
        while (!uid.equals((String)worksheetItemTable.getRowAt(index).getData()))
            index--;
        worksheetItemTable.selectRowAt(index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        moveDownButton.setEnabled(false);
        moveBottomButton.setEnabled(false);
        if (!moveUpButton.isEnabled()) {
            moveUpButton.setEnabled(true);
            moveTopButton.setEnabled(true);
        }
    }
        
    @SuppressWarnings("unused")
    @UiHandler("moveTopButton")
    protected void moveRowTop(ClickEvent event) {
        int index, itemIndex;
        String uid;
        WorksheetItemDO wiDO;
        WorksheetAnalysisViewDO waVDO;

        if (isState(UPDATE) && !((WorksheetBuilderScreenUI)parentScreen).updateWarningShown) {
            Window.alert(Messages.get().worksheet_builderUpdateWarning());
            ((WorksheetBuilderScreenUI)parentScreen).updateWarningShown = true;
            return;
        }

        worksheetItemTable.finishEditing();
        index = worksheetItemTable.getSelectedRow();
        waVDO = (WorksheetAnalysisViewDO)manager.getObject((String)worksheetItemTable.getRowAt(index).getData());
        wiDO = manager.item.getById(waVDO.getWorksheetItemId());
        itemIndex = manager.item.indexOf(wiDO);
        while (itemIndex > 0) {
            manager.item.move(itemIndex, true);
            itemIndex--;
        }
        worksheetItemTable.setModel(getTableModel());
        index = 0;
        uid = manager.getUid(waVDO);
        while (!uid.equals((String)worksheetItemTable.getRowAt(index).getData()))
            index++;
        worksheetItemTable.selectRowAt(index);
        //
        // Programmatically selecting the row doesn't fire selection events, so
        // we need to enable/disable the move buttons accordingly
        //
        if (!moveDownButton.isEnabled()) {
            moveDownButton.setEnabled(true);
            moveBottomButton.setEnabled(true);
        }
        moveUpButton.setEnabled(false);
        moveTopButton.setEnabled(false);
    }
        
    private void enableAddRowMenu(boolean enable) {
        addRowMenu.setEnabled(enable);
        addRowButton.setEnabled(enable);
        insertAnalysisAbove.setEnabled(enable);
        insertAnalysisThisPosition.setEnabled(enable);
        insertAnalysisBelow.setEnabled(enable);
        insertFromWorksheetAbove.setEnabled(enable);
        insertFromWorksheetBelow.setEnabled(enable);
        insertFromQcTableAbove.setEnabled(enable);
        insertFromQcTableBelow.setEnabled(enable);
    }
    
    private void enableLoadTemplateMenu(boolean enable) {
        Iterator<MenuItem> menuItems;
        MenuItem menuItem;
        
        loadTemplateMenu.setEnabled(enable);
        loadTemplateButton.setEnabled(enable);
        
        menuItems = templateMap.keySet().iterator();
        while (menuItems.hasNext()) {
            menuItem = menuItems.next();
            menuItem.setEnabled(enable);
        }
    }
    
    private void enableUndoQcsMenu(boolean enable) {
        undoQcsMenu.setEnabled(enable);
        undoQcsButton.setEnabled(enable);
        undoAll.setEnabled(enable);
        undoManual.setEnabled(enable);
        undoTemplate.setEnabled(enable);
    }
    
    private void enableMoveMenu(boolean enable) {
        moveTopButton.setEnabled(enable);
        moveUpButton.setEnabled(enable);
        moveDownButton.setEnabled(enable);
        moveBottomButton.setEnabled(enable);
    }
    
    private void copyDO(WorksheetAnalysisViewDO fromDO, WorksheetAnalysisViewDO toDO) {
        toDO.setAccessionNumber(fromDO.getAccessionNumber().toString());
        toDO.setAnalysisId(fromDO.getAnalysisId());
        toDO.setQcLotId(fromDO.getQcLotId());
        toDO.setQcId(fromDO.getQcId());
        toDO.setSystemUsers(fromDO.getSystemUsers());
        toDO.setStartedDate(fromDO.getStartedDate());
        toDO.setCompletedDate(fromDO.getCompletedDate());
        toDO.setDescription(fromDO.getDescription());
        toDO.setTestId(fromDO.getTestId());
        toDO.setTestName(fromDO.getTestName());
        toDO.setMethodName(fromDO.getMethodName());
        toDO.setSectionName(fromDO.getSectionName());
        toDO.setUnitOfMeasureId(fromDO.getUnitOfMeasureId());
        toDO.setUnitOfMeasure(fromDO.getUnitOfMeasure());
        toDO.setStatusId(fromDO.getStatusId());
        toDO.setTypeId(fromDO.getTypeId());
        toDO.setCollectionDate(fromDO.getCollectionDate());
        toDO.setReceivedDate(fromDO.getReceivedDate());
        toDO.setDueDays(fromDO.getDueDays());
        toDO.setExpireDate(fromDO.getExpireDate());
    }
    
    private void copyViewToDO(AnalysisViewVO avVO, WorksheetAnalysisViewDO waVDO) {
        DictionaryDO unitDO;
        
        waVDO.setAccessionNumber(avVO.getAccessionNumber().toString());
        waVDO.setAnalysisId(avVO.getAnalysisId());
        waVDO.setDescription(avVO.getWorksheetDescription());
        waVDO.setTestId(avVO.getTestId());
        waVDO.setTestName(avVO.getTestName());
        waVDO.setMethodName(avVO.getMethodName());
        waVDO.setSectionName(avVO.getSectionName());
        waVDO.setUnitOfMeasureId(avVO.getUnitOfMeasureId());
        if (waVDO.getUnitOfMeasureId() != null) {
            try {                                                               // unit
                unitDO = DictionaryCache.getById(waVDO.getUnitOfMeasureId());
                waVDO.setUnitOfMeasure(unitDO.getEntry());
            } catch (Exception anyE) {
                Window.alert("error: " + anyE.getMessage());
                logger.log(Level.SEVERE, anyE.getMessage(), anyE);
            }
        }
        waVDO.setStatusId(avVO.getAnalysisStatusId());
        waVDO.setCollectionDate(DataBaseUtil.toYD(avVO.getCollectionDate()));
        waVDO.setReceivedDate(DataBaseUtil.toYM(avVO.getReceivedDate()));

        //
        // Compute and set the number of days until the analysis is 
        // due to be completed based on when the sample was received,
        // what the tests average turnaround time is, and whether the
        // client requested a priority number of days.
        //
        if (avVO.getPriority() != null)
            waVDO.setDueDays(TurnaroundUtil.getDueDays(avVO.getReceivedDate(), avVO.getPriority()));
        else
            waVDO.setDueDays(TurnaroundUtil.getDueDays(avVO.getReceivedDate(), avVO.getTimeTaAverage()));
        
        //
        // Compute and set the expiration date on the analysis based
        // on the collection date and the tests definition of holding
        // hours.
        //
        waVDO.setExpireDate(TurnaroundUtil.getExpireDate(avVO.getCollectionDate(),
                                                       avVO.getCollectionTime(),
                                                       avVO.getTimeHolding()));
    }
    
    private HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> getQcLinkList() {
        ArrayList<WorksheetAnalysisViewDO> analyses;
        HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>> qcLinks;
        WorksheetAnalysisViewDO data;
        
        qcLinks = new HashMap<Integer, ArrayList<WorksheetAnalysisViewDO>>();
        for (Row row : worksheetItemTable.getModel()) {
            data = (WorksheetAnalysisViewDO)manager.getObject((String)row.getData());
            if (data.getWorksheetAnalysisId() != null) {
                analyses = qcLinks.get(data.getWorksheetAnalysisId());
                if (analyses == null) {
                    analyses = new ArrayList<WorksheetAnalysisViewDO>();
                    qcLinks.put(data.getWorksheetAnalysisId(), analyses);
                }
                analyses.add(data);
            }
        }
        
        return qcLinks;
    }
    
    private void sortItems(int col, int dir) {
        try {
            manager = WorksheetService1.get().sortItems(manager, col, dir);
            parentBus.fireEventFromSource(new WorksheetManagerModifiedEvent(manager), screen);
        } catch (Exception anyE) {
            Window.alert("error: " + anyE.getMessage());
            logger.log(Level.SEVERE, anyE.getMessage(), anyE);
        }
    }
}