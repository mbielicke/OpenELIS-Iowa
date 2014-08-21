package org.openelis.modules.buildKits.client;

import org.openelis.manager.BuildKitManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("buildkit")
public interface BuildKitsServiceInt extends RemoteService {

    BuildKitManager add(BuildKitManager man) throws Exception;

}