/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
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
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.remote.SystemUserRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OrganizationService implements AppScreenFormServiceInt, 
															  AutoCompleteServiceInt {

	private static final int leftTableRowsPerPage = 20;
    
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List organizationNames;
    	//if the rpc is null then we need to get the page
    	if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("OrganizationQuery");
    
            if(rpc == null)
            	throw new RPCException(openElisConstants.getString("queryExpiredException"));

            OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
            try{
                organizationNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
            	if(e instanceof LastPageException){
            		throw new LastPageException(openElisConstants.getString("lastPageException"));
            	}else{
            		throw new RPCException(e.getMessage());	
            	}			
            }    
    	}else{
    	    OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	
    	    HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
    	    fields.remove("contactsTable");

    	    try{	
    	        organizationNames = remote.query(fields,0,leftTableRowsPerPage);
    
    	    }catch(Exception e){
    	        throw new RPCException(e.getMessage());
    	    }
    
        
    	    //need to save the rpc used to the encache
    	    SessionManager.getSession().setAttribute("OrganizationQuery", rpcSend);
    	}
        
        //fill the model with the query results
    	int i=0;
        model.clear();
        while(i < organizationNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)organizationNames.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());
            
            row.setKey(id);         
            row.addObject(name);
            model.add(row);
            i++;
        } 
 
    	return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
    		NoteDO organizationNote = new NoteDO();
    		
    		//build the organizationAddress DO from the form
    		//organization info
    		newOrganizationDO = getOrganizationDOFromRPC(rpcSend);
    		
    		//contacts info
            TableModel contactsTable = (TableModel)((FormRPC)rpcSend.getField("contacts")).getField("contactsTable").getValue();
            organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());

    		
            //build the noteDo from the form
            organizationNote.setSubject((String)((FormRPC)rpcSend.getField("notes")).getFieldValue(OrgMeta.NOTE.getSubject()));
            organizationNote.setText((String)((FormRPC)rpcSend.getField("notes")).getFieldValue(OrgMeta.NOTE.getText()));
            organizationNote.setIsExternal("Y");
    		
    		//validate the fields on the backend
            List exceptionList = remote.validateForAdd(newOrganizationDO, organizationContacts);
                
            if(exceptionList.size() > 0){
                setRpcErrors(exceptionList, contactsTable, rpcSend);
            } 
    		
    		//send the changes to the database
    		Integer orgId;
    		try{
    			orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    		}catch(Exception e){
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, contactsTable, rpcSend);
    			
    			return rpcSend;
    		}
    		
    		//lookup the changes from the database and build the rpc
    		newOrganizationDO.setOrganizationId(orgId);
    
    		//set the fields in the RPC
    		setFieldsInRPC(rpcReturn, newOrganizationDO);
    
    		//we need to refresh the notes tab if it is showing
            String tab = (String)rpcReturn.getFieldValue("orgTabPanel");
            if(tab.equals("notesTab")){
                DataSet key = new DataSet();
                NumberObject id = new NumberObject(NumberObject.Type.INTEGER, newOrganizationDO.getOrganizationId());
                key.setKey(id);
                
                loadNotes(key, (FormRPC)rpcReturn.getField("notes"));
            }
            
    		return rpcReturn;
    	}

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
    		List organizationContacts = new ArrayList();
    		NoteDO organizationNote = new NoteDO();
    
    		//build the organizationAddress DO from the form
    		newOrganizationDO = getOrganizationDOFromRPC(rpcSend);
    		
    		//contacts info
            TableModel contactsTable = (TableModel)((FormRPC)rpcSend.getField("contacts")).getField("contactsTable").getValue();;
            if(((FormRPC)rpcSend.getField("contacts")).load)
                organizationContacts = getOrgContactsListFromRPC(contactsTable, newOrganizationDO.getOrganizationId());
    		
    //		build the noteDo from the form
            if(((FormRPC)rpcSend.getField("notes")).load){
                FormRPC notesRPC = (FormRPC)rpcSend.getField("notes");                
                organizationNote.setSubject((String)notesRPC.getFieldValue(OrgMeta.NOTE.getSubject()));
                organizationNote.setText((String)notesRPC.getFieldValue(OrgMeta.NOTE.getText()));
                organizationNote.setIsExternal("Y");
            }
    		
    //		validate the fields on the backend
    		List exceptionList = remote.validateForUpdate(newOrganizationDO, organizationContacts);
    		if(exceptionList.size() > 0){
    			setRpcErrors(exceptionList, contactsTable, rpcSend);
    			
    			return rpcSend;
    		} 
    		
    //		send the changes to the database
    		try{
    			remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
    		}catch(Exception e){
                if(e instanceof EntityLockedException)
                    throw new RPCException(e.getMessage());
                
    			exceptionList = new ArrayList();
    			exceptionList.add(e);
    			
    			setRpcErrors(exceptionList, contactsTable, rpcSend);
    			
    			return rpcSend;
    		}

    		//set the fields in the RPC
    		setFieldsInRPC(rpcReturn, newOrganizationDO);	
    		
            //we need to refresh the notes tab if it is showing
            String tab = (String)rpcReturn.getFieldValue("orgTabPanel");
            if(tab.equals("notesTab")){
                DataSet key = new DataSet();
                NumberObject id = new NumberObject(NumberObject.Type.INTEGER, newOrganizationDO.getOrganizationId());
                key.setKey(id);
                
                loadNotes(key, (FormRPC)rpcReturn.getField("notes"));
            }
            
    		return rpcReturn;
    	}

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
    	return null;
    }

    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		
    		OrganizationAddressDO organizationDO = remote.getOrganizationAddressAndUnlock((Integer)key.getKey().getValue(), SessionManager.getSession().getId());
    
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, organizationDO);
            
            if(((FormRPC)rpcReturn.getField("contacts")).load){
                FormRPC contactsRPC = (FormRPC)rpcReturn.getField("contacts");
                getContactsModel((NumberObject)key.getKey(), (TableField)contactsRPC.getField("contactsTable"));
            }
            
            if(((FormRPC)rpcReturn.getField("notes")).load){
                FormRPC notesRpc = (FormRPC)rpcReturn.getField("notes");
                notesRpc.setFieldValue("notesPanel",getNotesModel((NumberObject)key.getKey()).getValue());
            }
    		
            return rpcReturn;  
    	}

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
		//remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
