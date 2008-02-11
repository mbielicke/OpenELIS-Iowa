package org.openelis.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.dataEntry.client.organization.OrganizationServletInt;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.constants.UTFResource;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.remote.SystemUserRemote;

public class OrganizationServlet extends AppServlet implements AppScreenFormServiceInt, 
															  OrganizationServletInt,
															  AutoCompleteServiceInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945448239944359285L;
	private static final int leftTableRowsPerPage = 21;

	private TableModel model; 
	private String systemUserId = "";
	
	private UTFResource openElisConstants= UTFResource.getBundle("org.openelis.modules.main.client.constants.OpenELISConstants",
			new Locale(((SessionManager.getSession() == null  || (String)SessionManager.getSession().getAttribute("locale") == null) 
					? "en" : (String)SessionManager.getSession().getAttribute("locale"))));
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
	}

	public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		
		OrganizationAddressDO organizationDO = remote.getOrganizationAddressAndUnlock((Integer)key.getObject(0).getValue());

//		set the fields in the RPC
		rpcReturn.setFieldValue("orgId", (Integer)key.getObject(0).getValue());
		rpcReturn.setFieldValue("orgName",organizationDO.getName());
        rpcReturn.setFieldValue("streetAddress",organizationDO.getAddressDO().getStreetAddress());
        rpcReturn.setFieldValue("multUnit",organizationDO.getAddressDO().getMultipleUnit());
        rpcReturn.setFieldValue("city",organizationDO.getAddressDO().getCity());
        rpcReturn.setFieldValue("zipCode",organizationDO.getAddressDO().getZipCode());
        rpcReturn.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
        rpcReturn.setFieldValue("isActive",((organizationDO.getIsActive() != null && organizationDO.getIsActive().equals("Y")) ? true : false));
        rpcReturn.setFieldValue("stateId",organizationDO.getAddressDO().getState());
        rpcReturn.setFieldValue("countryId",organizationDO.getAddressDO().getCountry());
        rpcReturn.setFieldValue("addressId", organizationDO.getAddressDO().getId());
		
		//get the filled out DO object
		if(rpcReturn.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts((Integer)key.getObject(0).getValue());
	        //need to build the contacts table now...
	        rpcReturn.setFieldValue("contactsTable",fillContactsTable((TableModel)rpcReturn.getField("contactsTable").getValue(),contactsList));
	        
		}
        
      return rpcReturn;  
	}

	public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		List organizationContacts = new ArrayList();
		NoteDO organizationNote = new NoteDO();
		
		//build the organizationAddress DO from the form
		//organization info
		newOrganizationDO.setName((String) rpcSend.getFieldValue("orgName"));
		newOrganizationDO.setIsActive(((Boolean) rpcSend.getFieldValue("isActive")?"Y":"N"));
		newOrganizationDO.setParentOrganization((Integer)rpcSend.getFieldValue("parentOrgId"));
		//organization address value
		newOrganizationDO.getAddressDO().setMultipleUnit((String)rpcSend.getFieldValue("multUnit"));
		newOrganizationDO.getAddressDO().setStreetAddress((String)rpcSend.getFieldValue("streetAddress"));
		newOrganizationDO.getAddressDO().setCity((String)rpcSend.getFieldValue("city"));
		newOrganizationDO.getAddressDO().setState((String)rpcSend.getFieldValue("stateId"));
		newOrganizationDO.getAddressDO().setZipCode((String)rpcSend.getFieldValue("zipCode"));
		newOrganizationDO.getAddressDO().setCountry((String)rpcSend.getFieldValue("countryId"));
		
		//contacts info
		TableModel contactsTable = (TableModel)rpcSend.getField("contactsTable").getValue();
		for(int i=0; i<contactsTable.numRows(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			TableRow row = contactsTable.getRow(i);
			//contact data
			contactDO.setContactType((Integer)((NumberField)row.getColumn(0)).getValue());
			contactDO.setName(((StringField)row.getColumn(1)).toString());
			//contact address data
			contactDO.getAddressDO().setMultipleUnit(((StringField)row.getColumn(2)).toString());
			contactDO.getAddressDO().setStreetAddress(((StringField)row.getColumn(3)).toString());
			contactDO.getAddressDO().setCity(((StringField)row.getColumn(4)).toString());
			contactDO.getAddressDO().setState(((StringField)row.getColumn(5)).toString());
			contactDO.getAddressDO().setZipCode(((StringField)row.getColumn(6)).toString());
			contactDO.getAddressDO().setWorkPhone(((StringField)row.getColumn(7)).toString());
			contactDO.getAddressDO().setHomePhone(((StringField)row.getColumn(8)).toString());
			contactDO.getAddressDO().setCellPhone(((StringField)row.getColumn(9)).toString());
			contactDO.getAddressDO().setFaxPhone(((StringField)row.getColumn(10)).toString());
			contactDO.getAddressDO().setEmail(((StringField)row.getColumn(11)).toString());
			contactDO.getAddressDO().setCountry(((StringField)row.getColumn(12)).toString());
			
			organizationContacts.add(contactDO);
		}
		
		
		//build the noteDo from the form
		organizationNote.setSubject((String)rpcSend.getFieldValue("usersSubject"));
		organizationNote.setText((String)rpcSend.getFieldValue("usersNote"));
		organizationNote.setIsExternal("Y");
		
		//send the changes to the database
		Integer orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId);

