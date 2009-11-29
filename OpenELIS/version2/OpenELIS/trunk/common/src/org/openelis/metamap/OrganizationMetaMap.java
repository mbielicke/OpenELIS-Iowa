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
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.NoteMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.OrganizationParameterMeta;

public class OrganizationMetaMap extends OrganizationMeta implements MetaMap {

    public AddressMeta                ADDRESS;
    public OrganizationMeta           PARENT_ORGANIZATION;
    public OrganizationContactMetaMap ORGANIZATION_CONTACT;
    public OrganizationParameterMeta  ORGANIZATION_PARAMETER;

    public OrganizationMetaMap() {
        super("o.");
        ADDRESS = new AddressMeta(path + "address.");
        PARENT_ORGANIZATION = new OrganizationMeta(path + "parentOrganization.");
        ORGANIZATION_CONTACT = new OrganizationContactMetaMap(path + "contact.");
        ORGANIZATION_PARAMETER = new OrganizationParameterMeta(path + "parameter.");
    }

    public OrganizationMetaMap(String path) {
        super(path);
        ADDRESS = new AddressMeta(path + "address.");
        PARENT_ORGANIZATION = new OrganizationMeta(path + "parentOrganization.");
        ORGANIZATION_CONTACT = new OrganizationContactMetaMap(path + "contact.");
        ORGANIZATION_PARAMETER = new OrganizationParameterMeta(path + "parameter.");
    }

    public static OrganizationMetaMap getInstance() {
        return new OrganizationMetaMap();
    }

    public AddressMeta getAddress() {
        return ADDRESS;
    }

    public OrganizationMeta getParentOrganization() {
        return PARENT_ORGANIZATION;
    }

    public OrganizationContactMetaMap getOrganizationContact() {
        return ORGANIZATION_CONTACT;
    }

    public OrganizationParameterMeta getOrganizationParameter() {
        return ORGANIZATION_PARAMETER;
    }

    public boolean hasColumn(String name) {
        if (name.startsWith(path + "address."))
            return ADDRESS.hasColumn(name);
        else if (name.startsWith(path + "parentOrganization."))
            return PARENT_ORGANIZATION.hasColumn(name);
        else if (name.startsWith(path + "contact."))
            return ORGANIZATION_CONTACT.hasColumn(name);
        else if (name.startsWith(path + "parameter."))
            return ORGANIZATION_PARAMETER.hasColumn(name);
        return super.hasColumn(name);
    }

    public String buildFrom(String where) {
        String from = "Organization o ";

        if (where.indexOf("contact.") > -1)
            from += ", IN (o.organizationContact) contact ";
        if (where.indexOf("parameter.") > -1)
            from += ", IN (o.organizationParameter) parameter ";
        return from;
    }
}
