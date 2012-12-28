/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends qc DO and carries a commonly used field inventory_item
 * name. The additional field is for read/display only and does not get
 * committed to the database. Note: isChanged will reflect any changes to
 * read/display fields.
 */

public class QcViewDO extends QcDO {

    private static final long serialVersionUID = 1L;

    protected String          inventoryItemName;

    public QcViewDO() {
    }

    public QcViewDO(Integer id, String name, Integer typeId, Integer inventoryItemId,
                    String source, String isActive, String inventoryItemName) {
        super(id, name, typeId, inventoryItemId, source, isActive);
        setInventoryItemName(inventoryItemName);
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = DataBaseUtil.trim(inventoryItemName);
    }
}