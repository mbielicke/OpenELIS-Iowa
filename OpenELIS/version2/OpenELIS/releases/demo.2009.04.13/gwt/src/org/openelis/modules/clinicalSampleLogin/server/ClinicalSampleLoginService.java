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
package org.openelis.modules.clinicalSampleLogin.server;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.modules.clinicalSampleLogin.client.ClinicalSampleLoginForm;
import org.openelis.server.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class ClinicalSampleLoginService implements AppScreenFormServiceInt<ClinicalSampleLoginForm,Query<TableDataRow<Integer>>>{

    public ClinicalSampleLoginForm abort(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClinicalSampleLoginForm commitAdd(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClinicalSampleLoginForm commitDelete(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> data) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClinicalSampleLoginForm commitUpdate(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClinicalSampleLoginForm fetch(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public ClinicalSampleLoginForm fetchForUpdate(ClinicalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/clinicalSampleLogin.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public ClinicalSampleLoginForm getScreen(ClinicalSampleLoginForm rpc) throws RPCException{
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/clinicalSampleLogin.xsl");
        return rpc;
    }
}