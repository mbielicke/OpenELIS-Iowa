package org.openelis.modules.analysis.client.qaevent;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;

public interface QAEventServletInt extends AppScreenFormServiceInt {
    public DataModel getInitialModel(String cat);

}
