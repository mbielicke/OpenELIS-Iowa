package org.openelis.modules.storageunit.client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageUnitScreen extends OpenELISScreenForm {

	private Widget selected;
	
	public StorageUnitScreen() {
		super("org.openelis.modules.storageunit.server.StorageUnitService",true);
        name="Storage Unit";
	}
	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.startsWith("query:")){
			getStorageUnits(action.substring(6, action.length()), sender);
			
		}
	}
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

//		 get storage unit table and set the managers form
		TableWidget storageUnitTable = (TableWidget) getWidget("StorageUnitTable");
		modelWidget.addChangeListener(storageUnitTable.controller);

		message.setText("done");

		((StorageUnitDescTable) storageUnitTable.controller.manager)
				.setStorageUnitForm(this);

		super.afterDraw(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		bpanel.setButtonState("delete", AppButton.DISABLED);
		
		loadDropdowns();
	}
	
	public void afterCommitDelete(boolean success) {
		super.afterCommitDelete(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		bpanel.setButtonState("delete", AppButton.DISABLED);
		bpanel.setButtonState("update", AppButton.DISABLED);
	}
	
	public void add(int state) {
		super.add(state);

		//set focus to the name field
		AutoCompleteDropdown cat = (AutoCompleteDropdown)getWidget("category");
		cat.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("StorageUnitTable")).controller.unselect(-1);
	}
	
	public void query(int state) {
		super.query(state);
		
		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		AutoCompleteDropdown cat = (AutoCompleteDropdown)getWidget("category");
		cat.setFocus(true);
	}
	
	public void abort(int state) {
		super.abort(state);
		
//		 need to get the storage unit table
		TableWidget StorageUnitTable = (TableWidget) getWidget("StorageUnitTable");
		int rowSelected = StorageUnitTable.controller.selected;

		// set the update button if needed
		if (rowSelected == -1){
			bpanel.setButtonState("update", AppButton.DISABLED);
			bpanel.setButtonState("prev", AppButton.DISABLED);
			bpanel.setButtonState("next", AppButton.DISABLED);
			bpanel.setButtonState("delete", AppButton.DISABLED);
		}
	}
	
	private void getStorageUnits(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (bpanel.getState() == FormInt.DISPLAY) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			if(letter.equals("#"))
				letterRPC.setFieldValue("description", "0* | 1* | 2* | 3* | 4* | 5* | 6* | 7* | 8* | 9*");
			else
				letterRPC.setFieldValue("description", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
	
	private void loadDropdowns(){
		DataModel storageUnitCategoryDropdown = (DataModel)initData[0];
		
	    ScreenAutoDropdown displayCat = (ScreenAutoDropdown)widgets.get("category");
	    ScreenAutoDropdown queryCat = displayCat.getQueryWidget();
	               
	    ((AutoCompleteDropdown)displayCat.getWidget()).setModel(storageUnitCategoryDropdown);
	    ((AutoCompleteDropdown)queryCat.getWidget()).setModel(storageUnitCategoryDropdown);
	}
}
