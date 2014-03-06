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

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.qc.client.QcLookupScreen;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

public class WorksheetItemTabUI extends Screen {

    @UiTemplate("WorksheetItemTab.ui.xml")
    interface WorksheetItemTabUiBinder extends UiBinder<Widget, WorksheetItemTabUI> {
    };
    
    private static WorksheetItemTabUiBinder             uiBinder = GWT.create(WorksheetItemTabUiBinder.class);

    private boolean                                     canEdit, isVisible;
    private int                                         maxColumn;
    private WorksheetItemTabUI                          screen;
    private WorksheetManager1                           manager, displayedManager;

    @UiField
    protected AutoComplete                              unitOfMeasureId;
    @UiField
    protected Button                                    editMultipleButton;
    @UiField
    protected CheckBox                                  resultIsReportable;
    @UiField
    protected Dropdown<Integer>                         analysisStatusId, qcLink;
    @UiField
    protected Table                                     worksheetItemTable;
    
    
    
    protected ArrayList<Item<Integer>>                  qcLinkModel;
    protected ArrayList<String>                         manualAnalysisUids, templateAnalysisUids;
    protected Confirm                                   worksheetRemoveDuplicateQCConfirm,
                                                        worksheetRemoveQCConfirm, 
                                                        worksheetRemoveLastOfQCConfirm, 
                                                        worksheetSaveConfirm, worksheetExitConfirm;
    protected HashMap<Integer, Exception>               qcErrors;
    protected HashMap<Integer, DictionaryDO>            unitOfMeasureMap;
    protected HashMap<MenuItem, Integer>                templateMap;
    protected HashMap<String, ArrayList<QcLotViewDO>>   qcChoices;
    protected HashMap<String, ArrayList<Item<Integer>>> unitModels;
    protected QcLookupScreen                            qcLookupScreen;
    protected Screen                                    parentScreen;
    protected TestWorksheetDO                           testWorksheetDO;
    protected TestWorksheetManager                      twManager;
    protected WorksheetLookupScreenUI                   wLookupScreen;
    
    /**
     * Flags that specifies what type of data is in each row
     */
    public enum QC_SOURCE {
        MANUAL, TEMPLATE;
    };
    
    public WorksheetItemTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        if (bus != null)
            setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        
        manager = null;
        displayedManager = null;
        qcErrors = new HashMap<Integer, Exception>();
        templateMap = new HashMap<MenuItem, Integer>();
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        screen = this;

