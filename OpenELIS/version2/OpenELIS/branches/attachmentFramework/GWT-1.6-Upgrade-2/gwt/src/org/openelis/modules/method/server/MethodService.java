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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.MethodMetaMap;
import org.openelis.modules.method.client.MethodForm;
import org.openelis.modules.method.client.MethodRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.MethodRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MethodService implements AppScreenFormServiceInt<MethodRPC, Integer> {

    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
    private static final int leftTableRowsPerPage = 9;
    
    private static final MethodMetaMap MethodMeta = new MethodMetaMap();
    
    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List methodNames;
        //if the rpc is null then we need to get the page
        if(form == null){

            form = (Form)SessionManager.getSession().getAttribute("MethodQuery");
    
            if(form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
            try{
                methodNames = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
            }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }           
            }    
        }else{
            MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        
            HashMap<String,AbstractField> fields = form.getFieldMap();
            
            try{    
                methodNames = remote.query(fields,0,leftTableRowsPerPage);
    
            }catch(Exception e){
                throw new RPCException(e.getMessage());
            }
    
        
            //need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("MethodQuery", form);
        }
        
        //fill the model with the query results
        int i=0;
        if(model == null)
            model = new DataModel<Integer>();
        else
            model.clear();
        while(i < methodNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)methodNames.get(i);
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        } 
 
        return model;
    }
    
    public MethodRPC abort(MethodRPC rpc) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.key;
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndUnlock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpc.form, methodDO);
        return rpc;
    }

    public MethodRPC commitAdd(MethodRPC rpc) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpc.form);
        List<Exception> exceptionList = remote.validateForAdd(methodDO);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);    
            return rpc;
        }
        Integer testId =null;
        try{
           testId = remote.updateMethod(methodDO);
        }catch (Exception e) {
          if(e instanceof EntityLockedException) 
            throw new RPCException(e.getMessage());
            
           exceptionList = new ArrayList<Exception>();
           exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
            
        }
        methodDO.setId(testId);
        setFieldsInRPC(rpc.form, methodDO);
        return rpc;
    }

    public MethodRPC commitDelete(MethodRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }


    public MethodRPC commitUpdate(MethodRPC rpc) throws RPCException {
       
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        MethodDO methodDO = getMethodDOFromRPC(rpc.form);
        List<Exception> exceptionList = remote.validateForUpdate(methodDO);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);    
            return rpc;
        }
        try{
            remote.updateMethod(methodDO);
        }catch (Exception e) {
          if(e instanceof EntityLockedException) 
            throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
            
        }
        setFieldsInRPC(rpc.form, methodDO);
        
        return rpc;
    }

    public MethodRPC fetch(MethodRPC rpc) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.key;
        MethodDO methodDO = remote.getMethod(methodId);
        setFieldsInRPC(rpc.form, methodDO);
        return rpc;
    }

    public MethodRPC fetchForUpdate(MethodRPC rpc) throws RPCException {
        MethodRemote remote = (MethodRemote)EJBFactory.lookup("openelis/MethodBean/remote");
        Integer methodId = rpc.key;
        MethodDO methodDO = new MethodDO();
        try{
            methodDO = remote.getMethodAndLock(methodId,SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        } 
        setFieldsInRPC(rpc.form, methodDO);
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl"));
                        
        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public MethodRPC getScreen(MethodRPC rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/method.xsl");
        return rpc;
    }

    private void setFieldsInRPC(MethodForm form, MethodDO methodDO) {
        form.id.setValue(methodDO.getId());
        form.name.setValue(methodDO.getName());
        form.description.setValue(methodDO.getDescription());
        form.reportingDescription.setValue(methodDO.getReportingDescription());
        form.isActive.setValue(methodDO.getIsActive());        
        form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveBegin()
                                                                     .getDate()));
        form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                        Datetime.DAY,
                                                        methodDO.getActiveEnd()
                                                                     .getDate()));        
    }
    
    private MethodDO getMethodDOFromRPC(MethodForm form){
      IntegerField methodId = (IntegerField)form.getField(MethodMeta.getId());
      MethodDO methodDO = new MethodDO();
      methodDO.setId(methodId.getValue());
      methodDO.setName(((String)form.getFieldValue(MethodMeta.getName())));
      DatetimeRPC activeBegin = (DatetimeRPC)form.activeBegin.getValue();
      if (activeBegin != null)
          methodDO.setActiveBegin(activeBegin.getDate());

      DatetimeRPC activeEnd = (DatetimeRPC)form.activeEnd.getValue();
      if (activeEnd != null)
          methodDO.setActiveEnd(activeEnd.getDate());

      methodDO.setDescription(form.description.getValue());
      methodDO.setIsActive(form.isActive.getValue());
      methodDO.setReportingDescription(form.reportingDescription.getValue());
      return methodDO;
    }
    
    private void setRpcErrors(List exceptionList, Form form) {
      for (int i = 0; i < exceptionList.size(); i++) { 
        if (exceptionList.get(i) instanceof FieldErrorException) {
            String nameWithRPC = ((FieldErrorException)exceptionList.get(i)).getFieldName();            
            form.getField(nameWithRPC)
                   .addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
        }
        // if the error is on the entire form
        else if (exceptionList.get(i) instanceof FormErrorException)
            form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
       }
      form.status = Form.Status.invalid;
    }
}
