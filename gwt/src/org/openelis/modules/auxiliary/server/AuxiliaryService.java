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

package org.openelis.modules.auxiliary.server;

import java.util.HashMap;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.auxiliary.client.AuxiliaryRPC;
import org.openelis.server.constants.Constants;

public class AuxiliaryService implements
                             AppScreenFormServiceInt<AuxiliaryRPC, Integer> , AutoCompleteServiceInt{

    public AuxiliaryRPC abort(AuxiliaryRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC commitAdd(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC commitDelete(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC commitUpdate(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC fetch(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC fetchForUpdate(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC getScreen(AuxiliaryRPC rpc) throws RPCException {
        try {
         rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/auxiliary.xsl");
        }catch(Exception ex) {
            ex.printStackTrace();
            throw new RPCException(ex.getMessage());
        } 
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl"));       

        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }

    public DataModel getMatches(String cat,
                                DataModel model,
                                String match,
                                HashMap<String, FieldType> params) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

}
