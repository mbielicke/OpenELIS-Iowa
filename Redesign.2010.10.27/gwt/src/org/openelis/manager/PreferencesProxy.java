package org.openelis.manager;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.services.ScreenService;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is procy to the preferences that will direct calls to the 
 * server from the client.
 * @author tschmidt
 *
 */
public class PreferencesProxy {
	
	/**
	 * Service for calling persistence methods for preferences
	 */
	ScreenService service = new ScreenService("controller?service=org.openelis.server.PreferencesService");
    
	/**
	 * Method passes the call up to the server to persist the preferences
	 * @param prefs
	 * @throws Exception
	 */
	public void flush(Preferences prefs) throws Exception {
    	
    	service.call("flush",prefs,new AsyncCallback<RPC>() {
    		public void onSuccess(RPC result) {
    			//Do Nothing
    		}
    		
    		public void onFailure(Throwable caught) {
    			Window.alert("Persistence of Preference failed : " +caught.getMessage());
    		}
    	});
    	
    }
    
	/**
	 * Proxy call to return the Preferences for a user
	 * @return
	 * @throws Exception
	 */
    public Preferences userRoot() throws Exception{
    	return service.call("userRoot");
    }
    
    /**
     * Proxy call to return the Preference for the system
     * @return
     * @throws Exception
     */
    public Preferences systemRoot() throws Exception {
    	return service.call("systemRoot");
    }
}
