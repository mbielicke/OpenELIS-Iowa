package org.openelis.modules.favorites.server;

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

public class FavoritesService implements AppScreenFormServiceInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private static String[] items = new String[] {"fullLogin",
                                                  "quickEntry",
                                                  "secondEntry",
                                                  "tracking",
                                                  "project",
                                                  "provider,ProviderScreen",
                                                  "organization,OrganizationScreen",
                                                  "worksheetCreation",
                                                  "worksheetCompletion",
                                                  "addOrCancel",
                                                  "reviewAndRelease",
                                                  "toDo",
                                                  "labelFor",
                                                  "storage",
                                                  "QC",
                                                  "order",
                                                  "inventory",
                                                  "instrument",
                                                  "test",
                                                  "method",
                                                  "panel",
                                                  "QAEvent,QAEventScreen",
                                                  "labSection",
                                                  "analyte,AnalyteScreen",
                                                  "dictionary,DictionaryScreen",
                                                  "auxiliaryPrompt",
                                                  "barcodeLabel",
                                                  "standardNote,StandardNoteScreen",
                                                  "trailerForTest",
                                                  "storageUnit,StorageUnitScreen",
                                                  "storageLocation,StorageLocationScreen",
                                                  "scriptlet",
                                                  "systemVariable",
                                                  "finalReport",
                                                  "sampleDataExport",
                                                  "loginLabel"};
                                               
   
    
	public String getXML() throws RPCException {
       
        try {
            Document doc = XMLUtil.createNew("doc");
            Element root = doc.getDocumentElement();
            for(int i = 0; i < items.length; i++){
                String[] menu = items[i].split(",");
                if(menu.length > 1){
                    Element favorite = doc.createElement("favorite");
                    favorite.setAttribute("label",menu[0]);
                    favorite.setAttribute("value",menu[1]);
                    root.appendChild(favorite);
                }
            }
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/favoritesMenu.xsl",doc);
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
