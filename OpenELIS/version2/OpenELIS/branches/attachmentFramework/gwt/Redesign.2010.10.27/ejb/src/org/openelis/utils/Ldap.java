package org.openelis.utils;

import java.security.Principal;

public class Ldap extends org.jboss.security.auth.spi.LdapExtLoginModule {

	@Override
	protected Principal createIdentity(String arg0) throws Exception {
		return super.createIdentity(arg0.substring(0,arg0.indexOf("*")));
	}
}
