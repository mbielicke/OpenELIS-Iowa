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
package org.openelis.report.finalreport;

import java.util.ArrayList;

public class AnalysisUnitList {

    private ArrayList<Integer> unitList;
    
    public AnalysisUnitList() {
        this.unitList = new ArrayList<Integer>();
    }
    
    public void add(Integer unitId) {
        unitList.add(unitId);
    }
    
    public String toString() {
        int i;
        StringBuffer str;
                      
        str = new StringBuffer();
        for (i = 0; i < unitList.size(); i++ ) {
            if (i != 0)
                str.append(",");
            str.append(unitList.get(i));
        }
        return str.toString();
    }        
}
