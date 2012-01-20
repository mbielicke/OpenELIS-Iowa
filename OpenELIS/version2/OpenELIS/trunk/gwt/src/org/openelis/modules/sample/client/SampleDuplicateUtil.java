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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.manager.SampleProjectManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.manager.SampleSDWISManager;

public class SampleDuplicateUtil {

    private static Integer anaCancelledId, anaInPrepId, anaLoggedInId, anaErrorLoggedInId;  
    
    public static SampleManager duplicate(SampleManager oldMan) throws Exception {
        Integer statusId;
        SampleManager newMan;
        SampleItemManager siMan;
        AnalysisManager anMan;

        if (anaCancelledId == null) {
            anaCancelledId = DictionaryCache.getIdBySystemName("analysis_cancelled");
            anaInPrepId = DictionaryCache.getIdBySystemName("analysis_inprep");
            anaLoggedInId = DictionaryCache.getIdBySystemName("analysis_logged_in");
            anaErrorLoggedInId = DictionaryCache.getIdBySystemName("analysis_error_logged_in");
        }

        siMan = oldMan.getSampleItems();
        for (int i = 0; i < siMan.count(); i++ ) {
            anMan = siMan.getAnalysisAt(i);
            for (int j = 0; j < anMan.count(); j++ ) {
                statusId = anMan.getAnalysisAt(j).getStatusId();
                if ( !anaCancelledId.equals(statusId) && !anaInPrepId.equals(statusId) &&
                    !anaLoggedInId.equals(statusId) && !anaErrorLoggedInId.equals(statusId))
                    throw new LocalizedException("analysisHasAdvancedStatusException");
            }
        }
        newMan = SampleManager.getInstance();
        duplicate(newMan, oldMan);

        return newMan;
    }
    
