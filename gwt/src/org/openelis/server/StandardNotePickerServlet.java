package org.openelis.server;

import java.util.ArrayList;
import java.util.List;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.gwt.widget.pagedtree.TreeModelItem;
import org.openelis.modules.utilities.client.standardNotePicker.StandardNotePickerServletInt;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.server.constants.Constants;

public class StandardNotePickerServlet extends AppServlet implements AppScreenFormServiceInt, 
StandardNotePickerServletInt{

	private static final long serialVersionUID = -2489317407834940845L;

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
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNotePicker.xsl");
	}
	
	public TreeModel getTreeModel(Integer key, boolean topLevel){
		//////////////////////////
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
		TreeModel treeModel = new TreeModel();
		
		int id = remote.getCategoryId("standard_note_type");
		
		List entries = new ArrayList();
		if(id > -1)
			entries = remote.getDropdownValues(id);
		
		int i=0;
		while(i < entries.size()){
			Object[] result = (Object[]) entries.get(i);
			//id
			Integer dropdownId = (Integer)result[0];
			//entry
			String dropdownText = (String)result[1];
			
			TreeModelItem treeModelItem = new TreeModelItem();
			treeModelItem.setText(dropdownText);
			treeModelItem.setUserObject(String.valueOf(dropdownId));
			treeModelItem.setHasDummyChild(true);
			treeModel.addItem(treeModelItem);
			
			i++;
		}		
		
       return treeModel;
	}

}
