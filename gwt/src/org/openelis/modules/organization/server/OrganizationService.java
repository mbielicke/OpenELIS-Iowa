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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.organization.client.Contact;
import org.openelis.modules.organization.client.ContactRow;
import org.openelis.modules.organization.client.ContactsForm;
import org.openelis.modules.organization.client.ContactsRPC;
import org.openelis.modules.organization.client.NotesForm;
import org.openelis.modules.organization.client.NotesRPC;
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

public class OrganizationService implements AppScreenFormServiceInt<OrganizationRPC,Integer>, 
															  AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 18;
    
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
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
            model = new DataModel<Integer>();
        else
            model.clear();
        while(i < organizationNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)organizationNames.get(i); 
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
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
            TableField<Contact> contactsField = rpc.form.contacts.contacts;
            DataModel<Contact> contactsTable = contactsField.getValue();
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
                loadNotesForm(newOrganizationDO.getOrganizationId(), rpc.form.notes);
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
            TableField<Contact> contactsField = rpc.form.contacts.contacts;
            DataModel<Contact> contactsTable = contactsField.getValue();
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
                loadNotesForm(newOrganizationDO.getOrganizationId(), rpc.form.notes);
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
    		
    		
    		OrganizationAddressDO organizationDO = remote.getOrganizationAddressAndUnlock(rpc.key, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, organizationDO);
            
            if(rpc.form.contacts.load){
                getContactsModel(rpc.key, rpc.form.contacts.contacts);
            }
            
            if(rpc.form.notes.load){
                rpc.form.notes.notesPanel.setValue(getNotesModel(rpc.key));
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
		
        OrganizationAddressDO organizationDO = remote.getOrganizationAddress(rpc.key);

            //		set the fields in the RPC
        setFieldsInRPC(rpc.form, organizationDO);
            
        String tab = rpc.form.orgTabPanel;
        if(tab.equals("contactsTab")){
            loadContactsForm(rpc.key,rpc.form.contacts);
        }
       
        if(tab.equals("notesTab")){
            loadNotesForm(rpc.key, rpc.form.notes);
        }
        
        return rpc;  
	}
    
    public ContactsRPC loadContacts(ContactsRPC rpc) throws RPCException {
        loadContactsForm(rpc.key,rpc.form);
        return rpc;
    }
    
    public void loadContactsForm(Integer key, ContactsForm form) {
        getContactsModel(key, form.contacts);
        form.load = true;
    }
    
    public NotesRPC loadNotes(NotesRPC rpc) throws RPCException {
        loadNotesForm(rpc.key,rpc.form);
        return rpc;
    }
    
    public void loadNotesForm(Integer key, NotesForm form) {
        String so = getNotesModel(key);
        form.notesPanel.setValue(so);
        form.load = true;
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
    			organizationDO = remote.getOrganizationAddressAndLock(rpc.key, SessionManager.getSession().getId());
    		}catch(Exception e){
    			throw new RPCException(e.getMessage());
    		}
    		
    //		set the fields in the RPC
    		setFieldsInRPC(rpc.form, organizationDO);
            
            String tab = rpc.form.orgTabPanel;
            if(tab.equals("contactsTab")){
                loadContactsForm(rpc.key,rpc.form.contacts);
            }
           
            if(tab.equals("notesTab")){
                loadNotesForm(rpc.key, rpc.form.notes);
            }
            
    	    return rpc;  
    	}

    public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
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

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
    	return null;
    }


    
    public DataModel<Contact> fillContactsTable(DataModel<Contact> contactsModel, List contactsList){
    	try 
        {
    		contactsModel.clear();
    		
    		for(int iter = 0;iter < contactsList.size();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)contactsList.get(iter);
    
               //DataSet<Contact> row = contactsModel.createNewSet();
               ContactRow row = (ContactRow)contactsModel.createNewSet();
               Contact key = new Contact();
               key.orgId = contactRow.getId();
               key.addId = contactRow.getAddressDO().getId();
               row.setKey(key);
               
               row.contactType.setValue(new DataSet<Integer>(contactRow.getContactType()));
               row.name.setValue(contactRow.getName());
               row.multipleUnit.setValue(contactRow.getAddressDO().getMultipleUnit());
               row.streetAddress.setValue(contactRow.getAddressDO().getStreetAddress());
               row.city.setValue(contactRow.getAddressDO().getCity());          
               row.state.setValue(new DataSet<String>(contactRow.getAddressDO().getState()));
               row.zipCode.setValue(contactRow.getAddressDO().getZipCode());
               row.country.setValue(new DataSet<String>(contactRow.getAddressDO().getCountry()));
               row.workPhone.setValue(contactRow.getAddressDO().getWorkPhone());
               row.homePhone.setValue(contactRow.getAddressDO().getHomePhone());
               row.cellPhone.setValue(contactRow.getAddressDO().getCellPhone());
               row.faxPhone.setValue(contactRow.getAddressDO().getFaxPhone());
               row.email.setValue(contactRow.getAddressDO().getEmail());
               /*
               ((DropDownField<Integer>)row.get(0)).setValue(new DataSet<Integer>(contactRow.getContactType()));
               row.get(1).setValue(contactRow.getName());
               row.get(2).setValue(contactRow.getAddressDO().getMultipleUnit());
               row.get(3).setValue(contactRow.getAddressDO().getStreetAddress());
               row.get(4).setValue(contactRow.getAddressDO().getCity());          
               ((DropDownField<String>)row.get(5)).setValue(new DataSet<String>(contactRow.getAddressDO().getState()));
               row.get(6).setValue(contactRow.getAddressDO().getZipCode());
               ((DropDownField<String>)row.get(7)).setValue(new DataSet<String>(contactRow.getAddressDO().getCountry()));
               row.get(8).setValue(contactRow.getAddressDO().getWorkPhone());
               row.get(9).setValue(contactRow.getAddressDO().getHomePhone());
               row.get(10).setValue(contactRow.getAddressDO().getCellPhone());
               row.get(11).setValue(contactRow.getAddressDO().getFaxPhone());
               row.get(12).setValue(contactRow.getAddressDO().getEmail());
               */	                
               
               contactsModel.add(row);
           } 
    		
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }		
    	
    	return contactsModel;
    }

    public String getNotesModel(Integer key){
        //remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    
        //gets the whole notes list now
        List notesList = remote.getOrganizationNotes(key);
        
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
        
        return XMLUtil.toString(doc);
    	
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }
        return null;
    }

    public void getContactsModel(Integer orgId,TableField<Contact> model){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        List contactsList = remote.getOrganizationContacts(orgId);
        fillContactsTable((DataModel<Contact>)model.getValue(),contactsList);

    }

    //autocomplete textbox method
    //match is what they typed
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
    	if(cat.equals("parentOrg"))
    		return getParentOrgMatches(match);
    	
    	return null;		
    }

    private void setFieldsInRPC(OrganizationForm form, OrganizationAddressDO organizationDO){
		form.id.setValue(organizationDO.getOrganizationId().toString());
		form.name.setValue(organizationDO.getName());
		form.isActive.setValue(organizationDO.getIsActive());
		form.addressId = organizationDO.getAddressDO().getId();
		form.street.setValue(organizationDO.getAddressDO().getStreetAddress());
		form.multipleUnit.setValue(organizationDO.getAddressDO().getMultipleUnit());
		form.city.setValue(organizationDO.getAddressDO().getCity());
		form.zipCode.setValue(organizationDO.getAddressDO().getZipCode());
		form.state.setValue(new DataSet<String>(organizationDO.getAddressDO().getState()));
		form.country.setValue(new DataSet<String>(organizationDO.getAddressDO().getCountry()));
		
		//we need to create a dataset for the parent organization auto complete
		if(organizationDO.getParentOrganizationId() == null)
			form.parentOrg.clear();
		else{
            DataModel<Integer> model = new DataModel<Integer>();
            model.add(new DataSet<Integer>(organizationDO.getParentOrganizationId(),new StringObject(organizationDO.getParentOrganization())));
            form.parentOrg.setModel(model);
            form.parentOrg.setValue(model.get(0));
		}
	}
	
	private OrganizationAddressDO getOrganizationDOFromRPC(OrganizationForm form){
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		
		newOrganizationDO.setOrganizationId(form.id.getValue());
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
	
	private List<OrganizationContactDO> getOrgContactsListFromRPC(DataModel<Contact> contactsTable, Integer orgId){
		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
        List<DataSet<Contact>> deletedRows = contactsTable.getDeletions();
        
		for(int i=0; i<contactsTable.size(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			//DataSet<Contact> row = contactsTable.get(i);
            ContactRow row = (ContactRow)contactsTable.get(i);
			if(row.getKey() != null)
				contactDO.setId(row.getKey().orgId);
			contactDO.setOrganization(orgId);
            contactDO.setName(row.name.getValue());
            contactDO.getAddressDO().setId(row.getKey().addId);
            contactDO.setContactType((Integer)row.contactType.getSelectedKey());
            contactDO.getAddressDO().setMultipleUnit(row.multipleUnit.getValue());
            contactDO.getAddressDO().setStreetAddress(row.streetAddress.getValue());
            contactDO.getAddressDO().setCity(row.city.getValue());
            contactDO.getAddressDO().setState((String)row.state.getSelectedKey());
            contactDO.getAddressDO().setZipCode(row.zipCode.getValue());
            contactDO.getAddressDO().setCountry((String)row.country.getSelectedKey());
            contactDO.getAddressDO().setWorkPhone(row.workPhone.getValue());
            contactDO.getAddressDO().setHomePhone(row.homePhone.getValue());
            contactDO.getAddressDO().setCellPhone(row.cellPhone.getValue());
            contactDO.getAddressDO().setFaxPhone(row.faxPhone.getValue());
            contactDO.getAddressDO().setEmail(row.email.getValue());
            /*
			contactDO.setName((String)((StringField)row.get(1)).getValue());
			contactDO.getAddressDO().setId(row.getKey().addId);
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
			*/
			organizationContacts.add(contactDO);	
		}
        
        for(int j=0; j<deletedRows.size(); j++){
            DataSet<Contact> deletedRow = deletedRows.get(j);
            if(deletedRow.getKey() != null){
                OrganizationContactDO contactDO = new OrganizationContactDO();
                contactDO.setDelete(true);
                contactDO.setId(deletedRow.getKey().orgId);
                contactDO.getAddressDO().setId(deletedRow.getKey().addId);
                
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

    private DataModel<Integer> getParentOrgMatches(String match){
    	OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	DataModel<Integer> dataModel = new DataModel<Integer>();
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
            
            DataSet<Integer> data = new DataSet<Integer>();
            //hidden id
            data.setKey(orgId);
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
