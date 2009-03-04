/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.server.filters;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class XSLTFilter implements Filter {

    private boolean debugging;

    private long xsltLastChanged = 0;

    private File xsltFile;

    private ServletContext ctx;

    private String xslt;

    private TransformerFactory tf = TransformerFactory.newInstance();

    private Transformer xform;

    public void init(FilterConfig filterConfig) throws ServletException {

        ctx = filterConfig.getServletContext();
        xslt = filterConfig.getInitParameter("xslt");
        debugging = new Boolean(filterConfig.getInitParameter("debugging")).booleanValue();

        if (debugging)
            ctx.log("<XSLTFilter>Filter " + filterConfig.getFilterName()
                    + " using xslt "
                    + xslt);
        initTransformer();
    }

    private void initTransformer() throws ServletException {

        try {
            StreamSource ss = new StreamSource(ctx.getResourceAsStream(xslt));
            String appRoot = ctx.getRealPath("");
            String xslLocation = appRoot + "\\WEB-INF\\stylesheet\\";
            ss.setSystemId(xslLocation);
            xform = tf.newTransformer(ss);
        } catch (Exception e) {
            if (debugging)
                ctx.log("<XSLTFilter>Could not intialize transform", e);
            throw new ServletException("Could not initialize transform", e);
        }

        xsltFile = new File(ctx.getRealPath(xslt));
        xsltLastChanged = xsltFile.lastModified();
        if (debugging)
            ctx.log("<XSLTFilter>==>File last changed on " + xsltLastChanged);
    }

    public String httpReqLine(HttpServletRequest req) {

        StringBuffer ret = req.getRequestURL();
        String query = req.getQueryString();

        if (query != null)
            ret.append("?").append(query);

        return ret.toString();
    }

    public String getHeaders(HttpServletRequest req) throws IOException {

        Enumeration en = req.getHeaderNames();
        StringBuffer sb = new StringBuffer();
        while (en.hasMoreElements()) {
            String name = (String)en.nextElement();
            sb.append(name)
              .append(": ")
              .append(req.getHeader(name))
              .append("\n");
        }
        return sb.toString();
    }

    public void doFilter(javax.servlet.ServletRequest servletRequest,
                         javax.servlet.ServletResponse servletResponse,
                         javax.servlet.FilterChain filterChain) throws java.io.IOException,
                                                               javax.servlet.ServletException {

        HttpServletRequest hsr = (HttpServletRequest)servletRequest;
        final HttpServletResponse resp = (HttpServletResponse)servletResponse;
        if (debugging)
            ctx.log("<XSLTFilter>Accessing filter for " + httpReqLine(hsr)
                    + " "
                    + hsr.getMethod());

        final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
        final boolean[] xformNeeded = new boolean[1];
        HttpServletResponse wrappedResp = new HttpServletResponseWrapper(resp) {

            public byte[] toByteArray() {
                return pw.toByteArray();
            }

            public PrintWriter getWriter() {
                return pw.getWriter();
            }

            public ServletOutputStream getOutputStream() {
                return pw.getStream();
            }

            public void setContentType(String type) {
                if (type.equals("text/xml")) {
                    if (debugging)
                        ctx.log("<XSLTFilter>Converting xml to html");
                    resp.setContentType("text/html");
                    xformNeeded[0] = true;
                } else if (type.equals("text/css")) {
                    if (debugging)
                        ctx.log("<XSLTFilter>Converting xml to css");
                    resp.setContentType("text/css");
                    xformNeeded[0] = true;
                } else {
                    resp.setContentType(type);
                }
            }
        };
        filterChain.doFilter(servletRequest, wrappedResp);
        byte[] bytes = pw.toByteArray();
        if ((bytes == null || (bytes.length == 0)) && debugging)
            ctx.log("<XSLTFilter>No content!");
        if (xformNeeded[0] == true) {
            if (xsltLastChanged != xsltFile.lastModified())
                initTransformer();
            try {
                // Note: This can be _very_ inefficient for large transforms
                // Such transforms should be pre-calculated.
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                xform.transform(new StreamSource(new ByteArrayInputStream(bytes)),
                                new StreamResult(baos));
                byte[] xformBytes = baos.toByteArray();
                // This fixes a bug in the original published tip, which did
                // not set the content length to the _new_ length implied by
                // the xform.
                resp.setContentLength(xformBytes.length);
                resp.getOutputStream().write(xformBytes);
                if (debugging)
                    ctx.log("<XSLTFilter>XML -> HTML conversion completed");
            } catch (Exception e) {
                throw new ServletException("Unable to transform document", e);
            }
        } else {
            resp.getOutputStream().write(bytes);
        }
    }

    public void destroy() {
        if (debugging)
            ctx.log("<XSLTFilter>Destroying filter...");
    }

    private static class ByteArrayServletStream extends ServletOutputStream {

        ByteArrayOutputStream baos;

        ByteArrayServletStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        public void write(int param) throws java.io.IOException {
            baos.write(param);
        }
    }

    private static class ByteArrayPrintWriter {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private PrintWriter pw = new PrintWriter(baos);

        private ServletOutputStream sos = new ByteArrayServletStream(baos);

        public PrintWriter getWriter() {
            return pw;
        }

        public ServletOutputStream getStream() {
            return sos;
        }

        public byte[] toByteArray() {
            return baos.toByteArray();
        }
    }
}
