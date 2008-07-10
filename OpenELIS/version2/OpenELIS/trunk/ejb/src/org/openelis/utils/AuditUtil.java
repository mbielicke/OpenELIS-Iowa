/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
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
