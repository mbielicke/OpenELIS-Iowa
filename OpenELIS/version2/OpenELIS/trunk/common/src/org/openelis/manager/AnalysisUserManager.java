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

import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AnalysisUserManager implements RPC {

    private static final long                           serialVersionUID = 1L;
    protected Integer                                   analysisId;
    protected ArrayList<AnalysisUserViewDO>             analysisUserList, deletedList;

    protected transient static AnalysisUserManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static AnalysisUserManager getInstance() {
        AnalysisUserManager aum;

        aum = new AnalysisUserManager();
        aum.analysisUserList = new ArrayList<AnalysisUserViewDO>();

        return aum;
    }

    public AnalysisUserViewDO getAnalysisUserAt(int i) {
        return analysisUserList.get(i);
    }

    public void addAnalysisUser(AnalysisUserViewDO analysisUser) throws Exception {
        if (analysisUserList == null)
            analysisUserList = new ArrayList<AnalysisUserViewDO>();

        analysisUserList.add(analysisUser);
    }

    public void removeAnalysisUserAt(int i) {
        if (analysisUserList == null || i >= analysisUserList.size())
            return;

        AnalysisUserViewDO tmpDO = analysisUserList.remove(i);

        if (deletedList == null)
            deletedList = new ArrayList<AnalysisUserViewDO>();

        if (tmpDO.getId() != null)
            deletedList.add(tmpDO);
    }

    public int count() {
        if (analysisUserList == null)
            return 0;

        return analysisUserList.size();
    }

    public static AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return proxy().fetchByAnalysisId(analysisId);
    }

    // service methods
    public AnalysisUserManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisUserManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();

        proxy().validate(this, errorsList);

        if (errorsList.size() > 0)
            throw errorsList;
    }

    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }

    // these are friendly methods so only managers and proxies can call this
    // method
    Integer getAnalysisId() {
        return analysisId;
    }

    void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    ArrayList<AnalysisUserViewDO> getAnalysisUsers() {
        return analysisUserList;
    }

    void setAnalysisUsers(ArrayList<AnalysisUserViewDO> analysisUsers) {
        analysisUserList = analysisUsers;
    }

    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AnalysisUserViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }

    private static AnalysisUserManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisUserManagerProxy();

        return proxy;
    }
}