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
package org.openelis.modules.dictionary.client;

import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;


public class DictionaryEntriesTable implements TableManager {
    
    private DictionaryScreen dictionaryForm = null;
    private TableRow relEntryRow = null;           

    public void setDictionaryForm(DictionaryScreen dictionaryForm) {
        this.dictionaryForm = dictionaryForm;
    }

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
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {   
       if(dictionaryForm.state == FormInt.State.ADD 
                       || dictionaryForm.state == FormInt.State.UPDATE 
                       || dictionaryForm.state == FormInt.State.QUERY) 
        return true;
       
       return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {              
        if(col == 0 || col == 1 || col == 3)
            return true;
        else
            return false;
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
    
    public void setRelatedEntryId(Integer entryId){
        NumberField relEntryId = new NumberField(entryId); 
        relEntryRow.addHidden("relEntryId", relEntryId);        
    }    
            

    public void showError(int row, int col, TableController controller,String error) {
         AbstractField field =  controller.model.getFieldAt(row, col);      
         field.addError(error);
         ((TableCellInputWidget)controller.view.table.getWidget(row,col)).drawErrors();
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
    
    
    
}
