/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) The University of Iowa. All Rights Reserved.
 */
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.SampleTypePrepTestListDO;
import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;

@Remote
public interface TestRemote {

    public TestIdNameMethodIdDO getTestIdNameMethod(Integer testId);

    public TestIdNameMethodIdDO getTestIdNameMethodAndUnlock(Integer testId,
                                                             String session);

    public TestIdNameMethodIdDO getTestIdNameMethodAndLock(Integer testId,
                                                           String session) throws Exception;

    public TestDetailsDO getTestDetails(Integer testId);

    public SampleTypePrepTestListDO getSampleTypesAndPrepTests(Integer testId);

    public Integer updateTest(TestIdNameMethodIdDO testIdNameMethod,
                              TestDetailsDO testDetails,
                              SampleTypePrepTestListDO listDO) throws Exception;

    public List query(HashMap fields, int first, int max) throws Exception;

    public List validateForUpdate(TestIdNameMethodIdDO testIdNameMethod,
                                  TestDetailsDO testDetails,SampleTypePrepTestListDO listDO);

    public List validateForAdd(TestIdNameMethodIdDO testIdNameMethod,
                               TestDetailsDO testDetails,SampleTypePrepTestListDO listDO);

    public List getMethodDropDownValues();

    public List getLabelDropDownValues();

    public List getTestTrailerDropDownValues();

    public List getScriptletDropDownValues();

    public List getPrepTestDropDownValues();

}
