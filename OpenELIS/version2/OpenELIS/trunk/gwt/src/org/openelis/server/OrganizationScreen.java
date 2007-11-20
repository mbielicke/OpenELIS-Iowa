package org.openelis.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.client.dataEntry.screen.organization.OrganizationScreenInt;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationTableRowDO;
import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.client.services.AutoCompleteServiceInt;
import org.openelis.gwt.client.services.TableServiceInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.client.widget.pagedtree.TreeModelItem;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.NumberField;
import org.openelis.gwt.common.OptionField;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableModel;
import org.openelis.gwt.common.TableRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.user.client.Window;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.remote.SystemUserRemote;

public class OrganizationScreen extends AppServlet implements AppScreenFormServiceInt, 
															  OrganizationScreenInt, 
															  TableServiceInt, 
															  AutoCompleteServiceInt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945448239944359285L;
	private static final int leftTableRowsPerPage = 16;

	private TableModel model; 
	private boolean queryByLetter = false;
	private String letter = "";
	
	public String getXML() throws RPCException {
		return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organization.xsl");
	}

	public FormRPC abort(FormRPC rpc, AbstractField key) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)key.getValue(),true);

//		set the fields in the RPC
		rpc.setFieldValue("orgId", (Integer)key.getValue());
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganizationName());
        rpc.setFieldValue("isActive",((organizationDO.getIsActive() != null && organizationDO.getIsActive().equals("Y")) ? true : false));
     //   rpc.setFieldValue("addType","");
        rpc.setFieldValue("state",organizationDO.getState());
        rpc.setFieldValue("country",organizationDO.getCountry());
        rpc.setFieldValue("addressId", organizationDO.getAddressId());
		
		//get the filled out DO object
		if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts((Integer)key.getValue());
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
		}
        
      return rpc;  
	}

	public FormRPC commitAdd(FormRPC rpc) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		List organizationContacts = new ArrayList();
		NoteDO organizationNote = new NoteDO();
		
		//build the organizationAddress DO from the form
		//organization info
		newOrganizationDO.setName((String) rpc.getFieldValue("orgName"));
		newOrganizationDO.setIsActive(((Boolean) rpc.getFieldValue("isActive")?"Y":"N"));
		//newOrganizationDO.setParentOrganization(parentOrganization);
		//organization addres value
		//newOrganizationDO.setType(type); FIXME need to eventually set this type to business (or org)
		newOrganizationDO.setMultipleUnit((String)rpc.getFieldValue("multUnit"));
		newOrganizationDO.setStreetAddress((String)rpc.getFieldValue("streetAddress"));
		newOrganizationDO.setCity((String)rpc.getFieldValue("city"));
		newOrganizationDO.setState((String)rpc.getFieldValue("state"));
		newOrganizationDO.setZipCode((String)rpc.getFieldValue("zipCode"));
		//newOrganizationDO.setWorkPhone(workPhone); not used for organization 
		//newOrganizationDO.setHomePhone(homePhone); not used for organization
		//newOrganizationDO.setFaxPhone(faxPhone);  not used for organization
		//newOrganizationDO.setEmail(email);  not used for organization
		newOrganizationDO.setCountry((String)rpc.getFieldValue("country"));
		
		//contacts info
		TableModel contactsTable = (TableModel)rpc.getField("contactsTable").getValue();
		for(int i=0; i<contactsTable.numRows()-1; i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			TableRow row = contactsTable.getRow(i);
			//contact data
			contactDO.setContactType(1); //FIXME need to find out what value goes in here
			contactDO.setName(((StringField)row.getColumn(0)).toString());
			//contact address data
			//contactDO.setReferenceId(referenceId); //set inside the update bean
			//contactDO.setReferenceTable(referenceTable); //set inside the update bean
			contactDO.setType(1); //FIXME need to find out what value goes in here
			contactDO.setMultipleUnit(((StringField)row.getColumn(1)).toString());
			contactDO.setStreetAddress(((StringField)row.getColumn(2)).toString());
			contactDO.setCity(((StringField)row.getColumn(3)).toString());
			contactDO.setState(((StringField)row.getColumn(4)).toString());
			contactDO.setZipCode(((StringField)row.getColumn(5)).toString());
			contactDO.setWorkPhone(((StringField)row.getColumn(6)).toString());
			contactDO.setHomePhone(((StringField)row.getColumn(7)).toString());
			contactDO.setCellPhone(((StringField)row.getColumn(8)).toString());
			contactDO.setFaxPhone(((StringField)row.getColumn(9)).toString());
			contactDO.setEmail(((StringField)row.getColumn(10)).toString());
			contactDO.setCountry(((StringField)row.getColumn(11)).toString());
			
			organizationContacts.add(contactDO);
		}
		
		
		//build the noteDo from the form
		organizationNote.setSubject((String)rpc.getFieldValue("usersSubject"));
		organizationNote.setText((String)rpc.getFieldValue("usersNote"));
		organizationNote.setIsExternal("Y");
		//organizationNote.setReferenceId(referenceId); handled in the update bean
		//organizationNote.setSystemUser(systemUser);   handled in the update bean
		//organizationNote.setTimestamp(new Date());    handled in the update bean		
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$BEFORE UPDATE");
		//send the changes to the database
		Integer orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$AFTER UPDATE");
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId,false);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$AFTER LOOKUP CHANGES");

