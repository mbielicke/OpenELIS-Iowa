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
import org.openelis.gwt.common.IForm;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.QueryNotFoundException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.meta.SystemVariableMeta;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemVariableRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class SystemVariableService implements AppScreenFormServiceInt {

    private static final long serialVersionUID = 1L;
    private static final int leftTableRowsPerPage = 9;  
    
    private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
    
     public DataModel commitQuery(FormRPC rpcSend, DataModel model) throws RPCException {
         List sysVars = new ArrayList();
        if(rpcSend == null){           
            
            FormRPC rpc = (FormRPC)SessionManager.getSession().getAttribute("SystemVariableQuery");
            
            if(rpc == null)
                throw new QueryNotFoundException(openElisConstants.getString("queryExpiredException"));
    
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
     model.clear();

     while(i < sysVars.size() && i < leftTableRowsPerPage) {
         IdNameDO resultDO = (IdNameDO)sysVars.get(i);
         //qaEvent id
         Integer idResult = resultDO.getId();
         //qaEvent name
         String nameResult = resultDO.getName();             

         DataSet row = new DataSet();
         
         NumberObject id = new NumberObject(NumberObject.INTEGER);

         StringObject svname = new StringObject();

         id.setValue(idResult);

          svname.setValue(nameResult);                   
         row.setKey(id);          

         row.addObject(svname);

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
            remote.deleteSystemVariable((Integer)key.getKey().getValue());
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }
        
        setFieldsInRPC(rpcReturn, new SystemVariableDO());
        return rpcReturn;
    }


    public FormRPC abort(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)key.getKey().getValue();
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndUnlock(svId);
        }catch(Exception ex){
            throw new RPCException(ex.getMessage());
        }  
        
        setFieldsInRPC(rpcReturn, svDO);
        return rpcReturn;
    }


    public FormRPC fetch(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)key.getKey().getValue();
        
        SystemVariableDO svDO = remote.getSystemVariable(svId);
        setFieldsInRPC(rpcReturn, svDO);
        return rpcReturn;
    }


    public FormRPC fetchForUpdate(DataSet key, FormRPC rpcReturn) throws RPCException {
        SystemVariableRemote remote = (SystemVariableRemote)EJBFactory.lookup("openelis/SystemVariableBean/remote");
        Integer svId = (Integer)key.getKey().getValue();
        
        SystemVariableDO svDO = null;
        try{
          svDO = remote.getSystemVariableAndLock(svId);
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
        rpcReturn.setFieldValue(SystemVariableMeta.ID, svDO.getId());
        rpcReturn.setFieldValue(SystemVariableMeta.NAME,svDO.getName());
        rpcReturn.setFieldValue(SystemVariableMeta.VALUE,svDO.getValue());        
    }
    
    
    private SystemVariableDO getSystemVariableDOFromRPC(FormRPC rpcsend){
        SystemVariableDO sysVarDO = new SystemVariableDO();
        Integer svId = (Integer) rpcsend.getFieldValue(SystemVariableMeta.ID);        
        sysVarDO.setId(svId);
        sysVarDO.setName(((String)rpcsend.getFieldValue(SystemVariableMeta.NAME)));
        sysVarDO.setValue(((String)rpcsend.getFieldValue(SystemVariableMeta.VALUE)));
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
        rpcSend.status = IForm.INVALID_FORM;
    }
}
