package org.openelis.modules.test.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;

public class TestGeneralPurposeRPC extends RPC<Form, Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public String stringValue;
    public DataModel<Integer> model;
    public DataMap map;
    public Integer integerValue;
    public String fieldName;
       
}
