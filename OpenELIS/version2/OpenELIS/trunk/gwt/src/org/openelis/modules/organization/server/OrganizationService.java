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
package org.openelis.modules.organization.server;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.organization.client.ContactsForm;
import org.openelis.modules.organization.client.NotesForm;
import org.openelis.modules.organization.client.OrganizationForm;
import org.openelis.modules.organization.client.OrganizationRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.ContactTypeCacheHandler;
import org.openelis.server.handlers.CountryCacheHandler;
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

public class OrganizationService implements AppScreenFormServiceInt<OrganizationRPC, DataModel<DataSet>>, 
															  AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 18;
    
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> model) throws RPCException {
        List organizationNames;
    	//if the rpc is null then we need to get the page
    	if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("OrganizationQuery");
    
            if(form == null)
            	throw new RPCException(openElisConstants.getString("queryExpiredException"));

            OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
            try{
                organizationNames = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
            	if(e instanceof LastPageException){
            		throw new LastPageException(openElisConstants.getString("lastPageException"));
            	}else{
            		throw new RPCException(e.getMessage());	
            	}			
            }    
    	}else{
    	    OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	
    	    HashMap<String,AbstractField> fields = form.getFieldMap();
    	    fields.remove("contactsTable");

    	    try{	
    	        organizationNames = remote.query(fields,0,leftTableRowsPerPage);
    
    	    }catch(Exception e){
    	        throw new RPCException(e.getMessage());
    	    }
    
        
    	    //need to save the rpc used to the encache
    	    SessionManager.getSession().setAttribute("OrganizationQuery", form);
    	}
        
        //fill the model with the query results
    	int i=0;
        if(model == null)
            model = new DataModel<DataSet>();
        else
            model.clear();
        while(i < organizationNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)organizationNames.get(i); 
            model.add(new DataSet<Data>(new NumberObject(resultDO.getId()),new StringObject(resultDO.getName())));
            i++;
        } 
    	return model;
    }

    public OrganizationRPC commitAdd(OrganizationRPC rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
    		NoteDO organizationNote = new NoteDO();
    		
    		//build the organizationAddress DO from the form
    		//organization info
    		newOrganizationDO = getOrganizationDOFromRPC(rpc.form);
    		
    		//contacts info
            TableField contactsField = rpc.form.contacts.contacts;
            DataModel contactsTable = contactsField.getValue();
            organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());

    		
            //build the noteDo from the form
            organizationNote.setSubject(rpc.form.notes.subject.getValue());
            organizationNote.setText(rpc.form.notes.text.getValue());
            organizationNote.setIsExternal("Y");
    		
    		//validate the fields on the backend
            List exceptionList = remote.validateForAdd(newOrganizationDO, organizationContacts);
                
            if(exceptionList.size() > 0){
                setRpcErrors(exceptionList, contactsField, rpc.form);
            } 
    		
    		//send the changes to the database
    		Integer orgId;
    		try{
    			orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    		}catch(Exception e){
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, contactsField, rpc.form);
    			
    			return rpc;
    		}
    		
    		//lookup the changes from the database and build the rpc
    		newOrganizationDO.setOrganizationId(orgId);
    
    		//set the fields in the RPC
    		setFieldsInRPC(rpc.form, newOrganizationDO);
    
    		//we need to refresh the notes tab if it is showing
            String tab = rpc.form.orgTabPanel;
            if(tab.equals("notesTab")){
                DataSet key = new DataSet();
                NumberObject id = new NumberObject(NumberObject.Type.INTEGER, newOrganizationDO.getOrganizationId());
                key.setKey(id);
                
                loadNotes(key, rpc.form.notes);
            }
            
            //we need to clear out the note subject and the note text fields after a commit
            rpc.form.notes.subject.setValue(null);
            rpc.form.notes.text.setValue(null);
            
    		return rpc;
    	}

    public OrganizationRPC commitUpdate(OrganizationRPC rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List organizationContacts = new ArrayList();
    		NoteDO organizationNote = new NoteDO();
    
    		//build the organizationAddress DO from the form
    		newOrganizationDO = getOrganizationDOFromRPC(rpc.form);
    		
    		//contacts info
            TableField contactsField = rpc.form.contacts.contacts;
            DataModel contactsTable = contactsField.getValue();
            if(rpc.form.contacts.load)
                organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());
    		
    //		build the noteDo from the form
            if(rpc.form.notes.load){
                organizationNote.setSubject(rpc.form.notes.subject.getValue());
                organizationNote.setText(rpc.form.notes.text.getValue());
                organizationNote.setIsExternal("Y");
            }
    		
    //		validate the fields on the backend
    		List exceptionList = remote.validateForUpdate(newOrganizationDO, organizationContacts);
    		if(exceptionList.size() > 0){
    			setRpcErrors(exceptionList, contactsField, rpc.form);
    			
    			return rpc;
    		} 
    		
    //		send the changes to the database
    		try{
    			remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    		}catch(Exception e){
                if(e instanceof EntityLockedException)
                    throw new RPCException(e.getMessage());
                
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, contactsField, rpc.form);
    			
    			return rpc;
    		}

    		//set the fields in the RPC
    		setFieldsInRPC(rpc.form, newOrganizationDO);	
    		
            //we need to refresh the notes tab if it is showing
            String tab = rpc.form.orgTabPanel;
            if(tab.equals("notesTab")){
                DataSet key = new DataSet();
                NumberObject id = new NumberObject(NumberObject.Type.INTEGER, newOrganizationDO.getOrganizationId());
                key.setKey(id);
                
                loadNotes(key, rpc.form.notes);
            }
            
            //we need to set the notes load param to true because update doesnt call resetRPC
            rpc.form.notes.load = false;
            
            //we need to clear out the note subject and the note text fields after a commit
            rpc.form.notes.subject.setValue(null);
            rpc.form.notes.text.setValue(null);
            
    		return rpc;
    	}

    public OrganizationRPC commitDelete(OrganizationRPC rpcReturn) throws RPCException {
    	return null;
    }

    public OrganizationRPC abort(OrganizationRPC rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		
    		OrganizationAddressDO organizationDO = remote.getOrganizationAddressAndUnlock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, organizationDO);
            
            if(rpc.form.contacts.load){
                getContactsModel((NumberObject)((DataSet)rpc.key).getKey(), rpc.form.contacts.contacts);
            }
            
            if(rpc.form.notes.load){
                rpc.form.notes.notesPanel.setValue(getNotesModel((NumberObject)((DataSet)rpc.key).getKey()).getValue());
            }
    		
            return rpc;  
    	}

    public OrganizationRPC fetch(OrganizationRPC rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
		//remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
//		    System.out.println("in contacts");
        OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue());

            //		set the fields in the RPC
        setFieldsInRPC(rpc.form, organizationDO);
            
        String tab = rpc.form.orgTabPanel;
        if(tab.equals("contactsTab")){
            loadContacts((DataSet)rpc.key,rpc.form.contacts);
        }
       
        if(tab.equals("notesTab")){
            loadNotes((DataSet)rpc.key, rpc.form.notes);
        }
        
        return rpc;  
	}
    
    public ContactsForm loadContacts(DataSet key, ContactsForm form) throws RPCException {
        getContactsModel((NumberObject)key.getKey(), form.contacts);
        form.load = true;
        return form;
    }
    
    public NotesForm loadNotes(DataSet key, NotesForm form) throws RPCException {
        StringObject so = getNotesModel((NumberObject)key.getKey());
        form.notesPanel.setValue(so.getValue());
        form.load = true;
        return form;
    }
	
    public OrganizationRPC fetchForUpdate(OrganizationRPC rpc) throws RPCException {
            /*
             * Call checkModels to make screen has most recent versions of dropdowns
             */
            checkModels(rpc);
   
        //		remote interface to call the organization bean 
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		OrganizationAddressDO organizationDO = new OrganizationAddressDO();
    		try{
    			organizationDO = remote.getOrganizationAddressAndLock((Integer)((DataObject)((DataSet)rpc.key).getKey()).getValue(), SessionManager.getSession().getId());
    		}catch(Exception e){
    			throw new RPCException(e.getMessage());
    		}
    		
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, organizationDO);
            
            String tab = rpc.form.orgTabPanel;
            if(tab.equals("contactsTab")){
                loadContacts((DataSet)rpc.key,rpc.form.contacts);
            }
           
            if(tab.equals("notesTab")){
                loadNotes((DataSet)rpc.key, rpc.form.notes);
            }
            
    	    return rpc;  
    	}

    public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl"));
        
        DataModel stateDropdownField = StatesCacheHandler.getStates();
        DataModel countryDropdownField = CountryCacheHandler.getCountries();
        DataModel contactTypeDropdownField = ContactTypeCacheHandler.getContactTypes();
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("states", stateDropdownField);
        map.put("countries", countryDropdownField);
        map.put("contacts", contactTypeDropdownField);
        
        return map;
    }
    
    public OrganizationRPC getScreen(OrganizationRPC rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.states = StatesCacheHandler.getStates();
        SessionManager.getSession().setAttribute("statesVersion",StatesCacheHandler.version);
        rpc.countries = CountryCacheHandler.getCountries();
        SessionManager.getSession().setAttribute("countriesVersion",CountryCacheHandler.version);
        rpc.contactTypes = ContactTypeCacheHandler.getContactTypes();
        SessionManager.getSession().setAttribute("contactTypesVersion",ContactTypeCacheHandler.version);
        return rpc;
    }
    
    public void checkModels(OrganizationRPC rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int states = (Integer)SessionManager.getSession().getAttribute("statesVersion");
        int countries = (Integer)SessionManager.getSession().getAttribute("countriesVersion");
        int contactTypes = (Integer)SessionManager.getSession().getAttribute("contactTypesVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(states != StatesCacheHandler.version){
            rpc.states = StatesCacheHandler.getStates();
            SessionManager.getSession().setAttribute("statesVersion", StatesCacheHandler.version);
        }
        if(countries != CountryCacheHandler.version){
            rpc.countries = CountryCacheHandler.getCountries();
            SessionManager.getSession().setAttribute("countriesVersion", CountryCacheHandler.version);
        }
        if(contactTypes != ContactTypeCacheHandler.version){
            rpc.contactTypes = ContactTypeCacheHandler.getContactTypes();
            SessionManager.getSession().setAttribute("contactTypesVersion", ContactTypeCacheHandler.version);
        }
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	return null;
    }

    public DataModel<DataSet> fillContactsTable(DataModel<DataSet> contactsModel, List contactsList){
    	try 
        {
    		contactsModel.clear();
    		
    		for(int iter = 0;iter < contactsList.size();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)contactsList.get(iter);
    
               DataSet<NumberObject> row = contactsModel.createNewSet();
               
               NumberObject id = new NumberObject(contactRow.getId());
               NumberObject addId = new NumberObject(contactRow.getAddressDO().getId());
               row.setData(addId);
               row.setKey(id);
               
               row.get(0).setValue(new DataSet(new NumberObject(contactRow.getContactType())));
               row.get(1).setValue(contactRow.getName());
               row.get(2).setValue(contactRow.getAddressDO().getMultipleUnit());
               row.get(3).setValue(contactRow.getAddressDO().getStreetAddress());
               row.get(4).setValue(contactRow.getAddressDO().getCity());          
               row.get(5).setValue(new DataSet(new StringObject(contactRow.getAddressDO().getState())));
               row.get(6).setValue(contactRow.getAddressDO().getZipCode());
               row.get(7).setValue(new DataSet(new StringObject(contactRow.getAddressDO().getCountry())));
               row.get(8).setValue(contactRow.getAddressDO().getWorkPhone());
               row.get(9).setValue(contactRow.getAddressDO().getHomePhone());
               row.get(10).setValue(contactRow.getAddressDO().getCellPhone());
               row.get(11).setValue(contactRow.getAddressDO().getFaxPhone());
               row.get(12).setValue(contactRow.getAddressDO().getEmail());	                
               
               contactsModel.add(row);
           } 
    		
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }		
    	
    	return contactsModel;
    }

    public StringObject getNotesModel(NumberObject key){
        //remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    
        //gets the whole notes list now
        List notesList = remote.getOrganizationNotes((Integer)key.getValue());
        
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
        	System.out.println(e.getMessage());
        }
        return null;
    }

    public void getContactsModel(NumberObject orgId,TableField model){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        List contactsList = remote.getOrganizationContacts((Integer)orgId.getValue());
        fillContactsTable((DataModel<DataSet>)model.getValue(),contactsList);

    }

    //autocomplete textbox method
    //match is what they typed
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
    	if(cat.equals("parentOrg"))
    		return getParentOrgMatches(match);
    	
    	return null;		
    }

    private void setFieldsInRPC(OrganizationForm form, OrganizationAddressDO organizationDO){
		form.id.setValue(organizationDO.getOrganizationId());
		form.name.setValue(organizationDO.getName());
		form.isActive.setValue(organizationDO.getIsActive());
		form.addressId = organizationDO.getAddressDO().getId();
		form.street.setValue(organizationDO.getAddressDO().getStreetAddress());
		form.multipleUnit.setValue(organizationDO.getAddressDO().getMultipleUnit());
		form.city.setValue(organizationDO.getAddressDO().getCity());
		form.zipCode.setValue(organizationDO.getAddressDO().getZipCode());
		form.state.setValue(new DataSet(new StringObject(organizationDO.getAddressDO().getState())));
		form.country.setValue(new DataSet(new StringObject(organizationDO.getAddressDO().getCountry())));
		
		//we need to create a dataset for the parent organization auto complete
		if(organizationDO.getParentOrganizationId() == null)
			form.parentOrg.setValue(null);
		else{
            DataModel<DataSet> model = new DataModel<DataSet>();
            model.add(new NumberObject(organizationDO.getParentOrganizationId()),new StringObject(organizationDO.getParentOrganization()));
            form.parentOrg.setModel(model);
            form.parentOrg.setValue(model.get(0));
		}
	}
	
	private OrganizationAddressDO getOrganizationDOFromRPC(OrganizationForm form){
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		
		newOrganizationDO.setOrganizationId(form.id.getIntegerValue());
		newOrganizationDO.setName(form.name.getValue());
		newOrganizationDO.setIsActive(form.isActive.getValue());
		newOrganizationDO.setParentOrganizationId((Integer)form.parentOrg.getSelectedKey());		
		newOrganizationDO.setParentOrganization((String)form.parentOrg.getTextValue());
		
		//organization address value
		newOrganizationDO.getAddressDO().setId(form.addressId);
		newOrganizationDO.getAddressDO().setMultipleUnit(form.multipleUnit.getValue());
		newOrganizationDO.getAddressDO().setStreetAddress(form.street.getValue());
		newOrganizationDO.getAddressDO().setCity(form.city.getValue());
		newOrganizationDO.getAddressDO().setState((String)form.state.getSelectedKey());
		newOrganizationDO.getAddressDO().setZipCode(form.zipCode.getValue());
		newOrganizationDO.getAddressDO().setCountry((String)form.country.getSelectedKey());
		
		return newOrganizationDO;
	}
	
	private List<OrganizationContactDO> getOrgContactsListFromRPC(DataModel<DataSet> contactsTable, Integer orgId){
		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
        List deletedRows = contactsTable.getDeletions();
        
		for(int i=0; i<contactsTable.size(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			DataSet row = contactsTable.get(i);
			//contact data
			NumberObject contactId = (NumberObject)row.getKey();
			NumberObject addId = (NumberObject)row.getData();
			//StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
            //contactsTable.getde
			//if(deleteFlag == null){
			//contactDO.setDelete(false);
			//}else{
			//	contactDO.setDelete("Y".equals(deleteFlag.getValue()));
			//}
            
			//if the user created the row and clicked the remove button before commit...
			//we dont need to do anything with that row
			//if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && contactId == null){
				//do nothing
			//}else{
			if(contactId != null)
				contactDO.setId((Integer)contactId.getValue());
			contactDO.setOrganization(orgId);
			contactDO.setName((String)((StringField)row.get(1)).getValue());
			//contact address data
			if(addId != null)
			contactDO.getAddressDO().setId((Integer)addId.getValue());
		    contactDO.setContactType((Integer)((DropDownField)row.get(0)).getSelectedKey());
			contactDO.getAddressDO().setMultipleUnit((String)((StringField)row.get(2)).getValue());
			contactDO.getAddressDO().setStreetAddress((String)((StringField)row.get(3)).getValue());
			contactDO.getAddressDO().setCity((String)((StringField)row.get(4)).getValue());
			contactDO.getAddressDO().setState((String)((DropDownField)row.get(5)).getSelectedKey());
			contactDO.getAddressDO().setZipCode((String)((StringField)row.get(6)).getValue());
			contactDO.getAddressDO().setCountry((String)((DropDownField)row.get(7)).getSelectedKey());
			contactDO.getAddressDO().setWorkPhone((String)((StringField)row.get(8)).getValue());
			contactDO.getAddressDO().setHomePhone((String)((StringField)row.get(9)).getValue());
			contactDO.getAddressDO().setCellPhone((String)((StringField)row.get(10)).getValue());
			contactDO.getAddressDO().setFaxPhone((String)((StringField)row.get(11)).getValue());
			contactDO.getAddressDO().setEmail((String)((StringField)row.get(12)).getValue());
			
			organizationContacts.add(contactDO);	
		}
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet deletedRow = (DataSet)deletedRows.get(j);
            if(deletedRow.getKey() != null){
                OrganizationContactDO contactDO = new OrganizationContactDO();
                contactDO.setDelete(true);
                contactDO.setId((Integer)((NumberObject)deletedRow.getKey()).getValue());
                contactDO.getAddressDO().setId((Integer)((NumberObject)deletedRow.getData()).getValue());
                
                organizationContacts.add(contactDO);
            }
        }
		
		return organizationContacts;
	}
	
	private void setRpcErrors(List exceptionList, TableField contactsTable, OrganizationForm form){
	    for (int i=0; i<exceptionList.size();i++) {
	        //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                contactsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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

    private DataModel<DataSet> getParentOrgMatches(String match){
    	OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	DataModel<DataSet> dataModel = new DataModel<DataSet>();
        List autoCompleteList;
    
    	try{
    		int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
    		//lookup by id...should only bring back 1 result
    		autoCompleteList = remote.autoCompleteLookupById(id);
    		
    	}catch(NumberFormatException e){
    		//it isnt an id
    		//lookup by name
    		autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
    	}
        
    	for(int i=0; i < autoCompleteList.size(); i++){
            OrganizationAutoDO resultDO = (OrganizationAutoDO) autoCompleteList.get(i);
            //org id
            Integer orgId = resultDO.getId();
            //org name
            String name = resultDO.getName();
            //org street address
            String address = resultDO.getAddress();
            //org city
            String city = resultDO.getCity();
            //org state
            String state = resultDO.getState();
            
            DataSet data = new DataSet();
            //hidden id
            NumberObject idObject = new NumberObject(orgId);
            data.setKey(idObject);
            //columns
            StringObject nameObject = new StringObject();
            nameObject.setValue(name);
            data.add(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.add(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.add(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.add(stateObject);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
    	
    	return dataModel;		
    }
}
