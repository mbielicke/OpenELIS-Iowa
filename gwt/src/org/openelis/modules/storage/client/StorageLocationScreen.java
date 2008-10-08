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
package org.openelis.modules.storage.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageLocationScreen extends OpenELISScreenForm implements ClickListener {
	
	private TextBox nameTextbox;
	private EditTable childTable;
    private KeyListManager keyList = new KeyListManager();
	
    private AppButton removeEntryButton;
   	
    private StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
	public StorageLocationScreen() {
		super("org.openelis.modules.storage.server.StorageLocationService",false);
	}
	
	public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton) {
           String baction = ((AppButton)obj).action;
           if(baction.startsWith("query:")){
        	   getStorageLocs(baction.substring(6, baction.length()));      
           }else
               super.performCommand(action,obj);
        }else{
            super.performCommand(action, obj);
        }
    }

	
	public void onClick(Widget sender) {
		String action = ((AppButton)sender).action;
        if (action.equals("removeRow")) {
			onRemoveRowButtonClick();
		}
	}
	
	public void afterDraw(boolean success) {
        AToZTable atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
       
        removeEntryButton = (AppButton) getWidget("removeEntryButton");
        
        nameTextbox = (TextBox)getWidget(StorageLocationMeta.getName());
        startWidget = (ScreenInputWidget)widgets.get(StorageLocationMeta.getName());
		childTable = ((TableWidget) getWidget("childStorageLocsTable")).controller;
        addCommandListener(childTable);
		
		((ChildStorageLocsTable) childTable.manager).setStorageForm(this);

		super.afterDraw(success);
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextbox.setFocus(true);
		
		removeEntryButton.changeState(AppButton.ButtonState.DISABLED);
	}
	
	private void onRemoveRowButtonClick(){
			int selectedRow = childTable.selected;
			if (selectedRow > -1 && childTable.model.numRows() > 0) {
				TableRow row = childTable.model.getRow(selectedRow);
                childTable.model.hideRow(row);
				// delete the last row of the table because it is autoadd
				childTable.model.deleteRow(childTable.model.numRows() - 1);
				// reset the model
				childTable.reset();
				// need to set the deleted flag to "Y" also
				StringField deleteFlag = new StringField();
				deleteFlag.setValue("Y");

				row.addHidden("deleteFlag", deleteFlag);
			}
    }

    private void getStorageLocs(String query) {
    	if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {
    
    		FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
    		
    		letterRPC.setFieldValue(StorageLocationMeta.getName(), query);
    
    		commitQuery(letterRPC);
    	}
    }
}
