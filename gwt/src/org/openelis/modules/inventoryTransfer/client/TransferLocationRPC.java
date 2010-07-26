package org.openelis.modules.inventoryTransfer.client;

import org.openelis.gwt.common.RPC;

public class TransferLocationRPC implements RPC {
    private static final long serialVersionUID = 1L;
    
    public Integer oldLocId;
    public Integer currentLocId;
    
    public Integer currentQtyOnHand;
}
