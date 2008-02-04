package org.openelis.modules.supply.client.storageUnit;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface StorageUnitServletInt extends AppScreenFormServiceInt {

	public DataModel getInitialModel(String cat);
}
