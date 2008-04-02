package org.openelis.modules.standardnote.client;

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

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardNoteScreen extends OpenELISScreenForm {

	private TableWidget standardNoteTable;
	private ScreenTextArea textArea;
	private TextBox nameTextbox;
	private Widget selected;
	
	private static DataModel typeDropdown;
	private static boolean loaded = false;
	
	public StandardNoteScreen() {
		super("org.openelis.modules.standardnote.server.StandardNoteService",!loaded);
        name="Standard Note";
	}
	
	public void afterDraw(boolean success) {

		bpanel = (ButtonPanel) getWidget("buttons");

//		 get storage unit table and set the managers form
		standardNoteTable = (TableWidget) getWidget("StandardNoteTable");
		modelWidget.addChangeListener(standardNoteTable.controller);
		
		textArea = (ScreenTextArea)widgets.get("standardNote.text");
		nameTextbox = (TextBox)getWidget("standardNote.name");

		message.setText("done");

		((StandardNoteNameTable) standardNoteTable.controller.manager).setStandardNoteForm(this);
		
		loadDropdowns();
		
		super.afterDraw(success);
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
		if (bpanel.getState() == FormInt.DISPLAY || bpanel.getState() == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue("standardNote.name", letter.toUpperCase() + "* | "+letter.toLowerCase()+"*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	public void query(int state) {
		super.query(state);
		
		//users cant query by text so disable it
		textArea.enable(false);

		nameTextbox.setFocus(true);
	}
	
	public void commitQuery(FormRPC rpcQuery) {
		super.commitQuery(rpcQuery);
		
		//enable the text area
		textArea.enable(true);
	}
	
	public void add(int state) {
		super.add(state);

		//set focus to the name field
		nameTextbox.setFocus(true);
		
//		 unselect the row from the table
		((TableWidget) getWidget("StandardNoteTable")).controller.unselect(-1);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextbox.setFocus(true);
	}

	
	protected void setStyleNameOnButton(Widget sender) {
		((AppButton)sender).changeState(AppButton.PRESSED);
		if (selected != null)
			((AppButton)selected).changeState(AppButton.UNPRESSED);
		selected = sender;
	}
	
	private void loadDropdowns(){
		if(typeDropdown == null)
		    typeDropdown = (DataModel)initData[0];
		
//		load standard note type dropdowns
        ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("standardNote.type");
       // ScreenAutoDropdown queryType = displayType.getQueryWidget();
	               
       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDropdown);
       //((AutoCompleteDropdown)queryType.getWidget()).setModel(standardNoteTypeDropdown);
	}
	
}
