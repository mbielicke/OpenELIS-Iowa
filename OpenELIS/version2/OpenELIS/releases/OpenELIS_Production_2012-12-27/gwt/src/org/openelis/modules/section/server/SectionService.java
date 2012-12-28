/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.section.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.SectionManager;
import org.openelis.manager.SectionParameterManager;
import org.openelis.server.EJBFactory;

public class SectionService {

    public SectionManager fetchById(Integer id) throws Exception {
        return EJBFactory.getSectionManager().fetchById(id);
    }

    public ArrayList<SectionDO> fetchByName(String search) throws Exception {
        return EJBFactory.getSection().fetchByName(search + "%", 10);
    }
    
    public SectionManager fetchWithParameters(Integer id) throws Exception {
        return EJBFactory.getSectionManager().fetchWithParameters(id);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return EJBFactory.getSection().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public SectionManager add(SectionManager man) throws Exception {
        return EJBFactory.getSectionManager().add(man);
    }

    public SectionManager update(SectionManager man) throws Exception {
        return EJBFactory.getSectionManager().update(man);
    }

    public SectionManager fetchForUpdate(Integer id) throws Exception {
        return EJBFactory.getSectionManager().fetchForUpdate(id);
    }

    public SectionManager abortUpdate(Integer id) throws Exception {
        return EJBFactory.getSectionManager().abortUpdate(id);
    }
    
    //
    // support for SectionParameterManager
    //   
    public SectionParameterManager fetchParameterBySectionId(Integer id) throws Exception {
        return EJBFactory.getSectionManager().fetchParameterBySectionId(id);
    }
}
