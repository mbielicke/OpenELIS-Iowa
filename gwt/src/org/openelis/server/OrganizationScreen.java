package org.openelis.server;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.CacheManager;

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
import org.openelis.gwt.common.AutoCompleteRPC;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.NumberField;
import org.openelis.gwt.common.OptionField;
import org.openelis.gwt.common.QueryField;
import org.openelis.gwt.common.QueryNumberField;
import org.openelis.gwt.common.QueryOptionField;
import org.openelis.gwt.common.QueryStringField;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.StringField;
import org.openelis.gwt.common.TableField;
import org.openelis.gwt.common.TableModel;
import org.openelis.gwt.common.TableRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.CachingManager;
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
	private String systemUserId = "";
	
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
        rpc.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
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
		newOrganizationDO.setParentOrganization((Integer)rpc.getFieldValue("parentOrgId"));
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
		
		//send the changes to the database
		Integer orgId = (Integer)remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId,false);

//		set the fields in the RPC
		rpc.setFieldValue("orgId", orgId);
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
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

	public AbstractField commitQuery(FormRPC rpc) throws RPCException {
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
		HashMap<String,AbstractField> fields = rpc.getFieldMap();

		//contacts table
		TableModel contactsTable = (TableModel)rpc.getField("contactsTable").getValue();
		
		if(contactsTable != null){
			QueryOptionField aa = (QueryOptionField)contactsTable.getRow(0).getColumn(4);
			fields.put("contactName",(QueryStringField)contactsTable.getRow(0).getColumn(0));
			fields.put("contactMultUnit",(QueryStringField)contactsTable.getRow(0).getColumn(1));
			fields.put("contactStreetAddress",(QueryStringField)contactsTable.getRow(0).getColumn(2));
			fields.put("contactCity",(QueryStringField)contactsTable.getRow(0).getColumn(3));
			fields.put("contactState",(QueryOptionField)contactsTable.getRow(0).getColumn(4));
			fields.put("contactZipCode",(QueryStringField)contactsTable.getRow(0).getColumn(5));
			fields.put("contactWorkPhone",(QueryStringField)contactsTable.getRow(0).getColumn(6));
			fields.put("contactHomePhone",(QueryStringField)contactsTable.getRow(0).getColumn(7));
			fields.put("contactCellPhone",(QueryStringField)contactsTable.getRow(0).getColumn(8));
			fields.put("contactFaxPhone",(QueryStringField)contactsTable.getRow(0).getColumn(9));
			fields.put("contactEmail",(QueryStringField)contactsTable.getRow(0).getColumn(10));
			fields.put("contactCountry",(QueryOptionField)contactsTable.getRow(0).getColumn(11));
		}

		List organizationNames = new ArrayList();
			try{
			organizationNames = remote.query(fields,0,leftTableRowsPerPage);

		}catch(Exception e){
			throw new RPCException(e.getMessage());
		}
		
		//minimum i need
		//what page the query was on previously
		//the rpc for the query
		TableModel orgModel = new TableModel();
		orgModel.paged = true;
		orgModel.rowsPerPage = leftTableRowsPerPage;
		orgModel.showIndex = false;
		orgModel.pageIndex = 0;
		//orgModel.totalPages = (organizationNames.size() > leftTableRowsPerPage ? 2 : 1);
		//orgModel.totalRows = (1*leftTableRowsPerPage)+leftTableRowsPerPage+1;
		
		Iterator itraaa = organizationNames.iterator();
		
		while(itraaa.hasNext()){
			Object[] result = (Object[])itraaa.next();
			//org id
			Integer id = (Integer)result[0];
			//org name
			String name = (String)result[1];

			 TableRow row = new TableRow();

			 NumberField hiddenId = new NumberField();
			 hiddenId.setType("integer");
			 StringField nameField = new StringField();
			 nameField.setValue(name);
      
			 hiddenId.setValue(id);
			 row.addHidden("id", hiddenId);
			 row.addColumn(nameField);

			 orgModel.addRow(row);

		}
		TableField tf = new TableField();
        tf.setValue(orgModel);
        
        //need to save the rpc used to the encache
        if(systemUserId.equals(""))
        	systemUserId = remote.getSystemUserId().toString();
        CachingManager.putElement("screenQueryRpc", systemUserId+":Organization", rpc);
		return tf;
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
		newOrganizationDO.setParentOrganization((Integer) rpc.getFieldValue("parentOrgId"));
		//organization addres value
		newOrganizationDO.setAddressId((Integer) rpc.getFieldValue("addressId"));
		//newOrganizationDO.setType(type); FIXME need to eventually set this type to business (or org)
		newOrganizationDO.setMultipleUnit((String)rpc.getFieldValue("multUnit"));
		newOrganizationDO.setStreetAddress((String)rpc.getFieldValue("streetAddress"));
		newOrganizationDO.setCity((String)rpc.getFieldValue("city"));
		newOrganizationDO.setState((String)rpc.getFieldValue("state"));
		newOrganizationDO.setZipCode((String)rpc.getFieldValue("zipCode"));
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
			
			StringField deleteFlag = (StringField)row.getHidden("deleteFlag");
			if(deleteFlag == null){
				contactDO.setDelete(false);
			}else{
				contactDO.setDelete("Y".equals(deleteFlag.getValue()));
			}
			
			organizationContacts.add(contactDO);
		}
		
		
		//build the noteDo from the form
		organizationNote.setSubject((String)rpc.getFieldValue("usersSubject"));
		organizationNote.setText((String)rpc.getFieldValue("usersNote"));
		organizationNote.setIsExternal("Y");
		//organizationNote.setReferenceId(referenceId); handled in the update bean
		//organizationNote.setSystemUser(systemUser);   handled in the update bean
		//organizationNote.setTimestamp(new Date());    handled in the update bean		
		
		//send the changes to the database
		remote.updateOrganization(newOrganizationDO, organizationNote, organizationContacts);
		
		//lookup the changes from the database and build the rpc
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress(orgId,false);

