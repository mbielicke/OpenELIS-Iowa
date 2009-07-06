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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
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
import org.openelis.gwt.screen.ScreenTabPanel;
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class TestScreen extends OpenELISScreenForm<TestForm, Query<TableDataRow<Integer>>> implements
                                                                                          ClickListener,
                                                                                          TabListener,
                                                                                          TableManager,
                                                                                          TableWidgetListener,
                                                                                          TreeManager,
                                                                                          TreeWidgetListener,
                                                                                          ChangeListener{

    private TableDataModel<TableDataRow<Integer>> testAnalyteModel, sampleTypeUnitModel,
                                                  resultUnitModel, resultGroupModel,
                                                  testResultDefaultModel;

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
    
    private ScreenTabPanel screenTestTab;

    private ArrayList<TableDataModel<TableDataRow<Integer>>> resultTableModelCollection,
                                                             resultDropdownModelCollection;

    private int tempAnaId,tempResId; 

    private static String panelString = "<VerticalPanel/>";
    
    private Dropdown revisionMethod, sortingMethod, reportingMethod, 
                     testFormat, wsFormat;  
    
    private boolean unitListChanged, analyteListChanged;        

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
        ArrayList cache;
        TableDataModel<TableDataRow> model;

        atozTable = (ResultsTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);

        bpanel = (ButtonPanel)getWidget("buttons");
        
        testTabPanel = (TabPanel)getWidget("testTabPanel");
        resultPanel = (TabPanel)getWidget("resultTabPanel");
                       
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
        
        screenTestTab = (ScreenTabPanel)widgets.get("testTabPanel");
 
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
        testFormat = (Dropdown)getWidget(TestMeta.getTestFormatId());                
        wsFormat = (Dropdown)getWidget(TestMeta.getTestWorksheet().getFormatId());        

        //
        // set the model for each column. Note that we have to do it twice:
        // once for the normal table and once for query table.
        // see setTableDropdownModel(TableWidget widget,int column,DataModel
        // model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeTableWidget = (TableWidget)s.getWidget();
        sampleTypeTableWidget.addTableWidgetListener(this);
        
        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestTableWidget = (TableWidget)s.getWidget();

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestTableWidget = (TableWidget)s.getWidget();
        reflexTestTableWidget.addTableWidgetListener(this);
        
        setTableDropdownModel(reflexTestTableWidget,2,getSingleRowModel());

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemTableWidget = (TableWidget)s.getWidget();       
        wsItemTableWidget.addTableWidgetListener(this);
        
        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionTableWidget = (TableWidget)s.getWidget();
        sectionTableWidget.addTableWidgetListener(this);

        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultTableWidget = (TableWidget)s.getWidget();
        resultTableWidget.addTableWidgetListener(this);
        
        s = (ScreenTableWidget)widgets.get("worksheetAnalyteTable");
        wsAnalyteTableWidget = (TableWidget)s.getWidget();        

        wsAnalyteTableWidget.addTableWidgetListener(this);

        resultGroupModel = new TableDataModel<TableDataRow<Integer>>();
        
        resultGroupModel.setDefaultSet(new TableDataRow<Integer>(null,new StringObject("")));
        
        setTableDropdownModel(analyteTreeWidget,4,"analyte",resultGroupModel);

        analyteTreeWidget.model.manager = this;
        analyteTreeWidget.enabled(false);
        analyteTreeWidget.addTreeWidgetListener(this);

        // override the callbacks
        fetchChain.add(0, beforeFetch);
        updateChain.add(0, beforeFetch);
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);        
        abortChain.add(0, beforeFetch);

        form.testAnalyte.analyteTree.setValue(analyteTreeWidget.model.getData());
        
        //
        // the submit method puts the TreeDataModel from the TreeWidget in the
        // tree field from the rpc
        //        
        analyteTree.submit(form.testAnalyte.analyteTree);        
        
        testResultDefaultModel = (TableDataModel<TableDataRow<Integer>>)resultTableWidget.model.getData().clone();
        form.testAnalyte.defaultResultModel = testResultDefaultModel;
        super.afterDraw(success);

        analyteTree.enable(true);     
                
        cache = DictionaryCache.getListByCategorySystemName("test_revision_method");
        model = getDictionaryIdEntryList(cache);
        revisionMethod.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_reflex_flags");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)reflexTestTableWidget.columns.get(3).getColumnWidget()).setModel(model);

        cache = DictionaryCache.getListByCategorySystemName("test_reporting_method");
        model = getDictionaryIdEntryList(cache);
        reportingMethod.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_analyte_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)analyteTreeWidget.columns.get(1).getColumnWidget("analyte")).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_result_flags");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)resultTableWidget.columns.get(6).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_result_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)resultTableWidget.columns.get(1).getColumnWidget()).setModel(model);          
        
        cache = DictionaryCache.getListByCategorySystemName("rounding_method");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)resultTableWidget.columns.get(8).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        model = getDictionaryIdEntryList(cache);
        setUnitsOfMeasureModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("type_of_sample");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)sampleTypeTableWidget.columns.get(0).getColumnWidget()).setModel(model);
        sampleTypeUnitModel.clone();
        
        cache = DictionaryCache.getListByCategorySystemName("test_section_flags");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)sectionTableWidget.columns.get(1).getColumnWidget()).setModel(model);        
        
        cache = DictionaryCache.getListByCategorySystemName("test_format");
        model = getDictionaryIdEntryList(cache);
        testFormat.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_sorting_method");
        model = getDictionaryIdEntryList(cache);
        sortingMethod.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_worksheet_analyte_flags");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)wsAnalyteTableWidget.columns.get(3).getColumnWidget()).setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("test_worksheet_format");
        model = getDictionaryIdEntryList(cache);
        wsFormat.setModel(model);

        cache = SectionCache.getSectionList();
        model = getSectionList(cache);
        ((TableDropdown)sectionTableWidget.columns.get(0).getColumnWidget()).setModel(model);
                              
        cache = DictionaryCache.getListByCategorySystemName("test_worksheet_item_type");
        model = getDictionaryIdEntryList(cache);
        ((TableDropdown)wsItemTableWidget.columns.get(1).getColumnWidget()).setModel(model); 
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
        ArrayList<TableDataRow<Integer>> selectedRows;
        if (action == KeyListManager.Action.FETCH) {                    
            form.entityKey = (Integer)((Object[])obj)[0];
            setTableDropdownModel(reflexTestTableWidget,2,getSingleRowModel());
            //TODO make sure to use this with drag and drop as well
            analyteListChanged = false;                       
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
        } else if(action == DictionaryEntryPickerScreen.Action.COMMIT) {              
            selectedRows = (ArrayList<TableDataRow<Integer>>)obj;
            addResultRows(selectedRows);
        }
        super.performCommand(action, obj);
    }
    
    public boolean canPerformCommand(Enum action, Object obj) {
        if(action == DictionaryEntryPickerScreen.Action.COMMIT || 
                        action == DictionaryEntryPickerScreen.Action.ABORT)
            return true;
        else
            return super.canPerformCommand(action, obj);
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

    private SyncCallback<TestForm> beforeFetch = new SyncCallback<TestForm>() {
        public void onSuccess(TestForm result) {            
            loadListsAndModels(result);
        }        
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }
    };
    
    public void query() {
        super.query();        
        enableTableAutoAdd(false);
        setActiveRow();
        setTableDropdownModel(resultTableWidget, 0, sampleTypeUnitModel);
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
        resetResultGroupDropDownModel();
        resetTestAnalyteDropDownModel();
        resetUnitDropDownModel();
        resetTestResultDropDownModel();
        resultPanel.clear();
        resultTableModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();
        resultDropdownModelCollection = new ArrayList<TableDataModel<TableDataRow<Integer>>>();        

        //TODO use this with drag and drop as well 
        analyteListChanged = true;
        
        enableTableAutoAdd(true);
        setActiveRow();
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
        
        //TODO use this with drag and drop as well 
        analyteListChanged = false;               
        super.abort();       
    }

    protected SyncCallback<TestForm> afterUpdate = new SyncCallback<TestForm>() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(TestForm result) {            
            //
            // disable anything that is not editable and set focus to the widget
            // which should get the first focus for editing
            //
            enableTableAutoAdd(true);
            setActiveRow();
            testId.enable(false);
            testName.setFocus(true);
                        
            //
            // we set this flag to true to force the unit dropdown in resultTableWidget to
            // be reloaded with the list of units specific to the current test 
            //
            unitListChanged = true;            
            reloadTestResultUnitDropdownModel();                                           
            //TODO use this with drag and drop as well 
            analyteListChanged = true;
            
        }
    };   

    protected SyncCallback<TestForm> commitUpdateCallback = new SyncCallback<TestForm>() {
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

    protected SyncCallback<TestForm> commitAddCallback = new SyncCallback<TestForm>() {
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

    protected void submitForm() {
        super.submitForm();

        if (!(state == State.QUERY)) {
            form.testAnalyte.resultTableModelCollection = resultTableModelCollection;

            resultTableWidget.finishEditing();
            sectionTableWidget.finishEditing();
            reflexTestTableWidget.finishEditing();
        }
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {            
        TableDataModel<TableDataRow<Integer>> model;                
        if (sender == resultPanel) {
            //
            // when a user clicks a tab on the test results panel (resultPanel)
            // to see the data for that result group, the model for the table
            // displayed under it, is taken from the list of models
            // (resultModelCollection) and set to the table
            // 
            if (resultTableModelCollection!=null && resultTableModelCollection.size() > tabIndex) {
                    model = resultTableModelCollection.get(tabIndex);
                    resultTableWidget.model.load(model);                    
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
                    //
                    // this is done here again because screenTestTab.getSelectedTabKey()
                    // doesn't return the right key for any tab after the 3rd one 
                    // because the 3rd tab has another TabPanel inside it which makes
                    // an unwanted tab get added to screenTestTab's tabList                    
                    //
                    form.testTabPanel = "prepAndReflexTab";
                    if(!form.prepAndReflex.load) {                          
                        fillPrepTestsReflexTests();
                    }                        
                    reloadTestAnalyteDropdownModel();
                    resultTableWidget.finishEditing();
                } else if (tabIndex == 4) { 
                    //
                    // this is done here again because screenTestTab.getSelectedTabKey()
                    // doesn't return the right key for any tab after the 3rd one 
                    // because the 3rd tab has another TabPanel inside it which makes
                    // an unwanted tab get added to screenTestTab's tabList                     
                    //
                    form.testTabPanel = "worksheetTab";
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
            Window.alert(consts.get("anaTypeAndNameRequired"));
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
        DropDownField<Integer> ddField;
        if (widget == wsItemTableWidget) {
            return canAutoAddAfterWsItemRow(addRow);           
        } else if (widget == reflexTestTableWidget) {
            if (isReflexRowEmpty(addRow))
                return false;            
        } else if (widget == resultTableWidget) {
            ddField = (DropDownField<Integer>)addRow.cells[1];
            if(ddField.getSelectedKey() == null)
                return false;            
        } else if(widget == sectionTableWidget) {
            if (isSectionRowEmpty(addRow))
                return false;            
        } else if (widget == prepTestTableWidget) {
            ddField = (DropDownField<Integer>)addRow.cells[0];
            if(ddField.getSelectedKey() == null)
                return false;            
        } else if (widget == sampleTypeTableWidget) {
            ddField = (DropDownField<Integer>)addRow.cells[0];
            if(ddField.getSelectedKey() == null)
                return false;            
        }
        
        return true;
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
        
        if (state == State.UPDATE || state == State.ADD) {
            // column 2 is for the Results dropdown
            if (widget == reflexTestTableWidget && col == 2 && set !=null) {
                ddfield = (DropDownField<Integer>)set.cells[1];
                //
                // checks to see if the analyte list has at least one option in it
                // 
                if(ddfield.getValue() != null && ddfield.getValue().size() > 0)
                    vset = ddfield.getValue().get(0);
                                                 
                    if (vset != null && ddfield.getErrors().size() == 0 &&
                                    vset.key != null) {
                         ddset = ModelUtil.getRowByKey(testAnalyteModel,vset.key);
                         iobj = (IntegerObject)ddset.getData();
                         if(iobj!=null) {
                             rg = iobj.getValue();                          
                             setTestResultsForResultGroup(rg, set);
                         }                                                  
                    }   else {
                        Window.alert(consts.get("selectAnaBeforeRes"));
                        return false;
                    }

            } else if (widget == resultTableWidget) {
                if(resultPanel.getTabBar().getTabCount() == 0) {
                    Window.alert(consts.get("atleastOneResGrp"));
                    return false;
                }                
            }                            
            return true;
        } else if(state == State.QUERY) {
            if((widget == resultTableWidget && col == 2) ||
                            (widget == reflexTestTableWidget && (col==1 || col==2))||
                            (widget == wsAnalyteTableWidget)) {
                return false;
            }
            return true;
        }
        
        return false;   
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
            if(state == State.QUERY && (widget == wsAnalyteTableWidget))
                 return false;                  
            return true;
        }
        return false;
    }

    /**
     * This function is called whenever a given cell in a table looses focus,
     * which signifies that it is no longer being edited
     */
    public void finishedEditing(SourcesTableWidgetEvents sender,int row, int col) {     
        String systemName;
        Integer selValue;
        if (sender == sectionTableWidget && row < sectionTableWidget.model.getData().size()&& col == 1) {
            
            selValue = (Integer)((DropDownField)sectionTableWidget.model.getRow(row).cells[col]).getSelectedKey();                                      
            // 
            // This code is for finding out which option was chosen in the "Options"
            // column of the Test Section table in the Test Details tab, so that
            // the values for the other rows can be changed accordingly
            //
            systemName = getSelectedSystemName(row,1,sectionTableWidget);            
            if(systemName != null) {
                if ("test_section_default".equals(systemName)) {
                    for (int iter = 0; iter < sectionTableWidget.model.numRows(); iter++) {
                        if (iter != row) {
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
            
        } else if (sender == resultTableWidget && row < resultTableWidget.model.getData()
                        .size() && state != State.QUERY) {
            final String value = ((StringField)resultTableWidget.model.getRow(row).cells[2]).getValue();
            if (col == 2 && !"".equals(value.trim())) {
                final int currRow = row;
                systemName = getSelectedSystemName(row,1,resultTableWidget);
                //              
                // This code is for finding out which option was chosen in the 
                // "Type" column of the Test Results table in the "Analyte" tab, 
                // so that error checking or formatting can be done for the value 
                // set in the "Value" column
                //
                if(systemName != null) {
                    Double darray[] = new Double[2];                    
                    String finalValue = "";
                    if ("test_res_type_dictionary".equals(systemName)) {
                        //
                        // Find out if this value is stored in the database if
                        // the type chosen was "Dictionary"
                        //
                        TestGeneralPurposeRPC tsrpc = new TestGeneralPurposeRPC();
                        tsrpc.stringValue = value;
                        screenService.call("getEntryIdForEntryText",tsrpc,
                                           new SyncCallback<TestGeneralPurposeRPC>() {
                                               public void onSuccess(TestGeneralPurposeRPC result) {
                                                   //
                                                   // If this value is not stored in the
                                                   // database then add error to this
                                                   // cell in the "Value" column
                                                   //
                                                   if (result.key == null) {
                                                       resultTableWidget.model.setCellError(currRow,2,
                                                                                            consts.get("illegalDictEntryException"));
                                                   } else {
                                                       TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);
                                                       if (set.key == null) {
                                                           set.key = getNextTempResId();
                                                       }
                                                       resultTableWidget.model.setCell(currRow,2,
                                                                                       (result.stringValue));

                                                       checkAndAddNewResultValue(value,set);
                                                   }
                                               }

                                               public void onFailure(Throwable caught) {
                                                   Window.alert(caught.getMessage());
                                                   window.clearStatus();
                                               }
                                           });

                    } else if ("test_res_type_numeric".equals(systemName)) {
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
                                    darray[iter] = doubleVal;
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
                            if (darray[0].toString().indexOf(".") == -1) {
                                finalValue = darray[0].toString() + ".0" + ",";
                            } else {
                                finalValue = darray[0].toString() + ",";
                            }

                            if (darray[1].toString().indexOf(".") == -1) {
                                finalValue += darray[1].toString() + ".0";
                            } else {
                                finalValue += darray[1].toString();
                            }
                            resultTableWidget.model.setCell(currRow,2,finalValue);
                            TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);

                            if (set.key == null)
                                set.key = getNextTempResId();

                            checkAndAddNewResultValue(finalValue, set);

                        } else {
                            resultTableWidget.model.setCellError(currRow,
                                                                 2,
                                                                 consts.get("illegalNumericFormatException"));
                        }

                    } else if ("test_res_type_titer".equals(systemName)) {
                        //
                        // Get the string that was entered if the type chosen
                        // was "Titer" and try to
                        // break it up at the ":" if it follows the pattern
                        // "number:number"
                        //
                        String[] strList = value.split(":");
                        boolean convert = false;
                        if (strList.length == 2) {
                            for (int iter = 0; iter < strList.length; iter++) {
                                String token = strList[iter];
                                try {
                                    //
                                    // Convert each number obtained from the
                                    // string and store its value converted to 
                                    // int if it's a valid number, into an array
                                    //
                                    Integer.parseInt(token);                                    
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
                            resultTableWidget.model.setCell(currRow,2,value);
                            TableDataRow<Integer> set = resultTableWidget.model.getRow(currRow);

                            if (set.key == null)
                                set.key = getNextTempResId();

                            checkAndAddNewResultValue(value, set);

                        } else {
                            resultTableWidget.model.setCellError(currRow,
                                                                 2,
                                                                 consts.get("illegalTiterFormatException"));
                        }

                    } else if ("test_res_type_date".equals(systemName)) {
                        resultTableWidget.model.setCell(currRow, 2, value);
                        TableDataRow set = (TableDataRow)resultTableWidget.model.getData()
                                                                                .get(currRow);
                        try {
                            finalValue = validateDate(value);
                            resultTableWidget.model.setCell(currRow,
                                                            2,
                                                            finalValue);

                            if (set.key == null)
                                set.key = getNextTempResId();

                            checkAndAddNewResultValue(finalValue, set);

                        } catch (IllegalArgumentException ex) {
                            resultTableWidget.model.setCellError(currRow,
                                                                 2,
                                                                 consts.get("illegalDateValueException"));
                        }
                    } else if ("test_res_type_date_time".equals(systemName)) {
                        resultTableWidget.model.setCell(currRow, 2, value);
                        TableDataRow set = (TableDataRow)resultTableWidget.model.getData()
                                                                                .get(currRow);
                        try {
                            finalValue = validateDateTime(value);
                            resultTableWidget.model.setCell(currRow,
                                                            2,
                                                            finalValue);

                            if (set.key == null)
                                set.key = getNextTempResId();

                            checkAndAddNewResultValue(finalValue, set);

                        } catch (IllegalArgumentException ex) {
                            resultTableWidget.model.setCellError(currRow,
                                                                 2,
                                                                 consts.get("illegalDateTimeValueException"));
                        }
                    } else if ("test_res_type_time".equals(systemName)) {
                        resultTableWidget.model.setCell(currRow, 2, value);
                        TableDataRow set = (TableDataRow)resultTableWidget.model.getData()
                                                                                .get(currRow);
                        try {
                            finalValue = validateTime(value);
                            resultTableWidget.model.setCell(currRow,
                                                            2,
                                                            finalValue);

                            if (set.key == null)
                                set.key = getNextTempResId();

                            checkAndAddNewResultValue(finalValue, set);

                        } catch (IllegalArgumentException ex) {
                            resultTableWidget.model.setCellError(currRow,
                                                                 2,
                                                                 consts.get("illegalTimeValueException"));
                        }
                    }
             }   
            }
        } else if (sender == wsItemTableWidget && row < wsItemTableWidget.model.getData()
                                                                     .size()) {            
            if (col == 0 && row < wsItemTableWidget.model.getData().size()) {                      
                systemName = getSelectedSystemName(row,1,wsItemTableWidget);                
                if(systemName!=null) {
                    Integer value = (Integer)(wsItemTableWidget.model.getRow(row).cells[0]).getValue();
                    if ("pos_duplicate".equals(systemName) || "pos_fixed".equals(systemName)) {
                        if (value == null) {
                            wsItemTableWidget.model.setCellError(row,0,
                                                                 consts.get("fixedDuplicatePosException"));
                        }    
                    } else if (!("pos_duplicate".equals(systemName) && !("pos_fixed".equals(systemName)))) {
                        if (value != null) {
                            wsItemTableWidget.model.setCellError(row,0,
                                                                 consts.get("posSpecifiedException"));
                        }
                    }
                }       
            }
        } else if (sender == reflexTestTableWidget && row < reflexTestTableWidget.model.getData()
                                                                             .size()) {
            TableDataRow<Integer> rset,vset;
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
                if (vset != null) {
                     iobj = (IntegerObject)vset.getData(); 
                     if(iobj!=null) {
                         rg = iobj.getValue();
                         setTestResultsForResultGroup(rg, rset);
                     }
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
    
    
    /**
     * This function adds new rows to the Results table with the values for the
     * "Value" column being set to the entries that were chosen through the
     * dictionary entry lookup screen
     */
    private void addResultRows(ArrayList<TableDataRow<Integer>> selectedRows) {
        List<String> entries;
        TableDataRow<Integer> row,set,dictSet;
        String entry;    
        Integer key;
                  
        key = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
        if (selectedRows != null) {
          if (resultPanel.getTabBar().getTabCount() == 0) {
                Window.alert(consts.get("atleastOneResGrp"));
                return;
          }
            dictSet = new TableDataRow<Integer>(key); 
            entries = new ArrayList<String>();
            for (int iter = 0; iter < selectedRows.size(); iter++) {
                set = selectedRows.get(iter);
                entry = (String)(set.cells[0]).getValue();
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
            loadListsAndModels(result);
            refreshTempIds();
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
    private void setTestResultsForResultGroup(int resultGroup,TableDataRow<Integer> set) {
        TableDataModel<TableDataRow<Integer>> model;
        DropDownField<Integer> field;
        TableDataRow<Integer> prevSet;  
        Integer key;               
        
        if(resultDropdownModelCollection != null && resultDropdownModelCollection.size() > resultGroup-1) {            
            model = (TableDataModel<TableDataRow<Integer>>)resultDropdownModelCollection.get(resultGroup-1);
            field = (DropDownField<Integer>)set.cells[2];                       
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
    
    private void setUnitsOfMeasureModel(TableDataModel<TableDataRow> units) {
        ((TableDropdown)sampleTypeTableWidget.columns.get(1).getColumnWidget()).setModel(units);
        ((TableDropdown)resultTableWidget.columns.get(0).getColumnWidget()).setModel(units);
        sampleTypeUnitModel = (TableDataModel<TableDataRow<Integer>>)units.clone();
        resultUnitModel = (TableDataModel<TableDataRow<Integer>>)units.clone();
    }
    
    private void setSectionsModel(TableDataModel<TableDataRow<Integer>> sections) {
        ((TableDropdown)sectionTableWidget.columns.get(0).getColumnWidget()).setModel(sections);        
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
                                   loadListsAndModels(form);
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
                                   loadListsAndModels(form);
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
                                   loadListsAndModels(form);
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
                                   loadListsAndModels(form);
                                   load(form.testAnalyte);                                               
                                   window.clearStatus();
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

        resultPanel.add(getDummyPanel(), new Integer(group).toString());        
        resultTableModelCollection.add((TableDataModel<TableDataRow<Integer>>)testResultDefaultModel.clone());
        resultDropdownModelCollection.add(getDefaultResultDropdownModel());
        resultPanel.selectTab(group - 1);

        ddset = new TableDataRow<Integer>(group,new StringObject((new Integer(group)).toString()));

        resultGroupModel.add(ddset);

        setTableDropdownModel(analyteTreeWidget,4,"analyte",resultGroupModel);
    }

    private void onAddRowButtonClicked() {
        TreeDataItem newItem = analyteTreeWidget.model.createTreeItem("analyte");
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
        ScreenWindow modal;
        DictionaryEntryPickerScreen pickerScreen;
        
        pickerScreen = new DictionaryEntryPickerScreen();       
        modal = new ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen","Loading...",true,false);
        pickerScreen.addCommandListener(this);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(pickerScreen);
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
          //  model.refresh();
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
        String error = consts.get("fieldRequiredException");
        if (field.getSelectedKey() == null && !field.getErrors().contains(error)) {
            someError = true;                        
        }

        field = (DropDownField)item.cells[1];
        if (field.getSelectedKey() == null && !field.getErrors().contains(error)) {;
            someError = true;
        }

        return someError;
    }

    private void refreshTempIds() {
        TableDataRow<Integer> set;
        Integer id;
        TableDataModel<TableDataRow<Integer>> model;
        int i,j;
        if (testAnalyteModel != null) {
            for (i = 1; i < testAnalyteModel.size(); i++) {
                set = testAnalyteModel.get(i);
                id = (Integer)set.key;                
                if (id < tempAnaId)
                    tempAnaId = id;
            }
        }
        
        if (resultDropdownModelCollection != null) {
            for (i = 0; i < resultDropdownModelCollection.size(); i++){
                model = resultDropdownModelCollection.get(i);
                for (j = 1; i < model.size(); i++) {
                    set = model.get(j);
                    id = (Integer)set.key;                
                    if (id < tempResId)
                        tempResId = id;
                }
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
     * This method checks to see if all the cells in a given row of sectionTableWidget
     * i.e. the argument "row" have either the blank option or no option selected
     * for them, if this is the case then it returns true otherwise false  
     */
    private boolean isSectionRowEmpty (TableDataRow<Integer> row) {
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
     * This method checks to see if a row can be auto added after the argument
     * "row" in the table for worksheet items. If both the type dropdown and 
     * qc item columns have non null and non empty values in them then the 
     * method returns true otherwise it returns false   
     */
    private boolean canAutoAddAfterWsItemRow(TableDataRow<Integer> row) {
        DropDownField<Integer> tField;
        DropDownField<String> qField;       
        
        tField = (DropDownField<Integer>)row.cells[1];
        qField = (DropDownField<String>)row.cells[2];
                    
        if(tField.getSelectedKey() == null && qField.getSelectedKey() == null) {            
                return false;            
        }
        
        return true;
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
        // it previously had some text in that column and thus was assigned a key
        //
        if (item.key != null && selText != null && !(selText.length()==0)) {
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
    
    /**
     * This function returns the system name for the option that was selected
     * in a given row, represented by the argument "row", from the dropdown in
     * column col in the TableWidget tableWidget, it returns null if no option was selected   
     */
    private String getSelectedSystemName(int row,int col, TableWidget tableWidget){
        TableDataRow<Integer> trow,ddRow;        
        String sysname;
        Integer key;
        if(row > -1) {
            trow = tableWidget.model.getRow(row);
            key = (Integer)(((DropDownField<Integer>)trow.cells[col]).getSelectedKey());            
            sysname = DictionaryCache.getSystemNameFromId(key);
            return sysname;                                                   
        }    
        return null;
    }        
    
    private void setActiveRow() {
        sectionTableWidget.activeRow = -1;
        sampleTypeTableWidget.activeRow = -1;
        analyteTreeWidget.activeRow = -1;
        resultTableWidget.activeRow = -1;
        prepTestTableWidget.activeRow = -1;
        reflexTestTableWidget.activeRow = -1;
        wsItemTableWidget.activeRow = -1;
        wsAnalyteTableWidget.activeRow = -1;
    }
    
    private void loadListsAndModels(TestForm form){        
        if(form.testAnalyte.resultTableModelCollection != null){
            resultTableModelCollection = form.testAnalyte.resultTableModelCollection;
            if(resultTableModelCollection.size() > 0) {                      
                form.testAnalyte.testResultsTable.setValue(resultTableModelCollection.get(0));
                refreshResGrpModelAndTab(resultTableModelCollection.size());
            } else {
                refreshResGrpModelAndTab(0);
            }            
        }
        
        if(form.testAnalyte.resultDropdownModelCollection != null) 
            resultDropdownModelCollection = form.testAnalyte.resultDropdownModelCollection;        
                               
        if(form.prepAndReflex.testAnalyteModel != null) {
            setTableDropdownModel(reflexTestTableWidget,1,form.prepAndReflex.testAnalyteModel);
            testAnalyteModel = form.prepAndReflex.testAnalyteModel;
        }
    }
    
    private void refreshResGrpModelAndTab(int rg) {
        int difference,i;
        group = rg;                                                                      
        
        if (group > 0) {
            difference = group - resultPanel.getTabBar().getTabCount();
            while (difference != 0) {
                if (difference > 0) {
                    resultPanel.add(getDummyPanel(),
                                    String.valueOf(resultPanel.getTabBar().getTabCount() + 1));
                    difference--;
                } else {
                    resultPanel.remove(resultPanel.getTabBar()
                                                  .getTabCount() - 1);
                    difference++;
                }
            }                                                                                                                            

           if (resultPanel.getTabBar().getTabCount() > 0) {                                       
               resultPanel.selectTab(0);                                      
           }
        } else {
            resultPanel.clear();
        }                                                                                                               
        
        resultGroupModel.clear();
        resultGroupModel.add(new TableDataRow<Integer>(null, new StringObject("")));        
        for (i = 0;i < group; i++) {                        
            resultGroupModel.add(new TableDataRow<Integer>(i+1,new StringObject(String.valueOf(i+1))));
        }
                             
        setTableDropdownModel(analyteTreeWidget,4,"analyte",resultGroupModel);        
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataRow<Integer> row;
        DictionaryDO dictDO;
        TableDataModel<TableDataRow> m;
        
        if(list == null)
            return null;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
    
    private TableDataModel<TableDataRow> getSectionList(ArrayList list){
        TableDataModel<TableDataRow> m;
        TableDataRow<Integer> row;
        SectionDO sectDO;
        
        if(list == null)
            return null;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            sectDO = (SectionDO)list.get(i);
            row.key = sectDO.getId();
            row.cells[0] = new StringObject(sectDO.getName());
            m.add(row);
        }
        
        return m;
    }
}