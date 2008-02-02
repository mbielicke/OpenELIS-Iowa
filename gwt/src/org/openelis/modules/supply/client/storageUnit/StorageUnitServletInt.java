package org.openelis.modules.supply.client.storageUnit;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;

public interface StorageUnitServletInt extends AppScreenFormServiceInt {

	public DataModel getInitialModel(String cat);
}
