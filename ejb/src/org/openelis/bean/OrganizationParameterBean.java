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

import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.entity.OrganizationParameter;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EmailUtil;

@Stateless
@SecurityDomain("openelis")
public class OrganizationParameterBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    private static char   delim = ';';

    public ArrayList<OrganizationParameterDO> fetchByOrganizationId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrganizationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<OrganizationParameterDO> fetchByOrganizationIds(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrganizationIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<OrganizationParameterDO> fetchByOrgIdAndDictSystemName(Integer id,
                                                                            String systemName) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrgIdDictSystemName");
        query.setParameter("id", id);
        query.setParameter("systemName", systemName);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<OrganizationParameterDO> fetchByDictionarySystemName(String systemName) {
        Query query;

        query = manager.createNamedQuery("OrganizationParameter.FetchByDictionarySystemName");
        query.setParameter("systemName", systemName);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrganizationParameterDO add(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new OrganizationParameter();
        entity.setOrganizationId(data.getOrganizationId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationParameterDO update(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(OrganizationParameterDO data) throws Exception {
        Integer typeId;
        String value, email;
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        typeId = data.getTypeId();
        value = data.getValue();
        email = decodeEmail(value).getEmail();

        if (DataBaseUtil.isEmpty(typeId))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             OrganizationMeta.getOrganizationParameterTypeId()));
        if (DataBaseUtil.isEmpty(email)) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             OrganizationMeta.getOrganizationParameterValue()));
        } else if (Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL.equals(typeId) ||
                   Constants.dictionary().RELEASED_REPORTTO_EMAIL.equals(typeId)) {
            try {
                EmailUtil.validateAddress(email);
            } catch (AddressException e) {
                list.add(new FieldErrorException(Messages.get().invalidFormatEmailException(email),
                                                 OrganizationMeta.getOrganizationParameterValue()));
            }
        }

        if (list.size() > 0)
            throw list;
    }

    /**
     * separates the email string from the filter data using the delimiter
     * character
     */
    public EmailFilter decodeEmail(String encoded) throws Exception {
        int i;
        char character, str[];
        StringBuffer sb;
        EmailFilter decoded;

        decoded = new EmailFilter();
        if (DataBaseUtil.isEmpty(encoded))
            return decoded;
        sb = new StringBuffer();

        str = encoded.toCharArray();
        for (i = 0; i < encoded.length(); i++ ) {
            character = str[i];
            if (character == '\\') {
                try {
                    character = str[ ++i];
                } catch (IndexOutOfBoundsException e) {
                    throw new Exception("invalid escape character");
                }
                sb.append(character);
            } else if (character == delim) {
                break;
            } else {
                sb.append(character);
            }
        }
        decoded.setEmail(sb.toString());
        if (i == encoded.length())
            return decoded;
        try {
            decoded.setFilter(encoded.substring(i + 1, i + 3));
            decoded.setFilterValue(encoded.substring(i + 3));
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("Invalid filter string");
        }
        return decoded;
    }

    public class EmailFilter {
        private String email, filter, filterValue;

        public EmailFilter() {
        }

        public EmailFilter(String email, String filter, String filterValue) {
            this.email = email;
            this.filter = filter;
            this.filterValue = filterValue;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getfilterValue() {
            return filterValue;
        }

        public void setFilterValue(String filterValue) {
            this.filterValue = filterValue;
        }
    }
}