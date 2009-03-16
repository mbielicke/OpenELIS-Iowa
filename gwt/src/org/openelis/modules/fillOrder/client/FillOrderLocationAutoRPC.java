package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataModel;

public class FillOrderLocationAutoRPC extends RPC<Form,Integer>{
    private static final long serialVersionUID = 1L;
    
    public String cat;
    public String match;
    public Integer id;
    
    public DataModel autoMatches;
}
