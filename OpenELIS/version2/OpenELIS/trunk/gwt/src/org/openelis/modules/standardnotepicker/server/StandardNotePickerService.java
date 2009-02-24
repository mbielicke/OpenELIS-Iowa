/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.standardnotepicker.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;

public class StandardNotePickerService implements AppScreenFormServiceInt<StandardNotePickerRPC,Integer> {

	private static final long serialVersionUID = 1L;

    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
    	return null;
    }

    public StandardNotePickerRPC commitAdd(StandardNotePickerRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardNotePickerRPC commitUpdate(StandardNotePickerRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardNotePickerRPC commitDelete(StandardNotePickerRPC rpc) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public StandardNotePickerRPC abort(StandardNotePickerRPC rpc) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public StandardNotePickerRPC fetch(StandardNotePickerRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardNotePickerRPC fetchForUpdate(StandardNotePickerRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXML() throws RPCException {
		return null;
	}
    
    public HashMap<String, FieldType> getXMLData() throws RPCException {
        return null;
    }
	
	public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
    	return null;
    }
    
    public StandardNotePickerRPC getScreen(StandardNotePickerRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNotePicker.xsl");

        return rpc;
    }

    public StandardNotePickerRPC getTreeModel(StandardNotePickerRPC rpc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		TreeDataModel returnTree = new TreeDataModel();
		//TreeModel treeModel = new TreeModel();
		
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		nameField.setValue(rpc.queryString);
		descField.setValue(rpc.queryString);
		
		HashMap fields = new HashMap();
		fields.put(StandardNoteMeta.getName(), nameField);
		fields.put(StandardNoteMeta.getDescription(), descField);
		
		List standardNoteCategories = new ArrayList();
		try{
			standardNoteCategories = remote.queryForType(fields);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		Iterator catItr = standardNoteCategories.iterator();
		
		int i=0;
		while(catItr.hasNext()){
			Object[] result = (Object[])catItr.next();
			//standard note category id
			Integer idParam = (Integer)result[0];
			//standard note category name
			String nameParam = (String)result[1];
			
			TreeDataItem treeModelItem = new TreeDataItem();
			treeModelItem.leafType = "top";
			treeModelItem.lazy = true;
			treeModelItem.setKey(idParam);
			treeModelItem.add(new StringObject(nameParam));
			
			returnTree.add(treeModelItem);
			
			i++;
		}	
		rpc.treeModel = returnTree;
	    return rpc;
	}

    public StandardNotePickerRPC getTreeModelSecondLevel(StandardNotePickerRPC rpc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		QueryNumberField typeField = new QueryNumberField();
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		typeField.setType("integer");
		typeField.setValue(String.valueOf(rpc.id));
		nameField.setValue(rpc.queryString);
		descField.setValue(rpc.queryString);
		HashMap<String,AbstractField> fields = new HashMap<String, AbstractField>();
		fields.put(StandardNoteMeta.getTypeId(), typeField);
		fields.put(StandardNoteMeta.getName(), nameField);
		fields.put(StandardNoteMeta.getDescription(), descField);
		List list = new ArrayList();
		try{
			list = remote.getStandardNoteByType(fields);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
	       StringObject returnString = new StringObject(); 
	     try {
	    	 Iterator itr = list.iterator();
	            TreeDataModel treeModel = new TreeDataModel();
	            while(itr.hasNext()){
	                StandardNoteDO standardNoteDO = (StandardNoteDO) itr.next();
	                
	                TreeDataItem treeModelItem = new TreeDataItem();
	                treeModelItem.leafType = "leaf";
	                treeModelItem.setKey(rpc.id);
	                treeModelItem.setData(new StringObject(standardNoteDO.getText()));
	                treeModelItem.add(new StringObject(standardNoteDO.getName()+" : "+standardNoteDO.getDescription()));
	                
	                
	                treeModel.add(treeModelItem);
	            }   
	            rpc.treeModel = treeModel;
	            return rpc;
		}catch(Exception e){
			return rpc;
		}
	}
}
