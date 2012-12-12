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
package org.openelis.bean;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.remote.ScriptletRemote;

@Stateless
@SecurityDomain("openelis")

public class ScriptletBean implements ScriptletRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    
    @SuppressWarnings("unchecked")
	public ArrayList<IdNameVO> fetchByName(String match, int maxResults) {
        Query query = manager.createNamedQuery("Scriptlet.ScriptletFindByName");
        query.setParameter("name", match);
        query.setMaxResults(maxResults);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

}
