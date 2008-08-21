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

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
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

public class InventoryItemScreen extends OpenELISScreenForm implements TableManager, ClickListener, TabListener{

    private boolean startedLoadingTable = false;
    private AppButton        removeComponentButton, standardNoteButton;
	private ScreenTextBox nameTextbox;
    TextBox subjectBox; 
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
    private KeyListManager keyList = new KeyListManager();
	
	private EditTable componentsController, locsController;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenCheck isActive, isSerializedCheck;
    
    private static boolean loaded = false;
    private static DataModel storesDropdown, categoriesDropdown, dispensedUnitsDropdown;
    
    private ScreenVertical   svp       = null;
	
    private InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
	public InventoryItemScreen() {
        super("org.openelis.modules.inventoryItem.server.InventoryItemService",!loaded);
	}

    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){
           String baction = ((AppButton)obj).action;
           if(baction.startsWith("query:")){
        	   getInventories(baction.substring(6, baction.length()), ((AppButton)obj));      
           }else
               super.performCommand(action, obj);
        }else{
            super.performCommand(action, obj);
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
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        AToZTable atozTable = (AToZTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        loaded = true;
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        removeComponentButton = (AppButton)getWidget("removeComponentButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");
        
        isActive = (ScreenCheck)widgets.get(InvItemMeta.getIsActive());
        isSerializedCheck = (ScreenCheck)widgets.get(InvItemMeta.getIsSerialMaintained());
        
        duplicateMenuPanel = (ScreenMenuPanel)widgets.get("optionsMenu");
        
        nameTextbox = (ScreenTextBox) widgets.get(InvItemMeta.getName());
        startWidget = nameTextbox;
        idTextBox = (ScreenTextBox) widgets.get(InvItemMeta.getId());
        noteText = (ScreenTextArea) widgets.get(InvItemMeta.ITEM_NOTE.getText());
        subjectBox = (TextBox)getWidget(InvItemMeta.ITEM_NOTE.getSubject()); 
        
        locsController = ((TableWidget)getWidget("locQuantitiesTable")).controller;
		locsController.setAutoAdd(false);
        ((InventoryLocationsTable)locsController.manager).setInventoryForm(this);
		
		componentsController = ((TableWidget)getWidget("componentsTable")).controller;
		componentsController.setAutoAdd(false);
        addCommandListener(componentsController);
       
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        if (storesDropdown == null) {
            storesDropdown = (DataModel)initData.get("stores");
            categoriesDropdown = (DataModel)initData.get("categories");
            dispensedUnitsDropdown = (DataModel)initData.get("units");
        }
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getStoreId());
        drop.setModel(storesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getCategoryId());
        drop.setModel(categoriesDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(InvItemMeta.getDispensedUnitsId());
        drop.setModel(dispensedUnitsDropdown);
        
		super.afterDraw(success);			
	}
    
    public void add() {
		componentsController.setAutoAdd(true);
		
		super.add();
		
		idTextBox.enable(false);
        
        ((CheckBox)isActive.getWidget()).setState(CheckBox.CHECKED);
	}
	
	protected AsyncCallback afterUpdate = new AsyncCallback() {
         public void onSuccess(Object result) {
            nameTextbox.setFocus(true);
			idTextBox.enable(false);
         }
         
         public void onFailure(Throwable caught){
             
         }
      };
      
	public void query() {		
		super.query();
        //
        // disable notes and contact remove button
        //
        noteText.enable(false);
	}
	
	public void abort() {
		componentsController.setAutoAdd(false);
        
        super.abort();
	}
	
	public void update() {
		componentsController.setAutoAdd(true);
		super.update();
	}
	
	protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
          public void onSuccess(Object result){
              componentsController.setAutoAdd(false);
        
			//we need to do this reset to get rid of the last row
	        componentsController.reset();
          }
          
          public void onFailure(Throwable caught){
              
          }
    };
	
	protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            
        }
        public void onSuccess(Object result) {
            componentsController.setAutoAdd(false);
        
			//we need to do this reset to get rid of the last row
    	    componentsController.reset();
        }   
    };
    
    /*
     * Overriden to allow lazy loading Contact and Note tabs
     */
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != FormInt.State.QUERY){
            if (index == 0 && !((FormRPC)rpc.getField("components")).load) 
                fillComponentsModel(false);
            else if (index == 1 && !((FormRPC)rpc.getField("locations")).load) 
                fillLocationsModel();
            else if(index == 4 && !((FormRPC)rpc.getField("comments")).load)
                fillCommentsModel();
        }
        
        return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {}
      
    private void fillComponentsModel(final boolean forDuplicate){
        if(key == null)
            return;

        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, new BooleanObject(forDuplicate), rpc.getField("components")};

        screenService.getObject("loadComponents", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("components", (FormRPC)result);

                if(forDuplicate)
                    key = null;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillLocationsModel(){
        if(key == null)
            return;

        window.setStatus("","spinnerIcon");
        
        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, new StringObject(((CheckBox)isSerializedCheck.getWidget()).getState()), rpc.getField("locations")};

        screenService.getObject("loadLocations", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("locations", (FormRPC)result);
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillCommentsModel(){
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");
                 
       // prepare the argument list for the getObject function
       DataObject[] args = new DataObject[] {key, rpc.getField("comments")}; 
         
       screenService.getObject("loadComments", args, new AsyncCallback(){
           public void onSuccess(Object result){    
               load((FormRPC)result);
               rpc.setField("comments",(FormRPC)result);

               window.setStatus("","");
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });     
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
            displayRPC.setFieldValue(InvItemMeta.getAverageLeadTime(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageCost(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageDailyUse(),null);
            
            ((FormRPC)displayRPC.getField("locations")).setFieldValue("locQuantitiesTable", null);
            ((FormRPC)displayRPC.getField("components")).setFieldValue("componentsTable", null);
            ((FormRPC)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getSubject(),null);
            ((FormRPC)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getText(),null);   
            
            DataSet tempKey = key;
                    
            add();
            
            key = tempKey;
            
            rpc = displayRPC;
            
            //set the load flags correctly
            ((FormRPC)rpc.getField("components")).load = false;
            ((FormRPC)rpc.getField("locations")).load = true;
            ((FormRPC)rpc.getField("comments")).load = true;
            
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
    
    public boolean doAutoAdd(TableRow addRow, TableController controller) {
        return !tableRowEmpty(addRow);
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
