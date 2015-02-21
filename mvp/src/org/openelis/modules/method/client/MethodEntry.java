package org.openelis.modules.method.client;

import org.openelis.di.AppDI;

import com.google.gwt.core.client.EntryPoint;

public class MethodEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        AppDI.INSTANCE.methodPoint().onModuleLoad();
    }

}
