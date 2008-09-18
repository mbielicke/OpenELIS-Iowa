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
package org.openelis.modules.test.client;

import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableCellInputWidget;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class TestReflexTable implements TableManager {
    
    private TestScreen testForm = null;
    
    /*private class Delay extends Timer {        
        public Delay(int time) {            
            this.schedule(time);
        }

        public void run() {
            testForm.window.setStatus("","spinnerIcon");
            }
        }*/
    

    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
      if(col == 2){  
       if(testForm.state != FormInt.State.QUERY){
        if(row >= 0){           
         TableRow trow = controller.model.getRow(row);
         DropDownField analyteId = (DropDownField)trow.getColumn(1);
         if (analyteId.getValue() != null && !analyteId.getValue().equals(new Integer(-1))){
            testForm.setTestResultsForAnalyte((Integer)analyteId.getValue());
         }else{
             showError(row,col,controller,"An analyte must be selected before selecting a result value.");
         }
        }        
       }
      } 
       
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        
        return false;
    }

    public boolean doAutoAdd(TableRow autoAddRow, TableController controller) {
        return autoAddRow.getColumn(0).getValue() != null && !autoAddRow.getColumn(0).getValue().equals(0);
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub

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

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void setTestForm(TestScreen testForm) {
        this.testForm = testForm;
    }
    
    public void showError(int row, int col, TableController controller,String error) {
        AbstractField field =  controller.model.getFieldAt(row, col);      
        field.addError(error);
        ((TableCellInputWidget)controller.view.table.getWidget(row,col)).drawErrors();
   }

}
