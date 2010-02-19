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

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.AnalysisManager.AnalysisListItem;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class AnalysisManagerProxy {
    protected static final String ANALYSIS_SERVICE_URL = "org.openelis.modules.analysis.server.AnalysisService";
    protected ScreenService service, testService;
    
    public AnalysisManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+ANALYSIS_SERVICE_URL);
    }
    
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return service.call("fetchBySampleItemId", sampleItemId);
    }
   
    public AnalysisManager add(AnalysisManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public AnalysisManager update(AnalysisManager man) throws Exception{
        assert false : "not supported";
        return null;
    }
    
    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Calendar.getCurrentDatetime(begin, end);
    }
    
    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        validate(man, null, null, errorsList);
    }
    
    public void validate(AnalysisManager man, String sampleItemSequence, Integer sampleTypeId, ValidationErrorsList errorsList) throws Exception {
        AnalysisListItem item;
        Integer cancelledStatusId;
        AnalysisViewDO analysisDO;
        TestManager testMan;
        
        cancelledStatusId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
        
        if(man.count() == 0)
            errorsList.add(new FormErrorWarning("minOneAnalysisException", sampleItemSequence));
        
        for(int i=0; i<man.count(); i++){
            analysisDO = man.getAnalysisAt(i);
            testMan = man.getTestAt(i);
            
            if(analysisDO.getTestId() == null)
                errorsList.add(new FormErrorException("analysisTestIdMissing", sampleItemSequence));
            
            if(analysisDO.getTestId() != null && analysisDO.getSectionId() == null)
                errorsList.add(new FormErrorException("analysisSectionIdMissing", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            //per dari clinical wont use units so the validation is being removed
            //if(analysisDO.getTestId() != null && analysisDO.getUnitOfMeasureId() == null)
            //    errorsList.add(new FormErrorWarning("analysisUnitIdMissing", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            //ignore the sample type check if analysis is cancelled.  This is the only
            //way they can fix this error in some cases.
            if(analysisDO.getTestId() != null && !cancelledStatusId.equals(analysisDO.getStatusId()) && 
                            !testMan.getSampleTypes().hasType(sampleTypeId))
                errorsList.add(new FormErrorWarning("sampleTypeInvalid", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            item = man.getItemAt(i);
            //validate the children
            
            //we want to always run thorugh validate so it loads the data
            //if the user didnt click on the tab
            man.getAnalysisResultAt(i).validate(analysisDO, errorsList);
            
            if(item.qaEvents != null)
                man.getQAEventAt(i).validate(errorsList);
            
            if(item.storages != null)
                man.getStorageAt(i).validate(errorsList);
        }
    }
    
    public SecurityUtil getSecurityUtil() {
        return OpenELIS.security;
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception {
        return DictionaryCache.getIdFromSystemName(systemName);
    }
    
    public SectionViewDO getSectionFromId(Integer sectionId) throws Exception {
        return SectionCache.getSectionFromId(sectionId);
    }
}
