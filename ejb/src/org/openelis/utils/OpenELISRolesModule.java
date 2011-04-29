package org.openelis.utils;

import org.jboss.security.auth.spi.DatabaseServerLoginModule;

public class OpenELISRolesModule extends DatabaseServerLoginModule {

	@Override
	protected String getUsername() {		
		return super.getUsername().split(":")[0];
	}

}
