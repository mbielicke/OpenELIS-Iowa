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

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.AnalysisManager.AnalysisListItem;

public class AnalysisManagerProxy {
    protected static final String ANALYSIS_SERVICE_URL = "org.openelis.modules.analysis.server.AnalysisService";
    protected static final String TEST_MANAGER_SERVICE_URL = "org.openelis.modules.test.server.TestService";
    protected ScreenService service, testService;
    
    public AnalysisManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+ANALYSIS_SERVICE_URL);
        testService = new ScreenService("OpenELISServlet?service="+TEST_MANAGER_SERVICE_URL);
    }
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return service.call("fetchBySampleItemId", sampleItemId);
    }
    
    public AnalysisManager add(AnalysisManager man) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    public AnalysisManager update(AnalysisManager man) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    public boolean testHasSampleType(Integer testId, Integer sampleTypeId){
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        try{
            fields = new ArrayList<QueryData>();
            query = new Query();
        
            field = new QueryData();
            field.query = testId.toString();
            fields.add(field);
            
            field = new QueryData();
            field.query = sampleTypeId.toString();
            fields.add(field);
            
            query.setFields(fields);

            testService.call("fetchTestByIdAndSampleType", query);
        
        }catch(NotFoundException e){
            return false;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        return true;
    }
    
    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        validate(man, null, null, errorsList);
    }
    
    public void validate(AnalysisManager man, String sampleItemSequence, Integer sampleTypeId, ValidationErrorsList errorsList) throws Exception {
        AnalysisListItem item;
        Integer cancelledStatusId;
        
        cancelledStatusId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
        
        if(man.count() == 0)
            errorsList.add(new FormErrorException("minOneAnalysisException", sampleItemSequence));
        
        for(int i=0; i<man.count(); i++){
            AnalysisViewDO analysisDO = man.getAnalysisAt(i);
            
            if(analysisDO.getTestId() == null)
                errorsList.add(new FormErrorException("analysisTestIdMissing", sampleItemSequence));
            
            if(analysisDO.getTestId() != null && analysisDO.getSectionId() == null)
                errorsList.add(new FormErrorException("analysisSectionIdMissing", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            if(analysisDO.getTestId() != null && analysisDO.getUnitOfMeasureId() == null)
                errorsList.add(new FormErrorException("analysisUnitIdMissing", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            //ignore the sample type check if analysis is cancelled.  This is the only
            //way they can fix this error in some cases.
            //FIXME this check is REALLY slow...need to find a better way to do this
            //if(analysisDO.getTestId() != null && !cancelledStatusId.equals(analysisDO.getStatusId()) && 
            //                !testHasSampleType(analysisDO.getTestId(), sampleTypeId))
            //    errorsList.add(new FormErrorException("sampleTypeInvalid", analysisDO.getTestName(), analysisDO.getMethodName()));
            
            item = man.getItemAt(i);
            //validate the children
            if(item.analysisResult != null)
                man.getAnalysisResultAt(i).validate(errorsList);
            
            if(item.qaEvents != null)
                man.getQAEventAt(i).validate(errorsList);
            
            if(item.storage != null)
                man.getStorageAt(i).validate(errorsList);
        }
    }
}
