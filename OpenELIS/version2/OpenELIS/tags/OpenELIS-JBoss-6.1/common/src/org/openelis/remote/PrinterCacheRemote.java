package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.OptionListItem;

@Remote
public interface PrinterCacheRemote {
	
	public ArrayList<OptionListItem> getListByType(String type);

}
