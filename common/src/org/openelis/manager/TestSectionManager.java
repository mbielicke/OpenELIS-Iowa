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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestSectionViewDO;

public class TestSectionManager implements Serializable {

    /**
     * 
     */
    private static final long                          serialVersionUID = 1L;

    protected Integer                                  testId;
    public ArrayList<TestSectionViewDO>                sections;
    private ArrayList<TestSectionViewDO>               deleted;

    protected transient static TestSectionManagerProxy proxy;

    protected TestSectionManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static TestSectionManager getInstance() {
        TestSectionManager tsm;

        tsm = new TestSectionManager();
        tsm.sections = new ArrayList<TestSectionViewDO>();

        return tsm;
    }

    public int count() {
        if (sections == null)
            return 0;

        return sections.size();
    }

    public TestSectionViewDO getSectionAt(int i) {
        return sections.get(i);
    }

    public void setSectionAt(TestSectionViewDO data, int i) {
        if (sections == null)
            sections = new ArrayList<TestSectionViewDO>();
        sections.set(i, data);
    }

    public void addSection(TestSectionViewDO data) {
        if (sections == null)
            sections = new ArrayList<TestSectionViewDO>();
        sections.add(data);
    }

    public void addSectionAt(TestSectionViewDO data, int i) {
        if (sections == null)
            sections = new ArrayList<TestSectionViewDO>();

        sections.add(i, data);
    }

    public void removeSectionAt(int i) {
        TestSectionViewDO data;
        if (sections == null || i >= sections.size())
            return;

        data = sections.remove(i);

        if (data.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<TestSectionViewDO>();

            deleted.add(data);
        }
    }

    // service methods
    public TestSectionManager add() throws Exception {
        return proxy().add(this);
    }

    public TestSectionManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    public TestSectionViewDO getDefaultSection() throws Exception {
        DictionaryDO locationDict;
        Integer locationId;
        Preferences1 prefs;
        String locationName, locationSuffix, sectionName;
        TestSectionViewDO data;

        data = null;

        prefs = proxy().userRoot();
        locationSuffix = "NONE";
        locationId = prefs.getInt("location", -1);
        if (locationId != -1) {
            locationDict = proxy().getDictionaryById(locationId);
            locationName = locationDict.getSystemName();
            locationSuffix = locationName.substring(locationName.lastIndexOf("_") + 1);
        }

        for (int i = 0; i < sections.size(); i++ ) {
            if (Constants.dictionary().TEST_SECTION_DEFAULT.equals(sections.get(i).getFlagId())) {
                data = sections.get(i);
                break;
            } else if (Constants.dictionary().TEST_SECTION_MATCH.equals(sections.get(i).getFlagId())) {
                sectionName = sections.get(i).getSection();
                if (locationSuffix.equals(sectionName.substring(sectionName.lastIndexOf("-") + 1))) {
                    data = sections.get(i);
                    break;
                }
            }
        }

        return data;
    }

    public ArrayList<TestSectionViewDO> getSections() {
        return sections;
    }

    Integer getTestId() {
        return testId;
    }

    void setTestId(Integer testId) {
        this.testId = testId;
    }

    void setSections(ArrayList<TestSectionViewDO> testSections) {
        this.sections = testSections;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    TestSectionViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static TestSectionManagerProxy proxy() {
        if (proxy == null)
            proxy = new TestSectionManagerProxy();

        return proxy;
    }

}
