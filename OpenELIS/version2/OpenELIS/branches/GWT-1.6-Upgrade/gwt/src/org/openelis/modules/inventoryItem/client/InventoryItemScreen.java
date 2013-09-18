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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InventoryItemScreen extends OpenELISScreenForm<InventoryItemRPC, InventoryItemForm, Integer> implements TableWidgetListener, ClickListener, TabListener, AutoCompleteCallInt{

    private AppButton        removeComponentButton, standardNoteButton;
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
    
    AsyncCallback<InventoryItemRPC> checkModels = new AsyncCallback<InventoryItemRPC>() {
        public void onSuccess(InventoryItemRPC rpc) {
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
        
        forms.put("display",new InventoryItemForm());
        getScreen(new InventoryItemRPC());
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
		}
	}
	
	public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        AToZTable atozTable = (AToZTable) getWidget("azTable");
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
        
        setStoresModel(rpc.stores);
        setCategoriesModel(rpc.categories);
        setDispensedUnitsModel(rpc.dispensedUnits);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        rpc.stores = null;
        rpc.categories = null;
        rpc.dispensedUnits = null;
        
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
		super.afterDraw(success);			
        
		rpc.form.components.componentsTable.setValue(componentsTable.model.getData());
		rpc.form.locations.locQuantitiesTable.setValue(locsTable.model.getData());
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
        componentsTable.model.enableAutoAdd(false);
        super.abort();
	}
	
	public void update() {
        componentsTable.model.enableAutoAdd(true);
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
        if(state != FormInt.State.QUERY){
            if (index == 0 && !((Form)form.getField("components")).load) 
                fillComponentsModel(false);
            else if (index == 1 && !((Form)form.getField("locations")).load) 
                fillLocationsModel();
            else if(index == 4 && !((Form)form.getField("comments")).load)
                fillCommentsModel();
        }
        
        return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {}
      
    private void fillComponentsModel(final boolean forDuplicate){
        if(key == null)
            return;

        window.setStatus("","spinnerIcon");

        //prepare the argument list
        InventoryComponentsRPC icrpc = new InventoryComponentsRPC();
        icrpc.key = rpc.key;
        icrpc.forDuplicate = forDuplicate;
        icrpc.form = rpc.form.components;
        
        screenService.call("loadComponents", icrpc, new AsyncCallback<InventoryComponentsRPC>() {
            public void onSuccess(InventoryComponentsRPC result) {
                load(result.form);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                rpc.form.fields.put("components", rpc.form.components = result.form);

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
        
      //prepare the argument list
        InventoryLocationsRPC ilrpc = new InventoryLocationsRPC();
        ilrpc.key = rpc.key;
        ilrpc.isSerialized = ((CheckBox)isSerializedCheck.getWidget()).getState();
        ilrpc.form = rpc.form.locations;

        screenService.call("loadLocations", ilrpc, new AsyncCallback<InventoryLocationsRPC>() {
            public void onSuccess(InventoryLocationsRPC result) {
                load(result.form);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                rpc.form.fields.put("locations", rpc.form.locations = result.form);
                
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
                 
      //prepare the argument list
        InventoryCommentsRPC icrpc = new InventoryCommentsRPC();
        icrpc.key = rpc.key;
        icrpc.form = rpc.form.comments;
         
       screenService.call("loadComments", icrpc, new AsyncCallback<InventoryCommentsRPC>(){
           public void onSuccess(InventoryCommentsRPC result){    
               load(result.form);
               /*
                * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                * stays in sync it needs to be assigned back into the hash and to its member field in the form
                */
               rpc.form.fields.put("comments", rpc.form.comments = result.form);

               window.setStatus("","");
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
               window.setStatus("","");
           }
       });     
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
    
    private void onDuplicateRecordClick(){
        if(state == FormInt.State.DISPLAY){
            //we need to do the duplicate method
            Form displayRPC = (Form)form.clone();
            displayRPC.setFieldValue(InvItemMeta.getId(), null);
            displayRPC.setFieldValue(InvItemMeta.getAverageLeadTime(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageCost(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageDailyUse(),null);
            
            ((Form)displayRPC.getField("locations")).setFieldValue("locQuantitiesTable", null);
            ((Form)displayRPC.getField("components")).setFieldValue("componentsTable", null);
            ((Form)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getSubject(),null);
            ((Form)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getText(),null);   
            
            Integer tempKey = key;
                    
            DataModel<Integer> beforeModel = (DataModel<Integer>)((Form)displayRPC.getField("components")).getFieldValue("componentsTable");
            beforeModel.size();
            
            add();
            
            DataModel<Integer> afterModel = (DataModel<Integer>)((Form)displayRPC.getField("components")).getFieldValue("componentsTable");
            afterModel.size();
            key = tempKey;
            
            form = displayRPC;
            
            //set the load flags correctly
            ((Form)form.getField("components")).load = false;
            ((Form)form.getField("locations")).load = true;
            ((Form)form.getField("comments")).load = true;
            
            load();
            
            fillComponentsModel(true);
        }
    }
    
    
    public void changeState(FormInt.State state) {
        if(state == FormInt.State.DISPLAY){
            ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(true);

        }else{
            ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(false);
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
                    window.setStatus("","spinnerIcon");
                      
                    //prepare the argument list
                    InventoryItemRPC iirpc = new InventoryItemRPC();
                    iirpc.key = rpc.key;
                    iirpc.componentId = (Integer)componentField.getSelectedKey();
                    iirpc.form = rpc.form;

                    screenService.call("getComponentDescriptionText", iirpc, new AsyncCallback<InventoryItemRPC>(){
                        public void onSuccess(InventoryItemRPC result){
                            if(row < componentsTable.model.numRows()){
                                Integer currentId = (Integer)((DropDownField<Integer>)componentsTable.model.getObject(row, 0)).getSelectedKey();
                                Integer oldId = result.componentId;
                                
                                //make sure the row hasnt been deleted and it still has the same values
                                if(currentId.equals(oldId))
                                    componentsTable.model.setCell(row, 1, result.descText);
                            }
                            
                            window.setStatus("","");
                            
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
   
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        // prepare the arguments
        InventoryComponentAutoRPC icarpc = new InventoryComponentAutoRPC();
        icarpc.cat = widget.cat;
        icarpc.match = text;
        
        //grab both these values from the widgets on the screen
        if(store.getSelections().size() > 0)
            icarpc.storeId = (Integer)store.getSelections().get(0).getKey();
        icarpc.name = ((TextBox)nameTextbox.getWidget()).getText();
        
        screenService.call("getMatchesCall", icarpc, new AsyncCallback<InventoryComponentAutoRPC>() {
            public void onSuccess(InventoryComponentAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }

    public void setCategoriesModel(DataModel<Integer> categoriesModel) {
        category.setModel(categoriesModel);
    }
    
    public void setDispensedUnitsModel(DataModel<Integer> dispensedUnitsModel) {
        dispensedUnit.setModel(dispensedUnitsModel);
    }
    
    public void setStoresModel(DataModel<Integer> storesModel) {
        store.setModel(storesModel);
    }
}