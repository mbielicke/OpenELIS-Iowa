package org.openelis.modules.SDWISSampleLogin.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;

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
