package org.openelis.modules.preferences1.client;

import java.util.ArrayList;

import org.openelis.ui.annotation.Service;
import org.openelis.ui.common.OptionListItem;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@Service
@RemoteServiceRelativePath("printer1")
public interface PrinterService1 extends XsrfProtectedService {

    ArrayList<OptionListItem> getPrinters(String type) throws Exception;

}