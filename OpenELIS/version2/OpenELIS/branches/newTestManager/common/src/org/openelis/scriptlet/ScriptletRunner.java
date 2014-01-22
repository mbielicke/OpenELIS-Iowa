package org.openelis.scriptlet;

import org.openelis.modules.scriptlet.client.ScriptletService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class to execute and return the results of a Scriptlet that is run on the
 * client or make a call to the servlet layer.
 */
public class ScriptletRunner {
    
    @SuppressWarnings("unchecked")
    public <T extends ScriptletObject> T run(T so) throws Exception {
        
        ScriptletInt scriptlet = null;
        
        /*
         * Check if the Scriptlet is available on the client
         */
        switch(so.getId()) {
            case 1 :
                scriptlet = new TestScriptlet();
                break;
        }
        
        /*
         * Run the client scriptlet
         */
        if(scriptlet != null)
            return (T)scriptlet.run(so);
        
        /*
         * Call the service to run the scriptlet 
         */
        return (T)ScriptletService.get().run(so);
    }
    
    public <T extends ScriptletObject> void run(final T so, final AsyncCallback<T> callback) {
        
        switch(so.getId()) {
            case 1 :
                GWT.runAsync(new RunAsyncCallback() {
                    
                    @Override
                    public void onSuccess() {
                        T sco = (T)new TestScriptlet().run(so);
                        callback.onSuccess(sco);
                    }
                    
                    @Override
                    public void onFailure(Throwable reason) {
                        callback.onFailure(reason);
                    }
                });
        }
    }
}
