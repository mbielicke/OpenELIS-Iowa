/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.utils;

import javax.naming.InitialContext;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.openelis.local.HistoryLocal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AuditUtil {

    public AuditUtil() {

    };

    private HistoryLocal getHistory() {
        try {
            InitialContext ctx = new InitialContext();
            return (HistoryLocal)ctx.lookup("openelis/HistoryBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostLoad
    void postLoad(Object entity) {
        if (entity instanceof Auditable) {
            ((Auditable)entity).setClone();
        }
    }

    @PostPersist
    void postCreate(Object entity) {
        if (entity instanceof Auditable) {
            Auditable aud = (Auditable)entity;
            getHistory().write(aud, 1, "entity inserted");
        }
    }

    @PostUpdate
    void postUpdate(Object entity) {
        if (entity instanceof Auditable) {
            Auditable aud = (Auditable)entity;
            getHistory().write(aud, 2, aud.getChangeXML());
        }
    }

    @PostRemove
    void postDelete(Object entity) {
        if (entity instanceof Auditable) {
            Auditable aud = (Auditable)entity;
            getHistory().write(aud, 3, "entity deleted");
        }
    }
    
    public static void getChangeXML(Object field, Object original, Document doc, String key){
        if((field == null && original != null) || 
           (field != null && !field.equals(original))){
             Element elem = doc.createElement(key);
             elem.appendChild(doc.createTextNode(original.toString().trim()));
             doc.getDocumentElement().appendChild(elem);
        }      
    }

}
