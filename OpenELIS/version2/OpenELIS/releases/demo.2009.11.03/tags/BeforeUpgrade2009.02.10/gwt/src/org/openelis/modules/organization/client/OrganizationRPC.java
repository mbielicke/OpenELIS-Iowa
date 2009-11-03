package org.openelis.modules.organization.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;

/**
 * This class extends RPC with parameter OrganizationForm, and OrganizationKey to
 * retrieve and send data in the OrganizationScreen.
 * @author tschmidt
 *
 */
public class OrganizationRPC extends RPC<OrganizationForm,Data>{

    private static final long serialVersionUID = 1L;
    
    /**
     * Initializing DataModels for widgets on the screen
     */
    public DataModel<DataSet> countries;
    public DataModel<DataSet> states;
    public DataModel<DataSet> contactTypes;
    

}
