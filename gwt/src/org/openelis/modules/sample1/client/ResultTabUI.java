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
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.modules.sample1.client.ResultCell.Value;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ResultTabUI extends Screen {

    @UiTemplate("ResultTab.ui.xml")
    interface ResultTabUIBinder extends UiBinder<Widget, ResultTabUI> {
    };

    private static ResultTabUIBinder                    uiBinder = GWT.create(ResultTabUIBinder.class);

    @UiField
    protected Button                                    addResultButton, removeResultButton,
                    checkAllButton, uncheckAllButton;
    @UiField
    protected Table                                     table;

    protected ResultTabUI                               resultPopoutScreen;
    protected SampleManager1                            manager;
    protected GetMatchesHandler                         resultMatchesHandler;
    protected AnalysisManager                           analysisMan;
    protected AnalysisViewDO                            analysis, emptyAnalysis;
    protected Screen                                    parentScreen;
    protected String                                    displayedUid;
    protected boolean                                   canEdit, isVisible;
    protected HashMap<String, ArrayList<Item<Integer>>> dictionaryModel;

    public ResultTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int index;
                Row row;
                ResultViewDO data;
                ResultCell rc;
                ArrayList<Item<Integer>> model;

                row = table.getRowAt(event.getRow());
                if ( !isState(ADD, UPDATE) || !canEdit ||
                    (event.getCol() > 0 && row instanceof HeaderRow)) {
                    event.cancel();
                    return;
                }

                if (event.getCol() > 1) {
                    index = row.getData();
                    data = manager.result.get(analysis, index, event.getCol() - 2);
                    /*
                     * if this result's result group only has dictionary values
                     * for this unit, then a dropdown is shown in this cell as
                     * the editor and its model is created from those values
                     */
                    try {
                        model = getDictionaryModel(analysis.getTestId(),
                                                   data.getResultGroup(),
                                                   analysis.getUnitOfMeasureId());
                        rc = (ResultCell)table.getColumnAt(event.getCol()).getCellEditor();
                        rc.setModel(model);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        e.printStackTrace();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }

            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, index, rowGroup;
                String reportable;
                Row row;
                ResultViewDO data;
                Object val;
                Value value;
                FormattedValue fv;

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
                     * Only the first column of a header row, the checkbox for
                     * reportability, can be edited. Make the results in this
                     * row group reportable or not based on its value.
                     */
                    for (int i = index + 1; i < manager.result.count(analysis); i++ ) {
                        data = manager.result.get(analysis, i, 0);
                        if ( !DataBaseUtil.isSame(rowGroup, data.getRowGroup()))
                            break;
                        data.setIsReportable(reportable);
                        row = table.getRowAt(r + i);
                        table.setValueAt(r + i, 0, reportable);
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
                            fv = getFormatter(analysis.getTestId()).format(data.getResultGroup(),
                                                                           analysis.getUnitOfMeasureId(),
                                                                           value.getDisplay());
                            if (fv != null) {
                                data.setValue(fv.getDisplay());
                                data.setTestResultId(fv.getId());
                                data.setTypeId(fv.getType());
                            } else {
                                /*
                                 * the value is not valid
                                 */
                                table.addException(r,
                                                   c,
                                                   new Exception(Messages.get()
                                                                         .illegalResultValueException()));
                                return;
                            }
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            e.printStackTrace();
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
                }
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

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = manager.getUid(analysis);
                else
                    uid = null;

                displayResults(uid);
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
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

                displayResults(uid);
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private void displayResults(String uid) {
        /*
         * don't redraw unless the data has changed
         */
        if (uid != null)
            analysis = (AnalysisViewDO)manager.getObject(uid);
        else
            analysis = null;

        if ( !isVisible)
            return;

        table.setVisible(analysis != null);
        /*
         * resets the table's view, so that if its model is changed, it shows
         * its headers and columns correctly, otherwise, problems like widths of
         * the columns not being correct or the headers not showing, may happen
         */
        table.onResize();

        if (DataBaseUtil.isDifferent(displayedUid, uid)) {
            displayedUid = uid;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null) {
            sectId = getSectionId();
            statId = getStatusId();

            if (sectId == null) {
                canEdit = true;
                return;
            }
            try {
                sect = SectionCache.getById(getSectionId());
                perm = UserCache.getPermission().getSection(sect.getName());
                canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                          !Constants.dictionary().ANALYSIS_RELEASED.equals(statId) &&
                          perm != null &&
                          (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception e) {
                Window.alert("canEdit:" + e.getMessage());
                e.printStackTrace();
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private ArrayList<Row> getTableModel() {
        int i, j;
        ResultViewDO data;
        Row row;
        ArrayList<Row> model;
        ArrayList<Integer> dictIds;
        ResultCell.Value value;

        model = new ArrayList<Row>();
        if (analysis == null)
            return model;

        resetColumns();

        dictIds = new ArrayList<Integer>();
        for (i = 0; i < manager.result.count(analysis); i++ ) {
            /*
             * create header row
             */
            if (manager.result.isHeader(analysis, i)) {
                row = new HeaderRow(table.getColumnCount());
                row.setCell(0, Messages.get().reportable());
                row.setCell(1, Messages.get().analyte());
                row.setCell(2, Messages.get().value());
                for (j = 1; j < manager.result.count(analysis, i); j++ ) {
                    data = manager.result.get(analysis, i, j);
                    row.setCell(j + 2, data.getAnalyte());
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
                }

                /*
                 * create the value to be set in the cell for this result
                 */

                if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(data.getTypeId())) {
                    if (data.getValue() != null)
                        dictIds.add(Integer.valueOf(data.getValue()));
                    value = new ResultCell.Value(null, data.getValue());
                } else {
                    value = new ResultCell.Value(data.getValue(), null);
                }

                row.setCell(j + 2, value);
            }
            row.setData(i);
            model.add(row);
        }

        try {
            /*
             * For type dictionary, the displayed text is looked up from the
             * cache. The following is done to fetch and put the dictionary
             * records needed for the results, in the cache, all at once so that
             * they won't have to be fetched one at a time.
             */
            if (dictIds.size() > 0)
                DictionaryCache.getByIds(dictIds);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return model;
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

    private ArrayList<Item<Integer>> getDictionaryModel(Integer testId, Integer resultGroup,
                                                        Integer unitId) throws Exception {
        String key;
        ResultFormatter rf;
        ArrayList<Item<Integer>> model;
        ArrayList<FormattedValue> values;

        if (dictionaryModel == null)
            dictionaryModel = new HashMap<String, ArrayList<Item<Integer>>>();

        key = testId + ":" + resultGroup + ":" + (unitId == null ? 0 : unitId);
        model = dictionaryModel.get(key);
        if (model == null) {
            rf = getFormatter(testId);
            /*
             * if all the ranges for this unit in this result group are
             * dictionary values, then create a dropdown model from them
             */
            if (rf != null && rf.hasAllDictionary(resultGroup, unitId)) {
                values = rf.getDictionaryValues(resultGroup, unitId);
                if (values != null) {
                    model = new ArrayList<Item<Integer>>();
                    model.add(new Item<Integer>(null, ""));
                    for (FormattedValue v : values)
                        model.add(new Item<Integer>(v.getId(), v.getDisplay()));
                }
            }
        }
        /*
         * this ensures that even if a model was not found above, it's not
         * looked up again
         */
        dictionaryModel.put(key, model);

        return model;
    }

    private ResultFormatter getFormatter(Integer testId) throws Exception {
        TestManager tm;

        if ( ! (parentScreen instanceof CacheProvider)) {
            Window.alert("Parent screen must implement " + CacheProvider.class.toString());
            return null;
        }
        tm = ((CacheProvider)parentScreen).get(testId, TestManager.class);
        return tm.getFormatter();
    }

    private void resetColumns() {
        int currNumCols, reqNumCols;
        Column col;

        reqNumCols = manager.result.maxColumns(analysis) + 2;
        currNumCols = table.getColumnCount();

        if (reqNumCols == currNumCols)
            return;

        /*
         * add columns to the table if it has less columns than needed to show
         * all column analytes, otherwise remove columns
         */
        if (reqNumCols > currNumCols) {
            for (int i = currNumCols; i < reqNumCols; i++ ) {
                col = table.addColumn(null, Messages.get().alphabet().substring(i, i + 1));
                if (i > 1) {
                    col.setCellRenderer(new ResultCell());
                    col.setWidth(200);
                }
            }
        } else {
            for (int i = currNumCols; i > reqNumCols; i-- )
                table.removeColumnAt(i - 1);
        }
    }
}