//		    System.out.println("in contacts");
        OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)key.getKey().getValue());

            //		set the fields in the RPC
        setFieldsInRPC(rpcReturn, organizationDO);
            
        String tab = (String)rpcReturn.getFieldValue("orgTabPanel");
        if(tab.equals("contactsTab")){
            loadContacts(key,(FormRPC)rpcReturn.getField("contacts"));
        }
       
        if(tab.equals("notesTab")){
            loadNotes(key, (FormRPC)rpcReturn.getField("notes"));
        }
        
        return rpcReturn;  
	}
    
    public FormRPC loadContacts(DataSet key, FormRPC rpcReturn) throws RPCException {
        getContactsModel((NumberObject)key.getKey(), (TableField)rpcReturn.getField("contactsTable"));
        rpcReturn.load = true;
        return rpcReturn;
    }
    
    public FormRPC loadNotes(DataSet key, FormRPC rpcReturn) throws RPCException {
        StringObject so = getNotesModel((NumberObject)key.getKey());
        rpcReturn.setFieldValue("notesPanel",so.getValue());
        rpcReturn.load = true;
        return rpcReturn;
    }
	
    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
    //		remote interface to call the organization bean
    		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    		
    		OrganizationAddressDO organizationDO = new OrganizationAddressDO();
    		try{
    			organizationDO = remote.getOrganizationAddressAndLock((Integer)key.getKey().getValue(), SessionManager.getSession().getId());
    		}catch(Exception e){
    			throw new RPCException(e.getMessage());
    		}
    		
    //		set the fields in the RPC
    		setFieldsInRPC(rpcReturn, organizationDO);
            
            String tab = (String)rpcReturn.getFieldValue("orgTabPanel");
            if(tab.equals("contactsTab")){
                loadContacts(key,(FormRPC)rpcReturn.getField("contacts"));
            }
           
            if(tab.equals("notesTab")){
                loadNotes(key, (FormRPC)rpcReturn.getField("notes"));
            }
            
    	    return rpcReturn;  
    	}

    public String getXML() throws RPCException {
    	return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
    }

    public HashMap getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl"));
        
        DataModel stateDropdownField = (DataModel)CachingManager.getElement("InitialData", "stateDropdown");
        DataModel countryDropdownField = (DataModel)CachingManager.getElement("InitialData", "countryDropdown");
        DataModel contactTypeDropdownField = (DataModel)CachingManager.getElement("InitialData", "contactTypeDropdown");
        
        //state dropdown
        if(stateDropdownField == null){
            stateDropdownField = getInitialModel("state");
            CachingManager.putElement("InitialData", "stateDropdown", stateDropdownField);
        }
        //country dropdown
        if(countryDropdownField == null){
            countryDropdownField = getInitialModel("country");
            CachingManager.putElement("InitialData", "countryDropdown", countryDropdownField);
            }
        //contact type dropdown
        if(contactTypeDropdownField == null){
            contactTypeDropdownField = getInitialModel("contactType");
            CachingManager.putElement("InitialData", "contactTypeDropdown", contactTypeDropdownField);
        }
        
        HashMap map = new HashMap();
        map.put("xml", xml);
        map.put("states", stateDropdownField);
        map.put("countries", countryDropdownField);
        map.put("contacts", contactTypeDropdownField);
        
        return map;
    }

    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public TableModel fillContactsTable(TableModel contactsModel, List contactsList){
    	try 
        {
    		contactsModel.reset();
    		
    		for(int iter = 0;iter < contactsList.size();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)contactsList.get(iter);
    
                   TableRow row = contactsModel.createRow();
                   NumberField id = new NumberField(contactRow.getId());
                   NumberField addId = new NumberField(contactRow.getAddressDO().getId());
                    row.addHidden("contactId", id);
                    row.addHidden("addId", addId);
                    row.getColumn(0).setValue(contactRow.getContactType());
                    row.getColumn(1).setValue(contactRow.getName());
                    row.getColumn(2).setValue(contactRow.getAddressDO().getMultipleUnit());
                    row.getColumn(3).setValue(contactRow.getAddressDO().getStreetAddress());
                    row.getColumn(4).setValue(contactRow.getAddressDO().getCity());          
                    row.getColumn(5).setValue(contactRow.getAddressDO().getState());
                    row.getColumn(6).setValue(contactRow.getAddressDO().getZipCode());
                    row.getColumn(7).setValue(contactRow.getAddressDO().getCountry());
                    row.getColumn(8).setValue(contactRow.getAddressDO().getWorkPhone());
                    row.getColumn(9).setValue(contactRow.getAddressDO().getHomePhone());
                    row.getColumn(10).setValue(contactRow.getAddressDO().getCellPhone());
                    row.getColumn(11).setValue(contactRow.getAddressDO().getFaxPhone());
                    row.getColumn(12).setValue(contactRow.getAddressDO().getEmail());	                
                    
                    contactsModel.addRow(row);
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
        	 mainRowPanel.setAttribute("width", "531px");
        	 
        	 topRowPanel.setAttribute("width", "531px");
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
        fillContactsTable((TableModel)model.getValue(),contactsList);

    }

    public DataModel getInitialModel(String cat){
    	Integer id = null;
    	CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    	
    	if(cat.equals("state"))
    		id = remote.getCategoryId("state");
    	else if(cat.equals("country"))
    		id = remote.getCategoryId("country");
    	else if(cat.equals("contactType"))
    		id = remote.getCategoryId("contact_type");
    	
    	List entries = new ArrayList();
    	if(id != null)
    		entries = remote.getDropdownValues(id);
    	
    	//we need to build the model to return
    	DataModel returnModel = new DataModel();
    	
        if(entries.size() > 0){	
        	//create a blank entry to begin the list
        	DataSet blankset = new DataSet();
        	
        	StringObject blankStringId = new StringObject("");
        	NumberObject blankNumberId = new NumberObject(0);
        	BooleanObject blankSelected = new BooleanObject();
            
        	blankset.addObject(blankStringId);
        	
        	
        	if(cat.equals("contactType"))
        		blankset.setKey(blankNumberId);
        	else
        		blankset.setKey(blankStringId);			
        	
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
    		StringObject stringId = new StringObject();
    		NumberObject numberId = new NumberObject(NumberObject.Type.INTEGER);
     	
    		textObject.setValue(dropdownText);
    		set.addObject(textObject);
    		
    		if(cat.equals("contactType")){
    			numberId.setValue(dropdownId);
    			set.setKey(numberId);
    		}else{
    			stringId.setValue(dropdownText);
    			set.setKey(stringId);			
    		}
    		
    		returnModel.add(set);
    		
    		i++;
    	}		
    	
    	return returnModel;
    }

    //autocomplete textbox method
    //match is what they typed
    public DataModel getMatches(String cat, DataModel model, String match, HashMap params) {
    	if(cat.equals("parentOrg"))
    		return getParentOrgMatches(match);
    	
    	return null;		
    }

    private void setFieldsInRPC(FormRPC rpcReturn, OrganizationAddressDO organizationDO){
		rpcReturn.setFieldValue(OrgMeta.getId(), organizationDO.getOrganizationId());
		rpcReturn.setFieldValue(OrgMeta.getName(),organizationDO.getName());
		rpcReturn.setFieldValue(OrgMeta.getIsActive(),organizationDO.getIsActive());
		rpcReturn.setFieldValue(OrgMeta.getAddressId(), organizationDO.getAddressDO().getId());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getStreetAddress(),organizationDO.getAddressDO().getStreetAddress());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getMultipleUnit(),organizationDO.getAddressDO().getMultipleUnit());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getCity(),organizationDO.getAddressDO().getCity());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getZipCode(),organizationDO.getAddressDO().getZipCode());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getState(),organizationDO.getAddressDO().getState());
		rpcReturn.setFieldValue(OrgMeta.ADDRESS.getCountry(),organizationDO.getAddressDO().getCountry());
		
		//we need to create a dataset for the parent organization auto complete
		if(organizationDO.getParentOrganizationId() == null)
			rpcReturn.setFieldValue(OrgMeta.PARENT_ORGANIZATION.getName(), null);
		else{
			DataSet parentOrgSet = new DataSet();
			NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
			StringObject text = new StringObject();
			id.setValue(organizationDO.getParentOrganizationId());
			text.setValue(organizationDO.getParentOrganization());
			parentOrgSet.setKey(id);
			parentOrgSet.addObject(text);
			rpcReturn.setFieldValue(OrgMeta.PARENT_ORGANIZATION.getName(), parentOrgSet);
		}
	}
	
	private OrganizationAddressDO getOrganizationDOFromRPC(FormRPC rpcSend){
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		
		newOrganizationDO.setOrganizationId((Integer) rpcSend.getFieldValue(OrgMeta.getId()));
		newOrganizationDO.setName((String) rpcSend.getFieldValue(OrgMeta.getName()));
		newOrganizationDO.setIsActive((String)rpcSend.getFieldValue(OrgMeta.getIsActive()));
		newOrganizationDO.setParentOrganizationId((Integer) rpcSend.getFieldValue(OrgMeta.PARENT_ORGANIZATION.getName()));		
		newOrganizationDO.setParentOrganization((String)((DropDownField)rpcSend.getField(OrgMeta.PARENT_ORGANIZATION.getName())).getTextValue());
		
		//organization address value
		newOrganizationDO.getAddressDO().setId((Integer) rpcSend.getFieldValue(OrgMeta.getAddressId()));
		newOrganizationDO.getAddressDO().setMultipleUnit((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getMultipleUnit()));
		newOrganizationDO.getAddressDO().setStreetAddress((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getStreetAddress()));
		newOrganizationDO.getAddressDO().setCity((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getCity()));
		newOrganizationDO.getAddressDO().setState((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getState()));
		newOrganizationDO.getAddressDO().setZipCode((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getZipCode()));
		newOrganizationDO.getAddressDO().setCountry((String)rpcSend.getFieldValue(OrgMeta.ADDRESS.getCountry()));
		
		return newOrganizationDO;
	}
	
	private List<OrganizationContactDO> getOrgContactsListFromRPC(TableModel contactsTable, Integer orgId){
		List<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
		
		for(int i=0; i<contactsTable.numRows(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			TableRow row = contactsTable.getRow(i);
			//contact data
			NumberField contactId = (NumberField)row.getHidden("contactId");
			NumberField addId = (NumberField)row.getHidden("addId");
			StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
			if(deleteFlag == null){
				contactDO.setDelete(false);
			}else{
				contactDO.setDelete("Y".equals(deleteFlag.getValue()));
			}
			//if the user created the row and clicked the remove button before commit...
			//we dont need to do anything with that row
			if(deleteFlag != null && "Y".equals(deleteFlag.getValue()) && contactId == null){
				//do nothing
			}else{
				if(contactId != null)
					contactDO.setId((Integer)contactId.getValue());
				contactDO.setOrganization(orgId);
				contactDO.setName((String)((StringField)row.getColumn(1)).getValue());
				//contact address data
				if(addId != null)
				contactDO.getAddressDO().setId((Integer)addId.getValue());
			    contactDO.setContactType((Integer)row.getColumn(0).getValue());
				contactDO.getAddressDO().setMultipleUnit((String)((StringField)row.getColumn(2)).getValue());
				contactDO.getAddressDO().setStreetAddress((String)((StringField)row.getColumn(3)).getValue());
				contactDO.getAddressDO().setCity((String)((StringField)row.getColumn(4)).getValue());
				contactDO.getAddressDO().setState((String)row.getColumn(5).getValue());
				contactDO.getAddressDO().setZipCode((String)((StringField)row.getColumn(6)).getValue());
				contactDO.getAddressDO().setCountry((String)row.getColumn(7).getValue());
				contactDO.getAddressDO().setWorkPhone((String)((StringField)row.getColumn(8)).getValue());
				contactDO.getAddressDO().setHomePhone((String)((StringField)row.getColumn(9)).getValue());
				contactDO.getAddressDO().setCellPhone((String)((StringField)row.getColumn(10)).getValue());
				contactDO.getAddressDO().setFaxPhone((String)((StringField)row.getColumn(11)).getValue());
				contactDO.getAddressDO().setEmail((String)((StringField)row.getColumn(12)).getValue());
				
				organizationContacts.add(contactDO);	
			}
		}
		
		return organizationContacts;
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
    	rpcSend.status = Status.invalid;
    }

    private DataModel getParentOrgMatches(String match){
    	OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    	DataModel dataModel = new DataModel();
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
            data.addObject(nameObject);
            StringObject addressObject = new StringObject();
            addressObject.setValue(address);
            data.addObject(addressObject);
            StringObject cityObject = new StringObject();
            cityObject.setValue(city);
            data.addObject(cityObject);
            StringObject stateObject = new StringObject();
            stateObject.setValue(state);
            data.addObject(stateObject);
            
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
    	
    	return dataModel;		
    }
}
