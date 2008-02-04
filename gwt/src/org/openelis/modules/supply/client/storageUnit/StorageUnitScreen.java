package org.openelis.modules.supply.client.storageUnit;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StorageUnitScreen extends AppScreenForm{

	private static StorageUnitServletIntAsync screenService = (StorageUnitServletIntAsync) GWT
	.create(StorageUnitServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;

	private Widget selected;
	
	public StorageUnitScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StorageUnitServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("#")) {
			getStorageUnits("#", sender);
		} else if (sender == widgets.get("a")) {
			getStorageUnits("a", sender);
		} else if (sender == widgets.get("b")) {
			getStorageUnits("b", sender);
		} else if (sender == widgets.get("c")) {
			getStorageUnits("c", sender);
		} else if (sender == widgets.get("d")) {
			getStorageUnits("d", sender);
		} else if (sender == widgets.get("e")) {
			getStorageUnits("e", sender);
		} else if (sender == widgets.get("f")) {
			getStorageUnits("f", sender);
		} else if (sender == widgets.get("g")) {
			getStorageUnits("g", sender);
		} else if (sender == widgets.get("h")) {
			getStorageUnits("h", sender);
		} else if (sender == widgets.get("i")) {
			getStorageUnits("i", sender);
		} else if (sender == widgets.get("j")) {
			getStorageUnits("j", sender);
		} else if (sender == widgets.get("k")) {
			getStorageUnits("k", sender);
		} else if (sender == widgets.get("l")) {
			getStorageUnits("l", sender);
		} else if (sender == widgets.get("m")) {
			getStorageUnits("m", sender);
		} else if (sender == widgets.get("n")) {
			getStorageUnits("n", sender);
		} else if (sender == widgets.get("o")) {
			getStorageUnits("o", sender);
		} else if (sender == widgets.get("p")) {
			getStorageUnits("p", sender);
		} else if (sender == widgets.get("q")) {
			getStorageUnits("q", sender);
		} else if (sender == widgets.get("r")) {
			getStorageUnits("r", sender);
		} else if (sender == widgets.get("s")) {
			getStorageUnits("s", sender);
		} else if (sender == widgets.get("t")) {
			getStorageUnits("t", sender);
		} else if (sender == widgets.get("u")) {
			getStorageUnits("u", sender);
		} else if (sender == widgets.get("v")) {
			getStorageUnits("v", sender);
		} else if (sender == widgets.get("w")) {
			getStorageUnits("w", sender);
		} else if (sender == widgets.get("x")) {
			getStorageUnits("x", sender);
		} else if (sender == widgets.get("y")) {
			getStorageUnits("y", sender);
		} else if (sender == widgets.get("z")) {
			getStorageUnits("z", sender);
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
		ScreenTextBox id = (ScreenTextBox) widgets.get("id");
		id.enable(false);

		//set focus to the name field
		AutoCompleteDropdown cat = (AutoCompleteDropdown)getWidget("category");
		cat.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("StorageUnitTable")).controller.unselect(-1);
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
		AutoCompleteDropdown cat = (AutoCompleteDropdown)getWidget("category");
		cat.setFocus(true);
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
	
	protected Widget setStyleNameOnButton(Widget sender) {
		sender.addStyleName("current");
		if (selected != null)
			selected.removeStyleName("current");
		selected = sender;
		return sender;
	}
	
private void loadDropdowns(){
		
		//load category dropdowns
		screenService.getInitialModel("category", new AsyncCallback(){
	           public void onSuccess(Object result){
	               DataModel catDataModel = (DataModel)result;
	               ScreenAutoDropdown displayCat = (ScreenAutoDropdown)widgets.get("category");
	               ScreenAutoDropdown queryCat = displayCat.getQueryWidget();
	               
	               ((AutoCompleteDropdown)displayCat.getWidget()).setModel(catDataModel);
	               ((AutoCompleteDropdown)queryCat.getWidget()).setModel(catDataModel);
	           }
	           public void onFailure(Throwable caught){
	        	   Window.alert(caught.getMessage());
	           }
	        });
	}
}
