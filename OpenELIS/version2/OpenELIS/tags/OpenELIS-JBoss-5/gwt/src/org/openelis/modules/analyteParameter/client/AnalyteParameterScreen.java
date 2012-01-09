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
package org.openelis.modules.analyteParameter.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenEvent;
import org.openelis.gwt.widget.tree.event.BeforeLeafOpenHandler;
import org.openelis.manager.AnalyteParameterManager;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.meta.QcMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalyteParameterScreen extends Screen {
    private AnalyteParameterManager manager;   
    private ModulePermission        userPermission;
        
    private AnalyteParameterScreen  screen;
    private ScreenNavigator         nav;
    
    private AppButton               queryButton, previousButton, nextButton, addButton,
                                    updateButton, commitButton, abortButton;
    
    private Dropdown<Integer>       referenceTableId, sampleTypeLatest, sampleTypePrevious;
    private AutoComplete<Integer>   referenceName;
    private TreeWidget              parameterTree;
    private TableWidget             atozTable;    
    
    private ScreenService           testService, qcService;  
    private ArrayList<TableDataRow> sampleTypeModel;
    private boolean                 warningShown;   
    
    public AnalyteParameterScreen() throws Exception {
        super((ScreenDefInt)GWT.create(AnalyteParameterDef.class));
        
        service = new ScreenService("controller?service=org.openelis.modules.analyteParameter.server.AnalyteParameterService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        qcService = new ScreenService("controller?service=org.openelis.modules.qc.server.QcService");

        userPermission =  UserCache.getPermission().getModule("analyteparameter");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Analyte Parameter Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() { 
                postConstructor();
            }
        });
    }
    
    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        manager = AnalyteParameterManager.getInstance();
        
        try{
            CategoryCache.getBySystemNames("type_of_sample","analyte_parameter_type");
        } catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });
        
        referenceTableId = (Dropdown)def.getWidget(AnalyteParameterMeta.getReferenceTableId());
        addScreenHandler(referenceTableId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                referenceTableId.setSelection(manager.getReferenceTableId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) { 
                if (state == State.QUERY)
                    return;
                manager = AnalyteParameterManager.getInstance();
                manager.setReferenceTableId(event.getValue());
                manager.setReferenceId(null);
                manager.setReferenceName("");
                DataChangeEvent.fire(screen);                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceTableId.enable(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
                referenceTableId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        referenceName = (AutoComplete<Integer>)def.getWidget("referenceName");
        addScreenHandler(referenceName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                referenceName.setSelection(manager.getReferenceId(), manager.getReferenceName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer rid, rtid;

                rid = event.getValue();
                rtid = referenceTableId.getValue();

                manager = AnalyteParameterManager.getInstance();
                manager.setReferenceId(rid);
                manager.setReferenceTableId(rtid);
                manager.setReferenceName(referenceName.getTextBoxDisplay());                

                if (rid != null && rtid != null) {                    
                    window.setBusy(consts.get("fetching"));
                    try {
                        switch (rtid) {
                            case ReferenceTable.TEST:
                                loadFromTest(rid);
                                break;
                            case ReferenceTable.QC:                                
                                loadFromQc(rid);
                                sampleTypeLatest.setModel(sampleTypeModel);
                                sampleTypePrevious.setModel(sampleTypeModel);
                                break;
                            case ReferenceTable.PROVIDER:
                                break;
                        }
                    } catch (NotFoundException e) {
                        // do nothing
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    window.clearStatus();
                }                
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceName.enable(EnumSet.of(State.QUERY,State.ADD).contains(event.getState()));
                referenceName.setQueryMode(event.getState() == State.QUERY);
            }
        });        
        
        referenceName.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {           
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {                
                if (referenceTableId.getValue() == null) {
                    Window.alert(consts.get("pleaseSelectType"));                    
                    event.cancel();
                }
            }
        });
        
        referenceName.addGetMatchesHandler(new GetMatchesHandler() {           
            public void onGetMatches(GetMatchesEvent event) {
                String search;
                               
                search = QueryFieldUtil.parseAutocomplete(event.getMatch());
                switch (referenceTableId.getValue()) {
                    case ReferenceTable.TEST:  
                        referenceName.showAutoMatches(getTestModel(search));
                        break;
                    case ReferenceTable.QC:    
                        referenceName.showAutoMatches(getQcModel(search));
                        break;
                    case ReferenceTable.PROVIDER:                        
                        break;
                }
            }
        });
        
        parameterTree = (TreeWidget)def.getWidget("parameterTree");
        sampleTypeLatest = ((Dropdown)parameterTree.getColumns().get("latest").get(2).getColumnWidget());
        sampleTypePrevious = ((Dropdown)parameterTree.getColumns().get("previous").get(2).getColumnWidget());

        addScreenHandler(parameterTree, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) { 
                parameterTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                parameterTree.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));                                
            }
        });       
        
        parameterTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>() {           
            public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
            }
        });         
        
        parameterTree.addBeforeLeafOpenHandler(new BeforeLeafOpenHandler() {    
            public void onBeforeLeafOpen(BeforeLeafOpenEvent event) {
                int i,j,index;
                TreeDataItem item;
                ArrayList<AnalyteParameterViewDO> list;
                AnalyteParameterViewDO data, newData;
                Query query;
                QueryData field;
                ArrayList<QueryData> fields;
                
                query = new Query();
                fields = new ArrayList<QueryData>();                                  
                                
                item = event.getItem();
                if (item.hasChildren())                     
                    return;
                
                data = (AnalyteParameterViewDO)item.data;
                
                field = new QueryData();
                field.query = data.getAnalyteId().toString();
                field.type = QueryData.Type.INTEGER;
                fields.add(field);
                
                field = new QueryData();
                field.query = manager.getReferenceId().toString();
                field.type = QueryData.Type.INTEGER;
                fields.add(field);
                    
                field = new QueryData();
                field.query = manager.getReferenceTableId().toString();            
                field.type = QueryData.Type.INTEGER;
                fields.add(field);
                query.setFields(fields);                
                
                try {
                    window.setBusy(consts.get("fetching"));
                    list = service.callList("fetchByAnalyteIdReferenceIdReferenceTableId", query);
                    index = manager.indexOf(data);
                    if (data.getId() == null || index == 0)
                        j = 1;
                    else
                        j = 0;
                    for (i = 0; i < list.size(); i++) {
                        newData = list.get(i);
                        if (newData.getId().equals(data.getId())) 
                            continue;                        
                        item.addItem(getItem(newData, "previous"));                        
                        manager.addParamaterAt(newData, index+i+j);                             
                    }                        
                } catch (NotFoundException ignE) {
                    // do nothing
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }       
                
                item.checkForChildren(item.hasChildren());
                parameterTree.refresh(true);
                window.clearStatus();
            }
        });
        
        parameterTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {           
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int c;
                TreeDataItem item;
                AnalyteParameterViewDO data;
              
                c = event.getCol();
                if (c < 2) {
                    event.cancel();
                    return;
                }
                
                item = parameterTree.getSelection();
                data = (AnalyteParameterViewDO)item.data;
                if (state == State.ADD) {                     
                    if (data.getId() != null)
                        event.cancel();
                } else if (state == State.UPDATE) {
                    if (data.getId() != null && "N".equals(data.getIsActive()) && !warningShown) {
                        Window.alert(consts.get("editPreviousWarning"));
                        warningShown = true;
                    }
                }      
            }
        });
        
        parameterTree.addCellEditedHandler(new CellEditedHandler() {            
            public void onCellUpdated(CellEditedEvent event) {                
                int r, c;
                AnalyteParameterViewDO data, prevData;
                Object val;
                ArrayList<TreeDataItem> list;
                TreeDataItem item, prevItem;
                Datetime ab, prevae;
                                
                r = event.getRow();
                c = event.getCol();
                item = parameterTree.getSelection();                
                data = (AnalyteParameterViewDO)item.data;
                val = parameterTree.getObject(r, c);
                
                switch (c) {
                    case 2:
                        data.setTypeOfSampleId((Integer)val);
                        break;
                    case 3:
                        data.setActiveBegin((Datetime)val);    
                        if (data.getId() != null && val == null) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("fieldRequiredException")));
                            return;
                        }                                           
                        if (!beginDateValid(item)) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("beginDateInvalidException")));
                            return;
                        }
                        if (!endDateValid(data)) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("endDateInvalidException")));
                            return;
                        }
                        if (changeActive(data))
                            parameterTree.setCell(r, 0, data.getIsActive());
                        ab = data.getActiveBegin();
                        if (ab == null)
                            return;
                        list = item.getItems();
                        if (list != null && list.size() > 0) {
                            prevItem = list.get(0);
                            prevData = (AnalyteParameterViewDO)prevItem.data;
                            /*
                             * we set the end date of the previous entry to be
                             * a minute before this entry's begin date if the end
                             * date of the previous entry is not before the begin 
                             * date of this entry
                             */                               
                            prevae = prevData.getActiveEnd();
                            if (prevae.compareTo(ab) >= 0) {
                                prevae.getDate().setTime(ab.getDate().getTime() - 60000);                                 
                                prevItem.cells.get(4).setValue(prevae);                                
                            }
                            prevData.setIsActive("N");
                            prevItem.cells.get(0).setValue("N");
                            parameterTree.refreshRow(prevItem);
                        }
                        break;
                    case 4:
                        data.setActiveEnd((Datetime)val);
                        if (data.getId() != null && val == null) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("fieldRequiredException")));
                            return;
                        }
                        if (!endDateValid(data)) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("endDateInvalidException")));
                            return;
                        }
                        if (changeActive(data)) {
                            parameterTree.setCell(r, 0, data.getIsActive());
                            list = item.getItems();
                            if (list != null && list.size() > 0) {
                                prevItem = list.get(0);
                                prevData = (AnalyteParameterViewDO)prevItem.data;                                                                                      
                                prevData.setIsActive("N");
                                prevItem.cells.get(0).setValue("N");
                                parameterTree.refreshRow(prevItem);
                            }
                        }
                        break;
                    case 5:
                        data.setP1((Double)val);
                        if (data.getId() != null && val == null) {
                            parameterTree.setCellException(r, c, new LocalizedException(consts.get("fieldRequiredException")));
                            return;
                        }
                        if (changeActive(data)) {
                            parameterTree.setCell(r, 0, data.getIsActive());
                            list = item.getItems();
                            if (list != null && list.size() > 0) {
                                prevItem = list.get(0);
                                prevData = (AnalyteParameterViewDO)prevItem.data;                                                                                      
                                prevData.setIsActive("N");
                                prevItem.cells.get(0).setValue("N");
                                parameterTree.refreshRow(prevItem);
                            }
                        }
                        break;
                    case 6:
                        data.setP2((Double)val);
                        break;
                    case 7:
                        data.setP3((Double)val);
                        break;
                }
            }
        });
        
        atozTable = (TableWidget)def.getWidget("atozTable");
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                query.setRowsPerPage(18);
                service.callList("query", query, new AsyncCallback<ArrayList<ReferenceIdTableIdNameVO>>() {
                    public void onSuccess(ArrayList<ReferenceIdTableIdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(consts.get("noMoreRecordInDir"));
                        } else {
                            Window.alert("Error: Analyte Parameter call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                ReferenceIdTableIdNameVO data;
                if (entry == null) {
                    return fetchByRefIdRefTableId(null, null);
                } else {
                    data = (ReferenceIdTableIdNameVO)entry;
                    return fetchByRefIdRefTableId(data.getReferenceId(), data.getReferenceTableId());
                }
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<ReferenceIdTableIdNameVO> result;
                ArrayList<TableDataRow> model;
                String name;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();                    
                    for (ReferenceIdTableIdNameVO entry : result) {
                        if (entry.getReferenceDescription() == null)
                            name = entry.getReferenceName();
                        else
                            name = entry.getReferenceName()+ " , " + entry.getReferenceDescription();
                        model.add(new TableDataRow(entry.getReferenceId(), entry.getReferenceTableId(), name));
                    }
                }
                return model;
            }
        };
        
        addScreenHandler(atozTable, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                nav.enable(enable);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }    
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("analyte_parameter_type");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            if ("analyte_param_type_test".equals(resultDO.getSystemName())) {
                row = new TableDataRow(ReferenceTable.TEST, resultDO.getEntry());
                row.enabled = ("Y".equals(resultDO.getIsActive()));
                model.add(row);
            } else if ("analyte_param_type_qc".equals(resultDO.getSystemName())) {
                row = new TableDataRow(ReferenceTable.QC, resultDO.getEntry());
                row.enabled = ("Y".equals(resultDO.getIsActive()));
                model.add(row);
            } else if ("analyte_param_type_provider".equals(resultDO.getSystemName())) {
                row = new TableDataRow(ReferenceTable.PROVIDER, resultDO.getEntry());
                row.enabled = ("Y".equals(resultDO.getIsActive()));
                model.add(row);                      
            }
        }        
        
        referenceTableId.setModel(model);
        
        ((Dropdown)atozTable.getColumnWidget(AnalyteParameterMeta.getReferenceTableId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("type_of_sample");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        sampleTypeModel = model;
        sampleTypeLatest.setModel(model);
        sampleTypePrevious.setModel(model);
    }   
    
    public boolean validate() {
        ArrayList<TableDataRow> sels;
        
        if (state != State.QUERY)
            return super.validate();
                
        sels = referenceTableId.getSelections();
        
        if (sels.size() == 0 || sels.get(0).key == null) { 
            //
            // type e.g. Test, QC etc. must be specified in query mode
            //
            referenceTableId.addException(new LocalizedException(consts.get("fieldRequiredException")));            
        } else if (sels.size() > 1) {
            //
            // we don't allow more than one type to be selected
            //
            referenceTableId.addException(new LocalizedException(consts.get("onlyOneTypeSelectionForQueryException")));
        } 
        return super.validate();
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = AnalyteParameterManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);
        
        setFocus(referenceTableId);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = AnalyteParameterManager.getInstance();

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(referenceTableId);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        TestTypeOfSampleManager ttsm;
        ArrayList<TableDataRow> model;
        
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            if (manager.getReferenceTableId().equals(ReferenceTable.TEST)) {
                ttsm = TestTypeOfSampleManager.fetchByTestId(manager.getReferenceId());
                model = getSampleTypeModel(ttsm);                
            } else {
                model = sampleTypeModel;
            }            
            sampleTypeLatest.setModel(model);
            sampleTypePrevious.setModel(model);
            
            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(referenceName);
            warningShown = false;
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        Query query;
        
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchByRefIdRefTableId(null, null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchByRefIdRefTableId(null, null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchByRefIdRefTableId(null, null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    private boolean fetchByRefIdRefTableId(Integer refId, Integer refTableId) {
        if (refId == null || refTableId ==  null) {
            manager = AnalyteParameterManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = AnalyteParameterManager.fetchActiveByReferenceIdReferenceTableId(refId, refTableId);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchByRefIdRefTableId(null, null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchByRefIdRefTableId(null, null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<TableDataRow> getTestModel(String search) {
        ArrayList<TableDataRow> model;
        ArrayList<TestMethodVO> list;
        
        model = new ArrayList<TableDataRow>();
        try {
            list  = testService.callList("fetchByName", search);
            for (TestMethodVO data: list) 
                model.add(new TableDataRow(data.getTestId(), data.getTestName()+", "+data.getMethodName()));            
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        return model;
    }
    
    private ArrayList<TableDataRow> getQcModel(String search) {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;
        ArrayList<TableDataRow> model;
        ArrayList<QcDO> list;
        
        model = new ArrayList<TableDataRow>();
        query = new Query();        
        fields = new ArrayList<QueryData>();
        
        field = new QueryData();
        field.key = AnalyteParameterMeta.getQcName();
        field.query = search + "*";
        field.type = QueryData.Type.STRING;
        fields.add(field);
        
        field = new QueryData();
        field.key = QcMeta.getIsActive();
        field.type = QueryData.Type.STRING;
        field.query = "Y";
        fields.add(field);
        
        query.setFields(fields);
        try {
            list  = qcService.callList("fetchActiveByName", query);
            for (QcDO data: list) 
                model.add(new TableDataRow(data.getId(), data.getName()+", "+ data.getLotNumber()));            
        } catch (NotFoundException e) {
            // do nothing
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        return model;
    }   
    
    private void loadFromTest(Integer id) throws Exception {        
        int i, j;
        AnalyteParameterViewDO data; 
        TestAnalyteManager tam;
        TestTypeOfSampleManager ttsm;
        ArrayList<TestAnalyteViewDO> list;
        ArrayList<TableDataRow> model;
        ArrayList<ArrayList<TestAnalyteViewDO>> grid;
        TestAnalyteViewDO ta;
        TestManager tm;
        
        tm = TestManager.fetchById(id);                
        tam = tm.getTestAnalytes();
        grid = tam.getAnalytes();
        //
        // load analyte parameters in this AnalyteParameterManager from the test's analytes
        //
        for (i = 0; i < grid.size(); i++) {
            list = grid.get(i);
            for (j = 0; j < list.size(); j++) {
                ta = list.get(j);
                if ("Y".equals(ta.getIsColumn()))
                    continue;
                data = new AnalyteParameterViewDO();
                data.setReferenceId(manager.getReferenceId());    
                data.setReferenceTableId(manager.getReferenceTableId());
                data.setReferenceName(manager.getReferenceName());
                data.setAnalyteId(ta.getAnalyteId());
                data.setAnalyteName(ta.getAnalyteName());         
                data.setIsActive("N");
                manager.addParamater(data);
            }
        }
        
        ttsm = tm.getSampleTypes();        
        //
        // load the dropdown that shows sample types from the test's sample types
        //
        model = getSampleTypeModel(ttsm);
        sampleTypeLatest.setModel(model);
        sampleTypePrevious.setModel(model);
    }
    
    private void loadFromQc(Integer id) throws Exception {
        int i;
        AnalyteParameterViewDO data; 
        QcAnalyteManager man;
        QcAnalyteViewDO qca;
                
        man = QcAnalyteManager.fetchByQcId(id);        
        for (i = 0; i < man.count(); i++) {
            qca = man.getAnalyteAt(i);
            data = new AnalyteParameterViewDO();
            data.setReferenceId(manager.getReferenceId());    
            data.setReferenceTableId(manager.getReferenceTableId());
            data.setReferenceName(manager.getReferenceName());
            data.setAnalyteId(qca.getAnalyteId());
            data.setAnalyteName(qca.getAnalyteName());         
            data.setIsActive("N");
            manager.addParamater(data);
        }
    }
    
    private ArrayList<TreeDataItem> getTreeModel() {
        int i;
        ArrayList<TreeDataItem> model;
        AnalyteParameterViewDO data;
        TreeDataItem item;
        Integer anaId;
        
        model = new ArrayList<TreeDataItem>();        
        if (manager == null)
            return model;
      
        if (state == State.ADD) {                                    
            anaId = null;
            for (i = 0; i < manager.count(); i++) {                
                data = manager.getParameterAt(i);
                item = getItem(data, "latest");                    
                item.checkForChildren(true);                    
                model.add(item);                             
            }            
        } else if (state == State.DISPLAY || state == State.UPDATE) {
            anaId = null;
            for (i = 0; i < manager.count(); i++) {                
                data = manager.getParameterAt(i);
                if (!data.getAnalyteId().equals(anaId)) {
                    item = getItem(data, "latest");              
                    item.checkForChildren(true);
                    model.add(item);                             
                }
                anaId = data.getAnalyteId();
            }
        }                

        return model;
    }    
    
    private TreeDataItem getItem(AnalyteParameterViewDO data, String leaftype) {
        TreeDataItem item;
        
        item = new TreeDataItem(8);
        item.leafType = leaftype;
        item.cells.get(0).setValue(data.getIsActive());
        item.cells.get(1).setValue(data.getAnalyteName());
        item.cells.get(2).setValue(data.getTypeOfSampleId());
        item.cells.get(3).setValue(data.getActiveBegin());
        item.cells.get(4).setValue(data.getActiveEnd());
        item.cells.get(5).setValue(data.getP1());
        item.cells.get(6).setValue(data.getP2());
        item.cells.get(7).setValue(data.getP3());
        item.data = data;
        
        return item;
    }      
    
    private boolean changeActive(AnalyteParameterViewDO data) {
        boolean active;
        
        if (data.getId() != null)
            return false;
        active = (data.getActiveBegin() != null && data.getActiveEnd() != null && data.getP1() != null);
        if (active && "N".equals(data.getIsActive())) {
            data.setIsActive("Y");
            return true;
        } else if (!active && "Y".equals(data.getIsActive())) {
            data.setIsActive("N");
            return true;
        }
        
        return false;
    }
    
    private ArrayList<TableDataRow> getSampleTypeModel(TestTypeOfSampleManager man) {
        ArrayList<TableDataRow> model;
        TestTypeOfSampleDO data;
        DictionaryDO dict;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        try {
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getTypeAt(i);
                dict = DictionaryCache.getById(data.getTypeOfSampleId());
                model.add(new TableDataRow(dict.getId(), dict.getEntry()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        return model;
    }
    

    private boolean beginDateValid(TreeDataItem item) {
        int i, index;
        AnalyteParameterViewDO data, chData;
        ArrayList<TreeDataItem> list;
        Datetime ab, chab;
        TreeDataItem tmp;

        data = (AnalyteParameterViewDO)item.data;
        ab = data.getActiveBegin();
        if (ab == null)
            return true;
        
        index = -1;
        if (item.parent != null) {
            list = item.parent.getItems();
            index = item.childIndex;
        } else {
            list = item.getItems();
            if (list == null || list.size() == 0)
                return true;           
        }
        
        //
        // begin date must be at least a minute after the begin date of each child
        //
        for (i = index+1; i < list.size(); i++) {
            tmp = list.get(i);
            chData = (AnalyteParameterViewDO)tmp.data;
            chab = chData.getActiveBegin();
            if (chab != null && (ab.getDate().getTime() - chab.getDate().getTime()) <= 60000)
                return false;
        }
        return true;
    }
    
    private boolean endDateValid(AnalyteParameterViewDO data) {
        Datetime ab, ae;
        
        ab = data.getActiveBegin();
        ae = data.getActiveEnd();
        if (ab == null || ae == null) 
            return true;               
        //
        // end date must be after begin date 
        //
        return (ab.compareTo(ae) == -1);        
    }
}
