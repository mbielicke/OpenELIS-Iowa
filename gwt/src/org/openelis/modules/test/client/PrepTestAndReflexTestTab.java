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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class PrepTestAndReflexTestTab extends Screen implements GetMatchesHandler,
                                                    ActionHandler<AnalyteAndResultTab.Action> {

    private TestManager            manager;
    private TestMetaMap            meta = new TestMetaMap();
    private TestAnalyteManager     testAnalyteManager;
    private TestResultManager      testResultManager;

    private boolean                loaded;

    private TableWidget            testPrepTable, testReflexTable;
    private AppButton              addPrepTestButton, removePrepTestButton, addReflexTestButton,
                                   removeReflexTestButton;
    private AutoComplete<Integer>  prepTestAuto, reflexTestAuto, analyteAuto, resultAuto;

    public PrepTestAndReflexTestTab(ScreenDefInt def, ScreenService service) {
        setDef(def);

        this.service = service;
        initialize();

        initializeDropdowns();
    }

    private void initialize() {

        testPrepTable = (TableWidget)def.getWidget("testPrepTable");
        addScreenHandler(testPrepTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    testPrepTable.load(getPrepTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testPrepTable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                testPrepTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testPrepTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                TestPrepViewDO prepDO;

                r = event.getRow();
                c = event.getCol();

                val = testPrepTable.getRow(r).cells.get(c).getValue();

                try {
                    prepDO = manager.getPrepTests().getPrepAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        row = (TableDataRow)val;
                        prepDO.setPrepTestId((Integer) (row.key));
                        if (row.key != null) {
                            prepDO.setPrepTestName((String)row.cells.get(0).getValue());
                            prepDO.setMethodName((String)row.cells.get(1).getValue());
                        }
                        break;
                    case 2:
                        prepDO.setIsOptional((String)val);
                        break;
                }

            }
        });
        testPrepTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    TestPrepViewDO prepDO;
                    prepDO = new TestPrepViewDO();
                    prepDO.setIsOptional("N");
                    manager.getPrepTests().addPrep(prepDO);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        testPrepTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getPrepTests().removePrepAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        prepTestAuto = (AutoComplete<Integer>)testPrepTable.getColumnWidget(meta.getTestPrep().getPrepTest().getName());
        prepTestAuto.addGetMatchesHandler(this);

        prepTestAuto.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableDataRow selectedRow;
                String value;
                int index;

                selectedRow = event.getSelectedItem().row;
                value = (String)selectedRow.cells.get(1).getValue();
                index = testPrepTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.key != null)
                    testPrepTable.setCell(index, 1, value);
                else
                    testPrepTable.setCell(index, 1, null);
            }

        });

        addPrepTestButton = (AppButton)def.getWidget("addPrepTestButton");
        addScreenHandler(addPrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                testPrepTable.addRow();
                testPrepTable.selectRow(testPrepTable.numRows() - 1);
                testPrepTable.scrollToSelection();
                testPrepTable.startEditing(testPrepTable.numRows() - 1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addPrepTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        removePrepTestButton = (AppButton)def.getWidget("removePrepTestButton");
        addScreenHandler(removePrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int i;

                i = testPrepTable.getSelectedRow();
                if (i > -1 && testPrepTable.numRows() > 0)
                    testPrepTable.deleteRow(i);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                removePrepTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
            }
        });

        testReflexTable = (TableWidget)def.getWidget("testReflexTable");
        addScreenHandler(testReflexTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    testReflexTable.load(getReflexTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testReflexTable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                testReflexTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testReflexTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int row, col;
                TableDataRow val;
                TableDataCell tdc;

                row = event.getRow();
                col = event.getCol();
                if (col == 3) {
                    val = (TableDataRow)testReflexTable.getRow(row).cells.get(2).getValue();
                    tdc = testReflexTable.getCell(row, 3);
                    if (val == null || val.key == null) {
                        Window.alert(consts.get("selectAnaBeforeRes"));
                        event.cancel();
                    }
                }

            }

        });

        testReflexTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Object val;
                TestReflexViewDO refDO;
                TableDataRow trow;

                row = event.getRow();
                col = event.getCol();
                val = testReflexTable.getRow(row).cells.get(col).getValue();
                try {
                    refDO = manager.getReflexTests().getReflexAt(row);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (col) {
                    case 0:
                        trow = (TableDataRow)val;
                        refDO.setAddTestId((Integer) (trow.key));
                        if (trow.key != null) {
                            refDO.setAddTestName((String)trow.cells.get(0).getValue());
                            refDO.setAddMethodName((String)trow.cells.get(1).getValue());
                        }
                        break;
                    case 2:
                        trow = (TableDataRow)val;
                        refDO.setTestAnalyteId((Integer) (trow.key));
                        refDO.setTestAnalyteName((String)trow.cells.get(0).getValue());
                        break;
                    case 3:
                        trow = (TableDataRow)val;
                        refDO.setTestResultId((Integer) (trow.key));
                        refDO.setTestResultValue((String)trow.cells.get(0).getValue());
                        break;
                    case 4:
                        refDO.setFlagsId((Integer)val);
                        break;
                }
            }
        });

        testReflexTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getReflexTests().addReflex(new TestReflexViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        testReflexTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getReflexTests().removeReflexAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        reflexTestAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(meta.getTestReflex().getAddTest().getName());
        reflexTestAuto.addGetMatchesHandler(this);

        reflexTestAuto.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableDataRow selectedRow;
                String value;
                int index;

                selectedRow = event.getSelectedItem().row;
                value = (String)selectedRow.cells.get(1).getValue();
                index = testReflexTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.key != null)
                    testReflexTable.setCell(index, 1, value);
                else
                    testReflexTable.setCell(index, 1, null);

            }

        });

        analyteAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(meta.getTestReflex().getTestAnalyte().getAnalyte().getName());
        analyteAuto.addGetMatchesHandler(new GetMatchesHandler() {

            public void onGetMatches(GetMatchesEvent event) {
                TestAnalyteViewDO ana;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                String name;

                model = new ArrayList<TableDataRow>();
                for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
                    ana = testAnalyteManager.getAnalyteAt(i, 0);
                    name = ana.getAnalyteName();
                    if (name != null && name.startsWith(event.getMatch())) {
                        row = new TableDataRow(1);
                        row.key = ana.getId();
                        row.cells.get(0).setValue(name);
                        model.add(row);
                    }
                }

                if (model.size() == 0)
                    model.add(new TableDataRow(null, ""));

                analyteAuto.showAutoMatches(model);
            }

        });

        resultAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(meta.getTestReflex().getTestResult().getValue());
        resultAuto.addGetMatchesHandler(new GetMatchesHandler() {

            public void onGetMatches(GetMatchesEvent event) {
                TestResultViewDO res;
                ArrayList<TableDataRow> model;
                TableDataRow row, trow, arow;
                Integer rg;
                int ar, size;
                String value;
                Integer dictId;

                ar = testReflexTable.getSelectedRow();
                trow = testReflexTable.getRow(ar);
                arow = (TableDataRow)trow.cells.get(2).getValue();

                model = new ArrayList<TableDataRow>();

                if (arow.key == null) {
                    model.add(new TableDataRow(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                rg = getResultGroupForTestAnalyte((Integer)arow.key);

                if (rg == null) {
                    model.add(new TableDataRow(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                size = testResultManager.getResultGroupSize(rg);
                dictId = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");

                for (int i = 0; i < size; i++ ) {
                    res = testResultManager.getResultAt(rg, i);
                    row = new TableDataRow(1);
                    if (dictId.equals(res.getTypeId()))
                        value = res.getDictionary();
                    else
                        value = res.getValue();
                    if (value != null && !"".equals(value)) {
                        row.key = res.getId();
                        row.cells.get(0).setValue(value);
                        model.add(row);
                    }
                }

                if (model.size() == 0)
                    model.add(new TableDataRow(null, ""));

                resultAuto.showAutoMatches(model);
            }

        });

        addReflexTestButton = (AppButton)def.getWidget("addReflexTestButton");
        addScreenHandler(addReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                testReflexTable.addRow();
                testReflexTable.selectRow(testReflexTable.numRows() - 1);
                testReflexTable.scrollToSelection();
                testReflexTable.startEditing(testReflexTable.numRows() - 1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addReflexTestButton.enable(true);
                else
                    addReflexTestButton.enable(false);
            }
        });

        removeReflexTestButton = (AppButton)def.getWidget("removeReflexTestButton");
        addScreenHandler(removeReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = testReflexTable.getSelectedRow();
                if (selectedRow > -1)
                    testReflexTable.deleteRow(selectedRow);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeReflexTestButton.enable(true);
                else
                    removeReflexTestButton.enable(false);
            }
        });

    }

    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (state == State.UPDATE || state == State.ADD) {
                    testAnalyteManager = manager.getTestAnalytes();
                    testResultManager = manager.getTestResults();
                }
                DataChangeEvent.fire(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        loaded = true;
    }

    public void onGetMatches(GetMatchesEvent event) {
        QueryFieldUtil parser;
        TestMethodVO data;
        ArrayList<TestMethodVO> list;
        ArrayList<TableDataRow> model;

        parser = new QueryFieldUtil();
        parser.parse(event.getMatch());

        window.setBusy();
        try {
            list = service.callList("fetchByName", parser.getParameter().get(0));
            model = new ArrayList<TableDataRow>();
            for (int i = 0; i < list.size(); i++ ) {
                data = list.get(i);
                model.add(new TableDataRow(data.getTestId(), data.getTestName(),
                                           data.getMethodName(), data.getTestDescription()));
            }
            ((AutoComplete)event.getSource()).showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();

    }

    public void onAction(ActionEvent<AnalyteAndResultTab.Action> event) {
        TestAnalyteViewDO anaDO;
        TestResultViewDO resDO;

        if (state == State.QUERY)
            return;

        if (event.getAction() == Action.ANALYTE_CHANGED) {
            anaDO = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(anaDO.getId(), anaDO.getAnalyteName(), "analyteNameChanged", true);
        } else if (event.getAction() == Action.ANALYTE_DELETED) {
            anaDO = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(anaDO.getId(), anaDO.getAnalyteName(), "analyteDeleted", false);
        }
        if (event.getAction() == Action.RESULT_CHANGED) {
            resDO = (TestResultViewDO)event.getData();
            setResultErrors(resDO, "resultValueChanged", true);
        } else if (event.getAction() == Action.RESULT_DELETED) {
            resDO = (TestResultViewDO)event.getData();
            setResultErrors(resDO, "resultDeleted", false);
        }

    }

    protected void clearKeys(TestPrepManager tpm, TestReflexManager tfm) {
        TestPrepViewDO prepDO;
        TestReflexViewDO refDO;
        int i;

        for (i = 0; i < tpm.count(); i++ ) {
            prepDO = tpm.getPrepAt(i);
            prepDO.setId(null);
            prepDO.setTestId(null);
        }

        for (i = 0; i < tfm.count(); i++ ) {
            refDO = tfm.getReflexAt(i);
            refDO.setId(null);
            refDO.setTestAnalyteId(refDO.getTestAnalyteId() * ( -1));
            refDO.setTestResultId(refDO.getTestResultId() * ( -1));
            refDO.setTestId(null);
        }
    }

    protected void finishEditing() {
        testReflexTable.finishEditing();
    }

    private ArrayList<TableDataRow> getPrepTestModel() {
        ArrayList<TableDataRow> model;
        TestPrepManager tpm;
        TestPrepViewDO prepTest;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            tpm = manager.getPrepTests();
            for (int i = 0; i < tpm.count(); i++ ) {
                prepTest = tpm.getPrepAt(i);
                row = new TableDataRow(3);
                row.key = prepTest.getId();

                row.cells.get(0).setValue(
                                          new TableDataRow(prepTest.getPrepTestId(),
                                                           prepTest.getPrepTestName()));
                row.cells.get(1).setValue(prepTest.getMethodName());
                row.cells.get(2).setValue(prepTest.getIsOptional());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private ArrayList<TableDataRow> getReflexTestModel() {
        ArrayList<TableDataRow> model;
        TestReflexManager trm;
        TestReflexViewDO reflexTest;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            trm = manager.getReflexTests();
            for (int i = 0; i < trm.count(); i++ ) {
                reflexTest = trm.getReflexAt(i);
                row = new TableDataRow(5);
                row.key = reflexTest.getId();

                row.cells.get(0).setValue(
                                          new TableDataRow(reflexTest.getAddTestId(),
                                                           reflexTest.getAddTestName()));
                row.cells.get(1).setValue(reflexTest.getAddMethodName());
                row.cells.get(2).setValue(
                                          new TableDataRow(reflexTest.getTestAnalyteId(),
                                                           reflexTest.getTestAnalyteName()));
                row.cells.get(3).setValue(
                                          new TableDataRow(reflexTest.getTestResultId(),
                                                           reflexTest.getTestResultValue()));
                row.cells.get(4).setValue(reflexTest.getFlagsId());

                model.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;

        list = DictionaryCache.getListByCategorySystemName("test_reflex_flags");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown)testReflexTable.getColumnWidget(meta.getTestReflex().getFlagsId())).setModel(model);
    }

    private Integer getResultGroupForTestAnalyte(Integer taId) {
        TestAnalyteViewDO anaDO;
        for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
            anaDO = testAnalyteManager.getAnalyteAt(i, 0);
            if (taId.equals(anaDO.getId()))
                return anaDO.getResultGroup();
        }

        return null;
    }

    private void setAnalyteErrors(Integer id, String name, String key, boolean matchLabel) {
        TableDataRow trow, arow;
        String val;

        for (int i = 0; i < testReflexTable.numRows(); i++ ) {
            trow = testReflexTable.getRow(i);
            arow = (TableDataRow)trow.cells.get(2).getValue();
            val = (String)arow.cells.get(0).getValue();

            if (id.equals(arow.key)) {
                if ((matchLabel && ! (val.equals(name))) || !matchLabel) {
                    arow = new TableDataRow(null, "");
                    trow.cells.get(2).setValue(arow);
                    testReflexTable.setCellException(i, 2, new LocalizedException(key));                    
                }
            }
        }
    }

    private void setResultErrors(TestResultViewDO resDO, String key, boolean matchLabel) {
        TableDataRow trow, rrow;
        String val;
        Integer id;
        String value;
        Integer dictId;

        id = resDO.getId();
        dictId = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
        for (int i = 0; i < testReflexTable.numRows(); i++ ) {
            trow = testReflexTable.getRow(i);
            rrow = (TableDataRow)trow.cells.get(3).getValue();
            val = (String)rrow.cells.get(0).getValue();
            if (dictId.equals(resDO.getTypeId()))
                value = resDO.getDictionary();
            else
                value = resDO.getValue();
            if (id.equals(rrow.key)) {
                if ( (matchLabel && ! (val.equals(value))) || !matchLabel) {
                    rrow = new TableDataRow(null, "");
                    trow.cells.get(3).setValue(rrow);
                    testReflexTable.setCellException(i, 3, new LocalizedException(key));
                }
            }
        }

    }

}