//		set the fields in the RPC
		rpc.setFieldValue("orgId", orgId);
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
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
		rpc.setFieldValue("orgId", organizationDO.getOrganizationId());
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("parentOrgId", organizationDO.getParentOrganization());
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
        rpc.setFieldValue("parentOrgId",organizationDO.getParentOrganization());
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

	public TableModel getOrganizationByLetter(String letter, TableModel returnModel, FormRPC letterRPC) {
		 OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		 queryByLetter = true;
		 this.letter = letter;
		 letterRPC.setFieldValue("orgName",letter.toUpperCase()+"*");
		 try 
         {
			 List organizations = new ArrayList();
				try{
					organizations = remote.query(letterRPC.getFieldMap(),0,16);

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
	//	returnModel.paged = true;
		//returnModel.totalRows=1;
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

	public TableModel getPage(int page, int selected) throws RPCException {
		TableModel reqModel = model;
        reqModel.paged = true;
        reqModel.rowsPerPage = leftTableRowsPerPage;
        reqModel.showIndex = false;
        reqModel.pageIndex = page;
        
        //need to get the query rpc out of the cache
        FormRPC rpc = (FormRPC)CachingManager.getElement("screenQueryRpc", systemUserId+":Organization");
        
        List organizations = null;
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        try{
        	organizations = remote.query(rpc.getFieldMap(), (page*leftTableRowsPerPage), leftTableRowsPerPage+1);
        }catch(Exception e){
			throw new RPCException(e.getMessage());
		}
        
        if(organizations.size() > leftTableRowsPerPage){
        	reqModel.totalPages = page+1;
        	reqModel.totalRows = (page*leftTableRowsPerPage)+leftTableRowsPerPage+1;          
        }else{
        	reqModel.totalPages = page;
            reqModel.totalRows = (page*leftTableRowsPerPage)+organizations.size();
        }
        
        reqModel.reset();
        int i=0;
        while(i < organizations.size() && i < leftTableRowsPerPage) {
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
			reqModel.addRow(row);
			i++;
         } 

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
	
	public AutoCompleteRPC getDisplay(String cat, Integer value) {
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		AutoCompleteRPC rpc = new AutoCompleteRPC();
		
		List autoCompleteList = remote.autoCompleteLookupById(value);
		
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
		
		String[] displayArray = new String[1];
		displayArray[0] = name.trim();
		Integer[] idArray = new Integer[1];
		idArray[0] = orgId;
		
		rpc.dict_value = name.trim();
		rpc.display = displayArray;
		rpc.id = idArray;
		
		return rpc;		
	}

	//autocomplete textbox method
	//match is what they typed
	public AutoCompleteRPC getMatches(String cat, String match) {
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		AutoCompleteRPC rpc = new AutoCompleteRPC();

		try{
			int id = Integer.parseInt(match); //this will throw an exception if it isnt an id
			//lookup by id...should only bring back 1 result
			List autoCompleteList = remote.autoCompleteLookupById(id);
			String[] displayArray = new String[autoCompleteList.size()];
			Integer[] idArray = new Integer[autoCompleteList.size()];
			String[] textboxValues = new String[autoCompleteList.size()];
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
				
				displayArray[0] = name.trim()+" "+"("+address.trim()+" "+city.trim()+", "+state.trim()+")";
				textboxValues[0] = name.trim();
				idArray[0] = orgId;				
			}
			rpc.display = displayArray;
			rpc.textboxValue = textboxValues;
			rpc.id = idArray;
			rpc.category = "";
			rpc.match = match;
			
		}catch(NumberFormatException e){
			//it isnt an id
			//lookup by name
			List autoCompleteList = remote.autoCompleteLookupByName(match+"%", 10);
			Iterator itr = autoCompleteList.iterator();
			String[] displayArray = new String[autoCompleteList.size()];
			String[] textBoxValues = new String[autoCompleteList.size()];
			Integer[] idArray = new Integer[autoCompleteList.size()];
			int i=0;
			
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
				
				displayArray[i] = name.trim()+" "+"("+address.trim()+" "+city.trim()+", "+state.trim()+")";
				textBoxValues[i] = name.trim();
				idArray[i] = orgId;
				i++;
			}
			
			rpc.display = displayArray;
			rpc.textboxValue = textBoxValues;
			rpc.id = idArray;
			rpc.category = "";
			rpc.match = match;
		}
		
		return rpc;
		
	}

	//???
	public String getTip(AbstractField key) throws RPCException {
		return null;
	}
}
