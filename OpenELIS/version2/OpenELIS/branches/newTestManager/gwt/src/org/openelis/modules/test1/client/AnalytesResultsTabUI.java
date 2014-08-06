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
package org.openelis.modules.test1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.manager.TestManager1;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
import org.openelis.modules.test.client.TestAnalyteDisplayManager;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.GridFieldErrorException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ScrollableTabBar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.BeforeRowAddedEvent;
import org.openelis.ui.widget.table.event.BeforeRowAddedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalytesResultsTabUI extends Screen {

    @UiTemplate("AnalytesResultsTab.ui.xml")
    interface AnalytesResultsTabUiBinder extends UiBinder<Widget, AnalytesResultsTabUI> {
    };

    private static AnalytesResultsTabUiBinder uiBinder = GWT.create(AnalytesResultsTabUiBinder.class);

    public enum Action {
        ANALYTE_CHANGED, ANALYTE_DELETED, RESULT_CHANGED, RESULT_DELETED
    };

    // @UiField
    protected Table                                        analyteTable, resultTable;

    // @UiField
    protected Dropdown<Integer>                            analyteType, scriptlet, resultType,
                    resultFlag, roundingMethod, unitOfMeasure;

    // @UiField
    protected Dropdown<String>                             tableActions;

    // @UiField
    protected AutoComplete                                 auto;

    // @UiField
    protected ScrollableTabBar                             resultTabPanel;

    // @UiField
    protected CheckBox                                     isReportable;

    // @UiField
    protected Button                                       addButton, removeButton;

    protected DictionaryLookupScreen                       dictLookup;

    protected Screen                                       parentScreen;

    protected EventBus                                     parentBus;

    protected TestManager1                                 manager;

    protected TestAnalyteDisplayManager<TestAnalyteViewDO> displayManager;

    private ArrayList<GridFieldErrorException>             resultErrorList;

    protected boolean                                      addAnalyteRow, loaded,
                    headerAddedInTheMiddle, canAddRemoveColumn, isVisible;

    protected int                                          anaSelCol, tempId;

    public AnalytesResultsTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;
        Item<Integer> item;

        addScreenHandler(analyteTable, "analyteTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                analyteTable.setModel(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                analyteTable.setEnabled(true);
            }
        });

        analyteTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Integer selRow, selections[];
                Row row;

                selections = analyteTable.getSelectedRows();

                if ( !isState(ADD, UPDATE))
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
                selRow = event.getItem();

                if (selections.length > 0) {
                    row = analyteTable.getRowAt(selections[selections.length - 1]);
                    if ( ! (row.getData().equals(analyteTable.getRowAt(selRow).getData()))) {
                        Window.alert(Messages.get().headerCantSelWithAnalytes());
                        event.cancel();
                    }
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                    removeButton.setEnabled(true);
                }
            }
        });

        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Row row, val, prevVal;
                boolean cancel;

                r = event.getRow();
                c = event.getCol();
                anaSelCol = c;

                if ( !isState(ADD, UPDATE)) {
                    enableAnalyteWidgets(false);
                    event.cancel();
                } else {
                    enableAnalyteWidgets(true);
                }

                cancel = false;
                auto = (AutoComplete)analyteTable.getColumnAt(c).asWidget();

                try {
                    row = analyteTable.getRowAt(r);
                    if ((Boolean)row.getData()) {
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
                            val = row.getCell(c - 1);
                            //
                            // we don't want to allow editing of a header cell
                            // if the
                            // cell to its left doesn't have any data in it
                            //
                            if (val == null) {
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
                        // refreshAnalyteWidgets();
                        fireDataChange();
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
                                prevVal = row.getCell(c - 1);
                                //
                                // we disallow the editing of the first result
                                // group
                                // cell if there's no analyte selected in the
                                // first
                                // cell of the analyte row
                                //
                                if (prevVal == null) {
                                    event.cancel();
                                    cancel = true;
                                }
                            }
                            val = row.getCell(c);
                            if (val != null) {
                                //
                                // here we check to see if there was a result
                                // group
                                // selected and if there was we open the tab
                                // corresponding
                                // to it
                                if ( !DataBaseUtil.isEmpty(val.getCell(c).toString())) {
                                    resultTabPanel.selectTab(Integer.parseInt(val.getCell(c)
                                                                                 .toString()));
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
                        // refreshAnalyteWidgets();
                        fireDataChange();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (analyteTable.getSelectedRows().length > 1 && !cancel)
                    window.setStatus(Messages.get().multiSelRowEditCol() + c, "");
                else
                    window.clearStatus();
            }
        });

        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Integer i, r, c, numCol, dindex, rows[];
                String name;
                Row row, val;
                TestAnalyteViewDO data;
                AutoComplete auto;

                r = event.getRow();
                c = event.getCol();
                row = analyteTable.getRowAt(r);
                val = (Row)analyteTable.getValueAt(r, c);
                auto = (AutoComplete)analyteTable.getColumnAt(c).asWidget();
                rows = analyteTable.getSelectedRows();

                try {
                    if ((Boolean)row.getData()) {
                        numCol = displayManager.columnCount(r);
                        if (numCol < c) {
                            if (val.getCell(c) != null) {
                                //
                                // we need to add a new column to the data grid
                                // if
                                // this column in the table in a sub header was
                                // edited for the first time and the key set as
                                // its value is not null
                                //
                                dindex = displayManager.getDataRowIndex(r);
                                displayManager.setDataGrid(manager.analyte);
                            }
                        } else {
                            //
                            // we need to update all the cells in this column in
                            // the data grid if the column already exists in it
                            //
                            for (i = r + 1; i < analyteTable.getRowCount(); i++ ) {
                                if (displayManager.isHeaderRow(i))
                                    break;
                                data = displayManager.getObjectAt(i, c - 1);
                                data.setAnalyteId(key);
                                data.setAnalyteName(auto.getDisplay());
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
                            data.setAnalyteName(auto.getDisplay());
                            // ActionEvent.fire(screen, Action.ANALYTE_CHANGED,
                            // data);
                            fireDataChange();
                        } else {
                            //
                            // otherwise we need to set the key as the result
                            // group number in the DO
                            //
                            for (i = 0; i < rows.length; i++ ) {
                                data = displayManager.getObjectAt(rows[i], c - 1);
                                if (data == null)
                                    continue;
                                data.setResultGroup(val);
                                if (val == null)
                                    name = "";
                                else
                                    name = key.toString();
                                analyteTable.setValueAt(rows[i], c, new Row(val, val));
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
                Row row;
                int index;
                try {
                    row = event.getRow();
                    index = event.getIndex();

                    //
                    // if the table is empty and an analyte row is to be added
                    // then a header row should be added before that row
                    //
                    if ( !(Boolean)row.getData() && index == 0) {
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
                Row prow, row, nrow, addrow;
                int index, dindex;

                try {
                    row = event.getRow();
                    index = event.getIndex();

                    //
                    // if the row added to the table is an analyte row
                    //
                    if ( !(Boolean)row.getData()) {
                        //
                        // the row above the current one in the table
                        //
                        prow = analyteTable.getRowAt(index - 1);

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

                        if ((Boolean)prow.getData()) {
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
                            if (index + 1 < analyteTable.getRowCount()) {
                                nrow = analyteTable.getRowAt(index + 1);
                                if ( !(Boolean)nrow.getData()) {
                                    manager.analyte.add();
                                    // .addRowAt(dindex, false, false,
                                    // getNextTempId());
                                    displayManager.setDataGrid(manager.analyte);
                                    analyteTable.selectRowAt(index);
                                    return;
                                }
                            }
                            //
                            // otherwise the header row is for a new row group
                            // and thus the first boolean argument to addRowAt
                            // is true
                            //
                            // man.addRowAt(dindex + 1, true, false,
                            // getNextTempId());
                            manager.analyte.add();
                        } else {
                            //
                            // if prow is an analyte row then the newly added
                            // row has
                            // not been added to a new group and it will have to
                            // look at row previous to it in the data grid to
                            // copy
                            // data from look
                            //
                            // man.addRowAt(dindex + 1, false, true,
                            // getNextTempId());
                            manager.analyte.add();
                        }
                        displayManager.setDataGrid(manager.analyte);
                        analyteTable.selectRowAt(index);
                    } else if (addAnalyteRow) {
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added
                        // after it if the add button was clicked with "Header"
                        // selected
                        //
                        addrow = new Row(10);
                        addrow.setData(new Boolean(false));
                        if (index + 1 >= analyteTable.getRowCount()) {
                            analyteTable.addRow(addrow);
                        } else {
                            analyteTable.addRowAt(index + 1, addrow);
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
                Row row;
                TestAnalyteViewDO data;

                index = event.getIndex();
                row = event.getRow();

                if ( !(Boolean)row.getData()) {
                    dindex = displayManager.getDataRowIndex(index);
                    data = manager.analyte.get(dindex);
                    manager.analyte.remove(dindex);
                    displayManager.setDataGrid(manager.analyte);
                    // ActionEvent.fire(screen, Action.ANALYTE_DELETED, data);
                    fireDataChange();
                }
            }
        });

        addScreenHandler(analyteType, "analyteType", new ScreenHandler<Integer>() {
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
                        analyteType.setValue(data.getTypeId());
                    else
                        analyteType.setValue(null);

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
                    if (analyteType.getValue() != null)
                        analyteType.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO data;
                Row row, nrow;
                Integer i, j, index, r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRowAt(index);
                    if ((Boolean)row.getData()) {
                        for (i = 0; i < r.length; i++ ) {
                            index = r[i];
                            for (j = index; j < analyteTable.getRowCount(); j++ ) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if (data == null)
                                    continue;

                                data.setTypeId(event.getValue());
                                analyteTable.clearExceptions(j, anaSelCol);

                                if (j + 1 < analyteTable.getRowCount()) {
                                    nrow = analyteTable.getRowAt(j + 1);
                                    if ((Boolean)nrow.getData())
                                        j = analyteTable.getRowCount();
                                }
                            }
                        }
                    } else {
                        for (j = 0; j < r.length; j++ ) {
                            nrow = analyteTable.getRowAt(r[j]);
                            if ((Boolean)nrow.getData())
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setTypeId(event.getValue());
                            analyteTable.clearExceptions(r[j], anaSelCol);
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent event) {
                //
                // everytime the state of the screen changes, the model in the
                // analyte
                // table gets refreshed; thus there are no rows and columns
                // selected
                // at that point and hence this widget needs to be cleared of
                // any
                // previous selection and disabled
                //
                if (analyteType.getValue() != null)
                    analyteType.setValue(null);

                analyteType.setEnabled(false);
            }
        });

        addScreenHandler(isReportable, "isReportable", new ScreenHandler<String>() {
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
        });

        addScreenHandler(scriptlet, "scriptlet", new ScreenHandler<Integer>() {
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
                        scriptlet.setValue(data.getScriptletId());
                    else
                        scriptlet.setValue(null);
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
                    scriptlet.setValue(null);
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO data;
                Row row, nrow;
                Integer i, j, index, r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRowAt(index);
                    if ((Boolean)row.getData()) {
                        for (i = 0; i < r.length; i++ ) {
                            index = r[i];
                            for (j = index; j < analyteTable.getRowCount(); j++ ) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if (data == null)
                                    continue;

                                data.setScriptletId(event.getValue());

                                if (j + 1 < analyteTable.getRowCount()) {
                                    nrow = analyteTable.getRowAt(j + 1);
                                    if ((Boolean)nrow.getData())
                                        j = analyteTable.getRowCount();
                                }
                            }
                        }
                    } else {
                        for (j = 0; j < r.length; j++ ) {
                            nrow = analyteTable.getRowAt(r[j]);
                            if ((Boolean)nrow.getData())
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setScriptletId(event.getValue());
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent event) {
                //
                // everytime the state of the screen changes, the model in the
                // analyte
                // table gets refreshed; thus there are no rows and columns
                // selected
                // at that point and hence this widget needs to be cleared of
                // any
                // previous selection and disabled
                //
                scriptlet.setValue(null);
                scriptlet.setEnabled(false);
            }
        });

        addScreenHandler(resultTabPanel, "resultTabPanel", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                int numTabs;

                resultTabPanel.clearTabs();
                numTabs = manager.result.count();

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
                resultTable.setModel(getResultTableModel(tab));
                showErrorsForResultGroup(tab);
            }
        });
        
        addScreenHandler(resultTable, "resultTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                int selTab;

                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                if (!isState(QUERY)) {
                    selTab = resultTabPanel.getTabBar().getSelectedTab();
                    resultTable.setModel(getResultTableModel(selTab));
                } else {
                    resultTable.setModel(new ArrayList<Row>());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                resultTable.setEnabled(true);
            }
        });

//        resultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
//            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
//                int r, c, group;
//
//                if (state != State.ADD && state != State.UPDATE)
//                    event.cancel();
//
//                r = event.getRow();
//                c = event.getCol();
//                group = resultTabPanel.getTabBar().getSelectedTab();
//
//                switch (c) {
//                    case 0:
//                        clearResultCellError(group,
//                                             r,
//                                             TestMeta.getResultUnitOfMeasureId());
//                        break;
//                    case 1:
//                        clearResultCellError(group, r, TestMeta.getResultTypeId());
//                        break;
//                    case 2:
//                        clearResultCellError(group, r, TestMeta.getResultValue());
//                        break;
//                }
//            }
//
//        });
//
//        resultTable.addCellEditedHandler(new CellEditedHandler() {
//            public void onCellUpdated(CellEditedEvent event) {
//                int r, c, group;
//                TestResultViewDO data;
//                Object val;
//                TestResultManager man;
//
//                r = event.getRow();
//                c = event.getCol();
//                val = resultTable.getObject(r, c);
//                group = resultTabPanel.getTabBar().getSelectedTab();
//
//                man = null;
//                try {
//                    man = manager.getTestResults();
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                    e.printStackTrace();
//                }
//
//                data = man.getResultAt(group + 1, r);
//
//                switch (c) {
//                    case 0:
//                        if (val != null)
//                            data.setUnitOfMeasureId((Integer)val);
//                        else
//                            data.setUnitOfMeasureId(null);
//                        break;
//                    case 1:
//                        if (val != null)
//                            data.setTypeId((Integer)val);
//                        else
//                            data.setTypeId(null);
//
//                        resultTable.clearCellExceptions(r, 2);
//                        try {
//                            validateValue(data, (String)resultTable.getObject(r, 2));
//                        } catch (Exception e) {
//                            resultTable.setCellException(r, 2, e);
//                            addToResultErrorList(group,
//                                                 r,
//                                                 TestMeta.getResultValue(),
//                                                 e.getMessage());
//                        }
//                        break;
//                    case 2:
//                        resultTable.clearCellExceptions(r, c);
//                        try {
//                            validateValue(data, (String)val);
//                        } catch (Exception e) {
//                            resultTable.setCellException(r, c, e);
//                            addToResultErrorList(group,
//                                                 r,
//                                                 TestMeta.getResultValue(),
//                                                 e.getMessage());
//                        }
//                        ActionEvent.fire(screen, Action.RESULT_CHANGED, data);
//                        break;
//                    case 3:
//                        if (val != null)
//                            data.setFlagsId((Integer)val);
//                        else
//                            data.setFlagsId(null);
//                        break;
//                    case 4:
//                        if (val != null)
//                            data.setSignificantDigits((Integer)val);
//                        else
//                            data.setSignificantDigits(null);
//                        break;
//                    case 5:
//                        if (val != null)
//                            data.setRoundingMethodId((Integer)val);
//                        else
//                            data.setRoundingMethodId(null);
//                        break;
//                }
//            }
//        });
//
//        resultTable.addRowAddedHandler(new RowAddedHandler() {
//            public void onRowAdded(RowAddedEvent event) {
//                int selTab;
//                TestResultManager man;
//
//                man = null;
//                try {
//                    man = manager.getTestResults();
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                    e.printStackTrace();
//                }
//
//                if (man.groupCount() == 0)
//                    man.addResultGroup();
//
//                selTab = resultTabPanel.getTabBar().getSelectedTab();
//                man.addResult(selTab + 1, getNextTempId());
//            }
//        });
//
//        resultTable.addRowDeletedHandler(new RowDeletedHandler() {
//            public void onRowDeleted(RowDeletedEvent event) {
//                int r, selTab;
//                TestResultViewDO data;
//                TestResultManager man;
//
//                man = null;
//                try {
//                    man = manager.getTestResults();
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                    e.printStackTrace();
//                }
//
//                selTab = resultTabPanel.getTabBar().getSelectedTab();
//                r = event.getIndex();
//                data = man.getResultAt(selTab + 1, r);
//
//                man.removeResultAt(selTab + 1, r);
//
//                ActionEvent.fire(screen, Action.RESULT_DELETED, data);
//            }
//        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
            }
        });

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("test_analyte_type");
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }

        analyteType.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("scriptlet_test_analyte");
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }

        scriptlet.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("test_result_type");
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }

        resultType.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("test_result_flags");
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }

        resultFlag.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("rounding_method");
        for (DictionaryDO resultDO : list) {
            item = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            item.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(item);
        }

        roundingMethod.setModel(model);
        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>("analyte", Messages.get().analyte()));
        smodel.add(new Item<String>("column", Messages.get().column()));
        smodel.add(new Item<String>("header", Messages.get().header()));

        tableActions.setModel(smodel);
        tableActions.setSelectedIndex(0);

        setUnitsOfMeasure();
    }

    @UiHandler("addButton")
    protected void addOrganization(ClickEvent event) {
        analyteTable.finishEditing();
        if ("analyte".equals(tableActions.getValue())) {
            addAnalyte();
        } else if ("column".equals(tableActions.getValue())) {
            addColumn();
        } else {
            addHeader();
        }

        tableActions.setValue("analyte");
    }

    @UiHandler("removeButton")
    protected void removeOrganization(ClickEvent event) {
        analyteTable.finishEditing();
        if ("analyte".equals(tableActions.getValue())) {
            removeAnalyte();
        } else if ("column".equals(tableActions.getValue())) {
            removeColumn();
        } else {
            removeHeader();
        }

        tableActions.setValue("analyte");
    }

    private void setUnitsOfMeasure() {
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> dictList;
        TestTypeOfSampleDO data;
        String entry;
        Integer unitId;
        ArrayList<Integer> unitList;

        model = new ArrayList<Item<Integer>>();

        model.add(new Item<Integer>(null, ""));

        if (isState(ADD, UPDATE)) {
            unitList = new ArrayList<Integer>();

            for (int i = 0; i < manager.type.count(); i++ ) {
                data = manager.type.get(i);
                unitId = data.getUnitOfMeasureId();
                try {
                    if (unitId != null && !unitList.contains(unitId)) {
                        entry = DictionaryCache.getById(unitId).getEntry();
                        model.add(new Item<Integer>(unitId, entry));
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
                model.add(new Item<Integer>(dict.getId(), dict.getEntry()));
            }
        }
        unitOfMeasure.setModel(model);
    }

    private ArrayList<Row> getAnalyteTableModel() {
        boolean headerFilled;
        int m, c, len;
        Row row, hrow;
        TestAnalyteViewDO data;
        ArrayList<Row> model;
        // TODO
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        hrow = null;
        headerFilled = false;

        for (m = 0; m < manager.analyte.count(); m++ ) {
            if ("Y".equals(manager.analyte.get(m).getIsColumn())) {
                m++ ;
                hrow = createHeaderRow();
                model.add(hrow);
                headerFilled = false;
            }

            len = displayManager.columnCount(m);
            row = new Row(10);
            row.setData(new Boolean(false));
            for (c = 0; c < len; c++ ) {
                data = displayManager.getObjectAt(m, c);
                row.setData(data.getId());
                if (c == 0) {
                    row.setCell(0, new Item<Integer>(data.getAnalyteId(), data.getAnalyteName()));
                    row.setCell(1, new Item<Integer>(data.getResultGroup(),
                                                     String.valueOf(data.getResultGroup())));
                    continue;
                }

                if ( !headerFilled)
                    hrow.setCell(c + 1, new Item<Integer>(data.getAnalyteId(),
                                                          data.getAnalyteName()));

                row.setCell(c + 1, new Item<Integer>(data.getResultGroup(),
                                                     String.valueOf(data.getResultGroup())));
            }
            headerFilled = true;
            model.add(row);
        }
        return model;

    }

    private Row createHeaderRow() {
        Row row;

        row = new Row(10);
        row.setCell(0, new Item<Integer>( -1, Messages.get().analyte()));
        row.setCell(1, new Item<Integer>( -1, Messages.get().value()));
        // row.style = "SubHeader";
        row.setData(new Boolean(true));

        return row;
    }

    private ArrayList<Row> getResultTableModel(int group) {
        ArrayList<Row> model;
        Row row;
        TestResultViewDO data;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.result.count(); i++ ) {
                data = manager.result.get(i);
                row = new Row(6);
                row.setCell(0, data.getUnitOfMeasureId());
                row.setCell(1, data.getTypeId());
                if (data.getDictionary() == null)
                    row.setCell(2, data.getValue());
                else
                    row.setCell(2, data.getDictionary());
                row.setCell(3, data.getFlagsId());
                row.setCell(4, data.getSignificantDigits());
                row.setCell(5, data.getRoundingMethodId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private void enableAnalyteWidgets(boolean enable) {
        isReportable.setEnabled(enable);
        analyteType.setEnabled(enable);
        scriptlet.setEnabled(enable);
    }

    private void addAnalyte() {
        int r;
        Row row;

        r = analyteTable.getSelectedRow();
        row = new Row(10);
        row.setData(new Boolean(false));
        if (r == -1 || r == analyteTable.getRowCount() - 1) {
            analyteTable.addRow(row);
            analyteTable.selectRowAt(analyteTable.getRowCount() - 1);
            analyteTable.startEditing(analyteTable.getRowCount() - 1, 0);
        } else {
            analyteTable.addRowAt(r + 1, row);
            analyteTable.selectRowAt(r + 1);
            analyteTable.startEditing(r + 1, 0);
        }
    }

    private void removeAnalyte() {
        int r;
        Row row, nrow, prow;

        r = analyteTable.getSelectedRow();
        if (r != -1) {
            row = analyteTable.getRowAt(r);
            if ( !(Boolean)row.getData()) {
                //
                // we want to make sure that there are at least 2 analyte rows
                // in the row group that this row belongs to, if that's not the
                // case we don't allow this row to be deleted
                //
                prow = analyteTable.getRowAt(r - 1);
                if ((Boolean)prow.getData()) {
                    //
                    // if the row above the current one is a header row then the
                    // row next to the current one should be an analyte row in
                    // order to be able to delete the current row; the current
                    // row can also not be deleted in the case that it is the
                    // last row in the table
                    //
                    if (r + 1 < analyteTable.getRowCount()) {
                        nrow = analyteTable.getRowAt(r + 1);
                        if ( !(Boolean)nrow.getData())
                            analyteTable.removeRowAt(r);
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
                    analyteTable.removeRowAt(r);
                }

            }
        }
    }

    private void addColumn() {
        int r, index;

        if ( !canAddRemoveColumn) {
            Window.alert(Messages.get().cantAddColumn());
            return;
        }

        r = analyteTable.getSelectedRow();
        try {
            if (anaSelCol != -1 && r != -1) {
                index = displayManager.getDataRowIndex(r);
                manager.analyte.add(null);
                displayManager.setDataGrid(manager.analyte);

                addColumnToRow(r);
                addColumnToRowsBelow(r);

                // analyteTable.refresh();
                anaSelCol = -1;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeColumn() {
        int r, index;

        if ( !canAddRemoveColumn) {
            Window.alert(Messages.get().cantRemoveColumn());
            return;
        }

        r = analyteTable.getSelectedRow();
        try {
            if (anaSelCol != -1 && r != -1) {
                removeColumnFromRow(r);
                removeColumnFromRowsBelow(r);

                // analyteTable.refresh();

                index = displayManager.getDataRowIndex(r);
                // man.removeColumnAt(index, anaSelCol - 1);
                displayManager.setDataGrid(manager.analyte);

                anaSelCol = -1;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addColumnToRowsBelow(int row) {
        Row trow;

        for (int i = row + 1; i < analyteTable.getRowCount(); i++ ) {
            trow = analyteTable.getRowAt(i);
            if ( !(Boolean)trow.getData())
                addColumnToRow(i);
            else
                break;
        }
    }

    private void addColumnToRow(int row) {
        int finCol;
        Row trow, lrow;
        String name;

        if (anaSelCol < 2 || anaSelCol == 10 || row < 0)
            return;

        trow = analyteTable.getRowAt(row);
        finCol = displayManager.columnCount(row);

        if (finCol == 10)
            return;

        name = "";
        for (int i = finCol; i > anaSelCol; i-- ) {
            lrow = (Row)trow.getCell(i - 1);
            if (lrow != null) {
                name = (String)lrow.getCell(0);
            }
            trow.setCell(i, new Row(name));
            trow.setCell(i - 1, new Row(""));
        }
    }

    private void removeColumnFromRow(int r) {
        int finalIndex;
        String name;
        Row row, rrow, blankrow;

        if (anaSelCol < 2 || r < 0)
            return;

        row = analyteTable.getRowAt(r);
        finalIndex = displayManager.columnCount(r) + 1;
        blankrow = new Row(null, "");

        if (finalIndex == 2)
            return;

        if (anaSelCol == 9) {
            row.setCell(anaSelCol, blankrow);
            analyteTable.clearExceptions(row, anaSelCol);
        } else {
            for (int i = anaSelCol; i < finalIndex; i++ ) {
                analyteTable.clearExceptions(row, i);
                if (i == finalIndex - 1) {
                    row.setCell(i, blankrow);
                } else {
                    if (analyteTable.getEndUserExceptions(i + 1, anaSelCol) != null &&
                        analyteTable.getEndUserExceptions(i + 1, anaSelCol).size() > 0) {
                        for (Exception ex : analyteTable.getEndUserExceptions(i + 1, anaSelCol))
                            analyteTable.addException(i, anaSelCol, ex);
                    }
                    rrow = row.getCell(i + 1);
                    if (rrow == null) {
                        row.setCell(i, blankrow);
                        continue;
                    }
                    // key = (Integer)rrow.key;
                    name = (String)rrow.getCell(0);

                    // if (key != null)
                    // row.setCell(i, new Row(name));
                    // else
                    // row.setCell(i, blankrow);
                }
            }
        }
    }

    private void removeColumnFromRowsBelow(int r) {
        Row row;

        for (int i = r + 1; i < analyteTable.getRowCount(); i++ ) {
            row = analyteTable.getRowAt(i);
            if ( !(Boolean)row.getData())
                removeColumnFromRow(i);
            else
                break;
        }
    }

    private void addHeader() {
        int r, num;
        Row row;

        r = analyteTable.getSelectedRow();
        num = analyteTable.getRowCount();

        if (r == -1 || r == num - 1) {
            analyteTable.addRow(createHeaderRow());
//            analyteTable.scrollToSelection();
        } else {
            row = analyteTable.getRowAt(r);
            if ((Boolean)row.getData()) {
                headerAddedInTheMiddle = true;
                analyteTable.addRowAt(r, createHeaderRow());
//                analyteTable.scrollToSelection();
            } else {
                row = analyteTable.getRowAt(r + 1);
                if ((Boolean)row.getData()) {
                    headerAddedInTheMiddle = true;
                    analyteTable.addRowAt(r + 1, createHeaderRow());
//                    analyteTable.scrollToSelection();
                } else {
                    Window.alert(Messages.get().headerCantBeAddedInsideGroup());
                }
            }
        }
    }

    private void removeHeader() {
        int r;
        Row row;

        r = analyteTable.getSelectedRow();

        if (r != -1) {
            row = analyteTable.getRowAt(r);
            if ((Boolean)row.getData()) {
                analyteTable.removeRowAt(r);
                while (r < analyteTable.getRowCount()) {
                    row = analyteTable.getRowAt(r);
                    if ((Boolean)row.getData())
                        break;

                    analyteTable.removeRowAt(r);
                }
            }
        }
    }

    private void showErrorsForResultGroup(int group) {
        GridFieldErrorException error;
        int i, col;
        ArrayList<GridFieldErrorException> list;
        Row row;

        if (resultErrorList == null)
            return;

        for (i = 0; i < resultErrorList.size(); i++ ) {
            error = resultErrorList.get(i);
            col = error.getColumnIndex();
            row = resultTable.getRowAt(col);
            if (error.getRowIndex() == group) {
                if (row.getData() == null)
                    row.setData(new ArrayList<GridFieldErrorException>());

                list = (ArrayList<GridFieldErrorException>)row.getData();

                if ( !list.contains(error)) {
                    // resultTable.setException(col, error.getFieldName(),
                    // error);
                    resultTable.addException(row, col, error);
                    list.add(error);
                }
            }
        }
    }
}
