package org.openelis.modules.order.client;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataRow;

import java.util.ArrayList;

public class OrderQuery extends Query<TableDataRow<Integer>> {
    
    private static final long serialVersionUID = 1L;
    
    public String type;

}