        addScreenHandler(worksheetItemTable, "worksheetItemTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
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
                if (worksheetItemTable.getSelectedRows().length > 1 && isState(ADD, UPDATE) && canEdit)
                    editMultipleButton.setEnabled(true);
                else
                    editMultipleButton.setEnabled(false);
            }
        });
        
        worksheetItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                DataObject data;
                SectionPermission perm;
                String accessionNumber;
                WorksheetAnalysisViewDO waVDO;
                
                if (!isState(UPDATE) || !canEdit || event.getCol() == 1 || event.getCol() == 2 ||
                    event.getCol() == 3 || event.getCol() == 4 || event.getCol() == 5 ||
                    event.getCol() == 6 || event.getCol() == 9 || event.getCol() > maxColumn) {
                    event.cancel();
                } else if (!getUpdatePushMode() && (event.getCol() == 0 || event.getCol() == 7 || event.getCol() == 8 || event.getCol() == 10)) {
                    event.cancel();
                } else if (getUpdatePushMode() && event.getCol() != 0 && event.getCol() != 7 && event.getCol() != 8 && event.getCol() != 10) {
                    event.cancel();
                } else {
                    data = manager.getObject((String)worksheetItemTable.getRowAt(event.getRow()).getData());
                    accessionNumber = worksheetItemTable.getValueAt(event.getRow(), 2);
                    if (data instanceof WorksheetItemDO) {
                        event.cancel();
                    } else if ((accessionNumber == null || accessionNumber.length() <= 0) && event.getCol() < 10) {
                        event.cancel();
                    } else if (data instanceof WorksheetQcResultViewDO && event.getCol() < 11) {
                        event.cancel();
                    } else {
                        if (data instanceof WorksheetResultViewDO)
                            waVDO = (WorksheetAnalysisViewDO)manager.getObject(manager.getWorksheetAnalysisUid(((WorksheetResultViewDO)data).getWorksheetAnalysisId()));
                        else
                            waVDO = (WorksheetAnalysisViewDO)data;
                        perm = UserCache.getPermission().getSection(waVDO.getSectionName());
                        if (waVDO.getQcLotId() != null || waVDO.getFromOtherId() != null ||
                            Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
                            Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId())) {
                            event.cancel();
                        } else if (perm == null || !perm.hasCompletePermission()) {
                            Window.alert(Messages.get().worksheet_completePermissionRequiredForOperation(waVDO.getSectionName(),
                                                                                                         Messages.get().edit()));
                            event.cancel();
                        }
                    }
                }
            }
        });
        
        worksheetItemTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                DataObject data;
                Object val;
                Row row;
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
                        if (wrVDO != null)
                            wrVDO.setValueAt(c - 11, (String)val);
                        else if (wqrVDO != null)
                            wqrVDO.setValueAt(c - 11, (String)val);
                        break;
                }
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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                editMultipleButton.setEnabled(false);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayItemData();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(), parentScreen, new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                evaluateEdit();
                setState(event.getState());
            }
        });

        bus.addHandlerToSource(DataChangeEvent.getType(), parentScreen, new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
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
//        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        analysisStatusId.setModel(model);
        
        //
        // Set the reportable column to query mode so that it has three states
        //
        resultIsReportable.setQueryMode(true);
    }
    
    public void setData(WorksheetManager1 manager) {
        if (!DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
            evaluateEdit();
        }
    }
    
    private boolean getUpdatePushMode() {
        return ((WorksheetCompletionScreenUI)parentScreen).updatePushMode;
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null)
            canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                             .getStatusId());
    }

    private void displayItemData() {
        int i, a, count1, count2, count3, count4, count5, count6;
        boolean dataChanged;
        WorksheetAnalysisViewDO waDO1, waDO2;
        WorksheetItemDO wiDO1, wiDO2;

        if (!isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.item.count();
        count2 = manager == null ? 0 : manager.item.count();

        /*
         * find out if there's any difference between the item data of the two
         * managers
         */
        if (count1 == count2) {
            dataChanged = false;
            if (count1 != 0) {
                items:
                for (i = 0; i < count1; i++ ) {
                    wiDO1 = displayedManager.item.get(i);
                    wiDO2 = manager.item.get(i);
                    count3 = displayedManager.analysis.count(wiDO1);
                    count4 = manager.analysis.count(wiDO2);
                    if (count3 == count4) {
                        for (a = 0; a < count3; a++ ) {
                            waDO1 = displayedManager.analysis.get(wiDO1, a);
                            waDO2 = manager.analysis.get(wiDO2, a);
                            if (DataBaseUtil.isDifferent(waDO1.getAccessionNumber(), waDO2.getAccessionNumber()) ||
                                DataBaseUtil.isDifferent(waDO1.getDescription(), waDO2.getDescription()) ||
                                DataBaseUtil.isDifferent(waDO1.getWorksheetAnalysisId(), waDO2.getWorksheetAnalysisId()) ||
                                DataBaseUtil.isDifferent(waDO1.getStatusId(), waDO2.getStatusId()) ||
                                DataBaseUtil.isDifferent(waDO1.getUnitOfMeasureId(), waDO2.getUnitOfMeasureId())) {
                                dataChanged = true;
                                break items;
                            }
                            count5 = displayedManager.result.count(waDO1);
                            count6 = manager.result.count(waDO2);
                            if (count5 == count6) {
                            } else {
                                dataChanged = true;
                                break items;
                            }
                            count5 = displayedManager.qcResult.count(waDO1);
                            count6 = manager.qcResult.count(waDO2);
                            if (count5 == count6) {
                            } else {
                                dataChanged = true;
                                break items;
                            }
                        }
                    } else {
                        dataChanged = true;
                        break;
                    }
                }
            }
        } else {
            dataChanged = true;
        }

        if (dataChanged) {
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i, j, k, l;
        ArrayList<IdNameVO> headers;
        ArrayList<Row> model;
        Row row;
        WorksheetAnalysisViewDO waDO;
        WorksheetItemDO wiDO;
        WorksheetQcResultViewDO wqrVDO;
        WorksheetResultViewDO wrVDO;

        qcLinkModel.clear();
        qcLinkModel.add(new Item<Integer>(null, ""));

        model = new ArrayList<Row>();
        if (manager != null) {
            try {
                headers = new ArrayList<IdNameVO>();
                try {
                    headers = WorksheetService1.get().getHeaderLabelsForScreen(manager);
                } catch (Exception anyE) {
                    Window.alert("Error loading headers for format; " + anyE.getMessage());
                    anyE.printStackTrace();
                }

                maxColumn = 10 + headers.size();
                for (i = 0; i < 30; i++) {
                    if (i < headers.size())
                        worksheetItemTable.getColumnAt(i + 11).setLabel(headers.get(i).getName());
                    else
                        worksheetItemTable.getColumnAt(i + 11).setLabel("");
                }

                for (i = 0; i < manager.item.count(); i++) {
                    wiDO = (WorksheetItemDO)manager.item.get(i);
    
                    row = new Row(41);
                    row.setCell(0, "N");
                    row.setCell(1, wiDO.getPosition());
                    for (j = 0; j < manager.analysis.count(wiDO); j++) {
                        waDO = manager.analysis.get(wiDO, j);
                        qcLinkModel.add(new Item<Integer>(waDO.getId(), waDO.getAccessionNumber() +
                                                                        " (" + wiDO.getPosition() +
                                                                        ")"));
    
                        if (j > 0) {
                            row = new Row(41);
                            row.setCell(0, "N");
                            row.setCell(1, wiDO.getPosition());
                        }
                        row.setCell(2, waDO.getAccessionNumber());
                        
                        if (waDO.getAnalysisId() != null) {
                            row.setCell(3, new AutoCompleteValue(null, waDO.getDescription()));
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
                                    row = new Row(41);
                                    row.setCell(0, "N");
                                    row.setCell(1, "");
                                    row.setCell(2, "");
                                    row.setCell(3, new AutoCompleteValue());
                                    row.setCell(4, "");
                                    row.setCell(5, "");
                                    row.setCell(6, "");
                                    row.setCell(7, new AutoCompleteValue());
                                    row.setCell(8, "");
                                }
                                row.setCell(9, wrVDO.getAnalyteName());
                                row.setCell(10, wrVDO.getIsReportable());
                                for (l = 0; l < 30; l++)
                                    row.setCell(11 + l, wrVDO.getValueAt(l));
                                row.setData(manager.getUid(wrVDO)); 
                                model.add(row);
                            }
                            if (k == 0) {
                                row.setCell(9, "NO ANAlYTES FOOUND");
                                row.setCell(10, null);
                                for (l = 11; l < 40; l++)
                                    row.setCell(l, "");
                                row.setData(manager.getUid(waDO)); 
                                model.add(row);
                            }
                        } else if (waDO.getQcLotId() != null) {
                            row.setCell(3, new AutoCompleteValue(waDO.getQcLotId(),
                                                                 waDO.getDescription()));
                            row.setCell(4, waDO.getWorksheetAnalysisId());
                            row.setCell(5, "");
                            row.setCell(6, "");
                            row.setCell(7, new AutoCompleteValue());
                            row.setCell(8, "");
                            
                            for (k = 0; k < manager.qcResult.count(waDO); k++) {
                                wqrVDO = manager.qcResult.get(waDO, k);
                                if (k > 0) {
                                    row = new Row(41);
                                    row.setCell(0, "N");
                                    row.setCell(1, "");
                                    row.setCell(2, "");
                                    row.setCell(3, new AutoCompleteValue());
                                    row.setCell(4, "");
                                    row.setCell(5, "");
                                    row.setCell(6, "");
                                    row.setCell(7, new AutoCompleteValue());
                                    row.setCell(8, "");
                                }
                                row.setCell(9, wqrVDO.getAnalyteName());
                                row.setCell(10, null);
                                for (l = 0; l < 30; l++)
                                    row.setCell(11 + l, wqrVDO.getValueAt(l));
                                row.setData(manager.getUid(wqrVDO)); 
                                model.add(row);
                            }
                            if (k == 0) {
                                row.setCell(9, "NO ANAlYTES FOOUND");
                                row.setCell(10, null);
                                for (l = 11; l < 40; l++)
                                    row.setCell(l, "");
                                row.setData(manager.getUid(waDO)); 
                                model.add(row);
                            }
                        }
                    }
                    if (j == 0) {
                        row.setCell(3, new AutoCompleteValue(null, "ERROR: Item with no analyses"));
                        row.setData(manager.getUid(wiDO));
                        model.add(row);
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }        

        /*
         * Reload the model for the QC Link column
         */
        qcLink.setModel(qcLinkModel);

        return model;
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
}