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
package org.openelis.modules.provider.server;

import org.openelis.domain.IdLastNameFirstNameDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.ProviderRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.CountryCacheHandler;
import org.openelis.server.handlers.ProviderTypeCacheHandler;
import org.openelis.server.handlers.StatesCacheHandler;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ProviderService implements AppScreenFormServiceInt<RPC, DataModel<DataSet>>{
    
    private static final long serialVersionUID = 0L;
    private static final int leftTableRowsPerPage = 18;
    
    private static final ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {        
        List providers = new ArrayList();
        
        if(form == null){
           //need to get the query rpc out of the cache        
            form = (Form)SessionManager.getSession().getAttribute("ProviderQuery");
    
           if(form == null)
               throw new QueryException(openElisConstants.getString("queryExpiredException"));
                
            try{
                ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote"); 
                providers = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);                
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
            }
          
        } else{
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            
            HashMap<String,AbstractField> fields = form.getFieldMap();
            
                try{
                    providers = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }              
             
//          need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("ProviderQuery", form);
            }
            
            //fill the model with the query result
            int i=0;
            if(model == null)
                model = new DataModel<DataSet>();
            else
                model.clear();
            while(i < providers.size() && i < leftTableRowsPerPage) {
        
                IdLastNameFirstNameDO resultDO = (IdLastNameFirstNameDO)providers.get(i);
                //org id
                Integer idResult = resultDO.getId();
                //org name
                String lnameResult = resultDO.getLastName();
                
                String fnameResult = resultDO.getFirstName();
        
                DataSet row = new DataSet();
                
                NumberObject id = new NumberObject(idResult);            
                StringObject lname = new StringObject();
                StringObject fname = new StringObject();
                 lname.setValue(lnameResult);
                 fname.setValue(fnameResult);
                
                
                row.setKey(id); 
                row.add(lname);
                row.add(fname);
                
                model.add(row);
                i++;
             }
        return model;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
               
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpc.form);
    
        NoteDO providerNote = new NoteDO();       
        
        DataModel addressTable = (DataModel)((Form)rpc.form.getField("addresses")).getField("providerAddressTable").getValue();;
        Integer providerId = providerDO.getId();
                
        ArrayList<ProviderAddressDO> provAddDOList =  getProviderAddressListFromRPC(addressTable,providerId);
                                        
//      build the noteDo from the form
        providerNote.setSubject((String)((Form)rpc.form.getField("notes")).getFieldValue(ProvMeta.getNote().getSubject()));
        providerNote.setText((String)((Form)rpc.form.getField("notes")).getFieldValue(ProvMeta.getNote().getText()));
        providerNote.setIsExternal("Y");
        
        List<Exception> exceptionList = remote.validateForAdd(providerDO, provAddDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            
            return rpc;
        } 
                         
        try{
            providerId = (Integer)remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList,rpc.form);
            
            return rpc;
        }
         
        providerDO.setId(providerId);
        
        setFieldsInRPC(rpc.form, providerDO); 
        
        String tab = (String)rpc.form.getFieldValue("provTabPanel");
        if(tab.equals("notesTab")){
            DataSet key = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER, providerDO.getId());
            key.setKey(id);
            
            loadNotes(key, (Form)rpc.form.getField("notes"));
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        ((Form)rpc.form.getField("notes")).load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        ((Form)rpc.form.getField("notes")).setFieldValue(ProvMeta.getNote().getSubject(), null);
        ((Form)rpc.form.getField("notes")).setFieldValue(ProvMeta.getNote().getText(), null);
        
        return rpc;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        ProviderDO providerDO  = getProviderDOFromRPC(rpc.form);
    
        ArrayList<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
        NoteDO providerNote = new NoteDO();       
        
        DataModel addressTable = (DataModel)((Form)rpc.form.getField("addresses")).getField("providerAddressTable").getValue();;
        if(((Form)rpc.form.getField("addresses")).load)        
         provAddDOList = getProviderAddressListFromRPC(addressTable,providerDO.getId());
         
        if(((Form)rpc.form.getField("notes")).load){
         Form notesRPC = (Form)rpc.form.getField("notes");  
         providerNote.setSubject((String)notesRPC.getFieldValue(ProvMeta.getNote().getSubject()));
         providerNote.setText((String)notesRPC.getFieldValue(ProvMeta.getNote().getText()));
         providerNote.setIsExternal("Y");
        }
        
        List<Exception> exceptionList = remote.validateForUpdate(providerDO, provAddDOList);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList,rpc.form);   
            rpc.form.status = Form.Status.invalid;
            return rpc;
        } 
                         
        try{
            remote.updateProvider(providerDO, providerNote, provAddDOList);        
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList,rpc.form);
            
