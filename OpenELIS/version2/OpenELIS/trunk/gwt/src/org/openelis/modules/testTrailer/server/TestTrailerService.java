package org.openelis.modules.testTrailer.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.server.constants.Constants;

public class TestTrailerService implements AppScreenFormServiceInt {

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/testTrailer.xsl");
	}

	public DataObject[] getXMLData() throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

}
