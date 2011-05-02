package org.openelis.utils;

import org.jboss.security.auth.spi.LdapExtLoginModule;

/**
 * JBOSS does not allow the passage of additional parameters in the context from
 * servlet to the bean. To overcome this limitation, we pass username;session-id;locale
 * in the security principal name.
 * This class overrides the get username to allow ldap login. Please see the
 * jboss login-config.xml in conf directory, application policy for openelis.
 * 
 *   <application-policy name="openelis">
 *        <authentication>
 *            ...
 *            <login-module code="org.openelis.utils.OpenELISLDAPModule"
 *               flag="required">
 * 
 */

public class OpenELISLDAPModule extends LdapExtLoginModule {

    protected String getUsername() {
        String name, parts[];

        name = super.getUsername();
        if (name != null) {
            parts = name.split(";", 3);
            if (parts.length == 3)
                return parts[0];
        }
        return name;
    }
}
