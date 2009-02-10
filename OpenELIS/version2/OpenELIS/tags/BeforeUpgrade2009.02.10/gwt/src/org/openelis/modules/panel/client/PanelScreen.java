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
package org.openelis.modules.panel.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class PanelScreen extends OpenELISScreenForm<RPC<Form,Data>,Form> implements
                                                   ClickListener,
                                                   ChangeListener,
                                                   TableManager {
        
    private static boolean loaded = false;

    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTableWidget addedTestsTable = null;
    private TableWidget addedTestsController = null; 
    
    private TableModel addTestModel = null;
    
    private static DataModel allTestsDataModel;
    
    private PanelMetaMap PanelMeta = new PanelMetaMap();
    
    private AppButton removeTestButton,moveUpButton, moveDownButton, addTestButton;
    
    private TableWidget allTestTable;
    
    private TableModel allTestsTableModel;
    
    private TextBox panelName;
    
    public void onClick(Widget sender) {
        if (sender == removeTestButton)
          onTestRowButtonClick(); 
        else if (sender == moveUpButton)
         onMoveUpButtonClick();
        else if (sender == moveDownButton)
         onMoveDownButtonClick();
        else if(sender == addTestButton) 
         addTestButtonClick();
    }
    
    public PanelScreen() {
        super("org.openelis.modules.panel.server.PanelService",!loaded, new RPC<Form,Data>());
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                getPanels(baction.substring(6, baction.length()));
            }else
                super.performCommand(action, obj);
         } else {                       
            super.performCommand(action, obj);
        }
    }
    
    
    public void afterDraw(boolean success) {
        loaded = true;
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        AToZTable atozTable = (AToZTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");        
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
                
        //bpanel.enableButton("delete", false);
        
        if(allTestsDataModel==null){
            allTestsDataModel = (DataModel)initData.get("allTests");
            
        }
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);          
        addedTestsTable = (ScreenTableWidget)widgets.get("addedTestTable");
        addedTestsController = ((TableWidget)addedTestsTable.getWidget());   
                
        addTestModel = (TableModel)addedTestsController.model;                          
        
        removeTestButton = (AppButton)getWidget("removeTestButton");
        moveUpButton = (AppButton)getWidget("moveUpButton");
        moveDownButton = (AppButton)getWidget("moveDownButton");
        addTestButton = (AppButton)getWidget("addTestButton");
        
        allTestTable =  ((TableWidget)getWidget("allTestsTable"));
        
        allTestsTableModel = (TableModel)allTestTable.model;
        allTestsTableModel.enableMultiSelect(true);
        
        panelName = (TextBox)getWidget(PanelMeta.getName());
        updateChain.add(afterUpdate);  
                
        super.afterDraw(success);
        
        allTestTable.model.setModel(allTestsDataModel);
        allTestTable.model.refresh();
        allTestTable.enabled(false);
        form.setFieldValue("addedTestTable",addTestModel.getData());
      
  }
    
    public void query() {        
        super.query();        
        removeTestButton.changeState(ButtonState.DISABLED) ;
        moveUpButton.changeState(ButtonState.DISABLED) ;
        moveDownButton.changeState(ButtonState.DISABLED) ; 
        allTestTable.enabled(false);
    }
    
    public void add(){
        super.add();
        allTestTable.enabled(true);
        panelName.setFocus(true);
    } 
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {            
            panelName.setFocus(true);       
            allTestTable.enabled(true);
        }
    };           
    
                      
    public boolean canAdd(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget,DataSet addRow) {        
       return false;
    }

    public boolean canDelete(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget,DataSet set, int row, int col) {        
        if(state == State.QUERY)
            return true;
       return false;
    }

    public boolean canSelect(TableWidget widget,DataSet set, int row) {        
       if(state == State.UPDATE || state == State.ADD||
                       state == State.QUERY)
        return true;
       
       return false;
    }
    
    private void getPanels(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {            
            Form form = (Form)this.forms.get("queryByLetter");
            form.setFieldValue(PanelMeta.getName(), query);
            commitQuery(form);
        }
    }
    
    private boolean testAdded(String testName,String methodName){    
         for(int iter = 0; iter <  addTestModel.numRows(); iter++){
             String tname = (String)((DataObject)addTestModel.getRow(iter).get(0)).getValue();
             String mname = (String)((DataObject)addTestModel.getRow(iter).get(1)).getValue();
             if(tname.equals(testName.trim())&& mname.equals(methodName)){                 
                return true;
             }
             
         }
        return false; 
    }
    
    private void onTestRowButtonClick(){
        addTestModel.deleteRow(addTestModel.getData().getSelectedIndex());                
    } 
    
    private void onMoveUpButtonClick(){
        int selIndex = addTestModel.getData().getSelectedIndex();
        if(selIndex > 0){         
         DataSet moveUpRow = addTestModel.getRow(selIndex);
         DataSet movedownRow = addTestModel.getRow(selIndex-1);
         addTestModel.setRow(selIndex, movedownRow);
         addTestModel.setRow(selIndex-1, moveUpRow);
         addTestModel.selectRow(selIndex-1);
         addTestModel.refresh();
      }  
    }
    
      private void onMoveDownButtonClick(){
          int selIndex = addTestModel.getData().getSelectedIndex();
          if(selIndex < addTestModel.getData().size()-1){         
           DataSet moveUpRow = addTestModel.getRow(selIndex+1);
           DataSet movedownRow = addTestModel.getRow(selIndex);
           addTestModel.setRow(selIndex+1, movedownRow);
           addTestModel.setRow(selIndex, moveUpRow);
           addTestModel.selectRow(selIndex+1);
           addTestModel.refresh();
        }
      } 
      
       private void addTestButtonClick(){
              int selIndex = allTestsTableModel.getData().getSelectedIndex();
              if(selIndex > -1){         
                  DataSet<Data> atRow =  (DataSet<Data>)allTestsTableModel.getData().get(selIndex);
                  String display = (String)((StringObject)atRow.getKey()).getValue();                  
                  DataSet<Data> row = addedTestsController.model.createRow();            
                  String[] namesArray= display.split(",");                     
                  if(testAdded(namesArray[0],namesArray[1])){
                    boolean ok = Window.confirm("This test has already been added to the panel." +
                          " Add it anyway?");
                    if(ok){    
                        row.get(0).setValue(namesArray[0]);
                        row.get(1).setValue(namesArray[1]);    
                        addTestModel.addRow(row);
                        addTestModel.refresh();
                    }
                  }else{                
                      row.get(0).setValue(namesArray[0]);
                      row.get(1).setValue(namesArray[1]);              
                      addTestModel.addRow(row);
                      addTestModel.refresh();
                  }
          }            
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
        // TODO Auto-generated method stub
        
    }

    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub
        
    }
}