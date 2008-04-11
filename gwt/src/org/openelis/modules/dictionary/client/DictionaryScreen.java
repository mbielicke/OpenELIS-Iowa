package org.openelis.modules.dictionary.client;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
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

public class DictionaryScreen extends OpenELISScreenForm implements
                                                        MouseListener {

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

    private Widget selected;

    public void afterDraw(boolean success) {       

        loaded = true;
        
        bpanel = (ButtonPanel)getWidget("buttons");
        message.setText("done");

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
        removeEntryButton.addClickListener(this);
        removeEntryButton.changeState(AppButton.DISABLED);
                
        displaySection = (ScreenAutoDropdown)widgets.get("category.section");       
        
        loadDropdowns();
        super.afterDraw(success);

    }

    public void up() {

        dictEntryController.setAutoAdd(true);

        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryController.manager);
        dictEntManager.resetLists();

        dictEntManager.createLists(dictEntryController);
        
        
        super.up();
    }

    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        removeEntryButton.changeState(AppButton.UNPRESSED);

        // set focus to the name field
        tname.setFocus(true);
        
    }
    

    public void commitAdd() {

        dictEntryController.unselect(-1);

        super.commitAdd();
        removeEntryButton.changeState(AppButton.DISABLED);

        dictEntryController.setAutoAdd(false);
    }

    public void afterCommitAdd(boolean success) {
        Integer categoryId = (Integer)rpc.getFieldValue("category.id");
        NumberObject categoryIdObj = new NumberObject();
        categoryIdObj.setType("integer");
        categoryIdObj.setValue(categoryId);

        // done because key is set to null in AppScreenForm for the add
        // operation
        if (key == null) {
            key = new DataSet();
            key.addObject(categoryIdObj);
        } else {
            key.setObject(0, categoryIdObj);
        }
        super.afterCommitAdd(success);
    }

    public void commitUpdate() {

        dictEntryController.unselect(-1);

        super.commitUpdate();
        removeEntryButton.changeState(AppButton.DISABLED);

        dictEntryController.setAutoAdd(false);

    }

    public void abort() {
        removeEntryButton.changeState(AppButton.DISABLED);
        try {

            dictEntryController.setAutoAdd(false);

            super.abort();

        } catch (Exception ex) {
            Window.alert(ex.getMessage());
        }
    }

    public void onChange(Widget sender) {
        
        if(sender == getWidget("atozButtons")){           
           String action = ((ButtonPanel)sender).buttonClicked.action;           
           if (action.startsWith("query:")) {
               getCategories(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);
           } 
        } else{
            super.onChange(sender);
           }
       
    }   
    
    public void onClick(Widget sender) {
        String action = ((AppButton)sender).action;
         if (action.equals("removeEntry")) {

            int selectedRow = dictEntryController.selected;

            if (selectedRow > -1 && dictEntryController.model.numRows() > 1) {
                TableRow row = dictEntryController.model.getRow(selectedRow);

                row.setShow(false);
                // delete the last row of the table because it is autoadd

                dictEntryController.model.deleteRow(dictEntryController.model.numRows() - 1);

                // reset the model
                dictEntryController.reset();
                // need to set the deleted flag to "Y" also
                StringField deleteFlag = new StringField();
                deleteFlag.setValue("Y");

                row.addHidden("deleteFlag", deleteFlag);
            }
        }
    }

    private void getCategories(String letter, Widget sender) {
        // we only want to allow them to select a letter if they are in display
        // mode..
        if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

            FormRPC letterRPC = (FormRPC)this.forms.get("queryByLetter");
            letterRPC.setFieldValue("category.name", letter.toUpperCase() + "*"
                                            + " | "
                                            + letter.toLowerCase()
                                            + "*");
            
            commitQuery(letterRPC);



        }
    }

    protected void setStyleNameOnButton(Widget sender) {
        ((AppButton)sender).changeState(AppButton.PRESSED);
        if (selected != null)
            ((AppButton)selected).changeState(AppButton.UNPRESSED);
        selected = sender;
    }

    public void add() {
        dictEntryController.setAutoAdd(true);

        DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryController.manager);
        dictEntManager.resetLists();

        super.add();        

        removeEntryButton.changeState(AppButton.UNPRESSED);

        // set focus to the name field
        // tname = (TextBox)getWidget("name");
        tname.setFocus(true);

        

    }

    public void query() {
        super.query();

        // set focus to the name field
       
        tname.setFocus(true);

      
        removeEntryButton.changeState(AppButton.DISABLED);
    }
    

    public void checkSystemName(Integer id, String systemName, int row) {
        final Integer entryId = id;

        final int trow = row;
        StringObject sysNameObj = new StringObject();
        sysNameObj.setValue(systemName);

        DataObject[] args = {sysNameObj};
        screenService.getObject("getEntryIdForSystemName",
                                args,
                                new AsyncCallback() {
                                    boolean hasError = false;

                                    public void onSuccess(Object result) {
                                        NumberField idField = (NumberField)result;
                                        Integer retId = (Integer)idField.getValue();
                                        if (retId != null) {
                                            if (entryId != null) {
                                                if (!entryId.equals(retId)) {
                                                    hasError = true;
                                                }
                                            } else {
                                                hasError = true;
                                            }
                                        }

                                        if (hasError) {
                                            DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryController.manager);
                                            String dictSystemNameError = "An entry with this System Name already exists in the database.Please choose some other name";
                                           
                                            dictEntManager.showError(trow,
                                                                     1,
                                                                     dictEntryController,
                                                                     dictSystemNameError);
                                        }

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                    }
                                });

    }

    public void checkEntry(Integer id, String entry, int row) {
        final Integer entryId = id;
        final int trow = row;
        StringObject entryObj = new StringObject();
        entryObj.setValue(entry);

        DataObject[] args = {entryObj};

        screenService.getObject("getEntryIdForEntry",
                                args,
                                new AsyncCallback() {
                                    boolean hasError = false;

                                    public void onSuccess(Object result) {
                                        NumberField idField = (NumberField)result;
                                        Integer retId = (Integer)idField.getValue();
                                        if (retId != null) {
                                            if (entryId != null) {
                                                if (!entryId.equals(retId)) {
                                                    hasError = true;
                                                }
                                            } else {
                                                hasError = true;
                                            }
                                        }

                                        if (hasError) {
                                            DictionaryEntriesTable dictEntManager = ((DictionaryEntriesTable)dictEntryController.manager);
                                            String dictEntryError = "An entry with this Entry text already exists in the database.Please choose some other text";
                                            
                                            dictEntManager.showError(trow,
                                                                     3,
                                                                     dictEntryController,
                                                                     dictEntryError);
                                        }
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                    }
                                });

    }

    private void loadDropdowns() {
        if (sectionDropDown == null) {
            sectionDropDown = (DataModel)initData[0];
        }

       

        ((AutoCompleteDropdown)displaySection.getWidget()).setModel(sectionDropDown);
        
    }

    public boolean validate() {     
        boolean entryError = false;        
        for (int iter = 0; iter < dictEntryController.model.numRows(); iter++) {            
            StringField efield = (StringField)dictEntryController.model.getFieldAt(iter,
                                                                                   3);
            if (efield.getValue() != null) {
                if ((efield.getValue().toString().trim().equals(""))) {
                    efield.addError("Field is required");
                }
            } else {
                efield.addError("Field is required");
            }

            if (!(efield.getErrors().length == 0)) {
                entryError = true;
            }
          
        }

        if (entryError) {
            return false;
        } else {
            return true;
        }
    }
      
}