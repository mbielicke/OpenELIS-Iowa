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
package org.openelis.modules.storageunit.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.StorageUnitMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageUnitScreen extends OpenELISScreenForm<RPC<Form,Data>,Form> {

	private Dropdown cat;
    private static boolean loaded = false;
    private static DataModel storageUnitCategoryDropdown;
    private KeyListManager keyList = new KeyListManager();
	
    private StorageUnitMetaMap StorageUnitMeta = new StorageUnitMetaMap();
    
	public StorageUnitScreen() {
		super("org.openelis.modules.storageunit.server.StorageUnitService",!loaded,new RPC<Form,Data>());
	}
	
	 public void performCommand(Enum action, Object obj) {
	        if(obj instanceof AppButton){
	           String baction = ((AppButton)obj).action;
	           if(baction.startsWith("query:")){
	        	   getStorageUnits(baction.substring(6, baction.length()));      
	           }else
                   super.performCommand(action, obj);
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
        chain.addCommand(bpanel);
        chain.addCommand(this);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        chain.addCommand(keyList);
              
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        cat = (Dropdown)getWidget(StorageUnitMeta.getCategory());
        startWidget = (ScreenInputWidget)widgets.get(StorageUnitMeta.getCategory());

        //load the dropdowns
        if(storageUnitCategoryDropdown == null)
            storageUnitCategoryDropdown = (DataModel)initData.get("categories");
        
        cat.setModel(storageUnitCategoryDropdown);
        
		super.afterDraw(success);
	}
	
	private void getStorageUnits(String query) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			Form letter = (Form)forms.get("queryByLetter");
			
			letter.setFieldValue(StorageUnitMeta.getDescription(), query);

			commitQuery(letter);
		}
	}
}
