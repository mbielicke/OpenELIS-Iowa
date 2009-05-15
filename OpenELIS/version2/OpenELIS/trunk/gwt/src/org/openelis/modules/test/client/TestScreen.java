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
package org.openelis.modules.test.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTab;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTreeWidget;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ModelUtil;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeModel;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeWidgetEvents;
import org.openelis.gwt.widget.tree.event.TreeWidgetListener;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestScreen extends OpenELISScreenForm<TestForm, Query<TableDataRow<Integer>>> implements
                                                                                          ClickListener,
                                                                                          TabListener,
                                                                                          TableManager,
                                                                                          TableWidgetListener,
                                                                                          TreeManager,
                                                                                          TreeWidgetListener,
                                                                                          ChangeListener,
                                                                                          PopupListener {

    private TableDataModel<TableDataRow<Integer>> testAnalyteModel, sampleTypeUnitModel,
                    resultUnitModel, resultGroupModel, testResultDefaultModel;

    private AppButton removeSampleTypeButton, removePrepTestButton,
                    addRowButton, deleteButton, groupAnalytesButton,
                    ungroupAnalytesButton, removeReflexTestButton,
                    removeWSItemButton, removeTestSectionButton,
                    dictionaryLookUpButton, removeTestResultButton,
                    addResultTabButton;

    private TreeWidget analyteTreeWidget = null;

    private int group = 0;

    private TableWidget reflexTestTableWidget,              // the widget for the table for reflexive tests
                        prepTestTableWidget,                // the widget for the table for prep tests  
                        wsItemTableWidget,                  // the widget for the table for worksheet items
                        sampleTypeTableWidget,              // the widget for the table for types of sample 
                        sectionTableWidget,                 // the widget for the table for test sections  
                        resultTableWidget,                  // the widget for the table for test results
                        wsAnalyteTableWidget;               // the widget for the table for worksheet analytes   


    private TestMetaMap TestMeta = new TestMetaMap();

    private KeyListManager keyList = new KeyListManager();

    private ScreenTextBox testId;
    private TextBox testName;

    private TabPanel resultPanel,testTabPanel;    
    
    private ScreenTab screenTestTab;
    
    private MenuItem dupItem;    

    private ArrayList<TableDataModel<TableDataRow<Integer>>> resultTableModelCollection,
                                                             resultDropdownModelCollection;

    private int prevSelTabIndex = -1, tempAnaId = -1, tempResId = -1;

    private static String panelString = "<VerticalPanel/>";

    private ArrayList<TableDataRow<Integer>> selectedRows;

    private ScreenWindow pickerWindow;
    
    private Dropdown revisionMethod, sortingMethod, reportingMethod, trailer,
                     testFormat, testScriptlet, label, wsFormat,wsScriptlet;  
    
    private boolean unitListChanged, analyteListChanged;
    
    AsyncCallback<TestForm> checkModels = new AsyncCallback<TestForm>() {     
        public void onSuccess(TestForm rpc) {
              if(rpc.revisionMethods != null) {
                  setRevisionMethodsModel(rpc.revisionMethods);
                  rpc.revisionMethods = null;
              }
              if(rpc.reflexFlags != null) {
                  setReflexFlagsModel(rpc.reflexFlags);
                  rpc.reflexFlags = null;
              }
              if(rpc.reportingMethods != null) {
                  setReportingMethods(rpc.reportingMethods);
                  rpc.reportingMethods = null;
              }
              if(rpc.analyteTypes != null) {
                  setAnalyteTypesModel(rpc.analyteTypes);
                  rpc.analyteTypes = null;
              }
              if(rpc.resultFlags != null) {
                  setResultFlagsModel(rpc.resultFlags);
                  rpc.resultFlags = null;
              }
              if(rpc.resultTypes != null) {
                  setResultTypesModel(rpc.resultTypes);
                  rpc.resultTypes = null;
              }
              if(rpc.roundingMethods != null) {
                  setRoundingMethodsModel(rpc.roundingMethods);
                  rpc.roundingMethods = null;
              }
              if(rpc.units != null) {
                  setUnitsOfMeasureModel(rpc.units);
                  rpc.units = null;
              }
              if(rpc.sampleTypes != null) {
                  setSampleTypesModel(rpc.sampleTypes);
                  rpc.sampleTypes = null;
              }
              if(rpc.sectionFlags != null) {
                  setSectionFlagsModel(rpc.sectionFlags);
                  rpc.sectionFlags = null;
              }
              if(rpc.testFormats != null) {
                  setTestFormatsModel(rpc.testFormats);
                  rpc.testFormats = null;
              }
              if(rpc.sortingMethods != null) {
                  setSortingMethodsModel(rpc.sortingMethods);
                  rpc.sortingMethods = null;
              }
              if(rpc.wsAnalyteFlags != null) {
                  setWorksheetAnalyteFlagsModel(rpc.wsAnalyteFlags);
                  rpc.wsAnalyteFlags = null;
              }
              if(rpc.wsItemTypes != null) {
                  setWorksheetItemTypesModel(rpc.wsItemTypes);
                  rpc.wsItemTypes = null;
              }
              if(rpc.wsFormats != null) {
                  setWorksheetFormatsModel(rpc.wsFormats);
                  rpc.wsFormats = null;
              }
        }

        public void onFailure(Throwable caught) {
              
          }
      };

    public TestScreen() {
        super("org.openelis.modules.test.server.TestService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new TestForm());
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel, atozButtons;
        CommandChain chain;       
        ResultsTable atozTable;
        
        ScreenTableWidget s;
        ScreenTreeWidget analyteTree;
        ScreenMenuPanel duplicatePanel;

        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        bpanel = (ButtonPanel)getWidget("buttons");
        
        testTabPanel = (TabPanel)getWidget("testTabPanel");
        resultPanel = (TabPanel)getWidget("resultTabPanel");

        duplicatePanel = (ScreenMenuPanel)widgets.get("optionsMenu");
        dupItem = ((MenuItem)((MenuItem)duplicatePanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0));
        tempAnaId = -1;
        tempResId = -1;

        unitListChanged = false;
        analyteListChanged = false;
        //
        // this is done to remove an unwanted tab that gets added to
        // testTabPanel, for some reason, when you put a tab panel inside one
        // of its tabs
        //
        testTabPanel.remove(3);
        
        screenTestTab = (ScreenTab)widgets.get("testTabPanel");
 
        //
        // this is done to remove the default tab that's added to
        // resultPanel,so that it looks empty when the screen gets drawn
        //
        resultPanel.remove(0);
        //
        // we are interested in getting button actions in two places,
        // modelwidget and this screen.
        //
        chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);

        //
        // initializing the buttons that are not part of any button panel
        //
        removeSampleTypeButton = (AppButton)getWidget("removeSampleTypeButton");
        removePrepTestButton = (AppButton)getWidget("removePrepTestButton");
        removeReflexTestButton = (AppButton)getWidget("removeReflexTestButton");
        removeWSItemButton = (AppButton)getWidget("removeWSItemButton");
        deleteButton = (AppButton)getWidget("deleteButton");
        addRowButton = (AppButton)getWidget("addRowButton");
        groupAnalytesButton = (AppButton)getWidget("groupAnalytesButton");
        ungroupAnalytesButton = (AppButton)getWidget("ungroupAnalytesButton");
        removeTestSectionButton = (AppButton)getWidget("removeTestSectionButton");
        dictionaryLookUpButton = (AppButton)getWidget("dictionaryLookUpButton");
        removeTestResultButton = (AppButton)getWidget("removeTestResultButton");
        addResultTabButton = (AppButton)getWidget("addResultTabButton");
        
        testId = (ScreenTextBox)widgets.get(TestMeta.getId());
        testName = (TextBox)getWidget(TestMeta.getName());

        //
        // initializing the tree-table widget
        //
        analyteTree = (ScreenTreeWidget)widgets.get("analyteTree");
        analyteTreeWidget = (TreeWidget)analyteTree.getWidget();
        
        // initializing all the dropdown widgets
        revisionMethod = (Dropdown)getWidget(TestMeta.getRevisionMethodId());
        sortingMethod = (Dropdown)getWidget(TestMeta.getSortingMethodId());
        reportingMethod = (Dropdown)getWidget(TestMeta.getReportingMethodId());
        trailer = (Dropdown)getWidget(TestMeta.getTestTrailerId());
        testFormat = (Dropdown)getWidget(TestMeta.getTestFormatId());
        testScriptlet = (Dropdown)getWidget(TestMeta.getScriptletId());
        label = (Dropdown)getWidget(TestMeta.getLabelId());
        wsFormat = (Dropdown)getWidget(TestMeta.getTestWorksheet().getFormatId());
        wsScriptlet = (Dropdown)getWidget(TestMeta.getTestWorksheet().getScriptletId());
        
        
        //
        // loading the models of all the dropdowns
        //   
        setRevisionMethodsModel(form.revisionMethods);        
        setReportingMethods(form.reportingMethods);                                    
        setTestFormatsModel(form.testFormats);
        setSortingMethodsModel(form.sortingMethods);        
        setWorksheetFormatsModel(form.wsFormats);        
        setLabelsModel(form.labels);
        setTestTrailersModel(form.trailers);
        setTestScriptletsModel(form.scriptlets);
        setWorksheetScriptletsModel(form.scriptlets);                              

        //
        // set the model for each column. Note that we have to do it twice:
        // once for the normal table and once for query table.
        // see setTableDropdownModel(TableWidget widget,int column,DataModel
        // model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeTableWidget = (TableWidget)s.getWidget();
        sampleTypeTableWidget.addTableWidgetListener(this);
        
        setSampleTypesModel(form.sampleTypes);

        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestTableWidget = (TableWidget)s.getWidget();
        
        setPrepTestsModel(form.testMethods);

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestTableWidget = (TableWidget)s.getWidget();
        reflexTestTableWidget.addTableWidgetListener(this);
        
        setReflexFlagsModel(form.reflexFlags);
        setReflexTestsModel(form.testMethods); 
        setTableDropdownModel(reflexTestTableWidget,2,getSingleRowModel());

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemTableWidget = (TableWidget)s.getWidget();       

        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionTableWidget = (TableWidget)s.getWidget();
        sectionTableWidget.addTableWidgetListener(this);
        
        setSectionsModel(form.sections);
        setSectionFlagsModel(form.sectionFlags);

        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultTableWidget = (TableWidget)s.getWidget();
        resultTableWidget.addTableWidgetListener(this);
        
        setResultFlagsModel(form.resultFlags);  
        setResultTypesModel(form.resultTypes);
        setRoundingMethodsModel(form.roundingMethods);

        s = (ScreenTableWidget)widgets.get("worksheetAnalyteTable");
        wsAnalyteTableWidget = (TableWidget)s.getWidget();        

        wsAnalyteTableWidget.addTableWidgetListener(this);
        
        setWorksheetAnalyteFlagsModel(form.wsAnalyteFlags);
        setWorksheetItemTypesModel(form.wsItemTypes);
        
        setUnitsOfMeasureModel(form.units);

        //
        // set dropdown models for column 1 and 3
        //                       
        setAnalyteTypesModel(form.analyteTypes);        

        resultGroupModel = new TableDataModel<TableDataRow<Integer>>();
        
        resultGroupModel.setDefaultSet(new TableDataRow<Integer>(null,new StringObject("")));
        
        setTableDropdownModel(analyteTreeWidget,4,"analyte",resultGroupModel);

        analyteTreeWidget.model.manager = this;
        analyteTreeWidget.enabled(false);
        analyteTreeWidget.addTreeWidgetListener(this);

        // override the callbacks
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);

        form.testAnalyte.analyteTree.setValue(analyteTreeWidget.model.getData());
        
        //
        // the submit method puts the TreeDataModel from the TreeWidget in the
        // tree field from the rpc
        //        
        analyteTree.submit(form.testAnalyte.analyteTree);        
             
        super.afterDraw(success);

        analyteTree.enable(true);
       
        //form.testAnalyte.testResultsTable.setValue(resultTableWidget.model.getData());
        
        testResultDefaultModel = (TableDataModel<TableDataRow<Integer>>)resultTableWidget.model.getData().clone();
        
    }

    /**
     * This function is for responding to the various events that take place on
     * the screen and that can be identified by the value of the argument
     * action. This is different from the handling of normal click events,
     * because more than one widget can send te same action and also the widget
     * sending an action may not be a button. This is called for all the objects
     * added to the CommandChain for the screen in the afterDraw method
     */
    public void performCommand(Enum action, Object obj) {
             
        if (action == KeyListManager.Action.FETCH) {                    
            form.entityKey = (Integer)((Object[])obj)[0];
            setTableDropdownModel(reflexTestTableWidget,2,getSingleRowModel());
            fillResultModelCollections();
            //TODO make sure to use this with drag and drop as well
            analyteListChanged = false;
            prevSelTabIndex = -1;
            dupItem.enable(true);
        } else if (action == ButtonPanel.Action.QUERY && obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf(":") != -1) {
                getTests(query.substring(6));
                return;
            }
        } else if (action == ButtonPanel.Action.COMMIT && obj instanceof AppButton
                   && !(state == State.QUERY)) {                               
            if (sectionTableWidget.model.getData().size() == 0) {
                    Window.alert(consts.get("atleastOneSection"));
                    return;
                }
            if (form.testAnalyte.load || state == State.ADD) {
                if (analyteTreeWidget.model.getData().size() > 0 && (resultTableModelCollection.size() == 0)) {
                    boolean ok = Window.confirm(consts.get("analyteNoResults"));
                    if (!ok)
                        return;

                } else if (resultTableModelCollection.size() > 0 && analyteTreeWidget.model.getData()
                                                                                          .size() == 0) {
                    boolean ok = Window.confirm(consts.get("resultNoAnalytes"));
                    if (!ok)
                        return;

                } else if (!checkResGrpSel()) {
                    boolean ok = Window.confirm(consts.get("resultGrpNotSelForAll"));
                    if (!ok)
                        return;
                }

            }
            if (!validate()) {
                window.setStatus(consts.get("correctErrors"), "ErrorPanel");
                return;
            }
            if (!dataInWorksheetForm()) {
                form.worksheet.load = false;
            }
        }
        super.performCommand(action, obj);
    }

    /**
     * Overridden to manage various clicks that don't come through perform
     * command.
     */
    public void onClick(Widget sender) {
        if (sender == removeSampleTypeButton)
            onSampleTypeRowButtonClick();
        else if (sender == removePrepTestButton)
            onPrepTestRowButtonClick();
        else if (sender == removeReflexTestButton)
            onReflexTestRowButtonClick();
        else if (sender == removeWSItemButton)
            onWSItemRowButtonClick();
        else if (sender == addRowButton)
            onAddRowButtonClicked();
        else if (sender == deleteButton)
            onDeleteTreeItemButtonClicked();
        else if (sender == removeTestSectionButton)
            onTestSectionRowButtonClicked();
        else if (sender == removeTestResultButton)
            onTestResultRowButtonClicked();
        else if (sender == dictionaryLookUpButton)
            onDictionaryLookUpButtonClicked();
        else if (sender == addResultTabButton)
            onAddResultTabButtonClicked();
        else if (sender == groupAnalytesButton)
            onGroupAnalytesButtonClicked();
        else if (sender == ungroupAnalytesButton)
            onUngroupAnalytesButtonClicked();
        else if (sender instanceof MenuItem) {
            if ("duplicateRecord".equals(((String)((MenuItem)sender).objClass))) {
                onDuplicateRecordClick();
            }
        }

    }

    public void query() {
        super.query();
        dupItem.enable(false);
        enableTableAutoAdd(false);
        testId.setFocus(true);
        resultPanel.clear();
        removeSampleTypeButton.changeState(ButtonState.DISABLED);
        removePrepTestButton.changeState(ButtonState.DISABLED);
        removeReflexTestButton.changeState(ButtonState.DISABLED);
        removeWSItemButton.changeState(ButtonState.DISABLED);
        deleteButton.changeState(ButtonState.DISABLED);
        addRowButton.changeState(ButtonState.DISABLED);
        removeTestResultButton.changeState(ButtonState.DISABLED);
        removeTestSectionButton.changeState(ButtonState.DISABLED);
        dictionaryLookUpButton.changeState(ButtonState.DISABLED);
        addResultTabButton.changeState(ButtonState.DISABLED);
        groupAnalytesButton.changeState(ButtonState.DISABLED);
        ungroupAnalytesButton.changeState(ButtonState.DISABLED);
    }
    

    public void add() {
        super.add();
        group = 0;
        dupItem.enable(false);        
        resetResultGroupDropDownModel();
        resetTestAnalyteDropDownModel();
        resetUnitDropDownModel();
        resetTestResultDropDownModel();
        resultPanel.clear();
        resultTableModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
        resultDropdownModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
        prevSelTabIndex = -1;        

        //TODO use this with drag and drop as well 
        analyteListChanged = true;
        
        enableTableAutoAdd(true);
        //
        // disable anything that is not editable and set focus to the widget
        // which should get the first focus for editing
        //
        testId.enable(false);
        testName.setFocus(true);
    }

    public void abort() {
        group = 0;
        enableTableAutoAdd(false);
        dupItem.enable(false);
        resultPanel.clear();        
        
        //
        // On committing after update, the model in the unit dropdown in resultTableWidget
        // (resultUnitModel) could have gotten changed from what it was when 
        // fetch was done as it depends on the list of units in sampleTypeTableWidget,
        // so when abort is executed the unit selected for some of the test results
        // in the database may not be in resultUnitModel and thus they won't show up
        // when the data in resultTableWidget is refreshed.
        // Hence, to make those units to show up the units dropdown widget in resultTableWidget
        // is loaded with the list of all units in the system
        //
        if(state == State.UPDATE)
            setTableDropdownModel(resultTableWidget, 0, sampleTypeUnitModel);
        
        fillResultModelCollections();   
        //TODO use this with drag and drop as well 
        analyteListChanged = false;
        prevSelTabIndex = -1;
        super.abort();
    }

    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {            
            //
            // disable anything that is not editable and set focus to the widget
            // which should get the first focus for editing
            //
            enableTableAutoAdd(true);
            testId.enable(false);
            testName.setFocus(true);
            dupItem.enable(false);

                        
            //
            // we set this flag to true to force the unit dropdown in resultTableWidget to
            // be reloaded with the list of units specific to the current test 
            //
            unitListChanged = true;            
            reloadTestResultUnitDropdownModel();
            
            fillResultModelCollections();
            
            //TODO use this with drag and drop as well 
            analyteListChanged = true;
            
            prevSelTabIndex = -1;
        }
    };

    protected AsyncCallback<TestForm> commitUpdateCallback = new AsyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {
            if (form.status == Form.Status.invalid) {               
                if (form.testAnalyte.resultTableModelCollection != null)
                    resultTableModelCollection = form.testAnalyte.resultTableModelCollection;
            } else {
                enableTableAutoAdd(false);
            }
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected AsyncCallback<TestForm> commitAddCallback = new AsyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {
            if (form.status == Form.Status.invalid) {                
                if (form.testAnalyte.resultTableModelCollection != null)
                    resultTableModelCollection = form.testAnalyte.resultTableModelCollection;
            } else {
                enableTableAutoAdd(false);
                dupItem.enable(false);
            }
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected void submitForm() {
        super.submitForm();

        if (!(state == State.QUERY)) {
            if (prevSelTabIndex == -1 && resultTableModelCollection.size() > 0) {
                //         
                // if a user doesn't select any tab from resultPanel then any new
                // rows added to the model for the first tab won't get added to the
                // model stored at index zero in resultModelCollection because the
                // code in onBeforeTabSelected() wont get a chance to execute;
                // this line makes sure that on committing a test's data,even if
                // no tab was selected from resultPanel, the latest model for the
                // first tab makes its way in resultModelCollection
                //            
                resultTableModelCollection.set(0,(TableDataModel)resultTableWidget.model.getData());
            }

            form.testAnalyte.resultTableModelCollection = resultTableModelCollection;

            resultTableWidget.finishEditing();
            sectionTableWidget.finishEditing();
            reflexTestTableWidget.finishEditing();
        }
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {            
        TableDataModel<TableDataRow<Integer>> model;
        ArrayList<TableDataModel<TableDataRow<Integer>>> list;
        TableDataRow<Integer> mr, dr;                 
        AbstractField mf,df;
        
        
        if (sender == resultPanel) {
            if (tabIndex != 0 && prevSelTabIndex == -1)
                prevSelTabIndex = 0;
            //
            // when a user clicks a tab on the test results panel (resultPanel)
            // to see the data for that result group, the model for the table
            // displayed under it, is taken from the list of models
            // (resultModelCollection) and set to the table
            // 
            if (resultTableModelCollection.size() > tabIndex) {
                if (prevSelTabIndex > -1 && prevSelTabIndex != tabIndex) {
                    resultTableWidget.finishEditing();
                    model = (TableDataModel<TableDataRow<Integer>>)resultTableWidget.model.getData()
                                                                                     .clone();
                    list = resultTableWidget.model.getData().getDeletions();

                    if(list!=null) { 
                     for (int i = 0; i < list.size(); i++) {
                        model.getDeletions().add((TableDataRow<Integer>)list.get(i).clone());
                     }
                    }
                    
                    for (int i = 0; i < model.size(); i++) {
                        dr = resultTableWidget.model.getRow(i);
                        mr = model.get(i);

                        for (int j = 0; j < dr.size(); j++) {
                            mf = (AbstractField)mr.cells[j];
                            df = (AbstractField)dr.cells[j];

                            for (int k = 0; k < df.getErrors().size(); k++) {
                                mf.addError((String)df.getErrors().get(k));
                            }
                        }
                    }
                    resultTableModelCollection.set(prevSelTabIndex,model);
                }

                if (!(prevSelTabIndex == -1 && tabIndex == 0)) {
                    model = resultTableModelCollection.get(tabIndex);
                    resultTableWidget.model.load(model);
                    prevSelTabIndex = tabIndex;
                }
            }
        } else {            
            form.testTabPanel = screenTestTab.getSelectedTabKey();
            if (state != State.QUERY) {                   
                if (tabIndex == 1) { 
                    if(!form.sampleType.load) {                       
                        fillSampleTypes();
                    }
                    resultTableWidget.finishEditing();
                } else if (tabIndex == 2) {
                    if(!form.testAnalyte.load) {                                    
                        fillTestAnalyte();
                    }                                     
                    reloadTestResultUnitDropdownModel();                                      
                } else if (tabIndex == 3) {
                    if(!form.prepAndReflex.load) {                          
                        fillPrepTestsReflexTests();
                    }                        
                    reloadTestAnalyteDropdownModel();
                    resultTableWidget.finishEditing();
                } else if (tabIndex == 4) { 
                    if(!form.worksheet.load) {                      
                        fillWorksheetLayout();
                    }
                    analyteTreeWidget.finishEditing();
                }
            }
        } 
    }

    /**
     * Overriden to allow lazy loading various tabs of the various tab panels on
     * the screen
     */
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        return true;
    }

    public boolean validate() {
        if (treeItemsHaveErrors()) {
            return false;
        }

        if (resultRowsHaveErrors()) {
            return false;
        }

        if (worksheetItemRowsHaveErrors()) {
            return false;
        }
        return true;
    }

    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {        
        if (widget == wsItemTableWidget)
            return (addRow.cells[0]).getValue() != null;
        else if (widget == reflexTestTableWidget) {
            if (!isReflexRowEmpty(addRow))
                return true;
            else 
                return false;
        } else if (widget == resultTableWidget) {
            if (((addRow.cells[0]).getValue() != null) || ((addRow.cells[1]).getValue() != null))
            return true;
        }

        return (addRow.cells[0]).getValue() != null;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget,TableDataRow set,int row,int col) {
        DropDownField<Integer> ddfield;
        TableDataRow<Integer> vset,ddset;
        IntegerObject iobj;        
        int rg;

        vset = null;
        
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
            // column 2 is for the Results dropdown
            if (widget == reflexTestTableWidget && col == 2 && set !=null) {
                ddfield = (DropDownField<Integer>)set.cells[1];
                //
                // checks to see if the analyte list has at least one option in it
                // 
                if(ddfield.getValue() != null && ddfield.getValue().size() > 0)
                    vset = ddfield.getValue().get(0);
                                                 
                    if (vset != null && vset.enabled == true
                                    && ddfield.getErrors().size() == 0) {
                        if(vset.key != null) {
                         ddset = ModelUtil.getRowByKey(testAnalyteModel,vset.key);
                         iobj = (IntegerObject)ddset.getData();
                         if(iobj!=null) {
                             rg = iobj.getValue();                          
                             setTestResultsForResultGroup(rg, set);
                         } 
                        }                          
                    }   else {
                        Window.alert(consts.get("selectAnaBeforeRes"));
                        return false;
                    }

            } else if (widget == resultTableWidget && resultPanel.getTabBar()
                                                            .getTabCount() == 0
                       && state != State.QUERY) {
                Window.alert(consts.get("atleastOneResGrp"));
                return false;
            }
            return true;
        }

        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        return false;
    }

    /**
     * This function is called whenever a given cell in a table looses focus,
     * which signifies that it is no longer being edited
     */
    public void finishedEditing(SourcesTableWidgetEvents sender,int row, int col) {
        TestGeneralPurposeRPC tsrpc = new TestGeneralPurposeRPC();
        if (sender == sectionTableWidget && row < sectionTableWidget.model.getData()
                                                                .size()
            && col == 1) {
            final int currRow = row;
            final Integer selValue = (Integer)((DropDownField)sectionTableWidget.model.getRow(row).cells[col]).getSelectedKey();
            tsrpc.key = selValue;
            // 
            // This code is for finding out which option was chosen in the "Options"
            // column of the Test Section table in the Test Details tab, so that
            // the values for the other rows can be changed accordingly
            //
            screenService.call("getCategorySystemName",tsrpc,
                               new AsyncCallback<TestGeneralPurposeRPC>() {
                                   public void onSuccess(TestGeneralPurposeRPC result) {
                                       if ("test_section_default".equals(result.stringValue)) {
                                           for (int iter = 0; iter < sectionTableWidget.model.numRows(); iter++) {
                                               if (iter != currRow) {
                                                   // 
                                                   // if the option chosen is "Default" for this
                                                   // row, then all the other rows must be set to
                                                   // the blank option
                                                   //                                                                                                                                                                     
                                                   DropDownField field = (DropDownField)sectionTableWidget.model.getRow(iter).cells[1];
                                                   field.setValue(new TableDataRow<Integer>(null));
                                               }
                                           }
                                           sectionTableWidget.model.refresh();
                                       } else {
                                           if (selValue != null) {
                                               for (int iter = 0; iter < sectionTableWidget.model.numRows(); iter++) {
                                                   DropDownField field = (DropDownField)sectionTableWidget.model.getRow(iter).cells[1];
                                                   //
                                                   // if the option chosen is "Ask" or "Match User Location" for
                                                   // this row, then all the other rows must be set to
                                                   // the same option which is this one
                                                   // 
                                                   field.setValue(new TableDataRow<Integer>(selValue));
                                               }
                                           }
                                           sectionTableWidget.model.refresh();
                                       }
                                   }

                                   public void onFailure(Throwable caught) {
                                       Window.alert(caught.getMessage());
                                       window.clearStatus();
                                   }
                               });

        } else if (sender == resultTableWidget && row < resultTableWidget.model.getData()
                                                                     .size()) {
            final String value = ((StringField)resultTableWidget.model.getRow(row).cells[2]).getValue();

            if (col == 2 && !"".equals(value.trim())) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)resultTableWidget.model.getRow(row).cells[1]).getSelectedKey();

                tsrpc.key = selValue;

                //              
                // This code is for finding out which option was chosen in the 
                // "Type" column of the Test Results table in the "Analyte" tab, 
                // so that error checking or formatting can be done for the value 
                // set in the "Value" column
                //
                screenService.call("getCategorySystemName",tsrpc,new SyncCallback() {
                                       public void onSuccess(Object result) {
                                           Double[] darray = new Double[2];
                                           String finalValue = "";
                                           if ("test_res_type_dictionary".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               //
                                               // Find out if this value is stored in the database if
                                               // the type chosen was "Dictionary"
                                               //
                                               TestGeneralPurposeRPC tsrpc1 = new TestGeneralPurposeRPC();
                                               tsrpc1.stringValue = value;
                                               screenService.call("getEntryIdForEntryText",tsrpc1,new SyncCallback() {
                                                                      public void onSuccess(Object result1) {
                                                                          //
                                                                          // If this value is not stored in the
                                                                          // database then add error to this
                                                                          // cell in the "Value" column
                                                                          //
                                                                          if (((TestGeneralPurposeRPC)result1).key == null) {
                                                                              resultTableWidget.model.setCellError(currRow,2,
                                                                                                              consts.get("illegalDictEntryException"));
                                                                          } else {
                                                                              TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);
                                                                              if (set.key == null) {                                                                                  
                                                                                  set.key = getNextTempResId();
                                                                              }
                                                                              resultTableWidget.model.setCell(currRow,2,
                                                                                                         ((TestGeneralPurposeRPC)result1).stringValue);

                                                                              checkAndAddNewResultValue(value,set);
                                                                          }
                                                                      }

                                                                      public void onFailure(Throwable caught) {
                                                                          Window.alert(caught.getMessage());
                                                                          window.clearStatus();
                                                                      }
                                                                  });

                                           } else if ("test_res_type_numeric".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               //
                                               // Get the string that was entered if the type
                                               // chosen was "Numeric" and try to break it up at
                                               // the "," if it follows the pattern number,number
                                               //
                                               String[] strList = value.split(",");
                                               boolean convert = false;
                                               if (strList.length == 2) {
                                                   for (int iter = 0; iter < strList.length; iter++) {
                                                       String token = strList[iter];
                                                       try {
                                                           // 
                                                           // Convert each number obtained
                                                           // from the string and store its value
                                                           // converted to double if its a valid
                                                           // number, into an array
                                                           //
                                                           Double doubleVal = Double.valueOf(token);
                                                           darray[iter] = doubleVal.doubleValue();
                                                           convert = true;
                                                       } catch (NumberFormatException ex) {
                                                           convert = false;
                                                       }
                                                   }
                                               }

                                               if (convert) {
                                                   //
                                                   // If its a valid string store the converted
                                                   // string back into the column otherwise add
                                                   // an error to the cell and store empty
                                                   // string into the cell
                                                   //  
                                                   if (darray[0].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue = darray[0].toString() + ".0"
                                                                    + ",";
                                                   } else {
                                                       finalValue = darray[0].toString() + ",";
                                                   }

                                                   if (darray[1].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue += darray[1].toString() + ".0";
                                                   } else {
                                                       finalValue += darray[1].toString();
                                                   }
                                                   resultTableWidget.model.setCell(currRow,2,finalValue);
                                                   TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);                                                   

                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();                                                                                                          

                                                   checkAndAddNewResultValue(finalValue,set);

                                               } else {
                                                   resultTableWidget.model.setCellError(currRow,2,
                                                                                   consts.get("illegalNumericFormatException"));
                                               }

                                           } else if ("test_res_type_titer".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               //
                                               // Get the string that was entered if the type chosen was "Titer" and try to
                                               // break it up at the ":" if it follows the pattern "number:number"
                                               //
                                               String[] strList = value.split(":");
                                               boolean convert = false;
                                               if (strList.length == 2) {
                                                   for (int iter = 0; iter < strList.length; iter++) {
                                                       String token = strList[iter];
                                                       try {
                                                           //
                                                           // Convert each number obtained from the string and store its
                                                           // value converted to double if it's a valid number, into an
                                                           // array
                                                           //
                                                           Double doubleVal = Double.valueOf(token);
                                                           darray[iter] = doubleVal.doubleValue();
                                                           convert = true;
                                                       } catch (NumberFormatException ex) {
                                                           convert = false;
                                                       }
                                                   }
                                               }
                                               if (convert) {
                                                   //
                                                   // If it's a valid string store the converted
                                                   // string back into the column otherwise add an
                                                   // error to the cell and store empty string
                                                   // into the cell
                                                   //
                                                   if (darray[0].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue = darray[0].toString() + ".0"
                                                                    + ":";
                                                   } else {
                                                       finalValue = darray[0].toString() + ":";
                                                   }

                                                   if (darray[1].toString()
                                                                .indexOf(".") == -1) {
                                                       finalValue += darray[1].toString() + ".0";
                                                   } else {
                                                       finalValue += darray[1].toString();
                                                   }
                                                   resultTableWidget.model.setCell(currRow,2,finalValue);
                                                   TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);                                                   

                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();                                                                                                         

                                                   checkAndAddNewResultValue(finalValue,set);

                                               } else {
                                                   resultTableWidget.model.setCellError(currRow,2,
                                                                                   consts.get("illegalTiterFormatException"));
                                               }

                                           } else if ("test_res_type_date".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultTableWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultTableWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateDate(value);
                                                   resultTableWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultTableWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalDateValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           } else if ("test_res_type_date_time".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultTableWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultTableWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateDateTime(value);
                                                   resultTableWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultTableWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalDateTimeValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           } else if ("test_res_type_time".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultTableWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultTableWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateTime(value);
                                                   resultTableWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultTableWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalTimeValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           }

                                       }

                                       public void onFailure(Throwable caught) {
                                           Window.alert(caught.getMessage());
                                           window.clearStatus();
                                       }
                                   });

            }
        } else if (sender == wsItemTableWidget && row < wsItemTableWidget.model.getData()
                                                                     .size()) {
            if (col == 0 && row < wsItemTableWidget.model.getData().size()) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)wsItemTableWidget.model.getRow(row).cells[1]).getSelectedKey();
                tsrpc.key = selValue;

                screenService.call("getCategorySystemName",tsrpc,
                                   new AsyncCallback<TestGeneralPurposeRPC>() {
                                       public void onSuccess(TestGeneralPurposeRPC result) {
                                           Integer value = (Integer)(wsItemTableWidget.model.getRow(currRow).cells[0]).getValue();
                                           if ("pos_duplicate".equals(result.stringValue) || "pos_fixed".equals(result.stringValue)) {
                                               if (value == null) {
                                                   wsItemTableWidget.model.setCellError(currRow,0,
                                                                                   consts.get("fixedDuplicatePosException"));
                                               }
                                           } else if (!("pos_duplicate".equals(result.stringValue)) && !("pos_fixed".equals(result.stringValue))) {
                                               if (value != null) {
                                                   wsItemTableWidget.model.setCellError(currRow,
                                                                                   0,
                                                                                   consts.get("posSpecifiedException"));
                                               }
                                           }
                                       }

                                       public void onFailure(Throwable caught) {
                                           Window.alert(caught.getMessage());
                                           window.clearStatus();
                                       }
                                   });
            }
        } else if (sender == reflexTestTableWidget && row < reflexTestTableWidget.model.getData()
                                                                             .size()) {
            TableDataRow<Integer> rset;
            TableDataRow<Integer> vset = null;
            DropDownField<Integer> ddfield;
            ArrayList<TableDataRow<Integer>> value;
            IntegerObject iobj;
            int rg;
            
            rset = reflexTestTableWidget.model.getRow(row);
            ddfield = (DropDownField<Integer>)rset.cells[col];
            value = ddfield.getValue();
            
            if(value != null && value.size() > 0) {
             if (col == 1) {
                vset = (TableDataRow<Integer>)value.get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestTableWidget.model.setCellError(row,1,
                                                        consts.get("analyteDeleted"));
                } else {
                     iobj = (IntegerObject)vset.getData(); 
                     if(iobj!=null) {
                         rg = iobj.getValue();
                         setTestResultsForResultGroup(rg, rset);
                     }
                }        
            } else if (col == 2) {
                vset = (TableDataRow<Integer>)value.get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestTableWidget.model.setCellError(row,2,
                                                        consts.get("resultDeleted"));
                }
            }
          }   
        } else if (sender == sampleTypeTableWidget && col == 1
                   && row < sampleTypeTableWidget.model.getData().size()) {
            unitListChanged = true;            
        } else if (sender == wsAnalyteTableWidget && col == 1) {
            TableDataRow<Integer> trow = wsAnalyteTableWidget.model.getRow(row);
            setAnalytesAvailable(trow);
            setRepeatValue(trow);
            wsAnalyteTableWidget.model.refresh();
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {

    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {

    }

    public void finishedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        int index,tempAnaId;
        Integer resGrpNum;
        boolean createNewSet;
        
        TreeDataItem item;
        DropDownField<Integer> field;
        String selText,prevVal;        
        TableDataRow<Integer> ddset;
        IntegerObject data;       

        index = analyteTreeWidget.modelIndexList[row];        
        tempAnaId = 0;
        item = analyteTreeWidget.model.getRow(index);
        createNewSet = false;
        
        if (col == 0) {            
            field = (DropDownField)item.cells[col];
            selText = (String)field.getTextValue();
            //
            // checks to see if any analyte was selected for this row, if not then
            // an error is added to the cell, this needs to be done because the tree 
            // widget on its own doesn't support it right now. The model of the tree
            // widget is refreshed to make sure that the newly added error shows 
            // in the cell as soon as the cell loses focus 
            //
            if (field.getSelectedKey() != null) {
               /* if (!field.getErrors()
                          .contains(consts.get("fieldRequiredException"))) {
                    field.addError(consts.get("fieldRequiredException"));
                    analyteTreeWidget.model.refresh();
                    return;
                }
            }*/
            if (item.key == null) {                             
                    createNewSet = true;
               } else {                    
                   data = (IntegerObject)item.getData();
                   ddset = ModelUtil.getRowByKey(testAnalyteModel,item.key);
                   if (ddset != null) {
                        prevVal = (String)ddset.cells[0].getValue();
                        if (!prevVal.trim().equals(selText.trim())) {
                            
                            setErrorToReflexFields(consts.get("analyteNameChanged"),
                                                   item.key,1);
                            
                            ddset.cells[0].setValue(selText);
                            
                            ((TableDropdown)reflexTestTableWidget.columns.get(1)
                               .getColumnWidget()).setModel(testAnalyteModel);
                            
                            updateWorksheetAnalyte(data.getValue(),
                               (Integer)field.getSelectedKey(),selText);
                            
                            data.setValue((Integer)field.getSelectedKey());
                        }
                    }
                }            

            if (createNewSet) {
                tempAnaId = getNextTempAnaId();
                ddset = new TableDataRow<Integer>(tempAnaId,new StringObject(selText));                
                item.key = tempAnaId;                                

                testAnalyteModel.add(ddset);
                ((TableDropdown)reflexTestTableWidget.columns.get(1)
                                                        .getColumnWidget()).setModel(testAnalyteModel);

                addWorksheetAnalyte((Integer)field.getSelectedKey(), selText);
            }
          }
        } else if (col == 4) {
            field = (DropDownField)item.cells[col];            
            resGrpNum = (Integer)field.getSelectedKey();                  
            resultTableWidget.finishEditing();

            //
            // checks to see if a result group was chosen, if so, sets the data 
            // field of the dropdown option in testAnalyteDropdownModel that 
            // corresponds to this TreeDataItem  
            //
            if (item.key != null && resGrpNum != null) {                                  
             ddset = ModelUtil.getRowByKey(testAnalyteModel,item.key); 
             if(ddset != null) {
                 data = (IntegerObject)ddset.getData();
                 if(data == null)                      
                     data = new IntegerObject();                 
                 data.setValue(resGrpNum);
                 ddset.setData(data);                 
             } 
            }
                 
        }

    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {

    }

    public void stopEditing(SourcesTreeWidgetEvents sender, int row, int col) {

    }
    

    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        if ((state == State.ADD) || (state == State.UPDATE))
            return true;

        return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem item, int row) {
        if ("analyte".equals(item.leafType)) {
            DropDownField field = (DropDownField)item.cells[4];
            Integer key = (Integer)field.getSelectedKey();
            if (key != null && key > 0) {
                //
                // the following check is there to make sure that if the user
                // somehow previously committed an analyte with a result group 
                // selected that had no results in it or if this data was present
                // in the database due to any reason, the analyte row can still
                // be selected because the selectTab(key-1) won't throw an
                // ArrayIndexOutOfBoundsException. This exception would be thrown
                // if we tried to select a tab that wasn't there
                //
                if((key-1) < resultPanel.getTabBar().getTabCount())
                    resultPanel.getTabBar().selectTab(key-1);
            }
        }        
        return true;
    }

    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        final DictionaryEntryPickerScreen pickerScreen = (DictionaryEntryPickerScreen)pickerWindow.content;
        
        TestGeneralPurposeRPC tgrpc = new TestGeneralPurposeRPC();
        tgrpc.stringValue = "test_res_type_dictionary";        
        screenService.call("getEntryIdForSystemName",tgrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                               public void onSuccess(TestGeneralPurposeRPC result) {
                                   TableDataRow<Integer> dictSet = new TableDataRow<Integer>(result.key);
                                   addResultRows(pickerScreen.selectedRows,dictSet);
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });

    }
    
    /**
     * This function adds new rows to the Results table with the values for the
     * "Value" column being set to the entries that were chosen through the
     * dictionary entry lookup screen
     */
    private void addResultRows(ArrayList<TableDataRow<Integer>> selectedRows,
                              TableDataRow<Integer> dictSet) {
        List<String> entries = new ArrayList<String>();
        TableDataRow<Integer> row = null;
        TableDataRow<Integer> set;
        String entry = null;        

        
        if (selectedRows != null) {
          if (resultPanel.getTabBar().getTabCount() == 0) {
                Window.alert(consts.get("atleastOneResGrp"));
                return;
          }
            for (int iter = 0; iter < selectedRows.size(); iter++) {
                set = selectedRows.get(iter);
                entry = (String)((Field)set.cells[0]).getValue();
                if (entry != null && !entries.contains(entry.trim())) {
                    entries.add(entry);
                    row = (TableDataRow<Integer>)resultTableWidget.model.createRow();
                    row.cells[1].setValue(dictSet);
                    row.cells[2].setValue(entry);                                        
                    row.key = getNextTempResId();
                    resultTableWidget.model.addRow(row);
                    checkAndAddNewResultValue(entry, row);
                }
            }
            resultTableWidget.model.refresh();
        }
    }

    private AsyncCallback<TestForm> fetchForDuplicateCallBack = new AsyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {
            form = result;
            resultTableModelCollection = result.testAnalyte.resultTableModelCollection;
            resultDropdownModelCollection = result.testAnalyte.resultDropdownModelCollection;
            flipSignsInAnalyteDropDown();
            ((TableDropdown)reflexTestTableWidget.columns.get(1).getColumnWidget()).setModel(testAnalyteModel);            
            loadScreen();
            enable(true);
            setState(State.ADD);
            enableTableAutoAdd(true);
            window.setDone(consts.get("enterInformationPressCommit"));
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
            window.setDone("Load Failed");
            setState(State.DEFAULT);
            form.entityKey = null;
        }
    };
    
    /**
     * This method fetches the data model stored in resultDropDownModelMap with
     * the analyte's id as the key and sets the model as the model for the
     * "Results" column's dropdown in the Reflexive Tests table
     */
    private void setTestResultsForResultGroup(int resultGroup,
                                          TableDataRow<Integer> set) {
        TableDataModel<TableDataRow<Integer>> model;
        DropDownField<Integer> field;
        TableDataRow<Integer> prevSet,blankSet;  
        Integer key;               
        
        if(resultDropdownModelCollection != null && resultDropdownModelCollection.size() > resultGroup-1) {            
            model = (TableDataModel<TableDataRow<Integer>>)resultDropdownModelCollection.get(resultGroup-1);
            field = (DropDownField<Integer>)set.cells[2];
            //if(field.getValue() != null && field.getValue().size() > 0)
              //  prevSet = field.getValue().get(0);            
            key = (Integer)field.getSelectedKey();
            
            if (model != null) {
                field.setModel(model);                
                // if the new model doesn't contain the dataset that was selected
                // from the previous one, then it may cause an
                // ArrayIndexOutOfBoundsException
                // when you try to edit the Results column, this code makes sure
                // that in that situation the blank set is set as the value so
                // that it becomes the one that's selected and one that still 
                // belongs to the current data model.
                // Otherwise the previously selected set is set as the value                 
                if (key != null) {
                    prevSet = ModelUtil.getRowByKey(model, key);
                    if(prevSet!=null) {
                        field.setValue(prevSet);
                    }
                    else { 
                        blankSet = new TableDataRow<Integer>(null);
                        field.setValue(blankSet);
                    }
                }
                else { 
                    blankSet = new TableDataRow<Integer>(null);
                    field.setValue(blankSet);
                }
            }
        }
    }

    private void getTests(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(TestMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }

    /**
     * This function sets the Data Model for a dropdown field that's inside the
     * TableWidget "widget" in the column "column" to the argument "model"
     */
    private void setTableDropdownModel(TableWidget widget,int column,
                                       TableDataModel<TableDataRow<Integer>> model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget()).setModel(model);
    }

    /**
     * This function sets the Data Model for a dropdown field that's inside the
     * TreeWidget "widget" in the column "column" of the node with leafType
     * "leafType" to the argument "model"
     */
    private void setTableDropdownModel(TreeWidget widget,int column,String leafType,
                                       TableDataModel<TableDataRow<Integer>> model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget(leafType)).setModel(model);
    }
    
    
    private void setRevisionMethodsModel(TableDataModel<TableDataRow<Integer>> revisionMethods) {
        revisionMethod.setModel(revisionMethods);        
    }
    
    private void setReportingMethods(TableDataModel<TableDataRow<Integer>> reportingMethods) {
        reportingMethod.setModel(reportingMethods);        
    }
    
    private void setTestFormatsModel(TableDataModel<TableDataRow<Integer>> testFormats) {
        testFormat.setModel(testFormats);        
    }
    
    private void setSortingMethodsModel(TableDataModel<TableDataRow<Integer>> sortingMethods) {        
        sortingMethod.setModel(sortingMethods);
    }
    
    private void setWorksheetFormatsModel(TableDataModel<TableDataRow<Integer>> wsFormats) {       
        wsFormat.setModel(wsFormats);
    }
    
    private void setLabelsModel(TableDataModel<TableDataRow<Integer>> labels) {
        label.setModel(labels);        
    }
    
    private void setTestTrailersModel(TableDataModel<TableDataRow<Integer>> trailers) {
        trailer.setModel(trailers);        
    }
    
    private void setTestScriptletsModel(TableDataModel<TableDataRow<Integer>> scriptlets) {
        testScriptlet.setModel(scriptlets);        
    }
    
    private void setWorksheetScriptletsModel(TableDataModel<TableDataRow<Integer>> scriptlets) {
        wsScriptlet.setModel(scriptlets);
        
    }
    
    private void setPrepTestsModel(TableDataModel<TableDataRow<Integer>> testMethods) {
        ((TableDropdown)prepTestTableWidget.columns.get(0).getColumnWidget()).setModel(testMethods);
    }
    
    private void setReflexTestsModel(TableDataModel<TableDataRow<Integer>> testMethods) {
        ((TableDropdown)reflexTestTableWidget.columns.get(0).getColumnWidget()).setModel(testMethods);        
    }
    
    private void setReflexFlagsModel(TableDataModel<TableDataRow<Integer>> reflexFlags) {
        ((TableDropdown)reflexTestTableWidget.columns.get(3).getColumnWidget()).setModel(reflexFlags);        
    }
    
    private void setAnalyteTypesModel(TableDataModel<TableDataRow<Integer>> analyteTypes) {        
        ((TableDropdown)analyteTreeWidget.columns.get(1).getColumnWidget("analyte")).setModel(analyteTypes);
    }
    
    private void setResultFlagsModel(TableDataModel<TableDataRow<Integer>> resultFlags) {
        ((TableDropdown)resultTableWidget.columns.get(6).getColumnWidget()).setModel(resultFlags);        
    }
    
    private void setResultTypesModel(TableDataModel<TableDataRow<Integer>> resultTypes) {
        ((TableDropdown)resultTableWidget.columns.get(1).getColumnWidget()).setModel(resultTypes);        
    }
    
    private void setRoundingMethodsModel(TableDataModel<TableDataRow<Integer>> roundingMethods) {
        ((TableDropdown)resultTableWidget.columns.get(8).getColumnWidget()).setModel(roundingMethods);        
    }
    
    private void setUnitsOfMeasureModel(TableDataModel<TableDataRow<Integer>> units) {
        ((TableDropdown)sampleTypeTableWidget.columns.get(1).getColumnWidget()).setModel(units);
        ((TableDropdown)resultTableWidget.columns.get(0).getColumnWidget()).setModel(units);
        sampleTypeUnitModel = (TableDataModel<TableDataRow<Integer>>)units.clone();
        resultUnitModel = (TableDataModel<TableDataRow<Integer>>)units.clone();
    }
    
    private void setSampleTypesModel(TableDataModel<TableDataRow<Integer>> sampleTypes) {
        ((TableDropdown)sampleTypeTableWidget.columns.get(0).getColumnWidget()).setModel(sampleTypes);        
    }
    
    private void setSectionsModel(TableDataModel<TableDataRow<Integer>> sections) {
        ((TableDropdown)sectionTableWidget.columns.get(0).getColumnWidget()).setModel(sections);        
    }
    
    private void setSectionFlagsModel(TableDataModel<TableDataRow<Integer>> sectionFlags) {        
        ((TableDropdown)sectionTableWidget.columns.get(1).getColumnWidget()).setModel(sectionFlags);
    }
    
    private void setWorksheetAnalyteFlagsModel(TableDataModel<TableDataRow<Integer>> wsAnalyteFlags) {
        ((TableDropdown)wsAnalyteTableWidget.columns.get(3).getColumnWidget()).setModel(wsAnalyteFlags);        
    }
    
    private void setWorksheetItemTypesModel(TableDataModel<TableDataRow<Integer>> wsItemTypes) {
        ((TableDropdown)wsItemTableWidget.columns.get(1).getColumnWidget()).setModel(wsItemTypes);        
    }


    /**
     * The function for loading the fields in the "prepAndReflex" sub-rpc
     * section as defined in the xsl file for the screen. It's mapped to the
     * "Prep Test & Reflexive Test" tab in the tab-panel on the screen
     */
    private void fillPrepTestsReflexTests() {       
        if (form.entityKey == null)
            return;

        form.prepAndReflex.entityKey = form.entityKey;

        window.setBusy();

        screenService.call("loadPrepTestsReflexTests",form.prepAndReflex,
                           new AsyncCallback<PrepAndReflexForm>() {
                               public void onSuccess(PrepAndReflexForm result) {
                                   form.prepAndReflex = result;
                                   load(result);
                                   window.clearStatus();
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });
    }

    /**
     * The function for loading the fields in "sampleType" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Sample Type"
     * tab in the tab-panel on the screen
     */
    private void fillSampleTypes() {       
        if (form.entityKey == null)
            return;
        
        form.sampleType.entityKey = form.entityKey;

        window.setBusy();        
        screenService.call("loadSampleTypes",form.sampleType,
                           new AsyncCallback<SampleTypeForm>() {
                               public void onSuccess(SampleTypeForm result) {
                                   form.sampleType = result;
                                   load(result);
                                   window.clearStatus();
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });
    }

    /**
     * The function for loading the fields in "worksheet" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "WorkSheet
     * Layout" tab in the tab-panel on the screen
     */
    private void fillWorksheetLayout() {
        if (form.entityKey == null)
            return;

        form.worksheet.entityKey = form.entityKey;

        window.setBusy();

        screenService.call("loadWorksheetLayout",form.worksheet,
                           new AsyncCallback<WorksheetForm>() {
                               public void onSuccess(WorksheetForm result) {
                                   form.worksheet = result;
                                   load(form.worksheet);
                                   window.clearStatus();
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });
    }

    /**
     * The function for loading the fields in "testAnalyte" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Analyte" tab
     * in the tab-panel on the screen
     */
    private void fillTestAnalyte() {        
        if (form.entityKey == null)
            return;
        
        form.testAnalyte.entityKey = form.entityKey;

        window.setBusy();

        screenService.call("loadTestAnalyte",form.testAnalyte,
                           new AsyncCallback<TestAnalyteForm>() {
                               public void onSuccess(TestAnalyteForm result) {
                                   form.testAnalyte = result;
                                   load(form.testAnalyte);                                     
                                   window.clearStatus();
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });

    }

    private void fillResultModelCollections() {
        TestGeneralPurposeRPC tirpc;

        if (form.entityKey == null)
            return;                                                  
        
        tirpc = new TestGeneralPurposeRPC();
        tirpc.key = form.entityKey;
        tirpc.defaultResultModel = (TableDataModel<TableDataRow<Integer>>)testResultDefaultModel.clone();
        tirpc.resultGroupModel = new TableDataModel<TableDataRow<Integer>>(); 
        tirpc.testAnalyteModel = new TableDataModel<TableDataRow<Integer>>();
        tirpc.resultDropdownModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
        tirpc.resultTableModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();

        screenService.call("getGroupsAndModels",tirpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                               public void onSuccess(TestGeneralPurposeRPC result) {
                                   int difference;
                                   group = result.integerValue;
                                   if (group > 0) {
                                       difference = group - resultPanel.getTabBar()
                                                                       .getTabCount();
                                       while (difference != 0) {
                                           if (difference > 0) {
                                               resultPanel.add(getDummyPanel(),
                                                               new Integer(resultPanel.getTabBar()
                                                                                      .getTabCount() + 1).toString());
                                               difference--;
                                           } else {
                                               resultPanel.remove(resultPanel.getTabBar()
                                                                             .getTabCount() - 1);
                                               difference++;
                                           }
                                       }
                                       
           
                                      resultTableModelCollection = result.resultTableModelCollection;
                                      resultDropdownModelCollection = result.resultDropdownModelCollection;         
                                      form.testAnalyte.resultTableModelCollection = result.resultTableModelCollection;
                                      form.testAnalyte.resultDropdownModelCollection = result.resultDropdownModelCollection; 
                                      
                                      setTableDropdownModel(reflexTestTableWidget,1,result.testAnalyteModel);
                                      testAnalyteModel = result.testAnalyteModel;                                      
                                                           
                                      setTableDropdownModel(analyteTreeWidget,4,"analyte",result.resultGroupModel);
                                      resultGroupModel = result.resultGroupModel;
                                      

                                      if (resultPanel.getTabBar().getTabCount() > 0) {
                                          resultPanel.selectTab(0);
                                      }
                                   } else {
                                       resultPanel.clear();
                                   }
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });

    }
 
    /**
     * The functions for handling the click events associated with the those
     * buttons on the screen which remove selected rows from particular tables
     */
    private void onSampleTypeRowButtonClick() {
        int row;
        row = sampleTypeTableWidget.modelIndexList[sampleTypeTableWidget.activeRow];        
        if(row > -1) {
            sampleTypeTableWidget.model.deleteRow(row);
            unitListChanged = true;
        } 
    }

    private void onPrepTestRowButtonClick() {
        int index = prepTestTableWidget.modelIndexList[prepTestTableWidget.activeRow];
        if (index > -1)
            prepTestTableWidget.model.deleteRow(index);
    }

    private void onWSItemRowButtonClick() {
        int index = wsItemTableWidget.modelIndexList[wsItemTableWidget.activeRow];
        if (index > -1)
            wsItemTableWidget.model.deleteRow(index);
    }

    private void onReflexTestRowButtonClick() {        
        int index = reflexTestTableWidget.modelIndexList[reflexTestTableWidget.activeRow];        
        if (index > -1)
            reflexTestTableWidget.model.deleteRow(index);
    }

    private void onTestSectionRowButtonClicked() {
        int index = sectionTableWidget.modelIndexList[sectionTableWidget.activeRow];
        if (index > -1) 
            sectionTableWidget.model.deleteRow(index);        
    }

    private void onTestResultRowButtonClicked() {
        TableDataModel<TableDataRow<Integer>> resModel,ddModel;
        int selIndex,selTab;
        TableDataRow<Integer> resSet,ddSet;
        
        resModel = resultTableWidget.model.getData();
        selIndex = resultTableWidget.modelIndexList[resultTableWidget.activeRow];
        selTab = resultPanel.getTabBar().getSelectedTab();
        
        if (selIndex > -1) {
            if (resModel.size() > 1) {
                resSet = resultTableWidget.model.getRow(selIndex);                
                if (resSet.key != null) {                    
                    setErrorToReflexFields(consts.get("resultDeleted"),resSet.key,2);  
                    ddModel = resultDropdownModelCollection.get(selTab);
                    ddSet = ModelUtil.getRowByKey(ddModel, resSet.key);
                    ddModel.delete(ddSet);
                    refreshReflexResultDropdowns(selTab+1);
                }
                resultTableWidget.model.deleteRow(selIndex);                
            } else {
                Window.alert(consts.get("atleastOneResInResGrp"));
            }
        }
    }

    private void onAddResultTabButtonClicked() {
        TableDataRow<Integer> ddset;        
        group++;

        if (group == 2)
            prevSelTabIndex = 0;

        resultPanel.add(getDummyPanel(), new Integer(group).toString());        
        resultTableModelCollection.add((TableDataModel<TableDataRow<Integer>>)testResultDefaultModel.clone());
        resultDropdownModelCollection.add(getDefaultResultDropdownModel());
        resultPanel.selectTab(group - 1);

        ddset = new TableDataRow<Integer>(group,new StringObject((new Integer(group)).toString()));

        resultGroupModel.add(ddset);

        setTableDropdownModel(analyteTreeWidget,4,"analyte",resultGroupModel);
    }

    private void onAddRowButtonClicked() {
        TreeDataItem newItem = analyteTreeWidget.model.getData()
                                                          .createTreeItem("analyte");
        analyteTreeWidget.model.addRow(newItem);
        analyteTreeWidget.model.refresh();
    }

    private void onGroupAnalytesButtonClicked() {
        TreeDataItem cloneItem = null;
        ArrayList<TreeDataItem> items = analyteTreeWidget.model.getSelections();
        if (items.size() < 2) {
            Window.alert(consts.get("atleastTwoAnalytes"));
            return;
        }
        ArrayList<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeWidget.model.getSelectedRowList()
                                                                                               .clone();
        Collections.sort(selectedRowIndexes);

        TreeDataItem groupItem = analyteTreeWidget.model.createTreeItem("top");
        groupItem.cells[0].setValue(consts.get("analyteGroup"));

        for (int iter = 0; iter < selectedRowIndexes.size(); iter++) {
            int index = selectedRowIndexes.get(iter);
            if (iter > 0 && index != (selectedRowIndexes.get(iter - 1) + 1)) {
                Window.alert(consts.get("analyteNotAdjcnt"));
                return;
            }
        }
        for (TreeDataItem item : items) {
            if ("top".equals(item.leafType)) {
                Window.alert(consts.get("cantGroupGroups"));
                return;
            }
            if (item.parent != null) {
                Window.alert(consts.get("analyteAlreadyGrouped"));
                return;
            }
            cloneItem = (TreeDataItem)item.clone();
            cloneItem.setData(item.getData());
            item.setData(null);
            groupItem.addItem(cloneItem);
        }

        analyteTreeWidget.model.deleteRows(analyteTreeWidget.model.getSelectedRowList());

        if (analyteTreeWidget.model.getData().size() > 0) {
            if (selectedRowIndexes.get(0) < analyteTreeWidget.model.getData()
                                                                       .size()) {
                analyteTreeWidget.model.addRow(selectedRowIndexes.get(0),
                                                   groupItem);
            } else {
                analyteTreeWidget.model.addRow(groupItem);
            }
        } else {
            analyteTreeWidget.model.addRow(groupItem);
        }

        analyteTreeWidget.model.refresh();
    }

    private void onUngroupAnalytesButtonClicked() {
        TreeDataItem item;
        TreeDataItem newItem;

        List<TreeDataItem> chItems;
        int index;
        ArrayList<Integer> selRows = (ArrayList<Integer>)analyteTreeWidget.model.getSelectedRowList()
                                                                                    .clone();

        if (selRows.size() > 0) {
            for (int iter = 0; iter < selRows.size(); iter++) {
                index = selRows.get(iter);
                item = analyteTreeWidget.model.getRow(index);
                if ("top".equals(item.leafType)) {
                    chItems = item.getItems();

                    for (int i = 0; i < chItems.size(); i++) {
                        newItem = (TreeDataItem)chItems.get(i).clone();
                        newItem.parent = null;
                        newItem.setData(chItems.get(i).getData());
                        chItems.get(i).setData(null);
                        analyteTreeWidget.model.addRow(index + i, newItem);
                    }

                    analyteTreeWidget.model.deleteRow(index);
                }
            }
            analyteTreeWidget.model.refresh();
        }
    }
    
   /**
    * The method for the code to be executed when one or more nodes from the 
    * analyte tree are removed  
    */
    private void onDeleteTreeItemButtonClicked() {
        List<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeWidget.model.getSelectedRowList()
                                                                                          .clone();
        int iter, index;
        TreeDataItem item, parent;
        TreeDataModel model;        
        Integer anaId;
        TableDataRow<Integer> ddset;              

        for (iter = 0; iter < selectedRowIndexes.size(); iter++) {
            index = selectedRowIndexes.get(iter);
            item = (TreeDataItem)analyteTreeWidget.model.getRow(index)
                                                            .clone();
            if (item.parent != null) {
                parent = item.parent;
                if (parent.getData() == null) {
                    model = new TreeDataModel();
                    parent.setData(model);
                }
                ((TreeDataModel)parent.getData()).add(item);
                analyteTreeWidget.model.deleteRow(index);
            } else {
                analyteTreeWidget.model.deleteRow(index);
            }

            if (item.key != null) {                               
                setErrorToReflexFields(consts.get("analyteDeleted"),item.key,1);
                
                ddset = ((TableDataRow<Integer>)ModelUtil.getRowByKey(testAnalyteModel,item.key));
                if(ddset != null) 
                    testAnalyteModel.delete(ddset);                                   

                anaId = (Integer)((DropDownField)item.cells[0]).getSelectedKey();
                deleteWorksheetAnalytes(anaId);
            }
            
            ((TableDropdown)reflexTestTableWidget.columns.get(1)
                            .getColumnWidget()).setModel(testAnalyteModel);
        }
        analyteTreeWidget.model.refresh();
    }

    private void onDuplicateRecordClick() {
        form.numGroups = group;
        form.resultTableModel = testResultDefaultModel;
        screenService.call("getDuplicateRPC", form, fetchForDuplicateCallBack);
    }

    /**
     * This function opens a dialog window which allows the users to select one
     * or more dictionary entries to be added to the Test Results table
     */
    private void onDictionaryLookUpButtonClicked() {
        int left = getAbsoluteLeft();
        int top = getAbsoluteTop();
        DictionaryEntryPickerScreen pickerScreen = new DictionaryEntryPickerScreen();
        PopupPanel dictEntryPickerPopupPanel = new PopupPanel(false, true);
        pickerWindow = new ScreenWindow(dictEntryPickerPopupPanel,
                                        consts.get("chooseDictEntry"),
                                        "dictionaryEntryPicker","Loading...");
        pickerScreen.selectedRows = selectedRows;
        pickerWindow.setContent(pickerScreen);
        dictEntryPickerPopupPanel.addPopupListener(this);
        dictEntryPickerPopupPanel.add(pickerWindow);
        dictEntryPickerPopupPanel.setPopupPosition(left, top);
        dictEntryPickerPopupPanel.show();
    }

    private void enableTableAutoAdd(boolean enable) {
        reflexTestTableWidget.model.enableAutoAdd(enable);
        wsItemTableWidget.model.enableAutoAdd(enable);
        prepTestTableWidget.model.enableAutoAdd(enable);
        sampleTypeTableWidget.model.enableAutoAdd(enable);
        sectionTableWidget.model.enableAutoAdd(enable);
        resultTableWidget.model.enableAutoAdd(enable);
    }

    private VerticalPanel getDummyPanel() {
        Document document = XMLParser.parse(panelString);
        ScreenVertical vertical = new ScreenVertical(document.getDocumentElement(),
                                                     this);
        VerticalPanel dummy = (VerticalPanel)vertical.getWidget();
        return dummy;
    }

    private boolean checkResGrpSel() {
        TreeDataItem item = null;
        TreeDataItem chItem = null;
        TreeModel model = analyteTreeWidget.model;
        Integer selVal = null;

        for (int i = 0; i < model.getData().size(); i++) {
            item = model.getData().get(i);
            if ("top".equals(item.leafType)) {
                for (int j = 0; j < item.getItems().size(); j++) {
                    chItem = item.getItems().get(j);
                    selVal = (Integer)((DropDownField)chItem.cells[4]).getSelectedKey();
                    if (selVal == null) {
                        return false;
                    }
                }
            } else {
                selVal = (Integer)((DropDownField)item.cells[4]).getSelectedKey();
                if (selVal == null) {
                    return false;

                }
            }
        }

        return true;
    }

    private boolean treeItemsHaveErrors() {
        TreeDataItem item = null;
        TreeModel model = analyteTreeWidget.model;
        int itemsInError = 0;

        for (int i = 0; i < model.getData().size(); i++) {
            item = model.getData().get(i);
            if ("top".equals(item.leafType)) {
                for (int j = 0; j < item.getItems().size(); j++) {
                    if (setErrorToItem(item.getItems().get(j)))
                        itemsInError++;
                }
            } else {
                if (setErrorToItem(item))
                    itemsInError++;
            }
        }

        if (itemsInError > 0) {
            model.refresh();
            return true;
        }

        return false;
    }

    private boolean resultRowsHaveErrors() {
        TableDataModel<TableDataRow<Integer>> model = reflexTestTableWidget.model.getData();
        TableDataRow<Integer> row = null;
        AbstractField field = null;
        if (model != null) {
            for (int j = 0; j < model.size(); j++) {
                row = model.get(j);
                for (int k = 0; k < row.size(); k++) {
                    field = (AbstractField)row.cells[k];
                    if (field.getErrors().size() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean worksheetItemRowsHaveErrors() {
        TableDataModel<TableDataRow<Integer>> model = wsItemTableWidget.model.getData();
        TableDataRow<Integer> row = null;
        AbstractField field = null;
        if (model != null) {
            for (int j = 0; j < model.size(); j++) {
                row = model.get(j);
                for (int k = 0; k < row.size(); k++) {
                    field = (AbstractField)row.cells[k];
                    if (field.getErrors().size() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getNextTempAnaId() {
        return --tempAnaId;
    }

    private int getNextTempResId() {
        return --tempResId;
    }

    private TableDataModel<TableDataRow<Integer>> getSingleRowModel() {
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
        TableDataRow<Integer> blankset = new TableDataRow<Integer>(null,new StringObject(""));
        model.add(blankset);
        return model;
    } 

    /**
     *  This method sets fields in the TableDataModel of reflexTestTableWidget 
     *  in error specified by the parameter "error". Since all fields in the table 
     *  are DropDownFields, in order to find the row that will show the error, the 
     *  id of the selected option is needed and it is specified through the 
     *  parameter "id". The parameter col specifies the column in which the field 
     *  is. In addition to adding the error the method also sets the value of the
     *  field to null in order to select the blank option
     */
    private void setErrorToReflexFields(String error, Integer id, int col) {
        TableDataRow<Integer> rset;
        DropDownField<Integer> field,nextField;                
        for (int i = 0; i < reflexTestTableWidget.model.getData().size(); i++) {
            rset = reflexTestTableWidget.model.getRow(i);
            field = (DropDownField<Integer>)rset.cells[col];
            if (id.equals(field.getSelectedKey())) {
                if (!field.getErrors().contains(error)) {
                    field.setValue(null);
                    reflexTestTableWidget.model.setCellError(i, col, error);
                    
                    if(col == 1) {
                        //
                        // if the field in column 1 i.e. the field for test analyte
                        // has been set to null then to avoid committing a test result
                        // value (column 2) to the database that now belongs to an 
                        // invalid test analyte (null), the value of the field in column 2
                        // is set to null too. This avoids the situation where the user 
                        // selects an analyte in column 1 and the result in column 2 doesn't 
                        // belong to the result group associated with the analyte
                        // and commits the data without changing the result
                        //
                        nextField = (DropDownField<Integer>)rset.cells[col+1];
                        nextField.setValue(null);
                        reflexTestTableWidget.model.setCellError(i, col+1,consts.get("selectAnaBeforeRes"));
                    }
                }
            }
        }
    }
    
    /** 
     * This method finds all those rows in reflexWidget which have the dropdown 
     * for test results i.e. column 3, set to the values in the result group specified
     * by the argument "resultGroup" and then sets them with the corresponding
     * model from resultDropdownModelCollection. The value of resultGroup should
     * be greater than zero as it represents an actual result group as seen 
     * on the screen     
     */
    private void refreshReflexResultDropdowns(int resultGroup)  {
        TableDataRow<Integer> rset;
        DropDownField<Integer> anaField, resField;
        TableDataRow<Integer> taRow,ddaRow;
        TableDataModel<TableDataRow<Integer>> model;
        int rg;
        
        for (int i = 0; i < reflexTestTableWidget.model.getData().size(); i++) {
            rset = reflexTestTableWidget.model.getRow(i);
            anaField = (DropDownField<Integer>)rset.cells[1];
            resField = (DropDownField<Integer>)rset.cells[2];
            if(anaField.getValue() != null && anaField.getValue().size() > 0) {
                //
                // get the selected TableDataRow from the analyte dropdown
                //
                taRow = anaField.getValue().get(0);                
                if(taRow != null && taRow.key != null) {
                    //
                    // get the TableDataRow with the same key as taRow from 
                    // testAnalyteDropdownModel; taRow doesn't have its data
                    // set to anything because it belongs to the model that was 
                    // created on the servlet and at that point the result group 
                    // information is not avaliable because this model is 
                    // populated from the data from the table test_reflex
                    //
                    ddaRow = ModelUtil.getRowByKey(testAnalyteModel,taRow.key);                    
                    if(ddaRow!=null && ddaRow.getData()!=null){
                        //
                        // if the result group matches, get the corresponding 
                        // model from resultDropdownModelCollection and set it 
                        // in the test result dropdown field for this row  
                        //
                        rg = ((IntegerObject)ddaRow.getData()).getValue();
                        if(rg == resultGroup && resultGroup >= 1) {
                            model = resultDropdownModelCollection.get(rg-1);
                            resField.setModel(model);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is responsible for whenever a table row in resultTableWidget
     * gets edited, the change is reflected if necessary in the table for reflexive tests  
     */    
    private void checkAndAddNewResultValue(String value,TableDataRow<Integer> set) {
        int selTab;      
        TableDataModel<TableDataRow<Integer>> ddModel;
        TableDataRow<Integer> addset;
        String prevVal;

        selTab = resultPanel.getTabBar().getSelectedTab();
        ddModel = resultDropdownModelCollection.get(selTab); 
        addset = ModelUtil.getRowByKey(ddModel, set.key);
        
        if (addset != null) {
            prevVal = (String)addset.cells[0].getValue();
            if (!prevVal.trim().equals(value.trim())) {
                setErrorToReflexFields(consts.get("resultValueChanged"),
                                       set.key,2);
                addset.cells[0].setValue(value);
            }
        } else {
            addset = new TableDataRow<Integer>(set.key, new StringObject(value));
            ddModel.add(addset);
        }        
        
        refreshReflexResultDropdowns(selTab+1);
    }

    private boolean setErrorToItem(TreeDataItem item) {
        boolean someError = false;
        DropDownField field = (DropDownField)item.cells[0];
        if (fieldBlank(field) && !field.getErrors()
                                       .contains(consts.get("fieldRequiredException"))) {
            field.addError(consts.get("fieldRequiredException"));
            field.valid = false;
            someError = true;                        
        }

        field = (DropDownField)item.cells[1];
        if (fieldBlank(field) && !field.getErrors()
                                       .contains(consts.get("fieldRequiredException"))) {
            field.addError(consts.get("fieldRequiredException"));
            field.valid = false;
            someError = true;
        }

        return someError;
    }

    private boolean fieldBlank(DropDownField field) {
        if (field.getSelectedKey() == null) {
            return true;
        }
        return false;
    }

    private void addSetToUnitDropdown(DropDownField ufield) {
        boolean addSet = false;
        TableDataRow ddset = null;
        StringObject str = null;
        String text = (String)ufield.getTextValue();
        Integer id = (Integer)(ufield.getSelectedKey());

        if (resultUnitModel != null) {
            if (ModelUtil.getRowByKey(resultUnitModel, id) == null) {
                addSet = true;
            }
        } else {
            addSet = true;
            resultUnitModel = new TableDataModel<TableDataRow<Integer>>();
        }
        if (addSet) {
            str = new StringObject(text);
            ddset = new TableDataRow<Integer>(id, str);
            resultUnitModel.add(ddset);
            setTableDropdownModel(resultTableWidget, 0, resultUnitModel);
        }
    }
    

    private void flipSignsInAnalyteDropDown() {
        TableDataRow set = null;
        Integer id = null;
        if (testAnalyteModel != null && testAnalyteModel.size() > 1) {
            for (int i = 1; i < testAnalyteModel.size(); i++) {
                set = (TableDataRow)testAnalyteModel.get(i);
                id = (Integer)set.key;
                set.key = id * -2;
                if (id < tempAnaId)
                    tempAnaId = id;
            }
        }
    }

    private boolean dataInWorksheetForm() {
        WorksheetForm wsForm = form.worksheet;
        Integer key = (Integer)wsForm.formatId.getSelectedKey();
        if (wsForm.worksheetTable.getValue().size() == 0 && (key == null)
            && (wsForm.batchCapacity == null)
            && (wsForm.totalCapacity == null))
            return false;
        else
            return true;
    }

    private void deleteWorksheetAnalytes(Integer analyteId) {
        TableDataModel model = wsAnalyteTableWidget.model.getData();
        TableDataRow set = null;
        IntegerField data = null;        
        for (int iter = 0; iter < model.size(); iter++) {
            set = (TableDataRow<Integer>)model.get(iter);
            data = (IntegerField)set.getData();            
            if (data != null && analyteId.equals(data.getValue()))
                model.delete(iter);
        }
        wsAnalyteTableWidget.model.refresh();
    }

    private void addWorksheetAnalyte(Integer analyteId, String analyteName) {
        if (!analytePresent(analyteId)) {
            TableDataModel<TableDataRow<Integer>> model = wsAnalyteTableWidget.model.getData();
            TableDataRow<Integer> set = model.createNewSet();
            set.setData(new IntegerField(analyteId));
            set.cells[0].setValue(analyteName);
            model.add(set);
            wsAnalyteTableWidget.model.refresh();
        }
    }

    private boolean analytePresent(Integer analyteId) {
        TableDataRow row = null;
        IntegerField data = null;
        for (int i = 0; i < wsAnalyteTableWidget.model.getData().size(); i++) {
            row = wsAnalyteTableWidget.model.getData().get(i);            
            data = (IntegerField)row.getData();
            if (analyteId.equals(data.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void updateWorksheetAnalyte(Integer oldId,
                                        Integer newId,
                                        String analyteName) {
        deleteWorksheetAnalytes(oldId);
        addWorksheetAnalyte(newId, analyteName);
    }

    private void setAnalytesAvailable(TableDataRow<Integer> trow) {
        StringField strf = (StringField)trow.cells[0];
        String anaName = strf.getValue().trim();
        String selText = null;
        TreeDataItem item = null;
        DropDownField anaField = null;
        CheckField chf = (CheckField)trow.cells[1];

        for (int i = 0; i < analyteTreeWidget.model.getData().size(); i++) {
            item = analyteTreeWidget.model.getData().get(i);
            if ("top".equals(item.leafType)) {
                for (TreeDataItem citem : item.getItems()) {
                    anaField = (DropDownField)citem.cells[0];
                    selText = ((String)anaField.getTextValue()).trim();
                    if (anaName.equals(selText)) {
                        setAnalyteAvailable(item.getItems(), chf.isChecked());
                        break;
                    }
                }
            }
        }
    }

    private void setAnalyteAvailable(List<TreeDataItem> items, boolean available) {
        String tanaName = null;
        String ianaName = null;
        TreeDataItem item = null;
        TableDataRow<Integer> row = null;

        for (int i = 0; i < items.size(); i++) {
            item = items.get(i);
            ianaName = ((String)((DropDownField)item.cells[0]).getTextValue()).trim();
            for (int j = 0; j < wsAnalyteTableWidget.model.getData().size(); j++) {
                row = wsAnalyteTableWidget.model.getRow(j);
                tanaName = ((String)((StringField)row.cells[0]).getValue()).trim();

                if (tanaName.equals(ianaName)) {
                    if (available) {
                        ((CheckField)row.cells[1]).setValue("Y");
                        window.setStatus(consts.get("additionalAnalytesAdded"),"");
                    } else {
                        ((CheckField)row.cells[1]).setValue(null);
                        window.setStatus(consts.get("otherAnalytesRemoved"), "");
                    }
                    setRepeatValue(row);
                }
            }
        }
    }

    private void setRepeatValue(TableDataRow<Integer> row) {
        CheckField chf = (CheckField)row.cells[1];
        IntegerField rfield = (IntegerField)row.cells[2];

        if (chf.isChecked() && (rfield.getValue() == null || rfield.getValue() == 0))
            rfield.setValue(new Integer(1));
    }
    
    private String validateDate(String value) throws IllegalArgumentException {
        Date date = null;                                               
        DateField df = null;                                                 
      try{  
        date = new Date(value.replaceAll("-", "/"));                                              
        df = new DateField((byte)0, (byte)2,date);                                                       
      }catch(IllegalArgumentException ex) {
           throw ex;                                                 
       } 
      
      if(df!=null)
          return df.format();
     
      return null;
    }
    
    private String validateDateTime(String value) throws IllegalArgumentException {
        Date date = null;                                               
        DateField df = null;
        String[] split = null;
        String hhmm = null;
      try{                  
        split = value.split(" ");  
        if(split.length != 2)
          throw new IllegalArgumentException();
        
        hhmm = split[1];
        if(hhmm.split(":").length != 2) 
            throw new IllegalArgumentException();  
        
        date = new Date(value.replaceAll("-", "/"));                                              
        df = new DateField((byte)0, (byte)4,date);                                               
       
      }catch(IllegalArgumentException ex) {
           throw ex;           
       } 
      
      if(df!=null) 
       return df.format();
      
      return null;
    }
    
    private String validateTime(String value) throws IllegalArgumentException {
        Date date = null;                                               
        DateField df = null;
        String[] split = null;
        String defDate = "2000-01-01 ";
        String nextDayDate = "2000-01-02 ";
        String dateStr = defDate + value;
        boolean nextDay = false;
        
        try{                  
           split = value.split(":");  
           if(split.length != 2)
             throw new IllegalArgumentException();               
           
           date = new Date(dateStr.replaceAll("-", "/"));
           
           if(Integer.parseInt(split[0]) > 23) 
               nextDay = true;
               
           df = new DateField((byte)0, (byte)4,date);                                                      
         }catch(IllegalArgumentException ex) {          
             throw ex;                   
          } 
         
         if(df!=null) { 
          if(nextDay)    
           return df.format().replace(nextDayDate,"");   
          else   
           return df.format().replace(defDate,"");
         } 
         
         return null;
       }
    
    
    private void resetResultGroupDropDownModel() {
        resultGroupModel.clear();
        resultGroupModel.add(new TableDataRow<Integer>(null,new StringObject("")));
        setTableDropdownModel(analyteTreeWidget,4,"analyte",
                              resultGroupModel);                    
    }
    
    private void resetUnitDropDownModel() {
      if(resultUnitModel != null)   
        resultUnitModel.clear();
      else
        resultUnitModel = new TableDataModel<TableDataRow<Integer>>();
      
      resultUnitModel.add(new TableDataRow<Integer>(null,new StringObject("")));
      setTableDropdownModel(resultTableWidget,0, resultUnitModel);    
    }
    
    private void resetTestAnalyteDropDownModel() {
        if(testAnalyteModel != null)   
            testAnalyteModel.clear();
        else
            testAnalyteModel = new TableDataModel<TableDataRow<Integer>>();
        
        testAnalyteModel.add(new TableDataRow<Integer>(null,new StringObject("")));
        setTableDropdownModel(reflexTestTableWidget,1, testAnalyteModel); 
        
      }
      
    private void resetTestResultDropDownModel() {
        TableDropdown resdd  = (TableDropdown)reflexTestTableWidget.columns.get(2).getColumnWidget();
        TableDataModel model = new TableDataModel<TableDataRow<Integer>>();
        model.add(new TableDataRow<Integer>(null,new StringObject("")));
        setTableDropdownModel(reflexTestTableWidget,2, model); 
    }
    
    private TableDataModel<TableDataRow<Integer>> getDefaultResultDropdownModel(){
        TableDataModel<TableDataRow<Integer>> model;
        TableDataRow<Integer> row;
        
        row = new TableDataRow<Integer>(null, new StringObject(""));
        model = new TableDataModel<TableDataRow<Integer>>();
        model.add(row);
        
        return model;
        
    }
    
    /** 
     * This method checks to see if all the cells in a given row of reflexWidget
     * i.e. the argument "row" have either the blank option or no option selected
     * for them, if this is the case then it returns true otherwise false  
     */
    private boolean isReflexRowEmpty (TableDataRow<Integer> row) {
        int numCellsEmpty, i;
        DropDownField<Integer> ddField;
        
        numCellsEmpty = 0;
        
        for(i = 0; i < row.cells.length; i++) {
         ddField = (DropDownField<Integer>)row.cells[i];
         if(ddField.getSelectedKey() == null) 
            numCellsEmpty++;
        }
        
        if(numCellsEmpty == i) 
            return true;
        
        return false;
    }
    
    /**
     * This method reloads testAnalyteDropdownModel with the most current data in
     * analyteTreeWidget's TreeDataModel 
     */
    private void reloadTestAnalyteDropdownModel() {
        TreeDataItem item,citem;
        List<TreeDataItem> list;
        TreeDataModel model;
        DropDownField<Integer> anaField,rgField;
        TableDataRow row;
        IntegerObject data;
        String selText;
        Integer selKey;
        int i,j;       
        
        if(testAnalyteModel != null && analyteListChanged) {
            analyteTreeWidget.finishEditing();
            testAnalyteModel.clear();        
            testAnalyteModel.add(new TableDataRow<Integer>(null,new StringObject("")));
        
            model = analyteTreeWidget.model.getData();
            for (i = 0; i < model.size(); i++) {
                item = model.get(i);
                if ("top".equals(item.leafType)) {
                    list = item.getItems();
                    for (j = 0; j < list.size(); j++) {
                        citem = list.get(j);
                        row = getDropdownRowFromTreeItem(citem);
                        if(row !=null)                       
                            testAnalyteModel.add(row);                    
                    }
                } else {
                    row = getDropdownRowFromTreeItem(item);
                    if(row !=null)                       
                        testAnalyteModel.add(row);                   
                }
            }
        
            setTableDropdownModel(reflexTestTableWidget,1, testAnalyteModel);
        }
    }

    /**
     * This method returns a TableDataRow that can be added to testAnalyteDropdownModel
     * and which is created from the argument "item"
     */
    private TableDataRow<Integer> getDropdownRowFromTreeItem(TreeDataItem item){
        DropDownField<Integer> anaField, rgField;
        TableDataRow row;
        IntegerObject data;
        String selText;
        Integer selKey;
        int i, j;
        
        row = null;

        anaField = (DropDownField<Integer>)item.cells[0];
        rgField = (DropDownField<Integer>)item.cells[4];
        selText = (String)anaField.getTextValue();
        //
        // this check is to prevent "row" from having a null key and/or a blank
        // display label, which could happen if there's an item in the tree with
        // no text in the "Analyte" column, but the key for which is not null because
        // it previously had some text in that column
        //
        if (item.key != null && selText != null && !selText.trim().equals("")) {
            row = new TableDataRow<Integer>(item.key, new StringObject(selText));
            selKey = (Integer)rgField.getSelectedKey();
            if (selKey != null)
                row.setData(new IntegerObject(selKey));
        }
        
        return row;
    } 
    
    /**
     * This method reloads resultUnitModel with the most current data in
     * sampleTypeTableWidget's TableDataModel 
     */
    private void reloadTestResultUnitDropdownModel() {
        TableDataRow<Integer> smplRow, ddRow;
        TableDataModel<TableDataRow<Integer>> tmodel;
        DropDownField<Integer> ddField;
        Integer key;
        String selText;

        int i;

        if (!unitListChanged)
            return;

        sampleTypeTableWidget.finishEditing();
        tmodel = sampleTypeTableWidget.model.getData();
        resultUnitModel.clear();
        resultUnitModel.add(new TableDataRow<Integer>(null,
                                                      new StringObject("")));
        for (i = 0; i < tmodel.size(); i++) {
            smplRow = tmodel.get(i);
            ddField = (DropDownField<Integer>)smplRow.cells[1];
            key = (Integer)ddField.getSelectedKey();
            if (key != null) {
                ddRow = ModelUtil.getRowByKey(sampleTypeUnitModel, key);
                resultUnitModel.add(ddRow);
            }
        }

        setTableDropdownModel(resultTableWidget, 0, resultUnitModel);
        unitListChanged = false;
    }
}