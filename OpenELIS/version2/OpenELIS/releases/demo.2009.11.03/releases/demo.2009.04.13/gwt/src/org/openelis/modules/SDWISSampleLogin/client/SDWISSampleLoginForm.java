package org.openelis.modules.SDWISSampleLogin.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class SDWISSampleLoginForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public TableDataModel<TableDataRow<String>> sampleStatus;
    public TableDataModel<TableDataRow<String>> sampleTypes;
    public TableDataModel<TableDataRow<String>> sampleCats;
    public TableDataModel<TableDataRow<String>> leadSampleTypes;
   
    public AbstractField[] getFields() {
        return new AbstractField[] {};
    }

}
