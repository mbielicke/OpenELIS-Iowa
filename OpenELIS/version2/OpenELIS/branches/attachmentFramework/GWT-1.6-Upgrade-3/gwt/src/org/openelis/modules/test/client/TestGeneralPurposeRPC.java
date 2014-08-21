package org.openelis.modules.test.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class TestGeneralPurposeRPC implements RPC {// extends RPC<Form, Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public Integer key;
    public String stringValue;
    public TableDataModel<TableDataRow<Integer>> model;
    public DataMap map;
    public Integer integerValue;
    public String fieldName;
       
}
