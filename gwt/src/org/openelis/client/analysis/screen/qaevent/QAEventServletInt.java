package org.openelis.client.analysis.screen.qaevent;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;

public interface QAEventServletInt extends AppScreenFormServiceInt {
    public DataModel getInitialModel(String cat);

}
