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

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasExceptions;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Queryable;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class WorksheetCreationLookupScreen extends Screen 
                                           implements HasActionHandlers<WorksheetCreationLookupScreen.Action> {

    private Integer                 statusReleased, statusCancelled;
    private ScreenService           testService;
    private ModulePermission        userPermission;

    protected Button                searchButton, addButton, selectAllButton;
    protected AutoComplete          testId;
    protected Calendar              receivedDate, enteredDate;
    protected Dropdown<Integer>     sectionId, statusId, typeOfSampleId;
    protected Table                 analysesTable;
    protected TextBox               methodName;
    protected TextBox<Integer>      accessionNumber;
    
    public enum Action {
        ADD
    };

    public WorksheetCreationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(WorksheetCreationLookupDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.worksheetCreation.server.WorksheetCreationService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        
        userPermission = OpenELIS.getSystemUserPermission().getModule("worksheet");
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
            DictionaryCache.preloadByCategorySystemNames("analysis_status",
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
                Item<Integer> selectedRow = testId.getSelectedItem();
                
                //set the method
                if (selectedRow != null && selectedRow.getKey() != null)
                    methodName.setValue((String)selectedRow.getCell(1));
                else
                    methodName.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testId.setEnabled(true);
            }
        });

        testId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> matches;
                QueryFieldUtil          parser;
                Item<Integer>           row;
                TestMethodVO            tmVO;                

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e){
                	
                }
                try {
                    model = new ArrayList<Item<Integer>>();
                    matches = testService.callList("fetchByName", parser.getParameter().get(0));
                    for (int i = 0; i < matches.size(); i++) {
                        tmVO = (TestMethodVO)matches.get(i);
                        
                        row = new Item<Integer>(5);
                        row.setKey(tmVO.getTestId());
                        row.setCell(0,tmVO.getTestName());
                        row.setCell(1,tmVO.getMethodName());
                        row.setCell(2,tmVO.getTestDescription());
                        if ("N".equals(tmVO.getIsActive())) {
                            row.setCell(3,tmVO.getActiveBegin());
                            row.setCell(4,tmVO.getActiveEnd());
                        }
                        row.setData(tmVO.getMethodId());
                        
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
                methodName.setEnabled(false);
            }
        });

        sectionId = (Dropdown)def.getWidget(WorksheetCreationMeta.getAnalysisSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.setEnabled(true);
                sectionId.setQueryMode(true);
            }
        });

        accessionNumber = (TextBox)def.getWidget(WorksheetCreationMeta.getSampleAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.setEnabled(true);
                accessionNumber.setQueryMode(true);
            }
        });

        statusId = (Dropdown)def.getWidget(WorksheetCreationMeta.getAnalysisStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                statusId.setEnabled(true);
                statusId.setQueryMode(true);
            }
        });

        typeOfSampleId = (Dropdown)def.getWidget(WorksheetCreationMeta.getSampleItemTypeOfSampleId());
        addScreenHandler(typeOfSampleId, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                typeOfSampleId.setEnabled(true);
                typeOfSampleId.setQueryMode(true);
            }
        });

        receivedDate = (Calendar)def.getWidget(WorksheetCreationMeta.getSampleReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.setEnabled(true);
                receivedDate.setQueryMode(true);
            }
        });

        enteredDate = (Calendar)def.getWidget(WorksheetCreationMeta.getSampleEnteredDate());
        addScreenHandler(enteredDate, new ScreenEventHandler<Datetime>() {
            public void onStateChange(StateChangeEvent<State> event) {
                enteredDate.setEnabled(true);
                enteredDate.setQueryMode(true);
            }
        });

        searchButton = (Button)def.getWidget("searchButton");
        addScreenHandler(searchButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
                state = State.DEFAULT;
                setState(State.QUERY);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                searchButton.setEnabled(true);
            }
        });

        //
        // analysis search results table
        //
        analysesTable = (Table)def.getWidget("analysesTable");
        addScreenHandler(analysesTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent<State> event) {
                analysesTable.setEnabled(true);
                analysesTable.setAllowMultipleSelection(true);
            }
        });

        analysesTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent event) {
                addButton.setEnabled(true);
            }
        });
        
        analysesTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent event) {
                if (analysesTable.getSelectedRows().length == 0)
                    addButton.setEnabled(false);
            }
        });
        
        analysesTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
                
            }
        });

        addButton = (Button)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                addAnalyses();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(false);
            }
        });

        selectAllButton = (Button)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analysesTable.selectAll();
                if (analysesTable.getSelectedRows().length > 0)
                    addButton.setEnabled(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.setEnabled(true);
            }
        });   
    }
    
    @SuppressWarnings("unchecked")
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> dictList;
        ArrayList<SectionViewDO> sectList;
        ArrayList<Item<Integer>> model;

        try {
            statusReleased  = DictionaryCache.getIdFromSystemName("analysis_released");
            statusCancelled = DictionaryCache.getIdFromSystemName("analysis_cancelled");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        //
        // load analysis status dropdown model
        //
        sectList  = SectionCache.getSectionList();
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (SectionViewDO resultDO : sectList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getName()));
        sectionId.setModel(model);
        ((Dropdown<Integer>)analysesTable.getColumnWidget(4)).setModel(model);
        
        //
        // load analysis status dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("analysis_status");
        model = new ArrayList<Item<Integer>>();
