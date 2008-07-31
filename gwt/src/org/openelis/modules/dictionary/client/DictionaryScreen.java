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
package org.openelis.modules.dictionary.client;


import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
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

        loaded = true;
        
        setBpanel((ButtonPanel)getWidget("buttons"));

        AToZTable atozTable = (AToZTable) getWidget("hideablePanel");
        modelWidget.addCommandListener(atozTable);
        addCommandListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addCommandListener(this);
                
        dictEntryController = ((TableWidget)getWidget("dictEntTable")).controller;
        ((DictionaryEntriesTable)dictEntryController.manager).setDictionaryForm(this);
        dictEntryController.setAutoAdd(false);

        tname = (TextBox)getWidget(CatMap.getName());
        removeEntryButton = (AppButton)getWidget("removeEntryButton");
                
        displaySection = (ScreenAutoDropdown)widgets.get(CatMap.getSectionId());       
        
        if (sectionDropDown == null) {
            sectionDropDown = (DataModel)initData.get("sections");
        }

        ((AutoCompleteDropdown)displaySection.getWidget()).setModel(sectionDropDown);
        
        super.afterDraw(success);

    }

    public void query() {
        super.query();
    
        // set focus to the name field       
        tname.setFocus(true);
    
    
        removeEntryButton.changeState(ButtonState.DISABLED);
    }

    public void add() {
        dictEntryController.setAutoAdd(true);
    
        super.add();        
    
        // set focus to the name field
        tname.setFocus(true);
               
    }

    public void update() {

        dictEntryController.setAutoAdd(true);        
        
        super.update();
    }

    public void abort() {
        dictEntryController.setAutoAdd(false);
        super.abort();
    }

    public void afterUpdate(boolean success) {
        super.afterUpdate(success);

        // set focus to the name field
        tname.setFocus(true);
        
    }
    

    public Request commitAdd() {
        dictEntryController.setAutoAdd(false);
        return super.commitAdd();
    }    

    public Request commitUpdate() {
        dictEntryController.setAutoAdd(false);
        //we need to do this reset to get rid of the last row
        dictEntryController.reset();
        return super.commitUpdate();
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
