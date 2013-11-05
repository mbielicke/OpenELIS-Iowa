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
import org.openelis.scriptlet.ScriptletBeanInt;
import org.openelis.scriptlet.ScriptletObject;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.utils.EJBFactory;
import org.openelis.modules.scriptlet.client.ScriptletServiceInt;

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
    public ScriptletObject run(ScriptletObject so) throws Exception {
        ScriptletBeanInt script;
        ScriptletDO      sdo;
        
        script = (ScriptletBeanInt)getThreadLocalRequest().getSession().getAttribute("scriptlet"+so.getId());
        
        if(script == null) {
            sdo = scriptlet.fetchById(so.getId());
            script = EJBFactory.lookup(sdo.getBean());
            getThreadLocalRequest().getSession().setAttribute("scriptlet"+so.getId(), script);
        }
        
        try {
            return script.run(so);
        }catch(Exception e) {
            throw serializeForGWT(e);
        }
    }

}
