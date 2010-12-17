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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScrollableTabBar;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Column;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;


public class AnalyteAndResultTab extends Screen implements GetMatchesHandler,
                                               HasActionHandlers<AnalyteAndResultTab.Action>,
                                               ActionHandler<SampleTypeTab.Action> {
    public enum Action {
        ANALYTE_CHANGED, ANALYTE_DELETED, RESULT_CHANGED, RESULT_DELETED
    };

    private TestManager                                  manager;
    
    private AnalyteAndResultTab                          screen;
    private TestAnalyteDisplayManager<TestAnalyteViewDO> displayManager;

    private Integer                                      typeDict, typeNumeric, typeTiter,
                                                         typeDefault, typeDate, typeDateTime,
                                                         typeTime, typeAlphaLower, typeAlphaUpper,
                                                         typeAlphaMixed ;

    private DictionaryLookupScreen                       dictLookup;

    private Table                                        analyteTable, resultTable;
    private Dropdown<Integer>                            typeId;
    private Dropdown<String>                             tableActions;
    private ScrollableTabBar                             resultTabPanel;
    private CheckBox                                     isReportable;
    private AutoComplete                                 scriptlet;
    private Button                                       addButton, removeButton,
                                                         addResultTabButton, dictionaryLookUpButton, addTestResultButton,
                                                         removeTestResultButton;

    private ArrayList<GridFieldErrorException>           resultErrorList;

    private boolean                                      addAnalyteRow, loaded,
                                                         headerAddedInTheMiddle, canAddRemoveColumn;

    private int                                          anaSelCol, tempId;

    private ScreenService                                scriptletService, analyteService,
                                                         dictionaryService;

    private ResultRangeNumeric                           rangeNumeric;
    private ResultRangeTiter                             rangeTiter;

    public AnalyteAndResultTab(ScreenDefInt def, Window window, ScreenService service,
                               ScreenService scriptletService, ScreenService analyteService,
                               ScreenService dictionaryService) {
        setDefinition(def);
        setWindow(window);
        this.service = service;
        this.scriptletService = scriptletService;
        this.analyteService = analyteService;
        this.dictionaryService = dictionaryService;

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        analyteTable = (Table)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                analyteTable.setModel(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteTable.setEnabled(true);
            }
        });

        rangeNumeric = new ResultRangeNumeric();
        rangeTiter = new ResultRangeTiter();
        
        analyteTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
            	Integer[] sels;
                ArrayList<Row> selections;
                Row row,selRow;             
                
                sels = analyteTable.getSelectedRows();
                
                selections = new ArrayList<Row>();
                
                for(int i = 0; i < sels.length; i++)
                	selections.add(analyteTable.getRowAt(sels[i]));
                
                if(state != State.ADD  && state != State.UPDATE) 
                    return;
                
                //
                // since this table supports multiple selection, we want to make
                // sure that if the first row selected is an analyte row  then
                // all the subsequently selected rows are analyte rows and if the
                // first row selected is a header row then all the
                // subsequently selected rows are header rows, so through this
                // code we prevent users from selecting the other kind of row 
                //                
                selRow = analyteTable.getRowAt(event.getItem());               
                                                
                if(selections.size() > 0) {
                    row = selections.get(selections.size()-1);
                    if(!(row.getData().equals(selRow.getData()))) {                                    
                        com.google.gwt.user.client.Window.alert(consts.get("headerCantSelWithAnalytes"));
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
                Integer key;
                Row row; 
                Object val, prevVal;
                AutoComplete auto;
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
                auto = (AutoComplete)analyteTable.getColumnAt(c).getCellEditor().getWidget();

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
                            val = (Row)row.getCell(c - 1);
                            //
                            // we don't want to allow editing of a header cell
                            // if the
                            // cell to its left doesn't have any data in it
                            //
                            if (val == null || ((AutoCompleteValue)val).getId() == null) {
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
                            // that's next to last cell that has data in it but in the
                            // case of a non-header row, i.e. here, we don't;
                            // only cells under those header cells that have
                            // data in them can be edited; in addition to this, we
                            // disable the three widgets and disallow adding or removing
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
                                if (prevVal == null || ((AutoCompleteValue)prevVal).getId() == null) {
                                    event.cancel();
                                    cancel = true;
                                }
                            }
                            val = row.getCell(c);
                            if (val != null && ((AutoCompleteValue)val).getId() != null) {
                                //
                                // here we check to see if there was a result
                                // group
                                // selected and if there was we open the tab
                                // corresponding
                                // to it
                                key = ((AutoCompleteValue)val).getId();
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

                if (analyteTable.getSelectedRows().length > 1 && !cancel)
                    window.setStatus(consts.get("multiSelRowEditCol") + c, "");
                else
                    window.clearStatus();
            }

        });

        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int i,r, c, numCol, dindex;
                Integer rows[];
                Row row;
                AutoCompleteValue val;
                TestAnalyteViewDO data;
                Integer key;
                AutoComplete auto;
                TestAnalyteManager man;

                key = null;
                man = null;
                r = event.getRow();
                c = event.getCol();
                row = analyteTable.getRowAt(r);
                val = (AutoCompleteValue)analyteTable.getValueAt(r, c);
                if(val != null)
                    key = val.getId();
                auto = (AutoComplete)analyteTable.getColumnAt(c).getCellEditor().getWidget();
                rows = analyteTable.getSelectedRows();
                
                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
                try {
                    if ((Boolean)row.getData()) {                        
                        numCol = displayManager.columnCount(r);
                        if (numCol < c) {
                            if (key != null) {
                                //
                                // we need to add a new column to the data grid if
                                // this column in the table in a sub header was
                                // edited for the first time and the key set as
                                // its value is not null
                                //
                                dindex = displayManager.getDataRowIndex(r);
                                man.addColumnAt(dindex, c - 1, key,
                                                               auto.getDisplay());
                                displayManager.setDataGrid(man.getAnalytes());
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
                            ActionEvent.fire(screen, Action.ANALYTE_CHANGED, data);
                        } else {
                            //
                            // otherwise we need to set the key as the result
                            // group number in the DO
                            //
                            for(i = 0; i < rows.length; i++) {
                                data = displayManager.getObjectAt(rows[i], c - 1);
                                if(data == null)
                                    continue;
                                data.setResultGroup(key);
                                analyteTable.setValueAt(rows[i], c, new Row(key, key.toString()));
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
                TestAnalyteManager man;

                man = null;
                
                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
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
                        // number of rows in the table is more than one (index > 0),
                        // we try to find dindex for the last row in displayManager's
                        // index list; if prow is a header and has been added in
                        // the middle of the table, (headerAddedInTheMiddle == true),
                        // i.e. not at the end, then this means that the list
                        // corresponding to the row above this one which is the
                        // header row exists neither in TestAnalyteManager nor in
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
                                    man.addRowAt(dindex, false, false,
                                                                getNextTempId());
                                    displayManager.setDataGrid(man.getAnalytes());
                                    analyteTable.selectRowAt(index);
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
                            // if prow is an analyte row then the newly added row has
                            // not been added to a new group and it will have to
                            // look at row previous to it in the data grid to copy 
                            // data from look
                            //
                            man.addRowAt(dindex + 1, false, true, getNextTempId());
                        }
                        displayManager.setDataGrid(man.getAnalytes());
                        analyteTable.selectRowAt(index);
                    } else if (addAnalyteRow) {
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added
                        // after it if the add button was clicked with "Header" selected
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
                TestAnalyteManager man;

                index = event.getIndex();
                row = event.getRow();
                man = null;

                try {
                    man = manager.getTestAnalytes();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
                if ( !(Boolean)row.getData()) {
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
                            typeId.setValue(data.getTypeId());
                        else
                            typeId.setValue(null);                        

                } else {
                    //
                    // everytime the data on the screen changes, the model in the analyte
                    // table gets refreshed; thus there are no rows and columns selected 
                    // at that point and hence this widget needs to be cleared of any 
                    // previous selection
                    //
                    if(typeId.getModel() != null)
                        typeId.setValue(null);   
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {                
                TestAnalyteViewDO data;
                Row row, nrow;
                int i,j,index;
                Integer r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {   
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRowAt(index);                    
                    if ((Boolean)row.getData()) {
                        for (i = 0; i < r.length; i++) {
                            index = r[i];
                            for (j = index; j < analyteTable.getRowCount(); j++) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if(data == null)
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
                        for(j = 0; j < r.length; j++) {
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

            public void onStateChange(StateChangeEvent<State> event) {                
                //
                // everytime the state of the screen changes, the model in the analyte
                // table gets refreshed; thus there are no rows and columns selected 
                // at that point and hence this widget needs to be cleared of any 
                // previous selection and disabled
                //
                if(typeId.getModel() != null)
                    typeId.setValue(null);
                
                typeId.setEnabled(false);
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
                    // everytime the data on the screen changes, the model in the analyte
                    // table gets refreshed; thus there are no rows and columns selected 
                    // at that point and hence this widget needs to be cleared of any 
                    // previous affirmative choices made
                    //
                    isReportable.setValue("N");    
                }

            }

            public void onValueChange(ValueChangeEvent<String> event) {                
                TestAnalyteViewDO data;
                Row row, nrow;
                int i,j,index;
                Integer r[];

                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) {     
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRowAt(index);                    
                    if ((Boolean)row.getData()) {
                        for (i = 0; i < r.length; i++) {
                            index = r[i];
                            for (j = index; j < analyteTable.getRowCount(); j++) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if(data == null)
                                    continue;
                                
                                data.setIsReportable(event.getValue());
                                
                                if (j + 1 < analyteTable.getRowCount()) {
                                    nrow = analyteTable.getRowAt(j + 1);
                                    if ((Boolean)nrow.getData())
                                        j = analyteTable.getRowCount();
                                }
                            }
                        }
                    } else {
                        for(j = 0; j < r.length; j++) {
                            nrow = analyteTable.getRowAt(r[j]);
                            if ((Boolean)nrow.getData())
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setIsReportable(event.getValue());
                        }                        
                    }                   
                } 
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                //
                // everytime state of the screen changes, the model in the analyte
                // table gets refreshed; thus there are no rows and columns selected 
                // at that point and hence this widget needs to be cleared of any 
                // previous affirmative choice made and disabled
                //
                isReportable.setValue("N");                
                isReportable.setEnabled(false);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(TestMeta.getAnalyteScriptletId());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
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
                        scriptlet.setValue(data.getScriptletId(), data.getScriptletName());
                    else
                        scriptlet.setValue(null, "");
                } else {
                    //
                    // everytime the data on the screen changes, the model in the analyte
                    // table gets refreshed; thus there are no rows and columns selected 
                    // at that point and hence this widget needs to be cleared of any 
                    // previous selection and disabled
                    //
                    scriptlet.setValue(null, "");
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {   
                TestAnalyteViewDO data;
                Row row, nrow;
                int i,j,index;
                Integer r[];

                
                r = analyteTable.getSelectedRows();
                if (r != null && anaSelCol != -1) { 
                    analyteTable.finishEditing();
                    index = r[0];
                    row = analyteTable.getRowAt(index);                    
                    if ((Boolean)row.getData()) {
                        for (i = 0; i < r.length; i++) {
                            index = r[i];
                            for (j = index; j < analyteTable.getRowCount(); j++) {
                                data = displayManager.getObjectAt(j, anaSelCol - 1);
                                if(data == null)
                                    continue;
                                
                                data.setScriptletId(event.getValue());
                                data.setScriptletName(scriptlet.getDisplay());
                                
                                if (j + 1 < analyteTable.getRowCount()) {
                                    nrow = analyteTable.getRowAt(j + 1);
                                    if ((Boolean)nrow.getData())
                                        j = analyteTable.getRowCount();
                                }
                            }
                        }
                    } else {
                        for(j = 0; j < r.length; j++) {
                            nrow = analyteTable.getRowAt(r[j]);
                            if ((Boolean)nrow.getData())
                                break;
                            data = displayManager.getObjectAt(r[j], anaSelCol);
                            data.setScriptletId(event.getValue());
                            data.setScriptletName(scriptlet.getDisplay());
                        }                        
                    }                   
                } 
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                //
                // everytime the state of the screen changes, the model in the analyte
                // table gets refreshed; thus there are no rows and columns selected 
                // at that point and hence this widget needs to be cleared of any 
                // previous selection and disabled
                //
                scriptlet.setValue(null, "");
                scriptlet.setEnabled(false);
            }
        });

        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<IdNameVO> list;

                try {
                    list = scriptletService.callList("fetchByName", event.getMatch() + "%");
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO data : list)
                        model.add(new Item<Integer>(data.getId(), data.getName()));

                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        tableActions = (Dropdown<String>)def.getWidget("tableActions");
        addScreenHandler(tableActions, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tableActions.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                tableActions.setQueryMode(false);
                if (tableActions.getModel() != null &&
                    EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()))
                    tableActions.setValue("analyte");
            }
        });

        addButton = (Button)def.getWidget("addButton");
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

                tableActions.setValue("analyte");

            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeButton = (Button)def.getWidget("removeButton");
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

                tableActions.setValue("analyte");
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
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
                resultTable.setModel(getResultTableModel(tab));
                showErrorsForResultGroup(tab);
            }
        });

        resultTable = (Table)def.getWidget("resultTable");
        addScreenHandler(resultTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                int selTab;

                //
                // this table is not queried by,so it needs to be cleared in
                // query mode
                //
                if (state != State.QUERY) {
                    selTab = resultTabPanel.getTabBar().getSelectedTab();
                    resultTable.setModel(getResultTableModel(selTab));
                } else {
                    resultTable.setModel(new ArrayList<Row>());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resultTable.setEnabled(true);
            }
        });

        resultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c, group;

                if(state != State.ADD  && state != State.UPDATE) 
                    event.cancel();
                
                r = event.getRow();
                c = event.getCol();
                group = resultTabPanel.getTabBar().getSelectedTab();

                switch (c) {
                    case 0:
                        clearResultCellError(group, r, TestMeta.getResultUnitOfMeasureId());
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
                val = resultTable.getValueAt(r, c);
                group = resultTabPanel.getTabBar().getSelectedTab();
                
                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
                data = man.getResultAt(group + 1, r);

                switch (c) {
                    case 0:
                        if(val != null) 
                            data.setUnitOfMeasureId((Integer)val);
                        else
                            data.setUnitOfMeasureId(null);
                        break;
                    case 1:
                        if(val != null) 
                            data.setTypeId((Integer)val);
                        else 
                            data.setTypeId(null);
                        
                        resultTable.clearExceptions(r, 2);
                        try {
                            validateValue(data, (String)resultTable.getValueAt(r, 2));
                        } catch (LocalizedException e) {
                            resultTable.addException(r, 2, e);
                            addToResultErrorList(group, r, TestMeta.getResultValue(),
                                                 e.getMessage());
                        }                        
                        break;
                    case 2:
                        resultTable.clearExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (LocalizedException e) {
                            resultTable.addException(r, c, e);
                            addToResultErrorList(group, r, TestMeta.getResultValue(),
                                                 e.getMessage());
                        }
                        ActionEvent.fire(screen, Action.RESULT_CHANGED, data);
                        break;
                    case 3:
                        if(val != null) 
                            data.setFlagsId((Integer)val);
                        else
                            data.setFlagsId(null);
                        break;
                    case 4:
                        if(val != null) 
                            data.setSignificantDigits((Integer)val);
                        else
                            data.setSignificantDigits(null);
                        break;
                    case 5:
                        if(val != null) 
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                selTab = resultTabPanel.getTabBar().getSelectedTab();
                r = event.getIndex();
                data = man.getResultAt(selTab + 1, r);

                man.removeResultAt(selTab + 1, r);

                ActionEvent.fire(screen, Action.RESULT_DELETED, data);
            }
        });

        addResultTabButton = (Button)def.getWidget("addResultTabButton");
        addScreenHandler(addResultTabButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int count;
                TestResultManager man;
                
                man = null;
                try {
                    man = manager.getTestResults();
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                
                count = resultTabPanel.getTabBar().getTabCount();

                resultTabPanel.addTab(String.valueOf(count + 1));
                resultTabPanel.selectTab(count);
                man.addResultGroup();

            }

            public void onStateChange(StateChangeEvent<State> event) {
                addResultTabButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeTestResultButton = (Button)def.getWidget("removeTestResultButton");
        addScreenHandler(removeTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = resultTable.getSelectedRow();

                if (r != -1 && resultTable.getRowCount() > 0)
                    resultTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestResultButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });

        addTestResultButton = (Button)def.getWidget("addTestResultButton");
        addScreenHandler(addTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n, numTabs;

                numTabs = resultTabPanel.getTabBar().getTabCount();

                if (numTabs == 0) {
                    resultTabPanel.addTab(String.valueOf(1));
                    resultTabPanel.selectTab(0);
                }

                resultTable.addRow();
                n = resultTable.getRowCount() - 1;
                resultTable.selectRowAt(n);
                resultTable.scrollToVisible(n);
                resultTable.startEditing(n, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestResultButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        dictionaryLookUpButton = (Button)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                resultTable.finishEditing();
                showDictionary(null, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryLookUpButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
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
        QueryFieldUtil parser;
        ArrayList<Item<Integer>> model;
        int rg;
        String match;
        ArrayList<AnalyteDO> list;
        AutoComplete auto;

        parser = new QueryFieldUtil();
        try {
        	parser.parse(event.getMatch());
        }catch(Exception e) {
        	
        }

        try {
            auto = ((AutoComplete)event.getSource());
            
            if (isAnalyteQuery()) {
                list = analyteService.callList("fetchByName", parser.getParameter().get(0));
                model = new ArrayList<Item<Integer>>();
                for (AnalyteDO data : list)
                    model.add(new Item<Integer>(data.getId(), data.getName()));
            } else {
                auto.setDelay(1);
                model = new ArrayList<Item<Integer>>();
                match = event.getMatch();
                try {
                    rg = Integer.parseInt(match);
                    if (rg > 0 && rg <= resultTabPanel.getTabBar().getTabCount()) {
                        model.add(new Item<Integer>(rg, match));
                    } else {
                        model.add(new Item<Integer>(null, ""));
                    }
                } catch (NumberFormatException e) {
                    model.add(new Item<Integer>(null, ""));
                }                
            }
            auto.showAutoMatches(model);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }

    public void showTestAnalyteError(GridFieldErrorException error) {
        int dindex, trindex, col, i;
        String field;
        Row row;
        ArrayList<LocalizedException> errors;

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
            errors = analyteTable.getEndUserExceptions(trindex, col + 1);
            if (errors == null || !errors.contains(error))
                analyteTable.addException(trindex, col + 1, error);
        } else if (col == 0) {
            errors = analyteTable.getEndUserExceptions(trindex, col);
            if (errors == null || !errors.contains(error))
                analyteTable.addException(trindex, col, error);
        } else {
            for (i = trindex - 1; i > -1; i-- ) {
                row = analyteTable.getRowAt(i);
                errors = analyteTable.getEndUserExceptions(i, col + 1);
                if ((Boolean)row.getData()) {
                    if ( (errors == null || !errors.contains(error))) {
                        analyteTable.addException(i, col + 1, error);
                    }
                    break;
                }
            }
        }
    }

    public void showTestResultError(GridFieldErrorException error) {
        int tab;
        tab = resultTabPanel.getTabBar().getSelectedTab();
        if(!errorExistsInList(error.getRowIndex(),error.getColumnIndex(),error.getFieldName(),error.getKey())) {
            if(resultErrorList == null)
                resultErrorList = new ArrayList<GridFieldErrorException>();
            resultErrorList.add(error);
        }
        if(error.getRowIndex() == tab)
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

    private ArrayList<Row> getAnalyteTableModel() {
        int m, c, len;
        ArrayList<Row> model;
        Row hrow, row;
        TestAnalyteViewDO data;
        boolean headerFilled;

        model = new ArrayList<Row>();
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
            row = new Row(10);
            row.setData(new Boolean(false));
            for (c = 0; c < len; c++ ) {
                data = displayManager.getObjectAt(m, c);
                //row.setey = data.getId();
                if (c == 0) {
                    row.setCell(0,new AutoCompleteValue(data.getAnalyteId(),
                                                               data.getAnalyteName()));
                    row.setCell(1,new AutoCompleteValue(data.getResultGroup(),
                                                        String.valueOf(data.getResultGroup())));
                    continue;
                }

                if ( !headerFilled)
                    hrow.setCell(c + 1,new AutoCompleteValue(data.getAnalyteId(),
                                                                    data.getAnalyteName()));

                row.setCell(c + 1,new AutoCompleteValue(data.getResultGroup(),
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
        field = new QueryData(CategoryMeta.getDictionaryEntry(),QueryData.Type.STRING,entry);
        fields.add(field);

        field = new QueryData(CategoryMeta.getIsSystem(),QueryData.Type.STRING,"N");
        fields.add(field);

        query.setFields(fields);

        try {
            list = dictionaryService.callList("fetchByEntry", query);
            if (list.size() == 1)
                return list.get(0);
            else if (list.size() > 1)
                showDictionary(entry, list);
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        return null;
    }

    private void validateValue(TestResultViewDO data, String value) throws LocalizedException {
        IdNameVO dict;
        
        if(value == null)
            return;
        
        try {
            if (typeDict.equals(data.getTypeId())) {
                dict = getDictionary((String)value);
                if (dict != null) {
                    data.setValue(dict.getId().toString());
                    data.setDictionary(dict.getName());
                } else {
                    data.setDictionary(null);
                    throw new LocalizedException("test.invalidValue");
                }
            } else if (typeNumeric.equals(data.getTypeId())) {
                rangeNumeric.setRange((String)value);
                data.setValue(rangeNumeric.toString());
            } else if (typeTiter.equals(data.getTypeId())) {
                rangeTiter.setRange((String)value);
                data.setValue(rangeTiter.toString());
            } else if (typeDefault.equals(data.getTypeId()) || typeDate.equals(data.getTypeId()) ||
                       typeDateTime.equals(data.getTypeId()) || typeTime.equals(data.getTypeId()) ||
                       typeAlphaLower.equals(data.getTypeId()) || typeAlphaUpper.equals(data.getTypeId()) || 
                       typeAlphaMixed.equals(data.getTypeId())) {
                data.setValue((String)value);
            } else {
                throw new LocalizedException("test.invalidValue");
            }
        } catch (LocalizedException e) {
            data.setValue(null);
            data.setDictionary(null);
            throw e;
        }
    }

    private Row createHeaderRow() {
        SubHeaderRow row;
        
        row = new SubHeaderRow(10);
        row.setCell(0,new AutoCompleteValue( -1, consts.get("analyte")));

        row.setCell(1,new AutoCompleteValue( -1, consts.get("value")));
        row.setData(new Boolean(true));
        return row;
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;
        List<DictionaryDO> list;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("test_analyte_type");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("test_result_type");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnAt(resultTable.getColumnByName(TestMeta.getResultTypeId())).getCellEditor().getWidget()).setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("test_result_flags");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnAt(resultTable.getColumnByName(TestMeta.getResultFlagsId())).getCellEditor().getWidget()).setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("rounding_method");
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)resultTable.getColumnAt(resultTable.getColumnByName(TestMeta.getResultRoundingMethodId())).getCellEditor().getWidget()).setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>("analyte", consts.get("analyte")));
        smodel.add(new Item<String>("column", consts.get("column")));
        smodel.add(new Item<String>("header", consts.get("header")));

        tableActions.setModel(smodel);
        tableActions.setValue("analyte");

        setUnitsOfMeasure();

        try {
            typeDict = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
            typeNumeric = DictionaryCache.getIdFromSystemName("test_res_type_numeric");
            typeTiter = DictionaryCache.getIdFromSystemName("test_res_type_titer");
            typeDefault = DictionaryCache.getIdFromSystemName("test_res_type_default");
            typeDate = DictionaryCache.getIdFromSystemName("test_res_type_date");
            typeDateTime = DictionaryCache.getIdFromSystemName("test_res_type_date_time");
            typeTime = DictionaryCache.getIdFromSystemName("test_res_type_time");
            typeAlphaLower = DictionaryCache.getIdFromSystemName("test_res_type_alpha_lower"); 
            typeAlphaUpper = DictionaryCache.getIdFromSystemName("test_res_type_alpha_upper");
            typeAlphaMixed = DictionaryCache.getIdFromSystemName("test_res_type_alpha_mixed");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    private void setUnitsOfMeasure() {
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> dictList;
        TestTypeOfSampleDO data;
        String entry;
        Integer unitId;
        ArrayList<Integer> unitList;
        TestTypeOfSampleManager man;

        model = new ArrayList<Item<Integer>>();

        model.add(new Item<Integer>(null, ""));

        if (state == State.ADD || state == State.UPDATE) {
            unitList = new ArrayList<Integer>();
            man = null;
            try {
                man = manager.getSampleTypes();
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                e.printStackTrace();
            }
            
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getTypeAt(i);
                unitId = data.getUnitOfMeasureId();
                try {
                    if (unitId != null && !unitList.contains(unitId)) {
                        entry = DictionaryCache.getEntryFromId(unitId).getEntry();
                        model.add(new Item<Integer>(unitId, entry));
                        unitList.add(unitId);
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            dictList = DictionaryCache.getListByCategorySystemName("unit_of_measure");
            for (DictionaryDO dict : dictList) {
                model.add(new Item<Integer>(dict.getId(), dict.getEntry()));
            }
        }
        ((Dropdown<Integer>)resultTable.getColumnAt(resultTable.getColumnByName(TestMeta.getResultUnitOfMeasureId())).getCellEditor().getWidget()).setModel(model);
    }

    private boolean isAnalyteQuery() {
        Row row;
        int ac;
        row = analyteTable.getRowAt(analyteTable.getSelectedRow());
        ac = analyteTable.getEditingCol();
        if ( ((Boolean)row.getData() && ac > 1) || ( !(Boolean)row.getData() && ac == 0))
            return true;

        return false;
    }

    /**
     * This method adds a BeforeGetMatchesHandler and a GetMatchesHandler to all
     * the cells in the table showing test analytes. This is done in a method like
     * this and not through the standard way of adding these handlers to individual
     * cells because the code for the handlers for multiple cells is the same and
     * very similar across all the cells in the table   
     */
    private void addMatchesHandlerToAnalyteCells() {
        AutoComplete auto;
        ArrayList<Column> columns;

        //columns = analyteTable.getColumn();
        for (int i = 0; i < analyteTable.getColumnCount(); i++ ) {
            auto = (AutoComplete)analyteTable.getColumnAt(i).getCellEditor().getWidget();
            //auto.addBeforeGetMatchesHandler(this);
            auto.addGetMatchesHandler(this);
        }
    }

    private void refreshAnalyteWidgets() {
        DataChangeEvent.fire(screen, isReportable);
        DataChangeEvent.fire(screen, typeId);
        DataChangeEvent.fire(screen, scriptlet);
    }

    private void enableAnalyteWidgets(boolean enable) {
        isReportable.setEnabled(enable);
        typeId.setEnabled(enable);
        scriptlet.setEnabled(enable);
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
        Row trow;
        AutoCompleteValue lrow;
        String name;
        Integer key;

        if (anaSelCol < 2 || anaSelCol == 10 || row < 0)
            return;

        trow = analyteTable.getRowAt(row);
        finCol = displayManager.columnCount(row);

        if (finCol == 10)
            return;

        key = null;
        name = "";
        try {
            for (int i = finCol; i > anaSelCol; i-- ) {
                lrow = (AutoCompleteValue)trow.getCell(i - 1);
                if (lrow != null) {
                    key = (Integer)lrow.getId();
                    name = (String)lrow.getDisplay();
                }
                trow.setCell(i,new AutoCompleteValue(key, name));
                trow.setCell(i - 1,new AutoCompleteValue(null, ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void removeColumnFromRow(int r) {
        int finalIndex;
        Row row;
        AutoCompleteValue rrow, blankrow;
        String name;
        Integer key;

        if (anaSelCol < 2 || r < 0)
            return;

        row = analyteTable.getRowAt(r);
        finalIndex = displayManager.columnCount(r) + 1;
        blankrow = new AutoCompleteValue(null, "");

        if (finalIndex == 2)
            return;

        if (anaSelCol == 9) {
            row.setCell(anaSelCol,blankrow);
        } else {
            for (int i = anaSelCol; i < finalIndex; i++ ) {
                if (i == finalIndex - 1) {
                    row.setCell(i,blankrow);
                } else {
                    rrow = (AutoCompleteValue)row.getCell(i + 1);
                    if (rrow == null) {
                        row.setCell(i,blankrow);
                        continue;
                    }

                    key = (Integer)rrow.getId();
                    name = (String)rrow.getDisplay();

                    if (key != null)
                        row.setCell(i,new AutoCompleteValue(key, name));
                    else
                        row.setCell(i,blankrow);
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

    private ArrayList<Row> getResultTableModel(int group) {
        int size;
        ArrayList<Row> model;
        Row row;
        TestResultViewDO data;
        TestResultManager man;
        
        man = null;
        try {
           man = manager.getTestResults();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        size = man.getResultGroupSize(group + 1);
        for (int i = 0; i < size; i++ ) {
            data = man.getResultAt(group + 1, i);
            row = new Row(6);
            row.setCell(0,data.getUnitOfMeasureId());
            row.setCell(1,data.getTypeId());
            if (data.getDictionary() == null) {
                row.setCell(2,data.getValue());
            } else {
                row.setCell(2,data.getDictionary());                
            }

            row.setCell(3,data.getFlagsId());
            row.setCell(4,data.getSignificantDigits());
            row.setCell(5,data.getRoundingMethodId());
            model.add(row);
        }

        return model;
    }

    private void showErrorsForResultGroup(int group) {
        GridFieldErrorException error;
        int i,col;
        ArrayList<GridFieldErrorException> list;
        Row row;
        
        if (resultErrorList == null)
            return;                       
        
        for (i = 0; i < resultErrorList.size(); i++) {
            error = resultErrorList.get(i);            
            col = error.getColumnIndex();
            row = resultTable.getRowAt(col);
            if (error.getRowIndex() == group) {             
                if(row.getData() == null) 
                    row.setData(new ArrayList<GridFieldErrorException>());                                    
                
                list = (ArrayList<GridFieldErrorException>)row.getData();
                
                if(!list.contains(error)) {
                    //resultTable.addException(row, resultTable.getColumnByName(error.getFieldName()), error);
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

        if(!errorExistsInList(group,row,field,message)) {
            error = new GridFieldErrorException(message,group, row,field,"resultTable");
            if(resultErrorList == null)
                resultErrorList = new ArrayList<GridFieldErrorException>();
            resultErrorList.add(error);
        }
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
            if(!(Boolean)row.getData()) {  
                //
                // we want to make sure that there are at least 2 analyte rows 
                // in the row group that this row belongs to, if that's not the
                // case we don't allow this row to be deleted
                //                 
                prow = analyteTable.getRowAt(r-1);                
                if((Boolean)prow.getData()) {
                    //
                    // if the row above the current one is a header row then the
                    // row next to the current one should be an analyte row in 
                    // order to be able to delete the current row; the current 
                    // row can also not be deleted in the case that it is the 
                    // last row in the table 
                    //
                    if (r+1 < analyteTable.getRowCount()) {
                        nrow = analyteTable.getRowAt(r+1);
                        if(!(Boolean)nrow.getData()) {
                            analyteTable.removeRowAt(r);
                        } else {
                            com.google.gwt.user.client.Window.alert(consts.get("atleastTwoRowsInRowGroup"));
                        }
                    } else {
                        com.google.gwt.user.client.Window.alert(consts.get("atleastTwoRowsInRowGroup"));
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
        TestAnalyteManager testAnalyteManager;

        if ( !canAddRemoveColumn) {
            com.google.gwt.user.client.Window.alert(consts.get("cantAddColumn"));
            return;
        }

        r = analyteTable.getSelectedRow();

        testAnalyteManager = null;
        
        try {
            testAnalyteManager = manager.getTestAnalytes();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        if (anaSelCol != -1 && r != -1) {
            index = displayManager.getDataRowIndex(r);
            testAnalyteManager.addColumnAt(index, anaSelCol - 1, null);
            displayManager.setDataGrid(testAnalyteManager.getAnalytes());

            addColumnToRow(r);
            addColumnToRowsBelow(r);

            //analyteTable.refresh();
            anaSelCol = -1;
        }
    }

    private void removeColumn() {
        int r, index;
        TestAnalyteManager man;

        if ( !canAddRemoveColumn) {
            com.google.gwt.user.client.Window.alert(consts.get("cantRemoveColumn"));
            return;
        }

        man = null;
        try {
            man = manager.getTestAnalytes();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        r = analyteTable.getSelectedRow();
        if (anaSelCol != -1 && r != -1) {
            removeColumnFromRow(r);
            removeColumnFromRowsBelow(r);

            //analyteTable.refresh();

            index = displayManager.getDataRowIndex(r);
            man.removeColumnAt(index, anaSelCol - 1);
            displayManager.setDataGrid(man.getAnalytes());

            anaSelCol = -1;
        }
    }

    private void addHeader() {
        int r, num;
        Row row;

        r = analyteTable.getSelectedRow();
        num = analyteTable.getRowCount();

        if (r == -1 || r == num - 1) {
            analyteTable.addRow(createHeaderRow());
            analyteTable.scrollToVisible(analyteTable.getSelectedRow());
        } else {
            row = analyteTable.getRowAt(r);
            if ((Boolean)row.getData()) {
                headerAddedInTheMiddle = true;
                analyteTable.addRowAt(r, createHeaderRow());
                analyteTable.scrollToVisible(analyteTable.getSelectedRow());
            } else {
                row = analyteTable.getRowAt(r + 1);
                if ((Boolean)row.getData()) {
                    headerAddedInTheMiddle = true;
                    analyteTable.addRowAt(r + 1, createHeaderRow());
                    analyteTable.scrollToVisible(analyteTable.getSelectedRow());
                } else {
                    com.google.gwt.user.client.Window.alert(consts.get("headerCantBeAddedInsideGroup"));
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

    private void showDictionary(String entry, ArrayList<IdNameVO> list) {
        ModalWindow modal;
        TestResultManager man;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreen();
            } catch (Exception e) {
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert("DictionaryLookup error: " + e.getMessage());
                return;
            }            
            
            man = null;
            
            try {
                man = manager.getTestResults();
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                e.printStackTrace();                
            } 
            
            dictLookup.addActionHandler(new ActionHandler<DictionaryLookupScreen.Action>() {
                public void onAction(ActionEvent<DictionaryLookupScreen.Action> event) {
                    int selTab, r;
                    ArrayList<IdNameVO> list;
                    TestResultViewDO data;
                    IdNameVO entry;
                    TestResultManager man;

                    man = null;
                        
                    try {
                        man = manager.getTestResults();
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                        e.printStackTrace();                
                    } 

                    selTab = resultTabPanel.getTabBar().getSelectedTab();
                    if (event.getAction() == DictionaryLookupScreen.Action.OK) {
                        list = (ArrayList<IdNameVO>)event.getData();
                        if (list != null) {
                            r = resultTable.getSelectedRow();
                            if (r == -1) {
                                window.setError(consts.get("test.noSelectedRow"));
                                return;
                            }

                            //
                            // set the first dictionary value in the row that was
                            // selected when the lookup screen was brought up 
                            // 
                            entry = list.get(0);
                            data = man.getResultAt(selTab + 1, r);
                            data.setValue(entry.getId().toString());
                            data.setDictionary(entry.getName());
                            data.setTypeId(typeDict);
                            resultTable.setValueAt(r, 1, typeDict);
                            resultTable.setValueAt(r, 2, data.getDictionary());
                            resultTable.clearExceptions(r, 2);
                            clearResultCellError(selTab, r, TestMeta.getResultValue());
                            ActionEvent.fire(screen, Action.RESULT_CHANGED, data);
                            
                            //
                            // set the rest of the dictionary values in newly 
                            // added rows at the end of the table 
                            //                           
                            for (int i = 1; i < list.size(); i++ ) {
                                resultTable.addRow();
                                entry = list.get(i);
                                r = resultTable.getRowCount() - 1;
                                data = man.getResultAt(selTab + 1, r);
                                data.setValue(entry.getId().toString());
                                data.setDictionary(entry.getName());
                                data.setTypeId(typeDict);
                                resultTable.setValueAt(r, 1, typeDict);
                                resultTable.setValueAt(r, 2, data.getDictionary());
                            }
                           
                        }
                    }
                }

            });

        }
        modal = new ModalWindow();
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(dictLookup);
        dictLookup.setScreenState(State.DEFAULT);
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
        
        if(resultErrorList == null)
            return false;
               
        for(int i = 0; i < resultErrorList.size(); i++) {
            ex = resultErrorList.get(i);
            if ( (ex.getRowIndex() == group) && (ex.getColumnIndex() == row) &&
                (ex.getFieldName().equals(field)) && (ex.getKey().equals(message))) {
                return true;
            }
        }

        return false;
    }
    
    private class SubHeaderRow extends Row {
    	
    	public SubHeaderRow(int cells) {
    		super(cells);
    	}
    	
    	public String getStyle(int index) {
    		return "SubHeader";
    	}
    }
}
