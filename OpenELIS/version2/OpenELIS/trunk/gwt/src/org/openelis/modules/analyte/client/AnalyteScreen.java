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
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class AnalyteScreen extends OpenELISScreenForm {
	
	private TextBox nameTextBox;
    private KeyListManager keyList = new KeyListManager();
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
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        AToZTable atozTable = (AToZTable)getWidget("azTable");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        nameTextBox = (TextBox) getWidget(Meta.getName());
        
        startWidget = (ScreenInputWidget)widgets.get(Meta.getName());

		super.afterDraw(success);
	}
	
	private void getAnalytes(String query) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");

			letterRPC.setFieldValue(Meta.getName(), query);

			commitQuery(letterRPC);
		}
	}
}
