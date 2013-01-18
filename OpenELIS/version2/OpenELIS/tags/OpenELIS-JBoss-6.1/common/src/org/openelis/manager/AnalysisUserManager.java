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
import org.openelis.domain.Constants;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;

public class AnalysisUserManager implements RPC {

    private static final long                         serialVersionUID = 1L;
    protected Integer                                   analysisId;
    protected ArrayList<AnalysisUserViewDO>             analysisUserList, deletedList;

    protected transient static AnalysisUserManagerProxy proxy;

    protected AnalysisUserManager() {
        analysisUserList = new ArrayList<AnalysisUserViewDO>();
    }

    /**
     * Creates a new instance of this object.
     */
    public static AnalysisUserManager getInstance() {
        return new AnalysisUserManager();
    }

    public AnalysisUserViewDO getAnalysisUserAt(int i) {
        return analysisUserList.get(i);
    }

    public int addAnalysisUser(AnalysisUserViewDO analysisUser) {
        analysisUserList.add(analysisUser);
        return count() - 1;
    }

    public int addCompleteRecord() throws Exception {
        SystemUserVO user;
        AnalysisUserViewDO data;

        user = proxy().getSystemUser();

        // if this user has already completed this record, don't add another one
        if (getIndex(Constants.dictionary().AN_USER_AC_COMPLETED, user.getId()) >= 0)
            return -1;

        // multiples allowed
        data = new AnalysisUserViewDO();
        data.setActionId(Constants.dictionary().AN_USER_AC_COMPLETED);
        data.setSystemUserId(user.getId());
        data.setSystemUser(user.getLoginName());

        return addAnalysisUser(data);
    }

    public int addCompleteRecord(SystemUserVO user) throws Exception {
        AnalysisUserViewDO data;

        // if this user has already completed this record, don't add another one
        if (getIndex(Constants.dictionary().AN_USER_AC_COMPLETED, user.getId()) >= 0)
            return -1;

        // multiples allowed
        data = new AnalysisUserViewDO();
        data.setActionId(Constants.dictionary().AN_USER_AC_COMPLETED);
        data.setSystemUserId(user.getId());
        data.setSystemUser(user.getLoginName());

        return addAnalysisUser(data);
    }

    public int addReleaseRecord() throws Exception {
        int i;
        SystemUserVO user;
        AnalysisUserViewDO data;

        user = proxy().getSystemUser();

        // if a release entry already exists delete it, and create the new one
        i = getIndex(Constants.dictionary().AN_USER_AC_RELEASED, null);
        if (i != -1)
            removeAnalysisUserAt(i);
            
        data = new AnalysisUserViewDO();
        data.setActionId(Constants.dictionary().AN_USER_AC_RELEASED);
        data.setSystemUserId(user.getId());
        data.setSystemUser(user.getLoginName());

        return addAnalysisUser(data);
    }

    public void removeAnalysisUserAt(int i) {
        AnalysisUserViewDO data;

        if (i >= analysisUserList.size())
            return;

        data = analysisUserList.remove(i);
        if (data.getId() != null) {
            if (deletedList == null)
                deletedList = new ArrayList<AnalysisUserViewDO>();
            deletedList.add(data);
        }
    }

    public int count() {
        return analysisUserList.size();
    }

    // service methods
    public static AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception {
        return proxy().fetchByAnalysisId(analysisId);
    }

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

    private int getIndex(Integer actionId, Integer systemUserId) {
        AnalysisUserViewDO data;

        for (int i = 0; i < analysisUserList.size(); i++ ) {
            data = analysisUserList.get(i);

            if (actionId.equals(data.getActionId()) &&
                (systemUserId == null || systemUserId.equals(data.getSystemUserId())))
                return i;
        }
        return -1;
    }

    private static AnalysisUserManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisUserManagerProxy();

        return proxy;
    }
}