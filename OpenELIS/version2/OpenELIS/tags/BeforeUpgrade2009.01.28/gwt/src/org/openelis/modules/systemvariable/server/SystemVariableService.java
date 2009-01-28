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
package org.openelis.modules.systemvariable.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class SystemVariableService implements AppScreenFormServiceInt<FormRPC, DataSet, DataModel> {

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9;  
    
    private static SystemVariableMetaMap Meta = new SystemVariableMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
     public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
         List sysVars = new ArrayList();
        if(rpcSend == null){           
            
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("SystemVariableQuery");
            
            if(rpc == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));
    
             try{                 
                 SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote"); 
                 sysVars = remote.query(rpc.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
        
         } else{
             SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote"); 
             
             HashMap<String,AbstractField> fields = rpcSend.getFieldMap();             

             try{
                 sysVars = remote.query(fields,0,leftTableRowsPerPage);
    
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             } 
             
//           need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("SystemVariableQuery", rpcSend);
             }    
     
     int i=0;
     
     if(model == null)
         model = new DataModel();
     else
         model.clear();

     while(i < sysVars.size() && i < leftTableRowsPerPage) {
         IdNameDO resultDO = (IdNameDO)sysVars.get(i);
         //qaEvent id
         Integer idResult = resultDO.getId();
         //qaEvent name
         String nameResult = resultDO.getName();             

         DataSet row = new DataSet();
         
         NumberObject id = new NumberObject(NumberObject.Type.INTEGER);

         StringObject svname = new StringObject();

         id.setValue(idResult);

          svname.setValue(nameResult);                   
         row.setKey(id);          

         row.add(svname);

         model.add(row);
         i++;
      }  
     
         return model;
    }

    public FormRPC commitAdd(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        SystemVariableDO sysVarDO = getSystemVariableDOFromRPC(rpcSend);
        Integer svId = null;
        List<Exception> exceptionList = remote.validateforAdd(sysVarDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);   
            return rpcSend;
        }
        try{ 
         svId = remote.updateSystemVariable(sysVarDO);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        } 
        
        sysVarDO.setId(svId);
        
        setFieldsInRPC(rpcReturn, sysVarDO);
        return rpcReturn;
    }

    public FormRPC commitUpdate(FormRPC rpcSend, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        SystemVariableDO sysVarDO = getSystemVariableDOFromRPC(rpcSend);
        Integer svId = null;
        List<Exception> exceptionList = remote.validateforUpdate(sysVarDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpcSend);   
            return rpcSend;
        }
        try{ 
         svId = remote.updateSystemVariable(sysVarDO);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpcSend);
            
            return rpcSend;
        }
        
        setFieldsInRPC(rpcReturn, sysVarDO);
        
        return rpcReturn;
    }


    public FormRPC commitDelete(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        try{
            remote.deleteSystemVariable((Integer)((DataObject)key.getKey()).getValue());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        
        setFieldsInRPC(rpcReturn, new SystemVariableDO());
        return rpcReturn;
    }


    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)((DataObject)key.getKey()).getValue();
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndUnlock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        
        setFieldsInRPC(rpcReturn, svDO);
        return rpcReturn;
    }


    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)((DataObject)key.getKey()).getValue();
        
        SystemVariableDO svDO = remote.getSystemVariable(svId);
        setFieldsInRPC(rpcReturn, svDO);
        return rpcReturn;
    }


    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)((DataObject)key.getKey()).getValue();
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndLock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        setFieldsInRPC(rpcReturn, svDO);
        return rpcReturn;
    }


    public String getXML() throws RPCException {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/systemVariable.xsl"); 
     }


    public HashMap getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }


    public HashMap getXMLData(HashMap args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }


    private void setFieldsInRPC(FormRPC rpcReturn, SystemVariableDO svDO){
        rpcReturn.setFieldValue(Meta.getId(), svDO.getId());
        rpcReturn.setFieldValue(Meta.getName(),svDO.getName());
        rpcReturn.setFieldValue(Meta.getValue(),svDO.getValue());        
    }
    
    
    private SystemVariableDO getSystemVariableDOFromRPC(FormRPC rpcsend){
        SystemVariableDO sysVarDO = new SystemVariableDO();
        Integer svId = (Integer) rpcsend.getFieldValue(Meta.getId());        
        sysVarDO.setId(svId);
        sysVarDO.setName(((String)rpcsend.getFieldValue(Meta.getName())));
        sysVarDO.setValue(((String)rpcsend.getFieldValue(Meta.getValue())));
        return sysVarDO;
    }


	private void setRpcErrors(List exceptionList, FormRPC rpcSend){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
             if(exceptionList.get(i) instanceof FieldErrorException)
                rpcSend.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                rpcSend.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        rpcSend.status = Status.invalid;
    }
}
