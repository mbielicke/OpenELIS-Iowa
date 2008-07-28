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
package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InventoryItemScreen extends OpenELISScreenForm implements TableManager, ClickListener, TabListener{

    private boolean startedLoadingTable = false;
    private AppButton        removeComponentButton, standardNoteButton;
	private ScreenTextBox nameTextbox;
    TextBox subjectBox; 
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
	
	private EditTable componentsController, locsController;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenCheck isActive, isSerializedCheck;
    
    private static boolean loaded = false;
    private static DataModel storesDropdown, categoriesDropdown,
                    purchasedUnitsDropdown, dispensedUnitsDropdown;
    
    private boolean          loadComponents = true, 
                             loadLocations = true, 
                             loadComments = true,
                             clearComponents = false, 
                             clearLocations = false, 
                             clearComments = false;
    
    private ScreenVertical   svp       = null;
	
    private InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
	public InventoryItemScreen() {
        super("org.openelis.modules.inventoryItem.server.InventoryItemService",!loaded);
	}

    public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getInventories(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
           }
        }else{
            super.onChange(sender);
        }
    }
    
	public void onClick(Widget sender) {
		if(sender instanceof ScreenMenuItem){
        	if("duplicateRecord".equals(((String)((ScreenMenuItem)sender).objClass))){
                onDuplicateRecordClick();
        	}
        }else if(sender == removeComponentButton){
            onRemoveComponentRowButtonClick();
		}else if(sender == standardNoteButton){
			onStandardNoteButtonClick();
		}
	}
	
	public void afterDraw(boolean success) {
        AutoCompleteDropdown drop;
        
        loaded = true;
        
		setBpanel((ButtonPanel) getWidget("buttons"));

        AToZTable atozTable = (AToZTable) getWidget("azTable");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        removeComponentButton = (AppButton)getWidget("removeComponentButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");
        
        isActive = (ScreenCheck)widgets.get(InvItemMeta.getIsActive());
        isSerializedCheck = (ScreenCheck)widgets.get(InvItemMeta.getIsSerialMaintained());
        
        duplicateMenuPanel = (ScreenMenuPanel)widgets.get("optionsMenu");
        
        nameTextbox = (ScreenTextBox) widgets.get(InvItemMeta.getName());
        idTextBox = (ScreenTextBox) widgets.get(InvItemMeta.getId());
        noteText = (ScreenTextArea) widgets.get(InvItemMeta.ITEM_NOTE.getText());
        subjectBox = (TextBox)getWidget(InvItemMeta.ITEM_NOTE.getSubject()); 
        
        locsController = ((TableWidget)getWidget("locQuantitiesTable")).controller;
		locsController.setAutoAdd(false);
        ((InventoryLocationsTable)locsController.manager).setInventoryForm(this);
		
		componentsController = ((TableWidget)getWidget("componentsTable")).controller;
		componentsController.setAutoAdd(false);
       
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        if (storesDropdown == null) {
            storesDropdown = (DataModel)initData.get("stores");
            categoriesDropdown = (DataModel)initData.get("categories");
            purchasedUnitsDropdown = (DataModel)initData.get("units");
            dispensedUnitsDropdown = (DataModel)initData.get("units");
        }
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getStoreId());
        drop.setModel(storesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getCategoryId());
        drop.setModel(categoriesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getPurchasedUnitsId());
        drop.setModel(purchasedUnitsDropdown);

        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getDispensedUnitsId());
        drop.setModel(dispensedUnitsDropdown);
        
		super.afterDraw(success);			
	}
    
    /*
     * Overriden to reset the data in Contact and Note tabs
     */
    public void afterFetch(boolean success) {
        if (success) {
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
            loadTabs();
        }
        super.afterFetch(success);
    }
	
	public void add() {
		componentsController.setAutoAdd(true);
		
		super.add();
		
		nameTextbox.setFocus(true);
		
		idTextBox.enable(false);
        clearComments();
        
        ((CheckBox)isActive.getWidget()).setState(CheckBox.CHECKED);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
        if (success) {
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
            
            loadTabs();
        }
		
		nameTextbox.setFocus(true);
		idTextBox.enable(false);
	}
	
	public void query() {		
		super.query();
		
		nameTextbox.setFocus(true);
		
        //
        // disable notes and contact remove button
        //
        noteText.enable(false);
        //removeContactButton.changeState(AppButton.DISABLED);
        clearComments();
	}
	
	public void abort() {
		componentsController.setAutoAdd(false);
        
        if(state == FormInt.State.ADD || state == FormInt.State.QUERY){
            loadComponents = false;
            clearComponents = true;
            
            loadLocations = false;
            clearLocations = true;
            
            loadComments = false;
            clearComments = true;
        }else{
            loadComponents = true;
            loadLocations = true;
            loadComments = true;
        }
        
        //the super needs to go before the load tabs method or the table wont load.
        super.abort();
        
        loadTabs();
	}
	
	public void update() {
		//locsController.setAutoAdd(true);
		componentsController.setAutoAdd(true);
		super.update();
	}

	public void afterCommitUpdate(boolean success) {
		componentsController.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
        
//      we need to load the comments tab if it has been already loaded
        if(success){ 
            loadComments = true;
            clearComments = true;
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearCommentsFields();
        }
	}
	
	public void afterCommitAdd(boolean success) {
		componentsController.setAutoAdd(false);
		
		super.afterCommitAdd(success);
        
//       we need to load the comments tab if it has been already loaded
        if(success){ 
            loadComments = true;
            clearComments = true;
            
            loadComponents = true;
            clearComponents = false;
            
            loadLocations = true;
            clearLocations = false;
            
            Integer itemId = (Integer)rpc.getFieldValue(InvItemMeta.getId());
            NumberObject itemIdObj = new NumberObject(itemId);
            
            // done because key is set to null in AppScreenForm for the add operation 
            if(key ==null){  
                key = new DataSet();
                key.setKey(itemIdObj);
                
            }else{
                key.setKey(itemIdObj);
                
            }
            
            loadTabs();
            
            //the note subject and body fields need to be refeshed after every successful commit
            clearCommentsFields();
        }
	}
	
    /*
     * Overriden to allow lazy loading Contact and Note tabs
     */
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if (index == 0 && loadComponents) {
            if (clearComponents)
                clearComponents();
            fillComponentsModel(false);
            loadComponents = false;
        } else if (index == 1 && loadLocations) {
            if (clearLocations)
                clearLocations();
            fillLocationsModel();
            loadLocations = false;
        } else if(index == 4 && loadComments){
            if (clearComments)
                clearComments();
            fillCommentsModel();
            loadComments = false;            
        }
        return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
	
		
	}
    
    private void loadTabs() {
        loadTabs(false);
    }
    private void loadTabs(boolean forDuplicates) {
        TabPanel tabPanel = (TabPanel)getWidget("tabPanel");
        int selectedTab = tabPanel.getTabBar().getSelectedTab();

        if (selectedTab == 0 && loadComponents) {
            // if there was data previously then clear the components table
            // otherwise don't
            if (clearComponents) {
                clearComponents();
            }
            // load the components table
            fillComponentsModel(forDuplicates);
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadComponents = false;
            
        } else if (selectedTab == 1 && loadLocations) {
            // if there was data previously then clear the locations table otherwise
            // don't
            if (clearLocations) {
                clearLocations();
            }
            // load the locations table
            fillLocationsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadLocations = false;

        } else if (selectedTab == 4 && loadComments) {
            // if there was data previously then clear the comments panel otherwise
            // don't
            if (clearComments) {
                clearComments();
            }
            // load the comments model
            fillCommentsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadComments = false;

        }
    }
    
    private void clearCommentsFields(){
        //     the note subject and body fields need to be refeshed after every successful commit 
          subjectBox.setText("");
          TextArea noteArea = (TextArea)noteText.getWidget();
          noteArea.setText("");           
          rpc.setFieldValue(InvItemMeta.ITEM_NOTE.getSubject(), null);
          rpc.setFieldValue(InvItemMeta.ITEM_NOTE.getText(), null);  
       }
    
    private void fillComponentsModel(boolean forDuplicate){
        Integer itemId = null;
        NumberObject itemIdObj;
        BooleanObject duplicateObj;
        TableField f;

        if (key == null || key.getKey() == null) {
            clearComponents = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        itemId = (Integer)key.getKey().getValue();
        itemIdObj = new NumberObject(itemId);
        duplicateObj = new BooleanObject();
        
        if(forDuplicate)
            duplicateObj.setValue("Y");
        else
            duplicateObj.setValue("N");


        f = new TableField();
        f.setValue(componentsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {itemIdObj, duplicateObj, f};

        screenService.getObject("getComponentsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                rpc.setFieldValue("componentsTable",
                                  (TableModel)((TableField)result).getValue());

                componentsController.loadModel((TableModel)((TableField)result).getValue());

                clearComponents = componentsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillLocationsModel(){
        Integer itemId = null;
        NumberObject itemIdObj;
        StringObject isSerialized = new StringObject();
        TableField f;

        if (key == null || key.getKey() == null) {
            clearLocations = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        itemId = (Integer)key.getKey().getValue();
        itemIdObj = new NumberObject(itemId);
        isSerialized.setValue(((CheckBox)isSerializedCheck.getWidget()).getState());

        f = new TableField();
        f.setValue(locsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {itemIdObj, isSerialized, f};

        screenService.getObject("getLocationsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                TableModel model = (TableModel)((TableField)result).getValue();
                rpc.setFieldValue("locQuantitiesTable", model);

                locsController.loadModel(model);

                clearLocations = locsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillCommentsModel(){
        Integer itemId = null;
        boolean getModel = false;
         
         // access the database only if id is not null  
         if(key!=null && key.getKey()!=null){        
             getModel = true;
             itemId = (Integer)key.getKey().getValue(); 
          
         }else{
             clearComments = false;
         } 
            
           if(getModel){ 
               window.setStatus("","spinnerIcon");
               NumberObject itemIdObj = new NumberObject(itemId);
                 
               // prepare the argument list for the getObject function
               DataObject[] args = new DataObject[] {itemIdObj}; 
                 
               screenService.getObject("getCommentsModel", args, new AsyncCallback(){
                   public void onSuccess(Object result){    
                     // get the datamodel, load it in the comments panel and set the value in the rpc
                       String xmlString = (String) ((StringObject)result).getValue();
                       svp.load(xmlString);   
                       
                       if(((VerticalPanel)svp.getPanel()).getWidgetCount() > 0){
                           clearComments = true;
                        }else {
                            clearComments = false;
                        }
                       window.setStatus("","");
                   }
                   
                   public void onFailure(Throwable caught){
                       Window.alert(caught.getMessage());
                   }
               }); 
         }       
    }
    
    private void clearComponents() {
        componentsController.model.reset();
        componentsController.setModel(componentsController.model);
        rpc.setFieldValue("componentsTable", componentsController.model);
    }
    
    private void clearLocations() {
        locsController.model.reset();
        locsController.setModel(locsController.model);
        rpc.setFieldValue("locQuantitiesTable", locsController.model);
    }
	
    private void clearComments() {
        svp = (ScreenVertical)widgets.get("notesPanel");
        svp.clear();
    }
    
	private void getInventories(String query, Widget sender) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue(InvItemMeta.getName(), query);

			commitQuery(letterRPC);
		}
	}
	
	private void onStandardNoteButtonClick(){
   	 	PopupPanel standardNotePopupPanel = new PopupPanel(false,true);
		ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
		pickerWindow.setContent(new StandardNotePickerScreen((TextArea)noteText.getWidget()));
			
		standardNotePopupPanel.add(pickerWindow);
		int left = this.getAbsoluteLeft();
		int top = this.getAbsoluteTop();
		standardNotePopupPanel.setPopupPosition(left,top);
		standardNotePopupPanel.show();
     }

    private void onRemoveComponentRowButtonClick() {
        int selectedRow = componentsController.selected;
        if (selectedRow > -1 && componentsController.model.numRows() > 0) {
            TableRow row = componentsController.model.getRow(selectedRow);
            componentsController.model.hideRow(row);

            // reset the model
            componentsController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
    
    private void onDuplicateRecordClick(){
        if(state == FormInt.State.DISPLAY){
            //we need to do the duplicate method
            FormRPC displayRPC = rpc.clone();
            displayRPC.setFieldValue(InvItemMeta.getId(), null);
            displayRPC.setFieldValue("locQuantitiesTable", null);
            displayRPC.setFieldValue(InvItemMeta.getAverageLeadTime(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageCost(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageDailyUse(),null);
            displayRPC.setFieldValue(InvItemMeta.ITEM_NOTE.getSubject(),null);
            displayRPC.setFieldValue(InvItemMeta.ITEM_NOTE.getText(),null);   
                       
            add();
            
            clearComponents = false;
            loadComponents = true;
            
            clearLocations = false;
            loadLocations = false;
            
            clearComments = false;
            loadComments = false;
            
            rpc = displayRPC;
            
            load();
            
            fillComponentsModel(true);
        }
    }
    
    
    public void changeState(FormInt.State state) {
        if(state == FormInt.State.DISPLAY){
            ((ScreenMenuItem)((ScreenMenuItem) duplicateMenuPanel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(true);

        }else{
            ((ScreenMenuItem)((ScreenMenuItem)duplicateMenuPanel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(false);
        } 
        
        super.changeState(state);
    }
    
    //
    //Table Manager methods
    //
    public boolean action(int row, int col, TableController controller) {
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       return true;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }
    
    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(row > -1 && row < controller.model.numRows() && !tableRowEmpty(controller.model.getRow(row)))
            return true;
        else if(row > -1 && row == controller.model.numRows() && !tableRowEmpty(((EditTable)controller).autoAddRow))
            return true;
      
        return false;
    }
    
    public void finishedEditing(final int row, int col, final TableController controller) {
        DropDownField componentField;
        if(col == 0 && row > -1 && row < controller.model.numRows() && !startedLoadingTable){
            startedLoadingTable = true;
            componentField = (DropDownField)controller.model.getFieldAt(row, col);
            if(componentField.getValue() != null){
                window.setStatus("","spinnerIcon");
                NumberObject componentIdObj = new NumberObject((Integer)componentField.getValue());
                  
                // prepare the argument list for the getObject function
                DataObject[] args = new DataObject[] {componentIdObj}; 
                  
                screenService.getObject("getComponentDescriptionText", args, new AsyncCallback(){
                    public void onSuccess(Object result){    
                      // get the datamodel, load it in the notes panel and set the value in the rpc
                        StringField descString = new StringField();
                        descString.setValue((String) ((StringObject)result).getValue());
                        
                        TableRow tableRow = controller.model.getRow(row);
                        tableRow.setColumn(1, descString);
                        
                        controller.scrollLoad(-1);
                        
                        controller.select(row, 2);
                        
                        window.setStatus("","");
                        startedLoadingTable = false;
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        startedLoadingTable = false;
                    }
                });
            }
        }     
    }

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void rowAdded(int row, TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void setMultiple(int row, int col, TableController controller) {}
    //
    //End Table Manager Methods
    //
    
    private boolean tableRowEmpty(TableRow row){
        boolean empty = true;
        
        for(int i=0; i<row.numColumns(); i++){
            if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
}
