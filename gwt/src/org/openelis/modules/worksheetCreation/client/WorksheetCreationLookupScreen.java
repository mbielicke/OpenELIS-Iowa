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
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.domain.WorksheetCreationViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
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
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.metamap.SampleMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationLookupScreen extends Screen {

    private SecurityModule security;
    private SampleMetaMap  sampleMetaMap;

    protected AutoComplete<Integer> testId;
    protected TextBox               methodName;
    protected Dropdown<Integer>     statusId;
    protected TableWidget           analysesTable;
    
    public WorksheetCreationLookupScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super((ScreenDefInt)GWT.create(WorksheetCreationLookupDef.class));
        
        sampleMetaMap = new SampleMetaMap();

        security = OpenELIS.security.getModule("organization");

        // Setup service used by screen
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        setState(State.QUERY);
        setStatusesModel(DictionaryCache.getListByCategorySystemName("analysis_status"));
    }
    
    private void initialize() {
        testId = (AutoComplete)def.getWidget(sampleMetaMap.SAMPLE_ITEM.ANALYSIS.TEST.getId());
        addScreenHandler(testId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TableDataRow selectedRow = testId.getSelection();
                
                //set the method
                if (selectedRow != null && selectedRow.key != null)
                    methodName.setValue(selectedRow.cells.get(1).value);
                else
                    methodName.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testId.enable(true);
            }
        });

        testId.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc;
                TableDataRow    row;

                rpc       = new AutocompleteRPC();
                rpc.match = event.getMatch();
                
                try {
                    rpc = service.call("getTestMethodMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i = 0; i < rpc.model.size(); i++) {
                        TestMethodViewDO autoDO = (TestMethodViewDO)rpc.model.get(i);
                        
                        row = new TableDataRow(5);
                        row.key = autoDO.getTestId();
                        row.cells.get(0).value = autoDO.getTestName();
                        row.cells.get(1).value = autoDO.getMethodName();
                        row.cells.get(2).value = autoDO.getTestDescription();
                        if ("N".equals(autoDO.getIsActive())) {
                            row.cells.get(3).value = autoDO.getActiveBegin();
                            row.cells.get(4).value = autoDO.getActiveEnd();
                        }
                        row.data = autoDO.getMethodId();
                        
                        model.add(row);
                    } 
                    
                    testId.showAutoMatches(model);
                        
                } catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            } 
        });

        methodName = (TextBox)def.getWidget(sampleMetaMap.SAMPLE_ITEM.ANALYSIS.TEST.METHOD.getName());
        addScreenHandler(methodName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                methodName.enable(false);
            }
        });

        final TextBox accessionNumber = (TextBox)def.getWidget(sampleMetaMap.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(true);
                accessionNumber.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(sampleMetaMap.SAMPLE_ITEM.ANALYSIS.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(true);
                statusId.setQueryMode(true);
            }
        });

        final AppButton findButton = (AppButton)def.getWidget("findButton");
        addScreenHandler(findButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                findButton.enable(true);
            }
        });

        final CalendarLookUp receivedDate = (CalendarLookUp)def.getWidget(sampleMetaMap.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(true);
                receivedDate.setQueryMode(true);
            }
        });

        final CalendarLookUp enteredDate = (CalendarLookUp)def.getWidget(sampleMetaMap.getEnteredDate());
        addScreenHandler(enteredDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                enteredDate.enable(true);
                enteredDate.setQueryMode(true);
            }
        });

        analysesTable = (TableWidget)def.getWidget("analysesTable");
        addScreenHandler(analysesTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
            }

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

        analysesTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
            }
        });

        analysesTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
            }
        });

        final AppButton addButton = (AppButton)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(true);
            }
        });

        final AppButton selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analysesTable.selectAll();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(true);
            }
        });   
    }

    //
    // Needs to be moved to the base screen code
    //
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData            qd;
        QueryFieldUtil       qField;
        TableDataRow         row;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            //
            // overriding AutoComplete's getQuery to return the id of the
            // selection instead of the text
            //
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
        WorksheetCreationQuery query;

        query = new WorksheetCreationQuery();
        query.fields = getQueryFields();

        if (query.fields.size() > 0) {
            window.setBusy(consts.get("querying"));
    
            service.call("query", query, new AsyncCallback<WorksheetCreationQuery>() {
                public void onSuccess(WorksheetCreationQuery query) {
                    loadQuery(query);
                }
    
                public void onFailure(Throwable caught) {
                    if (caught instanceof ValidationErrorsList)
                        showErrors((ValidationErrorsList)caught);
                    else
                        Window.alert(caught.getMessage());
                }
            });
        } else {
            window.setDone(consts.get("emptyQueryException"));
        }
    }

    private void loadQuery(WorksheetCreationQuery query) {
        int i;
        ArrayList<TableDataRow> model;
        DictionaryDO dictDo;
        TableDataRow row;
        WorksheetCreationViewDO analysisRow;
        
        window.setDone(consts.get("queryingComplete"));

        if (query.results == null || query.results.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
        } else {
            window.setDone(consts.get("queryingComplete"));
        }

        model = new ArrayList<TableDataRow>();
        query.model = new ArrayList<TableDataRow>();
        for (i = 0; i < query.results.size(); i++) {
            analysisRow = query.results.get(i);

            dictDo = DictionaryCache.getEntryFromId(analysisRow.getStatusId());
            
            row = new TableDataRow(6);
            row.key = analysisRow.getAnalysisId();
            row.cells.get(0).value = analysisRow.getAccessionNumber();
            row.cells.get(1).value = analysisRow.getTestName();
            row.cells.get(2).value = analysisRow.getMethodName();
            row.cells.get(3).value = analysisRow.getSectionName();
            if (dictDo != null)
                row.cells.get(4).value = dictDo.getEntry();          
            row.cells.get(5).value = analysisRow.getReceivedDate();

            model.add(row);
        }

        analysesTable.load(model);
    }

    private void setStatusesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        statusId.setModel(model);
    }
}
