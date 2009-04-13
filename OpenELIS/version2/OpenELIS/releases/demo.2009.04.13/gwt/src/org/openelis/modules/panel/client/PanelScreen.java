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

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class PanelScreen extends OpenELISScreenForm<PanelForm,Query<TableDataRow<Integer>>> implements
                                                   ClickListener,
                                                   ChangeListener,
                                                   TableManager {       

    private KeyListManager keyList = new KeyListManager();
        
    private TableWidget addedTestsWidget = null, allTestsWidget = null;
    
    private TableModel addTestModel = null, allTestsTableModel = null; 
        
    private PanelMetaMap PanelMeta = new PanelMetaMap();
    
    private AppButton removeTestButton,moveUpButton, moveDownButton, addTestButton;         
    
    private TextBox panelName = null;
    
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
        super("org.openelis.modules.panel.server.PanelService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new PanelForm());        
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
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");        
        ResultsTable atozTable = (ResultsTable) getWidget("azTable");    
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons"); 
        ScreenTableWidget addedTestsTable = null;
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);               
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);          
        addedTestsTable = (ScreenTableWidget)widgets.get("addedTestTable");
        addedTestsWidget = ((TableWidget)addedTestsTable.getWidget());   
                
        addTestModel = (TableModel)addedTestsWidget.model;                          
        
        removeTestButton = (AppButton)getWidget("removeTestButton");
        moveUpButton = (AppButton)getWidget("moveUpButton");
        moveDownButton = (AppButton)getWidget("moveDownButton");
        addTestButton = (AppButton)getWidget("addTestButton");
        
        allTestsWidget = ((TableWidget)getWidget("allTestsTable"));
        
        setAllTestsDataModel(form.allTests);
        form.allTests = null;
        allTestsTableModel = (TableModel)allTestsWidget.model;
        allTestsTableModel.enableMultiSelect(true);
        
        panelName = (TextBox)getWidget(PanelMeta.getName());
        updateChain.add(afterUpdate);  
                                
        super.afterDraw(success);
        form.addedTestTable.setValue(addTestModel.getData());
                        
  }
    
    public void query() {        
        super.query();  
        
        removeTestButton.changeState(ButtonState.DISABLED) ;
        moveUpButton.changeState(ButtonState.DISABLED) ;
        moveDownButton.changeState(ButtonState.DISABLED) ; 
        allTestsWidget.enabled(false);
    }
    
    public void add(){
        super.add();
        allTestsWidget.enabled(true);
        panelName.setFocus(true);
    } 
    
    public Request commitQuery() {       
       loadAllTestsModel();
       return super.commitQuery();
    }
    
    public void abort(){
        if(state == State.QUERY)
            loadAllTestsModel();
        super.abort();                              
    }
    
    protected AsyncCallback<PanelForm> afterUpdate = new AsyncCallback<PanelForm>() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(PanelForm result) {            
            panelName.setFocus(true);       
            allTestsWidget.enabled(true);
        }
    };           
    
                      
    public boolean canAdd(TableWidget widget,TableDataRow set, int row) {        
        return false;
    }

    public boolean canAutoAdd(TableWidget widget,TableDataRow addRow) {        
       return false;
    }

    public boolean canDelete(TableWidget widget,TableDataRow set, int row) {        
        return false;
    }

    public boolean canEdit(TableWidget widget,TableDataRow set, int row, int col) {        
        if(state == State.QUERY)
            return true;
       return false;
    }

    public boolean canSelect(TableWidget widget,TableDataRow set, int row) {        
       if(state == State.UPDATE || state == State.ADD||state == State.QUERY)
        return true;
       
       return false;
    }
    
    private void getPanels(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) { 
            QueryStringField qField = new QueryStringField(PanelMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
        }
    }
    
    private boolean testAdded(String testName,String methodName){    
         for(int iter = 0; iter <  addTestModel.numRows(); iter++){
             String tname = (String)((DataObject)addTestModel.getRow(iter).getCells().get(0)).getValue();
             String mname = (String)((DataObject)addTestModel.getRow(iter).getCells().get(1)).getValue();
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
         TableDataRow moveUpRow = addTestModel.getRow(selIndex);
         TableDataRow movedownRow = addTestModel.getRow(selIndex-1);
         addTestModel.setRow(selIndex, movedownRow);
         addTestModel.setRow(selIndex-1, moveUpRow);
         addTestModel.selectRow(selIndex-1);
         addTestModel.refresh();
      }  
    }
    
      private void onMoveDownButtonClick(){
          int selIndex = addTestModel.getData().getSelectedIndex();
          if(selIndex < addTestModel.getData().size()-1){         
           TableDataRow moveUpRow = addTestModel.getRow(selIndex+1);
           TableDataRow movedownRow = addTestModel.getRow(selIndex);
           addTestModel.setRow(selIndex+1, movedownRow);
           addTestModel.setRow(selIndex, moveUpRow);
           addTestModel.selectRow(selIndex+1);
           addTestModel.refresh();
        }
      } 
      
       private void addTestButtonClick(){
              int selIndex = allTestsTableModel.getData().getSelectedIndex();
              if(selIndex > -1){         
                  TableDataRow<String> atRow =  (TableDataRow<String>)allTestsTableModel.getData().get(selIndex);
                  String display = atRow.key;                  
                  TableDataRow row = addedTestsWidget.model.createRow();            
                  String[] namesArray= display.split(",");                     
                  if(testAdded(namesArray[0],namesArray[1])){
                    boolean ok = Window.confirm("This test has already been added to the panel." +
                          " Add it anyway?");
                    if(ok){    
                        ((StringField)row.getCells().get(0)).setValue(namesArray[0]);
                        ((StringField)row.getCells().get(1)).setValue(namesArray[1]);    
                        addTestModel.addRow(row);
                        addTestModel.refresh();
                    }
                  }else{                
                      ((StringField)row.getCells().get(0)).setValue(namesArray[0]);
                      ((StringField)row.getCells().get(1)).setValue(namesArray[1]);              
                      addTestModel.addRow(row);
                      addTestModel.refresh();
                  }
          }            
       }
       
       private void setAllTestsDataModel(TableDataModel<TableDataRow<String>> model) {
           allTestsWidget.model.setModel(model);
           allTestsWidget.model.refresh();
           allTestsWidget.enabled(false);             
       }
       
       private void loadAllTestsModel() {
           PanelForm pf = new PanelForm();
           screenService.call("getTestMethodNames", pf, new AsyncCallback<PanelForm>(){
            public void onSuccess(PanelForm result) {
                allTestsWidget.model.load(result.addedTestTable.getValue());           
            }
            
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();            
            }
               
           }) ;
       }
}