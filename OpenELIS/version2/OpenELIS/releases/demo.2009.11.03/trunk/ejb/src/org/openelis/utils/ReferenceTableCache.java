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
package org.openelis.utils;

import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;

import org.openelis.domain.IdNameDO;
import org.openelis.local.ReferenceTableLocal;

public class ReferenceTableCache {
    protected static HashMap<String, Integer> hash;
    
    static {
        hash = new HashMap<String, Integer>();
        
        try{
            InitialContext ctx = new InitialContext();
            ReferenceTableLocal local = (ReferenceTableLocal)ctx.lookup("openelis/ReferenceTableBean/local");
            List refTables = local.GetAllReferenceTables();
    
            //lookup all the reference tables and put them in the hash
            if(refTables != null){
                for(int i=0; i<refTables.size();i++){
                    IdNameDO rtDO = (IdNameDO)refTables.get(i);
                    hash.put(rtDO.getName(), rtDO.getId());
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
            
    }
    
    public static Integer getReferenceTable(String tableName){
        return hash.get(tableName);
    }
}
