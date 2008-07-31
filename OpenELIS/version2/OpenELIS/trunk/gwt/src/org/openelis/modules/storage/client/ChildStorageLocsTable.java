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
package org.openelis.modules.storage.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class ChildStorageLocsTable implements TableManager {
    private StorageLocationScreen userForm;
    public boolean disableRows = false;
    
    public void setStorageForm(StorageLocationScreen form) {
        userForm = form;
    }  

    public boolean canSelect(int row, TableController controller) {        
    	if(userForm.state == FormInt.State.ADD || userForm.state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
    	if(disableRows){
            return false;
        }
        
       return true;
    }

    public boolean canDelete(int row, TableController controller) {
            return true;
      }

    public boolean action(int row, int col, TableController controller) {
        
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
    	return false;     
    }

    public void finishedEditing(int row, int col, TableController controller) {}

    public boolean doAutoAdd(TableRow row, TableController controller) {
        return row.getColumn(0).getValue() != null && !row.getColumn(0).getValue().equals(0);
    }

    public void rowAdded(int row, TableController controller) {}

	public void getNextPage(TableController controller) {}

	public void getPage(int page) {}

	public void getPreviousPage(TableController controller) {}

	public void setModel(TableController controller, DataModel model) {}

	public void validateRow(int row, TableController controller) {
		// TODO Auto-generated method stub
		
	}

	public void setMultiple(int row, int col, TableController controller) {
		// TODO Auto-generated method stub
		
	}
}
