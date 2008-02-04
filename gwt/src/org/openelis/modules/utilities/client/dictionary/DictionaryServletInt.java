package org.openelis.modules.utilities.client.dictionary;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;


public interface DictionaryServletInt extends AppScreenFormServiceInt {
    public Integer getEntryIdForSystemName(String systemName);
    public Integer getEntryIdForEntry(String entry);   
    public DataModel getInitialModel(String cat);
}
