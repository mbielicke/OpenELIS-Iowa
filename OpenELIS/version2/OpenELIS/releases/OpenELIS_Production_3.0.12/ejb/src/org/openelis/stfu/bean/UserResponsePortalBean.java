package org.openelis.stfu.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ReportStatus.Status;

@Stateless
@SecurityDomain("openelis")
public class UserResponsePortalBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public ReportStatus saveReponse(String response) {
        ReportStatus status;

        status = new ReportStatus();
        try {
            // TODO store in database
        } catch (Exception e) {
            status.setMessage(e.getMessage());
            return status;
        }
        status.setStatus(Status.SAVED);
        return status;
    }
}