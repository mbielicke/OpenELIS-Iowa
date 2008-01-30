package org.openelis.client.utilities.screen.dictionary;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.data.DataModel;


public interface DictionaryServletInt extends AppScreenFormServiceInt {
    public Integer getEntryIdForSystemName(String systemName);
    public Integer getEntryIdForEntry(String entry);   
    public DataModel getInitialModel(String cat);
}
