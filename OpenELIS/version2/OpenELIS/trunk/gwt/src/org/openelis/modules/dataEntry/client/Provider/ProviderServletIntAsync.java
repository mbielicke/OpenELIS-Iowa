package org.openelis.modules.dataEntry.client.Provider;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProviderServletIntAsync extends AppScreenFormServiceIntAsync{
    
    public void getNotesModel(Integer key,DataModel model, AsyncCallback callback);
    public void getInitialModel(String cat, AsyncCallback callback);
    public void getAddressModel(Integer providerId, TableModel model,AsyncCallback callback);
  
}
