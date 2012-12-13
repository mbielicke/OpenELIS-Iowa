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

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.meta.CategoryMeta;
import org.openelis.utils.EJBFactory;

public class DictionaryManagerProxy {

    public DictionaryManager fetchByCategoryId(Integer id) throws Exception {
        DictionaryManager man;
        ArrayList<DictionaryViewDO> list;

        list = EJBFactory.getDictionary().fetchByCategoryId(id);
        man = DictionaryManager.getInstance();
        man.setCategoryId(id);
        man.setEntries(list);
        
        return man;
    }

    public DictionaryManager add(DictionaryManager man) throws Exception {
        DictionaryViewDO data;
        DictionaryLocal dl;

        dl = EJBFactory.getDictionary();

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getEntryAt(i);
            data.setSortOrder(i + 1);
            data.setCategoryId(man.getCategoryId());
            dl.add(data);
        }

        return man;
    }

    public DictionaryManager update(DictionaryManager man) throws Exception {
        int i;
        DictionaryViewDO data;
        DictionaryLocal dl;


        dl = EJBFactory.getDictionary();

        for (i = 0; i < man.deleteCount(); i++ ) {
            dl.delete(man.getDeletedAt(i));
        }

        for (i = 0; i < man.count(); i++ ) {
            data = man.getEntryAt(i);
            data.setSortOrder(i + 1);

            if (data.getId() == null) {
                data.setCategoryId(man.getCategoryId());
                dl.add(data);
            } else {
                dl.update(data);
            }
        }        

        return man;
    }

    public DictionaryManager fetchForUpdate(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public DictionaryManager abort(Integer testId) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(DictionaryManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        validateDictionary(list,man);
        
        if(list.size() > 0)
            throw list;
    }

    private void validateDictionary(ValidationErrorsList list, DictionaryManager man) throws Exception {
        int i;
        String name,systemName;
        ArrayList<String> systemNames, entries;
        DictionaryViewDO data;
        DictionaryDO dictionary;
        DictionaryLocal dl;

        systemNames = new ArrayList<String>();
        entries = new ArrayList<String>();
        data = null;
        dl = EJBFactory.getDictionary();

        for(i = 0;  i < man.deleteCount(); i++) {
            data = man.getDeletedAt(i);
            try {
                dl.validateForDelete(data);
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e);                
            }
        }
        
        for (i = 0; i < man.count(); i++ ) {
            data = man.getEntryAt(i);
            name = data.getEntry();
            systemName = data.getSystemName();           
            if(!data.isChanged()) {
                entries.add(name);                                
                systemNames.add(systemName);                
                continue;
            }
            
            try {
                dl.validate(data);
                if (!entries.contains(name)) {
                    entries.add(name);
                } else {                    
                    list.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                          CategoryMeta.getDictionaryEntry(),
                                                          "dictEntTable"));
                }
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e, "dictEntTable", i);                
            }           
            
            if (!DataBaseUtil.isEmpty(systemName)) {
                if (!systemNames.contains(systemName)) {
                   try {
                       dictionary = dl.fetchBySystemName(systemName);
                       if (!dictionary.getCategoryId().equals(man.getCategoryId())) {
                           list.add(new TableFieldErrorException("fieldUniqueException", i,
                                                                 CategoryMeta.getDictionarySystemName(),
                                                                 "dictEntTable"));                        
                      }
                   } catch (NotFoundException e) {
                       //do nothing               
                   }
                   systemNames.add(systemName);
                } else if (data.getId() == null) {
                        list.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                              CategoryMeta.getDictionarySystemName(),
                                                              "dictEntTable"));                        
                } else {
                    list.add(new TableFieldErrorException("fieldUniqueException", i,
                                                          CategoryMeta.getDictionarySystemName(),
                                                              "dictEntTable"));                    
                }
            }                    
        }
    }    
}
