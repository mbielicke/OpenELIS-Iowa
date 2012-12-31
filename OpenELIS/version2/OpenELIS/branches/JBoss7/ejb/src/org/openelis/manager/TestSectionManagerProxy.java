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
import java.util.List;

import org.openelis.domain.Constants;
import org.openelis.bean.DictionaryBean;
import org.openelis.bean.TestSectionBean;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.meta.TestMeta;
import org.openelis.utils.EJBFactory;

public class TestSectionManagerProxy {

    public TestSectionManager add(TestSectionManager man) throws Exception {
        TestSectionBean tl;
        TestSectionViewDO data;

        tl = EJBFactory.getTestSection();

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getSectionAt(i);
            data.setTestId(man.getTestId());

            tl.add(data);
        }

        return man;
    }

    public TestSectionManager update(TestSectionManager man) throws Exception {
        TestSectionBean tl;
        TestSectionViewDO data;

        tl = EJBFactory.getTestSection();

        for (int i = 0; i < man.deleteCount(); i++ )
            tl.delete(man.getDeletedAt(i));

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getSectionAt(i);
            if (data.getId() == null) {
                data.setTestId(man.getTestId());
                tl.add(data);
            } else {
                tl.update(data);
            }
        }

        return man;
    }

    public void validate(TestSectionManager man) throws Exception {
        ValidationErrorsList list;
        List<TestSectionViewDO> sectionList;
        TestSectionViewDO data;
        TestSectionBean sl;
        Integer flagId, sectId;
        List<Integer> idList;
        int numDef, numMatch, numBlank, i;
        TableFieldErrorException exc;

        sl = EJBFactory.getTestSection();
        sectionList = man.getSections();
        list = new ValidationErrorsList();

        if (man.count() == 0) {
            list.add(new FieldErrorException("atleastOneSection", null));
            throw list;
        }

        numDef = 0;
        numMatch = 0;
        numBlank = 0;

        idList = new ArrayList<Integer>();
        for (i = 0; i < man.count(); i++ ) {
            data = man.getSectionAt(i);
            flagId = data.getFlagId();
            sectId = data.getSectionId();

            try {
                sl.validate(data);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "sectionTable", i);
            }

            if (idList.contains(sectId)) {
                exc = new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                   TestMeta.getSectionSectionId(), "sectionTable");
                list.add(exc);
            } else {
                idList.add(sectId);
            }

            if (flagId == null) {
                numBlank++ ;
            } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_SECTION_DEFAULT, flagId)) {
                numDef++ ;
            } else if (DataBaseUtil.isSame(Constants.dictionary().TEST_SECTION_MATCH, flagId)) {
                numMatch++ ;
            }
        }

        if (numDef > 1) {
            //
            // exactly one section can be set as "default"
            //
            for (i = 0; i < man.count(); i++ ) {
                data = sectionList.get(i);
                flagId = data.getFlagId();
                if (flagId != null) {
                    exc = new TableFieldErrorException("allSectBlankIfDefException", i,
                                                       TestMeta.getSectionFlagId(), "sectionTable");
                    list.add(exc);
                }
            }
        } else if (numDef == 1 && numBlank != (man.count() - 1)) {
            //
            // if one section has been marked as "default" then all the others
            // must
            // be set to blank
            //
            for (i = 0; i < man.count(); i++ ) {
                data = sectionList.get(i);
                flagId = data.getFlagId();
                if ( !DataBaseUtil.isSame(Constants.dictionary().TEST_SECTION_DEFAULT, flagId)) {
                    exc = new TableFieldErrorException("allSectBlankIfDefException", i,
                                                       TestMeta.getSectionFlagId(), "sectionTable");
                    list.add(exc);
                }
            }
        } else if (numMatch > 0 && numMatch != man.count()) {
            //
            // if one section has been set as "match user location" then all
            // the others must be set to that option
            //
            for (i = 0; i < man.count(); i++ ) {
                data = sectionList.get(i);
                flagId = data.getFlagId();

                if ( !DataBaseUtil.isSame(Constants.dictionary().TEST_SECTION_MATCH, flagId)) {
                    exc = new TableFieldErrorException("allSectMatchFlagException", i,
                                                       TestMeta.getSectionFlagId(), "sectionTable");
                    list.add(exc);
                }
            }
        }

        if (list.size() > 0)
            throw list;
    }
    
    public DictionaryDO getDictionaryById(Integer id) throws Exception {
        return EJBFactory.getDictionaryCache().getById(id);
    }
}