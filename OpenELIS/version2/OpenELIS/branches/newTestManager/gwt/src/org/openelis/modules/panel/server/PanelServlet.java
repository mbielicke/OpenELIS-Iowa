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
package org.openelis.modules.panel.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.PanelBean;
import org.openelis.bean.PanelManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;
import org.openelis.modules.panel.client.PanelServiceInt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/panel")
public class PanelServlet extends RemoteServlet implements PanelServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    PanelManagerBean panelManager;
    
    @EJB
    PanelBean        panel;

    public PanelManager fetchById(Integer id) throws Exception {
        try {        
            return panelManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }   

    public PanelManager fetchWithItems(Integer id) throws Exception {
        try {        
            return panelManager.fetchWithItems(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<PanelDO> fetchByName(String name) throws Exception {
        try {        
            return panel.fetchByName(name+"%", 100);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<TestMethodVO> fetchByNameWithTests(String name) throws Exception {
        try {        
            return panel.fetchByNameWithTests(name, 100);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<TestMethodVO> fetchByNameSampleTypeWithTests(Query query) throws Exception {
        try {
            // TODO: Rewrite this method to user parameters instead of Query
            if (query.getFields().size() == 2)
                return panel.fetchByNameSampleTypeWithTests(query.getFields().get(0).getQuery(), new Integer(query.getFields().get(1).getQuery()), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
        return null;
    }
    
    public ArrayList<PanelDO> fetchAll() throws Exception {
        try {        
            return panel.fetchAll();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {        
            return panel.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PanelManager add(PanelManager man) throws Exception {
        try {        
            return panelManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PanelManager update(PanelManager man) throws Exception {
        try {        
            return panelManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PanelManager fetchForUpdate(Integer id) throws Exception {
        try {        
            return panelManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PanelManager abortUpdate(Integer id) throws Exception {
        try {        
            return panelManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public PanelItemManager fetchItemByPanelId(Integer id) throws Exception {
        try {        
            return panelManager.fetchItemByPanelId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdVO> fetchTestIdsByPanelId(Integer panelId) throws Exception {
        try {        
            return panel.fetchTestIdsFromPanel(panelId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public ArrayList<IdVO> fetchAuxIdsByPanelId(Integer panelId) throws Exception {
        try {        
            return panel.fetchAuxIdsFromPanel(panelId);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
