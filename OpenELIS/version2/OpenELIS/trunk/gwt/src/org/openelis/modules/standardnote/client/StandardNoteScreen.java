/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.modules.standardnote.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardNoteScreen extends OpenELISScreenForm {

	private ScreenTextArea textArea;
	private TextBox nameTextbox;
	
	private static DataModel typeDropdown;
	private static boolean loaded = false;
	
    private StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
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
		
        AToZTable atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        textArea = (ScreenTextArea)widgets.get(StandardNoteMeta.getText());
        nameTextbox = (TextBox)getWidget(StandardNoteMeta.getName());

        if(typeDropdown == null)
            typeDropdown = (DataModel)initData.get("noteTypes");
        
//      load standard note type dropdowns
        ScreenAutoDropdown displayType = (ScreenAutoDropdown)widgets.get(StandardNoteMeta.getTypeId());
                   
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
    	if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
    
    		FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
    		
    		letterRPC.setFieldValue(StandardNoteMeta.getName(), query);
    
    		commitQuery(letterRPC);
    	}
    }	
}
