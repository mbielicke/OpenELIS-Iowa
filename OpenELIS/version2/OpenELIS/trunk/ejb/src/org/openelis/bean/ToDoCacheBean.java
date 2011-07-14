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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.AnalysisCacheVO;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleCacheVO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleQaEventDO;
import org.openelis.domain.SectionDO;
import org.openelis.domain.WorksheetCacheVO;
import org.openelis.entity.AnalysisQaevent;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SectionLocal;
import org.openelis.local.ToDoCacheLocal;
import org.openelis.remote.ToDoCacheRemote;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for todo lists
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=ToDoCacheBean")
public class ToDoCacheBean implements ToDoCacheLocal, ToDoCacheRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    private Cache         loggedInCache, initiatedCache, completedCache, 
                          releasedCache, toBeVerifiedCache, otherCache;
    
    public ToDoCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        loggedInCache = cm.getCache("loggedIn");       
        initiatedCache = cm.getCache("initiated");
        completedCache = cm.getCache("completed");
        releasedCache = cm.getCache("released");
        toBeVerifiedCache = cm.getCache("toBeVerified");
        otherCache = cm.getCache("other");
    }     
    
    public ArrayList<AnalysisCacheVO> getLoggedIn() throws Exception {
        int size;
        ArrayList<AnalysisCacheVO> list;        
        List entryList;
        DictionaryDO data;  
        
        entryList = loggedInCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || loggedInCache.getSize() > size) {
            data = EJBFactory.getDictionaryCache().getBySystemName("analysis_logged_in");
            list = reloadAnalysisCache(loggedInCache, data.getId());
        } else { 
            list = getAnalysisListFromCache(loggedInCache);
        }
        
        return list;
    }
    
    public ArrayList<AnalysisCacheVO> getInitiated() throws Exception {
        int size;
        ArrayList<AnalysisCacheVO> list;        
        List entryList;
        DictionaryDO data;  
        
        entryList = initiatedCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || initiatedCache.getSize() > size) {
            data = EJBFactory.getDictionaryCache().getBySystemName("analysis_initiated");
            list = reloadAnalysisCache(initiatedCache, data.getId());
        } else { 
            list = getAnalysisListFromCache(initiatedCache);
        }
        
        return list;
    }
    
    public ArrayList<AnalysisCacheVO> getCompleted() throws Exception {
        int size;
        ArrayList<AnalysisCacheVO> list;        
        List entryList;
        DictionaryDO data;  
        
        entryList = completedCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || completedCache.getSize() > size) {
            data = EJBFactory.getDictionaryCache().getBySystemName("analysis_completed");
            list = reloadAnalysisCache(completedCache, data.getId());
        } else { 
            list = getAnalysisListFromCache(completedCache);
        }
        
        return list;
    }
    
    public ArrayList<AnalysisCacheVO> getReleased() throws Exception {
        int size;
        ArrayList<AnalysisCacheVO> list;        
        List entryList;
        
        entryList = releasedCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || releasedCache.getSize() > size)             
            list = reloadAnalysisCache(releasedCache, null);
        else 
            list = getAnalysisListFromCache(releasedCache);        
        
        return list;
    }
    
    public ArrayList<SampleCacheVO> getToBeVerified() throws Exception {
        int size;
        ArrayList<SampleCacheVO> list;        
        List entryList;
        DictionaryDO data;  
        
        entryList = toBeVerifiedCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || toBeVerifiedCache.getSize() > size) {
            data = EJBFactory.getDictionaryCache().getBySystemName("sample_not_verified");
            list = reloadSampleCache(toBeVerifiedCache, data.getId());
        } else { 
            list = getSampleListFromCache(toBeVerifiedCache);
        }
        
        return list;
    }
    
    public ArrayList<AnalysisCacheVO> getOther() throws Exception {
        int size;
        ArrayList<AnalysisCacheVO> list;        
        List entryList;
        
        entryList = otherCache.getKeysWithExpiryCheck();
        size = entryList.size();
        /*
         * if either there are no entries in the cache or the alive entries are
         * less than the total entries, the cache is reloaded from the database 
         */
        if (size == 0 || otherCache.getSize() > size) {
            list = reloadAnalysisCache(otherCache, null);
        } else { 
            list = getAnalysisListFromCache(otherCache);
        }
        
        return list;
    }
    
    public ArrayList<WorksheetCacheVO> getWorksheet() throws Exception {
        ArrayList<WorksheetCacheVO> list;
        try {
        list = EJBFactory.getWorksheetAnalysis().fetchByWorking();
        return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }                
    }
        
    public void update(SampleCacheVO data) {
        Integer id, statusId;
        String sname;

        id = data.getId();
        statusId = data.getStatusId();

        try {
            sname = EJBFactory.getDictionaryCache().getById(statusId).getSystemName();
            /*
             * If the status of the sample represented by "data" has changed 
             * from what it was when the sample was previously added to one of
             * the caches then the Element for the sample is removed from the
             * cache that contains it and a new one containing "data" is added 
             * to the appropriate cache based on the current status of the sample.
             * A new Element is also created even if the status is the same as the 
             * previous time because otherwise all the fields in the new and old
             * entry will have to be compared in order to find out about any other changes.         
             */
            if ("sample_not_verified".equals(sname)) {
                getToBeVerified();
                toBeVerifiedCache.put(new Element(id, data));                
            } else {
                /*
                 * this is done so that if the sample's status has changed to 
                 * something other than what the caches pertain to, the sample
                 * gets removed from the cache that it was in previously 
                 */
                if (toBeVerifiedCache.get(id) != null)
                    toBeVerifiedCache.remove(id);                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    public void update(AnalysisCacheVO data) {        
        Integer id, statusId;
        String sname;
        Datetime rd;
        Date mn;

        id = data.getId();
        statusId = data.getStatusId();

        try {
            sname = EJBFactory.getDictionaryCache().getById(statusId).getSystemName();
            /*
             * If the status of the analysis represented by "data" has changed 
             * from what it was when the analysis was previously added to one of
             * the caches then the Element for the analysis is removed from the
             * cache that contains it and a new one containing "data" is added 
             * to the appropriate cache based on the current status of the analysis.
             * A new Element is also created even if the status is the same as the 
             * previous time because otherwise all the fields in the new and old
             * entry will have to be compared in order to find out about any other changes.         
             */
            if ("analysis_logged_in".equals(sname)) {
                getLoggedIn();
                loggedInCache.put(new Element(id, data));
                if (initiatedCache.get(id) != null)
                    initiatedCache.remove(id);
                else if (completedCache.get(id) != null)
                    completedCache.remove(id);
                else if (releasedCache.get(id) != null)
                    releasedCache.remove(id);
                else if (otherCache.get(id) != null)
                    otherCache.remove(id);
            } else if ("analysis_initiated".equals(sname)) {
                getInitiated();
                initiatedCache.put(new Element(id, data));
                if (loggedInCache.get(id) != null)
                    loggedInCache.remove(id);
                else if (completedCache.get(id) != null)
                    completedCache.remove(id);
                else if (releasedCache.get(id) != null)
                    releasedCache.remove(id);
                else if (otherCache.get(id) != null)
                    otherCache.remove(id);
            } else if ("analysis_completed".equals(sname)) {
                getCompleted();
                completedCache.put(new Element(id, data));
                if (loggedInCache.get(id) != null)
                    loggedInCache.remove(id);
                else if (initiatedCache.get(id) != null)
                    initiatedCache.remove(id);
                else if (releasedCache.get(id) != null)
                    releasedCache.remove(id);
                else if (otherCache.get(id) != null)
                    otherCache.remove(id);
            } else if ("analysis_released".equals(sname)) {
                rd = data.getReleasedDate();
                mn = new Date();
                mn.setHours(0);
                mn.setMinutes(0);
                mn.setSeconds(0);
                if ((rd.getDate().getTime()) >= (mn.getTime() - 4*86400000)) {   
                    getReleased();
                    releasedCache.put(new Element(id, data));
                    if (loggedInCache.get(id) != null)
                        loggedInCache.remove(id);
                    else if (initiatedCache.get(id) != null)
                        initiatedCache.remove(id);
                    else if (completedCache.get(id) != null)
                        completedCache.remove(id);
                    else if (otherCache.get(id) != null)
                        otherCache.remove(id);
                } 
            } else {
                /*
                 * this is done so that if the analysis' status has changed to 
                 * something other than what the caches pertain to, the analysis
                 * gets removed from the cache that it was in previously 
                 */
                if (loggedInCache.get(id) != null)
                    loggedInCache.remove(id);
                else if (initiatedCache.get(id) != null)
                    initiatedCache.remove(id);
                else if (completedCache.get(id) != null)
                    completedCache.remove(id);
                else if (releasedCache.get(id) != null)
                    releasedCache.remove(id);
                
                getOther();
                otherCache.put(new Element(id, data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private ArrayList<SampleCacheVO> reloadSampleCache(Cache cache, Integer statusId) throws Exception {
        Query query;
        ArrayList<SampleCacheVO> list;        
        List queryList;
        
        query = manager.createNamedQuery("Sample.FetchForCachingByStatusId");
        query.setParameter("statusId", statusId);        
        queryList = query.getResultList();
        list = createSampleCacheEntries(cache, queryList);
        
        return list;
    }
    
    private ArrayList<AnalysisCacheVO> reloadAnalysisCache(Cache cache, Integer statusId) throws Exception {
        Query query;
        ArrayList<AnalysisCacheVO> list;        
        List queryList;                
        //Datetime d;
        Date rd,mn;
        
        if (statusId != null) {
            query = manager.createNamedQuery("Analysis.FetchForCachingByStatusId");
            query.setParameter("statusId", statusId);
        } else if (cache == releasedCache) {
            //d = Datetime.getInstance(Datetime.YEAR, Datetime.MONTH);
            mn = new Date();
            mn.setHours(0);
            mn.setMinutes(0);
            mn.setSeconds(0);
            rd = new Date(mn.getTime() - 4*86400000);
            query = manager.createNamedQuery("Analysis.FetchReleasedForCaching");
            query.setParameter("releasedDate", rd);
        } else {
            query = manager.createNamedQuery("Analysis.FetchOtherForCaching");
        }
        queryList = query.getResultList();
        list = createAnalysisCacheEntries(cache, queryList);
        
        return list;
    }
    
    private ArrayList<SampleCacheVO> createSampleCacheEntries(Cache cache, List<SampleCacheVO> queryList) throws Exception {
        Integer id;
        String name;      
        Sample sample;       
        AnalysisQAEventLocal aqel;
        SampleQAEventLocal sqel;
        SampleOrganizationLocal sol;
        SamplePrivateWellLocal spwl;
        SamplePrivateWellViewDO spw;
        SampleCacheVO svo;
        Element elem;
        HashMap<Integer, String> sorgMap;
        ArrayList<Integer> sidList;
        ArrayList<SampleOrganizationViewDO> sorgList;
        ArrayList<SampleQaEventDO> sqeList;
        ArrayList<AnalysisQaevent> aqeList;
        ArrayList<SampleCacheVO> voList;

        aqel = EJBFactory.getAnalysisQAEvent();
        sqel = EJBFactory.getSampleQAEvent();
        sol = EJBFactory.getSampleOrganization();
        spwl = EJBFactory.getSamplePrivateWell();
        sorgMap = new HashMap<Integer, String>();
        sidList = new ArrayList<Integer>();
        
        try {
            for (SampleCacheVO data : queryList) {
                data.setQaeventResultOverride("N");
                id = data.getId();
                name = sorgMap.get(id);
                if (name == null) {
                    /*
                     * if the sample's domain is private well(W) then it may not have
                     * its "report to" linked to sample_organization, but present
                     * in sample_private_well     
                     */
                    if ("W".equals(data.getDomain())) {
                        spw = spwl.fetchBySampleId(id);
                        if (spw.getOrganization() != null)
                            name = spw.getOrganization().getName();
                        else
                            name = spw.getReportToName();
                    } else if (!"Q".equals(data.getDomain())) {
                        //
                        // there is no "report to" for quick-entry (Q) samples 
                        //
                        try {
                            sorgList = sol.fetchReportToBySampleId(id);
                            name = sorgList.get(0).getOrganizationName();
                        } catch (NotFoundException e) {
                            name = "";
                        }
                    }
                    sorgMap.put(id, name);
                }
                data.setReportToName(name);
                cache.put(new Element(data.getId(), data));
                sidList.add(data.getId());
            }
        } catch (Exception e) {
            throw e;
        }
        
        /*
         * We find all the sample_qa_events that are linked to the samples 
         * in the cache and have the type "Result Override". We then set the flag
         * corresponding to this field to "Y" in the VO for the sample that a 
         * given sample_qa_event belongs to. We also remove the ids of such samples
         * from the list used in this query so that when we query for analysis_qa_events
         * belonging to the analyses under individual samples we don't unnecessarily
         * query for those samples again as the flag has already been set to "Y" for them. 
         */        
        try {
            if (sidList != null && sidList.size() > 0) {
                sqeList = sqel.fetchResultOverrideBySampleIdList(sidList);
                for (SampleQaEventDO sqe : sqeList) {
                    elem = cache.get(sqe.getSampleId());
                    svo = (SampleCacheVO)elem.getValue();
                    svo.setQaeventResultOverride("Y");
                    sidList.remove(svo.getId());
                }
            }
        } catch (NotFoundException e) {
            // ignore
        } 
        
        /*
         * We find all the analysis_qa_events that are linked to the analyses 
         * under the samples in the cache and have the type "Result Override".
         * We then set the flag corresponding to this field to "Y" in the VO for
         * the sample that contains the analysis that given analysis_qa_events belong to.          
         */        
        try {
            if (sidList != null && sidList.size() > 0) {
                aqeList = aqel.fetchResultOverrideBySampleIdList(sidList);
                for (AnalysisQaevent aqe : aqeList) {
                    sample = aqe.getAnalysis().getSampleItem().getSample();
                    elem = cache.get(sample.getId());
                    svo = (SampleCacheVO)elem.getValue();
                    svo.setQaeventResultOverride("Y");
                }
            }
        } catch (NotFoundException e) {
            // ignore
        }
        voList = getSampleListFromCache(cache);        
        return voList;
    }
    
    private ArrayList<AnalysisCacheVO> createAnalysisCacheEntries(Cache cache, List<AnalysisCacheVO> queryList) throws Exception {
        Integer secId, sampleId;
        String name;
        SectionLocal sl;
        AnalysisQAEventLocal aqel;
        SampleOrganizationLocal sol;
        SamplePrivateWellLocal spwl;
        SectionDO sec;
        SamplePrivateWellViewDO spw;
        Element elem;
        AnalysisCacheVO cvo;
        HashMap<Integer, SectionDO> secMap;
        HashMap<Integer, String> sorgMap;
        ArrayList<Integer> aidList;
        ArrayList<SampleOrganizationViewDO> sorgList;
        ArrayList<AnalysisCacheVO> voList;
        ArrayList<AnalysisQaEventDO> aqeList;

        sl = EJBFactory.getSection();
        aqel = EJBFactory.getAnalysisQAEvent();
        sol = EJBFactory.getSampleOrganization();
        spwl = EJBFactory.getSamplePrivateWell();
        secMap = new HashMap<Integer, SectionDO>();
        sorgMap = new HashMap<Integer, String>();
        voList = new ArrayList<AnalysisCacheVO>();
        aidList = new ArrayList<Integer>();

        try {
            for (AnalysisCacheVO data : queryList) {
                secId = data.getSectionId();
                if (secId != null) {
                    sec = secMap.get(secId);
                    if (sec == null) {
                        sec = sl.fetchById(secId);
                        secMap.put(secId, sec);
                    }
                    data.setSectionName(sec.getName());
                }

                data.setQaeventResultOverride("N");
                sampleId = data.getSampleId();
                name = sorgMap.get(sampleId);
                if (name == null) {
                    /*
                     * if the sample's domain is private well(W) then it may not have
                     * its "report to" linked to sample_organization, but present
                     * in sample_private_well     
                     */
                    if ("W".equals(data.getSampleDomain())) {
                        spw = spwl.fetchBySampleId(sampleId);
                        if (spw.getOrganization() != null)
                            name = spw.getOrganization().getName();
                        else
                            name = spw.getReportToName();
                    } else if ( !"Q".equals(data.getSampleDomain())) {
                         //
                         // there is no "report to" for quick-entry (Q) samples 
                         //
                        try {
                            sorgList = sol.fetchReportToBySampleId(sampleId);
                            name = sorgList.get(0).getOrganizationName();
                        } catch (NotFoundException e) {
                            name = "";
                        }
                    }
                    sorgMap.put(sampleId, name);
                }
                data.setSampleReportToName(name);
                cache.put(new Element(data.getId(), data));

                aidList.add(data.getId());
            }
        } catch (Exception e) {
            throw e;
        }

        /*
         * We find all the analysis_qa_events that are linked to the analyses 
         * in the cache and have the type "Result Override". We then set the flag
         * corresponding to this field to "Y" in the VO for the analysis that a 
         * given analysis_qa_event belongs to. 
         */
        try {
            if (aidList != null && aidList.size() > 0) {
                aqeList = aqel.fetchResultOverrideByAnalysisIdList(aidList);
                for (AnalysisQaEventDO aqe : aqeList) {
                    elem = cache.get(aqe.getAnalysisId());
                    cvo = (AnalysisCacheVO)elem.getValue();
                    cvo.setQaeventResultOverride("Y");
                }
            }
        } catch (NotFoundException e) {
            // ignore
        }

        voList = getAnalysisListFromCache(cache);
        return voList;
    }    
    
    private ArrayList<SampleCacheVO> getSampleListFromCache(Cache cache) {
        ArrayList<SampleCacheVO> list;
        List<Integer> entryList;
        Element elem;

        entryList = cache.getKeys();
        list = new ArrayList<SampleCacheVO>();

        for (Integer key : entryList) {
            elem = cache.get(key);
            list.add((SampleCacheVO)elem.getValue());
        }

        return list;
    }
    
    private ArrayList<AnalysisCacheVO> getAnalysisListFromCache(Cache cache) {
        ArrayList<AnalysisCacheVO> list;
        List<Integer> entryList;
        Element elem;

        entryList = cache.getKeys();
        list = new ArrayList<AnalysisCacheVO>();

        for (Integer key : entryList) {
            elem = cache.get(key);
            list.add((AnalysisCacheVO)elem.getValue());
        }

        return list;
    }
}