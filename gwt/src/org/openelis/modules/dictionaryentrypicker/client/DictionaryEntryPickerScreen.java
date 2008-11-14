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

import java.util.ArrayList;
import java.util.List;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.test.client.TestScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DictionaryEntryPickerScreen extends OpenELISScreenForm implements TableManager, 
                                                                               ClickListener,
                                                                               ChangeListener{
    private TestScreen testScreen;
    
    private static DataModel categoryDropdown;
    private static boolean loaded =  false;
    
    private TextBox findTextBox = null;
    
    private TableWidget dictionaryController;
    
    private Dropdown categoryDrop;       
    
    private AppButton findButton;
    
    private Integer categoryId;
    
    public DictionaryEntryPickerScreen(TestScreen testScreen) {        
        super("org.openelis.modules.dictionaryentrypicker.server.DictionaryEntryPickerService",!loaded);
        this.testScreen = testScreen;
    }
    
    public void performCommand(Enum action, Object obj) {
        if (obj instanceof AppButton) {
            String baction = ((AppButton)obj).action;
            if (baction.startsWith("query:")) {
                String query = baction.substring(6, baction.length());
                findTextBox.setText(query);
            }else{
                super.performCommand(action, obj);
            }
        }
    }
    
    public void commit() {
        List<String> entries = getSelectedEntries();        
        testScreen.addResultRows(entries);
        window.close();
    }

    public void abort() {
        window.close();
    }
    
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           DataSet dropTarget,
                           int targetRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {        
        return true;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     DataSet dropTarget,
                     int targetRow) {
        // TODO Auto-generated method stub

    }

    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub

    }

    public void onClick(Widget sender) {
        if(sender == findButton){                     
            String queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
            FormRPC queryRPC = (FormRPC) this.forms.get("queryByName");
            queryRPC.setFieldValue("findTextBox", queryString);            
            
            StringObject pattern = new StringObject();
            pattern.setValue(queryString);  
          loadDictionaryModel(pattern);
        }
    }
    
    public void onChange(Widget sender){
        if(sender == categoryDrop){
           NumberObject numObj = (NumberObject)categoryDrop.getSelections().get(0).getKey();
           categoryId = (Integer)numObj.getValue();
        }
    }
    
    public void afterDraw(boolean sucess) {
        loaded = true;
        window.setStatus("","spinnerIcon");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        addCommandListener(bpanel);
        bpanel.addCommandListener(this);
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozButtons);
        //chain.addCommand(bpanel);               
        
        findTextBox = (TextBox)getWidget("findTextBox");
        
        if (categoryDropdown == null) {
            categoryDropdown = (DataModel)initData.get("categories");
        }
        
        categoryDrop = (Dropdown)getWidget("category");
        categoryDrop.setModel(categoryDropdown);              
        
        ScreenTableWidget dictTable = (ScreenTableWidget)widgets.get("dictEntTable");        
        dictionaryController  = (TableWidget)dictTable.getWidget();
        
        dictionaryController.model.enableMultiSelect(true);
        
        findButton = (AppButton)getWidget("findButton"); 
        
        super.afterDraw(sucess);
        
        
    }
    
    private void loadDictionaryModel(StringObject pattern){ 
       
        //final String fpattern = pattern;        
        if(categoryId == null)
            categoryId = new Integer(-1);               
        
        final DataModel model = dictionaryController.model.getData();
        screenService.getObject("getDictionaryEntries", new Data[] {model,new NumberObject(categoryId),pattern}, new AsyncCallback<DataModel>() {
            public void onFailure(Throwable caught) {                
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }

            public void onSuccess(DataModel result) {               
                dictionaryController.model.setModel(result);
                dictionaryController.model.refresh();
            }
            
        });
        
    }
    
    private List<String> getSelectedEntries(){
        List<String> entries = new ArrayList<String>();
        for(int iter = 0; iter < dictionaryController.model.getSelections().size();iter++){
            DataSet set = dictionaryController.model.getSelections().get(iter);
            String entry = (String)set.get(0).getValue();
            entries.add(entry);
        }
       return entries; 
    }

}
