package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;

import java.util.ArrayList;

public class InventoryReceiptQuery extends Query<TableDataRow<InvReceiptItemInfoForm>> {
    
    public String type;

}
