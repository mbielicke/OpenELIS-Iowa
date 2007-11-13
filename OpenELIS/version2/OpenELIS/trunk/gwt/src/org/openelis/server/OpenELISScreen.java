package org.openelis.server;

import org.openelis.client.main.screen.openelis.OpenELISScreenInt;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.server.constants.Constants;

public class OpenELISScreen extends AppServlet implements OpenELISScreenInt {
   
    private static final long serialVersionUID = 1L;

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
	}

}
