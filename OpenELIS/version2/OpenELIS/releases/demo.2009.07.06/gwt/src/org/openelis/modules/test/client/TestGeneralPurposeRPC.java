package org.openelis.modules.test.client;

import java.util.ArrayList;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class TestGeneralPurposeRPC implements RPC {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public Integer key,integerValue;
    public String stringValue,fieldName;
    public TableDataModel<TableDataRow<Integer>> defaultResultModel, testAnalyteModel, resultGroupModel;
    public ArrayList<TableDataModel<TableDataRow<Integer>>> resultTableModelCollection,
                                                            resultDropdownModelCollection;
    public boolean duplicate;
       
}
