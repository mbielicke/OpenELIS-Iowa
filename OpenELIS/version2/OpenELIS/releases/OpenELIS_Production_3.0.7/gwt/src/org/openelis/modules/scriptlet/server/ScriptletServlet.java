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
package org.openelis.modules.scriptlet.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ScriptletBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.modules.scriptlet.client.ScriptletServiceInt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/openelis/scriptlet")
public class ScriptletServlet extends RemoteServlet implements ScriptletServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    ScriptletBean scriptlet;

    public ArrayList<IdNameVO> fetchByName(String search)throws Exception {
        try {        
            return scriptlet.fetchByName(search+"%", 100);       
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ScriptletDO fetchById(Integer id) throws Exception {
        try {
            return scriptlet.fetchById(id);
        }catch(Exception e){
            throw serializeForGWT(e);
        }
    }
    
    @Override
    public ArrayList<ScriptletDO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        try {
            return scriptlet.fetchByIds(ids);
        }catch(Exception e){
            throw serializeForGWT(e);
        }
    }

    @Override
    public ScriptletDO fetchForUpdate(Integer id) throws Exception {
        try {
            return scriptlet.fetchForUpdate(id);
        }catch(Exception e) {
            throw serializeForGWT(e);
        }
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return scriptlet.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    @Override
    public ScriptletDO add(ScriptletDO data) throws Exception {
        try {
            return scriptlet.add(data);
        }catch(Exception e) {
            throw serializeForGWT(e);
        }
    }

    @Override
    public ScriptletDO update(ScriptletDO data) throws Exception {
        try {
            return scriptlet.update(data);
        }catch(Exception e) {
            throw serializeForGWT(e);
        }
    }

    @Override
    public ScriptletDO abortUpdate(Integer id) throws Exception {
        try {
            return scriptlet.abortUpdate(id);
        }catch(Exception e) {
            throw serializeForGWT(e);
        }
    }

}
