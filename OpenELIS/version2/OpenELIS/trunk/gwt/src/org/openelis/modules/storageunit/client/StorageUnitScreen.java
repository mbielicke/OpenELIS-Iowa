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
package org.openelis.modules.storageunit.client;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.metamap.StorageUnitMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageUnitScreen extends OpenELISScreenForm {

	private AutoCompleteDropdown cat;
    private static boolean loaded = false;
    private static DataModel storageUnitCategoryDropdown;
	
    private StorageUnitMetaMap StorageUnitMeta = new StorageUnitMetaMap();
    
	public StorageUnitScreen() {
		super("org.openelis.modules.storageunit.server.StorageUnitService",!loaded);
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
		setBpanel((ButtonPanel) getWidget("buttons"));

        AToZTable atozTable = (AToZTable)getWidget("azTable");
        modelWidget.addCommandListener(atozTable);
        addCommandListener(atozTable);
        
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        atozButtons.addCommandListener(this);
        
        cat = (AutoCompleteDropdown)getWidget(StorageUnitMeta.getCategory());

        //load the dropdowns
        if(storageUnitCategoryDropdown == null)
            storageUnitCategoryDropdown = (DataModel)initData.get("categories");
        
        ScreenAutoDropdown displayCat = (ScreenAutoDropdown)widgets.get(StorageUnitMeta.getCategory());
                   
        ((AutoCompleteDropdown)displayCat.getWidget()).setModel(storageUnitCategoryDropdown);
        
		super.afterDraw(success);
	}
	
	public void query() {
    	super.query();
    	
    	//set focus to the name field
    	cat.setFocus(true);
    }

    public void add() {
		super.add();

		//set focus to the name field
		cat.setFocus(true);
	}
	
	public void afterUpdate(boolean success) {
		super.afterUpdate(success);

		//set focus to the name field
		cat.setFocus(true);
	}
	
	private void getStorageUnits(String query) {
		if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

			FormRPC letterRPC = (FormRPC) this.forms.get("queryByLetter");
			
			letterRPC.setFieldValue(StorageUnitMeta.getDescription(), query);

			commitQuery(letterRPC);
		}
	}
}
