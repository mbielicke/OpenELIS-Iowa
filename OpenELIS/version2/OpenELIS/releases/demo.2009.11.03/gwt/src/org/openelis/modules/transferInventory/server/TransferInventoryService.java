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
package org.openelis.modules.transferInventory.server;

import java.util.HashMap;

import org.openelis.gwt.common.data.deprecated.FieldType;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Query;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.deprecated.AppScreenFormServiceInt;
import org.openelis.modules.transferInventory.client.TransferInventoryForm;
import org.openelis.server.constants.Constants;

public class TransferInventoryService implements AppScreenFormServiceInt<TransferInventoryForm,Query<TableDataRow<Integer>>>{

    public TransferInventoryForm abort(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferInventoryForm commitAdd(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferInventoryForm commitDelete(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferInventoryForm commitUpdate(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferInventoryForm fetch(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TransferInventoryForm fetchForUpdate(TransferInventoryForm rpc) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws Exception {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/transferInventory.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    public TransferInventoryForm getScreen(TransferInventoryForm rpc){
        return rpc;
    }

}
