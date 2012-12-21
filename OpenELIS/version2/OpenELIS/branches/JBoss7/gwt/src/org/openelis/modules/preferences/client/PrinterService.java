package org.openelis.modules.preferences.client;

import java.util.ArrayList;

import org.openelis.gwt.common.OptionListItem;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PrinterService implements PrinterServiceInt, PrinterServiceIntAsync {
    
    static PrinterService instance;
    
    PrinterServiceIntAsync service;
    
    public static PrinterService get() {
        if(instance == null)
            instance = new PrinterService();
        
        return instance;
    }
    
    private PrinterService() {
        service = (PrinterServiceIntAsync)GWT.create(PrinterServiceInt.class);
    }

    @Override
    public void getPrinters(String type, AsyncCallback<ArrayList<OptionListItem>> callback) {
        service.getPrinters(type, callback);
    }

    @Override
    public ArrayList<OptionListItem> getPrinters(String type) {
        Callback<ArrayList<OptionListItem>> callback;
        
        callback = new Callback<ArrayList<OptionListItem>>();
        service.getPrinters(type, callback);
        try {
            return callback.getResult();
        }catch(Exception e){
            return null;
        }
    }

}
