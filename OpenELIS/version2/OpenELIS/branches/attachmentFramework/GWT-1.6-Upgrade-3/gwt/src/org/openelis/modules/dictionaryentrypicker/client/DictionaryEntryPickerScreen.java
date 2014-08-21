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
package org.openelis.modules.dictionaryentrypicker.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import java.util.ArrayList;

public class DictionaryEntryPickerScreen extends OpenELISScreenForm<DictionaryEntryPickerForm,Query<TableDataRow<Integer>>> implements TableManager, 
                                                                                                                                 ClickListener,
                                                                                                                                 ChangeListener{
    
    private TextBox findTextBox = null;
    
    private TableWidget dictionaryController;
    
    private Dropdown categoryDrop;       
    
    private AppButton findButton;
    
    private ButtonPanel atozButtons = null;
    
    private Integer categoryId;      
    
    private AppButton prevPressed = null;
    
    public ArrayList<TableDataRow<Integer>> selectedRows = null; 
    
    public DictionaryEntryPickerScreen() {        
        super("org.openelis.modules.dictionaryentrypicker.server.DictionaryEntryPickerService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new DictionaryEntryPickerForm());
    }
    
    public void performCommand(Enum action, Object obj) {        
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                if(prevPressed!=null) 
                    atozButtons.setButtonState(prevPressed, AppButton.ButtonState.UNPRESSED);
                String query = baction.substring(6);
                findTextBox.setText(query); 
                prevPressed = (AppButton)obj;
            }else{
                super.performCommand(action, obj);
            }
        }
    }
    
    public void afterDraw(boolean sucess) {        
        window.setStatus("","spinnerIcon");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        addCommandListener(bpanel);
        bpanel.addCommandListener(this);
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        DictionaryEntryPickerDataRPC dedrpc = null;
       
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozButtons);               
        
        prevPressed = null;
        findTextBox = (TextBox)getWidget("findTextBox");
        
        loadCategoryDropdown();
        
        ScreenTableWidget dictTable = (ScreenTableWidget)widgets.get("dictEntTable");        
        dictionaryController  = (TableWidget)dictTable.getWidget();
        
        dictionaryController.model.enableMultiSelect(true);
        
        findButton = (AppButton)getWidget("findButton"); 
        
        super.afterDraw(sucess);              

    }
    
    public void commit() {
        selectedRows = dictionaryController.model.getSelections();
        window.close();  
    }

    public void abort() {
        selectedRows = null;
        window.close();
    }
    
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {        
        return true;
    }

    public void onClick(Widget sender) {
        String queryString = null;
        if(sender == findButton){                     
          queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
          loadDictionaryModel(queryString);
        }
    }
    
    public void onChange(Widget sender){
        if(sender == categoryDrop){
           Integer numObj = (Integer)categoryDrop.getSelections().get(0).key;
           categoryId = numObj;
        }
    }  
    
    private void loadDictionaryModel(String pattern){ 
        DictionaryEntryPickerDataRPC dedrpc = null;
        if(categoryId == null)
            categoryId = new Integer(-1);               
        
        
        window.setStatus("", "spinnerIcon");   
        dedrpc = new DictionaryEntryPickerDataRPC();
        
        dedrpc.id = categoryId;
        dedrpc.model = dictionaryController.model.getData();
        dedrpc.stringValue = pattern;
        
        screenService.call("getDictionaryEntries", dedrpc, new AsyncCallback<DictionaryEntryPickerDataRPC>() {
            public void onFailure(Throwable caught) {                
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }

            public void onSuccess(DictionaryEntryPickerDataRPC result) {                  
                dictionaryController.model.setModel(result.model);
                dictionaryController.model.refresh();
                window.setStatus(consts.get("loadCompleteMessage"),"");
            }
            
        });
        
    }
    
 
        
        
    private void loadCategoryDropdown() {
        DictionaryEntryPickerDataRPC dedrpc = new DictionaryEntryPickerDataRPC();        
        screenService.call("getInitialModel",dedrpc,
                                new AsyncCallback<DictionaryEntryPickerDataRPC>() {
                                    public void onSuccess(DictionaryEntryPickerDataRPC result) {                                       
                                        categoryDrop = (Dropdown)getWidget("category");
                                        categoryDrop.setModel(result.model);    
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                            });
    }
    

}
