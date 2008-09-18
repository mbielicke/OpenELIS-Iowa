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
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
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
                                                  ChangeListener{                                                  
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
    
    private EditTable reflexTestContoller;
    
    private QueryTable reflexTestQueryTable;
    
    private AutoCompleteDropdown analyteDropDown;
    
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
        analyteDropDown = (AutoCompleteDropdown)sender;
        Integer analyteId = (Integer)analyteDropDown.getSelectedValue();
         setTestResultsForAnalyte(analyteId);        
       }

    public void afterDraw(boolean success) {
        loaded = true;
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        AToZTable atozTable = (AToZTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        AutoCompleteDropdown drop;

        TableWidget d;
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
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getMethodId());
       drop.setModel(methodDropdown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getLabelId());
       drop.setModel(labelDropdown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getTestTrailerId());
       drop.setModel(testTrailerDropdown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getScriptletId());
       drop.setModel(scriptletDropDown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getSectionId());
       drop.setModel(sectionDropDown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getRevisionMethodId());
       drop.setModel(revisionMethodDropDown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getTestFormatId());
       drop.setModel(testFormatDropDown);
       
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getTestWorksheet().getNumberFormatId());
       drop.setModel(testWSNumFormatDropDown);
              
       drop = (AutoCompleteDropdown)getWidget(TestMeta.getTestWorksheet().getScriptletId());
       drop.setModel(scriptletDropDown);
       
       sampleTypeTable = (ScreenTableWidget)widgets.get("sampleTypeTable");
       prepTestTable = (ScreenTableWidget)widgets.get("testPrepTable");
       testReflexTable = (ScreenTableWidget)widgets.get("testReflexTable"); 
       worksheetItemTable = (ScreenTableWidget)widgets.get("worksheetTable");       
       
       d = (TableWidget)sampleTypeTable.getWidget();
       q = (QueryTable)sampleTypeTable.getQueryWidget().getWidget();
       
       
       //
       // state dropdown
       //
       ((TableAutoDropdown)d.controller.editors[0]).setModel(sampleTypeDropDown);
       ((TableAutoDropdown)q.editors[0]).setModel(sampleTypeDropDown);
       
       ((TableAutoDropdown)d.controller.editors[1]).setModel(measureUnitDropDown);
       ((TableAutoDropdown)q.editors[1]).setModel(measureUnitDropDown);
       
       addCommandListener(d.controller);
       
       d = (TableWidget)prepTestTable.getWidget();
       q = (QueryTable)prepTestTable.getQueryWidget().getWidget();
       //
       // state dropdown
       //
       ((TableAutoDropdown)d.controller.editors[0]).setModel(prepTestDropDown);
       ((TableAutoDropdown)q.editors[0]).setModel(prepTestDropDown); 
       
       addCommandListener(d.controller);
       
       d = (TableWidget)testReflexTable.getWidget();
       reflexTestQueryTable = (QueryTable)testReflexTable.getQueryWidget().getWidget();
       
       reflexTestContoller = d.controller;       
       //addCommandListener(reflexTestContoller);
       
       TestReflexTable reflexTable = (TestReflexTable)reflexTestContoller.manager;
       reflexTable.setTestForm(this);
       
       ((TableAutoDropdown)reflexTestContoller.editors[0]).setModel(prepTestDropDown);
       ((TableAutoDropdown)reflexTestQueryTable.editors[0]).setModel(prepTestDropDown); 
       
       ((TableAutoDropdown)reflexTestContoller.editors[3]).setModel(reflexTestFlagsDropDown);
       ((TableAutoDropdown)reflexTestQueryTable.editors[3]).setModel(reflexTestFlagsDropDown);              
              
       testId = (ScreenTextBox)widgets.get(TestMeta.getId());
       testName = (TextBox)getWidget(TestMeta.getName());
       
       d = (TableWidget)worksheetItemTable.getWidget();
       q = (QueryTable)worksheetItemTable.getQueryWidget().getWidget();
       
       ((TableAutoDropdown)d.controller.editors[1]).setModel(testWSItemTypeDropDown);
       ((TableAutoDropdown)q.editors[1]).setModel(testWSItemTypeDropDown); 
       
       addCommandListener(d.controller);
       
       updateChain.add(afterUpdate);       
              
       super.afterDraw(success);
              
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
        
    }
  
    protected AsyncCallback afterAdd = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            testId.enable(false);
            testName.setFocus(true);            
            removeReflexTestButton.changeState(ButtonState.DISABLED) ;      
        }
    };
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            testId.enable(false);
            testName.setFocus(true);            
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
         ((TableAutoDropdown)reflexTestContoller.editors[2]).setModel(model);             
           /* }
            //public void onFailure(Throwable caught) {
             //   Window.alert(caught.getMessage());
              //  window.setStatus("","");
            }
        });*/
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
               ((TableAutoDropdown)reflexTestContoller.editors[1]).setModel(model);
               
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
                ((TableAutoDropdown)reflexTestContoller.editors[2]).setModel(model);
                
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
     EditTable controller = ((TableWidget)sampleTypeTable.getWidget()).controller;
     
       int selectedRow = controller.selected;
       if (selectedRow > -1 && controller.model.numRows() > 0) {
           TableRow row = controller.model.getRow(selectedRow);
           controller.model.hideRow(row);
           
           // reset the model
           controller.reset();
           // need to set the deleted flag to "Y" also
           StringField deleteFlag = new StringField();
           deleteFlag.setValue("Y");

           row.addHidden("deleteFlag", deleteFlag);
       }
   }
   
   private void onPrepTestRowButtonClick() {
       EditTable controller = ((TableWidget)prepTestTable.getWidget()).controller;
       
         int selectedRow = controller.selected;
         if (selectedRow > -1 && controller.model.numRows() > 0) {
             TableRow row = controller.model.getRow(selectedRow);
             controller.model.hideRow(row);
             
             // reset the model
             controller.reset();
             // need to set the deleted flag to "Y" also
             StringField deleteFlag = new StringField();
             deleteFlag.setValue("Y");

             row.addHidden("deleteFlag", deleteFlag);
         }
     }
   
   private void onWSItemRowButtonClick() {      
       EditTable controller = ((TableWidget)worksheetItemTable.getWidget()).controller;
         int selectedRow = controller.selected;
         if (selectedRow > -1 && controller.model.numRows() > 0) {
             TableRow row = controller.model.getRow(selectedRow);
             controller.model.hideRow(row);
             
             // reset the model
             controller.reset();
             // need to set the deleted flag to "Y" also
             StringField deleteFlag = new StringField();
             deleteFlag.setValue("Y");

             row.addHidden("deleteFlag", deleteFlag);
         }
     }
   
   private void onReflexTestRowButtonClick() {      
       
       int selectedRow = reflexTestContoller.selected;
       if (selectedRow > -1 && reflexTestContoller.model.numRows() > 0) {
           TableRow row = reflexTestContoller.model.getRow(selectedRow);
           reflexTestContoller.model.hideRow(row);
           
           // reset the model
           reflexTestContoller.reset();
           // need to set the deleted flag to "Y" also
           StringField deleteFlag = new StringField();
           deleteFlag.setValue("Y");

           row.addHidden("deleteFlag", deleteFlag);
       }
   }
 }  

