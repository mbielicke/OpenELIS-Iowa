package org.openelis.utils;


import org.jboss.security.auth.spi.LdapExtLoginModule;


public class OpenELISLDAPModule extends LdapExtLoginModule {
	
	@Override
	protected String getUsername() {		
		return super.getUsername().split(":")[0];
	}
	

}
