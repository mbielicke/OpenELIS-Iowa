package org.openelis;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openelis.bean.LockBean;
import org.openelis.bean.LockCacheBean;

@WebServlet("/Lock")
public class TestLockServlet extends HttpServlet {
	
	@EJB
	LockBean lockBean;
	
	@EJB
	LockCacheBean lockCache;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameterMap().containsKey("count")) {
			PrintWriter out = resp.getWriter();
			out.println(lockCache.getAll().size());
			out.close();
		} else if (req.getParameterMap().containsKey("lock")){
			int tableId = Integer.parseInt(req.getParameter("tableId"));
			int id = Integer.parseInt(req.getParameter("id"));
			try {
				lockBean.lock(tableId, id);
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		} else if (req.getParameterMap().containsKey("validate")) {
			int tableId = Integer.parseInt(req.getParameter("tableId"));
			int id = Integer.parseInt(req.getParameter("id"));
			try {
				lockBean.validateLock(tableId, id);
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		} else if (req.getParameterMap().containsKey("unlock")) {
			int tableId = Integer.parseInt(req.getParameter("tableId"));
			int id = Integer.parseInt(req.getParameter("id"));
			try {
				lockBean.unlock(tableId, id);
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		}
	}
	
	
}
