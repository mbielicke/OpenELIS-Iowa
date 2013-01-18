package org.openelis.local;

import javax.ejb.Local;


@Local
public interface TurnaroundNotificationMaximumReportLocal {
    public void runReport() throws Exception;
}
