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
package org.openelis.modules.worksheetCompletion.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.analyte.client.AnalyteService;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.sample1.client.ResultCell;
import org.openelis.modules.sample1.client.ResultCell.Value;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
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
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;

public class WorksheetItemTabUI extends Screen {

    @UiTemplate("WorksheetItemTab.ui.xml")
    interface WorksheetItemTabUiBinder extends UiBinder<Widget, WorksheetItemTabUI> {
    };
    
    private static WorksheetItemTabUiBinder               uiBinder = GWT.create(WorksheetItemTabUiBinder.class);

    private boolean                                       canEdit, isVisible, redraw;
    private WorksheetManager1                             manager;

    @UiField
    protected AutoComplete                                unitOfMeasureId;
    @UiField
    protected Button                                      editMultipleButton, selectAllButton,
                                                          unselectAllButton;
    @UiField
    protected Dropdown<Integer>                           analysisStatusId, qcLink;
    @UiField
    protected Table                                       worksheetItemTable;

    protected ArrayList<Item<Integer>>                    qcLinkModel;
    protected ArrayList<String>                           manualAnalysisUids, templateAnalysisUids;
    protected Confirm                                     worksheetRemoveDuplicateQCConfirm,
                                                          worksheetRemoveQCConfirm,
                                                          worksheetRemoveLastOfQCConfirm,
                                                          worksheetSaveConfirm,
                                                          worksheetExitConfirm;
    protected EventBus                                    parentBus;
    protected HashMap<Integer, DictionaryDO>              unitOfMeasureMap;
    protected HashMap<Integer, Integer>                   transferRowMap;
    protected HashMap<Integer, TestManager>               testManagers;
    protected HashMap<Integer, HashMap<Integer, Integer>> resultGroupMap;
    protected HashMap<MenuItem, Integer>                  templateMap;
    protected HashMap<String, ArrayList<QcLotViewDO>>     qcChoices;
    protected HashMap<String, ArrayList<Item<Integer>>>   unitModels, dictionaryResultMap;
    protected QcLookupScreen                              qcLookupScreen;
    protected Screen                                      parentScreen;
    protected TestWorksheetDO                             testWorksheetDO;
    protected TestWorksheetManager                        twManager;
    protected WorksheetEditMultiplePopupUI                editMultiplePopup;
    protected WorksheetLookupScreenUI                     wLookupScreen;
    
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
        
