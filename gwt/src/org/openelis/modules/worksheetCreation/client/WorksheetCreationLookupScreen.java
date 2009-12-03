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

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
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
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.metamap.WorksheetCreationMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetCreationLookupScreen extends Screen 
                                           implements HasActionHandlers<WorksheetCreationLookupScreen.Action> {

    private ScreenService            testService;
    private SecurityModule           security;
    private WorksheetCreationMetaMap meta;

    protected AppButton             searchButton, addButton, selectAllButton;
    protected AutoComplete<Integer> testId;
    protected CalendarLookUp        receivedDate, enteredDate;
    protected Dropdown<Integer>     sectionId, statusId, typeOfSampleId;
    protected TableWidget           analysesTable;
    protected TextBox               methodName;
    protected TextBox<Integer>      accessionNumber;
    
    public enum Action {
        ADD
    };

    public WorksheetCreationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationLookupDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        testService = new ScreenService("OpenELISServlet?service=org.openelis.modules.test.server.TestService");
        
        security = OpenELIS.security.getModule("worksheet");
        if (security == null)
            throw new SecurityException("screenPermException", "System Variable Screen");

        meta = new WorksheetCreationMetaMap();

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
        initialize();
        setState(State.DEFAULT);
        setState(State.QUERY);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // screen fields and buttons
        //
        testId = (AutoComplete)def.getWidget(meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.getId());
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

        testId.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<TestMethodVO> matches;
                QueryFieldUtil          parser;
                TableDataRow            row;
                TestMethodVO            tmVO;                

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                try {
                    model = new ArrayList<TableDataRow>();
                    matches = testService.callList("fetchByName", parser.getParameter().get(0)/*+"%"*/);
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

        methodName = (TextBox)def.getWidget(meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName());
        addScreenHandler(methodName, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                methodName.enable(false);
            }
        });

        sectionId = (Dropdown)def.getWidget(meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.SECTION.getId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(true);
                sectionId.setQueryMode(true);
            }
        });

        accessionNumber = (TextBox)def.getWidget(meta.SAMPLE.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(true);
                accessionNumber.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(meta.SAMPLE.SAMPLE_ITEM.ANALYSIS.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(true);
                statusId.setQueryMode(true);
            }
        });

        typeOfSampleId = (Dropdown)def.getWidget(meta.SAMPLE.SAMPLE_ITEM.getTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.enable(true);
                typeOfSampleId.setQueryMode(true);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(meta.SAMPLE.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(true);
                receivedDate.setQueryMode(true);
            }
        });

        enteredDate = (CalendarLookUp)def.getWidget(meta.SAMPLE.getEnteredDate());
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
                addButton.enable(true);
            }
        });

        selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analysesTable.selectAll();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(true);
            }
        });   
    }
    
    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<SectionDO> sectList;
        ArrayList<TableDataRow> model;

        //
        // load analysis status dropdown model
        //
        sectList  = SectionCache.getSectionList();
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (SectionDO resultDO : sectList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getName()));
        sectionId.setModel(model);
        
        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);
        ((Dropdown<Integer>)analysesTable.getColumns().get(5).getColumnWidget()).setModel(model);
        
        //
        // load type of sample dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("type_of_sample");
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
        
        window.setDone(consts.get("queryingComplete"));

        if (list == null || list.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
            
            analysesTable.clear();
        } else {
            window.setDone(consts.get("queryingComplete"));

            model = new ArrayList<TableDataRow>();
            for (i = 0; i < list.size(); i++) {
                analysisRow = list.get(i);
                
                row = new TableDataRow(11);
                row.key = analysisRow.getAnalysisId();
                row.cells.get(0).value = analysisRow.getAccessionNumber();
                row.cells.get(1).value = analysisRow.getDescription();
//                row.cells.get(2).value = analysisRow.getProjectName();
                row.cells.get(2).value = analysisRow.getTestName();
                row.cells.get(3).value = analysisRow.getMethodName();
                row.cells.get(4).value = analysisRow.getSectionName();
                row.cells.get(5).value = analysisRow.getStatusId();          
                row.cells.get(6).value = analysisRow.getCollectionDate();
                row.cells.get(7).value = analysisRow.getReceivedDate();
                row.cells.get(8).value = analysisRow.getDueDays();
                row.cells.get(9).value = analysisRow.getExpireDate();

                if (analysisRow.getPriority() != null)
                    row.cells.get(10).value = analysisRow.getPriority();

                row.data = analysisRow;

                model.add(row);
            }

            analysesTable.load(model);
        }
    }
    
    protected void addAnalyses() {
        ActionEvent.fire(this, Action.ADD, analysesTable.getSelections());
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
