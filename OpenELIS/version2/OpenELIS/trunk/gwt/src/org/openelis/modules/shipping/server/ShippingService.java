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
package org.openelis.modules.shipping.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ShippingManagerRemote;
import org.openelis.remote.ShippingRemote;

public class ShippingService {
    
    private static final int rowPP = 12;
    
    public ShippingManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }
    
    public ShippingManager fetchWithItemsAndTrackings (Integer id) throws Exception {
        return remoteManager().fetchWithItemsAndTracking(id);
    }
    
    public ShippingManager fetchWithNotes(Integer id) throws Exception {
        return remoteManager().fetchWithNotes(id);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }
    
    public ShippingManager add(ShippingManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    public ShippingManager update(ShippingManager man) throws Exception {
        return remoteManager().update(man);
    }
    
    public ShippingManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }
    
    public ShippingManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }
    
    public ShippingItemManager fetchItemByShippingId(Integer id) throws Exception {
        return remoteManager().fetchItemByShippingId(id);
    }
    
    public ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception {
        return remoteManager().fetchTrackingByShippingId(id);
    }
    
    private ShippingRemote remote() {
        return (ShippingRemote)EJBFactory.lookup("openelis/ShippingBean/remote"); 
    }
    
    private ShippingManagerRemote remoteManager() {
        return (ShippingManagerRemote)EJBFactory.lookup("openelis/ShippingManagerBean/remote"); 
    }
    
}
    
    //implements AppScreenFormServiceInt<ShippingForm, Query<TableDataRow<Integer>>>, AutoCompleteServiceInt {
   /*
	private UTFResource openElisConstants = UTFResource.getBundle((String) SessionManager.getSession().getAttribute("locale"));

	private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

	private static final int leftTableRowsPerPage = 20;

	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query)
			throws Exception {
		List shippingIds;
/*
		if (qList == null) {

			qList = (ArrayList<AbstractField>) SessionManager.getSession().getAttribute(
					"ShippingQuery");

			if (qList == null)
				throw new QueryException(openElisConstants
						.getString("queryExpiredException"));

			ShippingRemote remote = (ShippingRemote) EJBFactory
					.lookup("openelis/ShippingBean/remote");
			try {
				shippingIds = remote.query(qList,
						(model.getPage() * leftTableRowsPerPage),
						leftTableRowsPerPage + 1);
			} catch (Exception e) {
				if (e instanceof LastPageException) {
					throw new LastPageException(openElisConstants
							.getString("lastPageException"));
				} else {
					throw new Exception(e.getMessage());
				}
			}
		} else {*/
