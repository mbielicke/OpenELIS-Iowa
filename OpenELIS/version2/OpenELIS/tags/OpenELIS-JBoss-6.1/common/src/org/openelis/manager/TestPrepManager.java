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

import org.openelis.domain.TestPrepViewDO;
import org.openelis.gwt.common.RPC;

public class TestPrepManager implements RPC {

    private static final long                       serialVersionUID = 1L;

    protected Integer                               testId;
    protected ArrayList<TestPrepViewDO>             preps;
    protected ArrayList<TestPrepViewDO>             deleted;

    protected transient static TestPrepManagerProxy proxy;

    protected TestPrepManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static TestPrepManager getInstance() {
        return new TestPrepManager();
    }
    
    public int count() {
        if (preps == null)
            return 0;

        return preps.size();
    }
    
    public TestPrepViewDO getPrepAt(int i) {
        return preps.get(i);
    }

    public void setPrepAt(TestPrepViewDO prepTest, int i) {
        if (preps == null)
            preps = new ArrayList<TestPrepViewDO>();
        preps.set(i, prepTest);
    }

    public void addPrep(TestPrepViewDO prepTest) {
        if (preps == null)
            preps = new ArrayList<TestPrepViewDO>();
        preps.add(prepTest);
    }

    public void addPrepAt(TestPrepViewDO prepTest, int i) {
        if (preps == null)
            preps = new ArrayList<TestPrepViewDO>();
        preps.add(i, prepTest);
    }

    public void removePrepAt(int i) {
        TestPrepViewDO prepTest;
        if (preps == null || i >= preps.size())
            return;

        prepTest = preps.remove(i);
        if (prepTest.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<TestPrepViewDO>();
            deleted.add(prepTest);
        }
    }

    public TestPrepViewDO getRequiredTestPrep() {
        TestPrepViewDO prepDO;
        for (int i = 0; i < count(); i++ ) {
            prepDO = preps.get(i);
            if ("N".equals(prepDO.getIsOptional()))
                return prepDO;
        }

        return null;
    }

    public static TestPrepManager fetchByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public TestPrepManager add() throws Exception {
        return proxy().add(this);
    }

    public TestPrepManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    Integer getTestId() {
        return testId;
    }

    void setTestId(Integer testId) {
        this.testId = testId;
    }

    ArrayList<TestPrepViewDO> getPreps() {
        return preps;
    }

    void setPreps(ArrayList<TestPrepViewDO> prepTests) {
        this.preps = prepTests;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;

        return deleted.size();
    }

    TestPrepViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static TestPrepManagerProxy proxy() {
        if (proxy == null)
            proxy = new TestPrepManagerProxy();

        return proxy;
    }

}
