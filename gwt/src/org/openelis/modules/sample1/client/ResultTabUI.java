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
package org.openelis.modules.sample1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.exception.ParseException;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.sample1.client.ResultCell.Value;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
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
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

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

public class ResultTabUI extends Screen {

    @UiTemplate("ResultTab.ui.xml")
    interface ResultTabUIBinder extends UiBinder<Widget, ResultTabUI> {
    };

    private static ResultTabUIBinder                    uiBinder = GWT.create(ResultTabUIBinder.class);

    @UiField
    protected Table                                     table;

    @UiField
    protected Button                                    addResultButton, removeResultButton,
                    checkAllButton, uncheckAllButton;

    @UiField
    protected Label<String>                             overrideLabel;

    protected Screen                                    parentScreen;

    protected ResultTabUI                               screen;

    protected TestAnalyteLookupUI                       testAnalyteLookup;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected EventBus                                  parentBus;

    protected TestReflexUtility1                        testReflexUtility;

    protected SampleManager1                            manager;

    protected AnalysisViewDO                            analysis;

    protected String                                    displayedUid;

    protected boolean                                   canEdit, isVisible, redraw, isBusy;

    protected HashMap<String, ArrayList<Item<Integer>>> dictionaryModel;

    protected static int                                MEAN_CHAR_WIDTH = 8,
                    CHECK_BOX_NUM_CHARS = 4, DEFAULT_NUM_CHARS = 10;

    public ResultTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        screen = this;

        addScreenHandler(table, "table", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());

                if (table.getRowCount() > 0 && (isState(ADD, UPDATE) && canEdit)) {
                    checkAllButton.setEnabled(true);
                    uncheckAllButton.setEnabled(true);
                }
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                Row row;
                ResultViewDO data;

                if (isState(ADD, UPDATE) && canEdit) {
                    row = table.getRowAt(table.getSelectedRow());
                    data = manager.result.get(analysis, (Integer)row.getData(), 0);

                    addResultButton.setEnabled(true);
                    removeResultButton.setEnabled( !Constants.dictionary().TEST_ANALYTE_REQ.equals(data.getTestAnalyteTypeId()) &&
                                                  ! (row instanceof HeaderRow));
                } else {
                    addResultButton.setEnabled(false);
                    removeResultButton.setEnabled(false);
                    checkAllButton.setEnabled(false);
                    uncheckAllButton.setEnabled(false);
                }
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int index, c;
                Integer rg, testId, unitId;
                Row row;
                ResultViewDO data;
                ArrayList<Item<Integer>> model;
                String key;
                TestManager tm;
                ResultCell rc;
                ResultFormatter rf;
                ArrayList<FormattedValue> values;

                row = table.getRowAt(event.getRow());
                index = row.getData();
                c = event.getCol();
                /*
                 * In a header row, no column other than the first one can be
                 * edited. Also, no column after the one showing the last column
                 * analyte in the row group, can be edited.
                 */
                if ( !isState(ADD, UPDATE) || !canEdit || (c > 0 && row instanceof HeaderRow) ||
                    (c >= (manager.result.count(analysis, index) + 2))) {
                    event.cancel();
                    return;
                }

                if (c > 1) {
                    data = manager.result.get(analysis, index, c - 2);
                    /*
                     * if this result's result group only has dictionary values
                     * for this unit, then a dropdown is shown in this cell as
                     * the editor and its model is created from those values
                     */

                    if (dictionaryModel == null)
                        dictionaryModel = new HashMap<String, ArrayList<Item<Integer>>>();

                    testId = analysis.getTestId();
                    rg = data.getResultGroup();
                    unitId = analysis.getUnitOfMeasureId();

                    key = testId + ":" + rg + ":" + (unitId == null ? 0 : unitId);
                    model = dictionaryModel.get(key);
                    if (model == null) {
                        try {
                            tm = getTestManager(testId);
                            rf = tm.getFormatter();
                            /*
                             * if all the ranges for this unit in this result
                             * group are dictionary values, then create a
                             * dropdown model from them
                             */
                            if (rf.hasAllDictionary(rg, unitId)) {
                                values = rf.getDictionaryValues(rg, unitId);
                                if (values != null) {
                                    model = new ArrayList<Item<Integer>>();
                                    for (FormattedValue v : values)
                                        model.add(new Item<Integer>(v.getId(), v.getDisplay()));
                                }
                            }
                            dictionaryModel.put(key, model);
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            event.cancel();
                            return;
                        }
                    }
                    rc = (ResultCell)table.getColumnAt(event.getCol()).getCellEditor();
                    rc.setModel(model);
                }
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, index, rowGroup;
                String reportable;
                Row row;
                Object val;
                Value value;
                TestManager tm;
                ResultViewDO data;
                ResultFormatter rf;
                ModalWindow modal;
                ArrayList<ResultViewDO> results;
                ArrayList<SampleTestRequestVO> tests;

