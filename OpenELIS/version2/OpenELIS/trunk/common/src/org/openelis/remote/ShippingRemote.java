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

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface ShippingRemote {

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int i, int rowPP) throws Exception;

    /*//method to return shipping record
    public ShippingDO getShipment(Integer shippingId);
    
    //method to unlock entity and return shipping record
    public ShippingDO getShipmentAndUnlock(Integer shippingId);
    
    //method to lock entity and return shipping record
    public ShippingDO getShipmentAndLock(Integer shippingId) throws Exception;
    
    public List getTrackingNumbers(Integer shippingId);
    
    public List getShippingItems(Integer shippingId);
    
    //commit a change to shipping record, or insert a new shipping record
    public Integer updateShipment(ShippingDO shippingDO, List<ShippingItemDO> shippingItems, List<ShippingTrackingDO> trackingNumbers, NoteViewDO shippingNotes) throws Exception;
    
    //method to query for shipments
   // public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception;
     
    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception;
    
    public NoteViewDO getShippingNote(Integer shippingId);*/
}