//		set the fields in the RPC
		rpcReturn.setFieldValue("orgId", orgId);
		rpcReturn.setFieldValue("orgName",organizationDO.getName());
		rpcReturn.setFieldValue("streetAddress",organizationDO.getAddressDO().getStreetAddress());
		rpcReturn.setFieldValue("multUnit",organizationDO.getAddressDO().getMultipleUnit());
        rpcReturn.setFieldValue("city",organizationDO.getAddressDO().getCity());
        rpcReturn.setFieldValue("zipCode",organizationDO.getAddressDO().getZipCode());
        rpcReturn.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
        rpcReturn.setFieldValue("isActive",organizationDO.getIsActive());
//        rpc.setFieldValue("addType","");
        rpcReturn.setFieldValue("stateId",organizationDO.getAddressDO().getState());
        rpcReturn.setFieldValue("countryId",organizationDO.getAddressDO().getCountry());
        rpcReturn.setFieldValue("addressId", organizationDO.getAddressDO().getId());
		
		//get the filled out DO object
	//	if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts(orgId);
	        //need to build the contacts table now...
	        rpcReturn.setFieldValue("contactsTable",fillContactsTable((TableModel)rpcReturn.getField("contactsTable").getValue(),contactsList));
	        
	//	}
		
		return rpcReturn;
	}

	public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
		//if the rpc is null then we need to get the page
		if(rpcSend == null){
			//need to get the query rpc out of the cache
	        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", systemUserId+":Organization");

	        if(rpc == null)
	        	throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));

	        List organizations = null;
	        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
	        try{
	        	organizations = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
	        }catch(Exception e){
	        	if(e instanceof LastPageException){
	        		throw new LastPageException(openElisConstants.getString("lastPageException"));
	        	}else{
	        		throw new RPCException(e.getMessage());	
	        	}			
	        }

	        int i=0;
	        model.clear();
	        while(i < organizations.size() && i < leftTableRowsPerPage) {
	    	   	Object[] result = (Object[])organizations.get(i);
				//org id
				Integer idResult = (Integer)result[0];
				//org name
				String nameResult = (String)result[1];

				DataSet row = new DataSet();
				NumberObject id = new NumberObject();
				StringObject name = new StringObject();
				id.setType("integer");
				name.setValue(nameResult);
				id.setValue(idResult);
				
				row.addObject(id);			
				row.addObject(name);
				model.add(row);
				i++;
	         } 

	        return model;

		}else{
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		HashMap<String,AbstractField> fields = rpcSend.getFieldMap();

		//contacts table
		TableModel contactsTable = null;
		if(rpcSend.getField("contactsTable") != null)
			contactsTable = (TableModel)rpcSend.getField("contactsTable").getValue();		
		
		if(contactsTable != null){
			fields.put("contactType",(CollectionField)contactsTable.getRow(0).getColumn(0));
			fields.put("contactName",(QueryStringField)contactsTable.getRow(0).getColumn(1));
			fields.put("contactMultUnit",(QueryStringField)contactsTable.getRow(0).getColumn(2));
			fields.put("contactStreetAddress",(QueryStringField)contactsTable.getRow(0).getColumn(3));
			fields.put("contactCity",(QueryStringField)contactsTable.getRow(0).getColumn(4));
			fields.put("contactState",(CollectionField)contactsTable.getRow(0).getColumn(5));
			fields.put("contactZipCode",(QueryStringField)contactsTable.getRow(0).getColumn(6));
			fields.put("contactWorkPhone",(QueryStringField)contactsTable.getRow(0).getColumn(7));
			fields.put("contactHomePhone",(QueryStringField)contactsTable.getRow(0).getColumn(8));
			fields.put("contactCellPhone",(QueryStringField)contactsTable.getRow(0).getColumn(9));
			fields.put("contactFaxPhone",(QueryStringField)contactsTable.getRow(0).getColumn(10));
			fields.put("contactEmail",(QueryStringField)contactsTable.getRow(0).getColumn(11));
			fields.put("contactCountry",(CollectionField)contactsTable.getRow(0).getColumn(12));
		}

		List organizationNames = new ArrayList();
			try{
			organizationNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}

		Iterator itraaa = organizationNames.iterator();
		model=  new DataModel();
		while(itraaa.hasNext()){
			Object[] result = (Object[])itraaa.next();
			//org id
			Integer id = (Integer)result[0];
			//org name
			String name = (String)result[1];

			DataSet row = new DataSet();

			 NumberObject idField = new NumberObject();
			 idField.setType("integer");
			 StringObject nameField = new StringObject();
			 nameField.setValue(name);
      
			 idField.setValue(id);
			 row.addObject(idField);
			 row.addObject(nameField);

			 model.add(row);

		}
        
        //need to save the rpc used to the encache
        if(systemUserId.equals(""))
        	systemUserId = remote.getSystemUserId().toString();
        CachingManager.putElement("screenQueryRpc", systemUserId+":Organization", rpcSend);
		}
		return model;
	}

	public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		List organizationContacts = new ArrayList();
		NoteDO organizationNote = new NoteDO();

		//build the organizationAddress DO from the form
		Integer orgId = (Integer) rpcSend.getFieldValue("orgId");
		//organization info
		newOrganizationDO.setOrganizationId(orgId);
		newOrganizationDO.setName((String) rpcSend.getFieldValue("orgName"));
		newOrganizationDO.setIsActive(((Boolean) rpcSend.getFieldValue("isActive")?"Y":"N"));
		newOrganizationDO.setParentOrganization((Integer) rpcSend.getFieldValue("parentOrgId"));
		//organization address value
		newOrganizationDO.getAddressDO().setId((Integer) rpcSend.getFieldValue("addressId"));
		newOrganizationDO.getAddressDO().setMultipleUnit((String)rpcSend.getFieldValue("multUnit"));
		newOrganizationDO.getAddressDO().setStreetAddress((String)rpcSend.getFieldValue("streetAddress"));
		newOrganizationDO.getAddressDO().setCity((String)rpcSend.getFieldValue("city"));
		newOrganizationDO.getAddressDO().setState((String)rpcSend.getFieldValue("stateId"));
		newOrganizationDO.getAddressDO().setZipCode((String)rpcSend.getFieldValue("zipCode"));
		newOrganizationDO.getAddressDO().setCountry((String)rpcSend.getFieldValue("countryId"));
		
		//contacts info
		TableModel contactsTable = (TableModel)rpcSend.getField("contactsTable").getValue();
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
				contactDO.setContactType((Integer)((NumberField)row.getColumn(0)).getValue());
				contactDO.getAddressDO().setMultipleUnit((String)((StringField)row.getColumn(2)).getValue());
				contactDO.getAddressDO().setStreetAddress((String)((StringField)row.getColumn(3)).getValue());
				contactDO.getAddressDO().setCity((String)((StringField)row.getColumn(4)).getValue());
				contactDO.getAddressDO().setState((String)((StringField)row.getColumn(5)).getValue());
				contactDO.getAddressDO().setZipCode((String)((StringField)row.getColumn(6)).getValue());
				contactDO.getAddressDO().setWorkPhone((String)((StringField)row.getColumn(7)).getValue());
				contactDO.getAddressDO().setHomePhone((String)((StringField)row.getColumn(8)).getValue());
				contactDO.getAddressDO().setCellPhone((String)((StringField)row.getColumn(9)).getValue());
				contactDO.getAddressDO().setFaxPhone((String)((StringField)row.getColumn(10)).getValue());
				contactDO.getAddressDO().setEmail((String)((StringField)row.getColumn(11)).getValue());
				contactDO.getAddressDO().setCountry((String)((StringField)row.getColumn(12)).getValue());
				
				organizationContacts.add(contactDO);	
			}
		}
		
		
		//build the noteDo from the form
		organizationNote.setSubject((String)rpcSend.getFieldValue("usersSubject"));
		organizationNote.setText((String)rpcSend.getFieldValue("usersNote"));
		organizationNote.setIsExternal("Y");
		
		//send the changes to the database
		remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId);

