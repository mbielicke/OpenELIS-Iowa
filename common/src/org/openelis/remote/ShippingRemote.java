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

import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.domain.ShippingDO;
import org.openelis.domain.ShippingItemDO;
import org.openelis.domain.ShippingTrackingDO;

@Remote
public interface ShippingRemote {

    //method to return shipping record
    public ShippingDO getShipment(Integer shippingId);
    
    //method to unlock entity and return shipping record
    public ShippingDO getShipmentAndUnlock(Integer shippingId);
    
    //method to lock entity and return shipping record
    public ShippingDO getShipmentAndLock(Integer shippingId) throws Exception;
    
    public List getTrackingNumbers(Integer shippingId);
    
    public List getShippingItems(Integer shippingId);
    
    //commit a change to shipping record, or insert a new shipping record
    public Integer updateShipment(ShippingDO shippingDO, List<ShippingItemDO> shippingItems, List<ShippingTrackingDO> trackingNumbers) throws Exception;
    
    //method to query for shipments
    public List query(HashMap fields, int first, int max) throws Exception;
     
    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception;
    
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(ShippingDO shippingDO, List shippingItems, List trackngNumbers);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(ShippingDO shippingDO, List shippingItems, List trackngNumbers);
}
