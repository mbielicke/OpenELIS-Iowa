package org.openelis.client.supply.screen.storage;

import org.openelis.gwt.client.screen.AppScreenForm;
import org.openelis.gwt.client.screen.ScreenTextBox;
import org.openelis.gwt.client.widget.AppButton;
import org.openelis.gwt.client.widget.ButtonPanel;
import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableWidget;
import org.openelis.gwt.common.FormRPC;

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
		if (sender == widgets.get("#")) {
			getStorageLocs("#", sender);
		} else if (sender == widgets.get("a")) {
			getStorageLocs("a", sender);
		} else if (sender == widgets.get("b")) {
			getStorageLocs("b", sender);
		} else if (sender == widgets.get("c")) {
			getStorageLocs("c", sender);
		} else if (sender == widgets.get("d")) {
			getStorageLocs("d", sender);
		} else if (sender == widgets.get("e")) {
			getStorageLocs("e", sender);
		} else if (sender == widgets.get("f")) {
			getStorageLocs("f", sender);
		} else if (sender == widgets.get("g")) {
			getStorageLocs("g", sender);
		} else if (sender == widgets.get("h")) {
			getStorageLocs("h", sender);
		} else if (sender == widgets.get("i")) {
			getStorageLocs("i", sender);
		} else if (sender == widgets.get("j")) {
			getStorageLocs("j", sender);
		} else if (sender == widgets.get("k")) {
			getStorageLocs("k", sender);
		} else if (sender == widgets.get("l")) {
			getStorageLocs("l", sender);
		} else if (sender == widgets.get("m")) {
			getStorageLocs("m", sender);
		} else if (sender == widgets.get("n")) {
			getStorageLocs("n", sender);
		} else if (sender == widgets.get("o")) {
			getStorageLocs("o", sender);
		} else if (sender == widgets.get("p")) {
			getStorageLocs("p", sender);
		} else if (sender == widgets.get("q")) {
			getStorageLocs("q", sender);
		} else if (sender == widgets.get("r")) {
			getStorageLocs("r", sender);
		} else if (sender == widgets.get("s")) {
			getStorageLocs("s", sender);
		} else if (sender == widgets.get("t")) {
			getStorageLocs("t", sender);
		} else if (sender == widgets.get("u")) {
			getStorageLocs("u", sender);
		} else if (sender == widgets.get("v")) {
			getStorageLocs("v", sender);
		} else if (sender == widgets.get("w")) {
			getStorageLocs("w", sender);
		} else if (sender == widgets.get("x")) {
			getStorageLocs("x", sender);
		} else if (sender == widgets.get("y")) {
			getStorageLocs("y", sender);
		} else if (sender == widgets.get("z")) {
			getStorageLocs("z", sender);
		}
	}
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

//		 get storage unit table and set the managers form
		TableWidget storageTable = (TableWidget) getWidget("storageLocsTable");
		modelWidget.addChangeListener(storageTable.controller);

		message.setText("done");

		((StorageNameTable) storageTable.controller.manager)
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
	
	protected Widget setStyleNameOnButton(Widget sender) {
		sender.addStyleName("current");
		if (selected != null)
			selected.removeStyleName("current");
		selected = sender;
		return sender;
	}

}
