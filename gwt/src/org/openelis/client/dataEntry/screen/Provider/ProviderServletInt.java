package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;

public interface ProviderServletInt extends AppScreenFormServiceInt{
        
    public DataModel getNotesModel(Integer key);
    
    public DataModel getInitialModel(String cat);
}
