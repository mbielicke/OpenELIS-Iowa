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
package org.openelis.modules.qc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameLotNumberDO;
import org.openelis.domain.InventoryItemAutoDO;
import org.openelis.domain.QcAnalyteDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.SecuritySystemUserDO;
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
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.qc.client.QCForm;
import org.openelis.modules.qc.client.QCGeneralPurposeRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AnalyteRemote;
import org.openelis.remote.CategoryRemote;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.remote.QcRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.FormUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class QCService implements
                      AppScreenFormServiceInt<QCForm, Query<TableDataRow<Integer>>>,
                      AutoCompleteServiceInt {

    private static final int leftTableRowsPerPage = 20;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale"));
    
    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        List<IdNameLotNumberDO> qcNames;
        QcRemote remote;
        IdNameLotNumberDO resultDO;
        StringObject name,lotNumber;
        int i;
        
        try {
            remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
            qcNames = remote.query(query.fields,
                                     query.page * leftTableRowsPerPage,
                                     leftTableRowsPerPage);
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }

        // fill the model with the query results
        i = 0;
        if (query.results == null)
            query.results = new TableDataModel<TableDataRow<Integer>>();
        else
            query.results.clear();
        while (i < qcNames.size() && i < leftTableRowsPerPage) {
            resultDO = (IdNameLotNumberDO)qcNames.get(i);
            name = new StringObject(resultDO.getName());
            lotNumber = new StringObject(resultDO.getLotNumber());
            query.results.add(new TableDataRow<Integer>(resultDO.getId(), 
                                                        new FieldType[] {name,lotNumber}));
            i++;
        }

        return query;   
    }
    
    public QCForm commitAdd(QCForm rpc) throws RPCException {
        QcRemote remote;
        QcDO qcDO;
        Integer qcId;
        List<QcAnalyteDO> qcaDOList;
        
        remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        qcDO = getQcDOFromRPC(rpc);
        qcaDOList = getQcAnalyteListFromRPC(rpc);             
        try {            
            qcId = remote.updateQc(qcDO, qcaDOList); 
            qcDO = remote.getQc(qcId);
            setFieldsInRPC(rpc,qcDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {            
            throw new RPCException(e.getMessage());            
        }
        
        return rpc;
    }   

    public QCForm commitUpdate(QCForm rpc) throws RPCException {
        QcRemote remote;
        QcDO qcDO;
        List<QcAnalyteDO> qcaDOList;
        
        remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        qcDO = getQcDOFromRPC(rpc);
        qcaDOList = getQcAnalyteListFromRPC(rpc);             
        try {            
            remote.updateQc(qcDO, qcaDOList); 
            qcDO = remote.getQc(rpc.entityKey);
            setFieldsInRPC(rpc,qcDO);
        } catch(ValidationErrorsList e) {
            setRpcErrors(e.getErrorList(), rpc);
            return rpc;
        } catch (Exception e) {            
            throw new RPCException(e.getMessage());            
        }
        
        return rpc;
    }
    
    public QCForm commitDelete(QCForm rpc) throws RPCException {
        return null;
    }

    public QCForm abort(QCForm rpc) throws RPCException {
        QcDO qcDO;
        List<QcAnalyteDO> qcAnaDOList;
        QcRemote remote;
        
        remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        qcDO = remote.getQcAndUnlock(rpc.entityKey,SessionManager.getSession().getId());
        qcAnaDOList = remote.getQcAnalytes(rpc.entityKey);
        
        setFieldsInRPC(rpc,qcDO);    
        fillQcAnalyteTable(qcAnaDOList,rpc);
        return rpc;
    }

    public QCForm fetch(QCForm rpc) throws RPCException {
        QcDO qcDO;
        List<QcAnalyteDO> qcAnaDOList;
        QcRemote remote;
        
        remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        qcDO = remote.getQc(rpc.entityKey);
        qcAnaDOList = remote.getQcAnalytes(rpc.entityKey);
        
        setFieldsInRPC(rpc,qcDO);    
        fillQcAnalyteTable(qcAnaDOList,rpc);
        return rpc;
    }

    public QCForm fetchForUpdate(QCForm rpc) throws RPCException {
        QcDO qcDO;
        QcRemote remote;
        List<QcAnalyteDO> qcAnaDOList;
        
        remote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
        try {
            qcDO = remote.getQcAndLock(rpc.entityKey,
                                          SessionManager.getSession().getId());            
            setFieldsInRPC(rpc,qcDO);  
            qcAnaDOList = remote.getQcAnalytes(rpc.entityKey);
            fillQcAnalyteTable(qcAnaDOList,rpc);
        } catch(Exception ex) {
            throw new RPCException(ex.getMessage());
        }
        
        return rpc;        
    }

    public QCForm getScreen(QCForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/qc.xsl");               
        return rpc;
    }

    
    public QCGeneralPurposeRPC getEntryIdForEntryText(QCGeneralPurposeRPC rpc) {
        CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
        try {
            rpc.key = remote.getEntryIdForEntry(rpc.stringValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rpc;
    }

    public TableDataModel getMatches(String cat,TableDataModel model,String match,
                                     HashMap<String, FieldType> params) throws RPCException {
        TableDataModel<TableDataRow<Integer>> dataModel;
        List<IdNameDO> analytes;
        List<InventoryItemAutoDO> items;
        List<SecuritySystemUserDO> users;
        InventoryItemRemote invRemote;
        AnalyteRemote anaRemote;
        QcRemote qcRemote;
        
        dataModel = null;        
        if("inventoryItem".equals(cat)) {
            invRemote = (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
            items = invRemote.inventoryItemStoreAutoCompleteLookupByName(match.trim()+"%",10, false, true);
            dataModel = getInvItemAutocompleteModel(items);
        } else if("preparedBy".equals(cat)) {
            qcRemote = (QcRemote)EJBFactory.lookup("openelis/QcBean/remote");
            users =  qcRemote.preparedByAutocompleteByName(match.trim()+"%", 10);
            dataModel = getPrepByAutocompleteModel(users);
        } else if("analyte".equals(cat)) {
            anaRemote = (AnalyteRemote)EJBFactory.lookup("openelis/AnalyteBean/remote");
            analytes = anaRemote.autoCompleteLookupByName(match.trim() + "%", 10);
            dataModel = getAutocompleteModel(analytes);
        }
        return dataModel;
    }
    
    private TableDataModel<TableDataRow<Integer>> getInvItemAutocompleteModel(List<InventoryItemAutoDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel;
        TableDataRow<Integer> data;
        Integer itemId;
        InventoryItemAutoDO resultDO;
        String name, store;
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        
        for(int i=0; i < entries.size(); i++){
            resultDO = (InventoryItemAutoDO) entries.get(i);
            
            itemId = resultDO.getId();
            name = resultDO.getName();
            store = resultDO.getStore();
            
            data = new TableDataRow<Integer>(itemId,
                                             new FieldType[] {new StringObject(name),
                                                              new StringObject(store)});
                        
            //add the dataset to the datamodel
            dataModel.add(data);                            
        }       
        
        return dataModel;
    }
    
    private TableDataModel<TableDataRow<Integer>> getPrepByAutocompleteModel(List<SecuritySystemUserDO> entries){
        TableDataModel<TableDataRow<Integer>> dataModel;
        SecuritySystemUserDO element;
        Integer entryId;
        String entryText;
        TableDataRow<Integer> data;
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            element = (SecuritySystemUserDO)iter.next();
            entryId = element.getId();
            entryText = element.getLoginName();
            data = new TableDataRow<Integer>(entryId,new StringObject(entryText));
            dataModel.add(data);
        }
        
        return dataModel;
    }
    

    public TableDataModel getAutocompleteModel(List<IdNameDO> entries) throws RPCException {
        TableDataModel<TableDataRow<Integer>> dataModel;
        IdNameDO element;
        Integer entryId;
        String entryText;
        TableDataRow<Integer> data;
        
        dataModel = new TableDataModel<TableDataRow<Integer>>();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {            
            element = (IdNameDO)iter.next();
            entryId = element.getId();                   
            entryText = element.getName();            
            data = new TableDataRow<Integer>(entryId,new StringObject(entryText));                                      
            dataModel.add(data);
        }       
        
        return dataModel;
    } 
    
    private QcDO getQcDOFromRPC(QCForm rpc) {
        QcDO qcDO;
        DatetimeRPC prepDate,expDate,usbDate;
        
        qcDO = new QcDO();
        
        qcDO.setId(rpc.id.getValue());
        qcDO.setName(rpc.name.getValue());
        qcDO.setInventoryItemId((Integer)rpc.inventoryItemId.getSelectedKey());
        qcDO.setIsSingleUse(rpc.isSingleUse.getValue());
        qcDO.setLotNumber(rpc.lotNumber.getValue());
        qcDO.setPreparedById((Integer)rpc.preparedById.getSelectedKey());
        qcDO.setPreparedVolume(rpc.preparedVolume.getValue());
        qcDO.setPreparedUnitId((Integer)rpc.preparedUnitId.getSelectedKey());
        qcDO.setSource(rpc.source.getValue());
        qcDO.setTypeId((Integer)rpc.typeId.getSelectedKey());
        
        prepDate = rpc.preparedDate.getValue(); 
        expDate = rpc.expireDate.getValue();
        usbDate = rpc.usableDate.getValue();
        
        if(prepDate != null)
            qcDO.setPreparedDate(prepDate.getDate());
        if(expDate != null)
            qcDO.setExpireDate(expDate.getDate());
        if(usbDate != null)
            qcDO.setUsableDate(usbDate.getDate());
                        
        return qcDO;
    }
    
    private void setRpcErrors(ArrayList<RPCException> exceptionList, QCForm form) {
        HashMap<String,AbstractField> map;
        String fieldName;
        int index;
        TableFieldErrorException exc;
        
        map = null;
        if(exceptionList.size() > 0)
            map = FormUtil.createFieldMap(form);
                  //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the entries table
            if(exceptionList.get(i) instanceof TableFieldErrorException){       
                exc = (TableFieldErrorException)exceptionList.get(i);
                fieldName = exc.getFieldName();                    
                index =  exc.getRowIndex();                    
                form.qcAnalyteTable.getField(index, fieldName)
                    .addError(openElisConstants.getString(((FieldErrorException)exc).getMessage()));
                //if the error is on the field
            } else if(exceptionList.get(i) instanceof FieldErrorException) {
                 map.get(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
                //if the error is on the entire form
            } else if(exceptionList.get(i) instanceof FormErrorException) {
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
            }
        }   
        form.status = Form.Status.invalid;
        
    }

    private void setFieldsInRPC(QCForm rpc, QcDO qcDO) {
        TableDataModel<TableDataRow<Integer>> prepModel,invModel;
        Datetime date;
        
        rpc.id.setValue(qcDO.getId());
        rpc.name.setValue(qcDO.getName());
        rpc.typeId.setValue(new TableDataRow<Integer>(qcDO.getTypeId()));
        
        prepModel = new TableDataModel<TableDataRow<Integer>>();
        prepModel.add(new TableDataRow<Integer>(qcDO.getPreparedById(),
                            new StringObject(qcDO.getPreparedByName())));
        
        rpc.preparedById.setModel(prepModel);
        rpc.preparedById.setValue(prepModel.get(0));
        
        invModel = new TableDataModel<TableDataRow<Integer>>();
        rpc.inventoryItemId.setModel(invModel);        
        if(qcDO.getInventoryItemId() != null) {            
            invModel.add(new TableDataRow<Integer>(qcDO.getInventoryItemId(),
                            new StringObject(qcDO.getInventoryItemName()))); 
            
            rpc.inventoryItemId.setValue(invModel.get(0));
        }
        
                
        rpc.source.setValue(qcDO.getSource());
        rpc.lotNumber.setValue(qcDO.getLotNumber());
        rpc.isSingleUse.setValue(qcDO.getIsSingleUse());
        
        date = qcDO.getPreparedDate();
        if(date != null && date.getDate() != null) {
            rpc.preparedDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.MINUTE,
                                                               date.getDate()));
        }
        
        date = qcDO.getUsableDate();
        if(date != null && date.getDate() != null) {
            rpc.usableDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.MINUTE,
                                                             date.getDate()));
        }
        
        date = qcDO.getExpireDate();
        if(date != null && date.getDate() != null) {
            rpc.expireDate.setValue(DatetimeRPC.getInstance(Datetime.YEAR,Datetime.MINUTE,
                                                             date.getDate()));
        }
        
        rpc.preparedVolume.setValue(qcDO.getPreparedVolume());
        rpc.preparedUnitId.setValue(new TableDataRow<Integer>(qcDO.getPreparedUnitId()));
        
    }
    
    private void fillQcAnalyteTable(List<QcAnalyteDO> qcAnaDOList, QCForm rpc) {
        TableDataModel<TableDataRow<Integer>> tmodel,rmodel;
        TableDataRow<Integer> row,analyteSet;
        QcAnalyteDO qcaDO;
                
        tmodel = rpc.qcAnalyteTable.getValue();        
        tmodel.clear();
        
        for(int i=0; i < qcAnaDOList.size(); i++) {
            qcaDO = qcAnaDOList.get(i);
            row = tmodel.createNewSet();
            row.key = qcaDO.getId();
            
            analyteSet = new TableDataRow<Integer>(qcaDO.getAnalyteId(),new StringObject(qcaDO.getAnalyteName()));       
            rmodel = new TableDataModel<TableDataRow<Integer>>();
            rmodel.add(analyteSet);            
            ((DropDownField)row.cells[0]).setModel(rmodel);
            row.cells[0].setValue(analyteSet);
            
            row.cells[1].setValue(new TableDataRow<Integer>(qcaDO.getTypeId()));
            row.cells[2].setValue(qcaDO.getIsTrendable());
            row.cells[3].setValue(qcaDO.getValue());
            
            tmodel.add(row);
        }
    }
    
    private List<QcAnalyteDO> getQcAnalyteListFromRPC(QCForm rpc) {
        QcAnalyteDO qcaDO;
        TableDataRow<Integer> row;
        TableDataModel<TableDataRow<Integer>> model;
        List<QcAnalyteDO> qcaDOList;
        List<TableDataRow<Integer>> deletions;
        int i;
        
        model = rpc.qcAnalyteTable.getValue();
        qcaDOList = new ArrayList<QcAnalyteDO>();
        
        for(i = 0; i < model.size(); i++) {
            row = model.get(i);
            qcaDO = new QcAnalyteDO();
            qcaDO.setDelete(false);
            qcaDO.setId(row.key);
            qcaDO.setAnalyteId((Integer)((DropDownField<Integer>)row.cells[0]).getSelectedKey());
            qcaDO.setTypeId((Integer)((DropDownField<Integer>)row.cells[1]).getSelectedKey());
            qcaDO.setQcId(rpc.entityKey);
            qcaDO.setIsTrendable((String)row.cells[2].getValue());
            qcaDO.setValue((String)row.cells[3].getValue());            
            qcaDOList.add(qcaDO);
        }
        
        deletions = model.getDeletions();
        if(deletions != null) {
            for(i = 0; i < deletions.size(); i++) {
                row = deletions.get(i);
                qcaDO = new QcAnalyteDO();
                qcaDO.setDelete(true);
                qcaDO.setId(row.key);
                qcaDOList.add(qcaDO);
            }
            deletions.clear();
        }
        
        return qcaDOList;
    }
}
