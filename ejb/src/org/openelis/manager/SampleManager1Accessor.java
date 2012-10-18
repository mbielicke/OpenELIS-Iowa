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

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.DataObject;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleSDWISDO;
import org.openelis.domain.StorageViewDO;

/**
 * This class is used to bulk load sample manager.
 */
public class SampleManager1Accessor {
    /**
     * Set/get objects from sample manager 
     */
    public static SampleDO getSample(SampleManager1 sm) {
        return sm.sample;
    }

    public static void setSample(SampleManager1 sm, SampleDO sample) {
        sm.sample = sample;
    }
    
    public static SampleEnvironmentalDO getSampleEnvironmental(SampleManager1 sm) {
        return sm.sampleEnvironmental;
    }
    
    public static void setSampleEnvironmental(SampleManager1 sm, SampleEnvironmentalDO sampleEnvironmental) {
        sm.sampleEnvironmental = sampleEnvironmental;
    }
    
    public static SampleSDWISDO getSampleSDWIS(SampleManager1 sm) {
        return sm.sampleSDWIS;
    }
    
    public static void setSampleSDWIS(SampleManager1 sm, SampleSDWISDO sampleSDWIS) {
        sm.sampleSDWIS = sampleSDWIS;
    }
    
    public static SamplePrivateWellDO getSamplePrivateWell(SampleManager1 sm) {
        return sm.samplePrivateWell;
    }
    
    public static void setSamplePrivateWell(SampleManager1 sm, SamplePrivateWellDO samplePrivateWell) {
        sm.samplePrivateWell = samplePrivateWell;
    }
    
    public static ArrayList<SampleOrganizationViewDO> getOrganizations(SampleManager1 sm) {
        return sm.organizations;
    }
    
    public static void setOrganizations(SampleManager1 sm, ArrayList<SampleOrganizationViewDO> organizations) {
        sm.organizations = organizations;
    }
    
    public static void addOrganization(SampleManager1 sm, SampleOrganizationViewDO organization) {
        if (sm.organizations == null)
            sm.organizations = new ArrayList<SampleOrganizationViewDO>();
        sm.organizations.add(organization);
    }
    
    public static ArrayList<SampleProjectViewDO> getProjects(SampleManager1 sm) {
        return sm.projects;
    }
    
    public static void setProjects(SampleManager1 sm, ArrayList<SampleProjectViewDO> projects) {
        sm.projects = projects;
    }
    
    public static void addProject(SampleManager1 sm, SampleProjectViewDO project) {
        if (sm.projects == null)
            sm.projects = new ArrayList<SampleProjectViewDO>();
        sm.projects.add(project);
    }
    
    public static ArrayList<SampleQaEventViewDO> getSampleQAs(SampleManager1 sm) {
        return sm.sampleQAs;
    }
    
    public static void setSampleQAs(SampleManager1 sm, ArrayList<SampleQaEventViewDO> sampleQAs) {
        sm.sampleQAs = sampleQAs;
    }
    
    public static void addSampleQA(SampleManager1 sm, SampleQaEventViewDO sampleQA) {
        if (sm.sampleQAs == null)
            sm.sampleQAs = new ArrayList<SampleQaEventViewDO>();
        sm.sampleQAs.add(sampleQA);
    }
    
    public static ArrayList<AnalysisQaEventViewDO> getAnalysisQAs(SampleManager1 sm) {
        return sm.analysisQAs;
    }
    
    public static void setAnalysisQAs(SampleManager1 sm, ArrayList<AnalysisQaEventViewDO> analysisQAs) {
        sm.analysisQAs = analysisQAs;
    }
    
    public static void addAnalysisQA(SampleManager1 sm, AnalysisQaEventViewDO analysisQA) {
        if (sm.analysisQAs == null)
            sm.analysisQAs = new ArrayList<AnalysisQaEventViewDO>();
        sm.analysisQAs.add(analysisQA);
    }
    
    public static ArrayList<AuxDataViewDO> getAuxilliary(SampleManager1 sm) {
        return sm.auxilliary;
    }
    
    public static void setAuxilliary(SampleManager1 sm, ArrayList<AuxDataViewDO> auxilliary) {
        sm.auxilliary = auxilliary;
    }
    
    public static void addAuxilliary(SampleManager1 sm, AuxDataViewDO auxilliary) {
        if (sm.auxilliary == null)
            sm.auxilliary = new ArrayList<AuxDataViewDO>();
        sm.auxilliary.add(auxilliary);
    }
    
    public static ArrayList<SampleItemViewDO> getItems(SampleManager1 sm) {
        return sm.items;
    }
    
    public static void setItems(SampleManager1 sm, ArrayList<SampleItemViewDO> items) {
        sm.items = items;
    }
    
    public static void addItem(SampleManager1 sm, SampleItemViewDO item) {
        if (sm.items == null)
            sm.items = new ArrayList<SampleItemViewDO>();
        sm.items.add(item);
    }
    
    public static ArrayList<StorageViewDO> getStorages(SampleManager1 sm) {
        return sm.storages;
    }
    
    public static void setStorages(SampleManager1 sm, ArrayList<StorageViewDO> storages) {
        sm.storages = storages;
    }
    
    public static void addStorage(SampleManager1 sm, StorageViewDO storage) {
        if (sm.storages == null)
            sm.storages = new ArrayList<StorageViewDO>();
        sm.storages.add(storage);
    }
    
    public static ArrayList<AnalysisViewDO> getAnalyses(SampleManager1 sm) {
        return sm.analyses;
    }
    
    public static void setAnalyses(SampleManager1 sm, ArrayList<AnalysisViewDO> analyses) {
        sm.analyses = analyses;
    }
    
    public static void addAnalysis(SampleManager1 sm, AnalysisViewDO analysis) {
        if (sm.analyses == null)
            sm.analyses = new ArrayList<AnalysisViewDO>();
        sm.analyses.add(analysis);
    }
    
    public static ArrayList<AnalysisUserViewDO> getUsers(SampleManager1 sm) {
        return sm.users;
    }
    
    public static void setUsers(SampleManager1 sm, ArrayList<AnalysisUserViewDO> users) {
        sm.users = users;
    }
    
    public static void addUser(SampleManager1 sm, AnalysisUserViewDO user) {
        if (sm.users == null)
            sm.users = new ArrayList<AnalysisUserViewDO>();
        sm.users.add(user);
    }
    
    public static ArrayList<ResultViewDO> getResults(SampleManager1 sm) {
        return sm.results;
    }
    
    public static void setResults(SampleManager1 sm, ArrayList<ResultViewDO> results) {
        sm.results = results;
    }
    
    public static void addResult(SampleManager1 sm, ResultViewDO result) {
        if (sm.results == null)
            sm.results = new ArrayList<ResultViewDO>();
        sm.results.add(result);
    }
    
    public static ArrayList<DataObject> getRemoved(SampleManager1 sm) {
        return sm.removed;
    }
    
    public static void setRemoved(SampleManager1 sm, ArrayList<DataObject> removed) {
        sm.removed = removed;
    }
}