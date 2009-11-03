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
package org.openelis.manager;

public class ManagerFactory {
    public static boolean isClient = false;

    public static SampleManagerIOInt getSampleManagerIO() {
        if (isClient)
            return (SampleManagerIOInt)getManager("org.openelis.manager.SampleManagerIOClient");
        else
            return (SampleManagerIOInt)getManager("org.openelis.bean.SampleManagerIOEJB");
    }

    public static SampleItemsManagerIOInt getSampleItemManagerIO() {
        if (isClient)
            return (SampleItemsManagerIOInt)getManager("org.openelis.manager.SampleItemsManagerIOClient");
        else
            return (SampleItemsManagerIOInt)getManager("org.openelis.bean.SampleItemsManagerIOEJB");
    }
    
    public static SampleHumanManagerIOInt getSampleHumanManagerIO() {
        if (isClient)
            return (SampleHumanManagerIOInt)getManager("org.openelis.manager.SampleHumanManagerIOClient");
        else
            return (SampleHumanManagerIOInt)getManager("org.openelis.bean.SampleHumanManagerIOEJB");
    }
    
    public static SampleEnvironmentalManagerIOInt getSampleEnvironmentalManagerIO() {
        if (isClient)
            return (SampleEnvironmentalManagerIOInt)getManager("org.openelis.manager.SampleEnvironmentalManagerIOClient");
        else
            return (SampleEnvironmentalManagerIOInt)getManager("org.openelis.bean.SampleEnvironmentalManagerIOEJB");
    }
    
    public static SampleProjectsManagerIOInt getSampleProjectManagerIO() {
        if (isClient)
            return (SampleProjectsManagerIOInt)getManager("org.openelis.manager.SampleProjectsManagerIOClient");
        else
            return (SampleProjectsManagerIOInt)getManager("org.openelis.bean.SampleProjectsManagerIOEJB");
    }
    
    public static SampleOrganizationsManagerIOInt getSampleOrganizationManagerIO() {
        if (isClient)
            return (SampleOrganizationsManagerIOInt)getManager("org.openelis.manager.SampleOrganizationsManagerIOClient");
        else
            return (SampleOrganizationsManagerIOInt)getManager("org.openelis.bean.SampleOrganizationsManagerIOEJB");
    }
    
    public static AnalysesManagerIOInt getAnalysesManagerIO() {
        if (isClient)
            return (AnalysesManagerIOInt)getManager("org.openelis.manager.AnalysesManagerIOClient");
        else
            return (AnalysesManagerIOInt)getManager("org.openelis.bean.AnalysesManagerIOEJB");
    }

    private static Object getManager(String className) {
        Class cls;
        Object obj;

        try {
            cls = Class.forName(className);
            obj = cls.newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            obj = null;
        }
        return obj;
    }
}
