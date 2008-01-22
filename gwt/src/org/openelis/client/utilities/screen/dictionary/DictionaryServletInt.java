package org.openelis.client.utilities.screen.dictionary;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;

public interface DictionaryServletInt extends AppScreenFormServiceInt {
    public Integer getEntryIdForSystemName(String systemName);
    public Integer getEntryIdForEntry(String entry);    
}
