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
    
    
    public void deleteSystemVariable(Integer sysVarId) throws Exception;
    
    public List<Exception> validateforAdd(SystemVariableDO sysVarDO);
    
    public List<Exception> validateforUpdate(SystemVariableDO sysVarDO);
       
}
