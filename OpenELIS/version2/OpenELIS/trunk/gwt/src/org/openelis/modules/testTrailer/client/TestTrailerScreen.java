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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.testTrailer.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZPanel;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TestTrailerScreen extends OpenELISScreenForm {
	
	private TextBox nameTextBox;
	private ScreenTextArea textArea;
	
    private TestTrailerMetaMap TestTrailerMeta = new TestTrailerMetaMap();
     
	public TestTrailerScreen() {
		super("org.openelis.modules.testTrailer.server.TestTrailerService",false);
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

        AToZTable atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addChangeListener(atozTable);
        addChangeListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addChangeListener(this);
        
        nameTextBox = (TextBox) getWidget(TestTrailerMeta.getName());
        textArea = (ScreenTextArea) widgets.get(TestTrailerMeta.getText());

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
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue(TestTrailerMeta.getName(), query);

			commitQuery(letterRPC);
		}
	}
}
