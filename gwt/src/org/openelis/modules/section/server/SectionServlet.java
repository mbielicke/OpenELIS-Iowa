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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.SectionBean;
import org.openelis.bean.SectionManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.SectionManager;
import org.openelis.manager.SectionParameterManager;
import org.openelis.modules.section.client.SectionServiceInt;

@WebServlet("/openelis/section")
public class SectionServlet extends RemoteServlet implements SectionServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    SectionManagerBean sectionManager;
    
    @EJB
    SectionBean        section;
    

    public SectionManager fetchById(Integer id) throws Exception {
        try {        
            return sectionManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<SectionDO> fetchByName(String search) throws Exception {
        try {        
            return section.fetchByName(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public SectionManager fetchWithParameters(Integer id) throws Exception {
        try {        
            return sectionManager.fetchWithParameters(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return section.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SectionManager add(SectionManager man) throws Exception {
        try {        
            return sectionManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SectionManager update(SectionManager man) throws Exception {
        try {        
            return sectionManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SectionManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return sectionManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public SectionManager abortUpdate(Integer id) throws Exception {
        try {        
            return sectionManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    //
    // support for SectionParameterManager
    //   
    public SectionParameterManager fetchParameterBySectionId(Integer id) throws Exception {
        try {        
            return sectionManager.fetchParameterBySectionId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
