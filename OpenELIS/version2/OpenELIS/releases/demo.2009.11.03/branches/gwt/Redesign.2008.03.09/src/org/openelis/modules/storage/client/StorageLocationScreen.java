package org.openelis.modules.storage.client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageLocationScreen extends OpenELISScreenForm {
	
	private Widget selected;
	
	public StorageLocationScreen() {
		super("org.openelis.modules.storage.server.StorageLocationService",false);
        name = "Storage Location";
	}
	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
		if(action.startsWith("query:")){
			getStorageLocs(action.substring(6, action.length()), sender);
			
		}
	}
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

//		 get storage unit table and set the managers form
		TableWidget storageTable = (TableWidget) getWidget("storageLocsTable");
		modelWidget.addChangeListener(storageTable.controller);

		message.setText("done");

		TableWidget storageChildTable = (TableWidget) getWidget("childStorageLocsTable");
		
		((StorageNameTable) storageTable.controller.manager)
				.setStorageForm(this);
		((ChildStorageLocsTable) storageChildTable.controller.manager)
				.setStorageForm(this);

		super.afterDraw(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		bpanel.setButtonState("delete", AppButton.DISABLED);
	}
	
	public void add(int state) {
		super.add(state);

		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("storageLocsTable")).controller.unselect(-1);
		
		TableWidget childTable = (TableWidget) getWidget("childStorageLocsTable");
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		childTableManager.disableRows = false;
		childTable.controller.setAutoAdd(true);
		childTable.controller.addRow();
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
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
		
		TableWidget childTable = (TableWidget) getWidget("childStorageLocsTable");
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) childTable.controller.manager;
		childTableManager.disableRows = false;
		childTable.controller.setAutoAdd(true);
		childTable.controller.addRow();
	}
	
	public void abort(int state) {
		super.abort(state);
		
		TableWidget childTable = (TableWidget) getWidget("childStorageLocsTable");
		childTable.controller.setAutoAdd(false);
		
		//if add delete the last row
		if (state == FormInt.UPDATE || state == FormInt.ADD)
			childTable.controller.deleteRow(childTable.controller.model.numRows() - 1);
		
//		 need to get the storage locs table
		TableWidget StorageLocsTable = (TableWidget) getWidget("storageLocsTable");
		int rowSelected = StorageLocsTable.controller.selected;

		// set the update button if needed
		if (rowSelected == -1){
			bpanel.setButtonState("update", AppButton.DISABLED);
			bpanel.setButtonState("prev", AppButton.DISABLED);
			bpanel.setButtonState("next", AppButton.DISABLED);
			bpanel.setButtonState("delete", AppButton.DISABLED);
		}
	}
	private void getStorageLocs(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (bpanel.getState() == FormInt.DISPLAY) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			if(letter.equals("#"))
				letterRPC.setFieldValue("name", "0* | 1* | 2* | 3* | 4* | 5* | 6* | 7* | 8* | 9*");
			else
				letterRPC.setFieldValue("name", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	public void afterCommitUpdate(boolean success) {
		ChildStorageLocsTable childTableManager = (ChildStorageLocsTable) ((TableWidget) getWidget("childStorageLocsTable")).controller.manager;
		childTableManager.disableRows = true;
		TableWidget childTable = (TableWidget) getWidget("childStorageLocsTable");
		childTable.controller.setAutoAdd(false);
		
		super.afterCommitUpdate(success);
	}
	
	public void commit(int state) {
		if (state == FormInt.QUERY) {
			((TableWidget) ((ScreenTableWidget) ((ScreenTableWidget) widgets
					.get("childStorageLocsTable")).getQueryWidget()).getWidget()).controller
					.unselect(-1);
		} else {
			((TableWidget) getWidget("childStorageLocsTable")).controller.unselect(-1);
		}
		super.commit(state);
	}
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
}
