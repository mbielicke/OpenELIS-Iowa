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
package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.richTextPopup.client.RichTextPopupScreen;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InventoryItemScreen extends OpenELISScreenForm<InventoryItemForm, Query<TableDataRow<Integer>>> implements TableWidgetListener, ClickListener, TabListener, AutoCompleteCallInt{

    private AppButton        removeComponentButton, standardNoteButton, editManufacturing;
    private HTML manufacturingText;
	private ScreenTextBox nameTextbox;
    TextBox subjectBox; 
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
    private KeyListManager keyList = new KeyListManager();
	
	private TableWidget componentsTable, locsTable;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private Dropdown store, category, dispensedUnit;
    
    private ScreenCheck isActive, isSerializedCheck;
    private ScreenVertical   svp;
	
    private InventoryItemMetaMap InvItemMeta = new InventoryItemMetaMap();
    
    AsyncCallback<InventoryItemForm> checkModels = new AsyncCallback<InventoryItemForm>() {
        public void onSuccess(InventoryItemForm rpc) {
            if(rpc.categories != null) {
                setCategoriesModel(rpc.categories);
                rpc.categories = null;
            }
            if(rpc.dispensedUnits != null) {
                setDispensedUnitsModel(rpc.dispensedUnits);
                rpc.dispensedUnits = null;
            }
            if(rpc.stores != null) {
                setStoresModel(rpc.stores);
                rpc.stores = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
	public InventoryItemScreen() {
	    super("org.openelis.modules.inventoryItem.server.InventoryItemService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new InventoryItemForm());
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
		if(sender instanceof MenuItem){
        	if("duplicateRecord".equals(((String)((MenuItem)sender).objClass))){
                onDuplicateRecordClick();
        	}
        }else if(sender == removeComponentButton){
            onRemoveComponentRowButtonClick();
		}else if(sender == standardNoteButton){
			onStandardNoteButtonClick();
		}else if(sender == editManufacturing)
		    onEditManufacturingClick();
	}
	
	public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        ResultsTable atozTable = (ResultsTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        removeComponentButton = (AppButton)getWidget("removeComponentButton");
        standardNoteButton = (AppButton)getWidget("standardNoteButton");
        editManufacturing = (AppButton)getWidget("editManufacturingButton");
        manufacturingText = (HTML)getWidget("manufacturingText");
        
        isActive = (ScreenCheck)widgets.get(InvItemMeta.getIsActive());
        isSerializedCheck = (ScreenCheck)widgets.get(InvItemMeta.getIsSerialMaintained());
        
        duplicateMenuPanel = (ScreenMenuPanel)widgets.get("optionsMenu");
        
        nameTextbox = (ScreenTextBox) widgets.get(InvItemMeta.getName());
        startWidget = nameTextbox;
        idTextBox = (ScreenTextBox) widgets.get(InvItemMeta.getId());
        noteText = (ScreenTextArea) widgets.get(InvItemMeta.ITEM_NOTE.getText());
        subjectBox = (TextBox)getWidget(InvItemMeta.ITEM_NOTE.getSubject()); 
        
        locsTable = (TableWidget)getWidget("locQuantitiesTable");
		locsTable.model.enableAutoAdd(false);
        
		componentsTable = (TableWidget)getWidget("componentsTable");
        componentsTable.addTableWidgetListener(this);
		componentsTable.model.enableAutoAdd(false);
       
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        store = (Dropdown)getWidget(InvItemMeta.getStoreId());
        category = (Dropdown)getWidget(InvItemMeta.getCategoryId());
        dispensedUnit =(Dropdown)getWidget(InvItemMeta.getDispensedUnitsId());
        
        setStoresModel(form.stores);
        setCategoriesModel(form.categories);
        setDispensedUnitsModel(form.dispensedUnits);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        form.stores = null;
        form.categories = null;
        form.dispensedUnits = null;
        
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        updateChain.add(0,checkModels);
        updateChain.add(afterUpdate);
        fetchChain.add(0,checkModels);
        //fetchChain.add(afterFetch);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
		super.afterDraw(success);			
        
		form.components.componentsTable.setValue(componentsTable.model.getData());
		form.locations.locQuantitiesTable.setValue(locsTable.model.getData());
	}
    
    public void add() {
		componentsTable.model.enableAutoAdd(true);
		
		super.add();
		idTextBox.enable(false);
        
        ((CheckBox)isActive.getWidget()).setState(CheckBox.CHECKED);
	}
	
	protected AsyncCallback afterUpdate = new AsyncCallback() {
         public void onSuccess(Object result) {
            nameTextbox.setFocus(true);
			idTextBox.enable(false);
            componentsTable.model.enableAutoAdd(true);
         }
         
         public void onFailure(Throwable caught){
             
         }
      };

      /*
      protected AsyncCallback afterFetch = new AsyncCallback() {
          public void onSuccess(Object result) {
              //we need to load the manufacturing tab
              manufacturingText.setHTML(form.manufacturingText.getValue());
          }
          
          public void onFailure(Throwable caught){
              
          }
       };
       */
      
	public void query() {
		super.query();
        //
        // disable notes and contact remove button
        //
        noteText.enable(false);
        
        standardNoteButton.changeState(ButtonState.DISABLED);
        editManufacturing.changeState(ButtonState.DISABLED);
        removeComponentButton.changeState(ButtonState.DISABLED);
	}
	
	public void abort() {
        componentsTable.model.enableAutoAdd(false);
        super.abort();
	}
	
	public void update() {
      
		super.update();
	}
	
	protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
          public void onSuccess(Object result){
              componentsTable.model.enableAutoAdd(false);
          }
          
          public void onFailure(Throwable caught){
              
          }
    };
	
	protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            
        }
        public void onSuccess(Object result) {
            componentsTable.model.enableAutoAdd(false);
        }   
    };
    
    //
    //Overriden to allow lazy loading Contact and Note tabs
    //
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != State.QUERY){
            if (index == 0 && !form.components.load) 
                fillComponentsModel(false);
            else if (index == 1 && !form.locations.load) 
                fillLocationsModel();
            else if(index == 3 && !form.manufacturing.load)
                fillManufacturingTab();
            else if(index == 4 && !form.comments.load)
                fillCommentsModel();
        }
        
        return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {}
      
    private void fillComponentsModel(final boolean forDuplicate){
        if(form.entityKey == null)
            return;

        window.setBusy();

        //prepare the argument list
        //InventoryComponentsForm icrpc = new InventoryComponentsForm();
        form.components.entityKey = form.entityKey;
        form.components.forDuplicate = forDuplicate;
        //icrpc.form = form.form.components;
        
        screenService.call("loadComponents", form.components, new AsyncCallback<InventoryComponentsForm>() {
            public void onSuccess(InventoryComponentsForm result) {
                load(result);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                form.components = result;

                if(forDuplicate)
                    form.entityKey = null;
                
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillLocationsModel(){
        if(form.entityKey == null)
            return;

        window.setBusy();
        
      //prepare the argument list
        //InventoryLocationsRPC ilrpc = new InventoryLocationsRPC();
        form.locations.entityKey = form.entityKey;
        form.locations.isSerialized = ((CheckBox)isSerializedCheck.getWidget()).getState();
        //ilrpc.form = form.form.locations;

        screenService.call("loadLocations", form.locations, new AsyncCallback<InventoryLocationsForm>() {
            public void onSuccess(InventoryLocationsForm result) {
                load(result);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                form.locations = result;
                
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillManufacturingTab(){
        if(form.entityKey == null)
            return;
        
        window.setBusy();
                 
      //prepare the argument list
        form.manufacturing.entityKey = form.entityKey;
         
       screenService.call("loadManufacturing", form.manufacturing, new AsyncCallback<InventoryManufacturingForm>(){
           public void onSuccess(InventoryManufacturingForm result){    
               load(result);
               /*
                * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                * stays in sync it needs to be assigned back into the hash and to its member field in the form
                */
               form.manufacturing = result;

               window.clearStatus();
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });
    }
    
    private void fillCommentsModel(){
        if(form.entityKey == null)
            return;
        
        window.setBusy();
                 
      //prepare the argument list
        //InventoryCommentsRPC icrpc = new InventoryCommentsRPC();
        form.comments.entityKey = form.entityKey;
        //rpc.form = form.form.comments;
         
       screenService.call("loadComments", form.comments, new AsyncCallback<InventoryCommentsForm>(){
           public void onSuccess(InventoryCommentsForm result){    
               load(result);
               /*
                * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                * stays in sync it needs to be assigned back into the hash and to its member field in the form
                */
               form.comments = result;

               window.clearStatus();
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.clearStatus();
           }
       });     
    }
    	
    private void getInventories(String query, Widget sender) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryStringField qField = new QueryStringField(InvItemMeta.getName());
            qField.setValue(query);
            commitQuery(qField);
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
        int selectedRow = componentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && componentsTable.model.numRows() > 0) {
            componentsTable.model.deleteRow(selectedRow);
        }
    }
    
    private void onEditManufacturingClick(){
        ScreenWindow modal = new ScreenWindow(null,"Rich Text Editor","richTextEditorScreen","Loading...",true);
        modal.setName("Rich Text Editor");
        modal.setContent(new RichTextPopupScreen(manufacturingText));
    }
    
    private void onDuplicateRecordClick(){
        screenService.call("getDuplicateRPC", form, new AsyncCallback<InventoryItemForm>(){
            public void onSuccess(InventoryItemForm result) {
                form = result;
                loadScreen();
                enable(true);
                changeState(State.ADD);
                window.setDone(consts.get("enterInformationPressCommit"));
            }

            public void onFailure(Throwable caught) {
                handleError(caught);
                window.setDone("Load Failed");
                changeState(State.DEFAULT);
                form.entityKey = null;
            }
        });
    }
    
    
    public void changeState(State state) {
        if(duplicateMenuPanel != null){ 
            if(state == State.DISPLAY){
                ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(true);

            }else{  
                ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(false);
            }
        }
        
        super.changeState(state);
    }
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, final int col) {
        DropDownField componentField;
        if(sender == componentsTable){
 
            if(col == 0 && row < componentsTable.model.numRows()){
                componentField = (DropDownField)componentsTable.model.getObject(row, col);
                if(componentField.getValue() != null){
                    window.setBusy();
                      
                    //prepare the argument list
                    //InventoryItemRPC iirpc = new InventoryItemRPC();
                    //iirpc.key = form.key;
                    form.componentId = (Integer)componentField.getSelectedKey();
                    //iirpc.form = form.form;

                    screenService.call("getComponentDescriptionText", form, new AsyncCallback<InventoryItemForm>(){
                        public void onSuccess(InventoryItemForm result){
                            if(row < componentsTable.model.numRows()){
                                Integer currentId = (Integer)((DropDownField<Integer>)componentsTable.model.getObject(row, 0)).getSelectedKey();
                                Integer oldId = result.componentId;
                                
                                //make sure the row hasnt been deleted and it still has the same values
                                if(currentId.equals(oldId))
                                    componentsTable.model.setCell(row, 1, result.descText);
                            }
                            
                            window.clearStatus();
                            
                        }
                        
                        public void onFailure(Throwable caught){
                            Window.alert(caught.getMessage());
                        }
                    });
                }
            }
        }
    }
    
    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
   
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
        // prepare the arguments
        InventoryComponentAutoRPC icarpc = new InventoryComponentAutoRPC();
        icarpc.cat = widget.cat;
        icarpc.match = text;
        
        //grab both these values from the widgets on the screen
        if(store.getSelections().size() > 0)
            icarpc.storeId = (Integer)store.getSelections().get(0).key;
        icarpc.name = ((TextBox)nameTextbox.getWidget()).getText();
        
        screenService.call("getMatchesCall", icarpc, new AsyncCallback<InventoryComponentAutoRPC>() {
            public void onSuccess(InventoryComponentAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setError(caught.getMessage());
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }

    public void setCategoriesModel(TableDataModel<TableDataRow<Integer>> categoriesModel) {
        category.setModel(categoriesModel);
    }
    
    public void setDispensedUnitsModel(TableDataModel<TableDataRow<Integer>> dispensedUnitsModel) {
        dispensedUnit.setModel(dispensedUnitsModel);
    }
    
    public void setStoresModel(TableDataModel<TableDataRow<Integer>> storesModel) {
        store.setModel(storesModel);
    }
}
