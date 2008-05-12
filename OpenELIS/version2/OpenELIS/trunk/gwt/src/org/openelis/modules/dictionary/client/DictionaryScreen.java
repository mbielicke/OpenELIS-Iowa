package org.openelis.modules.dictionary.client;


import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DictionaryScreen extends OpenELISScreenForm implements ClickListener{

    private EditTable dictEntryController = null;
    private AppButton removeEntryButton = null;
    private TextBox tname = null;

    private ScreenAutoDropdown displaySection = null;
    private static boolean loaded = false;
    
    private static DataModel sectionDropDown = null;

    public DictionaryScreen() {
        super("org.openelis.modules.dictionary.server.DictionaryService", !loaded);
        name = "Dictionary";
    }

    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if (action.startsWith("query:")) {
               getCategories(action.substring(6, action.length()));
           } 
        } else{
            super.onChange(sender);
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
        
        initWidgets();
        super.afterDraw(success);

    }

    public void query() {
        super.query();
    
        // set focus to the name field       
        tname.setFocus(true);
    
    
        removeEntryButton.changeState(AppButton.DISABLED);
    }

    public void add() {
        dictEntryController.setAutoAdd(true);
    
        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryController.manager);
        dictEntManager.resetLists();
    
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
    

    public void commitAdd() {
        dictEntryController.setAutoAdd(false);
        super.commitAdd();
    }    

    public void commitUpdate() {
    	dictEntryController.setAutoAdd(false);
        //we need to do this reset to get rid of the last row
        dictEntryController.reset();
    	super.commitUpdate();
    }

    private void getCategories(String query) {
        if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

            FormRPC letterRPC = (FormRPC)this.forms.get("queryByLetter");
            letterRPC.setFieldValue("category.name", query);
            
            commitQuery(letterRPC);
        }
    }

    private void initWidgets() {
        message.setText("Done");

        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
                
        dictEntryController = ((TableWidget)getWidget("dictEntTable")).controller;
        ((DictionaryEntriesTable)dictEntryController.manager).setDictionaryForm(this);

        dictEntryController.setAutoAdd(false);

        tname = (TextBox)getWidget("category.name");
        removeEntryButton = (AppButton)getWidget("removeEntryButton");
                
        displaySection = (ScreenAutoDropdown)widgets.get("category.section");       
        
        if (sectionDropDown == null) {
            sectionDropDown = (DataModel)initData.get("categories");
        }

        ((AutoCompleteDropdown)displaySection.getWidget()).setModel(sectionDropDown);
        
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