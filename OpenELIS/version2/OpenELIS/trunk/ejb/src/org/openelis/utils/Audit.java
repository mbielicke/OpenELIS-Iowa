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
package org.openelis.utils;

import java.util.ArrayList;

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used by entities and AuditUtil to manage a list of field for
 * auditing purposes. The field values that are saved in the history table
 * represent the previous snapshot; the latest values are stored in their
 * respective table.
 */
public class Audit {

    protected class Field {
        boolean changed;
        String  name;
        Object  value;
    }

    protected Integer          referenceId, referenceTableId;
    protected ArrayList<Field> fields;

    /**
     * Get the primary key for the current record that is being archived.
     */
    public Integer getReferenceId() {
        return referenceId;
    }

    /**
     * Set the primary key for the current record that is being archived.
     */
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Get the reference table constant key for the current record that is being
     * archived.
     */
    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    /**
     * Set the reference table constant key for the current record that is being
     * archived.
     */
    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }

    /**
     * Adds a field given by field name and current and original values 
     * to the list of xml elements, that can later retrieved through getXML(boolean)  
     */
    public Audit setField(String fieldName, Object currentValue, Object originalValue) {
        Field field;

        field = new Field();
        field.changed = isDifferent(currentValue, originalValue);
        field.name = fieldName;
        field.value = originalValue;

        if (fields == null)
            fields = new ArrayList<Field>();
        fields.add(field);

        return this;
    }

    /**
     * This method creates a list of xml elements that represent the changes made to 
     * certain fields. The withDifferences flag indicates that elements should be
     * created regardless of whether or not the field was changed.
     */
    public String getXML(boolean withDifferences) {
        Document doc;
        Element root, elem;

        if (fields != null) {
            try {
                doc = XMLUtil.createNew("doc");
                root = doc.getDocumentElement();

                for (Field f : fields) {
                    if ( (f.changed || !withDifferences) && f.value != null) {
                        elem = doc.createElement(f.name);
                        elem.appendChild(doc.createTextNode(f.value.toString()));
                        doc.getDocumentElement().appendChild(elem);
                    }
                }

                if (root.hasChildNodes())
                    return XMLUtil.toString(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /*
     * Overridden because the string value from the original clone contains trailing
     * spaces, thus trimming. 
     */
    private boolean isDifferent(Object a, Object b) {
        if (b instanceof String)
            return DataBaseUtil.isDifferent(a, ((String)b).trim());
        return DataBaseUtil.isDifferent(a,b);
    }
}
