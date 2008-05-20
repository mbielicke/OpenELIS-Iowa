package org.openelis.modules.inventory.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class InventoryScreen extends OpenELISScreenForm implements ClickListener, TabListener{

	private ScreenTextBox nameTextbox;
	private ScreenTextBox idTextBox;
	private ScreenTextArea noteText;
	
	private EditTable componentsController;
	private EditTable locsController;
	
	public InventoryScreen() {
        super("org.openelis.modules.inventory.server.InventoryService",false);
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
        	if(((String)((ScreenMenuItem)sender).getUserObject()).equals("duplicateRecord")){
                Window.alert("clicked duplicate record");
                return;
        	}
		}else if(sender instanceof AppButton){
			String action = ((AppButton)sender).action;
			if(action.equals("standardNote"))
				onStandardNoteButtonClick();
		}
	}
	
	public void afterDraw(boolean success) {
		setBpanel((ButtonPanel) getWidget("buttons"));
		
		//removeContactButton = (AppButton) getWidget("removeContactButton");

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextbox = (ScreenTextBox) widgets.get("inventoryItem.name");
        idTextBox = (ScreenTextBox) widgets.get("inventoryItem.id");
        noteText = (ScreenTextArea) widgets.get("note.text");
        
        locsController = ((TableWidget)getWidget("locQuantitiesTable")).controller;
		locsController.setAutoAdd(false);
		
		componentsController = ((TableWidget)getWidget("componentsTable")).controller;
		componentsController.setAutoAdd(false);
		
        //loadDropdowns();
		super.afterDraw(success);			
	}
	
	public void add() {
		locsController.setAutoAdd(true);
		componentsController.setAutoAdd(true);
		
		super.add();
		
		nameTextbox.setFocus(true);
		
		idTextBox.enable(false);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
		
		nameTextbox.setFocus(true);
		
		idTextBox.enable(false);
	}
	
	public void query() {		
		super.query();
		
		nameTextbox.setFocus(true);
		
		noteText.enable(false);
	}
	
	public void abort() {
		locsController.setAutoAdd(false);
		componentsController.setAutoAdd(false);
		
		super.abort();
	}
	
	public void update() {
		locsController.setAutoAdd(true);
		componentsController.setAutoAdd(true);
		super.update();
	}

	public void afterCommitUpdate(boolean success) {
		locsController.setAutoAdd(false);
		componentsController.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
	}
	
	public void afterCommitAdd(boolean success) {
		locsController.setAutoAdd(false);
		componentsController.setAutoAdd(false);
		
		super.afterCommitAdd(success);
	}
	
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
		return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
	
		
	}
	
	private void getInventories(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			letterRPC.setFieldValue("inventoryItem.name", letter.toUpperCase() + "*");

			commitQuery(letterRPC);
		}
	}
	
	private void onStandardNoteButtonClick(){
   	 	PopupPanel standardNotePopupPanel = new PopupPanel(false,true);
		ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
		pickerWindow.setContent(new StandardNotePickerScreen((TextArea)getWidget("note.text")));
			
		standardNotePopupPanel.add(pickerWindow);
		int left = this.getAbsoluteLeft();
		int top = this.getAbsoluteTop();
		standardNotePopupPanel.setPopupPosition(left,top);
		standardNotePopupPanel.show();
     }

}
