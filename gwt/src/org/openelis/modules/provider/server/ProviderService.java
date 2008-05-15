package org.openelis.modules.provider.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.meta.ProviderMeta;
import org.openelis.meta.ProviderNoteMeta;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.ProviderRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.remote.SystemUserRemote;


public class ProviderService implements AppScreenFormServiceInt{
    
    private static final long serialVersionUID = 0L;
    private static final int leftTableRowsPerPage = 19;
           
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {        
        List providers = new ArrayList();
        //ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");        
        if(rpcSend == null){
           //need to get the query rpc out of the cache        
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("ProviderQuery");
    
           if(rpc == null)
               throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
                
            try{
                ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote"); 
                providers = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);                
            }catch(Exception e){
            	if(e instanceof LastPageException){
            		throw new LastPageException(openElisConstants.getString("lastPageException"));
            	}else{
            		throw new RPCException(e.getMessage());	
            	}
            }
          
        } else{
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            fields.remove("providerAddressTable");
            
                try{
                    providers = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }              
             
//          need to save the rpc used to the encache
            if(SessionManager.getSession().getAttribute("ProviderQuery") == null)
                SessionManager.getSession().setAttribute("ProviderQuery", rpcSend);
            }
            
            //fill the model with the query result
            int i=0;
            model.clear();        
            while(i < providers.size() && i < leftTableRowsPerPage) {
        
                IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)providers.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String lnameResult = resultDO.getLastName();
                
                String fnameResult = resultDO.getFirstName();
        
                DataSet row = new DataSet();
                
                NumberObject id = new NumberObject(NumberObject.INTEGER);            
                StringObject lname = new StringObject();
                StringObject fname = new StringObject();
                 lname.setValue((lnameResult != null ? lnameResult.trim() : null));
                 fname.setValue((fnameResult != null ? fnameResult.trim() : null));
                
                id.setValue(idResult);
                
                row.setKey(id); 
                row.addObject(lname);
                row.addObject(fname);
                
