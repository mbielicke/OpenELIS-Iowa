package org.openelis.modules.PTSampleLogin.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;

public class PTSampleLoginForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public TableDataModel<TableDataRow<String>> sampleStatus;
    public TableDataModel<TableDataRow<String>> ptProviderNames;
    public TableDataModel<TableDataRow<String>> ptDepartmentNames;
    
    public AbstractField[] getFields() {
        return new AbstractField[] {};
    }

}
