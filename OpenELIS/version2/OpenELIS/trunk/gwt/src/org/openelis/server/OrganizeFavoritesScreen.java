package org.openelis.server;

import org.openelis.client.dataEntry.screen.organizeFavorites.OrganizeFavoritesScreenInt;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.server.constants.Constants;

public class OrganizeFavoritesScreen extends AppServlet implements OrganizeFavoritesScreenInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985921800767730818L;

	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organizeFavorites.xsl");
	}

	public FormRPC abort(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitAdd(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractField commitQuery(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitUpdate(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC delete(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetchForUpdate(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
