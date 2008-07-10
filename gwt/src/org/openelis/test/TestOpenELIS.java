/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
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