/*			ShippingRemote remote = (ShippingRemote) EJBFactory
					.lookup("openelis/ShippingBean/remote");

			try {
				shippingIds = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}

			// need to save the rpc used to the encache
			//SessionManager.getSession().setAttribute("ShippingQuery", qList);
		//}

		// fill the model with the query results
		int i = 0;
		if (query.results == null)
			query.results = new TableDataModel<TableDataRow<Integer>>();
		else
			query.results.clear();
		while (i < shippingIds.size() && i < leftTableRowsPerPage) {
			IdNameDO resultDO = (IdNameDO) shippingIds.get(i);

			TableDataRow<Integer> row = new TableDataRow<Integer>();

			row.key = resultDO.getId();
			query.results.add(row);
			i++;
		}

		return query;
	}

	public ShippingForm commitAdd(ShippingForm rpc) throws Exception {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteViewDO shippingNote = new NoteViewDO();
		List trackingNumbers = new ArrayList();
		List shippingItems = new ArrayList();
		System.out.println("before get out of rpc");
		// build the shippingDO from the form
		shippingDO = getShippingDOFromRPC(rpc);
		System.out.println("after out");
		// tracking numbers info
		TableDataModel<TableDataRow<Integer>> trackingNumsTable = (TableDataModel<TableDataRow<Integer>>) rpc.shippingItemsForm.trackingNumbersTable.getValue();
		trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());
		System.out.println("after tracking");
		// shipping items info
		TableDataModel<TableDataRow<Integer>> shippingItemsTable = (TableDataModel<TableDataRow<Integer>>) rpc.shippingItemsForm.itemsTable.getValue();
		shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());
System.out.println("after shipping items");
		//set the shipping notes
		shippingNote.setSubject("");
		shippingNote.setText((String)rpc.shippingNotesForm.text.getValue());
		shippingNote.setIsExternal("Y");


		// send the changes to the database
		Integer shippingId;
		try {
			shippingId = (Integer) remote.updateShipment(shippingDO, shippingItems, trackingNumbers, shippingNote);
		} catch (Exception e) {
		    if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new Exception(e.getMessage());
		}

		// lookup the changes from the database and build the rpc
		shippingDO.setId(shippingId);

		// set the fields in the RPC
		setFieldsInRPC(rpc, shippingDO);
		
		String tab = rpc.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.entityKey, rpc.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingForm commitUpdate(ShippingForm rpc) throws Exception {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteViewDO shippingNote = new NoteViewDO();
		List trackingNumbers = new ArrayList();
		List shippingItems = new ArrayList();

		// build the shippingDO from the form
		shippingDO = getShippingDOFromRPC(rpc);

		// tracking numbers info
		TableDataModel<TableDataRow<Integer>> trackingNumsTable = (TableDataModel<TableDataRow<Integer>>) rpc.shippingItemsForm.trackingNumbersTable.getValue();
		trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());

		// shipping items info
		TableDataModel<TableDataRow<Integer>> shippingItemsTable = (TableDataModel<TableDataRow<Integer>>) rpc.shippingItemsForm.itemsTable.getValue();
		shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());

		//set the shipping notes
		shippingNote.setId(rpc.shippingNotesForm.id);
        shippingNote.setText((String)rpc.shippingNotesForm.text.getValue());
        shippingNote.setIsExternal("Y");
        
		// send the changes to the database
		try {
			remote.updateShipment(shippingDO, shippingItems, trackingNumbers, shippingNote);
		} catch (Exception e) {
            if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc);
                return rpc;
            }else
                throw new Exception(e.getMessage());
        }

		// set the fields in the RPC
		setFieldsInRPC(rpc, shippingDO);
		
		String tab = rpc.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.entityKey, rpc.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingForm commitDelete(ShippingForm rpc) throws Exception {
		return null;
	}

	public ShippingForm abort(ShippingForm rpc) throws Exception {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		ShippingDO shippingDO = remote.getShipmentAndUnlock(rpc.entityKey);

		// set the fields in the RPC
		setFieldsInRPC(rpc, shippingDO);

		if (rpc.shippingItemsForm.load) 
			loadShippingItemsForm(rpc.entityKey, rpc.shippingItemsForm);
		
		if (rpc.shippingNotesForm.load) 
			loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotesForm);
		
		return rpc;
	}

	public ShippingForm fetch(ShippingForm rpc) throws Exception {
	    // remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		// get the shipping record
		ShippingDO shippingDO = remote.getShipment(rpc.entityKey);

		// set the fields in the RPC
		setFieldsInRPC(rpc, shippingDO);

		String tab = rpc.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.entityKey, rpc.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingForm fetchForUpdate(ShippingForm rpc) throws Exception {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		ShippingDO shippingDO = new ShippingDO();
		try {
			shippingDO = remote.getShipmentAndLock(rpc.entityKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc, shippingDO);

		String tab = rpc.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.entityKey, rpc.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.entityKey, rpc.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingForm getScreen(ShippingForm rpc) throws Exception{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl");

        return rpc;
	}
	
	public ShippingItemsForm loadShippingItems(ShippingItemsForm rpc) throws Exception {
		loadShippingItemsForm(rpc.entityKey, rpc);
		return rpc;
	}
	
	public void loadShippingItemsForm(Integer key, ShippingItemsForm form) throws Exception {
		getShippingItemsModel(key, form.itemsTable);
		getTrackingNumbersModel(key, form.trackingNumbersTable);
		form.load = true;
	}

	public ShippingNotesForm loadOrderShippingNotes(ShippingNotesForm rpc) throws Exception {
		loadOrderShippingNotesForm(rpc.entityKey, rpc);
		return rpc;	
}
	
	public void loadOrderShippingNotesForm(Integer key, ShippingNotesForm form) throws Exception {
	    getOrderShippingNotesValue(key, form);
		form.load = true;
	}

	public void getShippingItemsModel(Integer key, TableField<TableDataRow<Integer>> shippingItemsTable)
			throws Exception {
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		List shippingItemsList = remote.getShippingItems(key);
		TableDataModel<TableDataRow<Integer>> shippingItemsModel = shippingItemsTable.getValue();

		shippingItemsModel.clear();

		for (int iter = 0; iter < shippingItemsList.size(); iter++) {
			ShippingItemDO itemDO = (ShippingItemDO) shippingItemsList.get(iter);

			TableDataRow<Integer> row = shippingItemsModel.createNewSet();
			ShippingItemsData hiddenData = new ShippingItemsData();
			hiddenData.referenceTableId = itemDO.getReferenceTableId();
			hiddenData.referenceId = itemDO.getReferenceId();

			row.key = itemDO.getId();
			row.setData(hiddenData);

			row.getCells().get(0).setValue(itemDO.getQuantity());
			row.getCells().get(1).setValue(itemDO.getItemDescription());

			shippingItemsModel.add(row);
		}
	}

	public void getTrackingNumbersModel(Integer key, TableField<TableDataRow<Integer>> trackingTable)
			throws Exception {
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		List trackingNumbersList = remote.getTrackingNumbers(key);
		TableDataModel<TableDataRow<Integer>> trackingNumbersModel = trackingTable.getValue();

		trackingNumbersModel.clear();

		for (int iter = 0; iter < trackingNumbersList.size(); iter++) {
			ShippingTrackingDO trackingDO = (ShippingTrackingDO) trackingNumbersList
					.get(iter);

			TableDataRow<Integer> row = trackingNumbersModel.createNewSet();

			row.key = trackingDO.getId();
			row.getCells().get(0).setValue(trackingDO.getTrackingNumber());

			trackingNumbersModel.add(row);
		}
	}

	public void getOrderShippingNotesValue(Integer key, ShippingNotesForm form) throws Exception {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");

		NoteViewDO noteDO = remote.getShippingNote(key);

		if (noteDO != null){
		    form.text.setValue(noteDO.getText());
		    form.id = noteDO.getId();
		}
	}

	public TableDataModel<TableDataRow<Integer>> getInitialModel(String cat) {
		Integer id = null;
		CategoryRemote remote = (CategoryRemote) EJBFactory
				.lookup("openelis/CategoryBean/remote");
		DictionaryRemote dictRemote = (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");

		try {
		if (cat.equals("status"))
			id = (remote.fetchBySystemName("shippingStatus")).getId();
		else if (cat.equals("shipFrom"))
			id = (remote.fetchBySystemName("shipFrom")).getId();
		else if (cat.equals("shippingMethod"))
			id = (remote.fetchBySystemName("shippingMethod")).getId();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		List<IdNameVO> entries = new ArrayList();

		// we need to build the model to return
		TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();

		if (entries.size() > 0) {
			// create a blank entry to begin the list
			returnModel.add(new TableDataRow<Integer>(0, new StringObject("")));
		}

		for(IdNameVO resultDO : entries) {
			returnModel.add(new TableDataRow<Integer>(resultDO.getId(),
					new StringObject(resultDO.getName())));
		}

		return returnModel;
	}

	public ShippingForm getAddAutoFillValues(ShippingForm rpc) throws Exception {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");
		ShippingAddAutoFillDO autoDO;

		autoDO = remote.getAddAutoFillValues();
		
		rpc.statusId.setValue(new TableDataRow<Integer>(autoDO.getStatus()));
		rpc.processedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getProcessedDate().getDate()));
		rpc.processedBy.setValue(autoDO.getProcessedBy());
		rpc.systemUserId = autoDO.getSystemUserId();

		return rpc;
	}

	private ShippingDO getShippingDOFromRPC(ShippingForm form) {
		ShippingDO shippingDO = new ShippingDO();

		shippingDO.setId(form.id.getValue());
		shippingDO.setCost(form.cost.getValue());
		shippingDO.setNumberOfPackages(form.numberOfPackages.getValue());
		shippingDO.setProcessedById(form.systemUserId);

		if (form.processedDate.getValue() != null)
			shippingDO.setProcessedDate(form.processedDate.getValue().getDate());

		if (form.shippedDate.getValue() != null)
			shippingDO.setShippedDate(form.shippedDate.getValue().getDate());

		shippingDO.setShippedFromId((Integer) form.shippedFromId.getSelectedKey());
		shippingDO.setShippedMethodId((Integer) form.shippedMethodId.getSelectedKey());
		shippingDO.setStatusId((Integer) form.statusId.getSelectedKey());

		// set shipped to values
		shippingDO.setShippedToId((Integer) form.organization.getSelectedKey());
		shippingDO.setShippedTo((String) form.organization.getTextValue());
		shippingDO.addressDO.setMultipleUnit(form.multipleUnit.getValue());
		shippingDO.addressDO.setStreetAddress(form.streetAddress.getValue());
		shippingDO.addressDO.setCity(form.city.getValue());
		shippingDO.addressDO.setState(form.state.getValue());
		shippingDO.addressDO.setZipCode(form.zipcode.getValue());

		return shippingDO;
	}

	private List getShippingItemsListFromRPC(TableDataModel<TableDataRow<Integer>> itemsTable,
			Integer shippingId) {
		List shippingItems = new ArrayList();

		for (int i = 0; i < itemsTable.size(); i++) {
			ShippingItemDO itemDO = new ShippingItemDO();
			TableDataRow<Integer> row = itemsTable.get(i);

			Integer itemId = row.key;

			ShippingItemsData rowData = (ShippingItemsData) row.getData();
			//Integer referenceId = hiddenData.referenceId;
			//Integer referenceTableId = hiddenData.referenceTableId;
			//Integer invLocId = locIdObj.getValue();
			//Integer transId = hiddenData.transId;

			itemDO.setId(itemId);
			itemDO.setReferenceId(rowData.referenceId);
			itemDO.setReferenceTableId(rowData.referenceTableId);
			itemDO.setQuantity((Integer)row.cells[0].getValue());
			itemDO.setDescription((String)row.cells[1].getValue());
			itemDO.setShippingId(shippingId);

			shippingItems.add(itemDO);
		}

		return shippingItems;
	}

	private List getTrackingNumberListFromRPC(TableDataModel<TableDataRow<Integer>> trackingNumsTable, Integer shippingId) {
		List trackingNums = new ArrayList();
		List<TableDataRow<Integer>> deletedRows = trackingNumsTable.getDeletions();

		for (int i = 0; i < trackingNumsTable.size(); i++) {
			ShippingTrackingDO trackingDO = new ShippingTrackingDO();
			TableDataRow<Integer> row = trackingNumsTable.get(i);

			Integer itemId = row.key;

			if (itemId != null)
				trackingDO.setId(itemId);

			trackingDO.setShippingId(shippingId);
			trackingDO.setTrackingNumber((String) row.getCells().get(0).getValue());

			trackingNums.add(trackingDO);
		}

		if(deletedRows != null){
    		for (int j = 0; j < deletedRows.size(); j++) {
    			TableDataRow<Integer> deletedRow = deletedRows.get(j);
    			if (deletedRow.key != null) {
    				ShippingTrackingDO trackingDO = new ShippingTrackingDO();
    				trackingDO.setDelete(true);
    				trackingDO.setId(deletedRow.key);
    			}
    		}
		}

		return trackingNums;
	}

	private void setRpcErrors(List exceptionList, Form<? extends Object> form) {
        HashMap<String,AbstractField> map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
		for (int i = 0; i < exceptionList.size(); i++) {
			// if the error is inside the table
			
				// if the error is on the field
			if (exceptionList.get(i) instanceof FieldErrorException)
				map.get(
						((FieldErrorException) exceptionList.get(i))
								.getFieldName()).addError(
						openElisConstants
								.getString(((FieldErrorException) exceptionList
										.get(i)).getMessage()));

			// if the error is on the entire form
			else if (exceptionList.get(i) instanceof FormErrorException)
				form.addError(openElisConstants
						.getString(((FormErrorException) exceptionList.get(i))
								.getMessage()));
		}

		form.status = Form.Status.invalid;
	}

	private void setFieldsInRPC(ShippingForm form, ShippingDO shippingDO) {
		// create dataset for ship to auto complete
		if (shippingDO.getShippedToId() == null)
			form.organization.clear();
		else {
			TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
			model.add(new TableDataRow<Integer>(shippingDO.getShippedToId(),
					new StringObject(shippingDO.getShippedTo())));
			form.organization.setModel(model);
			form.organization.setValue(model.get(0));
		}

		form.multipleUnit.setValue(shippingDO.addressDO.getMultipleUnit());
		form.streetAddress.setValue(shippingDO.addressDO.getStreetAddress());
		form.city.setValue(shippingDO.addressDO.getCity());
		form.state.setValue(shippingDO.addressDO.getState());
		form.zipcode.setValue(shippingDO.addressDO.getZipCode());

		form.id.setValue(shippingDO.getId());
		form.cost.setValue(shippingDO.getCost());
		form.numberOfPackages.setValue(shippingDO.getNumberOfPackages());
		form.processedBy.setValue(shippingDO.getProcessedBy());
		form.systemUserId = shippingDO.getProcessedById();
		if (shippingDO.getProcessedDate() != null
				&& shippingDO.getProcessedDate().getDate() != null)
			form.processedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getProcessedDate().getDate()));

		if (shippingDO.getShippedDate() != null
				&& shippingDO.getShippedDate().getDate() != null)
			form.shippedDate.setValue(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getShippedDate().getDate()));
		if (shippingDO.getShippedFromId() != null)
			form.shippedFromId.setValue(new TableDataRow<Integer>(shippingDO.getShippedFromId()));
		if (shippingDO.getShippedMethodId() != null)
			form.shippedMethodId.setValue(new TableDataRow<Integer>(shippingDO.getShippedMethodId()));
		if (shippingDO.getStatusId() != null)
			form.statusId.setValue(new TableDataRow<Integer>(shippingDO.getStatusId()));
	}

	public TableDataModel getMatches(String cat, TableDataModel model, String match,
			HashMap<String,FieldType> params) throws Exception {
		if ("shippedTo".equals(cat))
			return getShippedToMatches(match);

		return null;
	}

	private TableDataModel<TableDataRow<Integer>> getShippedToMatches(String match) {
		OrganizationRemote remote = (OrganizationRemote) EJBFactory
				.lookup("openelis/OrganizationBean/remote");
		TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
		List autoCompleteList = null;
		Integer id;

		try {
			id = Integer.parseInt(match);
			// this will throw an exception if it isnt an id
			// lookup by id...should only bring back 1 result
		} catch (NumberFormatException e) {
		    id = null;
		}

		try {
		    if (id != null) {
		        autoCompleteList = new ArrayList(1);
		        autoCompleteList.add(remote.fetchActiveById(id));
		    } else {
		        autoCompleteList = remote.fetchActiveByName(match + "%", 10);
		    }
		} catch (Exception e) {
		    Window.alert(e.getMessage());
		}

		for (int i = 0; i < autoCompleteList.size(); i++) {
			OrganizationDO resultDO = (OrganizationDO) autoCompleteList
					.get(i);
			// org id
			Integer orgId = resultDO.getId();
			// org name
			String name = resultDO.getName();
			// org apt suite #
			String aptSuite = resultDO.getAddress().getMultipleUnit();
			// org street address
			String address = resultDO.getAddress().getStreetAddress();
			// org city
			String city = resultDO.getAddress().getCity();
			// org state
			String state = resultDO.getAddress().getState();
			// org zipcode
			String zipCode = resultDO.getAddress().getZipCode();

			TableDataRow<Integer> data = new TableDataRow<Integer>(orgId,
                                                                   new FieldType[] {
                                                                                    new StringObject(name),
                                                                                    new StringObject(address),
                                                                                    new StringObject(city),
                                                                                    new StringObject(state)
                                                                   }
                                         );

			ShippingShipToKey hiddenData = new ShippingShipToKey();
			hiddenData.aptSuite = aptSuite;
			hiddenData.zipCode = zipCode;
			data.setData(hiddenData);

			// add the dataset to the datamodel
			dataModel.add(data);
		}

		return dataModel;
	}

	class ShippingItemsHidden {
		public Integer referenceTableId;
		public Integer referenceId;
	}
	
	   private void setRpcErrors(List exceptionList, TableField itemsTable, ShippingForm form){
           HashMap<String,AbstractField> map = null;
           if(exceptionList.size() > 0) {
               map = FormUtil.createFieldMap(form);
           }
	        for (int i=0; i<exceptionList.size();i++) {
	            //if the error is inside the org contacts table
	            if(exceptionList.get(i) instanceof TableFieldErrorException){
	                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
	                itemsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
	    */