package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataModel;

public class FillOrderRPC extends RPC<FillOrderForm,Integer> {

    private static final long serialVersionUID = 1L;

    public DataModel<Integer> costCenters;
    public DataModel<Integer> shipFroms;
    public DataModel<Integer> statuses;
    public Integer orderPendingValue;
    
}
