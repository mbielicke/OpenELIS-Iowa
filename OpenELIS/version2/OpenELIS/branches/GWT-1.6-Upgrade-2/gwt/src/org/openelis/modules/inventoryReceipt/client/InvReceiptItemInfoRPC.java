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
package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.common.RPC;

public class InvReceiptItemInfoRPC extends RPC<InvReceiptItemInfoForm,Integer>{

    private static final long serialVersionUID = 1L;

    //item info
    //public String itemDesc;
    //public String itemStore;
    //public String itemDisUnit;
    //public String itemIsBulk;
    //public String itemIsLotMaintained;
    //public String itemIsSerialMaintained;
    //public String itemDisUnit;
    
    //address info
    //public String multUnit;
    //public String streetAddress;
    //public String city;
    //public String state;
    //public String zipCode;
    
    //location info
    //public DataModel<Integer> inventoryLocation;
    //String lotNum;
    //public DatetimeRPC expDate;
    
    //disable flags
    public boolean disableOrderId = false;
    public boolean disableInvItem = false;
    public boolean disableUpc = false;
    public boolean disableOrg = false;
    
    public Integer receiptId;
    public Integer orderItemId;
    
    public String itemIsBulk;
    public String itemIsLotMaintained;
    public String itemIsSerialMaintained;
    
    //public DataModel<Integer> fromInvItem;
    //public DataModel<Integer> fromInvLoc;

    /*
    public Integer orderNumber;
    public DatetimeRPC receivedDate;
    public String upc;
    public DataModel<Integer> inventoryItem;
    public DataModel<Integer> org;
    public Integer qtyReceived;
    public Integer qtyRequested;
    public Double cost;
    public String qc;
    public String extRef;
    public String multUnit;
    public String streetAddress;
    public String city;
    public String state;
    public String zipCode;
    public String itemDesc;
    public String itemStore;
    public String itemDisUnit;
    public String itemIsBulk;
    public String itemIsLotMaintained;
    public String itemIsSerialMaintained;
    public Integer orderItemId;
    public String addToExisting;
    public DataModel<Integer> inventoryLocation;
    public String lotNumber;
    public DatetimeRPC expDate;
    public Integer receiptId;
    public Integer transReceiptOrder;
    public Integer qtyOnHand;
    public DataModel<Integer> fromInvItem;
    public DataModel<Integer> fromInvLoc;
    */
}