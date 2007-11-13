package org.openelis.server;

import java.util.List;

import org.openelis.client.dataEntry.screen.organization.OrganizationScreenInt;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationTableRowDO;
import org.openelis.gwt.client.services.TableServiceInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.client.widget.pagedtree.TreeModelItem;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.Filter;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.NumberField;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableModel;
import org.openelis.gwt.common.TableRow;
import org.openelis.gwt.server.AppServlet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrganizationScreen extends AppServlet implements OrganizationScreenInt, TableServiceInt {

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

	public FormRPC abort(AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitAdd(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitQuery(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC commitUpdate(FormRPC rpc) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC delete(AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
	}

	public FormRPC fetch(FormRPC rpc, AbstractField key) throws RPCException {
		//remote interface to call the organization bean
		OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
//		System.out.println("in contacts");
		OrganizationAddressDO organizationDO = remote.getOrganizationAddress((Integer)key.getValue());
//		set the fields in the RPC
        rpc.setFieldValue("orgName",organizationDO.getName());
        rpc.setFieldValue("streetAddress",organizationDO.getStreetAddress());
        rpc.setFieldValue("multUnit",organizationDO.getMultipleUnit());
        rpc.setFieldValue("city",organizationDO.getCity());
        rpc.setFieldValue("zipCode",organizationDO.getZipCode());
        rpc.setFieldValue("workPhone",organizationDO.getWorkPhone());
        rpc.setFieldValue("cellPhone",organizationDO.getCellPhone());
        rpc.setFieldValue("faxPhone",organizationDO.getFaxPhone());
        rpc.setFieldValue("email",organizationDO.getEmail());
        //<string key="action" max="20" required="false"/>
        rpc.setFieldValue("parentOrg",organizationDO.getParentOrganization());
        rpc.setFieldValue("isActive",((organizationDO.getIsActive() != null && organizationDO.getIsActive().equals("Y")) ? true : false));
        //<number key="id" required="false" type="integer"/>
        rpc.setFieldValue("addType","");
        //rpc.setFieldValue("contactAddType","");
        rpc.setFieldValue("state",organizationDO.getState());
        //rpc.setFieldValue("contactState","");
        rpc.setFieldValue("country","");
        //rpc.setFieldValue("contactCountry","");
		
		//get the filled out DO object
		if(rpc.getFieldValue("action").equals("contacts")){		
	        //load the contacts
	        List contactsList = remote.getOrganizationContacts((Integer)key.getValue());
	        //need to build the contacts table now...
	        rpc.setFieldValue("contactsTable",fillContactsTable((TableModel)rpc.getField("contactsTable").getValue(),contactsList));
	        
		}else if(rpc.getFieldValue("action").equals("notes")){
	        //load the notes
	        List notesList = remote.getOrganizationNotes((Integer)key.getValue());
	        TreeModel treeModel = new TreeModel();
	        TreeModelItem treeModelItem = new TreeModelItem();
	        //treeModelItem.setUserObject(useObject);
	        treeModelItem.setText("testItem");
	       // treeModelItem.
	        treeModelItem.setHasDummyChild(true);
	        treeModel.addItem(treeModelItem);
	       rpc.setFieldValue("noteTree", treeModel); 
	        
		}else{
			throw new RPCException("You need to specify what you want to load on the screen");
		}
        
      return rpc;  
	}

	public FormRPC fetchForUpdate(FormRPC rpc, AbstractField key) throws RPCException {
		// TODO Auto-generated method stub
		return null;
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
	                	                
	                id.setValue(contactRow.getId());
	                row.addHidden("id", id);


	                row.getColumn(0).setValue(contactRow.getName());
	                row.getColumn(1).setValue(contactRow.getMultipleUnit());
	                row.getColumn(2).setValue(contactRow.getStreetAddress());
	                row.getColumn(3).setValue(contactRow.getCity());
	                //state is dropdown
	                row.getColumn(5).setValue(contactRow.getZipCode());
	                row.getColumn(6).setValue(contactRow.getWorkPhone());
	                row.getColumn(7).setValue(contactRow.getHomePhone());
	                row.getColumn(8).setValue(contactRow.getCellPhone());
	                row.getColumn(9).setValue(contactRow.getFaxPhone());
	                row.getColumn(10).setValue(contactRow.getEmail());
	                //country row is dropdown
	                
	                contactsModel.addRow(row);
	       } 
			TableRow row = contactsModel.createRow();
			contactsModel.addRow(row);
			
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
}
