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
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.ProjectMeta;
import org.openelis.meta.ScriptletMeta;

public class ProjectMetaMap extends ProjectMeta implements MetaMap {

    private ProjectParameterMetaMap PROJECT_PARAMETER;
    private ScriptletMeta SCRIPTLET;
    
    public ProjectMetaMap() {
        super("proj.");
        PROJECT_PARAMETER = new ProjectParameterMetaMap("projectParameter");
        SCRIPTLET = new ScriptletMeta("proj.scriptlet.");
    }
    
    public ProjectMetaMap(String path){
        super(path);  
        PROJECT_PARAMETER = new ProjectParameterMetaMap("projectParameter");
        SCRIPTLET = new ScriptletMeta(path+"proj.scriptlet.");
    }
    
    public ProjectParameterMetaMap getProjectParameter(){
        return PROJECT_PARAMETER;
    }

    public ScriptletMeta getScriptlet (){
        return SCRIPTLET;
    }
    
    public String buildFrom(String name) {        
        String from = "Project proj ";
        if(name.indexOf("projectParameter.") > -1)
            from += ", IN (proj.projectParameter) projectParameter "; 
        return from;
    }
    
    public static ProjectMetaMap getInstance() {
        return new ProjectMetaMap();
    }
    
    public boolean hasColumn(String name){ 
        if(name.startsWith("projectParameter."))
            return PROJECT_PARAMETER.hasColumn(name);
        if(name.startsWith(path+"scriptlet."))
            return SCRIPTLET.hasColumn(name);
        return super.hasColumn(name);
    }
        
}
