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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.test.client.TestScreen;

import java.util.ArrayList;
import java.util.List;

public class DictionaryEntryPickerScreen extends OpenELISScreenForm<DictionaryEntryPickerRPC,DictionaryEntryPickerForm,Integer> implements TableManager, 
                                                                                                                                 ClickListener,
                                                                                                                                 ChangeListener{
    private TestScreen testScreen;    
    
    private TextBox findTextBox = null;
    
    private TableWidget dictionaryController;
    
    private Dropdown categoryDrop;       
    
    private AppButton findButton;
    
    private ButtonPanel atozButtons = null;
    
    private Integer categoryId, dictId;
    
    private AppButton prevPressed = null;
    
    public DictionaryEntryPickerScreen(TestScreen testScreen) {        
        super("org.openelis.modules.dictionaryentrypicker.server.DictionaryEntryPickerService");
        forms.put("display", new DictionaryEntryPickerForm());
        getScreen(new DictionaryEntryPickerRPC());
        this.testScreen = testScreen;
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
        
        dedrpc = new DictionaryEntryPickerDataRPC();
        dedrpc.stringValue = "test_res_type_dictionary";
        
        screenService.call("getEntryId",dedrpc,
                new AsyncCallback<DictionaryEntryPickerDataRPC>() {
                   public void onSuccess(DictionaryEntryPickerDataRPC result) {                                                                                                                           
                      dictId = result.key;
                    }
                   public void onFailure(Throwable caught) {
                     Window.alert(caught.getMessage());
                     window.setStatus("","");
                    }
       });
    }
    
    public void commit() {
        List<Integer> idList = new ArrayList<Integer>();
        List<String> strlist = getSelectedEntries(idList);
        DataSet set = new DataSet(new IntegerObject(dictId));
        window.close();
        testScreen.addResultRows(strlist,set,idList);        
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
        String queryString = null;
        Form queryRPC = null;
        if(sender == findButton){                     
          queryString = findTextBox.getText()+(findTextBox.getText().endsWith("*") ? "" : "*");
          queryRPC = (Form) forms.get("queryByName");
          queryRPC.setFieldValue("findTextBox", queryString);      
          loadDictionaryModel(queryString);
        }
    }
    
    public void onChange(Widget sender){
        if(sender == categoryDrop){
           IntegerObject numObj = (IntegerObject)categoryDrop.getSelections().get(0).getKey();
           categoryId = numObj.getValue();
        }
    }  
    
    private void loadDictionaryModel(String pattern){ 
        DictionaryEntryPickerDataRPC dedrpc = null;
        if(categoryId == null)
            categoryId = new Integer(-1);               
        
        
        window.setStatus("", "spinnerIcon");   
        dedrpc = new DictionaryEntryPickerDataRPC();
        
        dedrpc.key = categoryId;
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
    
    private List<String> getSelectedEntries(List<Integer> idList){
        List<String> entries = new ArrayList<String>();
        DataSet<Object> set = null;
        String entry = null;
        Integer id = null;
        DataMap data = null;
        for(int iter = 0; iter < dictionaryController.model.getSelections().size();iter++){
            set = dictionaryController.model.getSelections().get(iter);
            data = (DataMap)set.getData();
            id = ((IntegerField)data.get("id")).getValue();
            entry = (String)((Field)set.get(0)).getValue();            
            
            if(!idList.contains(id)) {
             idList.add(id);
             entries.add(entry);
            } 
        }
       return entries; 
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
