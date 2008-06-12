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
