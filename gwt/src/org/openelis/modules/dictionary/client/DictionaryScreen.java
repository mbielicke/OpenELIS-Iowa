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
package org.openelis.modules.dictionary.client;


import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class DictionaryScreen extends OpenELISScreenForm implements ClickListener, TableManager{

    private TableWidget dictEntryController = null;
    private AppButton removeEntryButton = null;
    //private TextBox tname = null;
    private KeyListManager keyList = new KeyListManager();

    private Dropdown displaySection = null;
    private static boolean loaded = false;
    
    private static DataModel sectionDropDown = null;
        
    private CategoryMetaMap CatMap = new CategoryMetaMap();
    public DictionaryScreen() {
        super("org.openelis.modules.dictionary.server.DictionaryService", !loaded);
    }

    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){           
           String baction = ((AppButton)obj).action;           
           if (baction.startsWith("query:")) {
               getCategories(baction.substring(6, baction.length()));
           }else               
               super.performCommand(action, obj);
        } else{
            if(action == State.ADD ||action == State.UPDATE){
                dictEntryController.model.enableAutoAdd(true);
             }else{
                dictEntryController.model.enableAutoAdd(false); 
             }
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        
        String action = ((AppButton)sender).action;        
         if (action.equals("removeEntry")) {
             onRemoveRowButtonClick();            
        }
    }

    public void afterDraw(boolean success) {       
        AToZTable atozTable = (AToZTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
               
        dictEntryController = ((TableWidget)getWidget("dictEntTable"));

        

        //tname = (TextBox)getWidget(CatMap.getName());
        startWidget = (ScreenInputWidget)widgets.get(CatMap.getName());
        removeEntryButton = (AppButton)getWidget("removeEntryButton");
                
        displaySection = (Dropdown)getWidget(CatMap.getSectionId());       
        
        if (sectionDropDown == null) {
            sectionDropDown = (DataModel)initData.get("sections");
        }

        displaySection.setModel(sectionDropDown);
        
        super.afterDraw(success);
        
        loaded = true;
        rpc.setFieldValue("dictEntTable",dictEntryController.model.getData());
    }

    public void query() {
        super.query();
        removeEntryButton.changeState(ButtonState.DISABLED);
    }

    private void getCategories(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

            FormRPC letterRPC = (FormRPC)this.forms.get("queryByLetter");
            letterRPC.setFieldValue(CatMap.getName(), query);
            
            commitQuery(letterRPC);            
        }
    }
    
    private void onRemoveRowButtonClick(){
        ((TableWidget)dictEntryController.getWidget()).model
        .deleteRow(((TableWidget)dictEntryController.getWidget()).model.getData().getSelectedIndex());;       
    }     
            

   /* public void showError(int row, int col, TableController controller,String error) {
         AbstractField field =  controller.model.getFieldAt(row, col);      
         field.addError(error);
         ((TableCellInputWidget)controller.view.table.getWidget(row,col)).drawErrors();
    }*/


    public boolean canAdd(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean canAutoAdd(TableWidget widget,DataSet row) {        
        return row.get(0).getValue() != null;
    }


    public boolean canDelete(TableWidget widget,DataSet set, int row) {
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
    
}
