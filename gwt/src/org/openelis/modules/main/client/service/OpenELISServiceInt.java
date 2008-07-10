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
package org.openelis.modules.main.client.service;

import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface OpenELISServiceInt extends AppScreenFormServiceInt {
    
    public DataObject getObject(String method, DataObject[] args) throws RPCException;
   
    
    public void logout();
	 
}
