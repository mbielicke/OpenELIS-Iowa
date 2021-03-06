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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScrollableTabBar;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.BeforeRowAddedEvent;
import org.openelis.gwt.widget.table.event.BeforeRowAddedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.TestMeta;
import org.openelis.modules.analyte1.client.AnalyteService1Impl;
import org.openelis.modules.dictionary1.client.DictionaryLookupScreenUI;
import org.openelis.modules.dictionary1.client.DictionaryService1Impl;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.GridFieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.WindowInt;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalyteAndResultTab extends Screen
                                               implements
                                               GetMatchesHandler,
                                               BeforeGetMatchesHandler,
                                               HasActionHandlers<AnalyteAndResultTab.Action>,
                                               ActionHandler<SampleTypeTab.Action> {
    public enum Action {
        ANALYTE_CHANGED, ANALYTE_DELETED, RESULT_CHANGED, RESULT_DELETED
    };

    private TestManager                                  manager;

    private AnalyteAndResultTab                          screen;
    private TestAnalyteDisplayManager<TestAnalyteViewDO> displayManager;

    private DictionaryLookupScreenUI                       dictLookup;

    private TableWidget                                  analyteTable, resultTable;
    private Dropdown<Integer>                            typeId, scriptletId;
    private Dropdown<String>                             tableActions;
    private ScrollableTabBar                             resultTabPanel;
    private CheckBox                                     isReportable;
    private AppButton                                    addButton, removeButton,
                    addResultTabButton, dictionaryLookUpButton, addTestResultButton,
                    removeTestResultButton;

    private ArrayList<GridFieldErrorException>           resultErrorList;

    private boolean                                      addAnalyteRow, loaded,
                    headerAddedInTheMiddle, canAddRemoveColumn;

    private int                                          anaSelCol, tempId;

    private ResultRangeNumeric                           rangeNumeric;
    private ResultRangeTiter                             rangeTiter;

    public AnalyteAndResultTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                analyteTable.load(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteTable.enable(true);
            }
        });

        rangeNumeric = new ResultRangeNumeric();
        rangeTiter = new ResultRangeTiter();

        analyteTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                ArrayList<TableDataRow> selections;
                TableDataRow row, selRow;

                selections = analyteTable.getSelections();

                if (state != State.ADD && state != State.UPDATE)
                    return;

                //
                // since this table supports multiple selection, we want to make
                // sure that if the first row selected is an analyte row then
                // all the subsequently selected rows are analyte rows and if
                // the
                // first row selected is a header row then all the
                // subsequently selected rows are header rows, so through this
                // code we prevent users from selecting the other kind of row
                //
                selRow = event.getItem().row;

                if (selections.size() > 0) {
                    row = selections.get(selections.size() - 1);
                    if ( ! (row.data.equals(selRow.data))) {
                        Window.alert(Messages.get().headerCantSelWithAnalytes());
                        event.cancel();
                    }
                    addButton.enable(false);
                    removeButton.enable(false);
                } else {
                    addButton.enable(true);
                    removeButton.enable(true);
                }
            }
        });

        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Integer key;
                TableDataRow row, val, prevVal;
                AutoComplete<Integer> auto;
                boolean cancel;

                r = event.getRow();
                c = event.getCol();
                anaSelCol = c;

                if (state != State.ADD && state != State.UPDATE) {
                    enableAnalyteWidgets(false);
                    event.cancel();
                } else {
                    enableAnalyteWidgets(true);
                }

                cancel = false;
                auto = (AutoComplete<Integer>)analyteTable.getColumns()
                                                          .get(c)
                                                          .getColumnWidget();

                try {
                    row = analyteTable.getRow(r);
                    if ((Boolean)row.data) {
                        //
                        // the first two columns of a header row are immutable
                        // and we also don't want to allow a user to be able to
                        // edit a header row column that's beyond the number of
                        // columns for the row group that the analyte rows under
                        // the header belong to; in addition to cancelling the
                        // edit event, we disable the three widgets, disallow
                        // adding or removing columns and set anaSelCol to -1
                        // such that the three widgets don't show any data when
                        // refreshAnalyteWidgets() is called
                        //
                        if (c < 2 || c > (displayManager.columnCount(r) + 1)) {
                            event.cancel();
                            cancel = true;
                            enableAnalyteWidgets(false);
                            canAddRemoveColumn = false;
                            anaSelCol = -1;
                        } else if (c != 2) {
                            val = (TableDataRow)row.cells.get(c - 1).getValue();
                            //
                            // we don't want to allow editing of a header cell
                            // if the
                            // cell to its left doesn't have any data in it
                            //
                            if (val == null || val.key == null) {
                                event.cancel();
                                cancel = true;
                                enableAnalyteWidgets(false);
                                anaSelCol = -1;
                            }

                            //
                            // since we cannot allow adding or removing of
                            // columns
                            // if col exceeds the number of columns that a given
                            // row group has for itself in the manager, we set
                            // canAddRemoveColumn to false if this is the case
                            // and to true otherwise
                            //
                            if (c <= displayManager.columnCount(r))
                                canAddRemoveColumn = true;
                            else
                                canAddRemoveColumn = false;
                        } else if (c == 2) {
                            //
                            // we always allow adding or removing of columns at
                            // the third column of a header row
                            //
                            canAddRemoveColumn = true;
                        }

                        //
                        // send DataChangeEvent to the three widgets to make
                        // them either show the data that corresponds to this
                        // cell or make them go blank
                        //
                        refreshAnalyteWidgets();
                    } else {
                        if (c > displayManager.columnCount(r)) {
                            //
                            // for a header row, we allow editing of the cell
                            // that's next to last cell that has data in it but
                            // in the
                            // case of a non-header row, i.e. here, we don't;
                            // only cells under those header cells that have
                            // data in them can be edited; in addition to this,
                            // we
                            // disable the three widgets and disallow adding or
                            // removing
                            // columns
                            //
                            event.cancel();
                            cancel = true;
                            enableAnalyteWidgets(false);
                            canAddRemoveColumn = false;
                        } else if (c > 0) {
                            if (c == 1) {
                                prevVal = (TableDataRow)row.cells.get(c - 1).getValue();
                                //
                                // we disallow the editing of the first result
                                // group
                                // cell if there's no analyte selected in the
                                // first
                                // cell of the analyte row
                                //
                                if (prevVal == null || prevVal.key == null) {
                                    event.cancel();
                                    cancel = true;
                                }
                            }
                            val = (TableDataRow)row.cells.get(c).getValue();
                            if (val != null && val.key != null) {
                                //
                                // here we check to see if there was a result
                                // group
                                // selected and if there was we open the tab
                                // corresponding
                                // to it
                                key = (Integer)val.key;
                                if (key - 1 < resultTabPanel.getTabBar().getTabCount()) {
                                    resultTabPanel.selectTab(key - 1);
                                }
                            }
                            //
                            // we disable the three widgets and disallow adding
                            // or removing columns and make the three widget to
                            // show no data
                            //
                            enableAnalyteWidgets(false);
                            canAddRemoveColumn = false;
                            anaSelCol = -1;
                            auto.setDelay(1);
                        }

                        //
                        // send DataChangeEvent to the three widgets to make
                        // them either show the data that corresponds to this
                        // cell or make them go blank
                        //
                        refreshAnalyteWidgets();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (analyteTable.getSelections().size() > 1 && !cancel)
                    window.setStatus(Messages.get().multiSelRowEditCol() + c, "");
                else
                    window.clearStatus();
            }

        });

        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int i, r, c, numCol, dindex, rows[];
                String name;
                TableDataRow row, val;
                TestAnalyteViewDO data;
                Integer key;
                AutoComplete<Integer> auto;
                TestAnalyteManager man;

                key = null;
                man = null;
                r = event.getRow();
                c = event.getCol();
                row = analyteTable.getRow(r);
                val = (TableDataRow)analyteTable.getObject(r, c);
                if (val != null)
                    key = (Integer)val.key;
                auto = (AutoComplete<Integer>)analyteTable.getColumns()
                                                          .get(c)
                                                          .getColumnWidget();
                rows = analyteTable.getSelectedRows();

                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                try {
                    if ((Boolean)row.data) {
                        numCol = displayManager.columnCount(r);
                        if (numCol < c) {
                            if (key != null) {
                                //
                                // we need to add a new column to the data grid
                                // if
                                // this column in the table in a sub header was
                                // edited for the first time and the key set as
                                // its value is not null
                                //
                                dindex = displayManager.getDataRowIndex(r);
                                man.addColumnAt(dindex,
                                                c - 1,
                                                key,
                                                auto.getTextBoxDisplay());
                                displayManager.setDataGrid(man.getAnalytes());
                            }
                        } else {
                            //
                            // we need to update all the cells in this column in
                            // the data grid if the column already exists in it
                            //
                            for (i = r + 1; i < analyteTable.numRows(); i++ ) {
                                if (displayManager.isHeaderRow(i))
                                    break;
                                data = displayManager.getObjectAt(i, c - 1);
                                data.setAnalyteId(key);
                                data.setAnalyteName(auto.getTextBoxDisplay());
                            }
                        }

                        //
                        // since we cannot allow adding or removing of columns
                        // if col exceeds the number of columns that a given
                        // row group has for itself in the manager, we set
                        // canAddRemoveColumn to false if this is the case
                        // and to true otherwise
                        //
                        if (c <= displayManager.columnCount(r))
                            canAddRemoveColumn = true;
                        else
                            canAddRemoveColumn = false;
                    } else {
                        if (c == 0) {
                            //
                            // if the updated cell was in a regular row, then
                            // we need to set the key as the test analyte's id
                            // in the DO at the appropriate location in the grid
                            //
                            data = displayManager.getObjectAt(r, c);
                            data.setAnalyteId(key);
                            data.setAnalyteName(auto.getTextBoxDisplay());
                            ActionEvent.fire(screen, Action.ANALYTE_CHANGED, data);
                        } else {
                            //
                            // otherwise we need to set the key as the result
                            // group number in the DO
                            //
                            for (i = 0; i < rows.length; i++ ) {
                                data = displayManager.getObjectAt(rows[i], c - 1);
                                if (data == null)
                                    continue;
                                data.setResultGroup(key);
                                if (key == null)
                                    name = "";
                                else
                                    name = key.toString();
                                analyteTable.setCell(rows[i], c, new TableDataRow(key,
                                                                                  name));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        analyteTable.addBeforeRowAddedHandler(new BeforeRowAddedHandler() {
            public void onBeforeRowAdded(BeforeRowAddedEvent event) {
                TableDataRow row;
                int index;
                try {
                    row = event.getRow();
                    index = event.getIndex();

                    //
                    // if the table is empty and an analyte row is to be added
                    // then a header row should be added before that row
                    //
                    if ( !(Boolean)row.data && index == 0) {
                        addAnalyteRow = true;
                        event.cancel();
                        analyteTable.addRow(createHeaderRow());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        analyteTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow prow, row, nrow, addrow;
                int index, dindex;
                TestAnalyteManager man;

                man = null;

                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                try {
                    row = event.getRow();
                    index = event.getIndex();

                    //
                    // if the row added to the table is an analyte row
                    //
                    if ( !(Boolean)row.data) {
                        //
                        // the row above the current one in the table
                        //
                        prow = analyteTable.getRow(index - 1);

                        //
                        // the index of the list in TestAnalyteManager that
                        // corresponds to the row above the current one
                        //
                        dindex = displayManager.getDataRowIndex(index - 1);

                        //
                        // dindex can be returned as -1 if index-1 excceds the
                        // size of displayManager's index list because the list
                        // corresponding to the new row won't have been added to
                        // the grid maintained by TestAnalyteManager;so if the
                        // number of rows in the table is more than one (index >
                        // 0),
                        // we try to find dindex for the last row in
                        // displayManager's
                        // index list; if prow is a header and has been added in
                        // the middle of the table, (headerAddedInTheMiddle ==
                        // true),
                        // i.e. not at the end, then this means that the list
                        // corresponding to the row above this one which is the
                        // header row exists neither in TestAnalyteManager nor
                        // in
                        // displayManager,thus we need to look at the list
                        // corresponding to the row two places above (index-2)
                        // in the table
                        //
                        if ( (dindex == -1 && index > 0) || headerAddedInTheMiddle) {
                            dindex = displayManager.getDataRowIndex(index - 2);
                            headerAddedInTheMiddle = false;
                        }

                        if ((Boolean)prow.data) {
                            //
                            // if there were rows after the header row in the
                            // table
                            // before the current row was added then we need to
                            // find out whether the next row after the current
                            // row is
                            // an analyte row and if it is then the current
                            // has not been added to a new row group but an
                            // existing one
                            // and thus the first boolean argument to addRowAt
                            // is false
                            //
                            if (index + 1 < analyteTable.numRows()) {
                                nrow = analyteTable.getRow(index + 1);
                                if ( !(Boolean)nrow.data) {
                                    man.addRowAt(dindex, false, false, getNextTempId());
                                    displayManager.setDataGrid(man.getAnalytes());
                                    analyteTable.selectRow(index);
                                    return;
                                }
                            }
                            //
                            // otherwise the header row is for a new row group
                            // and thus the first boolean argument to addRowAt
                            // is true
                            //
                            man.addRowAt(dindex + 1, true, false, getNextTempId());
                        } else {
                            //
                            // if prow is an analyte row then the newly added
                            // row has
                            // not been added to a new group and it will have to
                            // look at row previous to it in the data grid to
                            // copy
                            // data from look
                            //
                            man.addRowAt(dindex + 1, false, true, getNextTempId());
                        }
                        displayManager.setDataGrid(man.getAnalytes());
                        analyteTable.selectRow(index);
                    } else if (addAnalyteRow) {
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added
                        // after it if the add button was clicked with "Header"
                        // selected
                        //
                        addrow = new TableDataRow(10);
                        addrow.data = new Boolean(false);
                        if (index + 1 >= analyteTable.numRows()) {
                            analyteTable.addRow(addrow);
                        } else {
                            analyteTable.addRow(index + 1, addrow);
                        }
                    } else {
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added
                        // after it
                        //
                        addAnalyteRow = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        analyteTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index, dindex;
                TableDataRow row;
                TestAnalyteViewDO data;
                TestAnalyteManager man;

                index = event.getIndex();
                row = event.getRow();
                man = null;

                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                if ( !(Boolean)row.data) {
                    dindex = displayManager.getDataRowIndex(index);
                    data = man.getAnalyteAt(dindex, 0);
                    man.removeRowAt(dindex);
                    displayManager.setDataGrid(man.getAnalytes());
                    ActionEvent.fire(screen, Action.ANALYTE_DELETED, data);
                }
            }
        });

        addMatchesHandlerToAnalyteCells();

        typeId = (Dropdown<Integer>)def.getWidget(TestMeta.getAnalyteTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO data;
                int r;

                r = analyteTable.getSelectedRow();

                if (displayManager != null && r != -1) {
                    if (anaSelCol == 0)
                        data = displayManager.getObjectAt(r, anaSelCol);
                    else
                        data = displayManager.getObjectAt(r, anaSelCol - 1);

                    if (data != null)
                        typeId.setSelection(data.getTypeId());
                    else
                        typeId.setSelection(null);

                } else {
                    //
                    // everytime the data on the screen changes, the model in
                    // the analyte
                    // table gets refreshed; thus there are no rows and columns
                    // selected
                    // at that point and hence this widget needs to be cleared
                    // of any
                    // previous selection
                    //
                    if (typeId.getData() != null)
                        typeId.setSelection(null);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO data;
                TableDataRow row, nrow;
                int i, j, index, r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRow(index);
                    if ((Boolean)row.data) {
                        for (i = 0; i < r.length; i++ ) {
                            index = r[i];
                            for (j = index; j < analyteTable.numRows(); j++ ) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if (data == null)
                                    continue;

                                data.setTypeId(event.getValue());
                                analyteTable.clearCellExceptions(j, anaSelCol);

                                if (j + 1 < analyteTable.numRows()) {
                                    nrow = analyteTable.getRow(j + 1);
                                    if ((Boolean)nrow.data)
                                        j = analyteTable.numRows();
                                }
                            }
                        }
                    } else {
                        for (j = 0; j < r.length; j++ ) {
                            nrow = analyteTable.getRow(r[j]);
                            if ((Boolean)nrow.data)
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setTypeId(event.getValue());
                            analyteTable.clearCellExceptions(r[j], anaSelCol);
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                //
                // everytime the state of the screen changes, the model in the
                // analyte
                // table gets refreshed; thus there are no rows and columns
                // selected
                // at that point and hence this widget needs to be cleared of
                // any
                // previous selection and disabled
                //
                if (typeId.getData() != null)
                    typeId.setSelection(null);

                typeId.enable(false);
            }
        });

        isReportable = (CheckBox)def.getWidget(TestMeta.getAnalyteIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO data;
                int ar;

                ar = analyteTable.getSelectedRow();

                if (displayManager != null && ar != -1) {
                    if (anaSelCol == 0)
                        data = displayManager.getObjectAt(ar, anaSelCol);
                    else
                        data = displayManager.getObjectAt(ar, anaSelCol - 1);

                    if (data != null)
                        isReportable.setValue(data.getIsReportable());
                    else
                        isReportable.setValue("N");

                } else {
                    //
                    // everytime the data on the screen changes, the model in
                    // the analyte
                    // table gets refreshed; thus there are no rows and columns
                    // selected
                    // at that point and hence this widget needs to be cleared
                    // of any
                    // previous affirmative choices made
                    //
                    isReportable.setValue("N");
                }

            }

            public void onValueChange(ValueChangeEvent<String> event) {
                TestAnalyteViewDO data;
                TableDataRow row, nrow;
                int i, j, index, r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRow(index);
                    if ((Boolean)row.data) {
                        for (i = 0; i < r.length; i++ ) {
                            index = r[i];
                            for (j = index; j < analyteTable.numRows(); j++ ) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if (data == null)
                                    continue;

                                data.setIsReportable(event.getValue());

                                if (j + 1 < analyteTable.numRows()) {
                                    nrow = analyteTable.getRow(j + 1);
                                    if ((Boolean)nrow.data)
                                        j = analyteTable.numRows();
                                }
                            }
                        }
                    } else {
                        for (j = 0; j < r.length; j++ ) {
                            nrow = analyteTable.getRow(r[j]);
                            if ((Boolean)nrow.data)
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setIsReportable(event.getValue());
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                //
                // everytime state of the screen changes, the model in the
                // analyte
                // table gets refreshed; thus there are no rows and columns
                // selected
                // at that point and hence this widget needs to be cleared of
                // any
                // previous affirmative choice made and disabled
                //
                isReportable.setValue("N");
                isReportable.enable(false);
            }
        });

        scriptletId = (Dropdown<Integer>)def.getWidget(TestMeta.getAnalyteScriptletId());
        addScreenHandler(scriptletId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO data;
                int r;

                r = analyteTable.getSelectedRow();

                if (displayManager != null && r != -1) {
                    if (anaSelCol == 0)
                        data = displayManager.getObjectAt(r, anaSelCol);
                    else
                        data = displayManager.getObjectAt(r, anaSelCol - 1);

                    if (data != null)
                        scriptletId.setSelection(data.getScriptletId());
                    else
                        scriptletId.setSelection(null);
                } else {
                    //
                    // everytime the data on the screen changes, the model in
                    // the analyte
                    // table gets refreshed; thus there are no rows and columns
                    // selected
                    // at that point and hence this widget needs to be cleared
                    // of any
                    // previous selection and disabled
                    //
                    scriptletId.setSelection(null);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO data;
                TableDataRow row, nrow;
                int i, j, index, r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRow(index);
                    if ((Boolean)row.data) {
                        for (i = 0; i < r.length; i++ ) {
                            index = r[i];
                            for (j = index; j < analyteTable.numRows(); j++ ) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if (data == null)
                                    continue;

                                data.setScriptletId(event.getValue());

                                if (j + 1 < analyteTable.numRows()) {
                                    nrow = analyteTable.getRow(j + 1);
                                    if ((Boolean)nrow.data)
                                        j = analyteTable.numRows();
                                }
                            }
                        }
                    } else {
                        for (j = 0; j < r.length; j++ ) {
                            nrow = analyteTable.getRow(r[j]);
                            if ((Boolean)nrow.data)
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setScriptletId(event.getValue());
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                //
                // everytime the state of the screen changes, the model in the
                // analyte
                // table gets refreshed; thus there are no rows and columns
                // selected
                // at that point and hence this widget needs to be cleared of
                // any
                // previous selection and disabled
                //
                scriptletId.setSelection(null);
                scriptletId.enable(false);
            }
        });

        tableActions = (Dropdown<String>)def.getWidget("tableActions");
        addScreenHandler(tableActions, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tableActions.enable(EnumSet.of(State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                tableActions.setQueryMode(false);
                if (tableActions.getData() != null &&
                    EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()))
                    tableActions.setSelection("analyte");
            }
        });

        addButton = (AppButton)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analyteTable.finishEditing();
                if ("analyte".equals(tableActions.getValue())) {
                    addAnalyte();
                } else if ("column".equals(tableActions.getValue())) {
                    addColumn();
                } else {
                    addHeader();
                }

                tableActions.setSelection("analyte");

            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                        .contains(event.getState()));
            }
        });

        removeButton = (AppButton)def.getWidget("removeButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analyteTable.finishEditing();
                if ("analyte".equals(tableActions.getValue())) {
                    removeAnalyte();
                } else if ("column".equals(tableActions.getValue())) {
                    removeColumn();
                } else {
                    removeHeader();
                }

                tableActions.setSelection("analyte");
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        resultTabPanel = (ScrollableTabBar)def.getWidget("resultTabPanel");

        addScreenHandler(resultTabPanel, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                int numTabs;
                TestResultManager man;

                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                resultTabPanel.clearTabs();
                numTabs = man.groupCount();

                for (int i = 0; i < numTabs; i++ )
                    resultTabPanel.addTab(String.valueOf(i + 1));
                if (numTabs > 0)
                    resultTabPanel.selectTab(0);
            }

        });

        resultTabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                resultTable.finishEditing();
            }
        });

        resultTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tab;

                tab = resultTabPanel.getTabBar().getSelectedTab();
                resultTable.load(getResultTableModel(tab));
                showErrorsForResultGroup(tab);
            }
        });

        resultTable = (TableWidget)def.getWidget("resultTable");
        addScreenHandler(resultTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                int selTab;

                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                if (state != State.QUERY) {
                    selTab = resultTabPanel.getTabBar().getSelectedTab();
                    resultTable.load(getResultTableModel(selTab));
                } else {
                    resultTable.load(new ArrayList<TableDataRow>());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resultTable.enable(true);
            }
        });

        resultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c, group;

                if (state != State.ADD && state != State.UPDATE)
                    event.cancel();

                r = event.getRow();
                c = event.getCol();
                group = resultTabPanel.getTabBar().getSelectedTab();

                switch (c) {
                    case 0:
                        clearResultCellError(group,
                                             r,
                                             TestMeta.getResultUnitOfMeasureId());
                        break;
                    case 1:
                        clearResultCellError(group, r, TestMeta.getResultTypeId());
                        break;
                    case 2:
                        clearResultCellError(group, r, TestMeta.getResultValue());
                        break;
                }
            }

        });

        resultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, group;
                TestResultViewDO data;
                Object val;
                TestResultManager man;

                r = event.getRow();
                c = event.getCol();
                val = resultTable.getObject(r, c);
                group = resultTabPanel.getTabBar().getSelectedTab();

                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                data = man.getResultAt(group + 1, r);

                switch (c) {
                    case 0:
                        if (val != null)
                            data.setUnitOfMeasureId((Integer)val);
                        else
                            data.setUnitOfMeasureId(null);
                        break;
                    case 1:
                        if (val != null)
                            data.setTypeId((Integer)val);
                        else
                            data.setTypeId(null);

                        resultTable.clearCellExceptions(r, 2);
                        try {
                            validateValue(data, (String)resultTable.getObject(r, 2));
                        } catch (Exception e) {
                            resultTable.setCellException(r, 2, e);
                            addToResultErrorList(group,
                                                 r,
                                                 TestMeta.getResultValue(),
                                                 e.getMessage());
                        }
                        break;
                    case 2:
                        resultTable.clearCellExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (Exception e) {
                            resultTable.setCellException(r, c, e);
                            addToResultErrorList(group,
                                                 r,
                                                 TestMeta.getResultValue(),
                                                 e.getMessage());
                        }
                        ActionEvent.fire(screen, Action.RESULT_CHANGED, data);
                        break;
                    case 3:
                        if (val != null)
                            data.setFlagsId((Integer)val);
                        else
                            data.setFlagsId(null);
                        break;
                    case 4:
                        if (val != null)
                            data.setSignificantDigits((Integer)val);
                        else
                            data.setSignificantDigits(null);
                        break;
                    case 5:
                        if (val != null)
                            data.setRoundingMethodId((Integer)val);
                        else
                            data.setRoundingMethodId(null);
                        break;
                }
            }
        });

        resultTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int selTab;
                TestResultManager man;

                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                if (man.groupCount() == 0)
                    man.addResultGroup();

                selTab = resultTabPanel.getTabBar().getSelectedTab();
                man.addResult(selTab + 1, getNextTempId());
            }
        });

        resultTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int r, selTab;
                TestResultViewDO data;
                TestResultManager man;

                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                selTab = resultTabPanel.getTabBar().getSelectedTab();
                r = event.getIndex();
                data = man.getResultAt(selTab + 1, r);

                man.removeResultAt(selTab + 1, r);

                ActionEvent.fire(screen, Action.RESULT_DELETED, data);
            }
        });

        addResultTabButton = (AppButton)def.getWidget("addResultTabButton");
        addScreenHandler(addResultTabButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int count;
                TestResultManager man;

                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                count = resultTabPanel.getTabBar().getTabCount();

                resultTabPanel.addTab(String.valueOf(count + 1));
                resultTabPanel.selectTab(count);
                man.addResultGroup();

            }

            public void onStateChange(StateChangeEvent<State> event) {
                addResultTabButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeTestResultButton = (AppButton)def.getWidget("removeTestResultButton");
        addScreenHandler(removeTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = resultTable.getSelectedRow();

                if (r != -1 && resultTable.numRows() > 0)
                    resultTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestResultButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });

        addTestResultButton = (AppButton)def.getWidget("addTestResultButton");
        addScreenHandler(addTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n, numTabs;

                numTabs = resultTabPanel.getTabBar().getTabCount();

                if (numTabs == 0) {
                    resultTabPanel.addTab(String.valueOf(1));
                    resultTabPanel.selectTab(0);
                }

                resultTable.addRow();
                n = resultTable.numRows() - 1;
                resultTable.selectRow(n);
                resultTable.scrollToSelection();
                resultTable.startEditing(n, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestResultButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        dictionaryLookUpButton = (AppButton)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                resultTable.finishEditing();
                showDictionary(null, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryLookUpButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });

        addAnalyteRow = true;
        anaSelCol = -1;
        screen = this;
        tempId = -1;
        headerAddedInTheMiddle = false;
        resultErrorList = null;
        canAddRemoveColumn = false;

    }

    public void setManager(TestManager manager) {
        this.manager = manager;

        loaded = false;

        displayManager = new TestAnalyteDisplayManager<TestAnalyteViewDO>();
        displayManager.setType(TestAnalyteDisplayManager.Type.TEST);
    }

    public void draw() {
        if ( !loaded) {
            try {
                resultErrorList = null;
                displayManager.setDataGrid(manager.getTestAnalytes().getAnalytes());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            DataChangeEvent.fire(this);
            setUnitsOfMeasure();
        }

        loaded = true;
    }

    public void onGetMatches(GetMatchesEvent event) {
        ArrayList<TableDataRow> model;
        int rg;
        String match;
        ArrayList<AnalyteDO> list;
        AutoComplete auto;

        try {
            auto = ((AutoComplete)event.getSource());

            if (isAnalyteQuery()) {
                list = AnalyteService1Impl.INSTANCE.fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                model = new ArrayList<TableDataRow>();
                for (AnalyteDO data : list)
                    model.add(new TableDataRow(data.getId(), data.getName()));
            } else {
                auto.setDelay(1);
                model = new ArrayList<TableDataRow>();
                match = event.getMatch();
                try {
                    rg = Integer.parseInt(match);
                    if (rg > 0 && rg <= resultTabPanel.getTabBar().getTabCount()) {
                        model.add(new TableDataRow(rg, match));
                    } else {
                        model.add(new TableDataRow(null, ""));
                    }
                } catch (NumberFormatException e) {
                    model.add(new TableDataRow(null, ""));
                }
            }
            auto.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
    }

    public void showTestAnalyteError(GridFieldErrorException error) {
        int dindex, trindex, col, i;
        String field;
        TableDataRow row;
        ArrayList<Exception> errors;

        dindex = error.getRowIndex();
        col = error.getColumnIndex();
        field = error.getFieldName();

        //
        // find out which table row does the grid row at dindex represent
        //
        for (trindex = 0; trindex < displayManager.rowCount(); trindex++ ) {
            if (dindex == displayManager.getIndexAt(trindex))
                break;
        }

        if (TestMeta.getAnalyteResultGroup().equals(field)) {
            errors = analyteTable.getCell(trindex, col + 1).getExceptions();
            if (errors == null || !errors.contains(error))
                analyteTable.setCellException(trindex, col + 1, error);
        } else if (col == 0) {
            errors = analyteTable.getCell(trindex, col).getExceptions();
            if (errors == null || !errors.contains(error))
                analyteTable.setCellException(trindex, col, error);
        } else {
            for (i = trindex - 1; i > -1; i-- ) {
                row = analyteTable.getRow(i);
                errors = analyteTable.getCell(i, col + 1).getExceptions();
                if ((Boolean)row.data) {
                    if ( (errors == null || !errors.contains(error))) {
                        analyteTable.setCellException(i, col + 1, error);
                    }
                    break;
                }
            }
        }
    }

    public void showTestResultError(GridFieldErrorException error) {
        int tab;
        tab = resultTabPanel.getTabBar().getSelectedTab();
        if ( !errorExistsInList(error.getRowIndex(),
                                error.getColumnIndex(),
                                error.getFieldName(),
                                error.getMessage())) {
            if (resultErrorList == null)
                resultErrorList = new ArrayList<GridFieldErrorException>();
            resultErrorList.add(error);
        }
        if (error.getRowIndex() == tab)
            showErrorsForResultGroup(tab);
    }

    public HandlerRegistration addActionHandler(ActionHandler<AnalyteAndResultTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void setState(State state) {
        super.setState(state);
        if (state == State.ADD || state == State.UPDATE) {
            anaSelCol = -1;
            canAddRemoveColumn = false;
        }
    }

    public void onAction(ActionEvent<SampleTypeTab.Action> event) {
        if (state == State.QUERY)
            return;

        if (event.getAction() == SampleTypeTab.Action.UNIT_CHANGED ||
            event.getAction() == SampleTypeTab.Action.UNIT_DELETED) {
            setUnitsOfMeasure();
        }
    }

    protected void clearKeys(TestAnalyteManager tam, TestResultManager trm) {
        TestAnalyteViewDO ana;
        TestResultViewDO res;
        Integer id;
        int i, j;

        for (i = 0; i < tam.rowCount(); i++ ) {
            for (j = 0; j < tam.columnCount(i); j++ ) {
                ana = tam.getAnalyteAt(i, j);
                if (j == 0) {
                    ana.setId(ana.getId() * ( -1));
                    id = ana.getId();
                    if (id < tempId)
                        tempId = id;
                } else {
                    ana.setId(null);
                }
                ana.setTestId(null);
            }
        }

        for (i = 1; i < trm.groupCount() + 1; i++ ) {
            for (j = 0; j < trm.getResultGroupSize(i); j++ ) {
                res = trm.getResultAt(i, j);
                res.setId(res.getId() * ( -1));
                res.setTestId(null);
                id = res.getId();
                if (id < tempId)
                    tempId = id;
            }
        }

    }

    protected void finishEditing() {
        analyteTable.finishEditing();
        resultTable.finishEditing();
    }

    private ArrayList<TableDataRow> getAnalyteTableModel() {
        int m, c, len;
        ArrayList<TableDataRow> model;
        TableDataRow hrow, row;
        TestAnalyteViewDO data;
        boolean headerFilled;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        hrow = null;
        headerFilled = false;

        for (m = 0; m < displayManager.rowCount(); m++ ) {
            if (displayManager.isHeaderRow(m)) {
                m++ ;
                hrow = createHeaderRow();
                model.add(hrow);
                headerFilled = false;
            }

            len = displayManager.columnCount(m);
            row = new TableDataRow(10);
            row.data = new Boolean(false);
            for (c = 0; c < len; c++ ) {
                data = displayManager.getObjectAt(m, c);
                row.key = data.getId();
                if (c == 0) {
                    row.cells.get(0).setValue(new TableDataRow(data.getAnalyteId(),
                                                               data.getAnalyteName()));
                    row.cells.get(1)
                             .setValue(new TableDataRow(data.getResultGroup(),
                                                        String.valueOf(data.getResultGroup())));
                    continue;
                }

                if ( !headerFilled)
                    hrow.cells.get(c + 1)
                              .setValue(new TableDataRow(data.getAnalyteId(),
                                                         data.getAnalyteName()));

                row.cells.get(c + 1)
                         .setValue(new TableDataRow(data.getResultGroup(),
                                                    String.valueOf(data.getResultGroup())));
            }
            headerFilled = true;
            model.add(row);
        }
        return model;

    }

    private IdNameVO getDictionary(String entry) {
        ArrayList<IdNameVO> list;
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        entry = DataBaseUtil.trim(entry);
        if (entry == null)
            return null;

        query = new Query();
        fields = new ArrayList<QueryData>();
        field = new QueryData();
        field.setKey(CategoryMeta.getDictionaryEntry());
        field.setType(QueryData.Type.STRING);
        field.setQuery(entry);
        fields.add(field);

        field = new QueryData();
        field.setKey(CategoryMeta.getIsSystem());
        field.setType(QueryData.Type.STRING);
        field.setQuery("N");
        fields.add(field);

        query.setFields(fields);

        try {
            list = DictionaryService1Impl.INSTANCE.fetchByEntry(query);
            if (list.size() == 1)
                return list.get(0);
            else if (list.size() > 1)
                showDictionary(entry, list);
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        return null;
    }

    private void validateValue(TestResultViewDO data, String value) throws Exception {
        IdNameVO dict;

        if (value == null)
            return;

        try {
            if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId())) {
                dict = getDictionary((String)value);
                if (dict != null) {
                    data.setValue(dict.getId().toString());
                    data.setDictionary(dict.getName());
                } else {
                    data.setDictionary(null);
                    throw new Exception(Messages.get().test_invalidValue());
                }
            } else if (Constants.dictionary().TEST_RES_TYPE_NUMERIC.equals(data.getTypeId())) {
                rangeNumeric.setRange((String)value);
                data.setValue(rangeNumeric.toString());
            } else if (Constants.dictionary().TEST_RES_TYPE_TITER.equals(data.getTypeId())) {
                rangeTiter.setRange((String)value);
                data.setValue(rangeTiter.toString());
            } else if (Constants.dictionary().TEST_RES_TYPE_DEFAULT.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_DATE.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_DATE_TIME.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_TIME.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER.equals(data.getTypeId()) ||
                       Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED.equals(data.getTypeId())) {
                data.setValue((String)value);
            } else {
                throw new Exception(Messages.get().test_invalidValue());
            }
        } catch (Exception e) {
            data.setValue(null);
            data.setDictionary(null);
            throw e;
        }
    }

    private TableDataRow createHeaderRow() {
        TableDataRow row;
        TableDataCell cell;

        row = new TableDataRow(10);

        cell = row.cells.get(0);
        cell.setValue(new TableDataRow( -1, Messages.get().analyte()));

        cell = row.cells.get(1);
        cell.setValue(new TableDataRow( -1, Messages.get().value()));
        row.style = "SubHeader";
        row.data = new Boolean(true);

        return row;
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("test_analyte_type");
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("scriptlet_test_analyte");
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        scriptletId.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("test_result_type");
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnWidget(TestMeta.getResultTypeId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("test_result_flags");
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnWidget(TestMeta.getResultFlagsId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("rounding_method");
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnWidget(TestMeta.getResultRoundingMethodId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow("analyte", Messages.get().analyte()));
        model.add(new TableDataRow("column", Messages.get().column()));
        model.add(new TableDataRow("header", Messages.get().header()));

        tableActions.setModel(model);
        tableActions.setSelection("analyte");

        setUnitsOfMeasure();
    }

    private void setUnitsOfMeasure() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> dictList;
        TestTypeOfSampleDO data;
        String entry;
        Integer unitId;
        ArrayList<Integer> unitList;
        TestTypeOfSampleManager man;

        model = new ArrayList<TableDataRow>();

        model.add(new TableDataRow(null, ""));

        if (state == State.ADD || state == State.UPDATE) {
            unitList = new ArrayList<Integer>();
            man = null;
            try {
                man = manager.getSampleTypes();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }

            for (int i = 0; i < man.count(); i++ ) {
                data = man.getTypeAt(i);
                unitId = data.getUnitOfMeasureId();
                try {
                    if (unitId != null && !unitList.contains(unitId)) {
                        entry = DictionaryCache.getById(unitId).getEntry();
                        model.add(new TableDataRow(unitId, entry));
                        unitList.add(unitId);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            dictList = CategoryCache.getBySystemName("unit_of_measure");
            for (DictionaryDO dict : dictList) {
                model.add(new TableDataRow(dict.getId(), dict.getEntry()));
            }
        }
        ((Dropdown<Integer>)resultTable.getColumnWidget(TestMeta.getResultUnitOfMeasureId())).setModel(model);
    }

    private boolean isAnalyteQuery() {
        TableDataRow row;
        int ac;
        row = analyteTable.getRow(analyteTable.getSelectedRow());
        ac = analyteTable.getSelectedCol();
        if ( ((Boolean)row.data && ac > 1) || ( !(Boolean)row.data && ac == 0))
            return true;

        return false;
    }

    /**
     * This method adds a BeforeGetMatchesHandler and a GetMatchesHandler to all
     * the cells in the table showing test analytes. This is done in a method
     * like this and not through the standard way of adding these handlers to
     * individual cells because the code for the handlers for multiple cells is
     * the same and very similar across all the cells in the table
     */
    private void addMatchesHandlerToAnalyteCells() {
        AutoComplete<Integer> auto;
        ArrayList<TableColumn> columns;

        columns = analyteTable.getColumns();
        for (int i = 0; i < columns.size(); i++ ) {
            auto = (AutoComplete<Integer>)columns.get(i).getColumnWidget();
            auto.addBeforeGetMatchesHandler(this);
            auto.addGetMatchesHandler(this);
        }
    }

    private void refreshAnalyteWidgets() {
        DataChangeEvent.fire(screen, isReportable);
        DataChangeEvent.fire(screen, typeId);
        DataChangeEvent.fire(screen, scriptletId);
    }

    private void enableAnalyteWidgets(boolean enable) {
        isReportable.enable(enable);
        typeId.enable(enable);
        scriptletId.enable(enable);
    }

    private void addColumnToRowsBelow(int row) {
        TableDataRow trow;

        for (int i = row + 1; i < analyteTable.numRows(); i++ ) {
            trow = analyteTable.getRow(i);
            if ( !(Boolean)trow.data)
                addColumnToRow(i);
            else
                break;
        }
    }

    private void addColumnToRow(int row) {
        int finCol;
        TableDataRow trow, lrow;
        String name;
        Integer key;

        if (anaSelCol < 2 || anaSelCol == 10 || row < 0)
            return;

        trow = analyteTable.getRow(row);
        finCol = displayManager.columnCount(row);

        if (finCol == 10)
            return;

        key = null;
        name = "";
        for (int i = finCol; i > anaSelCol; i-- ) {
            lrow = (TableDataRow)trow.cells.get(i - 1).getValue();
            if (lrow != null) {
                key = (Integer)lrow.key;
                name = (String)lrow.cells.get(0).getValue();
            }
            trow.cells.get(i).setValue(new TableDataRow(key, name));
            trow.cells.get(i - 1).setValue(new TableDataRow(null, ""));
        }
    }

    private void removeColumnFromRow(int r) {
        int finalIndex;
        String name;
        Integer key;
        TableDataRow row, rrow, blankrow;
        TableDataCell cell, ncell;

        if (anaSelCol < 2 || r < 0)
            return;

        row = analyteTable.getRow(r);
        finalIndex = displayManager.columnCount(r) + 1;
        blankrow = new TableDataRow(null, "");

        if (finalIndex == 2)
            return;

        if (anaSelCol == 9) {
            cell = row.cells.get(anaSelCol);
            cell.setValue(blankrow);
            cell.clearExceptions();
        } else {
            for (int i = anaSelCol; i < finalIndex; i++ ) {
                cell = row.cells.get(i);
                cell.clearExceptions();
                if (i == finalIndex - 1) {
                    cell.setValue(blankrow);
                } else {
                    ncell = row.cells.get(i + 1);
                    if (ncell.getExceptions() != null) {
                        for (Exception ex : ncell.getExceptions())
                            cell.addException(ex);
                    }
                    rrow = (TableDataRow)ncell.getValue();
                    if (rrow == null) {
                        cell.setValue(blankrow);
                        continue;
                    }
                    key = (Integer)rrow.key;
                    name = (String)rrow.cells.get(0).getValue();

                    if (key != null)
                        cell.setValue(new TableDataRow(key, name));
                    else
                        cell.setValue(blankrow);
                }
            }
        }
    }

    private void removeColumnFromRowsBelow(int r) {
        TableDataRow row;

        for (int i = r + 1; i < analyteTable.numRows(); i++ ) {
            row = analyteTable.getRow(i);
            if ( !(Boolean)row.data)
                removeColumnFromRow(i);
            else
                break;
        }
    }

    private ArrayList<TableDataRow> getResultTableModel(int group) {
        int size;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        TestResultViewDO data;
        TestResultManager man;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            man = manager.getTestResults();
            size = man.getResultGroupSize(group + 1);
            for (int i = 0; i < size; i++ ) {
                data = man.getResultAt(group + 1, i);
                row = new TableDataRow(6);
                row.cells.get(0).setValue(data.getUnitOfMeasureId());
                row.cells.get(1).setValue(data.getTypeId());
                if (data.getDictionary() == null)
                    row.cells.get(2).setValue(data.getValue());
                else
                    row.cells.get(2).setValue(data.getDictionary());
                row.cells.get(3).setValue(data.getFlagsId());
                row.cells.get(4).setValue(data.getSignificantDigits());
                row.cells.get(5).setValue(data.getRoundingMethodId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private void showErrorsForResultGroup(int group) {
        GridFieldErrorException error;
        int i, col;
        ArrayList<GridFieldErrorException> list;
        TableDataRow row;

        if (resultErrorList == null)
            return;

        for (i = 0; i < resultErrorList.size(); i++ ) {
            error = resultErrorList.get(i);
            col = error.getColumnIndex();
            row = resultTable.getRow(col);
            if (error.getRowIndex() == group) {
                if (row.data == null)
                    row.data = new ArrayList<GridFieldErrorException>();

                list = (ArrayList<GridFieldErrorException>)row.data;

                if ( !list.contains(error)) {
                    resultTable.setCellException(col, error.getFieldName(), error);
                    list.add(error);
                }
            }
        }
    }

    private int getNextTempId() {
        return --tempId;
    }

    private void clearResultCellError(int group, int row, String field) {
        GridFieldErrorException error;
        int i;

        if (resultErrorList == null)
            return;

        for (i = 0; i < resultErrorList.size(); i++ ) {
            error = resultErrorList.get(i);
            if (error.getRowIndex() == group && error.getColumnIndex() == row &&
                field.equals(error.getFieldName())) {
                resultErrorList.remove(error);
            }
        }
    }

    private void addToResultErrorList(int group, int row, String field, String message) {
        GridFieldErrorException error;

        if ( !errorExistsInList(group, row, field, message)) {
            error = new GridFieldErrorException(message, group, row, field, "resultTable");
            if (resultErrorList == null)
                resultErrorList = new ArrayList<GridFieldErrorException>();
            resultErrorList.add(error);
        }
    }

    private void addAnalyte() {
        int r;
        TableDataRow row;

        r = analyteTable.getSelectedRow();
        row = new TableDataRow(10);
        row.data = new Boolean(false);
        if (r == -1 || r == analyteTable.numRows() - 1) {
            analyteTable.addRow(row);
            analyteTable.selectRow(analyteTable.numRows() - 1);
            analyteTable.startEditing(analyteTable.numRows() - 1, 0);
        } else {
            analyteTable.addRow(r + 1, row);
            analyteTable.selectRow(r + 1);
            analyteTable.startEditing(r + 1, 0);
        }
    }

    private void removeAnalyte() {
        int r;
        TableDataRow row, nrow, prow;

        r = analyteTable.getSelectedRow();
        if (r != -1) {
            row = analyteTable.getRow(r);
            if ( !(Boolean)row.data) {
                //
                // we want to make sure that there are at least 2 analyte rows
                // in the row group that this row belongs to, if that's not the
                // case we don't allow this row to be deleted
                //
                prow = analyteTable.getRow(r - 1);
                if ((Boolean)prow.data) {
                    //
                    // if the row above the current one is a header row then the
                    // row next to the current one should be an analyte row in
                    // order to be able to delete the current row; the current
                    // row can also not be deleted in the case that it is the
                    // last row in the table
                    //
                    if (r + 1 < analyteTable.numRows()) {
                        nrow = analyteTable.getRow(r + 1);
                        if ( !(Boolean)nrow.data)
                            analyteTable.deleteRow(r);
                        else
                            Window.alert(Messages.get().atleastTwoRowsInRowGroup());
                    } else {
                        Window.alert(Messages.get().atleastTwoRowsInRowGroup());
                    }
                } else {
                    //
                    // if the row above the current one is an analyte row then
                    // the current row can be deleted because certainly it's not
                    // the only row in its row group
                    //
                    analyteTable.deleteRow(r);
                }

            }
        }
    }

    private void addColumn() {
        int r, index;
        TestAnalyteManager man;

        if ( !canAddRemoveColumn) {
            Window.alert(Messages.get().cantAddColumn());
            return;
        }

        r = analyteTable.getSelectedRow();
        try {
            if (anaSelCol != -1 && r != -1) {
                man = manager.getTestAnalytes();
                index = displayManager.getDataRowIndex(r);
                man.addColumnAt(index, anaSelCol - 1, null);
                displayManager.setDataGrid(man.getAnalytes());

                addColumnToRow(r);
                addColumnToRowsBelow(r);

                analyteTable.refresh();
                anaSelCol = -1;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeColumn() {
        int r, index;
        TestAnalyteManager man;

        if ( !canAddRemoveColumn) {
            Window.alert(Messages.get().cantRemoveColumn());
            return;
        }

        r = analyteTable.getSelectedRow();
        try {
            if (anaSelCol != -1 && r != -1) {
                man = manager.getTestAnalytes();
                removeColumnFromRow(r);
                removeColumnFromRowsBelow(r);

                analyteTable.refresh();

                index = displayManager.getDataRowIndex(r);
                man.removeColumnAt(index, anaSelCol - 1);
                displayManager.setDataGrid(man.getAnalytes());

                anaSelCol = -1;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addHeader() {
        int r, num;
        TableDataRow row;

        r = analyteTable.getSelectedRow();
        num = analyteTable.numRows();

        if (r == -1 || r == num - 1) {
            analyteTable.addRow(createHeaderRow());
            analyteTable.scrollToSelection();
        } else {
            row = analyteTable.getRow(r);
            if ((Boolean)row.data) {
                headerAddedInTheMiddle = true;
                analyteTable.addRow(r, createHeaderRow());
                analyteTable.scrollToSelection();
            } else {
                row = analyteTable.getRow(r + 1);
                if ((Boolean)row.data) {
                    headerAddedInTheMiddle = true;
                    analyteTable.addRow(r + 1, createHeaderRow());
                    analyteTable.scrollToSelection();
                } else {
                    Window.alert(Messages.get().headerCantBeAddedInsideGroup());
                }
            }
        }
    }

    private void removeHeader() {
        int r;
        TableDataRow row;

        r = analyteTable.getSelectedRow();

        if (r != -1) {
            row = analyteTable.getRow(r);
            if ((Boolean)row.data) {
                analyteTable.deleteRow(r);
                while (r < analyteTable.numRows()) {
                    row = analyteTable.getRow(r);
                    if ((Boolean)row.data)
                        break;

                    analyteTable.deleteRow(r);
                }
            }
        }
    }

    private void showDictionary(String entry, ArrayList<IdNameVO> list) {
        ModalWindow modal;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreenUI();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("DictionaryLookup error: " + e.getMessage());
                return;
            }

            dictLookup.addActionHandler(new org.openelis.ui.event.ActionHandler<DictionaryLookupScreenUI.Action>() {
                public void onAction(org.openelis.ui.event.ActionEvent<DictionaryLookupScreenUI.Action> event) {
                    int selTab, r;
                    ArrayList<IdNameVO> list;
                    TestResultViewDO data;
                    IdNameVO entry;
                    TestResultManager man;

                    man = null;

                    selTab = resultTabPanel.getTabBar().getSelectedTab();
                    if (event.getAction() == DictionaryLookupScreenUI.Action.OK) {
                        list = (ArrayList<IdNameVO>)event.getData();
                        if (list != null) {
                            r = resultTable.getSelectedRow();
                            if (r == -1) {
                                window.setError(Messages.get().test_noSelectedRow());
                                return;
                            }

                            try {
                                man = manager.getTestResults();
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                e.printStackTrace();
                                return;
                            }

                            //
                            // set the first dictionary value in the row that
                            // was
                            // selected when the lookup screen was brought up
                            //
                            entry = list.get(0);
                            data = man.getResultAt(selTab + 1, r);
                            data.setValue(entry.getId().toString());
                            data.setDictionary(entry.getName());
                            data.setTypeId(Constants.dictionary().TEST_RES_TYPE_DICTIONARY);
                            resultTable.setCell(r, 1, Constants.dictionary().TEST_RES_TYPE_DICTIONARY);
                            resultTable.clearCellExceptions(r, 1);
                            resultTable.setCell(r, 2, data.getDictionary());
                            resultTable.clearCellExceptions(r, 2);
                            clearResultCellError(selTab, r, TestMeta.getResultValue());
                            ActionEvent.fire(screen, Action.RESULT_CHANGED, data);

                            //
                            // set the rest of the dictionary values in newly
                            // added rows at the end of the table
                            //
                            for (int i = 1; i < list.size(); i++ ) {
                                resultTable.addRow();
                                entry = list.get(i);
                                r = resultTable.numRows() - 1;
                                data = man.getResultAt(selTab + 1, r);
                                data.setValue(entry.getId().toString());
                                data.setDictionary(entry.getName());
                                data.setTypeId(Constants.dictionary().TEST_RES_TYPE_DICTIONARY);
                                resultTable.setCell(r, 1, Constants.dictionary().TEST_RES_TYPE_DICTIONARY);
                                resultTable.setCell(r, 2, data.getDictionary());
                            }

                        }
                    }
                }

            });

        }
        modal = new ModalWindow();
        modal.setName(Messages.get().chooseDictEntry());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(dictLookup);
        modal.setSize("515px", "390px");
        dictLookup.setWindow(modal);
        dictLookup.setState(org.openelis.ui.screen.State.DEFAULT);
        if (list != null) {
            dictLookup.clearFields();
            dictLookup.setQueryResult(entry, list);
        } else if (entry != null) {
            dictLookup.clearFields();
            dictLookup.executeQuery(entry);
        }
    }

    private boolean errorExistsInList(int group, int row, String field, String message) {
        GridFieldErrorException ex;

        if (resultErrorList == null)
            return false;

        for (int i = 0; i < resultErrorList.size(); i++ ) {
            ex = resultErrorList.get(i);
            if ( (ex.getRowIndex() == group) && (ex.getColumnIndex() == row) &&
                (ex.getFieldName().equals(field)) && (ex.getMessage().equals(message))) {
                return true;
            }
        }

        return false;
    }
}
