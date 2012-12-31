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
package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.SectionPermission;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.meta.WorksheetCreationMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetCreationLookupScreen1 extends Screen 
                                           implements HasActionHandlers<WorksheetCreationLookupScreen1.Action> {

    private ScreenService           resultService, testService;
    private ModulePermission        userPermission;

    protected AppButton             searchButton, addButton, selectAllButton;
    protected AutoComplete<Integer> testId;
    protected CalendarLookUp        receivedDate, enteredDate;
    protected Dropdown<Integer>     sectionId, statusId, typeOfSampleId;
    protected HashMap<Integer, ArrayList<ResultViewDO>> resultMap; 
    protected TableWidget           analysesTable, analyteTable;
    protected TextBox               methodName;
    protected TextBox<Integer>      accessionNumber;
    
    public enum Action {
        ADD
    };

    public WorksheetCreationLookupScreen1() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationLookupDef1.class));
        service = new ScreenService("controller?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        resultService = new ScreenService("controller?service=org.openelis.modules.result.server.ResultService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        
        userPermission = UserCache.getPermission().getModule("worksheet");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Worksheet Creation Lookup Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred command.
     */
    private void postConstructor() {
        try {
            CategoryCache.getBySystemNames("analysis_status",
                                           "unit_of_measure",
                                           "type_of_sample");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        initialize();
        setState(State.QUERY);
        initializeDropdowns();
        DataChangeEvent.fire(this);
        setFocus(testId);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen.  This
     * screen is query only, so most of the widgets do not need an onValueChange method.
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // screen fields and buttons
        //
        testId = (AutoComplete)def.getWidget(WorksheetCreationMeta.getAnalysisTestId());
        addScreenHandler(testId, new ScreenEventHandler<Integer>() {
            public void onValueChange(ValueChangeEvent<Integer> event) {
                TableDataRow selectedRow = testId.getSelection();
                
                //set the method
                if (selectedRow != null && selectedRow.key != null)
                    methodName.setValue((String)selectedRow.cells.get(1).value);
                else
                    methodName.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testId.enable(true);
            }
        });

        testId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<TestMethodVO> matches;
                TableDataRow            row;
                TestMethodVO            tmVO;                

                try {
                    model = new ArrayList<TableDataRow>();
                    matches = testService.callList("fetchByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (int i = 0; i < matches.size(); i++) {
                        tmVO = (TestMethodVO)matches.get(i);
                        
                        row = new TableDataRow(5);
                        row.key = tmVO.getTestId();
                        row.cells.get(0).value = tmVO.getTestName();
                        row.cells.get(1).value = tmVO.getMethodName();
                        row.cells.get(2).value = tmVO.getTestDescription();
                        if ("N".equals(tmVO.getIsActive())) {
                            row.cells.get(3).value = tmVO.getActiveBegin();
                            row.cells.get(4).value = tmVO.getActiveEnd();
                        }
                        row.data = tmVO.getMethodId();
                        
                        model.add(row);
                    } 
                    
                    testId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        methodName = (TextBox)def.getWidget(WorksheetCreationMeta.getAnalysisTestMethodName());
        addScreenHandler(methodName, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                methodName.enable(false);
            }
        });

        sectionId = (Dropdown)def.getWidget(WorksheetCreationMeta.getAnalysisSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(true);
                sectionId.setQueryMode(true);
            }
        });

        accessionNumber = (TextBox)def.getWidget(WorksheetCreationMeta.getSampleAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(true);
                accessionNumber.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(WorksheetCreationMeta.getAnalysisStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(true);
                statusId.setQueryMode(true);
            }
        });

        typeOfSampleId = (Dropdown)def.getWidget(WorksheetCreationMeta.getSampleItemTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.enable(true);
                typeOfSampleId.setQueryMode(true);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(WorksheetCreationMeta.getSampleReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(true);
                receivedDate.setQueryMode(true);
            }
        });

        enteredDate = (CalendarLookUp)def.getWidget(WorksheetCreationMeta.getSampleEnteredDate());
        addScreenHandler(enteredDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                enteredDate.enable(true);
                enteredDate.setQueryMode(true);
            }
        });

        searchButton = (AppButton)def.getWidget("searchButton");
        addScreenHandler(searchButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
                state = State.DEFAULT;
                setState(State.QUERY);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                searchButton.enable(true);
            }
        });

        //
        // analysis search results table
        //
        analysesTable = (TableWidget)def.getWidget("analysesTable");
        addScreenHandler(analysesTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                analysesTable.enable(true);
                analysesTable.enableMultiSelect(true);
            }
        });

        analysesTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent event) {
                addButton.enable(true);
                showAnalytes();
            }
        });
        
        analysesTable.addUnselectionHandler(new UnselectionHandler<TableDataRow>() {
            public void onUnselection(UnselectionEvent event) {
                if (analysesTable.getSelectedRows().length == 0)
                    addButton.enable(false);
            }
        });
        
        analysesTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });

        addButton = (AppButton)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addAnalyses();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(false);
            }
        });

        selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analysesTable.selectAll();
                if (analysesTable.getSelectedRows().length > 0)
                    addButton.enable(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(true);
            }
        });   

        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                analysesTable.enable(true);
            }
        });

        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<SectionViewDO> sectList;
        ArrayList<TableDataRow> model;

        //
        // load section dropdown model
        //
        sectList  = SectionCache.getList();
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (SectionViewDO resultDO : sectList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getName()));
        sectionId.setModel(model);
        ((Dropdown<Integer>)analysesTable.getColumns().get(4).getColumnWidget()).setModel(model);
        
        //
        // load unit of measure dropdown model
        //
        dictList  = CategoryCache.getBySystemName("unit_of_measure");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        ((Dropdown<Integer>)analysesTable.getColumns().get(5).getColumnWidget()).setModel(model);
        
        //
        // load analysis status dropdown model
        //
        dictList  = CategoryCache.getBySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);
        ((Dropdown<Integer>)analysesTable.getColumns().get(6).getColumnWidget()).setModel(model);
        
        //
        // load type of sample dropdown model
        //
        dictList  = CategoryCache.getBySystemName("type_of_sample");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        typeOfSampleId.setModel(model);
    }

    //
    // overriding AutoComplete's getQuery to return the id of the
    // selection instead of the text
    //
    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData            qd;
        QueryFieldUtil       qField;
        TableDataRow         row;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof AutoComplete) {
                row = ((AutoComplete)def.getWidget(key)).getSelection();
                if(row != null && row.key != null) {
                    qd = new QueryData();
                    qd.key = key;
                    qd.query = ((Integer)row.key).toString();
                    qd.type = QueryData.Type.INTEGER;
                    list.add(qd);

                    qField = new QueryFieldUtil();
                    qField.parse(qd.query);
                }
            } else if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }
        return list;
    }

    protected void executeQuery() {
        Query query;

        if (!validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        if (query.getFields().size() > 0) {
            window.setBusy(consts.get("querying"));
    
            query.setRowsPerPage(500);
            service.callList("query", query, new AsyncCallback<ArrayList<WorksheetCreationVO>>() {
                public void onSuccess(ArrayList<WorksheetCreationVO> list) {
                    setQueryResult(list);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(consts.get("noRecordsFound"));
                    } else {
                        Window.alert("Error: WorksheetCreationLookup call query failed; "+error.getMessage());
                        window.setError(consts.get("queryFailed"));
                    }
                }
            });
        } else {
            window.setDone(consts.get("emptyQueryException"));
        }
    }

    private void setQueryResult(ArrayList<WorksheetCreationVO> list) {
        int                     i;
        ArrayList<TableDataRow> model;
        TableDataRow            row;
        WorksheetCreationVO     analysisRow;
        
        if (list == null || list.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
            
            analysesTable.clear();
        } else {
            window.setDone(consts.get("queryingComplete"));

            model = new ArrayList<TableDataRow>();
            for (i = 0; i < list.size(); i++) {
                analysisRow = list.get(i);
                
                row = new TableDataRow(12);
                row.key = analysisRow.getAnalysisId();
                row.cells.get(0).value = analysisRow.getAccessionNumber();
                row.cells.get(1).value = analysisRow.getDescription();
                row.cells.get(2).value = analysisRow.getTestName();
                row.cells.get(3).value = analysisRow.getMethodName();
                row.cells.get(4).value = analysisRow.getSectionId();
                row.cells.get(5).value = analysisRow.getUnitOfMeasureId();
                row.cells.get(6).value = analysisRow.getStatusId();          
                row.cells.get(7).value = analysisRow.getCollectionDate();
                row.cells.get(8).value = analysisRow.getReceivedDate();
                row.cells.get(9).value = analysisRow.getDueDays();
                row.cells.get(10).value = analysisRow.getExpireDate();

                if (analysisRow.getPriority() != null)
                    row.cells.get(11).value = analysisRow.getPriority();

                row.data = analysisRow;

                model.add(row);
            }

            analysesTable.load(model);
            showAnalytes();
        }
    }
    
    protected void addAnalyses() {
        int                     i;
        SectionViewDO           sectionVDO;
        StringBuffer            message;
        WorksheetCreationVO     analysisRow;
        ArrayList<TableDataRow> selections;
        
        i = 0;
        message = new StringBuffer();
        selections = analysesTable.getSelections();
        while (i < selections.size()) {
            analysisRow = (WorksheetCreationVO) selections.get(i).data;
            if (! isAnalysisEditable(analysisRow)) {
                selections.remove(i);
                
                message.append(consts.get("accessionNum")).append(analysisRow.getAccessionNumber())
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
                i++;
            }
        }
        
        if (message.length() > 0)
            Window.alert(consts.get("worksheetItemsNotAdded")+":\n\n"+message.toString());
        if (selections.size() > 0)
            ActionEvent.fire(this, Action.ADD, selections);
    }

    /**
     * Returns true if analysis can be added to the worksheet and the user has
     * permission to add it.
     */
    private boolean isAnalysisEditable(WorksheetCreationVO analysisRow) {
        boolean editable;
        
        editable = false;
        if (analysisRow != null) {
            editable = canAddTest(analysisRow) &&
                       Boolean.FALSE.equals(analysisRow.getHasQaOverride()) &&
                       !Constants.dictionary().ANALYSIS_ERROR_INPREP.equals(analysisRow.getStatusId()) &&
                       !Constants.dictionary().ANALYSIS_INPREP.equals(analysisRow.getStatusId()) &&
                       !Constants.dictionary().ANALYSIS_RELEASED.equals(analysisRow.getStatusId()) &&
                       !Constants.dictionary().ANALYSIS_CANCELLED.equals(analysisRow.getStatusId());
        }
        return editable;
    }

    private boolean canAddTest(WorksheetCreationVO analysisRow) {
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
        ArrayList<TableDataRow> selections;
        WorksheetCreationVO data;
        
        if (analyteTable.isVisible()) {
            selections = analysesTable.getSelections();
            if (selections.size() == 1) {
                data = (WorksheetCreationVO) selections.get(0).data;
                resultService.call("fetchByAnalysisIdForDisplay", data.getAnalysisId(), new AsyncCallback<AnalysisResultManager>() {
                    public void onSuccess(AnalysisResultManager arMan) {
                        loadAnalyteTable(arMan);
                    }
        
                    public void onFailure(Throwable error) {
                        analyteTable.load(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noAnalytesFoundForRow"));
                        } else {
                            Window.alert("Error: WorksheetCreationLookup call showAnalytes failed; "+error.getMessage());
                        }
                    }
                });
            } else {
                analyteTable.load(null);
            }            
        } else {
            analyteTable.load(null);
        }
    }

    public void loadAnalyteTable(AnalysisResultManager arMan) {
        int i;
        ArrayList<ResultViewDO> resultRow;
        ArrayList<TableDataRow> model;
        ResultViewDO result;
        TableDataRow row;

        model = null;
        if (arMan != null) {
            model = new ArrayList<TableDataRow>();
            for (i = 0; i < arMan.getResults().size(); i++) {
                resultRow = arMan.getRowAt(i);
                result = (ResultViewDO)resultRow.get(0);
                
                if ("Y".equals(result.getIsReportable())) {
                    row = new TableDataRow(1);
                    row.key = result.getId();
                    row.cells.get(0).value = result.getAnalyte();
                    row.data = result;
                    model.add(row);
                }
            }
        }
        analyteTable.load(model);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
