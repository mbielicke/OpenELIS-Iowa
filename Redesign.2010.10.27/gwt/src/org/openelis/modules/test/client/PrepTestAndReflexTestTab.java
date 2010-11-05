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
import org.openelis.gwt.common.DataBaseUtil;
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
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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
import org.openelis.meta.TestMeta;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class PrepTestAndReflexTestTab extends Screen implements
                                                    ActionHandler<AnalyteAndResultTab.Action> {

    private TestManager           manager;
    private TestAnalyteManager    testAnalyteManager;
    private TestResultManager     testResultManager;

    private boolean               loaded;
    private Integer               typeDict, typeDefault;

    private Table                 testPrepTable, testReflexTable;
    private Button                addPrepTestButton, removePrepTestButton, addReflexTestButton,
                                  removeReflexTestButton;
    private AutoComplete          prepTestAuto, reflexTestAuto, analyteAuto, resultAuto;
    private Label<String>         prepMethodName, reflexMethodName;

    public PrepTestAndReflexTestTab(ScreenDefInt def,Window window, ScreenService service) {
        setDefinition(def);
        setWindow(window);
        this.service = service;

        initialize();
        initializeDropdowns();
    }

    private void initialize() {

        testPrepTable = (Table)def.getWidget("testPrepTable");
        addScreenHandler(testPrepTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    testPrepTable.setModel(getPrepTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testPrepTable.setEnabled(true);
                testPrepTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testPrepTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE && state != State.QUERY)
                    event.cancel();
            }
        });

        testPrepTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AutoCompleteValue av;
                TestPrepViewDO data;

                r = event.getRow();
                c = event.getCol();

                val = testPrepTable.getValueAt(r, c);

                try {
                    data = manager.getPrepTests().getPrepAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        av = (AutoCompleteValue)val;
                        data.setPrepTestId((Integer) (av.getId()));
                        data.setPrepTestName(prepTestAuto.getDisplay());
                        data.setMethodName(prepMethodName.getText());
                        break;
                    case 2:
                        data.setIsOptional((String)val);
                        break;
                }

            }
        });

        testPrepTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                TestPrepViewDO data;

                r = event.getIndex();
                try {
                    data = new TestPrepViewDO();
                    data.setIsOptional("N");
                    manager.getPrepTests().addPrep(data);
                    testPrepTable.setValueAt(r, 2, "N");
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        testPrepTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getPrepTests().removePrepAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        prepTestAuto = (AutoComplete)testPrepTable.getColumnAt(testPrepTable.getColumnByName(TestMeta.getPrepPrepTestName())).getCellEditor().getWidget();
        prepTestAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TestMethodVO data;
                ArrayList<TestMethodVO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = service.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new Item<Integer>(data.getTestId(), data.getTestName(),
                                                   data.getMethodName(), data.getTestDescription()));
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();

            }
        });

        prepMethodName = (Label<String>)testPrepTable.getColumnAt(testPrepTable.getColumnByName("method")).getCellEditor().getWidget();

        prepTestAuto.getPopupContext().addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Item<Integer> selectedRow;
                String value;
                int r;

                selectedRow = (Item<Integer>)prepTestAuto.getPopupContext().getRowAt(event.getSelectedItem());
                r = testPrepTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.getKey() != null) {
                    value = (String)selectedRow.getCell(1);
                    testPrepTable.setValueAt(r, 1, value);
                } else {
                    testPrepTable.setValueAt(r, 1, null);
                }
            }

        });

        addPrepTestButton = (Button)def.getWidget("addPrepTestButton");
        addScreenHandler(addPrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testPrepTable.getRowCount();
                testPrepTable.addRow();
                testPrepTable.selectRowAt(r);
                testPrepTable.scrollToVisible(r);
                testPrepTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addPrepTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        removePrepTestButton = (Button)def.getWidget("removePrepTestButton");
        addScreenHandler(removePrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int i;

                i = testPrepTable.getSelectedRow();
                if (i > -1 && testPrepTable.getRowCount() > 0)
                    testPrepTable.removeRowAt(i);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                removePrepTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
            }
        });

        testReflexTable = (Table)def.getWidget("testReflexTable");
        addScreenHandler(testReflexTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    testReflexTable.setModel(getReflexTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testReflexTable.setEnabled(true);
                testReflexTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testReflexTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                AutoCompleteValue val;

                if (state != State.ADD && state != State.UPDATE && state != State.QUERY)
                    event.cancel();

                r = event.getRow();
                c = event.getCol();

                if (c == 3) {
                    val = (AutoCompleteValue)testReflexTable.getValueAt(r, 2);
                    if (val == null || val.getId() == null) {
                        com.google.gwt.user.client.Window.alert(consts.get("selectAnaBeforeRes"));
                        event.cancel();
                    }
                }

            }

        });

        testReflexTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TestReflexViewDO data;
                AutoCompleteValue av;

                r = event.getRow();
                c = event.getCol();
                val = testReflexTable.getValueAt(r, c);
                try {
                    data = manager.getReflexTests().getReflexAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        if(val != null) {
                            av = (AutoCompleteValue)val;
                            data.setAddTestId((Integer) (av.getId()));
                            data.setAddTestName(reflexTestAuto.getDisplay());
                            data.setAddMethodName(reflexMethodName.getText());
                        } else {
                            data.setAddTestId(null);
                            data.setAddTestName(null);
                            data.setAddMethodName(null);
                        }
                        break;
                    case 2:
                        if(val != null) {
                            av = (AutoCompleteValue)val;
                            data.setTestAnalyteId((Integer) (av.getId()));
                            data.setTestAnalyteName(analyteAuto.getDisplay());
                        } else {                            
                            data.setTestAnalyteId(null);
                            data.setTestAnalyteName(null);
                        }
                        break;
                    case 3:
                        if(val != null) {
                            av = (AutoCompleteValue)val;
                            data.setTestResultId((Integer) (av.getId()));
                            data.setTestResultValue(resultAuto.getDisplay());
                        } else {
                            data.setTestResultId(null);
                            data.setTestResultValue(null);
                        }
                        break;
                    case 4:
                        if(val != null)
                            data.setFlagsId((Integer)val);
                        else 
                            data.setFlagsId(null);
                        break;
                }
            }
        });

        testReflexTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getReflexTests().addReflex(new TestReflexViewDO());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        testReflexTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getReflexTests().removeReflexAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        reflexTestAuto = (AutoComplete)testReflexTable.getColumnAt(testReflexTable.getColumnByName(TestMeta.getReflexAddTestName())).getCellEditor().getWidget();
        reflexTestAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TestMethodVO data;
                ArrayList<TestMethodVO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = service.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new Item<Integer>(data.getTestId(), data.getTestName(),
                                                   data.getMethodName(), data.getTestDescription()));
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();

            }
        });

        reflexMethodName = (Label<String>)testReflexTable.getColumnAt(testReflexTable.getColumnByName("method")).getCellEditor().getWidget();

        reflexTestAuto.getPopupContext().addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Item<Integer> selectedRow;
                String value;
                int index;

                selectedRow = (Item<Integer>)reflexTestAuto.getPopupContext().getRowAt(event.getSelectedItem());
                index = testReflexTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.getKey() != null) {
                    value = (String)selectedRow.getCell(1);
                    testReflexTable.setValueAt(index, 1, value);
                } else {
                    testReflexTable.setValueAt(index, 1, null);
                }

            }

        });

        analyteAuto = (AutoComplete)testReflexTable.getColumnAt(testReflexTable.getColumnByName(TestMeta.getReflexTestAnalyteName())).getCellEditor().getWidget();
        analyteAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestAnalyteViewDO data;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;
                String name;

                model = new ArrayList<Item<Integer>>();
                for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
                    data = testAnalyteManager.getAnalyteAt(i, 0);
                    name = data.getAnalyteName();
                    if (name != null && name.startsWith(event.getMatch())) {
                        row = new Item<Integer>(data.getId(), name);
                        model.add(row);
                    }
                }

                if (model.size() == 0)
                    model.add(new Item<Integer>(null, ""));

                analyteAuto.showAutoMatches(model);
            }

        });

        resultAuto = (AutoComplete)testReflexTable.getColumnAt(testReflexTable.getColumnByName(TestMeta.getReflexTestResultValue())).getCellEditor().getWidget();
        resultAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestResultViewDO data;
                ArrayList<Item<Integer>> model;
                Row trow;
                AutoCompleteValue arow;
                Integer rg;
                int r, size;
                String value;

                r = testReflexTable.getSelectedRow();
                trow = testReflexTable.getRowAt(r);
                arow = (AutoCompleteValue)trow.getCell(2);

                model = new ArrayList<Item<Integer>>();

                if (arow.getId() == null) {
                    model.add(new Item<Integer>(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                rg = getResultGroupForTestAnalyte((Integer)arow.getId());

                if (rg == null) {
                    model.add(new Item<Integer>(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                size = testResultManager.getResultGroupSize(rg);

                for (int i = 0; i < size; i++ ) {
                    data = testResultManager.getResultAt(rg, i);
                    if (DataBaseUtil.isSame(typeDict, data.getTypeId()))
                        value = data.getDictionary();
                    else
                        value = data.getValue();
                    
                    if (!typeDefault.equals(data.getTypeId()) && value != null) 
                        model.add(new Item<Integer>(data.getId(), value));                    
                }

                if (model.size() == 0)
                    model.add(new Item<Integer>(null, ""));

                resultAuto.showAutoMatches(model);
            }

        });

        addReflexTestButton = (Button)def.getWidget("addReflexTestButton");
        addScreenHandler(addReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testReflexTable.getRowCount();
                testReflexTable.addRow();
                testReflexTable.selectRowAt(r);
                testReflexTable.scrollToVisible(r);
                testReflexTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReflexTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        removeReflexTestButton = (Button)def.getWidget("removeReflexTestButton");
        addScreenHandler(removeReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testReflexTable.getSelectedRow();
                if (r > -1 && testReflexTable.getRowCount() > 0)
                    testReflexTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReflexTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
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

    public void onAction(ActionEvent<AnalyteAndResultTab.Action> event) {
        TestAnalyteViewDO ana;
        TestResultViewDO res;

        if (state == State.QUERY)
            return;

        if (event.getAction() == Action.ANALYTE_CHANGED) {
            ana = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(ana.getId(), ana.getAnalyteName(), "analyteNameChanged", true);
        } else if (event.getAction() == Action.ANALYTE_DELETED) {
            ana = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(ana.getId(), ana.getAnalyteName(), "analyteDeleted", false);
        }
        if (event.getAction() == Action.RESULT_CHANGED) {
            res = (TestResultViewDO)event.getData();
            setResultErrors(res, "resultValueChanged", true);
        } else if (event.getAction() == Action.RESULT_DELETED) {
            res = (TestResultViewDO)event.getData();
            setResultErrors(res, "resultDeleted", false);
        }

    }

    protected void clearKeys(TestPrepManager tpm, TestReflexManager tfm) {
        TestPrepViewDO prep;
        TestReflexViewDO ref;
        int i;

        for (i = 0; i < tpm.count(); i++ ) {
            prep = tpm.getPrepAt(i);
            prep.setId(null);
            prep.setTestId(null);
        }

        for (i = 0; i < tfm.count(); i++ ) {
            ref = tfm.getReflexAt(i);
            ref.setId(null);
            ref.setTestAnalyteId(ref.getTestAnalyteId() * ( -1));
            ref.setTestResultId(ref.getTestResultId() * ( -1));
            ref.setTestId(null);
        }
    }

    protected void finishEditing() {
        testReflexTable.finishEditing();
    }

    private ArrayList<Row> getPrepTestModel() {
        ArrayList<Row> model;
        TestPrepManager tpm;
        TestPrepViewDO data;
        Row row;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            tpm = manager.getPrepTests();
            for (int i = 0; i < tpm.count(); i++ ) {
                data = tpm.getPrepAt(i);
                row = new Row(3);
                //row.key = data.getId();

                row.setCell(0,new AutoCompleteValue(data.getPrepTestId(), data.getPrepTestName()));
                row.setCell(1,data.getMethodName());
                row.setCell(2,data.getIsOptional());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private ArrayList<Row> getReflexTestModel() {
        ArrayList<Row> model;
        TestReflexManager trm;
        TestReflexViewDO data;
        Row row;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            trm = manager.getReflexTests();
            for (int i = 0; i < trm.count(); i++ ) {
                data = trm.getReflexAt(i);
                row = new Row(5);
                //row.key = data.getId();

                row.setCell(0,new AutoCompleteValue(data.getAddTestId(),
                                                           data.getAddTestName()));
                row.setCell(1,data.getAddMethodName());
                row.setCell(2,new AutoCompleteValue(data.getTestAnalyteId(),
                                                           data.getTestAnalyteName()));
                row.setCell(3,new AutoCompleteValue(data.getTestResultId(),
                                                           data.getTestResultValue()));
                row.setCell(4,data.getFlagsId());

                model.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> list;
        Item<Integer> row;

        list = DictionaryCache.getListByCategorySystemName("test_reflex_flags");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO data : list) {
            row = new Item<Integer>(data.getId(), data.getEntry());
            row.setEnabled("Y".equals(data.getIsActive()));
            model.add(row);
        }
        ((Dropdown)testReflexTable.getColumnAt(testReflexTable.getColumnByName(TestMeta.getReflexFlagsId())).getCellEditor().getWidget()).setModel(model);

        try {
            typeDict = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
            typeDefault = DictionaryCache.getIdFromSystemName("test_res_type_default");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    private Integer getResultGroupForTestAnalyte(Integer taId) {
        TestAnalyteViewDO data;
        for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
            data = testAnalyteManager.getAnalyteAt(i, 0);
            if (taId.equals(data.getId()))
                return data.getResultGroup();
        }

        return null;
    }

    private void setAnalyteErrors(Integer id, String name, String key, boolean matchLabel) {
        AutoCompleteValue av;
        String val;

        for (int i = 0; i < testReflexTable.getRowCount(); i++ ) {
            av = (AutoCompleteValue)testReflexTable.getValueAt(i, 2);
            val = av.getDisplay();

            if (id.equals(av.getId())) {
                if ( (matchLabel && ! (val.equals(name))) || !matchLabel) {
                    av = new AutoCompleteValue(null, "");
                    testReflexTable.setValueAt(i, 2, av);
                    testReflexTable.addException(i, 2, new LocalizedException(key));
                }
            }
        }
    }

    private void setResultErrors(TestResultViewDO data, String key, boolean matchLabel) {
        AutoCompleteValue av;
        String val, value;
        Integer id;

        id = data.getId();
        for (int i = 0; i < testReflexTable.getRowCount(); i++ ) {
            av = (AutoCompleteValue)testReflexTable.getValueAt(i, 3);
            val = av.getDisplay();
            if (DataBaseUtil.isSame(typeDict, data.getTypeId()))
                value = data.getDictionary();
            else
                value = data.getValue();
            if (id.equals(av.getId())) {
                if ( (matchLabel && ! (val.equals(value))) || !matchLabel) {
                    av = new AutoCompleteValue(null, "");
                    testReflexTable.setValueAt(i, 3, av);
                    testReflexTable.addException(i, 3, new LocalizedException(key));
                }
            }
        }
    }

}
