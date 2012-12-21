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
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.ToDoSampleViewVO;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.gwt.common.DataBaseUtil;

/**
 * This class provides data for todo lists corresponding to analyses and samples
 * in various statuses and worksheets, shown on the various tabs on To Do screen. 
 * Each get* method is used to fill a specific tab.
 */
@Stateless
@SecurityDomain("openelis")

public class ToDoBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager          manager;

    @EJB
    private DictionaryBean         dictionary;

    @EJB
    private WorksheetAnalysisBean worksheetAnalysis;

    private static Integer        loggedInStatusId, initiatedStatusId, completedStatusId,
                                    notVerifiedStatusId;

    private static final Logger  log = Logger.getLogger("openelis");
    
    @PostConstruct
    public void init() {
        try {
            loggedInStatusId = dictionary.fetchBySystemName("analysis_logged_in").getId();
            initiatedStatusId = dictionary.fetchBySystemName("analysis_initiated").getId();
            completedStatusId = dictionary.fetchBySystemName("analysis_completed").getId();
            notVerifiedStatusId = dictionary.fetchBySystemName("sample_not_verified").getId();
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
        }
    }
    
    public ArrayList<AnalysisViewVO> getLoggedIn() throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchByAnalysisStatusId");
        query.setParameter("statusId", loggedInStatusId);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<AnalysisViewVO> getInitiated() throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchByAnalysisStatusId");
        query.setParameter("statusId", initiatedStatusId);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<AnalysisViewVO> getCompleted() throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchByAnalysisStatusId");
        query.setParameter("statusId", completedStatusId);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<AnalysisViewVO> getReleased() throws Exception {
        Query query;
        Calendar cal;

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-4);
        
        query = manager.createNamedQuery("AnalysisView.FetchReleased");
        query.setParameter("releasedDate", cal.getTime());

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<AnalysisViewVO> getOther() throws Exception {
        Query query;

        query = manager.createNamedQuery("AnalysisView.FetchOther");
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<ToDoSampleViewVO> getToBeVerified() throws Exception {
        Query query;

        query = manager.createNamedQuery("ToDoSampleView.FetchBySampleStatusId");
        query.setParameter("statusId", notVerifiedStatusId);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<ToDoWorksheetVO> getWorksheet() throws Exception {
        return worksheetAnalysis.fetchByWorking();
    }
}