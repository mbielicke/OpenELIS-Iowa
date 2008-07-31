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
package org.openelis.modules.provider.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class ProviderAddressesTable implements TableManager {

    //public boolean disableRows = false;
    
    private ProviderScreen providerForm;
    
    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
            return true;
    }

    public boolean canInsert(int row, TableController controller) {
        
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        if(providerForm.state == FormInt.State.ADD || providerForm.state == FormInt.State.UPDATE || providerForm.state == FormInt.State.QUERY){      

            return true;
        } 
        return false;
    }

    public boolean doAutoAdd(TableRow row, TableController controller) {
        return row.getColumn(0).getValue() != null && !row.getColumn(0).getValue().equals(0);
    }

    public void finishedEditing(int row, int col, TableController controller) {

    }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub

    }

    public void getPage(int page) {
        // TODO Auto-generated method stub

    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub

    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub

    }

    public void setProviderForm(ProviderScreen providerForm) {
       this.providerForm = providerForm;
    }


    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

}
