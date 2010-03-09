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
package org.openelis.modules.auxiliary.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.BeforeDragStartEvent;
import org.openelis.gwt.event.BeforeDragStartHandler;
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
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.RowMovedEvent;
import org.openelis.gwt.widget.table.event.RowMovedHandler;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.meta.AuxFieldGroupMeta;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.ResultRangeNumeric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuxiliaryScreen extends Screen {
    private AuxFieldGroupManager               manager;
    private SecurityModule                     security;

    private ButtonGroup                        atoz;
    private ScreenNavigator                    nav; 

    private CalendarLookUp                     activeBegin, activeEnd;
    private TextBox                            name, description;
    private CheckBox                           isActive;
    private AppButton                          queryButton, previousButton, nextButton, addButton, updateButton,
                                               commitButton, abortButton, addAuxFieldButton, removeAuxFieldButton,
                                               addAuxFieldValueButton,removeAuxFieldValueButton, dictionaryLookUpButton;
    protected MenuItem                         auxFieldGroupHistory, auxFieldHistory, auxFieldValueHistory;
    private Dropdown<Integer>                  unitOfMeasureId, auxFieldValueTypeId;
    private AutoComplete<Integer>              analyte, scriptlet, method;
    private TableWidget                        auxFieldTable, auxFieldValueTable;
    private Integer                            typeDict, typeNumeric, typeDefault, prevSelFieldRow;
    private DictionaryLookupScreen             dictLookup;
    
    private ScreenService                      methodService,scriptletService,analyteService,dictionaryService;
    private ResultRangeNumeric                 rangeNumeric;   
    
    private ArrayList<GridFieldErrorException> valueErrorList;

    public AuxiliaryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(AuxiliaryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        scriptletService = new ScreenService("controller?service=org.openelis.modules.scriptlet.server.ScriptletService");
        methodService = new ScreenService("controller?service=org.openelis.modules.method.server.MethodService"); 
        analyteService = new ScreenService("controller?service=org.openelis.modules.analyte.server.AnalyteService");
        dictionaryService = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");
        
        security = OpenELIS.security.getModule("auxiliary");
        if (security == null)
            throw new SecurityException("screenPermException", "Auxiliary Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        manager = AuxFieldGroupManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        
        prevSelFieldRow = -1;
        
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
                                   security.hasSelectPermission());
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
                                 security.hasAddPermission());
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
                                    security.hasUpdatePermission());
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
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });
        
        auxFieldGroupHistory = (MenuItem)def.getWidget("auxFieldGroupHistory");
        addScreenHandler(auxFieldGroupHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                auxFieldGroupHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxFieldGroupHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        auxFieldHistory = (MenuItem)def.getWidget("auxFieldHistory");
        addScreenHandler(auxFieldHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                auxFieldHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxFieldHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        auxFieldValueHistory = (MenuItem)def.getWidget("auxFieldValueHistory");
        addScreenHandler(auxFieldValueHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                auxFieldValueHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxFieldValueHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        name = (TextBox)def.getWidget(AuxFieldGroupMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getGroup().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getGroup().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(AuxFieldGroupMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getGroup().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getGroup().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(AuxFieldGroupMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getGroup().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getGroup().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activeBegin = (CalendarLookUp)def.getWidget(AuxFieldGroupMeta.getActiveBegin());
        addScreenHandler(activeBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(manager.getGroup().getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if (event.getValue() != null)
                    manager.getGroup().setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeBegin.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                activeBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activeEnd = (CalendarLookUp)def.getWidget(AuxFieldGroupMeta.getActiveEnd());
        addScreenHandler(activeEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(manager.getGroup().getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if (event.getValue() != null)
                    manager.getGroup().setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeEnd.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                activeEnd.setQueryMode(event.getState() == State.QUERY);
            }
        });

        auxFieldTable = (TableWidget)def.getWidget("auxFieldTable");
        unitOfMeasureId = (Dropdown<Integer>)auxFieldTable.getColumnWidget(AuxFieldGroupMeta.getFieldUnitOfMeasureId());
        analyte = (AutoComplete<Integer>)auxFieldTable.getColumnWidget(AuxFieldGroupMeta.getFieldAnalyteName());
        method = (AutoComplete<Integer>)auxFieldTable.getColumnWidget(AuxFieldGroupMeta.getFieldMethodName());
        scriptlet = (AutoComplete<Integer>)auxFieldTable.getColumnWidget(AuxFieldGroupMeta.getFieldScriptletName());        
        addScreenHandler(auxFieldTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    auxFieldTable.load(getAuxFieldModel());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                auxFieldTable.enable(true);
                auxFieldTable.setQueryMode(event.getState() == State.QUERY);

                enable = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                auxFieldTable.enableDrag(enable);
                auxFieldTable.enableDrop(enable);
            }
        });
        
        rangeNumeric = new ResultRangeNumeric();     
        
        auxFieldTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                AuxFieldValueManager man;
                int r;                                
                
                r = auxFieldTable.getSelectedRow();
                
                if(state == State.DISPLAY || state == State.DEFAULT) {
                    try {                        
                        man = manager.getFields().getValuesAt(r);                    
                        auxFieldValueTable.load(getAuxFieldValueModel(man));
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    } 
                } 
            }
            
        });
        
        auxFieldTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                AuxFieldValueManager man;
                int r;          
                
                if(state == State.DISPLAY || state == State.DEFAULT) {
                    event.cancel();
                    return;
                }
                
                try {
                    r = event.getRow();                      
                    auxFieldValueTable.finishEditing();
                    man = manager.getFields().getValuesAt(r);                    
                    auxFieldValueTable.load(getAuxFieldValueModel(man));
                    setFieldValueErrors(r);
                    addAuxFieldValueButton.enable(true); 
                    removeAuxFieldValueButton.enable(false);
                    dictionaryLookUpButton.enable(false);
                    prevSelFieldRow = r;                    
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                } 
                
            }
            
        });
        
        auxFieldTable.addCellEditedHandler(new CellEditedHandler() {
              public void onCellUpdated(CellEditedEvent event) { 
                  int r, c;
                  Object val;
                  AuxFieldViewDO data;
                  TableDataRow row;
                  
                  r = event.getRow();
                  c = event.getCol();
                  val = auxFieldTable.getObject(r,c); 
                  data = null;
                  
                  try {
                      data = manager.getFields().getAuxFieldAt(r);
                  } catch (Exception e) {
                      Window.alert(e.getMessage());
                  }
                  
                  switch(c) {
                      case 0:
                          row = (TableDataRow)val;
                          data.setAnalyteId((Integer)row.key);
                          data.setAnalyteName(analyte.getTextBoxDisplay());
                          break;
                      case 1:
                          row = (TableDataRow)val;
                          data.setMethodId((Integer)row.key);
                          data.setMethodName(method.getTextBoxDisplay());
                          break;
                      case 2:
                          data.setUnitOfMeasureId((Integer)val);
                          break;
                      case 3:
                          data.setIsActive((String)val);
                          break;
                      case 4:
                          data.setIsRequired((String)val);
                          break;
                      case 5:
                          data.setIsReportable((String)val);
                          break;
                      case 6:
                          data.setDescription((String)val);
                          break;
                      case 7:
                          row = (TableDataRow)val;
                          data.setScriptletId((Integer)row.key);
                          data.setScriptletName(method.getTextBoxDisplay());
                          break;
                  }
              } 
          });
         
        
        // Screens now must implement AutoCompleteCallInt and set themselves as
        // the calling interface
        analyte.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                AnalyteDO data;
                ArrayList<AnalyteDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = analyteService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    }
                    analyte.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        method.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<MethodDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                                
                try {
                    list = methodService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    
                    for (MethodDO data : list)
                        model.add(new TableDataRow(data.getId(),data.getName()));                    
                    method.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

        });
        
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());
                
                try {
                    list = scriptletService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO data : list) {                       
                        model.add(new TableDataRow(data.getId(),data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        auxFieldTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                AuxFieldViewDO data;
                ArrayList<AuxFieldValueViewDO> values;
                AuxFieldValueManager man;                
                int r;
                
                r = event.getIndex();
                try {
                    data = new AuxFieldViewDO();
                    data.setIsActive("Y");
                    data.setIsReportable("N");
                    data.setIsRequired("N");
                     
                    values = new ArrayList<AuxFieldValueViewDO>();
                    values.add(new AuxFieldValueViewDO());
                    manager.getFields().addAuxFieldAndValues(data, values);
                    
                    auxFieldTable.setCell(r, 3, "Y");
                    auxFieldTable.setCell(r, 4, "N");
                    auxFieldTable.setCell(r, 5, "N");
                                        
                    auxFieldValueTable.finishEditing();
                    man = manager.getFields().getValuesAt(r);                    
                    auxFieldValueTable.load(getAuxFieldValueModel(man));                   
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        auxFieldTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getFields().removeAuxFieldAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        auxFieldTable.addRowMovedHandler(new RowMovedHandler() {
            public void onRowMoved(RowMovedEvent event) {
                try {
                    manager.getFields().moveField(event.getOldIndex(), event.getNewIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }                
            }            
        });
        
        auxFieldTable.enableDrag(true);
        auxFieldTable.enableDrop(true);
        auxFieldTable.addTarget(auxFieldTable);
        
        auxFieldTable.addBeforeDragStartHandler(new BeforeDragStartHandler<TableRow>(){            
            public void onBeforeDragStart(BeforeDragStartEvent<TableRow> event) {
                auxFieldValueTable.finishEditing();                
            }            
        });

        addAuxFieldButton = (AppButton)def.getWidget("addAuxFieldButton");
        addScreenHandler(addAuxFieldButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                auxFieldValueTable.finishEditing();
                r = auxFieldTable.numRows();
                auxFieldTable.addRow();
                auxFieldTable.selectRow(r);
                prevSelFieldRow = r;
                auxFieldTable.scrollToSelection(); 
                auxFieldTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxFieldButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
        
        removeAuxFieldButton = (AppButton)def.getWidget("removeAuxFieldButton");
        addScreenHandler(removeAuxFieldButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                auxFieldValueTable.finishEditing();
                r = auxFieldTable.getSelectedRow();
                if (r > -1 && auxFieldTable.numRows() > 0) {
                    auxFieldTable.deleteRow(r);
                    auxFieldValueTable.load(getAuxFieldValueModel(null));
                    prevSelFieldRow = -1;
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAuxFieldButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        auxFieldValueTable = (TableWidget)def.getWidget("auxFieldValueTable");
        auxFieldValueTypeId = (Dropdown<Integer>)auxFieldValueTable.getColumnWidget(AuxFieldGroupMeta.getFieldValueTypeId());
        addScreenHandler(auxFieldValueTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxFieldValueTable.load(getAuxFieldValueModel(null));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxFieldValueTable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));                
            }
        });   
        
        auxFieldValueTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                int r, fr;
                GridFieldErrorException ex;
                
                auxFieldTable.finishEditing();
                r = auxFieldValueTable.getSelectedRow();  
                fr = auxFieldTable.getSelectedRow();
                
                for(int i = 0; i < valueErrorList.size(); i++) {
                    ex = valueErrorList.get(i);
                    if(ex.getColumnIndex() == r)
                        valueErrorList.remove(i);
                }
                
                clearFieldError(fr);                  
                
                addAuxFieldValueButton.enable(true);
                if(auxFieldValueTable.numRows() > 1)
                    removeAuxFieldValueButton.enable(true); 
                else 
                    removeAuxFieldValueButton.enable(false);
                dictionaryLookUpButton.enable(true);
            }
            
        });        
        
        auxFieldValueTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, fr;
                Object val;
                AuxFieldValueViewDO data;
                
                fr = prevSelFieldRow;
                if(fr == -1) 
                    fr = auxFieldTable.getSelectedRow(); 
                r = event.getRow();    
                c = event.getCol();
                val = auxFieldValueTable.getObject(r,c);    
                data = null;                
                
                try {
                    data = manager.getFields().getValuesAt(fr).getAuxFieldValueAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                
                switch(c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        auxFieldValueTable.clearCellExceptions(r, 1);
                        try {
                            validateValue(data, (String)auxFieldValueTable.getObject(r, 1));
                        } catch (LocalizedException e) {
                            auxFieldValueTable.setCellException(r, 1, e);
                        }
                        break;
                    case 1:
                        //we need to set the new value before we can validate it
                        data.setValue((String)val);
                        
                        auxFieldValueTable.clearCellExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (LocalizedException e) {
                            auxFieldValueTable.setCellException(r, c, e);
                        }
                        break;
                }                 
            }
        });

        auxFieldValueTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {    
                int r;
                
                r = auxFieldTable.getSelectedRow();
                try {
                    manager.getFields().getValuesAt(r).addAuxFieldValue(new AuxFieldValueViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        auxFieldValueTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int fr;
                
                fr = auxFieldTable.getSelectedRow();
                try {
                    manager.getFields().getValuesAt(fr).removeAuxFieldValueAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }                
            }            
        });

        addAuxFieldValueButton = (AppButton)def.getWidget("addAuxFieldValueButton");
        addScreenHandler(addAuxFieldValueButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = auxFieldValueTable.numRows();
                auxFieldValueTable.addRow();
                auxFieldValueTable.selectRow(r);                      
                auxFieldValueTable.scrollToSelection();
                auxFieldValueTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxFieldValueButton.enable(false);                     
            }
        });
        
        removeAuxFieldValueButton = (AppButton)def.getWidget("removeAuxFieldValueButton");
        addScreenHandler(removeAuxFieldValueButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = auxFieldValueTable.getSelectedRow();
                if (r > -1 && auxFieldValueTable.numRows() > 0) 
                    auxFieldValueTable.deleteRow(r);                                   
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                removeAuxFieldValueButton.enable(false); 
            }
        });

        dictionaryLookUpButton = (AppButton)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showDictionary(null,null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryLookUpButton.enable(false);
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
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
                            Window.alert("Error: Auxiliary call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = AuxFieldGroupMeta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }            
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // unit of measure dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("unit_of_measure"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        unitOfMeasureId.setModel(model);

        // aux field value type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("aux_field_value_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        auxFieldValueTypeId.setModel(model);
        
        try {
            typeDict    = DictionaryCache.getIdFromSystemName("aux_dictionary");
            typeNumeric = DictionaryCache.getIdFromSystemName("aux_numeric");
            typeDefault = DictionaryCache.getIdFromSystemName("aux_default");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    protected void query() {
        manager = AuxFieldGroupManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = AuxFieldGroupManager.getInstance();
        manager.getGroup().setIsActive("N");

        valueErrorList = new ArrayList<GridFieldErrorException>();
        setState(State.ADD);
        DataChangeEvent.fire(this);

        prevSelFieldRow = -1;
        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));        
    }

    protected void update() {
        window.setBusy("Locking Record for update...");
        valueErrorList = new ArrayList<GridFieldErrorException>();
        prevSelFieldRow = -1; 
        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

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
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    

    protected void auxFieldGroupHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getGroup().getId(), manager.getGroup().getName());
        HistoryScreen.showHistory(consts.get("auxFieldGroupHistory"),
                                  ReferenceTable.AUX_FIELD_GROUP, hist);                
    }
    
    protected void auxFieldHistory() {
        int i, count;
        IdNameVO refVoList[];
        AuxFieldManager man;
        AuxFieldViewDO data;

        try {
            man = manager.getFields();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getAuxFieldAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getAnalyteName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("auxFieldHistory"), ReferenceTable.AUX_FIELD, refVoList);
    }
    
    protected void auxFieldValueHistory() {        
        int i, count, r;
        IdNameVO refVoList[];
        AuxFieldValueManager afvm;
        AuxFieldValueViewDO data;
        String value;
        DictionaryDO dict;
        String entry;
        Integer typeId;

        try {
            r = auxFieldTable.getSelectedRow();
            if(r == -1)
                return;
            
            afvm = manager.getFields().getValuesAt(r);
            count = afvm.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = afvm.getAuxFieldValueAt(i);
                typeId = data.getTypeId();
                dict = DictionaryCache.getEntryFromId(typeId);
                if(dict != null)
                    entry = dict.getEntry();
                else
                    entry = typeId.toString();
                
                value = data.getDictionary();
                if(value == null)
                    value = data.getValue();
                
                if(value != null)
                    refVoList[i] = new IdNameVO(data.getId(), entry+":  "+value);
                else
                    refVoList[i] = new IdNameVO(data.getId(), entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("auxFieldValueHistory"), ReferenceTable.AUX_FIELD_VALUE,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = AuxFieldGroupManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                window.setBusy(consts.get("fetching"));
                manager = AuxFieldGroupManager.fetchWithFields(id);
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
            setState(State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    public void showErrors(ValidationErrorsList errors) {
        ArrayList<LocalizedException> formErrors;
        TableFieldErrorException tfe;
        GridFieldErrorException gfe;
        FormErrorException fe;
        FieldErrorException flde;
        ArrayList<Integer> rowList;
        Integer row; 
        
        formErrors = new ArrayList<LocalizedException>();
        rowList = new ArrayList<Integer>();
        
        for (Exception ex : errors.getErrorList()) {            
            if (ex instanceof TableFieldErrorException) {
                if(ex instanceof GridFieldErrorException) {
                    gfe = (GridFieldErrorException)ex;
                    row = gfe.getRowIndex();
                    //
                    // this check is made here in order to make sure that the same error
                    // is not added to the same aux field row just because more than one 
                    // aux field values belonging to it are in error
                    //
                    if(!rowList.contains(row)) {
                        auxFieldTable.setCellException(row,AuxFieldGroupMeta.getFieldAnalyteName(),
                                                       new LocalizedException("errorsWithAuxFieldValuesException"));
                        rowList.add(row);
                    }
                    valueErrorList.add(gfe);
                } else {
                    tfe = (TableFieldErrorException)ex;
                    auxFieldTable.setCellException(tfe.getRowIndex(),tfe.getFieldName(),tfe);
                }
            } else if (ex instanceof FormErrorException) {
                fe = (FormErrorException)ex;
                formErrors.add(fe);

            } else if (ex instanceof FieldErrorException){
                flde = (FieldErrorException)ex;
                ((HasField)def.getWidget(flde.getFieldName())).addException(flde);
            } 
        }

        if (formErrors.size() == 0)
            window.setError(consts.get("correctErrors"));
        else if (formErrors.size() == 1)
            window.setError(formErrors.get(0).getMessage());
        else {
            window.setError("(Error 1 of " + formErrors.size() + ") " + formErrors.get(0).getMessage());
            window.setMessagePopup(formErrors, "ErrorPanel");
        }
    }
    
    private ArrayList<TableDataRow> getAuxFieldModel() {
        int i;
        AuxFieldViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            for(i = 0; i < manager.getFields().count(); i++) {
                data = manager.getFields().getAuxFieldAt(i);
                row = new TableDataRow(8);
                row.key = data.getId();
                row.cells.get(0).setValue(new TableDataRow(data.getAnalyteId(), data.getAnalyteName()));
                row.cells.get(1).setValue(new TableDataRow(data.getMethodId(), data.getMethodName()));
                row.cells.get(2).setValue(data.getUnitOfMeasureId());
                row.cells.get(3).setValue(data.getIsActive());
                row.cells.get(4).setValue(data.getIsRequired());
                row.cells.get(5).setValue(data.getIsReportable());
                row.cells.get(6).setValue(data.getDescription());
                row.cells.get(7).setValue(new TableDataRow(data.getScriptletId(), data.getScriptletName()));
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;       
    }
    
    private ArrayList<TableDataRow> getAuxFieldValueModel(AuxFieldValueManager man) {
        int i;
        AuxFieldValueViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        String value;    
        
        model = new ArrayList<TableDataRow>();
        if (man == null)
            return model;
        
        try {
            for(i = 0; i < man.count(); i++) {
                data = man.getAuxFieldValueAt(i);
                // either show them the value or the dictionary entry
                value = data.getValue();
                if (data.getDictionary() != null)
                    value = data.getDictionary();
                
                row = new TableDataRow(2);
                row.key = data.getId();
                row.cells.get(0).setValue(data.getTypeId());
                row.cells.get(1).setValue(value);
                
                model.add(row);
            }                        
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;               
    }
    
    private IdNameVO getDictionary(String entry) {
        ArrayList<IdNameVO> list;
        Query query;  
        QueryData field;
        
        entry = DataBaseUtil.trim(entry); 
        if (entry == null)
            return null;
        
        query = new Query();
        field = new QueryData();
        field.key = CategoryMeta.getDictionaryEntry();
        field.type = QueryData.Type.STRING;
        field.query = entry;
        query.setFields(field);       
        
        field = new QueryData();
        field.key = CategoryMeta.getIsSystem();
        field.type = QueryData.Type.STRING;
        field.query = "N";
        query.setFields(field); 
        
        try {
            list = dictionaryService.callList("fetchByEntry", query);
            if (list.size() == 1)
                return list.get(0);
            else if (list.size() > 1)                
                showDictionary(entry,list);
        } catch(NotFoundException e){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        return null;
    }
    
    private void validateValue(AuxFieldValueViewDO data, String value) throws LocalizedException {
        IdNameVO dict;

        if(value == null)
            return;
        
        try {
            if (typeDict.equals(data.getTypeId())) {
                dict = getDictionary((String)value);
                if (dict != null) {
                    data.setValue(dict.getId().toString());
                    data.setDictionary(dict.getName());
                } else {
                    data.setDictionary(null);
                    throw new LocalizedException("aux.invalidValueException");
                }
            } else if (typeNumeric.equals(data.getTypeId())) {
                rangeNumeric.setRange((String)value);
                data.setValue(rangeNumeric.toString());
            } else if (typeDefault.equals(data.getTypeId())) {
                data.setValue((String)value);
            } else if (DataBaseUtil.trim(value) != null){
                throw new LocalizedException("valuePresentForTypeException");
            }
        } catch (LocalizedException e) {
            data.setValue(null);
            data.setDictionary(null);
            throw e;
        }
    }
    
    private void showDictionary(String entry,ArrayList<IdNameVO> list) {
        ScreenWindow modal;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("DictionaryLookup Error: " + e.getMessage());
                return;
            }
        
            dictLookup.addActionHandler(new ActionHandler<DictionaryLookupScreen.Action>() {
                public void onAction(ActionEvent<DictionaryLookupScreen.Action> event) {
                    int r, fr; 
                    IdNameVO entry;
                    AuxFieldValueViewDO data;
                    ArrayList<IdNameVO> list;

                    if (event.getAction() == DictionaryLookupScreen.Action.OK) {
                        list = (ArrayList<IdNameVO>)event.getData();
                        if (list != null) {
                            r = auxFieldValueTable.getSelectedRow();
                            fr = auxFieldTable.getSelectedRow();
                            if (r == -1) {
                                window.setError(consts.get("aux.noSelectedRow"));
                                return;
                            }
                            entry = list.get(0);
                            try {
                                data = manager.getFields().getValuesAt(fr).getAuxFieldValueAt(r);
                                data.setValue(entry.getId().toString());
                                data.setDictionary(entry.getName());
                                data.setTypeId(typeDict);            
                                auxFieldValueTable.setCell(r, 0, typeDict);
                                auxFieldValueTable.setCell(r, 1, data.getDictionary());
                                auxFieldValueTable.clearCellExceptions(r, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                auxFieldValueTable.setCell(r, 1, "");
                                Window.alert("DictionaryLookup Error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(dictLookup);
        dictLookup.setScreenState(State.DEFAULT);
        if (list != null) {
            dictLookup.clearFields();
            dictLookup.setQueryResult(entry, list);
        } else if (entry != null) {
            dictLookup.clearFields();
            dictLookup.executeQuery(entry);
        }
    }
    
    private void setFieldValueErrors(int index) {
        GridFieldErrorException ex;
        
        if(state == State.ADD || state == State.UPDATE) {
            for(int i = 0; i < valueErrorList.size(); i++) {
                ex = valueErrorList.get(i);
                if(ex.getRowIndex() == index) 
                    auxFieldValueTable.setCellException(ex.getColumnIndex(), ex.getFieldName(),ex);                    
            }
        }
    }
    
    private void clearFieldError(int index) {
        ArrayList<LocalizedException> list;
        LocalizedException ex;
        
        list = auxFieldTable.getCell(index, 0).getExceptions();
        if(list != null) {
            for(int i = 0 ; i < list.size(); i++) {
                ex = list.get(i);
                if("errorsWithAuxFieldValuesException".equals(ex.getKey())) {
                    auxFieldTable.removeCellException(index, 0, ex);
                } 
            }                    
        }
    }
        
}