package org.openelis.modules.test.client;

import java.util.HashMap;
import java.util.List;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class TestGeneralPurposeRPC implements RPC {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public Integer key;
    public String stringValue;
    public TableDataModel<TableDataRow<Integer>> model;
    public HashMap<Integer, TableDataModel<TableDataRow<Integer>>> resultDropdownModelMap; 
    public HashMap<Integer, List<Integer>> resGroupAnalyteIdMap;
    public HashMap<Integer,Integer> unitIdNumResMap;
    public Integer integerValue;
    public String fieldName;
       
}
