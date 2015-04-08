/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tschmidt
 *
 */
public class EmbeddedTest {
    

    EJBContainer container;
    
    @Before
    public void init() {
        System.setProperty("jboss.home","/home/tschmidt/jboss-as-7.1.1.Final");
        //System.setProperty("jboss.home.dir","/home/tschmidt/jboss-as-7.1.1.Final");
        System.setProperty("org.jboss.as.embedded.ejb3.BARREN","true");
        Map properties = new HashMap<>();
        properties.put(EJBContainer.MODULES, "openelis.ear");
        container = EJBContainer.createEJBContainer(properties);
    }
    
    @After
    public void destroy() {
        container.close();
    }
    
    @Test 
    public void testDefault() throws Exception {
        assertNotNull(container);
        System.out.println(container.getContext().getEnvironment().keySet());
        MethodBean bean = (MethodBean)container.getContext().lookup("java:global/openelis/openelis.jar/MethodBean");
        assertNotNull(bean);
    }
}
