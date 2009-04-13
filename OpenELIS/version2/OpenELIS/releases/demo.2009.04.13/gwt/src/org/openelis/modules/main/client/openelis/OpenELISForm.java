package org.openelis.modules.main.client.openelis;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.data.AbstractField;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenELISForm extends Form<Integer> {
    
    private static final long serialVersionUID = 1L;
    
    public ArrayList<String> modules;
    public HashMap<String,String> appConstants;
    public SecurityUtil security;
    
    public AbstractField[] getFields() {
        return new AbstractField[] {};
    }
}
