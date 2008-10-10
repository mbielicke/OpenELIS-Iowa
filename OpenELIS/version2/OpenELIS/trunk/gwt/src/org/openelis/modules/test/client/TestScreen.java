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
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
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
                                                  TableManager{                                                  
    private static boolean loaded = false;
    
    private static DataModel methodDropdown,labelDropdown,
                             testTrailerDropdown,scriptletDropDown,
                             sectionDropDown,sampleTypeDropDown,
                             measureUnitDropDown,prepTestDropDown,
                             reflexTestFlagsDropDown,revisionMethodDropDown,
                             testFormatDropDown,testWSItemTypeDropDown,
                             testWSNumFormatDropDown;
    
    private AppButton removeSampleTypeButton, removePrepTestButton, 
                      removeAnalyteButton, addAnalyteButton,
                      removeReflexTestButton,removeWSItemButton;
    
    private ScreenTableWidget prepTestTable, sampleTypeTable, 
                              testReflexTable,worksheetItemTable ;
    
    private TableWidget reflexTestContoller,prepTestController,
                        wsItemController,sampleTypeController;
    
    private QueryTable reflexTestQueryTable;
    
    //private AutoCompleteDropdown analyteDropDown;
    
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
            /*if(action == KeyListManager.Action.FETCH){                
                key = (DataSet)((Object[])obj)[0];                
                fillTestAnalyteDropDown();
                fillTestResultDropDown();                
                fillModelMap();                             
            }*/
            if(action == State.ADD ||action == State.UPDATE){
                reflexTestContoller.model.enableAutoAdd(true);
                wsItemController.model.enableAutoAdd(true);
                prepTestController.model.enableAutoAdd(true);
                sampleTypeController.model.enableAutoAdd(true);
            }else{
                reflexTestContoller.model.enableAutoAdd(false);
                wsItemController.model.enableAutoAdd(false);
                prepTestController.model.enableAutoAdd(false);
                sampleTypeController.model.enableAutoAdd(false);
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
    }
    
    public void onChange(Widget sender){
        //analyteDropDown = (AutoCompleteDropdown)sender;
        //Integer analyteId = (Integer)analyteDropDown.getSelectedValue();
         //setTestResultsForAnalyte(analyteId);        
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
       removeAnalyteButton = (AppButton)getWidget("removeAnalyteButton");
       addAnalyteButton = (AppButton)getWidget("addAnalyteButton");
       removeReflexTestButton = (AppButton)getWidget("removeReflexTestButton");
       removeWSItemButton = (AppButton)getWidget("removeWSItemButton");
       
       removeAnalyteButton.setEnabled(false);
       addAnalyteButton.setEnabled(false);
       
       if (methodDropdown == null) {
           methodDropdown = (DataModel)initData.get("methods");       
           labelDropdown = (DataModel)initData.get("labels");
           testTrailerDropdown = (DataModel)initData.get("testTrailers");
           scriptletDropDown = (DataModel)initData.get("scriptlets");
           sectionDropDown = (DataModel)initData.get("sections");
           sampleTypeDropDown = (DataModel)initData.get("sampleTypes");
           measureUnitDropDown = (DataModel)initData.get("unitsOfMeasure");
           prepTestDropDown = (DataModel)initData.get("prepTests"); 
           reflexTestFlagsDropDown = (DataModel)initData.get("testReflexFlags");
           revisionMethodDropDown = (DataModel)initData.get("revisionMethods");
           testFormatDropDown = (DataModel)initData.get("testFormats");
           testWSItemTypeDropDown = (DataModel)initData.get("testWSItemTypes");
           testWSNumFormatDropDown = (DataModel)initData.get("testWSNumFormats");
           
       }
       
       drop = (Dropdown)getWidget(TestMeta.getMethodId());
       drop.setModel(methodDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getLabelId());
       drop.setModel(labelDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestTrailerId());
       drop.setModel(testTrailerDropdown);
       
       drop = (Dropdown)getWidget(TestMeta.getScriptletId());
       drop.setModel(scriptletDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getSectionId());
       drop.setModel(sectionDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getRevisionMethodId());
       drop.setModel(revisionMethodDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestFormatId());
       drop.setModel(testFormatDropDown);
       
       drop = (Dropdown)getWidget(TestMeta.getTestWorksheet().getNumberFormatId());
       drop.setModel(testWSNumFormatDropDown);
              
       drop = (Dropdown)getWidget(TestMeta.getTestWorksheet().getScriptletId());
       drop.setModel(scriptletDropDown);
       
       sampleTypeTable = (ScreenTableWidget)widgets.get("sampleTypeTable");
       prepTestTable = (ScreenTableWidget)widgets.get("testPrepTable");
       testReflexTable = (ScreenTableWidget)widgets.get("testReflexTable"); 
       worksheetItemTable = (ScreenTableWidget)widgets.get("worksheetTable");       
       
       sampleTypeController = (TableWidget)sampleTypeTable.getWidget();
       q = (QueryTable)sampleTypeTable.getQueryWidget().getWidget();          
       
       //
       // state dropdown
       //
       ((TableDropdown)sampleTypeController.columns.get(0).getColumnWidget()).setModel(sampleTypeDropDown);
       ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(sampleTypeDropDown);
       
       ((TableDropdown)sampleTypeController.columns.get(1).getColumnWidget()).setModel(measureUnitDropDown);
       ((TableDropdown)q.columns.get(1).getColumnWidget()).setModel(measureUnitDropDown);
       
       //addCommandListener(d.controller);
       
       prepTestController = (TableWidget)prepTestTable.getWidget();
       q = (QueryTable)prepTestTable.getQueryWidget().getWidget();
        
       //
       // state dropdown
       //
       ((TableDropdown)prepTestController.columns.get(0).getColumnWidget()).setModel(prepTestDropDown);
       ((TableDropdown)q.columns.get(0).getColumnWidget()).setModel(prepTestDropDown); 
       
       //addCommandListener(d.controller);
       
       reflexTestContoller = (TableWidget)testReflexTable.getWidget();
       reflexTestQueryTable = (QueryTable)testReflexTable.getQueryWidget().getWidget();
       
                    
       //addCommandListener(reflexTestContoller);
       
       //TestReflexTable reflexTable = (TestReflexTable)reflexTestContoller.manager;
       //reflexTable.setTestForm(this);
       
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
       
       //addCommandListener(d.controller);
       
       updateChain.add(afterUpdate);        
              
       super.afterDraw(success);
       
       
       ((FormRPC)rpc.getField("sampleType")).setFieldValue("sampleTypeTable",
                                             sampleTypeController.model.getData());
       
       ((FormRPC)rpc.getField("worksheet")).setFieldValue("worksheetTable",
                                             wsItemController.model.getData());
                    
       ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testPrepTable",
                                               prepTestController.model.getData());
       
       ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testReflexTable",
                                               reflexTestContoller.model.getData());
              
    }
        
    public void query() {        
        super.query();
        testId.setFocus(true);
        removeAnalyteButton.changeState(ButtonState.DISABLED) ;
        addAnalyteButton.changeState(ButtonState.DISABLED) ;
        removeSampleTypeButton.changeState(ButtonState.DISABLED) ;
        removePrepTestButton.changeState(ButtonState.DISABLED) ;
        removeReflexTestButton.changeState(ButtonState.DISABLED) ; 
        removeWSItemButton.changeState(ButtonState.DISABLED);
    }
    
    public void add() {
        super.add();
        testId.enable(false);
        testName.setFocus(true);
        removeReflexTestButton.changeState(ButtonState.DISABLED) ; 
        removeReflexTestButton.changeState(ButtonState.DISABLED) ; 
        
    }
  
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            testId.enable(false);
            testName.setFocus(true);
            removeReflexTestButton.changeState(ButtonState.DISABLED) ;
        }
    }; 
    
    
    
    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    }
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if(state != FormInt.State.QUERY){
            if (tabIndex == 0 && !((FormRPC)rpc.getField("details")).load) {
                fillTestDetails();
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
    
    public void setTestResultsForAnalyte(Integer analyteId){    
       if(analyteId!=null){            
        /*NumberObject testId = (NumberObject)key.getKey();
        //screenService.getObject("getTestResultModel", new DataObject[] {testId, new NumberObject(analyteId)}, new AsyncCallback() {
          //  public void onSuccess(Object result) {*/                                       
         DataModel model = (DataModel)modelMap.get(analyteId.toString()); 
         //AutoCompleteDropdown dropdown = (AutoCompleteDropdown)((TableAutoDropdown)reflexTestContoller.editors[2]).getWidget();
         //dropdown.clear();
         ((Dropdown)reflexTestContoller.columns.get(2).getColumnWidget()).setModel(model);             
           /* }
            //public void onFailure(Throwable caught) {             
              //  window.setStatus("","");
            }
        });*/
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
       if(state == State.UPDATE || state == State.ADD|| state == State.QUERY)
           return true;       
          return false;
      }

      public boolean canSelect(TableWidget widget, DataSet set, int row) {
       if(state == State.UPDATE || state == State.ADD|| state == State.QUERY)
           return true;       
          return false;
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
       
       screenService.getObject("loadTestDetails", new DataObject[] {key,rpc.getField("details")}, new AsyncCallback() {
           public void onSuccess(Object result) {              
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
       
       screenService.getObject("loadPrepTestsReflexTests", new DataObject[] {key,rpc.getField("prepAndReflex")}, new AsyncCallback() {
           public void onSuccess(Object result) {              
               load((FormRPC)result);
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
       
       screenService.getObject("loadSampleTypes", new DataObject[] {key,rpc.getField("sampleType")}, new AsyncCallback() {
           public void onSuccess(Object result) {              
               load((FormRPC)result);
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
       
       screenService.getObject("loadWorksheetLayout", new DataObject[] {key,rpc.getField("worksheet")}, new AsyncCallback() {
           public void onSuccess(Object result) {              
               load((FormRPC)result);
               rpc.setField("worksheet", (FormRPC)result);
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
       screenService.getObject("getTestAnalyteModel", new DataObject[] {testId}, new AsyncCallback() {
           public void onSuccess(Object result) {              
               DataModel model = (DataModel)result;
               ((Dropdown)reflexTestContoller.columns.get(1).getColumnWidget()).setModel(model);
               
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
        screenService.getObject("getTestResultModel", new DataObject[] {testId}, new AsyncCallback() {
            public void onSuccess(Object result) {              
                DataModel model = (DataModel)result;                
                ((Dropdown)reflexTestContoller.columns.get(2).getColumnWidget()).setModel(model);
                
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
        screenService.getObject("getTestResultModelMap", new DataObject[] {testId}, new AsyncCallback() {
            public void onSuccess(Object result) { 
                DataMap map = (DataMap)result;
                modelMap = (HashMap<String, DataModel>)map.getValue();                
            }
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }      
      
   private void onSampleTypeRowButtonClick() {
     ((TableWidget)sampleTypeTable.getWidget()).model
     .deleteRow(((TableWidget)sampleTypeTable.getWidget()).model.getData().getSelectedIndex());;
   }
   
   private void onPrepTestRowButtonClick() {
       ((TableWidget)prepTestTable.getWidget()).model
        .deleteRow(((TableWidget)prepTestTable.getWidget()).model.getData().getSelectedIndex());
     }
   
   private void onWSItemRowButtonClick() {      
       ((TableWidget)worksheetItemTable.getWidget()).model
       .deleteRow(((TableWidget)worksheetItemTable.getWidget()).model.getData().getSelectedIndex());
     }
   
   private void onReflexTestRowButtonClick() {      
       reflexTestContoller.model.deleteRow(reflexTestContoller.model.getData().getSelectedIndex());
   }
   
   /*public void showError(int row, int col, TableController controller,String error) {
   AbstractField field =  controller.model.getFieldAt(row, col);      
   field.addError(error);
   ((TableCellInputWidget)controller.view.table.getWidget(row,col)).drawErrors();
}*/

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