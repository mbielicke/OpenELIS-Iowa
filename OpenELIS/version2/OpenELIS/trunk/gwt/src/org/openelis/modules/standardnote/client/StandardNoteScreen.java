package org.openelis.modules.standardnote.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardNoteScreen extends OpenELISScreenForm {

	private ScreenTextArea textArea;
	private TextBox nameTextbox;
	
	private static DataModel typeDropdown;
	private static boolean loaded = false;
	
	public StandardNoteScreen() {
    	super("org.openelis.modules.standardnote.server.StandardNoteService",!loaded);
    }

    public void onChange(Widget sender) {
        if(sender == getWidget("atozButtons")){
           String action = ((ButtonPanel)sender).buttonClicked.action;
           if(action.startsWith("query:")){
        	   getStandardNotes(action.substring(6, action.length()));      
           }
        }else{
            super.onChange(sender);
        }
    }

    public void afterDraw(boolean success) {
		loaded = true;
		setBpanel((ButtonPanel) getWidget("buttons"));
		
        AToZPanel atozTable = (AToZPanel) getWidget("hideablePanel");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        textArea = (ScreenTextArea)widgets.get("standardNote.text");
        nameTextbox = (TextBox)getWidget("standardNote.name");

        if(typeDropdown == null)
            typeDropdown = (DataModel)initData.get("noteTypes");
        
//      load standard note type dropdowns
        ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get("standardNote.type");
                   
       ((AutoCompleteDropdown)displayType.getWidget()).setModel(typeDropdown);
		
		super.afterDraw(success);
	}
	
	public void query() {
		super.query();
		
		//users cant query by text so disable it
		textArea.enable(false);

		nameTextbox.setFocus(true);
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

	
	public void commitQuery(FormRPC rpcQuery) {
    	super.commitQuery(rpcQuery);
    	
    	//enable the text area
    	textArea.enable(true);
    }

    private void getStandardNotes(String query) {
    	if (state == FormInt.DISPLAY || state == FormInt.DEFAULT) {
    
    		FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
    		
    		letterRPC.setFieldValue("standardNote.name", query);
    
    		commitQuery(letterRPC);
    	}
    }	
}
