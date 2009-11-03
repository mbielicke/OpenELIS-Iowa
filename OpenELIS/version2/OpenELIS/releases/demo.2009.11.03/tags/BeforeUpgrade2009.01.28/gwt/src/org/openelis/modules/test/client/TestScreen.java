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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.screen.CommandChain;
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
import org.openelis.gwt.widget.AppButton.ButtonState;
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class TestScreen extends OpenELISScreenForm implements
                                                  ClickListener,
                                                  TabListener,
                                                  ChangeListener,
                                                  TableManager,
                                                  TableWidgetListener,
                                                  TreeManager,
                                                  TreeWidgetListener{   

    private DataModel resultGroupDropdownModel, testAnalyteDropdownModel,defaultModel;

    private AppButton removeSampleTypeButton, removePrepTestButton,
                      addRowButton,deleteButton,
                      groupAnalytesButton,ungroupAnalytesButton,
                      removeReflexTestButton, removeWSItemButton, 
                      removeTestSectionButton, dictionaryLookUpButton,
                      removeTestResultButton, addResultTabButton;                      
                          

    private TreeWidget analyteTreeController = null;

    private int gid = 0;

    private int group = 0;

    private TableWidget reflexTestWidget, prepTestWidget, wsItemWidget,
                        sampleTypeWidget, sectionWidget, resultWidget;
    
    private TableField reflexTableField;
    
    private DataMap resultDropdownModelMap, resGroupAnalyteIdMap;
    
    private FormRPC analyteRPC;

    private TestMetaMap TestMeta = new TestMetaMap();
    
    private KeyListManager keyList = new KeyListManager();

    private ScreenTextBox testId;
    private TextBox testName;

    private TabPanel resultPanel;   
    //private ScrollableTabBar resultPanel;

    private ArrayList<DataModel> resultModelCollection;   

    private int prevSelTabIndex = -1,tempAnaId = -1,tempResId = -1;
             
    private static String panelString = "<VerticalPanel/>";       

    public TestScreen() {
        super("org.openelis.modules.test.server.TestService", true);
    }
    
    public void afterDraw(boolean success) {

        ButtonPanel bpanel, atozButtons;                     
        CommandChain chain;                        
        TabPanel testTabPanel;
        AToZTable atozTable;
        QueryTable q;
        ScreenTableWidget s;
        ScreenTreeWidget analyteTree;
        
        atozTable = (AToZTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        bpanel = (ButtonPanel)getWidget("buttons");       
        bpanel.enableButton("delete", false);

        testTabPanel = (TabPanel)getWidget("testTabPanel");
        resultPanel = (TabPanel)getWidget("resultTabPanel");
        
        tempAnaId = -1;
        tempResId = -1;
        
        //
        // this is done to remove an unwanted tab that gets added to 
        // testTabPanel, for some reason, when you put a tab panel inside one
        // of its tabs  
        //
        testTabPanel.remove(2);
        
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
        //loadDropdownModel(TestMeta.getMethodId());
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
        // see setTableDropdownModel(TableWidget widget,int column,DataModel model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(sampleTypeWidget, 0, TestMeta.getTestTypeOfSample().getTypeOfSampleId());
        loadTableDropdownModel(q, 0, TestMeta.getTestTypeOfSample().getTypeOfSampleId());     
        loadTableDropdownModel(sampleTypeWidget, 1, TestMeta.getTestTypeOfSample().getUnitOfMeasureId());
        loadTableDropdownModel(q, 1, TestMeta.getTestTypeOfSample().getUnitOfMeasureId());

        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        loadTableDropdownModel(prepTestWidget,0,TestMeta.getTestPrep().getPrepTestId());
        loadTableDropdownModel(q,0,TestMeta.getTestPrep().getPrepTestId());

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestWidget = (TableWidget)s.getWidget();
        reflexTestWidget.addTableWidgetListener(this);
        q = (QueryTable)s.getQueryWidget().getWidget();         
        loadTableDropdownModel(reflexTestWidget,0,TestMeta.getTestPrep().getPrepTestId());
        loadTableDropdownModel(q,0,TestMeta.getTestPrep().getPrepTestId());
        loadTableDropdownModel(reflexTestWidget,3,TestMeta.getTestReflex().getFlagsId());
        loadTableDropdownModel(q,3,TestMeta.getTestReflex().getFlagsId());
        
        ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();          
        loadTableDropdownModel(wsItemWidget,1,TestMeta.getTestWorksheetItem().getTypeId());
        loadTableDropdownModel(q,1,TestMeta.getTestWorksheetItem().getTypeId());

        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionWidget = (TableWidget)s.getWidget();        
        sectionWidget.addTableWidgetListener(this);
        q = (QueryTable)s.getQueryWidget().getWidget();            
        loadTableDropdownModel(sectionWidget,0,TestMeta.getTestSection().getSectionId());  
        loadTableDropdownModel(q,0,TestMeta.getTestSection().getSectionId());
        loadTableDropdownModel(sectionWidget,1,TestMeta.getTestSection().getFlagId());
        loadTableDropdownModel(q,1,TestMeta.getTestSection().getFlagId());
                
        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        resultWidget.addTableWidgetListener(this);                    
        
        DataModel umodel = new DataModel();        
        umodel.add(new NumberObject(-1), new StringObject(""));
        umodel.add(new NumberObject(1), new StringObject("mL"));
        umodel.add(new NumberObject(1), new StringObject("ug/L"));
        umodel.add(new NumberObject(1), new StringObject("kg"));
        setTableDropdownModel(resultWidget,0,umodel);

        loadTableDropdownModel(resultWidget,1,TestMeta.getTestResult().getTypeId());
        loadTableDropdownModel(q,1,TestMeta.getTestResult().getTypeId());
        loadTableDropdownModel(resultWidget,4,TestMeta.getTestResult().getFlagsId());
        loadTableDropdownModel(q,4,TestMeta.getTestResult().getFlagsId());
        loadTableDropdownModel(resultWidget,5,TestMeta.getTestResult().getRoundingMethodId());
        loadTableDropdownModel(q,5,TestMeta.getTestResult().getRoundingMethodId());
                
        //
        // set dropdown models for column 1 and 3 
        //               
        loadTableDropdownModel(analyteTreeController,1,"analyte",TestMeta.getTestAnalyte().getTypeId());
        loadTableDropdownModel(analyteTreeController,3,"analyte",TestMeta.getTestAnalyte().getScriptletId());
        
        resultGroupDropdownModel = new DataModel();
        resultGroupDropdownModel.setDefaultSet(new DataSet());
        setTableDropdownModel(analyteTreeController,4,"analyte",resultGroupDropdownModel);

        analyteTreeController.model.manager = this;
        analyteTreeController.enabled(false);
        analyteTreeController.addTreeWidgetListener(this);

        //override the callbacks
        updateChain.add(afterUpdate);        
        commitUpdateChain.add(commitUpdateCallback);
        commitAddChain.add(commitAddCallback);

        super.afterDraw(success);

        analyteTree.enable(true);
        
        //
        // Below, each TableField or TreeField from the rpc is set the DataModel of the TableWidget or TreeWidget 
        // that it represents. This is done in order to make sure that the defaultSet
        // variable in default model of the field is iniliazed and is not null,
        // so that when the first time fetch is done for that field,on the server side 
        // when a new DataSet is to created from the model in the field for creating a 
        // new row in the table on the screen, there isn't a NullPointerException,
        // as creating a new dataset from the model involves the use of defaultSet for that model.
        //
        ((FormRPC)rpc.getField("sampleType")).setFieldValue("sampleTypeTable",
                                                            sampleTypeWidget.model.getData());

        ((FormRPC)rpc.getField("worksheet")).setFieldValue("worksheetTable",
                                                           wsItemWidget.model.getData());

        ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testPrepTable",
                                                               prepTestWidget.model.getData());       
        
        reflexTableField = (TableField)((FormRPC)rpc.getField("prepAndReflex"))
                                                   .getField("testReflexTable");        
        reflexTableField.setValue(reflexTestWidget.model.getData());               

        ((FormRPC)rpc.getField("details")).setFieldValue("sectionTable",
                                                         sectionWidget.model.getData());

        analyteRPC = (FormRPC)(rpc.getField("testAnalyte"));
        
        resultModelCollection = new ArrayList<DataModel>();       
        
        //
        // the submit method puts the TreeDataModel from the TreeWidget in the 
        // tree field from the rpc
        //
        analyteTree.submit(analyteRPC.getField("analyteTree"));

        analyteRPC.setFieldValue("testResultsTable",
                                 resultWidget.model.getData());
        
        defaultModel = (DataModel)resultWidget.model.getData().clone();                
    }

    /**
     * This function is for responding to the various events that take place on 
     * the screen and that can be identified by the value of the argument action.
     * This is different from the handling of normal click events, because more 
     * than one widget can send te same action and also the widget sending an action
     * may not be a button. This is called for all the objects added to the 
     * CommandChain for the screen in the afterDraw method
     */
    public void performCommand(Enum action, Object obj) {
        if (action == KeyListManager.Action.FETCH) {
            key = (DataSet)((Object[])obj)[0];
            //
            // See fillTestAnalyteDropDown(), fillTestResultDropDown(),  
            //     fillMaps(), fillResultModelCollection()
            // 
            ((TableDropdown)reflexTestWidget.columns.get(2).getColumnWidget()).setModel(getSingleRowModel());
            fillTestAnalyteDropDown();           
            fillMaps();     
            fillResultGroupDropdown(); 
            fillResultModelCollection();            
            prevSelTabIndex = -1;
        } else if(action == ButtonPanel.Action.QUERY && obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf(":") != -1) {
                getTests(query.substring(6));
                return;
            }                            
        }  else if(action == ButtonPanel.Action.COMMIT && obj instanceof AppButton && !(state == State.QUERY)){
            analyteRPC = ((FormRPC)rpc.getField("testAnalyte"));
            if (analyteRPC.load || state == State.ADD) {                            
              if(analyteTreeController.model.getData().size() > 0 && (resultModelCollection.size() == 0)) {
                 boolean ok = Window.confirm(consts.get("analyteNoResults"));
                 if(!ok)
                    return;
                
               } else if(resultModelCollection.size() > 0 && analyteTreeController.model.getData().size() == 0) {
                 boolean ok = Window.confirm(consts.get("resultNoAnalytes"));                 
                 if(!ok) 
                     return;           
                 
               } else if(!checkResGrpSel()) {
                 boolean ok = Window.confirm(consts.get("resultGrpNotSelForAll"));                 
                 if(!ok) 
                     return;
             }
            
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
    }
        
    public void query() {
        super.query();       
        enableTableAutoAdd(false);
        testId.setFocus(true);      
        resultPanel.clear();  
        //resultPanel.clearTabs();
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
        resultGroupDropdownModel.clear();
        if(testAnalyteDropdownModel != null)
         testAnalyteDropdownModel.clear();
        resultPanel.clear();
        //resultPanel.clearTabs();
        resultModelCollection = new ArrayList<DataModel>();
        prevSelTabIndex = -1;
        resultDropdownModelMap = new DataMap();
        resGroupAnalyteIdMap = new DataMap();
        
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
        resultPanel.clear();
        //resultPanel.clearTabs();
        resultPanel.addTabListener(this);
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
                        
            fillTestAnalyteDropDown();
            fillMaps();
            fillResultGroupDropdown();
            fillResultModelCollection();
                        
            fillPrepTestsReflexTests();
            
            prevSelTabIndex = -1;                       
        }
    };
    
    protected AsyncCallback<? extends Data> commitUpdateCallback = new AsyncCallback<FormRPC>() {
        public void onSuccess(FormRPC result){            
            CollectionField cf = null;
            if(rpc.status == Status.invalid) {
                analyteRPC = (FormRPC)(rpc.getField("testAnalyte"));
                cf = (CollectionField)(analyteRPC.getField("resultModelCollection"));
                             
               if(cf.getValue() !=null)            
                 resultModelCollection = (ArrayList<DataModel>)cf.getValue();  
            } else {
                enableTableAutoAdd(false);
            }
        }
        public void onFailure(Throwable caught){
            handleError(caught);
        }
    };
    
    protected AsyncCallback<? extends Data> commitAddCallback = new AsyncCallback<FormRPC>() {
        public void onSuccess(FormRPC result){            
            CollectionField cf = null;
            if(rpc.status == Status.invalid) {
                analyteRPC = (FormRPC)(rpc.getField("testAnalyte"));
                cf = (CollectionField)(analyteRPC.getField("resultModelCollection"));
                             
               if(cf.getValue() !=null)            
                 resultModelCollection = (ArrayList<DataModel>)cf.getValue();  
            } else {
                enableTableAutoAdd(false);
            }
        }
        public void onFailure(Throwable caught){
            handleError(caught);
        }
    };  
    
    protected void doSubmit() {                   
      super.doSubmit();    
      
      if(!(state == State.QUERY)) {           
       if(prevSelTabIndex == -1 && resultModelCollection.size() > 0) {
           //         
           // if a user doesn't select any tab from resultPanel then any new
           // rows added to the model for the first tab won't get added to the 
           // model stored at index zero in resultModelCollection 
           // because the code in onBeforeTabSelected() wont get a chance to execute;
           // this line makes sure that on committing a test's data,even if 
           // no tab was selected from resultPanel, the latest model for the 
           // first tab makes its way in resultModelCollection
           //            
        resultModelCollection.set(0, resultWidget.model.getData());
       }
       
       analyteRPC = (FormRPC)(rpc.getField("testAnalyte")); 
       CollectionField collField = (CollectionField)(analyteRPC.getField("resultModelCollection"));      
       collField.setValue(resultModelCollection);
       
       resultWidget.finishEditing();
       sectionWidget.finishEditing();
       reflexTestWidget.finishEditing();              
      }             
    }
         

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        
    }

    /**
     *Overriden to allow lazy loading various tabs of the various tab panels on
     *the screen 
     */
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {           
        DataModel model = null;
        ArrayList<Data> list = null;
        DataSet mr = null;
        DataSet dr = null;                
        
        AbstractField mf = null;
        AbstractField df = null;                
        
        if (sender == resultPanel) {    
          if(tabIndex != 0 && prevSelTabIndex == -1) 
              prevSelTabIndex = 0;           
          //
          // when a user clicks a tab on the test results panel (resultPanel)
          // to see the data for that result group, the model for the table 
          // displayed under it, is taken from the list of models (resultModelCollection)
          // and set to the table
          // 
           if(resultModelCollection.size() > tabIndex) {
            if(prevSelTabIndex > -1 && prevSelTabIndex != tabIndex){
                resultWidget.finishEditing();
                model = (DataModel)resultWidget.model.getData().clone();
                list = resultWidget.model.getData().getDeletions();
                
                for(int i = 0; i < list.size(); i++){
                    model.getDeletions().add((Data)list.get(i).clone());
                }                  
                                 
                  for(int i = 0; i < model.size(); i++) {
                      dr = (DataSet)resultWidget.model.getData().get(i);
                      mr = model.get(i);
                      
                      for(int j= 0; j < dr.size(); j++) { 
                       mf = (AbstractField)mr.get(j);   
                       df = (AbstractField)dr.get(j);   
                       
                       for(int k = 0; k < df.getErrors().size(); k++) {
                         mf.addError((String)df.getErrors().get(k));   
                        }  
                      }
                   }                                                
                resultModelCollection.set(prevSelTabIndex,model);                  
             }               
            
            if(!(prevSelTabIndex == -1 && tabIndex == 0)) {  
              model = (DataModel)resultModelCollection.get(tabIndex);               
              resultWidget.model.load(model);               
              prevSelTabIndex = tabIndex;
            }            
         }  
        } else {
            if (state != FormInt.State.QUERY) {
                if (tabIndex == 0 && !((FormRPC)rpc.getField("details")).load) {
                    fillTestDetails();
                }else if (tabIndex == 2 && !((FormRPC)rpc.getField("sampleType")).load) {
                    fillSampleTypes();
                } else if (tabIndex == 1 && !((FormRPC)rpc.getField("testAnalyte")).load) {                                        
                    fillTestAnalyte();       
                    fillResultModelCollection();                                          
                } else if (tabIndex == 3) {
                   if(!((FormRPC)rpc.getField("prepAndReflex")).load) 
                    fillPrepTestsReflexTests();
                   analyteTreeController.finishEditing();
                   resultWidget.finishEditing();
                }  else if (tabIndex == 4 && !((FormRPC)rpc.getField("worksheet")).load) {
                    fillWorksheetLayout();
                }
            }
        }
        return true;
    }
    
    public boolean validate() {
        return !treeItemsHaveErrors();
    }        
    
    public boolean canAdd(TableWidget widget, DataSet set, int row) {        
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        if (widget == wsItemWidget)                 
            return addRow.get(0).getValue() != null;
         else if (widget == reflexTestWidget) { 
            if((addRow.get(0).getValue() != null && !addRow.get(0).getValue().equals(-1))
               || (addRow.get(1).getValue() != null && !addRow.get(1).getValue().equals(-1)))  
            return true;
         } else if (widget == resultWidget) {
             return addRow.get(1).getValue() != null && !addRow.get(1).getValue()
             .equals(-1);   
         }
        
        return addRow.get(0).getValue() != null && !addRow.get(0).getValue()
                                                          .equals(-1);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {        
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {             
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
            // column 2 is for the Results dropdown 
            if (widget == reflexTestWidget && col == 2) {
               DropDownField ddfield = (DropDownField)set.get(1);
               DataSet vset = (DataSet)ddfield.getValue();               
               Integer analyteId = (Integer)ddfield.getSelectedKey();
               if(vset != null && vset.enabled == true && ddfield.getErrors().size() == 0) {
                 setTestResultsForAnalyte(analyteId, set);
               } else {
                 Window.alert(consts.get("selectAnaBeforeRes"));
                 return false;
               }  
            } else if(widget == resultWidget && resultPanel.getTabBar().getTabCount() == 0 
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
    
    public boolean canDrop(TreeWidget widget, Widget dragWidget, Widget dropWidget) {        
        return false;
    }
     
    /**
     * This function is called whenever a given cell in a table looses focus, 
     * which signifies that it is no longer being edited 
     */
    public void finishedEditing(SourcesTableWidgetEvents sender,
                                int row,
                                int col) {        
        if (sender == sectionWidget && col == 1) {
            final int currRow = row;
            final Integer selValue = (Integer)((DropDownField)sectionWidget.model.getRow(row)
                                                                 .get(col))
                                                                 .getSelectedKey();
            // 
            // This code is to find out which option was chosen in the "Options"
            // column of the Test Section table in the Test Details tab, so that 
            // the values for the other rows can be changed accordingly  
            //
            screenService.getObject("getCategorySystemName",
                                    new Data[] {new NumberObject(selValue)},
                                    new AsyncCallback<StringObject>() {
                                        public void onSuccess(StringObject result) {
                                            if ("test_section_default".equals((String)result.getValue())) {
                                                for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                    if (iter != currRow) {
                                                       // 
                                                       // if the option chosen is "Default" for this row, 
                                                       // then all the other rows must be set to the blank option
                                                       // 
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
                                                        field.setValue(new DataSet(new NumberObject(-1)));
                                                    }
                                                }
                                                sectionWidget.model.refresh();
                                            } else {
                                                if (selValue.intValue() != -1) {
                                                    for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
                                                        //
                                                        // if the option chosen is "Ask" or "Match User Location" for this row, 
                                                        // then all the other rows must be set to the same option which is this one
                                                        // 
                                                        field.setValue(new DataSet(new NumberObject(selValue)));
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

        } else if (sender == resultWidget) {
            final String value = (String)resultWidget.model.getRow(row)
                                                           .get(2)
                                                           .getValue(); 
            
            if(resultWidget.model.getData().size() > 0){
              DataSet set = resultWidget.model.getData().get(row);
              DataMap data = (DataMap)set.getData();
              Integer rg = null;
              if(data == null) {
                data = new DataMap();                                                                                            
                set.setData(data);
             }
                        
              if(data.get("resGrp") == null) {
               rg = resultPanel.getTabBar().getSelectedTab();                                                                                        
               data.put("resGrp", new NumberField(rg+1));
              }
            } 
            if (col == 2 && !"".equals(value.trim())) {                
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)resultWidget.model.getRow(row)
                                                                    .get(1))
                                                                    .getSelectedKey();
                
                //              
                // This code is for finding out which option was chosen in the "Type"
                // column of the Test Results table in the "Analyte" tab, so that error checking 
                // or formatting can be done for the value set in the "Value" column    
                //
                screenService.getObject("getCategorySystemName",
                                        new Data[] {new NumberObject(selValue)},
                                        new AsyncCallback<StringObject>() {
                                            public void onSuccess(StringObject result) {
                                                Double[] darray = new Double[2];
                                                String finalValue = "";
                                                if ("test_res_type_dictionary".equals((String)result.getValue())) {
                                                    //
                                                    // Find out if this value is stored in the database if the type chosen was "Dictionary"
                                                    //
                                                    screenService.getObject("getEntryId",
                                                                            new Data[] {new StringObject(value)},
                                                                            new AsyncCallback<NumberObject>() {

                                                                                public void onSuccess(NumberObject result1) {                                                                                    
                                                                                    //
                                                                                    // If this value is not stored in the database
                                                                                    // then add error to this cell in the "Value"
                                                                                    // column
                                                                                    if (result1.getValue() == null) {
                                                                                        resultWidget.model.setCellError(currRow,
                                                                                             2,consts.get("illegalDictEntryException"));
                                                                                    } else {
                                                                                        DataSet set = resultWidget.model.getData().get(currRow);
                                                                                        DataMap data = (DataMap)set.getData();
                                                                                        if(data == null) {
                                                                                            data = new DataMap();                                                                                            
                                                                                            set.setData(data);
                                                                                        }
                                                                                        
                                                                                        if(data.get("id") == null) {
                                                                                         Integer tempResId = getNextTempResId();                                                                                        
                                                                                         data.put("id", new NumberField(tempResId));
                                                                                        }
                                                                                        
                                                                                        data.put("value", result1);
                                                                                        
                                                                                        checkAndAddNewResultValue(value,set);                                                                                        
                                                                                    }
                                                                                }

                                                                                public void onFailure(Throwable caught) {
                                                                                    Window.alert(caught.getMessage());
                                                                                    window.setStatus("","");
                                                                                }
                                                                            });
                                                   
                                                } else if ("test_res_type_numeric".equals((String)result.getValue())) {
                                                    //
                                                    // Get the string that was entered if the type chosen was "Numeric" and try to 
                                                    // break it up at the "," if it follows the pattern number,number 
                                                    //
                                                    String[] strList = value.split(",");
                                                    boolean convert = false;
                                                    if (strList.length == 2) {
                                                        for (int iter = 0; iter < strList.length; iter++) {
                                                            String token = strList[iter];
                                                            try {
                                                                // 
                                                                // Convert each number obtained from the string and store 
                                                                // its value converted to double if its a valid number, into an array
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
                                                            // If its a valid string store the converted string back into the column
                                                            // otherwise add an error to the cell and store empty string into the cell
                                                            //  
                                                          if(darray[0].toString().indexOf(".") == -1){
                                                              finalValue = darray[0].toString()+".0" + ",";  
                                                          }else {
                                                              finalValue = darray[0].toString() + ",";
                                                          }  
                                                          
                                                          if(darray[1].toString().indexOf(".") == -1) {
                                                              finalValue+= darray[1].toString()+".0";
                                                          }else {
                                                              finalValue+= darray[1].toString();
                                                          }
                                                          resultWidget.model.setCell(currRow, 2, finalValue);
                                                          DataSet set = resultWidget.model.getData().get(currRow);
                                                          DataMap data = (DataMap)set.getData();
                                                          if(data == null) {
                                                              data = new DataMap();                                                                                            
                                                              set.setData(data);
                                                          }
                                                          
                                                          if(data.get("id") == null) {
                                                           Integer tempResId = getNextTempResId();                                                                                        
                                                           data.put("id", new NumberField(tempResId));
                                                          }
                                                          
                                                          data.put("value", new NumberObject(-999));                                                            
                                                          
                                                          checkAndAddNewResultValue(finalValue,set);
                                                          
                                                        } else {                                                            
                                                            resultWidget.model.setCellError(currRow,
                                                                                            2,
                                                                                            consts.get("illegalNumericFormatException"));                                                            
                                                        }                                                                                                       

                                                } else if ("test_res_type_titer".equals((String)result.getValue())) {
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
                                                                // Convert each number obtained from the string and store 
                                                                // its value converted to double if it's a valid number, into an array
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
                                                            // If it's a valid string store the converted string back into the column
                                                            // otherwise add an error to the cell and store empty string into the cell
                                                            //
                                                            if(darray[0].toString().indexOf(".") == -1){
                                                                finalValue = darray[0].toString()+".0" + ":";  
                                                            }else {
                                                                finalValue = darray[0].toString() + ":";
                                                            }  
                                                            
                                                            if(darray[1].toString().indexOf(".") == -1) {
                                                                finalValue+= darray[1].toString()+".0";
                                                            }else {
                                                                finalValue+= darray[1].toString();
                                                            }                                                              
                                                            resultWidget.model.setCell(currRow, 2, finalValue);
                                                            DataSet set = resultWidget.model.getData().get(currRow);
                                                            DataMap data = (DataMap)set.getData();
                                                            if(data == null) {
                                                                data = new DataMap();                                                                                            
                                                                set.setData(data);
                                                            }
                                                            
                                                            if(data.get("id") == null) {
                                                             Integer tempResId = getNextTempResId();                                                                                        
                                                             data.put("id", new NumberField(tempResId));
                                                            }
                                                            
                                                            data.put("value", new NumberObject(-999));
                                                            
                                                            checkAndAddNewResultValue(finalValue,set);
                                                            
                                                    } else {                                                            
                                                            resultWidget.model.setCellError(currRow,
                                                                                            2,
                                                                                            consts.get("illegalTiterFormatException"));                                                            
                                                        }                                                                                                       

                                                }

                                            }

                                            public void onFailure(Throwable caught) {
                                                Window.alert(caught.getMessage());
                                                window.setStatus("", "");
                                            }
                                        });

            }            
        } else if(sender == wsItemWidget) {
            if(col == 0){
                final int currRow = row;
                final Integer selValue = (Integer)((DropDownField)wsItemWidget.model.getRow(row)
                                                                    .get(1))
                                                                    .getSelectedKey();
                screenService.getObject("getCategorySystemName",
                                        new Data[] {new NumberObject(selValue)},
                                        new AsyncCallback<StringObject>() {
                                            public void onSuccess(StringObject result) {
                                                Integer value = (Integer)wsItemWidget.model.getRow(currRow)
                                                                                            .get(0).getValue();
                                                if ("pos_duplicate".equals((String)result.getValue())||
                                                              "pos_fixed".equals((String)result.getValue())) {                                                                                                      
                                                    if(value == null){
                                                        wsItemWidget.model.setCellError(currRow,0,
                                                                                       consts.get("fixedDuplicatePosException"));                                                        
                                                    }
                                                }else if(!("pos_duplicate".equals((String)result.getValue()))&&
                                                                !("pos_fixed".equals((String)result.getValue()))){
                                                    if(value != null){
                                                        wsItemWidget.model.setCellError(currRow,0,
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
        } else if(sender == reflexTestWidget) {          
           DataSet rset = reflexTestWidget.model.getRow(row);
           DataSet vset = null;
           DropDownField ddfield = (DropDownField)rset.get(col);
           Integer analyteId = null;           
           if(col == 1) { 
            vset = (DataSet)ddfield.getValue();   
            if(vset != null && vset.enabled == false) {
              reflexTestWidget.model.setCellError(row,1,consts.get("analyteDeleted"));
            }    
             analyteId = (Integer)ddfield.getSelectedKey();
             setTestResultsForAnalyte(analyteId, rset);                                                  
           } else if(col == 2) {
              vset = (DataSet)ddfield.getValue();
              if(vset != null && vset.enabled == false) {
                 reflexTestWidget.model.setCellError(row,2,consts.get("resultDeleted"));
              }
           }   
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
       
       DataSet ddset = null;
       DataSet lset = null;
       
       DataMap data = null;       
       
       NumberObject obj = null;
              
       boolean createNewSet = false;
       
       if(col == 0) { 
        field = (DropDownField)item.get(col);
        selText = (String)field.getTextValue();
        if(field.getSelectedKey() == null || (Integer)field.getSelectedKey()==-1) {
           if(!field.getErrors().contains(consts.get("fieldRequiredException"))) {           
             field.addError(consts.get("fieldRequiredException"));
             reflexTestWidget.model.refresh();
             return;
           } 
        }
        if(item.getData() != null) {
          data = (DataMap)item.getData();
          if(data.get("id") == null) { 
             createNewSet = true; 
          } else {
             obj = (NumberObject)data.get("id");
             ddset = testAnalyteDropdownModel.getByKey(obj);
             prevVal = (String)ddset.get(0).getValue();
             if(!prevVal.trim().equals(selText.trim())) {
                 setErrorToReflexFields(consts.get("analyteNameChanged"),(Integer)obj.getValue(),1);                 
              ddset.get(0).setValue(selText); 
              ((TableDropdown)reflexTestWidget.columns.get(1).getColumnWidget())
                                             .setModel(testAnalyteDropdownModel);
             }  
          }
        } else {
            createNewSet = true;            
        } 
        
        if(createNewSet) {
           tempAnaId = getNextTempAnaId();
           ddset =  new DataSet();
           ddset.setKey(new NumberObject(tempAnaId));
           ddset.add(new StringObject(selText));
            
           data = new DataMap();
           data.put("id", new NumberObject(tempAnaId));
           item.setData(data);
           
           if(testAnalyteDropdownModel == null)
               testAnalyteDropdownModel = new DataModel();  
           
            testAnalyteDropdownModel.add(ddset);
           ((TableDropdown)reflexTestWidget.columns.get(1).getColumnWidget())
                                            .setModel(testAnalyteDropdownModel);             
        }
                
     } else if (col == 1) {
         field = (DropDownField)item.get(col);
         if(field.getSelectedKey() == null || (Integer)field.getSelectedKey()==-1) {
             field.addError(consts.get("fieldRequiredException"));
             analyteTreeController.model.refresh();             
         }
     }else if(col == 4) {
         field = (DropDownField)item.get(col);           
         selText = (String)field.getTextValue();
         resultWidget.finishEditing();
         
         if(item.getData() != null && !("").equals(selText.trim())) {
             data = (DataMap)item.getData();
             if(data.get("id") != null) {
                anaId = (Integer)((NumberObject)data.get("id")).getValue();                
                lset = (DataSet)resGroupAnalyteIdMap.get(selText);  
                obj = new NumberObject(anaId);
                if(lset != null) {
                   if(!lset.contains(obj)) {
                       lset.add(obj);
                   }
                } else {
                    lset = new DataSet();
                    lset.add(obj);
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
     * This function is called when the dictionary entry lookup screen is closed.
     * The function adds new rows to the Results table with the values for the "Value" column
     * being set to the entries that were chosen through the lookup screen 
     */    
    public void addResultRows(List<String> deList,DataSet set,List<Integer>idList) {
        DataMap data = null;
        DataSet row = null;
        String entry = null;
        Integer id = null; 
        if (resultPanel.getTabBar().getTabCount() == 0 ) { 
          Window.alert(consts.get("atleastOneResGrp"));
          return;
        }           
        for (int iter = 0; iter < idList.size(); iter++) {
          row = resultWidget.model.createRow();
          id = idList.get(iter); 
          entry  = deList.get(iter);
          row.get(0).setValue(set);
          row.get(1).setValue(entry);
          data = new DataMap();
          row.setData(data);                                                                                           
          data.put("id", new NumberField(getNextTempResId()));           
          data.put("value", new NumberObject(id));
          resultWidget.model.addRow(row);           
        }
        resultWidget.model.refresh();
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

    public boolean canDrop(TreeWidget widget,Widget dragWidget,
                           TreeDataItem dropTarget,int targetRow) {
        return true;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
       if((state == State.ADD) ||(state == State.UPDATE)) 
        return true;
       
       return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem item, int row) {
     if("analyte".equals(item.leafType)) {  
       DropDownField field  = (DropDownField)item.get(4);
       Integer key = (Integer)field.getSelectedKey();
       if(key != null && key != -1) {          
          resultPanel.getTabBar().selectTab(key-1);  
       } 
     } 
        return true;
    }

    public void drop(TreeWidget widget,Widget dragWidget,TreeDataItem dropTarget,
                                                                    int targetRow) {
        TreeDataItem drag = (TreeDataItem)((TreeRow)dragWidget).item;         
        TreeDataItem drop = (TreeDataItem)drag.clone();                  
        int chindex;
        TreeDataModel deletions = null;
                
        drop.setData(null);
        drop.parent = null;
                                  
        // 
        // if the dragged item was the child of a group item then it should be 
        // added to the list of deleted children of its parent
        //
        if(drag.parent!=null) {
            if(drag.parent.getData()==null) {
                deletions = new TreeDataModel();    
                drag.parent.setData(deletions);                 
            }
            ((TreeDataModel)drag.parent.getData()).add(drag);
             widget.model.unlink(drag);            
        }
               
        //
        // if the item on which drop occurred is a child of a group item
        // then the dragged item should be added to this item's parent, just before 
        // the item
        //
        if(dropTarget.parent!=null) {     
         if(drag.parent == null) {
           widget.model.unlink(drag);   
           widget.model.getData().delete(drag);
         }            
          chindex = dropTarget.childIndex;
          dropTarget.parent.addItem(chindex, drop);           
                  
        //
        // if the item on which drop occurred is not a child item then the 
        // dragged item should be added just before this item at the top level
        //
        }else { 
         if(drag.parent == null) {  
             widget.model.unlink(drag);
             widget.model.getData().delete(drag);              
         }   
         
          widget.model.addRow(targetRow, drop);
          
        } 
        
        widget.model.refresh();                    
    }

    public void drop(TreeWidget widget, Widget dragWidget) {

    }       
    
    /**
     * This method fetches the data model stored in resultDropDownModelMap with
     * the analyte's id as the key and sets the model as the model for the "Results" column's dropdown
     * in the Reflexive Tests table 
     */
    private void setTestResultsForAnalyte(Integer analyteId, DataSet set) {
        if (analyteId != null) {
            DataModel model = (DataModel)resultDropdownModelMap.get(analyteId.toString());
            DropDownField field = (DropDownField)set.get(2);
            DataSet prevSet = new DataSet(new NumberObject((Integer)field.getSelectedKey()));
            DataSet blankSet = new DataSet(new NumberObject(-1));

            if (model != null) {
                field.setModel(model);
                // if the new model doesn't contain the dataset that was selected 
                // from the previous one, then it may cause an ArrayIndexOutOfBoundsException
                // when you try to edit the Results column, this code makes sure
                // that in that situation the blank set is set as the value so that it becomes the
                // one that's selected and one that still belongs to the current data model.
                // Otherwise the previously selected set is set as the value
                if (field.getSelectedKey() !=null &&  model.contains(prevSet))
                    field.setValue(prevSet);
                else
                    field.setValue(blankSet);
            }                        
        }
    }

    private void getTests(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

            FormRPC rpc;

            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(TestMeta.getName(), query);
            commitQuery(rpc);
        }
    }

    /**
     * This function sets the Data Model for a dropdown field on the screen specified by "fieldName",
     * that's not inside a tree or table etc, to the argument "model"  
     */
    private void setDropdownModel(String fieldName, DataModel model) {
        Dropdown drop = (Dropdown)getWidget(fieldName);
        drop.setModel(model);
    }
    
    /**
     * This function sets the Data Model for a dropdown field that's inside the 
     * TableWidget "widget" in the column "column" to the argument "model"  
     */
    private void setTableDropdownModel(TableWidget widget,int column, DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget()).setModel(model);
    }
    
    /**
     * This function sets the Data Model for a dropdown field that's inside the 
     * TreeWidget "widget" in the column "column" of the node with leafType "leafType" to the argument "model"  
     */
    private void setTableDropdownModel(TreeWidget widget,int column,String leafType,DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget(leafType)).setModel(model);        
    }

    /**          
     * The function for loading the fields in "details" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Test Details"
     * tab in the tab-panel on the screen
     */
    private void fillTestDetails() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadTestDetails",
                                new Data[] {key, rpc.getField("details")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load((FormRPC)result);
                                        rpc.setField("details", (FormRPC)result);
                                        window.setStatus("", "");

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /** 
     * The function for loading the fields in the "prepAndReflex" sub-rpc section as
     * defined in the xsl file for the screen. It's mapped to the "Prep Test & Reflexive Test"
     * tab in the tab-panel on the screen
     */
    private void fillPrepTestsReflexTests() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadPrepTestsReflexTests",
                                new Data[] {key, rpc.getField("prepAndReflex")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("prepAndReflex",
                                                     (FormRPC)result);
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
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadSampleTypes",
                                new Data[] {key, rpc.getField("sampleType")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("sampleType",
                                                     (FormRPC)result);
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
     * defined in the xsl file for the screen. It's mapped to the "WorkSheet Layout"
     * tab in the tab-panel on the screen
     */
    private void fillWorksheetLayout() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadWorksheetLayout",
                                new Data[] {key, rpc.getField("worksheet")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("worksheet", result);
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
     * defined in the xsl file for the screen. It's mapped to the "Analyte"
     * tab in the tab-panel on the screen
     */
    private void fillTestAnalyte() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");        
        
        screenService.getObject("loadTestAnalyte",
                                new Data[] {key, rpc.getField("testAnalyte")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("testAnalyte", result);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
    }
    
    private void fillResultModelCollection(){
        if (key == null)
            return;
                
        resultPanel.clear();
        //resultPanel.clearTabs();
        resultModelCollection = new ArrayList<DataModel>();               
        
        screenService.getObject("getGroupCountForTest",
                                new Data[] {(NumberObject)key.getKey()},
                                new AsyncCallback<NumberObject>() {                                        
                                    public void onSuccess(NumberObject result) {                                          
                                       group = (Integer)result.getValue();                                       
                                       if(group > 0) {                              
                                           for(int iter = 1; iter < group+1; iter++){                                                  
                                              resultPanel.add(getDummyPanel(),new Integer(iter).toString());
                                              //resultPanel.addTab(new Integer(iter).toString());
                                              screenService.getObject("loadTestResultsByGroup",
                                                  new Data[] {(NumberObject)key.getKey(),
                                                  new NumberObject(iter),(DataModel)defaultModel.clone()},
                                                        new AsyncCallback<DataModel>() {                                        
                                                          public void onSuccess(DataModel result) {                                          
                                                            resultModelCollection.add((DataModel)result);                                            
                                                          }

                                                          public void onFailure(Throwable caught) {
                                                            Window.alert(caught.getMessage());
                                                              window.setStatus("", "");
                                                            }
                                                          });
                                            }            
                                            
                                           if(resultPanel.getTabBar().getTabCount() > 0){
                                             resultPanel.selectTab(0);                                             
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
     * This function loads the Analyte dropdown in the Reflexive Test table with
     * the analytes added to the test, the data for which was the most recently fetched
     */ 
    private void fillTestAnalyteDropDown() {
        if (key == null) {
            return;
        }

        NumberObject testId = (NumberObject)key.getKey();
        screenService.getObject("getTestAnalyteModel",
                                new Data[] {testId},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {                                        
                                        ((TableDropdown)reflexTestWidget.columns.get(1)
                                            .getColumnWidget()).setModel(result);
                                        testAnalyteDropdownModel = result;

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }
        
    private void fillResultGroupDropdown() {
        if (key == null) {
            return;
        }
        NumberObject testId = (NumberObject)key.getKey();
        screenService.getObject("getResultGroupModel",
                                new Data[] {testId, rpc.getField("testAnalyte")},
                                new AsyncCallback<ModelField>() {
                                    public void onSuccess(ModelField result) {
                                        resultGroupDropdownModel = (DataModel)result.getValue();                                        
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

    /**
     * This function loads the resultDropDownModelMap variable, which is a DataMap
     * with the key-value pairs, such that the keys are the ids of the rows in 
     * the test_analyte table that correspond to this test and the corresponding
     * values are the lists of result values that are associated with that test_analyte. 
     * This map is then used to for faster access to these models, 
     * so that they don't have to fetched from the database every time
     */
    private void fillMaps() {
        if (key == null) {
            return;
        }
        NumberObject testId = (NumberObject)key.getKey();
        DataMap dataMap = new DataMap();
        screenService.getObject("getTestResultModelMap",
                                new Data[] {testId, dataMap},
                                new AsyncCallback<DataMap>() {
                                    public void onSuccess(DataMap result) {
                                        resultDropdownModelMap = result;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
        dataMap = new DataMap();
        screenService.getObject("getResultGroupAnalyteMap",
                                new Data[] {testId, dataMap},
                                new AsyncCallback<DataMap>() {
                                    public void onSuccess(DataMap result) {
                                        resGroupAnalyteIdMap = result;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    /**
     * The functions for handling the click events associated with the
     * those buttons on the screen which remove selected rows from particular tables
     */
    private void onSampleTypeRowButtonClick() {
        if (sampleTypeWidget.model.getData().getSelectedIndex() > -1)
            sampleTypeWidget.model.deleteRow(sampleTypeWidget.model.getData()
                                                                   .getSelectedIndex());
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
    
    private void onTestResultRowButtonClicked(){
        int selIndex = resultWidget.model.getData().getSelectedIndex(); 
        DataSet set = null;
        NumberField field = null;
        
        if (selIndex > -1) { 
            if(resultWidget.model.getData().size() > 1) {
             set = resultWidget.model.getRow(selIndex);
             if(set.getData() != null) {
               field = (NumberField)((DataMap)set.getData()).get("id");
               if(field!=null) {
                disableResultOptions(set);
                setErrorToReflexFields(consts.get("resultDeleted"),(Integer)field.getValue(),2);
               } 
             }
             resultWidget.model.deleteRow(selIndex);             
             
            } else {
                Window.alert(consts.get("atleastOneResInResGrp"));
            }

        }        
    }

    private void onAddResultTabButtonClicked() {
        DataSet ddset = null;
        DataSet lset = null;
        group++; 
        
        if(group == 2) 
          prevSelTabIndex = 0;
        
        resultPanel.add(getDummyPanel(),new Integer(group).toString());
        //resultPanel.addTab(new Integer(group).toString());
        resultModelCollection.add((DataModel)defaultModel.clone());        
        resultPanel.selectTab(group-1);       
        
        ddset = new DataSet();
        ddset.add(new StringObject((new Integer(group)).toString()));
        ddset.setKey(new NumberObject(group));        
        resultGroupDropdownModel.add(ddset);
        
        lset = new DataSet();
        if(resGroupAnalyteIdMap == null) {
            resGroupAnalyteIdMap = new DataMap();
        }
        resGroupAnalyteIdMap.put(Integer.toString(group), lset);
        
        setTableDropdownModel(analyteTreeController, 4, "analyte", resultGroupDropdownModel);
   
    }
    
    private void onAddRowButtonClicked() {
        gid++;
        TreeDataItem newItem = analyteTreeController.model.getData()
                                                          .createTreeItem("analyte",
                                                                          new NumberObject(gid));
        analyteTreeController.model.addRow(newItem);        
        analyteTreeController.model.refresh();                    
    }
    
    private void onGroupAnalytesButtonClicked() {                         
        int[] selectedRowIndexes =  analyteTreeController.model.getSelectedRowIndexes();         
         TreeDataItem item = null;
         TreeDataItem newItem = null;
         TreeDataItem delItem = null;
                         
         if(selectedRowIndexes.length < 2) {
            Window.alert(consts.get("atleastTwoAnalytes"));
            return;
         }       
            gid++;
            item = analyteTreeController.model.getData().createTreeItem("top",
                                                              new NumberObject(gid));  
            item.get(0).setValue(consts.get("group"));
                        
            Arrays.sort(selectedRowIndexes);
                                                            
            for(int iter = 0; iter < selectedRowIndexes.length; iter++) { 
                 int index = selectedRowIndexes[iter];                  
                 delItem = analyteTreeController.model.getRow(index);
                if("top".equals(delItem.leafType)) {
                     Window.alert(consts.get("cantGroupGroups"));
                     return;
                }  
                if(delItem.parent != null) {
                    Window.alert(consts.get("analyteAlreadyGrouped"));
                    return;
                }
                if(iter > 0 && index != ((Integer)selectedRowIndexes[iter-1]+1)){                
                    Window.alert(consts.get("analyteNotAdjcnt"));
                    return;
                }
                
                newItem = (TreeDataItem)delItem.clone();   
                newItem.setData(null);
                item.addItem(newItem);
                item.open = true;                                                             
            }
                                     
            for(int iter = selectedRowIndexes.length; iter > 0 ; iter--) {                
                int index = selectedRowIndexes[iter-1];               
                analyteTreeController.model.deleteRow(index);
            }
             
            if(analyteTreeController.model.getData().size() > 0) {
             if(selectedRowIndexes[0] < analyteTreeController.model.getData().size()) {  
                 analyteTreeController.model.addRow(selectedRowIndexes[0],item);
             } else {
                 analyteTreeController.model.addRow(item); 
             }             
            }else {
                analyteTreeController.model.addRow(item);  
            }
            
            analyteTreeController.model.refresh();
    }
    
    private void onUngroupAnalytesButtonClicked() {                
        TreeDataItem item;
        TreeDataItem newItem;

        List<TreeDataItem> chItems;
        int index;
        ArrayList<Integer> selRows = analyteTreeController.model.selectedRows; 
                
        if(selRows.size() > 0) {
         for(int iter = 0 ; iter < selRows.size(); iter++) { 
          index = selRows.get(iter);          
            item = analyteTreeController.model.getRow(index);
            if("top".equals(item.leafType)){
                chItems = item.getItems();                
                
                for(int i = 0; i < chItems.size(); i++) {                                                          
                    newItem = (TreeDataItem)chItems.get(i).clone();
                    newItem.parent = null;
                    newItem.setData(null);
                    analyteTreeController.model.addRow(index, newItem);                   
                }
                         
                analyteTreeController.model.getData().delete(item);
            }
          } 
            analyteTreeController.model.refresh();
        }
      }  

 
    private void onDeleteTreeItemButtonClicked() {
       int[] selectedRowIndexes = analyteTreeController.model.getSelectedRowIndexes();
       int index = 0;   
       TreeDataItem item = null;
       TreeDataItem parent = null;
       TreeDataModel model;     
       NumberObject field = null;
       
       for(int iter = 0; iter < selectedRowIndexes.length ; iter++) {   
            index = selectedRowIndexes[iter];
            item = analyteTreeController.model.getRow(index);
            if(item.parent != null) {
                parent = item.parent;
                if(parent.getData()==null){
                    model = new TreeDataModel();    
                    parent.setData(model);                 
                }
                ((TreeDataModel)parent.getData()).add(item);
            } else {                         
              analyteTreeController.model.deleteRow(index);
            }              
             analyteTreeController.model.unlink(item);
       }
       
       if(item.getData() != null) {
        field = (NumberObject)((DataMap)item.getData()).get("id");
        if(field != null) 
            setErrorToReflexFields(consts.get("analyteDeleted"),(Integer)field.getValue(),1);   
         testAnalyteDropdownModel.getByKey(field).enabled = false;
         ((TableDropdown)reflexTestWidget.columns.get(1).getColumnWidget())
                         .setModel(testAnalyteDropdownModel);        
       } 
       
       analyteTreeController.model.refresh();
    }

    /**
     *This function opens a modal window which allows the users to select one or
     * more dictionary entries to be added to the Test Results table   
     */
    private void onDictionaryLookUpButtonClicked() {
        int left = getAbsoluteLeft();
        int top = getAbsoluteTop();
        PopupPanel standardNotePopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel,
                                                     "Choose Dictionary Entry",
                                                     "dictionaryEntryPicker",
                                                     "Loading...");
        pickerWindow.setContent(new DictionaryEntryPickerScreen(this));
        standardNotePopupPanel.add(pickerWindow);        
        standardNotePopupPanel.setPopupPosition(left, top);
        standardNotePopupPanel.show();
    }
    
    private void enableTableAutoAdd(boolean enable){       
        reflexTestWidget.model.enableAutoAdd(enable);
        wsItemWidget.model.enableAutoAdd(enable);
        prepTestWidget.model.enableAutoAdd(enable);
        sampleTypeWidget.model.enableAutoAdd(enable);
        sectionWidget.model.enableAutoAdd(enable);
        resultWidget.model.enableAutoAdd(enable);
    }
    
    private void loadDropdownModel(String fieldName){
        final String name = fieldName;
        StringObject catObj = new StringObject(name);
        screenService.getObject("getInitialModel",
                                new Data[] {catObj},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        setDropdownModel(name, result);
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
    }
    
    private void loadTableDropdownModel(TableWidget widget,int column,String fieldName){
        final String name = fieldName;
        final int col = column;
        final TableWidget w = widget;
        StringObject catObj = new StringObject(name);
        screenService.getObject("getInitialModel",
                                new Data[] {catObj},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        setTableDropdownModel(w, col, result);
                                    }
                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
    }
    
    private void loadTableDropdownModel(TreeWidget widget,int column,String leafType,String fieldName){
        final String name = fieldName;
        final int col = column;
        final String leaf = leafType;
        final TreeWidget w = widget;
        StringObject catObj = new StringObject(name);
        screenService.getObject("getInitialModel",
                                new Data[] {catObj},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        setTableDropdownModel(w, col,leaf,result);
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
    }             
    
   private VerticalPanel getDummyPanel(){ 
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
              
       for(int i = 0; i  < model.getData().size(); i++) {
          item = model.getData().get(i);
          if("top".equals(item.leafType)) {
            for(int j = 0; j < item.getItems().size(); j++) {
              chItem = item.getItems().get(j);             
              selVal = (Integer)((DropDownField)chItem.get(4)).getSelectedKey();
               if(selVal == null || unselVal.equals(selVal)) {
                   return false;
               }                                                  
             }            
         } else {             
             selVal = (Integer)((DropDownField)item.get(4)).getSelectedKey();
             if(selVal == null || unselVal.equals(selVal)) {
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
              
       for(int i = 0; i  < model.getData().size(); i++) {
          item = model.getData().get(i);
          if("top".equals(item.leafType)) {
            for(int j = 0; j < item.getItems().size(); j++) {
             if(setErrorToItem(item.getItems().get(j))) 
                 itemsInError++;
           }            
         } else {
            if(setErrorToItem(item))
              itemsInError++;  
        }
     }
       
     if(itemsInError > 0) { 
        model.refresh();    
        return true;
     }
     
     return false;  
   }
   
   private int getNextTempAnaId(){
       return --tempAnaId;
   }
   
   private int getNextTempResId(){
       return --tempResId;
   }
   
   private DataModel getSingleRowModel(){
       DataModel model = new DataModel();
       DataSet blankset = new DataSet();
       blankset.add(new StringObject(""));
       blankset.setKey(new NumberObject(-1));
       model.add(blankset);       
       return model;
   }
   
   private DataModel getDropdownModelForRsltGrp(int rsltGrp) {
      DataModel rmodel = null;       
      DataModel ddmodel =  new DataModel();
      
      if(resultPanel.getTabBar().getSelectedTab() != rsltGrp-1)  
          rmodel = resultModelCollection.get(rsltGrp-1);
      else 
          rmodel = resultWidget.model.getData();
      
      DataSet rset = null;
      DataSet ddset = null;
      
      DataMap data = null;
      
      NumberField field = null; 
      
      DataSet blankset = new DataSet();
      blankset.add(new StringObject(""));
      blankset.setKey(new NumberObject(-1));
      ddmodel.add(blankset);
      
      for(int i = 0; i < rmodel.size(); i++) {          
        ddset = new DataSet();
        rset = rmodel.get(i);
        data = (DataMap)rset.getData();
        field = (NumberField)data.get("id");
        ddset.setKey(new NumberObject((Integer)field.getValue()));
        ddset.add(new StringObject((String)rset.get(1).getValue()));
        ddmodel.add(ddset);        
      }      
      return ddmodel;
   }  
   
   private void setErrorToReflexFields(String error, Integer id, int col) {
       DataSet rset = null;
       DataSet vset = null;
       DropDownField field = null;
       for(int i = 0; i < reflexTestWidget.model.getData().size(); i++) {
          rset = reflexTestWidget.model.getData().get(i);
          field = (DropDownField)rset.get(col);
          vset = (DataSet)field.getValue();
          
          if(id.equals((Integer)((NumberObject)vset.getKey()).getValue())) 
             if(!field.getErrors().contains(error)) {
               reflexTestWidget.model.setCellError(i,col,error);                 
          } 
       }
   } 
   
   private void checkAndAddNewResultValue(String value,DataSet set) {
       int selTab = resultPanel.getTabBar().getSelectedTab();                                                                 
       NumberObject obj = null;
       NumberField field = (NumberField)((DataMap)set.getData()).get("id");
       Integer addKey = (Integer)field.getValue();
       NumberObject akobj = new NumberObject(addKey);              
       DataModel ddModel = null;       
       DataSet lset = (DataSet)resGroupAnalyteIdMap.get(new Integer(selTab+1).toString());
       DataSet addset = null;
       String prevVal = null;
      
       if(lset != null) {
        for(int i = 0; i < lset.size(); i++) {     
          obj = (NumberObject)lset.get(i);
          ddModel = (DataModel)resultDropdownModelMap.get(((Integer)obj.getValue()).toString());                      
              addset = ddModel.getByKey(akobj);              
              if(addset != null) {             
                  prevVal = (String)addset.get(0).getValue();
                  if(!prevVal.trim().equals(value.trim())) {
                    setErrorToReflexFields(consts.get("resultValueChanged"), addKey, 2);                   
                    addset.get(0).setValue(value);                    
                  }  
               } else {
                  addset = new DataSet(akobj,new StringObject(value));   
                  ddModel.add(addset);  
             }                        
        }                 
      }   
   }
   
   private void disableResultOptions(DataSet set) {
       int selTab = resultPanel.getTabBar().getSelectedTab();                                                                 
       NumberObject obj = null;
       NumberField field = (NumberField)((DataMap)set.getData()).get("id");
       if(field != null) {
        Integer addKey = (Integer)field.getValue();
        NumberObject akobj = new NumberObject(addKey);              
        DataModel ddModel = null;       
        DataSet lset = (DataSet)resGroupAnalyteIdMap.get(new Integer(selTab+1).toString());
        DataSet addset = null;       
      
        if(lset != null) {
         for(int i = 0; i < lset.size(); i++) {     
           obj = (NumberObject)lset.get(i);
           ddModel = (DataModel)resultDropdownModelMap.get(((Integer)obj.getValue()).toString());                      
               addset = ddModel.getByKey(akobj);              
               if(addset != null)            
                  addset.enabled = false;                        
          }                 
        }    
       }   
    }
   
   private boolean setErrorToItem(TreeDataItem item) {
       boolean someError = false;
       DropDownField field = (DropDownField)item.get(0); 
       if(fieldBlank(field) && !field.getErrors().contains(consts.get("fieldRequiredException"))) {           
            field.addError(consts.get("fieldRequiredException")); 
            field.valid = false;
            someError = true;
       } 
       
       field = (DropDownField)item.get(1);               
       if(fieldBlank(field) && !field.getErrors().contains(consts.get("fieldRequiredException"))) {           
           field.addError(consts.get("fieldRequiredException"));   
           field.valid = false;
           someError = true;
       } 
      return someError; 
   }
   
   private boolean fieldBlank(DropDownField field) {                    
       if(field.getSelectedKey() == null || (Integer)field.getSelectedKey()==-1) {
          return true; 
       }
     return false;  
   }

}