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
                script = (T)new org.openelis.scriptlet.env.ia.Scriptlet(new org.openelis.scriptlet.server.env.ScriptletProxy());
                break;
            case "scriptlet_sdwis_ia1":
                script = (T)new org.openelis.scriptlet.sdwis.ia.Scriptlet(new org.openelis.scriptlet.server.sdwis.ScriptletProxy());
                break;
            case "scriptlet_neonatal_ia1":
                script = (T)new org.openelis.scriptlet.nbs.ia.Scriptlet(new org.openelis.scriptlet.server.nbs.ScriptletProxy());
                break;
            case "scriptlet_nbs_bt1":
                script = (T)new org.openelis.scriptlet.nbs.bt.Scriptlet(new org.openelis.scriptlet.server.nbs.ScriptletProxy(),
                                                                        managedId);
                break;
            case "scriptlet_nbs_tsh1":
                script = (T)new org.openelis.scriptlet.nbs.tsh.Scriptlet(new org.openelis.scriptlet.server.nbs.ScriptletProxy(),
                                                                         managedId);
                break;
            case "scriptlet_nbs_galt1":
                script = (T)new org.openelis.scriptlet.nbs.galt.Scriptlet(new org.openelis.scriptlet.server.nbs.ScriptletProxy(),
                                                                          managedId);
                break;
            case "scriptlet_nbs_cah1":
                script = (T)new org.openelis.scriptlet.nbs.cah.Scriptlet(new org.openelis.scriptlet.server.nbs.ScriptletProxy(),
                                                                         managedId);
                break;
            case "scriptlet_pws_validate1":
                script = (T)new org.openelis.scriptlet.pws.validate.Scriptlet(new org.openelis.scriptlet.server.pws.ScriptletProxy(),
                                                                              managedId);
                break;
            case "scriptlet_cf_carrier1":
                script = (T)new org.openelis.scriptlet.cf.carrier.Scriptlet(new org.openelis.scriptlet.server.cf.ScriptletProxy(),
                                                                            managedId);
                break;
            case "scriptlet_cf_pregnancy1":
                script = (T)new org.openelis.scriptlet.cf.pregnancy.Scriptlet(new org.openelis.scriptlet.server.cf.ScriptletProxy(),
                                                                              managedId);
                break;
            case "scriptlet_serogroup_result1":
                script = (T)new SerogroupResultScriptlet1(new SerogroupResultScriptletProxy1(),
                                                          managedId);
                break;
            case "scriptlet_chlgc_worksheet1":
                script = (T)new ChlGcWorksheetScriptlet1(new ChlGcWorksheetScriptletProxy1());
                break;
            case "scriptlet_ms_quad1":
                script = (T)new org.openelis.scriptlet.ms.quad.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                         managedId);
                break;
            case "scriptlet_ms_ntd1":
                script = (T)new org.openelis.scriptlet.ms.ntd.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                        managedId);
                break;
            case "scriptlet_ms_conf1":
                script = (T)new org.openelis.scriptlet.ms.conf.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                         managedId);
                break;
            case "scriptlet_ms_1st_tri1":
                script = (T)new org.openelis.scriptlet.ms.firstTri.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                             managedId);
                break;
            case "scriptlet_ms_1st_integ1":
                script = (T)new org.openelis.scriptlet.ms.firstInteg.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                             managedId);
                break;
            case "scriptlet_ms_integrate1":
                script = (T)new org.openelis.scriptlet.ms.integrate.Scriptlet(new org.openelis.scriptlet.server.ms.ScriptletProxy(),
                                                                             managedId);
                break;
        }

        return script;
    }
}