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
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.event.CommandListenerCollection;
import org.openelis.gwt.event.SourcesCommandEvents;
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
                                                                                                                                 SourcesCommandEvents{
    
    private TextBox findTextBox;
    
    private TableWidget dictionaryController;
    
    private Dropdown categoryDrop;       
    
    private AppButton findButton,prevPressed;
    
    private ButtonPanel atozButtons;      
   
    public enum Action {COMMIT,ABORT};
    
    public ArrayList<TableDataRow<Integer>> selectedRows; 
    
    
    public DictionaryEntryPickerScreen() {        
        super("org.openelis.modules.dictionaryentrypicker.server.DictionaryEntryPickerService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new DictionaryEntryPickerForm());
    }
    
    public void performCommand(Enum action, Object obj) {   
        String baction, query;
        if (obj instanceof AppButton) {
            baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                if(prevPressed!=null) 
                    atozButtons.setButtonState(prevPressed, AppButton.ButtonState.UNPRESSED);
                query = baction.substring(6);                
                findTextBox.setText(query); 
                prevPressed = (AppButton)obj;                                          
                loadDictionaryModel(query);
            }else{
                super.performCommand(action, obj);
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        ButtonPanel bpanel;
        ScreenTableWidget dictTable;
        CommandChain chain;
        
        window.setStatus("","spinnerIcon");
        
        bpanel = (ButtonPanel) getWidget("buttons");
        dictTable = (ScreenTableWidget)widgets.get("dictEntTable");        
        dictionaryController  = (TableWidget)dictTable.getWidget();
        
        addCommandListener(bpanel);
        bpanel.addCommandListener(this);
        atozButtons = (ButtonPanel)getWidget("atozButtons");
       
        chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozButtons);                               
        
        prevPressed = null;
        findTextBox = (TextBox)getWidget("findTextBox");        
        
        categoryDrop = (Dropdown)getWidget("category");        
        categoryDrop.setModel(form.categoryModel);                
        
        dictionaryController.model.enableMultiSelect(true);
        
        findButton = (AppButton)getWidget("findButton"); 
        
        super.afterDraw(sucess);                   
    }
    
    public void commit() {
        selectedRows = dictionaryController.model.getSelections();   
        if(commandListeners != null)
            commandListeners.fireCommand(Action.COMMIT,selectedRows);     
        window.close();
    }

    public void abort() {
        selectedRows = null;
        if(commandListeners != null)
            commandListeners.fireCommand(Action.ABORT,selectedRows);
        window.close();
    }
    
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {        
        return true;
    }

    public void onClick(Widget sender) {
        String queryString;
        if(sender == findButton){                     
          queryString = findTextBox.getText();   
          if(queryString != null && !"".equals(queryString.trim()))
              loadDictionaryModel(queryString);
        }
    }
    
    public void addCommandListener(CommandListener listener) {
        if(commandListeners == null){
            commandListeners = new CommandListenerCollection();
        }
        commandListeners.add(listener);
    }
    
    private void loadDictionaryModel(String pattern){ 
        DictionaryEntryPickerDataRPC dedrpc;
        Integer categoryId;
        
        categoryId = null;
        
        if(categoryDrop.getSelections().size() > 0)
            categoryId = (Integer)categoryDrop.getSelections().get(0).key;
        
        if(categoryId == null) {                          
            Window.alert(consts.get("plsSelCat"));
            return;
        }
        
        window.setStatus("", "spinnerIcon");   
        dedrpc = new DictionaryEntryPickerDataRPC();
        
        dedrpc.id = categoryId;
        dedrpc.dictionaryTableModel = dictionaryController.model.getData();
        dedrpc.stringValue = pattern;
        
        screenService.call("getDictionaryEntries", dedrpc, new AsyncCallback<DictionaryEntryPickerDataRPC>() {
            public void onFailure(Throwable caught) {                
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }

            public void onSuccess(DictionaryEntryPickerDataRPC result) {                  
                dictionaryController.model.setModel(result.dictionaryTableModel);
                dictionaryController.model.refresh();
                window.setStatus(consts.get("loadCompleteMessage"),"");
            }
            
        });
        
    }    

}
