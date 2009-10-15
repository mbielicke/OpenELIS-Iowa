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

import org.openelis.gwt.common.data.deprecated.KeyListManager;
import org.openelis.gwt.common.data.deprecated.QueryStringField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.screen.deprecated.CommandChain;
import org.openelis.gwt.screen.deprecated.ScreenInputWidget;
import org.openelis.gwt.widget.deprecated.AppButton;
import org.openelis.gwt.widget.deprecated.ButtonPanel;
import org.openelis.gwt.widget.deprecated.ResultsTable;
import org.openelis.gwt.widget.table.deprecated.TableManager;
import org.openelis.gwt.widget.table.deprecated.TableWidget;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StorageLocationScreen extends OpenELISScreenForm<StorageLocationForm,Query<TableDataRow<Integer>>> implements ClickListener, TableManager {
	
	private TextBox nameTextbox;
	private TableWidget childTable;
    private KeyListManager keyList = new KeyListManager();
	
    private AppButton removeEntryButton;
   	
    private StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();
    
    public StorageLocationScreen() {                
        super("org.openelis.modules.storage.server.StorageLocationService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new StorageLocationForm());
    }
    
	public void performCommand(Enum action, Object obj) {
        if(obj instanceof AppButton) {
           String baction = ((AppButton)obj).action;
           if(baction.indexOf("*") > -1){
        	   getStorageLocs(baction);      
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
        ResultsTable atozTable = (ResultsTable)getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(bpanel);
        chain.addCommand(keyList);
        chain.addCommand(atozButtons);
        chain.addCommand(atozTable);
        
       //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
       
        removeEntryButton = (AppButton) getWidget("removeEntryButton");
        
        nameTextbox = (TextBox)getWidget(StorageLocationMeta.getName());
        startWidget = (ScreenInputWidget)widgets.get(StorageLocationMeta.getName());
		childTable = (TableWidget) getWidget("childStorageLocsTable");
        childTable.model.enableAutoAdd(false);

		super.afterDraw(success);
	}
	
	public void query() {
		super.query();
		
		//set focus to the name field
		nameTextbox.setFocus(true);
		
		removeEntryButton.changeState(AppButton.ButtonState.DISABLED);
	}
    
    public void add() {
        super.add();
        childTable.model.enableAutoAdd(true);
    }
    
    public void update() {
        super.update();
        childTable.model.enableAutoAdd(true);
    }
    
    public void abort() {
        super.abort();
        childTable.model.enableAutoAdd(false);
    }
	
	private void onRemoveRowButtonClick(){
        int selectedRow = childTable.model.getSelectedIndex();
        if (selectedRow > -1 && childTable.model.numRows() > 0) {
            childTable.model.deleteRow(selectedRow);

        }
    }

    private void getStorageLocs(String query) {
    	if (state == State.DISPLAY || state == State.DEFAULT) {
    	    QueryStringField qField = new QueryStringField(StorageLocationMeta.getName());
            qField.setValue(query);
    		commitQuery(qField);
    	}
    }

    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return addRow.cells[0].getValue() != null;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(state == State.ADD || state == State.UPDATE || state == state.QUERY)
            return true;
        
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE || state == state.QUERY)
            return true;
        
        return false;
    }
    //
    //end table manager methods
    //
}
