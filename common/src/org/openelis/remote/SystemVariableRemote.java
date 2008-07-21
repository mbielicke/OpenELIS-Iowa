/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.SystemVariableDO;

@Remote
public interface SystemVariableRemote {
   
    // method to return SystemVariable 
    public SystemVariableDO getSystemVariable(Integer sysVarId);
    
    public SystemVariableDO getSystemVariableAndUnlock(Integer sysVarId, String session);
    
    public SystemVariableDO getSystemVariableAndLock(Integer sysVarId, String session)throws Exception;    
     
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
