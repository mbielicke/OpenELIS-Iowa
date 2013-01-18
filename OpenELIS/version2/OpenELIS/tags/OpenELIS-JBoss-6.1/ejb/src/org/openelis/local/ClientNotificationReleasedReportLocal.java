package org.openelis.local;

import javax.ejb.Remote;


@Remote
public interface ClientNotificationReleasedReportLocal {
    public void runReport() throws Exception;
}
