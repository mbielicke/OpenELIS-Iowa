package org.openelis.modules.favorites.server;

import java.util.HashMap;
import java.util.Iterator;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EditFavoritesService implements AppScreenFormServiceInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private static HashMap items = new HashMap();
    
     {items.put("ProviderScreen", "provider");
      items.put("OrganizationScreen","organization");
      items.put("QAEventScreen","QAEvent");
      items.put("AnalyteScreen", "analyte");
      items.put("DictionaryScreen", "dictionary");
      items.put("StandardNoteScreen", "standardNote");
      items.put("StorageUnitScreen", "storageUnit");
      items.put("StorageLocationScreen", "storageLocation");
     }
                                               
    
	public String getXML() throws RPCException {
       
        try {
            Document doc = XMLUtil.createNew("doc");
            Element root = doc.getDocumentElement();
            Iterator itemsIt = items.keySet().iterator();
            while(itemsIt.hasNext()){
            	String key = (String)itemsIt.next();
                String label = (String)items.get(key);
                Element favorite = doc.createElement("favorite");
                favorite.setAttribute("label",label);
                favorite.setAttribute("value",key);
                    root.appendChild(favorite);
                }
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/favoritesEdit.xsl",doc);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
	}
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organizeFavorites.xsl"));
        DataModel model = new DataModel();
        ModelField data = new ModelField();
        data.setValue(model);
        return new DataObject[] {xml,data};
    }

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
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

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
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

    public DataModel getInitialModel(String cat) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
}
