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
import org.openelis.gwt.common.data.TreeField;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenMenuPanel;
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
import org.openelis.modules.auxiliary.client.AuxiliaryForm;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestScreen extends OpenELISScreenForm<TestForm, Query<TableDataRow<Integer>>> implements
                                                                                          ClickListener,
                                                                                          TabListener,
                                                                                          ChangeListener,
                                                                                          TableManager,
                                                                                          TableWidgetListener,
                                                                                          TreeManager,
                                                                                          TreeWidgetListener,
                                                                                          PopupListener {

    private TableDataModel<TableDataRow<Integer>> testAnalyteDropdownModel,
                    unitDropdownModel, resultGroupDropdownModel, defaultModel;

    private AppButton removeSampleTypeButton, removePrepTestButton,
                    addRowButton, deleteButton, groupAnalytesButton,
                    ungroupAnalytesButton, removeReflexTestButton,
                    removeWSItemButton, removeTestSectionButton,
                    dictionaryLookUpButton, removeTestResultButton,
                    addResultTabButton;

    private TreeWidget analyteTreeController = null;

    private int group = 0;

    private TableWidget reflexTestWidget,                                   // the widget for the table for reflexive tests
                        prepTestWidget,                                     // the widget for the table for prep tests  
                        wsItemWidget,                                       // the widget for the table for worksheet items
                        sampleTypeWidget,                                   // the widget for the table for test sections 
                        sectionWidget,                                      // the widget for the table for test results  
                        resultWidget,                                       // the widget for the table for worksheet analytes
                        wsAnalyteWidget;

          
    private HashMap<Integer, TableDataModel<TableDataRow<Integer>>> resultDropdownModelMap; // the HashMap that contains mappings 
                                                                                            // between a test analyte id's and 
                                                                                            // the test result dropdown model
                                                                                            // that's associated with each id.
    
    private HashMap<Integer, List<Integer>> resGroupAnalyteIdMap;           // the HashMap that contains mappings between a 
                                                                            // given result group number such as 1, 2 etc. and 
                                                                            // the list of the test analyte id's that have this
                                                                            // result group assigned to them 
    
    private HashMap<Integer,Integer> unitIdNumResMap;                       // the HashMap that contains mappings between the id's 
                                                                            // for units of measure and the number of test results
                                                                            // that have this unit assigned to them currently  


    private TestMetaMap TestMeta = new TestMetaMap();

    private KeyListManager keyList = new KeyListManager();

    private ScreenTextBox testId;
    private TextBox testName;

    private TabPanel resultPanel,testTabPanel;
    
    // private ScrollableTabBar resultPanel;

    private MenuItem dupItem;    

    private ArrayList<TableDataModel<TableDataRow<Integer>>> resultModelCollection;

    private int prevSelTabIndex = -1, tempAnaId = -1, tempResId = -1;

    private static String panelString = "<VerticalPanel/>";

    private ArrayList<TableDataRow<Integer>> selectedRows = null;

    private ScreenWindow pickerWindow = null;
    
    private Dropdown revisionMethod, sortingMethod, reportingMethod, trailer,
                     testFormat, testScriptlet, label, wsFormat,wsScriptlet;  
    
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

        //
        // this is done to remove an unwanted tab that gets added to
        // testTabPanel, for some reason, when you put a tab panel inside one
        // of its tabs
        //
        testTabPanel.remove(3);

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
        analyteTreeController = (TreeWidget)analyteTree.getWidget();
        
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
        
        loadDropdownModel(TestMeta.getLabelId());
        loadDropdownModel(TestMeta.getTestTrailerId());
        loadDropdownModel(TestMeta.getScriptletId());
        //loadDropdownModel(TestMeta.getRevisionMethodId());
        //loadDropdownModel(TestMeta.getTestFormatId());
        //loadDropdownModel(TestMeta.getSortingMethodId());
        //loadDropdownModel(TestMeta.getReportingMethodId());
        loadDropdownModel(TestMeta.getTestWorksheet().getScriptletId());
        //loadDropdownModel(TestMeta.getTestWorksheet().getFormatId());

        //
        // set the model for each column. Note that we have to do it twice:
        // once for the normal table and once for query table.
        // see setTableDropdownModel(TableWidget widget,int column,DataModel
        // model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeWidget = (TableWidget)s.getWidget();
        //loadTableDropdownModel(sampleTypeWidget,0,TestMeta.getTestTypeOfSample().getTypeOfSampleId());
        //loadTableDropdownModel(sampleTypeWidget,1,TestMeta.getTestTypeOfSample().getUnitOfMeasureId());
        sampleTypeWidget.addTableWidgetListener(this);
        setUnitsOfMeasureModel(form.units);
        setSampleTypesModel(form.sampleTypes);

        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestWidget = (TableWidget)s.getWidget();
        loadTableDropdownModel(prepTestWidget, 0, TestMeta.getTestPrep()
                                                          .getPrepTestId());

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestWidget = (TableWidget)s.getWidget();
        reflexTestWidget.addTableWidgetListener(this);
        loadTableDropdownModel(reflexTestWidget, 0, TestMeta.getTestPrep()
                                                            .getPrepTestId());
        setReflexFlagsModel(form.reflexFlags);

        ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemWidget = (TableWidget)s.getWidget();       

        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionWidget = (TableWidget)s.getWidget();
        sectionWidget.addTableWidgetListener(this);
        loadTableDropdownModel(sectionWidget, 0, TestMeta.getTestSection()
                                                         .getSectionId());
        setSectionFlagsModel(form.sectionFlags);

        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultWidget = (TableWidget)s.getWidget();
        resultWidget.addTableWidgetListener(this);
        
        setResultFlagsModel(form.resultFlags);  
        setResultTypesModel(form.resultTypes);
        setRoundingMethodsModel(form.roundingMethods);

        s = (ScreenTableWidget)widgets.get("worksheetAnalyteTable");
        wsAnalyteWidget = (TableWidget)s.getWidget();        

        wsAnalyteWidget.addTableWidgetListener(this);
        
        setWorksheetAnalyteFlagsModel(form.wsAnalyteFlags);
        setWorksheetItemTypesModel(form.wsItemTypes);

        //
        // set dropdown models for column 1 and 3
        //                       
        setAnalyteTypesModel(form.analyteTypes);        

        resultGroupDropdownModel = new TableDataModel<TableDataRow<Integer>>();
        
        resultGroupDropdownModel.setDefaultSet(new TableDataRow<Integer>(null,new StringObject("")));
        
        setTableDropdownModel(analyteTreeController,4,"analyte",resultGroupDropdownModel);

        analyteTreeController.model.manager = this;
        analyteTreeController.enabled(false);
        analyteTreeController.addTreeWidgetListener(this);

        // override the callbacks
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);

        super.afterDraw(success);

        analyteTree.enable(true);

        //
        // Below, each TreeField or TableField from the rpc
        // is assigned the data model of the TreeWidget or TableWidget that it
        // represents.
        // This is done in order to make sure that the defaultSet
        // variable in the default data model of the field is initialized and
        // is not null.
        // So that when the first time data is fetched for that field and on the
        // server side a new DataSet is created from the model in the field to
        // represent a new row in the table on the screen, a
        // NullPointerException is not thrown.
        // As, creating a new dataset from the model involves the use of the
        // defaultSet for that model.
        //
        form.sampleType.sampleTypeTable.setValue(sampleTypeWidget.model.getData());

        form.worksheet.worksheetTable.setValue(wsItemWidget.model.getData());

        form.worksheet.worksheetAnalyteTable.setValue(wsAnalyteWidget.model.getData());

        form.prepAndReflex.testPrepTable.setValue(prepTestWidget.model.getData());

        form.prepAndReflex.testReflexTable.setValue(reflexTestWidget.model.getData());

        form.sectionTable.setValue(sectionWidget.model.getData());

        resultModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();

        form.testAnalyte.analyteTree.setValue(analyteTreeController.model.getData());
        //
        // the submit method puts the TreeDataModel from the TreeWidget in the
        // tree field from the rpc
        //        
        analyteTree.submit(form.testAnalyte.analyteTree);
        

        form.testAnalyte.testResultsTable.setValue(resultWidget.model.getData());

        defaultModel = (TableDataModel<TableDataRow<Integer>>)resultWidget.model.getData()
                                                                                .clone();
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
            //
            // See fillTestAnalyteDropDown(), fillTestResultDropDown(),
            // fillMaps(), fillResultModelCollection()
            // 
            ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());
            fillTestAnalyteDropDown();
            fillMapsAndUnitDropDownModel();
            fillResultGroupDropdown();
            fillResultModelCollection();                     
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
            if (sectionWidget.model.getData().size() == 0) {
                    Window.alert(consts.get("atleastOneSection"));
                    return;
                }
            if (form.testAnalyte.load || state == State.ADD) {
                if (analyteTreeController.model.getData().size() > 0 && (resultModelCollection.size() == 0)) {
                    boolean ok = Window.confirm(consts.get("analyteNoResults"));
                    if (!ok)
                        return;

                } else if (resultModelCollection.size() > 0 && analyteTreeController.model.getData()
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
        // resultPanel.clearTabs();
        //resultPanel.addTabListener(this);
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
        // resultPanel.clearTabs();
        resultModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
        prevSelTabIndex = -1;
        resultDropdownModelMap = new HashMap<Integer, TableDataModel<TableDataRow<Integer>>>();
        resGroupAnalyteIdMap = new HashMap<Integer, List<Integer>>();
        unitIdNumResMap = new HashMap<Integer, Integer>();

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
        // resultPanel.clearTabs();
        fillTestAnalyteDropDown();
        fillResultGroupDropdown();
        fillResultModelCollection();

        prevSelTabIndex = -1;

        super.abort();
    }

    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            int selTab = testTabPanel.getTabBar().getSelectedTab();
            //
            // disable anything that is not editable and set focus to the widget
            // which should get the first focus for editing
            //
            enableTableAutoAdd(true);
            testId.enable(false);
            testName.setFocus(true);
            dupItem.enable(false);

            fillTestAnalyteDropDown();
            fillMapsAndUnitDropDownModel();
            fillResultGroupDropdown();
            fillResultModelCollection();               
            
            if(selTab == 1 || selTab == 2) 
               testTabPanel.selectTab(selTab);            
            
            fillPrepTestsReflexTests();
            fillWorksheetLayout();

            prevSelTabIndex = -1;
        }
    };

    protected AsyncCallback<TestForm> commitUpdateCallback = new AsyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {
            if (form.status == Form.Status.invalid) {               
                if (form.testAnalyte.resultModelCollection != null)
                    resultModelCollection = form.testAnalyte.resultModelCollection;
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
                if (form.testAnalyte.resultModelCollection != null)
                    resultModelCollection = form.testAnalyte.resultModelCollection;
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
            if (prevSelTabIndex == -1 && resultModelCollection.size() > 0) {
                //         
                // if a user doesn't select any tab from resultPanel then any new
                // rows added to the model for the first tab won't get added to the
                // model stored at index zero in resultModelCollection because the
                // code in onBeforeTabSelected() wont get a chance to execute;
                // this line makes sure that on committing a test's data,even if
                // no tab was selected from resultPanel, the latest model for the
                // first tab makes its way in resultModelCollection
                //            
                resultModelCollection.set(0,(TableDataModel)resultWidget.model.getData());
            }

            form.testAnalyte.resultModelCollection = resultModelCollection;

            resultWidget.finishEditing();
            sectionWidget.finishEditing();
            reflexTestWidget.finishEditing();
        }
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {            
 
    }

    /**
     * Overriden to allow lazy loading various tabs of the various tab panels on
     * the screen
     */
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        TableDataModel<TableDataRow<Integer>> model = null;
        ArrayList<TableDataModel<TableDataRow<Integer>>> list = null;
        TableDataRow<Integer> mr = null;
        TableDataRow<Integer> dr = null;                 

        AbstractField mf = null;
        AbstractField df = null;

        if (sender == resultPanel) {
            if (tabIndex != 0 && prevSelTabIndex == -1)
                prevSelTabIndex = 0;
            //
            // when a user clicks a tab on the test results panel (resultPanel)
            // to see the data for that result group, the model for the table
            // displayed under it, is taken from the list of models
            // (resultModelCollection) and set to the table
            // 
            if (resultModelCollection.size() > tabIndex) {
                if (prevSelTabIndex > -1 && prevSelTabIndex != tabIndex) {
                    resultWidget.finishEditing();
                    model = (TableDataModel<TableDataRow<Integer>>)resultWidget.model.getData()
                                                                                     .clone();
                    list = resultWidget.model.getData().getDeletions();

                    if(list!=null) { 
                     for (int i = 0; i < list.size(); i++) {
                        model.getDeletions().add((TableDataRow<Integer>)list.get(i).clone());
                     }
                    }
                    
                    for (int i = 0; i < model.size(); i++) {
                        dr = resultWidget.model.getRow(i);
                        mr = model.get(i);

                        for (int j = 0; j < dr.size(); j++) {
                            mf = (AbstractField)mr.cells[j];
                            df = (AbstractField)dr.cells[j];

                            for (int k = 0; k < df.getErrors().size(); k++) {
                                mf.addError((String)df.getErrors().get(k));
                            }
                        }
                    }
                    resultModelCollection.set(prevSelTabIndex,(TableDataModel)model);
                }

                if (!(prevSelTabIndex == -1 && tabIndex == 0)) {
                    model = resultModelCollection.get(tabIndex);
                    resultWidget.model.load(model);
                    prevSelTabIndex = tabIndex;
                }
            }
        } else {
            
            if (state != State.QUERY) {                
                if (tabIndex == 1) { 
                 if(!form.sampleType.load) {   
                    form.testTabPanel = "sampleTypeTab";
                    fillSampleTypes();
                 }
                 resultWidget.finishEditing();
                } else if (tabIndex == 2) {
                   if(!form.testAnalyte.load) {
                    form.testTabPanel = "analyteTab";                    
                    fillTestAnalyte();
                   } 
                   sampleTypeWidget.finishEditing();
                } else if (tabIndex == 3) {
                    if(!form.prepAndReflex.load) {
                        form.testTabPanel = "prepAndReflexTab";   
                        fillPrepTestsReflexTests();
                    }    
                    analyteTreeController.finishEditing();
                    resultWidget.finishEditing();
                } else if (tabIndex == 4) { 
                   if(!form.worksheet.load) {
                    form.testTabPanel = "worksheetTab";   
                    fillWorksheetLayout();
                   }
                   analyteTreeController.finishEditing();
                }
            }
        } 
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
        if (widget == wsItemWidget)
            return (addRow.cells[0]).getValue() != null;
        else if (widget == reflexTestWidget) {
            if (((addRow.cells[0]).getValue() != null && !(addRow.cells[0]).getValue().equals(-1))
                    || ((addRow.cells[1]).getValue() != null && !(addRow.cells[1]).getValue()
                                                                                                                                                                                                      .equals(-1)))
                return true;
        } else if (widget == resultWidget) {
            if (((addRow.cells[0]).getValue() != null && !(addRow.cells[0]).getValue().equals(-1))
                            || ((addRow.cells[1]).getValue() != null && !(addRow.cells[1]).getValue()
                                                                                                                                                                                                          .equals(-1)))
            return true;
        }

        return (addRow.cells[0]).getValue() != null && !(addRow.cells[0]).getValue().equals(-1);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget,TableDataRow set,int row,int col) {
        DropDownField<Integer> ddfield = null;
        TableDataRow<Integer> vset = null;
        Integer id = null;

        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
            // column 2 is for the Results dropdown
            if (widget == reflexTestWidget && col == 2 && set !=null) {
                ddfield = (DropDownField<Integer>)set.cells[1];
                //
                // checks to see if the analyte list has at least one option in it
                // 
                if(ddfield.getValue() != null && ddfield.getValue().size() > 0)
                    vset = ddfield.getValue().get(0);
                
                id = (Integer)ddfield.getSelectedKey();
                if (vset != null && vset.enabled == true
                    && ddfield.getErrors().size() == 0) {
                    setTestResultsForAnalyte(id, set);
                } else {
                    Window.alert(consts.get("selectAnaBeforeRes"));
                    return false;
                }
            } else if (widget == resultWidget && resultPanel.getTabBar()
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
        if (sender == sectionWidget && row < sectionWidget.model.getData()
                                                                .size()
            && col == 1) {
            final int currRow = row;
            final Integer selValue = (Integer)((DropDownField)sectionWidget.model.getRow(row).cells[col]).getSelectedKey();
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
                                           for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                               if (iter != currRow) {
                                                   // 
                                                   // if the option chosen is "Default" for this
                                                   // row, then all the other rows must be set to
                                                   // the blank option
                                                   //                                                                                                                                                                     
                                                   DropDownField field = (DropDownField)sectionWidget.model.getRow(iter).cells[1];
                                                   field.setValue(new TableDataRow<Integer>(null));
                                               }
                                           }
                                           sectionWidget.model.refresh();
                                       } else {
                                           if (selValue != null) {
                                               for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                   DropDownField field = (DropDownField)sectionWidget.model.getRow(iter).cells[1];
                                                   //
                                                   // if the option chosen is "Ask" or "Match User Location" for
                                                   // this row, then all the other rows must be set to
                                                   // the same option which is this one
                                                   // 
                                                   field.setValue(new TableDataRow<Integer>(selValue));
                                               }
                                           }
                                           sectionWidget.model.refresh();
                                       }
                                   }

                                   public void onFailure(Throwable caught) {
                                       Window.alert(caught.getMessage());
                                       window.clearStatus();
                                   }
                               });

        } else if (sender == resultWidget && row < resultWidget.model.getData()
                                                                     .size()) {
            final String value = ((StringField)resultWidget.model.getRow(row).cells[2]).getValue();

            if (col == 2 && !"".equals(value.trim())) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)resultWidget.model.getRow(row).cells[1]).getSelectedKey();

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
                                                                              resultWidget.model.setCellError(currRow,2,
                                                                                                              consts.get("illegalDictEntryException"));
                                                                          } else {
                                                                              TableDataRow<Integer> set = resultWidget.model.getRow(currRow);
                                                                              if (set.key == null) {                                                                                  
                                                                                  set.key = getNextTempResId();
                                                                              }
                                                                              resultWidget.model.setCell(currRow,2,
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
                                                   resultWidget.model.setCell(currRow,2,finalValue);
                                                   TableDataRow<Integer> set = resultWidget.model.getRow(currRow);                                                   

                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();                                                                                                          

                                                   checkAndAddNewResultValue(finalValue,set);

                                               } else {
                                                   resultWidget.model.setCellError(currRow,2,
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
                                                   resultWidget.model.setCell(currRow,2,finalValue);
                                                   TableDataRow<Integer> set = resultWidget.model.getRow(currRow);                                                   

                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();                                                                                                         

                                                   checkAndAddNewResultValue(finalValue,set);

                                               } else {
                                                   resultWidget.model.setCellError(currRow,2,
                                                                                   consts.get("illegalTiterFormatException"));
                                               }

                                           } else if ("test_res_type_date".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateDate(value);
                                                   resultWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalDateValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           } else if ("test_res_type_date_time".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateDateTime(value);
                                                   resultWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalDateTimeValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           } else if ("test_res_type_time".equals(((TestGeneralPurposeRPC)result).stringValue)) {
                                               resultWidget.model.setCell(currRow,2,value);
                                               TableDataRow set = (TableDataRow)resultWidget.model.getData().get(currRow);                                              
                                               try{                    
                                                   finalValue = validateTime(value);
                                                   resultWidget.model.setCell(currRow,2,finalValue);
                                                   
                                                   if (set.key == null) 
                                                       set.key = getNextTempResId();
                                                   
                                                   checkAndAddNewResultValue(finalValue,set);
                                                   
                                                  }catch(IllegalArgumentException ex) {
                                                      resultWidget.model.setCellError(currRow,2,
                                                                      consts.get("illegalTimeValueException"));                                                 
                                                 }                                                                                                                                                                                                  
                                           }

                                       }

                                       public void onFailure(Throwable caught) {
                                           Window.alert(caught.getMessage());
                                           window.clearStatus();
                                       }
                                   });

            } else if (col == 0 && row < resultWidget.model.getData().size()) {
                TableDataRow<Integer> set = resultWidget.model.getRow(row);
                IntegerObject data = (IntegerObject)set.getData();                
                Integer id = (Integer)((DropDownField)resultWidget.model.getRow(row).cells[0]).getSelectedKey();
                Integer numRes = null;
                if (data == null) {
                    data = new IntegerObject();
                    set.setData(data);
                }

                //
                // the following code manipulates unitIdNumResMap such that 
                // if for a given result the unit selected is different from what
                // it was previously then the value for that unit in unitIdNumResMap 
                // is reduced by one and it is increased by one if a result that
                // didn't have it selected previously has it selected now
                //
                if (data.getValue() == null) {                                          
                    if (id != -1) {
                         //
                         // if data's value is null then this is a newly edited
                         // row and thus if the blank option was not selected,
                         // then the value for this unit in unitIdNumResMap 
                         // should be incremented by one
                         //
                         data.setValue(id);
                         set.setData(data);
                         numRes = unitIdNumResMap.get(id);
                         if(numRes == null)
                          numRes = 0;                         
                         unitIdNumResMap.put(id,++numRes);                                                                  
                    }
                } else {                    
                    if (!data.getValue().equals(id)) {
                        //
                        // if data's value is not null and the unit set as data
                        // is not the same as the one selected right now  
                        // then the value for this unit from data in unitIdNumResMap 
                        // should be decremented by one and the value for the unit 
                        // currently selected should be incremented by one
                        //
                        numRes = unitIdNumResMap.get(data.getValue());      
                        if(numRes != null && numRes > 0)
                         unitIdNumResMap.put(data.getValue(), --numRes);
                        data.setValue(id);
                        set.setData(data);
                        if (id != -1) {
                            numRes = unitIdNumResMap.get(id);
                            if(numRes == null)
                                numRes = 0;                                   
                            
                            unitIdNumResMap.put(id,++numRes); 
                              
                        }
                    }
                }
            }
        } else if (sender == wsItemWidget && row < wsItemWidget.model.getData()
                                                                     .size()) {
            if (col == 0 && row < wsItemWidget.model.getData().size()) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)wsItemWidget.model.getRow(row).cells[1]).getSelectedKey();
                tsrpc.key = selValue;

                screenService.call("getCategorySystemName",tsrpc,
                                   new AsyncCallback<TestGeneralPurposeRPC>() {
                                       public void onSuccess(TestGeneralPurposeRPC result) {
                                           Integer value = (Integer)(wsItemWidget.model.getRow(currRow).cells[0]).getValue();
                                           if ("pos_duplicate".equals(result.stringValue) || "pos_fixed".equals(result.stringValue)) {
                                               if (value == null) {
                                                   wsItemWidget.model.setCellError(currRow,0,
                                                                                   consts.get("fixedDuplicatePosException"));
                                               }
                                           } else if (!("pos_duplicate".equals(result.stringValue)) && !("pos_fixed".equals(result.stringValue))) {
                                               if (value != null) {
                                                   wsItemWidget.model.setCellError(currRow,
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
        } else if (sender == reflexTestWidget && row < reflexTestWidget.model.getData()
                                                                             .size()) {
            TableDataRow<Integer> rset = reflexTestWidget.model.getRow(row);
            TableDataRow<Integer> vset = null;
            DropDownField<Integer> ddfield = (DropDownField<Integer>)rset.cells[col];
            ArrayList<TableDataRow<Integer>> value = ddfield.getValue();
            Integer analyteId = null;
            if(value != null && value.size() > 0) {
             if (col == 1) {
                vset = (TableDataRow<Integer>)value.get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestWidget.model.setCellError(row,1,
                                                        consts.get("analyteDeleted"));
                } else {
                     analyteId = (Integer)ddfield.getSelectedKey();
                     setTestResultsForAnalyte(analyteId, rset);
                }        
            } else if (col == 2) {
                vset = (TableDataRow<Integer>)value.get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestWidget.model.setCellError(row,2,
                                                        consts.get("resultDeleted"));
                }
            }
          }   
        } else if (sender == sampleTypeWidget && col == 1
                   && row < sampleTypeWidget.model.getData().size()) {
            TableDataRow<Integer> set = sampleTypeWidget.model.getRow(row);
            DropDownField ufield = (DropDownField)set.cells[col];
            Integer id = (Integer)(ufield.getSelectedKey());
            IntegerField data = (IntegerField)set.getData();
            Integer numRes = null;
            Integer unitId = null;
            int numRowsUnit = 0;
            TableDataRow<Integer> ddset = null;
            
            if (data != null) {
                //
                // find out what unit was previously selected for this row. If
                // data for this row is not null then we know that this row was
                // edited before and a legitimate unit, i.e. not the blank option,
                // was selected for it
                //
                unitId = data.getValue();
            } else {                
                //
                // if data is null then we need to set data to be the unit 
                // currently selected
                //
                if (id != null && id != -1) {
                    //
                    // but we only do it if the currently selected unit is a 
                    // legitimate value
                    //
                    data = new IntegerField(id);                    
                    set.setData(data);                    
                    //
                    // we also need to make sure that we create a key value pair for
                    // the unit in unitIdNumResMap where the key is the id of the unit
                    // and value is zero if the key is not already in unitIdNumResMap  
                    //
                    numRes = unitIdNumResMap.get(id);
                    if (numRes == null) {
                        unitIdNumResMap.put(id, 0);
                    }
                    // 
                    // then we create an option representing this unit in the unit
                    // dropdown in the table for test results if necessary
                    //
                    addSetToUnitDropdown(ufield);
                }
            }

            if (unitId != null && !unitId.equals(id)) {
                    //
                    // if this row was edited before and a legitimate unit was selected
                    // for it, then we need to find out how many test results have 
                    // it selected as their unit 
                    //
                    numRes = unitIdNumResMap.get(unitId);     
                    numRowsUnit = getNumRowsHavingUnit(unitId);
                    if (numRes != null && numRes > 0 && !(numRowsUnit >= 1)) {
                        //
                        // if atleast one test result has this unit selected for it 
                        // and this is the only row in the sample type table that
                        // has this unit,(the value returned by getNumRowsHavingUnit())
                        // then we cannot allow the user to select another unit for this row.
                        // We display an error that states this compulsion and also
                        // set the unit back to what it was before the user decided to change it
                        //                        
                        ufield.setValue(new TableDataRow<Integer>(unitId));
                        sampleTypeWidget.model.refresh();
                        Window.alert(consts.get("cantChangeUnit"));
                    }else {                        
                        if (numRes == null || (numRes != null && numRes == 0)) {
                          if(numRowsUnit == 0) { 
                            ddset = ModelUtil.getRowByKey(unitDropdownModel,unitId);
                            unitDropdownModel.delete(ddset);
                            setTableDropdownModel(resultWidget,0,unitDropdownModel);
                          } 
                        }
                        
                        if (id != null && id != -1) {
                          data = new IntegerField(id);                    
                          set.setData(data);         
                          numRes = unitIdNumResMap.get(id);
                          if (numRes == null) {
                               unitIdNumResMap.put(id,0);
                          }
                         addSetToUnitDropdown(ufield);
                        }
                    }
            }          
        } else if (sender == wsAnalyteWidget && col == 1) {
            final TableDataRow<Integer> trow = wsAnalyteWidget.model.getRow(row);

            if (analyteTreeController.model.getData() == null || analyteTreeController.model.getData()
                                                                                            .size() == 0) {
                form.testAnalyte.entityKey = form.entityKey;
                screenService.call("getAnalyteTreeModel",form.testAnalyte,
                                   new AsyncCallback<TestAnalyteForm>() {
                                       public void onSuccess(TestAnalyteForm result) {
                                           TreeField tfield = result.analyteTree;
                                           analyteTreeController.model.setModel(tfield.getValue());
                                           setAnalytesAvailable(trow);
                                           wsAnalyteWidget.model.refresh();
                                       }

                                       public void onFailure(Throwable caught) {
                                           Window.alert(caught.getMessage());
                                           window.clearStatus();
                                       }

                                   });
            } else {
                setAnalytesAvailable(trow);
            }

            setRepeatValue(trow);
            wsAnalyteWidget.model.refresh();
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {

    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {

    }

    public void finishedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        int index = analyteTreeController.modelIndexList[row];
        int tempAnaId = 0;

        TreeDataItem item = analyteTreeController.model.getRow(index);
        DropDownField field = null;

        String selText = null;
        String prevVal = null;
        
        Integer resGrpNum = null;

        TableDataRow<Integer> ddset = null;
        List<Integer> lset = null;

        IntegerObject data = null;        

        boolean createNewSet = false;

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
            if (field.getSelectedKey() != null && (Integer)field.getSelectedKey() != -1) {
               /* if (!field.getErrors()
                          .contains(consts.get("fieldRequiredException"))) {
                    field.addError(consts.get("fieldRequiredException"));
                    analyteTreeController.model.refresh();
                    return;
                }
            }*/
            if (item.key == null) {                             
                    createNewSet = true;
               } else {                    
                   data = (IntegerObject)item.getData();
                   ddset = ModelUtil.getRowByKey(testAnalyteDropdownModel,item.key);
                   if (ddset != null) {
                        prevVal = (String)ddset.cells[0].getValue();
                        if (!prevVal.trim().equals(selText.trim())) {
                            
                            setErrorToReflexFields(consts.get("analyteNameChanged"),
                                                   item.key,1);
                            
                            ddset.cells[0].setValue(selText);
                            
                            ((TableDropdown)reflexTestWidget.columns.get(1)
                               .getColumnWidget()).setModel(testAnalyteDropdownModel);
                            
                            updateWorksheetAnalyte(data.getValue(),
                               (Integer)field.getSelectedKey(),selText);
                            
                            data.setValue((Integer)field.getSelectedKey());
                        }
                    }
                }            

            if (createNewSet) {
                tempAnaId = getNextTempAnaId();
                ddset = new TableDataRow<Integer>(tempAnaId,
                                                  new StringObject(selText));                
                item.key = tempAnaId;                

                if (testAnalyteDropdownModel == null)
                    testAnalyteDropdownModel = new TableDataModel<TableDataRow<Integer>>();

                testAnalyteDropdownModel.add(ddset);
                ((TableDropdown)reflexTestWidget.columns.get(1)
                                                        .getColumnWidget()).setModel(testAnalyteDropdownModel);

                addWorksheetAnalyte((Integer)field.getSelectedKey(), selText);
            }
          }
        } else if (col == 4) {
            field = (DropDownField)item.cells[col];
            selText = (String)field.getTextValue();
            resGrpNum = (Integer)field.getSelectedKey();
            resultWidget.finishEditing();

            //
            // checks to see if a result group was chosen, if so, adds the 
            // analyte-result group pairing to resGroupAnalyteIdMap if the pairing 
            // doesn't exist
            //
            if (item.key != null && selText != null && (selText.trim().length() > 0)) {                                  
                lset = (ArrayList<Integer>)resGroupAnalyteIdMap.get(resGrpNum);                 
                if (lset != null) {
                    if (!lset.contains(item.key))
                        lset.add(item.key);                    
                } else {
                    lset = new ArrayList<Integer>();
                    lset.add(item.key);
                    resGroupAnalyteIdMap.put(resGrpNum, lset);
                }

                //
                // sets the result model from the chosen result group to the 
                // selected analyte in resultDropdownModelMap. 
                //
                resultDropdownModelMap.put(item.key,
                                           getDropdownModelForRsltGrp(resGrpNum));
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
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem item, int row) {
        if ("analyte".equals(item.leafType)) {
            DropDownField field = (DropDownField)item.cells[4];
            Integer key = (Integer)field.getSelectedKey();
            if (key != null) {
                resultPanel.getTabBar().selectTab(key - 1);
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
                    row = (TableDataRow<Integer>)resultWidget.model.createRow();
                    row.cells[1].setValue(dictSet);
                    row.cells[2].setValue(entry);                                        
                    row.key = getNextTempResId();
                    resultWidget.model.addRow(row);
                    checkAndAddNewResultValue(entry, row);
                }
            }
            resultWidget.model.refresh();
        }
    }

    private AsyncCallback<TestForm> fetchForDuplicateCallBack = new AsyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {
            form = result;
            resultModelCollection = result.testAnalyte.resultModelCollection;
            flipSignsInAnalyteDropDown();
            ((TableDropdown)reflexTestWidget.columns.get(1).getColumnWidget()).setModel(testAnalyteDropdownModel);
            flipSignsInModelMaps();
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
    private void setTestResultsForAnalyte(Integer analyteId,
                                          TableDataRow<Integer> set) {
        if (analyteId != null) {
            TableDataModel<TableDataRow<Integer>> model = (TableDataModel)resultDropdownModelMap.get(analyteId);
            DropDownField<Integer> field = (DropDownField<Integer>)set.cells[2];
            TableDataRow<Integer> prevSet = new TableDataRow<Integer>((Integer)field.getSelectedKey());
            TableDataRow<Integer> blankSet = new TableDataRow<Integer>(new Integer(-1));

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
                if (field.getSelectedKey() != null && model.list.contains(prevSet))
                    field.setValue(prevSet);
                else
                    field.setValue(blankSet);
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
     * This function sets the Data Model for a dropdown field on the screen
     * specified by "fieldName", that's not inside a tree or table etc, to the
     * argument "model"
     */
    private void setDropdownModel(String fieldName,
                                  TableDataModel<TableDataRow<Integer>> model) {
        Dropdown drop = (Dropdown)getWidget(fieldName);
        drop.setModel(model);
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
    
    private void setReflexFlagsModel(TableDataModel<TableDataRow<Integer>> reflexFlags) {
        ((TableDropdown)reflexTestWidget.columns.get(3).getColumnWidget()).setModel(reflexFlags);        
    }
    
    private void setAnalyteTypesModel(TableDataModel<TableDataRow<Integer>> analyteTypes) {        
        ((TableDropdown)analyteTreeController.columns.get(1).getColumnWidget("analyte")).setModel(analyteTypes);
    }
    
    private void setResultFlagsModel(TableDataModel<TableDataRow<Integer>> resultFlags) {
        ((TableDropdown)resultWidget.columns.get(6).getColumnWidget()).setModel(resultFlags);        
    }
    
    private void setResultTypesModel(TableDataModel<TableDataRow<Integer>> resultTypes) {
        ((TableDropdown)resultWidget.columns.get(1).getColumnWidget()).setModel(resultTypes);        
    }
    
    private void setRoundingMethodsModel(TableDataModel<TableDataRow<Integer>> roundingMethods) {
        ((TableDropdown)resultWidget.columns.get(8).getColumnWidget()).setModel(roundingMethods);        
    }
    
    private void setUnitsOfMeasureModel(TableDataModel<TableDataRow<Integer>> units) {
        ((TableDropdown)sampleTypeWidget.columns.get(1).getColumnWidget()).setModel(units);        
    }
    
    private void setSampleTypesModel(TableDataModel<TableDataRow<Integer>> sampleTypes) {
        ((TableDropdown)sampleTypeWidget.columns.get(0).getColumnWidget()).setModel(sampleTypes);        
    }
    
    private void setSectionFlagsModel(TableDataModel<TableDataRow<Integer>> sectionFlags) {        
        ((TableDropdown)sectionWidget.columns.get(1).getColumnWidget()).setModel(sectionFlags);
    }
    
    private void setWorksheetAnalyteFlagsModel(TableDataModel<TableDataRow<Integer>> wsAnalyteFlags) {
        ((TableDropdown)wsAnalyteWidget.columns.get(3).getColumnWidget()).setModel(wsAnalyteFlags);        
    }
    
    private void setWorksheetItemTypesModel(TableDataModel<TableDataRow<Integer>> wsItemTypes) {
        ((TableDropdown)wsItemWidget.columns.get(1).getColumnWidget()).setModel(wsItemTypes);        
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

    private void fillResultModelCollection() {
        TestGeneralPurposeRPC tirpc = null;

        if (form.entityKey == null)
            return;

        resultModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();

        tirpc = new TestGeneralPurposeRPC();
        tirpc.key = form.entityKey;

        screenService.call("getGroupCountForTest",tirpc,
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
                                       
                                       form.testAnalyte.model = (TableDataModel<TableDataRow<Integer>>)defaultModel.clone();
                                       form.testAnalyte.integerValue = group;                                       

                                       form.testAnalyte.resultModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
                                       form.testAnalyte.duplicate = false;

                                       screenService.call("loadTestResultModellist",
                                                          form.testAnalyte,
                                                          new AsyncCallback<TestAnalyteForm>() {
                                                              public void onSuccess(TestAnalyteForm result) {
                                                                  resultModelCollection = result.resultModelCollection;
                                                              }

                                                              public void onFailure(Throwable caught) {
                                                                  Window.alert(caught.getMessage());
                                                                  window.clearStatus();
                                                              }
                                                          });

                                       if (resultPanel.getTabBar()
                                                      .getTabCount() > 0) {
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
     * This function loads the Analyte dropdown in the Reflexive Test table with
     * the analytes added to the test, the data for which was the most recently
     * fetched
     */
    private void fillTestAnalyteDropDown() {        
        if (form.entityKey == null) {
            return;
        }
        
        form.testAnalyte.entityKey = form.entityKey;

        screenService.call("getTestAnalyteModel",form.testAnalyte,
                           new AsyncCallback<TestAnalyteForm>() {
                               public void onSuccess(TestAnalyteForm result) {
                                   ((TableDropdown)reflexTestWidget.columns.get(1)
                                                                           .getColumnWidget()).setModel(result.model);
                                   testAnalyteDropdownModel = result.model;

                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });
    }

    private void fillResultGroupDropdown() {
        TestGeneralPurposeRPC tdmrpc = null;
        if (form.entityKey == null) {
            return;
        }

        tdmrpc = new TestGeneralPurposeRPC();
        tdmrpc.key = form.entityKey;

        screenService.call("getResultGroupModel",tdmrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                               public void onSuccess(TestGeneralPurposeRPC result) {
                                   resultGroupDropdownModel = result.model;
                                   ((TableDropdown)analyteTreeController.columns.get(4)
                                                                                .getColumnWidget("analyte")).setModel(resultGroupDropdownModel);
                                   group = resultGroupDropdownModel.size() - 1;
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });
    }

    /**
     * This function loads the resultDropDownModelMap variable, which is a
     * HashMap with keys being the ids of the rows in the test_analyte table 
     * that correspond to this test and the corresponding values are the lists
     * of result values that are associated  with that test_analyte. This map is
     * then used for fast access to these models, so that they don't have to be 
     * fetched from the database every time
     */
    private void fillMapsAndUnitDropDownModel() {
        TestGeneralPurposeRPC imrpc = null;
        if (form.entityKey == null) {
            return;
        }

        imrpc = new TestGeneralPurposeRPC();
        imrpc.key = form.entityKey;

        imrpc.resultDropdownModelMap = new HashMap<Integer, TableDataModel<TableDataRow<Integer>>>();
        imrpc.resGroupAnalyteIdMap = new HashMap<Integer, List<Integer>>();
        imrpc.unitIdNumResMap = new HashMap<Integer, Integer>();
        
        screenService.call("getMapsAndUnitDropDownModel",imrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                               public void onSuccess(TestGeneralPurposeRPC result) {
                                   resultDropdownModelMap = result.resultDropdownModelMap;
                                   resGroupAnalyteIdMap = result.resGroupAnalyteIdMap;
                                   unitIdNumResMap = result.unitIdNumResMap;
                                   
                                   if (result.model != null) {
                                       ((TableDropdown)resultWidget.columns.get(0)
                                                                           .getColumnWidget()).setModel(result.model);
                                       unitDropdownModel = result.model;
                                   } else {
                                       if (unitDropdownModel != null) {
                                           unitDropdownModel.clear();
                                           unitDropdownModel.add(new TableDataRow<Integer>(-1,
                                                                                           new StringObject("")));
                                           ((TableDropdown)resultWidget.columns.get(0)
                                                                               .getColumnWidget()).setModel(unitDropdownModel);
                                       }
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
        sampleTypeWidget.finishEditing();
        int row = sampleTypeWidget.modelIndexList[sampleTypeWidget.activeRow];
        TableDataRow<Integer> set = sampleTypeWidget.model.getRow(row);
        DropDownField ufield = (DropDownField)set.cells[1];
        Integer id = (Integer)(ufield.getSelectedKey());
        Integer numRes = null;
        if (row > -1) {
            if (id != null && id != -1) {
                numRes = unitIdNumResMap.get(id);
                if (numRes != null && numRes > 0 && getNumRowsHavingUnit(id) <= 1) {
                    Window.alert(consts.get("cantChangeUnit"));
                    ufield.setValue(new TableDataRow<Integer>(id));
                    sampleTypeWidget.model.refresh();
                } else {
                    sampleTypeWidget.model.deleteRow(row);
                }
            }
        }
    }

    private void onPrepTestRowButtonClick() {
        int index = prepTestWidget.modelIndexList[prepTestWidget.activeRow];
        if (index > -1)
            prepTestWidget.model.deleteRow(index);
    }

    private void onWSItemRowButtonClick() {
        int index = wsItemWidget.modelIndexList[wsItemWidget.activeRow];
        if (index > -1)
            wsItemWidget.model.deleteRow(index);
    }

    private void onReflexTestRowButtonClick() {        
        int index = reflexTestWidget.modelIndexList[reflexTestWidget.activeRow];        
        if (index > -1)
            reflexTestWidget.model.deleteRow(index);
    }

    private void onTestSectionRowButtonClicked() {
        int index = sectionWidget.modelIndexList[sectionWidget.activeRow];
        if (index > -1) 
            sectionWidget.model.deleteRow(index);        
    }

    private void onTestResultRowButtonClicked() {
        TableDataModel<TableDataRow<Integer>> model = resultWidget.model.getData();
        int selIndex = resultWidget.modelIndexList[resultWidget.activeRow];
        TableDataRow<Integer> set = null;
        
        if (selIndex > -1) {
            if (model.size() > 1) {
                set = resultWidget.model.getRow(selIndex);                
                if (set.key != null) {                    
                        disableResultOptions(set);
                        setErrorToReflexFields(consts.get("resultDeleted"),
                                               set.key,2);
                    
                }

                resultWidget.model.deleteRow(selIndex);      
            } else {
                Window.alert(consts.get("atleastOneResInResGrp"));
            }
        }
    }

    private void onAddResultTabButtonClicked() {
        TableDataRow<Integer> ddset = null;
        List<Integer> lset = null;
        group++;

        if (group == 2)
            prevSelTabIndex = 0;

        resultPanel.add(getDummyPanel(), new Integer(group).toString());
        // resultPanel.addTab(new Integer(group).toString());
        resultModelCollection.add((TableDataModel<TableDataRow<Integer>>)defaultModel.clone());
        resultPanel.selectTab(group - 1);

        ddset = new TableDataRow<Integer>(group,
                                          new StringObject((new Integer(group)).toString()));

        resultGroupDropdownModel.add(ddset);

        lset = new ArrayList<Integer>();
        if (resGroupAnalyteIdMap == null) {
            resGroupAnalyteIdMap = new HashMap<Integer, List<Integer>>();
        }
        resGroupAnalyteIdMap.put(group, lset);

        setTableDropdownModel(analyteTreeController,4,"analyte",
                              resultGroupDropdownModel);

    }

    private void onAddRowButtonClicked() {
        TreeDataItem newItem = analyteTreeController.model.getData()
                                                          .createTreeItem("analyte");
        analyteTreeController.model.addRow(newItem);
        analyteTreeController.model.refresh();
    }

    private void onGroupAnalytesButtonClicked() {
        TreeDataItem cloneItem = null;
        ArrayList<TreeDataItem> items = analyteTreeController.model.getSelections();
        if (items.size() < 2) {
            Window.alert(consts.get("atleastTwoAnalytes"));
            return;
        }
        ArrayList<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList()
                                                                                               .clone();
        Collections.sort(selectedRowIndexes);

        TreeDataItem groupItem = analyteTreeController.model.createTreeItem("top");
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

        analyteTreeController.model.deleteRows(analyteTreeController.model.getSelectedRowList());

        if (analyteTreeController.model.getData().size() > 0) {
            if (selectedRowIndexes.get(0) < analyteTreeController.model.getData()
                                                                       .size()) {
                analyteTreeController.model.addRow(selectedRowIndexes.get(0),
                                                   groupItem);
            } else {
                analyteTreeController.model.addRow(groupItem);
            }
        } else {
            analyteTreeController.model.addRow(groupItem);
        }

        analyteTreeController.model.refresh();
    }

    private void onUngroupAnalytesButtonClicked() {
        TreeDataItem item;
        TreeDataItem newItem;

        List<TreeDataItem> chItems;
        int index;
        ArrayList<Integer> selRows = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList()
                                                                                    .clone();

        if (selRows.size() > 0) {
            for (int iter = 0; iter < selRows.size(); iter++) {
                index = selRows.get(iter);
                item = analyteTreeController.model.getRow(index);
                if ("top".equals(item.leafType)) {
                    chItems = item.getItems();

                    for (int i = 0; i < chItems.size(); i++) {
                        newItem = (TreeDataItem)chItems.get(i).clone();
                        newItem.parent = null;
                        newItem.setData(chItems.get(i).getData());
                        chItems.get(i).setData(null);
                        analyteTreeController.model.addRow(index + i, newItem);
                    }

                    analyteTreeController.model.deleteRow(index);
                }
            }
            analyteTreeController.model.refresh();
        }
    }

    private void onDeleteTreeItemButtonClicked() {
        List<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList()
                                                                                          .clone();
        int index = 0;
        TreeDataItem item = null;
        TreeDataItem parent = null;
        TreeDataModel model;        
        Integer anaId = null;

        for (int iter = 0; iter < selectedRowIndexes.size(); iter++) {
            index = selectedRowIndexes.get(iter);
            item = (TreeDataItem)analyteTreeController.model.getRow(index)
                                                            .clone();
            if (item.parent != null) {
                parent = item.parent;
                if (parent.getData() == null) {
                    model = new TreeDataModel();
                    parent.setData(model);
                }
                ((TreeDataModel)parent.getData()).add(item);
                analyteTreeController.model.deleteRow(index);
            } else {
                analyteTreeController.model.deleteRow(index);
            }

            if (item.key != null) {
                                
                    setErrorToReflexFields(consts.get("analyteDeleted"),
                                           item.key,1);
                ((TableDataRow<Integer>)ModelUtil.getRowByKey(testAnalyteDropdownModel,
                                                              item.key)).enabled = false;
                ((TableDropdown)reflexTestWidget.columns.get(1)
                                                        .getColumnWidget()).setModel(testAnalyteDropdownModel);

                anaId = (Integer)((DropDownField)item.cells[0]).getSelectedKey();
                deleteWorksheetAnalytes(anaId);
            }
        }
        analyteTreeController.model.refresh();
    }

    private void onDuplicateRecordClick() {
        form.numGroups = group;
        form.resultTableModel = defaultModel;
        screenService.call("getDuplicateRPC", form, fetchForDuplicateCallBack);
    }

    /**
     * This function opens a modal window which allows the users to select one
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
        reflexTestWidget.model.enableAutoAdd(enable);
        wsItemWidget.model.enableAutoAdd(enable);
        prepTestWidget.model.enableAutoAdd(enable);
        sampleTypeWidget.model.enableAutoAdd(enable);
        sectionWidget.model.enableAutoAdd(enable);
        resultWidget.model.enableAutoAdd(enable);
    }

    private void loadDropdownModel(String fieldName) {       
        TestGeneralPurposeRPC tfnrpc = new TestGeneralPurposeRPC();
        tfnrpc.fieldName = fieldName;
        screenService.call("getInitialModel",tfnrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                             public void onSuccess(TestGeneralPurposeRPC result) {
                               setDropdownModel(result.fieldName,result.model);
                             }

                             public void onFailure(Throwable caught) {
                               Window.alert(caught.getMessage());
                               window.clearStatus();
                             }
                           });

    }

    private void loadTableDropdownModel(TableWidget widget,
                                        int column,
                                        String fieldName) {
        final int col = column;
        final TableWidget w = widget;
        TestGeneralPurposeRPC tfnrpc = new TestGeneralPurposeRPC();
        tfnrpc.fieldName = fieldName;

        screenService.call("getInitialModel",tfnrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                            public void onSuccess(TestGeneralPurposeRPC result) {
                               setTableDropdownModel(w, col, result.model);
                            }

                            public void onFailure(Throwable caught) {
                               Window.alert(caught.getMessage());
                               window.clearStatus();
                            }
                           });

    }

    private void loadTableDropdownModel(TreeWidget widget,int column,
                                        String leafType,String fieldName) {        
        final int col = column;
        final String leaf = leafType;
        final TreeWidget w = widget;
        TestGeneralPurposeRPC tfnrpc = new TestGeneralPurposeRPC();
        tfnrpc.fieldName = fieldName;

        screenService.call("getInitialModel",tfnrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                               public void onSuccess(TestGeneralPurposeRPC result) {
                                   setTableDropdownModel(w,col,leaf,result.model);
                               }

                               public void onFailure(Throwable caught) {
                                   Window.alert(caught.getMessage());
                                   window.clearStatus();
                               }
                           });

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
        TreeModel model = analyteTreeController.model;
        Integer selVal = null;
        Integer unselVal = new Integer(-1);

        for (int i = 0; i < model.getData().size(); i++) {
            item = model.getData().get(i);
            if ("top".equals(item.leafType)) {
                for (int j = 0; j < item.getItems().size(); j++) {
                    chItem = item.getItems().get(j);
                    selVal = (Integer)((DropDownField)chItem.cells[4]).getSelectedKey();
                    if (selVal == null || unselVal.equals(selVal)) {
                        return false;
                    }
                }
            } else {
                selVal = (Integer)((DropDownField)item.cells[4]).getSelectedKey();
                if (selVal == null || unselVal.equals(selVal)) {
                    return false;

                }
            }
        }

        return true;
    }

    private boolean treeItemsHaveErrors() {
        TreeDataItem item = null;
        TreeModel model = analyteTreeController.model;
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
        TableDataModel<TableDataRow<Integer>> model = reflexTestWidget.model.getData();
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
        TableDataModel<TableDataRow<Integer>> model = wsItemWidget.model.getData();
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

    private TableDataModel getSingleRowModel() {
        TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
        TableDataRow<Integer> blankset = new TableDataRow<Integer>(-1,
                                                                   new StringObject(""));
        model.add(blankset);
        return model;
    }

    private TableDataModel<TableDataRow<Integer>> getDropdownModelForRsltGrp(int rsltGrp) {
        TableDataModel<TableDataRow<Integer>> rmodel = null;
        TableDataModel<TableDataRow<Integer>> ddmodel = new TableDataModel<TableDataRow<Integer>>();
        TableDataRow<Integer> rset = null;
        TableDataRow<Integer> ddset = null;        

        if (resultPanel.getTabBar().getSelectedTab() != rsltGrp - 1)
            rmodel = resultModelCollection.get(rsltGrp - 1);
        else
            rmodel = resultWidget.model.getData();

        ddmodel.add(new TableDataRow<Integer>(-1, new StringObject("")));

        for (int i = 0; i < rmodel.size(); i++) {
            rset = rmodel.get(i);
            
            ddset = new TableDataRow<Integer>(rset.key,
                                              new StringObject((String)rset.cells[2].getValue()));
            ddmodel.add(ddset);
        }
        return ddmodel;
    }    

    private void setErrorToReflexFields(String error, Integer id, int col) {
        TableDataRow<Integer> rset = null;
        DropDownField field = null;
        for (int i = 0; i < reflexTestWidget.model.getData().size(); i++) {
            rset = reflexTestWidget.model.getRow(i);
            field = (DropDownField)rset.cells[col];

            if (id.equals(field.getSelectedKey())) {
                if (!field.getErrors().contains(error)) {
                    reflexTestWidget.model.setCellError(i, col, error);
                }
            }
        }
    }

    /**
     * This method is responsible for whenever a table row in 
     * gets edited, the change is reflected if necessary in the table for reflexive tests  
     */
    private void checkAndAddNewResultValue(String value,TableDataRow<Integer> set) {
        int selTab = resultPanel.getTabBar().getSelectedTab();
        Integer id = null;        
        TableDataModel<TableDataRow<Integer>> ddModel = null;
        List<Integer> lset = resGroupAnalyteIdMap.get(new Integer(selTab + 1));
        TableDataRow<Integer> addset = null;
        String prevVal = null;

        if (lset != null) {
            for (int i = 0; i < lset.size(); i++) {
                id = lset.get(i);
                ddModel = (TableDataModel)resultDropdownModelMap.get(id);
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
            }         
        } 
    }

    private void disableResultOptions(TableDataRow<Integer> set) {
        int selTab = resultPanel.getTabBar().getSelectedTab();
        IntegerObject obj = null;
        
        if (set.key != null) {            
            IntegerObject akobj = new IntegerObject(set.key);
            TableDataModel<TableDataRow<Integer>> ddModel = null;
            TableDataRow<Integer> lset = (TableDataRow<Integer>)resGroupAnalyteIdMap.get(new Integer(selTab + 1).toString());
            TableDataRow<Integer> addset = null;

            if (lset != null) {
                for (int i = 0; i < lset.size(); i++) {
                    obj = (IntegerObject)lset.cells[i];
                    ddModel = (TableDataModel)resultDropdownModelMap.get((obj.getValue()).toString());
                    addset = ModelUtil.getRowByKey(ddModel, akobj);
                    if (addset != null)
                        addset.enabled = false;
                }
            }
        }
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

    private int getNumRowsHavingUnit(Integer unitId) {
        int numRows = 0;
        TableDataRow<Integer> set = null;
        TableDataModel<TableDataRow<Integer>> model = sampleTypeWidget.model.getData();
        DropDownField field = null;
        for (int i = 0; i < model.size(); i++) {
            set = model.get(i);
            field = (DropDownField)set.cells[1];
            if (unitId.equals(field.getSelectedKey())) {
                numRows++;
            }
        }
        return numRows;
    }

    private boolean fieldBlank(DropDownField field) {
        if (field.getSelectedKey() == null || (Integer)field.getSelectedKey() == -1) {
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

        if (unitDropdownModel != null) {
            if (ModelUtil.getRowByKey(unitDropdownModel, id) == null) {
                addSet = true;
            }
        } else {
            addSet = true;
            unitDropdownModel = new TableDataModel<TableDataRow<Integer>>();
        }
        if (addSet) {
            str = new StringObject(text);
            ddset = new TableDataRow<Integer>(id, str);
            unitDropdownModel.add(ddset);
            setTableDropdownModel(resultWidget, 0, unitDropdownModel);
        }
    }
    

    private void flipSignsInAnalyteDropDown() {
        TableDataRow set = null;
        Integer id = null;
        if (testAnalyteDropdownModel != null && testAnalyteDropdownModel.size() > 1) {
            for (int i = 1; i < testAnalyteDropdownModel.size(); i++) {
                set = (TableDataRow)testAnalyteDropdownModel.get(i);
                id = (Integer)set.key;
                set.key = id * -2;
                if (id < tempAnaId)
                    tempAnaId = id;
            }
        }
    }

    private void flipSignsInModelMaps() {
        Map.Entry<Integer, TableDataModel<TableDataRow<Integer>>> ddmentry = null;
        Map.Entry<Integer, List<Integer>> grpentry = null;
        Iterator<Map.Entry<Integer, TableDataModel<TableDataRow<Integer>>>> ddmiter = null;
        Iterator<Map.Entry<Integer, List<Integer>>> grpiter = null;
        TableDataRow<Integer> set = null;
        Integer akey = null;
        Integer rkey = null;
        Integer entryKey = null;        
        TableDataModel<TableDataRow<Integer>> model = null;
        List<Integer> list = null;
        IntegerObject anaObj = null;       
        HashMap map = new HashMap();
        
        if (resultDropdownModelMap != null && resultDropdownModelMap.size() > 0) {
            ddmiter = (Iterator<Map.Entry<Integer, TableDataModel<TableDataRow<Integer>>>>)resultDropdownModelMap.entrySet()
                                                                                     .iterator();                        
            while (ddmiter.hasNext()) {
                ddmentry = (Map.Entry<Integer, TableDataModel<TableDataRow<Integer>>>)ddmiter.next();
                entryKey = ddmentry.getKey();                
                if (entryKey != -1) {
                    entryKey *= -2;

                    model = (TableDataModel)ddmentry.getValue();

                    for (int i = 1; i < model.size(); i++) {
                        set = model.get(i);
                        rkey = set.key;
                        rkey *= -2;
                        set.key = rkey;

                        if (tempResId < rkey)
                            tempResId = rkey;
                    }                   
                    map.put(entryKey, model);
                }
            }

            resultDropdownModelMap = map;

            if (resGroupAnalyteIdMap != null) {
                grpiter = (Iterator<Map.Entry<Integer, List<Integer>>>)resGroupAnalyteIdMap.entrySet()
                                                                                       .iterator();
               while (grpiter.hasNext()) {
                grpentry = (Map.Entry<Integer, List<Integer>>)grpiter.next();                
                list = (List<Integer>)grpentry.getValue();                
                for (int i = 0; i < list.size(); i++) {
                    list.set(i,(list.get(i)*-2));                    
                }
              } 
            }

        }
    }

    private boolean dataInWorksheetForm() {
        WorksheetForm wsForm = form.worksheet;
        Integer key = (Integer)wsForm.formatId.getSelectedKey();
        if (wsForm.worksheetTable.getValue().size() == 0 && (key == null || key == -1)
            && (wsForm.batchCapacity == null)
            && (wsForm.totalCapacity == null))
            return false;
        else
            return true;
    }

    private void deleteWorksheetAnalytes(Integer analyteId) {
        TableDataModel model = wsAnalyteWidget.model.getData();
        TableDataRow set = null;
        IntegerField data = null;        
        for (int iter = 0; iter < model.size(); iter++) {
            set = (TableDataRow<Integer>)model.get(iter);
            data = (IntegerField)set.getData();            
            if (data != null && analyteId.equals(data.getValue()))
                model.delete(iter);
        }
        wsAnalyteWidget.model.refresh();
    }

    private void addWorksheetAnalyte(Integer analyteId, String analyteName) {
        if (!analytePresent(analyteId)) {
            TableDataModel<TableDataRow<Integer>> model = wsAnalyteWidget.model.getData();
            TableDataRow<Integer> set = model.createNewSet();
            set.setData(new IntegerField(analyteId));
            set.cells[0].setValue(analyteName);
            model.add(set);
            wsAnalyteWidget.model.refresh();
        }
    }

    private boolean analytePresent(Integer analyteId) {
        TableDataRow row = null;
        IntegerField data = null;
        for (int i = 0; i < wsAnalyteWidget.model.getData().size(); i++) {
            row = wsAnalyteWidget.model.getData().get(i);            
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

        for (int i = 0; i < analyteTreeController.model.getData().size(); i++) {
            item = analyteTreeController.model.getData().get(i);
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
            for (int j = 0; j < wsAnalyteWidget.model.getData().size(); j++) {
                row = wsAnalyteWidget.model.getRow(j);
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
        resultGroupDropdownModel.clear();
        resultGroupDropdownModel.add(new TableDataRow<Integer>(-1,new StringObject("")));
        setTableDropdownModel(analyteTreeController,4,"analyte",
                              resultGroupDropdownModel);                    
    }
    
    private void resetUnitDropDownModel() {
      if(unitDropdownModel != null)   
        unitDropdownModel.clear();
      else
        unitDropdownModel = new TableDataModel<TableDataRow<Integer>>();
      
      unitDropdownModel.add(new TableDataRow<Integer>(-1,new StringObject("")));
      setTableDropdownModel(resultWidget,0, unitDropdownModel);    
    }
    
    private void resetTestAnalyteDropDownModel() {
        if(testAnalyteDropdownModel != null)   
            testAnalyteDropdownModel.clear();
        else
            testAnalyteDropdownModel = new TableDataModel<TableDataRow<Integer>>();
        
        testAnalyteDropdownModel.add(new TableDataRow<Integer>(-1,new StringObject("")));
        setTableDropdownModel(reflexTestWidget,1, testAnalyteDropdownModel); 
        
      }
      
    private void resetTestResultDropDownModel() {
        TableDropdown resdd  = (TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget();
        TableDataModel model = new TableDataModel<TableDataRow<Integer>>();
        model.add(new TableDataRow<Integer>(-1,new StringObject("")));
        setTableDropdownModel(reflexTestWidget,2, model); 
    }
        
}