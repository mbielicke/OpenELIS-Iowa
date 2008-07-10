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
package org.openelis.modules.inventoryReceipt.client;

import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.widget.AutoCompleteParamsInt;

public class InventoryReceiptAutoParams implements AutoCompleteParamsInt{

    public HashMap<String, AbstractField> getParams(FormRPC rpc) {
        HashMap params = new HashMap();
        params.put("addToExisting", rpc.getField("addToExisting"));
        
        return params;
    }

}