//		set the fields in the RPC
		rpcReturn.setFieldValue("orgId", orgId);
		rpcReturn.setFieldValue("orgName",organizationDO.getName());
		rpcReturn.setFieldValue("streetAddress",organizationDO.getAddressDO().getStreetAddress());
		rpcReturn.setFieldValue("multUnit",organizationDO.getAddressDO().getMultipleUnit());
		rpcReturn.setFieldValue("city",organizationDO.getAddressDO().getCity());
		rpcReturn.setFieldValue("zipCode",organizationDO.getAddressDO().getZipCode());
		rpcReturn.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
		rpcReturn.setFieldValue("isActive",organizationDO.getIsActive());
		rpcReturn.setFieldValue("stateId",organizationDO.getAddressDO().getState());
		rpcReturn.setFieldValue("countryId",organizationDO.getAddressDO().getCountry());
		rpcReturn.setFieldValue("addressId", organizationDO.getAddressDO().getId());
		
		//get the filled out DO object
		//if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts(orgId);
	        //need to build the contacts table now...
	        rpcReturn.setFieldValue("contactsTable",fillContactsTable((TableModel)rpcReturn.getField("contactsTable").getValue(),contactsList));
	        
	        //load the notes
	        rpcReturn.setFieldValue("usersSubject", null);
	        rpcReturn.setFieldValue("usersNote", null);
	        
	        DataModel notesModel = getNotesModel(orgId);
	        rpcReturn.setFieldValue("notesModel", notesModel);
	        
		//}
		
		return rpcReturn;
	}

	public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
		//remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
