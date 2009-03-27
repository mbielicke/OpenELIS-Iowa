package org.openelis.modules.PTSampleLogin.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;

public class PTSampleLoginForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public TableDataModel<TableDataRow<String>> sampleStatus;
    public TableDataModel<TableDataRow<String>> ptProviderNames;
    public TableDataModel<TableDataRow<String>> ptDepartmentNames;
    
    public AbstractField[] getFields() {
        return new AbstractField[] {};
    }

}
