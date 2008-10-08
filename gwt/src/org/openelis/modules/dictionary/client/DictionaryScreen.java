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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class DictionaryScreen extends OpenELISScreenForm implements ClickListener{

    private EditTable dictEntryController = null;
    private AppButton removeEntryButton = null;
    private TextBox tname = null;
    private KeyListManager keyList = new KeyListManager();

    private ScreenAutoDropdown displaySection = null;
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
               
        dictEntryController = ((TableWidget)getWidget("dictEntTable")).controller;
        ((DictionaryEntriesTable)dictEntryController.manager).setDictionaryForm(this);
        dictEntryController.setAutoAdd(false);
        addCommandListener(dictEntryController);

        tname = (TextBox)getWidget(CatMap.getName());
        startWidget = (ScreenInputWidget)widgets.get(CatMap.getName());
        removeEntryButton = (AppButton)getWidget("removeEntryButton");
                
        displaySection = (ScreenAutoDropdown)widgets.get(CatMap.getSectionId());       
        
        if (sectionDropDown == null) {
            sectionDropDown = (DataModel)initData.get("sections");
        }

        ((AutoCompleteDropdown)displaySection.getWidget()).setModel(sectionDropDown);
        
        super.afterDraw(success);
        
        loaded = true;

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
        int selectedRow = dictEntryController.selected;            
        
        if (selectedRow > -1 && dictEntryController.model.numRows() > 0) {
            TableRow row = dictEntryController.model.getRow(selectedRow);
            
            dictEntryController.model.hideRow(row);

            // reset the model
            dictEntryController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
}