                r = event.getRow();
                c = event.getCol();
                row = table.getRowAt(r);
                index = row.getData();
                val = table.getValueAt(r, c);

                if (row instanceof HeaderRow) {
                    data = manager.result.get(analysis, index, 0);
                    rowGroup = data.getRowGroup();
                    reportable = (String)val;
                    data.setIsReportable(reportable);
                    /*
                     * check or uncheck all rows in the row group starting at
                     * this header, based on the value of the checkbox in this
                     * cell
                     */
                    for (int i = index; i < manager.result.count(analysis); i++ ) {
                        data = manager.result.get(analysis, i, 0);
                        if ( !DataBaseUtil.isSame(rowGroup, data.getRowGroup()))
                            break;
                        data.setIsReportable(reportable);
                        table.setValueAt( ++r, 0, reportable);
                    }

                    return;
                }

                if (c == 0) {
                    data = manager.result.get(analysis, index, c);
                    data.setIsReportable((String)val);
                } else {
                    data = manager.result.get(analysis, index, c - 2);
                    value = (Value)val;
                    table.clearExceptions(r, c);
                    if ( !DataBaseUtil.isEmpty(value.getDisplay())) {
                        /*
                         * validate the value entered by the user
                         */
                        try {
                            tm = getTestManager(analysis.getTestId());
                            rf = tm.getFormatter();
                            ResultHelper.formatValue(data,
                                                     value.getDisplay(),
                                                     analysis.getUnitOfMeasureId(),
                                                     rf);
                        } catch (ParseException e) {
                            /*
                             * the value is not valid
                             */
                            table.addException(r, c, e);
                            data.setValue(value.getDisplay());
                            data.setTypeId(null);
                            data.setTestResultId(null);
                            return;
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            return;
                        }
                    } else {
                        data.setValue(null);
                        data.setTypeId(null);
                        data.setTestResultId(null);
                    }
                    /*
                     * Set the formatted and validated value as the displayed
                     * text, but only if the type is not dictionary, because the
                     * text for a valid dictionary value is already being
                     * displayed.
                     */
                    if ( !Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId()))
                        value.setDisplay(data.getValue());

                    table.setValueAt(r, c, value);

                    if (data.getValue() == null)
                        return;

                    if (testReflexUtility == null) {
                        testReflexUtility = new TestReflexUtility1() {
                            @Override
                            public TestManager getTestManager(Integer testId) throws Exception {
                                return screen.getTestManager(testId);
                            }
                        };
                    }

                    /*
                     * find out if a reflex test needs to be added for this
                     * value
                     */
                    try {
                        results = new ArrayList<ResultViewDO>(1);
                        results.add(data);
                        tests = testReflexUtility.getReflexTests(manager, results);
                        if (tests != null) {
                            isBusy = true;
                            /*
                             * show the popup for selecting the reflex test
                             */
                            if (testSelectionLookup == null) {
                                testSelectionLookup = new TestSelectionLookupUI() {
                                    @Override
                                    public TestManager getTestManager(Integer testId) throws Exception {
                                        return screen.getTestManager(testId);
                                    }

                                    @Override
                                    public void ok() {
                                        ArrayList<SampleTestRequestVO> tests;

                                        /*
                                         * if a reflex test was selected on the
                                         * popup then notify the main screen of
                                         * this and mark the tab as busy
                                         */
                                        tests = testSelectionLookup.getSelectedTests();
                                        if (tests != null && tests.size() > 0)
                                            parentBus.fireEvent(new AddTestEvent(tests));
                                        else
                                            isBusy = false;
                                    }
                                };
                            }

                            modal = new ModalWindow();
                            modal.setSize("520px", "350px");
                            modal.setName(Messages.get().testSelection_reflexTestSelection());
                            modal.setCSS(UIResources.INSTANCE.popupWindow());
                            modal.setContent(testSelectionLookup);

                            testSelectionLookup.setData(manager, tests);
                            testSelectionLookup.setWindow(modal);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            @Override
            public void onRowDeleted(RowDeletedEvent event) {
                manager.result.remove(analysis, (Integer)event.getRow().getData());
            }
        });

