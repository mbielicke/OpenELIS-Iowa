package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataRow;

import java.util.ArrayList;

public class InventoryReceiptQuery extends Query<TableDataRow<InvReceiptItemInfoForm>> {
    
    public String type;

}
