package org.openelis.modules.analysis.client.qaevent;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface QAEventServletInt extends AppScreenFormServiceInt {
    public DataModel getInitialModel(String cat);

}
