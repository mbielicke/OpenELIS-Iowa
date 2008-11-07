/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.test.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTreeWidget;
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
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeModelEvents;
import org.openelis.gwt.widget.tree.event.TreeModelListener;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TestScreen extends OpenELISScreenForm implements
                                                  ClickListener,
                                                  TabListener,
                                                  ChangeListener,
                                                  TableManager,
                                                  TableWidgetListener,
                                                  TreeModelListener,
                                                  TreeManager{                                                  
    private static boolean loaded = false;
    
    private static DataModel methodDropdown,labelDropdown,
                             testTrailerDropdown,scriptletDropDown,
                             sampleTypeDropDown,measureUnitDropDown,
                             prepTestDropDown,reflexTestFlagsDropDown,
                             revisionMethodDropDown,testFormatDropDown,
                             testWSItemTypeDropDown,testWSNumFormatDropDown,
                             reportingMethodDropDown,sortingMethodDropDown,
                             testSectionFlagDropDown,testAnalyteTypeDropDown,
                             sectionDropDown;
    
    private AppButton removeSampleTypeButton, removePrepTestButton, 
                      addAnalyteButton,addGroupButton,
                      addAnalyteBeforeButton,addGroupBeforeButton,
                      //addAnalyteAfterButton,addGroupAfterButton,
                      removeReflexTestButton,removeWSItemButton,
                      deleteButton,removeTestSectionButton;
    
    private TreeWidget analyteTreeController = null;   
    
    private int gid = 0;
    
    private ScreenTableWidget prepTestTable, sampleTypeTable, 
                              testReflexTable,worksheetItemTable,sectionTable;
    
    private TableWidget reflexTestContoller,prepTestController,
                        wsItemController,sampleTypeController,sectionController;
    
    private QueryTable reflexTestQueryTable;    
    
    private HashMap<String,DataModel> modelMap;
    
    private TestMetaMap TestMeta = new TestMetaMap();
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTextBox testId;
    private TextBox testName;                            
                            
    public TestScreen() {
        super("org.openelis.modules.test.server.TestService",!loaded);
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getTests(baction.substring(6, baction.length()));
            }else                             
                super.performCommand(action, obj);
        } else {
            if(action == KeyListManager.Action.FETCH){                
                key = (DataSet)((Object[])obj)[0];                
                fillTestAnalyteDropDown();
                fillTestResultDropDown();                
                fillModelMap();                             
            }
            if(action == State.ADD ||action == State.UPDATE){
                reflexTestContoller.model.enableAutoAdd(true);
                wsItemController.model.enableAutoAdd(true);
                prepTestController.model.enableAutoAdd(true);
                sampleTypeController.model.enableAutoAdd(true);
                sectionController.model.enableAutoAdd(true);
            }else{
                reflexTestContoller.model.enableAutoAdd(false);
                wsItemController.model.enableAutoAdd(false);
                prepTestController.model.enableAutoAdd(false);
                sampleTypeController.model.enableAutoAdd(false);
                sectionController.model.enableAutoAdd(false);
            }
            if(action == ButtonPanel.Action.ABORT || action == ButtonPanel.Action.COMMIT){                
                fillTestResultDropDown();                                                          
            }
            super.performCommand(action, obj);
        }
    }
    
    public void onClick(Widget sender) {        
        if (sender == removeSampleTypeButton)
            onSampleTypeRowButtonClick();
        else if (sender == removePrepTestButton)
            onPrepTestRowButtonClick();
        else if (sender == removeReflexTestButton)
           onReflexTestRowButtonClick();
        else if (sender == removeWSItemButton)        
            onWSItemRowButtonClick();
        else if(sender == addAnalyteButton)
              onAddAnalyteButtonClicked();            
        else if(sender == addGroupButton)            
            onAddGroupButtonClicked();   
        else if(sender == addAnalyteBeforeButton)
           onAddAnalyteBeforeButtonClicked();            
       else if(sender == addGroupBeforeButton)            
          onAddGroupBeforeButtonClicked();
       //else if(sender == addAnalyteAfterButton)
          //onAddAnalyteAfterButtonClicked();            
        //else if(sender == addGroupAfterButton)            
           //onAddGroupAfterButtonClicked();
        else if(sender == deleteButton)
            onDeleteButtonClicked();
        else if(sender == removeTestSectionButton)
            onTestSectionRowButtonClicked();
        
    }
    

    public void afterDraw(boolean success) {
        loaded = true;
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        AToZTable atozTable = (AToZTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        Dropdown drop;

        
        QueryTable q;
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
                
        bpanel.enableButton("delete", false);
       ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
       removeSampleTypeButton = (AppButton)getWidget("removeSampleTypeButton");
       removePrepTestButton = (AppButton)getWidget("removePrepTestButton");

       removeReflexTestButton = (AppButton)getWidget("removeReflexTestButton");
       removeWSItemButton = (AppButton)getWidget("removeWSItemButton");       
       
       deleteButton = (AppButton)getWidget("deleteButton");
       addAnalyteButton = (AppButton)getWidget("addAnalyteButton");
       addGroupButton = (AppButton)getWidget("addGroupButton");
       //addAnalyteAfterButton = (AppButton)getWidget("addAnalyteAfterButton");
       //addGroupAfterButton = (AppButton)getWidget("addGroupAfterButton");
       addAnalyteBeforeButton = (AppButton)getWidget("addAnalyteBeforeButton");
       addGroupBeforeButton = (AppButton)getWidget("addGroupBeforeButton");
       removeTestSectionButton = (AppButton)getWidget("removeTestSectionButton");
       
       ScreenTreeWidget analyteTree = (ScreenTreeWidget)widgets.get("analyteTree");
       analyteTreeController = (TreeWidget)analyteTree.getWidget();   
       
       if (methodDropdown == null) {
           methodDropdown = (DataModel)initData.get("methods");       
           labelDropdown = (DataModel)initData.get("labels");
           testTrailerDropdown = (DataModel)initData.get("testTrailers");
           scriptletDropDown = (DataModel)initData.get("scriptlets");           
           sampleTypeDropDown = (DataModel)initData.get("sampleTypes");
           measureUnitDropDown = (DataModel)initData.get("unitsOfMeasure");
           prepTestDropDown = (DataModel)initData.get("prepTests"); 
           reflexTestFlagsDropDown = (DataModel)initData.get("testReflexFlags");
           revisionMethodDropDown = (DataModel)initData.get("revisionMethods");
           testFormatDropDown = (DataModel)initData.get("testFormats");
           testWSItemTypeDropDown = (DataModel)initData.get("testWSItemTypes");
           testWSNumFormatDropDown = (DataModel)initData.get("testWSNumFormats");
           testAnalyteTypeDropDown = (DataModel)initData.get("testAnalyteTypes");
           reportingMethodDropDown = (DataModel)initData.get("reportingMethods");
           sortingMethodDropDown = (DataModel)initData.get("sortingMethods");
           testSectionFlagDropDown = (DataModel)initData.get("testSectionFlags");
           sectionDropDown = (DataModel)initData.get("sections");
       }
       
       drop = (Dropdown)getWidget(TestMeta.getMethodId());
       drop.setModel(methodDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getLabelId());
       drop.setModel(labelDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestTrailerId());
       drop.setModel(testTrailerDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getScriptletId());
       drop.setModel(scriptletDropDown);       
       
       drop = (Dropdown)getWidget(TestMeta.getRevisionMethodId());
       drop.setModel(revisionMethodDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestFormatId());
       drop.setModel(testFormatDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getSortingMethodId());
       drop.setModel(sortingMethodDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getReportingMethodId());
       drop.setModel(reportingMethodDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestWorksheet().getNumberFormatId());
       drop.setModel(testWSNumFormatDropDown);
              
       drop = (Dropdown)getWidget(TestMeta.getTestWorksheet().getScriptletId());
       drop.setModel(scriptletDropDown);
       
       sampleTypeTable = (ScreenTableWidget)widgets.get("sampleTypeTable");
       prepTestTable = (ScreenTableWidget)widgets.get("testPrepTable");
       testReflexTable = (ScreenTableWidget)widgets.get("testReflexTable"); 
       worksheetItemTable = (ScreenTableWidget)widgets.get("worksheetTable"); 
       sectionTable = (ScreenTableWidget)widgets.get("sectionTable");
       
       sampleTypeController = (TableWidget)sampleTypeTable.getWidget();
       q = (QueryTable)sampleTypeTable.getQueryWidget().getWidget();          
       
       ((TableDropdown)sampleTypeController.columns.get(0).getColumnWidget()).setModel(sampleTypeDropDown);
       ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(sampleTypeDropDown);
       
       ((TableDropdown)sampleTypeController.columns.get(1).getColumnWidget()).setModel(measureUnitDropDown);
       ((TableDropdown)q.columns.get(1).getColumnWidget()).setModel(measureUnitDropDown);
       
       
       prepTestController = (TableWidget)prepTestTable.getWidget();
       q = (QueryTable)prepTestTable.getQueryWidget().getWidget();
        
       ((TableDropdown)prepTestController.columns.get(0).getColumnWidget()).setModel(prepTestDropDown);
       ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(prepTestDropDown); 
       
       
       reflexTestContoller = (TableWidget)testReflexTable.getWidget();
       reflexTestQueryTable = (QueryTable)testReflexTable.getQueryWidget().getWidget();                        
       
       
       ((TableDropdown)reflexTestContoller.columns.get(0).getColumnWidget()).setModel(prepTestDropDown);
       ((TableDropdown)reflexTestQueryTable.columns.get(0).getColumnWidget()).setModel(prepTestDropDown); 
       
       ((TableDropdown)reflexTestContoller.columns.get(3).getColumnWidget()).setModel(reflexTestFlagsDropDown);
       ((TableDropdown)reflexTestQueryTable.columns.get(3).getColumnWidget()).setModel(reflexTestFlagsDropDown);                                 
       
       testId = (ScreenTextBox)widgets.get(TestMeta.getId());
       testName = (TextBox)getWidget(TestMeta.getName());
       
       wsItemController = (TableWidget)worksheetItemTable.getWidget();
       q = (QueryTable)worksheetItemTable.getQueryWidget().getWidget();
       
       ((TableDropdown)wsItemController.columns.get(1).getColumnWidget()).setModel(testWSItemTypeDropDown);
       ((TableDropdown)q.columns.get(1).getColumnWidget()).setModel(testWSItemTypeDropDown); 
       
       sectionController = (TableWidget)sectionTable.getWidget();
       sectionController.addTableWidgetListener(this);
       q = (QueryTable)sectionTable.getQueryWidget().getWidget();
       
       ((TableDropdown)sectionController.columns.get(0).getColumnWidget()).setModel(sectionDropDown);
       ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(sectionDropDown); 
       
       ((TableDropdown)sectionController.columns.get(1).getColumnWidget()).setModel(testSectionFlagDropDown);
       ((TableDropdown)q.columns.get(1).getColumnWidget()).setModel(testSectionFlagDropDown);
       
       //addCommandListener(d.controller);
       ((TableDropdown)analyteTreeController.columns.get(3).getColumnWidget("analyte")).setModel(scriptletDropDown); 
       ((TableDropdown)analyteTreeController.columns.get(1).getColumnWidget("analyte")).setModel(testAnalyteTypeDropDown);              
       analyteTreeController.model.addTreeModelListener(this);
       
       analyteTreeController.model.manager = this;
       
       updateChain.add(afterUpdate);        
                     
       
       super.afterDraw(success);
       
       analyteTree.enable(true);
       
       
       ((FormRPC)rpc.getField("sampleType")).setFieldValue("sampleTypeTable",
                                             sampleTypeController.model.getData());
       
       ((FormRPC)rpc.getField("worksheet")).setFieldValue("worksheetTable",
                                             wsItemController.model.getData());
                    
       ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testPrepTable",
                                               prepTestController.model.getData());
       
       ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testReflexTable",
                                               reflexTestContoller.model.getData());
       
       ((FormRPC)rpc.getField("details")).setFieldValue("sectionTable",
                                                sectionController.model.getData());
       
       FormRPC analyteRPC = (FormRPC)(rpc.getField("testAnalyte"));       
       analyteTree.submit(analyteRPC.getField("analyteTree"));         
              
    }
        
    public void query() {        
        super.query();
        testId.setFocus(true);
        removeSampleTypeButton.changeState(ButtonState.DISABLED) ;
        removePrepTestButton.changeState(ButtonState.DISABLED) ;
        removeReflexTestButton.changeState(ButtonState.DISABLED) ; 
        removeWSItemButton.changeState(ButtonState.DISABLED);
        deleteButton.changeState(ButtonState.DISABLED);
        addAnalyteButton.changeState(ButtonState.DISABLED); 
        addGroupButton.changeState(ButtonState.DISABLED); 
        //addAnalyteAfterButton.changeState(ButtonState.DISABLED);
        //addGroupAfterButton.changeState(ButtonState.DISABLED); 
        addAnalyteBeforeButton.changeState(ButtonState.DISABLED); 
        addGroupBeforeButton.changeState(ButtonState.DISABLED); 
    }
    
    public void add() {
        super.add();        
        testId.enable(false);
        testName.setFocus(true);
        removeReflexTestButton.changeState(ButtonState.UNPRESSED) ;                        
    }    
       
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            testId.enable(false);
            testName.setFocus(true);
            removeReflexTestButton.changeState(ButtonState.UNPRESSED) ;
        }
    }; 
    
    
    
    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    }
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if(state != FormInt.State.QUERY){
            if (tabIndex == 0 && !((FormRPC)rpc.getField("details")).load) {
                fillTestDetails();
            }else if (tabIndex == 1 && !((FormRPC)rpc.getField("testAnalyte")).load) {                
                fillTestAnalyte();
            } else if (tabIndex == 2 && !((FormRPC)rpc.getField("prepAndReflex")).load) {                 
                fillPrepTestsReflexTests();
            }
            else if (tabIndex == 3 && !((FormRPC)rpc.getField("sampleType")).load) {                
                fillSampleTypes();
            }
            else if (tabIndex == 4 && !((FormRPC)rpc.getField("worksheet")).load) {                
                fillWorksheetLayout();
            }            
           
         }
     
        return true;
    }
    
    public void setTestResultsForAnalyte(Integer analyteId,DataSet set){    
       if(analyteId!=null){                                               
         DataModel model = (DataModel)modelMap.get(analyteId.toString());      
         DropDownField field = (DropDownField)set.get(2);
         DataSet prevSet = new DataSet(new NumberObject((Integer)field.getValue()));
         DataSet blankSet = new DataSet(new NumberObject(-1));
         
         if(model != null && !model.equals(field.getModel())){
          field.setModel(model);
          if(model.contains(prevSet)) 
           field.setValue(prevSet);
          else
            field.setValue(blankSet);  
         } 

       }
    }
    
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
      }

       public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
          if(widget == wsItemController){
              return addRow.get(0).getValue() != null;
          } else
           return addRow.get(0).getValue() != null && !addRow.get(0).getValue().equals(-1);
      }

      public boolean canDelete(TableWidget widget, DataSet set, int row) {
       // TODO Auto-generated method stub
       return false;
      }

      public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
       if(state == State.UPDATE || state == State.ADD|| state == State.QUERY){
         if(widget == reflexTestContoller && col == 2) {                     
          DropDownField ddfield = (DropDownField)set.get(1);
          Integer analyteId =  (Integer)(ddfield.getValue());          
          setTestResultsForAnalyte(analyteId,set);
         } 
         return true;
       }
                  
          return false;
      }

      public boolean canSelect(TableWidget widget, DataSet set, int row) {
       if(state == State.UPDATE || state == State.ADD|| state == State.QUERY)
           return true;       
          return false;
      }
      
      public boolean canDrag(TableWidget widget, DataSet item, int row) {
          // TODO Auto-generated method stub
          return false;
      }

      public boolean canDrop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
          // TODO Auto-generated method stub
          return false;
      }

      public void drop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {          
          
      }

      public void drop(TableWidget widget, Widget dragWidget) {
          // TODO Auto-generated method stub
          
      }

      public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {          
          if(sender == sectionController && col == 1){
              final int currRow = row;
              final Integer selValue = (Integer)sectionController.model.getRow(row).get(col).getValue();
              screenService.getObject("getSystemName", new Data[] {new NumberObject(selValue)}, new AsyncCallback<StringObject>() {
                  public void onSuccess(StringObject result) {                        
                     if("test_section_default".equals((String)result.getValue())){
                        for(int iter = 0; iter < sectionController.model.numRows(); iter++){
                           if(iter != currRow){
                            DropDownField field = (DropDownField)sectionController.model.getRow(iter).get(1);
                            field.setValue(new DataSet(new NumberObject(-1)));
                           } 
                        }
                        sectionController.model.refresh();
                     }else {
                        if(selValue.intValue() != -1){
                            for(int iter = 0; iter < sectionController.model.numRows(); iter++){                                
                                 DropDownField field = (DropDownField)sectionController.model.getRow(iter).get(1);
                                 field.setValue(new DataSet(new NumberObject(selValue)));                                
                             }                            
                        }                        
                        sectionController.model.refresh();
                     }
                  }
                  public void onFailure(Throwable caught) {
                      Window.alert(caught.getMessage());
                      window.setStatus("","");
                  }
              });
              
          }
          
      }

      public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
          // TODO Auto-generated method stub
          
      }

      public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
          // TODO Auto-generated method stub
          
      }
      
    private void getTests(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
            
            FormRPC rpc;

            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(TestMeta.getName(), query);
            commitQuery(rpc);
        }
    }                
           
    
   private void fillTestDetails(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadTestDetails", new Data[] {key,rpc.getField("details")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {              
               load((FormRPC)result);               
               rpc.setField("details", (FormRPC)result);
               window.setStatus("","");

           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       }); 
   }
   
   private void fillPrepTestsReflexTests(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadPrepTestsReflexTests", new Data[] {key,rpc.getField("prepAndReflex")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {              
               load(result);
               rpc.setField("prepAndReflex", (FormRPC)result);
               window.setStatus("","");

           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
   }
   
   private void fillSampleTypes(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadSampleTypes", new Data[] {key,rpc.getField("sampleType")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {              
               load(result);
               rpc.setField("sampleType", (FormRPC)result);
               window.setStatus("","");

           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
   }
   
   
   private void fillWorksheetLayout(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadWorksheetLayout", new Data[] {key,rpc.getField("worksheet")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {              
               load(result);
               rpc.setField("worksheet", result);
               window.setStatus("","");

           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
   }
   
   private void fillTestAnalyte(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadTestAnalyte", new Data[] {key,rpc.getField("testAnalyte")}, new AsyncCallback<FormRPC>() {
           public void onSuccess(FormRPC result) {               
               load(result);               
               rpc.setField("testAnalyte", result);
               window.setStatus("","");
           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
   }
      
   
   private void fillTestAnalyteDropDown(){
      if(key == null){
          return;
      } 
      
       NumberObject testId = (NumberObject)key.getKey();
       screenService.getObject("getTestAnalyteModel", new Data[] {testId}, new AsyncCallback<DataModel>() {
           public void onSuccess(DataModel result) {              
               DataModel model = result;
               ((TableDropdown)reflexTestContoller.columns.get(1).getColumnWidget()).setModel(model);
               
           }
           public void onFailure(Throwable caught) {
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });
   }
   
   private void fillTestResultDropDown(){
       if(key == null){
           return;
       }        
        NumberObject testId = (NumberObject)key.getKey();
        screenService.getObject("getTestResultModel", new Data[] {testId}, new AsyncCallback<DataModel>() {
            public void onSuccess(DataModel result) {              
                DataModel model = result;                
                ((TableDropdown)reflexTestContoller.columns.get(2).getColumnWidget()).setModel(model);             
            }
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
   
   private void fillModelMap(){
       if(key == null){
           return;
       }        
        NumberObject testId = (NumberObject)key.getKey();
        DataMap dataMap = new DataMap();
        screenService.getObject("getTestResultModelMap", new Data[] {testId, dataMap}, new AsyncCallback<DataMap>() {
            public void onSuccess(DataMap result) { 
                DataMap map = result;
                //modelMap = (HashMap<String, DataModel>)map.getValue();                
            }
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }      
      
   private void onSampleTypeRowButtonClick() {
    if(sampleTypeController.model.getData().getSelectedIndex()>-1)   
       sampleTypeController.model.deleteRow(sampleTypeController.model.getData().getSelectedIndex());
   }
   
   private void onPrepTestRowButtonClick() {
      if(prepTestController.model.getData().getSelectedIndex()>-1) 
          prepTestController.model.deleteRow(prepTestController.model.getData().getSelectedIndex());
     }
   
   private void onWSItemRowButtonClick() {
      if(wsItemController.model.getData().getSelectedIndex()>-1) 
          wsItemController.model.deleteRow(wsItemController.model.getData().getSelectedIndex());
     }
   
   private void onReflexTestRowButtonClick() {      
       if(reflexTestContoller.model.getData().getSelectedIndex()>-1)   
        reflexTestContoller.model.deleteRow(reflexTestContoller.model.getData().getSelectedIndex());
      }
   
   private void onTestSectionRowButtonClicked(){
      if(sectionController.model.getData().getSelectedIndex()>-1){
         sectionController.model.deleteRow(sectionController.model.getData().getSelectedIndex());   
      }
   }
   
   private void onAddGroupButtonClicked(){
       gid++;       
       TreeDataItem item = analyteTreeController.model.getData().createTreeItem("top",new NumberObject(gid)); 
       item.get(0).setValue("Group");
       analyteTreeController.model.addRow(item);
       analyteTreeController.model.refresh();   
   }      
   
   private void onAddGroupBeforeButtonClicked(){
       //TreeDataItem selItem = analyteTreeController.model.getData().getSelected();
       int index = analyteTreeController.model.getSelectedRowIndex();
       if(index >-1){
        //int index = analyteTreeController.model.getData().indexOf(selItem);        
        gid++;       
        TreeDataItem item = analyteTreeController.model.getData().createTreeItem("top",new NumberObject(gid)); 
        item.get(0).setValue("Group");
        if(index==0)
            analyteTreeController.model.addRow(index, item);
        else
            analyteTreeController.model.addRow(index-1, item);           
        analyteTreeController.model.refresh();
       }        
   }
   
   private void onAddAnalyteButtonClicked(){  
     //TreeDataItem item = analyteTreeController.model.getData().getSelected(); 
     int index = analyteTreeController.model.getSelectedRowIndex();
       if(index >-1){   
           TreeDataItem item = analyteTreeController.model.getRow(index);
      if("top".equals(item.leafType)){ 
       gid++;
       TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte",new NumberObject(gid));
       item.addItem(newItem);
       analyteTreeController.model.refresh();
      }else {
          gid++;
          TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte",new NumberObject(gid));
          analyteTreeController.model.addRow(newItem);
          analyteTreeController.model.refresh();
      }
     }else {
         gid++;
         TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte",new NumberObject(gid));
         analyteTreeController.model.addRow(newItem);
         analyteTreeController.model.refresh();
     }
   }

   
   private void onAddAnalyteBeforeButtonClicked(){             
       int index = analyteTreeController.model.getSelectedRowIndex();
       if(index >-1){   
           TreeDataItem item = analyteTreeController.model.getRow(index);                      
       
      if(item.parent==null){ 
       gid++;
       TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte",new NumberObject(gid));
       if(index==0)
           analyteTreeController.model.addRow(index, newItem);
          else
              analyteTreeController.model.addRow(index-1, newItem);      
       analyteTreeController.model.refresh();
      }
     }//else {
        // gid++;
         //TreeDataItem newItem = analyteTreeController.model.getData().createTreeItem("analyte",new NumberObject(gid));
         //TreeDataItem parent = newItem.parent;         
         //int chindex = parent.indexOf(selItem);
         
         //analyteTreeController.model.refresh();
     //}
   }   
     
   private void onDeleteButtonClicked(){               
       /*int index = analyteTreeController.model.getSelectedIndex();     
       if(index > -1){                                   
       
          analyteTreeController.model.deleteRow(index);          
        //} 
        analyteTreeController.model.refresh();
       }*/
       TreeDataItem item = analyteTreeController.model.getData().getSelected(); 
       if(item !=null ){                                   
        int index =  analyteTreeController.model.getData().indexOf(item);
        analyteTreeController.model.deleteRow(index);
        analyteTreeController.model.refresh();
       }
     }

  public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {
    // TODO Auto-generated method stub
    
  }

  public void dataChanged(SourcesTreeModelEvents sender) {
    // TODO Auto-generated method stub
    
  }

  public void rowAdded(SourcesTreeModelEvents sender, int rows) {
    // TODO Auto-generated method stub
    
  }

  public void rowClosed(SourcesTreeModelEvents sender, int row, TreeDataItem item) {
    // TODO Auto-generated method stub
    
  }

  public void rowDeleted(SourcesTreeModelEvents sender, int row) {
    // TODO Auto-generated method stub
    
 }

 public void rowOpened(SourcesTreeModelEvents sender, int row, TreeDataItem item) {
    // TODO Auto-generated method stub
    
 }

 public void rowSelectd(SourcesTreeModelEvents sender, int row) { 
     /*TreeDataItem selItem = analyteTreeController.model.getRow(row);
     if(selItem.getData()==null){
         DataMap data = new DataMap();
         selItem.setData(data);
     }
           
     if(!new StringObject("Y").equals(((DataMap)selItem.getData()).get("unselect"))){
         ((DataMap)selItem.getData()).put("unselect", new StringObject("Y")); 
     }else{
         ((DataMap)selItem.getData()).put("unselect", new StringObject("N"));
         analyteTreeController.model.unselectRow(row);
     }*/
     
    
 }

 public void rowUnselected(SourcesTreeModelEvents sender, int row) {
    // TODO Auto-generated method stub
    
 }

 public void rowUpdated(SourcesTreeModelEvents sender, int row) {
    // TODO Auto-generated method stub
    
 }

  public void unload(SourcesTreeModelEvents sender) {
    // TODO Auto-generated method stub
    
 }

 public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
    // TODO Auto-generated method stub
    return false;
 }

 public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
    // TODO Auto-generated method stub
    return true;
 }

 public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
    // TODO Auto-generated method stub
    return false;
 }

 public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {          
    if("top".equals(item.leafType))
     return false;    
    return true;
 }

 public boolean canDrop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {
   if("analyte".equals(dropTarget.leafType))     
    return false;     
   return true;         
 } 

 public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {      
    return true;
 }

 public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
    // TODO Auto-generated method stub
    return true;
 }

 public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
    // TODO Auto-generated method stub
    return true;
 }

 public void drop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {
  if("top".equals(dropTarget.leafType)){
      TreeRow row = (TreeRow)dragWidget;      
      analyteTreeController.model.deleteRow(row.index);
      dropTarget.addItem(row.item);      
    analyteTreeController.model.refresh();
  }
 }

 public void drop(TreeWidget widget, Widget dragWidget) {
    // TODO Auto-generated method stub
    
 }
   

/*public boolean canEdit(int row, int col, TableController controller) {
   if(col == 2){  
    if(testForm.state != FormInt.State.QUERY){
     if(row >= 0){           
      TableRow trow = controller.model.getRow(row);
      DropDownField analyteId = (DropDownField)trow.getColumn(1);
      if (analyteId.getValue() != null && !analyteId.getValue().equals(new Integer(-1))){
         testForm.setTestResultsForAnalyte((Integer)analyteId.getValue());
      }else{
          showError(row,col,controller,"An analyte must be selected before selecting a result value.");
      }
     }        
    }
   } 
    
     return false;
 }*/
 }  