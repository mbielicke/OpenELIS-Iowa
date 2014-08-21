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
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.modules.inventoryItem.client.InventoryManufacturingForm;
import org.openelis.modules.shipping.client.ShippingForm;
import org.openelis.modules.shipping.client.ShippingItemsData;
import org.openelis.modules.shipping.client.ShippingItemsForm;
import org.openelis.modules.shipping.client.ShippingNotesForm;
import org.openelis.modules.shipping.client.ShippingShipToKey;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.ShipFromCacheHandler;
import org.openelis.server.handlers.ShippedMethodCacheHandler;
import org.openelis.server.handlers.ShippingStatusCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class ShippingService implements AppScreenFormServiceInt<ShippingForm, Query<TableDataRow<Integer>>>, AutoCompleteServiceInt {

	private UTFResource openElisConstants = UTFResource.getBundle((String) SessionManager.getSession().getAttribute("locale"));

	private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

	private static final int leftTableRowsPerPage = 20;

	public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query)
			throws RPCException {
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
					throw new RPCException(e.getMessage());
				}
			}
		} else {*/
			ShippingRemote remote = (ShippingRemote) EJBFactory
					.lookup("openelis/ShippingBean/remote");

			try {
				shippingIds = remote.query(query.fields, query.page*leftTableRowsPerPage, leftTableRowsPerPage);
            }catch(LastPageException e) {
                throw new LastPageException(openElisConstants.getString("lastPageException"));
			} catch (Exception e) {
				throw new RPCException(e.getMessage());
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

	public ShippingForm commitAdd(ShippingForm rpc) throws RPCException {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteDO shippingNote = new NoteDO();
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
                throw new RPCException(e.getMessage());
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

	public ShippingForm commitUpdate(ShippingForm rpc) throws RPCException {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteDO shippingNote = new NoteDO();
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
                throw new RPCException(e.getMessage());
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

	public ShippingForm commitDelete(ShippingForm rpc) throws RPCException {
		return null;
	}

	public ShippingForm abort(ShippingForm rpc) throws RPCException {
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

	public ShippingForm fetch(ShippingForm rpc) throws RPCException {
		/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
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

	public ShippingForm fetchForUpdate(ShippingForm rpc) throws RPCException {
		/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		ShippingDO shippingDO = new ShippingDO();
		try {
			shippingDO = remote.getShipmentAndLock(rpc.entityKey);
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
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

	public ShippingForm getScreen(ShippingForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl");

        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.shippedStatus = ShippingStatusCacheHandler.getShippingStatuses();
        SessionManager.getSession().setAttribute("shippingStatusVersion",ShippingStatusCacheHandler.version);
        rpc.shippedFrom = ShipFromCacheHandler.getShipFroms();
        SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);
        rpc.shippedMethod = ShippedMethodCacheHandler.getShippedMethods();
        SessionManager.getSession().setAttribute("shippedMethodVersion",ShippedMethodCacheHandler.version);

        return rpc;
	}
	
	public void checkModels(ShippingForm rpc) {
        /*
         * Retrieve current version of models from session.
         */
        int statuses = (Integer)SessionManager.getSession().getAttribute("shippingStatusVersion");
        int shippedFroms = (Integer)SessionManager.getSession().getAttribute("shipFromVersion");
        int shippedMethods = (Integer)SessionManager.getSession().getAttribute("shippedMethodVersion");
        /*
         * Compare stored version to current cache versions and update if necessary. 
         */
        if(statuses != ShippingStatusCacheHandler.version){
            rpc.shippedStatus = ShippingStatusCacheHandler.getShippingStatuses();
            SessionManager.getSession().setAttribute("shippingStatusVersion",ShippingStatusCacheHandler.version);
        }
        if(shippedFroms != ShipFromCacheHandler.version){
            rpc.shippedFrom = ShipFromCacheHandler.getShipFroms();
            SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);
        }
        if(shippedMethods != ShippedMethodCacheHandler.version){
            rpc.shippedMethod = ShippedMethodCacheHandler.getShippedMethods();
            SessionManager.getSession().setAttribute("shippedMethodVersion",ShippedMethodCacheHandler.version);
        }
    }

	public ShippingItemsForm loadShippingItems(ShippingItemsForm rpc) throws RPCException {
		loadShippingItemsForm(rpc.entityKey, rpc);
		return rpc;
	}
	
	public void loadShippingItemsForm(Integer key, ShippingItemsForm form) throws RPCException {
		getShippingItemsModel(key, form.itemsTable);
		getTrackingNumbersModel(key, form.trackingNumbersTable);
		form.load = true;
	}

	public ShippingNotesForm loadOrderShippingNotes(ShippingNotesForm rpc) throws RPCException {
		loadOrderShippingNotesForm(rpc.entityKey, rpc);
		return rpc;	
}
	
	public void loadOrderShippingNotesForm(Integer key, ShippingNotesForm form) throws RPCException {
	    getOrderShippingNotesValue(key, form);
		form.load = true;
	}

	public void getShippingItemsModel(Integer key, TableField<TableDataRow<Integer>> shippingItemsTable)
			throws RPCException {
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
			throws RPCException {
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

	public void getOrderShippingNotesValue(Integer key, ShippingNotesForm form) throws RPCException {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");

		NoteDO noteDO = remote.getShippingNote(key);

		if (noteDO != null){
		    form.text.setValue(noteDO.getText());
		    form.id = noteDO.getId();
		}
	}

	public TableDataModel<TableDataRow<Integer>> getInitialModel(String cat) {
		Integer id = null;
		CategoryRemote remote = (CategoryRemote) EJBFactory
				.lookup("openelis/CategoryBean/remote");

		if (cat.equals("status"))
			id = remote.getCategoryId("shippingStatus");
		else if (cat.equals("shipFrom"))
			id = remote.getCategoryId("shipFrom");
		else if (cat.equals("shippingMethod"))
			id = remote.getCategoryId("shippingMethod");

		List<IdNameDO> entries = new ArrayList();
		if (id != null)
			entries = remote.getDropdownValues(id);

		// we need to build the model to return
		TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();

		if (entries.size() > 0) {
			// create a blank entry to begin the list
			returnModel.add(new TableDataRow<Integer>(0, new StringObject("")));
		}

		for(IdNameDO resultDO : entries) {
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
		rpc.processedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getProcessedDate().getDate()));
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
			form.processedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getProcessedDate().getDate()));

		if (shippingDO.getShippedDate() != null
				&& shippingDO.getShippedDate().getDate() != null)
			form.shippedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, shippingDO.getShippedDate().getDate()));
		if (shippingDO.getShippedFromId() != null)
			form.shippedFromId.setValue(new TableDataRow<Integer>(shippingDO.getShippedFromId()));
		if (shippingDO.getShippedMethodId() != null)
			form.shippedMethodId.setValue(new TableDataRow<Integer>(shippingDO.getShippedMethodId()));
		if (shippingDO.getStatusId() != null)
			form.statusId.setValue(new TableDataRow<Integer>(shippingDO.getStatusId()));
	}

	public TableDataModel getMatches(String cat, TableDataModel model, String match,
			HashMap<String,FieldType> params) throws RPCException {
		if ("shippedTo".equals(cat))
			return getShippedToMatches(match);

		return null;
	}

	private TableDataModel<TableDataRow<Integer>> getShippedToMatches(String match) {
		OrganizationRemote remote = (OrganizationRemote) EJBFactory
				.lookup("openelis/OrganizationBean/remote");
		TableDataModel<TableDataRow<Integer>> dataModel = new TableDataModel<TableDataRow<Integer>>();
		List autoCompleteList;

		try {
			int id = Integer.parseInt(match); // this will throw an exception
												// if it isnt an id
			// lookup by id...should only bring back 1 result
			autoCompleteList = remote.autoCompleteLookupById(id);

		} catch (NumberFormatException e) {
			// it isnt an id
			// lookup by name
			autoCompleteList = remote.autoCompleteLookupByName(match + "%", 10);
		}

		for (int i = 0; i < autoCompleteList.size(); i++) {
			OrganizationAutoDO resultDO = (OrganizationAutoDO) autoCompleteList
					.get(i);
			// org id
			Integer orgId = resultDO.getId();
			// org name
			String name = resultDO.getName();
			// org apt suite #
			String aptSuite = resultDO.getAptSuite();
			// org street address
			String address = resultDO.getAddress();
			// org city
			String city = resultDO.getCity();
			// org state
			String state = resultDO.getState();
			// org zipcode
			String zipCode = resultDO.getZipCode();

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
}