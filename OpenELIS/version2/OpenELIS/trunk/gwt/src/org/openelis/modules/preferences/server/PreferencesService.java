package org.openelis.modules.preferences.server;

import java.util.ArrayList;

import org.openelis.domain.OptionListItem;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.PrinterCacheRemote;

public class PreferencesService {
	
	public ArrayList<OptionListItem> getPrinters(String type) {
		return printerCacheRemote().getListByType(type);
	}
	
    public static PrinterCacheRemote printerCacheRemote() {
        return (PrinterCacheRemote)EJBFactory.lookup("openelis/PrinterCacheBean/remote");
    }

}
