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
package org.openelis.modules.method.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.MethodRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class MethodService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 9;
    
    private static final MethodMetaMap MethodMeta = new MethodMetaMap();
    
    public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
        List methodNames;
        //if the rpc is null then we need to get the page
        if(rpcSend == null){

            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("MethodQuery");
    
            if(rpc == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
            try{
                methodNames = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        
            HashMap<String,AbstractField> fields = rpcSend.getFieldMap();
            
            try{    
                methodNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("MethodQuery", rpcSend);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel();
        else
            model.clear();
        while(i < methodNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)methodNames.get(i);
 
            DataSet row = new DataSet();
            NumberObject id = new NumberObject(resultDO.getId());
            StringObject name = new StringObject(resultDO.getName());
            
            row.setKey(id);         
            row.add(name);
            model.add(row);
            i++;
        } 
 
        return model;
    }
    
    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = (Integer)((DataObject)key.getKey()).getValue();
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndUnlock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpcReturn, methodDO);
        return rpcReturn;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpcSend);
        List<Exception> exceptionList = remote.validateForAdd(methodDO);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);    
            return rpcSend;
        }
        Integer testId =null;
        try{
           testId = remote.updateMethod(methodDO);
        }catch (Exception e) {
          if(e instanceof EntityLockedException) 
            throw new RPCException(e.getMessage());
            
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
            
        }
        methodDO.setId(testId);
        setFieldsInRPC(rpcReturn, methodDO);
        return rpcReturn;
    }

    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }


    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
       
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpcSend);
        List<Exception> exceptionList = remote.validateForUpdate(methodDO);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpcSend);    
            return rpcSend;
        }
        try{
            remote.updateMethod(methodDO);
        }catch (Exception e) {
          if(e instanceof EntityLockedException) 
            throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
            
        }
        setFieldsInRPC(rpcReturn, methodDO);
        
        return rpcReturn;
    }

    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = (Integer)((DataObject)key.getKey()).getValue();
        MethodDO methodDO = remote.getMethod(methodId);
        setFieldsInRPC(rpcReturn, methodDO);
        return rpcReturn;
    }

    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = (Integer)((DataObject)key.getKey()).getValue();
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndLock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpcReturn, methodDO);
        return rpcReturn;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl"));
                        
        HashMap<String, Data> map = new HashMap<String, Data>();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    private void setFieldsInRPC(FormRPC rpcReturn, MethodDO methodDO) {
        rpcReturn.setFieldValue(MethodMeta.getId(), methodDO.getId());
        rpcReturn.setFieldValue(MethodMeta.getName(), methodDO.getName());
        rpcReturn.setFieldValue(MethodMeta.getDescription(),
                                methodDO.getDescription());
        rpcReturn.setFieldValue(MethodMeta.getReportingDescription(),
                                methodDO.getReportingDescription());
        rpcReturn.setFieldValue(MethodMeta.getIsActive(),
                                methodDO.getIsActive());        
        rpcReturn.setFieldValue(MethodMeta.getActiveBegin(),
                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveBegin()
                                                                     .getDate()));
        rpcReturn.setFieldValue(MethodMeta.getActiveEnd(),
                                DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveEnd()
                                                                     .getDate()));        
    }
    
    private MethodDO getMethodDOFromRPC(FormRPC rpcSend){
      NumberField methodId = (NumberField)rpcSend.getField(MethodMeta.getId());
      MethodDO methodDO = new MethodDO();
      methodDO.setId((Integer)methodId.getValue());
      methodDO.setName(((String)rpcSend.getFieldValue(MethodMeta.getName())));
      DatetimeRPC activeBegin = (DatetimeRPC)rpcSend.getFieldValue(MethodMeta.getActiveBegin());
      if (activeBegin != null)
          methodDO.setActiveBegin(activeBegin.getDate());

      DatetimeRPC activeEnd = (DatetimeRPC)rpcSend.getFieldValue(MethodMeta.getActiveEnd());
      if (activeEnd != null)
          methodDO.setActiveEnd(activeEnd.getDate());

      methodDO.setDescription((String)rpcSend.getFieldValue(MethodMeta.getDescription()));
      methodDO.setIsActive((String)rpcSend.getFieldValue(MethodMeta.getIsActive()));
      methodDO.setReportingDescription((String)rpcSend.getFieldValue(MethodMeta.getReportingDescription()));
      return methodDO;
    }
    
    private void setRpcErrors(List exceptionList, FormRPC rpcSend) {
      for (int i = 0; i < exceptionList.size(); i++) { 
        if (exceptionList.get(i) instanceof FieldErrorException) {
            String nameWithRPC = ((FieldErrorException)exceptionList.get(i)).getFieldName();            
            rpcSend.getField(nameWithRPC)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
        }
        // if the error is on the entire form
        else if (exceptionList.get(i) instanceof FormErrorException)
            rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
       }
      rpcSend.status = Status.invalid;
    }
}
