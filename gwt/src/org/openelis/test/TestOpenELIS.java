package org.openelis.test;

import org.openelis.client.dataEntry.screen.Provider.TestProvider;
import org.openelis.client.dataEntry.screen.organization.TestOrganization;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TestOpenELIS extends TestCase {
    
    public TestOpenELIS(String method) {
        super(method);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestOrganization.class);
        suite.addTestSuite(TestProvider.class);
        return suite;
    }

}
