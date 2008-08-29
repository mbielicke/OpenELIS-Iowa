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

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.KeyListManager;
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
                             measureUnitDropDown,prepTestDropDown;
    
    private AppButton removeSampleTypeButton, removePrepTestButton;
    
    private ScreenTableWidget prepTestTable;
    
    private ScreenTableWidget sampleTypeTable;
    
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
            super.performCommand(action, obj);
        }
    }
    
    public void onClick(Widget sender) {
        if (sender == removeSampleTypeButton)
            onSampleTypeRowButtonClick();
        else if (sender == removePrepTestButton)
            onPrepTestRowButtonClick();
    }

    public void afterDraw(boolean success) {
        loaded = true;
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        AToZTable atozTable = (AToZTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        AutoCompleteDropdown drop;
        //ScreenTableWidget sw;
        TableWidget d;
        QueryTable q;
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
                
       ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
       removeSampleTypeButton = (AppButton)getWidget("removeSampleTypeButton");
       removePrepTestButton = (AppButton)getWidget("removePrepTestButton");
       
       if (methodDropdown == null) {
           methodDropdown = (DataModel)initData.get("methods");       
           labelDropdown = (DataModel)initData.get("labels");
           testTrailerDropdown = (DataModel)initData.get("testTrailers");
           scriptletDropDown = (DataModel)initData.get("scriptlets");
           sectionDropDown = (DataModel)initData.get("sections");
           sampleTypeDropDown = (DataModel)initData.get("sampleTypes");
           measureUnitDropDown = (DataModel)initData.get("unitsOfMeasure");
           prepTestDropDown = (DataModel)initData.get("prepTests");           
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
       
       sampleTypeTable = (ScreenTableWidget)widgets.get("sampleTypeTable");
       prepTestTable = (ScreenTableWidget)widgets.get("testPrepTable");
       
       d = (TableWidget)sampleTypeTable.getWidget();
       q = (QueryTable)sampleTypeTable.getQueryWidget().getWidget();
       
       //((FormRPC)rpc.getField("sampleAndPrep")).setFieldValue("sampleTypeTable", d.controller.model);
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
       
       testId = (ScreenTextBox)widgets.get(TestMeta.getId());
       testName = (TextBox)getWidget(TestMeta.getName());
       
       updateChain.add(afterUpdate);
              
       super.afterDraw(success);
              
    }
    
    public void query() {
        super.query();
        testId.setFocus(true);
    }
    
    public void add() {
        super.add();
        testId.enable(false);
        testName.setFocus(true);
        
    }
    
    protected AsyncCallback afterAdd = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            testId.enable(false);
            testName.setFocus(true);                             
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
            } else if (tabIndex == 2 && !((FormRPC)rpc.getField("sampleAndPrep")).load) {
                fillSampleTypesPrepTests();
            }
        }
     
        return true;
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
   
   private void fillSampleTypesPrepTests(){
       if(key == null)
           return;
       
       window.setStatus("","spinnerIcon");
       
       screenService.getObject("loadSampleTypesPrepTests", new DataObject[] {key,rpc.getField("sampleAndPrep")}, new AsyncCallback() {
           public void onSuccess(Object result) {              
               load((FormRPC)result);
               rpc.setField("sampleAndPrep", (FormRPC)result);
               window.setStatus("","");

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
        
  }

