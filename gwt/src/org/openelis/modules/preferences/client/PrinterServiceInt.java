package org.openelis.modules.preferences.client;

import java.util.ArrayList;

import org.openelis.gwt.common.OptionListItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("printer")
public interface PrinterServiceInt extends RemoteService {

    ArrayList<OptionListItem> getPrinters(String type);

}