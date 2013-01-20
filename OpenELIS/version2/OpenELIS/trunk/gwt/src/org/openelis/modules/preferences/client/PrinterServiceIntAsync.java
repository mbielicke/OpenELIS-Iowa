package org.openelis.modules.preferences.client;

import java.util.ArrayList;

import org.openelis.gwt.common.OptionListItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PrinterServiceIntAsync {

    void getPrinters(String type, AsyncCallback<ArrayList<OptionListItem>> callback);

}
