package org.openelis.modules.standardnotepicker.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;
import org.openelis.gwt.widget.pagedtree.TreeModelItem;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StandardNotePickerService implements AppScreenFormServiceInt {

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
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/standardNotePicker.xsl"));
        DataModel model = new DataModel();
        ModelField data = new ModelField();
        data.setValue(model);
        return new DataObject[] {xml,data};
    }
	
	public TreeModel getTreeModel(){
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

	public String getTreeModelSecondLevel(int type) {
		StandardNoteRemote remote = (StandardNoteRemote)EJBFactory.lookup("openelis/StandardNoteBean/remote");
		List list = remote.getStandardNoteByType(type);
	        
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
			             
			 return XMLUtil.toString(doc);
		}catch(Exception e){
			return "";
		}
	}

    public DataModel getInitialModel(String cat) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

}
