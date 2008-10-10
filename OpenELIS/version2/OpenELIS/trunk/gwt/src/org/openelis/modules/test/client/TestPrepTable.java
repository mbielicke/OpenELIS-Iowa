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

import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;

public class TestPrepTable implements TableManager {
    
    private TestScreen testForm = null;

    public boolean canAdd(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget,DataSet autoAddRow) {        
        return autoAddRow.get(0).getValue() != null && !autoAddRow.get(0).getValue().equals(-1);
    }

    public boolean canDelete(TableWidget widget,DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget,DataSet set, int row, int col) {
        if(testForm.state == State.UPDATE || testForm.state == State.ADD||
                        testForm.state == State.QUERY)
         return true;
        
        return false;
    }

    public boolean canSelect(TableWidget widget,DataSet set, int row) {
        if(testForm.state == State.UPDATE || testForm.state == State.ADD||
                        testForm.state == State.QUERY)
         return true;
        
        return false;
    }

    public void setTestForm(TestScreen testForm) {
        this.testForm = testForm;
    }
  



}
