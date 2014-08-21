package org.openelis.modules.provider.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.ProviderRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.remote.SystemUserRemote;


public class ProviderService implements AppScreenFormServiceInt{

    /**
     * 
     */
    private static final long serialVersionUID = 0L;
    private static final int leftTableRowsPerPage = 19;
           
    private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.server.constants.OpenELISConstants",
                                                                new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
                                                                        ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
    
    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl");        
    }
    
    public DataObject[] getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl"));
        
        DataModel providerTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "providerTypeDropDown");
        DataModel stateDropDownField = (DataModel)CachingManager.getElement("InitialData", "stateDropDown");
        DataModel countryDropDownField = (DataModel)CachingManager.getElement("InitialData", "countryDropDown");
        
        if(providerTypeDropDownField ==null)
            providerTypeDropDownField = getInitialModel("providerType");
           
         if(stateDropDownField == null)
             stateDropDownField = getInitialModel("state");
         
         if(countryDropDownField == null)
             countryDropDownField = getInitialModel("country");
           
           return new DataObject[] {xml,providerTypeDropDownField,stateDropDownField,countryDropDownField};
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        Integer providerId = (Integer)key.getObject(0).getValue();
        
        
        ProviderDO provDO = new ProviderDO();
         try{
          provDO =  (ProviderDO)remote.getProviderAndUnlock(providerId);
          }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());                
        
        //List addressList = remote.getProviderAddresses(providerId);
        //rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));              

        //DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
        //rpcReturn.setFieldValue("notesModel", notesModel);                                    
        
        return rpcReturn;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
               
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO = new ProviderDO();
        NoteDO providerNote = new NoteDO();
        //provider info        
        
        providerDO.setFirstName((String)rpcSend.getFieldValue("firstName"));
        providerDO.setLastName((String)rpcSend.getFieldValue("lastName"));
        providerDO.setMiddleName((String)rpcSend.getFieldValue("middleName"));
        providerDO.setNpi((String)rpcSend.getFieldValue("npi"));
        providerDO.setTypeId((Integer)rpcSend.getFieldValue("providerTypeId"));
        
        
        
        List<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
        
        TableModel addressTable = (TableModel)rpcSend.getField("providerAddressTable").getValue();
                
        for(int iter = 0; iter < addressTable.numRows(); iter++){
            
            ProviderAddressDO provAddDO = new ProviderAddressDO();
            TableRow row = addressTable.getRow(iter);
            
            //NumberField provAddId = (NumberField)row.getHidden("provAddId");
            //NumberField addId = (NumberField)row.getHidden("addId");
            
           // if(provAddId != null){
           //  provAddDO.setId((Integer)(provAddId).getValue());
           // }             
            provAddDO.setLocation((String)((StringField)row.getColumn(0)).getValue());
            provAddDO.setExternalId((String)((StringField)row.getColumn(1)).getValue());
            //provAddDO.setProvider((Integer)providerId.getValue());
           
            //System.out.println("addId "+addId); 
            
           // if(addId != null){              
           //  provAddDO.getAddressDO().setId((Integer)addId.getValue());             
           StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if(deleteFlag == null){
                provAddDO.setDelete(false);
            }else{
                provAddDO.setDelete("Y".equals(deleteFlag.getValue()));
            }
            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            
            
            provAddDO.getAddressDO().setMultipleUnit((String)((StringField)row.getColumn(2)).getValue());
            provAddDO.getAddressDO().setStreetAddress((String)((StringField)row.getColumn(3)).getValue());
            provAddDO.getAddressDO().setCity((String)((StringField)row.getColumn(4)).getValue());
            provAddDO.getAddressDO().setState((String)((StringField)row.getColumn(5)).getValue());
            provAddDO.getAddressDO().setCountry((String)((StringField)row.getColumn(6)).getValue());
            provAddDO.getAddressDO().setZipCode((String)((StringField)row.getColumn(7)).getValue());
            provAddDO.getAddressDO().setWorkPhone((String)((StringField)row.getColumn(8)).getValue());
            provAddDO.getAddressDO().setHomePhone((String)((StringField)row.getColumn(9)).getValue());
            provAddDO.getAddressDO().setCellPhone((String)((StringField)row.getColumn(10)).getValue());
            provAddDO.getAddressDO().setFaxPhone((String)((StringField)row.getColumn(11)).getValue());
            provAddDO.getAddressDO().setEmail((String)((StringField)row.getColumn(12)).getValue());
            
            provAddDOList.add(provAddDO);            
        }
        
        providerNote.setSubject((String)rpcSend.getFieldValue("usersSubject"));
        providerNote.setText((String)rpcSend.getFieldValue("usersNote"));
        providerNote.setIsExternal("Y");
                
        Integer providerId =null;
        try{
            providerId = (Integer)remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
         
        ProviderDO provDO = remote.getProvider(providerId);
        rpcReturn.setFieldValue("providerId", provDO.getId());        
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());
                
        
        //List addressList = remote.getProviderAddresses(providerId);
        //rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));                                                  
        
       // DataModel notesModel = getNotesModel(provDO.getId());
        //rpcReturn.setFieldValue("notesModel", notesModel);
        
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {        
        //ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");        
        if(rpcSend == null){
           //need to get the query rpc out of the cache        
                       
            FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Provider");

           if(rpc == null)
               throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

            List providers = null;
                
            try{
                
                ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote"); 
                providers = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                System.out.println("providers.size() "+providers.size());
            }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}
            }
        
        int i=0;
        model.clear();
       // List providers = new ArrayList();
        
        //while(i < organizations.size() && i < leftTableRowsPerPage) {
        while(i < providers.size() && i < leftTableRowsPerPage) {
            //Object[] result = (Object[])organizations.get(i);
            Object[] result = (Object[])providers.get(i);
            //org id
            Integer idResult = (Integer)result[0];
            //org name
            String lnameResult = (String)result[1];
            
            String fnameResult = (String)result[2];

            DataSet row = new DataSet();
            
            NumberObject id = new NumberObject();            
            StringObject lname = new StringObject();
            StringObject fname = new StringObject();
            id.setType("integer");
             lname.setValue(lnameResult);
             fname.setValue(fnameResult);
            
            id.setValue(idResult);
            
            row.addObject(id);          
            row.addObject(lname);
            row.addObject(fname);
            
            model.add(row);
            i++;
         }
        
                
        return model;   
        } else{
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();

            //contacts table
             TableModel provAddTable = null;
            if(rpcSend.getField("providerAddressTable") != null)
                provAddTable = (TableModel)rpcSend.getField("providerAddressTable").getValue();
                
            System.out.println("providerAddressTable != null");
            
            if(provAddTable != null){    
                System.out.println("provAddTable != null");
                fields.put("location",(QueryStringField)provAddTable.getRow(0).getColumn(0));                                                    
                fields.put("externalId",(QueryStringField)provAddTable.getRow(0).getColumn(1));
                fields.put("multiUnit",(QueryStringField)provAddTable.getRow(0).getColumn(2));
                fields.put("streetAddress",(QueryStringField)provAddTable.getRow(0).getColumn(3));                
                fields.put("city",(QueryStringField)provAddTable.getRow(0).getColumn(4));                                                                                       
                fields.put("state",(CollectionField)provAddTable.getRow(0).getColumn(5));
                fields.put("country",(CollectionField)provAddTable.getRow(0).getColumn(6));
                fields.put("zipCode",(QueryStringField)provAddTable.getRow(0).getColumn(7));
                fields.put("workPhone",(QueryStringField)provAddTable.getRow(0).getColumn(8));
                fields.put("homePhone",(QueryStringField)provAddTable.getRow(0).getColumn(9));
                fields.put("cellPhone",(QueryStringField)provAddTable.getRow(0).getColumn(10));
                fields.put("faxPhone",(QueryStringField)provAddTable.getRow(0).getColumn(11));
                fields.put("email",(QueryStringField)provAddTable.getRow(0).getColumn(12));               
            }

            List providerNames = new ArrayList();
                try{
                    providerNames = remote.query(fields,0,leftTableRowsPerPage);

            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            Iterator itraaa = providerNames.iterator();
            model=  new DataModel();
            while(itraaa.hasNext()){
                Object[] result = (Object[])(Object[])itraaa.next();
                //org id
                Integer idResult = (Integer)result[0];
                //org name
                String lnameResult = (String)result[1];
                
                String fnameResult = (String)result[2];

                DataSet row = new DataSet();
                
                NumberObject id = new NumberObject();                
                StringObject lname = new StringObject();
                StringObject fname = new StringObject();
                id.setType("integer");
                 lname.setValue(lnameResult);
                 fname.setValue(fnameResult);
                
                id.setValue(idResult);
                
                row.addObject(id);                  
                row.addObject(lname);
                row.addObject(fname);
                model.add(row);

            } 
            if(SessionManager.getSession().getAttribute("systemUserId") == null)
                SessionManager.getSession().setAttribute("systemUserId", remote.getSystemUserId().toString());
            CachingManager.putElement("screenQueryRpc", SessionManager.getSession().getAttribute("systemUserId")+":Provider", rpcSend);         
       }
        return model;
    }      

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO = new ProviderDO();
        NumberField providerId = (NumberField) rpcSend.getField("providerId");
        NoteDO providerNote = new NoteDO();
        //provider info        
        providerDO.setId((Integer)providerId.getValue());
        providerDO.setFirstName((String)rpcSend.getFieldValue("firstName"));
        providerDO.setLastName((String)rpcSend.getFieldValue("lastName"));
        providerDO.setMiddleName((String)rpcSend.getFieldValue("middleName"));
        providerDO.setNpi((String)rpcSend.getFieldValue("npi"));
        providerDO.setTypeId((Integer)rpcSend.getFieldValue("providerTypeId"));
                
              
        
        //providerDO.setTypeId(new Integer(selItem.akey));
        
        //System.out.println("selected type id "+ new Integer(selItem.akey));
        
        List<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
        
        TableModel addressTable = (TableModel)rpcSend.getField("providerAddressTable").getValue();
        
        System.out.println("addressTable.numRows() "+ addressTable.numRows());
        for(int iter = 0; iter < addressTable.numRows(); iter++){
            
            ProviderAddressDO provAddDO = new ProviderAddressDO();
            TableRow row = addressTable.getRow(iter);
            
            NumberField provAddId = (NumberField)row.getHidden("provAddId");
            NumberField addId = (NumberField)row.getHidden("addId");
            
            if(provAddId != null){
             provAddDO.setId((Integer)(provAddId).getValue());
            } 
            provAddDO.setLocation((String)((StringField)row.getColumn(0)).getValue());
            provAddDO.setExternalId((String)((StringField)row.getColumn(1)).getValue());
            provAddDO.setProvider((Integer)providerId.getValue());
           
            System.out.println("addId "+addId); 
            StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            if(deleteFlag == null){
                provAddDO.setDelete(false);
            }else{
                provAddDO.setDelete("Y".equals(deleteFlag.getValue()));
            }
            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && provAddId == null){
                //do nothing
            }else{
             if(addId != null){              
              provAddDO.getAddressDO().setId((Integer)addId.getValue());             
             }             
            
            provAddDO.getAddressDO().setMultipleUnit((String)((StringField)row.getColumn(2)).getValue());
            provAddDO.getAddressDO().setStreetAddress((String)((StringField)row.getColumn(3)).getValue());
            provAddDO.getAddressDO().setCity((String)((StringField)row.getColumn(4)).getValue());
            provAddDO.getAddressDO().setState((String)((StringField)row.getColumn(5)).getValue());
            provAddDO.getAddressDO().setCountry((String)((StringField)row.getColumn(6)).getValue());
            provAddDO.getAddressDO().setZipCode((String)((StringField)row.getColumn(7)).getValue());
            provAddDO.getAddressDO().setWorkPhone((String)((StringField)row.getColumn(8)).getValue());
            provAddDO.getAddressDO().setHomePhone((String)((StringField)row.getColumn(9)).getValue());
            provAddDO.getAddressDO().setCellPhone((String)((StringField)row.getColumn(10)).getValue());
            provAddDO.getAddressDO().setFaxPhone((String)((StringField)row.getColumn(11)).getValue());
            provAddDO.getAddressDO().setEmail((String)((StringField)row.getColumn(12)).getValue());
            
            provAddDOList.add(provAddDO);   
           } 
        }
                                
        
        providerNote.setSubject((String)rpcSend.getFieldValue("usersSubject"));
        providerNote.setText((String)rpcSend.getFieldValue("usersNote"));
        providerNote.setIsExternal("Y");
                
        
        try{
            remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        
        ProviderDO provDO = remote.getProvider((Integer)providerId.getValue());
        //set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());
                
        
        //List addressList = remote.getProviderAddresses((Integer)providerId.getValue());
        //rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));                               
               
        //DataModel notesModel = getNotesModel(provDO.getId());
        //rpcReturn.setFieldValue("notesModel", notesModel);
        
        return rpcReturn;
    }

   

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {        
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        Integer providerId = (Integer)key.getObject(0).getValue();
                
        ProviderDO provDO = (ProviderDO)remote.getProvider(providerId);        
//      set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());                
        
        //List addressList = remote.getProviderAddresses(providerId);
        //rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));
                

        //DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
        //rpcReturn.setFieldValue("notesModel", notesModel);                                    
        
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        Integer providerId = (Integer)key.getObject(0).getValue();
        
        
        ProviderDO provDO = new ProviderDO();
         try{
          provDO =  (ProviderDO)remote.getProviderAndLock(providerId);
         }catch(Exception ex){
             throw new RPCException(ex.getMessage());
         }  
//      set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());                
        
        //List addressList = remote.getProviderAddresses(providerId);
        //rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));               

        //DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
        //rpcReturn.setFieldValue("notesModel", notesModel);                                    
        
        return rpcReturn;
    }    
    
    
    public TableModel fillAddressTable(TableModel addressModel, List contactsList){       
        try{
            addressModel.reset();
            
            for(int iter = 0;iter < contactsList.size();iter++) {
                ProviderAddressDO addressRow = (ProviderAddressDO)contactsList.get(iter);

                   TableRow row = addressModel.createRow();
                   NumberField id = new NumberField();
                   id.setType("integer");
                   NumberField addId = new NumberField();
                   addId.setType("integer");
                    id.setValue(addressRow.getId());
                    addId.setValue(addressRow.getAddressDO().getId());
                    row.addHidden("provAddId", id);
                    row.addHidden("addId", addId);
                    
                    row.getColumn(0).setValue(addressRow.getLocation());
                    row.getColumn(1).setValue(addressRow.getExternalId());
                    row.getColumn(2).setValue(addressRow.getAddressDO().getMultipleUnit());
                    row.getColumn(3).setValue(addressRow.getAddressDO().getStreetAddress());
                    row.getColumn(4).setValue(addressRow.getAddressDO().getCity()); 
                    row.getColumn(5).setValue(addressRow.getAddressDO().getState());  
                    row.getColumn(6).setValue(addressRow.getAddressDO().getCountry());
                    row.getColumn(7).setValue(addressRow.getAddressDO().getZipCode());
                    row.getColumn(8).setValue(addressRow.getAddressDO().getWorkPhone());
                    row.getColumn(9).setValue(addressRow.getAddressDO().getHomePhone());
                    row.getColumn(10).setValue(addressRow.getAddressDO().getCellPhone());
                    row.getColumn(11).setValue(addressRow.getAddressDO().getFaxPhone());
                    row.getColumn(12).setValue(addressRow.getAddressDO().getEmail());
                                                            
                    addressModel.addRow(row);
           } 
            
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }       
        
        return addressModel;  
    }

    

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getInitialModel(String cat) {        
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        int id = -1;
                
        DataModel model = new DataModel();
        
        if(cat.equals("providerType")){
            id = catRemote.getCategoryId("provider_type");                       
        }else if(cat.equals("state")){
            id = catRemote.getCategoryId("state");
        }else if(cat.equals("country")){
            id = catRemote.getCategoryId("country");            
        }
                
        if(id >-1){
            entries = catRemote.getDropdownValues(id);
            DataSet blankset = new DataSet();           
            StringObject blankStringId = new StringObject();
                          
            BooleanObject blankSelected = new BooleanObject();               
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject();
            blankNumberId.setType("integer");
            blankNumberId.setValue(new Integer(0));
            if(cat.equals("providerType")){
              blankset.addObject(blankNumberId);
            } else{
              blankset.addObject(blankStringId);  
            }            
            
            blankSelected.setValue(new Boolean(false));
            blankset.addObject(blankSelected);
            
            model.add(blankset);        
          
        
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            Object[] idType = (Object[])iter.next();
            //System.out.println("typeId "+idType[0]);
            //System.out.println("type "+idType[1]); 
            DataSet set = new DataSet();
            //Object[] result = (Object[]) entries.get(i);
            //id
            Integer dropdownId = (Integer)idType[0];
            //entry
            String dropdownText = (String)idType[1];
            
            StringObject textObject = new StringObject();
            
            
            BooleanObject selected = new BooleanObject();
            
            textObject.setValue(dropdownText);
            set.addObject(textObject);
            
            if(cat.equals("providerType")){
                NumberObject numberId = new NumberObject();
                numberId.setType("integer");
                numberId.setValue(dropdownId);
                set.addObject(numberId);
            }else{
               StringObject stringId = new StringObject();
               stringId.setValue(dropdownText);
               set.addObject(stringId);            
            }
            
            selected.setValue(new Boolean(false));
            set.addObject(selected);
            
            model.add(set);
            
         }
        }        
        
        return model;
    }
    
    public StringObject getNotesModel(NumberObject key){
//    	remote interface to call the provider bean
    	ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");

        //gets the whole notes list now
        List notesList = remote.getProviderNotes((Integer)key.getValue());
        
        Iterator itr = notesList.iterator();
        try{
        Document doc = XMLUtil.createNew("panel");
		Element root = (Element) doc.getDocumentElement();
		root.setAttribute("key", "notePanel");   
		int i=0;
        while(itr.hasNext()){           
            Object[] result = (Object[])itr.next();
                        
            //user id
            Integer userId = (Integer)result[1];
            //body
            String body = (String)result[2];
            //date
            Datetime date = new Datetime(Datetime.YEAR,Datetime.MINUTE,result[3]);
            //subject
            String subject = (String)result[4];
                        
            
            SystemUserRemote securityRemote = (SystemUserRemote)EJBFactory.lookup("SystemUserBean/remote");
            SystemUserDO user = securityRemote.getSystemUser(userId,false);
            
            String userName = user.getLoginName().trim();  

        	 Element mainRowPanel = (Element) doc.createElement("panel");
        	 Element topRowPanel = (Element) doc.createElement("panel");
        	 Element titleWidgetTag = (Element) doc.createElement("widget");
        	 Element titleText = (Element) doc.createElement("text");
        	 Element authorWidgetTag = (Element) doc.createElement("widget");
        	 Element authorPanel = (Element) doc.createElement("panel");
        	 Element dateText = (Element) doc.createElement("text");
        	 Element authorText = (Element) doc.createElement("text");
        	 Element bodyWidgetTag = (Element) doc.createElement("widget");
        	 Element bodytextTag = (Element) doc.createElement("text");
        	 
        	 mainRowPanel.setAttribute("key", "note"+i);
        	 if(i % 2 == 1){
                 mainRowPanel.setAttribute("style", "AltTableRow");
             }else{
            	 mainRowPanel.setAttribute("style", "TableRow");
             }
        	 mainRowPanel.setAttribute("layout", "vertical");
        	 mainRowPanel.setAttribute("width", "507px");
        	 
        	 topRowPanel.setAttribute("layout", "horizontal");
        	 topRowPanel.setAttribute("width", "507px");
        	 titleText.setAttribute("key", "note"+i+"Title");
        	 titleText.setAttribute("style", "notesSubjectText");
        	 titleText.appendChild(doc.createTextNode(subject));
        	 authorWidgetTag.setAttribute("halign", "right");
        	 authorPanel.setAttribute("layout", "vertical");
        	 dateText.setAttribute("key", "note"+i+"Date");
        	 dateText.appendChild(doc.createTextNode(date.toString()));
        	 authorText.setAttribute("key", "note"+i+"Author");
        	 authorText.appendChild(doc.createTextNode("by "+userName));
        	 bodytextTag.setAttribute("key", "note"+i+"Body");
        	 bodytextTag.setAttribute("wordwrap", "true");
        	 bodytextTag.appendChild(doc.createTextNode(body));
        	 
        	 root.appendChild(mainRowPanel);
        	 mainRowPanel.appendChild(topRowPanel);
        	 mainRowPanel.appendChild(bodyWidgetTag);
        	 topRowPanel.appendChild(titleWidgetTag);
        	 topRowPanel.appendChild(authorWidgetTag);
        	 titleWidgetTag.appendChild(titleText);
        	 authorWidgetTag.appendChild(authorPanel);
        	 authorPanel.appendChild(dateText);
        	 authorPanel.appendChild(authorText);
        	 bodyWidgetTag.appendChild(bodytextTag);
          
          i++;
      }

        StringObject returnObject = new StringObject();
        returnObject.setValue(XMLUtil.toString(doc));
        
        return returnObject;
    	
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }
        return null;        
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public TableField getAddressModel(NumberObject providerId,TableField model){
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        List addressList = remote.getProviderAddresses((Integer)providerId.getValue());
        model.setValue(fillAddressTable((TableModel)model.getValue(),addressList));
        return model;
    }
        
    
    
}
