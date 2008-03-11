package org.openelis.modules.main.server;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenELISService implements OpenELISServiceInt {
   
    private static final long serialVersionUID = 1L;

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl");
	}
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/OpenELIS.xsl"));
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

    public DataModel getInitialModel(String cat) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getMenuList() {
        try {
        //  System.out.println((SessionManager.getSession().getAttribute("locale") == null ? "en" : (String)SessionManager.getSession().getAttribute("locale")));
            Document doc = XMLUtil.createNew("panel");
            Element root = doc.getDocumentElement();
            root.setAttribute("key", "projectRow");
            root.setAttribute("layout", "table");
            //root.setAttribute("xsi:type", "Table");
            root.setAttribute("style", "FavMenuRowContainer");
            root.setAttribute("hover", "Hover");
            root.setAttribute("onPanelClick", "this");
            
            root.setAttribute("height", "100%");
            root.setAttribute("vertical","true");
            
            //dictionary
            Element mainPanel = doc.createElement("panel");
            Element rowNode = doc.createElement("row");
            Element iconWidgetNode = doc.createElement("widget");
            Element iconNode = doc.createElement("html");
            Element middleWidgetNode = doc.createElement("widget");
            Element textPanelNode = doc.createElement("panel");
            Element titleWidgetNode = doc.createElement("widget");
            Element titleNode = doc.createElement("label");
            Element descWidgetNode = doc.createElement("widget");
            Element descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftDictionaryRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "dictionaryIcon");
            iconNode.setAttribute("style", "DictionaryIcon");
            //iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode("<div/>"));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "dictionaryLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Dictionary");
            
            descNode.setAttribute("key", "dictionaryDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
            //organization
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftOrganizationRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "organizationIcon");
            iconNode.setAttribute("style", "organizationIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "organizationLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Organization");
            
            descNode.setAttribute("key", "organizationDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
//          organize favorites
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftOrganizeFavoritesRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "organizeFavoritesIcon");
            iconNode.setAttribute("style", "organizeFavoritesIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "organizeFavoritesLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Organize Favorites");
            
            descNode.setAttribute("key", "organizeFavoritesDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
//          provider
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftProviderRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "providerIcon");
            iconNode.setAttribute("style", "providerIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "providerLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Provider");
            
            descNode.setAttribute("key", "providerDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
			
//          qa events
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftQaEventsRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "qaEventsIcon");
            iconNode.setAttribute("style", "qaEventsIconIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "qaEventsLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "QA Events");
            
            descNode.setAttribute("key", "qaEventsDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
//          standard note
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftStandardNoteRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "standardNoteIcon");
            iconNode.setAttribute("style", "standardNoteIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "standardNoteLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Standard Note");
            
            descNode.setAttribute("key", "standardNoteDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
//          storage loc
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftStorageLocationRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "storageIcon");
            iconNode.setAttribute("style", "storageIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "storageLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Storage Location");
            
            descNode.setAttribute("key", "storageDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
            
//          storage unit
            mainPanel = doc.createElement("panel");
            rowNode = doc.createElement("row");
            iconWidgetNode = doc.createElement("widget");
            iconNode = doc.createElement("html");
            middleWidgetNode = doc.createElement("widget");
            textPanelNode = doc.createElement("panel");
            titleWidgetNode = doc.createElement("widget");
            titleNode = doc.createElement("label");
            descWidgetNode = doc.createElement("widget");
            descNode = doc.createElement("label");
            
            mainPanel.setAttribute("key", "favLeftStorageUnitRow");
            mainPanel.setAttribute("layout", "table");
            mainPanel.setAttribute("style", "FavMenuRowContainer");
            mainPanel.setAttribute("hover", "Hover");
            mainPanel.setAttribute("onPanelClick", "this");
            
            iconWidgetNode.setAttribute("style", "topMenuIcon");
            
            iconNode.setAttribute("key", "storageUnitIcon");
            iconNode.setAttribute("style", "storageUnitIcon");
            iconNode.setAttribute("xml:Space", "preserve");
            iconNode.appendChild(doc.createTextNode(" "));
            
            middleWidgetNode.setAttribute("style", "topMenuItemMiddle");
            
            textPanelNode.setAttribute("layout", "vertical");
              
            titleNode.setAttribute("key", "storageUnitLabel");
            titleNode.setAttribute("style", "topMenuItemTitle");
            titleNode.setAttribute("text", "Storage Unit");
            
            descNode.setAttribute("key", "storageUnitDescription");
            descNode.setAttribute("wordwrap", "true");
            descNode.setAttribute("style", "topMenuitemDesc");
            descNode.setAttribute("text", "");
            
            mainPanel.appendChild(rowNode);
            rowNode.appendChild(iconWidgetNode);
            iconWidgetNode.appendChild(iconNode);
            rowNode.appendChild(middleWidgetNode);
            middleWidgetNode.appendChild(textPanelNode);
            textPanelNode.appendChild(titleWidgetNode);
            titleWidgetNode.appendChild(titleNode);
            textPanelNode.appendChild(descWidgetNode);
            descWidgetNode.appendChild(descNode);
            root.appendChild(mainPanel);
                    
            return XMLUtil.toString(doc);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public DataObject getObject(String method, DataObject[] args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public void logout() {
        // TODO Auto-generated method stub
        
    }

}
