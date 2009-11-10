package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;

public class FillOrderLocationAutoRPC implements RPC{
    private static final long serialVersionUID = 1L;
    
    public String cat;
    public String match;
    public Integer id;
    
    public TableDataModel<TableDataRow<Integer>> autoMatches;
}
