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
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.modules.fillOrder.client.FillOrderOrderItemsKey;
import org.openelis.modules.organization.client.OrganizationForm;
import org.openelis.modules.shipping.client.ShippingForm;
import org.openelis.modules.shipping.client.ShippingItemsData;
import org.openelis.modules.shipping.client.ShippingItemsForm;
import org.openelis.modules.shipping.client.ShippingItemsRPC;
import org.openelis.modules.shipping.client.ShippingNotesForm;
import org.openelis.modules.shipping.client.ShippingNotesRPC;
import org.openelis.modules.shipping.client.ShippingRPC;
import org.openelis.modules.shipping.client.ShippingShipToKey;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.OrderRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.remote.ShippingRemote;
import org.openelis.server.constants.Constants;
import org.openelis.server.handlers.OrderStatusCacheHandler;
import org.openelis.server.handlers.ShipFromCacheHandler;
import org.openelis.server.handlers.ShippedMethodCacheHandler;
import org.openelis.server.handlers.ShippingStatusCacheHandler;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class ShippingService implements AppScreenFormServiceInt<ShippingRPC, Integer>, AutoCompleteServiceInt {

	private UTFResource openElisConstants = UTFResource.getBundle((String) SessionManager.getSession().getAttribute("locale"));

	private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

	private static final int leftTableRowsPerPage = 20;

	public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model)
			throws RPCException {
		List shippingIds;

		if (form == null) {

			form = (Form) SessionManager.getSession().getAttribute(
					"ShippingQuery");

			if (form == null)
				throw new QueryException(openElisConstants
						.getString("queryExpiredException"));

			HashMap<String, AbstractField> fields = form.getFieldMap();
			fields.remove("trackingNumbersTable");

			ShippingRemote remote = (ShippingRemote) EJBFactory
					.lookup("openelis/ShippingBean/remote");
			try {
				shippingIds = remote.query(fields,
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
		} else {
			ShippingRemote remote = (ShippingRemote) EJBFactory
					.lookup("openelis/ShippingBean/remote");
			HashMap<String, AbstractField> fields = form.getFieldMap();
			fields.remove("itemsTable");
			fields.remove("trackingNumbersTable");

			try {
				shippingIds = remote.query(fields, 0, leftTableRowsPerPage);

			} catch (Exception e) {
				throw new RPCException(e.getMessage());
			}

			// need to save the rpc used to the encache
			SessionManager.getSession().setAttribute("ShippingQuery", form);
		}

		// fill the model with the query results
		int i = 0;
		if (model == null)
			model = new DataModel<Integer>();
		else
			model.clear();
		while (i < shippingIds.size() && i < leftTableRowsPerPage) {
			IdNameDO resultDO = (IdNameDO) shippingIds.get(i);

			DataSet<Integer> row = new DataSet<Integer>();

			row.setKey(resultDO.getId());
			model.add(row);
			i++;
		}

		return model;
	}

	public ShippingRPC commitAdd(ShippingRPC rpc) throws RPCException {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteDO shippingNote = new NoteDO();
		List trackingNumbers = new ArrayList();
		List shippingItems = new ArrayList();

		// build the shippingDO from the form
		shippingDO = getShippingDOFromRPC(rpc.form);

		// tracking numbers info
		DataModel<Integer> trackingNumsTable = (DataModel<Integer>) rpc.form.shippingItemsForm.trackingNumbersTable.getValue();
		trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());

		// shipping items info
		DataModel<Integer> shippingItemsTable = (DataModel<Integer>) rpc.form.shippingItemsForm.itemsTable.getValue();
		shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());

		//set the shipping notes
		shippingNote.setSubject("");
		shippingNote.setText((String)rpc.form.shippingNotesForm.text.getValue());
		shippingNote.setIsExternal("Y");

		DataModel model = (DataModel) rpc.form.getFieldValue("unlockModel");

		// send the changes to the database
		Integer shippingId;
		try {
			shippingId = (Integer) remote.updateShipment(shippingDO, shippingItems, trackingNumbers, model, shippingNote);
		} catch (Exception e) {
		    if(e instanceof ValidationErrorsList){
                setRpcErrors(((ValidationErrorsList)e).getErrorList(), rpc.form);
                return rpc;
            }else
                throw new RPCException(e.getMessage());
		}

		// lookup the changes from the database and build the rpc
		shippingDO.setId(shippingId);

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, shippingDO);
		
		String tab = rpc.form.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.key, rpc.form.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingRPC commitUpdate(ShippingRPC rpc) throws RPCException {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		ShippingDO shippingDO = new ShippingDO();
		NoteDO shippingNote = new NoteDO();
		List trackingNumbers = new ArrayList();
		List shippingItems = new ArrayList();

		// build the shippingDO from the form
		shippingDO = getShippingDOFromRPC(rpc.form);

		// tracking numbers info
		DataModel trackingNumsTable = (DataModel) rpc.form.shippingItemsForm.trackingNumbersTable.getValue();
		trackingNumbers = getTrackingNumberListFromRPC(trackingNumsTable, shippingDO.getId());

		// shipping items info
		DataModel shippingItemsTable = (DataModel) rpc.form.shippingItemsForm.itemsTable.getValue();
		shippingItems = getShippingItemsListFromRPC(shippingItemsTable, shippingDO.getId());

		//set the shipping notes
        shippingNote.setSubject("");
        shippingNote.setText((String)rpc.form.shippingNotesForm.text.getValue());
        shippingNote.setIsExternal("Y");
        
		// validate the fields on the backend
		List exceptionList = remote.validateForUpdate(shippingDO,
				shippingItems, trackingNumbers);

		if (exceptionList.size() > 0) {
			// TODO setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

			return rpc;
		}

		// send the changes to the database
		try {
			remote.updateShipment(shippingDO, shippingItems, trackingNumbers, null, shippingNote);
		} catch (Exception e) {
			if (e instanceof EntityLockedException)
				throw new RPCException(e.getMessage());

			exceptionList = new ArrayList();
			exceptionList.add(e);

			// TODO setRpcErrors(exceptionList, trackingNumsTable, rpcSend);

			return rpc;
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, shippingDO);
		
		String tab = rpc.form.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.key, rpc.form.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingRPC commitDelete(ShippingRPC rpc) throws RPCException {
		return null;
	}

	public ShippingRPC abort(ShippingRPC rpc) throws RPCException {
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		DataModel model = (DataModel) rpc.unlockModel;

		ShippingDO shippingDO = remote.getShipmentAndUnlock(rpc.key, model);

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, shippingDO);

		if (rpc.form.shippingItemsForm.load) 
			loadShippingItemsForm(rpc.key, rpc.form.shippingItemsForm);
		
		if (rpc.form.shippingNotesForm.load) 
			loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotesForm);
		
		return rpc;
	}

	public ShippingRPC fetch(ShippingRPC rpc) throws RPCException {
		/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		// get the shipping record
		ShippingDO shippingDO = remote.getShipment(rpc.key);

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, shippingDO);

		String tab = rpc.form.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.key, rpc.form.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotesForm);
        }

		return rpc;
	}

	public ShippingRPC fetchForUpdate(ShippingRPC rpc) throws RPCException {
		/*
         * Call checkModels to make screen has most recent versions of dropdowns
         */
        checkModels(rpc);
        
		// remote interface to call the shipping bean
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");

		ShippingDO shippingDO = new ShippingDO();
		try {
			shippingDO = remote.getShipmentAndLock(rpc.key);
		} catch (Exception e) {
			throw new RPCException(e.getMessage());
		}

		// set the fields in the RPC
		setFieldsInRPC(rpc.form, shippingDO);

		String tab = rpc.form.shippingTabPanel;
        if(tab.equals("itemsTab")){
        	loadShippingItemsForm(rpc.key, rpc.form.shippingItemsForm);
        }
       
        if(tab.equals("orderNotesTab")){
        	loadOrderShippingNotesForm(rpc.key, rpc.form.shippingNotesForm);
        }

		return rpc;
	}

	public String getXML() throws RPCException {
		return null;
	}

	public HashMap<String, FieldType> getXMLData() throws RPCException {
		return null;
	}

	public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
		return null;
	}

	public ShippingRPC getScreen(ShippingRPC rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/shipping.xsl");

        /*
         * Load initial  models to RPC and store cache verison of models into Session for 
         * comparisons for later fetches
         */
        rpc.status = ShippingStatusCacheHandler.getShippingStatuses();
        SessionManager.getSession().setAttribute("shippingStatusVersion",ShippingStatusCacheHandler.version);
        rpc.shippedFrom = ShipFromCacheHandler.getShipFroms();
        SessionManager.getSession().setAttribute("shipFromVersion",ShipFromCacheHandler.version);
        rpc.shippedMethod = ShippedMethodCacheHandler.getShippedMethods();
        SessionManager.getSession().setAttribute("shippedMethodVersion",ShippedMethodCacheHandler.version);

        return rpc;
	}
	
	public void checkModels(ShippingRPC rpc) {
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
            rpc.status = ShippingStatusCacheHandler.getShippingStatuses();
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

	public ShippingItemsRPC loadShippingItems(ShippingItemsRPC rpc) throws RPCException {
		loadShippingItemsForm(rpc.key, rpc.form);
		return rpc;
	}
	
	public void loadShippingItemsForm(Integer key, ShippingItemsForm form) throws RPCException {
		getShippingItemsModel(key, form.itemsTable);
		getTrackingNumbersModel(key, form.trackingNumbersTable);
		form.load = true;
	}

	public ShippingNotesRPC loadOrderShippingNotes(ShippingNotesRPC rpc) throws RPCException {
		loadOrderShippingNotesForm(rpc.key, rpc.form);
		return rpc;	
}
	
	public void loadOrderShippingNotesForm(Integer key, ShippingNotesForm form) throws RPCException {
		form.text.setValue(getOrderShippingNotesValue(key));
		form.load = true;
	}

	public void getShippingItemsModel(Integer key, TableField<Integer> shippingItemsTable)
			throws RPCException {
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		List shippingItemsList = remote.getShippingItems(key);
		DataModel shippingItemsModel = (DataModel) shippingItemsTable
				.getValue();

		shippingItemsModel.clear();

		for (int iter = 0; iter < shippingItemsList.size(); iter++) {
			ShippingItemDO itemDO = (ShippingItemDO) shippingItemsList
					.get(iter);

			DataSet<Integer> row = shippingItemsModel.createNewSet();
			FillOrderOrderItemsKey hiddenData = new FillOrderOrderItemsKey();
			hiddenData.referenceTableId = itemDO.getReferenceTableId();
			hiddenData.referenceId = itemDO.getReferenceId();
			hiddenData.locId = itemDO.getInventoryLocationId();
			hiddenData.transId = itemDO.getTransId();

			row.setKey(itemDO.getId());
			row.setData(hiddenData);

			row.get(0).setValue(itemDO.getQuantity());
			row.get(1).setValue(itemDO.getItemDescription());

			shippingItemsModel.add(row);
		}
	}

	public void getTrackingNumbersModel(Integer key, TableField<Integer> trackingTable)
			throws RPCException {
		ShippingRemote remote = (ShippingRemote) EJBFactory
				.lookup("openelis/ShippingBean/remote");
		List trackingNumbersList = remote.getTrackingNumbers(key);
		DataModel trackingNumbersModel = (DataModel) trackingTable.getValue();

		trackingNumbersModel.clear();

		for (int iter = 0; iter < trackingNumbersList.size(); iter++) {
			ShippingTrackingDO trackingDO = (ShippingTrackingDO) trackingNumbersList
					.get(iter);

			DataSet<Integer> row = trackingNumbersModel.createNewSet();

			row.setKey(trackingDO.getId());
			row.get(0).setValue(trackingDO.getTrackingNumber());

			trackingNumbersModel.add(row);
		}
	}

	public String getOrderShippingNotesValue(Integer key) throws RPCException {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");

		NoteDO noteDO = remote.getShippingNote(key);

		if (noteDO != null)
			return noteDO.getText();

		return null;
	}

	public ShippingRPC unlockOrderRecords(ShippingRPC rpc) {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");

		remote.unlockOrders(rpc.checkedOrderIds);

		return rpc;
	}

	public DataModel getInitialModel(String cat) {
		Integer id = null;
		CategoryRemote remote = (CategoryRemote) EJBFactory
				.lookup("openelis/CategoryBean/remote");

		if (cat.equals("status"))
			id = remote.getCategoryId("shippingStatus");
		else if (cat.equals("shipFrom"))
			id = remote.getCategoryId("shipFrom");
		else if (cat.equals("shippingMethod"))
			id = remote.getCategoryId("shippingMethod");

		List entries = new ArrayList();
		if (id != null)
			entries = remote.getDropdownValues(id);

		// we need to build the model to return
		DataModel<Integer> returnModel = new DataModel<Integer>();

		if (entries.size() > 0) {
			// create a blank entry to begin the list
			returnModel.add(new DataSet<Integer>(0, new StringObject("")));
		}

		int i = 0;
		while (i < entries.size()) {
			IdNameDO resultDO = (IdNameDO) entries.get(i);
			returnModel.add(new DataSet<Integer>(resultDO.getId(),
					new StringObject(resultDO.getName())));
			i++;
		}

		return returnModel;
	}

	public ShippingRPC getAddAutoFillValues(ShippingRPC rpc) throws Exception {
		ShippingRemote remote = (ShippingRemote) EJBFactory.lookup("openelis/ShippingBean/remote");
		ShippingAddAutoFillDO autoDO;

		autoDO = remote.getAddAutoFillValues();
		
		rpc.form.statusId.setValue(new DataSet<Integer>(autoDO.getStatus()));
		rpc.form.processedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR, Datetime.DAY, autoDO.getProcessedDate().getDate()));
		rpc.form.processedBy.setValue(autoDO.getProcessedBy());
		rpc.form.systemUserId = autoDO.getSystemUserId();

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

	private List getShippingItemsListFromRPC(DataModel<Integer> itemsTable,
			Integer shippingId) {
		List shippingItems = new ArrayList();

		for (int i = 0; i < itemsTable.size(); i++) {
			ShippingItemDO itemDO = new ShippingItemDO();
			DataSet<Integer> row = itemsTable.get(i);

			Integer itemId = row.getKey();

			ShippingItemsData rowData = (ShippingItemsData) row.getData();
			//Integer referenceId = hiddenData.referenceId;
			//Integer referenceTableId = hiddenData.referenceTableId;
			//Integer invLocId = locIdObj.getValue();
			//Integer transId = hiddenData.transId;

			itemDO.setId(itemId);
			itemDO.setReferenceId(rowData.referenceId);
			itemDO.setReferenceTableId(rowData.referenceTableId);
			itemDO.setQuantity((Integer)row.get(0).getValue());
			itemDO.setDescription((String)row.get(1).getValue());
			itemDO.setShippingId(shippingId);

			shippingItems.add(itemDO);
		}

		return shippingItems;
	}

	private List getTrackingNumberListFromRPC(DataModel<Integer> trackingNumsTable, Integer shippingId) {
		List tackingNums = new ArrayList();
		List<DataSet<Integer>> deletedRows = trackingNumsTable.getDeletions();

		for (int i = 0; i < trackingNumsTable.size(); i++) {
			ShippingTrackingDO trackingDO = new ShippingTrackingDO();
			DataSet<Integer> row = trackingNumsTable.get(i);

			Integer itemId = row.getKey();

			if (itemId != null)
				trackingDO.setId(itemId);

			trackingDO.setShippingId(shippingId);
			trackingDO.setTrackingNumber((String) row.get(0).getValue());

			tackingNums.add(trackingDO);
		}

		for (int j = 0; j < deletedRows.size(); j++) {
			DataSet<Integer> deletedRow = deletedRows.get(j);
			if (deletedRow.getKey() != null) {
				ShippingTrackingDO trackingDO = new ShippingTrackingDO();
				trackingDO.setDelete(true);
				trackingDO.setId(deletedRow.getKey());
			}
		}

		return tackingNums;
	}

	private void setRpcErrors(List exceptionList, Form form) {
		for (int i = 0; i < exceptionList.size(); i++) {
			// if the error is inside the table
		    if (exceptionList.get(i) instanceof FieldErrorException)
				form.getField(
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
			DataModel<Integer> model = new DataModel<Integer>();
			model.add(new DataSet<Integer>(shippingDO.getShippedToId(),
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
			form.shippedFromId.setValue(new DataSet<Integer>(shippingDO.getShippedFromId()));
		if (shippingDO.getShippedMethodId() != null)
			form.shippedMethodId.setValue(new DataSet<Integer>(shippingDO.getShippedMethodId()));
		if (shippingDO.getStatusId() != null)
			form.statusId.setValue(new DataSet<Integer>(shippingDO.getStatusId()));
	}

	public DataModel getMatches(String cat, DataModel model, String match,
			HashMap params) throws RPCException {
		if ("shippedTo".equals(cat))
			return getShippedToMatches(match);

		return null;
	}

	private DataModel<Integer> getShippedToMatches(String match) {
		OrganizationRemote remote = (OrganizationRemote) EJBFactory
				.lookup("openelis/OrganizationBean/remote");
		DataModel<Integer> dataModel = new DataModel<Integer>();
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

			DataSet<Integer> data = new DataSet<Integer>();
			// hidden id
			data.setKey(orgId);
			// columns
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
	        for (int i=0; i<exceptionList.size();i++) {
	            //if the error is inside the org contacts table
	            if(exceptionList.get(i) instanceof TableFieldErrorException){
	                int rowindex = ((TableFieldErrorException)exceptionList.get(i)).getRowIndex();
	                itemsTable.getField(rowindex,((TableFieldErrorException)exceptionList.get(i)).getFieldName())
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
}