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

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.metamap.StorageUnitMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class StorageUnitScreen extends OpenELISScreenForm<StorageUnitForm,Query<TableDataRow<Integer>>> {

    private static Dropdown category;
    private KeyListManager keyList = new KeyListManager();
	
    private StorageUnitMetaMap StorageUnitMeta = new StorageUnitMetaMap();
    
	public StorageUnitScreen() {                
	    super("org.openelis.modules.storageunit.server.StorageUnitService");
        query = new Query<TableDataRow<Integer>>();
	    getScreen(new StorageUnitForm());
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
		ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(bpanel);
        chain.addCommand(this);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        chain.addCommand(keyList);
              
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        category = (Dropdown)getWidget(StorageUnitMeta.getCategory());
        startWidget = (ScreenInputWidget)widgets.get(StorageUnitMeta.getCategory());
        
		super.afterDraw(success);
		
		ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("storage_unit_category");
        model = getDictionaryEntryKeyList(cache);
		category.setModel(model);
	}
	
	private void getStorageUnits(String query) {
		if (state == State.DISPLAY || state == State.DEFAULT) {
		    QueryStringField qField = new QueryStringField(StorageUnitMeta.getDescription());
            qField.setValue(query);
			commitQuery(qField);
		}
	}
	
	private TableDataModel<TableDataRow> getDictionaryEntryKeyList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<String> row = new TableDataRow<String>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getEntry();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}
