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
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
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

public class InventoryItemScreen extends OpenELISScreenForm implements TableWidgetListener, ClickListener, TabListener, AutoCompleteCallInt{

    private AppButton        removeComponentButton, standardNoteButton;
	private ScreenTextBox nameTextbox;
    TextBox subjectBox; 
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
    private KeyListManager keyList = new KeyListManager();
	
	private TableWidget componentsTable, locsTable;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenCheck isActive, isSerializedCheck;
    
    private static boolean loaded = false;
    private static DataModel storesDropdown, categoriesDropdown, dispensedUnitsDropdown;
    
    private ScreenVertical   svp;
	
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
        Dropdown drop;
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
        
        locsTable = (TableWidget)getWidget("locQuantitiesTable");
		locsTable.model.enableAutoAdd(false);
        
		componentsTable = (TableWidget)getWidget("componentsTable");
        componentsTable.addTableWidgetListener(this);
		componentsTable.model.enableAutoAdd(false);
       
        svp = (ScreenVertical) widgets.get("notesPanel");
        
        if (storesDropdown == null) {
            storesDropdown = (DataModel)initData.get("stores");
            categoriesDropdown = (DataModel)initData.get("categories");
            dispensedUnitsDropdown = (DataModel)initData.get("units");
        }
        
        drop = (Dropdown)getWidget(InvItemMeta.getStoreId());
        drop.setModel(storesDropdown);
        
        drop = (Dropdown)getWidget(InvItemMeta.getCategoryId());
        drop.setModel(categoriesDropdown);
        
        drop = (Dropdown)getWidget(InvItemMeta.getDispensedUnitsId());
        drop.setModel(dispensedUnitsDropdown);
        
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
		super.afterDraw(success);			
        
        ((FormRPC)rpc.getField("components")).setFieldValue("componentsTable", componentsTable.model.getData());
        ((FormRPC)rpc.getField("locations")).setFieldValue("locQuantitiesTable", locsTable.model.getData());
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
        Data[] args = new Data[] {key, new BooleanObject(forDuplicate), rpc.getField("components")};

        screenService.getObject("loadComponents", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, new StringObject(((CheckBox)isSerializedCheck.getWidget()).getState()), rpc.getField("locations")};

        screenService.getObject("loadLocations", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, rpc.getField("comments")}; 
         
       screenService.getObject("loadComments", args, new AsyncCallback<FormRPC>(){
           public void onSuccess(FormRPC result){    
               load(result);
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
        int selectedRow = componentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && componentsTable.model.numRows() > 0) {
            componentsTable.model.deleteRow(selectedRow);
        }
    }
    
    private void onDuplicateRecordClick(){
        if(state == FormInt.State.DISPLAY){
            //we need to do the duplicate method
            FormRPC displayRPC = (FormRPC)rpc.clone();
            displayRPC.setFieldValue(InvItemMeta.getId(), null);
            displayRPC.setFieldValue(InvItemMeta.getAverageLeadTime(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageCost(),null);
            displayRPC.setFieldValue(InvItemMeta.getAverageDailyUse(),null);
            
            ((FormRPC)displayRPC.getField("locations")).setFieldValue("locQuantitiesTable", null);
            ((FormRPC)displayRPC.getField("components")).setFieldValue("componentsTable", null);
            ((FormRPC)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getSubject(),null);
            ((FormRPC)displayRPC.getField("comments")).setFieldValue(InvItemMeta.ITEM_NOTE.getText(),null);   
            
            DataSet tempKey = key;
                    
            DataModel beforeModel = (DataModel)((FormRPC)displayRPC.getField("components")).getFieldValue("componentsTable");
            beforeModel.size();
            
            add();
            
            DataModel afterModel = (DataModel)((FormRPC)displayRPC.getField("components")).getFieldValue("componentsTable");
            afterModel.size();
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
                    final NumberObject componentIdObj = new NumberObject((Integer)componentField.getValue());
                      
                    // prepare the argument list for the getObject function
                    Data[] args = new Data[] {componentIdObj}; 
                      
                    screenService.getObject("getComponentDescriptionText", args, new AsyncCallback<StringObject>(){
                        public void onSuccess(StringObject result){
                            if(row < componentsTable.model.numRows()){
                                Integer currentId = (Integer)componentsTable.model.getCell(row, 0);
                                Integer oldId = (Integer)componentIdObj.getValue();
                                
                                //make sure the row hasnt been deleted and it still has the same values
                                if(currentId.equals(oldId))
                                    componentsTable.model.setCell(row, 1, result.getValue());
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
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap();
        
        paramsObj.put("id", rpc.getField(InvItemMeta.getId()));
        paramsObj.put("store", rpc.getField(InvItemMeta.getStoreId()));
        paramsObj.put("name", rpc.getField(InvItemMeta.getName()));
        
        // prepare the argument list for the getObject function
        Data[] args = new Data[] {catObj, model, matchObj, paramsObj}; 
        
        
        screenService.getObject("getMatchesObj", args, new AsyncCallback<DataModel>() {
            public void onSuccess(DataModel model) {
                widget.showAutoMatches(model);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }
}
