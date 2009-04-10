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
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.organization.client.Contact;
import org.openelis.modules.organization.client.ContactsForm;
import org.openelis.modules.organization.client.NotesForm;
import org.openelis.modules.organization.client.OrganizationForm;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.ContactTypeCacheHandler;
import org.openelis.server.handlers.CountryCacheHandler;
import org.openelis.server.handlers.StatesCacheHandler;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OrganizationService implements AppScreenFormServiceInt<OrganizationForm,Query<TableDataRow<Integer>>>, 
															  AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 18;
    
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List organizationNames;
    	/*
        //if the rpc is null then we need to get the page
    	if(qList == null){

            qList = (ArrayList<AbstractField>)SessionManager.getSession().getAttribute("OrganizationQuery");
    
            if(qList == null)
            	throw new RPCException(openElisConstants.getString("queryExpiredException"));

            OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
            try{
                organizationNames = remote.query(qList, (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
            	if(e instanceof LastPageException){
            		throw new LastPageException(openElisConstants.getString("lastPageException"));
            	}else{
            		throw new RPCException(e.getMessage());	
            	}			
            }    
    	}else{*/
    	    OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	
    	    //HashMap<String,AbstractField> fields = ;
    	    //fields.remove("contactsTable");

    	    try{	
    	        organizationNames = remote.query(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
    	    }catch(Exception e){
    	        throw new RPCException(e.getMessage());
    	    }
    
        
    	    //need to save the rpc used to the encache
    	   // SessionManager.getSession().setAttribute("OrganizationQuery", qList);
    	//}
        
        //fill the model with the query results
    	int i=0;
        if(query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while(i < organizationNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)organizationNames.get(i); 
            query.results.add(new TableDataRow<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
    	return query;
    }

    public OrganizationForm commitAdd(OrganizationForm rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
    		NoteDO organizationNote = new NoteDO();
    		
    		//build the organizationAddress DO from the form
    		//organization info
    		newOrganizationDO = getOrganizationDOFromRPC(rpc);
    		
    		//contacts info
            TableField<TableDataRow<Contact>> contactsField = rpc.contacts.contacts;
            TableDataModel<TableDataRow<Contact>> contactsTable = contactsField.getValue();
            organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());

    		
            //build the noteDo from the form
            organizationNote.setSubject(rpc.notes.subject.getValue());
            organizationNote.setText(rpc.notes.text.getValue());
            organizationNote.setIsExternal("Y");
    		
    		//send the changes to the database
    		Integer orgId;
    		try{
    			orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), contactsField, rpc);
                    return rpc;
                }else
                    throw new RPCException(e.getMessage());
            }
    		
    		//lookup the changes from the database and build the rpc
    		newOrganizationDO.setOrganizationId(orgId);
    
    		//set the fields in the RPC
    		setFieldsInRPC(rpc, newOrganizationDO);
    
    		//we need to refresh the notes tab if it is showing
            String tab = rpc.orgTabPanel;
            if(tab.equals("notesTab")){
                loadNotesForm(newOrganizationDO.getOrganizationId(), rpc.notes);
            }
            
            //we need to clear out the note subject and the note text fields after a commit
            rpc.notes.subject.setValue(null);
            rpc.notes.text.setValue(null);
            
    		return rpc;
    	}

    public OrganizationForm commitUpdate(OrganizationForm rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List organizationContacts = new ArrayList();
    		NoteDO organizationNote = new NoteDO();
    
    		//build the organizationAddress DO from the form
    		newOrganizationDO = getOrganizationDOFromRPC(rpc);
    		
    		//contacts info
            TableField<TableDataRow<Contact>> contactsField = rpc.contacts.contacts;
            TableDataModel<TableDataRow<Contact>> contactsTable = contactsField.getValue();
            if(rpc.contacts.load)
                organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());
    		
    //		build the noteDo from the form
            if(rpc.notes.load){
                organizationNote.setSubject(rpc.notes.subject.getValue());
                organizationNote.setText(rpc.notes.text.getValue());
                organizationNote.setIsExternal("Y");
            }
    		
    		
    //		send the changes to the database
    		try{
    			remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    			
    		}catch(Exception e){
                if(e instanceof ValidationErrorsList){
                    setRpcErrors(((ValidationErrorsList)e).getErrorList(), contactsField, rpc);
                    return rpc;
                }else
                    throw new RPCException(e.getMessage());
            }

    		//set the fields in the RPC
    		setFieldsInRPC(rpc, newOrganizationDO);	
    		
            //we need to refresh the notes tab if it is showing
            String tab = rpc.orgTabPanel;
            if(tab.equals("notesTab")){
                loadNotesForm(newOrganizationDO.getOrganizationId(), rpc.notes);
            }
            
            //we need to set the notes load param to true because update doesnt call resetRPC
            rpc.notes.load = false;
            
            //we need to clear out the note subject and the note text fields after a commit
            rpc.notes.subject.setValue(null);
            rpc.notes.text.setValue(null);
            
    		return rpc;
    	}

    public OrganizationForm commitDelete(OrganizationForm rpcReturn) throws RPCException {
    	return null;
    }

    public OrganizationForm abort(OrganizationForm rpc) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		
    		OrganizationAddressDO organizationDO = remote.getOrganizationAddressAndUnlock(rpc.entityKey, SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, organizationDO);
            
            if(rpc.contacts.load){
                getContactsModel(rpc.entityKey, rpc.contacts.contacts);
            }
            
            if(rpc.notes.load){
                rpc.notes.notesPanel.setValue(getNotesModel(rpc.entityKey));
            }
    		
            return rpc;  
    	}

    public OrganizationForm fetch(OrganizationForm rpc) throws RPCException {
        /*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
		//remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
        OrganizationAddressDO organizationDO = remote.getOrganizationAddress(rpc.entityKey);

            //		set the fields in the RPC
        setFieldsInRPC(rpc, organizationDO);
            
        String tab = rpc.orgTabPanel;
        if(tab.equals("contactsTab")){
            loadContactsForm(rpc.entityKey,rpc.contacts);
        }
       
        if(tab.equals("notesTab")){
            loadNotesForm(rpc.entityKey, rpc.notes);
        }
        
        return rpc;  
	}
    
    public ContactsForm loadContacts(ContactsForm rpc) throws RPCException {
        loadContactsForm(rpc.entityKey,rpc);
        return rpc;
    }
    
    public void loadContactsForm(Integer key, ContactsForm form) {
        getContactsModel(key, form.contacts);
        form.load = true;
    }
    
    public NotesForm loadNotes(NotesForm rpc) throws RPCException {
        loadNotesForm(rpc.entityKey,rpc);
        return rpc;
    }
    
    public void loadNotesForm(Integer key, NotesForm form) {
        String so = getNotesModel(key);
        form.notesPanel.setValue(so);
        form.load = true;
    }
	
    public OrganizationForm fetchForUpdate(OrganizationForm rpc) throws RPCException {
            /*
             * Call checkModels to make screen has most recent versions of dropdowns
             */
            checkModels(rpc);
   
        //		remote interface to call the organization bean 
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		OrganizationAddressDO organizationDO = new OrganizationAddressDO();
    		try{
    			organizationDO = remote.getOrganizationAddressAndLock(rpc.entityKey, SessionManager.getSession().getId());
    		}catch(Exception e){
    			throw new RPCException(e.getMessage());
    		}
    		
    //		set the fields in the RPC
    		setFieldsInRPC(rpc, organizationDO);
            
            String tab = rpc.orgTabPanel;
            if(tab.equals("contactsTab")){
                loadContactsForm(rpc.entityKey,rpc.contacts);
            }
           
            if(tab.equals("notesTab")){
                loadNotesForm(rpc.entityKey, rpc.notes);
            }
            
    	    return rpc;  
    	}
    
    public OrganizationForm getScreen(OrganizationForm rpc) throws RPCException {
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
    
    public void checkModels(OrganizationForm rpc) {
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
    
    public TableDataModel<TableDataRow<Contact>> fillContactsTable(TableDataModel<TableDataRow<Contact>> contactsModel, List contactsList){
    	try 
        {
    		contactsModel.clear();
    		
    		for(int iter = 0;iter < contactsList.size();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)contactsList.get(iter);
    		
               TableDataRow<Contact> row = (TableDataRow<Contact>)contactsModel.createNewSet();
               Contact key = new Contact();
               key.orgId = contactRow.getId();
               key.addId = contactRow.getAddressDO().getId();
               row.key = key;
               /*
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
               */
               row.cells[0].setValue(new TableDataRow<Integer>(contactRow.getContactType()));
               row.cells[1].setValue(contactRow.getName());
               row.cells[2].setValue(contactRow.getAddressDO().getMultipleUnit());
               row.cells[3].setValue(contactRow.getAddressDO().getStreetAddress());
               row.cells[4].setValue(contactRow.getAddressDO().getCity());          
               row.cells[5].setValue(new TableDataRow<String>(contactRow.getAddressDO().getState()));
               row.cells[6].setValue(contactRow.getAddressDO().getZipCode());
               row.cells[7].setValue(new TableDataRow<String>(contactRow.getAddressDO().getCountry()));
               row.cells[8].setValue(contactRow.getAddressDO().getWorkPhone());
               row.cells[9].setValue(contactRow.getAddressDO().getHomePhone());
               row.cells[10].setValue(contactRow.getAddressDO().getCellPhone());
               row.cells[11].setValue(contactRow.getAddressDO().getFaxPhone());
               row.cells[12].setValue(contactRow.getAddressDO().getEmail());
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

    public void getContactsModel(Integer orgId,TableField<TableDataRow<Contact>> model){
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        List contactsList = remote.getOrganizationContacts(orgId);
        fillContactsTable((TableDataModel<TableDataRow<Contact>>)model.getValue(),contactsList);

    }

    //autocomplete textbox method
    //match is what they typed
    public TableDataModel getMatches(String cat, TableDataModel model, String match, HashMap<String,FieldType> params) {
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
		form.state.setValue(new TableDataRow<String>(organizationDO.getAddressDO().getState()));
		form.country.setValue(new TableDataRow<String>(organizationDO.getAddressDO().getCountry()));
		
		//we need to create a dataset for the parent organization auto complete
		if(organizationDO.getParentOrganizationId() == null){
			form.parentOrg.clear();
            form.parentOrg.setModel(null);
        }else{
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(new TableDataRow<Integer>(organizationDO.getParentOrganizationId(),new StringObject(organizationDO.getParentOrganization())));
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
	
	private List<OrganizationContactDO> getOrgContactsListFromRPC(TableDataModel<TableDataRow<Contact>> contactsTable, Integer orgId){
		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
        List<TableDataRow<Contact>> deletedRows = contactsTable.getDeletions();
        
		for(int i=0; i<contactsTable.size(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			//DataSet<Contact> row = contactsTable.get(i);
            TableDataRow<Contact> row = (TableDataRow<Contact>)contactsTable.get(i);
			if(row.key != null)
				contactDO.setId(row.key.orgId);
            /*
			contactDO.setOrganization(orgId);
            contactDO.setName(row.name.getValue());
            contactDO.getAddressDO().setId(row.key.addId);
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
            */
            contactDO.setOrganization(orgId);
            contactDO.setName((String)row.cells[1].getValue());
            contactDO.getAddressDO().setId(row.key.addId);
            contactDO.setContactType((Integer)((DropDownField<Integer>)row.cells[0]).getSelectedKey());
            contactDO.getAddressDO().setMultipleUnit((String)row.cells[2].getValue());
            contactDO.getAddressDO().setStreetAddress((String)row.cells[3].getValue());
            contactDO.getAddressDO().setCity((String)row.cells[4].getValue());
            contactDO.getAddressDO().setState((String)((DropDownField<String>)row.cells[5]).getSelectedKey());
            contactDO.getAddressDO().setZipCode((String)row.cells[6].getValue());
            contactDO.getAddressDO().setCountry((String)((DropDownField<String>)row.cells[7]).getSelectedKey());
            contactDO.getAddressDO().setWorkPhone((String)row.cells[8].getValue());
            contactDO.getAddressDO().setHomePhone((String)row.cells[9].getValue());
            contactDO.getAddressDO().setCellPhone((String)row.cells[10].getValue());
            contactDO.getAddressDO().setFaxPhone((String)row.cells[11].getValue());
            contactDO.getAddressDO().setEmail((String)row.cells[12].getValue());
			organizationContacts.add(contactDO);	
		}
        
		if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Contact> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    OrganizationContactDO contactDO = new OrganizationContactDO();
                    contactDO.setDelete(true);
                    contactDO.setId(deletedRow.key.orgId);
                    contactDO.getAddressDO().setId(deletedRow.key.addId);
                    
                    organizationContacts.add(contactDO);
                }
            }
		}
		
		return organizationContacts;
	}
	
	private void setRpcErrors(List exceptionList, TableField contactsTable, OrganizationForm form){
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
	    for (int i=0; i<exceptionList.size();i++) {
	        //if the error is inside the org contacts table
            if(exceptionList.get(i) instanceof TableFieldErrorException){
                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
                contactsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
                    .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));

            //if the error is on the field
            }else if(exceptionList.get(i) instanceof FieldErrorException)
                map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }        
	    
        form.status = Form.Status.invalid;
    }

    private TableDataModel<TableDataRow<Integer>> getParentOrgMatches(String match){
    	OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
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
            
            TableDataRow<Integer> data = new TableDataRow<Integer>(orgId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(address),
                                                                                    new StringObject(city),
                                                                                    new StringObject(state)
                                                                   }
                                         );
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
    	
    	return dataModel;		
    }
}
