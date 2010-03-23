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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.meta.SampleMeta;

public class SampleManagerProxy {
    public SampleManager fetchById(Integer sampleId) throws Exception {
        SampleDO sampleDO;
        SampleManager sm;
        
        sampleDO = sampleLocal().fetchById(sampleId);

        sm = SampleManager.getInstance();
        sm.setSample(sampleDO);

        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();
        sm.getSampleItems();

        return sm;
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        SampleDO sampleDO;
        SampleManager sm;
        
        sampleDO= sampleLocal().fetchByAccessionNumber(accessionNumber);

        sm = SampleManager.getInstance();
        sm.setSample(sampleDO);
        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();
        sm.getSampleItems();

        return sm;
    }

    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        SampleDO sampleDO;
        SampleManager sm;
        SampleItemManager sim;
        SampleItemViewDO siDO;
        ArrayList<SampleItemViewDO> items;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisManager> anaMap;
        AnalysisViewDO anDO;
        AnalysisManager am;
        
        sampleDO = sampleLocal().fetchById(sampleId);

        sm = SampleManager.getInstance();
        sm.setSample(sampleDO);

        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();

        // sample item
        items = (ArrayList<SampleItemViewDO>)sampleItemLocal().fetchBySampleId(sampleId);
        sim = SampleItemManager.getInstance();
        sim.setSampleId(sampleId);
        sim.setSampleManager(sm);
        sim.addSampleItems(items);
        sm.sampleItems = sim;

        anaMap = new HashMap<Integer, AnalysisManager>();
        for (int i = 0; i < sim.count(); i++ ) {
            siDO = sim.getSampleItemAt(i);
            am = AnalysisManager.getInstance();
            am.setSampleItemId(siDO.getId());
            am.setSampleItemManager(sim);
            am.setSampleItemBundle(sim.getBundleAt(i));
            sim.setAnalysisAt(am, i);

            anaMap.put(siDO.getId(), am);
        }

        // fetch analyses
        try{
            analyses = (ArrayList<AnalysisViewDO>)analysisLocal().fetchBySampleId(sampleId);
            for (int i = 0; i < analyses.size(); i++ ) {
                anDO = analyses.get(i);
                am = anaMap.get(anDO.getSampleItemId());
                am.addAnalysis(anDO);
            }
        }catch(NotFoundException e){
            //ignore
        }

