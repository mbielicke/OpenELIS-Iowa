package org.openelis.modules.dictionaryentrypicker.client;

import org.openelis.gwt.screen.AppModule;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class DictionaryEntryPickerEntry implements AppModule {

    public String getModuleName() {        
        return "DictionaryEntryPicker";
    }

    public void onModuleLoad() {
        OpenELIS.modules.add(getModuleName());

    }

}
