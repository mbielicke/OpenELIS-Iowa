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

import java.util.ArrayList;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

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

    private static ResultTabUIBinder uiBinder = GWT.create(ResultTabUIBinder.class);

    public enum Action {
        RESULT_HISTORY, REFLEX_ADDED
    };

    @UiField
    protected Button            addResultButton, removeResultButton, suggestionsButton,
                    popoutTable, checkAllButton, uncheckAllButton;
    @UiField
    protected Table             resultsTable;

    protected ResultTabUI       resultPopoutScreen;
    protected SampleManager1    manager;
    protected GetMatchesHandler resultMatchesHandler;
    protected AnalysisManager   analysisMan;
    protected AnalysisViewDO    analysis, emptyAnalysis;
    protected Screen            parentScreen;
    protected String            displayedUid;
    protected boolean           canEdit, isVisible;

    public ResultTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        addScreenHandler(resultsTable, "testResultsTable", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                resultsTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                resultsTable.setEnabled(true);
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

        addScreenHandler(suggestionsButton, "suggestionsButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                suggestionsButton.setEnabled(false);
            }
        });

        addScreenHandler(popoutTable, "popoutTable", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                popoutTable.setEnabled(false);
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

        addScreenHandler(popoutTable, "popoutTable", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {

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
        if ( !DataBaseUtil.isSame(this.manager, manager))
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

        resultsTable.setVisible(analysis != null);

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
            } catch (Exception anyE) {
                Window.alert("canEdit:" + anyE.getMessage());
            }
        }
    }

    private ArrayList<Row> getTableModel() {
        int j;
        ResultViewDO data;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (analysis == null)
            return model;

        resizeTable();

        for (int i = 0; i < manager.result.count(analysis); i++ ) {
            /*
             * create header row
             */
            if (manager.result.isHeader(analysis, i)) {
                row = new HeaderRow(resultsTable.getColumnCount());
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
            row = new Row(resultsTable.getColumnCount());
            for (j = 0; j < manager.result.count(analysis, i); j++ ) {
                data = manager.result.get(analysis, i, j);
                if (j == 0) {
                    row.setCell(0, data.getIsReportable());
                    row.setCell(1, data.getAnalyte());
                }
                row.setCell(j + 2, data.getValue());
            }
            row.setData(i);
            model.add(row);
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

    private void resizeTable() {
        int currNumCols, reqNumCols;

        reqNumCols = manager.result.maxColumns(analysis) + 2;
        currNumCols = resultsTable.getColumnCount();

        if (reqNumCols == currNumCols)
            return;

        /*
         * add columns to the table if it has less columns than needed to show
         * all column analytes, otherwise remove columns
         */
        if (reqNumCols > currNumCols) {
            for (int i = currNumCols; i < reqNumCols; i++ )
                resultsTable.addColumn(null, Messages.get().alphabet().substring(i, i + 1));
        } else {
            for (int i = currNumCols; i > reqNumCols; i-- )
                resultsTable.removeColumnAt(i - 1);
        }
    }
}