        return sm;
    }

    public SampleManager add(SampleManager man) throws Exception {
        Integer sampleId, sampleRefId;
        
        sampleLocal().add(man.getSample());
        sampleId = man.getSample().getId();
        sampleRefId = ReferenceTable.SAMPLE;

        if(man.getDomainManager() != null){
            man.getDomainManager().setSampleId(sampleId);
            man.getDomainManager().add();
        }

        man.getSampleItems().setSampleId(sampleId);
        man.getSampleItems().add();

        if (man.organizations != null) {
            man.getOrganizations().setSampleId(sampleId);
            man.getOrganizations().add();
        }

        if (man.projects != null) {
            man.getProjects().setSampleId(sampleId);
            man.getProjects().add();
        }

        if (man.qaEvents != null) {
            man.getQaEvents().setSampleId(sampleId);
            man.getQaEvents().add();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceTableId(sampleRefId);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().add();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(sampleRefId);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().add();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(sampleRefId);
            man.getExternalNote().setReferenceId(sampleId);
            man.getExternalNote().add();
        }

        return man;
    }

    public SampleManager update(SampleManager man) throws Exception {
        Integer sampleId, sampleRefId;
        
        sampleLocal().update(man.getSample());
        sampleId = man.getSample().getId();
        sampleRefId = ReferenceTable.SAMPLE;

        if (man.sampleDomain != null) {
            man.getDomainManager().setSampleId(sampleId);
            man.getDomainManager().update();
        }

        if (man.sampleItems != null) {
            man.getSampleItems().setSampleId(sampleId);
            man.getSampleItems().update();
        }

        if (man.organizations != null) {
            man.getOrganizations().setSampleId(sampleId);
            man.getOrganizations().update();
        }

        if (man.projects != null) {
            man.getProjects().setSampleId(sampleId);
            man.getProjects().update();
        }

        if (man.qaEvents != null) {
            man.getQaEvents().setSampleId(sampleId);
            man.getQaEvents().update();
        }

        if (man.auxData != null) {
            man.getAuxData().setReferenceTableId(sampleRefId);
            man.getAuxData().setReferenceId(sampleId);
            man.getAuxData().update();
        }

        if (man.sampleInternalNotes != null) {
            man.getInternalNotes().setReferenceTableId(sampleRefId);
            man.getInternalNotes().setReferenceId(sampleId);
            man.getInternalNotes().update();
        }

        if (man.sampleExternalNote != null) {
            man.getExternalNote().setReferenceTableId(sampleRefId);
            man.getExternalNote().setReferenceId(sampleId);
            man.getExternalNote().update();
        }

        return man;
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        SampleDO sampleDO;
        SampleManager sm;
        SampleItemManager sim;
        SampleItemViewDO siDO;
        ArrayList<SampleItemViewDO> items;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisManager> anaMap;
        AnalysisViewDO anDO;
        AnalysisManager am;
        HashMap<Integer, TestManager> testCache;
        int addedIndex;
        TestManager tm;

        sampleDO = sampleLocal().fetchById(sampleId);

        sm = SampleManager.getInstance();
        sm.setSample(sampleDO);

        sm.getDomainManager();
        sm.getOrganizations();
        sm.getProjects();

        // sample item
        items = (ArrayList<SampleItemViewDO>)sampleItemLocal().fetchBySampleId(sampleId);
        sim = SampleItemManager.getInstance();
        sim.setSampleId(sampleId);
        sim.setSampleManager(sm);
        sim.addSampleItems(items);
        sm.sampleItems = sim;

        anaMap = new HashMap<Integer, AnalysisManager>();
        for (int i = 0; i < sim.count(); i++ ) {
            siDO = sim.getSampleItemAt(i);
            am = AnalysisManager.getInstance();
            am.setSampleItemId(siDO.getId());
            am.setSampleItemManager(sim);
            am.setSampleItemBundle(sim.getBundleAt(i));
            sim.setAnalysisAt(am, i);

            anaMap.put(siDO.getId(), am);
        }

        // fetch analyses for update
        try{
            analyses = (ArrayList<AnalysisViewDO>)analysisLocal().fetchBySampleId(sampleId);
            testCache = new HashMap<Integer, TestManager>();
            for (int i = 0; i < analyses.size(); i++ ) {
                anDO = analyses.get(i);
    
                am = anaMap.get(anDO.getSampleItemId());
                addedIndex = am.addAnalysis(anDO);
    
                tm = testCache.get(anDO.getTestId());
                if (tm == null) {
                    tm = TestManager.fetchWithPrepTestsSampleTypes(anDO.getTestId());
                    testCache.put(anDO.getTestId(), tm);
                }
                am.setTestAt(tm, addedIndex);
            }
        }catch(NotFoundException e){
            //ignore
        }

        return sm;
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public Integer getIdFromSystemName(String systemName) throws Exception {
        DictionaryDO dictDO;
        
        dictDO = dictionaryLocal().fetchBySystemName(systemName);
        return dictDO.getId();
    }
    
    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin,end);    
    }

    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
      //revalidate accession number
        validateAccessionNumber(man.getSample(), errorsList);
        
        //sample validate code
        SampleDO sampleDO = man.getSample();
        
        if(sampleDO.getCollectionDate() != null && sampleDO.getReceivedDate() != null){
            if(sampleDO.getCollectionDate().compareTo(sampleDO.getReceivedDate()) == 1)
                errorsList.add(new FieldErrorException("collectedDateInvalidError", SampleMeta.getReceivedDate()));
       }
        
       if(man.sampleItems != null)
           man.getSampleItems().validate(errorsList);
       
       if(man.organizations != null)
           man.getOrganizations().validate(errorsList);
       
       if(man.projects != null)
           man.getProjects().validate(errorsList);
       
       if(man.qaEvents != null)
           man.getQaEvents().validate(errorsList);
       
       if(man.auxData != null)
           man.getAuxData().validate(errorsList);
    }

    private SampleLocal sampleLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SampleLocal)ctx.lookup("openelis/SampleBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private void validateAccessionNumber(SampleDO sampleDO, ValidationErrorsList errorsList) throws Exception {
        try{
            sampleManagerLocal().validateAccessionNumber(sampleDO);
    
        }catch(ValidationErrorsList e){
            ArrayList<Exception> errors = e.getErrorList();
            
            for(int i=0; i<errors.size(); i++)
                errorsList.add(errors.get(i));
        }
    }

    private SampleManagerLocal sampleManagerLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SampleManagerLocal)ctx.lookup("openelis/SampleManagerBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private SampleItemLocal sampleItemLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SampleItemLocal)ctx.lookup("openelis/SampleItemBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private AnalysisLocal analysisLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (AnalysisLocal)ctx.lookup("openelis/AnalysisBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private SystemVariableLocal sysVariableLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (SystemVariableLocal)ctx.lookup("openelis/SystemVariableBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static DictionaryLocal dictionaryLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
