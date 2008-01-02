package org.openelis.client.dataEntry.screen.Provider;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;

public interface ProviderServletInt extends AppScreenFormServiceInt{
    
    public TreeModel getNoteTreeModel(Integer key, boolean topLevel);
    
    public String getNoteTreeSecondLevelXml(String key, boolean topLevel);
}
