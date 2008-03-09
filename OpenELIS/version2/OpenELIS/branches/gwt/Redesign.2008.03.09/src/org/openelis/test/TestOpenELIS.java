package org.openelis.test;

import org.openelis.junit.client.TestOrganization;
import org.openelis.junit.client.TestProvider;

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
