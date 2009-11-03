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
package org.openelis.utilgwt;

import org.openelis.gwt.common.data.TableDataRow;

public class ProjectEntryManager extends MultipleEntryManager<Integer> {

    public TableDataRow<Integer> getFirstPermanentProject(){
        int i;
        TableDataRow<Integer> row;
        
        for(i=0;i<size(); i++){
            row = getRow(i);
            if("Y".equals(row.cells[2].getValue()))
                return row;
        }
        return null;
    }
    
    public void addFirstPermanentProject(TableDataRow<Integer> newRow) {
        TableDataRow<Integer> oldRow = getFirstPermanentProject();

        if (oldRow == null && newRow != null) { // insert
            newRow.cells[2].setValue("Y");
            addRow(newRow);

        } else if (oldRow != null && newRow == null) { // delete
            delete(0);
        } else if (oldRow != null && newRow != null) { // update
            newRow.cells[2].setValue("Y");
            setRow(0, newRow);
        }
    }
    
    public void addRow(int i, TableDataRow<Integer> row) {
        if("Y".equals(row.cells[2].getValue()))
            super.addRow(0,row);
        else
            super.addRow(i, row);
    }
}
