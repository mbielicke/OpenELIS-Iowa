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

import javax.naming.InitialContext;

import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.TestSectionLocal;
import org.openelis.metamap.TestMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

public class TestSectionManagerProxy {

    private static final TestMetaMap meta = new TestMetaMap();

    public TestSectionManager add(TestSectionManager man) throws Exception {
        TestSectionLocal tl;
        TestSectionViewDO section;

        tl = local();

        for (int i = 0; i < man.count(); i++ ) {
            section = man.getSectionAt(i);
            section.setTestId(man.getTestId());

            tl.add(section);
        }

        return man;
    }

    public TestSectionManager update(TestSectionManager man) throws Exception {
        TestSectionLocal tl;
        TestSectionViewDO section;

        tl = local();

        for (int i = 0; i < man.deleteCount(); i++ ) {
            tl.delete(man.getDeletedAt(i));
        }

        for (int i = 0; i < man.count(); i++ ) {
            section = man.getSectionAt(i);

            if (section.getId() == null) {
                section.setTestId(man.getTestId());
                tl.add(section);
            } else
                tl.update(section);
        }

        return man;
    }

    public void validate(TestSectionManager man) throws Exception {
        ValidationErrorsList list;
        List<TestSectionViewDO> sectionDOList;
        TestSectionViewDO secDO;
        TestSectionLocal sl;
        DictionaryLocal dl;
        Integer defId, matchId, flagId, sectId;
        List<Integer> idList;
        int size, numDef, numMatch, numBlank, iter;
        TableFieldErrorException exc;

        sl = local();
        sectionDOList = man.getSections();
        list = new ValidationErrorsList();

        size = man.count();
        if (size == 0) {
            list.add(new FieldErrorException("atleastOneSection",null));
            return;
        }

        dl = dictLocal();
        size = sectionDOList.size();

        defId = (dl.fetchBySystemName("test_section_default")).getId();
        matchId = (dl.fetchBySystemName("test_section_match")).getId();

        numDef = 0;
        numMatch = 0;
        numBlank = 0;

        idList = new ArrayList<Integer>();
        for (iter = 0; iter < size; iter++ ) {
            secDO = sectionDOList.get(iter);
            flagId = secDO.getFlagId();
            sectId = secDO.getSectionId();

            try {
                sl.validate(secDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "sectionTable", iter);
            }

            if (idList.contains(sectId)) {                
                exc = new TableFieldErrorException("fieldUniqueOnlyException",iter,
                                                   meta.getTestSection().getFlagId(),"sectionTable");
                list.add(exc);
            } else {
                idList.add(sectId);
            }

            if (flagId == null) {
                numBlank++ ;
            } else if (defId.equals(flagId)) {
                numDef++ ;
            } else if (matchId.equals(flagId)) {
                numMatch++ ;
            }

        }

        if (numBlank == size) {
            for (iter = 0; iter < size; iter++ ) {
                exc = new TableFieldErrorException("allSectCantBeBlankException",iter,
                                                   meta.getTestSection().getFlagId(),"sectionTable");
                list.add(exc);
            }
        } else if (numDef > 1) {
            for (iter = 0; iter < size; iter++ ) {
                secDO = sectionDOList.get(iter);
                flagId = secDO.getFlagId();
                if (flagId != null) {
                    exc = new TableFieldErrorException("allSectBlankIfDefException",iter,
                                                       meta.getTestSection().getFlagId(),"sectionTable");
                    list.add(exc);
                }
            }
        } else if (numDef == 1 && numBlank != (size - 1)) {
            for (iter = 0; iter < size; iter++ ) {
                secDO = sectionDOList.get(iter);
                flagId = secDO.getFlagId();
                if (flagId != null && !defId.equals(flagId)) {
                    exc = new TableFieldErrorException("allSectBlankIfDefException",iter,
                                                       meta.getTestSection().getFlagId(),"sectionTable");
                    list.add(exc);
                }
            }
        } else if (numMatch > 0 && numMatch != size) {
            for (iter = 0; iter < size; iter++ ) {
                secDO = sectionDOList.get(iter);
                flagId = secDO.getFlagId();

                if (flagId == null || (flagId != null && !matchId.equals(flagId))) {
                    exc = new TableFieldErrorException("allSectMatchFlagException",iter,
                                                       meta.getTestSection().getFlagId(),"sectionTable");
                    list.add(exc);
                }

            }
        } 
        
        if (list.size() > 0)
            throw list;
    }

    private TestSectionLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (TestSectionLocal)ctx.lookup("openelis/TestSectionBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    private DictionaryLocal dictLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
}
