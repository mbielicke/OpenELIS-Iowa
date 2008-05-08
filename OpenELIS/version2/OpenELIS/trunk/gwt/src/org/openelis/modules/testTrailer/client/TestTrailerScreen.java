package org.openelis.modules.testTrailer.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TestTrailerScreen extends OpenELISScreenForm {
	
	private TextBox nameTextBox;
	private ScreenTextArea textArea;
	
	public TestTrailerScreen() {
		super("org.openelis.modules.testTrailer.server.TestTrailerService",false);
        name="Trailer For Test";
	}
	
	 public void onChange(Widget sender) {
	        if(sender == getWidget("atozButtons")){
	           String action = ((ButtonPanel)sender).buttonClicked.action;
	           if(action.startsWith("query:")){
	        	   getTestTrailers(action.substring(6, action.length()));      
	           }
	        }else{
	            super.onChange(sender);
	        }
	    }
	
	public void afterDraw(boolean success) {
		setBpanel((ButtonPanel) getWidget("buttons"));

		AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
		modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextBox = (TextBox) getWidget("testTrailer.name");
        textArea = (ScreenTextArea) widgets.get("testTrailer.text");
        
		message.setText("Done");

		super.afterDraw(success);
	}
	
	public void query() {
    		super.query();
    		
    //		users cant query by text so disable it
    		textArea.enable(false);
    		
    		//set focus to the name field
    		nameTextBox.setFocus(true);
    	}

    public void add() {
		super.add();

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	private void getTestTrailers(String query) {
		if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue("testTrailer.name", query);

			commitQuery(letterRPC);
		}
	}
}
