package org.openelis.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.client.dataEntry.screen.Provider.ProviderServletInt;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.client.services.AppScreenServiceInt;
import org.openelis.gwt.client.services.AutoCompleteServiceInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.client.widget.pagedtree.TreeModelItem;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.NumberField;

import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.StringField;

import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;

import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
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


public class ProviderServlet extends AppServlet implements
                                               AppScreenServiceInt,
                                               ProviderServletInt,
                                               AutoCompleteServiceInt{

    /**
     * 
     */
    private static final long serialVersionUID = 0L;
    private static final int leftTableRowsPerPage = 20;
    private String systemUserId = "";
    

    private UTFResource openElisConstants = UTFResource.getBundle("org.openelis.client.main.constants.OpenELISConstants",
                                                                new Locale((SessionManager.getSession().getAttribute("locale") == null ? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
    
    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl");        
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return fetch(key, rpcReturn);
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        
       System.out.println("starting commitAdd() ");
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
        
        System.out.println("addressTable.numRows() "+ addressTable.numRows());
        for(int iter = 0; iter < addressTable.numRows(); iter++){
            
            ProviderAddressDO provAddDO = new ProviderAddressDO();
            TableRow row = addressTable.getRow(iter);
            
            //NumberField provAddId = (NumberField)row.getHidden("provAddId");
            //NumberField addId = (NumberField)row.getHidden("addId");
            
           // if(provAddId != null){
           //  provAddDO.setId((Integer)(provAddId).getValue());
           // } 
            System.out.println("location "+ (String)((StringField)row.getColumn(0)).getValue());
            provAddDO.setLocation((String)((StringField)row.getColumn(0)).getValue());
            provAddDO.setExternalId((String)((StringField)row.getColumn(1)).getValue());
            //provAddDO.setProvider((Integer)providerId.getValue());
           
            //System.out.println("addId "+addId); 
            
           // if(addId != null){              
           //  provAddDO.getAddressDO().setId((Integer)addId.getValue());             
          //  } 
            
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
        
        System.out.println("provAddDOList.size() "+ provAddDOList.size());
        
        Integer providerId = (Integer)remote.updateProvider(providerDO, providerNote, provAddDOList);
        System.out.println("after add "+ provAddDOList.size());
        
        ProviderDO provDO = remote.getProvider(providerId,false);
        //set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());
                
        
        List addressList = remote.getProviderAddresses(providerId);
        rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));
            
        System.out.println("size of addressList from database"+ addressList.size());
        
        //TreeModel treeModel = getNoteTreeModel(provDO.getId(), true);
        //rpcReturn.setFieldValue("notesTree", treeModel);
        
        rpcReturn.setFieldValue("usersSubject", null);
        rpcReturn.setFieldValue("usersNote", null);
        
        DataModel notesModel = getNotesModel(provDO.getId());
        rpcReturn.setFieldValue("notesModel", notesModel);
        
        return rpcReturn;
    }

    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        System.out.println("starting commitQuery");
        //ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");        
        if(rpcSend == null){
           //need to get the query rpc out of the cache
        //if(systemUserId.equals("")){
           // systemUserId = remote.getSystemUserId().toString();
        //CachingManager.putElement("screenQueryRpc", systemUserId+":Provider", rpcSend);
       // }
        
        System.out.println("put screenQueryRpc");
        
            FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", systemUserId+":Provider");

           if(rpc == null)
               throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

            List providers = null;
                
            try{
                
                ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote"); 
                providers = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                System.out.println("providers.size() "+providers.size());
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());
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
            //StringObject lname = new StringObject();
            //StringObject fname = new StringObject();
            StringObject name = new StringObject();
            id.setType("integer");
            //lname.setValue(lnameResult);
            if(fnameResult !=null){
             //fname.setValue(", "+fnameResult);
               if(!(fnameResult.trim().equals(""))) 
                name.setValue(lnameResult+","+fnameResult);
               else {
                   name.setValue(lnameResult);       
               }
            }
            else {
                name.setValue(lnameResult);       
            }
            id.setValue(idResult);
            
            row.addObject(id);          
            //row.addObject(lname);
            //row.addObject(fname);
            row.addObject(name);
            model.add(row);
            i++;
         }
        //} 
        //if(systemUserId.equals("")){
            //systemUserId = remote.getSystemUserId().toString();
        //CachingManager.putElement("screenQueryRpc", systemUserId+":Organization", rpcSend);
        
        System.out.println("ending commitQuery");
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
                //StringObject lname = new StringObject();
                //StringObject fname = new StringObject();
                StringObject name = new StringObject();
                id.setType("integer");
                //lname.setValue(lnameResult);
                if(fnameResult !=null){                    
                   if(!(fnameResult.trim().equals(""))){
                     name.setValue(lnameResult+","+fnameResult);
                   }else {
                       name.setValue(lnameResult);   
                   }
                }
                else {
                    name.setValue(lnameResult);       
                }
                id.setValue(idResult);
                
                row.addObject(id);          
                //row.addObject(lname);
                //row.addObject(fname);
                row.addObject(name);
                model.add(row);

            } 
        if(systemUserId.equals(""))
          systemUserId = remote.getSystemUserId().toString();
          CachingManager.putElement("screenQueryRpc", systemUserId+":Provider", rpcSend);          
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
         
        System.out.println("new note in commitUpdate");
        
        System.out.println("new note's subject : "+(String)rpcSend.getFieldValue("usersSubject"));
        System.out.println("new note's body : "+(String)rpcSend.getFieldValue("usersNote"));
        
        providerNote.setSubject((String)rpcSend.getFieldValue("usersSubject"));
        providerNote.setText((String)rpcSend.getFieldValue("usersNote"));
        providerNote.setIsExternal("Y");
        
        System.out.println("provAddDOList.size() "+ provAddDOList.size());
        
        remote.updateProvider(providerDO, providerNote, provAddDOList);
        
        ProviderDO provDO = remote.getProvider((Integer)providerId.getValue(),false);
        //set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());
                
        
        List addressList = remote.getProviderAddresses((Integer)providerId.getValue());
        rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));
        
        //TreeModel treeModel = getNoteTreeModel(provDO.getId(), true);
        //rpcReturn.setFieldValue("notesTree", treeModel);
        
        rpcReturn.setFieldValue("usersSubject", null);
        rpcReturn.setFieldValue("usersNote", null);
        
        System.out.println("notes model in commitUpdate");
        DataModel notesModel = getNotesModel(provDO.getId());
        rpcReturn.setFieldValue("notesModel", notesModel);
        
        return rpcReturn;
    }

    public FormRPC delete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        System.out.println("starting provider fetch");
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        Integer providerId = (Integer)key.getObject(0).getValue();
        
        System.out.println("provider "+remote.getProvider(providerId,false));
        
        ProviderDO provDO = (ProviderDO)remote.getProvider(providerId,false);        
