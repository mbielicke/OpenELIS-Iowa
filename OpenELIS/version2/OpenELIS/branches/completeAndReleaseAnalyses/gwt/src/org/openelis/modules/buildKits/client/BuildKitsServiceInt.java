package org.openelis.modules.buildKits.client;

import org.openelis.manager.BuildKitManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("buildkit")
public interface BuildKitsServiceInt extends XsrfProtectedService {

    BuildKitManager add(BuildKitManager man) throws Exception;

}