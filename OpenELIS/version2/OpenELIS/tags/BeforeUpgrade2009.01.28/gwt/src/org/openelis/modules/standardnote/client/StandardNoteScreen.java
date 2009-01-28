/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.standardnote.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.TextBox;

public class StandardNoteScreen extends OpenELISScreenForm {

	private ScreenTextArea textArea;
	private TextBox nameTextbox;
    private KeyListManager keyList = new KeyListManager();
	
	private static DataModel typeDropdown;
	private static boolean loaded = false;
	
    private StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public StandardNoteScreen() {
    	super("org.openelis.modules.standardnote.server.StandardNoteService",!loaded);
    }

    public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton){
           String baction = ((AppButton)obj).action;
           if(baction.startsWith("query:")){
        	   getStandardNotes(baction.substring(6, baction.length()));      
           }else
               super.performCommand(action,obj);
        }else{
            super.performCommand(action, obj);
        }
    }

    public void afterDraw(boolean success) {
		loaded = true;
        AToZTable atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        textArea = (ScreenTextArea)widgets.get(StandardNoteMeta.getText());
        nameTextbox = (TextBox)getWidget(StandardNoteMeta.getName());

        startWidget = (ScreenInputWidget)widgets.get(StandardNoteMeta.getName());
        
        if(typeDropdown == null)
            typeDropdown = (DataModel)initData.get("noteTypes");
        
//      load standard note type dropdowns
        ScreenDropDownWidget displayType = (ScreenDropDownWidget)widgets.get(StandardNoteMeta.getTypeId());
                   
       ((Dropdown)displayType.getWidget()).setModel(typeDropdown);
		
		super.afterDraw(success);
	}
	
	public void query() {
		super.query();
		
		//users cant query by text so disable it
		textArea.enable(false);

		nameTextbox.setFocus(true);
    }

	private void getStandardNotes(String query) {
    	if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
    
    		FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
    		
    		letterRPC.setFieldValue(StandardNoteMeta.getName(), query);
    
    		commitQuery(letterRPC);
    	}
    }	
}
