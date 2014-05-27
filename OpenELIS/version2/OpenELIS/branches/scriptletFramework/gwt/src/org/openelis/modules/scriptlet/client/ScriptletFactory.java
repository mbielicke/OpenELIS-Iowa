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
package org.openelis.modules.scriptlet.client;

import org.openelis.cache.DictionaryCache;
import org.openelis.scriptlet.NbsBtScriptlet1;
import org.openelis.scriptlet.NbsCahScriptlet1;
import org.openelis.scriptlet.NbsGaltScriptlet1;
import org.openelis.scriptlet.NbsTshScriptlet1;
import org.openelis.scriptlet.NeonatalDomainScriptlet1;
import org.openelis.scriptlet.PwsValidateScriptlet1;
import org.openelis.ui.scriptlet.ScriptletInt;

/**
 * This class is used to obtain instances of scriptlet classes for the front-end
 */
public class ScriptletFactory {
    public static <T extends ScriptletInt<?>> T get(Integer id) throws Exception {
        return get(DictionaryCache.getById(id).getSystemName());
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends ScriptletInt<?>> T get(String systemName) throws Exception {
        T script;

        script = null;
        switch (systemName) {
            case "scriptlet_neonatal_domain1":
                script = (T)new NeonatalDomainScriptlet1(new NeonatalDomainProxy1());
                break;
            case "scriptlet_nbs_bt1":
                script = (T)new NbsBtScriptlet1(new NbsBtProxy1());
                break;
            case "scriptlet_nbs_tsh1":
                script = (T)new NbsTshScriptlet1(new NbsTshProxy1());
                break;
            case "scriptlet_nbs_galt1":
                script = (T)new NbsGaltScriptlet1(new NbsGaltProxy1());
                break;
            case "scriptlet_nbs_cah1":
                script = (T)new NbsCahScriptlet1(new NbsCahProxy1());
                break;
            case "scriptlet_pws_validate1":
                script = (T)new PwsValidateScriptlet1(new PwsValidateProxy1());
                break;
        }

        return script;
    }
}