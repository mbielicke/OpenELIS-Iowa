package org.openelis.modules.supply.client.storageUnit;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StorageUnitServletIntAsync extends AppScreenFormServiceIntAsync {

	public void getInitialModel(String cat, AsyncCallback callback);
}
