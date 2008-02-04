package org.openelis.modules.utilities.client.standardNote;

import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardNoteScreen extends AppScreenForm {

	private static StandardNoteServletIntAsync screenService = (StandardNoteServletIntAsync) GWT
	.create(StandardNoteServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;
	
	private Widget selected;
	
	public StandardNoteScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StandardNoteServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

//		 get storage unit table and set the managers form
		//TableWidget storageTable = (TableWidget) getWidget("storageLocsTable");
		//modelWidget.addChangeListener(storageTable.controller);

		message.setText("done");

		//((StorageNameTable) storageTable.controller.manager)
		//		.setStorageForm(this);

		super.afterDraw(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		bpanel.setButtonState("delete", AppButton.DISABLED);
		
		loadDropdowns();
	}
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("a")) {
			getStandardNotes("a", sender);
		} else if (sender == widgets.get("b")) {
			getStandardNotes("b", sender);
		} else if (sender == widgets.get("c")) {
			getStandardNotes("c", sender);
		} else if (sender == widgets.get("d")) {
			getStandardNotes("d", sender);
		} else if (sender == widgets.get("e")) {
			getStandardNotes("e", sender);
		} else if (sender == widgets.get("f")) {
			getStandardNotes("f", sender);
		} else if (sender == widgets.get("g")) {
			getStandardNotes("g", sender);
		} else if (sender == widgets.get("h")) {
			getStandardNotes("h", sender);
		} else if (sender == widgets.get("i")) {
			getStandardNotes("i", sender);
		} else if (sender == widgets.get("j")) {
			getStandardNotes("j", sender);
		} else if (sender == widgets.get("k")) {
			getStandardNotes("k", sender);
		} else if (sender == widgets.get("l")) {
			getStandardNotes("l", sender);
		} else if (sender == widgets.get("m")) {
			getStandardNotes("m", sender);
		} else if (sender == widgets.get("n")) {
			getStandardNotes("n", sender);
		} else if (sender == widgets.get("o")) {
			getStandardNotes("o", sender);
		} else if (sender == widgets.get("p")) {
			getStandardNotes("p", sender);
		} else if (sender == widgets.get("q")) {
			getStandardNotes("q", sender);
		} else if (sender == widgets.get("r")) {
			getStandardNotes("r", sender);
		} else if (sender == widgets.get("s")) {
			getStandardNotes("s", sender);
		} else if (sender == widgets.get("t")) {
			getStandardNotes("t", sender);
		} else if (sender == widgets.get("u")) {
			getStandardNotes("u", sender);
		} else if (sender == widgets.get("v")) {
			getStandardNotes("v", sender);
		} else if (sender == widgets.get("w")) {
			getStandardNotes("w", sender);
		} else if (sender == widgets.get("x")) {
			getStandardNotes("x", sender);
		} else if (sender == widgets.get("y")) {
			getStandardNotes("y", sender);
		} else if (sender == widgets.get("z")) {
			getStandardNotes("z", sender);
		}
	}
	
	private void getStandardNotes(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (bpanel.getState() == FormInt.DISPLAY) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue("name", letter.toUpperCase() + "*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	public void query(int state) {
		super.query(state);
		
		//users cant query by text so disable it
		ScreenTextArea textArea = (ScreenTextArea)widgets.get("text");
		textArea.enable(false);
	}
	
	public void commitQuery(FormRPC rpcQuery) {
		super.commitQuery(rpcQuery);
		
		//enable the text area
		ScreenTextArea textArea = (ScreenTextArea)widgets.get("text");
		textArea.enable(true);
	}
	
	public void add(int state) {
		super.add(state);
		
		ScreenTextBox id = (ScreenTextBox) widgets.get("id");
		id.enable(false);

		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("StandardNoteTable")).controller.unselect(-1);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);
		
		ScreenTextBox id = (ScreenTextBox) widgets.get("id");
		id.enable(false);

		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
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
		screenService.getInitialModel("type", new AsyncCallback(){
	           public void onSuccess(Object result){
	               DataModel typeDataModel = (DataModel)result;
	               ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("type");
	               ScreenAutoDropdown queryType = displayType.getQueryWidget();
	               
	               ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDataModel);
	               ((AutoCompleteDropdown)queryType.getWidget()).setModel(typeDataModel);
	           }
	           public void onFailure(Throwable caught){
	        	   Window.alert(caught.getMessage());
	           }
	        });
	}
	
}