        manager = null;
        dictionaryResultMap = new HashMap<String, ArrayList<Item<Integer>>>();
        resultGroupMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        templateMap = new HashMap<MenuItem, Integer>();
        testManagers = new HashMap<Integer, TestManager>();
        transferRowMap = new HashMap<Integer, Integer>();
}

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        addScreenHandler(worksheetItemTable, "worksheetItemTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetItemTable.setModel(new ArrayList<Row>());
                worksheetItemTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetItemTable.setEnabled(true);
                worksheetItemTable.setAllowMultipleSelection(true);
                worksheetItemTable.unselectAll();
            }
        });

        worksheetItemTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                if (worksheetItemTable.getSelectedRows().length > 1 && isState(UPDATE) &&
                    canEdit && !getUpdateTransferMode())
                    editMultipleButton.setEnabled(true);
                else
                    editMultipleButton.setEnabled(false);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<FormattedValue> values;
                DataObject data;
                HashMap<Integer, Integer> rgRow;
                Integer rg, testId, unitId;
                ResultFormatter rf;
                SectionPermission perm;
                String accessionNumber, resultKey;
                TestManager tMan;
                WorksheetAnalysisViewDO waVDO;
                WorksheetQcResultViewDO wqrVDO;
                WorksheetResultViewDO wrVDO;
                
                if (!isState(UPDATE) || !canEdit || event.getCol() == 1 || event.getCol() == 2 ||
                    event.getCol() == 3 || event.getCol() == 4 || event.getCol() == 5 ||
                    event.getCol() == 6 || event.getCol() == 9) {
                    event.cancel();
                } else if (!getUpdateTransferMode() && (event.getCol() == 0 || event.getCol() == 7 ||
                                                        event.getCol() == 8 || event.getCol() == 10)) {
                    event.cancel();
                } else if (getUpdateTransferMode() && event.getCol() != 0 && event.getCol() != 7 &&
                                                      event.getCol() != 8 && event.getCol() != 10) {
                    event.cancel();
                } else {
                    data = manager.getObject((String)worksheetItemTable.getRowAt(event.getRow()).getData());
                    accessionNumber = worksheetItemTable.getValueAt(event.getRow(), 2);
                    if (data instanceof WorksheetItemDO ||
                        ((accessionNumber == null || accessionNumber.length() <= 0) && event.getCol() < 10)) {
                        event.cancel();
                    } else if (data instanceof WorksheetAnalysisViewDO) {
                        if (event.getCol() >= 10) {
                            event.cancel();
                        } else {
                            waVDO = (WorksheetAnalysisViewDO)data;
                            if (waVDO.getQcLotId() != null || waVDO.getFromOtherId() != null ||
                                Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                                Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
                                event.cancel();
                            }
                        }
                    } else if (data instanceof WorksheetQcResultViewDO) {
                        if (event.getCol() < 11) {
                            event.cancel();
                        } else {
                            wqrVDO = (WorksheetQcResultViewDO)data;
                            waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wqrVDO.getWorksheetAnalysisId()));
                            if (waVDO.getFromOtherId() != null)
                                event.cancel();
                            else
                                ((ResultCell)worksheetItemTable.getColumnAt(event.getCol()).getCellEditor()).setModel(null);
                        }
                    } else if (data instanceof WorksheetResultViewDO) {
                        wrVDO = (WorksheetResultViewDO)data;
                        waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wrVDO.getWorksheetAnalysisId()));
                        perm = UserCache.getPermission().getSection(waVDO.getSectionName());
                        if (waVDO.getFromOtherId() != null ||
                            Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                            Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
                            event.cancel();
                        } else if (perm == null || !perm.hasCompletePermission()) {
                            Window.alert(Messages.get().worksheet_completePermissionRequiredForOperation(waVDO.getSectionName(),
                                                                                                         Messages.get().edit()));
                            event.cancel();
                        } else if (event.getCol() >= 11) {
                            rgRow = resultGroupMap.get(((WorksheetResultViewDO)data).getTestAnalyteId());
                            rg = rgRow.get(event.getCol() - 1);
                            testId = waVDO.getTestId();
                            unitId = waVDO.getUnitOfMeasureId();

                            resultKey = testId + ":" + rg + ":" + (unitId == null ? 0 : unitId);
                            model = dictionaryResultMap.get(resultKey);
                            if (model == null) {
                                try {
                                    tMan = testManagers.get(testId);
                                    rf = tMan.getFormatter();
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
                                    dictionaryResultMap.put(resultKey, model);
                                } catch (Exception e) {
                                    Window.alert(e.getMessage());
                                    logger.log(Level.SEVERE, e.getMessage(), e);
                                    event.cancel();
                                }
                            }
                            ((ResultCell)worksheetItemTable.getColumnAt(event.getCol()).getCellEditor()).setModel(model);
                        }
                    }
                }
            }
        });
        
        worksheetItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                DataObject data;
                Integer rowIndex;
                Object val;
                Row row;
                Value value;
                WorksheetAnalysisViewDO waVDO;
                WorksheetResultViewDO wrVDO;
                WorksheetQcResultViewDO wqrVDO;

                r = event.getRow();
                c = event.getCol();
                
                row = worksheetItemTable.getRowAt(r);
                val = worksheetItemTable.getValueAt(r,c);

                data = manager.getObject((String)row.getData());
                if (data instanceof WorksheetAnalysisViewDO) {
                    waVDO = (WorksheetAnalysisViewDO)data;
                    wrVDO = null;
                    wqrVDO = null;
                } else if (data instanceof WorksheetResultViewDO) {
                    wqrVDO = null;
                    wrVDO = (WorksheetResultViewDO)data;
                    waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wrVDO.getWorksheetAnalysisId()));
                } else if (data instanceof WorksheetQcResultViewDO) {
                    wrVDO = null;
                    wqrVDO = (WorksheetQcResultViewDO)data;
                    waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wqrVDO.getWorksheetAnalysisId()));
                } else {
                    return;
                }
                
                switch (c) {
                    case 0:
                        if ("Y".equals(val)) {
                            rowIndex = transferRowMap.get(waVDO.getAnalysisId());
                            if (rowIndex == null) {
                                transferRowMap.put(waVDO.getAnalysisId(), r);
                            } else {
                                worksheetItemTable.setValueAt(rowIndex, 0, "N");
                                transferRowMap.put(waVDO.getAnalysisId(), r);
                            }
                        } else if ("N".equals(val)) {
                            transferRowMap.remove(waVDO.getAnalysisId());
                        }
                        break;
                    case 7:
                        if (val != null)
                            waVDO.setUnitOfMeasureId(((AutoCompleteValue)val).getId());
                        else
                            waVDO.setUnitOfMeasureId(null);
                        break;
                    case 8:
                        waVDO.setStatusId((Integer)val);
                        break;
                    case 10:
                        wrVDO.setIsReportable((String)val);
                        break;
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                        value = (Value)val;
                        if (wrVDO != null)
                            wrVDO.setValueAt(c - 11, value.getDisplay());
                        else if (wqrVDO != null)
                            wqrVDO.setValueAt(c - 11, value.getDisplay());
                        break;
                }
            }
        });

        unitOfMeasureId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<IdNameVO> list;
                DataObject data;
                WorksheetAnalysisViewDO waVDO;

                try {
                    model = new ArrayList<Item<Integer>>();

                    waVDO = null;
                    data = manager.getObject((String)worksheetItemTable.getRowAt(worksheetItemTable.getSelectedRow())
                                                                       .getData());
                    if (data instanceof WorksheetAnalysisViewDO) {
                        waVDO = (WorksheetAnalysisViewDO)data;
                    } else if (data instanceof WorksheetResultViewDO) {
                        waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetResultViewDO)data).getWorksheetAnalysisId()));
                    } else if (data instanceof WorksheetQcResultViewDO) {
                        waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetQcResultViewDO)data).getWorksheetAnalysisId()));
                    }

                    if (waVDO != null) {
                        list = WorksheetService1.get()
                                                .fetchUnitsForWorksheetAutocomplete(waVDO.getAnalysisId(),
                                                                                    QueryFieldUtil.parseAutocomplete(event.getMatch()) +
                                                                                    "%");
                        for (IdNameVO unitVO : list)
                            model.add(new Item<Integer>(unitVO.getId(), unitVO.getName()));
                    }
                    unitOfMeasureId.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                int i, j, l;
                AnalyteDO aDO;
                ArrayList<ArrayList<TestAnalyteViewDO>> testAnalytes;
                ArrayList<IdNameVO> colNames;
                HashMap<Integer, AnalyteDO> analytes;
                HashMap<Integer, Integer> rgRow;
                HashMap<String, Integer> colMap;
                TestAnalyteViewDO taVDO;
                TestManager tMan;
                WorksheetAnalysisViewDO waDO;
                WorksheetItemDO wiDO;

                editMultipleButton.setEnabled(false);
                selectAllButton.setEnabled(false);
                unselectAllButton.setEnabled(false);
                dictionaryResultMap.clear();
                resultGroupMap.clear();
                testManagers.clear();
                transferRowMap.clear();
                
                if (isState(UPDATE) && !getUpdateTransferMode()) {
                    if (canEdit) {
                        selectAllButton.setEnabled(true);
                        unselectAllButton.setEnabled(true);
                    }
                    analytes = new HashMap<Integer, AnalyteDO>();
                    colMap = new HashMap<String, Integer>();
                    try {
                        colNames = WorksheetService1.get().getColumnNames(manager.getWorksheet().getFormatId());
                        for (IdNameVO idVO : colNames)
                            colMap.put(idVO.getName(), idVO.getId());
                    } catch (Exception anyE) {
                        Window.alert("Error loading column names for format; " + anyE.getMessage());
                        logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                    }
                    try {
                        for (i = 0; i < manager.item.count(); i++) {
                            wiDO = (WorksheetItemDO)manager.item.get(i);
                            for (j = 0; j < manager.analysis.count(wiDO); j++) {
                                waDO = manager.analysis.get(wiDO, j);
                                if (waDO.getAnalysisId() != null) {
                                    tMan = testManagers.get(waDO.getTestId());
                                    if (tMan == null) {
                                        tMan = TestService.get().fetchWithAnalytesAndResults(waDO.getTestId());
                                        testManagers.put(waDO.getTestId(), tMan);
                                        testAnalytes = tMan.getTestAnalytes().getAnalytes();
                                        for (ArrayList<TestAnalyteViewDO> taRow : testAnalytes) {
                                            rgRow = new HashMap<Integer, Integer>();
                                            for (l = 0; l < taRow.size(); l++) {
                                                taVDO = taRow.get(l);
                                                if (l == 0) {
                                                    resultGroupMap.put(taVDO.getId(), rgRow);
                                                    if (colMap.get("raw_value") != null)
                                                        rgRow.put(colMap.get("raw_value"), taVDO.getResultGroup());
                                                    if (colMap.get("final_value") != null)
                                                        rgRow.put(colMap.get("final_value"), taVDO.getResultGroup());
                                                } else {
                                                    aDO = analytes.get(taVDO.getAnalyteId());
                                                    if (aDO == null) {
                                                        aDO = AnalyteService.get().fetchById(taVDO.getAnalyteId());
                                                        analytes.put(taVDO.getAnalyteId(), aDO);
                                                    }
                                                    if (colMap.get(aDO.getExternalId()) != null)
                                                        rgRow.put(colMap.get(aDO.getExternalId()), taVDO.getResultGroup());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception anyE) {
                        Window.alert("Error loading result group mappings; " + anyE.getMessage());
                        logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                    }
                }
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayItemData();
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
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
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
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int i, a, r, rowIndex;
        Row row;
        WorksheetAnalysisViewDO waVDO;
        WorksheetItemDO wiDO;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;

        if ((worksheetItemTable.getRowCount() > 0 && (manager == null || manager.item.count() == 0)) ||
            (worksheetItemTable.getRowCount() == 0 && manager != null && manager.item.count() > 0)) {
            redraw = true;
        } else if (manager != null) {
            rowIndex = 0;
            items:
            for (i = 0; i < manager.item.count(); i++) {
                wiDO = manager.item.get(i);
                if (manager.analysis.count(wiDO) > 0) {
                    for (a = 0; a < manager.analysis.count(wiDO); a++) {
                        waVDO = manager.analysis.get(wiDO, a);
                        if (manager.result.count(waVDO) > 0) {
                            for (r = 0; r < manager.result.count(waVDO); r++) {
                                wrVDO = manager.result.get(waVDO, r);
                                row = worksheetItemTable.getRowAt(rowIndex);
                                if (!manager.getUid(wrVDO).equals(row.getData()) || 
                                    DataBaseUtil.isDifferent(wrVDO.getAnalyteName(), row.getCell(9)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getIsReportable(), row.getCell(10)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(0), row.getCell(11)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(1), row.getCell(12)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(2), row.getCell(13)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(3), row.getCell(14)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(4), row.getCell(15)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(5), row.getCell(16)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(6), row.getCell(17)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(7), row.getCell(18)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(8), row.getCell(19)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(9), row.getCell(20)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(10), row.getCell(21)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(11), row.getCell(22)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(12), row.getCell(23)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(13), row.getCell(24)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(14), row.getCell(25)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(15), row.getCell(26)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(16), row.getCell(27)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(17), row.getCell(28)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(18), row.getCell(29)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(19), row.getCell(30)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(20), row.getCell(31)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(21), row.getCell(32)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(22), row.getCell(33)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(23), row.getCell(34)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(24), row.getCell(35)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(25), row.getCell(36)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(26), row.getCell(37)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(27), row.getCell(38)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(28), row.getCell(39)) ||
                                    DataBaseUtil.isDifferent(wrVDO.getValueAt(29), row.getCell(40))) {
                                    redraw = true;
                                    break items;
                                }
                                rowIndex++;
                            }
                        } else if (manager.qcResult.count(waVDO) > 0) {
                            for (r = 0; r < manager.qcResult.count(waVDO); r++) {
                                wqrVDO = manager.qcResult.get(waVDO, r);
                                row = worksheetItemTable.getRowAt(rowIndex);
                                if (!manager.getUid(wqrVDO).equals(row.getData()) || 
                                    DataBaseUtil.isDifferent(wqrVDO.getAnalyteName(), row.getCell(9)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(0), row.getCell(11)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(1), row.getCell(12)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(2), row.getCell(13)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(3), row.getCell(14)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(4), row.getCell(15)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(5), row.getCell(16)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(6), row.getCell(17)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(7), row.getCell(18)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(8), row.getCell(19)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(9), row.getCell(20)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(10), row.getCell(21)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(11), row.getCell(22)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(12), row.getCell(23)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(13), row.getCell(24)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(14), row.getCell(25)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(15), row.getCell(26)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(16), row.getCell(27)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(17), row.getCell(28)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(18), row.getCell(29)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(19), row.getCell(30)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(20), row.getCell(31)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(21), row.getCell(32)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(22), row.getCell(33)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(23), row.getCell(34)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(24), row.getCell(35)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(25), row.getCell(36)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(26), row.getCell(37)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(27), row.getCell(38)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(28), row.getCell(39)) ||
                                    DataBaseUtil.isDifferent(wqrVDO.getValueAt(29), row.getCell(40))) {
                                    redraw = true;
                                    break items;
                                }
                                rowIndex++;
                            }
                        } else {
                            row = worksheetItemTable.getRowAt(rowIndex);
                            if (!manager.getUid(waVDO).equals(row.getData()) || 
                                DataBaseUtil.isDifferent(waVDO.getAccessionNumber(), row.getCell(2)) ||
                                DataBaseUtil.isDifferent(waVDO.getDescription(), row.getCell(3)) ||
                                DataBaseUtil.isDifferent(waVDO.getWorksheetAnalysisId(), row.getCell(4)) ||
                                DataBaseUtil.isDifferent(waVDO.getTestName(), row.getCell(5)) ||
                                DataBaseUtil.isDifferent(waVDO.getMethodName(), row.getCell(6)) ||
                                DataBaseUtil.isDifferent(waVDO.getUnitOfMeasureId(), row.getCell(7)) ||
                                DataBaseUtil.isDifferent(waVDO.getStatusId(), row.getCell(8))) {
                                redraw = true;
                                break items;
                            }
                            rowIndex++;
                        }
                    }
                } else {
                    row = worksheetItemTable.getRowAt(rowIndex);
                    if (!manager.getUid(wiDO).equals(row.getData()) || 
                        DataBaseUtil.isDifferent(wiDO.getPosition(), row.getCell(1))) {
                        redraw = true;
                        break;
                    }
                    rowIndex++;
                }
            }
        }
        
        displayItemData();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("selectAllButton")
    public void selectAll(ClickEvent event) {
        worksheetItemTable.selectAll();
        if (worksheetItemTable.getSelectedRows().length > 0)
            editMultipleButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    @UiHandler("unselectAllButton")
    public void unselectAll(ClickEvent event) {
        worksheetItemTable.unselectAll();
        editMultipleButton.setEnabled(false);
    }

    protected ArrayList<WorksheetAnalysisViewDO> getTransferSelection() {
        ArrayList<WorksheetAnalysisViewDO> list;
        DataObject data;
        WorksheetAnalysisViewDO waVDO;
        WorksheetResultViewDO wrVDO;
        WorksheetQcResultViewDO wqrVDO;

        list = new ArrayList<WorksheetAnalysisViewDO>();
        for (Row row : worksheetItemTable.getModel()) {
            if (!"Y".equals(row.getCell(0)))
                continue;

            data = manager.getObject((String)row.getData());
            if (data instanceof WorksheetAnalysisViewDO) {
                waVDO = (WorksheetAnalysisViewDO)data;
            } else if (data instanceof WorksheetResultViewDO) {
                wrVDO = (WorksheetResultViewDO)data;
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wrVDO.getWorksheetAnalysisId()));
            } else if (data instanceof WorksheetQcResultViewDO) {
                wqrVDO = (WorksheetQcResultViewDO)data;
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(wqrVDO.getWorksheetAnalysisId()));
            } else {
                continue;
            }
            list.add(waVDO);
        }
        return list;
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
        boolean dupMsg;
        int i, j, k, l, r, rowSize;
        ArrayList<IdNameVO> headers;
        ArrayList<Row> model;
        Column col;
        Integer rowIndex;
        Row row;
        WorksheetAnalysisViewDO waDO;
        WorksheetItemDO wiDO;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;

        qcLinkModel.clear();
        qcLinkModel.add(new Item<Integer>(null, ""));
        transferRowMap.clear();
        
        r = 0;
        dupMsg = false;
        model = new ArrayList<Row>();
        if (manager != null) {
            try {
                headers = new ArrayList<IdNameVO>();
                try {
                    headers = WorksheetService1.get().getHeaderLabelsForScreen(manager);
                } catch (Exception anyE) {
                    Window.alert("Error loading headers for format; " + anyE.getMessage());
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }

                rowSize = Math.min(headers.size(), 30) + 11;
                for (i = 11; i < rowSize; i++) {
                    if (i == worksheetItemTable.getColumnCount())
                        worksheetItemTable.addColumn();
                    col = worksheetItemTable.getColumnAt(i);
                    col.setLabel(headers.get(i - 11).getName());
                    col.setCellRenderer(new ResultCell());
                    col.setWidth(150);
                }
                while (rowSize < worksheetItemTable.getColumnCount())
                    worksheetItemTable.removeColumnAt(rowSize);

                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = (WorksheetItemDO)manager.item.get(i);
    
                    row = new Row(rowSize);
                    row.setCell(0, "N");
                    row.setCell(1, wiDO.getPosition());
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        qcLinkModel.add(new Item<Integer>(waDO.getId(), waDO.getAccessionNumber() +
                                                                        " (" + wiDO.getPosition() +
                                                                        ")"));
    
                        if (j > 0) {
                            row = new Row(rowSize);
                            row.setCell(0, "N");
                            row.setCell(1, wiDO.getPosition());
                        }
                        row.setCell(2, waDO.getAccessionNumber());
                        
                        if (waDO.getAnalysisId() != null) {
                            if (getUpdateTransferMode()) {
                                if (transferRowMap.containsKey(waDO.getAnalysisId())) {
                                    row.setCell(0, "N");
                                    rowIndex = transferRowMap.get(waDO.getAnalysisId());
                                    if (rowIndex != null) {
                                        model.get(rowIndex).setCell(0, "N");
                                        transferRowMap.put(waDO.getAnalysisId(), null);
                                    }
                                    dupMsg = true;
                                } else if (Constants.dictionary().ANALYSIS_COMPLETED.equals(waDO.getStatusId()) ||
                                           Constants.dictionary().ANALYSIS_RELEASED.equals(waDO.getStatusId()) ||
                                           Constants.dictionary().ANALYSIS_CANCELLED.equals(waDO.getStatusId())) {
                                    row.setCell(0, "N");
                                } else {
                                    row.setCell(0, "Y");
                                    transferRowMap.put(waDO.getAnalysisId(), r);
                                }
                            }
                            row.setCell(3, waDO.getDescription());
                            row.setCell(4, waDO.getWorksheetAnalysisId());
                            row.setCell(5, waDO.getTestName());
                            row.setCell(6, waDO.getMethodName());
                            if (waDO.getUnitOfMeasureId() != null) {
                                row.setCell(7, new AutoCompleteValue(waDO.getUnitOfMeasureId(),
                                                                     waDO.getUnitOfMeasure()));
                            } else {
                                row.setCell(7, new AutoCompleteValue());
                            }
                            row.setCell(8, waDO.getStatusId());
                            
                            for (k = 0; k < manager.result.count(waDO); k++) {
                                wrVDO = manager.result.get(waDO, k);
                                if (k > 0) {
                                    row = new Row(rowSize);
                                    row.setCell(0, "N");
                                    row.setCell(1, "");
                                    row.setCell(2, "");
                                    row.setCell(3, "");
                                    row.setCell(4, "");
                                    row.setCell(5, "");
                                    row.setCell(6, "");
                                    row.setCell(7, new AutoCompleteValue());
                                    row.setCell(8, "");
                                }
                                row.setCell(9, wrVDO.getAnalyteName());
                                row.setCell(10, wrVDO.getIsReportable());
                                for (l = 11; l < rowSize; l++)
                                    row.setCell(l, new ResultCell.Value(wrVDO.getValueAt(l - 11), null));
                                row.setData(manager.getUid(wrVDO)); 
                                model.add(row);
                                r++;
                            }
                            if (k == 0) {
                                row.setCell(9, "NO ANAlYTES FOOUND");
                                row.setCell(10, null);
                                for (l = 11; l < rowSize; l++)
                                    row.setCell(l, new ResultCell.Value(null, null));
                                row.setData(manager.getUid(waDO)); 
                                model.add(row);
                                r++;
                            }
                        } else if (waDO.getQcLotId() != null) {
                            row.setCell(0, "N");
                            row.setCell(3, waDO.getDescription());
                            row.setCell(4, waDO.getWorksheetAnalysisId());
                            row.setCell(5, "");
                            row.setCell(6, "");
                            row.setCell(7, new AutoCompleteValue());
                            row.setCell(8, "");
                            
                            for (k = 0; k < manager.qcResult.count(waDO); k++) {
                                wqrVDO = manager.qcResult.get(waDO, k);
                                if (k > 0) {
                                    row = new Row(rowSize);
                                    row.setCell(0, "N");
                                    row.setCell(1, "");
                                    row.setCell(2, "");
                                    row.setCell(3, "");
                                    row.setCell(4, "");
                                    row.setCell(5, "");
                                    row.setCell(6, "");
                                    row.setCell(7, new AutoCompleteValue());
                                    row.setCell(8, "");
                                }
                                row.setCell(9, wqrVDO.getAnalyteName());
                                row.setCell(10, null);
                                for (l = 11; l < rowSize; l++)
                                    row.setCell(l, new ResultCell.Value(wqrVDO.getValueAt(l - 11), null));
                                row.setData(manager.getUid(wqrVDO)); 
                                model.add(row);
                                r++;
                            }
                            if (k == 0) {
                                row.setCell(9, "NO ANAlYTES FOOUND");
                                row.setCell(10, null);
                                for (l = 11; l < rowSize; l++)
                                    row.setCell(l, new ResultCell.Value(null, null));
                                row.setData(manager.getUid(waDO)); 
                                model.add(row);
                                r++;
                            }
                        }
                    }
                    if (j == 0) {
                        row.setCell(3, "ERROR: Item with no analyses");
                        row.setData(manager.getUid(wiDO));
                        model.add(row);
                        r++;
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        /*
         * Reload the model for the QC Link column
         */
        qcLink.setModel(qcLinkModel);

        if (dupMsg)
            Window.alert(Messages.get().worksheet_uncheckedDuplicateAnalyses());
        
        return model;
    }

    private boolean getUpdateTransferMode() {
        return ((WorksheetCompletionScreenUI)parentScreen).getUpdateTransferMode();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("editMultipleButton")
    protected void editMultiple(ClickEvent event) {
        ArrayList<WorksheetAnalysisViewDO> dataList;
        DataObject data;
        HashSet<Integer> waIds;
        Integer testId, unitId;
        ModalWindow modal;
        ResultFormatter rf;
        StringBuilder errorList;
        TestManager tMan;
        WorksheetAnalysisViewDO waVDO;
        
        dataList = new ArrayList<WorksheetAnalysisViewDO>();
        errorList = new StringBuilder();
        rf = null;
        testId = null;
        unitId = null;
        waIds = new HashSet<Integer>();
        for (Integer rowIndex : worksheetItemTable.getSelectedRows()) {
            data = (DataObject) manager.getObject((String)worksheetItemTable.getRowAt(rowIndex).getData());
            if (data instanceof WorksheetAnalysisViewDO) {
                waVDO = (WorksheetAnalysisViewDO)data;
                if (waIds.contains(waVDO.getId()))
                    continue;
            } else if (data instanceof WorksheetResultViewDO) {
                if (waIds.contains(((WorksheetResultViewDO)data).getWorksheetAnalysisId()))
                    continue;
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetResultViewDO)data).getWorksheetAnalysisId()));
            } else if (data instanceof WorksheetQcResultViewDO) {
                if (waIds.contains(((WorksheetQcResultViewDO)data).getWorksheetAnalysisId()))
                    continue;
                waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetQcResultViewDO)data).getWorksheetAnalysisId()));
            } else {
                continue;
            }

            if (waVDO.getAnalysisId() != null) {
                if (testId != null && !testId.equals(waVDO.getTestId())) {
                    Window.alert(Messages.get().worksheet_oneTestForEditMultiple());
                    return;
                } else if (testId == null) {
                    testId = waVDO.getTestId();
                    try {
                        tMan = testManagers.get(testId);
                        rf = tMan.getFormatter();
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        return;
                    }
                }
                
                if (unitId == null) {
                    if (waVDO.getUnitOfMeasureId() != null)
                        unitId = 0;
                    else
                        unitId = waVDO.getUnitOfMeasureId();
                } else if (!unitId.equals(waVDO.getUnitOfMeasureId()) &&
                           unitId != 0 && waVDO.getUnitOfMeasureId() != null) {
                    Window.alert(Messages.get().worksheet_oneUnitForEditMultiple());
                    return;
                }
                
                if (Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()) ||
                    Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId())) {
                    errorList.append("\n").append("\t").append("Row: ").append(rowIndex + 1);
                    continue;
                }
            }
            waIds.add(waVDO.getId());
            dataList.add(waVDO);
        }
        
        if (errorList.length() > 0) {
            Window.alert(Messages.get().worksheet_wrongStatusNoEditRows(errorList.toString()));
            return;
        }

        if (editMultiplePopup == null) {
            editMultiplePopup = new WorksheetEditMultiplePopupUI() {
                @Override
                public void ok() {
                    onDataChange();
                }
            };
        }
        
        modal = new ModalWindow();
        modal.setName(Messages.get().worksheet_editMultiple());
        modal.setContent(editMultiplePopup);
        modal.setSize("631px", "494px");
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        editMultiplePopup.setWindow(modal);
        editMultiplePopup.initialize();
        editMultiplePopup.setData(manager, testId, unitId, rf, dataList, resultGroupMap, dictionaryResultMap);
    }
}