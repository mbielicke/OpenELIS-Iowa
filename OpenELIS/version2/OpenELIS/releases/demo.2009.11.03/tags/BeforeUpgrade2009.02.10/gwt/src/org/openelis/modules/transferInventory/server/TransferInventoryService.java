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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.server.constants.Constants;

import java.util.HashMap;

public class TransferInventoryService implements AppScreenFormServiceInt<RPC,DataModel<DataSet>>{

    public RPC abort(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitAdd(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitDelete(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<DataSet> commitQuery(Form form, DataModel<DataSet> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC commitUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetch(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public RPC fetchForUpdate(RPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/transferInventory.xsl");
    }

    public HashMap<String, Data> getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<String, Data> getXMLData(HashMap<String, Data> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public RPC getScreen(RPC rpc){
        return rpc;
    }

}