        addScreenHandler(addResultButton, "addResultButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addResultButton.setEnabled(false);
            }
        });

        addScreenHandler(removeResultButton, "removeResultButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeResultButton.setEnabled(false);
            }
        });

        addScreenHandler(checkAllButton, "checkAllButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                checkAllButton.setEnabled(false);
            }
        });

        addScreenHandler(uncheckAllButton, "uncheckAllButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                uncheckAllButton.setEnabled(false);
            }
        });

        addScreenHandler(overrideLabel, "overrideLabel", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                showResultOverride();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = Constants.uid().get(analysis);
                else
                    uid = null;

                displayResults(uid);
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */

        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }

                if (uid != null)
                    analysis = (AnalysisViewDO)manager.getObject(uid);
                else
                    analysis = null;

                if (DataBaseUtil.isDifferent(displayedUid, uid))
                    redraw = true;

                setState(state);
                displayResults(uid);
            }
        });

        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) ||
                    AnalysisChangeEvent.Action.SECTION_CHANGED.equals(event.getAction())) {
                    /*
                     * reevaluate the permissions for this section or status to
                     * enable or disable the widgets in the tab
                     */
                    setState(state);
                }
            }
        });

        parentBus.addHandler(ResultChangeEvent.getType(), new ResultChangeEvent.Handler() {
            @Override
            public void onResultChange(ResultChangeEvent event) {
                redraw = true;
                analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                displayResults(event.getUid());
                isBusy = false;
            }
        });

        parentBus.addHandler(QAEventChangeEvent.getType(), new QAEventChangeEvent.Handler() {
            @Override
            public void onQAEventChange(QAEventChangeEvent event) {
                showResultOverride();
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * returns true if some operation performed by the tab needs to be completed
     * before the data can be committed
     */
    public boolean getIsBusy() {
        return isBusy;
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null) {
            perm = null;
            sectId = getSectionId();
            statId = getStatusId();

            try {
                if (sectId != null) {
                    sect = SectionCache.getById(sectId);
                    perm = UserCache.getPermission().getSection(sect.getName());
                }
                canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                          !Constants.dictionary().ANALYSIS_RELEASED.equals(statId) &&
                          perm != null &&
                          (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception e) {
                Window.alert("canEdit:" + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void displayResults(String uid) {
        if ( !isVisible)
            return;

        if (analysis != null) {
            table.setVisible(true);
            /*
             * Reset the table's view, so that if its model is changed, it shows
             * its headers and columns correctly. Otherwise, problems like
             * widths of the columns not being correct or the headers not
             * showing may happen.
             */
            table.onResize();
        } else {
            table.setVisible(false);
        }

        if (redraw) {
            redraw = false;
            displayedUid = uid;
            fireDataChange();
        }
    }

    @UiHandler("addResultButton")
    protected void addResult(ClickEvent event) {
        ModalWindow modal;
        Row row;
        ResultViewDO data;

        if (testAnalyteLookup == null) {
            testAnalyteLookup = new TestAnalyteLookupUI() {
                @Override
                public void ok() {
                    addRowAnalytes(getAnalytes());
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("600px", "300px");
        modal.setName(Messages.get().testAnalyteSelection_testAnalyteSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testAnalyteLookup);

        testAnalyteLookup.setWindow(modal);

        row = table.getRowAt(table.getSelectedRow());
        data = manager.result.get(analysis, (Integer)row.getData(), 0);

        try {
            testAnalyteLookup.setData(getTestManager(analysis.getTestId()), data.getRowGroup());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @UiHandler("removeResultButton")
    protected void removeResult(ClickEvent event) {
        int r, i, index;
        Row row;
        boolean prevHeader, nextHeader;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0) {
            if (r == 0) {
                prevHeader = false;
            } else {
                row = table.getRowAt(r - 1);
                prevHeader = row instanceof HeaderRow;
            }

            if (r == table.getRowCount() - 1) {
                nextHeader = true;
            } else {
                row = table.getRowAt(r + 1);
                nextHeader = row instanceof HeaderRow;
            }

            if (prevHeader && nextHeader) {
                parentScreen.setError(Messages.get().result_atleastOneResultInRowGroup());
            } else {
                /*
                 * remove the row if the row group has more than one row
                 */
                table.removeRowAt(r);

                /*
                 * The position of the result rows after the deleted row changed
                 * in the manager. Reset the "data" of each table row to be the
                 * new position of its corresponding result row in the manager.
                 */
                for (i = r; i < table.getRowCount(); i++ ) {
                    index = table.getRowAt(i).getData();
                    table.getRowAt(i).setData( --index);
                }
            }
        }
    }

    @UiHandler("checkAllButton")
    protected void checkAll(ClickEvent event) {
        check("Y");
    }

    @UiHandler("uncheckAllButton")
    protected void uncheckAll(ClickEvent event) {
        check("N");
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private ArrayList<Row> getTableModel() {
        int i, j, maxTextLength[];
        boolean validateResults;
        String entry;
        Integer dictId;
        ResultViewDO data;
        Column col;
        Row row;
        TestManager tm;
        ResultFormatter rf;
        ResultCell.Value value;
        ArrayList<Row> model;
        ArrayList<Integer> dictIds;
        HashSet<Integer> cols;
        HashMap<Integer, HashSet<Integer>> dictMap;

        model = new ArrayList<Row>();
        table.clearExceptions();

        if (analysis == null || manager.result.count(analysis) == 0)
            return model;

        resetColumns();

        /*
         * the array to keep track of the length of the longest text in each
         * column
         */
        maxTextLength = new int[table.getColumnCount()];
        maxTextLength[0] = CHECK_BOX_NUM_CHARS;
        setMaxTextLength(maxTextLength, 1, Messages.get().gen_analyte());
        setMaxTextLength(maxTextLength, 2, Messages.get().gen_value());
        for (i = 3; i < maxTextLength.length; i++ )
            maxTextLength[i] = DEFAULT_NUM_CHARS;

        dictIds = new ArrayList<Integer>();
        dictMap = new HashMap<Integer, HashSet<Integer>>();

        validateResults = canEdit && isState(ADD, UPDATE);
        rf = null;

        try {
            if (validateResults) {
                tm = getTestManager(analysis.getTestId());
                rf = tm.getFormatter();
            }

            for (i = 0; i < manager.result.count(analysis); i++ ) {
                /*
                 * create header row
                 */
                if (manager.result.isHeader(analysis, i)) {
                    row = new HeaderRow(table.getColumnCount());
                    row.setCell(0, Messages.get().gen_reportable());
                    row.setCell(1, Messages.get().gen_analyte());
                    row.setCell(2, Messages.get().gen_value());
                    for (j = 1; j < manager.result.count(analysis, i); j++ ) {
                        data = manager.result.get(analysis, i, j);
                        row.setCell(j + 2, data.getAnalyte());
                        setMaxTextLength(maxTextLength, j + 2, data.getAnalyte());
                    }
                    row.setData(i);
                    model.add(row);
                }

                /*
                 * create data row and fill the columns
                 */
                row = new Row(table.getColumnCount());
                for (j = 0; j < manager.result.count(analysis, i); j++ ) {
                    data = manager.result.get(analysis, i, j);
                    if (j == 0) {
                        row.setCell(0, data.getIsReportable());
                        row.setCell(1, data.getAnalyte());
                        setMaxTextLength(maxTextLength, 1, data.getAnalyte());
                    }

                    if (validateResults && data.getValue() != null && data.getTypeId() == null) {
                        /*
                         * Since the type is not set, the value was either not
                         * validated and formatted before or the validation
                         * didn't succeed. Thus to format the value and set the
                         * type or to show an error, validate it here.
                         */
                        try {
                            ResultHelper.formatValue(data,
                                                     data.getValue(),
                                                     analysis.getUnitOfMeasureId(),
                                                     rf);

                        } catch (Exception e) {
                            table.addException(row, j + 2, e);
                        }
                    }

                    /*
                     * create the value to be set in the cell for this result
                     */
                    if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId())) {
                        if (data.getValue() != null) {
                            dictId = Integer.valueOf(data.getValue());
                            dictIds.add(dictId);

                            /*
                             * keep track of which dictionary values are
                             * displayed in which column
                             */
                            if (dictMap.get(dictId) == null)
                                dictMap.put(dictId, new HashSet<Integer>());
                            cols = dictMap.get(dictId);
                            cols.add(j + 2);
                        }
                        value = new ResultCell.Value(null, data.getValue());
                    } else {
                        value = new ResultCell.Value(data.getValue(), null);
                        setMaxTextLength(maxTextLength, j + 2, data.getValue());
                    }

                    row.setCell(j + 2, value);
                }
                row.setData(i);
                model.add(row);
            }

            if (dictIds.size() > 0) {
                /*
                 * For type dictionary, the displayed text is looked up from the
                 * cache. The following is done to fetch and put the dictionary
                 * records needed for the results, in the cache, all at once.
                 */
                DictionaryCache.getByIds(dictIds);

                /*
                 * reset the length of the longest text in a column if it's
                 * showing dictionary entries
                 */
                for (Integer id : dictIds) {
                    entry = DictionaryCache.getById(id).getEntry();
                    cols = dictMap.get(id);
                    for (int c : cols)
                        setMaxTextLength(maxTextLength, c, entry);
                }
            }

            /*
             * set the width of a column in pixels based on the longest text
             * showing in it
             */
            for (i = 0; i < maxTextLength.length; i++ ) {
                col = table.getColumnAt(i);
                col.setWidth(maxTextLength[i] * MEAN_CHAR_WIDTH);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return model;
    }

    /**
     * adjusts the number of columns in the table based on the results for the
     * analysis and initializes the columns with the correct cell renderer
     */
    private void resetColumns() {
        int currNumCols, reqNumCols;
        Column col;

        reqNumCols = manager.result.maxColumns(analysis) + 2;
        currNumCols = table.getColumnCount();

        if (reqNumCols == currNumCols)
            return;

        /*
         * add columns to the table if it has less columns than needed to show
         * all column analytes, otherwise removes columns
         */
        if (reqNumCols > currNumCols) {
            for (int i = currNumCols; i < reqNumCols; i++ ) {
                col = table.addColumn(null, Messages.get().gen_alphabet().substring(i, i + 1));
                if (i > 1)
                    col.setCellRenderer(new ResultCell());
            }
        } else {
            for (int i = currNumCols; i > reqNumCols; i-- )
                table.removeColumnAt(i - 1);
        }
    }

    private TestManager getTestManager(Integer testId) throws Exception {
        if ( ! (parentScreen instanceof CacheProvider))
            throw new Exception("Parent screen must implement " + CacheProvider.class.toString());

        return ((CacheProvider)parentScreen).get(testId, TestManager.class);
    }

    private void addRowAnalytes(ArrayList<TestAnalyteViewDO> analytes) {
        Integer index;
        Row row;
        ArrayList<Integer> indexes;

        if (analytes == null || analytes.size() == 0)
            return;

        row = table.getRowAt(table.getSelectedRow());
        index = (Integer)row.getData();

        /*
         * if the selected row in the table is a header row then the new rows
         * are added beginning at the index of the corresponding result row in
         * the manager, otherwise the new rows are added beginning at the index
         * of the next result row in the manager
         */
        if ( ! (row instanceof HeaderRow))
            index += 1;

        indexes = new ArrayList<Integer>();
        for (int i = 0; i < analytes.size(); i++ )
            indexes.add(index + i);

        isBusy = true;
        parentBus.fireEvent(new AddRowAnalytesEvent(analysis, analytes, indexes));
    }

    private void check(String val) {
        ResultViewDO data;
        Row row;

        for (int i = 0; i < table.getRowCount(); i++ ) {
            row = table.getRowAt(i);
            data = manager.result.get(analysis, (Integer)row.getData(), 0);
            if ( !DataBaseUtil.isSame(val, data.getIsReportable()))
                data.setIsReportable(val);
            table.setValueAt(i, 0, val);
        }
    }

    /**
     * keeps track of the length of the longest string in the table column at
     * the passed index
     */
    private void setMaxTextLength(int maxTextLength[], int index, String text) {
        if ( !DataBaseUtil.isEmpty(text))
            maxTextLength[index] = Math.max(text.length(), maxTextLength[index]);
    }

    /**
     * notify the user if the results are overriden for either the sample or the
     * analysis
     */
    private void showResultOverride() {
        String label;
        Integer typeId;

        typeId = Constants.dictionary().QAEVENT_OVERRIDE;
        label = null;

        if (manager != null && analysis != null &&
            (manager.qaEvent.hasType(typeId) || manager.qaEvent.hasType(analysis, typeId)))
            label = Messages.get().result_overridden();
        overrideLabel.setText(label);
    }
}