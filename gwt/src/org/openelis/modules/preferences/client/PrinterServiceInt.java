package org.openelis.modules.preferences.client;

import java.util.ArrayList;

import org.openelis.ui.common.OptionListItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("printer")
public interface PrinterServiceInt extends XsrfProtectedService {

    ArrayList<OptionListItem> getPrinters(String type);

}