//		set the fields in the RPC
		rpc.setFieldValue("orgId", orgId);
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganizationName());
        rpc.setFieldValue("isActive",organizationDO.getIsActive());
//        rpc.setFieldValue("addType","");
        rpc.setFieldValue("state",organizationDO.getState());
        rpc.setFieldValue("country",organizationDO.getCountry());
        rpc.setFieldValue("addressId", organizationDO.getAddressId());
		
		//get the filled out DO object
	//	if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts(orgId);
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
	//	}
		
		return rpc;
	}

	public FormRPC commitQuery(FormRPC rpc) throws RPCException {
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		HashMap<String,AbstractField> fields = new HashMap<String, AbstractField>();
		//org form
		fields.put("orgId",rpc.getField("orgId"));
		fields.put("orgName",rpc.getField("orgName"));
		fields.put("streetAddress",rpc.getField("streetAddress"));
		fields.put("multUnit",rpc.getField("multUnit"));
		fields.put("city",rpc.getField("city"));
		fields.put("state", rpc.getField("state"));
		fields.put("zipCode",rpc.getField("zipCode"));
		fields.put("country", rpc.getField("country"));
		
		fields.put("parentOrg",rpc.getField("parentOrg"));
		fields.put("isActive", rpc.getField("isActive"));
		
		//contacts table
		
		//note 
		fields.put("usersSubject",rpc.getField("usersSubject"));
		fields.put("usersNote",rpc.getField("usersNote"));
		
        //List organizationNames = remote.query();
		return null;
	}

	public FormRPC commitUpdate(FormRPC rpc) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		OrganizationAddressDO newOrganizationDO = new OrganizationAddressDO();
		List organizationContacts = new ArrayList();
		NoteDO organizationNote = new NoteDO();
		
		//build the organizationAddress DO from the form
		Integer orgId = (Integer) rpc.getFieldValue("orgId");
		//organization info
		newOrganizationDO.setOrganizationId(orgId);
		newOrganizationDO.setName((String) rpc.getFieldValue("orgName"));
		newOrganizationDO.setIsActive(((Boolean) rpc.getFieldValue("isActive")?"Y":"N"));
		//newOrganizationDO.setParentOrganization(parentOrganization);
		//organization addres value
		newOrganizationDO.setAddressId((Integer) rpc.getFieldValue("addressId"));
		//newOrganizationDO.setType(type); FIXME need to eventually set this type to business (or org)
		newOrganizationDO.setMultipleUnit((String)rpc.getFieldValue("multUnit"));
		newOrganizationDO.setStreetAddress((String)rpc.getFieldValue("streetAddress"));
		newOrganizationDO.setCity((String)rpc.getFieldValue("city"));
		newOrganizationDO.setState((String)rpc.getFieldValue("state"));
		newOrganizationDO.setZipCode((String)rpc.getFieldValue("zipCode"));
		//newOrganizationDO.setWorkPhone(workPhone); not used for organization 
		//newOrganizationDO.setHomePhone(homePhone); not used for organization
		//newOrganizationDO.setFaxPhone(faxPhone);  not used for organization
		//newOrganizationDO.setEmail(email);  not used for organization
		newOrganizationDO.setCountry((String)rpc.getFieldValue("country"));
		
		//contacts info
		TableModel contactsTable = (TableModel)rpc.getField("contactsTable").getValue();
		for(int i=0; i<contactsTable.numRows()-1; i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			TableRow row = contactsTable.getRow(i);
			//contact data
			NumberField contactId = (NumberField)row.getHidden("contactId");
			NumberField addId = (NumberField)row.getHidden("addId");
			if(contactId != null)
				contactDO.setId((Integer)contactId.getValue());
			contactDO.setOrganization(orgId);
			contactDO.setContactType(1); //FIXME need to find out what value goes in here
			contactDO.setName(((StringField)row.getColumn(0)).toString());
			//contact address data
			if(addId != null)
				contactDO.setAddressId((Integer)addId.getValue()); 
			//contactDO.setReferenceId(referenceId); //set inside the update bean
			//contactDO.setReferenceTable(referenceTable); //set inside the update bean
			contactDO.setType(1); //FIXME need to find out what value goes in here
			contactDO.setMultipleUnit(((StringField)row.getColumn(1)).toString());
			contactDO.setStreetAddress(((StringField)row.getColumn(2)).toString());
			contactDO.setCity(((StringField)row.getColumn(3)).toString());
			contactDO.setState(((StringField)row.getColumn(4)).toString());
			contactDO.setZipCode(((StringField)row.getColumn(5)).toString());
			contactDO.setWorkPhone(((StringField)row.getColumn(6)).toString());
			contactDO.setHomePhone(((StringField)row.getColumn(7)).toString());
			contactDO.setCellPhone(((StringField)row.getColumn(8)).toString());
			contactDO.setFaxPhone(((StringField)row.getColumn(9)).toString());
			contactDO.setEmail(((StringField)row.getColumn(10)).toString());
			contactDO.setCountry(((StringField)row.getColumn(11)).toString());
			
			organizationContacts.add(contactDO);
		}
		
		
		//build the noteDo from the form
		organizationNote.setSubject((String)rpc.getFieldValue("usersSubject"));
		organizationNote.setText((String)rpc.getFieldValue("usersNote"));
		organizationNote.setIsExternal("Y");
		//organizationNote.setReferenceId(referenceId); handled in the update bean
		//organizationNote.setSystemUser(systemUser);   handled in the update bean
		//organizationNote.setTimestamp(new Date());    handled in the update bean		
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$BEFORE UPDATE");
		//send the changes to the database
		remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$AFTER UPDATE");
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId,false);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$AFTER LOOKUP CHANGES");