//        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        statusId.setModel(model);
        ((Dropdown<Integer>)analysesTable.getColumnWidget(5)).setModel(model);
        
        //
        // load type of sample dropdown model
        //
        dictList  = DictionaryCache.getListByCategorySystemName("type_of_sample");
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
    @SuppressWarnings("unchecked")
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData            qd;
        QueryFieldUtil       qField;
        Item<Integer>        row;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof AutoComplete) {
                row = ((AutoComplete)def.getWidget(key)).getSelectedItem();
                if(row != null && row.getKey() != null) {
                    qd = new QueryData(key, QueryData.Type.INTEGER,((Integer)row.getKey()).toString());
                    list.add(qd);

                    qField = new QueryFieldUtil();
                    try {
                    	qField.parse(qd.getQuery());
                    }catch(Exception e) {
                    	
                    }
                }
            } else if (def.getWidget(key) instanceof Queryable) {
            	qd = (QueryData)((Queryable)def.getWidget(key)).getQuery();
            	qd.setKey(key);
            	list.add(qd);
            	
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
        ArrayList<Row>          model;
        Row                     row;
        WorksheetCreationVO     analysisRow;
        
        if (list == null || list.size() == 0) {
            window.setDone(consts.get("noRecordsFound"));
            
            analysesTable.clear();
        } else {
            window.setDone(consts.get("queryingComplete"));

            model = new ArrayList<Row>();
            for (i = 0; i < list.size(); i++) {
                analysisRow = list.get(i);
                
                row = new Row(11);
                //row.key = analysisRow.getAnalysisId();
                row.setCell(0,analysisRow.getAccessionNumber());
                row.setCell(1,analysisRow.getDescription());
                row.setCell(2,analysisRow.getTestName());
                row.setCell(3,analysisRow.getMethodName());
                row.setCell(4,analysisRow.getSectionId());
                row.setCell(5,analysisRow.getStatusId());          
                row.setCell(6,analysisRow.getCollectionDate());
                row.setCell(7,analysisRow.getReceivedDate());
                row.setCell(8,analysisRow.getDueDays());
                row.setCell(9,analysisRow.getExpireDate());

                if (analysisRow.getPriority() != null)
                    row.setCell(10,analysisRow.getPriority());

                row.setData(analysisRow);

                model.add(row);
            }

            analysesTable.setModel(model);
        }
    }
    
    protected void addAnalyses() {
        int                     i;
        SectionViewDO           sectionVDO;
        StringBuffer            message;
        WorksheetCreationVO     analysisRow;
        ArrayList<Row>          selections;
        Integer[]               sels;
        
        i = 0;
        message = new StringBuffer();
        
        sels = analysesTable.getSelectedRows();
        selections = new ArrayList<Row>(sels.length);
        for(i = 0; i < sels.length; i++)
        	selections.add(analysesTable.getRowAt(sels[i]));
        
        while (i < selections.size()) {
            analysisRow = (WorksheetCreationVO) selections.get(i).getData();
            if (! isAnalysisEditable(analysisRow)) {
                selections.remove(i);
                
                message.append(consts.get("accessionNum")).append(analysisRow.getAccessionNumber())
                       .append("\t").append(analysisRow.getTestName().trim()).append(", ")
                       .append(analysisRow.getMethodName().trim());
                try {
                    sectionVDO = SectionCache.getSectionFromId(analysisRow.getSectionId());
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
                       (!statusReleased.equals(analysisRow.getStatusId()) ||
                        !statusCancelled.equals(analysisRow.getStatusId()));
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
            section = SectionCache.getSectionFromId(analysisRow.getSectionId());
            perm = OpenELIS.getSystemUserPermission().getSection(section.getName()); 
            if (perm != null && perm.hasCompletePermission())
                allow = true;
        } catch (Exception anyE) {
            Window.alert(anyE.getMessage());
        }

        return allow;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
