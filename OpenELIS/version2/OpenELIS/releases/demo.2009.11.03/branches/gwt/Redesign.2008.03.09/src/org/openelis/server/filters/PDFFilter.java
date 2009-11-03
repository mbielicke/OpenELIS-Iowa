package org.openelis.server.filters;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class PDFFilter implements Filter {

    private boolean debugging;

    private ServletContext ctx;

    private String filterCommand;

    public void init(FilterConfig filterConfig) throws ServletException {

        ctx = filterConfig.getServletContext();
        filterCommand = filterConfig.getInitParameter("filterCommand");
        debugging = new Boolean(filterConfig.getInitParameter("debugging")).booleanValue();

        if (debugging)
            ctx.log("<PDFFilter>Filter " + filterConfig.getFilterName()
                    + " using filter path: "
                    + filterCommand);

    }

    public void doFilter(javax.servlet.ServletRequest servletRequest,
                         javax.servlet.ServletResponse servletResponse,
                         javax.servlet.FilterChain filterChain) throws java.io.IOException,
                                                               javax.servlet.ServletException {

        final HttpServletResponse resp = (HttpServletResponse)servletResponse;

        final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();

        if (debugging)
            ctx.log("<PDFFilter>Accessing filter");
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
                resp.setContentType("application/pdf");
            }
        };
        filterChain.doFilter(servletRequest, wrappedResp);

        try {
            ByteArrayOutputStream pdfOS;
            Process p;
            InputStream stdout;
            OutputStream stdin;
            byte[] filterBytes;
            int b;

            pdfOS = new ByteArrayOutputStream();
            filterBytes = pw.toByteArray();

            p = Runtime.getRuntime().exec(new String[] {"/bin/sh",
                                                        "-c",
                                                        filterCommand});
            stdin = p.getOutputStream();
            stdout = p.getInputStream();

            if (debugging)
                ctx.log("<PDFFilter>Input length: " + filterBytes.length);
            stdin.write(filterBytes);
            stdin.close();

            while ((b = stdout.read()) != -1)
                pdfOS.write(b);
            stdout.close();

            p.waitFor();
            resp.setContentLength(pdfOS.size());
            resp.getOutputStream().write(pdfOS.toByteArray());
            if (debugging)
                ctx.log("<PDFFilter>Input length: " + filterBytes.length
                        + " Output length: "
                        + pdfOS.size());
        } catch (Exception e) {
            if (debugging)
                ctx.log("<PDFFilter>exception in filter");
            throw new ServletException("Unable to transform document", e);
        }
        if (debugging)
            ctx.log("<PDFFilter>Exiting filter");
    }

    public void destroy() {
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
