package org.openelis.client.supply.screen.storageUnit;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StorageUnitServletIntAsync extends AppScreenFormServiceIntAsync {

	public void getInitialModel(String cat, AsyncCallback callback);
}
