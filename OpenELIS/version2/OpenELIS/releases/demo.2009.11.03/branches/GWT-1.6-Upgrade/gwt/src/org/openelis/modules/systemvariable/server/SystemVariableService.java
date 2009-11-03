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

import org.openelis.domain.IdNameDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemVariableService implements AppScreenFormServiceInt<DefaultRPC,Integer> {

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9;  
    
    private static SystemVariableMetaMap Meta = new SystemVariableMetaMap();
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
     public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
         List sysVars = new ArrayList();
        if(form == null){           
            
            form = (Form)SessionManager.getSession().getAttribute("SystemVariableQuery");
            
            if(form == null)
                throw new QueryException(openElisConstants.getString("queryExpiredException"));
    
             try{                 
                 SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote"); 
                 sysVars = remote.query(form.getFieldMap(), (model.getPage()*leftTableRowsPerPage), leftTableRowsPerPage+1);
                 
             }catch(Exception e){
                if(e instanceof LastPageException){
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                }else{
                    throw new RPCException(e.getMessage()); 
                }
             }
        
         } else{
             SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote"); 
             
             HashMap<String,AbstractField> fields = form.getFieldMap();             

             try{
                 sysVars = remote.query(fields,0,leftTableRowsPerPage);
    
             }catch(Exception e){
                 e.printStackTrace();
                 throw new RPCException(e.getMessage());
             } 
             
//           need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("SystemVariableQuery", form);
             }    
     
     int i=0;
     
     if(model == null)
         model = new DataModel<Integer>();
     else
         model.clear();

     while(i < sysVars.size() && i < leftTableRowsPerPage) {
         IdNameDO resultDO = (IdNameDO)sysVars.get(i);
         model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
         i++;
      }  
     
         return model;
    }

    public DefaultRPC commitAdd(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        SystemVariableDO sysVarDO = getSystemVariableDOFromRPC(rpc.form);
        Integer svId = null;
        List<Exception> exceptionList = remote.validateforAdd(sysVarDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            return rpc;
        }
        try{ 
         svId = remote.updateSystemVariable(sysVarDO);
        }catch(Exception e){
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        } 
        
        sysVarDO.setId(svId);
        
        setFieldsInRPC(rpc.form, sysVarDO);
        return rpc;
    }

    public DefaultRPC commitUpdate(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        SystemVariableDO sysVarDO = getSystemVariableDOFromRPC(rpc.form);
        Integer svId = null;
        List<Exception> exceptionList = remote.validateforUpdate(sysVarDO);
        if(exceptionList.size() > 0){
            //we need to get the keys and look them up in the resource bundle for internationalization
            setRpcErrors(exceptionList, rpc.form);   
            return rpc;
        }
        try{ 
         svId = remote.updateSystemVariable(sysVarDO);
        }catch(Exception e){
            if(e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
            
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
            
            setRpcErrors(exceptionList, rpc.form);
            
            return rpc;
        }
        
        setFieldsInRPC(rpc.form, sysVarDO);
        
        return rpc;
    }


    public DefaultRPC commitDelete(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        try{
            remote.deleteSystemVariable(rpc.key);
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        
        setFieldsInRPC(rpc.form, new SystemVariableDO());
        return rpc;
    }


    public DefaultRPC abort(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.key;
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndUnlock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        
        setFieldsInRPC(rpc.form, svDO);
        return rpc;
    }


    public DefaultRPC fetch(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.key;
        
        SystemVariableDO svDO = remote.getSystemVariable(svId);
        setFieldsInRPC(rpc.form, svDO);
        return rpc;
    }


    public DefaultRPC fetchForUpdate(DefaultRPC rpc) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = rpc.key;
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndLock(svId, SessionManager.getSession().getId());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        setFieldsInRPC(rpc.form, svDO);
        return rpc;
    }


    public String getXML() throws RPCException {
            return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/systemVariable.xsl"); 
     }


    public HashMap<String, FieldType> getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }


    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
    	// TODO Auto-generated method stub
    	return null;
    }

    public DefaultRPC getScreen(DefaultRPC rpc){
        return rpc;
    }

    private void setFieldsInRPC(Form form, SystemVariableDO svDO){
        form.setFieldValue(Meta.getId(), svDO.getId());
        form.setFieldValue(Meta.getName(),svDO.getName());
        form.setFieldValue(Meta.getValue(),svDO.getValue());        
    }
    
    
    private SystemVariableDO getSystemVariableDOFromRPC(Form form){
        SystemVariableDO sysVarDO = new SystemVariableDO();
        Integer svId = (Integer) form.getFieldValue(Meta.getId());        
        sysVarDO.setId(svId);
        sysVarDO.setName(((String)form.getFieldValue(Meta.getName())));
        sysVarDO.setValue(((String)form.getFieldValue(Meta.getValue())));
        return sysVarDO;
    }


	private void setRpcErrors(List exceptionList, Form form){
        //we need to get the keys and look them up in the resource bundle for internationalization
        for (int i=0; i<exceptionList.size();i++) {
            //if the error is inside the org contacts table
             if(exceptionList.get(i) instanceof FieldErrorException)
                form.getField(((FieldErrorException)exceptionList.get(i)).getFieldName()).addError(openElisConstants.getString(((FieldErrorException)exceptionList.get(i)).getMessage()));
            //if the error is on the entire form
            else if(exceptionList.get(i) instanceof FormErrorException)
                form.addError(openElisConstants.getString(((FormErrorException)exceptionList.get(i)).getMessage()));
        }   
        form.status = Form.Status.invalid;
    }
}
