package org.openelis.modules.standardnote.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardNoteScreen extends OpenELISScreenForm {

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
		loaded = true;
		bpanel = (ButtonPanel) getWidget("buttons");

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
		
		textArea = (ScreenTextArea)widgets.get("standardNote.text");
		nameTextbox = (TextBox)getWidget("standardNote.name");

		message.setText("Done");
		
		loadDropdowns();
		
		super.afterDraw(success);
	}
	
	public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getStandardNotes(action.substring(6, action.length()), ((ButtonPanel)sender).buttonClicked);      
           }
        }else{
            super.onChange(sender);
        }
    }
	
	private void getStandardNotes(String letter, Widget sender) {
		// we only want to allow them to select a letter if they are in display
		// mode..
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue("standardNote.name", letter.toUpperCase() + "* | "+letter.toLowerCase()+"*");

			commitQuery(letterRPC);

			setStyleNameOnButton(sender);
		}
	}
	
	public void query() {
		super.query();
		
		//users cant query by text so disable it
		textArea.enable(false);

		nameTextbox.setFocus(true);
	}
	
	public void commitQuery(FormRPC rpcQuery) {
		super.commitQuery(rpcQuery);
		
		//enable the text area
		textArea.enable(true);
	}
	
	public void add() {
		super.add();

		//set focus to the name field
		nameTextbox.setFocus(true);
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
	               
       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDropdown);
	}	
}
