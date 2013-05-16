/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.worksheetBuilder.client;

import static org.openelis.modules.main.client.Logger.*;
//import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.meta.AnalysisViewMeta;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.common.data.Query;
//import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public class WorksheetBuilderLookupScreenUI extends Screen
                                            implements HasActionHandlers<WorksheetBuilderLookupScreenUI.Action> {

    @UiTemplate("WorksheetBuilderLookup.ui.xml")
    interface WorksheetBuilderLookupUiBinder extends UiBinder<Widget, WorksheetBuilderLookupScreenUI> {
    };

    private static WorksheetBuilderLookupUiBinder     uiBinder = GWT.create(WorksheetBuilderLookupUiBinder.class);

    private ModulePermission                          userPermission;

    @UiField
    protected AutoComplete                            testId;
    @UiField
    protected Button                                  search, addRow, selectAll;
    @UiField
    protected Calendar                                receivedDate, enteredDate;
    @UiField
    protected Dropdown<Integer>                       sectionId, analysisStatusId,
                                                      typeOfSampleId, tableSection,
                                                      tableUnit, tableStatus;
    protected HashMap<Integer, AnalysisResultManager> analyteMap;
    @UiField
    protected Table                                   analysesTable, analyteTable;
    @UiField
    protected TextBox<String>                         methodName;
    @UiField
    protected TextBox<Integer>                        accessionNumber;

    public enum Action {
        ADD
    };

    public WorksheetBuilderLookupScreenUI() throws Exception {
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Worksheet Creation Lookup Screen"));
        
        initWidget(uiBinder.createAndBindUi(this));
        
        analyteMap = new HashMap<Integer, AnalysisResultManager>();

        try {
            CategoryCache.getBySystemNames("analysis_status", "unit_of_measure", "type_of_sample");
        } catch (Exception e) {
            throw new Exception("WorksheetBuilderLookupScreen: missing dictionary entry; " + e.getMessage());
        }

        initialize();
//        setState(QUERY);
        initializeDropdowns();
        fireDataChange();
        testId.setFocus(true);
        
        logger.fine("WorksheetBuilderLookup Screen Opened");
    }
    
    /**
     * Setup state and data change handles for every widget on the screen.  This
     * screen is query only, so most of the widgets do not need an onValueChange method.
     */
//    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // screen fields and buttons
        //
        addScreenHandler(testId, AnalysisViewMeta.getTestId(), new ScreenHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                AutoCompleteValue selectedRow;

                selectedRow = testId.getValue();
                
                //set the method
                if (selectedRow != null)
                    methodName.setValue(((TestMethodVO)selectedRow.getData()).getMethodName());
                else
                    methodName.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                testId.setEnabled(true);
            }
        });

        testId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> matches;
                Item<Integer>           row;

                try {
                    model = new ArrayList<Item<Integer>>();
                    matches = TestService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (TestMethodVO tmVO : matches) {
                        row = new Item<Integer>(5);
                        row.setKey(tmVO.getTestId());
                        row.setCell(0, tmVO.getTestName());
                        row.setCell(1, tmVO.getMethodName());
                        row.setCell(2, tmVO.getTestDescription());
                        if ("N".equals(tmVO.getIsActive())) {
                            row.setCell(3, tmVO.getActiveBegin());
                            row.setCell(4, tmVO.getActiveEnd());
                        }
                        row.setData(tmVO);
                        model.add(row);
                    } 
                    
                    testId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        addScreenHandler(methodName, AnalysisViewMeta.getMethodName(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                methodName.setEnabled(false);
            }
        });

        addScreenHandler(sectionId, AnalysisViewMeta.getSectionId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                sectionId.setEnabled(true);
                sectionId.setQueryMode(true);
            }
        });

        addScreenHandler(accessionNumber, AnalysisViewMeta.getAccessionNumber(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                accessionNumber.setEnabled(true);
                accessionNumber.setQueryMode(true);
            }
        });

        addScreenHandler(analysisStatusId, AnalysisViewMeta.getAnalysisStatusId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                analysisStatusId.setEnabled(true);
                analysisStatusId.setQueryMode(true);
            }
        });

        addScreenHandler(typeOfSampleId, AnalysisViewMeta.getTypeOfSampleId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                typeOfSampleId.setEnabled(true);
                typeOfSampleId.setQueryMode(true);
            }
        });

        addScreenHandler(receivedDate, AnalysisViewMeta.getReceivedDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(true);
                receivedDate.setQueryMode(true);
            }
        });

        addScreenHandler(enteredDate, AnalysisViewMeta.getEnteredDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                enteredDate.setEnabled(true);
                enteredDate.setQueryMode(true);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(true);
            }
        });

        //
        // analysis search results table
        //
        addScreenHandler(analysesTable, "analysesTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                analysesTable.setEnabled(true);
                analysesTable.setAllowMultipleSelection(true);
            }
        });

        analysesTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                addRow.setEnabled(true);
                showAnalytes();
            }
        });
        
        analysesTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (analysesTable.getSelectedRows().length == 0)
                    addRow.setEnabled(false);
            }
        });
        
        analysesTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addRow.setEnabled(false);
            }
        });

        
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                selectAll.setEnabled(true);
            }
        });   

        addScreenHandler(analyteTable, "analyteTable", new ScreenHandler<ArrayList<Item<Integer>>>() {
            public void onStateChange(StateChangeEvent event) {
                analyteTable.setEnabled(true);
            }
        });

        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });
    }

    @SuppressWarnings("unused")
    @UiHandler("search")
    protected void executeQuery(ClickEvent event) {
        Query query;

        if (!validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        if (query.getFields().size() > 0) {
            window.setBusy(Messages.get().querying());
    
            query.setRowsPerPage(500);
            WorksheetBuilderService.get().lookupAnalyses(query, new AsyncCallback<ArrayList<WorksheetBuilderVO>>() {
                public void onSuccess(ArrayList<WorksheetBuilderVO> list) {
                    setQueryResult(list);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: WorksheetBuilderLookup call lookupAnalyses failed; "+error.getMessage());
                        window.setError(Messages.get().queryFailed());
                    }
                }
            });
        } else {
            window.setDone(Messages.get().emptyQueryException());
        }

//        state = State.DEFAULT;
//        setState(State.QUERY);
    }

    @SuppressWarnings("unused")
    @UiHandler("addRow")
    protected void addAnalyses(ClickEvent event) {
        int i;
        Integer selectedRows[];
        ArrayList<Row> selections;
        SectionViewDO sectionVDO;
        StringBuffer message;
        WorksheetBuilderVO analysisRow;
        
        message = new StringBuffer();
        selections = new ArrayList<Row>();
        selectedRows = analysesTable.getSelectedRows();
        for (i = 0; i < selectedRows.length; i++) {
            analysisRow = (WorksheetBuilderVO) analysesTable.getRowAt(i).getData();
            if (! isAnalysisEditable(analysisRow)) {
                message.append(Messages.get().accessionNum()).append(analysisRow.getAccessionNumber())
                       .append("\t").append(analysisRow.getTestName().trim()).append(", ")
                       .append(analysisRow.getMethodName().trim());
                try {
                    sectionVDO = SectionCache.getById(analysisRow.getSectionId());
                    message.append("\t\t").append(sectionVDO.getName().trim());
                } catch (Exception anyE) {
                    anyE.printStackTrace();
                    message.append("\t\t").append("ERROR");
                }
                message.append("\n");
            } else {
                selections.add(analysesTable.getRowAt(i));
            }
        }
        
        if (message.length() > 0)
            Window.alert(Messages.get().worksheetItemsNotAdded()+":\n\n"+message.toString());
        if (selections.size() > 0)
            ActionEvent.fire(this, Action.ADD, selections);
    }

    @SuppressWarnings("unused")
    @UiHandler("selectAll")
    public void selectAll(ClickEvent event) {
        analysesTable.selectAll();
        if (analysesTable.getSelectedRows().length > 0)
            addRow.setEnabled(true);
    }

    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;
        ArrayList<SectionViewDO> sectList;
        Item<Integer> row;

        //
        // load section dropdown model
        //
        sectList  = SectionCache.getList();
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (SectionViewDO resultDO : sectList) {
            row = new Item<Integer>(resultDO.getId(),resultDO.getName());
            row.setEnabled(true);
            model.add(row);
        }
        sectionId.setModel(model);
        tableSection.setModel(model);
        
        //
        // load unit of measure dropdown model
        //
        dictList  = CategoryCache.getBySystemName("unit_of_measure");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        tableUnit.setModel(model);
        
        //
        // load analysis status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        analysisStatusId.setModel(model);
        tableStatus.setModel(model);
        
        //
        // load type of sample dropdown model
        //
        dictList  = CategoryCache.getBySystemName("type_of_sample");
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        typeOfSampleId.setModel(model);
    }

    //
    // overriding AutoComplete's getQuery to return the id of the
    // selection instead of the text
    //
