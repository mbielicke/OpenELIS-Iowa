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
package org.openelis.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jasperreports.engine.util.JRLoader;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.data.QueryData;

public class ReportUtil {

    private static StringBuffer buf = new StringBuffer();
    /**
      * Decodes the passed argument to String.
      */
    public static String concat(Object a, Object b) {
        buf.setLength(0);
        if (a != null)
            buf.append(a.toString().trim());
        if (b != null)
            buf.append(b.toString().trim());
        return buf.toString();
    }

    public static String concatWithSeparator(Object a, Object s, Object b) {
        buf.setLength(0);
        if (a != null)
            buf.append(a.toString().trim());
        if (b != null) {
            if (a != null)
                buf.append(s);
            buf.append(b.toString().trim());
        }
        return buf.toString();
    }
    
    public static HashMap<String, QueryData> parameterMap(ArrayList<QueryData> list) {
        HashMap<String, QueryData> p;
        
        p = new HashMap<String, QueryData>();
        for (QueryData q : list)
            p.put(q.key, q);

        return p;
    }
    
    public static String getSingleParameter(HashMap<String, QueryData> parameter, String key) {
        QueryData q;
        
        q = parameter.get(key);
        if (q != null)
            return q.query;

        return null;
    }
    
    public static String getListParameter(HashMap<String, QueryData> parameter, String key) {
        QueryData q;
        
        q = parameter.get(key);
        if (q != null) {
            if (q.query.contains(","))
                return "in (" + q.query +")";
            else if (!DataBaseUtil.isEmpty(q.query))
                return " = "+q.query;
        }
        return null;
    }
    
    public static URL getResourceURL(String reportName) {        
        return JRLoader.getResource(reportName);        
    }
    
    public static String getResourcePath(URL url) {
        int i;
        String path;
        
        path = url.toExternalForm();
        i = path.lastIndexOf('/');
        if (i > 0) 
            return path.substring(0, i+1);
            
        return path;
    }
}