//          we need to refresh the notes tab if it is showing            
            
            return rpc;
        }

        //set the fields in the RPC
        setFieldsInRPC(rpc.form, providerDO);
         
        String tab = (String)rpc.form.getFieldValue("provTabPanel");
        if(tab.equals("notesTab")){
            DataSet key = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER, providerDO.getId());
            key.setKey(id);
            
            loadNotes(key, (Form)rpc.form.getField("notes"));
        }
        
        //we need to set the notes load param to true because update doesnt call resetRPC
        ((Form)rpc.form.getField("notes")).load = false;
        
        //we need to clear out the note subject and the note text fields after a commit
        ((Form)rpc.form.getField("notes")).setFieldValue(ProvMeta.getNote().getSubject(), null);
        ((Form)rpc.form.getField("notes")).setFieldValue(ProvMeta.getNote().getText(), null);
        
        return rpc;
    }

    public RPC commitDelete(RPC rpcReturn) throws RPCException {
        return null;
    }

    public RPC abort(RPC rpc) throws RPCException {
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();
            
            
            ProviderDO provDO = new ProviderDO();
             try{
              provDO =  (ProviderDO)remote.getProviderAndUnlock(providerId, SessionManager.getSession().getId());
              }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
              setFieldsInRPC(rpc.form, provDO);
              
              if(((Form)rpc.form.getField("addresses")).load){
                  Form contacts = (Form)rpc.form.getField("addresses");
                  getAddressesModel((NumberObject)((DataSet)rpc.key).getKey(), (TableField)contacts.getField("providerAddressTable"));
              }
              
              if(((Form)rpc.form.getField("notes")).load){
                  Form notesRpc = (Form)rpc.form.getField("notes");
                  notesRpc.setFieldValue("notesPanel",getNotesModel((NumberObject)((DataSet)rpc.key).getKey()).getValue());
              }
              return rpc;
           
        }

    public RPC fetch(RPC rpc) throws RPCException {        
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();
                    
            ProviderDO provDO = (ProviderDO)remote.getProvider(providerId);        
    //      set the fields in the RPC
            setFieldsInRPC(rpc.form, provDO);
             
            String tab = (String)rpc.form.getFieldValue("provTabPanel");
            if(tab.equals("addressesTab")){
                loadAddresses((DataSet)rpc.key,(Form)rpc.form.getField("addresses"));
            }
           
            if(tab.equals("notesTab")){
                loadNotes((DataSet)rpc.key, (Form)rpc.form.getField("notes"));
            }
            return rpc;
        }
    
    public Form loadAddresses(DataSet key, Form form) throws RPCException {
        getAddressesModel((NumberObject)key.getKey(), (TableField)form.getField("providerAddressTable"));
        form.load = true;
        return form;
    }
    
    public Form loadNotes(DataSet key, Form form) throws RPCException {
        StringObject so = getNotesModel((NumberObject)key.getKey());
        form.setFieldValue("notesPanel",so.getValue());
        form.load = true;
        return form;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
            ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
            Integer providerId = (Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue();
            
            
            ProviderDO provDO = new ProviderDO();
             try{
              provDO =  (ProviderDO)remote.getProviderAndLock(providerId, SessionManager.getSession().getId());
             }catch(Exception ex){
                 throw new RPCException(ex.getMessage());
             }  
    //      set the fields in the RPC
             setFieldsInRPC(rpc.form, provDO);              

             String tab = (String)rpc.form.getFieldValue("provTabPanel");
             if(tab.equals("addressesTab")){
                 loadAddresses((DataSet)rpc.key,(Form)rpc.form.getField("addresses"));
             }
            
             if(tab.equals("notesTab")){
                 loadNotes((DataSet)rpc.key, (Form)rpc.form.getField("notes"));
             }
                                                          
            return rpc;
        }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl");        
    }
    
    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/provider.xsl"));
        
        //DataModel providerTypeDropDownField = (DataModel)CachingManager.getElement("InitialData", "providerTypeDropDown");
        //DataModel stateDropDownField = (DataModel)CachingManager.getElement("InitialData", "stateDropDown");
        //DataModel countryDropDownField = (DataModel)CachingManager.getElement("InitialData", "countryDropDown");
        
        DataModel providerTypeDropDownField = ProviderTypeCacheHandler.getProviderTypes();
        DataModel stateDropdownField = StatesCacheHandler.getStates();
        DataModel countryDropdownField = CountryCacheHandler.getCountries();
        
        /*if(providerTypeDropDownField ==null){
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
         } */  
         
         HashMap map = new HashMap();
         map.put("xml", xml);
         map.put("providers", providerTypeDropDownField);
         map.put("states", stateDropdownField);
         map.put("countries", countryDropdownField);
         
         return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public RPC getScreen(RPC rpc) {
        return rpc;
    }

    public DataModel fillAddressTable(DataModel addressModel, List contactsList){       
        try{
            addressModel.clear();
            
            for(int iter = 0;iter < contactsList.size();iter++) {
                ProviderAddressDO addressRow = (ProviderAddressDO)contactsList.get(iter);

                   DataSet<Data> row = addressModel.createNewSet();
                   NumberField id = new NumberField(NumberObject.Type.INTEGER);
                   NumberField addId = new NumberField(NumberObject.Type.INTEGER);
                    id.setValue(addressRow.getId());
                    addId.setValue(addressRow.getAddressDO().getId());
                    DataMap data = new DataMap();                    
                    data.put("provAddId", id);
                    data.put("addId", addId);                    
                    row.setData(data);                   
                    row.get(0).setValue(addressRow.getLocation());
                    row.get(1).setValue(addressRow.getExternalId());
                    row.get(2).setValue(addressRow.getAddressDO().getMultipleUnit());
                    row.get(3).setValue(addressRow.getAddressDO().getStreetAddress());
                    row.get(4).setValue(addressRow.getAddressDO().getCity());     
                    if(addressRow.getAddressDO().getState()!=null){
                     row.get(5).setValue(new DataSet(new StringObject(addressRow
                                                                      .getAddressDO().getState())));
                    }else{
                        row.get(5).setValue(new DataSet(new StringObject("")));  
                    } 
                    if(addressRow.getAddressDO().getCountry()!=null){                    
                     row.get(6).setValue(new DataSet(new StringObject(addressRow
                                                                      .getAddressDO().getCountry())));
                    }else{
                        row.get(6).setValue(new DataSet(new StringObject("")));  
                    }                    
                                        
                    row.get(7).setValue(addressRow.getAddressDO().getZipCode());
                    row.get(8).setValue(addressRow.getAddressDO().getWorkPhone());
                    row.get(9).setValue(addressRow.getAddressDO().getHomePhone());
                    row.get(10).setValue(addressRow.getAddressDO().getCellPhone());
                    row.get(11).setValue(addressRow.getAddressDO().getFaxPhone());
                    row.get(12).setValue(addressRow.getAddressDO().getEmail());
                                                            
                    addressModel.add(row);
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

    
    public void getAddressesModel(NumberObject orgId,TableField model){
        ProviderRemote remote = (ProviderRemote)EJBFactory.lookup("openelis/ProviderBean/remote");
        List contactsList = remote.getProviderAddresses((Integer)orgId.getValue());
        fillAddressTable((DataModel)model.getValue(),contactsList);

    }
    
    public DataModel getInitialModel(String cat) {        
        CategoryRemote catRemote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        List entries = null; 
        Integer id = null;
                
        DataModel model = new DataModel();
        
        if(cat.equals("providerType")){
            id = catRemote.getCategoryId("provider_type");                       
        }else if(cat.equals("state")){
            id = catRemote.getCategoryId("state");
        }else if(cat.equals("country")){
            id = catRemote.getCategoryId("country");            
        }
                
        if(id != null){
            entries = catRemote.getDropdownValues(id);
            
            if(entries.size() > 0){ 
                DataSet blankset = new DataSet();           
                StringObject blankStringId = new StringObject();
                                           
                blankStringId.setValue("");
                blankset.add(blankStringId);
                
                NumberObject blankNumberId = new NumberObject(NumberObject.Type.INTEGER);
                blankNumberId.setValue(new Integer(-1));
                if(cat.equals("providerType")){
    
                  blankset.setKey(blankNumberId);
                } else{
    
                  blankset.setKey(blankStringId);
                }            
    
                
                model.add(blankset);        
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
    
                textObject.setValue(dropdownText);
                set.add(textObject);
                
                if(cat.equals("providerType")){
                    NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
                    numberId.setValue(dropdownId);
    
                    set.setKey(numberId);
                }else{
                   StringObject stringId = new StringObject();
                   stringId.setValue(dropdownText);
    
                   set.setKey(stringId);
                }
    
                
                model.add(set); 
                i++;
            }
        }        
        
        return model;
    }
    
    public StringObject getNotesModel(NumberObject key){
//      remote interface to call the provider bean
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
            
            if(body == null)
                body = "";
            
            //date
            String date = noteRow.getTimestamp().toString();
            //subject
            String subject = noteRow.getSubject();
            
            if(subject == null)
                subject = "";                        
            
            SystemUserRemote securityRemote = (SystemUserRemote)EJBFactory.lookup("SystemUserBean/remote");
            SystemUserDO user = securityRemote.getSystemUser(userId,false);
            
            String userName = user.getLoginName().trim();  

            Element mainRowPanel = (Element) doc.createElement("VerticalPanel");
            Element topRowPanel = (Element) doc.createElement("HorizontalPanel");
            Element titleWidgetTag = (Element) doc.createElement("widget");
            Element titleText = (Element) doc.createElement("text");
            Element authorWidgetTag = (Element) doc.createElement("widget");
            Element authorPanel = (Element) doc.createElement("VerticalPanel");
            Element dateText = (Element) doc.createElement("text");
            Element authorText = (Element) doc.createElement("text");
            Element bodyWidgetTag = (Element) doc.createElement("widget");
            Element bodytextTag =  (Element) doc.createElement("text");
             
             mainRowPanel.setAttribute("key", "note"+i);
             if(i % 2 == 1){
                 mainRowPanel.setAttribute("style", "AltTableRow");
             }else{
                 mainRowPanel.setAttribute("style", "TableRow");
             }
             mainRowPanel.setAttribute("width", "530px");

             topRowPanel.setAttribute("width", "530px");
             titleText.setAttribute("key", "note"+i+"Title");
             titleText.setAttribute("style", "notesSubjectText");
             titleText.appendChild(doc.createTextNode(subject));
             authorWidgetTag.setAttribute("halign", "right");
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
        model.setValue(fillAddressTable((DataModel)model.getValue(),addressList));
        return model;
    }
        
    private void setFieldsInRPC(Form form, ProviderDO provDO){
        form.setFieldValue(ProvMeta.getId(), provDO.getId());
        form.setFieldValue(ProvMeta.getLastName(),provDO.getLastName());
        form.setFieldValue(ProvMeta.getFirstName(),provDO.getFirstName());
        form.setFieldValue(ProvMeta.getNpi(),provDO.getNpi());        
        form.setFieldValue(ProvMeta.getMiddleName(),provDO.getMiddleName());              
        form.setFieldValue(ProvMeta.getTypeId(),new DataSet(new NumberObject(provDO.getTypeId())));         
    }
    
    private ProviderDO getProviderDOFromRPC(Form form){
     NumberField providerId = (NumberField) form.getField(ProvMeta.getId());   
     ProviderDO providerDO = new ProviderDO();
     //provider info        
     providerDO.setId((Integer)providerId.getValue());
     providerDO.setFirstName(((String)form.getFieldValue(ProvMeta.getFirstName())));
     providerDO.setLastName(((String)form.getFieldValue(ProvMeta.getLastName())));
     providerDO.setMiddleName(((String)form.getFieldValue(ProvMeta.getMiddleName())));
     providerDO.setNpi(((String)form.getFieldValue(ProvMeta.getNpi())));
     
     if(!new Integer(-1).equals(form.getFieldValue(ProvMeta.getTypeId())))
      providerDO.setTypeId((Integer)((DropDownField)form.getField(ProvMeta.getTypeId())).getSelectedKey());
     
     return providerDO;
    }
    
    
    private ArrayList<ProviderAddressDO> getProviderAddressListFromRPC(DataModel addressTable, Integer providerId){
        ArrayList<ProviderAddressDO> provAddDOList = new ArrayList<ProviderAddressDO>();
          
           for(int iter = 0; iter < addressTable.size(); iter++){            
            ProviderAddressDO provAddDO = new ProviderAddressDO();
            DataSet<Data> row = (DataSet)addressTable.get(iter);
            
            NumberField provAddId = (NumberField)((DataMap)row.getData()).get("provAddId");
            NumberField addId = (NumberField)((DataMap)row.getData()).get("addId");
            
            if(provAddId != null){
             provAddDO.setId((Integer)(provAddId).getValue());
            } 
            provAddDO.setLocation((String)((StringField)row.get(0)).getValue());
            provAddDO.setExternalId((String)((StringField)row.get(1)).getValue());
            provAddDO.setProvider((Integer)providerId);
           
 
            provAddDO.setDelete(false);

            //if the user created the row and clicked the remove button before commit...
            //we dont need to do anything with that row
            //if(provAddId == null){
                //do nothing
            //}else{
             if(addId != null){              
              provAddDO.getAddressDO().setId((Integer)addId.getValue());             
             }             
            //}
            provAddDO.getAddressDO().setMultipleUnit(((String)((StringField)row.get(2)).getValue()));
            provAddDO.getAddressDO().setStreetAddress(((String)((StringField)row.get(3)).getValue()));
            provAddDO.getAddressDO().setCity(((String)((StringField)row.get(4)).getValue()));
            provAddDO.getAddressDO().setState((String)((DropDownField)row.get(5)).getSelectedKey());
            provAddDO.getAddressDO().setCountry((String)((DropDownField)row.get(6)).getSelectedKey());
            provAddDO.getAddressDO().setZipCode(((String)((StringField)row.get(7)).getValue()));
            provAddDO.getAddressDO().setWorkPhone(((String)((StringField)row.get(8)).getValue()));
            provAddDO.getAddressDO().setHomePhone(((String)((StringField)row.get(9)).getValue()));
            provAddDO.getAddressDO().setCellPhone(((String)((StringField)row.get(10)).getValue()));
            provAddDO.getAddressDO().setFaxPhone(((String)((StringField)row.get(11)).getValue()));
            provAddDO.getAddressDO().setEmail(((String)((StringField)row.get(12)).getValue()));
            
            provAddDOList.add(provAddDO);                                    
        }
           for(int iter = 0; iter < addressTable.getDeletions().size(); iter++){            
               ProviderAddressDO provAddDO = new ProviderAddressDO();
               DataSet<Data> row = (DataSet)addressTable.getDeletions().get(iter);
               
               NumberField provAddId = null;
               NumberField addId = null;
               if(row.getData()!=null){
                provAddId = (NumberField)((DataMap)row.getData()).get("provAddId");
                addId = (NumberField)((DataMap)row.getData()).get("addId");
               }
               
               if(provAddId != null){
                provAddDO.setId((Integer)(provAddId).getValue());
               } 
               provAddDO.setLocation((String)((StringField)row.get(0)).getValue());
               provAddDO.setExternalId((String)((StringField)row.get(1)).getValue());
               provAddDO.setProvider((Integer)providerId);
              
    
               provAddDO.setDelete(true);

               //if the user created the row and clicked the remove button before commit...
               //we dont need to do anything with that row
               //if(provAddId == null){
                   //do nothing
               //}else{
                if(addId != null){              
                 provAddDO.getAddressDO().setId((Integer)addId.getValue());             
                }             
               //}
               
               provAddDO.getAddressDO().setMultipleUnit(((String)((StringField)row.get(2)).getValue()));
               provAddDO.getAddressDO().setStreetAddress(((String)((StringField)row.get(3)).getValue()));
               provAddDO.getAddressDO().setCity(((String)((StringField)row.get(4)).getValue()));
               
               if(!("").equals(row.get(5).getValue())){
                provAddDO.getAddressDO().setState((String)row.get(5).getValue());
               }
               if(!("").equals(row.get(6).getValue())){
                provAddDO.getAddressDO().setCountry((String)row.get(6).getValue());
               }
                           
               provAddDO.getAddressDO().setZipCode(((String)((StringField)row.get(7)).getValue()));
               provAddDO.getAddressDO().setWorkPhone(((String)((StringField)row.get(8)).getValue()));
               provAddDO.getAddressDO().setHomePhone(((String)((StringField)row.get(9)).getValue()));
               provAddDO.getAddressDO().setCellPhone(((String)((StringField)row.get(10)).getValue()));
               provAddDO.getAddressDO().setFaxPhone(((String)((StringField)row.get(11)).getValue()));
               provAddDO.getAddressDO().setEmail(((String)((StringField)row.get(12)).getValue()));
               
               provAddDOList.add(provAddDO);   
              }
               
       return provAddDOList;    
    }

    private void setRpcErrors(List exceptionList, Form form){
        TableField contactsTable = (TableField)((Form)form.getField("addresses")).getField("providerAddressTable");
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int index =  ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                String fieldName = ((TableFieldErrorException)exceptionList.get(i)).getFieldName();
                contactsTable.getField(index, fieldName)
                 .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        form.status = Form.Status.invalid;
    }
}
