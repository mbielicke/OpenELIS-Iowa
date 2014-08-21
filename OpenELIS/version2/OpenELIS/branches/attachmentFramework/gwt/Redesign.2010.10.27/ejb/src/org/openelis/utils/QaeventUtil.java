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
package org.openelis.utils;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.QaEventDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.local.QaeventLocal;

public class QaeventUtil {

    public static String getAnalysisQaeventText(Integer analysisId, Boolean notInternal) {        
        StringBuffer text;
        ArrayList<QaEventDO> list;
   
        try {
            if (notInternal)
                list = qaeventLocal().fetchNotInternalByAnalysisId(analysisId);
            else
                list = qaeventLocal().fetchByAnalysisId(analysisId);
            
            text = new StringBuffer();
            for (QaEventDO data : list) 
                text.append(data.getReportingText());
            
            return DataBaseUtil.trim(text.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }                
    }
    
    public static String getSampleQaeventText(Integer sampleId, Boolean notInternal) {                   
        StringBuffer text;
        ArrayList<QaEventDO> list;
   
        try {
            if (notInternal)
                list = qaeventLocal().fetchNotInternalBySampleId(sampleId);
            else
                list = qaeventLocal().fetchBySampleId(sampleId);
            
            text = new StringBuffer();
            for (QaEventDO data : list) 
                text.append(data.getReportingText());
            
            return DataBaseUtil.trim(text.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }   
    
    private static QaeventLocal qaeventLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (QaeventLocal)ctx.lookup("openelis/QaEventBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }           
    }
}
