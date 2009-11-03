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

import org.openelis.cache.CacheEntryNotFoundException;
import org.openelis.cache.DictionaryCache;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.TableDataRow;

public class OrganizationEntryManager extends MultipleEntryManager<Integer> {
    protected Integer primaryReportToId;
    protected Integer primaryBillToId;

    public OrganizationEntryManager() {
        primaryReportToId = DictionaryCache.getIdFromSystemName("org_report_to");
        primaryBillToId = DictionaryCache.getIdFromSystemName("org_bill_to");

        if (primaryBillToId == null || primaryReportToId == null)
            throw new CacheEntryNotFoundException("OrganizationEntryManager: org_report_to and/or org_bill_to report dictionary entry not found");
    }

    public TableDataRow<Integer> getReportTo() {
        int rowIndex;

        rowIndex = rowIndexFor(primaryReportToId);

        if (rowIndex != -1)
            return list.get(rowIndex);

        return null;
    }

    public TableDataRow<Integer> getBillTo() {
        int rowIndex;

        rowIndex = rowIndexFor(primaryBillToId);

        if (rowIndex != -1)
            return list.get(rowIndex);

        return null;
    }

    public void setReportTo(TableDataRow newRow) {
        setRowFor(primaryReportToId, newRow);
    }

    public void setBillTo(TableDataRow newRow) {
        setRowFor(primaryBillToId, newRow);
    }

    protected void setRowFor(Integer typeId, TableDataRow newRow) {
        int rowIndex;
        rowIndex = rowIndexFor(typeId);

        if (rowIndex == -1 && newRow != null) { // insert
            newRow.cells[0].setValue(new TableDataRow<Integer>(typeId));
            addRow(newRow);

        } else if (rowIndex != -1 && newRow == null) { // delete
            delete(rowIndex);
        } else if (rowIndex != -1 && newRow != null) { // update
            newRow.cells[0].setValue(new TableDataRow<Integer>(typeId));
            setRow(rowIndex, newRow);
        }
    }

    protected int rowIndexFor(Integer typeId) {
        int i;
        TableDataRow<Integer> row;

        for (i = 0; i < size(); i++) {
            row = getRow(i);
            if (typeId.equals(((DropDownField<Integer>)row.cells[0]).getSelectedKey()))
                return i;
        }

        return -1;
    }
}
