package org.openelis.modules.supply.client.storage;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenBase;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.dataEntry.client.organization.OrganizationContactsTable;
import org.openelis.modules.supply.client.storageUnit.StorageUnitDescTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StorageLocationScreen extends AppScreenForm{
    
	private static StorageLocationServletIntAsync screenService = (StorageLocationServletIntAsync) GWT
	.create(StorageLocationServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;
	
	private Widget selected;
	
	public StorageLocationScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StorageLocationServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}
	
	public void onClick(Widget sender) {
		if (sender == getWidget("a")) {
			getStorageLocs("a", sender);
		} else if (sender == getWidget("b")) {
			getStorageLocs("b", sender);
		} else if (sender == getWidget("c")) {
			getStorageLocs("c", sender);
		} else if (sender == getWidget("d")) {
			getStorageLocs("d", sender);
		} else if (sender == getWidget("e")) {
			getStorageLocs("e", sender);
		} else if (sender == getWidget("f")) {
			getStorageLocs("f", sender);
		} else if (sender == getWidget("g")) {
			getStorageLocs("g", sender);
		} else if (sender == getWidget("h")) {
			getStorageLocs("h", sender);
		} else if (sender == getWidget("i")) {
			getStorageLocs("i", sender);
		} else if (sender == getWidget("j")) {
			getStorageLocs("j", sender);
		} else if (sender == getWidget("k")) {
			getStorageLocs("k", sender);
		} else if (sender == getWidget("l")) {
			getStorageLocs("l", sender);
		} else if (sender == getWidget("m")) {
			getStorageLocs("m", sender);
		} else if (sender == getWidget("n")) {
			getStorageLocs("n", sender);
		} else if (sender == getWidget("o")) {
			getStorageLocs("o", sender);
		} else if (sender == getWidget("p")) {
			getStorageLocs("p", sender);
		} else if (sender == getWidget("q")) {
			getStorageLocs("q", sender);
		} else if (sender == getWidget("r")) {
			getStorageLocs("r", sender);
		} else if (sender == getWidget("s")) {
			getStorageLocs("s", sender);
		} else if (sender == getWidget("t")) {
			getStorageLocs("t", sender);
		} else if (sender == getWidget("u")) {
			getStorageLocs("u", sender);
		} else if (sender == getWidget("v")) {
			getStorageLocs("v", sender);
		} else if (sender == getWidget("w")) {
			getStorageLocs("w", sender);
		} else if (sender == getWidget("x")) {
			getStorageLocs("x", sender);
		} else if (sender == getWidget("y")) {
			getStorageLocs("y", sender);
		} else if (sender == getWidget("z")) {
			getStorageLocs("z", sender);
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
		ScreenTextBox id = (ScreenTextBox) widgets.get("id");
		id.enable(false);

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
		
		//set focus to the id field
		TextBox id = (TextBox)getWidget("id");
		id.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
		
		ScreenTextBox id = (ScreenTextBox) widgets.get("id");
		id.enable(false);

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
	
	protected Widget setStyleNameOnButton(Widget sender) {
		sender.addStyleName("current");
		if (selected != null)
			selected.removeStyleName("current");
		selected = sender;
		return sender;
	}

}