//		set the fields in the RPC
		rpc.setFieldValue("orgId", orgId);
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganizationName());
        rpc.setFieldValue("isActive",organizationDO.getIsActive());
//        rpc.setFieldValue("addType","");
        rpc.setFieldValue("state",organizationDO.getState());
        rpc.setFieldValue("country",organizationDO.getCountry());
        rpc.setFieldValue("addressId", organizationDO.getAddressId());
		
		//get the filled out DO object
		if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts(orgId);
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
		}
		
		return rpc;
	}

	public FormRPC delete(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(FormRPC rpc, AbstractField key) throws RPCException {
		//remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
//		System.out.println("in contacts");
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)key.getValue(),false);
//		set the fields in the RPC
		rpc.setFieldValue("orgId", (Integer)key.getValue());
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganizationName());
        rpc.setFieldValue("isActive",("Y".equals(organizationDO.getIsActive())));
//        rpc.setFieldValue("addType","");
        rpc.setFieldValue("state",organizationDO.getState());
        rpc.setFieldValue("country",organizationDO.getCountry());
        rpc.setFieldValue("addressId", organizationDO.getAddressId());
		
		//get the filled out DO object
		if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts((Integer)key.getValue());
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
		}
        
      return rpc;  
	}
	
	public TreeModel getNoteTreeModel(AbstractField key, boolean topLevel){
		//remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

		List notesList = remote.getOrganizationNotes((Integer)key.getValue(),topLevel);
		
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
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

		List notesList = remote.getOrganizationNotes(Integer.valueOf(key),topLevel);
		
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

	public FormRPC fetchForUpdate(FormRPC rpc, AbstractField key) throws RPCException {
//		remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		OrganizationAddressDO organizationDO = null;
		try {
			organizationDO = remote.getOrganizationAddressUpdate((Integer)key.getValue());
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}
//		set the fields in the RPC
		rpc.setFieldValue("orgId", (Integer)key.getValue());
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganizationName());
        rpc.setFieldValue("isActive",((organizationDO.getIsActive() != null && organizationDO.getIsActive().equals("Y")) ? true : false));
      //  rpc.setFieldValue("addType","");
        rpc.setFieldValue("state",organizationDO.getState());
        rpc.setFieldValue("country",organizationDO.getCountry());
        rpc.setFieldValue("addressId", organizationDO.getAddressId());
		
		//get the filled out DO object
		if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts((Integer)key.getValue());
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
		}
        
      return rpc;  
	}

	public TableModel getInitialModel(TableModel model) {
		TableModel returnModel = model;
		this.model = model;
        try 
          {
            OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
          //  System.out.println("remote: "+remote);
            returnModel.paged = true;
            returnModel.rowsPerPage = leftTableRowsPerPage;
            returnModel.pageIndex = 0;
            
           
            List organizations = remote.getOrganizationNameList(0,leftTableRowsPerPage+1);
           
            //if the list returns back more rows than the limit then we have another page
            if(organizations.size() == 17){
            	 returnModel.totalPages = 2;
            	 returnModel.totalRows = (returnModel.pageIndex*leftTableRowsPerPage)+leftTableRowsPerPage+1;          
            }else{
            	returnModel.totalPages = 1;
           	 	returnModel.totalRows = organizations.size();          
            }
            
            returnModel.reset();
            
            int iter = 0;
            while(iter < organizations.size() && iter < leftTableRowsPerPage) {
               OrganizationTableRowDO tableRow = (OrganizationTableRowDO)organizations.get(iter);
          //  System.out.println("name: ["+tableRow.getName()+"]  id: ["+tableRow.getId()+"]");
               TableRow row = returnModel.createRow();
          //      String name = new String();
               NumberField id = new NumberField();
               id.setType("integer");
                
          //      name = tableRow.getName();
                
                id.setValue(tableRow.getId());
                row.addHidden("id", id);
               //System.out.println(row.getColumn(0).toString());
                row.getColumn(0).setValue(tableRow.getName());
                returnModel.addRow(row);
                
                iter++;
              } 
            
        } catch (Exception e) {
         //   log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return returnModel;
	}
	
	public TableModel fillContactsTable(TableModel contactsModel, List contactsList){
		//TableModel contactsModel = new TableModel();
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
	                addId.setValue(contactRow.getAddressId());
	                row.addHidden("contactId", id);
	                row.addHidden("addId", addId);
	                
	                row.getColumn(0).setValue(contactRow.getName());
	                row.getColumn(1).setValue(contactRow.getMultipleUnit());
	                row.getColumn(2).setValue(contactRow.getStreetAddress());
	                row.getColumn(3).setValue(contactRow.getCity());
	                row.getColumn(4).setValue(contactRow.getState());
	                row.getColumn(5).setValue(contactRow.getZipCode());
	                row.getColumn(6).setValue(contactRow.getWorkPhone());
	                row.getColumn(7).setValue(contactRow.getHomePhone());
	                row.getColumn(8).setValue(contactRow.getCellPhone());
	                row.getColumn(9).setValue(contactRow.getFaxPhone());
	                row.getColumn(10).setValue(contactRow.getEmail());
	                row.getColumn(11).setValue(contactRow.getCountry());
	                
	                contactsModel.addRow(row);
	       } 
			//TableRow row = contactsModel.createRow();
			//row.getColumn(4).setValue("");
			//row.getColumn(11).setValue("");
			//contactsModel.addRow(row);
			
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }		
		
		return contactsModel;
	}

	public TableModel getOrganizationByLetter(String letter, TableModel returnModel) {
		 OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		 queryByLetter = true;
		 this.letter = letter;
		 
		 try 
         {
           List organizations = remote.getOrganizationNameListByLetter((letter.toUpperCase()+"%"),0,16);
          
           returnModel.reset();
           
           for(int iter = 0;iter < organizations.size();iter++) {
              OrganizationTableRowDO tableRow = (OrganizationTableRowDO)organizations.get(iter);

              TableRow row = returnModel.createRow();
              NumberField id = new NumberField();
              id.setType("integer");
               
               id.setValue(tableRow.getId());
               row.addHidden("id", id);

               row.getColumn(0).setValue(tableRow.getName());
               returnModel.addRow(row);
             } 
           
       } catch (Exception e) {
            e.printStackTrace();
           return null;
       }
	//	returnModel.paged = true;
		//returnModel.totalRows=1;
		returnModel.rowsPerPage=leftTableRowsPerPage;
		returnModel.pageIndex=0;
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

	public TableModel getPage(int page, int selected) throws RPCException {
		TableModel reqModel = model;
        reqModel.paged = true;
        reqModel.rowsPerPage = leftTableRowsPerPage;
        reqModel.showIndex = false;
        reqModel.pageIndex = page;
        
        List organizations = null;
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        if(queryByLetter){
        	 organizations = remote.getOrganizationNameListByLetter((letter.toUpperCase()+"%"),(page*leftTableRowsPerPage),leftTableRowsPerPage+1);
        }else{
        	organizations = remote.getOrganizationNameList((page*leftTableRowsPerPage),leftTableRowsPerPage+1);
        }
        
        if(organizations.size() > leftTableRowsPerPage){
        	reqModel.totalPages = page+1;
        	reqModel.totalRows = (page*leftTableRowsPerPage)+leftTableRowsPerPage+1;          
        }else{
        	reqModel.totalPages = page;
            reqModel.totalRows = (page*leftTableRowsPerPage)+organizations.size();
        }
        
        reqModel.reset();
        
        int iter = 0;
        while(iter < organizations.size() && iter < leftTableRowsPerPage) {
           OrganizationTableRowDO tableRow = (OrganizationTableRowDO)organizations.get(iter);
      //  System.out.println("name: ["+tableRow.getName()+"]  id: ["+tableRow.getId()+"]");
           TableRow row = reqModel.createRow();
      //      String name = new String();
           NumberField id = new NumberField();
           id.setType("integer");
            
      //      name = tableRow.getName();
            
            id.setValue(tableRow.getId());
            row.addHidden("id", id);
           //System.out.println(row.getColumn(0).toString());
            row.getColumn(0).setValue(tableRow.getName());
            reqModel.addRow(row);
            
            iter++;
          } 
       // reqModel.totalPages = model.numRows() / reqModel.rowsPerPage;
      //  reqModel.shown = model.numRows();
        
        
        
        //reqModel.totalRows = model.numRows();
        //if(model.numRows() % reqModel.rowsPerPage >  0)
        //    reqModel.totalPages++;
       // int i = page * reqModel.rowsPerPage;
       // int stop = i + reqModel.rowsPerPage;
       // while(i < model.numRows() && i < stop){
       //     reqModel.addRow(model.getRow(i));
       //     i++;
       // }
        return reqModel;
	}

	public TableModel saveModel(TableModel model) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public TableModel sort(int col, boolean down, int index, int selected) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	//autocomplete textbox method
	public void getDisplay(String cat, Integer value) {
		// TODO Auto-generated method stub
		
	}

	//autocomplete textbox method
	public void getMatches(String cat, String match) {
		// TODO Auto-generated method stub
		
	}
}
