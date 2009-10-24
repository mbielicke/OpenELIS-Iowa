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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class DictionaryBean implements DictionaryLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager          manager;

    private static CategoryMetaMap meta = new CategoryMetaMap();

    public ArrayList<DictionaryViewDO> fetchByCategoryId(Integer id) throws Exception {
        List<DictionaryViewDO> list;
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByCategoryId");
        query.setParameter("id", id);

        list = query.getResultList();// getting list of dictionary entries
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public DictionaryViewDO add(DictionaryViewDO data) throws Exception {
        Dictionary dictionary;

        manager.setFlushMode(FlushModeType.COMMIT);

        dictionary = new Dictionary();
        dictionary.setCategoryId(data.getCategoryId());
        dictionary.setEntry(data.getEntry());
        dictionary.setIsActive(data.getIsActive());
        dictionary.setLocalAbbrev(data.getLocalAbbrev());
        dictionary.setRelatedEntryId(data.getRelatedEntryId());
        dictionary.setSystemName(data.getSystemName());
        dictionary.setSortOrder(data.getSortOrder());

        manager.persist(dictionary);
        data.setId(dictionary.getId());

        return data;
    }

    public DictionaryViewDO update(DictionaryViewDO data) throws Exception {
        Dictionary dictionary;

        if ( !data.isChanged())
            return data;

        dictionary = manager.find(Dictionary.class, data.getId());

        dictionary.setCategoryId(data.getCategoryId());
        dictionary.setEntry(data.getEntry());
        dictionary.setIsActive(data.getIsActive());
        dictionary.setLocalAbbrev(data.getLocalAbbrev());
        dictionary.setRelatedEntryId(data.getRelatedEntryId());
        dictionary.setSystemName(data.getSystemName());
        dictionary.setSortOrder(data.getSortOrder());

        return data;
    }

    public void delete(DictionaryViewDO data) throws Exception {
        Dictionary dictionary;

        manager.setFlushMode(FlushModeType.COMMIT);

        dictionary = manager.find(Dictionary.class, data.getId());
        if (dictionary != null)
            manager.remove(dictionary);
    }

    public void validate(DictionaryViewDO data) throws Exception {
        ValidationErrorsList list;
        String entry;

        list = new ValidationErrorsList();
        entry = data.getEntry();

        if (DataBaseUtil.isEmpty(entry))
            list.add(new FieldErrorException("fieldRequiredException", meta.getDictionary()
                                                                           .getEntry()));

        if (list.size() > 0)
            throw list;

    }

}