//    @SuppressWarnings("unchecked")
//    public ArrayList<QueryData> getQueryFields() {
//        ArrayList<QueryData> list;
//        QueryData            qd;
//        QueryFieldUtil       qField;
//        Item<Integer>        row;
//
//        list = new ArrayList<QueryData>();
//        for (String key : def.getWidgets().keySet()) {
//            if (def.getWidget(key) instanceof AutoComplete) {
//                row = ((AutoComplete)def.getWidget(key)).getSelection();
//                if(row != null && row.key != null) {
//                    qd = new QueryData();
//                    qd.setKey(key);
//                    qd.setQuery(((Integer)row.key).toString());
//                    qd.setType(QueryData.Type.INTEGER);
//                    list.add(qd);
//
//                    qField = new QueryFieldUtil();
//                    qField.parse(qd.getQuery());
//                }
//            } else if (def.getWidget(key) instanceof HasField) {
//                ((HasField)def.getWidget(key)).getQuery(list, key);
//            }
//        }
//        return list;
//    }

    private void setQueryResult(ArrayList<WorksheetBuilderVO> list) {
        ArrayList<Item<Integer>> model;
        Item<Integer>            row;
        
        if (list == null || list.size() == 0) {
            window.setDone(Messages.get().noRecordsFound());
            
            analysesTable.clear();
        } else {
            window.setDone(Messages.get().queryingComplete());

            model = new ArrayList<Item<Integer>>();
            for (WorksheetBuilderVO analysisRow : list) {
                row = new Item<Integer>(12);
                row.setKey(analysisRow.getAnalysisId());
                row.setCell(0, analysisRow.getAccessionNumber());
                row.setCell(1, analysisRow.getWorksheetDescription());
                row.setCell(2, analysisRow.getTestName());
                row.setCell(3, analysisRow.getMethodName());
                row.setCell(4, analysisRow.getSectionId());
                row.setCell(5, analysisRow.getUnitOfMeasureId());
                row.setCell(6, analysisRow.getAnalysisStatusId());          
                row.setCell(7, analysisRow.getCollectionDate());
                row.setCell(8, analysisRow.getReceivedDate());
                row.setCell(9, analysisRow.getDueDays());
                row.setCell(10, analysisRow.getExpireDate());

                if (analysisRow.getPriority() != null)
                    row.setCell(11, analysisRow.getPriority());

                row.setData(analysisRow);

                model.add(row);
            }

            analysesTable.setModel(model);
            showAnalytes();
        }
    }
    
    /**
     * Returns true if analysis can be added to the worksheet and the user has
     * permission to addRow it.
     */
    private boolean isAnalysisEditable(WorksheetBuilderVO analysisRow) {
        boolean editable;
        
        editable = false;
        if (analysisRow != null) {
            editable = canAddTest(analysisRow) &&
                       "N".equals(analysisRow.getAnalysisResultOverride()) &&
                       !Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(analysisRow.getAnalysisStatusId()) &&
                       !Constants.dictionary().ANALYSIS_INPREP.equals(analysisRow.getAnalysisStatusId()) &&
                       !Constants.dictionary().ANALYSIS_RELEASED.equals(analysisRow.getAnalysisStatusId()) &&
                       !Constants.dictionary().ANALYSIS_CANCELLED.equals(analysisRow.getAnalysisStatusId());
        }
        return editable;
    }

    private boolean canAddTest(WorksheetBuilderVO analysisRow) {
        boolean       allow;
        SectionViewDO section;
        SectionPermission perm;

        allow = false;
        if (analysisRow == null)
            return allow;

        try {
            section = SectionCache.getById(analysisRow.getSectionId());
            perm = UserCache.getPermission().getSection(section.getName()); 
            if (perm != null && perm.hasCompletePermission())
                allow = true;
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
        }

        return allow;
    }
    
    private void showAnalytes() {
        Integer selectedRows[];
        AnalysisResultManager arMan;
        
        if (analyteTable.isVisible()) {
            selectedRows = analysesTable.getSelectedRows();
            if (selectedRows.length == 1) {
                final WorksheetBuilderVO data = (WorksheetBuilderVO) analysesTable.getRowAt(selectedRows[0]).getData();
                arMan = analyteMap.get(data.getAnalysisId());
                if (arMan != null) {
                    loadAnalyteTable(arMan);
                } else {
                    ResultService.get().fetchByAnalysisIdForDisplay(data.getAnalysisId(), new AsyncCallback<AnalysisResultManager>() {
                        public void onSuccess(AnalysisResultManager arMan) {
                            loadAnalyteTable(arMan);
                            analyteMap.put(data.getAnalysisId(), arMan);
                        }
            
                        public void onFailure(Throwable error) {
                            analyteTable.setModel(null);
                            if (error instanceof NotFoundException) {
                                window.setDone(Messages.get().noAnalytesFoundForRow());
                            } else {
                                Window.alert("Error: WorksheetCreationLookup call showAnalytes failed; "+error.getMessage());
                            }
                        }
                    });
                }
            } else {
                analyteTable.setModel(null);
            }            
        } else {
            analyteTable.setModel(null);
        }
    }

    public void loadAnalyteTable(AnalysisResultManager arMan) {
        ArrayList<Item<Integer>> model;
        ResultViewDO result;
        Item<Integer> row;

        model = null;
        if (arMan != null) {
            model = new ArrayList<Item<Integer>>();
            for (ArrayList<ResultViewDO> resultRow : arMan.getResults()) {
                result = (ResultViewDO)resultRow.get(0);
                if ("Y".equals(result.getIsReportable())) {
                    row = new Item<Integer>(1);
                    row.setKey(result.getId());
                    row.setCell(0, result.getAnalyte());
                    row.setData(result);
                    model.add(row);
                }
            }
        }
        analyteTable.setModel(model);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
