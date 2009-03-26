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

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
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
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeModel;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeWidgetEvents;
import org.openelis.gwt.widget.tree.event.TreeWidgetListener;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TestScreen extends OpenELISScreenForm<TestRPC, TestForm, Integer> implements
                                                                     ClickListener,
                                                                     TabListener,
                                                                     ChangeListener,
                                                                     TableManager,
                                                                     TableWidgetListener,
                                                                     TreeManager,
                                                                     TreeWidgetListener,
                                                                     PopupListener{

    private DataModel resultGroupDropdownModel,
                    testAnalyteDropdownModel, defaultModel, unitDropdownModel;

    private AppButton removeSampleTypeButton, removePrepTestButton,
                    addRowButton, deleteButton, groupAnalytesButton,
                    ungroupAnalytesButton, removeReflexTestButton,
                    removeWSItemButton, removeTestSectionButton,
                    dictionaryLookUpButton, removeTestResultButton,
                    addResultTabButton;

    private TreeWidget analyteTreeController = null;

    private int group = 0;

    private TableWidget reflexTestWidget, prepTestWidget,
                    wsItemWidget, sampleTypeWidget, sectionWidget,
                    resultWidget,wsAnalyteWidget;

    private DataMap resultDropdownModelMap, resGroupAnalyteIdMap,
                    unitIdNumResMap;

    private TestAnalyteForm analyteRPC;

    private TestMetaMap TestMeta = new TestMetaMap();

    private KeyListManager keyList = new KeyListManager();

    private ScreenTextBox testId;
    private TextBox testName;

    private TabPanel resultPanel;
    // private ScrollableTabBar resultPanel;
    
    private MenuItem dupItem;
    
    private DataSet<Integer> blankSet = new DataSet<Integer>(-1, new StringObject(""));

    private ArrayList resultModelCollection;

    private int prevSelTabIndex = -1, tempAnaId = -1, tempResId = -1;

    private static String panelString = "<VerticalPanel/>"; 
    
    private ArrayList<DataSet<Object>> selectedRows = null; 
    
    private ScreenWindow pickerWindow = null; 

    public TestScreen() {
        super("org.openelis.modules.test.server.TestService");
        forms.put("display", new TestForm());
        getScreen(new TestRPC());
    }

    public void afterDraw(boolean success) {

        ButtonPanel bpanel, atozButtons;
        CommandChain chain;
        TabPanel testTabPanel;
        AToZTable atozTable;
        QueryTable q;
        ScreenTableWidget s;
        ScreenTreeWidget analyteTree;
        ScreenMenuPanel duplicatePanel;

        atozTable = (AToZTable)getWidget("azTable");
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

        //
        // loading the models of all the dropdowns
        //        
        loadDropdownModel(TestMeta.getLabelId());
        loadDropdownModel(TestMeta.getTestTrailerId());
        loadDropdownModel(TestMeta.getScriptletId());
        loadDropdownModel(TestMeta.getRevisionMethodId());
        loadDropdownModel(TestMeta.getTestFormatId());
        loadDropdownModel(TestMeta.getSortingMethodId());
        loadDropdownModel(TestMeta.getReportingMethodId());
        loadDropdownModel(TestMeta.getTestWorksheet().getScriptletId());
        loadDropdownModel(TestMeta.getTestWorksheet().getFormatId());

        //
        // set the model for each column. Note that we have to do it twice:
        // once for the normal table and once for query table.
        // see setTableDropdownModel(TableWidget widget,int column,DataModel
        // model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(sampleTypeWidget,
                               0,
                               TestMeta.getTestTypeOfSample()
                                       .getTypeOfSampleId());
        loadTableDropdownModel(q, 0, TestMeta.getTestTypeOfSample().getTypeOfSampleId());
        loadTableDropdownModel(sampleTypeWidget,
                               1,
                               TestMeta.getTestTypeOfSample()
                                       .getUnitOfMeasureId());
        loadTableDropdownModel(q, 1, TestMeta.getTestTypeOfSample().getUnitOfMeasureId());
        sampleTypeWidget.addTableWidgetListener(this);

        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(prepTestWidget, 0, TestMeta.getTestPrep().getPrepTestId());
        loadTableDropdownModel(q, 0, TestMeta.getTestPrep().getPrepTestId());

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestWidget = (TableWidget)s.getWidget();
        reflexTestWidget.addTableWidgetListener(this);
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(reflexTestWidget, 0, TestMeta.getTestPrep()
                                                            .getPrepTestId());
        loadTableDropdownModel(q, 0, TestMeta.getTestPrep().getPrepTestId());
        loadTableDropdownModel(reflexTestWidget, 3, TestMeta.getTestReflex()
                                                            .getFlagsId());
        loadTableDropdownModel(q, 3, TestMeta.getTestReflex().getFlagsId());

        ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(wsItemWidget, 1, TestMeta.getTestWorksheetItem()
                                                        .getTypeId());
        loadTableDropdownModel(q, 1, TestMeta.getTestWorksheetItem().getTypeId());

        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionWidget = (TableWidget)s.getWidget();
        sectionWidget.addTableWidgetListener(this);
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(sectionWidget, 0, TestMeta.getTestSection()
                                                         .getSectionId());
        loadTableDropdownModel(q, 0, TestMeta.getTestSection().getSectionId());
        loadTableDropdownModel(sectionWidget, 1, TestMeta.getTestSection()
                                                         .getFlagId());
        loadTableDropdownModel(q, 1, TestMeta.getTestSection().getFlagId());

        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        resultWidget.addTableWidgetListener(this);

        loadTableDropdownModel(resultWidget, 1, TestMeta.getTestResult()
                                                        .getTypeId());
        //loadTableDropdownModel(q, 1, TestMeta.getTestResult().getTypeId());
        loadTableDropdownModel(resultWidget, 6, TestMeta.getTestResult().getFlagsId());
        //loadTableDropdownModel(q, 6, TestMeta.getTestResult().getFlagsId());
        loadTableDropdownModel(resultWidget, 8, TestMeta.getTestResult()
                                                        .getRoundingMethodId());
        //loadTableDropdownModel(q, 8, TestMeta.getTestResult().getRoundingMethodId());

        s = (ScreenTableWidget)widgets.get("worksheetAnalyteTable");        
        wsAnalyteWidget = (TableWidget)s.getWidget(); 
        
        loadTableDropdownModel(wsAnalyteWidget, 3, TestMeta.getTestWorksheetAnalyte().getFlagId());
        loadTableDropdownModel(q, 3, TestMeta.getTestWorksheetAnalyte().getFlagId());
        
        wsAnalyteWidget.addTableWidgetListener(this);
            
        //
        // set dropdown models for column 1 and 3
        //               
        loadTableDropdownModel(analyteTreeController,1,"analyte",
                               TestMeta.getTestAnalyte().getTypeId());
        loadTableDropdownModel(analyteTreeController,3,"analyte",
                               TestMeta.getTestAnalyte().getScriptletId());

        resultGroupDropdownModel = new DataModel();
        resultGroupDropdownModel.setDefaultSet(new DataSet());
        setTableDropdownModel(analyteTreeController,4,"analyte",resultGroupDropdownModel);

        analyteTreeController.model.manager = this;
        analyteTreeController.enabled(false);
        analyteTreeController.addTreeWidgetListener(this);

        // override the callbacks
        updateChain.add(afterUpdate);
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);

        super.afterDraw(success);

        analyteTree.enable(true);

        //
        // Below, each TreeField or TableField from the rpc 
        // is assigned the data model of the TreeWidget or TableWidget that it represents. 
        // This is done in order to make sure that the  defaultSet
        // variable in the default data model of the field is initialiazed and is not null.
        // So that when the first time data is fetched for that field and on the
        // server side a new DataSet is created from the model in the field to
        // represent a new row in the table on the screen, a NullPointerException is not thrown.
        // As, creating a new dataset from the model involves the use of the
        // defaultSet for that model.
        //
        rpc.form.sampleType.sampleTypeTable.setValue(sampleTypeWidget.model.getData());

        rpc.form.worksheet.worksheetTable.setValue(wsItemWidget.model.getData());
        
        rpc.form.worksheet.worksheetAnalyteTable.setValue(wsAnalyteWidget.model.getData());

        rpc.form.prepAndReflex.testPrepTable.setValue(prepTestWidget.model.getData());

        rpc.form.prepAndReflex.testReflexTable.setValue(reflexTestWidget.model.getData());        

        rpc.form.details.sectionTable.setValue(sectionWidget.model.getData());        

        analyteRPC = rpc.form.testAnalyte;

        resultModelCollection = new ArrayList<DataModel<DataSet>>();

        //
        // the submit method puts the TreeDataModel from the TreeWidget in the
        // tree field from the rpc
        //
        analyteTree.submit(analyteRPC.analyteTree);

        analyteRPC.setFieldValue("testResultsTable",
                                 resultWidget.model.getData());        

        defaultModel = (DataModel)resultWidget.model.getData().clone();                
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
        Form tdrpc = null;
        if (action == KeyListManager.Action.FETCH) {
            key = (Integer)((Object[])obj)[0];
            //
            // See fillTestAnalyteDropDown(), fillTestResultDropDown(),
            // fillMaps(), fillResultModelCollection()
            // 
            ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());
            fillTestAnalyteDropDown();
            fillMaps();
            fillResultGroupDropdown();
            fillResultModelCollection();
            fillUnitDropdownModel();
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
            analyteRPC = rpc.form.testAnalyte;
            tdrpc = rpc.form.details;
            
            if (tdrpc.load || state == State.ADD) {
                if (sectionWidget.model.getData().size() == 0) {
                    Window.alert(consts.get("atleastOneSection"));
                    return;
                }
            }
            if (analyteRPC.load || state == State.ADD) {                
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
            if(!validate()) {
                window.setStatus(consts.get("correctErrors"),"ErrorPanel");
                return;
            }
            if(!dataInWorksheetForm()) {
                rpc.form.worksheet.load = false;
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
        else if(sender instanceof MenuItem){
            if("duplicateRecord".equals(((String)((MenuItem)sender).objClass))){
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
        resultPanel.addTabListener(this);
        removeSampleTypeButton.changeState(ButtonState.DISABLED);
        removePrepTestButton.changeState(ButtonState.DISABLED);
        removeReflexTestButton.changeState(ButtonState.DISABLED);
        removeWSItemButton.changeState(ButtonState.DISABLED);
        deleteButton.changeState(ButtonState.DISABLED);
        addRowButton.changeState(ButtonState.DISABLED);
        removeTestResultButton.changeState(ButtonState.DISABLED);
        dictionaryLookUpButton.changeState(ButtonState.DISABLED);
        addResultTabButton.changeState(ButtonState.DISABLED);
        groupAnalytesButton.changeState(ButtonState.DISABLED);
        ungroupAnalytesButton.changeState(ButtonState.DISABLED);
    }

    public void add() {
        super.add();
        group = 0;
        dupItem.enable(false);
        resultGroupDropdownModel.clear();
        if (testAnalyteDropdownModel != null)
            testAnalyteDropdownModel.clear();
        if (unitDropdownModel != null) {
            unitDropdownModel.clear();
        }
        resultPanel.clear();
        // resultPanel.clearTabs();
        resultModelCollection = new ArrayList<DataModel<DataSet>>();
        prevSelTabIndex = -1;
        resultDropdownModelMap = new DataMap();
        resGroupAnalyteIdMap = new DataMap();
        unitIdNumResMap = new DataMap();

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
            //
            // disable anything that is not editable and set focus to the widget
            // which should get the first focus for editing
            //
            enableTableAutoAdd(true);
            testId.enable(false);
            testName.setFocus(true);
            dupItem.enable(false);
            
            fillTestAnalyteDropDown();
            fillMaps();
            fillResultGroupDropdown();
            fillResultModelCollection();
            fillUnitDropdownModel();
            
            fillPrepTestsReflexTests();
            fillWorksheetLayout();

            prevSelTabIndex = -1;
        }
    };

    protected AsyncCallback<TestRPC> commitUpdateCallback = new AsyncCallback<TestRPC>() {
        public void onSuccess(TestRPC result) {
            if (rpc.form.status == Form.Status.invalid) {
                analyteRPC = (TestAnalyteForm)(rpc.form.getField("testAnalyte"));

                if (analyteRPC.resultModelCollection != null)
                    resultModelCollection = analyteRPC.resultModelCollection;
            } else {
                enableTableAutoAdd(false);
            }
        }

        public void onFailure(Throwable caught) {
            handleError(caught);
        }
    };

    protected AsyncCallback<TestRPC> commitAddCallback = new AsyncCallback<TestRPC>() {
        public void onSuccess(TestRPC result) {
            if (rpc.form.status == Form.Status.invalid) {
                analyteRPC = (TestAnalyteForm)(rpc.form.getField("testAnalyte"));               
                if (analyteRPC.resultModelCollection != null)
                    resultModelCollection = analyteRPC.resultModelCollection;
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
                resultModelCollection.set(0, resultWidget.model.getData());
            }

            rpc.form.testAnalyte.resultModelCollection = resultModelCollection;

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
        DataModel model = null;
        ArrayList list = null;
        DataSet mr = null;
        DataSet dr = null;

        AbstractField mf = null;
        AbstractField df = null;

        if (sender == resultPanel) {
            if (tabIndex != 0 && prevSelTabIndex == -1)
                prevSelTabIndex = 0;
            //
            // when a user clicks a tab on the test results panel (resultPanel)
            // to see the data for that result group, the model for the table
            // displayed under it, is taken from the list of models
            // (resultModelCollection)
            // and set to the table
            // 
            if (resultModelCollection.size() > tabIndex) {
                if (prevSelTabIndex > -1 && prevSelTabIndex != tabIndex) {
                    resultWidget.finishEditing();
                    model = (DataModel)resultWidget.model.getData().clone();
                    list = resultWidget.model.getData().getDeletions();

                    for (int i = 0; i < list.size(); i++) {
                        model.getDeletions().add(((DataSet)list.get(i)).clone());
                    }

                    for (int i = 0; i < model.size(); i++) {
                        dr = (DataSet)resultWidget.model.getData().get(i);
                        mr = (DataSet)model.get(i);

                        for (int j = 0; j < dr.size(); j++) {
                            mf = (AbstractField)mr.get(j);
                            df = (AbstractField)dr.get(j);

                            for (int k = 0; k < df.getErrors().size(); k++) {
                                mf.addError((String)df.getErrors().get(k));
                            }
                        }
                    }
                    resultModelCollection.set(prevSelTabIndex, model);
                }

                if (!(prevSelTabIndex == -1 && tabIndex == 0)) {
                    model = (DataModel)resultModelCollection.get(tabIndex);
                    resultWidget.model.load(model);
                    prevSelTabIndex = tabIndex;
                }
            }
        } else {
            if (state != FormInt.State.QUERY) {
                if (tabIndex == 0 && !((Form)rpc.form.getField("details")).load) {
                    fillTestDetails();
                } else if (tabIndex == 1 && !((Form)rpc.form.getField("sampleType")).load) {
                    fillSampleTypes();
                } else if (tabIndex == 2 && !((Form)rpc.form.getField("testAnalyte")).load) {
                    fillTestAnalyte();                                      
                } else if (tabIndex == 3) {
                    if (!((Form)rpc.form.getField("prepAndReflex")).load)
                        fillPrepTestsReflexTests();
                    analyteTreeController.finishEditing();
                    resultWidget.finishEditing();
                } else if (tabIndex == 4 && !((Form)rpc.form.getField("worksheet")).load) {
                    fillWorksheetLayout();
                }
            }
        }
        return true;
    }

    public boolean validate() {                       
        if(treeItemsHaveErrors()) {
            return false;
        }        
        
        if(resultRowsHaveErrors()) {                 
            return false;
        }
        
        if(worksheetItemRowsHaveErrors()) {
            return false;
        }
       return true; 
    }

    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        if (widget == wsItemWidget)
            return ((DataObject)addRow.get(0)).getValue() != null;
        else if (widget == reflexTestWidget) {
            if ((((DataObject)addRow.get(0)).getValue() != null && !((DataObject)addRow.get(0)).getValue()
                                                                                               .equals(-1)) 
                                                               || (((DataObject)addRow.get(1)).getValue() != null && !((DataObject)addRow.get(1)).getValue()
                                                                                                                                                                                              .equals(-1)))
                return true;
        } else if (widget == resultWidget) {
            return ((DataObject)addRow.get(1)).getValue() != null && !((DataObject)addRow.get(1)).getValue()
                                                                                                 .equals(-1);
        }

        return ((DataObject)addRow.get(0)).getValue() != null && !((DataObject)addRow.get(0)).getValue()
                                                                                             .equals(-1);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        DropDownField ddfield = null;
        DataSet vset = null;
        Integer id = null;

        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
            // column 2 is for the Results dropdown
            if (widget == reflexTestWidget && col == 2) {
                ddfield = (DropDownField)set.get(1);
                vset = (DataSet)((ArrayList)ddfield.getValue()).get(0);
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

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        return false;
    }

    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           DataSet dropTarget,
                           int targetRow) {
        return false;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     DataSet dropTarget,
                     int targetRow) {

    }

    public void drop(TableWidget widget, Widget dragWidget) {

    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           Widget dropWidget) {
        return false;
    }

    /**
     * This function is called whenever a given cell in a table looses focus,
     * which signifies that it is no longer being edited
     */
    public void finishedEditing(SourcesTableWidgetEvents sender,
                                int row,
                                int col) {
        TestGeneralPurposeRPC tsrpc = new TestGeneralPurposeRPC();
        if (sender == sectionWidget && row < sectionWidget.model.getData()
                                                                .size()
            && col == 1) {
            final int currRow = row;
            final Integer selValue = (Integer)((DropDownField)sectionWidget.model.getRow(row)
                                                                                 .get(col)).getSelectedKey();            
            tsrpc.key = selValue;
            // 
            // This code is to find out which option was chosen in the "Options"
            // column of the Test Section table in the Test Details tab, so that
            // the values for the other rows can be changed accordingly
            //
            screenService.call("getCategorySystemName", tsrpc,
                                    new AsyncCallback<TestGeneralPurposeRPC>() {
                                        public void onSuccess(TestGeneralPurposeRPC result) {
                                            if ("test_section_default".equals(result.stringValue)) {
                                                for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                    if (iter != currRow) {
                                                        // 
                                                        // if the option chosen
                                                        // is "Default" for this
                                                        // row,
                                                        // then all the other
                                                        // rows must be set to
                                                        // the blank option
                                                        // 
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
                                                        field.setValue(new DataSet<Integer>(-1));
                                                    }
                                                }
                                                sectionWidget.model.refresh();
                                            } else {
                                                if (selValue.intValue() != -1) {
                                                    for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
                                                        //
                                                        // if the option chosen
                                                        // is "Ask" or "Match
                                                        // User Location" for
                                                        // this row,
                                                        // then all the other
                                                        // rows must be set to
                                                        // the same option which
                                                        // is this one
                                                        // 
                                                        field.setValue(new DataSet<Integer>(selValue));
                                                    }
                                                }
                                                sectionWidget.model.refresh();
                                            }
                                        }

                                        public void onFailure(Throwable caught) {
                                            Window.alert(caught.getMessage());
                                            window.setStatus("", "");
                                        }
                                    });

        } else if (sender == resultWidget && row < resultWidget.model.getData()
                                                                     .size()) {
            final String value = ((StringField)resultWidget.model.getRow(row)
                                                                        .get(2)).getValue();
            //Window.alert("value "+value+" row "+new Integer(row).toString());

            if (resultWidget.model.getData().size() > 0 && row < resultWidget.model.getData()
                                                                                   .size()) {
                DataSet set = resultWidget.model.getData().get(row);
                DataMap data = (DataMap)set.getData();
                Integer rg = null;
                if (data == null) {
                    data = new DataMap();
                    set.setData(data);
                }
            }
            if (col == 2 && !"".equals(value.trim())) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)resultWidget.model.getRow(row)
                                                                                    .get(1)).getSelectedKey();
                
                tsrpc.key = selValue;

                //              
                // This code is for finding out which option was chosen in the
                // "Type"
                // column of the Test Results table in the "Analyte" tab, so
                // that error checking
                // or formatting can be done for the value set in the "Value"
                // column
                //
                screenService.call("getCategorySystemName",tsrpc,
                                        new AsyncCallback<TestGeneralPurposeRPC>() {
                                            public void onSuccess(TestGeneralPurposeRPC result) {
                                                Double[] darray = new Double[2];
                                                String finalValue = "";
                                                if ("test_res_type_dictionary".equals(result.stringValue)) {
                                                    //
                                                    // Find out if this value is
                                                    // stored in the database if
                                                    // the type chosen was
                                                    // "Dictionary"
                                                    //
                                                    TestGeneralPurposeRPC tsrpc1 = new TestGeneralPurposeRPC();
                                                    tsrpc1.stringValue = value;
                                                    screenService.call("getEntryIdForEntryText",
                                                                       tsrpc1,
                                                                            new AsyncCallback<TestGeneralPurposeRPC>() {
                                                                                public void onSuccess(TestGeneralPurposeRPC result1) {
                                                                                    //
                                                                                    // If this value is not stored in the database
                                                                                    // then add error to this cell in the "Value"
                                                                                    // column
                                                                                    if (result1.key == null) {
                                                                                        resultWidget.model.setCellError(currRow,
                                                                                                                        2,
                                                                                                                        consts.get("illegalDictEntryException"));
                                                                                    } else {
                                                                                        DataSet set = (DataSet)resultWidget.model.getData()
                                                                                                                                 .get(currRow);
                                                                                        DataMap data = (DataMap)set.getData();
                                                                                        if (data == null) {
                                                                                            data = new DataMap();
                                                                                            set.setData(data);
                                                                                        }

                                                                                        if (data.get("id") == null) {
                                                                                            Integer tempResId = getNextTempResId();
                                                                                            data.put("id",new IntegerField(tempResId));
                                                                                        }
                                                                                        resultWidget.model.setCell(currRow,2,result1.stringValue);
                                                                                                                                                                                                                                                                                                                                            
                                                                                        checkAndAddNewResultValue(value,set);
                                                                                    }
                                                                                }

                                                                                public void onFailure(Throwable caught) {
                                                                                    Window.alert(caught.getMessage());
                                                                                    window.setStatus("",
                                                                                                     "");
                                                                                }
                                                                            });

                                                } else if ("test_res_type_numeric".equals(result.stringValue)) {
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
                                                                // Convert each number obtained from
                                                                // the string and store its value
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
                                                        DataSet set = (DataSet)resultWidget.model.getData()
                                                                                                 .get(currRow);
                                                        DataMap data = (DataMap)set.getData();
                                                        if (data == null) {                                                            
                                                            data = new DataMap();
                                                            set.setData(data);
                                                        }

                                                        if (data.get("id") == null) {                                                           
                                                            Integer tempResId = getNextTempResId();
                                                            data.put("id",
                                                                     new IntegerField(tempResId));
                                                        }

                                                        checkAndAddNewResultValue(finalValue,set);

                                                    } else {
                                                        resultWidget.model.setCellError(currRow,2,
                                                                                        consts.get("illegalNumericFormatException"));
                                                    }

                                                } else if ("test_res_type_titer".equals(result.stringValue)) {
                                                    //
                                                    // Get the string that was entered if the type
                                                    // chosen was "Titer" and try to break it up at 
                                                    // the ":" if it follows the pattern "number:number"
                                                    //
                                                    String[] strList = value.split(":");
                                                    boolean convert = false;
                                                    if (strList.length == 2) {
                                                        for (int iter = 0; iter < strList.length; iter++) {
                                                            String token = strList[iter];
                                                            try {
                                                                //
                                                                // Convert each number obtained from
                                                                // the string and store its value
                                                                // converted to double if it's a valid
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
                                                        DataSet set = (DataSet)resultWidget.model.getData()
                                                                                                 .get(currRow);
                                                        DataMap data = (DataMap)set.getData();
                                                        if (data == null) {
                                                            data = new DataMap();
                                                            set.setData(data);
                                                        }

                                                        if (data.get("id") == null) {
                                                            Integer tempResId = getNextTempResId();
                                                            data.put("id",
                                                                     new IntegerField(tempResId));
                                                        }

                                                        checkAndAddNewResultValue(finalValue,set);

                                                    } else {
                                                        resultWidget.model.setCellError(currRow,
                                                                                        2,
                                                                                        consts.get("illegalTiterFormatException"));
                                                    }

                                                }else {
                                                    resultWidget.model.setCell(currRow,2,value);   
                                                    DataSet set = (DataSet)resultWidget.model.getData().get(currRow);
                                                    DataMap data = (DataMap)set.getData();
                                                    if (data == null) {
                                                      data = new DataMap();
                                                      set.setData(data);
                                                    }

                                                    if (data.get("id") == null) {
                                                      Integer tempResId = getNextTempResId();
                                                      data.put("id",new IntegerField(tempResId));
                                                    }

                                                   checkAndAddNewResultValue(value,set);
                                                 }

                                             }

                                            public void onFailure(Throwable caught) {
                                                Window.alert(caught.getMessage());
                                                window.setStatus("", "");
                                            }
                                        });

            } else if (col == 0 && row < resultWidget.model.getData().size()) {
                DataSet set = (DataSet)resultWidget.model.getData().get(row);
                DataMap data = (DataMap)set.getData();
                IntegerObject unitId = null;
                Integer id = (Integer)((DropDownField)resultWidget.model.getRow(row)
                                                                        .get(0)).getSelectedKey();
                IntegerObject numRes = null;
                if (data == null) {
                    data = new DataMap();
                    set.setData(data);
                }

                if (data.get("unitId") == null) {
                    data.put("unitId", new IntegerObject(id));
                    if (id != -1) {
                        numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                        numRes.setValue(numRes.getValue() + 1);
                    }
                } else {
                    unitId = (IntegerObject)data.get("unitId");
                    if (unitId.getValue() != id) {
                        data.put("unitId", new IntegerObject(id));
                        numRes = (IntegerObject)unitIdNumResMap.get((unitId.getValue()).toString());
                        numRes.setValue(numRes.getValue() - 1);
                        if (id != -1) {
                            numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                            numRes.setValue(numRes.getValue() + 1);
                        }
                    }
                }
            }
        } else if (sender == wsItemWidget && row < wsItemWidget.model.getData()
                                                                     .size()) {
            if (col == 0 && row < wsItemWidget.model.getData().size()) {
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)wsItemWidget.model.getRow(row)
                                                                                    .get(1)).getSelectedKey();
                tsrpc.key = selValue;
                
                screenService.call("getCategorySystemName",tsrpc,
                                   new AsyncCallback<TestGeneralPurposeRPC>() {
                                            public void onSuccess(TestGeneralPurposeRPC result) {
                                                Integer value = (Integer)((DataObject)wsItemWidget.model.getRow(currRow)
                                                                                                        .get(0)).getValue();
                                                if ("pos_duplicate".equals(result.stringValue) || "pos_fixed".equals(result.stringValue)) {
                                                    if (value == null) {
                                                        wsItemWidget.model.setCellError(currRow,
                                                                                        0,
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
                                                window.setStatus("", "");
                                            }
                                        });
            }
        } else if (sender == reflexTestWidget && row < reflexTestWidget.model.getData()
                                                                             .size()) {
            DataSet rset = reflexTestWidget.model.getRow(row);
            DataSet vset = null;
            DropDownField ddfield = (DropDownField)rset.get(col);
            Integer analyteId = null;
            if (col == 1) {
                vset = (DataSet)((ArrayList)ddfield.getValue()).get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestWidget.model.setCellError(row,1,
                                                        consts.get("analyteDeleted"));
                }
                analyteId = (Integer)ddfield.getSelectedKey();
                setTestResultsForAnalyte(analyteId, rset);
            } else if (col == 2) {
                vset = (DataSet)((ArrayList)ddfield.getValue()).get(0);
                if (vset != null && vset.enabled == false) {
                    reflexTestWidget.model.setCellError(row,2,
                                                        consts.get("resultDeleted"));
                }
            }
        } else if (sender == sampleTypeWidget && col == 1
                   && row < sampleTypeWidget.model.getData().size()) {
            DataSet set = sampleTypeWidget.model.getRow(row);
            DropDownField ufield = (DropDownField)set.get(col);
            Integer id = (Integer)(ufield.getSelectedKey());
            DataMap data = (DataMap)set.getData();
            IntegerObject numRes = null;
            Integer unitId = null;

            if (data != null) {
                unitId = ((IntegerField)data.get("unitId")).getValue();
            } else {
                if (id != null && id != -1) {
                    data = new DataMap();
                    data.put("unitId", new IntegerField(id));
                    set.setData(data);
                    numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                    if (numRes == null) {
                        unitIdNumResMap.put(id.toString(), new IntegerObject(0));
                    }
                    addSetToUnitDropdown(ufield);
                }
            }

            if (unitId != null) {
                if (unitId != -1 && !unitId.equals(id)) {
                    numRes = (IntegerObject)unitIdNumResMap.get(unitId.toString());
                    if (numRes.getValue() > 0 && !(getNumRowsHavingUnit(unitId) >= 1)) {
                        Window.alert(consts.get("cantChangeUnit"));
                        ufield.setValue(new DataSet<Integer>(unitId));
                        sampleTypeWidget.model.refresh();
                    } else {
                        if (id != null && id != -1) {
                            numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                            if (numRes == null) {
                                unitIdNumResMap.put(id.toString(),
                                                    new IntegerObject(0));
                            }
                            addSetToUnitDropdown(ufield);
                        }
                    }
                }
            } else {
                if (id != null && id != -1) {
                    data.put("unitId", new IntegerField(id));
                    numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                    if (numRes == null) {
                        unitIdNumResMap.put(id.toString(), new IntegerObject(0));
                    }
                    addSetToUnitDropdown(ufield);
                }
            }
        } else if(sender == wsAnalyteWidget && col == 1){
            final DataSet trow =  wsAnalyteWidget.model.getRow(row);
            CheckField chf =  (CheckField)trow.get(1);           
            IntegerField rfield = (IntegerField)trow.get(2);
            TestGeneralPurposeRPC tgrpc = null;
            
            if(analyteTreeController.model.getData() == null ||
                            analyteTreeController.model.getData().size() == 0) {
                tgrpc = new TestGeneralPurposeRPC();
                tgrpc.key = rpc.key;
                tgrpc.form = rpc.form.testAnalyte;               
                screenService.call("getAnalyteTreeModel", tgrpc, new AsyncCallback<TestGeneralPurposeRPC>(){                     
                   public void onSuccess(TestGeneralPurposeRPC result) {
                     TreeField tfield = ((TestAnalyteForm)result.form).analyteTree;
                     analyteTreeController.model.setModel(tfield.getValue());
                     setAnalytesAvailable(trow);       
                     wsAnalyteWidget.model.refresh();
                   }
                    
                   public void onFailure(Throwable caught) {
                       Window.alert(caught.getMessage());
                       window.setStatus("", "");
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

        Integer anaId = null;

        TreeDataItem item = analyteTreeController.model.getRow(index);
        DropDownField field = null;

        String selText = null;
        String prevVal = null;

        DataSet<Integer> ddset = null;
        DataSet<Integer> lset = null;

        DataMap data = null;

        IntegerObject idObj = null;     
        IntegerObject anaIdObj = null;

        boolean createNewSet = false;

        if (col == 0) {
            field = (DropDownField)item.get(col);
            selText = (String)field.getTextValue();
            if (field.getSelectedKey() == null || (Integer)field.getSelectedKey() == -1) {
                if (!field.getErrors()
                          .contains(consts.get("fieldRequiredException"))) {
                    field.addError(consts.get("fieldRequiredException"));
                    reflexTestWidget.model.refresh();
                    return;
                }
            }
            if (item.getData() != null) {
                data = (DataMap)item.getData();
                if (data.get("id") == null) {
                    createNewSet = true;
                } else {
                    idObj = (IntegerObject)data.get("id");
                    anaIdObj = (IntegerObject)data.get("analyteId");
                    ddset = testAnalyteDropdownModel.getByKey(idObj);
                    if(ddset != null) {
                     prevVal = (String)ddset.get(0).getValue();
                     if (!prevVal.trim().equals(selText.trim())) {
                        setErrorToReflexFields(consts.get("analyteNameChanged"),
                                               idObj.getValue(),
                                               1);
                        ddset.get(0).setValue(selText);
                        ((TableDropdown)reflexTestWidget.columns.get(1)
                                                                .getColumnWidget()).setModel(testAnalyteDropdownModel);
                        updateWorksheetAnalyte(anaIdObj.getValue(),(Integer)field.getSelectedKey(),selText);
                        data.put("analyteId", new IntegerObject((Integer)field.getSelectedKey()));
                     }                     
                  }   
                }
            } else {
                createNewSet = true;
            }

            if (createNewSet) {
                tempAnaId = getNextTempAnaId();
                ddset = new DataSet<Integer>(tempAnaId,new StringObject(selText));

                data = new DataMap();
                data.put("id", new IntegerObject(tempAnaId));
                item.setData(data);

                if (testAnalyteDropdownModel == null)
                    testAnalyteDropdownModel = new DataModel<Integer>();

                testAnalyteDropdownModel.add(ddset);
                ((TableDropdown)reflexTestWidget.columns.get(1)
                                                        .getColumnWidget()).setModel(testAnalyteDropdownModel);
                
                addWorksheetAnalyte((Integer)field.getSelectedKey(),selText);
            }

        } else if (col == 1) {
            field = (DropDownField)item.get(col);
            if (field.getSelectedKey() == null || (Integer)field.getSelectedKey() == -1) {
                field.addError(consts.get("fieldRequiredException"));
                analyteTreeController.model.refresh();
            }
        } else if (col == 4) {
            field = (DropDownField)item.get(col);
            selText = (String)field.getTextValue();
            resultWidget.finishEditing();

            if (item.getData() != null && !("").equals(selText.trim())) {
                data = (DataMap)item.getData();
                if (data.get("id") != null) {
                    anaId = ((IntegerObject)data.get("id")).getValue();
                    lset = (DataSet)resGroupAnalyteIdMap.get(selText);
                    idObj = new IntegerObject(anaId);
                    if (lset != null) {
                        if (!lset.list.contains(idObj)) {
                            lset.add(idObj);
                        }
                    } else {
                        lset = new DataSet();
                        lset.add(idObj);
                        resGroupAnalyteIdMap.put(selText, lset);
                    }

                    resultDropdownModelMap.put(anaId.toString(),
                                               getDropdownModelForRsltGrp(Integer.parseInt(selText)));
                }
            }

        }

    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {

    }

    public void stopEditing(SourcesTreeWidgetEvents sender, int row, int col) {

    }

    /**
     * This function adds new rows to the Results table with the values
     * for the "Value" column being set to the entries that were chosen through
     * the dictionary entry lookup screen
     */
    private void addResultRows(ArrayList<DataSet<Object>> selectedRows,DataSet dictSet){
        List<String> entries = new ArrayList<String>();
        DataSet<Object> set = null;
        String entry = null;
        DataSet<Integer> row = null;
        DataMap rowdata = null;
        
        if (resultPanel.getTabBar().getTabCount() == 0) {
            Window.alert(consts.get("atleastOneResGrp"));
            return;
        }
        
        if(selectedRows != null) { 
         for(int iter = 0; iter < selectedRows.size();iter++){
            set = selectedRows.get(iter);
            entry = (String)((Field)set.get(0)).getValue();                        
            if(entry != null && !entries.contains(entry.trim())) {
             entries.add(entry);
             row = (DataSet<Integer>)resultWidget.model.createRow();            
             row.get(1).setValue(dictSet);
             row.get(2).setValue(entry);
             rowdata = new DataMap();
             row.setData(rowdata);
             rowdata.put("id", new IntegerField(getNextTempResId()));                       
             resultWidget.model.addRow(row);
             checkAndAddNewResultValue(entry, row);
            } 
        }               
        resultWidget.model.refresh();
     }  
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

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        if ("top".equals(item.leafType) || (!(state == State.ADD) && !(state == State.UPDATE)))
            return false;

        return true;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           TreeDataItem dropTarget,
                           int targetRow) {
        return true;
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
            DropDownField field = (DropDownField)item.get(4);
            Integer key = (Integer)field.getSelectedKey();
            if (key != null && key != -1) {
                resultPanel.getTabBar().selectTab(key - 1);
            }
        }
        return true;
    }

    public void drop(TreeWidget widget,
                     Widget dragWidget,
                     TreeDataItem dropTarget,
                     int targetRow) {        
    }

    public void drop(TreeWidget widget, Widget dragWidget) {

    }
    
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
      final DictionaryEntryPickerScreen pickerScreen = (DictionaryEntryPickerScreen)pickerWindow.content;
      TestGeneralPurposeRPC tgrpc = new TestGeneralPurposeRPC();
       tgrpc.stringValue = "test_res_type_dictionary";  
        screenService.call("getEntryIdForSystemName",tgrpc,
                           new AsyncCallback<TestGeneralPurposeRPC>() {
                              public void onSuccess(TestGeneralPurposeRPC result) {                                                                                                                           
                                  DataSet dictSet = new DataSet(result.key);                                   
                                  addResultRows(pickerScreen.selectedRows,dictSet);
                               }
                              public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                                window.setStatus("","");
                               }
                  });
               
    }
    
    private AsyncCallback fetchForDuplicateCallBack  = new AsyncCallback<TestRPC>() {
        public void onSuccess(TestRPC result) {
            rpc = result;
            resultModelCollection = result.form.testAnalyte.resultModelCollection;
            flipSignsInAnalyteDropDown();
            ((TableDropdown)reflexTestWidget.columns.get(1)
                            .getColumnWidget()).setModel(testAnalyteDropdownModel);
            flipSignsInModelMaps();
            loadScreen(rpc.form);                       
            enable(true);
            changeState(State.ADD);
            enableTableAutoAdd(true);
            window.setStatus(consts.get("enterInformationPressCommit"),"");
        }
        public void onFailure(Throwable caught){
            handleError(caught);
            window.setStatus("Load Failed", "");
            changeState(State.DEFAULT);
            key = null;
        }
    };

    /**
     * This method fetches the data model stored in resultDropDownModelMap with
     * the analyte's id as the key and sets the model as the model for the
     * "Results" column's dropdown in the Reflexive Tests table
     */
    private void setTestResultsForAnalyte(Integer analyteId, DataSet set) {
        if (analyteId != null) {
            DataModel model = (DataModel)resultDropdownModelMap.get(analyteId.toString());
            DropDownField field = (DropDownField)set.get(2);
            DataSet prevSet = new DataSet<Integer>((Integer)field.getSelectedKey());
            DataSet blankSet = new DataSet<Integer>(-1);

            if (model != null) {
                field.setModel(model);
                // if the new model doesn't contain the dataset that was
                // selected
                // from the previous one, then it may cause an
                // ArrayIndexOutOfBoundsException
                // when you try to edit the Results column, this code makes sure
                // that in that situation the blank set is set as the value so
                // that it becomes the
                // one that's selected and one that still belongs to the current
                // data model.
                // Otherwise the previously selected set is set as the value
                if (field.getSelectedKey() != null && model.list.contains(prevSet))
                    field.setValue(prevSet);
                else
                    field.setValue(blankSet);
            }
        }
    }

    private void getTests(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            Form form = (Form)forms.get("queryByLetter");
            form.setFieldValue(TestMeta.getName(), query);
            commitQuery(form);
        }
    }

    /**
     * This function sets the Data Model for a dropdown field on the screen
     * specified by "fieldName", that's not inside a tree or table etc, to the
     * argument "model"
     */
    private void setDropdownModel(String fieldName, DataModel model) {
        Dropdown drop = (Dropdown)getWidget(fieldName);
        drop.setModel(model);
    }

    /**
     * This function sets the Data Model for a dropdown field that's inside the
     * TableWidget "widget" in the column "column" to the argument "model"
     */
    private void setTableDropdownModel(TableWidget widget,
                                       int column,
                                       DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget()).setModel(model);
    }

    /**
     * This function sets the Data Model for a dropdown field that's inside the
     * TreeWidget "widget" in the column "column" of the node with leafType
     * "leafType" to the argument "model"
     */
    private void setTableDropdownModel(TreeWidget widget,
                                       int column,
                                       String leafType,
                                       DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget(leafType)).setModel(model);
    }

    /**
     * The function for loading the fields in "details" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Test Details"
     * tab in the tab-panel on the screen
     */
    private void fillTestDetails() {
        TestGeneralPurposeRPC drpc = null;
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");
        
        drpc = new TestGeneralPurposeRPC();
        drpc.key = key;
        drpc.form = rpc.form.details;
        
        screenService.call("loadTestDetails",drpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        load(result.form);                                        
                                        rpc.form.fields.put("details",rpc.form.details = (DetailsForm)result.form);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The function for loading the fields in the "prepAndReflex" sub-rpc
     * section as defined in the xsl file for the screen. It's mapped to the
     * "Prep Test & Reflexive Test" tab in the tab-panel on the screen
     */
    private void fillPrepTestsReflexTests() {
        TestGeneralPurposeRPC prrpc = null;
        if (key == null)
            return;

        prrpc = new TestGeneralPurposeRPC();       
        prrpc.key = key;
        prrpc.form = rpc.form.prepAndReflex;
        
        window.setStatus("", "spinnerIcon");       
        
        screenService.call("loadPrepTestsReflexTests",prrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        load(result.form);
                                        rpc.form.fields.put("prepAndReflex",rpc.form.prepAndReflex = (PrepAndReflexForm)result.form);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The function for loading the fields in "sampleType" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Sample Type"
     * tab in the tab-panel on the screen
     */
    private void fillSampleTypes() {
        TestGeneralPurposeRPC strpc = null; 
        if (key == null)
            return;

        strpc = new TestGeneralPurposeRPC();
        strpc.form = rpc.form.sampleType;
        strpc.key = key;
        
        window.setStatus("", "spinnerIcon");
       
        screenService.call("loadSampleTypes",strpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        load(result.form);
                                        rpc.form.fields.put("sampleType",rpc.form.sampleType = (SampleTypeForm)result.form);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The function for loading the fields in "worksheet" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "WorkSheet
     * Layout" tab in the tab-panel on the screen
     */
    private void fillWorksheetLayout() {
        TestGeneralPurposeRPC wsrpc = null;
        if (key == null)
            return;
        
        wsrpc = new TestGeneralPurposeRPC();
        wsrpc.key = key;
        wsrpc.form = rpc.form.worksheet;
        
        window.setStatus("", "spinnerIcon");

        screenService.call("loadWorksheetLayout",wsrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        load(result.form);
                                        rpc.form.fields.put("worksheet",rpc.form.worksheet = (WorksheetForm)result.form);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The function for loading the fields in "testAnalyte" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Analyte" tab
     * in the tab-panel on the screen
     */
    private void fillTestAnalyte() {
        TestGeneralPurposeRPC tarpc = null;       
        if (key == null)
            return;
        
        tarpc = new TestGeneralPurposeRPC();
        tarpc.key = key;
        tarpc.form = rpc.form.testAnalyte;

        window.setStatus("", "spinnerIcon");

        screenService.call("loadTestAnalyte",tarpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        load(result.form);
                                        rpc.form.fields.put("testAnalyte",rpc.form.testAnalyte = (TestAnalyteForm)result.form);
                                        /*if(resultModelCollection != null && resultModelCollection.size() > 0){                                          
                                         resultWidget.model.setModel((DataModel)resultModelCollection.get(0));                                         
                                        }*/
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });

    }

    private void fillResultModelCollection() {
        TestGeneralPurposeRPC tirpc = null; 
        
        if (key == null)
            return;

        // resultPanel.clear();
        // resultPanel.clearTabs();
        resultModelCollection = new ArrayList<DataModel>();
        
        tirpc = new TestGeneralPurposeRPC();
        tirpc.key = key;
        
        screenService.call("getGroupCountForTest",tirpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        TestGeneralPurposeRPC trgrpc = null;
                                        int difference;
                                        group = result.integerValue;                                                             
                                        if (group > 0) {                                           
                                            trgrpc = new TestGeneralPurposeRPC();                                            
                                              difference = group - resultPanel.getTabBar().getTabCount();
                                              while (difference != 0) {
                                                  if(difference > 0) {
                                                      resultPanel.add(getDummyPanel(),
                                                         new Integer(resultPanel.getTabBar().getTabCount()+1).toString());
                                                      difference--;
                                                  }else {
                                                      resultPanel.remove(resultPanel.getTabBar().getTabCount()-1);
                                                      difference++;
                                                  }
                                              }
                                                                                        
                                                trgrpc.key = key;                                                
                                                trgrpc.model = (DataModel<Integer>)defaultModel.clone();
                                                trgrpc.integerValue = group;
                                                trgrpc.form = new TestAnalyteForm();
                                               
                                                ((TestAnalyteForm)trgrpc.form).resultModelCollection = new ArrayList<DataModel<Integer>>();
                                                ((TestAnalyteForm)trgrpc.form).duplicate = false;
                                                
                                                screenService.call("loadTestResultModellist", trgrpc,
                                                                        new AsyncCallback<TestGeneralPurposeRPC>() {
                                                                            public void onSuccess(TestGeneralPurposeRPC result) {
                                                                                resultModelCollection =
                                                                                    ((TestAnalyteForm)result.form).resultModelCollection;                                                                                
                                                                            }

                                                                            public void onFailure(Throwable caught) {
                                                                                Window.alert(caught.getMessage());
                                                                                window.setStatus("",
                                                                                                 "");
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
                                        window.setStatus("", "");
                                    }
                                });

    }

    /**
     * This function loads the Analyte dropdown in the Reflexive Test table with
     * the analytes added to the test, the data for which was the most recently
     * fetched
     */
    private void fillTestAnalyteDropDown() {
        TestGeneralPurposeRPC tdmrpc = null;        
        if (key == null) {
            return;
        }

        tdmrpc = new TestGeneralPurposeRPC();
        tdmrpc.key = key;
                
        screenService.call("getTestAnalyteModel",tdmrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        ((TableDropdown)reflexTestWidget.columns.get(1)
                                                                                .getColumnWidget()).setModel(result.model);
                                        testAnalyteDropdownModel = result.model;

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    private void fillResultGroupDropdown() {
        TestGeneralPurposeRPC tdmrpc = null;   
        if (key == null) {
            return;
        }
        
        tdmrpc = new TestGeneralPurposeRPC();
        tdmrpc.key = key;
        
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
                                        window.setStatus("", "");
                                    }
                                });
    }

    private void fillUnitDropdownModel() {
        TestGeneralPurposeRPC tdmrpc = null; 
        if (key == null) {
            return;
        }
        
        tdmrpc = new TestGeneralPurposeRPC();
        tdmrpc.key = key;
       
        screenService.call("getUnitDropdownModel",tdmrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        if (result.model != null) {
                                            ((TableDropdown)resultWidget.columns.get(0)
                                                                                .getColumnWidget()).setModel(result.model);
                                            unitDropdownModel = result.model;
                                        } else {
                                            if (unitDropdownModel != null) {
                                                unitDropdownModel.clear();
                                                unitDropdownModel.add(blankSet);
                                                ((TableDropdown)resultWidget.columns.get(0)
                                                                                    .getColumnWidget()).setModel(unitDropdownModel);
                                            }
                                        }
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * This function loads the resultDropDownModelMap variable, which is a
     * DataMap with the key-value pairs, such that the keys are the ids of the
     * rows in the test_analyte table that correspond to this test and the
     * corresponding values are the lists of result values that are associated
     * with that test_analyte. This map is then used to for faster access to
     * these models, so that they don't have to fetched from the database every
     * time
     */
    private void fillMaps() {
        TestGeneralPurposeRPC imrpc = null;
        if (key == null) {
            return;
        }
        
        imrpc = new TestGeneralPurposeRPC();
        imrpc.key = key;
        
        imrpc.map = new DataMap();
        screenService.call("getTestResultModelMap",imrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        resultDropdownModelMap = result.map;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });

        imrpc.map = new DataMap();
        screenService.call("getResultGroupAnalyteMap",imrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        resGroupAnalyteIdMap = result.map;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });

        imrpc.map = new DataMap();
        screenService.call("getUnitIdNumResMapForTest",imrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        unitIdNumResMap = result.map;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The functions for handling the click events associated with the those
     * buttons on the screen which remove selected rows from particular tables
     */
    private void onSampleTypeRowButtonClick() {
        int row = sampleTypeWidget.model.getData().getSelectedIndex();
        DataSet set = sampleTypeWidget.model.getRow(row);
        DropDownField ufield = (DropDownField)set.get(1);
        Integer id = (Integer)(ufield.getSelectedKey());
        IntegerObject numRes = null;
        if (row > -1) {
            if (id != null && id != -1) {
                numRes = (IntegerObject)unitIdNumResMap.get(id.toString());
                if (numRes != null && numRes.getValue() > 0 && !(getNumRowsHavingUnit(id) >= 1)) {
                    Window.alert(consts.get("cantChangeUnit"));
                    ufield.setValue(new DataSet<Integer>(id));
                    sampleTypeWidget.model.refresh();
                } else {
                    sampleTypeWidget.model.deleteRow(row);
                }
            }
        }
    }

    private void onPrepTestRowButtonClick() {
        if (prepTestWidget.model.getData().getSelectedIndex() > -1)
            prepTestWidget.model.deleteRow(prepTestWidget.model.getData()
                                                               .getSelectedIndex());
    }

    private void onWSItemRowButtonClick() {
        if (wsItemWidget.model.getData().getSelectedIndex() > -1)
            wsItemWidget.model.deleteRow(wsItemWidget.model.getData()
                                                           .getSelectedIndex());
    }

    private void onReflexTestRowButtonClick() {
        if (reflexTestWidget.model.getData().getSelectedIndex() > -1)
            reflexTestWidget.model.deleteRow(reflexTestWidget.model.getData()
                                                                   .getSelectedIndex());
    }

    private void onTestSectionRowButtonClicked() {
        if (sectionWidget.model.getData().getSelectedIndex() > -1) {
            sectionWidget.model.deleteRow(sectionWidget.model.getData()
                                                             .getSelectedIndex());
        }
    }

    private void onTestResultRowButtonClicked() {
        DataModel model = resultWidget.model.getData();
        int selIndex = model.getSelectedIndex();
        DataSet set = null;
        IntegerField field = null;
        DataMap data = null;        

        if (selIndex > -1) {
            if (model.size() > 1) {
                set = resultWidget.model.getRow(selIndex);  
                data = (DataMap)set.getData();
                if (set.getData() != null) {
                    field = (IntegerField)((DataMap)set.getData()).get("id");
                    if (field != null) {
                        disableResultOptions(set);
                        setErrorToReflexFields(consts.get("resultDeleted"),
                                               field.getValue(),
                                               2);
                    }
                }            
                
               if(resultWidget.editingCell!=null) {
                   resultWidget.select(resultWidget.activeRow, resultWidget.activeCell);  
               }               
               resultWidget.model.deleteRow(selIndex);
               finishedEditing(resultWidget, selIndex, 2);                 
               } else {
                Window.alert(consts.get("atleastOneResInResGrp"));
            }
        }
    }

    private void onAddResultTabButtonClicked() {
        DataSet ddset = null;
        DataSet lset = null;
        group++;

        if (group == 2)
            prevSelTabIndex = 0;

        resultPanel.add(getDummyPanel(), new Integer(group).toString());
        // resultPanel.addTab(new Integer(group).toString());
        resultModelCollection.add((DataModel)defaultModel.clone());
        resultPanel.selectTab(group - 1);

        ddset = new DataSet<Integer>(group,new StringObject((new Integer(group)).toString()));

        resultGroupDropdownModel.add(ddset);

        lset = new DataSet();
        if (resGroupAnalyteIdMap == null) {
            resGroupAnalyteIdMap = new DataMap();
        }
        resGroupAnalyteIdMap.put(Integer.toString(group), lset);

        setTableDropdownModel(analyteTreeController,4,"analyte",resultGroupDropdownModel);

    }

    private void onAddRowButtonClicked() {        
        TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte");
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
        ArrayList<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList().clone();
        Collections.sort(selectedRowIndexes);
        
        TreeDataItem groupItem = analyteTreeController.model.createTreeItem("top");
        groupItem.get(0).setValue(consts.get("analyteGroup"));
        
        for (int iter = 0; iter < selectedRowIndexes.size(); iter++) {
            int index = selectedRowIndexes.get(iter);                                       
            if (iter > 0 && index != (selectedRowIndexes.get(iter-1)+1)) {
               Window.alert(consts.get("analyteNotAdjcnt"));
              return;
           }            
        }
        for(TreeDataItem item : items) {
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
                analyteTreeController.model.addRow(selectedRowIndexes.get(0), groupItem);                  
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
        ArrayList<Integer> selRows = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList().clone();

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
                        analyteTreeController.model.addRow(index+i, newItem);
                    }

                    analyteTreeController.model.deleteRow(index);
                }
            }
            analyteTreeController.model.refresh();
        }
    }

    private void onDeleteTreeItemButtonClicked() {
        List<Integer> selectedRowIndexes = (ArrayList<Integer>)analyteTreeController.model.getSelectedRowList().clone();
        int index = 0;
        TreeDataItem item = null;
        TreeDataItem parent = null;
        TreeDataModel model;
        IntegerObject idField = null;
        Integer anaId = null;

        for (int iter = 0; iter < selectedRowIndexes.size(); iter++) {
            index = selectedRowIndexes.get(iter);
            item = (TreeDataItem)analyteTreeController.model.getRow(index).clone();
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
        
        if (item.getData() != null) {
            idField = (IntegerObject)((DataMap)item.getData()).get("id");
            if (idField != null)
                setErrorToReflexFields(consts.get("analyteDeleted"),
                                       idField.getValue(),1);
            testAnalyteDropdownModel.getByKey(idField).enabled = false;
            ((TableDropdown)reflexTestWidget.columns.get(1).getColumnWidget()).setModel(testAnalyteDropdownModel);
           
            anaId = (Integer)((DropDownField)item.get(0)).getSelectedKey();
            deleteWorksheetAnalytes(anaId);
        }
      }  
        analyteTreeController.model.refresh();
    }
    
    private void onDuplicateRecordClick() {        
       rpc.numGroups = group;
       rpc.resultTableModel = defaultModel;
       screenService.call("getDuplicateRPC",rpc,fetchForDuplicateCallBack);
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
                                                     "dictionaryEntryPicker",
                                                     "Loading...");
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
        final String name = fieldName;    
        TestGeneralPurposeRPC tfnrpc = new TestGeneralPurposeRPC();
        tfnrpc.fieldName = fieldName;
        screenService.call("getInitialModel",
                                tfnrpc,
                                new AsyncCallback<TestGeneralPurposeRPC>() {
                                    public void onSuccess(TestGeneralPurposeRPC result) {
                                        setDropdownModel(result.fieldName, result.model);
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });

    }

    private void loadTableDropdownModel(TableWidget widget,
                                        int column,
                                        String fieldName) {
        final String name = fieldName;
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
                                        window.setStatus("", "");
                                    }
                                });

    }

    private void loadTableDropdownModel(TreeWidget widget,
                                        int column,
                                        String leafType,
                                        String fieldName) {
        final String name = fieldName;
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
                                        window.setStatus("", "");
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
                    selVal = (Integer)((DropDownField)chItem.get(4)).getSelectedKey();
                    if (selVal == null || unselVal.equals(selVal)) {
                        return false;
                    }
                }
            } else {
                selVal = (Integer)((DropDownField)item.get(4)).getSelectedKey();
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
      DataModel model = reflexTestWidget.model.getData();
      DataSet row = null;   
      AbstractField field = null;         
      if(model != null) {
         for(int j = 0; j < model.size(); j++) {
           row = model.get(j);
           for(int k = 0; k < row.size(); k++) {
             field = (AbstractField)row.get(k);
              if(field.getErrors().size() > 0) {
                  return true;
              }
           } 
         }
      } 
      return false;
    } 
    
    private boolean worksheetItemRowsHaveErrors() {
        DataModel model = wsItemWidget.model.getData();
        DataSet row = null;   
        AbstractField field = null;         
        if(model != null) {
           for(int j = 0; j < model.size(); j++) {
             row = model.get(j);
             for(int k = 0; k < row.size(); k++) {
               field = (AbstractField)row.get(k);
                if(field.getErrors().size() > 0) {
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

    private DataModel getSingleRowModel() {
        DataModel model = new DataModel();
        DataSet<Integer> blankset = new DataSet<Integer>(-1,new StringObject(""));
        model.add(blankset);
        return model;
    }       

    private DataModel getDropdownModelForRsltGrp(int rsltGrp) {
        DataModel rmodel = null;
        DataModel ddmodel = new DataModel();
        DataSet<Field> rset = null;
        DataSet ddset = null;

        DataMap data = null;

        IntegerField field = null;

        DataSet<Integer> blankset = new DataSet<Integer>(-1,new StringObject(""));

        if (resultPanel.getTabBar().getSelectedTab() != rsltGrp - 1)
            rmodel = (DataModel<DataSet>)resultModelCollection.get(rsltGrp - 1);
        else
            rmodel = resultWidget.model.getData();

        ddmodel.add(blankset);

        for (int i = 0; i < rmodel.size(); i++) {
            rset = (DataSet)rmodel.get(i);
            data = (DataMap)rset.getData();
            field = (IntegerField)data.get("id");
            ddset = new DataSet<Integer>(field.getValue(),new StringObject((String)rset.get(2).getValue()));
            ddmodel.add(ddset);
        }
        return ddmodel;
    }
    
    private DateField getDateField() {
        DatetimeRPC dr = new DatetimeRPC();  
        DateField df = new DateField((byte)0,(byte)2,dr);        
        return df;
    }

    private void setErrorToReflexFields(String error, Integer id, int col) {
        DataSet<Field> rset = null;
        DropDownField field = null;
        for (int i = 0; i < reflexTestWidget.model.getData().size(); i++) {
            rset = (DataSet)reflexTestWidget.model.getData().get(i);
            field = (DropDownField)rset.get(col);            

            if (id.equals(field.getSelectedKey())) {
                if (!field.getErrors().contains(error)) {
                    reflexTestWidget.model.setCellError(i, col, error);
                }
            }   
        }
    }

    private void checkAndAddNewResultValue(String value, DataSet set) {
        int selTab = resultPanel.getTabBar().getSelectedTab();
        IntegerObject obj = null;
        IntegerField field = (IntegerField)((DataMap)set.getData()).get("id");
        Integer addKey = field.getValue();
        IntegerObject akobj = new IntegerObject(addKey);
        DataModel<Integer> ddModel = null;
        DataSet<Field> lset = (DataSet)resGroupAnalyteIdMap.get(new Integer(selTab + 1).toString());
        DataSet<Integer> addset = null;
        String prevVal = null;

        if (lset != null) {
            for (int i = 0; i < lset.size(); i++) {
                obj = (IntegerObject)lset.get(i);
                ddModel = (DataModel)resultDropdownModelMap.get((obj.getValue()).toString());
                addset = ddModel.getByKey(akobj.getValue());
                if (addset != null) {
                    prevVal = (String)addset.get(0).getValue();
                    if (!prevVal.trim().equals(value.trim())) {
                        setErrorToReflexFields(consts.get("resultValueChanged"),addKey,2);
                        addset.get(0).setValue(value);
                    }
                } else {
                    addset = new DataSet(akobj.getValue(), new StringObject(value));
                    ddModel.add(addset);
                }
            }
        }
    }

    private void disableResultOptions(DataSet set) {
        int selTab = resultPanel.getTabBar().getSelectedTab();
        IntegerObject obj = null;
        IntegerField field = (IntegerField)((DataMap)set.getData()).get("id");
        if (field != null) {
            Integer addKey = field.getValue();
            IntegerObject akobj = new IntegerObject(addKey);
            DataModel ddModel = null;
            DataSet lset = (DataSet)resGroupAnalyteIdMap.get(new Integer(selTab + 1).toString());
            DataSet addset = null;

            if (lset != null) {
                for (int i = 0; i < lset.size(); i++) {
                    obj = (IntegerObject)lset.get(i);
                    ddModel = (DataModel)resultDropdownModelMap.get((obj.getValue()).toString());
                    addset = ddModel.getByKey(akobj);
                    if (addset != null)
                        addset.enabled = false;
                }
            }
        }
    }

    private boolean setErrorToItem(TreeDataItem item) {
        boolean someError = false;
        DropDownField field = (DropDownField)item.get(0);
        if (fieldBlank(field) && !field.getErrors()
                                       .contains(consts.get("fieldRequiredException"))) {
            field.addError(consts.get("fieldRequiredException"));
            field.valid = false;
            someError = true;
        }

        field = (DropDownField)item.get(1);
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
        DataSet set = null;
        DataModel<Field> model = (DataModel<Field>)sampleTypeWidget.model.getData();
        DropDownField field = null;
        for (int i = 0; i < model.size(); i++) {
            set = model.get(i);
            field = (DropDownField)set.get(1);
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
        DataSet ddset = null;
        StringObject str = null;
        String text = (String)ufield.getTextValue();
        Integer id = (Integer)(ufield.getSelectedKey());

        if (unitDropdownModel != null) {
            if (unitDropdownModel.getByKey(id) == null) {
                addSet = true;
            }
        } else {
            addSet = true;
            unitDropdownModel = new DataModel<Integer>();
        }
        if (addSet) {
            str = new StringObject(text);
            ddset = new DataSet<Integer>(id, str);
            unitDropdownModel.add(ddset);
            setTableDropdownModel(resultWidget, 0, unitDropdownModel);
        }
    }      
    
    private void flipSignsInAnalyteDropDown() {
        DataSet set = null;        
        Integer id = null;
        if (testAnalyteDropdownModel != null && testAnalyteDropdownModel.size() > 1) {
         for(int i = 1; i < testAnalyteDropdownModel.size(); i++) {
          set = (DataSet)testAnalyteDropdownModel.get(i);
          id = (Integer)set.getKey(); 
          set.setKey(id * (-2));
          if(id < tempAnaId)
              tempAnaId = id;
          }                
       }
    }
    
    private void flipSignsInModelMaps() {
     Map.Entry<String, FieldType> entry = null;
     Iterator<Map.Entry<String, FieldType>> iterator = null;
     DataSet set = null;
     Integer akey = null;
     Integer rkey = null;
     String entryKey = null; 
     DataModel<Integer> model = null;
     IntegerObject anaObj = null;    
     ArrayList<String> keyList = null;
     DataMap map = null;
     
     if(resultDropdownModelMap != null && resultDropdownModelMap.size() > 0) {
       iterator = (Iterator<Map.Entry<String, FieldType>>)resultDropdownModelMap.entrySet().iterator();
       keyList = new ArrayList<String>();
       map = new DataMap(); 
       
       while (iterator.hasNext()) {
        entry = (Map.Entry<String, FieldType>)iterator.next();
        entryKey = entry.getKey();
        akey = Integer.parseInt(entryKey);
        if(akey != -1) { 
         akey *= -2;        
        
         model = (DataModel)entry.getValue();
         
         for(int i = 1; i < model.size(); i++) {
          set = model.get(i); 
          rkey = ((Integer)set.getKey());
          rkey *= -2;
          set.setKey(rkey);         
         
          if(tempResId < rkey)
           tempResId = rkey;                  
         }
         keyList.add(entryKey);
                                   
         map.put(akey.toString(), model);         
        } 
       }
       
       resultDropdownModelMap = map;
      
       if(resGroupAnalyteIdMap != null) {
        iterator = (Iterator<Map.Entry<String,FieldType>>)resGroupAnalyteIdMap.entrySet().iterator(); 
        entry = (Map.Entry<String, FieldType>)iterator.next();
        entryKey = entry.getKey();
        set = (DataSet)entry.getValue();
        for(int i = 0 ; i < set.size(); i++) {
         anaObj = (IntegerObject)set.get(i);
         anaObj.setValue(anaObj.getValue() * -2);                     
        }            
       }       
      
     }
    }    
    
    private boolean dataInWorksheetForm() {
      WorksheetForm wsForm = rpc.form.worksheet;
      Integer key = (Integer)wsForm.formatId.getSelectedKey();
      if(wsForm.worksheetTable.getValue().size() == 0 && (key == null || key == -1) 
          && (wsForm.batchCapacity == null) &&  (wsForm.totalCapacity == null)) 
       return false;           
      else 
       return true;
    }

    private void deleteWorksheetAnalytes(Integer analyteId) {
        DataModel model = wsAnalyteWidget.model.getData();
        DataSet set = null;
        DataMap data = null;
        IntegerField anaId = null;
        for(int iter = 0; iter < model.size(); iter++) {
           set = (DataSet)model.get(iter);
           data = (DataMap)set.getData();
           if(data != null) 
              anaId = (IntegerField)data.get("analyteId"); 
           if(anaId != null && analyteId.equals(anaId.getValue()))
              model.delete(iter); 
        }        
        wsAnalyteWidget.model.refresh();        
    } 
    
    private void addWorksheetAnalyte(Integer analyteId,String analyteName) {
      if(!analytePresent(analyteId)) {
        DataModel model = wsAnalyteWidget.model.getData();
        DataSet set = model.createNewSet();
        DataMap data = new DataMap();
        data.put("analyteId", new IntegerField(analyteId));
        set.setData(data);
        set.get(0).setValue(analyteName);    
        model.add(set);        
        wsAnalyteWidget.model.refresh();
       } 
    }
    
    private boolean analytePresent(Integer analyteId) {
       DataSet row = null;
       DataMap data = null;
       IntegerField anaField = null;
       for(int i = 0; i < wsAnalyteWidget.model.getData().size(); i++) {
          row = wsAnalyteWidget.model.getData().get(i);
          data = (DataMap)row.getData();
          anaField = (IntegerField)data.get("analyteId");
          if(analyteId.equals(anaField.getValue())) {
              return true;
          }
       }
       return false;
    }
    
    private void updateWorksheetAnalyte(Integer oldId,Integer newId,String analyteName) {
      deleteWorksheetAnalytes(oldId);
      addWorksheetAnalyte(newId,analyteName);
    }
    
    private void setAnalytesAvailable(DataSet trow) {       
        StringField strf =  (StringField)trow.get(0);
        String anaName = strf.getValue().trim();
        String selText = null;
        TreeDataItem item = null;
        DropDownField anaField = null;
        CheckField chf =  (CheckField)trow.get(1);
        
        for(int i = 0; i < analyteTreeController.model.getData().size(); i++) {
            item = analyteTreeController.model.getData().get(i);
            if("top".equals(item.leafType)) {
                for(TreeDataItem citem:item.getItems()) {
                  anaField = (DropDownField)citem.get(0);
                  selText = ((String)anaField.getTextValue()).trim();
                  if(anaName.equals(selText)){
                    setAnalyteAvailable(item.getItems(),chf.isChecked());  
                    break;                                  
                  } 
                }
            }
        }  
    }
    
    private void setAnalyteAvailable(List<TreeDataItem> items,boolean available) {       
       String tanaName = null;
       String ianaName = null;
       TreeDataItem item = null;
       DataSet row = null;
       
      for(int i=0;  i < items.size(); i++) {
        item = items.get(i);  
        ianaName = ((String)((DropDownField)item.get(0)).getTextValue()).trim();
        for(int j = 0; j < wsAnalyteWidget.model.getData().size(); j++) {
          row = wsAnalyteWidget.model.getData().get(j);
          tanaName = ((String)((StringField)row.get(0)).getValue()).trim();          
         
           if(tanaName.equals(ianaName)) {
              if(available) {
               ((CheckField)row.get(1)).setValue("Y");
               window.setStatus(consts.get("additionalAnalytesAdded"), ""); 
              } 
              else {
               ((CheckField)row.get(1)).setValue(null);
               window.setStatus(consts.get("otherAnalytesRemoved"), ""); 
              } 
            setRepeatValue(row);
           }
        }
       }
    }
    
    private void setRepeatValue(DataSet row) {
        CheckField chf = (CheckField)row.get(1);
        IntegerField rfield = (IntegerField) row.get(2);
        
        if(chf.isChecked() && (rfield.getValue() == null || rfield.getValue() == 0)) 
            rfield.setValue(new Integer(1));
    }

}