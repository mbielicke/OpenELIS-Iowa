package org.openelis.modules.dataEntry.client.Provider;


import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface ProviderServletInt extends AppScreenFormServiceInt{
        
    public DataModel getNotesModel(Integer key,DataModel model);
    
    public DataModel getInitialModel(String cat);
    
    public TableModel getAddressModel(Integer providerId, TableModel model);
    
    
}