                model.add(row);
                i++;
             }
        return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
               
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpcSend);
    
        NoteDO providerNote = new NoteDO();       
        
        TableModel addressTable = (TableModel)rpcSend.getField("providerAddressTable").getValue();
        Integer providerId = providerDO.getId();
                
        ArrayList<ProviderAddressDO> provAddDOList =  getProviderAddressListFromRPC(addressTable,providerId);
                                        
        providerNote.setSubject((String)rpcSend.getFieldValue(ProviderNoteMeta.SUBJECT));
        providerNote.setText((String)rpcSend.getFieldValue(ProviderNoteMeta.TEXT));
        providerNote.setIsExternal("Y");
        
        List<Exception> exceptionList = remote.validateForAdd(providerDO, provAddDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, addressTable, rpcSend);   
            //rpcSend.status = IForm.INVALID_FORM;
            return rpcSend;
        } 
                         
        try{
            providerId = (Integer)remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, addressTable, rpcSend);
            
            return rpcSend;
        }
         
        //ProviderDO provDO = remote.getProvider(providerId);
        providerDO.setId(providerId);
        
        setFieldsInRPC(rpcReturn, providerDO);        
        
        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpcSend);
    
        NoteDO providerNote = new NoteDO();       
        
        TableModel addressTable = (TableModel)rpcSend.getField("providerAddressTable").getValue();
                
        ArrayList<ProviderAddressDO> provAddDOList =  getProviderAddressListFromRPC(addressTable,providerDO.getId());
                                        
        providerNote.setSubject((String)rpcSend.getFieldValue(ProviderNoteMeta.SUBJECT));
        providerNote.setText((String)rpcSend.getFieldValue(ProviderNoteMeta.TEXT));
        providerNote.setIsExternal("Y");
        
        List<Exception> exceptionList = remote.validateForUpdate(providerDO, provAddDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, addressTable, rpcSend);   
            rpcSend.status = IForm.INVALID_FORM;
            return rpcSend;
        } 
                         
        try{
            remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, addressTable, rpcSend);
            
            return rpcSend;
        }
        
        //ProviderDO provDO = remote.getProvider((Integer)providerId);
        //set the fields in the RPC
        setFieldsInRPC(rpcReturn, providerDO);
                              
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)key.getKey().getValue();
            
            
            ProviderDO provDO = new ProviderDO();
             try{
              provDO =  (ProviderDO)remote.getProviderAndUnlock(providerId);
              }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
              setFieldsInRPC(rpcReturn, provDO);
            return rpcReturn;
        }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {        
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)key.getKey().getValue();
                    
            ProviderDO provDO = (ProviderDO)remote.getProvider(providerId);        
    //      set the fields in the RPC
            setFieldsInRPC(rpcReturn, provDO);
                                                          
            return rpcReturn;
        }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)key.getKey().getValue();
            
            
            ProviderDO provDO = new ProviderDO();
             try{
              provDO =  (ProviderDO)remote.getProviderAndLock(providerId);
             }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpcReturn, provDO);
                    
                                                          
            return rpcReturn;
        }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl");        
    }
    
    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl"));
        
        DataModel providerTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "providerTypeDropDown");
        DataModel stateDropDownField = (DataModel)CachingManager.getElement("InitialData", "stateDropDown");
        DataModel countryDropDownField = (DataModel)CachingManager.getElement("InitialData", "countryDropDown");
        
        if(providerTypeDropDownField ==null){
            providerTypeDropDownField = getInitialModel("providerType");
            CachingManager.putElement("InitialData", "providerTypeDropDown", providerTypeDropDownField);
        }   
           
         if(stateDropDownField == null){
             stateDropDownField = getInitialModel("state");
             CachingManager.putElement("InitialData", "stateDropDown", stateDropDownField);
         }  
         
         if(countryDropDownField == null){
             countryDropDownField = getInitialModel("country");
             CachingManager.putElement("InitialData", "countryDropDown", countryDropDownField);
         }   
         
         HashMap map = new HashMap();
         map.put("xml", xml);
         map.put("providers", providerTypeDropDownField);
         map.put("states", stateDropDownField);
         map.put("countries", countryDropDownField);
         
         return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public TableModel fillAddressTable(TableModel addressModel, List contactsList){       
        try{
            addressModel.reset();
            
            for(int iter = 0;iter < contactsList.size();iter++) {
                ProviderAddressDO addressRow = (ProviderAddressDO)contactsList.get(iter);

                   TableRow row = addressModel.createRow();
                   NumberField id = new NumberField(NumberObject.INTEGER);
                   NumberField addId = new NumberField(NumberObject.INTEGER);
                    id.setValue(addressRow.getId());
                    addId.setValue(addressRow.getAddressDO().getId());
                    row.addHidden("provAddId", id);
                    row.addHidden("addId", addId);
                    
                    row.getColumn(0).setValue(addressRow.getLocation());
                    row.getColumn(1).setValue(addressRow.getExternalId());
                    row.getColumn(2).setValue(addressRow.getAddressDO().getMultipleUnit());
                    row.getColumn(3).setValue(addressRow.getAddressDO().getStreetAddress());
                    row.getColumn(4).setValue(addressRow.getAddressDO().getCity());     
                    if(addressRow.getAddressDO().getState()!=null){
                     row.getColumn(5).setValue(addressRow.getAddressDO().getState());
                    }else{
                        row.getColumn(5).setValue("");  
                    } 
                    if(addressRow.getAddressDO().getCountry()!=null){                    
                     row.getColumn(6).setValue(addressRow.getAddressDO().getCountry());
                    }else{
                        row.getColumn(6).setValue("");  
                    }                    
                                        
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
                                       
            blankStringId.setValue("");
            blankset.addObject(blankStringId);
            
            NumberObject blankNumberId = new NumberObject(NumberObject.INTEGER);
            blankNumberId.setValue(new Integer(-1));
            if(cat.equals("providerType")){

              blankset.setKey(blankNumberId);
            } else{

              blankset.setKey(blankStringId);
            }            

            
            model.add(blankset);        
          
        
            int i=0;
            while(i < entries.size()){
                DataSet set = new DataSet();
                IdNameDO resultDO = (IdNameDO) entries.get(i);

                //id
                Integer dropdownId = resultDO.getId();
                //entry
                String dropdownText = resultDO.getName();
                
                StringObject textObject = new StringObject();
    
                textObject.setValue(dropdownText);
                set.addObject(textObject);
                
                if(cat.equals("providerType")){
                    NumberObject numberId = new NumberObject(NumberObject.INTEGER);
                    numberId.setValue(dropdownId);
    
                    set.setKey(numberId);
                }else{
                   StringObject stringId = new StringObject();
                   stringId.setValue((dropdownText != null ? dropdownText.trim() : null));
    
                   set.setKey(stringId);
                }
    
                
                model.add(set); 
                i++;
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
            NoteDO noteRow = (NoteDO)itr.next();
            
            //user id
            Integer userId = noteRow.getSystemUser();
            //body
            String body = noteRow.getText();
            //date
            String date = noteRow.getTimestamp().toString();
            //subject
            String subject = noteRow.getSubject();
                        
            
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
        	 mainRowPanel.setAttribute("width", "531px");
        	 
        	 topRowPanel.setAttribute("layout", "horizontal");
        	 topRowPanel.setAttribute("width", "531px");
        	 titleText.setAttribute("key", "note"+i+"Title");
        	 titleText.setAttribute("style", "notesSubjectText");
        	 titleText.appendChild(doc.createTextNode(subject));
        	 authorWidgetTag.setAttribute("halign", "right");
        	 authorPanel.setAttribute("layout", "vertical");
        	 dateText.setAttribute("key", "note"+i+"Date");
        	 dateText.appendChild(doc.createTextNode(date));
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
        	e.printStackTrace();
        }
        return null;        
    }

    public TableField getAddressModel(NumberObject providerId,TableField model){
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        List addressList = remote.getProviderAddresses((Integer)providerId.getValue());
        model.setValue(fillAddressTable((TableModel)model.getValue(),addressList));
        return model;
    }
        
    private void setFieldsInRPC(FormRPC rpcReturn, ProviderDO provDO){
        rpcReturn.setFieldValue(ProviderMeta.ID, provDO.getId());
        rpcReturn.setFieldValue(ProviderMeta.LAST_NAME,provDO.getLastName());
        rpcReturn.setFieldValue(ProviderMeta.FIRST_NAME,provDO.getFirstName());
        rpcReturn.setFieldValue(ProviderMeta.NPI,provDO.getNpi());        
        rpcReturn.setFieldValue(ProviderMeta.MIDDLE_NAME,provDO.getMiddleName());                              
        rpcReturn.setFieldValue(ProviderMeta.TYPE,provDO.getTypeId());         
    }
    
    private ProviderDO getProviderDOFromRPC(FormRPC rpcSend){
     NumberField providerId = (NumberField) rpcSend.getField(ProviderMeta.ID);   
     ProviderDO providerDO = new ProviderDO();
     //provider info        
     providerDO.setId((Integer)providerId.getValue());
     providerDO.setFirstName(((String)rpcSend.getFieldValue(ProviderMeta.FIRST_NAME)).trim());
     providerDO.setLastName(((String)rpcSend.getFieldValue(ProviderMeta.LAST_NAME)).trim());
     providerDO.setMiddleName(((String)rpcSend.getFieldValue(ProviderMeta.MIDDLE_NAME)).trim());
     providerDO.setNpi(((String)rpcSend.getFieldValue(ProviderMeta.NPI)).trim());
     
     if(!new Integer(-1).equals(rpcSend.getFieldValue(ProviderMeta.TYPE)))
      providerDO.setTypeId((Integer)rpcSend.getFieldValue(ProviderMeta.TYPE));
     
     return providerDO;
    }
    
    
    private ArrayList<ProviderAddressDO> getProviderAddressListFromRPC(TableModel addressTable, Integer providerId){
        ArrayList<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
          
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
            provAddDO.setProvider((Integer)providerId);
           
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
            
            provAddDO.getAddressDO().setMultipleUnit(((String)((StringField)row.getColumn(2)).getValue()).trim());
            provAddDO.getAddressDO().setStreetAddress(((String)((StringField)row.getColumn(3)).getValue()).trim());
            provAddDO.getAddressDO().setCity(((String)((StringField)row.getColumn(4)).getValue()).trim());
            
            if(!("").equals(row.getColumn(5).getValue())){
             provAddDO.getAddressDO().setState((String)row.getColumn(5).getValue());
            }
            if(!("").equals(row.getColumn(6).getValue())){
             provAddDO.getAddressDO().setCountry((String)row.getColumn(6).getValue());
            }
                        
            provAddDO.getAddressDO().setZipCode(((String)((StringField)row.getColumn(7)).getValue()).trim());
            provAddDO.getAddressDO().setWorkPhone(((String)((StringField)row.getColumn(8)).getValue()).trim());
            provAddDO.getAddressDO().setHomePhone(((String)((StringField)row.getColumn(9)).getValue()).trim());
            provAddDO.getAddressDO().setCellPhone(((String)((StringField)row.getColumn(10)).getValue()).trim());
            provAddDO.getAddressDO().setFaxPhone(((String)((StringField)row.getColumn(11)).getValue()).trim());
            provAddDO.getAddressDO().setEmail(((String)((StringField)row.getColumn(12)).getValue()).trim());
            
            provAddDOList.add(provAddDO);   
           } 
        }
       return provAddDOList;    
    }

	private void setRpcErrors(List exceptionList, TableModel contactsTable, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                TableRow row = contactsTable.getRow(((TableFieldErrorException)exceptionList.get(i)).getRowIndex());
                row.getColumn(contactsTable.getColumnIndexByFieldName(((TableFieldErrorException)exceptionList.get(i)).getFieldName()))
                                                                        .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = IForm.INVALID_FORM;
    }
}
