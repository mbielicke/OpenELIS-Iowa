package org.openelis.modules.dataEntry.client.Provider;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface ProviderServletInt extends AppScreenFormServiceInt{
        
    public DataModel getNotesModel(Integer key);
    
    public DataModel getInitialModel(String cat);
}
