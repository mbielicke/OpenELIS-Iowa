package org.openelis.modules.utilities.client.standardNote;

import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;

public interface StandardNoteServletInt extends AppScreenFormServiceInt {

	public DataModel getInitialModel(String cat);
}
