package org.openelis.local;

import java.util.ArrayList;

import javax.ejb.Local;

import org.openelis.domain.OptionListItem;
import org.openelis.utils.Printer;

@Local
public interface PrinterCacheLocal {
	
	public ArrayList<OptionListItem> getList();
	
	public Printer getPrinterByName(String name);
	
	public ArrayList<OptionListItem> getListByType(String type);

}
