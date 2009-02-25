package org.openelis.modules.provider.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.DataModel;

public class ProviderRPC extends RPC<ProviderForm, Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Initializing DataModels for widgets on the screen
     */
    public DataModel<String> countries;
    public DataModel<String> states;
    public DataModel<Integer> providerTypes;

}
