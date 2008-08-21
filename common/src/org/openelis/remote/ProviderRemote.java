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

import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;

@Remote
public interface ProviderRemote {
    
    //  method to return provider 
    public ProviderDO getProvider(Integer providerId);
        
    public ProviderDO getProviderAndUnlock(Integer providerId, String session);
    
    public ProviderDO getProviderAndLock(Integer providerId, String session)throws Exception;
    
    //commit a change to provider, or insert a new provider
    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO, List addresses) throws Exception;
    
    //method to return just notes
    public List getProviderNotes(Integer providerId);
    
    //method to return just provider addresses
    public List getProviderAddresses(Integer providerId);
    
     //method to query for provider
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(ProviderDO providerDO, List<ProviderAddressDO> addresses);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(ProviderDO providerDO, List<ProviderAddressDO> addresses);
}
