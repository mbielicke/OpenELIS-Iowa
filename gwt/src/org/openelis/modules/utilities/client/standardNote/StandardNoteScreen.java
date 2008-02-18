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
		TableWidget standardNoteTable = (TableWidget) getWidget("StandardNoteTable");
		modelWidget.addChangeListener(standardNoteTable.controller);

		message.setText("done");

		((StandardNoteNameTable) standardNoteTable.controller.manager).setStandardNoteForm(this);

		super.afterDraw(success);
		
		bpanel.setButtonState("prev", AppButton.DISABLED);
		bpanel.setButtonState("next", AppButton.DISABLED);
		bpanel.setButtonState("delete", AppButton.DISABLED);
		
		loadDropdowns();
	}
	
	public void onClick(Widget sender) {
		if (sender == getWidget("a")) {
			getStandardNotes("a", sender);
		} else if (sender == getWidget("b")) {
			getStandardNotes("b", sender);
		} else if (sender == getWidget("c")) {
			getStandardNotes("c", sender);
		} else if (sender == getWidget("d")) {
			getStandardNotes("d", sender);
		} else if (sender == getWidget("e")) {
			getStandardNotes("e", sender);
		} else if (sender == getWidget("f")) {
			getStandardNotes("f", sender);
		} else if (sender == getWidget("g")) {
			getStandardNotes("g", sender);
		} else if (sender == getWidget("h")) {
			getStandardNotes("h", sender);
		} else if (sender == getWidget("i")) {
			getStandardNotes("i", sender);
		} else if (sender == getWidget("j")) {
			getStandardNotes("j", sender);
		} else if (sender == getWidget("k")) {
			getStandardNotes("k", sender);
		} else if (sender == getWidget("l")) {
			getStandardNotes("l", sender);
		} else if (sender == getWidget("m")) {
			getStandardNotes("m", sender);
		} else if (sender == getWidget("n")) {
			getStandardNotes("n", sender);
		} else if (sender == getWidget("o")) {
			getStandardNotes("o", sender);
		} else if (sender == getWidget("p")) {
			getStandardNotes("p", sender);
		} else if (sender == getWidget("q")) {
			getStandardNotes("q", sender);
		} else if (sender == getWidget("r")) {
			getStandardNotes("r", sender);
		} else if (sender == getWidget("s")) {
			getStandardNotes("s", sender);
		} else if (sender == getWidget("t")) {
			getStandardNotes("t", sender);
		} else if (sender == getWidget("u")) {
			getStandardNotes("u", sender);
		} else if (sender == getWidget("v")) {
			getStandardNotes("v", sender);
		} else if (sender == getWidget("w")) {
			getStandardNotes("w", sender);
		} else if (sender == getWidget("x")) {
			getStandardNotes("x", sender);
		} else if (sender == getWidget("y")) {
			getStandardNotes("y", sender);
		} else if (sender == getWidget("z")) {
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
		
		//set focus to the id field
		TextBox id = (TextBox)getWidget("id");
		id.setFocus(true);
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
	
	public void abort(int state) {
		super.abort(state);
		
//		 need to get the standard Note table
		TableWidget standardNoteTable = (TableWidget) getWidget("StandardNoteTable");
		int rowSelected = standardNoteTable.controller.selected;

		// set the update button if needed
		if (rowSelected == -1){
			bpanel.setButtonState("update", AppButton.DISABLED);
			bpanel.setButtonState("prev", AppButton.DISABLED);
			bpanel.setButtonState("next", AppButton.DISABLED);
			bpanel.setButtonState("delete", AppButton.DISABLED);
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
