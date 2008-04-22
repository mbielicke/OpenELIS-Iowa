package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.SystemVariableDO;

@Remote
public interface SystemVariableRemote {
   
    // method to return SystemVariable 
    public SystemVariableDO getSystemVariable(Integer sysVarId);
    
    public SystemVariableDO getSystemVariableAndUnlock(Integer sysVarId);
    
    public SystemVariableDO getSystemVariableAndLock(Integer sysVarId)throws Exception;    
     
    //  commit a change to SystemVariable or insert a new SystemVariable
    public Integer updateSystemVariable(SystemVariableDO sysVarDO)throws Exception;
    
    //  method to query for SystemVariable
    public List query(HashMap fields, int first, int max) throws Exception;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
    
    public void deleteSystemVariable(Integer sysVarId) throws Exception;
    
    public List<Exception> validateforAdd(SystemVariableDO sysVarDO);
    
    public List<Exception> validateforUpdate(SystemVariableDO sysVarDO);
       
}
