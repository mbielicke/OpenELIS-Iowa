package org.openelis.modules.order.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;

import java.util.ArrayList;

public class OrderQuery extends Query<TableDataRow<Integer>> {
    
    private static final long serialVersionUID = 1L;
    
    public String type;

}