    private static void duplicate(SampleManager newMan, SampleManager oldMan) throws Exception {        
        String domain;
        SampleDO newData, oldData;         
        
        newMan.setDefaults();
        newData = newMan.getSample();
        oldData = oldMan.getSample();
        domain = oldData.getDomain();
        
        newData.setDomain(domain);
        newData.setOrderId(oldData.getOrderId());
        newData.setReceivedById(oldData.getReceivedById());
        newData.setCollectionDate(oldData.getCollectionDate());
        newData.setCollectionTime(oldData.getCollectionTime());
        newData.setReceivedDate(oldData.getReceivedDate());
        newData.setPackageId(oldData.getPackageId());
        newData.setClientReference(oldData.getClientReference());
        
        duplicateSampleItems(newMan.getSampleItems(), oldMan.getSampleItems());     
        duplicateOrganizations(newMan.getOrganizations(), oldMan.getOrganizations());
        duplicateProjects(newMan.getProjects(), oldMan.getProjects());
        duplicateSampleQaEvents(newMan.getQaEvents(), oldMan.getQaEvents());
                
        if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain)) {           
            duplicateEnvironmental((SampleEnvironmentalManager)newMan.getDomainManager(),
                                   (SampleEnvironmentalManager)oldMan.getDomainManager());
        } else if (SampleManager.WELL_DOMAIN_FLAG.equals(domain)) {
            duplicatePrivateWell((SamplePrivateWellManager)newMan.getDomainManager(),
                                   (SamplePrivateWellManager)oldMan.getDomainManager());
        } else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain)) {
            duplicateSDWIS((SampleSDWISManager)newMan.getDomainManager(),
                           (SampleSDWISManager)oldMan.getDomainManager());
        }
        duplicateNotes(newMan.getExternalNote(), oldMan.getExternalNote());
        duplicateAuxData(newMan.getAuxData(), oldMan.getAuxData());
    }
    
    private static void duplicateSampleItems(SampleItemManager newMan, SampleItemManager oldMan) throws Exception {
        int index;
        SampleItemViewDO newData, oldData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getSampleItemAt(i);
            index = newMan.addSampleItem();
            newData = newMan.getSampleItemAt(index);
            
            newData.setTypeOfSampleId(oldData.getTypeOfSampleId());
            newData.setSourceOfSampleId(oldData.getSourceOfSampleId());
            newData.setSourceOther(oldData.getSourceOther());
            newData.setContainerId(oldData.getContainerId());
            newData.setContainerReference(oldData.getContainerReference());
            newData.setQuantity(oldData.getQuantity());
            newData.setUnitOfMeasureId(oldData.getUnitOfMeasureId());
            newData.setTypeOfSample(oldData.getTypeOfSample());
            newData.setSourceOfSample(oldData.getSourceOfSample());
            newData.setContainer(oldData.getContainer());
            
            duplicateAnalyses (newMan.getAnalysisAt(index), oldMan.getAnalysisAt(i)); 
        }
    }
    
    private static void duplicateOrganizations(SampleOrganizationManager newMan,
                                               SampleOrganizationManager oldMan) {
        SampleOrganizationViewDO newData, oldData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            newData = new SampleOrganizationViewDO();
            oldData = oldMan.getOrganizationAt(i);
            
            newData.setOrganizationId(oldData.getOrganizationId());
            newData.setOrganizationAttention(oldData.getOrganizationAttention());
            newData.setTypeId(oldData.getTypeId());
            newData.setOrganizationName(oldData.getOrganizationName());
            newData.setOrganizationMultipleUnit(oldData.getOrganizationMultipleUnit());
            newData.setOrganizationStreetAddress(oldData.getOrganizationStreetAddress());
            newData.setOrganizationCity(oldData.getOrganizationCity());
            newData.setOrganizationState(oldData.getOrganizationState());
            newData.setOrganizationZipCode(oldData.getOrganizationZipCode());
            newData.setOrganizationWorkPhone(oldData.getOrganizationWorkPhone());
            newData.setOrganizationFaxPhone(oldData.getOrganizationFaxPhone());
            newMan.addOrganization(newData);
        }
    }
    
    private static void duplicateProjects(SampleProjectManager newMan, SampleProjectManager oldMan) {
        SampleProjectViewDO newData, oldData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            newData = new SampleProjectViewDO();            
            oldData = oldMan.getProjectAt(i);
            
            newData.setProjectId(oldData.getProjectId());
            newData.setIsPermanent(oldData.getIsPermanent());
            newData.setProjectName(oldData.getProjectName());
            newData.setProjectDescription(oldData.getProjectDescription());            
            newMan.addProject(newData);
        }
    }
    
    private static void duplicateSampleQaEvents(SampleQaEventManager newMan,
                                                SampleQaEventManager oldMan) {
        SampleQaEventViewDO newData, oldData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getSampleQAAt(i);
            newData = new SampleQaEventViewDO();
            
            newData.setQaEventId(oldData.getQaEventId());
            newData.setTypeId(oldData.getTypeId());
            newData.setIsBillable(oldData.getIsBillable());
            newData.setQaEventName(oldData.getQaEventName());
            newData.setQaEventReportingText(oldData.getQaEventReportingText());
            newMan.addSampleQA(newData);
        }
    }
        
    private static void duplicateAnalyses(AnalysisManager newMan, AnalysisManager oldMan) throws Exception {
        int index;
        Integer statusId, prepIndex;
        String tname, mname;
        AnalysisViewDO newData, oldData, prepData;
        HashMap<Integer, Integer> prepIdMap;
        AnalysisResultManager newRM, oldRM;        
        
        prepIdMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getAnalysisAt(i);
            statusId = oldData.getStatusId();
            tname = oldData.getTestName();
            mname = oldData.getMethodName();
            //
            // cancelled analyses are not duplicated
            //
            if (anaCancelledId.equals(statusId)) 
                continue;
            //
            // we don't allow duplication if even one analysis has reflexed analyses
            //
            if (oldData.getParentAnalysisId() != null || oldData.getParentResultId() != null) 
                throw new LocalizedException("analysisHasReflexAnalysesException", oldData.getTestName()+ ":"+oldData.getMethodName());
            index = newMan.addAnalysis();
            newData = newMan.getAnalysisAt(index);
            
            if (anaInPrepId.equals(statusId)) {
                /*
                 * if the old analysis was in prep then we create an analysis in
                 * the new manager and put its id in the hash map as the value where
                 * the key is the old analysis' id so that when we find the analysis 
                 * in the old manager later which this one has as its prep, we will
                 * be able to set this analysis' prep analysis id 
                 */
                newData.setStatusId(anaInPrepId);
                prepIdMap.put(oldData.getPreAnalysisId(), index);                          
            }             
            prepIndex = prepIdMap.get(oldData.getId());            
            if (prepIndex != null) {         
                /*
                 * if this analysis is a prep test for another then we find it in
                 * the new manager and set the various fields
                 */
                prepData = newMan.getAnalysisAt(prepIndex);
                prepData.setPreAnalysisId(newData.getId());
                prepData.setAvailableDate(null);
                prepData.setPreAnalysisTest(tname);
                prepData.setPreAnalysisMethod(mname);
            }
            
            newData.setTestName(tname);
            newData.setMethodName(mname);
            newData.setMethodId(oldData.getMethodId());
            newData.setIsReportable(oldData.getIsReportable());
            newData.setUnitOfMeasureId(oldData.getUnitOfMeasureId());
            newData.setSectionId(oldData.getSectionId());
        
            newRM = newMan.getAnalysisResultAt(index);
            oldRM = oldMan.getAnalysisResultAt(i);
            duplicateResults(newRM, oldRM);
            /*
             * the test's id is set after duplicating the results to prevent them
             * from being fetched
             */
            newData.setTestId(oldData.getTestId());
            
            duplicateAnalysisQaEvents(newMan.getQAEventAt(index), oldMan.getQAEventAt(i));
            duplicateNotes(newMan.getExternalNoteAt(index), oldMan.getExternalNoteAt(i));
        }                
    }
    
    private static void duplicateResults(AnalysisResultManager newMan, AnalysisResultManager oldMan) {
        int i;
        ArrayList<ResultViewDO> newRow, oldRow;
        ResultViewDO newData, oldData;
        
        for (i = 0; i < oldMan.rowCount(); i++) {
            oldRow = oldMan.getRowAt(i);
            newRow = new ArrayList<ResultViewDO>();
            for (int j = 0; j < oldRow.size(); j++) {
                oldData = oldRow.get(j);
                newData = new ResultViewDO();
                
                newData.setTestAnalyteId(oldData.getTestAnalyteId());
                newData.setTestResultId(oldData.getTestResultId());
                newData.setIsColumn(oldData.getIsColumn());
                newData.setIsReportable(oldData.getIsReportable());
                newData.setAnalyteId(oldData.getAnalyteId());
                newData.setTypeId(oldData.getTypeId());
                newData.setValue(oldData.getValue());   
                newData.setAnalyte(oldData.getAnalyte());
                newData.setRowGroup(oldData.getRowGroup());
                newData.setTestAnalyteTypeId(oldData.getTestAnalyteTypeId());
                newData.setResultGroup(oldData.getResultGroup());      
                newRow.add(newData);
            }
            newMan.addRow(newRow);
        }
        
        newMan.setAnalyteList(oldMan.getAnalyteList());
        newMan.setTestAnalyteList(oldMan.getTestAnalyteList());
        newMan.setTestResultList(oldMan.getTestResultList());   
        newMan.setResultValidators(oldMan.getResultValidators());
    }
    
    private static void duplicateAnalysisQaEvents(AnalysisQaEventManager newMan, 
                                                  AnalysisQaEventManager oldMan) {
        AnalysisQaEventViewDO newData, oldData;
        
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getAnalysisQAAt(i);
            newData = new AnalysisQaEventViewDO();
            
            newData.setAnalysisId(oldData.getAnalysisId());
            newData.setQaeventId(oldData.getQaEventId());
            newData.setTypeId(oldData.getTypeId());
            newData.setIsBillable(oldData.getIsBillable());
            newData.setQaEventName(oldData.getQaEventName());
            newData.setQaEventReportingText(oldData.getQaEventReportingText());
            newMan.addAnalysisQA(newData);
        }               
    }   
    
    private static void duplicateEnvironmental(SampleEnvironmentalManager newMan,
                                               SampleEnvironmentalManager oldMan) {
        SampleEnvironmentalDO newData, oldData;
        AddressDO oldLoc;
                
        newData = newMan.getEnvironmental();
        oldData = oldMan.getEnvironmental();
        
        oldLoc = oldData.getLocationAddress();   
        if (oldLoc.getId() != null)
            duplicateAddress(newData.getLocationAddress(), oldLoc);  
        
        newData.setCollector(oldData.getCollector());
        newData.setCollectorPhone(oldData.getCollectorPhone());
        newData.setDescription(oldData.getDescription());
        newData.setIsHazardous(oldData.getIsHazardous());
        newData.setPriority(oldData.getPriority());
        newData.setSampleId(oldData.getSampleId());
        newData.setLocation(oldData.getLocation());
    }
    
    private static void duplicatePrivateWell(SamplePrivateWellManager newMan, SamplePrivateWellManager oldMan) {
        SamplePrivateWellViewDO newData, oldData;
        AddressDO oldAddr;
        
        newData = newMan.getPrivateWell();
        oldData = oldMan.getPrivateWell();
        
        oldAddr = oldData.getLocationAddress();   
        if (oldAddr.getId() != null)
            duplicateAddress(newData.getLocationAddress(), oldAddr);
        
        oldAddr = oldData.getReportToAddress();   
        if (oldAddr.getId() != null)
            duplicateAddress(newData.getReportToAddress(), oldAddr);
        
        newData.setOrganizationId(oldData.getOrganizationId());
        newData.setReportToName(oldData.getReportToName());
        newData.setReportToAttention(oldData.getReportToAttention());
        newData.setLocation(oldData.getLocation());
        newData.setOwner(oldData.getOwner());
        newData.setCollector(oldData.getCollector());
        newData.setWellNumber(oldData.getWellNumber());
        newData.setOrganization(oldData.getOrganization());
    }
    
    private static void duplicateSDWIS(SampleSDWISManager newMan, SampleSDWISManager oldMan) {
        SampleSDWISViewDO newData, oldData;
        
        newData = newMan.getSDWIS();
        oldData = oldMan.getSDWIS();
        
        newData.setPwsId(oldData.getPwsId());
        newData.setStateLabId(oldData.getStateLabId());
        newData.setFacilityId(oldData.getFacilityId());
        newData.setSampleTypeId(oldData.getSampleTypeId());
        newData.setSampleCategoryId(oldData.getSampleCategoryId());
        newData.setSamplePointId(oldData.getSamplePointId());
        newData.setLocation(oldData.getLocation());
        newData.setCollector(oldData.getCollector());
        newData.setPwsName(oldData.getPwsName());
        newData.setPwsNumber0(oldData.getPwsNumber0());
    }
    
    private static void duplicateNotes(NoteManager newMan, NoteManager oldMan) throws Exception {
        NoteViewDO newData, oldData;
                
        for (int i = 0; i < oldMan.count(); i++) {
            oldData = oldMan.getNoteAt(i);
            newData = new NoteViewDO();
            
            newData.setIsExternal(oldData.getIsExternal());
            newData.setReferenceId(oldData.getReferenceId());
            newData.setReferenceTableId(oldData.getReferenceTableId());
            newData.setSubject(oldData.getSubject());
            newData.setSystemUserId(oldData.getSystemUserId());
            newData.setText(oldData.getText());
            newData.setSystemUser(oldData.getSystemUser());
            newMan.addNote(newData);
        }
    }        
    
    private static void duplicateAuxData(AuxDataManager newMan, AuxDataManager oldMan) {
        AuxDataViewDO newAD, oldAD;
        AuxFieldViewDO newAF, oldAF;
        AuxFieldValueViewDO newAFV, oldAFV;
        ArrayList<AuxFieldValueViewDO> newList, oldList; 

        for (int i = 0; i < oldMan.count(); i++) {            
            oldAD = oldMan.getAuxDataAt(i);
            newAD = new AuxDataViewDO();           
            duplicateAuxData(newAD, oldAD);
                       
            newAF = new AuxFieldViewDO();
            oldAF = oldMan.getAuxFieldAt(i);            
            duplicateAuxField(newAF, oldAF); 
            
            newList = new ArrayList<AuxFieldValueViewDO>();
            oldList = oldMan.getAuxValuesAt(i);
            
            for (int j = 0; j < oldList.size(); j++) {
                newAFV = new AuxFieldValueViewDO();
                oldAFV = oldList.get(j);                
                duplicateAuxFieldValue(newAFV, oldAFV);
                newList.add(newAFV);
            }
            
            newMan.addAuxDataFieldAndValues(newAD, newAF, newList);
        }
    }
    
    private static void duplicateAddress(AddressDO newData, AddressDO oldData) {        
        newData.setCellPhone(oldData.getCellPhone());
        newData.setCity(oldData.getCity());
        newData.setCountry(oldData.getCountry());
        newData.setEmail(oldData.getEmail());
        newData.setFaxPhone(oldData.getFaxPhone());
        newData.setHomePhone(oldData.getHomePhone());
        newData.setMultipleUnit(oldData.getMultipleUnit());
        newData.setState(oldData.getState());
        newData.setStreetAddress(oldData.getStreetAddress());
        newData.setWorkPhone(oldData.getWorkPhone());
        newData.setZipCode(oldData.getZipCode());
    }
    
    private static void duplicateAuxData(AuxDataViewDO newData, AuxDataViewDO oldData) {
        newData.setAuxFieldId(oldData.getAuxFieldId());
        newData.setReferenceTableId(oldData.getReferenceTableId());
        newData.setIsReportable(oldData.getIsReportable());
        newData.setTypeId(oldData.getTypeId());
        newData.setValue(oldData.getValue());
        newData.setDictionary(oldData.getDictionary());
        newData.setGroupId(oldData.getGroupId());
        newData.setAnalyteId(oldData.getAnalyteId());
        newData.setAnalyteName(oldData.getAnalyteName());
        newData.setAnalyteExternalId(oldData.getAnalyteExternalId());
    }
    
    private static void duplicateAuxField(AuxFieldViewDO newData, AuxFieldViewDO oldData) {
        newData.setAuxFieldGroupId(oldData.getAuxFieldGroupId());
        newData.setSortOrder(oldData.getSortOrder());
        newData.setAnalyteId(oldData.getAnalyteId());
        newData.setDescription(oldData.getDescription());
        newData.setMethodId(oldData.getMethodId());
        newData.setUnitOfMeasureId(oldData.getUnitOfMeasureId());
        newData.setIsRequired(oldData.getIsRequired());
        newData.setIsActive(oldData.getIsActive());
        newData.setIsReportable(oldData.getIsReportable());
        newData.setScriptletId(oldData.getScriptletId());
        newData.setAnalyteName(oldData.getAnalyteName());
        newData.setMethodName(oldData.getMethodName());
        newData.setScriptletName(oldData.getScriptletName());
        newData.setUnitOfMeasureName(oldData.getUnitOfMeasureName());
    }   
    
    private static void duplicateAuxFieldValue(AuxFieldValueViewDO entity, AuxFieldValueViewDO data) {
        entity.setAuxFieldId(data.getAuxFieldId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());   
        entity.setDictionary(data.getDictionary());
    }
}