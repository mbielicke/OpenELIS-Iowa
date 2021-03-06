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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.SectionPermission;
import org.openelis.utils.EJBFactory;

/**
 * This class is used for providing the back-end functionality for the 
 * chl-gc worksheet scriptlet
 */
public class ChlGcWorksheetScriptletProxy1 implements ChlGcWorksheetScriptlet1Proxy {
    
    private static final Logger          log = Logger.getLogger("openelis");
    
    @Override
    public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception {
        return EJBFactory.getDictionaryCache().getBySystemName(systemName);
    }

    @Override
    public ArrayList<SampleManager1> fetchSampleManagersByAnalyses(ArrayList<Integer> analysisIds) throws Exception {
        return EJBFactory.getSampleManager1().fetchByAnalyses(analysisIds, (SampleManager1.Load[]) null);
    }
    
    @Override
    public boolean canEdit(WorksheetAnalysisViewDO waVDO) {
        SectionPermission perm;
        
        perm = EJBFactory.getUserCache().getPermission().getSection(waVDO.getSectionName());
        if (Constants.dictionary().ANALYSIS_RELEASED.equals(waVDO.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(waVDO.getStatusId()))
            return false;
        else if (perm == null || !perm.hasCompletePermission())
            return false;

        return true;
    }
    
    @Override
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        return EJBFactory.getWorksheetManager1().getColumnNames(formatId);
    }
    
    @Override
    public void log(Level level, String message, Exception e) {
        log.log(level, message, e);
    }
}