//		System.out.println("in contacts");
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)key.getObject(0).getValue());

//		set the fields in the RPC
		rpcReturn.setFieldValue("orgId", organizationDO.getOrganizationId());
		rpcReturn.setFieldValue("orgName",organizationDO.getName());
		rpcReturn.setFieldValue("streetAddress",organizationDO.getAddressDO().getStreetAddress());
		rpcReturn.setFieldValue("multUnit",organizationDO.getAddressDO().getMultipleUnit());
		rpcReturn.setFieldValue("city",organizationDO.getAddressDO().getCity());
		rpcReturn.setFieldValue("zipCode",organizationDO.getAddressDO().getZipCode());
		rpcReturn.setFieldValue("parentOrgId", organizationDO.getParentOrganization());
		rpcReturn.setFieldValue("isActive",("Y".equals(organizationDO.getIsActive())));
		rpcReturn.setFieldValue("stateId",organizationDO.getAddressDO().getState());
		rpcReturn.setFieldValue("countryId",organizationDO.getAddressDO().getCountry());
		rpcReturn.setFieldValue("addressId", organizationDO.getAddressDO().getId());
		
        //load the contacts
        List contactsList = remote.getOrganizationContacts((Integer)key.getObject(0).getValue());
        //need to build the contacts table now...
        TableModel rmodel = (TableModel)fillContactsTable((TableModel)rpcReturn.getField("contactsTable").getValue(),contactsList);
        rpcReturn.setFieldValue("contactsTable",rmodel);

        //load the notes
	    DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
	    rpcReturn.setFieldValue("notesModel", notesModel);
        
      return rpcReturn;  
	}
	
	public DataModel getNotesModel(Integer key){
		//remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

		//gets the whole notes list now
		List notesList = remote.getOrganizationNotes(key);
		
		DataModel notesModel = new DataModel();
		Iterator itr = notesList.iterator();
		while(itr.hasNext()){
			Object[] result = (Object[])itr.next();
			//id
			Integer id = (Integer)result[0];
			//user id
			Integer userId = (Integer)result[1];
			//body
			String body = (String)result[2];
			//date
			Datetime date = new Datetime(Datetime.YEAR,Datetime.MINUTE,result[3]);
			//subject
			String subject = (String)result[4];
			
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
	
	public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		OrganizationAddressDO organizationDO = new OrganizationAddressDO();
		try{
			organizationDO = remote.getOrganizationAddressAndLock((Integer)key.getObject(0).getValue());
		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
//		set the fields in the RPC
		rpcReturn.setFieldValue("orgId", organizationDO.getOrganizationId());
		rpcReturn.setFieldValue("orgName",organizationDO.getName());
		rpcReturn.setFieldValue("streetAddress",organizationDO.getAddressDO().getStreetAddress());
		rpcReturn.setFieldValue("multUnit",organizationDO.getAddressDO().getMultipleUnit());
		rpcReturn.setFieldValue("city",organizationDO.getAddressDO().getCity());
		rpcReturn.setFieldValue("zipCode",organizationDO.getAddressDO().getZipCode());
		rpcReturn.setFieldValue("parentOrgId", organizationDO.getParentOrganization());
		rpcReturn.setFieldValue("isActive",("Y".equals(organizationDO.getIsActive())));
		rpcReturn.setFieldValue("stateId",organizationDO.getAddressDO().getState());
		rpcReturn.setFieldValue("countryId",organizationDO.getAddressDO().getCountry());
		rpcReturn.setFieldValue("addressId", organizationDO.getAddressDO().getId());
		
        //load the contacts
        List contactsList = remote.getOrganizationContacts((Integer)key.getObject(0).getValue());
        //need to build the contacts table now...
        TableModel rmodel = (TableModel)fillContactsTable((TableModel)rpcReturn.getField("contactsTable").getValue(),contactsList);
        rpcReturn.setFieldValue("contactsTable",rmodel);

        //load the notes
	    DataModel notesModel = getNotesModel((Integer)key.getObject(0).getValue());
	    rpcReturn.setFieldValue("notesModel", notesModel);
        
	    return rpcReturn;  
	}
	
	public TableModel fillContactsTable(TableModel contactsModel, List contactsList){
		try 
        {
			contactsModel.reset();
			
			for(int iter = 0;iter < contactsList.size();iter++) {
				OrganizationContactDO contactRow = (OrganizationContactDO)contactsList.get(iter);

	               TableRow row = contactsModel.createRow();
	               NumberField id = new NumberField();
	               id.setType("integer");
	               NumberField addId = new NumberField();
	               addId.setType("integer");
	                id.setValue(contactRow.getId());
	                addId.setValue(contactRow.getAddressDO().getId());
	                row.addHidden("contactId", id);
	                row.addHidden("addId", addId);
	                
	                row.getColumn(0).setValue(contactRow.getContactType());
	                row.getColumn(1).setValue(contactRow.getName());
	                row.getColumn(2).setValue(contactRow.getAddressDO().getMultipleUnit());
	                row.getColumn(3).setValue(contactRow.getAddressDO().getStreetAddress());
	                row.getColumn(4).setValue(contactRow.getAddressDO().getCity());
	                row.getColumn(5).setValue(contactRow.getAddressDO().getState());
	                row.getColumn(6).setValue(contactRow.getAddressDO().getZipCode());
	                row.getColumn(7).setValue(contactRow.getAddressDO().getWorkPhone());
	                row.getColumn(8).setValue(contactRow.getAddressDO().getHomePhone());
	                row.getColumn(9).setValue(contactRow.getAddressDO().getCellPhone());
	                row.getColumn(10).setValue(contactRow.getAddressDO().getFaxPhone());
	                row.getColumn(11).setValue(contactRow.getAddressDO().getEmail());
	                row.getColumn(12).setValue(contactRow.getAddressDO().getCountry());
	                
	                contactsModel.addRow(row);
	       } 
			
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }		
		
		return contactsModel;
	}

	public TableModel getOrganizationByLetter(String letter, TableModel returnModel, FormRPC letterRPC) {
		 OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		 this.model = returnModel;

		 letterRPC.setFieldValue("orgName",letter.toUpperCase()+"*");
		 try 
         {
			 List organizations = new ArrayList();
				try{
					organizations = remote.query(letterRPC.getFieldMap(),0,leftTableRowsPerPage);

			}catch(Exception e){
				throw new RPCException(e.getMessage());
			}
          
           returnModel.reset();
           
           	for(int i = 0;i < organizations.size();i++) {
        	   	Object[] result = (Object[])organizations.get(i);
				//org id
				Integer idResult = (Integer)result[0];
				//org name
				String nameResult = (String)result[1];

				TableRow row = new TableRow();
				NumberField id = new NumberField();
				StringField name = new StringField();
				id.setType("integer");
				name.setValue(nameResult);
				id.setValue(idResult);
				row.addHidden("id", id);
			
				row.addColumn(name);
				returnModel.addRow(row);
             } 
           
       } catch (Exception e) {
            e.printStackTrace();
           return null;
       }
		returnModel.paged = true;
		returnModel.rowsPerPage=leftTableRowsPerPage;
		returnModel.pageIndex=0;
		
//		need to save the rpc used to the encache
		if(systemUserId.equals(""))
        	systemUserId = remote.getSystemUserId().toString();
        CachingManager.putElement("screenQueryRpc", systemUserId+":Organization", letterRPC);
       return returnModel;		
	}

	public TableModel filter(int col, Filter[] filters, int index, int selected) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public Filter[] getFilter(int col) {
		// TODO Auto-generated method stub
		return null;
	}

	public TableModel getModel(TableModel model) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public TableModel saveModel(TableModel model) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public TableModel sort(int col, boolean down, int index, int selected) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataModel getInitialModel(String cat){
		int id = -1;
		CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
		
		if(cat.equals("state")){
			id = remote.getCategoryId("state");
		}else if(cat.equals("country")){
			id = remote.getCategoryId("country");
		}else if(cat.equals("contactType")){
			id = remote.getCategoryId("contact_type");
		}
		/*else if(cat.equals("contactState")){
			id = remote.getCategoryId("state");
		}else if(cat.equals("contactCountry")){
			id = remote.getCategoryId("country");			
		}*/
		List entries = new ArrayList();
		if(id > -1)
			entries = remote.getDropdownValues(id);
		
		//we need to build the model to return
		DataModel returnModel = new DataModel();
			
		//create a blank entry to begin the list
		DataSet blankset = new DataSet();
		
		StringObject blankStringId = new StringObject();
		NumberObject blankNumberId = new NumberObject();
		BooleanObject blankSelected = new BooleanObject();
		
		blankStringId.setValue("");
		blankset.addObject(blankStringId);
		
		blankNumberId.setType("integer");
		blankNumberId.setValue(new Integer(0));
		
		if(cat.equals("contactType"))
			blankset.addObject(blankNumberId);
		else
			blankset.addObject(blankStringId);			
		
		blankSelected.setValue(new Boolean(false));
		blankset.addObject(blankSelected);
		
		returnModel.add(blankset);
		int i=0;
		while(i < entries.size()){
			DataSet set = new DataSet();
			Object[] result = (Object[]) entries.get(i);
			//id
			Integer dropdownId = (Integer)result[0];
			//entry
			String dropdownText = (String)result[1];
			
			StringObject textObject = new StringObject();
			StringObject stringId = new StringObject();
			NumberObject numberId = new NumberObject();
			BooleanObject selected = new BooleanObject();
			
			textObject.setValue(dropdownText);
			set.addObject(textObject);
			
			if(cat.equals("contactType")){
				numberId.setType("integer");
				numberId.setValue(dropdownId);
				set.addObject(numberId);
			}else{
				stringId.setValue(dropdownText);
				set.addObject(stringId);			
			}
			
			selected.setValue(new Boolean(false));
			set.addObject(selected);
			
			returnModel.add(set);
			
			i++;
		}		
		
		return returnModel;
	}
	
	//autocomplete textbox method	
	public DataModel getDisplay(String cat, DataModel model, AbstractField value) {
		if(cat.equals("parentOrg"))
			return getParentOrgDisplay((Integer)value.getValue());
		
		return null;		
		
	}
	
	private DataModel getParentOrgDisplay(Integer value){
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		List autoCompleteList = remote.autoCompleteLookupById(value);
		
		Object[] result = (Object[]) autoCompleteList.get(0);
		//org id
		Integer orgId = (Integer)result[0];
		//org name
		String name = (String)result[1];
		
		DataModel model = new DataModel();
		DataSet data = new DataSet();
		
		NumberObject id = new NumberObject();
		id.setType("integer");
		id.setValue(orgId);
		StringObject nameObject = new StringObject();
		nameObject.setValue(name.trim());
		
		data.addObject(id);
		data.addObject(nameObject);
		
		model.add(data);

		return model;		
	}
	
	//autocomplete textbox method
	//match is what they typed
	public DataModel getMatches(String cat, DataModel model, String match) {
		if(cat.equals("parentOrg"))
			return getParentOrgMatches(match);
		
		return null;		
	}
	
	private DataModel getParentOrgMatches(String match){
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		DataModel dataModel = new DataModel();

		try{
			int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
			//lookup by id...should only bring back 1 result
			List autoCompleteList = remote.autoCompleteLookupById(id);
			if(autoCompleteList.size() > 0){
				Object[] result = (Object[]) autoCompleteList.get(0);
				//org id
				Integer orgId = (Integer)result[0];
				//org name
				String name = (String)result[1];
				//org street address
				String address = (String)result[2];
				//org city
				String city = (String)result[3];
				//org state
				String state = (String)result[4];
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(orgId);
				data.addObject(idObject);
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
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(name);
				data.addObject(displayObject);
				//selected flag
				StringObject selectedFlag = new StringObject();
				selectedFlag.setValue("N");
				data.addObject(selectedFlag);
				
				//add the dataset to the datamodel
				dataModel.add(data);
								
			}		
			
		}catch(NumberFormatException e){
			//it isnt an id
			//lookup by name
			List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
			Iterator itr = autoCompleteList.iterator();
			
			while(itr.hasNext()){
				Object[] result = (Object[]) itr.next();
				//org id
				Integer orgId = (Integer)result[0];
				//org name
				String name = (String)result[1];
				//org street address
				String address = (String)result[2];
				//org city
				String city = (String)result[3];
				//org state
				String state = (String)result[4];				
				
				DataSet data = new DataSet();
				//hidden id
				NumberObject idObject = new NumberObject();
				idObject.setType("integer");
				idObject.setValue(orgId);
				data.addObject(idObject);
				//columns
				StringObject nameObject = new StringObject();
				nameObject.setValue(name.trim());
				data.addObject(nameObject);
				StringObject addressObject = new StringObject();
				addressObject.setValue(address.trim());
				data.addObject(addressObject);
				StringObject cityObject = new StringObject();
				cityObject.setValue(city.trim());
				data.addObject(cityObject);
				StringObject stateObject = new StringObject();
				stateObject.setValue(state.trim());
				data.addObject(stateObject);
				//display text
				StringObject displayObject = new StringObject();
				displayObject.setValue(name.trim());
				data.addObject(displayObject);
				//selected flag
				StringObject selectedFlag = new StringObject();
				selectedFlag.setValue("N");
				data.addObject(selectedFlag);
				
				//add the dataset to the datamodel
				dataModel.add(data);

			}
		}
		
		return dataModel;		
	}
	
	//???
	public String getTip(AbstractField key) throws RPCException {
		return null;
	}
}
