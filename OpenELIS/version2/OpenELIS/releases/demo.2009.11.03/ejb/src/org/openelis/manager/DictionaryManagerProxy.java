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

import javax.naming.InitialContext;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.CategoryLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.messages.DictionaryCacheMessage;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

public class DictionaryManagerProxy {

    private static CategoryMetaMap meta = new CategoryMetaMap();

    public DictionaryManager fetchByCategoryId(Integer categoryId) throws Exception {
        CategoryDO catDO;
        DictionaryManager man;
        ArrayList<DictionaryViewDO> entries;

        catDO = catLocal().fetchById(categoryId);
        man = DictionaryManager.getInstance();
        man.setCategory(catDO);

        try {
            entries = (ArrayList<DictionaryViewDO>)dictLocal().fetchByCategoryId(categoryId);
            man.entries = entries;
        } catch(NotFoundException e) {
            e.printStackTrace();
        }
        
        return man;
    }

    public DictionaryManager add(DictionaryManager man) throws Exception {
        DictionaryViewDO entry;
        DictionaryLocal dl;

        catLocal().add(man.getCategory());

        man.setCategoryId(man.getCategory().getId());

        dl = dictLocal();

        for (int i = 0; i < man.count(); i++ ) {
            entry = man.getEntryAt(i);
            entry.setSortOrder(i + 1);
            entry.setCategoryId(man.getCategoryId());

            dl.add(entry);
        }

        return man;
    }

    public DictionaryManager update(DictionaryManager man) throws Exception {
        DictionaryViewDO entry;
        DictionaryLocal dl;
        int i;
        DictionaryCacheMessage msg;
        CategoryDO catDO;
        boolean sendMessage;

        catDO = man.getCategory();
        sendMessage = false;
        dl = dictLocal();

        catLocal().update(catDO);
        man.setCategoryId(catDO.getId());

        for (i = 0; i < man.deleteCount(); i++ ) {
            dl.delete(man.getDeletedAt(i));
            sendMessage = true;
        }

        for (i = 0; i < man.count(); i++ ) {
            entry = man.getEntryAt(i);
            entry.setSortOrder(i + 1);

            if (entry.getId() == null) {
                entry.setCategoryId(man.getCategoryId());
                dl.add(entry);
                sendMessage = true;
            } else {
                dl.update(entry);
                if (entry.isChanged())
                    sendMessage = true;
            }
        }

        if (sendMessage) {
            // invalidate the cache
            msg = new DictionaryCacheMessage();
            msg.setCatDO(catDO);
            msg.action = DictionaryCacheMessage.Action.UPDATED;
            jmsLocal().writeMessage(msg);
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
        
        try {
            catLocal().validate(man.getCategory());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        
        validateDictionary(list,man);
        
        if(list.size() > 0)
            throw list;
    }

    private CategoryLocal catLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (CategoryLocal)ctx.lookup("openelis/CategoryBean/local");
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

    private JMSMessageProducerLocal jmsLocal() {
        try {
            InitialContext ctx = new InitialContext();
            return (JMSMessageProducerLocal)ctx.lookup("openelis/JMSMessageProducerBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void validateDictionary(ValidationErrorsList list, DictionaryManager man) {
        ArrayList<String> systemNames, entries;
        DictionaryViewDO data;
        DictionaryDO dictDO;
        DictionaryLocal dl;
        Integer catId;
        String systemName;
        String name;
        ArrayList<DictionaryViewDO> dictList;
        Integer categoryId;
        int i;

        systemNames = new ArrayList<String>();
        entries = new ArrayList<String>();
        data = null;
        dl = dictLocal();
        catId = null;
        dictList = man.getEntries();
        categoryId = man.getCategory().getId();
        

        for(i = 0;  i < man.deleteCount(); i++) {
            data = man.getDeletedAt(i);
            try {
                dl.validateForDelete(data);
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e);                
            }
        }
        
        for (i = 0; i < dictList.size(); i++ ) {
            data = dictList.get(i);
            name = data.getEntry();
            systemName = data.getSystemName();
            
            try {
                dl.validate(data);
                if (!entries.contains(name)) {
                    entries.add(name);
                } else {
                    list.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                          meta.getDictionary().getEntry(),
                                                          "dictEntTable"));
                }
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e, "dictEntTable", i);                
            }           

            if (!DataBaseUtil.isEmpty(systemName)) {
                if (!systemNames.contains(systemName)) {
                   try {
                    dictDO = dl.fetchBySystemName(systemName);
                    
                    catId = dictDO.getCategoryId();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   
                   if (!DataBaseUtil.isEmpty(catId) && !catId.equals(categoryId)) {
                        list.add(new TableFieldErrorException("fieldUniqueException", i,
                                                              meta.getDictionary().getSystemName(),
                                                              "dictEntTable"));                        
                   }
                    systemNames.add(systemName);
                } else if (DataBaseUtil.isEmpty(data.getId())) {
                        list.add(new TableFieldErrorException("fieldUniqueOnlyException", i,
                                                              meta.getDictionary().getSystemName(),
                                                              "dictEntTable"));                        
                } else {
                    list.add(new TableFieldErrorException("fieldUniqueException", i,
                                                              meta.getDictionary().getSystemName(),
                                                              "dictEntTable"));                    
                }
            }                    
        }
    }    
}