//      set the fields in the RPC
        rpcReturn.setFieldValue("providerId", provDO.getId());
        rpcReturn.setFieldValue("lastName",provDO.getLastName());
        rpcReturn.setFieldValue("firstName",provDO.getFirstName());
        rpcReturn.setFieldValue("npi",provDO.getNpi());        
        rpcReturn.setFieldValue("middleName",provDO.getMiddleName());
        rpcReturn.setFieldValue("providerTypeId",provDO.getTypeId());                
        
        List addressList = remote.getProviderAddresses(providerId);
        rpcReturn.setFieldValue("providerAddressTable",fillAddressTable((TableModel)rpcReturn.getField("providerAddressTable").getValue(),addressList));
        
        //TreeModel treeModel = getNoteTreeModel((Integer)key.getObject(0).getValue(), true);       
        //rpcReturn.setFieldValue("notesTree", treeModel);

        DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
        rpcReturn.setFieldValue("notesModel", notesModel);                                    
        
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        return fetch(key, rpcReturn);
    }    
    
    
    public TableModel fillAddressTable(TableModel addressModel, List contactsList){
       System.out.println("starting  fillAddressTable");
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

    public TreeModel getNoteTreeModel(Integer key, boolean topLevel){
        //remote interface to call the organization bean
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");

        List notesList = remote.getProviderNotes(key,topLevel);
        
        TreeModel treeModel = new TreeModel();
        Iterator itr = notesList.iterator();
        while(itr.hasNext()){
            Object[] result = (Object[])itr.next();
            //id
            Integer id = (Integer)result[0];
            //date
            Timestamp date = (Timestamp)result[1];
            //subject
            String subject = (String)result[2];
            
            TreeModelItem treeModelItem = new TreeModelItem();
            treeModelItem.setText(date.toString()+" / "+subject);
            treeModelItem.setUserObject(id.toString());
            
            treeModelItem.setHasDummyChild(true);
            
            treeModel.addItem(treeModelItem);
        }
       
       return treeModel;
    }
    
    public String getNoteTreeSecondLevelXml(String key, boolean topLevel){
        //remote interface to call the organization bean
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");

        List notesList = remote.getProviderNotes(Integer.valueOf(key),topLevel);
        
        try {
            Iterator itr = notesList.iterator();
            
            //int i=0;
            Document doc = XMLUtil.createNew("tree");
            while(itr.hasNext()){
            Object[] result = (Object[])itr.next(); 
            //id
            //Integer id = (Integer) result[0];
            //user
            Integer userId = (Integer) result[1];
            //body
            String body = (String) result[2];
            
            SystemUserRemote securityRemote = (SystemUserRemote)EJBFactory.lookup("SystemUserBean/remote");
            SystemUserDO user = securityRemote.getSystemUser(userId,false);
            
            Element root = doc.getDocumentElement();
            root.setAttribute("key", "menuList");
            root.setAttribute("height", "100%");
            root.setAttribute("vertical","true");           
                 
             Element elem = doc.createElement("label");
             elem.setAttribute("text", "Author: "+ user.getLastName()+", "+user.getFirstName()); 
             
             Element elem2 = doc.createElement("label");
             elem2.setAttribute("text", body); 
                                                                      
             root.appendChild(elem); 
             root.appendChild(elem2);
            }
             
            //}                       
            return XMLUtil.toString(doc);
        }catch(Exception e){
            //log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel getInitialModel(String cat) {
        //ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        int id = -1;
        
        System.out.println("starting getInitialModel");
        DataModel model = new DataModel();
        
        if(cat.equals("providerType")){
            id = catRemote.getCategoryId("provider_type");                       
        }else if(cat.equals("state")){
            id = catRemote.getCategoryId("state");
        }else if(cat.equals("country")){
            id = catRemote.getCategoryId("country");            
        }
        
        System.out.println("cat "+ cat +" id "+ id);
        if(id >-1){
            entries = catRemote.getDropdownValues(id);
            DataSet blankset = new DataSet();
            System.out.println("entries.size() "+ entries.size());
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
        System.out.println("added blankset");
       // System.out.println("typeId "+provDO.getTypeId());
        //System.out.println("type "+provDO.getType()); 
       // List<Object[]> provTypes = remote.getProviderTypes();        
        
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
            System.out.println("added dataset");
         }
        }
        System.out.println("ending getInitialModel");
        return model;
    }
    
    public DataModel getNotesModel(Integer key){
        //remote interface to call the organization bean
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");

        //gets the whole notes list now
        List notesList = remote.getProviderNotes(key);
        
        DataModel notesModel = new DataModel();
        System.out.println("notesList.size() "+ notesList.size());
        Iterator itr = notesList.iterator();
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
            
            System.out.println("userId "+ userId+ " body "+ body+ " date "+date+ " subject "+subject);
            
            DataSet set = new DataSet();
            StringObject subjectLine = new StringObject();
            StringObject bodyLine = new StringObject();
            
            SystemUserRemote securityRemote = (SystemUserRemote)EJBFactory.lookup("SystemUserBean/remote");
            SystemUserDO user = securityRemote.getSystemUser(userId,false);
            
            subjectLine.setValue(date+" "+user.getLoginName().trim()+": " + subject);
            bodyLine.setValue(body);
            
            set.addObject(subjectLine);
            set.addObject(bodyLine);
            notesModel.add(set);
        }
       
       return notesModel;
    }

    public DataModel getMatches(String cat, DataModel model, String match) {
        // TODO Auto-generated method stub
        return null;
    }
}
