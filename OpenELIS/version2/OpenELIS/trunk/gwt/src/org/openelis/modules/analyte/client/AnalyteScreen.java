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
package org.openelis.modules.analyte.client;

import com.google.gwt.user.client.ui.TextBox;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class AnalyteScreen extends OpenELISScreenForm {
	
	private TextBox nameTextBox;
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();

	public AnalyteScreen() {
		super("org.openelis.modules.analyte.server.AnalyteService",false);
    }
     
    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){
            String actionString = ((AppButton)obj).action;
            if(actionString.startsWith("query:")){
                getAnalytes(actionString.substring(6, actionString.length()));
            }else 
                super.performCommand(action, obj);
        }else{
            super.performCommand(action, obj);
        }
    }
	
	public void afterDraw(boolean success) {
	
		setBpanel((ButtonPanel) getWidget("buttons"));

        AToZTable atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addCommandListener(atozTable);
        addCommandListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addCommandListener(this);
        
        nameTextBox = (TextBox) getWidget(Meta.getName());

		super.afterDraw(success);
	}
	
	public void add() {
		super.add();

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		nameTextBox.setFocus(true);
	}
	
	private void getAnalytes(String query) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");

			letterRPC.setFieldValue(Meta.getName(), query);

			commitQuery(letterRPC);
		}
	}
}
