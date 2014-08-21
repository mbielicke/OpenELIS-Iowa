package org.openelis.modules.standardnote.client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StandardNoteScreen extends OpenELISScreenForm {

	private Widget selected;
	
	public StandardNoteScreen() {
		super("org.openelis.modules.standardnote.server.StandardNoteService",true);
        name="Standard Note";
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
		String action = ((AppButton)sender).action;
		if(action.startsWith("query:")){
			getStandardNotes(action.substring(6, action.length()), sender);
			
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
		
		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
	}
	
	public void commitQuery(FormRPC rpcQuery) {
		super.commitQuery(rpcQuery);
		
		//enable the text area
		ScreenTextArea textArea = (ScreenTextArea)widgets.get("text");
		textArea.enable(true);
	}
	
	public void add(int state) {
		super.add(state);

		//set focus to the name field
		TextBox name = (TextBox)getWidget("name");
		name.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("StandardNoteTable")).controller.unselect(-1);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

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
	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
	
	private void loadDropdowns(){
		DataModel standardNoteTypeDropdown = (DataModel) initData[0];
		
//		load standard note type dropdowns
        ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("type");
        ScreenAutoDropdown queryType = displayType.getQueryWidget();
	               
       ((AutoCompleteDropdown)displayType.getWidget()).setModel(standardNoteTypeDropdown);
       ((AutoCompleteDropdown)queryType.getWidget()).setModel(standardNoteTypeDropdown);
	}
	
}
