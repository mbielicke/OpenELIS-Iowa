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
package org.openelis.scriptlet;

import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.utils.EJBFactory;

/**
 * This class is used to obtain instances of scriptlet classes for the back-end
 */
public class ScriptletFactory {

    public static <T extends ScriptletInt<?>> T get(Integer scriptletId, Integer managedId) throws Exception {
        return get(EJBFactory.getDictionaryCache().getById(scriptletId).getSystemName(), managedId);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends ScriptletInt<?>> T get(String systemName, Integer managedId) throws Exception {
        T script;

        script = null;
        switch (systemName) {
            case "scriptlet_environmental_ia1":
                script = (T)new EnvironmentalIAScriptlet1(new EnvironmentalIAProxy1());
                break;
            case "scriptlet_sdwis_ia1":
                script = (T)new SDWISIAScriptlet1(new SDWISIAProxy1());
                break;
            case "scriptlet_neonatal_ia1":
                script = (T)new NeonatalIAScriptlet1(new NeonatalIAProxy1());
                break;
            case "scriptlet_nbs_bt1":
                script = (T)new NbsBtScriptlet1(new NBSScriptletProxy1(), managedId);
                break;
            case "scriptlet_nbs_tsh1":
                script = (T)new NbsTshScriptlet1(new NBSScriptletProxy1(), managedId);
                break;
            case "scriptlet_nbs_galt1":
                script = (T)new NbsGaltScriptlet1(new NBSScriptletProxy1(), managedId);
                break;
            case "scriptlet_nbs_cah1":
                script = (T)new NbsCahScriptlet1(new NBSScriptletProxy1(), managedId);
                break;
            case "scriptlet_pws_validate1":
                script = (T)new PwsValidateScriptlet1(new PwsValidateProxy1(), managedId);
                break;
            case "scriptlet_cf_carrier1":
                script = (T)new CFCarrierScriptlet1(new CFScriptletProxy1(), managedId);
                break;
            case "scriptlet_cf_pregnancy1":
                script = (T)new CFPregnancyScriptlet1(new CFScriptletProxy1(), managedId);
                break;
        }

        return script;
    }
}