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

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.PagedTreeField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.gwt.widget.pagedtree.TreeModelItem;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.metamap.StandardNoteMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StandardNotePickerService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {

	private static final long serialVersionUID = -2489317407834940845L;

    private static final StandardNoteMetaMap StandardNoteMeta = new StandardNoteMetaMap();
    
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
    	return null;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
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
    
    public HashMap getXMLData() throws RPCException {
        return null;
    }
	
	public HashMap getXMLData(HashMap args) throws RPCException {
    	return null;
    }

    public PagedTreeField getTreeModel(StringObject name, StringObject desc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		PagedTreeField returnTree = new PagedTreeField();
		TreeModel treeModel = new TreeModel();
		
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		nameField.setValue(name.getValue());
		descField.setValue(desc.getValue());
		
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
			
			TreeModelItem treeModelItem = new TreeModelItem();
			treeModelItem.setText(nameParam);
			treeModelItem.setUserObject(String.valueOf(idParam));
			treeModelItem.setHasDummyChild(true);
			treeModel.addItem(treeModelItem);
			
			i++;
		}	
		returnTree.setValue(treeModel);
	    return returnTree;
	}

	public StringObject getTreeModelSecondLevel(NumberObject type, StringObject name, StringObject desc) throws RPCException{
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		
		QueryNumberField typeField = new QueryNumberField();
		QueryStringField nameField = new QueryStringField();
		QueryStringField descField = new QueryStringField();
		typeField.setType("integer");
		typeField.setValue(String.valueOf(((Integer)type.getValue()).intValue()));
		nameField.setValue(name.getValue());
		descField.setValue(desc.getValue());
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
	            
	         Document doc = XMLUtil.createNew("tree");
	         while(itr.hasNext()){
				 StandardNoteDO standardNoteDO = (StandardNoteDO) itr.next();
				 
				 Element root = doc.getDocumentElement();
				 root.setAttribute("key", "menuList");
				 root.setAttribute("height", "100%");
				 root.setAttribute("vertical","true");           
				      
				  Element elem = doc.createElement("label");
				  elem.setAttribute("text", standardNoteDO.getName()+" : "+standardNoteDO.getDescription()); 
				  elem.setAttribute("value", standardNoteDO.getText()); 
				  
				  root.appendChild(elem); 
	         }
			 
	         returnString.setValue(XMLUtil.toString(doc));
			 return returnString;
		}catch(Exception e){
			returnString.setValue("");
			return returnString;
		}
	}
}
