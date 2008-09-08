package org.openelis.modules.shipping.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;

public class ShippingService implements AppScreenFormServiceInt{

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        // TODO Auto-generated method stub
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
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/shipping.xsl");
    }

    public HashMap<String, DataObject> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/shipping.xsl"));
       
        DataModel shipFromDropdownField = (DataModel)CachingManager.getElement("InitialData", "shipFromDropdown");
        DataModel statusDropdownField = (DataModel)CachingManager.getElement("InitialData", "orderStatusDropdown");
        DataModel shippingMethodDropdownField = (DataModel)CachingManager.getElement("InitialData", "shippingMethodDropdown");
       
        //status dropdown
        if(statusDropdownField == null){
            statusDropdownField = getInitialModel("status");
            CachingManager.putElement("InitialData", "orderStatusDropdown", statusDropdownField);
        }
        //ship from dropdown
        if(shipFromDropdownField == null){
            shipFromDropdownField = getInitialModel("shipFrom");
            CachingManager.putElement("InitialData", "shipFromDropdown", shipFromDropdownField);
        }
        //ship method dropdown
        if(shippingMethodDropdownField == null){
            shippingMethodDropdownField = getInitialModel("shippingMethod");
            CachingManager.putElement("InitialData", "shippingMethodDropdown", shippingMethodDropdownField);
        }
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("status", statusDropdownField);
        map.put("shipFrom", shipFromDropdownField);
        map.put("shippingMethod", shippingMethodDropdownField);
        
        return map;
    }

    public HashMap<String, DataObject> getXMLData(HashMap<String, DataObject> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public DataModel getInitialModel(String cat){
        Integer id = null;
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        
        if(cat.equals("status"))
            id = remote.getCategoryId("order_status");
        else if(cat.equals("shipFrom"))
            id = remote.getCategoryId("shipFrom");
        else if(cat.equals("shippingMethod"))
            id = remote.getCategoryId("shippingMethod");
        
        List entries = new ArrayList();
        if(id != null)
            entries = remote.getDropdownValues(id);
        
        //we need to build the model to return
        DataModel returnModel = new DataModel();

        if(entries.size() > 0){ 
            //create a blank entry to begin the list
            DataSet blankset = new DataSet();
            
            StringObject blankStringId = new StringObject();
            NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
            
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            blankNumberId.setValue(new Integer(0));
            blankset.setKey(blankNumberId);
            
            returnModel.add(blankset);
        }
        
        int i=0;
        while(i < entries.size()){
            DataSet set = new DataSet();
            IdNameDO resultDO = (IdNameDO) entries.get(i);
            //id
            Integer dropdownId = resultDO.getId();
            //entry
            String dropdownText = resultDO.getName();
            
            StringObject textObject = new StringObject();
            NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
        
            textObject.setValue(dropdownText);
            set.addObject(textObject);
            
            numberId.setValue(dropdownId);
            set.setKey(numberId);
            
            returnModel.add(set);
            
            i++;
        }       
        
        return returnModel;
    }
    
    public ModelObject getAddAutoFillValues() throws Exception {
        ModelObject modelObj = new ModelObject();
        DataModel model = new DataModel();
        DataSet set = new DataSet();
        
        ShippingRemote remote = (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote");
        ShippingAddAutoFillDO autoDO;
        
        autoDO = remote.getAddAutoFillValues();
                
        DropDownField status = new DropDownField(autoDO.getStatus());
        DateField processedDate = new DateField(Datetime.YEAR, Datetime.DAY, DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getProcessedDate().getDate()));
        StringField processedBy = new StringField(autoDO.getProcessedBy());
        DateField shippedDate = new DateField(Datetime.YEAR, Datetime.DAY, DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getShippedDate().getDate()));
        
        set.addObject(status);
        set.addObject(processedDate);
        set.addObject(processedBy);
        set.addObject(shippedDate);
        
        model.add(set);
        
        modelObj.setValue(model);
        
        return modelObj;
    }
}