package org.openelis.utils;

import org.openelis.local.HistoryLocal;

import javax.naming.InitialContext;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class AuditUtil {

    public AuditUtil() {

    };

    private HistoryLocal getHistory() {
        try {
            InitialContext ctx = new InitialContext();
            return (HistoryLocal)ctx.lookup("HistoryBean/local");
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
            getHistory().write(aud, "A");
        }
    }

    @PostUpdate
    void postUpdate(Object entity) {
        if (entity instanceof Auditable) {
            Auditable aud = (Auditable)entity;
            getHistory().write(aud, "U");
        }
    }

    @PostRemove
    void postDelete(Object entity) {
        if (entity instanceof Auditable) {
            Auditable aud = (Auditable)entity;
            getHistory().write(aud, "D");
        }
    }

}
